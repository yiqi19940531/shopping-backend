---
trigger: always_on
---
# 双语代码注释规范 / Bilingual Code Commenting Guidelines

## 规则说明 / Rule Description

所有代码注释必须同时包含中文和英文两种语言，格式如下：
All code comments must include both Chinese and English, following the format below:

## 注释格式 / Comment Format

### 单行注释 / Single-line Comment
// 中文说明 / English description

### 多行注释 / Multi-line Comment
/**
 * 中文说明
 * English description
 *
 * @param paramName 参数说明 / parameter description
 * @return 返回值说明 / return value description
 */

### 行内注释 / Inline Comment
int count = 0; // 计数器 / counter

## 示例 / Examples

### 类注释 / Class Comment
/**
 * 用户服务类，处理用户相关业务逻辑
 * User service class, handles user-related business logic
 */
public class UserService {
}

### 方法注释 / Method Comment
/**
 * 根据用户ID查询用户信息
 * Query user information by user ID
 *
 * @param userId 用户唯一标识 / unique user identifier
 * @return 用户信息对象 / user information object
 */
public User getUserById(Long userId) {
}

### 字段注释 / Field Comment
// 用户名，最大长度50个字符 / username, max length 50 characters
private String username;

// 创建时间 / creation timestamp
private LocalDateTime createTime;

## 注意事项 / Notes

- 中文说明在前，英文说明在后 / Chinese description first, English description second
- 中英文之间使用 ` / ` 分隔 / Use ` / ` as separator between Chinese and English
- 注释应简洁明了，避免冗余 / Comments should be concise and clear, avoid redundancy
- 复杂逻辑必须添加注释说明 / Complex logic must have comments
- 所有公共方法必须有双语注释 / All public methods must have bilingual comments
