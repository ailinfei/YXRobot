# Admin Business - 租赁数据分析模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 基于现有前端页面的后端API设计原则

**本设计文档基于现有的 RentalAnalytics.vue 前端页面，要求后端API设计完全适配前端页面的功能需求和数据结构：**

#### 🔥 前端页面架构分析

现有RentalAnalytics.vue页面采用以下技术架构：
1. **Vue 3 Composition API**：使用`<script setup>`语法和响应式数据绑定
2. **Element Plus UI组件**：使用el-table、el-card、el-dialog、el-select等组件
3. **ECharts图表库**：用于租赁趋势、利用率排行、地区分布等图表展示
4. **TypeScript类型定义**：严格的类型检查和接口定义
5. **模块化API调用**：通过mockRentalAPI模块调用后端接口

#### 🔥 后端API设计要求

1. **完全适配前端**：
   - ✅ **必须提供**：与前端页面功能完全匹配的API接口
   - ✅ **必须支持**：前端页面的所有数据查询、筛选、分页需求
   - ❌ **严禁要求**：前端页面修改数据结构或字段名称
   - ❌ **严禁返回**：与前端TypeScript接口不匹配的数据格式

2. **响应式数据支持**：
   - 支持前端页面的响应式数据绑定需求
   - 提供实时数据查询和统计计算
   - 支持时间周期动态筛选和数据刷新
   - 确保API响应格式与前端期望完全一致

#### 🚨 前端页面功能模块分析（后端必须支持）

**页面头部功能**：
- 页面标题和描述显示
- 响应式布局设计

**核心指标卡片**：
- 租赁总收入（带增长率）
- 租赁设备数（带增长率）
- 设备利用率（带增长率）
- 平均租期（带增长率）

**图表分析区域**：
- 租赁趋势分析（混合图表：收入+订单数+利用率）
- 设备利用率排行（水平柱状图）
- 地区分布（饼图）
- 设备型号分析（混合图表）

**设备利用率详情表格**：
- 搜索功能（设备ID）
- 筛选功能（设备型号、状态）
- 分页显示（默认20条/页）
- 查看详情操作

**右侧信息面板**：
- 今日概览统计
- 设备状态分布
- 利用率TOP5排行

## 概述

租赁数据分析模块是YXRobot管理后台的核心业务分析功能，基于现有的RentalAnalytics.vue前端页面提供完整的后端API支持。该模块需要开发与前端页面完全匹配的后端接口，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/rental-analytics。

**真实数据原则：**
- 前端组件不得包含任何硬编码的模拟数据
- 所有前端显示的数据必须通过API从数据库获取
- 数据库中可以包含真实的业务数据（通过管理后台录入或初始化脚本插入）
- 空数据状态必须正确处理，不得在前端组件中硬编码模拟数据填充

## 架构设计

### 系统架构（基于现有前端页面）

```
前端层 (已存在 - RentalAnalytics.vue)
├── 页面头部 (标题和描述)
├── 核心指标卡片 (4个统计卡片)
├── 图表分析区域 (4个ECharts图表)
├── 设备利用率详情表格 (DataTable组件)
├── 右侧信息面板 (3个信息卡片)
├── 设备详情弹窗 (DeviceDetailDialog组件)
└── 响应式数据绑定 (Vue 3 Composition API)

API层 (需要开发 - 适配前端)
├── GET /api/rental/stats (租赁统计数据)
├── GET /api/rental/devices (设备利用率列表)
├── GET /api/rental/charts/trends (租赁趋势图表数据)
├── GET /api/rental/charts/distribution (分布图表数据)
├── GET /api/rental/today-stats (今日概览数据)
└── 统一响应格式 {code, data, message}

业务层 (需要开发)
├── RentalService (租赁记录管理)
├── RentalStatsService (统计数据计算)
├── RentalAnalysisService (图表数据分析)
├── DeviceUtilizationService (设备利用率管理)
└── 复杂关联查询支持

数据层 (需要创建)
├── rental_records (租赁记录主表)
├── rental_devices (租赁设备表)
├── device_utilization (设备利用率表)
├── rental_stats (租赁统计表)
└── 优化的索引和关联查询
```

### 核心组件

#### 1. 前端组件结构（已存在 - RentalAnalytics.vue）
- **页面头部区域**: 
  - 标题和描述显示
  - 响应式布局设计
- **核心指标卡片区域**: 
  - 4个统计卡片（租赁总收入、设备数、利用率、平均租期）
  - CountUp动画组件
  - 增长率显示和趋势指示
  - 响应式网格布局
- **图表分析区域**: 
  - 4个ECharts图表（趋势、排行、分布、型号分析）
  - 时间周期选择器
  - 图表刷新和加载状态
- **设备利用率详情表格区域**: 
  - Element Plus表格组件
  - 搜索和筛选功能
  - 分页控制
  - 查看详情操作
- **右侧信息面板**: 
  - 今日概览卡片
  - 设备状态分布卡片
  - 利用率TOP5排行卡片
- **设备详情弹窗**: 
  - DeviceDetailDialog组件
  - 详细设备信息显示

#### 2. 后端服务组件（需要开发）
- **RentalController**: 
  - 适配前端API调用需求
  - 统一响应格式处理
  - 参数验证和错误处理
- **RentalService**: 
  - 租赁记录查询和管理
  - 支持复杂筛选和分页
  - 关联查询优化
- **RentalStatsService**: 
  - 实时统计数据计算
  - 增长率和趋势分析
  - 按时间周期聚合统计
- **RentalAnalysisService**: 
  - 图表数据生成
  - ECharts格式适配
  - 多维度数据分析
- **DeviceUtilizationService**: 
  - 设备利用率计算
  - 设备状态管理
  - 性能指标分析

#### 3. 数据适配组件（需要开发）
- **ResponseFormatter**: 统一API响应格式
- **FieldMapper**: 数据库字段到前端字段的映射
- **ChartDataFormatter**: 图表数据格式转换
- **PaginationHandler**: 分页数据处理

## 数据模型设计

**🔥 字段映射设计标准**

所有数据模型必须严格遵循四层字段映射标准：

```
数据库层 (snake_case) → Java层 (camelCase) → MyBatis映射 → 前端层 (camelCase)
     ↓                      ↓                    ↓                ↓
  rental_revenue      →  rentalRevenue     →  column/property  →  rentalRevenue
  device_id           →  deviceId          →  column/property  →  deviceId  
  utilization_rate    →  utilizationRate   →  column/property  →  utilizationRate
```

### 租赁记录主表 (rental_records)

```sql
CREATE TABLE `rental_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '租赁记录ID，主键',
  `rental_order_number` VARCHAR(50) NOT NULL COMMENT '租赁订单号',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `rental_start_date` DATE NOT NULL COMMENT '租赁开始日期',
  `rental_end_date` DATE COMMENT '租赁结束日期',
  `planned_end_date` DATE NOT NULL COMMENT '计划结束日期',
  `rental_period` INT NOT NULL COMMENT '租赁期间（天数）',
  `daily_rental_fee` DECIMAL(10,2) NOT NULL COMMENT '日租金',
  `total_rental_fee` DECIMAL(12,2) NOT NULL COMMENT '总租金',
  `deposit_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '押金金额',
  `actual_payment` DECIMAL(12,2) NOT NULL COMMENT '实际支付金额',
  `payment_status` ENUM('unpaid', 'partial', 'paid', 'refunded') DEFAULT 'unpaid' COMMENT '付款状态',
  `rental_status` ENUM('pending', 'active', 'completed', 'cancelled', 'overdue') DEFAULT 'pending' COMMENT '租赁状态',
  `delivery_method` VARCHAR(50) COMMENT '交付方式',
  `delivery_address` TEXT COMMENT '交付地址',
  `return_date` DATE COMMENT '实际归还日期',
  `return_condition` VARCHAR(100) COMMENT '归还状态',
  `notes` TEXT COMMENT '备注信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rental_order_number` (`rental_order_number`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_rental_start_date` (`rental_start_date`),
  INDEX `idx_rental_status` (`rental_status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁记录表';
```

### 租赁设备表 (rental_devices)

```sql
CREATE TABLE `rental_devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备编号',
  `device_model` VARCHAR(100) NOT NULL COMMENT '设备型号',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_category` VARCHAR(100) NOT NULL COMMENT '设备类别',
  `serial_number` VARCHAR(100) COMMENT '序列号',
  `purchase_date` DATE COMMENT '采购日期',
  `purchase_price` DECIMAL(10,2) COMMENT '采购价格',
  `daily_rental_price` DECIMAL(8,2) NOT NULL COMMENT '日租金价格',
  `current_status` ENUM('active', 'idle', 'maintenance', 'retired') DEFAULT 'idle' COMMENT '当前状态',
  `location` VARCHAR(200) COMMENT '设备位置',
  `region` VARCHAR(100) COMMENT '所在地区',
  `performance_score` INT DEFAULT 100 COMMENT '性能评分（0-100）',
  `signal_strength` INT DEFAULT 100 COMMENT '信号强度（0-100）',
  `maintenance_status` ENUM('normal', 'warning', 'urgent') DEFAULT 'normal' COMMENT '维护状态',
  `last_maintenance_date` DATE COMMENT '最后维护日期',
  `next_maintenance_date` DATE COMMENT '下次维护日期',
  `total_rental_days` INT DEFAULT 0 COMMENT '累计租赁天数',
  `total_available_days` INT DEFAULT 0 COMMENT '累计可用天数',
  `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '利用率',
  `last_rental_date` DATE COMMENT '最后租赁日期',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_device_model` (`device_model`),
  INDEX `idx_current_status` (`current_status`),
  INDEX `idx_region` (`region`),
  INDEX `idx_utilization_rate` (`utilization_rate`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁设备表';
```

### 设备利用率表 (device_utilization)

```sql
CREATE TABLE `device_utilization` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '利用率记录ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_period` ENUM('daily', 'weekly', 'monthly', 'yearly') NOT NULL COMMENT '统计周期',
  `rental_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '租赁小时数',
  `available_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '可用小时数',
  `utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '利用率',
  `rental_revenue` DECIMAL(10,2) DEFAULT 0 COMMENT '租赁收入',
  `rental_count` INT DEFAULT 0 COMMENT '租赁次数',
  `average_rental_duration` DECIMAL(8,2) DEFAULT 0 COMMENT '平均租赁时长',
  `downtime_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '停机时间',
  `maintenance_hours` DECIMAL(8,2) DEFAULT 0 COMMENT '维护时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_stat_date_period` (`device_id`, `stat_date`, `stat_period`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_stat_date` (`stat_date`),
  INDEX `idx_stat_period` (`stat_period`),
  INDEX `idx_utilization_rate` (`utilization_rate`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备利用率表';
```

### 租赁统计表 (rental_stats)

```sql
CREATE TABLE `rental_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_type` ENUM('daily', 'weekly', 'monthly', 'yearly') NOT NULL COMMENT '统计类型',
  `total_rental_revenue` DECIMAL(15,2) DEFAULT 0 COMMENT '总租赁收入',
  `total_rental_orders` INT DEFAULT 0 COMMENT '总租赁订单数',
  `total_rental_devices` INT DEFAULT 0 COMMENT '总租赁设备数',
  `active_rental_devices` INT DEFAULT 0 COMMENT '活跃租赁设备数',
  `average_rental_period` DECIMAL(8,2) DEFAULT 0 COMMENT '平均租赁期间',
  `average_daily_revenue` DECIMAL(10,2) DEFAULT 0 COMMENT '平均日收入',
  `device_utilization_rate` DECIMAL(5,2) DEFAULT 0 COMMENT '设备利用率',
  `new_customers` INT DEFAULT 0 COMMENT '新客户数',
  `returning_customers` INT DEFAULT 0 COMMENT '回头客户数',
  `top_device_model` VARCHAR(100) COMMENT '最受欢迎设备型号',
  `top_region` VARCHAR(100) COMMENT '最活跃地区',
  `revenue_growth_rate` DECIMAL(8,4) DEFAULT 0 COMMENT '收入增长率',
  `device_growth_rate` DECIMAL(8,4) DEFAULT 0 COMMENT '设备增长率',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type` (`stat_date`, `stat_type`),
  INDEX `idx_stat_date` (`stat_date`),
  INDEX `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁统计表';
```

### 客户信息表 (rental_customers)

```sql
CREATE TABLE `rental_customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID，主键',
  `customer_name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_type` ENUM('individual', 'enterprise', 'institution') NOT NULL COMMENT '客户类型',
  `contact_person` VARCHAR(100) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱地址',
  `address` TEXT COMMENT '地址',
  `region` VARCHAR(100) COMMENT '所在地区',
  `industry` VARCHAR(100) COMMENT '所属行业',
  `credit_level` ENUM('A', 'B', 'C', 'D') DEFAULT 'B' COMMENT '信用等级',
  `total_rental_amount` DECIMAL(12,2) DEFAULT 0 COMMENT '累计租赁金额',
  `total_rental_days` INT DEFAULT 0 COMMENT '累计租赁天数',
  `last_rental_date` DATE COMMENT '最后租赁日期',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否活跃',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_name` (`customer_name`),
  INDEX `idx_customer_type` (`customer_type`),
  INDEX `idx_region` (`region`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租赁客户表';
```

## 接口设计

### REST API 接口（基于前端页面需求）

#### 1. 租赁统计管理接口

```typescript
// 获取租赁统计数据 - 前端调用：mockRentalAPI.getRentalStats()
GET /api/rental/stats
参数: {
  startDate?: string,   // 开始日期 YYYY-MM-DD
  endDate?: string      // 结束日期 YYYY-MM-DD
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    totalRentalRevenue: number,      // 租赁总收入
    totalRentalDevices: number,      // 租赁设备总数
    activeRentalDevices: number,     // 活跃租赁设备数
    deviceUtilizationRate: number,   // 设备利用率
    averageRentalPeriod: number,     // 平均租期
    totalRentalOrders: number,       // 租赁订单总数
    revenueGrowthRate: number,       // 收入增长率
    deviceGrowthRate: number         // 设备增长率
  }
}

// RentalStats数据结构（必须与前端TypeScript接口匹配）
interface RentalStats {
  totalRentalRevenue: number;
  totalRentalDevices: number;
  activeRentalDevices: number;
  deviceUtilizationRate: number;
  averageRentalPeriod: number;
  totalRentalOrders: number;
  revenueGrowthRate: number;
  deviceGrowthRate: number;
}
```

#### 2. 设备利用率管理接口

```typescript
// 获取设备利用率列表 - 前端调用：mockRentalAPI.getDeviceUtilizationData({})
GET /api/rental/devices
参数: {
  page?: number,        // 页码，默认1
  pageSize?: number,    // 每页大小，默认20
  deviceModel?: string, // 设备型号筛选
  status?: string,      // 状态筛选
  keyword?: string      // 搜索关键词（设备ID）
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: DeviceUtilizationData[],  // 设备利用率数组
    total: number,                  // 总记录数
    page: number,                   // 当前页码
    pageSize: number                // 每页大小
  }
}

// DeviceUtilizationData数据结构（必须与前端TypeScript接口匹配）
interface DeviceUtilizationData {
  deviceId: string;           // 设备ID
  deviceModel: string;        // 设备型号
  utilizationRate: number;    // 利用率
  totalRentalDays: number;    // 租赁天数
  totalAvailableDays: number; // 可用天数
  currentStatus: string;      // 当前状态
  lastRentalDate?: string;    // 最后租赁日期
  region: string;             // 所在地区
  performanceScore: number;   // 性能评分
  signalStrength: number;     // 信号强度
  maintenanceStatus: string;  // 维护状态
}
```

#### 3. 图表数据接口

```typescript
// 获取租赁趋势图表数据 - 前端调用：mockRentalAPI.getRentalTrendData({period})
GET /api/rental/charts/trends
参数: {
  period: 'daily' | 'weekly' | 'monthly' | 'quarterly',  // 时间周期
  startDate?: string,     // 开始日期
  endDate?: string        // 结束日期
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    categories: string[],   // X轴数据（日期）
    series: [
      {
        name: "租赁收入",
        data: number[]      // 收入数据
      },
      {
        name: "订单数量",
        data: number[]      // 订单数据
      },
      {
        name: "设备利用率",
        data: number[]      // 利用率数据
      }
    ]
  }
}

// 获取分布图表数据 - 前端调用：mockRentalAPI.getRentalRevenueAnalysis({})
GET /api/rental/charts/distribution
参数: {
  type: 'region' | 'device-model' | 'utilization-ranking',  // 分布类型
  startDate?: string,
  endDate?: string
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    categories: string[],   // 分类名称
    series: [
      {
        name: "数据名称",
        data: number[]      // 对应的数值
      }
    ]
  }
}

// RentalTrendData数据结构（必须与前端TypeScript接口匹配）
interface RentalTrendData {
  date: string;
  revenue: number;
  orderCount: number;
  deviceCount: number;
  utilizationRate: number;
}
```

#### 4. 今日概览接口

```typescript
// 获取今日概览数据 - 前端调用：todayStats数据绑定
GET /api/rental/today-stats
响应: {
  code: 200,
  message: "查询成功",
  data: {
    revenue: number,        // 今日收入
    orders: number,         // 新增订单
    activeDevices: number,  // 活跃设备
    avgUtilization: number  // 平均利用率
  }
}

// 获取设备状态统计 - 前端调用：deviceStatusStats计算属性
GET /api/rental/device-status-stats
响应: {
  code: 200,
  message: "查询成功",
  data: {
    active: number,      // 运行中设备数
    idle: number,        // 空闲设备数
    maintenance: number  // 维护中设备数
  }
}
```

#### 5. 辅助功能接口

```typescript
// 获取利用率TOP设备
GET /api/rental/top-devices
参数: {
  limit?: number,  // 返回数量，默认5
  type?: string    // 排序类型：utilization、revenue
}
响应: {
  code: 200,
  data: DeviceUtilizationData[]
}

// 批量操作
POST /api/rental/batch
请求体: {
  ids: string[],
  operation: 'updateStatus' | 'maintenance',
  params?: object
}
响应: {
  code: 200,
  message: "批量操作成功"
}
```

## 空数据状态处理设计

### 空状态检测机制

```typescript
interface EmptyStateConfig {
  // 检测数据是否为空
  isEmpty: (data: any[]) => boolean;
  // 空状态显示配置
  emptyStateProps: {
    title: string;
    description: string;
    actionText: string;
    actionHandler: () => void;
  };
}

const rentalEmptyStateConfig: EmptyStateConfig = {
  isEmpty: (deviceList) => !deviceList || deviceList.length === 0,
  emptyStateProps: {
    title: "暂无租赁数据",
    description: "还没有录入任何租赁记录，点击下方按钮录入第一条租赁数据",
    actionText: "录入第一条租赁数据",
    actionHandler: () => openCreateRentalDialog()
  }
};
```

### 前端空状态处理

```vue
<template>
  <div class="rental-analytics">
    <!-- 正常数据显示 -->
    <div v-if="!isEmpty" class="rental-content">
      <!-- 核心指标卡片 -->
      <div class="metrics-cards">
        <div v-for="metric in metricsData" :key="metric.key" class="metric-card">
          <div class="card-content">
            <div class="card-title">{{ metric.title }}</div>
            <div class="card-value">
              <CountUp :value="metric.value" :decimals="metric.decimals" />
            </div>
          </div>
        </div>
      </div>
      
      <!-- 图表区域 -->
      <div class="charts-section">
        <div class="charts-row">
          <div class="chart-card">
            <rental-trend-chart :data="trendData" />
          </div>
          <div class="chart-card">
            <utilization-ranking-chart :data="utilizationData" />
          </div>
        </div>
      </div>
      
      <!-- 设备利用率表格 -->
      <device-utilization-table :data="deviceList" />
      <pagination :total="total" />
    </div>
    
    <!-- 空状态显示 -->
    <empty-state 
      v-else
      :title="emptyStateConfig.title"
      :description="emptyStateConfig.description"
      :action-text="emptyStateConfig.actionText"
      @action="emptyStateConfig.actionHandler"
    />
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue';
import { getRentalStats, getDeviceUtilizationData } from '@/api/rental';

const deviceList = ref([]);
const rentalStats = ref(null);
const trendData = ref([]);
const total = ref(0);
const loading = ref(false);

const isEmpty = computed(() => {
  return !deviceList.value || deviceList.value.length === 0;
});

// 加载租赁数据
const loadRentalData = async () => {
  loading.value = true;
  try {
    const [statsRes, deviceRes] = await Promise.all([
      getRentalStats(),
      getDeviceUtilizationData()
    ]);
    
    // 绑定真实数据，可能为空
    rentalStats.value = statsRes.data;
    deviceList.value = deviceRes.data.list;
    total.value = deviceRes.data.total;
    
    // 只有在有数据时才加载图表
    if (!isEmpty.value) {
      await loadChartsData();
    }
  } catch (error) {
    console.error('加载租赁数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 严禁在空状态时显示模拟数据
const showMockData = false; // 永远为false

onMounted(() => {
  loadRentalData();
});
</script>
```

### 后端空数据响应

```java
@RestController
@RequestMapping("/api/rental")
public class RentalController {
    
    @GetMapping("/devices")
    public ResponseEntity<Map<String, Object>> getDeviceUtilizationData(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String deviceModel,
        @RequestParam(required = false) String status) {
        
        // 查询真实数据
        List<DeviceUtilizationDTO> deviceList = rentalService.getDeviceUtilizationData(page, pageSize, deviceModel, status);
        
        // 即使数据为空，也返回真实的空结果，不插入模拟数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", Map.of(
            "list", deviceList, // 可能为空数组
            "total", rentalService.getTotalDeviceCount(),
            "page", page,
            "pageSize", pageSize,
            "isEmpty", deviceList.isEmpty() // 明确标识是否为空
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRentalStats() {
        // 只有在有租赁数据时才返回统计信息
        RentalStatsDTO stats = rentalService.getRentalStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", stats); // 可能包含全零的统计数据
        
        return ResponseEntity.ok(response);
    }
}
```

### 数据初始化防护

```java
@Service
public class DataInitializationService {
    
    private void initializeRentalData() throws SQLException {
        // 只初始化必要的基础数据
        if (!hasExistingDevices(connection)) {
            insertBasicDeviceTypes(connection);
        }
        
        if (!hasExistingCustomers(connection)) {
            insertBasicCustomerTypes(connection);
        }
        
        // 严禁插入示例租赁记录
        // 确保rental_records表保持空状态，让用户手动录入真实数据
        logger.info("租赁数据初始化完成 - 未插入任何示例租赁记录，保持真实数据原则");
    }
    
    // 移除或禁用示例租赁记录插入方法
    private void insertSampleRentalRecords(Connection connection) throws SQLException {
        // 此方法已被禁用 - 不插入任何示例租赁记录
        logger.warn("示例租赁记录插入已被禁用 - 遵循真实数据原则");
        return; // 直接返回，不执行任何插入操作
    }
}
```

## 业务逻辑设计

### 真实数据验证机制

```typescript
class RealDataValidator {
  // 验证数据是否为真实用户创建
  static validateDataSource(rentalRecord: RentalRecord): boolean {
    // 检查是否包含模拟数据特征
    const mockDataIndicators = [
      '示例', '测试', 'demo', 'sample', 'mock', 'fake', 'test'
    ];
    
    const customerName = rentalRecord.customerName?.toLowerCase() || '';
    const orderNumber = rentalRecord.rentalOrderNumber?.toLowerCase() || '';
    
    const hasIndicators = mockDataIndicators.some(indicator => 
      customerName.includes(indicator) || orderNumber.includes(indicator)
    );
    
    if (hasIndicators) {
      console.warn('检测到可能的模拟数据:', rentalRecord.rentalOrderNumber);
    }
    
    return !hasIndicators;
  }
  
  // 验证数据创建来源
  static validateCreationSource(rentalRecord: RentalRecord): boolean {
    // 确保数据来自用户操作，而非系统自动生成
    return !rentalRecord.rentalOrderNumber?.startsWith('SYS') && 
           !rentalRecord.notes?.includes('系统生成');
  }
}
```

### 租赁状态管理

```typescript
enum RentalStatus {
  PENDING = 'pending',      // 待确认
  ACTIVE = 'active',        // 租赁中
  COMPLETED = 'completed',  // 已完成
  CANCELLED = 'cancelled',  // 已取消
  OVERDUE = 'overdue'       // 逾期
}

enum PaymentStatus {
  UNPAID = 'unpaid',       // 未付款
  PARTIAL = 'partial',     // 部分付款
  PAID = 'paid',           // 已付款
  REFUNDED = 'refunded'    // 已退款
}

enum DeviceStatus {
  ACTIVE = 'active',       // 运行中
  IDLE = 'idle',           // 空闲
  MAINTENANCE = 'maintenance', // 维护中
  RETIRED = 'retired'      // 已退役
}

// 状态转换规则
const statusTransitions = {
  pending: ['active', 'cancelled'],
  active: ['completed', 'overdue', 'cancelled'],
  completed: [],
  cancelled: [],
  overdue: ['completed', 'cancelled']
}
```

### 数据验证规则

```typescript
const rentalValidationRules = {
  rentalOrderNumber: {
    required: true,
    pattern: /^[A-Z0-9]{8,20}$/,
    message: "租赁订单号格式不正确，应为8-20位字母数字组合"
  },
  deviceId: {
    required: true,
    message: "请选择设备"
  },
  customerId: {
    required: true,
    message: "请选择客户"
  },
  rentalStartDate: {
    required: true,
    message: "请选择租赁开始日期"
  },
  rentalPeriod: {
    required: true,
    min: 1,
    max: 365,
    message: "租赁期间应在1-365天之间"
  },
  dailyRentalFee: {
    required: true,
    min: 0.01,
    max: 9999.99,
    message: "日租金应在0.01-9999.99之间"
  }
}
```

### 统计计算逻辑

```typescript
class RentalStatsCalculator {
  // 计算租赁统计数据
  static calculateStats(rentalRecords: RentalRecord[]): RentalStats {
    if (!rentalRecords || rentalRecords.length === 0) {
      return {
        totalRentalRevenue: 0,
        totalRentalDevices: 0,
        activeRentalDevices: 0,
        deviceUtilizationRate: 0,
        averageRentalPeriod: 0,
        totalRentalOrders: 0,
        revenueGrowthRate: 0,
        deviceGrowthRate: 0
      };
    }
    
    const totalRentalRevenue = rentalRecords.reduce((sum, record) => sum + record.totalRentalFee, 0);
    const totalRentalOrders = rentalRecords.length;
    const averageRentalPeriod = rentalRecords.reduce((sum, record) => sum + record.rentalPeriod, 0) / totalRentalOrders;
    
    return {
      totalRentalRevenue,
      totalRentalOrders,
      averageRentalPeriod,
      totalRentalDevices: this.calculateTotalDevices(rentalRecords),
      activeRentalDevices: this.calculateActiveDevices(rentalRecords),
      deviceUtilizationRate: this.calculateUtilizationRate(rentalRecords)
    };
  }
  
  // 计算设备利用率
  static calculateDeviceUtilization(deviceId: string, rentalRecords: RentalRecord[]): DeviceUtilizationData {
    const deviceRecords = rentalRecords.filter(record => record.deviceId === deviceId);
    
    const totalRentalDays = deviceRecords.reduce((sum, record) => sum + record.rentalPeriod, 0);
    const totalAvailableDays = 365; // 一年的天数
    const utilizationRate = (totalRentalDays / totalAvailableDays) * 100;
    
    return {
      deviceId,
      utilizationRate,
      totalRentalDays,
      totalAvailableDays,
      // ... 其他字段
    };
  }
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 复合索引：`(rental_start_date, rental_status, is_deleted)`
   - 设备索引：`device_id`
   - 客户索引：`customer_id`
   - 订单号唯一索引：`rental_order_number`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据分离**
   - 租赁记录和统计数据分离
   - 设备、客户信息独立存储
   - 历史数据归档策略

### 前端性能优化

1. **组件优化**
   - 使用Vue 3 Composition API
   - 合理使用computed和watch
   - 避免不必要的重新渲染
   - 图表组件懒加载

2. **数据加载优化**
   - 分页加载减少数据量
   - 图表数据按需加载
   - 搜索防抖处理
   - 缓存常用数据（设备、客户列表）

3. **用户体验优化**
   - 加载状态提示
   - 操作反馈提示
   - 错误处理机制
   - 响应式设计

## 安全设计

### 数据验证

1. **前端验证**
   - 表单字段格式验证
   - 数值范围验证
   - 必填字段验证
   - XSS防护处理

2. **后端验证**
   - 参数完整性验证
   - 数据类型验证
   - 业务规则验证
   - SQL注入防护

### 权限控制

1. **操作权限**
   - 租赁记录创建权限
   - 租赁记录编辑权限
   - 设备管理权限

2. **数据权限**
   - 按地区过滤
   - 按设备类型过滤
   - 敏感数据脱敏

## 错误处理设计

### 异常分类

```typescript
enum RentalErrorCode {
  RENTAL_RECORD_NOT_FOUND = 'RENTAL_RECORD_NOT_FOUND',
  INVALID_RENTAL_ORDER_NUMBER = 'INVALID_RENTAL_ORDER_NUMBER',
  DEVICE_NOT_AVAILABLE = 'DEVICE_NOT_AVAILABLE',
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  INVALID_RENTAL_PERIOD = 'INVALID_RENTAL_PERIOD',
  PAYMENT_FAILED = 'PAYMENT_FAILED',
  PERMISSION_DENIED = 'PERMISSION_DENIED'
}
```

### 错误处理策略

1. **前端错误处理**
   - 表单验证错误提示
   - 网络请求错误处理
   - 用户友好的错误信息
   - 错误状态恢复机制

2. **后端错误处理**
   - 全局异常处理器
   - 业务异常分类
   - 错误日志记录
   - 统一错误响应格式

## 测试策略

### 真实数据测试原则

所有测试必须遵循真实数据原则，严禁使用模拟数据进行测试：

```typescript
// 测试数据创建规范
class TestDataFactory {
  // 创建真实的测试租赁数据
  static createRealTestRentalRecord(): RentalRecord {
    return {
      rentalOrderNumber: `TEST${Date.now()}`,
      deviceId: "YX-0001",
      customerId: 1,
      rentalStartDate: new Date().toISOString().split('T')[0],
      rentalPeriod: 30,
      dailyRentalFee: 100.00,
      totalRentalFee: 3000.00,
      rentalStatus: "pending"
    };
  }
  
  // 严禁创建模拟数据
  static createMockRentalRecord(): never {
    throw new Error("禁止创建模拟数据 - 请使用createRealTestRentalRecord()");
  }
}
```

### 单元测试

1. **前端测试**
   - 组件渲染测试（包括空状态测试）
   - 用户交互测试
   - API调用测试
   - 图表组件测试
   - 空数据状态处理测试

2. **后端测试**
   - Service层业务逻辑测试
   - Controller层接口测试
   - Mapper层数据访问测试
   - 统计计算逻辑测试
   - 数据初始化防护测试

3. **真实数据验证测试**
   - 模拟数据检测测试
   - 空状态处理测试
   - 数据来源验证测试

### 集成测试

1. **API集成测试**
   - 完整的CRUD流程测试
   - 统计分析功能测试
   - 批量操作测试
   - 空数据API响应测试

2. **前后端集成测试**
   - 端到端功能测试
   - 数据一致性测试
   - 图表数据准确性测试
   - 性能压力测试
   - 空状态用户体验测试

3. **数据真实性集成测试**
   - 系统启动后数据状态验证
   - 用户录入数据流程测试
   - 模拟数据防护机制测试

## 部署和监控

### 部署配置

1. **数据库配置**
   - 连接池设置
   - 索引优化
   - 备份策略
   - 性能监控

2. **应用配置**
   - 缓存配置
   - 日志配置
   - 安全配置
   - 性能调优

### 监控指标

1. **性能监控**
   - 页面加载时间
   - API响应时间
   - 数据库查询性能
   - 图表渲染性能

2. **业务监控**
   - 租赁数据录入量统计
   - 用户访问统计
   - 功能使用统计
   - 错误率监控

3. **数据质量监控**
   - 数据完整性检查
   - 数据一致性验证
   - 异常数据预警
   - 模拟数据检测