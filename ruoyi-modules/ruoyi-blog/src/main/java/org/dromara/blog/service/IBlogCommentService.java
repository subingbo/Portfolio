package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogCommentBo;
import org.dromara.blog.domain.vo.BlogCommentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 博客评论 Service（前台）
 *
 * @author ruoyi-blog
 */
public interface IBlogCommentService {

    int add(BlogCommentBo bo);

    TableDataInfo<BlogCommentVo> selectPageByArticle(Long articleId, PageQuery pageQuery);
}
