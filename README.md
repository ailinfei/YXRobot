# YXRobot 练字机器人项目

## 🚨 重要提醒

### 🚨 字段映射一致性开发规范（每个任务开发时必须遵守）

**项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确：**

#### 开发时强制执行的字段映射规范：
- **数据库字段**：snake_case（如：`news_title`, `publish_time`, `is_featured`）
- **Java实体类**：camelCase（如：`newsTitle`, `publishTime`, `isFeatured`）
- **MyBatis映射**：`<result column="news_title" property="newsTitle"/>`
- **前端接口**：camelCase（如：`newsTitle: string`, `publishTime: string`）

#### 每个任务开发时必须遵守的规范：
1. **设计数据库表时**：使用snake_case命名，确保字段名清晰明确
2. **创建Java实体类时**：使用camelCase命名，与数据库字段对应
3. **编写MyBatis映射时**：确保column和property正确对应
4. **定义前端接口时**：与Java实体类保持camelCase一致性
5. **编写API接口时**：确保请求响应参数命名统一

#### 开发过程中的字段映射检查点：
- 创建数据库表后：立即创建对应的Java实体类
- 编写实体类后：立即配置MyBatis映射文件
- 完成后端开发后：立即定义前端TypeScript接口
- 每个功能完成后：验证前后端数据传输的字段一致性

**⚠️ 核心原则：项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确，确保能够调通**

#### 🔥 必须严格遵守的字段映射规则

**在开发任何功能前，必须确保四层数据结构完全匹配：**

1. **数据库表字段** (snake_case) ↔ **Java实体类属性** (camelCase)
2. **Java实体类属性** (camelCase) ↔ **MyBatis XML映射** (column/property)
3. **后端API响应** (camelCase) ↔ **前端TypeScript接口** (camelCase)
4. **前端页面字段** (camelCase) ↔ **API调用参数** (camelCase)

#### 🚨 字段映射检查清单（每次开发必检查）

- [ ] **数据库字段命名**：使用snake_case，如 `platform_name`、`is_enabled`
- [ ] **Java实体类属性**：使用camelCase，如 `platformName`、`isEnabled`
- [ ] **MyBatis映射配置**：column="platform_name" property="platformName"
- [ ] **前端TypeScript接口**：使用camelCase，与Java实体保持一致
- [ ] **前端页面绑定**：v-model绑定的字段名与TypeScript接口一致
- [ ] **API调用参数**：请求参数字段名与后端Controller接收参数一致

#### ❌ 常见字段映射错误（必须避免）

```javascript
// ❌ 错误：前端使用snake_case
const formData = {
  platform_name: 'test',    // 错误：应该使用camelCase
  is_enabled: true          // 错误：应该使用camelCase
}

// ✅ 正确：前端使用camelCase
const formData = {
  platformName: 'test',     // 正确：与后端Java属性一致
  isEnabled: true           // 正确：与后端Java属性一致
}
```

```xml
<!-- ❌ 错误：MyBatis映射不匹配 -->
<result column="platform_name" property="platform_name"/>  <!-- 错误：property应该是camelCase -->

<!-- ✅ 正确：MyBatis映射匹配 -->
<result column="platform_name" property="platformName"/>   <!-- 正确：数据库字段→Java属性 -->
```

#### 🔍 字段映射验证方法

**开发前验证：**
```bash
# 1. 检查数据库表结构
DESCRIBE table_name;

# 2. 检查Java实体类属性
grep -n "private.*;" EntityClass.java

# 3. 检查MyBatis映射配置
grep -n "column.*property" MapperFile.xml

# 4. 检查前端TypeScript接口
grep -n ":" InterfaceFile.ts
```

**开发后验证：**
```bash
# 测试API调用是否正常
curl -X GET "http://localhost:8081/api/test-endpoint"

# 检查前端页面是否正常显示数据
# 访问对应的管理页面，确认数据加载和显示正常
```

详细规范请参考：[数据字段匹配验证规范](#数据字段匹配验证规范强制要求)

### � 前端页面真实数据绑定规范（强制要求）

**⚠️ 核心原则：开发完整前端静态页面里面的数据是数据库里面的数据，而不是代码中的模拟数据，要和Vue绑定好，展示**

#### 🔥 前端数据展示强制要求

**所有前端页面必须通过API接口从数据库获取数据，严禁在前端组件中硬编码模拟数据**

1. **数据来源要求**：
   - ✅ **必须使用**：通过API接口从数据库获取的真实业务数据
   - ✅ **允许存在**：数据库中的真实业务数据（通过管理后台录入或初始化脚本插入）
   - ❌ **严禁使用**：在Vue组件中硬编码的模拟数据
   - ❌ **严禁使用**：在constants文件中定义的静态假数据
   - ❌ **严禁使用**：在前端组件中写死的示例数据

2. **数据库与前端的正确关系**：
   - **数据库层面**：可以包含真实的业务数据，通过SQL脚本初始化或管理后台录入
   - **API层面**：后端从数据库查询真实数据并通过API返回给前端
   - **前端层面**：必须通过API调用获取数据，不能在组件中预设任何静态数据

3. **Vue数据绑定要求**：
   - 所有页面数据必须通过`reactive`或`ref`进行响应式绑定
   - 数据必须通过API调用动态加载，不能预设静态值
   - 页面加载时必须调用对应的API接口获取数据
   - 数据更新后必须重新调用API刷新页面显示

#### 🚨 前端页面开发检查清单（每个页面必检查）

- [ ] **API调用验证**：页面加载时是否正确调用后端API接口
- [ ] **数据绑定验证**：所有显示字段是否正确绑定到API返回的数据
- [ ] **响应式更新**：数据变更时页面是否自动更新显示
- [ ] **空数据处理**：当数据库为空时页面是否正确显示空状态
- [ ] **错误处理**：API调用失败时是否有适当的错误提示
- [ ] **加载状态**：数据加载过程中是否显示loading状态

#### ✅ 正确的前端数据绑定实现

```vue
<template>
  <div>
    <!-- ✅ 正确：绑定API数据 -->
    <el-table :data="newsList" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="publishTime" label="发布时间" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNewsList } from '@/api/news'
import type { NewsItem } from '@/types/news'

// ✅ 正确：响应式数据绑定
const newsList = ref<NewsItem[]>([])
const loading = ref(false)

// ✅ 正确：通过API获取数据库中的真实数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await getNewsList() // API从数据库查询真实数据
    newsList.value = response.data // 绑定API返回的数据库数据
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// ✅ 正确：页面加载时获取数据
onMounted(() => {
  loadData()
})
</script>
```

#### ❌ 错误的前端数据绑定实现

```vue
<template>
  <div>
    <!-- ❌ 错误：使用硬编码数据 -->
    <el-table :data="mockNewsList">
      <el-table-column prop="title" label="标题" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
// ❌ 错误：在前端组件中硬编码模拟数据
const mockNewsList = [
  { id: 1, title: '示例新闻1', status: 'published' },
  { id: 2, title: '示例新闻2', status: 'draft' }
]

// ❌ 错误：不调用API获取数据库中的真实数据
</script>
```

#### 📊 数据流转的正确模式

```
数据库 → 后端API → 前端组件
   ↓        ↓         ↓
真实数据 → 查询返回 → 动态绑定

✅ 正确：数据库中可以有真实的业务数据
✅ 正确：后端API从数据库查询并返回数据
✅ 正确：前端通过API调用获取数据并动态绑定

❌ 错误：前端组件中硬编码模拟数据
❌ 错误：前端不调用API，直接使用静态数据
```

#### 🔍 前端数据绑定验证方法

**开发时验证：**
```bash
# 1. 检查页面是否调用API
grep -r "api\." src/views/

# 2. 检查是否存在硬编码数据
grep -r "const.*=.*\[" src/views/

# 3. 检查响应式数据绑定
grep -r "ref\|reactive" src/views/
```

**运行时验证：**
```bash
# 1. 启动项目并访问页面
mvn spring-boot:run

# 2. 打开浏览器开发者工具
# 3. 检查Network标签页是否有API请求
# 4. 验证页面显示的数据与数据库数据一致
# 5. 测试数据为空时的页面显示
```

#### 🚨 数据展示一致性要求

1. **字段完整性**：前端页面显示的所有字段必须与数据库表字段完全对应
2. **数据准确性**：页面显示的数据必须与数据库中存储的数据完全一致
3. **实时同步**：数据库数据变更后，前端页面必须能够实时或手动刷新显示最新数据
4. **状态一致**：数据的状态（如启用/禁用、发布/草稿）在前后端必须保持一致

#### 🔄 数据刷新机制要求

- **自动刷新**：CRUD操作完成后必须自动重新加载数据
- **手动刷新**：提供刷新按钮允许用户手动重新加载数据
- **实时更新**：数据变更后页面显示必须立即更新
- **错误恢复**：API调用失败后提供重试机制

## 📚 目录

1. [项目概述](#项目概述)
2. [快速开始](#快速开始)
3. [项目架构](#项目架构)
4. [技术栈](#技术栈)
5. [开发环境](#开发环境)
6. [核心功能](#核心功能)
7. [API文档](#api文档)
8. [部署指南](#部署指南)
9. [开发规范](#开发规范)
10. [故障排除](#故障排除)

## 项目概述

YXRobot是一个全栈练字机器人管理系统，采用Spring Boot + Vue.js架构，支持管理后台、官网展示和公益项目管理等功能。

### ⚡ 快速启动
```bash
# 一键启动项目
mvn spring-boot:run

# 访问地址: http://localhost:8081
```

### 🚨 开发核心原则
**项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确！**
- 数据库字段 (snake_case) ↔ Java属性 (camelCase) ↔ 前端字段 (camelCase)
- 所有CRUD操作必须实现自动刷新机制
- 严格按照代码注释规范编写代码

### 核心特性
- 🎯 **前后端分离架构** - Spring Boot后端 + Vue.js前端
- � **响应式设计** - 支持多设备访问
- 🔐 **权限管理** - 完整的用户认证和授权体系
- 📊 **数据可视化** - ECharts图表展示
- � **一键部署** - 单WAR包部署到Tomcat

### 主要模块
- **管理后台** - Dashboard、用户管理、设备管理、内容管理
- **官网展示** - 首页、产品展示、新闻中心、公益活动
- **公益管理** - 统计数据、机构管理、活动管理、项目管理
- **平台链接管理** - 电商平台和租赁平台链接配置、监控和统计

## 快速开始

### 环境要求
- Java 11+
- Maven 3.6+
- MySQL 8.0+
- Node.js 18+

### 项目启动

#### 推荐方式：Maven命令启动
```bash
# 启动开发服务器（推荐）
mvn spring-boot:run

# 访问应用
# 后端API: http://localhost:8081/api
# 管理后台: http://localhost:8081/admin/content/charity
# 完整应用: http://localhost:8081
```

#### 备用方式：脚本启动
```bash
# 1. 启动开发服务器（备用方式）
scripts\dev-start-safe.bat

# 2. 访问应用
# 后端API: http://localhost:8081/api
# 管理后台: http://localhost:8081/admin
```

### 数据库连接
```bash
# 快速连接MySQL
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot
```

---

## 项目架构

```
YXRobot/
├── src/
│   ├── main/
│   │   ├── java/                    # Spring Boot 后端代码
│   │   │   └── com/yxrobot/
│   │   │       ├── controller/      # REST API控制器
│   │   │       ├── service/         # 业务逻辑层
│   │   │       ├── entity/          # 数据实体
│   │   │       ├── mapper/          # MyBatis映射器
│   │   │       ├── dto/             # 数据传输对象
│   │   │       ├── exception/       # 异常处理
│   │   │       ├── validation/      # 数据验证
│   │   │       └── config/          # 配置类
│   │   ├── resources/               # 后端资源文件
│   │   │   ├── mapper/              # MyBatis XML映射
│   │   │   ├── static/              # 静态资源（编译后的前端文件）
│   │   │   └── application.yml      # Spring Boot配置
│   │   └── webapp/                  # Web应用目录
│   │       └── WEB-INF/
│   ├── test/                        # 测试代码
│   │   ├── java/                    # Java单元测试
│   │   │   └── com/yxrobot/
│   │   │       ├── controller/      # 控制器测试
│   │   │       ├── service/         # 服务层测试
│   │   │       ├── mapper/          # 数据访问层测试
│   │   │       ├── integration/     # 集成测试
│   │   │       ├── validation/      # 验证测试
│   │   │       └── exception/       # 异常测试
│   │   └── resources/               # 测试资源
│   └── frontend/                    # Vue.js 前端源码
│       ├── src/
│       │   ├── views/               # 页面组件
│       │   │   ├── admin/           # 管理后台页面
│       │   │   ├── website/         # 官网页面
│       │   │   └── test/            # 测试页面
│       │   ├── components/          # 通用组件
│       │   ├── api/                 # API接口定义
│       │   ├── types/               # TypeScript类型定义
│       │   └── utils/               # 工具函数
│       ├── package.json
│       └── vite.config.ts
├── docs/                            # 项目文档
│   ├── specs/                       # 规格文档
│   │   └── frontend-pages-development/  # 前端页面开发规格
│   ├── api/                         # API文档
│   └── README.md                    # 文档总览
├── scripts/                         # 构建和部署脚本
│   ├── build-frontend.bat          # 前端构建脚本
│   ├── build-backend.bat           # 后端构建脚本
│   ├── build-all.bat               # 全栈构建脚本
│   ├── deploy-tomcat.bat           # Tomcat部署脚本
│   ├── dev-start-safe.bat          # 安全启动脚本
│   └── *.sql                       # 数据库脚本
├── uploads/                         # 文件上传目录
│   └── products/                    # 产品相关文件
├── pom.xml                          # Maven配置
└── target/                          # 构建输出目录
    └── yxrobot-fullstack.war        # 最终WAR包
```

---

## 技术栈

### 后端技术栈
- **Spring Boot 2.7.x** - 主框架
- **Spring MVC** - Web框架
- **MyBatis** - ORM框架
- **MySQL 9.3.0** - 数据库
- **Maven** - 构建工具
- **Jackson** - JSON处理
- **Logback** - 日志框架

### 🚨 重要说明：缓存功能
**本项目不需要缓存功能，请在编写任务和开发代码时不要添加任何缓存相关的功能。**

#### 缓存相关禁止事项
- ❌ 不要使用Redis缓存
- ❌ 不要使用内存缓存（如Spring Cache、ConcurrentMapCacheManager等）
- ❌ 不要添加@Cacheable、@CacheEvict、@CachePut等缓存注解
- ❌ 不要在任务文档中包含缓存相关的任务
- ❌ 不要在设计文档中包含缓存架构设计

#### 性能优化替代方案
- ✅ 使用数据库索引优化查询性能
- ✅ 优化SQL语句减少查询时间
- ✅ 使用分页查询处理大数据量
- ✅ 使用数据库连接池提升连接性能
- ✅ 通过合理的数据库设计提升性能

### 前端技术栈
- **Vue 3** - 前端框架
- **TypeScript** - 类型安全
- **Element Plus** - UI组件库
- **Pinia** - 状态管理
- **Vue Router 4** - 路由管理
- **Vite** - 构建工具
- **Axios** - HTTP客户端
- **ECharts** - 图表库
- **SCSS** - CSS预处理器

### 数据库环境
- **MySQL安装路径**: `E:\YXRobot\mysql-9.3.0-winx64`
- **数据库服务器**: yun.finiot.cn:3306
- **数据库名**: YXRobot
- **连接用户**: YXRobot
- **连接密码**: 2200548qq
- **快速连接**: `E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot`

#### 🚨 重要配置说明
**项目使用云数据库配置，所有数据存储在远程MySQL服务器上。**

- **云数据库地址**: yun.finiot.cn:3306
- **本地MySQL路径**: E:\YXRobot\mysql-9.3.0-winx64
- **应用配置**: 默认连接云数据库，无需本地MySQL服务
- **数据初始化**: 应用启动时自动执行schema.sql创建表结构

#### 数据库连接验证
```bash
# 测试数据库连接
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq -e "SELECT 1"

# 查看数据库表
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SHOW TABLES"

# 检查新闻分类表
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SELECT * FROM news_categories LIMIT 5"
```

### 部署环境
- **Apache Tomcat 9.x** - Web服务器
- **WAR包部署** - 传统Java Web部署方式

---

## 数据库设计规范

### 核心设计原则

#### 1. 关联表设计（必须遵循）
- **禁止使用外键约束**: 所有表与表之间的关联必须通过关联表实现
- **降低耦合度**: 通过关联表解耦主表之间的直接依赖关系
- **提高扩展性**: 关联表设计便于后续功能扩展和数据迁移

#### 2. 关联表命名规范
```sql
-- 格式: {主表名}_{关联表名}_relation
-- 示例:
user_role_relation          -- 用户角色关联表
device_user_relation        -- 设备用户关联表
content_category_relation   -- 内容分类关联表
```

#### 3. 关联表结构标准
```sql
-- 基础关联表结构
CREATE TABLE user_role_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT='用户角色关联表';
```

#### 4. 数据完整性保障
- **应用层控制**: 数据完整性检查在业务逻辑层实现
- **索引优化**: 关联字段必须建立索引提高查询性能
- **唯一约束**: 防止重复关联的业务唯一键约束
- **软删除**: 使用status字段实现逻辑删除，保留数据历史

#### 5. 查询优化策略
```java
// 示例：用户角色查询
@Mapper
public interface UserRoleRelationMapper {
    // 通过关联表查询用户的所有角色
    List<Role> selectRolesByUserId(@Param("userId") Long userId);
    
    // 通过关联表查询角色下的所有用户
    List<User> selectUsersByRoleId(@Param("roleId") Long roleId);
}
```

#### 6. 事务处理规范
- **关联操作事务**: 涉及关联表的增删改操作必须在事务中执行
- **回滚机制**: 确保关联数据的一致性，失败时完整回滚

#### 7. 实施要求
1. **新表设计**: 所有新建表必须遵循关联表设计原则
2. **代码审查**: 涉及表关联的代码必须经过数据库设计审查
3. **性能测试**: 关联查询必须进行性能测试，确保响应时间符合要求
4. **文档维护**: 所有关联表设计必须在数据库设计文档中详细记录

### 数据交互

- **前后端通信**: RESTful API + JSON
- **API路径**: `/api/*`
- **数据格式**: 统一JSON响应格式
- **跨域处理**: Spring Boot CORS配置

---

## 开发环境配置

### 🚀 开发环境启动指南

#### ⚠️ 重要：优先使用Spring Boot开发服务器

**强制要求：所有开发工作必须使用Spring Boot开发服务器，禁止使用Tomcat部署方式进行开发调试。**

#### 📋 开发前必读
1. **项目核心原则** - 理解项目开发理念
2. **技术栈规范** - 了解技术选型
3. **开发规范要求** - 代码注释和命名规范
4. **自动刷新机制指南** - 了解自动刷新实现要求

#### 📁 重要目录结构
- `src/frontend/src/views/admin/` - 管理后台页面
- `src/frontend/src/views/website/` - 官网展示页面
- `src/frontend/src/views/test/` - 测试页面
- `src/frontend/src/api/` - API接口定义
- `src/main/java/com/yxrobot/controller/` - REST API控制器
- `src/main/java/com/yxrobot/service/` - 业务逻辑层
- `docs/specs/frontend-pages-development/` - 前端页面规格文档

#### 🛠️ 开发命令

##### 🚀 项目启动（每次开发必用）
```bash
# 启动项目（推荐方式）
mvn spring-boot:run

# 项目将在以下地址启动：
# - 完整应用: http://localhost:8081
# - 后端API: http://localhost:8081/api
# - 管理后台: http://localhost:8081/admin/content/charity
# - 公益管理: http://localhost:8081/admin/content/charity
```

##### 前端开发
```bash
cd src/frontend
npm install
npm run dev          # 开发服务器
npm run build        # 生产构建
```

##### 后端开发
```bash
mvn clean compile    # 编译
mvn spring-boot:run  # 开发服务器（主要启动命令）
mvn clean package    # 生产构建
```

##### 全栈部署
```bash
mvn clean package -DskipTests  # 构建WAR包
```

#### 🔍 调试指南
- **后端API地址**: `http://localhost:8081/api/`（开发）/ `http://localhost:8080/yxrobot/api/`（生产）
- **前端开发代理**: `http://localhost:5173/`
- **API测试页面**: `http://localhost:8081/test/api`
- **健康检查**: `http://localhost:8081/actuator/health`（开发）/ `http://localhost:8080/yxrobot/actuator/health`（生产）

#### 📋 开发检查清单

##### 开发新功能前
- [ ] 查看对应的前端Vue组件代码
- [ ] 阅读相关的规格文档
- [ ] 确认前端的数据模型和API调用
- [ ] 理解业务逻辑和用户交互
- [ ] **阅读自动刷新机制指南** - 了解自动刷新实现要求

##### 开发过程中
- [ ] 遵循代码注释规范（强制要求）
- [ ] 使用统一的命名规范
- [ ] 确保API接口匹配前端需求
- [ ] **实现自动刷新功能** - 所有CRUD操作后必须重新加载数据
- [ ] 编写必要的单元测试

##### 开发完成后
- [ ] 测试前后端集成
- [ ] 验证所有功能正常工作
- [ ] **确保实现自动刷新机制** - 所有CRUD操作必须支持自动刷新
- [ ] 更新相关文档
- [ ] 提交代码并部署

#### 🔧 开发服务器启动流程

##### 端口管理规范（强制执行）

**固定端口配置：**
- **Spring Boot 后端服务**: 8081 端口（固定不变）
- **前端开发服务器**: 5173 端口（仅开发时使用）
- **数据库服务**: 3306 端口（远程MySQL）

**端口冲突处理流程：**
```powershell
# 1. 检查8081端口占用情况
netstat -ano | findstr :8081

# 2. 强制终止占用进程
taskkill /f /im java.exe

# 3. 再次确认端口已完全释放
netstat -ano | findstr :8081
```

##### 启动Spring Boot开发服务器

**推荐方式：使用安全启动脚本**
```powershell
# 进入项目根目录
cd E:\YXRobot\workspace\projects\YXRobot

# 使用安全启动脚本（自动处理端口清理和环境检查）
scripts\dev-start-safe.bat
```

**手动方式：**
```powershell
# 进入项目根目录
cd E:\YXRobot\workspace\projects\YXRobot

# 启动开发服务器（使用dev配置文件）
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

##### 验证服务器启动成功
- **后端服务器**: http://localhost:8081
- **API基础路径**: http://localhost:8081/api
- **管理后台**: http://localhost:8081/admin/content/products
- **健康检查**: http://localhost:8081/actuator/health

### Spring Boot 应用启动

#### 服务器配置
- **服务器端口**: 8081（开发环境）/ 8080（生产环境）
- **上下文路径**: /（开发环境）/ /yxrobot（生产环境）
- **数据库**: 远程MySQL数据库 (yun.finiot.cn:3306/YXRobot)
- **用户名**: YXRobot
- **密码**: 2200548qq

#### 启动方式

##### 方法一：使用Maven启动（推荐开发环境）
```bash
# 使用开发配置启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或使用默认配置启动
mvn spring-boot:run
```

##### 方法二：使用JAR/WAR包启动
```bash
# 首先构建项目
mvn clean package -DskipTests

# 使用开发配置启动
java -jar target/yxrobot.war --spring.profiles.active=dev

# 或使用默认配置启动
java -jar target/yxrobot.war
```

##### 方法三：使用启动脚本
```bash
# Windows环境
simple-start.bat

# 或使用开发环境脚本
scripts/dev-start.bat
```

### 配置文件说明
- **application.yml**: 生产环境配置（端口8080，上下文路径：/yxrobot）
- **application-dev.yml**: 开发环境配置（端口8081，上下文路径：/）
- **application-h2.yml**: H2内存数据库配置（测试用）

---

## 前端页面开发规格

### 🚨 重要说明 - 项目核心原则

#### ⚠️ 必读：基于现有前端页面的需求分析原则

**本项目的核心原则是：基于已开发完成的前端页面作为需求来源，而不是重新开发前端页面。**

#### 📋 工作流程说明
1. **前端页面已完成**：所有前端页面（Vue.js组件）已经开发完成并可正常运行
2. **分析现有功能**：通过阅读前端代码、组件结构、API调用等，分析页面的实际功能
3. **提取真实需求**：基于前端实现的功能，提取出真实的业务需求和用户故事
4. **编写规格文档**：为这些已实现的功能编写标准的需求文档、设计文档和任务文档
5. **开发后端API**：基于前端需求开发对应的后端API接口

#### 🔍 需求分析方法
- **代码分析**：阅读Vue组件代码，了解页面结构、数据模型、交互逻辑
- **功能识别**：识别页面的实际功能，如表格展示、表单编辑、文件上传等
- **API需求**：分析前端调用的API接口，确定需要开发的后端接口
- **数据结构**：基于前端数据模型，设计对应的数据库表结构

#### ❌ 禁止事项
- **不得添加前端未实现的功能**：如果前端页面没有搜索功能，需求文档中不能包含搜索需求
- **不得修改前端页面结构**：需求必须完全匹配现有前端实现
- **不得假设功能存在**：所有功能必须基于实际的前端代码验证

#### ✅ 正确做法
- **先查看前端代码**：每次编写需求文档前，必须先查看对应的前端Vue组件
- **验证功能存在**：确保每个需求都对应前端的实际实现
- **匹配数据结构**：API接口和数据库设计必须匹配前端的数据模型
- **保持一致性**：技术栈、命名规范、代码风格必须与现有项目保持一致

### 页面模块结构

#### 1. 管理后台模块 (Admin)
- **核心页面**: Dashboard, Login, Analytics, Settings
- **业务管理**: 客户管理、订单管理、销售分析、租赁分析
- **设备管理**: 设备管理、设备监控、固件管理
- **系统管理**: 课程管理、字体包管理、语言管理
- **内容管理**: 新闻管理、产品管理、平台管理、公益管理

#### 2. 官网展示模块 (Website)
- **展示页面**: 首页、产品展示、新闻中心、公益活动
- **服务页面**: 联系我们、技术支持
- **详情页面**: 新闻详情

#### 3. 测试模块 (Test)
- **功能测试**: 地图测试、订单测试、简单订单测试

### 🚀 快速启动（必读）

**开发前必须执行的步骤：**

```powershell
# 方式1: 使用安全启动脚本（推荐）
scripts\dev-start-safe.bat

# 方式2: 手动启动
# 1. 清理端口占用
taskkill /f /im java.exe

# 2. 进入项目目录
cd E:\YXRobot\workspace\projects\YXRobot

# 3. 启动Spring Boot开发服务器
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"

# 4. 验证启动成功
# 访问: http://localhost:8081/admin/content/products
# API测试: http://localhost:8081/api/admin/products
```

**⚠️ 重要提醒：开发期间只能使用Spring Boot开发服务器，禁止使用Tomcat部署！**
### 开发规范要求

详细的开发规范请参考前面的"开发环境配置"部分。

---

## 项目重构规划

### 任务分类

#### 1. 官网模块 (Website)
- 首页展示
- 产品介绍
- 新闻资讯
- 公益活动
- 联系我们
- 技术支持

#### 2. 管理后台 - 系统管理 (Admin System)
- 用户登录认证
- 仪表板概览
- 字体包管理
- 语言管理
- 课程管理

#### 3. 管理后台 - 设备管理 (Admin Device)
- 设备管理
- 设备监控
- 固件管理

#### 4. 管理后台 - 业务管理 (Admin Business)
- 客户管理
- 订单管理
- 销售分析
- 租赁分析

#### 5. 管理后台 - 内容管理 (Admin Content)
- 新闻管理
- 产品管理
- 平台管理
- 公益内容管理

### 任务优先级

#### 高优先级 (P0)
- 用户登录认证
- 仪表板概览
- 设备管理
- 客户管理

#### 中优先级 (P1)
- 字体包管理
- 订单管理
- 新闻管理
- 首页展示

#### 低优先级 (P2)
- 语言管理
- 课程管理
- 公益活动
- 技术支持

---

## 文件上传系统

### 目录结构

```
uploads/
├── products/                    # 产品相关文件
│   ├── covers/                  # 产品封面图片
│   │   └── YYYY/MM/DD/         # 按日期分组
│   └── media/                   # 产品媒体文件
│       ├── image/              # 产品图片
│       │   └── YYYY/MM/DD/     # 按日期分组
│       └── video/              # 产品视频
│           └── YYYY/MM/DD/     # 按日期分组
└── README.md                   # 说明文档
```

### 支持的文件类型

#### 图片文件
- **格式**: JPEG, JPG, PNG, GIF, WebP
- **最大大小**: 10MB
- **用途**: 产品封面、产品展示图片

#### 视频文件
- **格式**: MP4, AVI, MOV, WMV, FLV
- **最大大小**: 100MB
- **用途**: 产品展示视频

### 上传API

#### 1. 临时文件上传
- **接口**: `POST /api/v1/upload/temp`
- **参数**: `file` (MultipartFile)
- **用途**: 前端组件通用文件上传

#### 2. 产品封面上传
- **接口**: `POST /api/v1/upload/product/cover`
- **参数**: `file` (MultipartFile)
- **用途**: 专门用于产品封面上传

#### 3. 产品媒体上传
- **接口**: `POST /api/v1/upload/product/media`
- **参数**: 
  - `file` (MultipartFile)
  - `mediaType` (String): image 或 video
- **用途**: 产品图片和视频上传

### 文件访问URL

#### HTTP访问地址
- **基础URL**: `http://localhost:8080/yxrobot/api/v1/files/`
- **完整URL**: `{基础URL}{相对路径}`

#### 示例
```
封面图片: http://localhost:8080/yxrobot/api/v1/files/products/covers/2024/08/18/abc123.jpg
产品图片: http://localhost:8080/yxrobot/api/v1/files/products/media/image/2024/08/18/def456.png
产品视频: http://localhost:8080/yxrobot/api/v1/files/products/media/video/2024/08/18/ghi789.mp4
```

### 安全机制

#### 文件类型验证
- 检查文件MIME类型
- 验证文件扩展名
- 双重验证确保安全

#### 文件大小限制
- 图片文件: 最大10MB
- 视频文件: 最大100MB
- 防止大文件攻击

#### 路径安全
- 使用UUID生成唯一文件名
- 防止路径遍历攻击
- 文件访问权限控制

### 存储管理

#### 自动目录创建
- 上传时自动创建日期目录
- 确保目录结构完整

#### 文件清理
- 删除产品时自动清理相关文件
- 避免孤立文件占用存储空间

#### 备份建议
- 定期备份uploads目录
- 建议使用云存储作为备份

### 开发说明

#### 配置文件
- **上传路径**: `application.yml` 中的 `file.upload.path`
- **访问前缀**: `application.yml` 中的 `file.access.url.prefix`

#### 相关类
- **FileUploadService**: 文件上传业务逻辑
- **FileController**: 文件访问控制器
- **UploadController**: 文件上传控制器

#### 前端组件
- **FileUpload**: 通用文件上传组件
- **Products.vue**: 产品管理页面，使用文件上传功能

---

## 公益项目管理系统

### 数据库环境信息

#### MySQL连接信息
- **主机地址**: yun.finiot.cn
- **端口**: 3306
- **数据库名**: YXRobot
- **用户名**: YXRobot
- **密码**: 2200548qq

#### 快速连接命令
```cmd
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot
```

### 数据库初始化

#### 快速设置（推荐）
```bash
# 快速设置公益项目数据库
cd scripts
quick-charity-setup.bat
```

#### 手动执行
```cmd
# 创建表结构
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source create-charity-tables.sql"

# 插入测试数据
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source insert-charity-data.sql"

# 验证数据
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "source verify-charity-data.sql"
```

### 核心功能模块

#### 1. 统计数据管理
- 公益项目统计数据的获取和更新
- 通过数据库优化提高性能
- 版本控制防止并发冲突
- 数据一致性验证

#### 2. 合作机构管理
- 机构信息的CRUD操作
- 支持按类型、状态、地区筛选
- 分页查询和条件搜索
- 软删除机制

#### 3. 公益活动管理
- 活动信息的完整管理
- 活动状态跟踪
- 预算和费用管理
- 参与人数统计

#### 4. 图表数据服务
- 项目状态分布饼图
- 资金筹集趋势折线图
- 地区分布统计
- 志愿者活动统计

### 初始化数据说明

#### 统计数据 (charity_stats)
- 1条统计记录，包含项目、机构、活动等汇总数据

#### 合作机构 (charity_institutions)
- 17个合作机构，包括：
  - 学校：5个
  - 孤儿院：3个
  - 社区：3个
  - 医院：2个
  - 图书馆：2个
  - 暂停合作：2个

#### 公益项目 (charity_projects)
- 12个公益项目，包括：
  - 教育类：3个
  - 捐赠类：3个
  - 志愿服务类：2个
  - 培训类：2个
  - 已完成：2个

#### 公益活动 (charity_activities)
- 16个公益活动，包括：
  - 探访类：3个
  - 培训类：3个
  - 捐赠类：3个
  - 活动类：4个
  - 计划中：3个

---

## 测试体系

### 单元测试覆盖范围

#### 1. 业务逻辑层测试
- **CharityServiceTest**: 25个测试方法
  - 统计数据获取和计算
  - 统计数据更新和版本控制
  - 数据验证和异常处理
  - 历史记录和趋势数据

#### 2. 控制器层测试
- **CharityControllerTest**: 30个测试方法
  - HTTP OPTIONS请求处理
  - 各种API接口测试
  - 参数验证和异常处理
  - 图表数据API测试

#### 3. 数据访问层测试
- **CharityStatsMapperTest**: 20个测试方法
- **CharityInstitutionMapperTest**: 18个测试方法
  - CRUD操作测试
  - 条件查询和分页测试
  - 软删除机制测试

#### 4. 数据验证测试
- **CharityDataValidatorTest**: 35个测试方法
  - 所有字段的验证规则测试
  - 边界值和异常场景测试

#### 5. 异常处理测试
- **CharityExceptionTest**: 25个测试方法
  - 异常构造和传播测试
  - 错误码和消息测试

### 集成测试覆盖范围

#### 1. 端到端流程测试
- 统计数据更新的完整流程
- 数据一致性验证
- 数据一致性检查

#### 2. CRUD操作测试
- 合作机构管理完整流程
- 公益活动管理完整流程
- 数据完整性验证

#### 3. 图表数据测试
- 各种图表数据生成正确性
- 数据格式符合前端要求

#### 4. 性能和并发测试
- 并发访问安全性
- 数据库查询性能验证
- 响应时间测试

### 测试执行指南

#### 运行所有测试
```bash
mvn test
```

#### 运行特定测试类
```bash
mvn test -Dtest=CharityServiceTest
mvn test -Dtest=CharityIntegrationTestSimple
```

#### 生成测试报告
```bash
mvn test jacoco:report
```

### 测试覆盖率目标
- **行覆盖率**: ≥ 80%
- **分支覆盖率**: ≥ 75%
- **方法覆盖率**: ≥ 85%
- **类覆盖率**: ≥ 90%

---

## 部署指南

### 构建流程

1. **前端构建**: Vue项目编译为静态文件
2. **资源整合**: 前端静态文件复制到后端resources/static目录
3. **后端构建**: Spring Boot项目打包为WAR文件
4. **部署**: WAR文件部署到Tomcat服务器

### 快速开始

#### 开发模式
```bash
# 启动后端开发服务器（开发环境）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 启动前端开发服务器
cd src/frontend
npm run dev
```

#### 生产部署
```bash
# 一键构建和部署
scripts/build-all.bat
scripts/deploy-tomcat.bat
```

### 访问地址

#### 开发环境（dev profile）
- **完整应用**: http://localhost:8081/
- **API接口**: http://localhost:8081/api/*
- **前端开发服务器**: http://localhost:5173

#### 生产环境（默认配置）
- **完整应用**: http://localhost:8080/yxrobot
- **API接口**: http://localhost:8080/yxrobot/api/*
- **健康检查**: http://localhost:8080/yxrobot/actuator/health

---

## 开发规范

### API设计规范
- 基础路径: `/api/v1`
- 资源命名使用复数形式
- HTTP方法: GET(查询), POST(创建), PUT(更新), DELETE(删除)
- 响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### 数据库设计规范
- 表名使用下划线命名法 (snake_case)
- 字段名使用下划线命名法
- 主键统一使用 `id` (BIGINT AUTO_INCREMENT)
- 创建时间字段: `created_at` (DATETIME)
- 更新时间字段: `updated_at` (DATETIME)
- 软删除字段: `is_deleted` 或 `deleted` (TINYINT)
- 状态字段: `status` (TINYINT, 0-禁用, 1-启用)

### 前端开发规范
- 组件命名使用 PascalCase
- 文件命名使用 PascalCase
- 变量命名使用 camelCase
- 常量命名使用 UPPER_SNAKE_CASE
- 类型定义统一放在 `types` 目录
- API调用统一放在 `api` 目录
- 工具函数统一放在 `utils` 目录

### 版本控制规范
- 使用Git进行版本控制
- 遵循Git Flow工作流
- 编写清晰的提交信息
- 及时更新文档

### 字段映射开发流程（强制执行）

#### 新增数据表时的开发流程
1. **设计数据库表结构** - 使用snake_case命名，添加完整注释
2. **创建Java实体类** - 使用camelCase命名，添加验证注解
3. **编写MyBatis映射** - 确保column和property正确对应
4. **定义前端类型** - 使用camelCase命名，与Java实体保持一致
5. **编写API接口** - 确保请求响应数据结构匹配
6. **测试数据流转** - 验证前后端数据能够正确传输

#### 修改字段时的变更流程
1. **评估影响范围** - 确定哪些层次需要修改
2. **同步修改所有层次** - 数据库→Java→MyBatis→前端
3. **更新验证规则** - 同步前后端验证逻辑
4. **执行完整测试** - 确保所有功能正常
5. **更新相关文档** - 同步API文档和数据库文档

#### 字段映射检查工具使用
```bash
# 开发前检查现有字段映射
scripts/check-field-mapping.sh platform_links

# 验证新增字段的映射关系
scripts/validate-field-mapping.sh PlatformLink

# 生成字段映射对照表
scripts/generate-field-mapping-table.sh
```

### 数据字段匹配验证规范（强制要求）

#### 🚨 数据一致性验证原则
**⚠️ 重要：前端页面的字段要和mapper里面映射字段还是数据库中的字段一样，确保能够调通**

**核心要求：所有数据层必须保持字段定义的完全一致性，前端页面字段必须与后端数据库字段完全匹配**

#### 验证范围
数据字段匹配验证必须覆盖以下四个层次：
1. **数据库表字段定义** - MySQL表结构（CREATE TABLE语句中的字段）
2. **Java实体类字段** - Entity类属性（驼峰命名转换）
3. **MyBatis Mapper映射** - XML映射文件（column与property映射）
4. **前端页面字段** - Vue组件数据模型（TypeScript接口定义）

#### 字段一致性要求
**所有层次的字段必须能够正确映射和转换，确保数据能够在前后端之间正常流转**

#### 验证规则

##### 1. 字段名称一致性
```bash
# 验证规则：字段名称必须在所有层次保持一致（考虑命名转换）
# 数据库: snake_case (如: total_beneficiaries)
# Java: camelCase (如: totalBeneficiaries)  
# 前端: camelCase (如: totalBeneficiaries)
```

##### 2. 字段类型匹配性（以平台链接为例）
```sql
-- 数据库表字段定义
CREATE TABLE platform_links (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '链接ID，主键',
    platform_name VARCHAR(100) NOT NULL COMMENT '平台名称',
    platform_type ENUM('ecommerce', 'rental') NOT NULL COMMENT '平台类型',
    link_url VARCHAR(500) NOT NULL COMMENT '链接地址',
    region VARCHAR(50) NOT NULL COMMENT '地区',
    language_code VARCHAR(10) NOT NULL COMMENT '语言代码',
    is_enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    click_count INT DEFAULT 0 COMMENT '点击量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
```

```java
// Java实体类字段映射
@Entity
public class PlatformLink {
    private Long id;                    // BIGINT -> Long
    private String platformName;        // VARCHAR -> String (驼峰转换)
    private PlatformType platformType;  // ENUM -> 自定义枚举
    private String linkUrl;             // VARCHAR -> String (驼峰转换)
    private String region;              // VARCHAR -> String
    private String languageCode;        // VARCHAR -> String (驼峰转换)
    private Boolean isEnabled;          // TINYINT(1) -> Boolean (驼峰转换)
    private Integer clickCount;         // INT -> Integer (驼峰转换)
    private LocalDateTime createdAt;    // DATETIME -> LocalDateTime (驼峰转换)
}
```

```xml
<!-- MyBatis Mapper映射配置 -->
<resultMap id="PlatformLinkResultMap" type="com.yxrobot.entity.PlatformLink">
    <id column="id" property="id" jdbcType="BIGINT"/>
    <result column="platform_name" property="platformName" jdbcType="VARCHAR"/>
    <result column="platform_type" property="platformType" jdbcType="VARCHAR"/>
    <result column="link_url" property="linkUrl" jdbcType="VARCHAR"/>
    <result column="region" property="region" jdbcType="VARCHAR"/>
    <result column="language_code" property="languageCode" jdbcType="VARCHAR"/>
    <result column="is_enabled" property="isEnabled" jdbcType="TINYINT"/>
    <result column="click_count" property="clickCount" jdbcType="INTEGER"/>
    <result column="created_at" property="createdAt" jdbcType="TIMESTAMP"/>
</resultMap>
```

```typescript
// 前端TypeScript接口定义
export interface PlatformLink {
    id: number;                // Long -> number
    platformName: string;      // String -> string (保持驼峰命名)
    platformType: 'ecommerce' | 'rental'; // 枚举 -> 联合类型
    linkUrl: string;           // String -> string (保持驼峰命名)
    region: string;            // String -> string
    languageCode: string;      // String -> string (保持驼峰命名)
    isEnabled: boolean;        // Boolean -> boolean (保持驼峰命名)
    clickCount: number;        // Integer -> number (保持驼峰命名)
    createdAt: string;         // LocalDateTime -> string (ISO格式)
}
```

##### 3. 必填字段验证（以平台链接为例）
```sql
-- 数据库NOT NULL约束
CREATE TABLE platform_links (
    platform_name VARCHAR(100) NOT NULL COMMENT '平台名称',
    link_url VARCHAR(500) NOT NULL COMMENT '链接地址',
    region VARCHAR(50) NOT NULL COMMENT '地区',
    language_code VARCHAR(10) NOT NULL COMMENT '语言代码'
);
```

```java
// Java实体类验证注解（必须与数据库约束匹配）
public class PlatformLink {
    @NotBlank(message = "平台名称不能为空")
    @Size(max = 100, message = "平台名称长度不能超过100字符")
    private String platformName;
    
    @NotBlank(message = "链接地址不能为空")
    @URL(message = "链接地址格式不正确")
    @Size(max = 500, message = "链接地址长度不能超过500字符")
    private String linkUrl;
    
    @NotBlank(message = "地区不能为空")
    @Size(max = 50, message = "地区长度不能超过50字符")
    private String region;
    
    @NotBlank(message = "语言代码不能为空")
    @Size(max = 10, message = "语言代码长度不能超过10字符")
    private String languageCode;
}
```

```typescript
// 前端验证规则（必须与后端验证保持一致）
const platformLinkRules = {
    platformName: [
        { required: true, message: '平台名称不能为空', trigger: 'blur' },
        { max: 100, message: '平台名称长度不能超过100字符', trigger: 'blur' }
    ],
    linkUrl: [
        { required: true, message: '链接地址不能为空', trigger: 'blur' },
        { type: 'url', message: '链接地址格式不正确', trigger: 'blur' },
        { max: 500, message: '链接地址长度不能超过500字符', trigger: 'blur' }
    ],
    region: [
        { required: true, message: '地区不能为空', trigger: 'blur' },
        { max: 50, message: '地区长度不能超过50字符', trigger: 'blur' }
    ],
    languageCode: [
        { required: true, message: '语言代码不能为空', trigger: 'blur' },
        { max: 10, message: '语言代码长度不能超过10字符', trigger: 'blur' }
    ]
}
```

##### 4. MyBatis映射验证（确保column与property正确映射）
```xml
<!-- Mapper XML文件必须与数据库字段和Java属性完全匹配 -->
<resultMap id="PlatformLinkResultMap" type="com.yxrobot.entity.PlatformLink">
    <!-- 数据库字段 -> Java属性映射 -->
    <id column="id" property="id" jdbcType="BIGINT"/>
    <result column="platform_name" property="platformName" jdbcType="VARCHAR"/>
    <result column="platform_type" property="platformType" jdbcType="VARCHAR"/>
    <result column="link_url" property="linkUrl" jdbcType="VARCHAR"/>
    <result column="region" property="region" jdbcType="VARCHAR"/>
    <result column="language_code" property="languageCode" jdbcType="VARCHAR"/>
    <result column="is_enabled" property="isEnabled" jdbcType="TINYINT"/>
    <result column="click_count" property="clickCount" jdbcType="INTEGER"/>
    <result column="created_at" property="createdAt" jdbcType="TIMESTAMP"/>
</resultMap>

<!-- SQL查询必须使用正确的数据库字段名 -->
<select id="selectById" resultMap="PlatformLinkResultMap">
    SELECT id, platform_name, platform_type, link_url, region, 
           language_code, is_enabled, click_count, created_at
    FROM platform_links 
    WHERE id = #{id} AND is_deleted = 0
</select>

<!-- 插入语句必须使用正确的数据库字段名 -->
<insert id="insert" parameterType="com.yxrobot.entity.PlatformLink">
    INSERT INTO platform_links (
        platform_name, platform_type, link_url, region,
        language_code, is_enabled, click_count, created_at
    ) VALUES (
        #{platformName}, #{platformType}, #{linkUrl}, #{region},
        #{languageCode}, #{isEnabled}, #{clickCount}, NOW()
    )
</insert>
```

#### 验证测试规则

##### 1. 自动化验证测试
```java
/**
 * 数据字段匹配验证测试类
 * 验证数据库表字段与实体类、Mapper、前端字段的一致性
 */
@SpringBootTest
public class DataFieldMatchingTest {
    
    @Test
    public void testCharityStatsFieldMatching() {
        // 1. 验证数据库表结构
        validateDatabaseSchema("charity_stats");
        
        // 2. 验证Java实体类字段
        validateEntityFields(CharityStats.class);
        
        // 3. 验证Mapper映射
        validateMapperMapping("CharityStatsMapper.xml");
        
        // 4. 验证前端类型定义
        validateFrontendTypes("charity.ts");
    }
    
    @Test
    public void testFieldTypeConsistency() {
        // 验证字段类型在各层的一致性
        Map<String, String> dbFields = getDatabaseFields("charity_stats");
        Map<String, String> entityFields = getEntityFields(CharityStats.class);
        Map<String, String> frontendFields = getFrontendFields("charity.ts");
        
        // 断言字段类型匹配
        assertFieldTypesMatch(dbFields, entityFields, frontendFields);
    }
}
```

##### 2. 手动验证检查清单
开发新功能时必须执行以下验证步骤：

**数据库层验证**
- [ ] 检查表结构定义是否正确
- [ ] 验证字段类型、长度、约束
- [ ] 确认索引和外键设置

**Java实体类验证**
- [ ] 检查属性名称是否与数据库字段对应
- [ ] 验证数据类型转换是否正确
- [ ] 确认验证注解是否完整

**Mapper映射验证**
- [ ] 检查ResultMap映射是否完整
- [ ] 验证SQL语句字段名称
- [ ] 确认参数映射正确性

**前端类型验证**
- [ ] 检查TypeScript接口定义
- [ ] 验证表单字段绑定
- [ ] 确认验证规则一致性

##### 3. 错误检测规则
当发现字段不匹配时，必须进行全面检查：

```bash
# 字段不匹配检查脚本示例
# 1. 检查数据库字段
DESCRIBE charity_stats;

# 2. 检查Java实体类
grep -r "totalBeneficiaries" src/main/java/com/yxrobot/entity/

# 3. 检查Mapper映射
grep -r "total_beneficiaries" src/main/resources/mapper/

# 4. 检查前端类型定义
grep -r "totalBeneficiaries" src/frontend/src/types/
```

##### 4. 修复验证流程
发现字段不匹配时的修复步骤：

1. **确定标准字段定义** - 以数据库表结构为准
2. **更新Java实体类** - 修正属性名称和类型
3. **更新Mapper映射** - 修正XML映射配置
4. **更新前端类型** - 修正TypeScript接口定义
5. **更新验证规则** - 同步前后端验证逻辑
6. **执行完整测试** - 验证所有CRUD操作正常

#### 字段映射对照表（重要参考）

##### 平台链接字段映射对照表
| 数据库字段 | Java实体类属性 | MyBatis映射 | 前端TypeScript | 说明 |
|-----------|---------------|-------------|---------------|------|
| `id` | `id` | `id` | `id` | 主键ID |
| `platform_name` | `platformName` | `platformName` | `platformName` | 平台名称 |
| `platform_type` | `platformType` | `platformType` | `platformType` | 平台类型 |
| `link_url` | `linkUrl` | `linkUrl` | `linkUrl` | 链接地址 |
| `region` | `region` | `region` | `region` | 地区 |
| `country` | `country` | `country` | `country` | 国家 |
| `language_code` | `languageCode` | `languageCode` | `languageCode` | 语言代码 |
| `language_name` | `languageName` | `languageName` | `languageName` | 语言名称 |
| `is_enabled` | `isEnabled` | `isEnabled` | `isEnabled` | 是否启用 |
| `link_status` | `linkStatus` | `linkStatus` | `linkStatus` | 链接状态 |
| `last_checked_at` | `lastCheckedAt` | `lastCheckedAt` | `lastCheckedAt` | 最后检查时间 |
| `click_count` | `clickCount` | `clickCount` | `clickCount` | 点击量 |
| `conversion_count` | `conversionCount` | `conversionCount` | `conversionCount` | 转化量 |
| `created_at` | `createdAt` | `createdAt` | `createdAt` | 创建时间 |
| `updated_at` | `updatedAt` | `updatedAt` | `updatedAt` | 更新时间 |
| `is_deleted` | `isDeleted` | `isDeleted` | - | 软删除标记（前端不显示） |

##### 命名转换规则
- **数据库**: `snake_case` (如: `platform_name`, `is_enabled`)
- **Java**: `camelCase` (如: `platformName`, `isEnabled`)
- **前端**: `camelCase` (如: `platformName`, `isEnabled`)
- **MyBatis**: column使用`snake_case`，property使用`camelCase`

#### 强制执行要求
- **代码审查必检项** - 所有涉及数据字段的代码必须通过字段匹配验证
- **自动化测试覆盖** - CI/CD流程必须包含字段匹配验证测试
- **文档同步更新** - 字段变更必须同步更新API文档和数据库文档
- **版本控制记录** - 字段变更必须在提交信息中明确说明
- **前端调试验证** - 前端页面必须能够正确显示和提交所有字段数据

#### 快速验证检查清单

##### 开发新功能前必须检查
- [ ] **数据库表结构** - 确认所有字段名称、类型、约束正确
- [ ] **Java实体类** - 确认属性名称使用正确的驼峰命名
- [ ] **MyBatis映射** - 确认column和property映射关系正确
- [ ] **前端类型定义** - 确认TypeScript接口字段与后端一致
- [ ] **API接口** - 确认请求和响应数据结构匹配

##### 字段映射验证命令
```bash
# 1. 检查数据库表结构
mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "DESCRIBE platform_links;"

# 2. 搜索Java实体类字段
grep -r "private.*platformName" src/main/java/com/yxrobot/entity/

# 3. 检查MyBatis映射配置
grep -r "platform_name.*platformName" src/main/resources/mapper/

# 4. 验证前端类型定义
grep -r "platformName" src/frontend/src/api/types.ts
```

##### 常见字段映射错误
- ❌ **数据库字段**: `platform_name` → **Java属性**: `platform_name` (应该是`platformName`)
- ❌ **MyBatis映射**: `column="platformName"` (应该是`column="platform_name"`)
- ❌ **前端字段**: `platform_name` (应该是`platformName`)
- ❌ **类型不匹配**: 数据库`TINYINT(1)` → 前端`string` (应该是`boolean`)

##### 正确的字段映射示例
```javascript
// ✅ 正确的前端API调用
const platformLink = {
    platformName: 'Amazon',      // 对应数据库 platform_name
    platformType: 'ecommerce',   // 对应数据库 platform_type
    linkUrl: 'https://...',      // 对应数据库 link_url
    languageCode: 'en',          // 对应数据库 language_code
    isEnabled: true              // 对应数据库 is_enabled
}

// ❌ 错误的前端API调用
const platformLink = {
    platform_name: 'Amazon',     // 错误：应该使用驼峰命名
    platformtype: 'ecommerce',   // 错误：缺少大写字母
    url: 'https://...',          // 错误：字段名不匹配
    language: 'en',              // 错误：字段名不完整
    enabled: true                // 错误：缺少is前缀
}
```

#### 验证工具推荐
- **数据库工具**: MySQL Workbench, DBeaver
- **代码分析**: IntelliJ IDEA, SonarQube
- **前端验证**: Vue DevTools, TypeScript Compiler
- **自动化测试**: JUnit 5, Mockito, Testcontainers
- **字段映射检查**: 使用IDE的全局搜索功能验证字段一致性

## 📋 项目状态和成就

### 🎉 部署成功记录

#### Tomcat部署完成
- **构建时间**: 40.455秒（完整前后端一体化构建）
- **WAR文件**: `target/yxrobot.war` (25.9MB)
- **部署路径**: `E:\YXRobot\tomcat9\webapps\yxrobot.war`
- **Tomcat状态**: ✅ 运行中 (端口8080)
- **包含内容**: 前端静态资源 + 后端Java代码

#### 一体化部署架构
```
用户请求 → Tomcat → Spring Boot → 路由分发
                                ├── /api/* → REST API
                                ├── /static/* → 前端资源
                                └── / → index.html
```

#### Spring Boot启动状态
- **启动时间**: 2.434秒
- **运行端口**: 8081（开发环境）/ 8080（生产环境）
- **上下文路径**: `/`（开发）/ `/yxrobot`（生产）
- **健康状态**: ✅ 正常运行

#### 前端构建成果
- **模块转换**: 2,315个模块
- **CSS文件**: 36个 (总计 1.2MB)
- **JS文件**: 44个 (总计 2.8MB)
- **最大文件**: charts-4xgvsrqt.js (1.04MB)
- **压缩率**: 平均70%压缩
- **静态资源**: 110个文件已复制到后端resources/static目录

### 🏆 技术成就
- ✅ 前后端一体化架构实现
- ✅ Maven自动化构建流程
- ✅ 完整的公益项目管理系统
- ✅ 自动刷新机制实现
- ✅ 全面的测试体系（80%+覆盖率）
- ✅ 完善的错误处理机制
- ✅ 数据库查询优化和性能提升
- ✅ 单WAR包部署方案实现
- ✅ 开发和生产环境统一

### 📈 性能指标
- **构建速度**: 40秒完成完整构建
- **启动速度**: 2.4秒启动完成
- **文件大小**: 25.9MB（包含完整前后端）
- **压缩效率**: 前端资源平均70%压缩率
- **内存效率**: 单进程运行，资源占用优化

### 📊 系统功能概览

#### 已实现的核心功能
1. **公益项目管理系统**
   - 统计数据管理和可视化
   - 合作机构管理（17个测试机构）
   - 公益活动管理（16个测试活动）
   - 公益项目管理（12个测试项目）
   - 图表数据服务（多种图表类型）

2. **产品管理系统**
   - 产品CRUD操作
   - 媒体文件管理
   - 文件上传系统

3. **后台管理系统**
   - 仪表板数据展示
   - 用户权限管理
   - 系统配置管理
   - 自动刷新机制

#### 数据库设计成果
- **5个核心表**: charity_stats, charity_institutions, charity_activities, charity_projects, charity_stats_logs
- **完整测试数据**: 包含真实场景的测试数据
- **软删除机制**: 所有表支持软删除
- **索引优化**: 关键字段建立索引

### 🔧 开发工具和脚本

#### 自动化脚本
- `scripts/dev-start-safe.bat` - 安全启动脚本（自动端口清理）
- `scripts/build-all.bat` - 一键构建脚本
- `scripts/deploy-tomcat.bat` - Tomcat部署脚本
- `scripts/quick-charity-setup.bat` - 数据库快速初始化

#### 开发环境验证
- `scripts/verify-dev-environment.bat` - 环境验证脚本
- `scripts/setup-dev-environment.bat` - 环境配置脚本

### 📝 文档体系

#### 核心文档
- **COMPREHENSIVE-README.md** - 完整项目文档（本文档）
- **DEVELOPMENT-GUIDE.md** - 开发指南
- **ERROR-HANDLING-GUIDE.md** - 错误处理指南
- **AUTO-REFRESH-GUIDE.md** - 自动刷新机制指南
- **DEPLOYMENT-GUIDE.md** - 部署指南

#### 技术文档
- **charity-api-documentation.md** - 公益API文档
- **charity-development-guide.md** - 公益开发指南
- **DATABASE-INFO.md** - 数据库信息
- **README-charity-data.md** - 公益数据说明

#### 状态报告
- **DEPLOYMENT-SUCCESS.md** - 部署成功报告
- **SPRING-BOOT-STATUS.md** - Spring Boot状态报告
- **TOMCAT-DEPLOYMENT-COMPLETE.md** - Tomcat部署完成报告
- **MAPPER_FIXES_SUMMARY.md** - Mapper修复总结

---

## 故障排除

### 常见问题

#### 1. 端口占用问题
```powershell
# 查找占用端口的进程
netstat -ano | findstr :8081

# 强制终止特定进程
taskkill /f /pid [PID]

# 终止所有Java进程
taskkill /f /im java.exe
```

#### 2. 数据库连接问题
- 检查数据库服务器可访问性
- 验证连接配置信息
- 确认用户权限

#### 3. Maven构建问题
```powershell
# 清理并重新编译
mvn clean compile

# 跳过测试重新构建
mvn clean package -DskipTests
```

#### 4. 前端构建问题
```bash
# 清理node_modules并重新安装
cd src/frontend
rm -rf node_modules
npm install

# 重新构建
npm run build
```

---

## 联系支持

如果在使用过程中遇到问题，请：

1. 查看相关文档和错误日志
2. 检查开发环境配置
3. 联系开发团队获取支持
4. 提交Issue到项目仓库


## � 公益开发指息南

### 开发环境配置

#### 系统要求
- **操作系统**: Windows 10/11, macOS 10.15+, Ubuntu 18.04+
- **Java**: JDK 11或更高版本
- **Maven**: 3.6.0或更高版本
- **MySQL**: 8.0或更高版本

- **Node.js**: 18.0或更高版本（前端开发）

#### 快速开始
1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd YXRobot
   ```

2. **配置开发环境**
   ```bash
   # Windows
   scripts\setup-dev-environment.bat
   ```

3. **初始化数据库**
   ```bash
   # Windows
   scripts\init-charity-database.bat
   ```

4. **启动应用程序**
   ```bash
   # Windows
   scripts\dev-start-safe.bat
   ```

### 代码规范

#### Java代码规范
1. **命名规范**
   - 类名：使用PascalCase，如`CharityService`
   - 方法名：使用camelCase，如`getCharityStats`
   - 变量名：使用camelCase，如`totalBeneficiaries`
   - 常量名：使用UPPER_SNAKE_CASE，如`MAX_PAGE_SIZE`

2. **注释规范**
   ```java
   /**
    * 公益统计数据服务类
    * 
    * 提供公益项目统计数据的查询、更新和管理功能
    * 
    * @author YXRobot开发团队
    * @version 1.0
    * @since 2024-12-19
    */
   @Service
   public class CharityService {
       
       /**
        * 获取公益统计数据
        * 
        * @return 公益统计数据DTO对象
        * @throws CharityException 当数据查询失败时抛出
        */
       public CharityStatsDTO getCharityStats() throws CharityException {
           // 方法实现
       }
   }
   ```



---

## 📚 公益API文档

### API概述
公益项目管理API为YXRobot管理后台提供公益慈善项目的全面管理功能。

**API版本**: v1.0  
**基础URL**: `http://localhost:8081/api/admin/charity`  
**认证方式**: Bearer Token  
**数据格式**: JSON  

### 通用响应格式

#### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据内容
  },
  "timestamp": "2024-12-19T10:30:00Z"
}
```

#### 错误响应
```json
{
  "code": 400,
  "message": "请求参数错误",
  "data": null,
  "timestamp": "2024-12-19T10:30:00Z"
}
```

### 主要API接口

#### 1. 获取公益统计数据
**接口地址**: `GET /stats`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "totalBeneficiaries": 28650,
    "totalInstitutions": 342,
    "cooperatingInstitutions": 198,
    "totalVolunteers": 285,
    "totalRaised": 18500000.00,
    "totalDonated": 15200000.00,
    "totalProjects": 156,
    "activeProjects": 42,
    "completedProjects": 89,
    "totalActivities": 456,
    "thisMonthActivities": 28,
    "lastUpdated": "2024-12-19T10:30:00Z"
  }
}
```

#### 2. 获取合作机构列表
**接口地址**: `GET /institutions`

**请求参数**:
- `page` (可选): 页码，默认为1
- `pageSize` (可选): 每页数量，默认为10
- `keyword` (可选): 搜索关键词
- `type` (可选): 机构类型
- `status` (可选): 机构状态

#### 3. 获取公益活动列表
**接口地址**: `GET /activities`

**请求参数**:
- `page` (可选): 页码，默认为1
- `pageSize` (可选): 每页数量，默认为10
- `keyword` (可选): 搜索关键词
- `type` (可选): 活动类型
- `status` (可选): 活动状态

### 错误代码说明

| 错误代码 | 说明 | 解决方案 |
|---------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式和必填字段 |
| 401 | 未授权访问 | 检查认证令牌是否有效 |
| 403 | 权限不足 | 确认用户具有相应操作权限 |
| 404 | 资源不存在 | 检查请求的资源ID是否正确 |
| 500 | 服务器内部错误 | 联系技术支持或查看服务器日志 |

---

## 🔧 公益API错误修复方案

### 常见问题及解决方案

#### 1. 数据库连接问题
**问题描述**: 前端调用API时出现连接错误

**解决步骤**:
1. **检查MySQL服务**：确保MySQL服务已启动
2. **验证连接配置**：确认数据库连接参数正确
3. **初始化数据库**：
   ```bash
   cd E:\YXRobot\mysql-9.3.0-winx64\bin
   mysql.exe -hlocalhost -P3306 -uroot -p
   CREATE DATABASE IF NOT EXISTS yxrobot DEFAULT CHARSET utf8mb4;
   USE yxrobot;
   source E:\YXRobot\workspace\projects\YXRobot\scripts\simple-charity-init.sql
   ```

#### 2. API接口错误
**问题描述**: 获取增强公益统计数据失败

**修复方案**:
1. **数据库初始化**：使用批处理脚本初始化
   ```bash
   cd E:\YXRobot\workspace\projects\YXRobot\scripts
   mysql-init-charity.bat
   ```

2. **验证数据**：
   ```sql
   USE yxrobot;
   SELECT * FROM charity_stats WHERE deleted = 0;
   ```

3. **测试API**：
   ```
   GET http://localhost:8081/api/admin/charity/enhanced-stats
   ```

#### 3. 数据同步问题
**解决方案**:
- 重启应用重新加载数据
- 验证数据库连接配置

#### 4. 项目启动问题

##### 端口占用问题
**问题描述**: 启动时提示端口8081被占用

**解决方案**:
```bash
# 1. 查找占用端口的进程
netstat -ano | findstr :8081

# 2. 强制终止Java进程
taskkill /f /im java.exe

# 3. 重新启动项目
mvn spring-boot:run
```

##### Maven启动失败
**问题描述**: `mvn spring-boot:run` 命令执行失败

**解决方案**:
```bash
# 1. 清理项目
mvn clean

# 2. 编译项目
mvn compile

# 3. 启动项目
mvn spring-boot:run
```

##### 数据库连接失败
**问题描述**: 启动时数据库连接失败

**解决方案**:
1. 确认MySQL服务已启动
2. 检查数据库连接配置
3. 验证数据库用户权限

### 故障排除检查清单
- [ ] MySQL服务已启动
- [ ] 数据库连接配置正确
- [ ] 数据库表结构完整
- [ ] 测试数据已初始化
- [ ] 端口8081未被占用
- [ ] Maven环境配置正确
- [ ] 后端服务正常运行
- [ ] API接口响应正常

---

## 📝 文档维护信息

**YXRobot开发团队 - 专业的练字机器人管理系统**

- **创建时间**: 2024-12-19
- **最后更新**: 2025-08-20  
- **文档版本**: v2.1
- **整合文档数量**: 25个核心文档
- **文档状态**: ✅ 已优化，去除重复内容
