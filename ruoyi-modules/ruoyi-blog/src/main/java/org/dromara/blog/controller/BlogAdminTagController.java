package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.bo.BlogTagBo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.blog.service.IBlogTagService;
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
 * 博客标签：后台 CRUD。
 * <p>基路径：{@code /blog/admin/tag}；权限前缀 {@code blog:tag:*}。</p>
 * <p>前台只读列表见 {@link BlogFrontTagController}。</p>
 *
 * @author ruoyi-blog
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/admin/tag")
public class BlogAdminTagController extends BaseController {

    private final IBlogTagService tagService;

    /**
     * 分页查询标签。
     *
     * @param bo        查询条件
     * @param pageQuery 分页
     * @return 分页数据
     */
    @SaCheckPermission("blog:tag:list")
    @GetMapping("/list")
    public TableDataInfo<BlogTagVo> list(BlogTagBo bo, PageQuery pageQuery) {
        return tagService.selectPage(bo, pageQuery);
    }

    /**
     * 按 id 查询标签。
     *
     * @param id 主键
     * @return {@code R.ok(vo)}
     */
    @SaCheckPermission("blog:tag:query")
    @GetMapping("/{id}")
    public R<BlogTagVo> getInfo(@PathVariable Long id) {
        return R.ok(tagService.getVoById(id));
    }

    /**
     * 新增标签。
     *
     * @param bo 请求体
     * @return 写操作结果
     */
    @SaCheckPermission("blog:tag:add")
    @Log(title = "博客标签", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody BlogTagBo bo) {
        return toAjax(tagService.insert(bo) > 0);
    }

    /**
     * 修改标签。
     *
     * @param bo 请求体（{@link BlogTagBo.EditGroup}）
     * @return 写操作结果
     */
    @SaCheckPermission("blog:tag:edit")
    @Log(title = "博客标签", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(BlogTagBo.EditGroup.class) @RequestBody BlogTagBo bo) {
        return toAjax(tagService.update(bo) > 0);
    }

    /**
     * 批量删除标签。
     *
     * @param ids 主键数组
     * @return 写操作结果
     */
    @SaCheckPermission("blog:tag:remove")
    @Log(title = "博客标签", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(tagService.deleteByIds(idList) > 0);
    }
}
