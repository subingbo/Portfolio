package org.dromara.portfolio.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;
import org.dromara.portfolio.service.IPortfolioProjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 作品集公开接口（无需登录，需在 security.excludes 中放行 /portfolio/app/**）
 */
@SaIgnore
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/app/project")
public class PortfolioAppController extends BaseController {

    private final IPortfolioProjectService portfolioProjectService;

    @GetMapping("/list")
    public TableDataInfo<PortfolioProjectVo> list(PortfolioProjectBo bo, PageQuery pageQuery) {
        return portfolioProjectService.selectPage(bo, pageQuery);
    }

    @GetMapping("/{id}")
    public R<PortfolioProjectVo> detail(@PathVariable Long id) {
        PortfolioProjectVo vo = portfolioProjectService.getDetailVo(id);
        return vo != null ? R.ok(vo) : R.fail("项目不存在");
    }

    @GetMapping("/hot")
    public R<List<PortfolioProjectVo>> hot(@RequestParam(defaultValue = "10") int limit) {
        return R.ok(portfolioProjectService.selectHotList(limit));
    }

    @GetMapping("/recent")
    public R<List<PortfolioProjectVo>> recent(@RequestParam(defaultValue = "10") int limit) {
        return R.ok(portfolioProjectService.selectRecentList(limit));
    }

    @GetMapping("/search")
    public R<List<PortfolioProjectVo>> search(
        @RequestParam String keyword,
        @RequestParam(defaultValue = "10") int limit) {
        return R.ok(portfolioProjectService.searchByName(keyword, limit));
    }
}
