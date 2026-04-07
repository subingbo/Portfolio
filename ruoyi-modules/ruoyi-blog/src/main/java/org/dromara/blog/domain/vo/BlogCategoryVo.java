package org.dromara.blog.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.blog.domain.BlogCategory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 博客分类视图对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogCategory.class)
public class BlogCategoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer sort;
    private String description;
    private Date createTime;
    private Date updateTime;
}
