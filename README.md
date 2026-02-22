# Shopping Backend

电商后端系统 - 基于Spring Boot 3.2.5、MyBatis-Plus的电商平台后端服务

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.5
- **MyBatis-Plus**: 3.5.5
- **Spring Security**: JWT认证
- **数据库**: MySQL
- **API文档**: Knife4j (Swagger) 4.3.0
- **构建工具**: Maven

## 项目结构

```
shopping-backend
├── src/main/java/com/qoder/mall/
│   ├── common/           # 公共模块（常量、异常、工具类）
│   ├── config/           # 配置类
│   ├── controller/       # 控制器
│   ├── dto/              # 数据传输对象
│   ├── entity/           # 实体类
│   ├── mapper/           # MyBatis映射器
│   ├── security/         # 安全相关
│   ├── service/          # 服务层
│   └── vo/               # 视图对象
├── src/main/resources/
│   ├── db/               # 数据库脚本
│   └── application.yml   # 配置文件
└── pom.xml
```

## 主要功能

- 用户认证与授权（JWT）
- 商品管理
- 分类管理
- 购物车
- 订单管理
- 收货地址
- 支付功能
- 文件上传
- 管理员功能

## 快速开始

### 前置要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 运行步骤

1. 克隆项目
```bash
git clone https://github.com/yiqi19940531/shopping-backend.git
cd shopping-backend
```

2. 配置数据库
- 修改 `src/main/resources/application.yml` 中的数据库配置
- 执行 `src/main/resources/db/schema.sql` 创建表结构
- 执行 `src/main/resources/db/data.sql` 导入初始数据

3. 编译运行
```bash
mvn clean package
java -jar target/shopping-backend-1.0.0.jar
```

或直接运行：
```bash
mvn spring-boot:run
```

4. 访问API文档
```
http://localhost:8080/doc.html
```

## API端点

### 用户相关
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录

### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/{id}` - 获取商品详情

### 购物车相关
- `GET /api/cart` - 获取购物车
- `POST /api/cart` - 添加商品到购物车

### 订单相关
- `POST /api/orders` - 提交订单
- `GET /api/orders` - 获取订单列表

### 管理员相关
- `POST /api/admin/products` - 创建商品
- `PUT /api/admin/products/{id}` - 更新商品
- `DELETE /api/admin/products/{id}` - 删除商品

## 开发规范

- 使用Lombok简化代码
- 统一的异常处理
- RESTful API设计
- JWT令牌认证
- 逻辑删除策略

## 许可证

MIT License
