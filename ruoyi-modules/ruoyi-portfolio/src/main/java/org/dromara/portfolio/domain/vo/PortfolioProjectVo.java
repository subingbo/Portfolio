package org.dromara.portfolio.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.portfolio.domain.PortfolioProject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 作品集项目视图对象
 */
@Data
@AutoMapper(target = PortfolioProject.class)
public class PortfolioProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private String techStack;
    private String githubUrl;
    private String demoUrl;
    private String coverImage;
    private Long viewCount;
    private Date createTime;
    private Date updateTime;
}
