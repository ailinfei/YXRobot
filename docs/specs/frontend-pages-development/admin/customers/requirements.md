# Admin Business - 客户管理模块需求文档

## 🚨 核心需求要求（强制执行）

### ⚠️ 基于现有前端页面的后端API适配规范

**本需求文档基于现有的 Customers.vue 前端页面（位于 `/src/frontend/src/views/admin/Customers.vue`），要求后端API完全适配前端页面的数据需求和交互逻辑：**

#### 🔥 前端页面分析结果

现有Customers.vue页面包含以下核心功能模块：
1. **客户管理页面头部**：包含页面标题、描述信息和新增客户按钮
2. **客户统计卡片**：总客户数、普通客户、VIP客户、高级客户、活跃设备、总收入（6个统计卡片）
3. **客户列表表格**：包含头像、姓名、等级、电话、设备数量、消费金额、客户价值、地区、最后活跃时间等字段
4. **表格操作功能**：查看、编辑、拨打、短信、删除等操作按钮
5. **筛选和搜索功能**：支持按客户等级、设备类型、地区进行筛选
6. **分页功能**：支持分页显示和页面大小选择
7. **客户详情对话框**：显示完整的客户详细信息
8. **客户编辑对话框**：支持新增和编辑客户信息
9. **批量操作功能**：支持多选和批量操作
10. **数据导出功能**：支持客户数据导出

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

基于现有Customers.vue页面分析，后端必须提供以下字段：

**客户基本信息字段**：
- `id` - 客户ID（前端用于客户标识）
- `name` - 客户姓名（前端显示在表格和详情）
- `level` - 客户等级（前端显示等级标签：regular、vip、premium）
- `phone` - 联系电话（前端显示在表格和拨打功能）
- `email` - 邮箱地址（前端显示在详情页面）
- `company` - 公司名称（前端显示在详情页面）
- `status` - 客户状态（前端用于状态标签显示）
- `avatar` - 头像URL（前端显示在表格头像列）
- `address` - 地址信息（前端显示地区信息）
- `tags` - 客户标签（前端显示标签列表）
- `notes` - 备注信息（前端显示在详情页面）

**客户统计字段**：
- `totalSpent` - 消费金额（前端显示在表格和统计）
- `customerValue` - 客户价值（前端显示评分）
- `deviceCount` - 设备数量统计（前端显示设备数量信息）
- `registeredAt` - 注册时间（前端显示注册日期）
- `lastActiveAt` - 最后活跃时间（前端显示活跃状态）

**关联数据字段**：
- `devices` - 客户设备列表（前端详情页显示）
- `orders` - 客户订单列表（前端详情页显示）
- `serviceRecords` - 服务记录列表（前端详情页显示）

## 介绍

客户管理模块是 YXRobot 管理后台的核心业务管理功能，基于现有的 Customers.vue 前端页面提供客户信息的全面管理。该模块需要开发完整的后端API支持，确保前端页面的所有功能正常工作，访问地址为 http://localhost:8081/admin/business/customers。

**基于现有前端页面的技术实现要求:**

1. **前端页面功能支持**: 
   - 后端API必须完全支持现有Customers.vue页面的所有功能
   - 包括客户统计卡片、客户列表表格、详情查看、编辑操作等
   - 支持筛选、搜索、分页、批量操作、数据导出等交互功能

2. **API接口适配**: 
   - 提供与前端页面完全匹配的RESTful API接口
   - 支持前端页面的所有数据查询和操作需求
   - API响应格式必须与前端TypeScript接口定义完全一致

3. **数据库查询优化**: 
   - 后端API从MySQL数据库实时查询数据
   - 支持复杂的关联查询（客户信息、设备信息、订单信息、服务记录）
   - 优化查询性能，支持大数据量的分页和筛选

4. **统计数据支持**: 
   - 提供客户统计数据（总数、等级分布、设备统计、收入统计）
   - 支持实时统计计算和数据更新
   - 支持按条件的动态统计

5. **空数据状态处理**: 
   - 当数据库中没有数据时，API返回空数组而非错误
   - 前端页面已实现空状态显示，后端只需正确返回空数据
   - 支持数据为空时的统计指标显示（显示为0）

**技术架构要求:**
- 后端Spring Boot提供RESTful API接口
- 使用MyBatis进行数据库操作和复杂查询
- 数据传输格式严格遵循前端TypeScript接口定义
- **关键**：后端适配前端，而非前端适配后端
- 确保现有前端页面无需修改即可正常工作

**字段映射实施标准（基于现有表扩展）：**
```sql
-- 扩展现有customers表字段 (snake_case)
-- 现有字段：customer_name, phone, email, address, region...
-- 新增字段：customer_level, customer_status, avatar_url, total_spent...
ALTER TABLE customers ADD COLUMN customer_level ENUM('regular', 'vip', 'premium');
ALTER TABLE customers ADD COLUMN total_spent DECIMAL(12,2) DEFAULT 0;
```

```java
// Java实体类属性 (camelCase)
public class Customer {
  private Long id;
  private String customerName;
  private String customerLevel;
  private String phoneNumber;
  private BigDecimal totalSpent;
}
```

```xml
<!-- MyBatis映射配置 -->
<result column="customer_name" property="customerName"/>
<result column="customer_level" property="customerLevel"/>
<result column="phone_number" property="phoneNumber"/>
<result column="total_spent" property="totalSpent"/>
```

```typescript
// 前端TypeScript接口 (camelCase)
interface Customer {
  id: string;
  name: string;
  level: 'regular' | 'vip' | 'premium';
  phone: string;
  totalSpent: number;
}
```

## 需求

### 需求 1 - 客户管理页面头部功能

**用户故事:** 作为客户管理员，我希望能够查看客户管理页面的头部区域，包括页面标题、描述信息和新增客户按钮，以便了解当前页面的功能并快速创建新客户。

#### 验收标准

1. WHEN 用户访问客户管理页面 THEN 系统 SHALL 显示"客户管理"页面标题
2. WHEN 显示页面描述 THEN 系统 SHALL 显示"管理客户信息和设备关联，提供优质客户服务"副标题
3. WHEN 显示操作按钮 THEN 系统 SHALL 显示"新增客户"按钮，点击后打开客户创建对话框
4. WHEN 页面加载 THEN 系统 SHALL 正确显示页面布局和样式
5. WHEN 页面响应式设计 THEN 系统 SHALL 在不同设备上正确显示头部信息

### 需求 2 - 客户统计卡片功能

**用户故事:** 作为客户管理员，我希望能够在页面顶部看到客户统计卡片，显示总客户数、普通客户、VIP客户、高级客户、活跃设备、总收入等关键指标，以便快速了解客户业务概况。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示6个统计卡片：总客户数、普通客户、VIP客户、高级客户、活跃设备、总收入
2. WHEN 显示总客户数卡片 THEN 系统 SHALL 显示客户总数量和对应图标
3. WHEN 显示客户等级统计 THEN 系统 SHALL 分别显示普通客户、VIP客户、高级客户的数量
4. WHEN 显示活跃设备卡片 THEN 系统 SHALL 显示当前活跃设备总数
5. WHEN 显示总收入卡片 THEN 系统 SHALL 显示格式化的收入金额（以万元为单位）
6. WHEN 卡片悬停效果 THEN 系统 SHALL 显示卡片上浮动画效果
7. WHEN 数据为空 THEN 系统 SHALL 显示0值而不是错误信息

### 需求 3 - 客户列表表格功能

**用户故事:** 作为客户管理员，我希望能够查看客户列表表格，包含完整的客户信息、统计数据、联系方式等，并支持排序、筛选等功能，以便高效管理客户数据。

#### 验收标准

1. WHEN 页面加载 THEN 系统 SHALL 显示客户列表表格，包含头像、姓名、等级、电话、设备数量、消费金额、客户价值、地区、最后活跃时间、操作按钮
2. WHEN 显示客户头像 THEN 系统 SHALL 显示客户头像图片，如无头像则显示默认头像
3. WHEN 显示客户等级 THEN 系统 SHALL 使用不同颜色的标签显示：普通客户、VIP客户、高级客户
4. WHEN 显示设备数量 THEN 系统 SHALL 显示客户拥有的设备总数（购买+租赁）
5. WHEN 显示消费金额 THEN 系统 SHALL 显示格式化的金额（带千分位分隔符）
6. WHEN 显示客户价值 THEN 系统 SHALL 显示客户价值评分（0-10分）
7. WHEN 显示地区信息 THEN 系统 SHALL 显示省份和城市信息
8. WHEN 显示最后活跃时间 THEN 系统 SHALL 显示格式化的日期时间
9. WHEN 支持列排序 THEN 系统 SHALL 支持按姓名、设备数量、消费金额、客户价值、最后活跃时间排序
10. WHEN 数据为空 THEN 系统 SHALL 显示空状态页面

### 需求 4 - 表格操作功能

**用户故事:** 作为客户管理员，我希望能够对客户进行各种操作，包括查看详情、编辑信息、拨打电话、发送短信、删除客户等，以便全面管理客户关系。

#### 验收标准

1. WHEN 用户点击"查看"按钮 THEN 系统 SHALL 打开客户详情对话框，显示完整的客户信息
2. WHEN 用户点击"编辑"按钮 THEN 系统 SHALL 打开客户编辑对话框，允许修改客户信息
3. WHEN 用户点击"拨打"按钮 THEN 系统 SHALL 显示拨打电话确认对话框
4. WHEN 用户点击"短信"按钮 THEN 系统 SHALL 打开短信发送对话框，允许输入短信内容
5. WHEN 用户点击"删除"按钮 THEN 系统 SHALL 显示删除确认对话框，确认后删除客户
6. WHEN 删除操作确认 THEN 系统 SHALL 删除客户数据并刷新列表
7. WHEN 操作成功 THEN 系统 SHALL 显示成功提示信息
8. WHEN 操作失败 THEN 系统 SHALL 显示友好的错误提示信息

### 需求 5 - 筛选和搜索功能

**用户故事:** 作为客户管理员，我希望能够通过多种条件筛选和搜索客户，包括客户等级、设备类型、地区等，以便快速找到目标客户。

#### 验收标准

1. WHEN 用户选择客户等级筛选 THEN 系统 SHALL 按选定等级筛选客户列表
2. WHEN 用户选择设备类型筛选 THEN 系统 SHALL 按设备类型（购买设备、租赁设备）筛选客户
3. WHEN 用户选择地区筛选 THEN 系统 SHALL 按地区筛选客户列表
4. WHEN 用户输入搜索关键词 THEN 系统 SHALL 搜索客户姓名、电话、邮箱等字段并实时更新列表
5. WHEN 应用多个筛选条件 THEN 系统 SHALL 同时应用所有筛选条件
6. WHEN 清除筛选条件 THEN 系统 SHALL 重置筛选并显示所有客户
7. WHEN 筛选结果为空 THEN 系统 SHALL 显示无匹配结果的提示

### 需求 6 - 分页功能

**用户故事:** 作为客户管理员，我希望客户列表支持分页功能，能够处理大量客户数据并提供灵活的分页控制，以便高效浏览和管理客户记录。

#### 验收标准

1. WHEN 显示客户列表 THEN 系统 SHALL 支持分页显示，默认每页20条记录
2. WHEN 用户切换页码 THEN 系统 SHALL 加载对应页面的客户数据
3. WHEN 分页信息显示 THEN 系统 SHALL 显示总记录数、当前页码、总页数等信息
4. WHEN 用户选择页面大小 THEN 系统 SHALL 支持10、20、50、100条记录选择
5. WHEN 筛选条件变更 THEN 系统 SHALL 重置分页到第一页
6. WHEN 数据加载中 THEN 系统 SHALL 显示加载状态指示器
7. WHEN 数据加载完成 THEN 系统 SHALL 隐藏加载状态并显示数据

### 需求 7 - 客户详情查看功能

**用户故事:** 作为客户管理员，我希望能够查看客户的详细信息，包括基本信息、设备列表、订单历史、服务记录等，以便全面了解客户情况。

#### 验收标准

1. WHEN 用户点击"查看详情"按钮 THEN 系统 SHALL 打开客户详情对话框
2. WHEN 显示客户基本信息 THEN 系统 SHALL 显示姓名、等级、联系方式、地址、标签、备注等信息
3. WHEN 显示客户设备列表 THEN 系统 SHALL 显示客户拥有的所有设备信息
4. WHEN 显示客户订单历史 THEN 系统 SHALL 显示客户的所有订单记录
5. WHEN 显示服务记录 THEN 系统 SHALL 显示客户的服务历史记录
6. WHEN 显示统计信息 THEN 系统 SHALL 显示客户价值、消费统计等数据
7. WHEN 用户关闭对话框 THEN 系统 SHALL 关闭详情对话框并返回列表页面

### 需求 8 - 客户编辑功能

**用户故事:** 作为客户管理员，我希望能够创建新客户和编辑现有客户信息，包括基本信息、联系方式、地址等，以便维护准确的客户数据。

#### 验收标准

1. WHEN 用户点击"新增客户"按钮 THEN 系统 SHALL 打开客户创建对话框
2. WHEN 用户点击"编辑"按钮 THEN 系统 SHALL 打开客户编辑对话框，预填充现有数据
3. WHEN 用户填写客户信息 THEN 系统 SHALL 提供表单验证，确保必填字段完整
4. WHEN 用户保存客户信息 THEN 系统 SHALL 验证数据格式并保存到数据库
5. WHEN 保存成功 THEN 系统 SHALL 显示成功提示并刷新客户列表
6. WHEN 保存失败 THEN 系统 SHALL 显示具体的错误信息
7. WHEN 用户取消操作 THEN 系统 SHALL 关闭对话框不保存数据

### ~~需求 9 - 批量操作功能~~ - **已移除：功能不需要**

**⚠️ 功能已取消**：根据项目需求，批量操作功能暂不实现。前端页面的多选功能保留但不连接后端API，可在后续版本中添加此功能。

### ~~需求 10 - 数据导出功能~~ - **已移除：功能不需要**

**⚠️ 功能已取消**：根据项目需求，客户数据导出功能暂不实现。前端页面的导出按钮保留但不连接后端API，可在后续版本中添加此功能。

### 需求 9 - 响应式设计和用户体验

**用户故事:** 作为客户管理员，我希望系统具有良好的用户体验，包括响应式设计、加载状态、错误处理等，以便在不同设备上都能正常使用。

#### 验收标准

1. WHEN 页面在移动设备显示 THEN 系统 SHALL 自动适配屏幕尺寸并调整布局
2. WHEN 数据加载中 THEN 系统 SHALL 显示加载动画和状态提示
3. WHEN 操作执行中 THEN 系统 SHALL 显示相应的处理状态和进度提示
4. WHEN 操作成功 THEN 系统 SHALL 显示成功提示信息并自动刷新相关数据
5. WHEN 操作失败 THEN 系统 SHALL 显示友好的错误提示信息
6. WHEN 网络请求失败 THEN 系统 SHALL 在控制台记录错误但不影响用户体验
7. WHEN 表格渲染 THEN 系统 SHALL 确保表格在窗口大小变化时自动调整

### 需求 10 - 后端API接口完整性

**用户故事:** 作为前端开发者，我希望后端提供完整的API接口支持，确保前端页面的所有功能都能正常工作，包括数据查询、统计计算、CRUD操作等。

#### 验收标准

1. WHEN 前端调用客户列表API THEN 后端 SHALL 支持分页、搜索、筛选等查询参数
2. WHEN 前端调用客户统计API THEN 后端 SHALL 返回各类客户统计数据
3. WHEN 前端调用客户详情API THEN 后端 SHALL 返回完整的客户信息（包含关联数据）
4. WHEN 前端调用客户CRUD API THEN 后端 SHALL 支持创建、更新、删除操作
5. WHEN 前端调用客户关联数据API THEN 后端 SHALL 返回设备、订单、服务记录等关联信息
6. WHEN 前端调用任何API THEN 后端 SHALL 返回统一格式的响应（包含code、data、message字段）
7. WHEN API查询无数据 THEN 后端 SHALL 返回空数组而不是错误信息

### 需求 11 - 数据格式和字段映射

**用户故事:** 作为系统集成者，我希望确保前后端数据格式完全匹配，字段映射正确，数据类型一致，以便前端页面能够正确显示和处理数据。

#### 验收标准

1. WHEN 后端返回客户数据 THEN 字段名称 SHALL 使用camelCase格式（如customerName、phoneNumber、totalSpent）
2. WHEN 后端返回日期数据 THEN 格式 SHALL 为ISO 8601字符串格式
3. WHEN 后端返回金额数据 THEN 类型 SHALL 为数字类型，支持小数点后两位
4. WHEN 后端返回枚举数据 THEN 值 SHALL 与前端页面的枚举值完全匹配
5. WHEN 后端返回分页数据 THEN 结构 SHALL 包含list、total、page、pageSize字段
6. WHEN 后端返回客户等级 THEN 值 SHALL 为'regular'、'vip'、'premium'之一

### 需求 12 - 性能和稳定性

**用户故事:** 作为客户管理员，我希望系统具有良好的性能和稳定性，能够快速响应查询请求，稳定处理大量数据，以便高效完成日常工作。

#### 验收标准

1. WHEN 用户加载客户管理页面 THEN 系统 SHALL 在2秒内返回响应
2. WHEN 用户执行搜索和筛选 THEN 系统 SHALL 在1秒内返回结果
3. WHEN 系统处理大量客户数据 THEN 分页查询 SHALL 保持良好性能
4. WHEN 多个用户同时访问 THEN 系统 SHALL 保持稳定响应
5. WHEN 数据库连接异常 THEN 系统 SHALL 优雅处理错误并提供友好提示
6. WHEN API请求失败 THEN 前端 SHALL 在控制台记录错误但不影响用户界面