package org.dromara.blog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.dromara.blog.domain.BlogArticle;
import org.dromara.blog.domain.vo.BlogArticleVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 博客文章 Mapper
 *
 * @author ruoyi-blog
 */
@InterceptorIgnore(tenantLine = "true")
public interface BlogArticleMapper extends BaseMapperPlus<BlogArticle, BlogArticleVo> {

    /**
     * 原子增加文章浏览量（单行 {@code UPDATE}，避免并发下先读后写丢失更新）。
     *
     * @param id    文章主键
     * @param delta 增量，通常为 1；批量合并场景可大于 1
     * @return 影响行数（未命中行则为 0）
     */
    @Update("UPDATE blog_article SET view_count = view_count + #{delta}, update_time = NOW() WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id, @Param("delta") long delta);
}
