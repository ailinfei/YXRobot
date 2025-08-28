# Admin Content - 公益项目管理页面设计文档

## 概述

公益项目管理页面是 YXRobot 管理后台内容管理模块的重要功能，采用现代化的前后端分离架构，为管理员提供公益慈善项目的全面管理。该页面已完成前端开发，包含公益统计数据展示、合作机构管理、公益活动管理、统计图表分析和数据更新等功能，需要开发对应的后端API支持。

## 架构

### 系统架构
```
┌─────────────────────────────────────────────────────────────┐
│                    前端展示层 (Vue.js 3)                      │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │   统计数据展示    │ │   合作机构管理    │ │   公益活动管理    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    API接口层 (RESTful)                       │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │   公益统计API    │ │   机构管理API    │ │   活动管理API    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 业务逻辑层 (Spring MVC)                       │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │  CharityService │ │InstitutionService│ │ ActivityService │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 数据访问层 (MyBatis)                          │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │  CharityMapper  │ │InstitutionMapper │ │ ActivityMapper  │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                 数据存储层 (MySQL 9.3)                        │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐ │
│  │charity_stats表   │ │ institutions表   │ │ activities表    │ │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### 技术栈选择

**前端技术栈:**
- **框架**: Vue.js 3 + TypeScript - 提供现代化的响应式开发体验
- **UI组件库**: Element Plus - 统一的设计语言和丰富的表单组件
- **图表库**: ECharts - 强大的数据可视化功能
- **状态管理**: Pinia - 轻量级的状态管理方案
- **路由管理**: Vue Router 4 - 页面路由和权限控制

**后端技术栈:**
- **框架**: Spring MVC + MyBatis - 成熟稳定的企业级框架
- **数据验证**: Hibernate Validator - 数据完整性验证
- **缓存**: Redis - 统计数据缓存
- **日志**: Logback - 操作日志记录## 
组件和接口

### 前端组件架构（基于实际实现）
```
Charity.vue (公益项目管理主页面)
├── 页面头部 (page-header)
│   ├── 标题和描述 (header-left)
│   └── 更新公益数据按钮 (header-right)
├── CharityStatsOverview (公益统计数据概览组件)
│   ├── 统计卡片网格 (stats-grid)
│   ├── 受益人数统计 (beneficiaries-card)
│   ├── 机构统计 (institutions-card)
│   ├── 项目统计 (projects-card)
│   ├── 资金统计 (funding-card)
│   └── 活动统计 (activities-card)
├── 标签页导航 (el-tabs)
│   ├── 合作机构标签页 (institutions)
│   │   └── InstitutionManagement (机构管理组件)
│   │       ├── 机构列表表格 (DataTable)
│   │       ├── 新增机构按钮
│   │       ├── 编辑机构功能
│   │       └── 删除机构功能
│   ├── 公益活动标签页 (activities)
│   │   └── ActivityManagement (活动管理组件)
│   │       ├── 活动列表表格 (DataTable)
│   │       ├── 新增活动按钮
│   │       ├── 编辑活动功能
│   │       └── 删除活动功能
│   └── 统计数据标签页 (statistics)
│       ├── 图表区域 (charts-section)
│       │   ├── 项目状态分布图 (projectStatusChart)
│       │   ├── 资金筹集趋势图 (fundingTrendChart)
│       │   ├── 地区分布图 (regionDistributionChart)
│       │   └── 志愿者活动统计图 (volunteerActivityChart)
│       └── 图表数据管理
├── CommonDialog (数据更新对话框)
│   └── FormValidator (数据更新表单)
│       ├── 基础统计数据分组
│       │   ├── 累计受益人数 (el-input-number)
│       │   ├── 合作机构总数 (el-input-number)
│       │   ├── 活跃合作机构 (el-input-number)
│       │   └── 志愿者总数 (el-input-number)
│       ├── 资金统计数据分组
│       │   ├── 累计筹集金额 (el-input-number)
│       │   └── 累计捐赠金额 (el-input-number)
│       ├── 项目统计数据分组
│       │   ├── 项目总数 (el-input-number)
│       │   ├── 进行中项目 (el-input-number)
│       │   └── 已完成项目 (el-input-number)
│       ├── 活动统计数据分组
│       │   ├── 活动总数 (el-input-number)
│       │   └── 本月活动数 (el-input-number)
│       └── 更新说明 (el-input textarea)
└── 数据验证和提交逻辑
```

### API接口设计（基于前端实现）

#### 公益统计数据API

**1. 获取公益统计数据**
```http
GET /api/admin/charity/stats
Authorization: Bearer {token}
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProjects": 156,
    "activeProjects": 42,
    "completedProjects": 89,
    "totalBeneficiaries": 28650,
    "totalRaised": 18500000,
    "totalDonated": 15200000,
    "totalVolunteers": 285,
    "activeVolunteers": 168,
    "totalInstitutions": 342,
    "cooperatingInstitutions": 198,
    "totalActivities": 456,
    "thisMonthActivities": 28,
    "lastUpdated": "2024-12-19T10:00:00Z"
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

**2. 获取增强统计数据（包含趋势）**
```http
GET /api/admin/charity/enhanced-stats
Authorization: Bearer {token}
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalProjects": 156,
    "activeProjects": 42,
    "completedProjects": 89,
    "totalBeneficiaries": 28650,
    "totalRaised": 18500000,
    "totalDonated": 15200000,
    "totalVolunteers": 285,
    "totalInstitutions": 342,
    "cooperatingInstitutions": 198,
    "totalActivities": 456,
    "thisMonthActivities": 28,
    "trends": {
      "beneficiariesTrend": [22000, 23500, 25200, 26800, 27900, 28650],
      "institutionsTrend": [280, 295, 315, 325, 335, 342],
      "projectsTrend": [120, 135, 142, 148, 152, 156],
      "fundingTrend": [12500000, 14200000, 15800000, 16900000, 17800000, 18500000]
    },
    "monthlyComparison": {
      "beneficiariesChange": 12.5,
      "institutionsChange": 8.3,
      "projectsChange": 15.2,
      "fundingChange": 6.8
    },
    "lastUpdated": "2024-12-19T10:00:00Z"
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```**3. 更新
公益统计数据**
```http
PUT /api/admin/charity/stats
Authorization: Bearer {token}
Content-Type: application/json

{
  "totalBeneficiaries": 28650,
  "totalInstitutions": 342,
  "cooperatingInstitutions": 198,
  "totalVolunteers": 285,
  "totalRaised": 18500000,
  "totalDonated": 15200000,
  "totalProjects": 156,
  "activeProjects": 42,
  "completedProjects": 89,
  "totalActivities": 456,
  "thisMonthActivities": 28,
  "updateReason": "月度数据统计更新"
}
```

**4. 获取图表数据**
```http
GET /api/admin/charity/chart-data
Authorization: Bearer {token}
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "projectStatusData": [
      {"name": "进行中", "value": 42, "color": "#409EFF"},
      {"name": "已完成", "value": 89, "color": "#67C23A"},
      {"name": "规划中", "value": 25, "color": "#E6A23C"}
    ],
    "fundingTrendData": {
      "months": ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"],
      "raisedData": [850000, 920000, 1150000, 1380000, 1620000, 1450000, 1680000, 1820000, 1950000, 2100000, 1980000, 2250000],
      "donatedData": [720000, 780000, 980000, 1150000, 1350000, 1200000, 1420000, 1550000, 1680000, 1800000, 1720000, 1950000]
    },
    "regionDistributionData": [
      {"name": "华北", "value": 28, "color": "#409EFF"},
      {"name": "华东", "value": 35, "color": "#67C23A"},
      {"name": "华南", "value": 22, "color": "#E6A23C"}
    ],
    "volunteerActivityData": {
      "months": ["7月", "8月", "9月", "10月", "11月", "12月"],
      "volunteerData": [85, 92, 78, 105, 118, 125],
      "activityData": [12, 15, 11, 18, 22, 25]
    }
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

#### 合作机构管理API

**1. 获取合作机构列表**
```http
GET /api/admin/charity/institutions
Authorization: Bearer {token}
Query Parameters:
- page: int (页码，默认1)
- pageSize: int (每页数量，默认10)
- keyword: string (搜索关键词)
- type: string (机构类型)
- status: string (机构状态)
```

响应格式:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [
      {
        "id": 1,
        "name": "希望小学",
        "type": "school",
        "location": "贵州省黔东南州",
        "address": "贵州省黔东南州凯里市希望路123号",
        "contactPerson": "张校长",
        "contactPhone": "13900139001",
        "email": "hope.school@example.com",
        "studentCount": 450,
        "cooperationDate": "2023-03-15",
        "status": "active",
        "deviceCount": 8,
        "lastVisitDate": "2024-11-20",
        "notes": "学校积极配合公益项目，学生学习热情高涨",
        "createdAt": "2023-02-10"
      }
    ],
    "total": 80,
    "page": 1,
    "pageSize": 10,
    "totalPages": 8
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```