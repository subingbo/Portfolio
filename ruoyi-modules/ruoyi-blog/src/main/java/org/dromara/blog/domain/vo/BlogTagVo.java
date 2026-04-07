package org.dromara.blog.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.blog.domain.BlogTag;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 博客标签视图对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogTag.class)
public class BlogTagVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Date createTime;
    private Date updateTime;
}
