package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.constant.BlogConstants;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.blog.service.IBlogArticleService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客文章前台接口（仅已发布文章，无需登录；已在 security.excludes 放行 /blog/front/**）
 *
 * @author ruoyi-blog
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/front/article")
public class BlogFrontArticleController extends BaseController {

    private final IBlogArticleService articleService;

    /**
     * 分页列表，可按标题（模糊）、分类、标签筛选
     */
    @GetMapping("/list")
    public TableDataInfo<BlogArticleVo> list(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long tagId,
        PageQuery pageQuery) {
        return articleService.selectPublishedPage(title, categoryId, tagId, pageQuery);
    }

    /**
     * 热门（按浏览量）
     */
    @GetMapping("/hot")
    public R<List<BlogArticleVo>> hot(@RequestParam(defaultValue = "10") int limit) {
        int n = Math.min(Math.max(limit, 1), BlogConstants.FRONT_LIST_MAX);
        return R.ok(articleService.selectPublishedHot(n));
    }

    /**
     * 最新（按创建时间）
     */
    @GetMapping("/latest")
    public R<List<BlogArticleVo>> latest(@RequestParam(defaultValue = "10") int limit) {
        int n = Math.min(Math.max(limit, 1), BlogConstants.FRONT_LIST_MAX);
        return R.ok(articleService.selectPublishedLatest(n));
    }

    @GetMapping("/{id}")
    public R<BlogArticleVo> detail(@PathVariable Long id) {
        BlogArticleVo vo = articleService.getPublishedDetail(id);
        return vo != null ? R.ok(vo) : R.fail("文章不存在或未发布");
    }
}
