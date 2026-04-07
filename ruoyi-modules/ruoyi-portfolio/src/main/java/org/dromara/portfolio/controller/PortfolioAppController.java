package org.dromara.portfolio.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.redis.utils.RedisSlidingWindow;
import org.dromara.common.web.core.BaseController;
import org.dromara.portfolio.constant.PortfolioConstants;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;
import org.dromara.portfolio.service.IPortfolioProjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作品集项目：公开访问接口（站外/访客场景）。
 * <p>
 * 基路径：{@code /portfolio/app/project}。类级别 {@link SaIgnore} 跳过登录校验；
 * 须在 {@code application.yml} 的 {@code security.excludes} 中配置 {@code /portfolio/app/**}。
 * </p>
 * <p>
 * 列表/详情等查询经 Redis 滑动窗口按 IP 限流；点赞为 Redis 计数。
 * </p>
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/app/project")
public class PortfolioAppController extends BaseController {

    private final IPortfolioProjectService portfolioProjectService;

    /**
     * 公开分页列表。
     *
     * @param bo        筛选条件（与后台 list 相同 Bo，具体展示字段可由业务过滤）
     * @param pageQuery 分页参数
     * @return 分页 {@link PortfolioProjectVo} 列表
     */
    @GetMapping("/list")
    public TableDataInfo<PortfolioProjectVo> list(PortfolioProjectBo bo, PageQuery pageQuery) {
        requirePortfolioSliding("list");
        return portfolioProjectService.selectPage(bo, pageQuery);
    }

    /**
     * 公开详情。
     *
     * @param id 项目主键
     * @return 存在则 {@code R.ok(vo)}，否则 {@code R.fail("项目不存在")}
     */
    @GetMapping("/{id}")
    public R<PortfolioProjectVo> detail(@PathVariable Long id) {
        requirePortfolioSliding("detail");
        PortfolioProjectVo vo = portfolioProjectService.getDetailVo(id);
        return vo != null ? R.ok(vo) : R.fail("项目不存在");
    }

    /**
     * 热门项目（一般按浏览量降序取前 N 条）。
     *
     * @param limit 条数上限，默认 10
     * @return {@code R.ok(List<PortfolioProjectVo>)}
     */
    @GetMapping("/hot")
    public R<List<PortfolioProjectVo>> hot(@RequestParam(defaultValue = "10") int limit) {
        requirePortfolioSliding("hot");
        return R.ok(portfolioProjectService.selectHotList(limit));
    }

    /**
     * 最近发布/更新时间排序的推荐列表（具体排序以服务实现为准）。
     *
     * @param limit 条数上限，默认 10
     * @return {@code R.ok(List<PortfolioProjectVo>)}
     */
    @GetMapping("/recent")
    public R<List<PortfolioProjectVo>> recent(@RequestParam(defaultValue = "10") int limit) {
        requirePortfolioSliding("recent");
        return R.ok(portfolioProjectService.selectRecentList(limit));
    }

    /**
     * 按项目名称等关键词的简单模糊搜索。
     *
     * @param keyword 搜索关键词，必填
     * @param limit   最大返回条数，默认 10
     * @return 匹配的项目列表，无匹配时可能为空列表（非错误）
     */
    @GetMapping("/search")
    public R<List<PortfolioProjectVo>> search(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "10") int limit) {
        requirePortfolioSliding("search");
        return R.ok(portfolioProjectService.searchByName(keyword, limit));
    }

    /**
     * 点赞项目；同一 IP 在 Redis TTL 内仅首次有效，返回当前总赞数。
     *
     * @param id 项目主键
     * @return {@code R.ok(likeCount)}
     */
    @PostMapping("/{id}/like")
    public R<Long> like(@PathVariable Long id) {
        requirePortfolioSliding("like");
        long total = portfolioProjectService.likeProject(id, ServletUtils.getClientIP());
        return R.ok(total);
    }

    private static void requirePortfolioSliding(String endpoint) {
        String ip = ServletUtils.getClientIP();
        String key = PortfolioConstants.REDIS_APP_SLIDING_PREFIX + endpoint + ":" + ip;
        if (!RedisSlidingWindow.tryAcquire(key, PortfolioConstants.APP_SLIDING_MAX, PortfolioConstants.APP_SLIDING_WINDOW)) {
            throw new ServiceException("访问过于频繁，请稍后再试");
        }
    }
}
