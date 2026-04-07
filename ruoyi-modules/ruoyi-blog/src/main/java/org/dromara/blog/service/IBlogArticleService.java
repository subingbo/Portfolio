package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogArticleBo;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客文章 Service
 *
 * @author ruoyi-blog
 */
public interface IBlogArticleService {

    /** 后台分页（任意状态，标题 / 分类筛选） */
    TableDataInfo<BlogArticleVo> selectAdminPage(BlogArticleBo bo, PageQuery pageQuery);

    BlogArticleVo getAdminVo(Long id);

    int insertByBo(BlogArticleBo bo);

    int updateByBo(BlogArticleBo bo);

    int deleteByIds(List<Long> ids);

    /**
     * 前台分页：仅 status=1，支持标题、分类、标签
     */
    TableDataInfo<BlogArticleVo> selectPublishedPage(String title, Long categoryId, Long tagId, PageQuery pageQuery);

    /**
     * 前台详情：仅已发布，浏览量 +1
     */
    BlogArticleVo getPublishedDetail(Long id);

    List<BlogArticleVo> selectPublishedHot(int limit);

    List<BlogArticleVo> selectPublishedLatest(int limit);
}
