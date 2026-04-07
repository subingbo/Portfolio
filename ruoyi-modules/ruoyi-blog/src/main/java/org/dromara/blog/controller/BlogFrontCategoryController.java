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
 * 博客分类前台接口
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

    @GetMapping("/list")
    public R<List<BlogCategoryVo>> list() {
        return R.ok(categoryService.selectListForFront());
    }
}
