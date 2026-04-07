package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.bo.BlogCategoryBo;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.blog.service.IBlogCategoryService;
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
 * 博客分类后台接口
 *
 * @author ruoyi-blog
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/admin/category")
public class BlogAdminCategoryController extends BaseController {

    private final IBlogCategoryService categoryService;

    @SaCheckPermission("blog:category:list")
    @GetMapping("/list")
    public TableDataInfo<BlogCategoryVo> list(BlogCategoryBo bo, PageQuery pageQuery) {
        return categoryService.selectPage(bo, pageQuery);
    }

    @SaCheckPermission("blog:category:query")
    @GetMapping("/{id}")
    public R<BlogCategoryVo> getInfo(@PathVariable Long id) {
        return R.ok(categoryService.getVoById(id));
    }

    @SaCheckPermission("blog:category:add")
    @Log(title = "博客分类", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogCategoryBo bo) {
        return toAjax(categoryService.insert(bo) > 0);
    }

    @SaCheckPermission("blog:category:edit")
    @Log(title = "博客分类", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(BlogCategoryBo.EditGroup.class) @RequestBody BlogCategoryBo bo) {
        return toAjax(categoryService.update(bo) > 0);
    }

    @SaCheckPermission("blog:category:remove")
    @Log(title = "博客分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(categoryService.deleteByIds(idList) > 0);
    }
}
