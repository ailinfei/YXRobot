# 客户API集成测试使用说明

## 🚀 快速开始

### 前置条件
1. 后端服务已启动: `mvn spring-boot:run`
2. 数据库连接正常
3. Node.js 环境已安装

### 运行测试

#### 方法1: 使用批处理脚本（推荐）
```bash
# Windows
scripts/run-basic-api-test.bat
```

#### 方法2: 直接运行JavaScript测试
```bash
cd src/frontend
node src/__tests__/simpleApiTest.js
```

#### 方法3: 运行完整的Vitest测试套件
```bash
cd src/frontend
npx vitest run src/__tests__/integration/customerApiIntegration.test.ts
```

## 📋 测试内容

### 基础功能测试
- ✅ 后端服务健康检查
- ✅ 客户统计API测试
- ✅ 客户列表API测试
- ✅ 筛选选项API测试
- ✅ 搜索功能测试

### 完整集成测试
- ✅ API接口完整性验证
- ✅ 数据格式匹配验证
- ✅ 客户CRUD操作验证
- ✅ 搜索和筛选功能验证
- ✅ 客户关联数据验证
- ✅ 错误处理验证
- ✅ 性能要求验证

## 📊 测试报告

测试完成后会显示：
- 测试总结统计
- 通过/失败的测试列表
- API响应时间分析
- 性能评估结果
- 错误诊断和解决建议

## 🔧 故障排除

### 常见问题

#### 1. 连接被拒绝
```
❌ 无法连接到后端服务，请确保服务已启动
```
**解决方案**: 启动后端服务 `mvn spring-boot:run`

#### 2. 端口被占用
**解决方案**: 检查端口8081是否被其他程序占用

#### 3. 数据库连接失败
**解决方案**: 验证数据库服务是否正常运行

### 手动验证
```bash
# 检查后端服务状态
curl http://localhost:8081/api/admin/customers/stats

# 检查前端页面
http://localhost:8081/admin/business/customers
```

## 📝 测试文件说明

- `simpleApiTest.js` - 基础API功能测试（推荐）
- `customerApiIntegration.test.ts` - 完整集成测试套件
- `healthCheck.ts` - 服务健康检查工具
- `basicApiTest.ts` - TypeScript版本的基础测试

## 🎯 成功标准

测试通过标准：
- 所有API接口响应正常
- 数据格式与前端接口匹配
- 响应时间满足性能要求
- 错误处理机制完善

---

**注意**: 这些测试需要真实的后端服务和数据库连接。请确保在运行测试前已正确配置开发环境。