package org.dromara.blog.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文章与标签关联 blog_article_tag
 *
 * @author ruoyi-blog
 */
@Data
@TableName("blog_article_tag")
public class BlogArticleTag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private Long articleId;

    private Long tagId;
}
