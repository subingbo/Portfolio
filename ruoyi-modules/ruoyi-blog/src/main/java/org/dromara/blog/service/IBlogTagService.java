package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogTagBo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客标签服务：标签主数据维护及文章-标签多对多的一向展示列表。
 *
 * @author ruoyi-blog
 */
public interface IBlogTagService {

    /**
     * 管理端分页查询标签。
     *
     * @param bo        条件
     * @param pageQuery 分页
     * @return 分页 VO
     */
    TableDataInfo<BlogTagVo> selectPage(BlogTagBo bo, PageQuery pageQuery);

    /**
     * 按主键查询标签 VO。
     *
     * @param id 主键
     * @return 标签 VO
     */
    BlogTagVo getVoById(Long id);

    /**
     * 新增标签。
     *
     * @param bo 业务对象
     * @return 受影响行数
     */
    int insert(BlogTagBo bo);

    /**
     * 更新标签。
     *
     * @param bo 业务对象（含主键）
     * @return 受影响行数
     */
    int update(BlogTagBo bo);

    /**
     * 批量删除标签。
     * <p>通常需同步清理 {@code blog_article_tag} 关联，由实现类处理。</p>
     *
     * @param ids 主键列表
     * @return 受影响行数
     */
    int deleteByIds(List<Long> ids);

    /**
     * 前台：全部标签列表，用于侧边栏或筛选器。
     *
     * @return 标签 VO 列表
     */
    List<BlogTagVo> selectListForFront();
}
