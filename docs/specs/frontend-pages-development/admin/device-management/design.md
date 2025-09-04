# Admin Device - 设备管理模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 基于现有前端页面的后端API设计原则

**本设计文档基于现有的 DeviceManagement.vue 前端页面，要求后端API设计完全适配前端页面的功能需求和数据结构。**

## 概述

设备管理模块是YXRobot管理后台的核心设备管理功能，基于现有的DeviceManagement.vue前端页面提供完整的后端API支持。该模块需要开发与前端页面完全匹配的后端接口，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/device/management。

**🚨 重要说明：缓存功能**
**本项目不需要缓存功能，请在编写任务和开发代码时不要添加任何缓存相关的功能。**

## 架构设计

### 系统架构（基于现有前端页面）

```
前端层 (已存在 - DeviceManagement.vue)
├── 页面头部 (标题 + 添加设备按钮)
├── 设备统计卡片 (4个统计指标卡片)
├── 搜索筛选区域 (多条件筛选组件)
├── 设备列表表格 (DataTable组件 + 分页)
├── 设备详情对话框 (DeviceDetailDialog组件)
├── 设备编辑对话框 (DeviceFormDialog组件)
├── 设备日志对话框 (DeviceLogsDialog组件)
├── 批量操作区域 (批量操作下拉菜单)
└── 响应式数据绑定 (Vue 3 Composition API)

API层 (需要开发 - 适配前端)
├── GET /api/admin/devices (设备列表查询)
├── GET /api/admin/devices/{id} (设备详情查询)
├── POST /api/admin/devices (创建设备)
├── PUT /api/admin/devices/{id} (更新设备)
├── DELETE /api/admin/devices/{id} (删除设备)
├── PATCH /api/admin/devices/{id}/status (更新设备状态)
├── POST /api/admin/devices/{id}/reboot (重启设备)
├── POST /api/admin/devices/{id}/activate (激活设备)
├── POST /api/admin/devices/{id}/firmware (推送固件)
├── GET /api/admin/devices/{id}/logs (获取设备日志)
├── POST /api/admin/devices/batch/firmware (批量推送固件)
├── POST /api/admin/devices/batch/reboot (批量重启)
├── DELETE /api/admin/devices/batch (批量删除)
├── GET /api/admin/devices/stats (设备统计数据)
└── GET /api/admin/customers/options (客户选项)

业务层 (需要开发)
├── ManagedDeviceService (设备管理服务)
├── ManagedDeviceStatsService (设备统计服务)
├── ManagedDeviceOperationService (设备操作服务)
├── ManagedDeviceLogService (设备日志服务)
└── DeviceValidationService (设备数据验证服务)

数据层 (需要开发)
├── managed_devices (设备管理主表)
├── managed_device_specifications (设备技术参数表)
├── managed_device_usage_stats (设备使用统计表)
├── managed_device_maintenance_records (设备维护记录表)
├── managed_device_configurations (设备配置表)
├── managed_device_locations (设备位置信息表)
├── managed_device_logs (设备日志表)
├── managed_device_customer_relation (设备客户关联表)
├── managed_device_maintenance_relation (设备维护关联表)
└── customers (客户信息表 - 已存在)
```

## 数据模型设计

### 数据库设计规范

#### 关联表设计原则（强制遵循）

**核心设计原则：**
- **禁止使用外键约束**: 所有表与表之间的关联必须通过关联表实现
- **降低耦合度**: 通过关联表解耦主表之间的直接依赖关系
- **提高扩展性**: 关联表设计便于后续功能扩展和数据迁移

**关联表命名规范：**
```sql
-- 格式: managed_{主表名}_{关联表名}_relation
-- 示例:
managed_device_user_relation        -- 设备用户关联表
managed_device_customer_relation    -- 设备客户关联表
managed_device_maintenance_relation -- 设备维护关联表
```

**关联表结构标准：**
```sql
-- 基础关联表结构
CREATE TABLE managed_device_customer_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    device_id BIGINT NOT NULL COMMENT '设备ID',
    customer_id BIGINT NOT NULL COMMENT '客户ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    status TINYINT DEFAULT 1 COMMENT '状态：1-有效，0-无效',
    INDEX idx_device_id (device_id),
    INDEX idx_customer_id (customer_id),
    UNIQUE KEY uk_device_customer (device_id, customer_id)
) COMMENT='设备客户关联表';
```

**数据完整性保障：**
- **应用层控制**: 数据完整性检查在业务逻辑层实现
- **索引优化**: 关联字段必须建立索引提高查询性能
- **唯一约束**: 防止重复关联的业务唯一键约束
- **软删除**: 使用status字段实现逻辑删除，保留数据历史

### 设备主表 (managed_devices)

```sql
CREATE TABLE `managed_devices` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设备ID，主键',
  `serial_number` VARCHAR(50) NOT NULL COMMENT '设备序列号',
  `model` ENUM('YX-EDU-2024', 'YX-HOME-2024', 'YX-PRO-2024') NOT NULL COMMENT '设备型号',
  `status` ENUM('online', 'offline', 'error', 'maintenance') DEFAULT 'offline' COMMENT '设备状态',
  `firmware_version` VARCHAR(20) NOT NULL COMMENT '固件版本',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID（引用customers表）',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称（冗余字段，避免关联查询）',
  `customer_phone` VARCHAR(20) NOT NULL COMMENT '客户电话（冗余字段，避免关联查询）',
  `last_online_at` DATETIME COMMENT '最后在线时间',
  `activated_at` DATETIME COMMENT '激活时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` VARCHAR(100) COMMENT '创建人',
  `notes` TEXT COMMENT '设备备注',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_serial_number` (`serial_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_model` (`model`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备管理主表';
```

### 设备技术参数表 (managed_device_specifications)

```sql
CREATE TABLE `managed_device_specifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规格ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `cpu` VARCHAR(100) COMMENT 'CPU规格',
  `memory` VARCHAR(50) COMMENT '内存规格',
  `storage` VARCHAR(50) COMMENT '存储规格',
  `display` VARCHAR(100) COMMENT '显示屏规格',
  `battery` VARCHAR(50) COMMENT '电池规格',
  `connectivity` JSON COMMENT '连接性规格（JSON数组）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备技术参数表（设备管理模块）';
```

### 设备使用统计表 (managed_device_usage_stats)

```sql
CREATE TABLE `managed_device_usage_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `total_runtime` INT DEFAULT 0 COMMENT '总运行时间（分钟）',
  `usage_count` INT DEFAULT 0 COMMENT '使用次数',
  `last_used_at` DATETIME COMMENT '最后使用时间',
  `average_session_time` INT DEFAULT 0 COMMENT '平均使用时长（分钟）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备使用统计表（设备管理模块）';
```

### 设备维护记录表 (managed_device_maintenance_records)

```sql
CREATE TABLE `managed_device_maintenance_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '维护记录ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `type` ENUM('repair', 'upgrade', 'inspection', 'replacement') NOT NULL COMMENT '维护类型',
  `description` TEXT NOT NULL COMMENT '维护描述',
  `technician` VARCHAR(100) NOT NULL COMMENT '技术员',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `status` ENUM('pending', 'in_progress', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '维护状态',
  `cost` DECIMAL(10,2) COMMENT '维护费用',
  `parts` JSON COMMENT '更换部件（JSON数组）',
  `notes` TEXT COMMENT '维护备注',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_type` (`type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备维护记录表（设备管理模块）';
```

### 设备配置表 (managed_device_configurations)

```sql
CREATE TABLE `managed_device_configurations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `language` VARCHAR(10) DEFAULT 'zh-CN' COMMENT '语言设置',
  `timezone` VARCHAR(50) DEFAULT 'Asia/Shanghai' COMMENT '时区设置',
  `auto_update` TINYINT(1) DEFAULT 1 COMMENT '自动更新',
  `debug_mode` TINYINT(1) DEFAULT 0 COMMENT '调试模式',
  `custom_settings` JSON COMMENT '自定义设置（JSON对象）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备配置表（设备管理模块）';
```

### 设备位置信息表 (managed_device_locations)

```sql
CREATE TABLE `managed_device_locations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '位置ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `latitude` DECIMAL(10,8) COMMENT '纬度',
  `longitude` DECIMAL(11,8) COMMENT '经度',
  `address` VARCHAR(500) COMMENT '地址',
  `last_updated` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_latitude_longitude` (`latitude`, `longitude`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备位置信息表（设备管理模块）';
```

### 设备日志表 (managed_device_logs)

```sql
CREATE TABLE `managed_device_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `timestamp` DATETIME NOT NULL COMMENT '时间戳',
  `level` ENUM('info', 'warning', 'error', 'debug') NOT NULL COMMENT '日志级别',
  `category` ENUM('system', 'user', 'network', 'hardware', 'software') NOT NULL COMMENT '日志分类',
  `message` TEXT NOT NULL COMMENT '日志消息',
  `details` JSON COMMENT '详细信息（JSON对象）',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_timestamp` (`timestamp`),
  INDEX `idx_level` (`level`),
  INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备日志表（设备管理模块）';
```

## 接口设计

### REST API 接口（基于前端页面需求）

#### 1. 设备管理接口

```typescript
// 获取设备列表
GET /api/admin/devices
参数: {
  page?: number,        // 页码，默认1
  pageSize?: number,    // 每页大小，默认20
  keyword?: string,     // 搜索关键词
  status?: string,      // 设备状态筛选
  model?: string,       // 设备型号筛选
  customerId?: string,  // 客户筛选
  dateRange?: [string, string],  // 日期范围筛选
  sortBy?: string,      // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: Device[],      // 设备数组
    total: number,       // 总记录数
    page: number,        // 当前页码
    pageSize: number,    // 每页大小
    totalPages: number,  // 总页数
    stats: ManagedDeviceStats   // 统计数据
  }
}

// 获取设备详情
GET /api/admin/devices/{id}
响应: {
  code: 200,
  message: "查询成功",
  data: Device  // 完整的设备详情
}

// 创建设备
POST /api/admin/devices
请求体: CreateDeviceData
响应: {
  code: 200,
  message: "创建成功",
  data: Device
}

// 更新设备
PUT /api/admin/devices/{id}
请求体: UpdateDeviceData
响应: {
  code: 200,
  message: "更新成功",
  data: Device
}

// 删除设备
DELETE /api/admin/devices/{id}
响应: {
  code: 200,
  message: "删除成功"
}
```

#### 2. 设备操作管理接口

```typescript
// 更新设备状态
PATCH /api/admin/devices/{id}/status
请求体: {
  status: 'online' | 'offline' | 'error' | 'maintenance'
}
响应: {
  code: 200,
  message: "状态更新成功",
  data: { id: string, status: string, updatedAt: string }
}

// 重启设备
POST /api/admin/devices/{id}/reboot
响应: {
  code: 200,
  message: "重启指令已发送"
}

// 激活设备
POST /api/admin/devices/{id}/activate
响应: {
  code: 200,
  message: "激活指令已发送"
}

// 推送固件
POST /api/admin/devices/{id}/firmware
请求体: { version?: string }
响应: {
  code: 200,
  message: "固件推送已启动"
}
```

#### 3. 批量操作接口

```typescript
// 批量推送固件
POST /api/admin/devices/batch/firmware
请求体: { deviceIds: string[], version?: string }
响应: {
  code: 200,
  message: "批量固件推送已启动",
  data: { updatedCount: number }
}

// 批量重启设备
POST /api/admin/devices/batch/reboot
请求体: { deviceIds: string[] }
响应: {
  code: 200,
  message: "批量重启指令已发送",
  data: { updatedCount: number }
}

// 批量删除设备
DELETE /api/admin/devices/batch
请求体: { deviceIds: string[] }
响应: {
  code: 200,
  message: "批量删除成功",
  data: { deletedCount: number }
}
```

#### 4. 设备统计接口

```typescript
// 获取设备统计数据
GET /api/admin/devices/stats
响应: {
  code: 200,
  message: "查询成功",
  data: {
    total: number,              // 设备总数
    online: number,             // 在线设备数
    offline: number,            // 离线设备数
    error: number,              // 故障设备数
    maintenance: number         // 维护中设备数
  }
}
```

#### 5. 设备日志接口

```typescript
// 获取设备日志
GET /api/admin/devices/{id}/logs
参数: {
  page?: number,
  pageSize?: number,
  level?: string,
  category?: string,
  dateRange?: [string, string]
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: ManagedDeviceLog[],
    total: number,
    page: number,
    pageSize: number
  }
}
```

#### 6. 辅助功能接口

```typescript
// 获取客户选项
GET /api/admin/customers/options
响应: {
  code: 200,
  message: "查询成功",
  data: Array<{ id: string, name: string }>
}

// 导出设备数据
GET /api/admin/devices/export
参数: 筛选条件
响应: {
  code: 200,
  message: "导出成功",
  data: { downloadUrl: string, filename: string }
}
```

## 业务逻辑设计

### 设备状态管理

```typescript
enum DeviceStatus {
  ONLINE = 'online',        // 在线
  OFFLINE = 'offline',      // 离线
  ERROR = 'error',          // 故障
  MAINTENANCE = 'maintenance' // 维护中
}

// 状态转换规则
const statusTransitions = {
  offline: ['online', 'maintenance'],
  online: ['offline', 'error', 'maintenance'],
  error: ['offline', 'maintenance'],
  maintenance: ['online', 'offline']
}
```

### 设备数据验证规则

```typescript
const deviceValidationRules = {
  serialNumber: {
    required: true,
    pattern: /^[A-Z0-9-]{10,20}$/,
    message: "设备序列号格式不正确"
  },
  model: {
    required: true,
    enum: ['YX-EDU-2024', 'YX-HOME-2024', 'YX-PRO-2024'],
    message: "设备型号必须为教育版、家庭版或专业版"
  },
  customerId: {
    required: true,
    message: "必须指定所属客户"
  },
  firmwareVersion: {
    required: true,
    pattern: /^\d+\.\d+\.\d+$/,
    message: "固件版本格式不正确"
  }
}
```

### 设备统计计算逻辑

```typescript
class ManagedDeviceStatsCalculator {
  static calculateStats(devices: ManagedDevice[]): ManagedDeviceStats {
    if (!devices || devices.length === 0) {
      return {
        total: 0,
        online: 0,
        offline: 0,
        error: 0,
        maintenance: 0
      };
    }
    
    return {
      total: devices.length,
      online: devices.filter(d => d.status === 'online').length,
      offline: devices.filter(d => d.status === 'offline').length,
      error: devices.filter(d => d.status === 'error').length,
      maintenance: devices.filter(d => d.status === 'maintenance').length
    };
  }
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 唯一索引：`serial_number`
   - 复合索引：`(customer_id, created_at)`、`(model, status, created_at)`
   - 筛选索引：`model`、`status`
   - 时间索引：`created_at`、`last_online_at`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据库连接池配置**
   - 配置合适的连接池大小
   - 设置连接超时和空闲超时
   - 监控连接池使用情况
   - 优化连接池性能参数

4. **大数据量处理**
   - 使用分页查询处理大数据量
   - 实施数据归档策略
   - 优化慢查询性能
   - 使用读写分离提升性能

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
   - 懒加载设备详情

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
   - 设备创建权限
   - 设备编辑权限
   - 设备删除权限
   - 设备操作权限

### 数据保护和隐私

1. **个人身份信息(PII)处理**
   - 在代码示例和讨论中使用通用占位符替代真实PII数据
   - 示例：使用[name]、[phone_number]、[email]、[address]等占位符
   - 避免在日志中记录敏感个人信息

2. **恶意代码防护**
   - 拒绝任何恶意代码请求
   - 实施输入验证和输出编码
   - 使用参数化查询防止SQL注入

3. **安全最佳实践**
   - 遵循OWASP安全指南
   - 实施最小权限原则
   - 定期进行安全审计
   - 加密敏感数据传输和存储

### 文件上传安全

1. **文件类型验证**
   - 检查文件MIME类型
   - 验证文件扩展名
   - 双重验证确保安全

2. **文件大小限制**
   - 图片文件: 最大10MB
   - 视频文件: 最大100MB
   - 防止大文件攻击

3. **路径安全**
   - 使用UUID生成唯一文件名
   - 防止路径遍历攻击
   - 文件访问权限控制

## 错误处理设计

### 异常分类

```typescript
enum DeviceErrorCode {
  DEVICE_NOT_FOUND = 'DEVICE_NOT_FOUND',
  INVALID_SERIAL_NUMBER = 'INVALID_SERIAL_NUMBER',
  INVALID_STATUS_TRANSITION = 'INVALID_STATUS_TRANSITION',
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  FIRMWARE_VERSION_INVALID = 'FIRMWARE_VERSION_INVALID',
  DEVICE_OPERATION_FAILED = 'DEVICE_OPERATION_FAILED',
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

### 单元测试

1. **前端测试**
   - 组件渲染测试（包括空状态测试）
   - 用户交互测试
   - API调用测试
   - 状态管理测试

2. **后端测试**
   - Service层业务逻辑测试
   - Controller层接口测试
   - Mapper层数据访问测试
   - 设备操作逻辑测试

### 集成测试

1. **API集成测试**
   - 完整的CRUD流程测试
   - 设备操作功能测试
   - 批量操作测试

2. **前后端集成测试**
   - 端到端功能测试
   - 数据一致性测试
   - 性能压力测试

### 测试覆盖率目标
- **行覆盖率**: ≥ 80%
- **分支覆盖率**: ≥ 75%
- **方法覆盖率**: ≥ 85%
- **类覆盖率**: ≥ 90%

## 文件上传系统设计

### 目录结构
```
uploads/
├── devices/                     # 设备相关文件
│   ├── images/                  # 设备图片
│   │   └── YYYY/MM/DD/         # 按日期分组
│   ├── documents/               # 设备文档
│   │   └── YYYY/MM/DD/         # 按日期分组
│   └── firmware/                # 固件文件
│       └── YYYY/MM/DD/         # 按日期分组
└── README.md                   # 说明文档
```

### 支持的文件类型
- **图片文件**: JPEG, JPG, PNG, GIF, WebP (最大10MB)
- **文档文件**: PDF, DOC, DOCX, TXT (最大50MB)
- **固件文件**: BIN, HEX, ZIP (最大100MB)

### 文件上传API
- **设备图片上传**: `POST /api/admin/devices/{id}/upload/image`
- **设备文档上传**: `POST /api/admin/devices/{id}/upload/document`
- **固件文件上传**: `POST /api/admin/devices/{id}/upload/firmware`

### 文件访问URL
- **基础URL**: `http://localhost:8081/api/files/`
- **完整URL**: `{基础URL}{相对路径}`

### 存储管理
- **自动目录创建**: 上传时自动创建日期目录
- **文件清理**: 删除设备时自动清理相关文件
- **备份建议**: 定期备份uploads目录

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
   - 设备在线率统计
   - 用户访问统计
   - 功能使用统计
   - 错误率监控