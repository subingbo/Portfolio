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
 * 博客评论前台接口
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

    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogCommentBo bo) {
        return toAjax(commentService.add(bo) > 0);
    }

    @GetMapping("/list")
    public TableDataInfo<BlogCommentVo> list(
        @RequestParam Long articleId,
        PageQuery pageQuery) {
        return commentService.selectPageByArticle(articleId, pageQuery);
    }
}
