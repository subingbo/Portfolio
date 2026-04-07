package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogArticleBo;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客文章服务：后台全生命周期与前台仅“已发布”读模型。
 * <p>文章与分类、多标签的关联在实现层维护；删除时需考虑关联表一致性。</p>
 *
 * @author ruoyi-blog
 */
public interface IBlogArticleService {

    /**
     * 管理后台分页查询。
     * <p>可查询任意发布状态，支持 Bo 中标题、分类等条件（与 Mapper/XML 一致）。</p>
     *
     * @param bo        查询条件
     * @param pageQuery 分页
     * @return 分页 VO
     */
    TableDataInfo<BlogArticleVo> selectAdminPage(BlogArticleBo bo, PageQuery pageQuery);

    /**
     * 管理端按 id 取详情 VO（含正文等完整字段），不校验是否已发布。
     *
     * @param id 文章主键
     * @return 文章 VO
     */
    BlogArticleVo getAdminVo(Long id);

    /**
     * 根据 Bo 新增文章（含标签 id 列表等扩展字段的处理在实现类完成）。
     *
     * @param bo 业务对象
     * @return 受影响行数
     */
    int insertByBo(BlogArticleBo bo);

    /**
     * 根据 Bo 更新文章。
     *
     * @param bo 业务对象（须含主键）
     * @return 受影响行数
     */
    int updateByBo(BlogArticleBo bo);

    /**
     * 批量删除文章（及可能的标签关联，由实现类保证）。
     *
     * @param ids 主键列表
     * @return 受影响行数
     */
    int deleteByIds(List<Long> ids);

    /**
     * 前台分页：仅 {@code status = 已发布} 的文章。
     *
     * @param title      标题模糊，可 null
     * @param categoryId 分类 id，可 null
     * @param tagId      标签 id（关联表筛选），可 null
     * @param pageQuery  分页
     * @return 分页 VO
     */
    TableDataInfo<BlogArticleVo> selectPublishedPage(String title, Long categoryId, Long tagId, PageQuery pageQuery);

    /**
     * 前台详情：仅已发布；成功访问时通常递增浏览量。
     *
     * @param id 文章主键
     * @return 详情 VO，未发布或不存在返回 {@code null}
     */
    BlogArticleVo getPublishedDetail(Long id);

    /**
     * 已发布热门 Top N（一般按 {@code view_count}）。
     *
     * @param limit 条数上限（调用方已做合理裁剪时仍应与服务上限策略一致）
     * @return VO 列表
     */
    List<BlogArticleVo> selectPublishedHot(int limit);

    /**
     * 已发布最新 Top N（一般按创建时间降序）。
     *
     * @param limit 条数上限
     * @return VO 列表
     */
    List<BlogArticleVo> selectPublishedLatest(int limit);

    /**
     * 前台点赞：计数存 Redis；同一 {@code voterKey}（如客户端 IP）在 TTL 内仅首次有效。
     *
     * @param articleId 文章主键
     * @param voterKey  访客标识（建议传客户端 IP）
     * @return 当前点赞总数
     */
    long likePublishedArticle(Long articleId, String voterKey);
}
