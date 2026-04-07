package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogCommentBo;
import org.dromara.blog.domain.vo.BlogCommentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 博客评论服务（当前仅前台场景：发表 + 按文章分页查询）。
 * <p>后台审核、删除若未单独提供 Controller，可在此接口扩展方法。</p>
 *
 * @author ruoyi-blog
 */
public interface IBlogCommentService {

    /**
     * 新增一条评论。
     * <p>应校验目标文章存在且允许评论；可记录访客昵称、邮箱等非登录字段（见 Bo）。</p>
     *
     * @param bo 评论内容及相关 id
     * @return 受影响行数，成功一般为 1
     */
    int add(BlogCommentBo bo);

    /**
     * 按文章主键分页查询评论。
     *
     * @param articleId 文章 id
     * @param pageQuery 分页参数
     * @return 分页评论 VO
     */
    TableDataInfo<BlogCommentVo> selectPageByArticle(Long articleId, PageQuery pageQuery);
}
