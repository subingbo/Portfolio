package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.blog.service.IBlogCategoryService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客分类：前台只读。
 * <p>基路径：{@code /blog/front/category}；{@link SaIgnore} 匿名可访问。</p>
 * <p>返回全部分类列表，排序规则见 {@link IBlogCategoryService#selectListForFront()}。</p>
 *
 * @author ruoyi-blog
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/front/category")
public class BlogFrontCategoryController extends BaseController {

    private final IBlogCategoryService categoryService;

    /**
     * 获取前台展示用分类全表（无分页，体量由运营控制）。
     *
     * @return {@code R.ok(List<BlogCategoryVo>)}
     */
    @GetMapping("/list")
    public R<List<BlogCategoryVo>> list() {
        return R.ok(categoryService.selectListForFront());
    }
}
