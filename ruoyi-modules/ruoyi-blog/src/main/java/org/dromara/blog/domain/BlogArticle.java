package org.dromara.blog.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 博客文章 blog_article
 *
 * @author ruoyi-blog
 */
@Data
@TableName("blog_article")
public class BlogArticle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private String title;

    /** Markdown 正文 */
    private String content;

    private String summary;

    private String cover;

    private Long categoryId;

    /**
     * 0 草稿 1 发布
     */
    private Integer status;

    private Long viewCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
