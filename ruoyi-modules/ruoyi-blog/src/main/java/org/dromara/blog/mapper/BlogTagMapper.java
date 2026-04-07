package org.dromara.blog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.dromara.blog.domain.BlogTag;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 博客标签 Mapper
 *
 * @author ruoyi-blog
 */
@InterceptorIgnore(tenantLine = "true")
public interface BlogTagMapper extends BaseMapperPlus<BlogTag, BlogTagVo> {

}
