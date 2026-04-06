-- ----------------------------
-- 个人作品集 / 项目展示表
-- ----------------------------

DROP TABLE IF EXISTS `portfolio_project`;
CREATE TABLE `portfolio_project` (
    `id`           bigint       NOT NULL COMMENT '主键',
    `name`         varchar(128) NOT NULL COMMENT '项目名称',
    `description`  text         NULL COMMENT '项目描述',
    `tech_stack`   varchar(512) NULL COMMENT '技术栈（如 JSON 或逗号分隔）',
    `github_url`   varchar(512) NULL COMMENT '源码地址',
    `demo_url`     varchar(512) NULL COMMENT '演示地址',
    `cover_image`  varchar(512) NULL COMMENT '封面图 URL',
    `view_count`   bigint       NOT NULL DEFAULT 0 COMMENT '浏览量（落库累加，可配合 Redis 缓冲）',
    `create_time`  datetime     NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime     NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '作品集项目表';

-- 索引说明：
-- 1. PRIMARY KEY (id)：详情查询、浏览量更新均按主键，聚簇索引即可支撑高并发单行读写。
-- 2. 若列表常按「创建时间倒序」分页，可增加二级索引；避免在低选择性列上建过多索引以免拖累写入。
CREATE INDEX `idx_portfolio_create_time` ON `portfolio_project` (`create_time` DESC);
