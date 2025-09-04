# Admin Device - 设备监控模块需求文档

## 🚨 核心需求要求（强制执行）

### ⚠️ 基于现有前端页面的后端API适配规范

**本需求文档基于现有的 DeviceMonitoring.vue 前端页面（位于 `/src/frontend/src/views/admin/device/DeviceMonitoring.vue`），要求后端API完全适配前端页面的数据需求和交互逻辑：**

### 🔍 开发前置检查要求（必须执行）

#### 📋 数据库表名冲突检查结果

**✅ 已完成检查 - 无冲突**

监控模块计划创建的数据库表名与现有表名对比结果：

**计划创建的表名：**
- `device_monitoring_stats` - 设备监控统计表 ✅ 无冲突
- `device_monitoring_data` - 设备监控数据表 ✅ 无冲突  
- `device_alerts` - 设备告警表 ✅ 无冲突
- `device_performance_metrics` - 设备性能指标表 ✅ 无冲突
- `device_network_status` - 设备网络状态表 ✅ 无冲突
- `device_monitoring_alert_relation` - 设备监控告警关联表 ✅ 无冲突
- `device_performance_metric_relation` - 设备性能指标关联表 ✅ 无冲突
- `device_network_status_relation` - 设备网络状态关联表 ✅ 无冲突

**检查命令：**
```bash
# 已执行数据库表名检查
E:\YXRobot\mysql-9.3.0-winx64\bin\mysql.exe -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SHOW TABLES"
```

#### 📋 Java类重复检查结果

**⚠️ 发现冲突 - 需要重命名**

监控模块计划创建的Java类与现有类名对比结果：

**存在冲突的类名（需要重命名）：**
- `ManagedDevicePerformanceMonitorService` ❌ **已存在** - 需重命名为 `DeviceMonitoringService`
- `ManagedDevicePerformanceController` ❌ **已存在** - 需重命名为 `DeviceMonitoringController`
- `ManagedDevicePerformanceService` ❌ **已存在** - 需重命名为 `DevicePerformanceService`

**无冲突的类名（可直接使用）：**
- `DeviceMonitoringStatsService` ✅ 无冲突
- `DeviceAlertService` ✅ 无冲突
- `DeviceNetworkService` ✅ 无冲突
- `DeviceControlService` ✅ 无冲突

**重命名方案：**
```java
// 原计划类名 → 新类名（避免冲突）
ManagedDevicePerformanceMonitorService → DeviceMonitoringService
ManagedDevicePerformanceController → DeviceMonitoringController  
ManagedDevicePerformanceService → DevicePerformanceService
DeviceMonitoringController → DeviceMonitoringController
DeviceAlertController → DeviceAlertController
DevicePerformanceController → DevicePerformanceController
DeviceNetworkController → DeviceNetworkController
DeviceControlController → DeviceControlController
```

**检查命令：**
```bash
# 已执行Java类重复检查
find src/main/java -name "*Device*Performance*.java" | grep -v test
find src/main/java -name "*Device*Monitor*.java" | grep -v test
```



#### 🔥 前端页面分析结果

现有前端页面包含以下核心功能模块：
1. **设备监控页面头部**：包含页面标题、描述
2. **实时统计卡片**：在线设备、离线设备、故障设备、平均性能（带趋势显示）
3. **设备分布地图**：显示全球设备实时状态分布（地图组件）
4. **实时告警列表**：显示设备告警信息，按级别分类
5. **性能监控图表**：设备性能趋势图、网络连接状态图
6. **设备实时状态列表**：包含搜索、筛选、性能指标、网络状态显示

#### 🔥 前端数据绑定要求

1. **API数据源要求**：
   - ✅ **必须提供**：与前端页面完全匹配的后端API接口
   - ✅ **必须支持**：前端页面所有的数据查询和监控需求
   - ❌ **严禁修改**：现有前端页面的数据结构和字段名称
   - ❌ **严禁要求**：前端页面适配后端数据格式

2. **字段映射适配要求**：
   - 后端API响应必须完全匹配前端TypeScript接口定义
   - 数据库字段通过MyBatis映射转换为前端期望的camelCase格式
   - 所有枚举值必须与前端页面的显示逻辑一致
   - 日期格式必须符合前端页面的处理逻辑

#### 🚨 前端页面字段映射分析（强制遵循）

基于现有DeviceMonitoring.vue页面分析，后端必须提供以下字段：

**监控统计核心字段**：
- `online` - 在线设备数量（前端统计卡片显示）
- `offline` - 离线设备数量（前端统计卡片显示）
- `error` - 故障设备数量（前端统计卡片显示）
- `avgPerformance` - 平均性能百分比（前端统计卡片显示）
- `onlineTrend` - 在线设备趋势（前端趋势显示）
- `offlineTrend` - 离线设备趋势（前端趋势显示）
- `errorTrend` - 故障设备趋势（前端趋势显示）
- `performanceTrend` - 性能趋势（前端趋势显示）

**设备监控核心字段**：
- `serialNumber` - 设备序列号（前端表格显示）
- `customerName` - 客户名称（前端表格显示）
- `status` - 设备状态（前端状态标签显示）
- `performance` - 性能指标对象（包含cpu、memory字段）
- `lastOnlineAt` - 最后在线时间（前端格式化显示）
- `model` - 设备型号（前端设备信息显示）
- `firmwareVersion` - 固件版本（前端设备信息显示）

**告警信息核心字段**：
- `id` - 告警ID（前端列表key）
- `level` - 告警级别（'error' | 'warning' | 'info'）
- `message` - 告警消息（前端显示内容）
- `timestamp` - 告警时间（前端时间格式化显示）

## 介绍

设备监控模块是 YXRobot 管理后台的核心设备监控功能，基于现有的 DeviceMonitoring.vue 前端页面提供设备实时监控和状态分析。该模块需要开发完整的后端API支持，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/device/monitoring。

**基于现有前端页面的技术实现要求:**

1. **前端页面功能支持**: 
   - 后端API必须完全支持现有DeviceMonitoring.vue页面的所有功能
   - 包括实时统计卡片、设备分布地图、告警列表、性能图表、设备状态列表等
   - 支持实时数据更新、搜索筛选、性能监控等交互功能

2. **API接口适配**: 
   - 提供与前端页面完全匹配的RESTful API接口
   - 支持前端页面的所有数据查询和监控需求
   - API响应格式必须与前端TypeScript接口定义完全一致

3. **数据库查询优化**: 
   - 后端API从MySQL数据库实时查询数据
   - 支持复杂的关联查询（设备、客户、性能数据、告警信息）
   - 优化查询性能，支持实时监控数据的快速响应

4. **实时监控支持**: 
   - 提供设备状态实时监控数据
   - 提供性能指标实时数据（CPU、内存使用率）
   - 提供告警信息实时推送
   - 支持设备分布地图数据

5. **统计数据计算**: 
   - 实时计算监控统计指标（在线、离线、故障设备数量）
   - 支持趋势分析和变化率计算
   - 支持平均性能指标计算

6. **空数据状态处理**: 
   - 当数据库中没有数据时，API返回空数组而非错误
   - 前端页面已实现空状态显示，后端只需正确返回空数据
   - 支持数据为空时的统计指标显示（显示为0）

**技术架构要求:**
- 后端Spring Boot提供RESTful API接口
- 使用MyBatis进行数据库操作和复杂查询
- 数据传输格式严格遵循前端TypeScript接口定义
- **关键**：后端适配前端，而非前端适配后端
- 确保现有前端页面无需修改即可正常工作

**字段映射实施标准：**
```sql
-- 数据库表字段 (snake_case)
CREATE TABLE device_monitoring_stats (
  id BIGINT PRIMARY KEY,
  online_count INT,
  offline_count INT,
  error_count INT,
  avg_performance DECIMAL(5,2),
  online_trend INT,
  offline_trend INT,
  error_trend INT,
  performance_trend INT
);
```

```java
// Java实体类属性 (camelCase)
public class DeviceMonitoringStats {
  private Long id;
  private Integer onlineCount;
  private Integer offlineCount;
  private Integer errorCount;
  private BigDecimal avgPerformance;
  private Integer onlineTrend;
  private Integer offlineTrend;
  private Integer errorTrend;
  private Integer performanceTrend;
}
```

```xml
<!-- MyBatis映射配置 -->
<result column="online_count" property="onlineCount"/>
<result column="offline_count" property="offlineCount"/>
<result column="error_count" property="errorCount"/>
<result column="avg_performance" property="avgPerformance"/>
<result column="online_trend" property="onlineTrend"/>
<result column="offline_trend" property="offlineTrend"/>
<result column="error_trend" property="errorTrend"/>
<result column="performance_trend" property="performanceTrend"/>
```

```typescript
// 前端TypeScript接口 (camelCase)
interface DeviceMonitoringStats {
  online: number;
  offline: number;
  error: number;
  avgPerformance: number;
  onlineTrend: number;
  offlineTrend: number;
  errorTrend: number;
  performanceTrend: number;
}
```

## 需求

### 需求 1 - 设备监控页面头部功能

**用户故事:** 作为设备监控员，我希望能够查看设备监控页面的头部区域，包括页面标题和描述，以便了解页面功能和监控范围。

#### 验收标准

1. WHEN 用户访问设备监控页面 THEN 系统 SHALL 显示"设备监控"页面标题
2. WHEN 显示页面描述 THEN 系统 SHALL 显示"实时监控设备状态与性能 · 练字机器人管理系统"副标题
3. WHEN 页面加载 THEN 系统 SHALL 使用响应式设计适配不同屏幕尺寸

### 需求 2 - 实时统计卡片功能

**用户故事:** 作为设备监控员，我希望能够在页面顶部看到实时统计卡片，显示在线设备、离线设备、故障设备、平均性能等关键指标，以便快速了解设备整体状态。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示4个监控统计卡片：在线设备、离线设备、故障设备、平均性能
2. WHEN 显示在线设备卡片 THEN 系统 SHALL 显示在线设备数量和绿色勾选图标
3. WHEN 显示离线设备卡片 THEN 系统 SHALL 显示离线设备数量和灰色关闭图标
4. WHEN 显示故障设备卡片 THEN 系统 SHALL 显示故障设备数量和红色警告图标
5. WHEN 显示平均性能卡片 THEN 系统 SHALL 显示平均性能百分比和蓝色数据线图标
6. WHEN 统计数据更新 THEN 系统 SHALL 实时更新所有统计卡片数据
7. WHEN 显示趋势数据 THEN 系统 SHALL 显示每个指标的变化趋势（正数显示+号，负数显示-号）
8. WHEN 数据为空 THEN 系统 SHALL 显示0值而不是错误信息

### 需求 3 - 设备分布地图功能

**用户故事:** 作为设备监控员，我希望能够查看设备分布地图，显示全球设备的实时状态分布，以便了解设备的地理分布情况。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示设备分布地图区域
2. WHEN 显示地图 THEN 系统 SHALL 显示地图占位符和位置图标
3. WHEN 显示地图信息 THEN 系统 SHALL 显示"设备分布地图"标题和"显示全球设备实时状态分布"描述
4. WHEN 用户点击刷新按钮 THEN 系统 SHALL 刷新地图数据并显示提示信息
5. WHEN 地图数据更新 THEN 系统 SHALL 实时更新设备位置和状态信息

### 需求 4 - 实时告警列表功能

**用户故事:** 作为设备监控员，我希望能够查看实时告警列表，显示设备的告警信息，按级别分类显示，以便及时处理设备异常。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示实时告警区域
2. WHEN 显示告警标题 THEN 系统 SHALL 显示"实时告警"标题和告警数量徽章
3. WHEN 有告警信息 THEN 系统 SHALL 显示最多10条告警记录
4. WHEN 显示告警项 THEN 系统 SHALL 显示告警图标、消息内容、时间戳
5. WHEN 告警级别为error THEN 系统 SHALL 显示红色警告图标和红色左边框
6. WHEN 告警级别为warning THEN 系统 SHALL 显示橙色信息图标和橙色左边框
7. WHEN 告警级别为info THEN 系统 SHALL 显示蓝色勾选图标和蓝色左边框
8. WHEN 无告警信息 THEN 系统 SHALL 显示"暂无告警信息"和绿色勾选图标
9. WHEN 告警时间显示 THEN 系统 SHALL 格式化显示相对时间（刚刚、X分钟前、X小时前）

### 需求 5 - 性能监控图表功能

**用户故事:** 作为设备监控员，我希望能够查看性能监控图表，包括设备性能趋势和网络连接状态，以便分析设备性能变化。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示两个图表区域：设备性能趋势、网络连接状态
2. WHEN 显示性能趋势图表 THEN 系统 SHALL 显示"设备性能趋势"标题和数据线图标占位符
3. WHEN 显示网络状态图表 THEN 系统 SHALL 显示"网络连接状态"标题和链接图标占位符
4. WHEN 图表数据加载 THEN 系统 SHALL 支持ECharts图表数据格式
5. WHEN 图表更新 THEN 系统 SHALL 实时更新图表数据显示

### 需求 6 - 设备实时状态列表功能

**用户故事:** 作为设备监控员，我希望能够查看设备实时状态列表，包含搜索筛选、性能指标、网络状态等信息，以便详细监控每个设备的状态。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示设备实时状态表格
2. WHEN 显示表格标题 THEN 系统 SHALL 显示"设备实时状态"标题
3. WHEN 显示搜索功能 THEN 系统 SHALL 提供搜索框支持设备序列号和客户名称搜索
4. WHEN 显示筛选功能 THEN 系统 SHALL 提供状态筛选下拉框（全部、在线、离线、故障）
5. WHEN 显示表格列 THEN 系统 SHALL 显示设备序列号、客户、状态、性能指标、最后在线时间、设备信息、网络状态、操作列
6. WHEN 显示设备状态 THEN 系统 SHALL 使用不同颜色的标签和图标显示状态
7. WHEN 显示性能指标 THEN 系统 SHALL 显示CPU和内存使用率的进度条
8. WHEN 显示设备信息 THEN 系统 SHALL 显示设备型号和固件版本
9. WHEN 显示网络状态 THEN 系统 SHALL 显示信号强度进度条和网络类型标签
10. WHEN 显示操作按钮 THEN 系统 SHALL 提供"查看详情"和"远程控制"按钮
11. WHEN 设备状态为在线 THEN 系统 SHALL 显示"远程控制"按钮
12. WHEN 用户搜索设备 THEN 系统 SHALL 实时筛选匹配的设备记录
13. WHEN 用户筛选状态 THEN 系统 SHALL 显示对应状态的设备记录

### 需求 7 - 设备操作功能

**用户故事:** 作为设备监控员，我希望能够对设备进行操作，包括查看详情和远程控制，以便管理和控制设备。

#### 验收标准

1. WHEN 用户点击"查看详情"按钮 THEN 系统 SHALL 显示设备详情信息
2. WHEN 用户点击"远程控制"按钮 THEN 系统 SHALL 启动设备远程控制功能
3. WHEN 设备状态为离线 THEN 系统 SHALL 隐藏"远程控制"按钮
4. WHEN 操作成功 THEN 系统 SHALL 显示成功提示信息
5. WHEN 操作失败 THEN 系统 SHALL 显示错误提示信息

### 需求 8 - 实时数据更新功能

**用户故事:** 作为设备监控员，我希望监控数据能够实时更新，确保看到的是最新的设备状态信息。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 自动加载最新的监控数据
2. WHEN 数据更新 THEN 系统 SHALL 自动刷新统计卡片数据
3. WHEN 数据更新 THEN 系统 SHALL 自动刷新设备列表数据
4. WHEN 数据更新 THEN 系统 SHALL 自动刷新告警列表数据
5. WHEN 数据加载中 THEN 系统 SHALL 显示加载状态指示器
6. WHEN 数据加载完成 THEN 系统 SHALL 隐藏加载状态并显示数据

### 需求 9 - 响应式设计功能

**用户故事:** 作为设备监控员，我希望系统具有良好的响应式设计，能够在不同设备上正常使用。

#### 验收标准

1. WHEN 页面在大屏幕显示 THEN 系统 SHALL 使用4列网格布局显示统计卡片
2. WHEN 页面在中等屏幕显示 THEN 系统 SHALL 使用2列网格布局显示统计卡片
3. WHEN 页面在小屏幕显示 THEN 系统 SHALL 使用1列布局显示统计卡片
4. WHEN 页面在移动设备显示 THEN 系统 SHALL 调整页面头部为垂直布局
5. WHEN 表格在小屏幕显示 THEN 系统 SHALL 保持表格的可滚动性

### 需求 10 - 性能指标显示功能

**用户故事:** 作为设备监控员，我希望能够查看详细的设备性能指标，包括CPU和内存使用率，以便监控设备性能状态。

#### 验收标准

1. WHEN 显示性能指标 THEN 系统 SHALL 显示CPU使用率进度条
2. WHEN 显示性能指标 THEN 系统 SHALL 显示内存使用率进度条
3. WHEN CPU使用率>=80% THEN 系统 SHALL 使用红色进度条显示
4. WHEN CPU使用率>=60%且<80% THEN 系统 SHALL 使用橙色进度条显示
5. WHEN CPU使用率<60% THEN 系统 SHALL 使用绿色进度条显示
6. WHEN 内存使用率>=80% THEN 系统 SHALL 使用红色进度条显示
7. WHEN 内存使用率>=60%且<80% THEN 系统 SHALL 使用橙色进度条显示
8. WHEN 内存使用率<60% THEN 系统 SHALL 使用绿色进度条显示
9. WHEN 显示性能数值 THEN 系统 SHALL 显示具体的百分比数值

### 需求 11 - 网络状态监控功能

**用户故事:** 作为设备监控员，我希望能够监控设备的网络连接状态，包括信号强度和网络类型，以便了解设备的网络连接情况。

#### 验收标准

1. WHEN 显示网络状态 THEN 系统 SHALL 显示信号强度进度条
2. WHEN 显示网络状态 THEN 系统 SHALL 显示网络类型标签（4G、5G、WiFi）
3. WHEN 信号强度良好 THEN 系统 SHALL 使用绿色进度条显示
4. WHEN 网络类型显示 THEN 系统 SHALL 使用信息类型标签显示
5. WHEN 网络状态更新 THEN 系统 SHALL 实时更新网络连接信息

### 需求 12 - 数据格式和字段映射

**用户故事:** 作为系统集成者，我希望确保前后端数据格式完全匹配，字段映射正确，数据类型一致，以便前端页面能够正确显示和处理数据。

#### 验收标准

1. WHEN 后端返回监控统计数据 THEN 字段名称 SHALL 使用camelCase格式（如online、offline、avgPerformance）
2. WHEN 后端返回设备数据 THEN 字段名称 SHALL 使用camelCase格式（如serialNumber、customerName、lastOnlineAt）
3. WHEN 后端返回告警数据 THEN 字段名称 SHALL 使用camelCase格式（如id、level、message、timestamp）
4. WHEN 后端返回日期数据 THEN 格式 SHALL 为ISO 8601字符串格式
5. WHEN 后端返回枚举数据 THEN 值 SHALL 与前端页面的枚举值完全匹配
6. WHEN 后端返回性能数据 THEN 结构 SHALL 包含cpu、memory字段的对象格式

### 需求 13 - 数据库表名冲突检查和Java类重复检查

**用户故事:** 作为系统开发者，我希望在开发监控模块前检查数据库表名和Java类名是否与现有系统重复，确保系统稳定性和避免冲突。

#### 验收标准

1. WHEN 开发新模块前 THEN 系统开发者 SHALL 检查所有计划创建的数据库表名是否与现有表名重复
2. WHEN 发现数据库表名冲突 THEN 系统开发者 SHALL 保留现有表不删除，为新表重新命名
3. WHEN 开发新功能前 THEN 系统开发者 SHALL 检查所有计划创建的Java类名是否与现有类名重复
4. WHEN 发现Java类名冲突 THEN 系统开发者 SHALL 保留现有类不删除，为新类重新命名
5. WHEN 完成冲突检查 THEN 系统开发者 SHALL 更新所有相关文档中的表名和类名引用
6. WHEN 重命名后 THEN 系统开发者 SHALL 确保新的命名符合项目命名规范
7. WHEN 创建关联表 THEN 系统开发者 SHALL 遵循关联表设计规范，禁止使用外键约束
8. WHEN 设计新类 THEN 系统开发者 SHALL 使用模块特定的前缀避免与其他模块冲突

### 需求 14 - 错误处理和异常情况

**用户故事:** 作为设备监控员，我希望系统能够正确处理各种错误情况，提供友好的错误提示。

#### 验收标准

1. WHEN API调用失败 THEN 系统 SHALL 显示友好的错误提示信息
2. WHEN 网络连接异常 THEN 系统 SHALL 显示网络错误提示
3. WHEN 数据加载超时 THEN 系统 SHALL 显示超时提示并提供重试选项
4. WHEN 设备操作失败 THEN 系统 SHALL 显示具体的失败原因
5. WHEN 系统异常 THEN 系统 SHALL 记录错误日志并显示通用错误提示