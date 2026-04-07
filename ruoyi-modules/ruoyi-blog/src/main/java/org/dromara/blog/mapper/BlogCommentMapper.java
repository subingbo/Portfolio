package org.dromara.blog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.dromara.blog.domain.BlogComment;
import org.dromara.blog.domain.vo.BlogCommentVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 博客评论 Mapper
 *
 * @author ruoyi-blog
 */
@InterceptorIgnore(tenantLine = "true")
public interface BlogCommentMapper extends BaseMapperPlus<BlogComment, BlogCommentVo> {

}
