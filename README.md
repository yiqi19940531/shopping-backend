# Shopping Mall Backend / 购物商城后端系统

基于 Spring Boot 3.2.5 构建的电商购物商城后端系统，提供完整的 RESTful API 接口。

A comprehensive e-commerce shopping mall backend system built with Spring Boot 3.2.5, providing complete RESTful APIs.

## 技术栈 / Tech Stack

| 技术 / Technology | 版本 / Version | 说明 / Description |
|------------------|----------------|-------------------|
| Java | 17 | 编程语言 / Programming Language |
| Spring Boot | 3.2.5 | 应用框架 / Application Framework |
| Spring Security | 3.2.5 | 安全框架 / Security Framework |
| MyBatis-Plus | 3.5.5 | ORM 框架 / ORM Framework |
| MySQL | 8.x | 数据库 / Database |
| JWT | 0.11.5 | 身份认证 / Authentication |
| Knife4j | 4.3.0 | API 文档 / API Documentation |
| Lombok | 1.18.x | 代码简化 / Code Simplification |
| Maven | 3.8+ | 构建工具 / Build Tool |

## 核心功能 / Core Features

### 用户模块 / User Module
- 用户注册 / User Registration
- 用户登录 / User Login
- JWT 认证 / JWT Authentication
- 用户信息管理 / User Profile Management

### 商品模块 / Product Module
- 商品列表浏览 / Product List Browsing
- 商品详情查看 / Product Detail View
- 商品分类管理 / Product Category Management
- 商品搜索 / Product Search

### 购物车模块 / Shopping Cart Module
- 添加商品到购物车 / Add to Cart
- 修改购物车商品数量 / Update Cart Item Quantity
- 删除购物车商品 / Remove from Cart
- 购物车列表查询 / Cart List Query

### 订单模块 / Order Module
- 订单创建 / Order Creation
- 订单状态管理 / Order Status Management
- 订单查询 / Order Query
- 订单取消 / Order Cancellation
- 确认收货 / Order Receipt Confirmation

### 地址模块 / Address Module
- 收货地址管理 / Shipping Address Management
- 默认地址设置 / Default Address Setting

### 支付模块 / Payment Module
- 模拟支付流程 / Simulated Payment Process

### 管理员模块 / Admin Module
- 商品管理 / Product Management
- 订单管理 / Order Management
- 分类管理 / Category Management

## 项目结构 / Project Structure

```
shopping-backend/
├── src/main/java/com/qoder/mall/
│   ├── common/          # 公共组件 / Common Components
│   │   ├── constant/    # 常量定义 / Constants
│   │   ├── exception/   # 异常处理 / Exception Handling
│   │   ├── result/      # 结果封装 / Result Wrapper
│   │   └── util/        # 工具类 / Utility Classes
│   ├── config/          # 配置类 / Configuration Classes
│   ├── controller/      # 控制器层 / Controller Layer
│   │   └── admin/       # 管理员控制器 / Admin Controllers
│   ├── dto/             # 数据传输对象 / DTOs
│   │   ├── request/     # 请求对象 / Request Objects
│   │   └── response/    # 响应对象 / Response Objects
│   ├── entity/          # 实体类 / Entity Classes
│   ├── mapper/          # 数据访问层 / Data Access Layer
│   ├── security/        # 安全配置 / Security Configuration
│   ├── service/         # 业务逻辑层 / Service Layer
│   │   └── impl/        # 服务实现 / Service Implementations
│   └── vo/              # 视图对象 / View Objects
├── src/main/resources/
│   ├── db/              # 数据库脚本 / Database Scripts
│   └── application.yml  # 应用配置 / Application Config
└── src/test/            # 测试代码 / Test Code
```

## 快速开始 / Quick Start

### 环境要求 / Prerequisites

- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### 数据库配置 / Database Configuration

1. 创建数据库 / Create Database:
```sql
CREATE DATABASE shopping_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改配置文件 / Update Configuration:
编辑 `src/main/resources/application.yml` 中的数据库连接信息。
Edit database connection info in `src/main/resources/application.yml`.

### 运行项目 / Run Project

```bash
# 克隆项目 / Clone repository
git clone https://github.com/yourusername/shopping-backend.git

# 进入项目目录 / Enter project directory
cd shopping-backend

# 编译运行 / Compile and run
mvn spring-boot:run
```

### 访问 API 文档 / Access API Documentation

启动项目后，访问以下地址查看 API 文档：
After starting the project, access the API documentation at:

- Knife4j: http://localhost:8080/doc.html
- Swagger UI: http://localhost:8080/swagger-ui.html

## API 接口概览 / API Overview

### 认证接口 / Authentication APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/auth/register | POST | 用户注册 / User Registration |
| /api/auth/login | POST | 用户登录 / User Login |

### 商品接口 / Product APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/products | GET | 商品列表 / Product List |
| /api/products/{id} | GET | 商品详情 / Product Detail |
| /api/categories | GET | 分类列表 / Category List |

### 购物车接口 / Cart APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/cart | GET | 购物车列表 / Cart List |
| /api/cart | POST | 添加商品 / Add Item |
| /api/cart/{id} | PUT | 更新数量 / Update Quantity |
| /api/cart/{id} | DELETE | 删除商品 / Remove Item |

### 订单接口 / Order APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/orders | GET | 订单列表 / Order List |
| /api/orders | POST | 提交订单 / Submit Order |
| /api/orders/{id} | GET | 订单详情 / Order Detail |
| /api/orders/{id}/cancel | POST | 取消订单 / Cancel Order |
| /api/orders/{id}/receive | POST | 确认收货 / Confirm Receipt |

### 地址接口 / Address APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/addresses | GET | 地址列表 / Address List |
| /api/addresses | POST | 添加地址 / Add Address |
| /api/addresses/{id} | PUT | 更新地址 / Update Address |
| /api/addresses/{id} | DELETE | 删除地址 / Delete Address |

### 管理员接口 / Admin APIs
| 接口 / Endpoint | 方法 / Method | 描述 / Description |
|----------------|---------------|-------------------|
| /api/admin/products | POST | 添加商品 / Add Product |
| /api/admin/products/{id} | PUT | 更新商品 / Update Product |
| /api/admin/orders | GET | 订单管理 / Order Management |
| /api/admin/orders/{id}/ship | POST | 发货 / Ship Order |

## 安全认证 / Security

本项目使用 JWT (JSON Web Token) 进行身份认证：
This project uses JWT (JSON Web Token) for authentication:

1. 用户登录成功后获取 Token / Obtain token after successful login
2. 在请求头中携带 Token / Include token in request header:
   ```
   Authorization: Bearer {your_token}
   ```

## 开发团队 / Development Team

- **Author**: Qoder
- **Version**: 1.0.0
- **License**: MIT

## 致谢 / Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Knife4j](https://doc.xiaominfo.com/)
