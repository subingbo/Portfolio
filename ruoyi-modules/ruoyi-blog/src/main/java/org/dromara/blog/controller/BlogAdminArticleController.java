package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.bo.BlogArticleBo;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.blog.service.IBlogArticleService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 博客文章后台接口。
 * <p>RuoYi-Vue-Plus 使用 Sa-Token，权限注解为 {@link SaCheckPermission}（与 Spring Security 的 @PreAuthorize 同属“方法级权限”诉求）。</p>
 *
 * @author ruoyi-blog
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/admin/article")
public class BlogAdminArticleController extends BaseController {

    private final IBlogArticleService articleService;

    @SaCheckPermission("blog:article:list")
    @GetMapping("/list")
    public TableDataInfo<BlogArticleVo> list(BlogArticleBo bo, PageQuery pageQuery) {
        return articleService.selectAdminPage(bo, pageQuery);
    }

    @SaCheckPermission("blog:article:query")
    @GetMapping("/{id}")
    public R<BlogArticleVo> getInfo(@PathVariable Long id) {
        return R.ok(articleService.getAdminVo(id));
    }

    @SaCheckPermission("blog:article:add")
    @Log(title = "博客文章", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogArticleBo bo) {
        return toAjax(articleService.insertByBo(bo) > 0);
    }

    @SaCheckPermission("blog:article:edit")
    @Log(title = "博客文章", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(BlogArticleBo.EditGroup.class) @RequestBody BlogArticleBo bo) {
        return toAjax(articleService.updateByBo(bo) > 0);
    }

    @SaCheckPermission("blog:article:remove")
    @Log(title = "博客文章", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(articleService.deleteByIds(idList) > 0);
    }
}
