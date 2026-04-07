package org.dromara.blog.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.blog.domain.BlogArticle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 博客文章视图对象（后台列表/详情与前台列表/详情共用，扩展字段按需填充）
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogArticle.class)
public class BlogArticleVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String cover;
    private Long categoryId;
    private Integer status;
    private Long viewCount;
    private Date createTime;
    private Date updateTime;

    /** 分类名称（非表字段，业务填充） */
    private String categoryName;

    /** 标签 ID 列表（后台详情/编辑回显） */
    private List<Long> tagIds;

    /** 标签明细（前台展示） */
    private List<BlogTagVo> tags;
}
