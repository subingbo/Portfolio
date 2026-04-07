package org.dromara.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.constant.BlogConstants;
import org.dromara.blog.domain.BlogArticle;
import org.dromara.blog.domain.BlogArticleTag;
import org.dromara.blog.domain.BlogCategory;
import org.dromara.blog.domain.BlogTag;
import org.dromara.blog.domain.bo.BlogArticleBo;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.blog.mapper.BlogArticleMapper;
import org.dromara.blog.mapper.BlogArticleTagMapper;
import org.dromara.blog.mapper.BlogCategoryMapper;
import org.dromara.blog.domain.BlogComment;
import org.dromara.blog.mapper.BlogCommentMapper;
import org.dromara.blog.mapper.BlogTagMapper;
import org.dromara.blog.service.IBlogArticleService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
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
 * 博客文章 Service 实现
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

    @Override
    public TableDataInfo<BlogArticleVo> selectAdminPage(BlogArticleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogArticle> lqw = buildAdminWrapper(bo);
        lqw.orderByDesc(BlogArticle::getUpdateTime).orderByDesc(BlogArticle::getId);
        Page<BlogArticleVo> page = articleMapper.selectVoPage(pageQuery.build(), lqw);
        fillArticleExtras(page.getRecords());
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
        return TableDataInfo.build(page);
    }

    @Override
    public BlogArticleVo getPublishedDetail(Long id) {
        if (id == null) {
            return null;
        }
        BlogArticle row = articleMapper.selectOne(
            Wrappers.<BlogArticle>lambdaQuery()
                .eq(BlogArticle::getId, id)
                .eq(BlogArticle::getStatus, BlogConstants.STATUS_PUBLISHED));
        if (row == null) {
            return null;
        }
        articleMapper.incrViewCount(id, 1L);
        BlogArticleVo vo = MapstructUtils.convert(row, BlogArticleVo.class);
        if (vo.getViewCount() != null) {
            vo.setViewCount(vo.getViewCount() + 1);
        } else {
            vo.setViewCount(1L);
        }
        fillArticleExtras(Collections.singletonList(vo));
        return vo;
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
        return records;
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
