-- ============================================
-- Shopping Backend Import Data with Images
-- ============================================
-- 注意：此脚本需要配合 Python 脚本使用，先运行 Python 脚本上传二进制图片获取 ID
-- 本文件包含完整的测试数据 INSERT 语句

-- ----------------------------
-- 用户数据 (密码为 admin123/user123 的 BCrypt 哈希)
-- ----------------------------
INSERT INTO tb_user (id, username, password, nickname, phone, role) VALUES
(1, 'admin', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '管理员', '13800000000', 'ADMIN'),
(2, 'user1', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '张三', '13800000001', 'USER'),
(3, 'user2', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '李四', '13800000002', 'USER');

-- ----------------------------
-- 收货地址数据
-- ----------------------------
INSERT INTO tb_address (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, 2, '张三', '13800000001', '广东省', '深圳市', '南山区', '科技园南路 100 号 A 栋 1501', 1),
(2, 2, '张三', '13800000001', '广东省', '广州市', '天河区', '天河路 385 号太古汇', 0);

-- ----------------------------
-- 商品分类数据
-- ----------------------------
INSERT INTO tb_category (id, name, parent_id, level, sort_order) VALUES
(1, '数码电器', 0, 1, 1),
(2, '服装鞋包', 0, 1, 2),
(3, '食品饮料', 0, 1, 3),
(4, '手机', 1, 2, 1),
(5, '电脑', 1, 2, 2),
(6, '男装', 2, 2, 1),
(7, '女装', 2, 2, 2),
(8, '零食', 3, 2, 1),
(9, '饮品', 3, 2, 2);

-- ----------------------------
-- 商品数据 (cover_image_id 待 Python 脚本替换)
-- ----------------------------
-- 以下为占位符，实际 ID 由 Python 脚本读取图片后动态生成
-- INSERT INTO tb_product ... 将由 Python 脚本生成
