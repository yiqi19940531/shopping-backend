#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Upload product images to database and generate data.sql
"""

import mysql.connector
import os
from datetime import datetime

# Database connection
conn = mysql.connector.connect(
    host='120.77.200.122',
    user='root',
    password='RootPassw0rd!',
    database='appdb',
    charset='utf8mb4'
)
cursor = conn.cursor()

# Image paths mapping
images = [
    ('iphone-15-pro-max.png', 'iPhone 15 Pro Max 产品图'),
    ('samsung-s24-ultra.png', 'Samsung S24 Ultra 产品图'),
    ('macbook-pro-14.png', 'MacBook Pro 14 产品图'),
    ('mens-casual-shirt.png', '男士衬衫产品图'),
    ('womens-dress.png', '女士连衣裙产品图'),
    ('nuts-gift-box.png', '坚果礼盒产品图'),
]

base_path = '/Users/zhangyiqi/Desktop/qoder/quest/shopping-backend/vibe_images'

print("开始上传图片到数据库...")
file_ids = []

for filename, description in images:
    # Find the actual file (with timestamp in name)
    matching_file = None
    for f in os.listdir(base_path):
        if f.startswith(filename.replace('.png', '')) and f.endswith('.png'):
            matching_file = f
            break
    
    if not matching_file:
        print(f"❌ 未找到文件：{filename}")
        continue
    
    filepath = os.path.join(base_path, matching_file)
    
    # Read image file
    with open(filepath, 'rb') as img_file:
        img_data = img_file.read()
    
    file_size = len(img_data)
    
    # Insert into tb_file_storage
    cursor.execute("""
        INSERT INTO tb_file_storage 
        (file_name, file_size, file_type, file_extension, file_data, uploader_id)
        VALUES (%s, %s, %s, %s, %s, %s)
    """, (
        f"{description}.png",
        file_size,
        'image/png',
        'png',
        img_data,
        1  # admin user ID
    ))
    
    file_id = cursor.lastrowid
    file_ids.append(file_id)
    print(f"✅ 已上传：{matching_file} -> ID: {file_id}, 大小：{file_size/1024:.1f}KB")

conn.commit()

# Generate product data SQL
print("\n开始插入商品数据...")

products = [
    # id, spu_no, name, category_id, brand, price, original_price, stock, sales,
    # cover_image_id, description, detail, is_hot, is_recommend
    (1, 'SPU20240001', 'iPhone 15 Pro Max 256GB', 4, 'Apple', 9999.00, 10999.00, 100, 520,
     file_ids[0],
     'Apple iPhone 15 Pro Max，钛金属设计，A17 Pro 芯片', 
     '<h2>iPhone 15 Pro Max</h2><p>全新钛金属设计，更轻更坚固。A17 Pro 芯片带来突破性的性能表现。</p>', 
     1, 1),
    
    (2, 'SPU20240002', 'Samsung Galaxy S24 Ultra', 4, 'Samsung', 8999.00, 9499.00, 80, 310,
     file_ids[1],
     'Samsung Galaxy S24 Ultra，AI 智能手机',
     '<h2>Galaxy S24 Ultra</h2><p>Galaxy AI，改变你使用手机的方式。钛金属边框设计。</p>',
     1, 1),
    
    (3, 'SPU20240003', 'Huawei Mate 60 Pro', 4, 'Huawei', 6999.00, 7499.00, 120, 450,
     file_ids[0],  # 复用 iPhone 图片作为占位
     '华为 Mate 60 Pro，鸿蒙操作系统',
     '<h2>Mate 60 Pro</h2><p>超可靠昆仑玻璃，卫星通话功能，麒麟芯片。</p>',
     1, 0),
    
    (4, 'SPU20240004', 'MacBook Pro 14 英寸 M3 Pro', 5, 'Apple', 14999.00, 16499.00, 50, 180,
     file_ids[2],
     'MacBook Pro 14 英寸，M3 Pro 芯片',
     '<h2>MacBook Pro 14"</h2><p>M3 Pro 芯片，18 小时续航，Liquid Retina XDR 显示屏。</p>',
     1, 1),
    
    (5, 'SPU20240005', 'ThinkPad X1 Carbon Gen 11', 5, 'Lenovo', 9999.00, 11999.00, 60, 95,
     file_ids[2],  # 复用 MacBook 图片作为占位
     'ThinkPad X1 Carbon，商务旗舰',
     '<h2>ThinkPad X1 Carbon</h2><p>轻至 1.12kg，14 英寸 2.8K OLED 屏幕，Intel 13 代酷睿。</p>',
     0, 1),
    
    (6, 'SPU20240006', '纯棉商务休闲衬衫', 6, '优衣库', 199.00, 299.00, 500, 1200,
     file_ids[3],
     '高品质纯棉面料，舒适透气',
     '<h2>纯棉商务休闲衬衫</h2><p>100% 优质长绒棉，免烫工艺，多色可选。</p>',
     0, 1),
    
    (7, 'SPU20240007', '轻薄羽绒服男款', 6, '波司登', 599.00, 899.00, 300, 800,
     file_ids[3],  # 复用衬衫图片作为占位
     '90% 白鹅绒填充，轻盈保暖',
     '<h2>轻薄羽绒服</h2><p>90% 白鹅绒，蓬松度 700+，防风防泼水面料。</p>',
     1, 0),
    
    (8, 'SPU20240008', '法式碎花连衣裙', 7, 'ZARA', 399.00, 599.00, 200, 650,
     file_ids[4],
     '法式优雅碎花连衣裙',
     '<h2>法式碎花连衣裙</h2><p>浪漫法式印花，收腰设计，飘逸裙摆。</p>',
     0, 1),
    
    (9, 'SPU20240009', '小香风针织开衫', 7, 'CHANEL', 12999.00, 15000.00, 30, 45,
     file_ids[4],  # 复用连衣裙图片作为占位
     '经典小香风针织开衫',
     '<h2>小香风针织开衫</h2><p>经典编织工艺，珍珠纽扣，优雅知性。</p>',
     0, 0),
    
    (10, 'SPU20240010', '三只松鼠坚果礼盒', 8, '三只松鼠', 128.00, 168.00, 1000, 3500,
     file_ids[5],
     '8 袋坚果组合大礼包',
     '<h2>坚果礼盒</h2><p>精选 8 种坚果，每日坚果搭配，健康美味。</p>',
     1, 1),
    
    (11, 'SPU20240011', '良品铺子肉脯零食', 8, '良品铺子', 59.90, 79.90, 800, 2100,
     file_ids[5],  # 复用坚果图片作为占位
     '猪肉脯精选大包装',
     '<h2>猪肉脯</h2><p>精选猪后腿肉，炭火烘烤，酥香可口。</p>',
     0, 0),
    
    (12, 'SPU20240012', '农夫山泉矿泉水 24 瓶', 9, '农夫山泉', 39.90, 49.90, 2000, 8000,
     file_ids[5],  # 复用坚果图片作为占位
     '天然矿泉水 550ml*24 瓶',
     '<h2>农夫山泉</h2><p>天然弱碱性水，源自优质水源地。</p>',
     0, 0),
    
    (13, 'SPU20240013', '星巴克冷萃咖啡礼盒', 9, '星巴克', 188.00, 228.00, 150, 420,
     file_ids[5],  # 复用坚果图片作为占位
     '精选冷萃咖啡 8 罐装',
     '<h2>冷萃咖啡礼盒</h2><p>星巴克甄选咖啡豆冷萃，醇厚顺滑。</p>',
     0, 1),
    
    (14, 'SPU20240014', 'Xiaomi 14 Pro', 4, '小米', 4999.00, 5299.00, 200, 680,
     file_ids[1],  # 复用三星图片作为占位
     '小米 14 Pro，徕卡光学镜头',
     '<h2>Xiaomi 14 Pro</h2><p>骁龙 8 Gen 3，徕卡 Summilux 镜头，2K 护眼屏。</p>',
     1, 0),
    
    (15, 'SPU20240015', 'Dell XPS 15 OLED', 5, 'Dell', 12999.00, 14999.00, 40, 72,
     file_ids[2],  # 复用 MacBook 图片作为占位
     'Dell XPS 15 OLED 触控屏笔记本',
     '<h2>Dell XPS 15</h2><p>15.6 英寸 3.5K OLED 触控屏，Intel Core i9 处理器。</p>',
     0, 0),
]

for p in products:
    cursor.execute("""
        INSERT INTO tb_product 
        (id, spu_no, name, category_id, brand, price, original_price, stock, sales,
         cover_image_id, description, detail, is_hot, is_recommend)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, p)

conn.commit()
print(f"\n✅ 成功插入 {len(products)} 个商品")

# Close connection
cursor.close()
conn.close()

print("\n🎉 所有数据导入完成！")
print("\n图片文件 ID 映射:")
for i, (filename, _) in enumerate(images):
    print(f"  {i+1}. {filename} -> ID: {file_ids[i]}")
