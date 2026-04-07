package org.dromara.blog.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.constant.BlogConstants;
import org.dromara.blog.domain.BlogArticle;
import org.dromara.blog.domain.BlogArticleTag;
import org.dromara.blog.domain.BlogCategory;
import org.dromara.blog.domain.BlogComment;
import org.dromara.blog.domain.BlogTag;
import org.dromara.blog.domain.bo.BlogArticleBo;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.blog.mapper.BlogArticleMapper;
import org.dromara.blog.mapper.BlogArticleTagMapper;
import org.dromara.blog.mapper.BlogCategoryMapper;
import org.dromara.blog.mapper.BlogCommentMapper;
import org.dromara.blog.mapper.BlogTagMapper;
import org.dromara.blog.service.IBlogArticleService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 博客文章 Service 实现（前台详情 Redis 缓存、点赞计数、浏览量与缓存解耦）
 *
 * @author ruoyi-blog
 */
@RequiredArgsConstructor
@Service
public class BlogArticleServiceImpl implements IBlogArticleService {

    private final BlogArticleMapper articleMapper;
    private final BlogArticleTagMapper articleTagMapper;
    private final BlogTagMapper tagMapper;
    private final BlogCategoryMapper categoryMapper;
    private final BlogCommentMapper commentMapper;
    private final LockTemplate lockTemplate;

    @Override
    public TableDataInfo<BlogArticleVo> selectAdminPage(BlogArticleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogArticle> lqw = buildAdminWrapper(bo);
        lqw.orderByDesc(BlogArticle::getUpdateTime).orderByDesc(BlogArticle::getId);
        Page<BlogArticleVo> page = articleMapper.selectVoPage(pageQuery.build(), lqw);
        fillArticleExtras(page.getRecords());
        fillArticleLikeCounts(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public BlogArticleVo getAdminVo(Long id) {
        if (id == null) {
            return null;
        }
        BlogArticleVo vo = articleMapper.selectVoById(id);
        if (vo != null) {
            fillArticleExtras(Collections.singletonList(vo));
            fillArticleLikeCounts(Collections.singletonList(vo));
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertByBo(BlogArticleBo bo) {
        validateCategory(bo.getCategoryId());
        BlogArticle entity = MapstructUtils.convert(bo, BlogArticle.class);
        if (entity.getViewCount() == null) {
            entity.setViewCount(0L);
        }
        int rows = articleMapper.insert(entity);
        replaceArticleTags(entity.getId(), bo.getTagIds());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateByBo(BlogArticleBo bo) {
        validateCategory(bo.getCategoryId());
        BlogArticle entity = MapstructUtils.convert(bo, BlogArticle.class);
        int rows = articleMapper.updateById(entity);
        replaceArticleTags(bo.getId(), bo.getTagIds());
        evictArticleDetailCache(bo.getId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        for (Long id : ids) {
            articleTagMapper.delete(
                Wrappers.<BlogArticleTag>lambdaQuery().eq(BlogArticleTag::getArticleId, id));
            commentMapper.delete(
                Wrappers.<BlogComment>lambdaQuery().eq(BlogComment::getArticleId, id));
            articleMapper.deleteById(id);
            evictArticleDetailCache(id);
        }
        return ids.size();
    }

    @Override
    public TableDataInfo<BlogArticleVo> selectPublishedPage(String title, Long categoryId, Long tagId,
                                                            PageQuery pageQuery) {
        LambdaQueryWrapper<BlogArticle> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED);
        lqw.like(StringUtils.isNotBlank(title), BlogArticle::getTitle, title);
        lqw.eq(categoryId != null, BlogArticle::getCategoryId, categoryId);
        if (tagId != null) {
            lqw.inSql(BlogArticle::getId,
                "SELECT article_id FROM blog_article_tag WHERE tag_id = " + tagId);
        }
        lqw.orderByDesc(BlogArticle::getCreateTime);
        Page<BlogArticleVo> page = articleMapper.selectVoPage(pageQuery.build(), lqw);
        fillArticleExtras(page.getRecords());
        fillArticleLikeCounts(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public BlogArticleVo getPublishedDetail(Long id) {
        if (id == null) {
            return null;
        }
        String cacheKey = BlogConstants.REDIS_ARTICLE_FRONT_DETAIL_PREFIX + id;
        BlogArticleVo cached = RedisUtils.getCacheObject(cacheKey);
        if (cached != null) {
            return attachPublishedDetailRealtime(id, cached);
        }
        String lockName = BlogConstants.LOCK_ARTICLE_DETAIL_LOAD_PREFIX + id;
        LockInfo lockInfo = lockTemplate.lock(
            lockName,
            BlogConstants.LOCK_LEASE_MS,
            BlogConstants.LOCK_ACQUIRE_MS,
            RedissonLockExecutor.class);
        if (lockInfo == null) {
            BlogArticleVo retry = RedisUtils.getCacheObject(cacheKey);
            return retry != null ? attachPublishedDetailRealtime(id, retry) : loadAndCachePublishedDetail(id, cacheKey);
        }
        try {
            BlogArticleVo again = RedisUtils.getCacheObject(cacheKey);
            if (again != null) {
                return attachPublishedDetailRealtime(id, again);
            }
            return loadAndCachePublishedDetail(id, cacheKey);
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }
    }

    private BlogArticleVo loadAndCachePublishedDetail(Long id, String cacheKey) {
        BlogArticleVo loaded = loadPublishedDetailVoFromDb(id);
        if (loaded == null) {
            return null;
        }
        BlogArticleVo toCache = BeanUtil.toBean(loaded, BlogArticleVo.class);
        toCache.setViewCount(null);
        RedisUtils.setCacheObject(cacheKey, toCache, BlogConstants.ARTICLE_FRONT_DETAIL_TTL);
        return attachPublishedDetailRealtime(id, toCache);
    }

    /**
     * 缓存命中后合并实时浏览量（DB 原子自增 + 读回）与 Redis 点赞数。
     */
    private BlogArticleVo attachPublishedDetailRealtime(Long id, BlogArticleVo fromCache) {
        BlogArticleVo out = BeanUtil.toBean(fromCache, BlogArticleVo.class);
        articleMapper.incrViewCount(id, 1L);
        BlogArticle vcRow = articleMapper.selectOne(
            Wrappers.<BlogArticle>lambdaQuery()
                .select(BlogArticle::getViewCount)
                .eq(BlogArticle::getId, id));
        out.setViewCount(vcRow != null && vcRow.getViewCount() != null ? vcRow.getViewCount() : 0L);
        out.setLikeCount(likeCountArticle(id));
        return out;
    }

    private BlogArticleVo loadPublishedDetailVoFromDb(Long id) {
        BlogArticle row = articleMapper.selectOne(
            Wrappers.<BlogArticle>lambdaQuery()
                .eq(BlogArticle::getId, id)
                .eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED));
        if (row == null) {
            return null;
        }
        BlogArticleVo vo = MapstructUtils.convert(row, BlogArticleVo.class);
        fillArticleExtras(Collections.singletonList(vo));
        return vo;
    }

    private void evictArticleDetailCache(Long id) {
        if (id != null) {
            RedisUtils.deleteObject(BlogConstants.REDIS_ARTICLE_FRONT_DETAIL_PREFIX + id);
        }
    }

    @Override
    public List<BlogArticleVo> selectPublishedHot(int limit) {
        int n = normalizeLimit(limit);
        LambdaQueryWrapper<BlogArticle> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED);
        lqw.orderByDesc(BlogArticle::getViewCount).orderByDesc(BlogArticle::getCreateTime);
        Page<BlogArticleVo> page = articleMapper.selectVoPage(new Page<>(1, n), lqw);
        List<BlogArticleVo> records = page.getRecords();
        fillArticleExtras(records);
        fillArticleLikeCounts(records);
        return records;
    }

    @Override
    public List<BlogArticleVo> selectPublishedLatest(int limit) {
        int n = normalizeLimit(limit);
        LambdaQueryWrapper<BlogArticle> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED);
        lqw.orderByDesc(BlogArticle::getCreateTime);
        Page<BlogArticleVo> page = articleMapper.selectVoPage(new Page<>(1, n), lqw);
        List<BlogArticleVo> records = page.getRecords();
        fillArticleExtras(records);
        fillArticleLikeCounts(records);
        return records;
    }

    @Override
    public long likePublishedArticle(Long articleId, String voterKey) {
        if (articleId == null) {
            throw new ServiceException("参数错误");
        }
        Long exists = articleMapper.selectCount(
            Wrappers.<BlogArticle>lambdaQuery()
                .eq(BlogArticle::getId, articleId)
                .eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED));
        if (exists == null || exists == 0L) {
            throw new ServiceException("文章不存在或未发布");
        }
        String safe = sanitizeVoterKey(voterKey);
        String voterRedisKey = BlogConstants.REDIS_ARTICLE_LIKE_VOTER_PREFIX + articleId + ":" + safe;
        if (RedisUtils.setObjectIfAbsent(voterRedisKey, "1", BlogConstants.LIKE_VOTER_TTL)) {
            RedisUtils.incrAtomicValue(BlogConstants.REDIS_ARTICLE_LIKE_COUNT_PREFIX + articleId);
        }
        return likeCountArticle(articleId);
    }

    private static long likeCountArticle(Long articleId) {
        return RedisUtils.getAtomicValue(BlogConstants.REDIS_ARTICLE_LIKE_COUNT_PREFIX + articleId);
    }

    private static void fillArticleLikeCounts(List<BlogArticleVo> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        for (BlogArticleVo vo : records) {
            if (vo.getId() != null) {
                vo.setLikeCount(likeCountArticle(vo.getId()));
            }
        }
    }

    private static String sanitizeVoterKey(String voterKey) {
        if (voterKey == null || voterKey.isBlank()) {
            return "unknown";
        }
        return voterKey.replace(':', '_');
    }

    private void validateCategory(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        if (categoryMapper.selectById(categoryId) == null) {
            throw new ServiceException("分类不存在");
        }
    }

    private LambdaQueryWrapper<BlogArticle> buildAdminWrapper(BlogArticleBo bo) {
        LambdaQueryWrapper<BlogArticle> lqw = Wrappers.lambdaQuery();
        if (bo != null) {
            lqw.like(StringUtils.isNotBlank(bo.getTitle()), BlogArticle::getTitle, bo.getTitle());
            lqw.eq(bo.getCategoryId() != null, BlogArticle::getCategoryId, bo.getCategoryId());
            lqw.eq(bo.getStatus() != null, BlogArticle::getStatus, bo.getStatus());
        }
        return lqw;
    }

    private void replaceArticleTags(Long articleId, List<Long> tagIds) {
        articleTagMapper.delete(
            Wrappers.<BlogArticleTag>lambdaQuery().eq(BlogArticleTag::getArticleId, articleId));
        if (CollUtil.isEmpty(tagIds) || articleId == null) {
            return;
        }
        List<Long> distinct = tagIds.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        for (Long tid : distinct) {
            if (tagMapper.selectById(tid) == null) {
                throw new ServiceException("标签不存在: " + tid);
            }
            BlogArticleTag link = new BlogArticleTag();
            link.setArticleId(articleId);
            link.setTagId(tid);
            articleTagMapper.insert(link);
        }
    }

    /**
     * 批量填充分类名、标签列表及标签 ID（列表项）
     */
    private void fillArticleExtras(List<BlogArticleVo> records) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        Set<Long> categoryIds = records.stream()
            .map(BlogArticleVo::getCategoryId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = new HashMap<>(16);
        if (CollUtil.isNotEmpty(categoryIds)) {
            List<BlogCategory> categories = categoryMapper.selectByIds(categoryIds);
            for (BlogCategory c : categories) {
                categoryNameMap.put(c.getId(), c.getName());
            }
        }

        List<Long> articleIds = records.stream().map(BlogArticleVo::getId).filter(Objects::nonNull)
            .collect(Collectors.toList());
        Map<Long, List<BlogTagVo>> articleTagsMap = new HashMap<>(16);
        if (CollUtil.isNotEmpty(articleIds)) {
            List<BlogArticleTag> links = articleTagMapper.selectList(
                Wrappers.<BlogArticleTag>lambdaQuery().in(BlogArticleTag::getArticleId, articleIds));
            Set<Long> allTagIds = links.stream().map(BlogArticleTag::getTagId).collect(Collectors.toSet());
            Map<Long, BlogTag> tagEntityMap = new HashMap<>(16);
            if (CollUtil.isNotEmpty(allTagIds)) {
                List<BlogTag> tags = tagMapper.selectByIds(allTagIds);
                for (BlogTag t : tags) {
                    tagEntityMap.put(t.getId(), t);
                }
            }
            for (BlogArticleTag link : links) {
                BlogTag tag = tagEntityMap.get(link.getTagId());
                if (tag != null) {
                    articleTagsMap
                        .computeIfAbsent(link.getArticleId(), k -> new ArrayList<>())
                        .add(MapstructUtils.convert(tag, BlogTagVo.class));
                }
            }
        }

        for (BlogArticleVo vo : records) {
            if (vo.getCategoryId() != null) {
                vo.setCategoryName(categoryNameMap.get(vo.getCategoryId()));
            }
            List<BlogTagVo> tags = articleTagsMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tags);
            vo.setTagIds(tags.stream().map(BlogTagVo::getId).collect(Collectors.toList()));
        }
    }

    private static int normalizeLimit(int limit) {
        return Math.min(Math.max(limit, 1), BlogConstants.FRONT_LIST_MAX);
    }
}
