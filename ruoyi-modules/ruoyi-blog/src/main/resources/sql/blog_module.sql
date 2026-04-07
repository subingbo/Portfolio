-- ----------------------------
-- 个人的博客模块表结构（MySQL 8+）
-- 执行前请确认库名与字符集，可按需放进若依初始化脚本
-- ----------------------------

CREATE TABLE IF NOT EXISTS blog_category (
    id          BIGINT       NOT NULL COMMENT '主键',
    name        VARCHAR(100) NOT NULL COMMENT '分类名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序（升序）',
    description VARCHAR(500)          DEFAULT NULL COMMENT '描述',
    create_time DATETIME                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客分类';

CREATE TABLE IF NOT EXISTS blog_tag (
    id          BIGINT      NOT NULL COMMENT '主键',
    name        VARCHAR(64) NOT NULL COMMENT '标签名称',
    create_time DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_tag_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客标签';

CREATE TABLE IF NOT EXISTS blog_article (
    id           BIGINT        NOT NULL COMMENT '主键',
    title        VARCHAR(255)  NOT NULL COMMENT '标题',
    content      MEDIUMTEXT             COMMENT '正文（Markdown）',
    summary      VARCHAR(500)           DEFAULT NULL COMMENT '摘要',
    cover        VARCHAR(512)           DEFAULT NULL COMMENT '封面图URL',
    category_id  BIGINT                 DEFAULT NULL COMMENT '分类ID',
    status       TINYINT       NOT NULL DEFAULT 0 COMMENT '0草稿 1已发布',
    view_count   BIGINT        NOT NULL DEFAULT 0 COMMENT '浏览量',
    create_time  DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time  DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id),
    KEY idx_status_time (status, create_time),
    KEY idx_title (title)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='博客文章';

CREATE TABLE IF NOT EXISTS blog_article_tag (
    id         BIGINT NOT NULL COMMENT '主键',
    article_id BIGINT NOT NULL COMMENT '文章ID',
    tag_id     BIGINT NOT NULL COMMENT '标签ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_article_tag (article_id, tag_id),
    KEY idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章标签关联';

CREATE TABLE IF NOT EXISTS blog_comment (
    id           BIGINT       NOT NULL COMMENT '主键',
    article_id   BIGINT       NOT NULL COMMENT '文章ID',
    content      VARCHAR(2000) NOT NULL COMMENT '评论内容',
    nickname     VARCHAR(64)  NOT NULL COMMENT '昵称',
    create_time  DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文章评论';
