package org.dromara.blog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.dromara.blog.domain.BlogCategory;
import org.dromara.blog.domain.vo.BlogCategoryVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 博客分类 Mapper
 *
 * @author ruoyi-blog
 */
@InterceptorIgnore(tenantLine = "true")
public interface BlogCategoryMapper extends BaseMapperPlus<BlogCategory, BlogCategoryVo> {

}
