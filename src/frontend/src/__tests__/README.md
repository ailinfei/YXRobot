# 客户管理API集成测试

本目录包含客户管理功能的前后端API集成测试，用于验证前后端对接的完整性和正确性。

## 📋 测试概述

### 测试目标
- ✅ 验证API接口完整性
- ✅ 验证数据格式匹配
- ✅ 验证字段映射正确性
- ✅ 验证功能完整支持
- ✅ 验证性能要求满足
- ✅ 验证错误处理完善

### 测试范围
1. **API接口完整性验证**
   - 客户统计API
   - 客户列表API
   - 客户详情API
   - 客户CRUD操作API
   - 客户关联数据API

2. **数据格式匹配验证**
   - 响应数据结构验证
   - 字段类型验证
   - 枚举值验证
   - 日期格式验证

3. **字段映射验证**
   - 数据库字段到前端字段映射
   - camelCase命名规范验证
   - 禁止snake_case字段验证

4. **性能要求验证**
   - API响应时间测试
   - 并发请求测试
   - 大数据量处理测试

5. **错误处理验证**
   - 无效参数处理
   - 不存在资源处理
   - 网络错误处理

## 🚀 快速开始

### 前置条件
1. 后端服务已启动 (`mvn spring-boot:run`)
2. 数据库连接正常
3. Node.js 和 npm 已安装
4. 前端依赖已安装 (`npm install`)

### 运行测试

#### 方法1: 使用批处理脚本（推荐）
```bash
# Windows
scripts/run-integration-tests.bat
```

#### 方法2: 使用npm脚本
```bash
cd src/frontend
npm run test:integration
```

#### 方法3: 手动运行单个测试
```bash
cd src/frontend

# API接口完整性测试
npx vitest run src/__tests__/integration/customerApiIntegration.test.ts

# 字段映射验证测试
npx vitest run src/__tests__/validation/fieldMappingValidation.test.ts

# API性能验证测试
npx vitest run src/__tests__/performance/apiPerformanceValidation.test.ts
```

#### 方法4: 使用测试运行器
```bash
cd src/frontend
npx ts-node src/__tests__/runIntegrationTests.ts
```

## 📁 文件结构

```
src/__tests__/
├── README.md                          # 测试说明文档
├── config/
│   └── testConfig.ts                  # 测试配置文件
├── utils/
│   ├── apiResponseValidator.ts        # API响应验证工具
│   └── testReportGenerator.ts         # 测试报告生成器
├── integration/
│   └── customerApiIntegration.test.ts # API集成测试
├── validation/
│   └── fieldMappingValidation.test.ts # 字段映射验证测试
├── performance/
│   └── apiPerformanceValidation.test.ts # API性能验证测试
└── runIntegrationTests.ts             # 测试运行脚本
```

## ⚙️ 配置说明

### API配置
```typescript
// testConfig.ts
export const API_CONFIG = {
  baseURL: 'http://localhost:8081',
  timeout: 10000,
  retryAttempts: 3
}
```

### 性能要求配置
```typescript
export const PERFORMANCE_REQUIREMENTS = {
  customerStats: 1000,      // 客户统计API < 1秒
  customerList: 2000,       // 客户列表API < 2秒
  customerDetail: 1500,     // 客户详情API < 1.5秒
  customerCreate: 3000,     // 客户创建API < 3秒
  // ...
}
```

## 📊 测试报告

测试完成后会生成详细的测试报告，包含：

### 控制台报告
- 测试总结统计
- 分类测试结果
- 性能测试结果
- 数据验证结果
- 优化建议

### 示例报告输出
```
🎯 API集成测试报告
============================================================

📊 测试总结:
------------------------------
总测试数: 25
通过测试: 24 ✅
失败测试: 1 ❌
跳过测试: 0 ⏭️
通过率: 96%
总耗时: 15.32秒

⚡ 性能测试结果:
------------------------------
总体通过率: 100%
平均响应时间: 456ms

✅ 客户统计API: 234ms (23.4% of 1000ms)
✅ 客户列表API: 567ms (28.4% of 2000ms)
✅ 客户详情API: 345ms (23.0% of 1500ms)

💡 优化建议:
------------------------------
1. 所有测试都通过了！API接口运行良好，前后端对接成功
```

## 🔧 故障排除

### 常见问题

#### 1. 后端服务连接失败
```
❌ 错误: 后端服务启动超时
```
**解决方案:**
- 确保后端服务已启动: `mvn spring-boot:run`
- 检查端口8081是否被占用
- 验证数据库连接是否正常

#### 2. 测试数据创建失败
```
❌ 客户创建测试失败: 数据验证失败
```
**解决方案:**
- 检查数据库表结构是否正确
- 验证必需字段是否都有值
- 检查数据验证规则

#### 3. 字段映射验证失败
```
❌ 字段类型不匹配: totalSpent 期望 number, 实际 string
```
**解决方案:**
- 检查MyBatis映射配置
- 验证DTO类字段类型
- 确认数据库字段类型

#### 4. 性能测试失败
```
❌ 客户列表API性能测试失败: 3500ms > 2000ms
```
**解决方案:**
- 检查数据库索引配置
- 优化SQL查询语句
- 考虑添加缓存机制

### 调试技巧

#### 1. 启用详细日志
```bash
# 设置环境变量
export DEBUG=true
export LOG_LEVEL=debug
```

#### 2. 单独运行失败的测试
```bash
npx vitest run src/__tests__/integration/customerApiIntegration.test.ts --reporter=verbose
```

#### 3. 检查API响应
```bash
# 手动测试API
curl -X GET "http://localhost:8081/api/admin/customers/stats"
```

## 📈 持续集成

### GitHub Actions配置示例
```yaml
name: API Integration Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '16'
      - name: Install dependencies
        run: npm install
      - name: Start backend service
        run: mvn spring-boot:run &
      - name: Wait for backend
        run: sleep 30
      - name: Run integration tests
        run: npm run test:integration
```

## 📝 贡献指南

### 添加新测试
1. 在相应目录下创建测试文件
2. 遵循现有的测试结构和命名规范
3. 添加适当的错误处理和清理逻辑
4. 更新README文档

### 测试最佳实践
- 使用描述性的测试名称
- 每个测试应该独立运行
- 清理测试数据
- 添加适当的断言
- 处理异步操作

## 📞 支持

如果遇到问题或需要帮助，请：
1. 查看故障排除部分
2. 检查测试日志输出
3. 验证环境配置
4. 联系开发团队

---

**注意**: 这些测试需要真实的后端服务和数据库连接。请确保在运行测试前已正确配置开发环境。