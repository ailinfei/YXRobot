# Admin Business - 租赁数据分析模块需求文档

## 🚨 核心需求要求（强制执行）

### ⚠️ 基于现有前端页面的后端API适配规范

**本需求文档基于现有的 RentalAnalytics.vue 前端页面（位于 `/src/frontend/src/views/admin/business/RentalAnalytics.vue`），要求后端API完全适配前端页面的数据需求和交互逻辑：**

#### 🔥 前端页面分析结果

现有前端页面包含以下核心功能模块：
1. **租赁数据分析页面头部**：包含页面标题、描述信息
2. **核心指标卡片**：租赁总收入、租赁设备数、设备利用率、平均租期（带增长率显示）
3. **图表分析区域**：租赁趋势分析、设备利用率排行、地区分布、设备型号分析
4. **设备利用率详情表格**：包含搜索、筛选、分页、查看详情等功能
5. **右侧信息面板**：今日概览、设备状态分布、利用率TOP5排行
6. **设备详情弹窗**：显示完整的设备详细信息

#### 🔥 前端数据绑定要求

1. **API数据源要求**：
   - ✅ **必须提供**：与前端页面完全匹配的后端API接口
   - ✅ **必须支持**：前端页面所有的数据查询和操作需求
   - ❌ **严禁修改**：现有前端页面的数据结构和字段名称
   - ❌ **严禁要求**：前端页面适配后端数据格式

2. **字段映射适配要求**：
   - 后端API响应必须完全匹配前端TypeScript接口定义
   - 数据库字段通过MyBatis映射转换为前端期望的camelCase格式
   - 所有枚举值必须与前端页面的显示逻辑一致
   - 日期格式必须符合前端页面的处理逻辑

#### 🚨 前端页面字段映射分析（强制遵循）

基于现有RentalAnalytics.vue页面分析，后端必须提供以下字段：

**租赁统计核心字段**：
- `totalRentalRevenue` - 租赁总收入（前端用于收入卡片显示）
- `totalRentalDevices` - 租赁设备总数（前端显示在设备数卡片）
- `activeRentalDevices` - 活跃租赁设备数（前端计算利用率）
- `deviceUtilizationRate` - 设备利用率（前端显示在利用率卡片）
- `averageRentalPeriod` - 平均租期（前端显示在租期卡片）
- `totalRentalOrders` - 租赁订单总数（前端用于统计计算）
- `revenueGrowthRate` - 收入增长率（前端显示增长趋势）
- `deviceGrowthRate` - 设备增长率（前端显示设备增长趋势）

**设备利用率核心字段**：
- `deviceId` - 设备ID（前端用于设备标识显示）
- `deviceModel` - 设备型号（前端显示在型号标签）
- `utilizationRate` - 利用率（前端显示在进度条和排行）
- `totalRentalDays` - 租赁天数（前端显示在表格）
- `totalAvailableDays` - 可用天数（前端显示在表格）
- `currentStatus` - 当前状态（前端用于状态标签显示）
- `lastRentalDate` - 最后租赁日期（前端显示在表格）
- `region` - 所在地区（前端用于地区分布分析）
- `performanceScore` - 性能评分（前端详情页显示）
- `signalStrength` - 信号强度（前端详情页显示）
- `maintenanceStatus` - 维护状态（前端详情页显示）

## 介绍

租赁数据分析模块是 YXRobot 管理后台的核心业务分析功能，基于现有的 RentalAnalytics.vue 前端页面提供租赁业务数据的全面分析和管理。该模块需要开发完整的后端API支持，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/rental-analytics。

**基于现有前端页面的技术实现要求:**

1. **前端页面功能支持**: 
   - 后端API必须完全支持现有RentalAnalytics.vue页面的所有功能
   - 包括核心指标卡片、图表分析、设备利用率表格、详情查看等
   - 支持时间周期筛选、设备型号筛选、状态筛选、分页等交互功能

2. **API接口适配**: 
   - 提供与前端页面完全匹配的RESTful API接口
   - 支持前端页面的所有数据查询和操作需求
   - API响应格式必须与前端TypeScript接口定义完全一致

3. **数据库查询优化**: 
   - 后端API从MySQL数据库实时查询数据
   - 支持复杂的关联查询（租赁记录、设备信息、客户信息）
   - 优化查询性能，支持大数据量的分页和筛选

4. **图表数据支持**: 
   - 提供租赁趋势分析数据（支持ECharts格式）
   - 提供设备利用率排行数据（柱状图格式）
   - 提供地区分布数据（饼图格式）
   - 提供设备型号分析数据（混合图表格式）

5. **统计数据计算**: 
   - 实时计算租赁统计指标（总收入、设备数、利用率、平均租期）
   - 支持增长率计算和趋势分析
   - 支持按时间周期的动态统计

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
CREATE TABLE rental_records (
  id BIGINT PRIMARY KEY,
  rental_revenue DECIMAL(10,2),
  rental_start_date DATE,
  device_id BIGINT
);
```

```java
// Java实体类属性 (camelCase)
public class RentalRecord {
  private Long id;
  private BigDecimal rentalRevenue;
  private LocalDate rentalStartDate;
  private Long deviceId;
}
```

```xml
<!-- MyBatis映射配置 -->
<result column="rental_revenue" property="rentalRevenue"/>
<result column="rental_start_date" property="rentalStartDate"/>
<result column="device_id" property="deviceId"/>
```

```typescript
// 前端TypeScript接口 (camelCase)
interface RentalRecord {
  id: number;
  rentalRevenue: number;
  rentalStartDate: string;
  deviceId: number;
}
```

## 需求

### 需求 1 - 租赁数据分析页面头部功能

**用户故事:** 作为租赁管理员，我希望能够查看租赁数据分析页面的头部区域，包括页面标题和描述信息，以便了解当前页面的功能和用途。

#### 验收标准

1. WHEN 用户访问租赁数据分析页面 THEN 系统 SHALL 显示"租赁数据分析"页面标题
2. WHEN 显示页面描述 THEN 系统 SHALL 显示"租赁业务数据分析 · 练字机器人管理系统"副标题
3. WHEN 页面加载 THEN 系统 SHALL 正确显示页面布局和样式
4. WHEN 页面响应式设计 THEN 系统 SHALL 在不同设备上正确显示头部信息

### 需求 2 - 核心指标卡片统计功能

**用户故事:** 作为租赁管理员，我希望能够在页面顶部看到核心指标卡片，显示租赁总收入、租赁设备数、设备利用率、平均租期等关键指标，并显示增长率趋势，以便快速了解租赁业务概况。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示4个核心指标卡片：租赁总收入、租赁设备数、设备利用率、平均租期
2. WHEN 显示租赁总收入卡片 THEN 系统 SHALL 显示货币格式的总收入金额和增长率（带颜色指示）
3. WHEN 显示租赁设备数卡片 THEN 系统 SHALL 显示设备数量和增长率趋势
4. WHEN 显示设备利用率卡片 THEN 系统 SHALL 显示利用率百分比和增长率
5. WHEN 显示平均租期卡片 THEN 系统 SHALL 显示平均租期天数和增长趋势
6. WHEN 使用CountUp动画组件 THEN 系统 SHALL 显示数字动画效果
7. WHEN 数据为空 THEN 系统 SHALL 显示0值而不是错误信息

### 需求 3 - 图表分析区域功能

**用户故事:** 作为租赁管理员，我希望能够查看多种图表分析，包括租赁趋势分析、设备利用率排行、地区分布、设备型号分析，以便直观了解租赁数据的各个维度。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示4个图表容器：租赁趋势分析、设备利用率排行、地区分布、设备型号分析
2. WHEN 显示租赁趋势图 THEN 系统 SHALL 使用ECharts显示租赁收入、订单数量、设备利用率的混合图表
3. WHEN 显示设备利用率排行 THEN 系统 SHALL 使用水平柱状图显示TOP设备利用率排行
4. WHEN 显示地区分布 THEN 系统 SHALL 使用饼图显示各地区的租赁业务分布
5. WHEN 显示设备型号分析 THEN 系统 SHALL 使用混合图表显示各型号的租赁数量和收入贡献
6. WHEN 用户选择不同时间周期 THEN 系统 SHALL 重新加载趋势图表数据
7. WHEN 图表数据为空 THEN 系统 SHALL 显示空图表而不是错误信息
8. WHEN 图表支持交互 THEN 系统 SHALL 提供tooltip提示和图例控制

### 需求 4 - 设备利用率详情表格功能

**用户故事:** 作为租赁管理员，我希望能够查看设备利用率详情表格，包含完整的设备信息、利用率数据、状态信息，并支持搜索、筛选、分页等功能，以便高效管理设备数据。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示设备利用率数据表格，包含设备ID、设备型号、利用率、租赁天数、可用天数、当前状态、最后租赁日期、操作按钮
2. WHEN 显示设备型号 THEN 系统 SHALL 使用不同颜色的标签显示：YX-Robot-Pro、YX-Robot-Standard、YX-Robot-Lite、YX-Robot-Mini
3. WHEN 显示利用率 THEN 系统 SHALL 显示百分比数值和进度条（带颜色指示）
4. WHEN 显示当前状态 THEN 系统 SHALL 使用不同颜色的标签显示：运行中、空闲、维护中
5. WHEN 用户选择设备型号筛选 THEN 系统 SHALL 按型号筛选设备数据
6. WHEN 用户选择状态筛选 THEN 系统 SHALL 按状态筛选设备数据
7. WHEN 用户输入搜索关键词 THEN 系统 SHALL 搜索设备ID或型号并实时更新列表
8. WHEN 数据为空 THEN 系统 SHALL 显示空状态页面
9. WHEN 支持分页显示 THEN 系统 SHALL 提供分页控制和页面大小选择

### 需求 5 - 设备详情查看功能

**用户故事:** 作为租赁管理员，我希望能够查看设备的详细信息，包括基本信息、性能指标、维护状态等，以便全面了解设备的详细情况。

#### 验收标准

1. WHEN 用户点击"查看详情"按钮 THEN 系统 SHALL 打开设备详情对话框
2. WHEN 显示设备详情 THEN 系统 SHALL 显示设备ID、型号、利用率、性能评分、信号强度、维护状态等信息
3. WHEN 显示性能指标 THEN 系统 SHALL 使用进度条或图表形式展示性能数据
4. WHEN 显示维护状态 THEN 系统 SHALL 使用状态标签显示：正常、警告、紧急
5. WHEN 用户关闭对话框 THEN 系统 SHALL 关闭详情对话框并返回列表页面

### 需求 6 - 右侧信息面板功能

**用户故事:** 作为租赁管理员，我希望能够在右侧看到信息面板，显示今日概览、设备状态分布、利用率TOP5排行等实时信息，以便快速掌握当前业务状态。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示右侧信息面板，包含今日概览、设备状态、利用率TOP5三个卡片
2. WHEN 显示今日概览 THEN 系统 SHALL 显示今日收入、新增订单、活跃设备、平均利用率
3. WHEN 显示设备状态 THEN 系统 SHALL 显示运行中、空闲、维护中设备的数量统计
4. WHEN 显示利用率TOP5 THEN 系统 SHALL 显示利用率最高的5台设备排行
5. WHEN 数据实时更新 THEN 系统 SHALL 定期刷新面板数据
6. WHEN 面板响应式设计 THEN 系统 SHALL 在不同屏幕尺寸下正确显示

### 需求 7 - 时间周期筛选功能

**用户故事:** 作为租赁管理员，我希望能够选择不同的时间周期来查看租赁趋势数据，包括最近7天、最近4周、最近12月、最近4季度，以便分析不同时间维度的业务趋势。

#### 验收标准

1. WHEN 显示租赁趋势图表 THEN 系统 SHALL 提供时间周期选择器：最近7天、最近4周、最近12月、最近4季度
2. WHEN 用户选择"最近7天" THEN 系统 SHALL 显示按天统计的趋势数据
3. WHEN 用户选择"最近4周" THEN 系统 SHALL 显示按周统计的趋势数据
4. WHEN 用户选择"最近12月" THEN 系统 SHALL 显示按月统计的趋势数据
5. WHEN 用户选择"最近4季度" THEN 系统 SHALL 显示按季度统计的趋势数据
6. WHEN 切换时间周期 THEN 系统 SHALL 重新加载对应周期的图表数据
7. WHEN 数据加载中 THEN 系统 SHALL 显示加载状态指示器

### 需求 8 - 分页和数据加载功能

**用户故事:** 作为租赁管理员，我希望设备利用率表格支持分页功能，能够处理大量设备数据并提供灵活的分页控制，以便高效浏览和管理设备记录。

#### 验收标准

1. WHEN 显示设备利用率表格 THEN 系统 SHALL 支持分页显示，默认每页20条记录
2. WHEN 用户切换页码 THEN 系统 SHALL 加载对应页面的设备数据
3. WHEN 分页信息显示 THEN 系统 SHALL 显示总记录数、当前页码、总页数等信息
4. WHEN 用户选择页面大小 THEN 系统 SHALL 支持10、20、50、100条记录选择
5. WHEN 筛选条件变更 THEN 系统 SHALL 重置分页到第一页
6. WHEN 数据加载中 THEN 系统 SHALL 显示加载状态指示器
7. WHEN 数据加载完成 THEN 系统 SHALL 隐藏加载状态并显示数据

### 需求 9 - 响应式设计和用户体验

**用户故事:** 作为租赁管理员，我希望系统具有良好的用户体验，包括响应式设计、加载状态、错误处理等，以便在不同设备上都能正常使用。

#### 验收标准

1. WHEN 页面在移动设备显示 THEN 系统 SHALL 自动适配屏幕尺寸并调整布局
2. WHEN 数据加载中 THEN 系统 SHALL 显示加载动画和状态提示
3. WHEN 操作执行中 THEN 系统 SHALL 显示相应的处理状态和进度提示
4. WHEN 操作成功 THEN 系统 SHALL 显示成功提示信息并自动刷新相关数据
5. WHEN 操作失败 THEN 系统 SHALL 显示友好的错误提示信息
6. WHEN 网络请求失败 THEN 系统 SHALL 在控制台记录错误但不影响用户体验
7. WHEN 图表渲染 THEN 系统 SHALL 确保图表在窗口大小变化时自动调整

### 需求 10 - 后端API接口完整性

**用户故事:** 作为前端开发者，我希望后端提供完整的API接口支持，确保前端页面的所有功能都能正常工作，包括数据查询、统计计算、图表数据等。

#### 验收标准

1. WHEN 前端调用租赁统计API THEN 后端 SHALL 返回租赁总收入、设备数、利用率、平均租期等统计数据
2. WHEN 前端调用设备利用率列表API THEN 后端 SHALL 支持分页、搜索、筛选等查询参数
3. WHEN 前端调用图表数据API THEN 后端 SHALL 返回ECharts格式的图表数据
4. WHEN 前端调用趋势数据API THEN 后端 SHALL 支持不同时间周期的数据查询
5. WHEN 前端调用任何API THEN 后端 SHALL 返回统一格式的响应（包含code、data、message字段）
6. WHEN API查询无数据 THEN 后端 SHALL 返回空数组而不是错误信息

### 需求 11 - 数据格式和字段映射

**用户故事:** 作为系统集成者，我希望确保前后端数据格式完全匹配，字段映射正确，数据类型一致，以便前端页面能够正确显示和处理数据。

#### 验收标准

1. WHEN 后端返回租赁统计数据 THEN 字段名称 SHALL 使用camelCase格式（如totalRentalRevenue、deviceUtilizationRate）
2. WHEN 后端返回日期数据 THEN 格式 SHALL 为YYYY-MM-DD字符串格式
3. WHEN 后端返回金额数据 THEN 类型 SHALL 为数字类型，支持小数点后两位
4. WHEN 后端返回枚举数据 THEN 值 SHALL 与前端页面的枚举值完全匹配
5. WHEN 后端返回分页数据 THEN 结构 SHALL 包含list、total、page、pageSize字段
6. WHEN 后端返回图表数据 THEN 格式 SHALL 符合ECharts的数据结构要求

### 需求 12 - 性能和稳定性

**用户故事:** 作为租赁管理员，我希望系统具有良好的性能和稳定性，能够快速响应查询请求，稳定处理大量数据，以便高效完成日常工作。

#### 验收标准

1. WHEN 用户加载租赁数据分析页面 THEN 系统 SHALL 在2秒内返回响应
2. WHEN 用户切换时间周期 THEN 系统 SHALL 在3秒内更新所有数据（统计、图表）
3. WHEN 系统处理大量设备数据 THEN 分页查询 SHALL 保持良好性能
4. WHEN 多个用户同时访问 THEN 系统 SHALL 保持稳定响应
5. WHEN 数据库连接异常 THEN 系统 SHALL 优雅处理错误并提供友好提示
6. WHEN API请求失败 THEN 前端 SHALL 在控制台记录错误但不影响用户界面