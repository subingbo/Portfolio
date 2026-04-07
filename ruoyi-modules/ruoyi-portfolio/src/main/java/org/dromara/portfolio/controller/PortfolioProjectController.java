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
 * 作品集项目：后台维护接口。
 * <p>
 * 基路径：{@code /portfolio/project}。所有方法需登录且具备对应菜单权限（Sa-Token
 * {@link SaCheckPermission}），不列入 {@code security.excludes}。
 * </p>
 * <p>
 * 与前台 {@link PortfolioAppController} 区分：后台详情走 {@link IPortfolioProjectService#getVoForAdmin(Long)}，
 * 不参与对外展示用的浏览量与缓存策略。
 * </p>
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/portfolio/project")
public class PortfolioProjectController extends BaseController {

    private final IPortfolioProjectService portfolioProjectService;

    /**
     * 分页查询作品集项目。
     *
     * @param bo        查询条件（如名称、状态等，与 Bo 字段绑定）
     * @param pageQuery 分页参数（页码、每页条数等，由框架解析）
     * @return 分页数据 {@link TableDataInfo}{@code <PortfolioProjectVo>}
     */
    @SaCheckPermission("portfolio:project:list")
    @GetMapping("/list")
    public TableDataInfo<PortfolioProjectVo> list(PortfolioProjectBo bo, PageQuery pageQuery) {
        return portfolioProjectService.selectPage(bo, pageQuery);
    }

    /**
     * 按主键查询单条详情（管理端）。
     *
     * @param id 项目主键
     * @return {@code R.ok(vo)}，数据为 {@link PortfolioProjectVo}
     */
    @SaCheckPermission("portfolio:project:query")
    @GetMapping("/{id}")
    public R<PortfolioProjectVo> getInfo(@PathVariable Long id) {
        return R.ok(portfolioProjectService.getVoForAdmin(id));
    }

    /**
     * 新增项目。
     * <p>需权限 {@code portfolio:project:add}；带操作日志与防重复提交。</p>
     *
     * @param bo 请求体，需通过校验注解校验
     * @return 标准写操作结果（经 {@link #toAjax(boolean)}）
     */
    @SaCheckPermission("portfolio:project:add")
    @Log(title = "作品集项目", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody PortfolioProjectBo bo) {
        return toAjax(portfolioProjectService.insert(bo) > 0);
    }

    /**
     * 修改项目。
     * <p>需权限 {@code portfolio:project:edit}；{@code bo} 使用 {@link PortfolioProjectBo.EditGroup} 校验（通常含 id）。</p>
     *
     * @param bo 请求体
     * @return 标准写操作结果
     */
    @SaCheckPermission("portfolio:project:edit")
    @Log(title = "作品集项目", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(PortfolioProjectBo.EditGroup.class) @RequestBody PortfolioProjectBo bo) {
        return toAjax(portfolioProjectService.update(bo) > 0);
    }

    /**
     * 批量删除项目。
     * <p>路径 {@code /portfolio/project/{ids}}，{@code ids} 为逗号分隔的多个主键（框架解析为数组）。</p>
     *
     * @param ids 主键数组
     * @return 标准写操作结果
     */
    @SaCheckPermission("portfolio:project:remove")
    @Log(title = "作品集项目", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@PathVariable Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return toAjax(portfolioProjectService.deleteByIds(idList) > 0);
    }
}
