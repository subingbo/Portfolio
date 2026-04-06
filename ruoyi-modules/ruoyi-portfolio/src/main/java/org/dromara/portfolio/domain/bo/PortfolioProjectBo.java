package org.dromara.portfolio.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.dromara.portfolio.domain.PortfolioProject;

import java.io.Serial;
import java.io.Serializable;

/**
 * 作品集项目业务对象
 */
@Data
@AutoMapper(target = PortfolioProject.class, reverseConvertGenerate = false)
public class PortfolioProjectBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "主键不能为空", groups = { EditGroup.class })
    private Long id;

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String description;
    private String techStack;
    private String githubUrl;
    private String demoUrl;
    private String coverImage;

    /**
     * 校验分组：修改
     */
    public interface EditGroup {
    }
}
