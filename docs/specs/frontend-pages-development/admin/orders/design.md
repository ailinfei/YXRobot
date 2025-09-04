# Admin Business - 订单管理模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 基于现有前端页面的后端API设计原则

**本设计文档基于现有的 Orders.vue 和 OrderManagement.vue 前端页面，要求后端API设计完全适配前端页面的功能需求和数据结构：**

#### 🔥 前端页面架构分析

现有订单管理页面采用以下技术架构：
1. **Vue 3 Composition API**：使用`<script setup>`语法和响应式数据绑定
2. **Element Plus UI组件**：使用el-table、el-card、el-dialog、el-dropdown等组件
3. **TypeScript类型定义**：严格的类型检查和接口定义
4. **模块化API调用**：通过orderApi模块调用后端接口
5. **响应式设计**：支持多设备访问的自适应布局

#### 🔥 后端API设计要求

1. **完全适配前端**：
   - ✅ **必须提供**：与前端页面功能完全匹配的API接口
   - ✅ **必须支持**：前端页面的所有数据查询、筛选、分页、操作需求
   - ❌ **严禁要求**：前端页面修改数据结构或字段名称
   - ❌ **严禁返回**：与前端TypeScript接口不匹配的数据格式

2. **响应式数据支持**：
   - 支持前端页面的响应式数据绑定需求
   - 提供实时数据查询和统计计算
   - 支持复杂筛选条件和数据刷新
   - 确保API响应格式与前端期望完全一致

#### 🚨 前端页面功能模块分析（后端必须支持）

**页面头部功能**：
- 页面标题和描述显示
- 新建订单按钮功能

**订单统计卡片**：
- 订单总数统计
- 总收入统计（已完成订单）
- 处理中订单统计
- 已完成订单统计

**搜索筛选功能**：
- 关键词搜索（订单号、客户名称）
- 订单类型筛选（销售、租赁）
- 订单状态筛选
- 日期范围筛选

**订单列表表格**：
- 分页显示订单数据
- 多选功能支持
- 订单信息完整显示
- 操作按钮和下拉菜单

**订单操作功能**：
- 查看详情对话框
- 编辑订单功能
- 状态变更操作
- 删除订单功能
- 批量操作支持

## 概述

订单管理模块是YXRobot管理后台的核心业务管理功能，基于现有的Orders.vue和OrderManagement.vue前端页面提供完整的后端API支持。该模块需要开发与前端页面完全匹配的后端接口，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/orders。

**真实数据原则：**
- 前端组件不得包含任何硬编码的模拟数据
- 所有前端显示的数据必须通过API从数据库获取
- 数据库中可以包含真实的业务数据（通过管理后台录入或初始化脚本插入）
- 空数据状态必须正确处理，不得在前端组件中硬编码模拟数据填充

**🚨 重要说明：现有表结构约束**
**customers和products表是之前模块使用的现有表，只能新增字段，不能修改现有字段。订单管理模块必须基于现有表结构进行开发。**

**🚨 重要说明：缓存功能**
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

## 架构设计

### 系统架构（基于现有前端页面）

```
前端层 (已存在 - Orders.vue & OrderManagement.vue)
├── 页面头部 (标题 + 新建订单按钮)
├── 订单统计卡片 (4个统计指标卡片)
├── 搜索筛选区域 (多条件筛选组件)
├── 订单列表表格 (DataTable组件 + 分页)
├── 订单详情对话框 (CommonDialog组件)
├── 订单编辑对话框 (OrderFormDialog组件)
├── 批量操作区域 (批量操作按钮组)
└── 响应式数据绑定 (Vue 3 Composition API)

API层 (需要开发 - 适配前端)
├── GET /api/admin/orders (订单列表查询)
├── GET /api/admin/orders/{id} (订单详情查询)
├── POST /api/admin/orders (创建订单)
├── PUT /api/admin/orders/{id} (更新订单)
├── DELETE /api/admin/orders/{id} (删除订单)
├── PATCH /api/admin/orders/{id}/status (更新订单状态)
├── PATCH /api/admin/orders/batch/status (批量更新状态)
├── DELETE /api/admin/orders/batch (批量删除)
├── GET /api/admin/orders/stats (订单统计数据)
└── 统一响应格式 {code, data, message}

业务层 (需要开发)
├── OrderService (订单管理服务)
├── OrderStatsService (订单统计服务)
├── OrderStatusService (订单状态管理服务)
├── OrderValidationService (订单数据验证服务)
└── 复杂关联查询支持

数据层 (需要开发)
├── orders (订单主表)
├── order_items (订单商品表)
├── customers (客户信息表)
├── products (产品信息表)
├── order_logs (订单操作日志表)
└── 优化的索引和关联查询
```

### 核心组件

#### 1. 前端组件结构（已存在 - Orders.vue & OrderManagement.vue）
- **页面头部区域**: 
  - 标题和描述显示
  - 新建订单按钮
  - 响应式布局设计
- **订单统计卡片区域**: 
  - 4个统计卡片（订单总数、总收入、处理中、已完成）
  - 图标和数值显示
  - 响应式网格布局
- **搜索筛选区域**: 
  - 关键词搜索输入框
  - 订单类型下拉选择
  - 订单状态下拉选择
  - 日期范围选择器
  - 搜索和重置按钮
- **订单列表表格区域**: 
  - DataTable组件
  - 多选功能
  - 排序功能
  - 操作按钮和下拉菜单
- **订单详情对话框**: 
  - CommonDialog组件
  - 分区域显示详细信息
  - 状态标签和格式化显示
- **批量操作区域**: 
  - 选择信息显示
  - 批量操作按钮组
  - 取消选择功能

#### 2. 后端服务组件（需要开发）
- **OrderController**: 
  - 适配前端API调用需求
  - 统一响应格式处理
  - 参数验证和错误处理
- **OrderService**: 
  - 订单查询和管理
  - 支持复杂筛选和分页
  - 关联查询优化
- **OrderStatsService**: 
  - 实时统计数据计算
  - 按类型和状态聚合统计
  - 性能优化的统计查询
- **OrderStatusService**: 
  - 订单状态流转管理
  - 状态变更验证
  - 操作日志记录
- **OrderValidationService**: 
  - 订单数据验证
  - 业务规则检查
  - 数据完整性验证

#### 3. 数据适配组件（需要开发）
- **ResponseFormatter**: 统一API响应格式
- **FieldMapper**: 数据库字段到前端字段的映射
- **OrderDTOConverter**: 订单数据传输对象转换
- **PaginationHandler**: 分页数据处理

## 数据模型设计

### 🚨 关联表设计原则（强制遵循）

**本项目严格遵循关联表设计原则，禁止使用外键约束：**

#### 核心设计原则
- **禁止使用外键约束**: 所有表与表之间的关联必须通过关联表实现
- **降低耦合度**: 通过关联表解耦主表之间的直接依赖关系
- **提高扩展性**: 关联表设计便于后续功能扩展和数据迁移

#### 订单管理数据表设计原则
对于订单管理模块，我们采用简化的表结构设计：
- **orders表**：存储订单基本信息，包含customer_id引用客户
- **order_items表**：存储订单商品明细，一对多关系
- **shipping_info表**：存储物流信息，一对一关系
- **order_logs表**：存储操作日志，一对多关系
- **现有customers表**：客户信息（已存在）
- **现有products表**：产品信息（已存在）

**🔥 字段映射设计标准**

所有数据模型必须严格遵循四层字段映射标准：

```
数据库层 (snake_case) → Java层 (camelCase) → MyBatis映射 → 前端层 (camelCase)
     ↓                      ↓                    ↓                ↓
  order_number        →  orderNumber      →  column/property  →  orderNumber
  total_amount        →  totalAmount      →  column/property  →  totalAmount  
  customer_id         →  customerId       →  column/property  →  customerId
```

### 订单主表 (orders)

```sql
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `type` ENUM('sales', 'rental') NOT NULL COMMENT '订单类型：销售、租赁',
  `status` ENUM('pending', 'confirmed', 'processing', 'shipped', 'delivered', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID（引用customers表）',
  `delivery_address` TEXT NOT NULL COMMENT '配送地址',
  `subtotal` DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '小计金额',
  `shipping_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '运费',
  `discount` DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
  `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型',
  `payment_status` ENUM('pending', 'paid', 'failed', 'refunded') DEFAULT 'pending' COMMENT '支付状态',
  `payment_method` VARCHAR(50) COMMENT '支付方式',
  `payment_time` DATETIME COMMENT '支付时间',
  `expected_delivery_date` DATE COMMENT '预期交付日期',
  `sales_person` VARCHAR(100) COMMENT '销售人员',
  `notes` TEXT COMMENT '订单备注',
  `rental_start_date` DATE COMMENT '租赁开始日期（仅租赁订单）',
  `rental_end_date` DATE COMMENT '租赁结束日期（仅租赁订单）',
  `rental_days` INT COMMENT '租赁天数（仅租赁订单）',
  `rental_notes` TEXT COMMENT '租赁备注（仅租赁订单）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(100) COMMENT '创建人',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';
```

### 订单商品表 (order_items)

```sql
CREATE TABLE `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID（引用products表）',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称（冗余字段，避免关联查询）',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(12,2) NOT NULL COMMENT '小计金额',
  `notes` TEXT COMMENT '商品备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';
```

### 客户信息表 (customers) - 已存在

```sql
-- 现有customers表结构（已存在，只能新增字段，不能修改现有字段）
-- 主要字段包括：
-- id, customer_name, customer_type, customer_level, customer_status
-- contact_person, phone, email, address, region, industry
-- credit_level, is_active, created_at, updated_at, is_deleted
-- 
-- 订单管理模块字段映射：
-- customer_name -> customerName (客户名称)
-- phone -> customerPhone (客户电话)
-- email -> customerEmail (客户邮箱)
-- address -> deliveryAddress (配送地址)
```

### 产品信息表 (products) - 已存在

```sql
-- 现有products表结构（已存在，只能新增字段，不能修改现有字段）
-- 主要字段包括：
-- id, name, model, description, price, cover_image_url
-- status, created_at, updated_at, is_deleted
-- 
-- 订单管理模块字段映射：
-- name -> productName (产品名称)
-- price -> unitPrice (单价)
-- model -> productModel (产品型号)
-- description -> productDescription (产品描述)
```

### 物流信息表 (shipping_info)

```sql
CREATE TABLE `shipping_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '物流信息ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `company` VARCHAR(100) COMMENT '物流公司',
  `tracking_number` VARCHAR(100) COMMENT '运单号',
  `shipped_at` DATETIME COMMENT '发货时间',
  `delivered_at` DATETIME COMMENT '送达时间',
  `notes` TEXT COMMENT '物流备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  INDEX `idx_tracking_number` (`tracking_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流信息表';
```

### 订单操作日志表 (order_logs)

```sql
CREATE TABLE `order_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `action` VARCHAR(100) NOT NULL COMMENT '操作动作',
  `operator` VARCHAR(100) NOT NULL COMMENT '操作人',
  `notes` TEXT COMMENT '操作备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单操作日志表';
```

## 接口设计

### REST API 接口（基于前端页面需求）

#### 1. 订单管理接口

```typescript
// 获取订单列表 - 前端调用：orderApi.getOrders(queryParams)
GET /api/admin/orders
参数: {
  page?: number,        // 页码，默认1
  pageSize?: number,    // 每页大小，默认10
  keyword?: string,     // 搜索关键词（订单号或客户名称）
  type?: 'sales' | 'rental',  // 订单类型筛选
  status?: string,      // 订单状态筛选
  dateRange?: [string, string]  // 日期范围筛选
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: Order[],      // 订单数组，包含关联数据
    total: number,      // 总记录数
    stats: OrderStats   // 统计数据
  }
}

// Order数据结构（必须与前端TypeScript接口匹配）
interface Order {
  id: string;
  orderNumber: string;          // 订单号
  type: 'sales' | 'rental';     // 订单类型
  status: string;               // 订单状态
  customerName: string;         // 客户名称
  customerPhone: string;        // 客户电话
  customerEmail?: string;       // 客户邮箱
  deliveryAddress: string;      // 配送地址
  items: OrderItem[];           // 订单商品列表
  subtotal?: number;            // 小计
  shippingFee?: number;         // 运费
  discount?: number;            // 折扣
  totalAmount: number;          // 订单总金额
  currency?: string;            // 货币类型
  paymentStatus?: string;       // 支付状态
  paymentMethod?: string;       // 支付方式
  paymentTime?: string;         // 支付时间
  expectedDeliveryDate?: string; // 预期交付日期
  salesPerson?: string;         // 销售人员
  notes?: string;               // 订单备注
  rentalStartDate?: string;     // 租赁开始日期
  rentalEndDate?: string;       // 租赁结束日期
  rentalDays?: number;          // 租赁天数
  rentalNotes?: string;         // 租赁备注
  shippingInfo?: ShippingInfo;  // 物流信息
  createdAt: string;            // 创建时间
  updatedAt?: string;           // 更新时间
  logs?: OrderLog[];            // 操作日志
}

// 获取订单详情 - 前端调用：orderApi.getOrder(id)
GET /api/admin/orders/{id}
响应: {
  code: 200,
  message: "查询成功",
  data: Order  // 完整的订单详情，包含所有关联数据
}

// 创建订单 - 前端调用：orderApi.createOrder(orderData)
POST /api/admin/orders
请求体: CreateOrderData
响应: {
  code: 200,
  message: "创建成功",
  data: Order  // 创建的订单信息
}

// 更新订单 - 前端调用：orderApi.updateOrder(id, orderData)
PUT /api/admin/orders/{id}
请求体: UpdateOrderData
响应: {
  code: 200,
  message: "更新成功",
  data: Order  // 更新后的订单信息
}

// 删除订单 - 前端调用：orderApi.deleteOrder(id)
DELETE /api/admin/orders/{id}
响应: {
  code: 200,
  message: "删除成功"
}
```

#### 2. 订单状态管理接口

```typescript
// 更新订单状态 - 前端调用：orderApi.updateOrderStatus(id, status)
PATCH /api/admin/orders/{id}/status
请求体: {
  status: 'pending' | 'confirmed' | 'processing' | 'shipped' | 'delivered' | 'completed' | 'cancelled'
}
响应: {
  code: 200,
  message: "状态更新成功",
  data: Order  // 更新后的订单信息
}

// 批量更新订单状态 - 前端调用：orderApi.batchUpdateOrderStatus(orderIds, status)
PATCH /api/admin/orders/batch/status
请求体: {
  orderIds: string[],
  status: string
}
响应: {
  code: 200,
  message: "批量更新成功",
  data: {
    successCount: number,  // 成功更新数量
    failedCount: number,   // 失败数量
    failedOrders: string[] // 失败的订单ID列表
  }
}

// 批量删除订单 - 前端调用：orderApi.batchDeleteOrders(orderIds)
DELETE /api/admin/orders/batch
请求体: {
  orderIds: string[]
}
响应: {
  code: 200,
  message: "批量删除成功",
  data: {
    successCount: number,  // 成功删除数量
    failedCount: number,   // 失败数量
    failedOrders: string[] // 失败的订单ID列表
  }
}
```

#### 3. 订单统计接口

```typescript
// 获取订单统计数据 - 前端调用：orderApi.getOrderStats()
GET /api/admin/orders/stats
参数: {
  dateRange?: [string, string]  // 可选的日期范围筛选
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    total: number,              // 订单总数
    pending: number,            // 待确认订单数
    processing: number,         // 处理中订单数（已确认+处理中+已发货）
    completed: number,          // 已完成订单数
    cancelled: number,          // 已取消订单数
    totalRevenue: number,       // 总收入（已完成订单）
    averageOrderValue: number,  // 平均订单价值
    salesOrders: number,        // 销售订单数
    rentalOrders: number        // 租赁订单数
  }
}

// OrderStats数据结构（必须与前端TypeScript接口匹配）
interface OrderStats {
  total: number;
  pending: number;
  processing: number;
  completed: number;
  cancelled: number;
  totalRevenue: number;
  averageOrderValue: number;
  salesOrders: number;
  rentalOrders: number;
}
```

#### 4. 辅助功能接口

```typescript
// 获取客户列表
GET /api/admin/customers
参数: {
  keyword?: string,
  type?: string,
  active?: boolean
}
响应: {
  code: 200,
  data: Customer[]
}

// 获取产品列表
GET /api/admin/products
参数: {
  keyword?: string,
  category?: string,
  active?: boolean
}
响应: {
  code: 200,
  data: Product[]
}

// 导出订单数据
GET /api/admin/orders/export
参数: {
  keyword?: string,
  type?: string,
  status?: string,
  dateRange?: [string, string]
}
响应: 文件下载（Excel格式）

// 获取订单物流信息
GET /api/admin/orders/{id}/tracking
响应: {
  code: 200,
  data: ShippingInfo
}

// 更新订单物流信息
PUT /api/admin/orders/{id}/tracking
请求体: {
  company: string,
  trackingNumber: string
}
响应: {
  code: 200,
  message: "物流信息更新成功"
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

const orderEmptyStateConfig: EmptyStateConfig = {
  isEmpty: (orderList) => !orderList || orderList.length === 0,
  emptyStateProps: {
    title: "暂无订单数据",
    description: "还没有创建任何订单，点击下方按钮创建第一个订单",
    actionText: "创建第一个订单",
    actionHandler: () => openCreateOrderDialog()
  }
};
```

### 前端空状态处理

```vue
<template>
  <div class="order-management">
    <!-- 正常数据显示 -->
    <div v-if="!isEmpty" class="order-content">
      <!-- 统计面板 -->
      <div class="stats-panel">
        <div class="stat-card" v-for="stat in statsData" :key="stat.key">
          <div class="stat-icon">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
      
      <!-- 搜索筛选区域 -->
      <div class="search-section">
        <el-form :inline="true" class="search-form">
          <el-form-item label="搜索">
            <el-input v-model="searchKeyword" placeholder="搜索订单号、客户名称" />
          </el-form-item>
          <!-- 其他筛选条件 -->
        </el-form>
      </div>
      
      <!-- 订单列表 -->
      <order-list :data="orderList" />
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
import { getOrders, getOrderStats } from '@/api/order';

const orderList = ref([]);
const statsData = ref([]);
const total = ref(0);
const loading = ref(false);

const isEmpty = computed(() => {
  return !orderList.value || orderList.value.length === 0;
});

// 加载订单数据
const loadOrderData = async () => {
  loading.value = true;
  try {
    const [ordersRes, statsRes] = await Promise.all([
      getOrders(),
      getOrderStats()
    ]);
    
    // 绑定真实数据，可能为空
    orderList.value = ordersRes.data.list;
    total.value = ordersRes.data.total;
    
    // 只有在有数据时才加载统计
    if (!isEmpty.value) {
      statsData.value = formatStatsData(statsRes.data);
    }
  } catch (error) {
    console.error('加载订单数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 严禁在空状态时显示模拟数据
const showMockData = false; // 永远为false

onMounted(() => {
  loadOrderData();
});
</script>
```

### 后端空数据响应

```java
@RestController
@RequestMapping("/api/admin/orders")
public class OrderController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String status) {
        
        // 查询真实数据
        List<OrderDTO> orderList = orderService.getOrders(page, pageSize, keyword, type, status);
        OrderStats stats = orderService.getOrderStats();
        
        // 即使数据为空，也返回真实的空结果，不插入模拟数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", Map.of(
            "list", orderList, // 可能为空数组
            "total", orderService.getTotalCount(),
            "stats", stats,    // 统计数据可能全为0
            "isEmpty", orderList.isEmpty() // 明确标识是否为空
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        // 只有在有订单数据时才返回有意义的统计信息
        OrderStats stats = orderService.getOrderStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", stats); // 可能包含全零的统计数据
        
        return ResponseEntity.ok(response);
    }
}
```

## 业务逻辑设计

### 订单状态流转管理

```typescript
enum OrderStatus {
  PENDING = 'pending',        // 待确认
  CONFIRMED = 'confirmed',    // 已确认
  PROCESSING = 'processing',  // 处理中
  SHIPPED = 'shipped',        // 已发货
  DELIVERED = 'delivered',    // 已送达
  COMPLETED = 'completed',    // 已完成
  CANCELLED = 'cancelled'     // 已取消
}

enum PaymentStatus {
  PENDING = 'pending',        // 待支付
  PAID = 'paid',             // 已支付
  FAILED = 'failed',         // 支付失败
  REFUNDED = 'refunded'      // 已退款
}

// 状态转换规则
const statusTransitions = {
  pending: ['confirmed', 'cancelled'],
  confirmed: ['processing', 'cancelled'],
  processing: ['shipped'],
  shipped: ['delivered'],
  delivered: ['completed'],
  completed: [],
  cancelled: []
}

// 状态转换验证
class OrderStatusValidator {
  static canTransition(currentStatus: OrderStatus, targetStatus: OrderStatus): boolean {
    const allowedTransitions = statusTransitions[currentStatus];
    return allowedTransitions.includes(targetStatus);
  }
  
  static getAvailableTransitions(currentStatus: OrderStatus): OrderStatus[] {
    return statusTransitions[currentStatus] || [];
  }
}
```

### 订单数据验证规则

```typescript
const orderValidationRules = {
  orderNumber: {
    required: true,
    pattern: /^[A-Z0-9]{10,20}$/,
    message: "订单号格式不正确，应为10-20位字母数字组合"
  },
  type: {
    required: true,
    enum: ['sales', 'rental'],
    message: "订单类型必须为销售或租赁"
  },
  customerName: {
    required: true,
    minLength: 2,
    maxLength: 200,
    message: "客户名称长度应在2-200字符之间"
  },
  customerPhone: {
    required: true,
    pattern: /^1[3-9]\d{9}$/,
    message: "请输入正确的手机号码"
  },
  totalAmount: {
    required: true,
    min: 0.01,
    max: 999999.99,
    message: "订单金额应在0.01-999999.99之间"
  },
  deliveryAddress: {
    required: true,
    minLength: 10,
    maxLength: 500,
    message: "配送地址长度应在10-500字符之间"
  },
  items: {
    required: true,
    minItems: 1,
    message: "订单必须包含至少一个商品"
  }
}
```

### 订单统计计算逻辑

```typescript
class OrderStatsCalculator {
  // 计算订单统计数据
  static calculateStats(orders: Order[]): OrderStats {
    if (!orders || orders.length === 0) {
      return {
        total: 0,
        pending: 0,
        processing: 0,
        completed: 0,
        cancelled: 0,
        totalRevenue: 0,
        averageOrderValue: 0,
        salesOrders: 0,
        rentalOrders: 0
      };
    }
    
    const stats = {
      total: orders.length,
      pending: orders.filter(o => o.status === 'pending').length,
      processing: orders.filter(o => ['confirmed', 'processing', 'shipped'].includes(o.status)).length,
      completed: orders.filter(o => o.status === 'completed').length,
      cancelled: orders.filter(o => o.status === 'cancelled').length,
      totalRevenue: orders.filter(o => o.status === 'completed').reduce((sum, o) => sum + o.totalAmount, 0),
      averageOrderValue: 0,
      salesOrders: orders.filter(o => o.type === 'sales').length,
      rentalOrders: orders.filter(o => o.type === 'rental').length
    };
    
    stats.averageOrderValue = stats.total > 0 ? stats.totalRevenue / stats.total : 0;
    
    return stats;
  }
  
  // 计算按类型分组的统计
  static calculateStatsByType(orders: Order[]): { sales: OrderStats, rental: OrderStats } {
    const salesOrders = orders.filter(o => o.type === 'sales');
    const rentalOrders = orders.filter(o => o.type === 'rental');
    
    return {
      sales: this.calculateStats(salesOrders),
      rental: this.calculateStats(rentalOrders)
    };
  }
}
```

### 关联表查询逻辑设计

```java
// 订单关联数据查询服务
@Service
public class OrderService {
    
    // 查询订单的客户信息
    public Customer getOrderCustomer(Long orderId) {
        // 1. 查询订单获取customer_id
        Order order = orderMapper.selectById(orderId);
        if (order == null) return null;
        
        // 2. 通过customer_id查询客户详细信息
        return customerMapper.selectById(order.getCustomerId());
    }
    
    // 查询订单的商品列表
    public List<OrderItem> getOrderItems(Long orderId) {
        // 直接通过order_items表查询订单商品信息
        return orderItemMapper.selectByOrderId(orderId);
    }
    
    // 查询订单的物流信息
    public ShippingInfo getOrderShipping(Long orderId) {
        // 直接通过order_id查询物流信息
        return shippingInfoMapper.selectByOrderId(orderId);
    }
    
    // 查询订单的操作日志
    public List<OrderLog> getOrderLogs(Long orderId) {
        // 直接通过order_id查询操作日志
        return orderLogMapper.selectByOrderId(orderId);
        
        return orderLogMapper.selectByIds(logIds);
    }
}
```

### 订单操作日志管理

```typescript
class OrderLogManager {
  // 记录订单操作日志（通过关联表）
  static async logOrderAction(
    orderId: string, 
    action: string, 
    operator: string, 
    notes?: string
  ): Promise<void> {
    // 1. 创建日志记录
    const logEntry = {
      action,
      operator,
      notes: notes || '',
      createdAt: new Date().toISOString()
    };
    const log = await orderLogService.createLog(logEntry);
    
    // 2. 创建订单日志关联记录
    const relation = {
      orderId: orderId,
      logId: log.id,
      status: 1
    };
    await orderLogRelationService.createRelation(relation);
  }
  
  // 获取订单操作历史（通过关联表）
  static async getOrderLogs(orderId: string): Promise<OrderLog[]> {
    return await orderRelationService.getOrderLogs(orderId);
  }
  
  // 预定义的操作类型
  static readonly ACTION_TYPES = {
    CREATE: '创建订单',
    CONFIRM: '确认订单',
    PROCESS: '开始处理',
    SHIP: '发货',
    DELIVER: '送达',
    COMPLETE: '完成',
    CANCEL: '取消',
    UPDATE: '更新信息',
    DELETE: '删除订单'
  };
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 唯一索引：`order_number`
   - 复合索引：`(customer_id, created_at)`、`(type, status, created_at)`
   - 筛选索引：`type`、`status`、`payment_status`
   - 时间索引：`created_at`、`updated_at`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据分离**
   - 订单主表和商品明细分离
   - 客户、产品信息独立存储
   - 物流信息单独表存储
   - 操作日志独立存储

### 前端性能优化

1. **组件优化**
   - 使用Vue 3 Composition API
   - 合理使用computed和watch
   - 避免不必要的重新渲染
   - 表格组件虚拟滚动

2. **数据加载优化**
   - 分页加载减少数据量
   - 搜索防抖处理
   - 优化数据库查询减少响应时间
   - 懒加载订单详情

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
   - 订单创建权限
   - 订单编辑权限
   - 订单删除权限
   - 状态变更权限

2. **数据权限**
   - 按销售人员过滤
   - 按部门过滤
   - 敏感数据脱敏

## 错误处理设计

### 异常分类

```typescript
enum OrderErrorCode {
  ORDER_NOT_FOUND = 'ORDER_NOT_FOUND',
  INVALID_ORDER_NUMBER = 'INVALID_ORDER_NUMBER',
  INVALID_STATUS_TRANSITION = 'INVALID_STATUS_TRANSITION',
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  PRODUCT_NOT_FOUND = 'PRODUCT_NOT_FOUND',
  INSUFFICIENT_STOCK = 'INSUFFICIENT_STOCK',
  INVALID_ORDER_AMOUNT = 'INVALID_ORDER_AMOUNT',
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
  // 创建真实的测试订单数据
  static createRealTestOrder(): Order {
    return {
      id: `test_${Date.now()}`,
      orderNumber: `TEST${Date.now()}`,
      type: "sales",
      status: "pending",
      customerName: "真实测试客户",
      customerPhone: "13800138000",
      deliveryAddress: "北京市朝阳区测试地址123号",
      items: [
        {
          productId: "test_product_1",
          productName: "真实测试产品",
          quantity: 1,
          unitPrice: 1000.00,
          totalPrice: 1000.00
        }
      ],
      totalAmount: 1000.00,
      createdAt: new Date().toISOString()
    };
  }
  
  // 严禁创建模拟数据
  static createMockOrder(): never {
    throw new Error("禁止创建模拟数据 - 请使用createRealTestOrder()");
  }
}
```

### 单元测试

1. **前端测试**
   - 组件渲染测试（包括空状态测试）
   - 用户交互测试
   - API调用测试
   - 状态管理测试
   - 空数据状态处理测试

2. **后端测试**
   - Service层业务逻辑测试
   - Controller层接口测试
   - Mapper层数据访问测试
   - 状态流转逻辑测试
   - 数据验证测试

### 集成测试

1. **API集成测试**
   - 完整的CRUD流程测试
   - 状态管理功能测试
   - 批量操作测试
   - 空数据API响应测试

2. **前后端集成测试**
   - 端到端功能测试
   - 数据一致性测试
   - 性能压力测试
   - 空状态用户体验测试

## 部署和监控

### 部署配置

1. **数据库配置**
   - 连接池设置
   - 索引优化
   - 备份策略
   - 性能监控

2. **应用配置**
   - 数据库连接池配置
   - 日志配置
   - 安全配置
   - 性能调优

### 监控指标

1. **性能监控**
   - 页面加载时间
   - API响应时间
   - 数据库查询性能
   - 用户操作响应时间

2. **业务监控**
   - 订单创建量统计
   - 用户访问统计
   - 功能使用统计
   - 错误率监控

3. **数据质量监控**
   - 数据完整性检查
   - 数据一致性验证
   - 异常数据预警
   - 模拟数据检测