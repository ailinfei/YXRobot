# YXRobot 快速链接

## 🚀 开发必读

### 核心文档
- **[开发指南](DEVELOPMENT-GUIDE.md)** - 开发流程和规范
- **[前端页面开发规格](docs/specs/frontend-pages-development/README.md)** - 项目核心原则和技术规范
- **[项目结构](PROJECT-STRUCTURE.md)** - 完整的目录结构说明
- **[部署指南](DEPLOYMENT-GUIDE.md)** - 部署流程和配置

### 规格文档
- **[管理后台核心页面](docs/specs/frontend-pages-development/admin/core/README.md)** - Dashboard、Login等核心页面
- **[管理后台内容管理](docs/specs/frontend-pages-development/admin/content/README.md)** - 新闻、产品等内容管理页面
- **[管理后台系统管理](docs/specs/frontend-pages-development/admin/system/README.md)** - 字体包、语言等系统管理页面

## 🔧 开发工具

### 本地开发
```bash
# 前端开发服务器
cd src/frontend && npm run dev

# 后端开发服务器  
mvn spring-boot:run

# 完整构建部署
mvn clean package -DskipTests
```

### 访问地址
- **开发环境**: http://localhost:5173 (前端) + http://localhost:8080 (后端)
- **生产环境**: http://localhost:8080/yxrobot/
- **API测试**: http://localhost:8080/yxrobot/#/test/api

## 📁 重要目录

### 前端源码
- `src/frontend/src/views/admin/` - 管理后台页面
- `src/frontend/src/views/website/` - 官网页面
- `src/frontend/src/api/` - API接口定义
- `src/frontend/src/types/` - TypeScript类型

### 后端源码
- `src/main/java/com/yxrobot/controller/` - REST控制器
- `src/main/java/com/yxrobot/service/` - 业务服务
- `src/main/java/com/yxrobot/entity/` - 数据实体
- `src/main/resources/mapper/` - MyBatis映射

### 配置文件
- `src/main/resources/application.yml` - 后端主配置
- `src/frontend/vite.config.ts` - 前端构建配置
- `src/frontend/.env.development` - 前端开发环境变量
- `pom.xml` - Maven项目配置

## 🚨 重要提醒

### 项目开发原则
**本项目基于已完成的前端页面作为需求来源**

在开发任何功能前，请务必：
1. 查看对应的前端Vue组件代码
2. 阅读相关的规格文档
3. 确认前端的数据模型和API需求
4. 开发匹配的后端接口

### ⚠️ 错误处理原则
**遇到Bug时严禁立即修复单个问题！**

正确流程：
1. **全面排查** - 检查整个项目中的类似错误
2. **系统修复** - 一次性修复所有相关问题
3. **完整验证** - 确保所有修复都正确
4. **统一部署** - 确认无误后再启动服务

**切记：不要改一个错误就立马启动服务！**

---

**快速开始**: 阅读 [开发指南](DEVELOPMENT-GUIDE.md) → 查看 [前端页面规格](docs/specs/frontend-pages-development/README.md) → 开始开发