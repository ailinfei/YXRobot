# YXRobot 项目目录结构

## 📁 完整目录结构

```
YXRobot/                                    # 项目根目录
├── 📄 配置文件
│   ├── .gitignore                          # Git忽略文件配置
│   ├── pom.xml                             # Maven项目配置文件
│   └── README.md                           # 项目说明文档
│
├── 📋 项目文档
│   ├── DEVELOPMENT-GUIDE.md               # 开发指南
│   ├── DEPLOYMENT-GUIDE.md                # 部署指南
│   ├── DEPLOYMENT-SUCCESS.md              # 部署成功记录
│   ├── MERGE-VERIFICATION-REPORT.md       # 合并验证报告
│   ├── PROJECT-MERGE-SUMMARY.md           # 项目合并总结
│   ├── PROJECT-RENAME-SUMMARY.md          # 项目重命名总结
│   ├── PROJECT-STRUCTURE.md               # 项目结构文档（本文件）
│   └── TOMCAT-DEPLOYMENT-COMPLETE.md      # Tomcat部署完成记录
│
├── 📚 docs/                                # 项目文档目录
│   ├── README.md                           # 文档总览
│   ├── specs/                              # 规格文档
│   │   └── frontend-pages-development/     # 前端页面开发规格
│   │       ├── README.md                   # 开发规格总览
│   │       └── admin/                      # 管理后台规格
│   │           ├── core/                   # 核心页面规格
│   │           ├── content/                # 内容管理规格
│   │           └── system/                 # 系统管理规格
│   ├── api/                                # API文档
│   │   ├── product-create-api.md           # 产品创建API文档
│   │   └── product-details-api.md          # 产品详情API文档
│   ├── database-setup.md                  # 数据库设置文档
│   ├── task-completion-summary.md         # 任务完成总结
│   └── task4-completion-summary.md        # 任务4完成总结
│
├── 🚀 scripts/                             # 构建和部署脚本
│   ├── build-all.bat                      # 完整构建脚本
│   ├── build-and-run.bat                  # 构建并运行脚本
│   ├── deploy-tomcat.bat                  # Tomcat部署脚本
│   ├── dev-start.bat                      # 开发环境启动脚本
│   ├── init-database.bat                  # 数据库初始化脚本
│   ├── init-database.sql                  # 数据库初始化SQL
│   ├── start-application.bat              # 应用启动脚本
│   └── test-database-connection.bat       # 数据库连接测试脚本
│
└── 💻 src/                                 # 源代码目录
    ├── 🎨 frontend/                        # 前端源码目录
    │   ├── 📦 依赖和配置
    │   │   ├── node_modules/               # Node.js依赖包
    │   │   ├── .editorconfig               # 编辑器配置
    │   │   ├── .env.development            # 开发环境变量
    │   │   ├── .env.production             # 生产环境变量
    │   │   ├── .prettierrc.json            # 代码格式化配置
    │   │   ├── env.d.ts                    # 环境类型定义
    │   │   ├── index.html                  # HTML入口文件
    │   │   ├── package.json                # 前端依赖配置
    │   │   ├── package-lock.json           # 依赖锁定文件
    │   │   ├── tsconfig.json               # TypeScript主配置
    │   │   ├── tsconfig.app.json           # 应用TypeScript配置
    │   │   ├── tsconfig.node.json          # Node.js TypeScript配置
    │   │   └── vite.config.ts              # Vite构建配置
    │   │
    │   ├── 🌐 public/                      # 静态资源目录
    │   │   ├── images/                     # 图片资源
    │   │   │   ├── avatars/                # 头像图片
    │   │   │   ├── charity/                # 公益项目图片
    │   │   │   ├── products/               # 产品图片
    │   │   │   ├── states/                 # 状态图标
    │   │   │   └── visit/                  # 访问记录图片
    │   │   ├── favicon.ico                 # 网站图标
    │   │   └── logo.svg                    # 项目Logo
    │   │
    │   └── 📱 src/                         # 前端源码
    │       ├── 🧪 __tests__/               # 测试文件
    │       │   ├── components/             # 组件测试
    │       │   ├── composables/            # 组合函数测试
    │       │   ├── e2e/                    # 端到端测试
    │       │   ├── integration/            # 集成测试
    │       │   ├── performance/            # 性能测试
    │       │   └── services/               # 服务测试
    │       │
    │       ├── 🔌 api/                     # API接口层
    │       │   ├── mock/                   # Mock数据
    │       │   ├── charity.ts              # 公益项目API
    │       │   ├── config.ts               # API配置
    │       │   ├── customer.ts             # 客户管理API
    │       │   ├── dashboard.ts            # 仪表板API
    │       │   ├── language.ts             # 语言管理API
    │       │   ├── mapData.ts              # 地图数据API
    │       │   ├── news.ts                 # 新闻API
    │       │   ├── order.ts                # 订单API
    │       │   ├── platforms.ts            # 平台API
    │       │   ├── product.ts              # 产品API
    │       │   ├── rental.ts               # 租赁API
    │       │   ├── types.ts                # API类型定义
    │       │   └── website.ts              # 网站API
    │       │
    │       ├── 🎨 assets/                  # 资源文件
    │       │   ├── images/                 # 图片资源
    │       │   └── maps/                   # 地图数据
    │       │
    │       ├── 🧩 components/              # Vue组件
    │       │   ├── admin/                  # 管理后台组件
    │       │   ├── charity/                # 公益项目组件
    │       │   ├── common/                 # 通用组件
    │       │   ├── course/                 # 课程管理组件
    │       │   ├── customer/               # 客户管理组件
    │       │   ├── dashboard/              # 仪表板组件
    │       │   ├── device/                 # 设备管理组件
    │       │   ├── editors/                # 编辑器组件
    │       │   ├── firmware/               # 固件管理组件
    │       │   ├── font/                   # 字体管理组件
    │       │   ├── fontPackage/            # 字体包组件
    │       │   ├── language/               # 语言管理组件
    │       │   ├── maps/                   # 地图组件
    │       │   ├── order/                  # 订单管理组件
    │       │   ├── product/                # 产品管理组件
    │       │   ├── rental/                 # 租赁管理组件
    │       │   ├── sales/                  # 销售管理组件
    │       │   ├── tables/                 # 表格组件
    │       │   ├── test/                   # 测试组件
    │       │   ├── upload/                 # 上传组件
    │       │   └── website/                # 网站组件
    │       │
    │       ├── 🔧 composables/             # Vue组合函数
    │       │   ├── useImages.ts            # 图片处理
    │       │   ├── useMultiLanguage.ts     # 多语言支持
    │       │   ├── useProgressMonitoring.ts # 进度监控
    │       │   ├── useSmartSampleAnalysis.ts # 智能样本分析
    │       │   ├── useUserExperienceOptimization.ts # 用户体验优化
    │       │   └── useWizardStepIntegration.ts # 向导步骤集成
    │       │
    │       ├── ⚙️ config/                  # 配置文件
    │       │   ├── image-urls.ts           # 图片URL配置
    │       │   └── performance.ts          # 性能配置
    │       │
    │       ├── 🔌 plugins/                 # Vue插件
    │       │   └── performance.ts          # 性能插件
    │       │
    │       ├── 🛣️ router/                  # 路由配置
    │       │   └── index.ts                # 路由主配置
    │       │
    │       ├── 🔧 services/                # 业务服务层
    │       │   ├── authService.ts          # 认证服务
    │       │   ├── errorHandlingService.ts # 错误处理服务
    │       │   ├── firmwareUpdateService.ts # 固件更新服务
    │       │   ├── fontPackageProgressService.ts # 字体包进度服务
    │       │   ├── performanceMonitoringService.ts # 性能监控服务
    │       │   ├── performanceOptimizationService.ts # 性能优化服务
    │       │   ├── permissionService.ts    # 权限服务
    │       │   ├── platformService.ts      # 平台服务
    │       │   └── smartSampleAnalysis.ts  # 智能样本分析服务
    │       │
    │       ├── 📦 stores/                  # 状态管理
    │       │   └── counter.ts              # 计数器状态
    │       │
    │       ├── 🎨 styles/                  # 样式文件
    │       │   ├── global.scss             # 全局样式
    │       │   └── variables.scss          # 样式变量
    │       │
    │       ├── 📝 types/                   # TypeScript类型定义
    │       │   ├── customer.ts             # 客户类型
    │       │   ├── device.ts               # 设备类型
    │       │   ├── firmware.ts             # 固件类型
    │       │   ├── fontPackage.ts          # 字体包类型
    │       │   ├── order.ts                # 订单类型
    │       │   └── product.ts              # 产品类型
    │       │
    │       ├── 🛠️ utils/                   # 工具函数
    │       │   ├── cache.ts                # 缓存工具
    │       │   ├── dataUtils.ts            # 数据处理工具
    │       │   ├── dateTime.ts             # 日期时间工具
    │       │   ├── exportUtils.ts          # 导出工具
    │       │   ├── imageOptimizer.ts       # 图片优化工具
    │       │   ├── imageUtils.ts           # 图片处理工具
    │       │   ├── index.ts                # 工具函数入口
    │       │   ├── lazyLoading.ts          # 懒加载工具
    │       │   ├── mapDataTest.ts          # 地图数据测试
    │       │   ├── mapUtils.ts             # 地图工具
    │       │   ├── mockData.ts             # Mock数据工具
    │       │   ├── mockInterceptor.ts      # Mock拦截器
    │       │   ├── performance.ts          # 性能工具
    │       │   ├── request.ts              # 请求工具
    │       │   ├── validation.ts           # 验证工具
    │       │   └── websocket.ts            # WebSocket工具
    │       │
    │       ├── 📄 views/                   # 页面组件
    │       │   ├── admin/                  # 管理后台页面
    │       │   │   ├── business/           # 业务管理页面
    │       │   │   ├── content/            # 内容管理页面
    │       │   │   ├── device/             # 设备管理页面
    │       │   │   ├── system/             # 系统管理页面
    │       │   │   ├── Analytics.vue       # 数据分析页面
    │       │   │   ├── Customers.vue       # 客户管理页面
    │       │   │   ├── Dashboard.vue       # 仪表板页面
    │       │   │   ├── Login.vue           # 登录页面
    │       │   │   ├── Orders.vue          # 订单管理页面
    │       │   │   ├── Products.vue        # 产品管理页面
    │       │   │   └── Settings.vue        # 设置页面
    │       │   ├── test/                   # 测试页面
    │       │   ├── website/                # 网站页面
    │       │   │   ├── Charity.vue         # 公益项目页面
    │       │   │   ├── Contact.vue         # 联系我们页面
    │       │   │   ├── Home.vue            # 首页
    │       │   │   ├── News.vue            # 新闻页面
    │       │   │   ├── NewsDetail.vue      # 新闻详情页面
    │       │   │   ├── Products.vue        # 产品展示页面
    │       │   │   └── Support.vue         # 技术支持页面
    │       │   ├── Admin.vue               # 管理后台入口
    │       │   └── Home.vue                # 网站首页入口
    │       │
    │       ├── App.vue                     # Vue应用根组件
    │       └── main.ts                     # 应用入口文件
    │
    ├── ☕ main/                            # 后端主代码
    │   ├── java/com/yxrobot/              # Java源码包
    │   │   ├── common/                     # 通用类
    │   │   │   └── Result.java             # 统一响应格式
    │   │   ├── controller/                 # REST控制器
    │   │   │   └── ProductController.java  # 产品控制器
    │   │   ├── dto/                        # 数据传输对象
    │   │   │   ├── PageResult.java         # 分页结果
    │   │   │   ├── ProductCreateDTO.java   # 产品创建DTO
    │   │   │   ├── ProductDTO.java         # 产品DTO
    │   │   │   └── ProductQueryDTO.java    # 产品查询DTO
    │   │   ├── entity/                     # 数据实体
    │   │   │   └── Product.java            # 产品实体
    │   │   ├── mapper/                     # MyBatis映射器
    │   │   │   └── ProductMapper.java      # 产品映射器
    │   │   ├── service/                    # 业务服务层
    │   │   │   ├── FileUploadService.java  # 文件上传服务
    │   │   │   └── ProductService.java     # 产品服务
    │   │   └── YXRobotApplication.java     # 主应用启动类
    │   │
    │   └── resources/                      # 资源文件
    │       ├── db/migration/               # 数据库迁移脚本
    │       │   ├── V001__create_products_tables.sql # 建表脚本
    │       │   └── V002__insert_test_products_data.sql # 测试数据
    │       ├── mapper/                     # MyBatis XML映射
    │       │   └── ProductMapper.xml       # 产品映射XML
    │       ├── application.yml             # 主配置文件
    │       └── application-dev.yml         # 开发环境配置
    │
    └── 🧪 test/                           # 测试代码
        ├── java/com/yxrobot/              # Java测试代码
        │   ├── controller/                 # 控制器测试
        │   │   └── ProductControllerTest.java
        │   ├── integration/                # 集成测试
        │   │   └── ProductDetailsIntegrationTest.java
        │   ├── service/                    # 服务测试
        │   │   └── ProductServiceTest.java
        │   └── DatabaseConnectionTest.java # 数据库连接测试
        │
        └── resources/                      # 测试资源
            └── application-test.yml        # 测试环境配置
```

## 🏗️ 项目架构说明

### 📱 前端架构 (Vue 3 + TypeScript)
- **框架**: Vue 3 + Composition API
- **语言**: TypeScript
- **构建工具**: Vite
- **UI框架**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **图表**: ECharts
- **样式**: SCSS

### ☕ 后端架构 (Spring Boot)
- **框架**: Spring Boot 2.7.18
- **数据访问**: MyBatis
- **数据库**: MySQL 8.x
- **构建工具**: Maven
- **打包方式**: WAR (支持Tomcat部署)

### 🚀 部署架构
- **开发模式**: 前后端分离开发
- **生产模式**: 前端构建后集成到后端WAR包
- **服务器**: Apache Tomcat 9.x
- **访问方式**: 统一通过Tomcat访问

## 🔧 关键配置文件

| 文件 | 用途 | 位置 |
|------|------|------|
| `pom.xml` | Maven项目配置 | 根目录 |
| `package.json` | 前端依赖配置 | `src/frontend/` |
| `vite.config.ts` | 前端构建配置 | `src/frontend/` |
| `application.yml` | 后端主配置 | `src/main/resources/` |
| `application-dev.yml` | 开发环境配置 | `src/main/resources/` |
| `.env.development` | 前端开发环境变量 | `src/frontend/` |
| `.env.production` | 前端生产环境变量 | `src/frontend/` |

## 📊 项目统计

- **总文件数**: 500+ 文件
- **前端组件**: 120+ Vue组件
- **后端类**: 15+ Java类
- **API接口**: 30+ REST接口
- **测试文件**: 20+ 测试类
- **文档文件**: 10+ 文档

---

**文档更新时间**: 2025-08-16  
**项目版本**: 1.0.0  
**维护状态**: ✅ 活跃维护