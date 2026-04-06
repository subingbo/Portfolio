package org.dromara.portfolio.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.portfolio.domain.bo.PortfolioProjectBo;
import org.dromara.portfolio.domain.vo.PortfolioProjectVo;
import org.dromara.portfolio.service.IPortfolioProjectService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 作品集后台维护（需登录与菜单权限）
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/project")
public class PortfolioProjectController extends BaseController {

    private final IPortfolioProjectService portfolioProjectService;

    @SaCheckPermission("portfolio:project:list")
    @GetMapping("/list")
    public TableDataInfo<PortfolioProjectVo> list(PortfolioProjectBo bo, PageQuery pageQuery) {
        return portfolioProjectService.selectPage(bo, pageQuery);
    }

    @SaCheckPermission("portfolio:project:query")
    @GetMapping("/{id}")
    public R<PortfolioProjectVo> getInfo(@PathVariable Long id) {
        return R.ok(portfolioProjectService.getVoForAdmin(id));
    }

    @SaCheckPermission("portfolio:project:add")
    @Log(title = "作品集项目", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody PortfolioProjectBo bo) {
        return toAjax(portfolioProjectService.insert(bo) > 0);
    }

    @SaCheckPermission("portfolio:project:edit")
    @Log(title = "作品集项目", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(PortfolioProjectBo.EditGroup.class) @RequestBody PortfolioProjectBo bo) {
        return toAjax(portfolioProjectService.update(bo) > 0);
    }

    @SaCheckPermission("portfolio:project:remove")
    @Log(title = "作品集项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(portfolioProjectService.deleteByIds(idList) > 0);
    }
}
