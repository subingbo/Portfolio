package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.bo.BlogCommentBo;
import org.dromara.blog.domain.vo.BlogCommentVo;
import org.dromara.blog.service.IBlogCommentService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 博客评论：前台发表与分页查看。
 * <p>基路径：{@code /blog/front/comment}；{@link SaIgnore} 匿名可访问。</p>
 * <p>若业务需要登录后评论，可在服务层或后续版本中接入用户上下文校验。</p>
 *
 * @author ruoyi-blog
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/front/comment")
public class BlogFrontCommentController extends BaseController {

    private final IBlogCommentService commentService;

    /**
     * 提交一条评论。
     * <p>防短时间重复提交（{@link RepeatSubmit}）。请求体需通过 {@link BlogCommentBo} 校验，
     * 至少包含目标文章 id 与评论内容等字段。</p>
     *
     * @param bo 评论业务对象
     * @return 写操作结果
     */
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogCommentBo bo) {
        return toAjax(commentService.add(bo) > 0);
    }

    /**
     * 按文章分页查询评论列表（一般仅展示审核通过或全部，以服务与实体状态为准）。
     *
     * @param articleId 文章主键，必填
     * @param pageQuery 分页参数
     * @return 分页 {@link BlogCommentVo}
     */
    @GetMapping("/list")
    public TableDataInfo<BlogCommentVo> list(
        @RequestParam Long articleId,
        PageQuery pageQuery) {
        return commentService.selectPageByArticle(articleId, pageQuery);
    }
}
