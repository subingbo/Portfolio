package org.dromara.portfolio.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 作品集项目实体 portfolio_project
 */
@Data
@TableName("portfolio_project")
public class PortfolioProject implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private String name;

    private String description;

    /** 技术栈描述 */
    private String techStack;

    private String githubUrl;

    private String demoUrl;

    private String coverImage;

    private Long viewCount;

    private Date createTime;

    private Date updateTime;
}
