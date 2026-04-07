package org.dromara.portfolio.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.portfolio.domain.PortfolioProject;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;

import java.util.List;

/**
 * 作品集项目领域服务。
 * <p>封装项目 CRUD、前台展示扩展查询及详情缓存/浏览量等行为，供后台 Controller 与前台
 * {@code PortfolioAppController} 调用。</p>
 */
public interface IPortfolioProjectService {

    /**
     * 条件分页查询，返回 VO 列表。
     * <p>管理端列表与公开列表均可复用；若需对外隐藏字段，可在实现层组装或过滤。</p>
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页封装结果
     */
    TableDataInfo<PortfolioProjectVo> selectPage(PortfolioProjectBo bo, PageQuery pageQuery);

    /**
     * 按主键加载实体（含高并发场景优化）。
     * <p>典型实现：Redis 缓存、防缓存击穿（互斥锁）、异步/延迟合并增加浏览量等。
     * 返回 {@link PortfolioProject} 便于内部再转 VO 或更新字段。</p>
     *
     * @param id 主键
     * @return 实体，不存在时为 {@code null}
     */
    PortfolioProject getById(Long id);

    /**
     * 前台详情 VO。
     * <p>在 {@link #getById(Long)} 或等价加载基础上转换为 {@link PortfolioProjectVo}，
     * 并应用对外展示策略（浏览量、缓存等）。</p>
     *
     * @param id 主键
     * @return 详情 VO，未找到为 {@code null}
     */
    PortfolioProjectVo getDetailVo(Long id);

    /**
     * 后台管理用详情 VO。
     * <p>不累计对外浏览量、不走仅面向访客的缓存键策略，保证运营看到的数据即时、一致。</p>
     *
     * @param id 主键
     * @return 详情 VO
     */
    PortfolioProjectVo getVoForAdmin(Long id);

    /**
     * 热门 Top N（通常按 {@code view_count} 降序）。
     *
     * @param limit 最大条数
     * @return VO 列表，可能少于 limit
     */
    List<PortfolioProjectVo> selectHotList(int limit);

    /**
     * 最近动态 Top N（通常按创建或发布时间降序，以实现类为准）。
     *
     * @param limit 最大条数
     * @return VO 列表
     */
    List<PortfolioProjectVo> selectRecentList(int limit);

    /**
     * 按名称（或标题字段）模糊匹配，用于简单搜索框。
     *
     * @param keyword 关键词
     * @param limit   最大返回条数
     * @return 匹配结果列表
     */
    List<PortfolioProjectVo> searchByName(String keyword, int limit);

    /**
     * 新增项目。
     *
     * @param bo 业务对象
     * @return 受影响行数，成功一般为 1
     */
    int insert(PortfolioProjectBo bo);

    /**
     * 更新项目。
     *
     * @param bo 业务对象（须含主键等更新所需字段）
     * @return 受影响行数
     */
    int update(PortfolioProjectBo bo);

    /**
     * 按主键批量物理或逻辑删除（以实现类为准）。
     *
     * @param ids 主键集合
     * @return 受影响行数
     */
    int deleteByIds(List<Long> ids);

    /**
     * 公开点赞：计数存 Redis；同一 {@code voterKey} 在 TTL 内仅首次有效。
     *
     * @param projectId 项目主键
     * @param voterKey  访客标识（建议传客户端 IP）
     * @return 当前点赞总数
     */
    long likeProject(Long projectId, String voterKey);
}
