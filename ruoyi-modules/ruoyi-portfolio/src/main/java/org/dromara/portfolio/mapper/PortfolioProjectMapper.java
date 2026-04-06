package org.dromara.portfolio.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.dromara.portfolio.domain.PortfolioProject;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;

/**
 * 作品集项目 Mapper
 */
@InterceptorIgnore(tenantLine = "true")
public interface PortfolioProjectMapper extends BaseMapperPlus<PortfolioProject, PortfolioProjectVo> {

    /**
     * 原子增加浏览量（单行 UPDATE，避免先查后改）
     *
     * @param id    项目主键
     * @param delta 增量，通常为 1；批量合并时可大于 1
     * @return 影响行数
     */
    @Update("UPDATE portfolio_project SET view_count = view_count + #{delta}, update_time = NOW() WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id, @Param("delta") long delta);
}
