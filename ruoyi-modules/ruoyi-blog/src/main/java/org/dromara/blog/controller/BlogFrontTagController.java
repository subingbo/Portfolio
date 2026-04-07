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
 * 博客标签：前台只读。
 * <p>基路径：{@code /blog/front/tag}；{@link SaIgnore} 匿名可访问。</p>
 * <p>常用于侧边栏标签云或与文章列表的联动筛选（标签 id 在文章列表接口上传入）。</p>
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

    /**
     * 获取全部标签列表（无分页）。
     *
     * @return {@code R.ok(List<BlogTagVo>)}
     */
    @GetMapping("/list")
    public R<List<BlogTagVo>> list() {
        return R.ok(tagService.selectListForFront());
    }
}
