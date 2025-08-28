# API路径规范文档

## 概述

本文档定义了YXRobot项目中API路径的命名规范，确保前后端接口的一致性和可维护性。

## API路径规范

### 🌐 官网公开API
**路径格式**: `/api/website/v1/{resource}`

**适用场景**:
- 官网产品展示
- 公开信息查询
- 无需认证的接口
- 面向普通用户的功能

**示例**:
```http
GET /api/website/v1/products              # 获取公开产品列表
GET /api/website/v1/products/{id}         # 获取产品详情
GET /api/website/v1/products/statistics/status  # 获取产品统计
```

### 🔐 后台管理API
**路径格式**: `/api/admin/v1/{resource}`

**适用场景**:
- 管理后台功能
- 需要管理员权限
- CRUD操作
- 数据管理功能

**示例**:
```http
GET    /api/admin/v1/products             # 管理员获取产品列表
POST   /api/admin/v1/products             # 创建产品
PUT    /api/admin/v1/products/{id}        # 更新产品
DELETE /api/admin/v1/products/{id}        # 删除产品
GET    /api/admin/v1/products/{productId}/media  # 获取产品媒体
POST   /api/admin/v1/products/{productId}/media  # 上传产品媒体
```

## 控制器映射

### 官网产品控制器
```java
@RestController
@RequestMapping("/api/website/v1/products")
public class WebsiteProductController {
    // 处理官网公开的产品API
}
```

### 后台产品管理控制器
```java
@RestController
@RequestMapping("/api/admin/v1/products")
public class ProductController {
    // 处理后台管理的产品API
}
```

## 权限控制

### 官网API
- **无需认证**: 公开访问
- **限流控制**: 防止恶意请求
- **缓存策略**: 提高响应速度
- **只读操作**: 不允许修改数据

### 后台API
- **需要认证**: Bearer Token
- **权限验证**: 管理员角色
- **操作日志**: 记录所有操作
- **完整CRUD**: 支持所有操作

## 版本控制

### 当前版本: v1
- 所有API使用 `v1` 版本标识
- 向后兼容原则
- 新功能优先在新版本实现

### 版本升级策略
- 重大变更时创建新版本 (v2, v3...)
- 旧版本保持一定时间的兼容性
- 提前通知客户端升级

## 响应格式

### 统一响应结构
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2024-12-19T10:00:00Z"
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null,
  "timestamp": "2024-12-19T10:00:00Z"
}
```

## 实施检查清单

### ✅ 已完成
- [x] 更新admin content tasks.md中的API路径
- [x] 更新admin content design.md中的API路径
- [x] 更新ProductController的@RequestMapping
- [x] 更新ProductController中的注释
- [x] 更新website.ts中的API路径
- [x] 创建WebsiteProductController
- [x] 创建API路径规范文档

### 📋 待完成
- [ ] 更新前端admin页面的API调用
- [ ] 更新测试文件中的API路径
- [ ] 更新API文档和接口说明
- [ ] 验证所有API路径的一致性

## 注意事项

1. **路径一致性**: 确保前后端使用相同的API路径
2. **文档同步**: API路径变更时同步更新文档
3. **测试覆盖**: 所有API路径变更都需要测试验证
4. **向下兼容**: 避免破坏现有功能

---

**文档版本**: v1.0  
**创建日期**: 2024-12-19  
**维护人员**: YXRobot开发团队  
**最后更新**: 2024-12-19