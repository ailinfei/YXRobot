# Admin Business - 客户管理模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 基于现有前端页面的后端API设计原则

**本设计文档基于现有的 Customers.vue 前端页面，要求后端API设计完全适配前端页面的功能需求和数据结构：**

#### 🔥 前端页面架构分析

现有Customers.vue页面采用以下技术架构：
1. **Vue 3 Composition API**：使用`<script setup>`语法和响应式数据绑定
2. **Element Plus UI组件**：使用el-table、el-card、el-dialog、el-button等组件
3. **EnhancedDataTable组件**：自定义增强表格组件，支持筛选、分页、操作等功能
4. **TypeScript类型定义**：严格的类型检查和接口定义
5. **模块化API调用**：通过customerApi模块调用后端接口

#### 🔥 后端API设计要求

1. **完全适配前端**：
   - ✅ **必须提供**：与前端页面功能完全匹配的API接口
   - ✅ **必须支持**：前端页面的所有数据查询、筛选、分页、CRUD需求
   - ❌ **严禁要求**：前端页面修改数据结构或字段名称
   - ❌ **严禁返回**：与前端TypeScript接口不匹配的数据格式

2. **响应式数据支持**：
   - 支持前端页面的响应式数据绑定需求
   - 提供实时数据查询和统计计算
   - 支持多条件筛选和数据刷新
   - 确保API响应格式与前端期望完全一致

#### 🚨 前端页面功能模块分析（后端必须支持）

**页面头部功能**：
- 页面标题和描述显示
- 新增客户按钮操作
- 响应式布局设计

**客户统计卡片**：
- 总客户数统计
- 客户等级分布（普通、VIP、高级）
- 活跃设备统计
- 总收入统计

**客户列表表格**：
- 完整的客户信息展示（头像、姓名、等级、电话等）
- 多列排序功能
- 多条件筛选（等级、设备类型、地区）
- 分页显示（默认20条/页）
- 行操作（查看、编辑、拨打、短信、删除）

**对话框功能**：
- 客户详情查看对话框
- 客户编辑/创建对话框
- 各种确认对话框

**~~批量操作功能~~**：（已移除）
- 多选客户（前端保留，但不连接后端）
- 批量操作执行（暂不实现）

**~~数据导出功能~~**：（已移除）
- 客户数据导出（暂不实现）

## 概述

客户管理模块是YXRobot管理后台的核心业务管理功能，基于现有的Customers.vue前端页面提供完整的后端API支持。该模块需要开发与前端页面完全匹配的后端接口，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/customers。

**真实数据原则：**
- 前端组件不得包含任何硬编码的模拟数据
- 所有前端显示的数据必须通过API从数据库获取
- 数据库中可以包含真实的业务数据（通过管理后台录入或初始化脚本插入）
- 空数据状态必须正确处理，不得在前端组件中硬编码模拟数据填充

## 架构设计

### 系统架构（基于现有前端页面）

```
前端层 (已存在 - Customers.vue)
├── 页面头部 (标题、描述、新增按钮)
├── 客户统计卡片 (6个统计卡片)
├── 客户列表表格 (EnhancedDataTable组件)
├── 筛选和搜索功能 (多条件筛选)
├── 分页控制 (分页组件)
├── 客户详情对话框 (CustomerDetailDialog组件)
├── 客户编辑对话框 (CustomerEditDialog组件)
├── 批量操作功能 (多选和批量处理)
├── 数据导出功能 (Excel导出)
└── 响应式数据绑定 (Vue 3 Composition API)

API层 (需要开发 - 适配前端)
├── GET /api/admin/customers (客户列表查询)
├── GET /api/admin/customers/{id} (客户详情查询)
├── POST /api/admin/customers (创建客户)
├── PUT /api/admin/customers/{id} (更新客户)
├── DELETE /api/admin/customers/{id} (删除客户)
├── GET /api/admin/customers/stats (客户统计数据)
├── GET /api/admin/customers/{id}/devices (客户设备列表)
├── GET /api/admin/customers/{id}/orders (客户订单列表)
├── GET /api/admin/customers/{id}/service-records (客户服务记录)
├── PUT /api/admin/customers/batch (批量操作)
├── GET /api/admin/customers/export (数据导出)
└── 统一响应格式 {code, data, message}

业务层 (需要开发)
├── CustomerService (客户信息管理)
├── CustomerStatsService (客户统计计算)
├── CustomerDeviceService (客户设备管理)
├── CustomerOrderService (客户订单管理)
├── CustomerServiceRecordService (客户服务记录管理)
└── 复杂关联查询支持

数据层 (需要创建)
├── customers (客户基本信息表)
├── customer_devices (客户设备关联表)
├── customer_orders (客户订单表)
├── customer_service_records (客户服务记录表)
├── customer_stats (客户统计表)
└── 优化的索引和关联查询
```

### 核心组件

#### 1. 前端组件结构（已存在 - Customers.vue）
- **页面头部区域**: 
  - 标题和描述显示
  - 新增客户按钮
  - 响应式布局设计
- **客户统计卡片区域**: 
  - 6个统计卡片（总数、等级分布、设备、收入）
  - 动态数据绑定
  - 悬停动画效果
  - 响应式网格布局
- **客户列表表格区域**: 
  - EnhancedDataTable组件
  - 多列显示和排序
  - 筛选和搜索功能
  - 分页控制
  - 行操作按钮
- **对话框组件**: 
  - CustomerDetailDialog组件
  - CustomerEditDialog组件
  - 各种确认对话框
- **批量操作功能**: 
  - 多选支持
  - 批量操作按钮
- **数据导出功能**: 
  - 导出按钮和进度显示

#### 2. 后端服务组件（需要开发）
- **CustomerController**: 
  - 适配前端API调用需求
  - 统一响应格式处理
  - 参数验证和错误处理
- **CustomerService**: 
  - 客户信息查询和管理
  - 支持复杂筛选和分页
  - 关联查询优化
- **CustomerStatsService**: 
  - 实时统计数据计算
  - 客户等级分布统计
  - 设备和收入统计
- **CustomerDeviceService**: 
  - 客户设备关联管理
  - 设备状态统计
- **CustomerOrderService**: 
  - 客户订单历史管理
  - 订单统计计算
- **CustomerServiceRecordService**: 
  - 客户服务记录管理
  - 服务历史查询

#### 3. 数据适配组件（需要开发）
- **ResponseFormatter**: 统一API响应格式
- **FieldMapper**: 数据库字段到前端字段的映射
- **PaginationHandler**: 分页数据处理
- **FilterHandler**: 筛选条件处理
- **RelationQueryHandler**: 关联表查询处理（遵循项目规范）

#### 4. 关联查询优化策略（遵循项目数据库设计规范）

```java
// 示例：通过关联表查询客户的所有设备
@Mapper
public interface CustomerDeviceRelationMapper {
    // 查询客户的所有设备（通过关联表）
    List<Device> selectDevicesByCustomerId(@Param("customerId") Long customerId);
    
    // 查询设备的所有客户（通过关联表）
    List<Customer> selectCustomersByDeviceId(@Param("deviceId") Long deviceId);
    
    // 复杂关联查询：客户设备统计
    CustomerDeviceStats selectCustomerDeviceStats(@Param("customerId") Long customerId);
}
```

```xml
<!-- 通过关联表查询客户设备 -->
<select id="selectDevicesByCustomerId" resultType="Device">
    SELECT d.* 
    FROM devices d
    INNER JOIN customer_device_relation cdr ON d.id = cdr.device_id
    WHERE cdr.customer_id = #{customerId} 
    AND cdr.status = 1 
    AND d.is_deleted = 0
</select>

<!-- 客户设备统计查询 -->
<select id="selectCustomerDeviceStats" resultType="CustomerDeviceStats">
    SELECT 
        COUNT(*) as totalDevices,
        COUNT(CASE WHEN cdr.relation_type = 'purchased' THEN 1 END) as purchasedDevices,
        COUNT(CASE WHEN cdr.relation_type = 'rental' THEN 1 END) as rentalDevices
    FROM customer_device_relation cdr
    INNER JOIN devices d ON cdr.device_id = d.id
    WHERE cdr.customer_id = #{customerId} 
    AND cdr.status = 1 
    AND d.is_deleted = 0
</select>
```

## 数据模型设计

**🔥 字段映射设计标准（基于现有表扩展）**

所有数据模型必须严格遵循四层字段映射标准：

```
数据库层 (snake_case) → Java层 (camelCase) → MyBatis映射 → 前端层 (camelCase)
     ↓                      ↓                    ↓                ↓
  customer_name       →  customerName      →  column/property  →  name
  phone               →  phone             →  column/property  →  phone  
  customer_level      →  customerLevel     →  column/property  →  level
  customer_status     →  customerStatus    →  column/property  →  status
  avatar_url          →  avatarUrl         →  column/property  →  avatar
  total_spent         →  totalSpent        →  column/property  →  totalSpent
```

**现有字段复用映射：**
- `customer_name` (现有) → `name` (前端)
- `phone` (现有) → `phone` (前端)  
- `email` (现有) → `email` (前端)
- `contact_person` (现有) → `company` (前端) 或保持独立
- `address` (现有) → `address.detail` (前端)
- `region` (现有) → `address.province` (前端)

**新增字段映射：**
- `customer_level` (新增) → `level` (前端)
- `customer_status` (新增) → `status` (前端)
- `avatar_url` (新增) → `avatar` (前端)
- `customer_tags` (新增) → `tags` (前端)
- `total_spent` (新增) → `totalSpent` (前端)
- `customer_value` (新增) → `customerValue` (前端)

### 客户基本信息表 (customers) - 扩展现有表

**⚠️ 重要说明：复用现有的 `customers` 表，通过 ALTER TABLE 语句添加缺失字段**

现有表结构：
```sql
-- 现有字段（保持不变）：
-- id, customer_name, customer_type, contact_person, phone, email, address, region, industry, credit_level, is_active, created_at, updated_at, is_deleted

-- 需要添加的字段：
ALTER TABLE `customers` 
ADD COLUMN `customer_level` ENUM('regular', 'vip', 'premium') DEFAULT 'regular' COMMENT '客户等级' AFTER `customer_type`,
ADD COLUMN `customer_status` ENUM('active', 'inactive', 'suspended') DEFAULT 'active' COMMENT '客户状态' AFTER `customer_level`,
ADD COLUMN `avatar_url` VARCHAR(500) COMMENT '头像URL' AFTER `email`,
ADD COLUMN `customer_tags` JSON COMMENT '客户标签' AFTER `avatar_url`,
ADD COLUMN `notes` TEXT COMMENT '备注信息' AFTER `customer_tags`,
ADD COLUMN `total_spent` DECIMAL(12,2) DEFAULT 0 COMMENT '累计消费金额' AFTER `notes`,
ADD COLUMN `customer_value` DECIMAL(3,1) DEFAULT 0 COMMENT '客户价值评分（0-10）' AFTER `total_spent`,
ADD COLUMN `registered_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间' AFTER `customer_value`,
ADD COLUMN `last_active_at` DATETIME COMMENT '最后活跃时间' AFTER `registered_at`;

-- 添加新索引
ALTER TABLE `customers`
ADD INDEX `idx_customer_level` (`customer_level`),
ADD INDEX `idx_customer_status` (`customer_status`),
ADD INDEX `idx_total_spent` (`total_spent`),
ADD INDEX `idx_customer_value` (`customer_value`),
ADD INDEX `idx_registered_at` (`registered_at`),
ADD INDEX `idx_last_active_at` (`last_active_at`);
```

**字段映射说明：**
- `customer_name` → 前端 `name`
- `phone` → 前端 `phone`
- `email` → 前端 `email`
- `contact_person` → 前端 `company`（或新增company字段）
- `customer_level` → 前端 `level`
- `customer_status` → 前端 `status`
- `avatar_url` → 前端 `avatar`
- `customer_tags` → 前端 `tags`
- `total_spent` → 前端 `totalSpent`
- `customer_value` → 前端 `customerValue`
- `registered_at` → 前端 `registeredAt`
- `last_active_at` → 前端 `lastActiveAt`
```

### 客户地址信息表 (customer_addresses)

```sql
CREATE TABLE `customer_addresses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `country` VARCHAR(100) DEFAULT '中国' COMMENT '国家',
  `province` VARCHAR(100) NOT NULL COMMENT '省份',
  `city` VARCHAR(100) NOT NULL COMMENT '城市',
  `district` VARCHAR(100) COMMENT '区县',
  `detail_address` TEXT COMMENT '详细地址',
  `zip_code` VARCHAR(20) COMMENT '邮政编码',
  `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认地址',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_province` (`province`),
  INDEX `idx_city` (`city`),
  INDEX `idx_is_default` (`is_default`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户地址信息表';
```

### 设备信息表 (devices) - 独立设备主表

```sql
CREATE TABLE `devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `device_id` VARCHAR(50) NOT NULL COMMENT '设备编号',
  `serial_number` VARCHAR(100) NOT NULL COMMENT '设备序列号',
  `device_model` VARCHAR(100) NOT NULL COMMENT '设备型号',
  `device_name` VARCHAR(200) NOT NULL COMMENT '设备名称',
  `device_category` VARCHAR(100) NOT NULL COMMENT '设备类别',
  `device_status` ENUM('pending', 'active', 'offline', 'maintenance', 'retired') DEFAULT 'pending' COMMENT '设备状态',
  `activated_at` DATETIME COMMENT '激活时间',
  `last_online_at` DATETIME COMMENT '最后在线时间',
  `firmware_version` VARCHAR(50) COMMENT '固件版本',
  `health_score` INT DEFAULT 100 COMMENT '健康评分（0-100）',
  `purchase_date` DATE COMMENT '采购日期',
  `purchase_price` DECIMAL(10,2) COMMENT '采购价格',
  `location` VARCHAR(200) COMMENT '设备位置',
  `region` VARCHAR(100) COMMENT '所在地区',
  `maintenance_status` ENUM('normal', 'warning', 'urgent') DEFAULT 'normal' COMMENT '维护状态',
  `last_maintenance_date` DATE COMMENT '最后维护日期',
  `next_maintenance_date` DATE COMMENT '下次维护日期',
  `device_notes` TEXT COMMENT '设备备注',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  UNIQUE KEY `uk_serial_number` (`serial_number`),
  INDEX `idx_device_model` (`device_model`),
  INDEX `idx_device_status` (`device_status`),
  INDEX `idx_region` (`region`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';
```

### 客户设备关联表 (customer_device_relation) - 遵循关联表设计规范

```sql
CREATE TABLE `customer_device_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `relation_type` ENUM('purchased', 'rental') NOT NULL COMMENT '关联类型：购买、租赁',
  `start_date` DATE NOT NULL COMMENT '关联开始日期',
  `end_date` DATE COMMENT '关联结束日期（租赁设备）',
  `warranty_end_date` DATE COMMENT '保修结束日期（购买设备）',
  `daily_rental_fee` DECIMAL(8,2) COMMENT '日租金（租赁设备）',
  `purchase_price` DECIMAL(10,2) COMMENT '购买价格（购买设备）',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_device` (`customer_id`, `device_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户设备关联表';
```

### 订单信息表 (orders) - 独立订单主表

```sql
CREATE TABLE `orders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID，主键',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `order_type` ENUM('purchase', 'rental') NOT NULL COMMENT '订单类型',
  `order_status` ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '订单总金额',
  `currency` VARCHAR(10) DEFAULT 'CNY' COMMENT '货币类型',
  `payment_status` ENUM('unpaid', 'partial', 'paid', 'refunded') DEFAULT 'unpaid' COMMENT '支付状态',
  `payment_method` VARCHAR(50) COMMENT '支付方式',
  `order_date` DATE NOT NULL COMMENT '订单日期',
  `delivery_date` DATE COMMENT '交付日期',
  `shipping_address` VARCHAR(500) COMMENT '收货地址',
  `shipping_fee` DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `order_notes` TEXT COMMENT '订单备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  INDEX `idx_order_type` (`order_type`),
  INDEX `idx_order_status` (`order_status`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_order_date` (`order_date`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';
```

### 客户订单关联表 (customer_order_relation) - 遵循关联表设计规范

```sql
CREATE TABLE `customer_order_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `customer_role` ENUM('buyer', 'payer', 'receiver') DEFAULT 'buyer' COMMENT '客户角色',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_customer_role` (`customer_role`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_order_role` (`customer_id`, `order_id`, `customer_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户订单关联表';
```

### 订单商品明细表 (order_items)

```sql
CREATE TABLE `order_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '明细ID，主键',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `product_model` VARCHAR(100) NOT NULL COMMENT '产品型号',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_price` DECIMAL(12,2) NOT NULL COMMENT '小计金额',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品明细表';
```

### 服务记录表 (service_records) - 独立服务记录主表

```sql
CREATE TABLE `service_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务记录ID，主键',
  `service_number` VARCHAR(50) NOT NULL COMMENT '服务单号',
  `service_type` ENUM('maintenance', 'upgrade', 'consultation', 'complaint') NOT NULL COMMENT '服务类型',
  `service_title` VARCHAR(200) NOT NULL COMMENT '服务标题',
  `service_description` TEXT COMMENT '服务描述',
  `service_staff` VARCHAR(100) COMMENT '服务人员',
  `service_cost` DECIMAL(10,2) DEFAULT 0 COMMENT '服务费用',
  `service_status` ENUM('in_progress', 'completed', 'cancelled') DEFAULT 'in_progress' COMMENT '服务状态',
  `priority` ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium' COMMENT '优先级',
  `assigned_to` VARCHAR(100) COMMENT '分配给',
  `resolved_at` DATETIME COMMENT '解决时间',
  `service_date` DATE NOT NULL COMMENT '服务日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_service_number` (`service_number`),
  INDEX `idx_service_type` (`service_type`),
  INDEX `idx_service_status` (`service_status`),
  INDEX `idx_priority` (`priority`),
  INDEX `idx_service_date` (`service_date`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务记录表';
```

### 客户服务关联表 (customer_service_relation) - 遵循关联表设计规范

```sql
CREATE TABLE `customer_service_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `service_id` BIGINT NOT NULL COMMENT '服务记录ID',
  `customer_role` ENUM('requester', 'contact', 'beneficiary') DEFAULT 'requester' COMMENT '客户角色',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_customer_role` (`customer_role`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_customer_service_role` (`customer_id`, `service_id`, `customer_role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户服务关联表';
```

### 设备服务关联表 (device_service_relation) - 设备与服务的关联

```sql
CREATE TABLE `device_service_relation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID',
  `service_id` BIGINT NOT NULL COMMENT '服务记录ID',
  `relation_type` ENUM('target', 'related', 'affected') DEFAULT 'target' COMMENT '关联类型',
  `relation_notes` TEXT COMMENT '关联备注',
  `created_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_relation_type` (`relation_type`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_device_service_type` (`device_id`, `service_id`, `relation_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备服务关联表';
```

### 客户统计表 (customer_stats)

```sql
CREATE TABLE `customer_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `total_customers` INT DEFAULT 0 COMMENT '总客户数',
  `regular_customers` INT DEFAULT 0 COMMENT '普通客户数',
  `vip_customers` INT DEFAULT 0 COMMENT 'VIP客户数',
  `premium_customers` INT DEFAULT 0 COMMENT '高级客户数',
  `active_devices` INT DEFAULT 0 COMMENT '活跃设备数',
  `total_revenue` DECIMAL(15,2) DEFAULT 0 COMMENT '总收入',
  `new_customers_this_month` INT DEFAULT 0 COMMENT '本月新增客户数',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  INDEX `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户统计表';
```

## 接口设计

### REST API 接口（基于前端页面需求）

#### 1. 客户管理接口

```typescript
// 获取客户列表 - 前端调用：customerApi.getCustomers()
GET /api/admin/customers
参数: {
  page?: number,        // 页码，默认1
  pageSize?: number,    // 每页大小，默认20
  keyword?: string,     // 搜索关键词
  level?: string,       // 客户等级筛选
  deviceType?: string,  // 设备类型筛选
  region?: string,      // 地区筛选
  sortBy?: string,      // 排序字段
  sortOrder?: string    // 排序方向
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: Customer[],     // 客户列表
    total: number,        // 总记录数
    page: number,         // 当前页码
    pageSize: number      // 每页大小
  }
}

// Customer数据结构（必须与前端TypeScript接口匹配）
interface Customer {
  id: string;
  name: string;
  level: 'regular' | 'vip' | 'premium';
  phone: string;
  email: string;
  company?: string;
  status: 'active' | 'inactive' | 'suspended';
  address?: {
    province: string;
    city: string;
    detail: string;
  };
  tags?: string[];
  notes?: string;
  avatar?: string;
  deviceCount?: {
    total: number;
    purchased: number;
    rental: number;
  };
  totalSpent: number;
  customerValue: number;
  registeredAt: string;
  lastActiveAt?: string;
}
```

#### 2. 客户详情接口

```typescript
// 获取客户详情 - 前端调用：customerApi.getCustomer(id)
GET /api/admin/customers/{id}
响应: {
  code: 200,
  message: "查询成功",
  data: Customer & {
    devices?: CustomerDevice[],      // 客户设备列表
    orders?: CustomerOrder[],        // 客户订单列表
    serviceRecords?: ServiceRecord[] // 服务记录列表
  }
}

// CustomerDevice数据结构
interface CustomerDevice {
  id: string;
  serialNumber: string;
  model: string;
  type: 'purchased' | 'rental';
  status: 'pending' | 'active' | 'offline' | 'maintenance' | 'retired';
  activatedAt?: string;
  lastOnlineAt?: string;
  firmwareVersion: string;
  healthScore: number;
  usageStats?: {
    totalUsageHours: number;
    dailyAverageUsage: number;
    lastUsedAt: string;
    coursesCompleted: number;
    charactersWritten: number;
  };
  notes?: string;
}

// CustomerOrder数据结构
interface CustomerOrder {
  id: string;
  orderNumber: string;
  type: 'purchase' | 'rental';
  status: 'pending' | 'processing' | 'completed' | 'cancelled';
  totalAmount: number;
  currency: string;
  items: Array<{
    id: string;
    productId: string;
    productName: string;
    productModel: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
  }>;
  createdAt: string;
  updatedAt?: string;
}
```

#### 3. 客户CRUD接口

```typescript
// 创建客户 - 前端调用：customerApi.createCustomer(data)
POST /api/admin/customers
请求体: {
  name: string;
  level: string;
  phone: string;
  email: string;
  company?: string;
  status: string;
  address?: {
    province: string;
    city: string;
    detail: string;
  };
  tags?: string[];
  notes?: string;
}
响应: {
  code: 200,
  message: "客户创建成功",
  data: Customer
}

// 更新客户 - 前端调用：customerApi.updateCustomer(id, data)
PUT /api/admin/customers/{id}
请求体: Partial<CreateCustomerData>
响应: {
  code: 200,
  message: "客户更新成功",
  data: Customer
}

// 删除客户 - 前端调用：customerApi.deleteCustomer(id)
DELETE /api/admin/customers/{id}
响应: {
  code: 200,
  message: "客户删除成功"
}
```

#### 4. 客户统计接口

```typescript
// 获取客户统计数据 - 前端调用：customerApi.getCustomerStats()
GET /api/admin/customers/stats
响应: {
  code: 200,
  message: "查询成功",
  data: {
    total: number;           // 总客户数
    regular: number;         // 普通客户数
    vip: number;            // VIP客户数
    premium: number;        // 高级客户数
    activeDevices: number;  // 活跃设备数
    totalRevenue: number;   // 总收入
    newThisMonth: number;   // 本月新增客户数
  }
}
```

#### 5. 关联数据接口

```typescript
// 获取客户设备列表 - 前端调用：customerApi.getCustomerDevices(customerId)
GET /api/admin/customers/{id}/devices
响应: {
  code: 200,
  data: CustomerDevice[]
}

// 获取客户订单列表 - 前端调用：customerApi.getCustomerOrders(customerId)
GET /api/admin/customers/{id}/orders
响应: {
  code: 200,
  data: CustomerOrder[]
}

// 获取客户服务记录 - 前端调用：customerApi.getCustomerServiceRecords(customerId)
GET /api/admin/customers/{id}/service-records
响应: {
  code: 200,
  data: ServiceRecord[]
}
```

#### ~~6. 批量操作接口~~ - **已移除：功能不需要**

**⚠️ 接口已取消**：根据项目需求，批量操作和数据导出接口暂不实现。前端页面相关功能保留但不连接后端API。

```typescript
// 以下接口暂不实现：
// PUT /api/admin/customers/batch - 批量操作接口
// GET /api/admin/customers/export - 数据导出接口
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

const customerEmptyStateConfig: EmptyStateConfig = {
  isEmpty: (customerList) => !customerList || customerList.length === 0,
  emptyStateProps: {
    title: "暂无客户数据",
    description: "还没有录入任何客户信息，点击下方按钮创建第一个客户",
    actionText: "创建第一个客户",
    actionHandler: () => openCreateCustomerDialog()
  }
};
```

### 前端空状态处理

```vue
<template>
  <div class="customers-page">
    <!-- 正常数据显示 -->
    <div v-if="!isEmpty" class="customers-content">
      <!-- 客户统计卡片 -->
      <div class="stats-cards">
        <div v-for="stat in statsData" :key="stat.key" class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>
      </div>
      
      <!-- 客户列表表格 -->
      <EnhancedDataTable
        :data="customers"
        :columns="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
      />
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
import { customerApi } from '@/api/customer';

const customers = ref([]);
const stats = ref(null);
const loading = ref(false);

const isEmpty = computed(() => {
  return !customers.value || customers.value.length === 0;
});

// 加载客户数据
const loadCustomers = async () => {
  loading.value = true;
  try {
    const [customersRes, statsRes] = await Promise.all([
      customerApi.getCustomers(),
      customerApi.getCustomerStats()
    ]);
    
    // 绑定真实数据，可能为空
    customers.value = customersRes.data.list;
    stats.value = statsRes.data;
  } catch (error) {
    console.error('加载客户数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 严禁在空状态时显示模拟数据
const showMockData = false; // 永远为false

onMounted(() => {
  loadCustomers();
});
</script>
```

### 后端空数据响应

```java
@RestController
@RequestMapping("/api/admin/customers")
public class CustomerController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCustomers(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String level) {
        
        // 查询真实数据
        List<CustomerDTO> customerList = customerService.getCustomers(page, pageSize, keyword, level);
        
        // 即使数据为空，也返回真实的空结果，不插入模拟数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", Map.of(
            "list", customerList, // 可能为空数组
            "total", customerService.getTotalCustomerCount(),
            "page", page,
            "pageSize", pageSize,
            "isEmpty", customerList.isEmpty() // 明确标识是否为空
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        // 只有在有客户数据时才返回统计信息
        CustomerStatsDTO stats = customerService.getCustomerStats();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", stats); // 可能包含全零的统计数据
        
        return ResponseEntity.ok(response);
    }
}
```

## 业务逻辑设计

### 真实数据验证机制

```typescript
class RealDataValidator {
  // 验证数据是否为真实用户创建
  static validateDataSource(customer: Customer): boolean {
    // 检查是否包含模拟数据特征
    const mockDataIndicators = [
      '示例', '测试', 'demo', 'sample', 'mock', 'fake', 'test'
    ];
    
    const customerName = customer.name?.toLowerCase() || '';
    const email = customer.email?.toLowerCase() || '';
    
    const hasIndicators = mockDataIndicators.some(indicator => 
      customerName.includes(indicator) || email.includes(indicator)
    );
    
    if (hasIndicators) {
      console.warn('检测到可能的模拟数据:', customer.name);
    }
    
    return !hasIndicators;
  }
  
  // 验证数据创建来源
  static validateCreationSource(customer: Customer): boolean {
    // 确保数据来自用户操作，而非系统自动生成
    return !customer.notes?.includes('系统生成');
  }
}
```

### 客户等级管理

```typescript
enum CustomerLevel {
  REGULAR = 'regular',    // 普通客户
  VIP = 'vip',           // VIP客户
  PREMIUM = 'premium'    // 高级客户
}

enum CustomerStatus {
  ACTIVE = 'active',       // 活跃
  INACTIVE = 'inactive',   // 不活跃
  SUSPENDED = 'suspended'  // 暂停
}

// 客户等级升级规则
const levelUpgradeRules = {
  regular: {
    toVip: { minSpent: 10000, minDevices: 2 },
    toPremium: { minSpent: 50000, minDevices: 5 }
  },
  vip: {
    toPremium: { minSpent: 50000, minDevices: 5 }
  }
}
```

### 数据验证规则

```typescript
const customerValidationRules = {
  name: {
    required: true,
    minLength: 2,
    maxLength: 200,
    message: "客户姓名长度应在2-200字符之间"
  },
  phone: {
    required: true,
    pattern: /^1[3-9]\d{9}$/,
    message: "请输入正确的手机号码"
  },
  email: {
    required: true,
    pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: "请输入正确的邮箱地址"
  },
  level: {
    required: true,
    enum: ['regular', 'vip', 'premium'],
    message: "请选择正确的客户等级"
  }
}
```

### 统计计算逻辑

```typescript
class CustomerStatsCalculator {
  // 计算客户统计数据
  static calculateStats(customers: Customer[]): CustomerStats {
    if (!customers || customers.length === 0) {
      return {
        total: 0,
        regular: 0,
        vip: 0,
        premium: 0,
        activeDevices: 0,
        totalRevenue: 0,
        newThisMonth: 0
      };
    }
    
    const total = customers.length;
    const regular = customers.filter(c => c.level === 'regular').length;
    const vip = customers.filter(c => c.level === 'vip').length;
    const premium = customers.filter(c => c.level === 'premium').length;
    const totalRevenue = customers.reduce((sum, c) => sum + c.totalSpent, 0);
    
    return {
      total,
      regular,
      vip,
      premium,
      activeDevices: this.calculateActiveDevices(customers),
      totalRevenue,
      newThisMonth: this.calculateNewCustomersThisMonth(customers)
    };
  }
  
  // 计算客户价值
  static calculateCustomerValue(customer: Customer): number {
    const spentWeight = 0.4;
    const deviceWeight = 0.3;
    const activityWeight = 0.3;
    
    const spentScore = Math.min(customer.totalSpent / 10000, 10);
    const deviceScore = Math.min((customer.deviceCount?.total || 0) * 2, 10);
    const activityScore = this.calculateActivityScore(customer);
    
    return spentWeight * spentScore + deviceWeight * deviceScore + activityWeight * activityScore;
  }
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 复合索引：`(customer_level, customer_status, is_deleted)`
   - 搜索索引：`customer_name`、`phone_number`、`email_address`
   - 统计索引：`total_spent`、`customer_value`、`registered_at`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据分离**
   - 客户基本信息和详细信息分离
   - 地址、设备、订单信息独立存储
   - 历史数据归档策略

### 前端性能优化

1. **组件优化**
   - 使用Vue 3 Composition API
   - 合理使用computed和watch
   - 避免不必要的重新渲染
   - 表格组件虚拟滚动

2. **数据加载优化**
   - 分页加载减少数据量
   - 搜索防抖处理
   - 缓存常用数据（地区、标签列表）
   - 懒加载客户详情数据

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
   - 客户信息创建权限
   - 客户信息编辑权限
   - 客户信息删除权限
   - 敏感信息查看权限

2. **数据权限**
   - 按地区过滤
   - 按客户等级过滤
   - 敏感数据脱敏

## 错误处理设计

### 异常分类

```typescript
enum CustomerErrorCode {
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  INVALID_PHONE_NUMBER = 'INVALID_PHONE_NUMBER',
  INVALID_EMAIL_ADDRESS = 'INVALID_EMAIL_ADDRESS',
  DUPLICATE_PHONE_NUMBER = 'DUPLICATE_PHONE_NUMBER',
  DUPLICATE_EMAIL_ADDRESS = 'DUPLICATE_EMAIL_ADDRESS',
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
  // 创建真实的测试客户数据
  static createRealTestCustomer(): Customer {
    return {
      name: `测试客户${Date.now()}`,
      level: "regular",
      phone: "13800138000",
      email: `test${Date.now()}@example.com`,
      status: "active"
    };
  }
  
  // 严禁创建模拟数据
  static createMockCustomer(): never {
    throw new Error("禁止创建模拟数据 - 请使用createRealTestCustomer()");
  }
}
```

### 单元测试

1. **前端测试**
   - 组件渲染测试（包括空状态测试）
   - 用户交互测试
   - API调用测试
   - 表格组件测试
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
   - 表格渲染性能

2. **业务监控**
   - 客户数据录入量统计
   - 用户访问统计
   - 功能使用统计
   - 错误率监控

3. **数据质量监控**
   - 数据完整性检查
   - 数据一致性验证
   - 异常数据预警
   - 模拟数据检测