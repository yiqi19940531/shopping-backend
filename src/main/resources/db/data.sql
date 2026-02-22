-- ============================================
-- Shopping Backend Test Data
-- ============================================
-- Passwords are BCrypt encoded (admin123 / user123):
--   Generated via Spring Security BCryptPasswordEncoder

-- ----------------------------
-- 用户数据
-- ----------------------------
INSERT INTO tb_user (id, username, password, nickname, phone, role) VALUES
(1, 'admin', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '管理员', '13800000000', 'ADMIN'),
(2, 'user1', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '张三', '13800000001', 'USER'),
(3, 'user2', '$2a$10$XgY6t0bsgFQNn4rrACcbXubirycxrcnNPdhJcDbSa2wxOBl.llpzi', '李四', '13800000002', 'USER');

-- ----------------------------
-- 收货地址数据
-- ----------------------------
INSERT INTO tb_address (id, user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, 2, '张三', '13800000001', '广东省', '深圳市', '南山区', '科技园南路100号A栋1501', 1),
(2, 2, '张三', '13800000001', '广东省', '广州市', '天河区', '天河路385号太古汇', 0);

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
-- 商品数据
-- ----------------------------
INSERT INTO tb_product (id, spu_no, name, category_id, brand, price, original_price, stock, sales, description, detail, is_hot, is_recommend) VALUES
(1,  'SPU20240001', 'iPhone 15 Pro Max 256GB',    4, 'Apple',    9999.00,  10999.00, 100, 520, 'Apple iPhone 15 Pro Max，钛金属设计，A17 Pro芯片', '<h2>iPhone 15 Pro Max</h2><p>全新钛金属设计，更轻更坚固。A17 Pro芯片带来突破性的性能表现。</p>', 1, 1),
(2,  'SPU20240002', 'Samsung Galaxy S24 Ultra',    4, 'Samsung',  8999.00,  9499.00,  80,  310, 'Samsung Galaxy S24 Ultra，AI智能手机', '<h2>Galaxy S24 Ultra</h2><p>Galaxy AI，改变你使用手机的方式。钛金属边框设计。</p>', 1, 1),
(3,  'SPU20240003', 'Huawei Mate 60 Pro',          4, 'Huawei',   6999.00,  7499.00,  120, 450, '华为Mate 60 Pro，鸿蒙操作系统', '<h2>Mate 60 Pro</h2><p>超可靠昆仑玻璃，卫星通话功能，麒麟芯片。</p>', 1, 0),
(4,  'SPU20240004', 'MacBook Pro 14英寸 M3 Pro',   5, 'Apple',    14999.00, 16499.00, 50,  180, 'MacBook Pro 14英寸，M3 Pro芯片', '<h2>MacBook Pro 14"</h2><p>M3 Pro芯片，18小时续航，Liquid Retina XDR显示屏。</p>', 1, 1),
(5,  'SPU20240005', 'ThinkPad X1 Carbon Gen 11',   5, 'Lenovo',   9999.00,  11999.00, 60,  95,  'ThinkPad X1 Carbon，商务旗舰', '<h2>ThinkPad X1 Carbon</h2><p>轻至1.12kg，14英寸2.8K OLED屏幕，Intel 13代酷睿。</p>', 0, 1),
(6,  'SPU20240006', '纯棉商务休闲衬衫',            6, '优衣库',   199.00,   299.00,   500, 1200, '高品质纯棉面料，舒适透气', '<h2>纯棉商务休闲衬衫</h2><p>100%优质长绒棉，免烫工艺，多色可选。</p>', 0, 1),
(7,  'SPU20240007', '轻薄羽绒服男款',              6, '波司登',   599.00,   899.00,   300, 800, '90%白鹅绒填充，轻盈保暖', '<h2>轻薄羽绒服</h2><p>90%白鹅绒，蓬松度700+，防风防泼水面料。</p>', 1, 0),
(8,  'SPU20240008', '法式碎花连衣裙',              7, 'ZARA',     399.00,   599.00,   200, 650, '法式优雅碎花连衣裙', '<h2>法式碎花连衣裙</h2><p>浪漫法式印花，收腰设计，飘逸裙摆。</p>', 0, 1),
(9,  'SPU20240009', '小香风针织开衫',              7, 'CHANEL',   12999.00, 15000.00, 30,  45,  '经典小香风针织开衫', '<h2>小香风针织开衫</h2><p>经典编织工艺，珍珠纽扣，优雅知性。</p>', 0, 0),
(10, 'SPU20240010', '三只松鼠坚果礼盒',            8, '三只松鼠', 128.00,   168.00,   1000, 3500, '8袋坚果组合大礼包', '<h2>坚果礼盒</h2><p>精选8种坚果，每日坚果搭配，健康美味。</p>', 1, 1),
(11, 'SPU20240011', '良品铺子肉脯零食',            8, '良品铺子', 59.90,    79.90,    800, 2100, '猪肉脯精选大包装', '<h2>猪肉脯</h2><p>精选猪后腿肉，炭火烘烤，酥香可口。</p>', 0, 0),
(12, 'SPU20240012', '农夫山泉矿泉水24瓶',          9, '农夫山泉', 39.90,    49.90,    2000, 8000, '天然矿泉水550ml*24瓶', '<h2>农夫山泉</h2><p>天然弱碱性水，源自优质水源地。</p>', 0, 0),
(13, 'SPU20240013', '星巴克冷萃咖啡礼盒',          9, '星巴克',   188.00,   228.00,   150, 420, '精选冷萃咖啡8罐装', '<h2>冷萃咖啡礼盒</h2><p>星巴克甄选咖啡豆冷萃，醇厚顺滑。</p>', 0, 1),
(14, 'SPU20240014', 'Xiaomi 14 Pro',               4, '小米',     4999.00,  5299.00,  200, 680, '小米14 Pro，徕卡光学镜头', '<h2>Xiaomi 14 Pro</h2><p>骁龙8 Gen 3，徕卡Summilux镜头，2K护眼屏。</p>', 1, 0),
(15, 'SPU20240015', 'Dell XPS 15 OLED',            5, 'Dell',     12999.00, 14999.00, 40,  72,  'Dell XPS 15 OLED触控屏笔记本', '<h2>Dell XPS 15</h2><p>15.6英寸3.5K OLED触控屏，Intel Core i9处理器。</p>', 0, 0);
