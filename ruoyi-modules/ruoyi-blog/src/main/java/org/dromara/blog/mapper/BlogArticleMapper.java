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
     * 原子增加浏览量
     */
    @Update("UPDATE blog_article SET view_count = view_count + #{delta}, update_time = NOW() WHERE id = #{id}")
    int incrViewCount(@Param("id") Long id, @Param("delta") long delta);
}
