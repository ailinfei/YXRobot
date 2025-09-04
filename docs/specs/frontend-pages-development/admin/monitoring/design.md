# Admin Device - 设备监控模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 基于现有前端页面的后端API设计原则

**本设计文档基于现有的 DeviceMonitoring.vue 前端页面，要求后端API设计完全适配前端页面的功能需求和数据结构。**

### 🔍 开发前置检查设计（必须执行）

#### 📋 数据库表名冲突检查设计

**检查原则：**
- 所有新建表名必须与现有数据库表名进行对比验证
- 发现冲突时保留现有表，重新设计新表名
- 新表名必须能够清晰表达功能用途

**检查结果应用：**
```sql
-- 原设计表名 → 最终使用表名（经冲突检查后确认）
device_monitoring_stats     → device_monitoring_stats     ✅ 无冲突，直接使用
device_monitoring_data      → device_monitoring_data      ✅ 无冲突，直接使用
device_alerts              → device_alerts              ✅ 无冲突，直接使用
device_performance_metrics → device_performance_metrics ✅ 无冲突，直接使用
device_network_status      → device_network_status      ✅ 无冲突，直接使用
```

#### 📋 Java类重复检查设计

**检查原则：**
- 所有新建类名必须与现有Java类名进行对比验证
- 发现冲突时保留现有类，重新设计新类名
- 新类名必须使用模块特定前缀避免冲突

**检查结果应用：**
```java
// 原设计类名 → 最终使用类名（经冲突检查后确认）
ManagedDevicePerformanceMonitorService → DeviceMonitoringService           ⚠️ 重命名避免冲突
ManagedDevicePerformanceController     → DeviceMonitoringController        ⚠️ 重命名避免冲突
ManagedDevicePerformanceService        → DevicePerformanceService          ⚠️ 重命名避免冲突
DeviceMonitoringStatsService           → DeviceMonitoringStatsService      ✅ 无冲突，直接使用
DeviceAlertService                     → DeviceAlertService                ✅ 无冲突，直接使用
DeviceNetworkService                   → DeviceNetworkService              ✅ 无冲突，直接使用
DeviceControlService                   → DeviceControlService              ✅ 无冲突，直接使用
```

**冲突处理策略：**
1. **保留现有类**：不删除任何现有的Java类文件
2. **重命名新类**：使用更具体的模块前缀（如Device而非ManagedDevice）
3. **更新引用**：同步更新所有对新类的引用关系
4. **文档同步**：更新设计文档和任务文档中的类名



## 概述

设备监控模块是YXRobot管理后台的核心设备监控功能，基于现有的DeviceMonitoring.vue前端页面提供完整的后端API支持。该模块需要开发与前端页面完全匹配的后端接口，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/device/monitoring。

**🚨 重要说明：缓存功能**
**本项目不需要缓存功能，请在编写任务和开发代码时不要添加任何缓存相关的功能。**

## 架构设计

### 系统架构（基于现有前端页面）

```
前端层 (已存在 - DeviceMonitoring.vue)
├── 页面头部 (标题 + 描述)
├── 实时统计卡片 (4个监控指标卡片)
├── 设备分布地图 (地图组件显示设备分布)
├── 实时告警列表 (告警信息显示)
├── 性能监控图表 (性能趋势图 + 网络状态图)
├── 设备实时状态列表 (表格 + 搜索筛选)
└── 响应式数据绑定 (Vue 3 Composition API)

API层 (需要开发 - 适配前端)
├── GET /api/admin/device/monitoring/stats (监控统计数据)
├── GET /api/admin/device/monitoring/devices (设备监控列表)
├── GET /api/admin/device/monitoring/alerts (实时告警数据)
├── GET /api/admin/device/monitoring/performance (性能图表数据)
├── GET /api/admin/device/monitoring/network (网络状态数据)
├── GET /api/admin/device/monitoring/map (设备分布地图数据)
├── POST /api/admin/device/monitoring/control/{id} (远程控制设备)
└── GET /api/admin/device/monitoring/device/{id} (设备详情)

业务层 (需要开发)
├── DeviceMonitoringService (设备监控服务)
├── DeviceMonitoringStatsService (监控统计服务)
├── DeviceAlertService (设备告警服务)
├── DevicePerformanceService (设备性能服务)
├── DeviceNetworkService (设备网络服务)
└── DeviceControlService (设备控制服务)

数据层 (需要开发)
├── device_monitoring_stats (监控统计表)
├── device_monitoring_data (设备监控数据表)
├── device_alerts (设备告警表)
├── device_performance_metrics (设备性能指标表)
├── device_network_status (设备网络状态表)
├── device_locations (设备位置信息表)
└── managed_devices (设备管理主表 - 已存在)
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
-- 格式: device_{主表名}_{关联表名}_relation
-- 示例:
device_monitoring_alert_relation     -- 设备监控告警关联表
device_performance_metric_relation   -- 设备性能指标关联表
device_network_status_relation       -- 设备网络状态关联表
```

### 监控统计表 (device_monitoring_stats)

```sql
CREATE TABLE `device_monitoring_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `online_count` INT DEFAULT 0 COMMENT '在线设备数量',
  `offline_count` INT DEFAULT 0 COMMENT '离线设备数量',
  `error_count` INT DEFAULT 0 COMMENT '故障设备数量',
  `maintenance_count` INT DEFAULT 0 COMMENT '维护中设备数量',
  `total_count` INT DEFAULT 0 COMMENT '设备总数',
  `avg_performance` DECIMAL(5,2) DEFAULT 0.00 COMMENT '平均性能百分比',
  `online_trend` INT DEFAULT 0 COMMENT '在线设备趋势变化',
  `offline_trend` INT DEFAULT 0 COMMENT '离线设备趋势变化',
  `error_trend` INT DEFAULT 0 COMMENT '故障设备趋势变化',
  `performance_trend` INT DEFAULT 0 COMMENT '性能趋势变化',
  `stats_date` DATE NOT NULL COMMENT '统计日期',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stats_date` (`stats_date`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备监控统计表';
```

### 设备监控数据表 (device_monitoring_data)

```sql
CREATE TABLE `device_monitoring_data` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '监控数据ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `serial_number` VARCHAR(50) NOT NULL COMMENT '设备序列号（冗余字段）',
  `customer_name` VARCHAR(100) COMMENT '客户名称（冗余字段）',
  `status` ENUM('online', 'offline', 'error', 'maintenance') DEFAULT 'offline' COMMENT '设备状态',
  `last_online_at` DATETIME COMMENT '最后在线时间',
  `model` VARCHAR(50) COMMENT '设备型号',
  `firmware_version` VARCHAR(20) COMMENT '固件版本',
  `location_latitude` DECIMAL(10,8) COMMENT '位置纬度',
  `location_longitude` DECIMAL(11,8) COMMENT '位置经度',
  `location_address` VARCHAR(500) COMMENT '位置地址',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_serial_number` (`serial_number`),
  INDEX `idx_status` (`status`),
  INDEX `idx_last_online_at` (`last_online_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备监控数据表';
```

### 设备告警表 (device_alerts)

```sql
CREATE TABLE `device_alerts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '告警ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `alert_level` ENUM('error', 'warning', 'info') NOT NULL COMMENT '告警级别',
  `alert_type` VARCHAR(50) NOT NULL COMMENT '告警类型',
  `alert_message` TEXT NOT NULL COMMENT '告警消息',
  `alert_timestamp` DATETIME NOT NULL COMMENT '告警时间',
  `is_resolved` TINYINT(1) DEFAULT 0 COMMENT '是否已解决',
  `resolved_at` DATETIME COMMENT '解决时间',
  `resolved_by` VARCHAR(100) COMMENT '解决人',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_alert_level` (`alert_level`),
  INDEX `idx_alert_timestamp` (`alert_timestamp`),
  INDEX `idx_is_resolved` (`is_resolved`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备告警表';
```

### 设备性能指标表 (device_performance_metrics)

```sql
CREATE TABLE `device_performance_metrics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '性能指标ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `cpu_usage` DECIMAL(5,2) DEFAULT 0.00 COMMENT 'CPU使用率百分比',
  `memory_usage` DECIMAL(5,2) DEFAULT 0.00 COMMENT '内存使用率百分比',
  `disk_usage` DECIMAL(5,2) DEFAULT 0.00 COMMENT '磁盘使用率百分比',
  `temperature` DECIMAL(5,2) COMMENT '设备温度',
  `battery_level` DECIMAL(5,2) COMMENT '电池电量百分比',
  `network_latency` INT COMMENT '网络延迟（毫秒）',
  `network_bandwidth` DECIMAL(10,2) COMMENT '网络带宽（Mbps）',
  `metric_timestamp` DATETIME NOT NULL COMMENT '指标采集时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  INDEX `idx_device_id` (`device_id`),
  INDEX `idx_metric_timestamp` (`metric_timestamp`),
  INDEX `idx_cpu_usage` (`cpu_usage`),
  INDEX `idx_memory_usage` (`memory_usage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备性能指标表';
```

### 设备网络状态表 (device_network_status)

```sql
CREATE TABLE `device_network_status` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '网络状态ID，主键',
  `device_id` BIGINT NOT NULL COMMENT '设备ID（引用managed_devices表）',
  `network_type` ENUM('4G', '5G', 'WiFi', 'Ethernet') COMMENT '网络类型',
  `signal_strength` INT DEFAULT 0 COMMENT '信号强度百分比',
  `connection_status` ENUM('connected', 'disconnected', 'connecting') DEFAULT 'disconnected' COMMENT '连接状态',
  `ip_address` VARCHAR(45) COMMENT 'IP地址',
  `mac_address` VARCHAR(17) COMMENT 'MAC地址',
  `download_speed` DECIMAL(10,2) COMMENT '下载速度（Mbps）',
  `upload_speed` DECIMAL(10,2) COMMENT '上传速度（Mbps）',
  `ping_latency` INT COMMENT 'Ping延迟（毫秒）',
  `last_connected_at` DATETIME COMMENT '最后连接时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_id` (`device_id`),
  INDEX `idx_network_type` (`network_type`),
  INDEX `idx_connection_status` (`connection_status`),
  INDEX `idx_signal_strength` (`signal_strength`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备网络状态表';
```

## 接口设计

### REST API 接口（基于前端页面需求）

#### 1. 监控统计接口

```typescript
// 获取监控统计数据
GET /api/admin/device/monitoring/stats
响应: {
  code: 200,
  message: "查询成功",
  data: {
    online: number,           // 在线设备数量
    offline: number,          // 离线设备数量
    error: number,            // 故障设备数量
    avgPerformance: number,   // 平均性能百分比
    onlineTrend: number,      // 在线设备趋势
    offlineTrend: number,     // 离线设备趋势
    errorTrend: number,       // 故障设备趋势
    performanceTrend: number  // 性能趋势
  }
}
```

#### 2. 设备监控列表接口

```typescript
// 获取设备监控列表
GET /api/admin/device/monitoring/devices
参数: {
  keyword?: string,         // 搜索关键词
  status?: string,          // 状态筛选
  page?: number,            // 页码
  pageSize?: number         // 每页大小
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    list: Array<{
      id: string,
      serialNumber: string,
      customerName: string,
      status: 'online' | 'offline' | 'error' | 'maintenance',
      performance: {
        cpu: number,          // CPU使用率
        memory: number        // 内存使用率
      },
      lastOnlineAt: string,
      model: string,
      firmwareVersion: string,
      location: {
        latitude: number,
        longitude: number,
        address: string
      }
    }>,
    total: number,
    page: number,
    pageSize: number
  }
}
```

#### 3. 实时告警接口

```typescript
// 获取实时告警数据
GET /api/admin/device/monitoring/alerts
参数: {
  limit?: number,           // 限制数量，默认10
  level?: string            // 告警级别筛选
}
响应: {
  code: 200,
  message: "查询成功",
  data: Array<{
    id: string,
    level: 'error' | 'warning' | 'info',
    message: string,
    timestamp: string,
    deviceId: string,
    deviceSerialNumber: string
  }>
}
```

#### 4. 性能图表数据接口

```typescript
// 获取性能图表数据
GET /api/admin/device/monitoring/performance
参数: {
  timeRange?: string,       // 时间范围：1h, 6h, 24h, 7d
  deviceId?: string         // 特定设备ID
}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    performanceTrend: {
      timestamps: string[],
      cpuData: number[],
      memoryData: number[],
      avgPerformance: number[]
    },
    deviceCount: {
      online: number[],
      offline: number[],
      error: number[]
    }
  }
}
```

#### 5. 网络状态数据接口

```typescript
// 获取网络状态数据
GET /api/admin/device/monitoring/network
响应: {
  code: 200,
  message: "查询成功",
  data: {
    networkTypes: {
      '4G': number,
      '5G': number,
      'WiFi': number,
      'Ethernet': number
    },
    signalStrength: {
      excellent: number,      // 信号强度优秀的设备数
      good: number,          // 信号强度良好的设备数
      poor: number           // 信号强度较差的设备数
    },
    connectionStatus: {
      connected: number,
      disconnected: number,
      connecting: number
    }
  }
}
```

#### 6. 设备分布地图接口

```typescript
// 获取设备分布地图数据
GET /api/admin/device/monitoring/map
响应: {
  code: 200,
  message: "查询成功",
  data: Array<{
    deviceId: string,
    serialNumber: string,
    latitude: number,
    longitude: number,
    status: 'online' | 'offline' | 'error' | 'maintenance',
    customerName: string,
    address: string
  }>
}
```

#### 7. 设备控制接口

```typescript
// 远程控制设备
POST /api/admin/device/monitoring/control/{id}
请求体: {
  action: 'reboot' | 'shutdown' | 'restart_service' | 'update_firmware',
  parameters?: object
}
响应: {
  code: 200,
  message: "控制指令已发送",
  data: {
    deviceId: string,
    action: string,
    status: 'sent' | 'executing' | 'completed' | 'failed',
    timestamp: string
  }
}
```

#### 8. 设备详情接口

```typescript
// 获取设备详情
GET /api/admin/device/monitoring/device/{id}
响应: {
  code: 200,
  message: "查询成功",
  data: {
    id: string,
    serialNumber: string,
    customerName: string,
    status: string,
    model: string,
    firmwareVersion: string,
    lastOnlineAt: string,
    performance: {
      cpu: number,
      memory: number,
      disk: number,
      temperature: number,
      batteryLevel: number
    },
    network: {
      type: string,
      signalStrength: number,
      connectionStatus: string,
      ipAddress: string,
      downloadSpeed: number,
      uploadSpeed: number
    },
    location: {
      latitude: number,
      longitude: number,
      address: string
    },
    recentAlerts: Array<{
      level: string,
      message: string,
      timestamp: string
    }>
  }
}
```

## 业务逻辑设计

### 🔍 冲突检查后的类设计架构

**经过冲突检查后的最终类设计：**

```java
// 服务层类设计（避免冲突后的最终命名）
@Service
public class DeviceMonitoringService {
    // 设备监控核心业务逻辑（原计划：ManagedDevicePerformanceMonitorService）
}

@Service  
public class DeviceMonitoringStatsService {
    // 监控统计业务逻辑（无冲突，直接使用）
}

@Service
public class DevicePerformanceService {
    // 设备性能业务逻辑（原计划：ManagedDevicePerformanceService）
}

@Service
public class DeviceAlertService {
    // 设备告警业务逻辑（无冲突，直接使用）
}

@Service
public class DeviceNetworkService {
    // 设备网络业务逻辑（无冲突，直接使用）
}

@Service
public class DeviceControlService {
    // 设备控制业务逻辑（无冲突，直接使用）
}

// 控制器层类设计（避免冲突后的最终命名）
@RestController
@RequestMapping("/api/admin/device/monitoring")
public class DeviceMonitoringController {
    // 设备监控API接口（原计划：ManagedDevicePerformanceController）
}

@RestController
@RequestMapping("/api/admin/device/monitoring/alerts")
public class DeviceAlertController {
    // 设备告警API接口（无冲突，直接使用）
}

@RestController
@RequestMapping("/api/admin/device/monitoring/performance")
public class DevicePerformanceController {
    // 设备性能API接口（重命名避免冲突）
}

@RestController
@RequestMapping("/api/admin/device/monitoring/network")
public class DeviceNetworkController {
    // 设备网络API接口（无冲突，直接使用）
}

@RestController
@RequestMapping("/api/admin/device/monitoring/control")
public class DeviceControlController {
    // 设备控制API接口（无冲突，直接使用）
}
```

**类命名冲突解决方案：**
1. **现有类保留**：`ManagedDevicePerformanceMonitorService`、`ManagedDevicePerformanceController`、`ManagedDevicePerformanceService` 等现有类完全保留
2. **新类重命名**：使用 `Device` 前缀替代 `ManagedDevice` 前缀，突出监控模块特色
3. **功能区分**：通过不同的包路径和API路径区分功能模块

### 监控统计计算逻辑

```typescript
class DeviceMonitoringStatsCalculator {
  static calculateStats(devices: DeviceMonitoringData[]): MonitoringStats {
    if (!devices || devices.length === 0) {
      return {
        online: 0,
        offline: 0,
        error: 0,
        avgPerformance: 0,
        onlineTrend: 0,
        offlineTrend: 0,
        errorTrend: 0,
        performanceTrend: 0
      };
    }
    
    const stats = {
      online: devices.filter(d => d.status === 'online').length,
      offline: devices.filter(d => d.status === 'offline').length,
      error: devices.filter(d => d.status === 'error').length,
      avgPerformance: this.calculateAveragePerformance(devices)
    };
    
    // 计算趋势数据（与昨天对比）
    const trends = this.calculateTrends(stats);
    
    return { ...stats, ...trends };
  }
  
  static calculateAveragePerformance(devices: DeviceMonitoringData[]): number {
    const onlineDevices = devices.filter(d => d.status === 'online');
    if (onlineDevices.length === 0) return 0;
    
    const totalPerformance = onlineDevices.reduce((sum, device) => {
      const performance = (device.performance.cpu + device.performance.memory) / 2;
      return sum + (100 - performance); // 转换为性能分数
    }, 0);
    
    return Math.round(totalPerformance / onlineDevices.length);
  }
}
```

### 告警生成逻辑

```typescript
class DeviceAlertGenerator {
  static generateAlerts(devices: DeviceMonitoringData[]): DeviceAlert[] {
    const alerts: DeviceAlert[] = [];
    
    devices.forEach(device => {
      // CPU使用率告警
      if (device.performance.cpu > 90) {
        alerts.push({
          deviceId: device.id,
          level: 'error',
          type: 'high_cpu_usage',
          message: `设备 ${device.serialNumber} CPU使用率过高 (${device.performance.cpu}%)`,
          timestamp: new Date().toISOString()
        });
      } else if (device.performance.cpu > 80) {
        alerts.push({
          deviceId: device.id,
          level: 'warning',
          type: 'high_cpu_usage',
          message: `设备 ${device.serialNumber} CPU使用率较高 (${device.performance.cpu}%)`,
          timestamp: new Date().toISOString()
        });
      }
      
      // 内存使用率告警
      if (device.performance.memory > 90) {
        alerts.push({
          deviceId: device.id,
          level: 'error',
          type: 'high_memory_usage',
          message: `设备 ${device.serialNumber} 内存使用率过高 (${device.performance.memory}%)`,
          timestamp: new Date().toISOString()
        });
      }
      
      // 离线时间告警
      if (device.status === 'offline') {
        const offlineHours = this.calculateOfflineHours(device.lastOnlineAt);
        if (offlineHours > 24) {
          alerts.push({
            deviceId: device.id,
            level: 'error',
            type: 'device_offline',
            message: `设备 ${device.serialNumber} 已离线超过24小时`,
            timestamp: new Date().toISOString()
          });
        }
      }
    });
    
    return alerts.sort((a, b) => 
      new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
    );
  }
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 设备关联索引：`device_id`
   - 状态查询索引：`status`
   - 时间范围索引：`created_at`、`metric_timestamp`
   - 复合索引：`(device_id, metric_timestamp)`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据库连接池配置**
   - 配置合适的连接池大小
   - 设置连接超时和空闲超时
   - 监控连接池使用情况

### 实时数据处理

1. **数据采集策略**
   - 设备性能数据每分钟采集一次
   - 网络状态数据每30秒采集一次
   - 告警数据实时生成和推送

2. **数据存储优化**
   - 历史数据按天分表存储
   - 定期清理过期数据
   - 使用批量插入提高性能

## 安全设计

### 数据验证

1. **前端验证**
   - 搜索关键词长度限制
   - 参数类型验证
   - XSS防护处理

2. **后端验证**
   - 参数完整性验证
   - 数据类型验证
   - 业务规则验证
   - SQL注入防护

### 权限控制

1. **操作权限**
   - 设备监控查看权限
   - 设备控制操作权限
   - 告警管理权限

### 数据保护

1. **个人身份信息(PII)处理**
   - 在代码示例和讨论中使用通用占位符替代真实PII数据
   - 示例：使用[customer_name]、[device_serial]、[location]等占位符
   - 避免在日志中记录敏感设备信息

## 错误处理设计

### 异常分类

```typescript
enum DeviceMonitoringErrorCode {
  DEVICE_NOT_FOUND = 'DEVICE_NOT_FOUND',
  MONITORING_DATA_NOT_AVAILABLE = 'MONITORING_DATA_NOT_AVAILABLE',
  PERFORMANCE_DATA_INVALID = 'PERFORMANCE_DATA_INVALID',
  ALERT_GENERATION_FAILED = 'ALERT_GENERATION_FAILED',
  DEVICE_CONTROL_FAILED = 'DEVICE_CONTROL_FAILED',
  NETWORK_STATUS_UNAVAILABLE = 'NETWORK_STATUS_UNAVAILABLE'
}
```

### 错误处理策略

1. **前端错误处理**
   - 网络请求错误处理
   - 数据加载失败提示
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
   - 组件渲染测试
   - 用户交互测试
   - API调用测试
   - 状态管理测试

2. **后端测试**
   - Service层业务逻辑测试
   - Controller层接口测试
   - Mapper层数据访问测试
   - 监控数据计算测试

### 集成测试

1. **API集成测试**
   - 监控数据查询流程测试
   - 告警生成和推送测试
   - 设备控制操作测试

2. **前后端集成测试**
   - 端到端功能测试
   - 数据一致性测试
   - 实时更新测试

### 性能测试

1. **响应时间测试**
   - API响应时间 < 2秒
   - 大数据量查询性能
   - 并发访问性能

2. **实时性测试**
   - 数据更新延迟测试
   - 告警推送延迟测试

## 部署和监控

### 部署配置

1. **数据库配置**
   - 连接池设置
   - 索引优化
   - 备份策略

2. **应用配置**
   - 监控数据采集间隔
   - 告警阈值配置
   - 日志配置

### 监控指标

1. **业务监控**
   - 设备在线率
   - 告警响应时间
   - 性能数据准确性

2. **技术监控**
   - API响应时间
   - 数据库查询性能
   - 系统资源使用