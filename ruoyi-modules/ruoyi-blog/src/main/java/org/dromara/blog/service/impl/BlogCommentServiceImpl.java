package org.dromara.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.blog.constant.BlogConstants;
import org.dromara.blog.domain.BlogArticle;
import org.dromara.blog.domain.BlogComment;
import org.dromara.blog.domain.bo.BlogCommentBo;
import org.dromara.blog.domain.vo.BlogCommentVo;
import org.dromara.blog.mapper.BlogArticleMapper;
import org.dromara.blog.mapper.BlogCommentMapper;
import org.dromara.blog.service.IBlogCommentService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 博客评论 Service 实现
 *
 * @author ruoyi-blog
 */
@RequiredArgsConstructor
@Service
public class BlogCommentServiceImpl implements IBlogCommentService {

    private final BlogCommentMapper commentMapper;
    private final BlogArticleMapper articleMapper;

    @Override
    public int add(BlogCommentBo bo) {
        BlogArticle article = articleMapper.selectById(bo.getArticleId());
        if (article == null || !Objects.equals(article.getStatus(), BlogConstants.STATUS_PUBLISHED)) {
            throw new ServiceException("文章不存在或未发布");
        }
        BlogComment row = MapstructUtils.convert(bo, BlogComment.class);
        return commentMapper.insert(row);
    }

    @Override
    public TableDataInfo<BlogCommentVo> selectPageByArticle(Long articleId, PageQuery pageQuery) {
        LambdaQueryWrapper<BlogComment> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlogComment::getArticleId, articleId);
        lqw.orderByDesc(BlogComment::getCreateTime);
        Page<BlogCommentVo> page = commentMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }
}
