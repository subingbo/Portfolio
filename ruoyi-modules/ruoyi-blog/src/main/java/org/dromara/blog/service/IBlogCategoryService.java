package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogCategoryBo;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客分类服务：树形/扁平分类的后台维护与前台下拉列表。
 *
 * @author ruoyi-blog
 */
public interface IBlogCategoryService {

    /**
     * 管理端分页查询分类。
     *
     * @param bo        条件
     * @param pageQuery 分页
     * @return 分页 VO
     */
    TableDataInfo<BlogCategoryVo> selectPage(BlogCategoryBo bo, PageQuery pageQuery);

    /**
     * 按主键查询分类 VO。
     *
     * @param id 主键
     * @return 分类 VO
     */
    BlogCategoryVo getVoById(Long id);

    /**
     * 新增分类。
     *
     * @param bo 业务对象
     * @return 受影响行数
     */
    int insert(BlogCategoryBo bo);

    /**
     * 更新分类。
     *
     * @param bo 业务对象（含主键）
     * @return 受影响行数
     */
    int update(BlogCategoryBo bo);

    /**
     * 批量删除分类。
     * <p>若分类下仍有关联文章，实现类可能拒绝删除或级联处理，以具体实现为准。</p>
     *
     * @param ids 主键列表
     * @return 受影响行数
     */
    int deleteByIds(List<Long> ids);

    /**
     * 前台：返回全部启用分类，顺序一般为 {@code sort} 升序再 {@code id}。
     *
     * @return 分类列表，无分页
     */
    List<BlogCategoryVo> selectListForFront();
}
