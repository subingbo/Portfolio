package org.dromara.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.BlogArticle;
import org.dromara.blog.domain.BlogCategory;
import org.dromara.blog.domain.bo.BlogCategoryBo;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.blog.mapper.BlogArticleMapper;
import org.dromara.blog.mapper.BlogCategoryMapper;
import org.dromara.blog.service.IBlogCategoryService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 博客分类 Service 实现
 *
 * @author ruoyi-blog
 */
@RequiredArgsConstructor
@Service
public class BlogCategoryServiceImpl implements IBlogCategoryService {

    private final BlogCategoryMapper categoryMapper;
    private final BlogArticleMapper articleMapper;

    @Override
    public TableDataInfo<BlogCategoryVo> selectPage(BlogCategoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogCategory> lqw = buildWrapper(bo);
        lqw.orderByAsc(BlogCategory::getSort).orderByAsc(BlogCategory::getId);
        Page<BlogCategoryVo> page = categoryMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public BlogCategoryVo getVoById(Long id) {
        return id == null ? null : categoryMapper.selectVoById(id);
    }

    @Override
    public int insert(BlogCategoryBo bo) {
        BlogCategory row = MapstructUtils.convert(bo, BlogCategory.class);
        if (row.getSort() == null) {
            row.setSort(0);
        }
        return categoryMapper.insert(row);
    }

    @Override
    public int update(BlogCategoryBo bo) {
        BlogCategory row = MapstructUtils.convert(bo, BlogCategory.class);
        return categoryMapper.updateById(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return 0;
        }
        for (Long id : ids) {
            long cnt = articleMapper.selectCount(
                Wrappers.<BlogArticle>lambdaQuery().eq(BlogArticle::getCategoryId, id));
            if (cnt > 0) {
                throw new ServiceException("分类下仍存在文章，无法删除");
            }
        }
        return categoryMapper.deleteByIds(ids);
    }

    @Override
    public List<BlogCategoryVo> selectListForFront() {
        LambdaQueryWrapper<BlogCategory> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlogCategory::getSort).orderByAsc(BlogCategory::getId);
        return categoryMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlogCategory> buildWrapper(BlogCategoryBo bo) {
        LambdaQueryWrapper<BlogCategory> lqw = Wrappers.lambdaQuery();
        if (bo != null) {
            lqw.like(StringUtils.isNotBlank(bo.getName()), BlogCategory::getName, bo.getName());
        }
        return lqw;
    }
}
