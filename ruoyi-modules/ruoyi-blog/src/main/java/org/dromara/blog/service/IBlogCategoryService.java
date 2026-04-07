package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogCategoryBo;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客分类 Service
 *
 * @author ruoyi-blog
 */
public interface IBlogCategoryService {

    TableDataInfo<BlogCategoryVo> selectPage(BlogCategoryBo bo, PageQuery pageQuery);

    BlogCategoryVo getVoById(Long id);

    int insert(BlogCategoryBo bo);

    int update(BlogCategoryBo bo);

    int deleteByIds(List<Long> ids);

    /**
     * 前台：全部分类（按 sort、id 排序）
     */
    List<BlogCategoryVo> selectListForFront();
}
