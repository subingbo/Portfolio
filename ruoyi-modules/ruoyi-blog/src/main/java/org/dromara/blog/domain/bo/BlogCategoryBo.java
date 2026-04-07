package org.dromara.blog.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.blog.domain.BlogCategory;

import java.io.Serial;
import java.io.Serializable;

/**
 * 博客分类业务对象
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogCategory.class, reverseConvertGenerate = false)
public class BlogCategoryBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    @NotBlank(message = "分类名称不能为空")
    private String name;

    private Integer sort;

    private String description;

    /**
     * 修改校验分组
     */
    public interface EditGroup {
    }
}
