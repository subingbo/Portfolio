package org.dromara.blog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.blog.domain.BlogArticleTag;

/**
 * 文章标签关联 Mapper
 *
 * @author ruoyi-blog
 */
@InterceptorIgnore(tenantLine = "true")
public interface BlogArticleTagMapper extends BaseMapper<BlogArticleTag> {

}
