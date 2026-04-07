package org.dromara.blog.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.blog.domain.BlogArticle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 博客文章业务对象（后台维护）
 *
 * @author ruoyi-blog
 */
@Data
@AutoMapper(target = BlogArticle.class, reverseConvertGenerate = false)
public class BlogArticleBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long id;

    @NotBlank(message = "标题不能为空")
    private String title;

    /** Markdown 正文 */
    private String content;

    private String summary;

    private String cover;

    private Long categoryId;

    /**
     * 0 草稿 1 发布
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 关联标签 ID 列表（可选，全量覆盖）
     */
    private List<Long> tagIds;

    public interface EditGroup {
    }
}
