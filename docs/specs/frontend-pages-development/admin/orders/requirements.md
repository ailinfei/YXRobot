# Admin Business - 订单管理模块需求文档

## 🚨 核心需求要求（强制执行）

### ⚠️ 基于现有前端页面的后端API适配规范

**本需求文档基于现有的 Orders.vue 和 OrderManagement.vue 前端页面（位于 `/src/frontend/src/views/admin/Orders.vue` 和 `/src/frontend/src/views/admin/business/OrderManagement.vue`），要求后端API完全适配前端页面的数据需求和交互逻辑：**

#### 🔥 前端页面分析结果

现有前端页面包含以下核心功能模块：
1. **订单管理页面头部**：包含页面标题、描述、新建订单按钮
2. **订单统计卡片**：订单总数、总收入、处理中订单、已完成订单（带金额显示）
3. **搜索和筛选区域**：关键词搜索、订单类型筛选、订单状态筛选、日期范围筛选
4. **订单列表表格**：包含完整的订单信息、客户信息、商品信息、金额信息、状态信息、操作按钮
5. **订单操作功能**：查看详情、编辑、状态变更（确认、发货、完成、取消）、删除
6. **批量操作功能**：批量状态变更、批量删除
7. **分页功能**：支持分页显示和页面大小调整

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

基于现有Orders.vue和OrderManagement.vue页面分析，后端必须提供以下字段：

**订单记录核心字段**：
- `id` - 订单ID（前端用于唯一标识）
- `orderNumber` - 订单号（前端用于链接显示和搜索）
- `type` - 订单类型（'sales' | 'rental'，前端用于标签显示）
- `status` - 订单状态（前端用于状态标签显示和操作控制）
- `customerName` - 客户名称（前端显示在客户信息区域）
- `customerPhone` - 客户电话（前端显示在客户信息区域）
- `customerEmail` - 客户邮箱（前端详情页显示）
- `deliveryAddress` - 配送地址（前端详情页显示）
- `items` - 订单商品列表（前端显示商品信息）
- `totalAmount` - 订单总金额（前端显示在金额信息区域）
- `currency` - 货币类型（前端金额显示）
- `paymentStatus` - 付款状态（前端用于付款状态标签显示）
- `paymentMethod` - 付款方式（前端详情页显示）
- `createdAt` - 创建时间（前端表格显示）
- `expectedDeliveryDate` - 预期交付日期（前端详情页显示）
- `salesPerson` - 销售人员（前端详情页显示）
- `notes` - 备注信息（前端详情页显示）

**租赁订单特殊字段**：
- `rentalStartDate` - 租赁开始日期（前端租赁信息显示）
- `rentalEndDate` - 租赁结束日期（前端租赁信息显示）
- `rentalDays` - 租赁天数（前端租赁信息显示）
- `rentalNotes` - 租赁备注（前端详情页显示）

**物流信息字段**：
- `shippingInfo` - 物流信息对象（包含物流公司、运单号、发货时间、送达时间）

## 介绍

订单管理模块是 YXRobot 管理后台的核心业务管理功能，基于现有的 Orders.vue 和 OrderManagement.vue 前端页面提供订单数据的全面管理。该模块需要开发完整的后端API支持，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/orders。

**基于现有前端页面的技术实现要求:**

1. **前端页面功能支持**: 
   - 后端API必须完全支持现有Orders.vue和OrderManagement.vue页面的所有功能
   - 包括订单统计卡片、搜索筛选、订单列表、详情查看、状态管理等
   - 支持销售订单和租赁订单的统一管理

2. **API接口适配**: 
   - 提供与前端页面完全匹配的RESTful API接口
   - 支持前端页面的所有数据查询和操作需求
   - API响应格式必须与前端TypeScript接口定义完全一致

3. **数据库查询优化**: 
   - 后端API从MySQL数据库实时查询数据
   - 支持复杂的关联查询（订单、客户、产品、销售人员）
   - 优化查询性能，支持大数据量的分页和筛选

4. **订单状态管理**: 
   - 支持完整的订单状态流转（待确认→已确认→处理中→已发货→已送达→已完成）
   - 支持订单取消和删除操作
   - 支持批量状态变更操作

5. **统计数据计算**: 
   - 实时计算订单统计指标（总订单数、总收入、处理中订单、已完成订单）
   - 支持按订单类型分别统计（销售订单、租赁订单）
   - 支持按日期范围的动态统计

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
CREATE TABLE orders (
  id BIGINT PRIMARY KEY,
  order_number VARCHAR(50),
  total_amount DECIMAL(10,2),
  created_at DATETIME,
  customer_id BIGINT
);
```

```java
// Java实体类属性 (camelCase)
public class Order {
  private Long id;
  private String orderNumber;
  private BigDecimal totalAmount;
  private LocalDateTime createdAt;
  private Long customerId;
}
```

```xml
<!-- MyBatis映射配置 -->
<result column="order_number" property="orderNumber"/>
<result column="total_amount" property="totalAmount"/>
<result column="created_at" property="createdAt"/>
<result column="customer_id" property="customerId"/>
```

```typescript
// 前端TypeScript接口 (camelCase)
interface Order {
  id: string;
  orderNumber: string;
  totalAmount: number;
  createdAt: string;
  customerId: string;
}
```

## 需求

### 需求 1 - 订单管理页面头部功能

**用户故事:** 作为订单管理员，我希望能够查看订单管理页面的头部区域，包括页面标题、描述和新建订单按钮，以便了解页面功能和创建新订单。

#### 验收标准

1. WHEN 用户访问订单管理页面 THEN 系统 SHALL 显示"订单管理"页面标题
2. WHEN 显示页面描述 THEN 系统 SHALL 显示"销售和租赁订单管理 · 练字机器人管理系统"副标题
3. WHEN 页面加载 THEN 系统 SHALL 在页面右上角显示"新建订单"按钮
4. WHEN 用户点击新建订单按钮 THEN 系统 SHALL 打开订单创建对话框
5. WHEN 页面头部显示 THEN 系统 SHALL 使用响应式设计适配不同屏幕尺寸

### 需求 2 - 订单统计卡片功能

**用户故事:** 作为订单管理员，我希望能够在页面顶部看到订单统计卡片，显示订单总数、总收入、处理中订单、已完成订单等关键指标，以便快速了解订单业务概况。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示4个订单统计卡片：订单总数、总收入、处理中、已完成
2. WHEN 显示订单总数卡片 THEN 系统 SHALL 显示订单总数量和购物车图标
3. WHEN 显示总收入卡片 THEN 系统 SHALL 显示货币格式的总收入金额和货币图标
4. WHEN 显示处理中卡片 THEN 系统 SHALL 显示处理中订单数量和时钟图标
5. WHEN 显示已完成卡片 THEN 系统 SHALL 显示已完成订单数量和完成图标
6. WHEN 统计数据更新 THEN 系统 SHALL 实时更新所有统计卡片数据
7. WHEN 数据为空 THEN 系统 SHALL 显示0值而不是错误信息

### 需求 3 - 搜索和筛选功能

**用户故事:** 作为订单管理员，我希望能够通过多种条件搜索和筛选订单，包括关键词搜索、订单类型筛选、订单状态筛选、日期范围筛选，以便快速找到目标订单。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示搜索筛选区域，包含搜索框和筛选下拉框
2. WHEN 用户输入搜索关键词 THEN 系统 SHALL 搜索订单号或客户名称并实时更新列表
3. WHEN 用户选择订单类型筛选 THEN 系统 SHALL 按类型筛选订单（销售订单、租赁订单）
4. WHEN 用户选择订单状态筛选 THEN 系统 SHALL 按状态筛选订单（待处理、处理中、已完成、已取消）
5. WHEN 用户选择日期范围 THEN 系统 SHALL 按创建日期范围筛选订单
6. WHEN 用户点击搜索按钮 THEN 系统 SHALL 应用所有筛选条件并更新订单列表
7. WHEN 用户点击重置按钮 THEN 系统 SHALL 清空所有筛选条件并重新加载订单列表

### 需求 4 - 订单列表表格功能

**用户故事:** 作为订单管理员，我希望能够查看订单列表表格，包含完整的订单信息、客户信息、商品信息、金额信息和状态信息，并支持选择、排序等功能，以便高效管理订单数据。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示订单数据表格，包含选择框、订单号、订单类型、客户信息、订单商品、订单金额、订单状态、支付状态、创建时间、操作按钮
2. WHEN 显示客户信息 THEN 系统 SHALL 显示客户名称和客户电话（分两行显示）
3. WHEN 显示订单商品 THEN 系统 SHALL 显示商品名称和数量信息，超过2个商品时显示"+N个商品"
4. WHEN 显示订单金额 THEN 系统 SHALL 显示货币格式化的订单总金额和货币类型
5. WHEN 显示订单状态 THEN 系统 SHALL 使用不同颜色的标签显示：待确认、已确认、处理中、已发货、已送达、已完成、已取消
6. WHEN 显示支付状态 THEN 系统 SHALL 使用不同颜色的标签显示：待支付、已支付、支付失败、已退款
7. WHEN 显示租赁订单 THEN 系统 SHALL 额外显示租赁信息列（租赁天数、租赁日期范围）
8. WHEN 用户选择订单 THEN 系统 SHALL 支持单选和多选功能
9. WHEN 数据为空 THEN 系统 SHALL 显示空状态页面

### 需求 5 - 订单操作功能

**用户故事:** 作为订单管理员，我希望能够对订单进行各种操作，包括查看详情、编辑、状态变更（确认、发货、完成、取消）、删除等，以便灵活管理订单状态。

#### 验收标准

1. WHEN 用户点击"查看详情"按钮 THEN 系统 SHALL 打开订单详情对话框
2. WHEN 用户点击"编辑"按钮 THEN 系统 SHALL 打开订单编辑对话框
3. WHEN 用户点击"更多"下拉菜单 THEN 系统 SHALL 显示可用的状态操作选项
4. WHEN 订单状态为"待确认" THEN 系统 SHALL 显示"确认订单"操作选项
5. WHEN 订单状态为"已确认" THEN 系统 SHALL 显示"发货"操作选项
6. WHEN 订单状态为"已送达" THEN 系统 SHALL 显示"完成订单"操作选项
7. WHEN 订单状态为"待确认"或"已确认" THEN 系统 SHALL 显示"取消订单"操作选项
8. WHEN 用户执行状态变更操作 THEN 系统 SHALL 显示确认对话框并执行相应操作
9. WHEN 用户点击"删除订单"选项 THEN 系统 SHALL 显示确认对话框，确认后执行删除操作
10. WHEN 操作成功 THEN 系统 SHALL 显示成功消息并刷新订单列表
11. WHEN 操作失败 THEN 系统 SHALL 显示错误消息

### 需求 6 - 批量操作功能

**用户故事:** 作为订单管理员，我希望能够对多个订单进行批量操作，包括批量状态变更、批量删除等，以便提高工作效率。

#### 验收标准

1. WHEN 用户选择多个订单 THEN 系统 SHALL 显示批量操作区域
2. WHEN 显示批量操作区域 THEN 系统 SHALL 显示已选择订单数量和批量操作按钮
3. WHEN 用户点击"批量确认"按钮 THEN 系统 SHALL 对所有选中的待确认订单执行确认操作
4. WHEN 用户点击"批量发货"按钮 THEN 系统 SHALL 对所有选中的已确认订单执行发货操作
5. WHEN 用户点击"批量取消"按钮 THEN 系统 SHALL 对所有选中的可取消订单执行取消操作
6. WHEN 用户点击"批量删除"按钮 THEN 系统 SHALL 显示确认对话框，确认后删除所有选中订单
7. WHEN 批量操作成功 THEN 系统 SHALL 显示成功消息并刷新订单列表
8. WHEN 批量操作失败 THEN 系统 SHALL 显示错误消息
9. WHEN 用户点击"取消选择"按钮 THEN 系统 SHALL 清空所有选择并隐藏批量操作区域

### 需求 7 - 订单详情对话框功能

**用户故事:** 作为订单管理员，我希望能够查看订单的完整详细信息，包括基本信息、客户信息、商品信息、金额信息、支付信息、物流信息、租赁信息（如适用）等，以便全面了解订单情况。

#### 验收标准

1. WHEN 用户打开订单详情 THEN 系统 SHALL 显示详情对话框，标题包含订单号
2. WHEN 显示基本信息 THEN 系统 SHALL 显示订单号、订单类型、订单状态、创建时间、销售人员
3. WHEN 显示客户信息 THEN 系统 SHALL 显示客户名称、联系电话、邮箱地址、配送地址
4. WHEN 显示商品信息 THEN 系统 SHALL 显示商品列表，包含商品名称、数量、单价、小计
5. WHEN 显示金额信息 THEN 系统 SHALL 显示小计、运费、折扣、订单总金额
6. WHEN 显示支付信息 THEN 系统 SHALL 显示支付状态、支付方式、支付时间
7. WHEN 显示物流信息 THEN 系统 SHALL 显示物流公司、运单号、发货时间、送达时间
8. WHEN 订单为租赁订单 THEN 系统 SHALL 额外显示租赁开始日期、结束日期、租赁天数、租赁备注
9. WHEN 有备注信息 THEN 系统 SHALL 显示订单备注
10. WHEN 用户关闭对话框 THEN 系统 SHALL 关闭详情对话框并返回列表页面

### 需求 8 - 分页和数据加载功能

**用户故事:** 作为订单管理员，我希望订单列表支持分页功能，能够处理大量订单数据并提供灵活的分页控制，以便高效浏览和管理订单。

#### 验收标准

1. WHEN 显示订单列表 THEN 系统 SHALL 支持分页显示，默认每页10条记录
2. WHEN 用户切换页码 THEN 系统 SHALL 加载对应页面的订单数据
3. WHEN 分页信息显示 THEN 系统 SHALL 显示总记录数、当前页码、总页数等信息
4. WHEN 用户选择页面大小 THEN 系统 SHALL 支持10、20、50、100条记录选项
5. WHEN 筛选条件变更 THEN 系统 SHALL 重置分页到第一页
6. WHEN 数据加载中 THEN 系统 SHALL 显示加载状态指示器
7. WHEN 数据加载完成 THEN 系统 SHALL 隐藏加载状态并显示数据

### 需求 9 - 订单统计数据功能

**用户故事:** 作为订单管理员，我希望能够查看详细的订单统计数据，包括销售订单和租赁订单的分别统计，以便了解不同类型订单的业务表现。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 计算并显示订单总数统计
2. WHEN 计算总收入 THEN 系统 SHALL 统计所有已完成订单的总金额
3. WHEN 计算处理中订单 THEN 系统 SHALL 统计状态为"已确认"、"处理中"、"已发货"的订单数量
4. WHEN 计算已完成订单 THEN 系统 SHALL 统计状态为"已完成"的订单数量
5. WHEN 显示销售订单统计 THEN 系统 SHALL 分别统计销售订单数量和金额
6. WHEN 显示租赁订单统计 THEN 系统 SHALL 分别统计租赁订单数量和金额
7. WHEN 统计数据更新 THEN 系统 SHALL 实时重新计算所有统计指标

### 需求 10 - 响应式设计和用户体验

**用户故事:** 作为订单管理员，我希望系统具有良好的用户体验，包括响应式设计、加载状态、错误处理等，以便在不同设备上都能正常使用。

#### 验收标准

1. WHEN 页面在移动设备显示 THEN 系统 SHALL 自动适配屏幕尺寸并调整布局
2. WHEN 数据加载中 THEN 系统 SHALL 显示加载动画和状态提示
3. WHEN 操作执行中 THEN 系统 SHALL 显示相应的处理状态和进度提示
4. WHEN 操作成功 THEN 系统 SHALL 显示成功提示信息并自动刷新相关数据
5. WHEN 操作失败 THEN 系统 SHALL 显示友好的错误提示信息
6. WHEN 网络请求失败 THEN 系统 SHALL 在控制台记录错误但不影响用户体验
7. WHEN 页面在小屏幕显示 THEN 系统 SHALL 调整统计卡片布局为2列或1列显示

### 需求 11 - 后端API接口完整性

**用户故事:** 作为前端开发者，我希望后端提供完整的API接口支持，确保前端页面的所有功能都能正常工作，包括数据查询、订单操作、统计计算等。

#### 验收标准

1. WHEN 前端调用订单列表API THEN 后端 SHALL 返回分页的订单数据和统计信息
2. WHEN 前端调用订单详情API THEN 后端 SHALL 返回完整的订单详细信息
3. WHEN 前端调用订单创建API THEN 后端 SHALL 创建新订单并返回订单信息
4. WHEN 前端调用订单更新API THEN 后端 SHALL 更新订单信息并返回更新结果
5. WHEN 前端调用订单删除API THEN 后端 SHALL 执行软删除并返回操作结果
6. WHEN 前端调用状态变更API THEN 后端 SHALL 更新订单状态并记录操作日志
7. WHEN 前端调用批量操作API THEN 后端 SHALL 执行批量操作并返回操作结果
8. WHEN 前端调用任何API THEN 后端 SHALL 返回统一格式的响应（包含code、data、message字段）
9. WHEN API查询无数据 THEN 后端 SHALL 返回空数组而不是错误信息

### 需求 12 - 数据格式和字段映射

**用户故事:** 作为系统集成者，我希望确保前后端数据格式完全匹配，字段映射正确，数据类型一致，以便前端页面能够正确显示和处理数据。

#### 验收标准

1. WHEN 后端返回订单数据 THEN 字段名称 SHALL 使用camelCase格式（如orderNumber、customerName）
2. WHEN 后端返回日期数据 THEN 格式 SHALL 为ISO 8601字符串格式
3. WHEN 后端返回金额数据 THEN 类型 SHALL 为数字类型，支持小数点后两位
4. WHEN 后端返回枚举数据 THEN 值 SHALL 与前端页面的枚举值完全匹配
5. WHEN 后端返回分页数据 THEN 结构 SHALL 包含list、total、stats字段
6. WHEN 后端返回订单商品数据 THEN 结构 SHALL 包含productId、productName、quantity、unitPrice、totalPrice字段

### 需求 13 - 订单状态流转管理

**用户故事:** 作为订单管理员，我希望系统能够正确管理订单状态流转，确保状态变更的合理性和操作的可追溯性。

#### 验收标准

1. WHEN 订单创建 THEN 系统 SHALL 设置初始状态为"待确认"
2. WHEN 订单状态为"待确认" THEN 系统 SHALL 允许变更为"已确认"或"已取消"
3. WHEN 订单状态为"已确认" THEN 系统 SHALL 允许变更为"处理中"或"已取消"
4. WHEN 订单状态为"处理中" THEN 系统 SHALL 允许变更为"已发货"
5. WHEN 订单状态为"已发货" THEN 系统 SHALL 允许变更为"已送达"
6. WHEN 订单状态为"已送达" THEN 系统 SHALL 允许变更为"已完成"
7. WHEN 订单状态为"已完成"或"已取消" THEN 系统 SHALL 不允许再次变更状态
8. WHEN 状态变更 THEN 系统 SHALL 记录操作日志，包含操作人、操作时间、变更原因

### 需求 14 - 性能和稳定性

**用户故事:** 作为订单管理员，我希望系统具有良好的性能和稳定性，能够快速响应查询请求，稳定处理大量数据，以便高效完成日常工作。

#### 验收标准

1. WHEN 用户加载订单列表 THEN 系统 SHALL 在2秒内返回响应
2. WHEN 用户执行搜索筛选 THEN 系统 SHALL 在1秒内更新列表数据
3. WHEN 系统处理大量订单记录 THEN 分页查询 SHALL 保持良好性能
4. WHEN 多个用户同时访问 THEN 系统 SHALL 保持稳定响应
5. WHEN 数据库连接异常 THEN 系统 SHALL 优雅处理错误并提供友好提示
6. WHEN API请求失败 THEN 前端 SHALL 在控制台记录错误但不影响用户界面