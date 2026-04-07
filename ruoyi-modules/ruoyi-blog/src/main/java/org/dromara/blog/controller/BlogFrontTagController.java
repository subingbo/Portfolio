package org.dromara.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.blog.service.IBlogTagService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客标签前台接口
 *
 * @author ruoyi-blog
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog/front/tag")
public class BlogFrontTagController extends BaseController {

    private final IBlogTagService tagService;

    @GetMapping("/list")
    public R<List<BlogTagVo>> list() {
        return R.ok(tagService.selectListForFront());
    }
}
