package org.dromara.portfolio.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.portfolio.domain.PortfolioProject;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;

import java.util.List;

/**
 * 作品集项目服务
 */
public interface IPortfolioProjectService {

    /**
     * 分页列表（管理端 / 公开列表均可）
     */
    TableDataInfo<PortfolioProjectVo> selectPage(PortfolioProjectBo bo, PageQuery pageQuery);

    /**
     * 高并发详情：Redis 缓存 + 防击穿锁 + 异步浏览量
     */
    PortfolioProject getById(Long id);

    /**
     * 详情 VO（含转换）
     */
    PortfolioProjectVo getDetailVo(Long id);

    /**
     * 后台查看详情（不计浏览量、不走公开缓存策略）
     */
    PortfolioProjectVo getVoForAdmin(Long id);

    /**
     * 按浏览量热门 Top N
     */
    List<PortfolioProjectVo> selectHotList(int limit);

    /**
     * 最近发布 Top N
     */
    List<PortfolioProjectVo> selectRecentList(int limit);

    /**
     * 简单搜索（名称模糊）
     */
    List<PortfolioProjectVo> searchByName(String keyword, int limit);

    int insert(PortfolioProjectBo bo);

    int update(PortfolioProjectBo bo);

    int deleteByIds(List<Long> ids);
}
