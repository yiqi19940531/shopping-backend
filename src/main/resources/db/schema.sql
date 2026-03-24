-- ============================================
-- Shopping Backend Database Schema
-- ============================================

DROP TABLE IF EXISTS tb_order_item;
DROP TABLE IF EXISTS tb_order;
DROP TABLE IF EXISTS tb_cart_item;
DROP TABLE IF EXISTS tb_product_image;
DROP TABLE IF EXISTS tb_product;
DROP TABLE IF EXISTS tb_category;
DROP TABLE IF EXISTS tb_address;
DROP TABLE IF EXISTS tb_file_storage;
DROP TABLE IF EXISTS tb_user;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE tb_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '加密密码',
    nickname    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    email       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar_id   BIGINT       DEFAULT NULL COMMENT '头像文件ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色(USER/ADMIN)',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除(0否/1是)',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- 文件存储表
-- ----------------------------
CREATE TABLE tb_file_storage (
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    file_name      VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_size      BIGINT       NOT NULL COMMENT '文件大小(字节)',
    file_type      VARCHAR(50)  NOT NULL COMMENT '文件MIME类型',
    file_extension VARCHAR(10)  DEFAULT NULL COMMENT '文件扩展名',
    file_data      MEDIUMBLOB   NOT NULL COMMENT '文件二进制数据',
    uploader_id    BIGINT       DEFAULT NULL COMMENT '上传者ID',
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    is_deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文件存储表';

-- ----------------------------
-- 收货地址表
-- ----------------------------
CREATE TABLE tb_address (
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT       NOT NULL COMMENT '用户ID',
    receiver_name   VARCHAR(50)  NOT NULL COMMENT '收货人姓名',
    receiver_phone  VARCHAR(20)  NOT NULL COMMENT '收货人电话',
    province        VARCHAR(50)  NOT NULL COMMENT '省份',
    city            VARCHAR(50)  NOT NULL COMMENT '城市',
    district        VARCHAR(50)  NOT NULL COMMENT '区县',
    detail_address  VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否默认(0否/1是)',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收货地址表';

-- ----------------------------
-- 商品分类表
-- ----------------------------
CREATE TABLE tb_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(100) NOT NULL COMMENT '分类名称',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类ID(0表示顶级)',
    level       TINYINT      NOT NULL DEFAULT 1 COMMENT '层级(1/2)',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    icon_id     BIGINT       DEFAULT NULL COMMENT '图标文件ID',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态(0禁用/1启用)',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_parent (parent_id, status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品分类表';

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE tb_product (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    spu_no          VARCHAR(50)   NOT NULL COMMENT '商品编号',
    name            VARCHAR(200)  NOT NULL COMMENT '商品名称',
    category_id     BIGINT        NOT NULL COMMENT '分类ID',
    brand           VARCHAR(100)  DEFAULT NULL COMMENT '品牌',
    price           DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price  DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    stock           INT           NOT NULL DEFAULT 0 COMMENT '库存数量',
    sales           INT           NOT NULL DEFAULT 0 COMMENT '销量',
    cover_image_id  BIGINT        DEFAULT NULL COMMENT '封面图片文件ID',
    description     TEXT          DEFAULT NULL COMMENT '简要描述',
    detail          LONGTEXT      DEFAULT NULL COMMENT '富文本详情',
    status          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态(0下架/1上架)',
    is_hot          TINYINT       NOT NULL DEFAULT 0 COMMENT '是否热门',
    is_recommend    TINYINT       NOT NULL DEFAULT 0 COMMENT '是否推荐',
    create_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_spu_no (spu_no),
    KEY idx_category (category_id, status, is_deleted),
    KEY idx_hot_recommend (is_hot, is_recommend, status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表';

-- ----------------------------
-- 商品图片表
-- ----------------------------
CREATE TABLE tb_product_image (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    product_id  BIGINT   NOT NULL COMMENT '商品ID',
    file_id     BIGINT   NOT NULL COMMENT '文件ID',
    sort_order  INT      NOT NULL DEFAULT 0 COMMENT '排序序号',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted  TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_product (product_id, is_deleted, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品图片表';

-- ----------------------------
-- 购物车表
-- ----------------------------
CREATE TABLE tb_cart_item (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    product_id  BIGINT   NOT NULL COMMENT '商品ID',
    quantity    INT      NOT NULL DEFAULT 1 COMMENT '数量',
    is_selected TINYINT  NOT NULL DEFAULT 1 COMMENT '是否选中(0否/1是)',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_user (user_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购物车表';

-- ----------------------------
-- 订单表
-- ----------------------------
CREATE TABLE tb_order (
    id               BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no         VARCHAR(64)   NOT NULL COMMENT '订单号',
    user_id          BIGINT        NOT NULL COMMENT '用户ID',
    total_amount     DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    payment_amount   DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    status           VARCHAR(20)   NOT NULL COMMENT '订单状态',
    receiver_name    VARCHAR(50)   NOT NULL COMMENT '收货人姓名',
    receiver_phone   VARCHAR(20)   NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(500)  NOT NULL COMMENT '收货地址',
    payment_time     DATETIME      DEFAULT NULL COMMENT '支付时间',
    ship_time        DATETIME      DEFAULT NULL COMMENT '发货时间',
    tracking_no      VARCHAR(100)  DEFAULT NULL COMMENT '物流单号',
    receive_time     DATETIME      DEFAULT NULL COMMENT '收货时间',
    cancel_time      DATETIME      DEFAULT NULL COMMENT '取消时间',
    cancel_reason    VARCHAR(255)  DEFAULT NULL COMMENT '取消原因',
    remark           VARCHAR(500)  DEFAULT NULL COMMENT '订单备注',
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user (user_id, is_deleted),
    KEY idx_status (status, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单表';

-- ----------------------------
-- 订单明细表
-- ----------------------------
CREATE TABLE tb_order_item (
    id                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id          BIGINT        NOT NULL COMMENT '订单ID',
    product_id        BIGINT        NOT NULL COMMENT '商品ID',
    product_name      VARCHAR(200)  NOT NULL COMMENT '商品名称快照',
    product_image_url VARCHAR(500)  DEFAULT NULL COMMENT '商品图片URL快照',
    price             DECIMAL(10,2) NOT NULL COMMENT '商品单价快照',
    quantity          INT           NOT NULL COMMENT '购买数量',
    total_amount      DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    create_time       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    is_deleted        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_order (order_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';
