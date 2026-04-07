package org.dromara.portfolio.service.impl;

import com.baomidou.lock.LockInfo;
import com.baomidou.lock.LockTemplate;
import com.baomidou.lock.executor.RedissonLockExecutor;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.portfolio.constant.PortfolioConstants;
import org.dromara.portfolio.domain.PortfolioProject;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;
import org.dromara.portfolio.mapper.PortfolioProjectMapper;
import org.dromara.portfolio.service.IPortfolioProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 作品集项目服务实现：缓存、分布式锁、异步浏览量
 */
@Slf4j
@Service
public class PortfolioProjectServiceImpl implements IPortfolioProjectService {

    private final PortfolioProjectMapper baseMapper;
    private final LockTemplate lockTemplate;
    private final ExecutorService portfolioExecutor;

    public PortfolioProjectServiceImpl(
        PortfolioProjectMapper baseMapper,
        LockTemplate lockTemplate,
        @Qualifier("portfolioExecutor") ExecutorService portfolioExecutor) {
        this.baseMapper = baseMapper;
        this.lockTemplate = lockTemplate;
        this.portfolioExecutor = portfolioExecutor;
    }

    @Override
    public TableDataInfo<PortfolioProjectVo> selectPage(PortfolioProjectBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<PortfolioProject> lqw = buildQueryWrapper(bo);
        lqw.orderByDesc(PortfolioProject::getCreateTime);
        Page<PortfolioProjectVo> page = baseMapper.selectVoPage(pageQuery.build(), lqw);
        fillProjectLikeCounts(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public PortfolioProject getById(Long id) {
        if (id == null) {
            return null;
        }
        String cacheKey = PortfolioConstants.REDIS_DETAIL_PREFIX + id;
        PortfolioProject cached = RedisUtils.getCacheObject(cacheKey);
        if (cached != null) {
            recordViewAsync(id);
            return cached;
        }
        String lockName = PortfolioConstants.LOCK_DETAIL_LOAD_PREFIX + id;
        LockInfo lockInfo = lockTemplate.lock(
            lockName,
            PortfolioConstants.LOCK_LEASE_MS,
            PortfolioConstants.LOCK_ACQUIRE_MS,
            RedissonLockExecutor.class);
        if (lockInfo == null) {
            PortfolioProject retry = RedisUtils.getCacheObject(cacheKey);
            if (retry != null) {
                recordViewAsync(id);
                return retry;
            }
            PortfolioProject loaded = loadAndCacheDetail(id, cacheKey);
            recordViewAsync(id);
            return loaded;
        }
        try {
            PortfolioProject again = RedisUtils.getCacheObject(cacheKey);
            if (again != null) {
                recordViewAsync(id);
                return again;
            }
            PortfolioProject loaded = loadAndCacheDetail(id, cacheKey);
            recordViewAsync(id);
            return loaded;
        } finally {
            lockTemplate.releaseLock(lockInfo);
        }
    }

    private PortfolioProject loadAndCacheDetail(Long id, String cacheKey) {
        PortfolioProject db = baseMapper.selectById(id);
        if (db != null) {
            RedisUtils.setCacheObject(cacheKey, db, PortfolioConstants.DETAIL_CACHE_TTL);
        }
        return db;
    }

    /**
     * 异步累加浏览量，避免阻塞详情接口 RT；高并发下热点行仍会有行锁竞争，线上可改为 Redis 计数 + 定时批量刷库。
     */
    private void recordViewAsync(Long id) {
        portfolioExecutor.execute(() -> {
            try {
                baseMapper.incrViewCount(id, 1);
            } catch (Exception e) {
                log.warn("异步增加浏览量失败 projectId={}", id, e);
            }
        });
    }

    @Override
    public PortfolioProjectVo getDetailVo(Long id) {
        PortfolioProject entity = getById(id);
        if (entity == null) {
            return null;
        }
        PortfolioProjectVo vo = MapstructUtils.convert(entity, PortfolioProjectVo.class);
        fillProjectLike(vo);
        return vo;
    }

    @Override
    public PortfolioProjectVo getVoForAdmin(Long id) {
        if (id == null) {
            return null;
        }
        PortfolioProjectVo vo = baseMapper.selectVoById(id);
        fillProjectLike(vo);
        return vo;
    }

    @Override
    public List<PortfolioProjectVo> selectHotList(int limit) {
        int n = Math.min(Math.max(limit, 1), 50);
        LambdaQueryWrapper<PortfolioProject> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(PortfolioProject::getViewCount);
        Page<PortfolioProjectVo> page = baseMapper.selectVoPage(new Page<>(1, n), lqw);
        List<PortfolioProjectVo> records = page.getRecords();
        fillProjectLikeCounts(records);
        return records;
    }

    @Override
    public List<PortfolioProjectVo> selectRecentList(int limit) {
        int n = Math.min(Math.max(limit, 1), 50);
        LambdaQueryWrapper<PortfolioProject> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(PortfolioProject::getCreateTime);
        Page<PortfolioProjectVo> page = baseMapper.selectVoPage(new Page<>(1, n), lqw);
        List<PortfolioProjectVo> records = page.getRecords();
        fillProjectLikeCounts(records);
        return records;
    }

    @Override
    public List<PortfolioProjectVo> searchByName(String keyword, int limit) {
        if (StringUtils.isBlank(keyword)) {
            return List.of();
        }
        int n = Math.min(Math.max(limit, 1), 30);
        LambdaQueryWrapper<PortfolioProject> lqw = Wrappers.lambdaQuery();
        lqw.like(PortfolioProject::getName, keyword.trim());
        lqw.orderByDesc(PortfolioProject::getCreateTime);
        Page<PortfolioProjectVo> page = baseMapper.selectVoPage(new Page<>(1, n), lqw);
        List<PortfolioProjectVo> records = page.getRecords();
        fillProjectLikeCounts(records);
        return records;
    }

    @Override
    public long likeProject(Long projectId, String voterKey) {
        if (projectId == null) {
            throw new ServiceException("参数错误");
        }
        if (baseMapper.selectById(projectId) == null) {
            throw new ServiceException("项目不存在");
        }
        String safe = sanitizeVoterKey(voterKey);
        String voterRedisKey = PortfolioConstants.REDIS_PROJECT_LIKE_VOTER_PREFIX + projectId + ":" + safe;
        if (RedisUtils.setObjectIfAbsent(voterRedisKey, "1", PortfolioConstants.LIKE_VOTER_TTL)) {
            RedisUtils.incrAtomicValue(PortfolioConstants.REDIS_PROJECT_LIKE_COUNT_PREFIX + projectId);
        }
        return projectLikeCount(projectId);
    }

    private static String sanitizeVoterKey(String voterKey) {
        if (voterKey == null || voterKey.isBlank()) {
            return "unknown";
        }
        return voterKey.replace(':', '_');
    }

    private static long projectLikeCount(Long projectId) {
        return RedisUtils.getAtomicValue(PortfolioConstants.REDIS_PROJECT_LIKE_COUNT_PREFIX + projectId);
    }

    private static void fillProjectLike(PortfolioProjectVo vo) {
        if (vo != null && vo.getId() != null) {
            vo.setLikeCount(projectLikeCount(vo.getId()));
        }
    }

    private static void fillProjectLikeCounts(List<PortfolioProjectVo> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (PortfolioProjectVo vo : list) {
            fillProjectLike(vo);
        }
    }

    @Override
    public int insert(PortfolioProjectBo bo) {
        PortfolioProject row = MapstructUtils.convert(bo, PortfolioProject.class);
        if (row.getViewCount() == null) {
            row.setViewCount(0L);
        }
        return baseMapper.insert(row);
    }

    @Override
    public int update(PortfolioProjectBo bo) {
        evictDetailCache(bo.getId());
        PortfolioProject row = MapstructUtils.convert(bo, PortfolioProject.class);
        return baseMapper.updateById(row);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        ids.forEach(this::evictDetailCache);
        return baseMapper.deleteByIds(ids);
    }

    private LambdaQueryWrapper<PortfolioProject> buildQueryWrapper(PortfolioProjectBo bo) {
        LambdaQueryWrapper<PortfolioProject> lqw = Wrappers.lambdaQuery();
        if (bo != null) {
            lqw.like(StringUtils.isNotBlank(bo.getName()), PortfolioProject::getName, bo.getName());
            lqw.like(StringUtils.isNotBlank(bo.getTechStack()), PortfolioProject::getTechStack, bo.getTechStack());
        }
        return lqw;
    }

    private void evictDetailCache(Long id) {
        if (id != null) {
            RedisUtils.deleteObject(PortfolioConstants.REDIS_DETAIL_PREFIX + id);
        }
    }
}
