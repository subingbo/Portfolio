package org.dromara.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.BlogArticleTag;
import org.dromara.blog.domain.BlogTag;
import org.dromara.blog.domain.bo.BlogTagBo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.blog.mapper.BlogArticleTagMapper;
import org.dromara.blog.mapper.BlogTagMapper;
import org.dromara.blog.service.IBlogTagService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 博客标签 Service 实现
 *
 * @author ruoyi-blog
 */
@RequiredArgsConstructor
@Service
public class BlogTagServiceImpl implements IBlogTagService {

    private final BlogTagMapper tagMapper;
    private final BlogArticleTagMapper articleTagMapper;

    @Override
    public TableDataInfo<BlogTagVo> selectPage(BlogTagBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogTag> lqw = buildWrapper(bo);
        lqw.orderByAsc(BlogTag::getName);
        Page<BlogTagVo> page = tagMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public BlogTagVo getVoById(Long id) {
        return id == null ? null : tagMapper.selectVoById(id);
    }

    @Override
    public int insert(BlogTagBo bo) {
        BlogTag row = MapstructUtils.convert(bo, BlogTag.class);
        return tagMapper.insert(row);
    }

    @Override
    public int update(BlogTagBo bo) {
        BlogTag row = MapstructUtils.convert(bo, BlogTag.class);
        return tagMapper.updateById(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        articleTagMapper.delete(Wrappers.<BlogArticleTag>lambdaQuery().in(BlogArticleTag::getTagId, ids));
        return tagMapper.deleteByIds(ids);
    }

    @Override
    public List<BlogTagVo> selectListForFront() {
        LambdaQueryWrapper<BlogTag> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlogTag::getName);
        return tagMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlogTag> buildWrapper(BlogTagBo bo) {
        LambdaQueryWrapper<BlogTag> lqw = Wrappers.lambdaQuery();
        if (bo != null) {
            lqw.like(StringUtils.isNotBlank(bo.getName()), BlogTag::getName, bo.getName());
        }
        return lqw;
    }
}
