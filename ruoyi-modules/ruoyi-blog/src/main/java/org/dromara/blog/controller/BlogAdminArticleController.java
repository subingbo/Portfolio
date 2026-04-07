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
 * 博客文章：后台管理接口。
 * <p>
 * 基路径：{@code /blog/admin/article}。需登录，方法级权限由 {@link SaCheckPermission} 声明
 * （与 Spring Security 的 {@code @PreAuthorize} 类似，均为“接口级授权”）。
 * </p>
 * <p>
 * 后台列表包含草稿、待发布等全部状态；与前台 {@link BlogFrontArticleController} 仅“已发布”不同。
 * </p>
 *
 * @author ruoyi-blog
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/admin/article")
public class BlogAdminArticleController extends BaseController {

    private final IBlogArticleService articleService;

    /**
     * 后台分页查询文章。
     *
     * @param bo        筛选条件（标题、分类、状态等，见 {@link BlogArticleBo}）
     * @param pageQuery 分页参数
     * @return 分页 {@link BlogArticleVo}
     */
    @SaCheckPermission("blog:article:list")
    @GetMapping("/list")
    public TableDataInfo<BlogArticleVo> list(BlogArticleBo bo, PageQuery pageQuery) {
        return articleService.selectAdminPage(bo, pageQuery);
    }

    /**
     * 后台查看单篇文章（不区分是否已发布）。
     *
     * @param id 文章主键
     * @return {@code R.ok(vo)}
     */
    @SaCheckPermission("blog:article:query")
    @GetMapping("/{id}")
    public R<BlogArticleVo> getInfo(@PathVariable Long id) {
        return R.ok(articleService.getAdminVo(id));
    }

    /**
     * 新增文章。
     * <p>权限 {@code blog:article:add}；写操作日志 + 防重复提交。</p>
     *
     * @param bo 请求体
     * @return 标准写操作结果
     */
    @SaCheckPermission("blog:article:add")
    @Log(title = "博客文章", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogArticleBo bo) {
        return toAjax(articleService.insertByBo(bo) > 0);
    }

    /**
     * 修改文章。
     * <p>权限 {@code blog:article:edit}；校验分组 {@link BlogArticleBo.EditGroup}（含主键）。</p>
     *
     * @param bo 请求体
     * @return 标准写操作结果
     */
    @SaCheckPermission("blog:article:edit")
    @Log(title = "博客文章", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(BlogArticleBo.EditGroup.class) @RequestBody BlogArticleBo bo) {
        return toAjax(articleService.updateByBo(bo) > 0);
    }

    /**
     * 批量删除文章。
     *
     * @param ids 路径中的多主键
     * @return 标准写操作结果
     */
    @SaCheckPermission("blog:article:remove")
    @Log(title = "博客文章", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(articleService.deleteByIds(idList) > 0);
    }
}
