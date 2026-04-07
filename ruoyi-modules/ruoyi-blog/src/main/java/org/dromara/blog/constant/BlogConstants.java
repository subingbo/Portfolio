package org.dromara.blog.constant;

/**
 * 博客模块常量
 *
 * @author ruoyi-blog
 */
public final class BlogConstants {

    private BlogConstants() {
    }

    /** 草稿 */
    public static final int STATUS_DRAFT = 0;

    /** 已发布（前台可见） */
    public static final int STATUS_PUBLISHED = 1;

    /** 前台热门/最新列表默认条数上限 */
    public static final int FRONT_LIST_MAX = 50;

    /** 前台热门/最新默认条数 */
    public static final int FRONT_LIST_DEFAULT = 10;
}
