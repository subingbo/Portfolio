package org.dromara.blog.service;

import org.dromara.blog.domain.bo.BlogTagBo;
import org.dromara.blog.domain.vo.BlogTagVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 博客标签 Service
 *
 * @author ruoyi-blog
 */
public interface IBlogTagService {

    TableDataInfo<BlogTagVo> selectPage(BlogTagBo bo, PageQuery pageQuery);

    BlogTagVo getVoById(Long id);

    int insert(BlogTagBo bo);

    int update(BlogTagBo bo);

    int deleteByIds(List<Long> ids);

    /**
     * 前台：全部标签
     */
    List<BlogTagVo> selectListForFront();
}
