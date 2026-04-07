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
 * 博客评论 blog_comment
 *
 * @author ruoyi-blog
 */
@Data
@TableName("blog_comment")
public class BlogComment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private Long articleId;

    private String content;

    private String nickname;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
