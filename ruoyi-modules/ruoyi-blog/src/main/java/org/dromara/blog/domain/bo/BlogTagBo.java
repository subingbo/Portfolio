package org.dromara.blog.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.blog.domain.BlogTag;

import java.io.Serial;
import java.io.Serializable;

/**
 * 博客标签业务对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogTag.class, reverseConvertGenerate = false)
public class BlogTagBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    private String name;

    public interface EditGroup {
    }
}
