package org.dromara.blog.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.dromara.blog.domain.BlogComment;

import java.io.Serial;
import java.io.Serializable;

/**
 * 前台发表评论业务对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogComment.class, reverseConvertGenerate = false)
public class BlogCommentBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "文章ID不能为空")
    private Long articleId;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 2000, message = "评论内容不能超过2000个字符")
    private String content;
}
