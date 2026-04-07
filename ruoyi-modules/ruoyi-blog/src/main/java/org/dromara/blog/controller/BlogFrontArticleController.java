package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.constant.BlogConstants;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.blog.service.IBlogArticleService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisSlidingWindow;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客文章：前台只读接口（访客场景）。
 * <p>
 * 基路径：{@code /blog/front/article}。仅对外展示<strong>已发布</strong>文章。
 * 类级别 {@link SaIgnore}；须在 {@code security.excludes} 中放行 {@code /blog/front/**}。
 * </p>
 * <p>
 * 列表/详情等查询经 Redis 滑动窗口按 IP 限流；点赞为 Redis 计数。
 * </p>
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
     * 已发布文章分页列表。
     * <p>支持按标题模糊、分类 id、标签 id 组合筛选；分页由 {@link PageQuery} 传入。</p>
     *
     * @param title      标题关键词，可选
     * @param categoryId 分类主键，可选
     * @param tagId      标签主键，可选
     * @param pageQuery  分页参数
     * @return 分页 {@link BlogArticleVo}
     */
    @GetMapping("/list")
    public TableDataInfo<BlogArticleVo> list(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) Long tagId,
        PageQuery pageQuery) {
        requireBlogFrontSliding("list");
        return articleService.selectPublishedPage(title, categoryId, tagId, pageQuery);
    }

    /**
     * 热门文章（按浏览量等指标，以服务实现为准）。
     * <p>{@code limit} 会被限制在 [{@code 1}, {@link BlogConstants#FRONT_LIST_MAX}] 区间内，防止过大查询。</p>
     *
     * @param limit 期望条数，默认 10
     * @return {@code R.ok(List)}
     */
    @GetMapping("/hot")
    public R<List<BlogArticleVo>> hot(@RequestParam(defaultValue = "10") int limit) {
        requireBlogFrontSliding("hot");
        int n = Math.min(Math.max(limit, 1), BlogConstants.FRONT_LIST_MAX);
        return R.ok(articleService.selectPublishedHot(n));
    }

    /**
     * 最新文章（一般按创建时间降序）。
     *
     * @param limit 期望条数，默认 10，同样受 {@link BlogConstants#FRONT_LIST_MAX} 约束
     * @return {@code R.ok(List)}
     */
    @GetMapping("/latest")
    public R<List<BlogArticleVo>> latest(@RequestParam(defaultValue = "10") int limit) {
        requireBlogFrontSliding("latest");
        int n = Math.min(Math.max(limit, 1), BlogConstants.FRONT_LIST_MAX);
        return R.ok(articleService.selectPublishedLatest(n));
    }

    /**
     * 点赞已发布文章；同一 IP 在 Redis TTL 内仅首次有效，返回当前总赞数。
     *
     * @param id 文章主键
     * @return {@code R.ok(likeCount)}
     */
    @PostMapping("/{id}/like")
    public R<Long> like(@PathVariable Long id) {
        requireBlogFrontSliding("like");
        long total = articleService.likePublishedArticle(id, ServletUtils.getClientIP());
        return R.ok(total);
    }

    /**
     * 已发布文章详情；通常会触发浏览量 +1（见服务实现）。
     *
     * @param id 文章主键
     * @return 成功 {@code R.ok(vo)}；未发布或不存在 {@code R.fail("文章不存在或未发布")}
     */
    @GetMapping("/{id}")
    public R<BlogArticleVo> detail(@PathVariable Long id) {
        requireBlogFrontSliding("detail");
        BlogArticleVo vo = articleService.getPublishedDetail(id);
        return vo != null ? R.ok(vo) : R.fail("文章不存在或未发布");
    }

    private static void requireBlogFrontSliding(String endpoint) {
        String ip = ServletUtils.getClientIP();
        String key = BlogConstants.REDIS_SLIDING_KEY_PREFIX + endpoint + ":" + ip;
        if (!RedisSlidingWindow.tryAcquire(key, BlogConstants.FRONT_SLIDING_MAX, BlogConstants.FRONT_SLIDING_WINDOW)) {
            throw new ServiceException("访问过于频繁，请稍后再试");
        }
    }
}
