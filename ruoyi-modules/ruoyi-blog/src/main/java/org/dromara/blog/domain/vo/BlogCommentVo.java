package org.dromara.blog.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.blog.domain.BlogComment;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 评论视图对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogComment.class)
public class BlogCommentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long articleId;
    private String content;
    private String nickname;
    private Date createTime;
}
