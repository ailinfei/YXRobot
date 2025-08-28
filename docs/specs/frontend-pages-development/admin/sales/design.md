# Admin Business - 销售数据模块设计文档

## 🚨 核心设计要求（强制执行）

### ⚠️ 前端页面与数据库数据完全绑定设计原则

**本设计文档的核心要求是实现前端页面与数据库数据的完全绑定，确保使用真实数据，并能在前端页面正常显示：**

#### 🔥 前端数据展示设计要求
1. **数据来源设计**：
   - ✅ **必须使用**：通过API接口从数据库获取的真实数据
   - ❌ **严禁使用**：在Vue组件中硬编码的模拟数据
   - ❌ **严禁使用**：在constants文件中定义的静态假数据
   - ❌ **严禁使用**：任何形式的mock数据或示例数据

2. **Vue数据绑定设计**：
   - 所有页面数据必须通过`reactive`或`ref`进行响应式绑定
   - 数据必须通过API调用动态加载，不能预设静态值
   - 页面加载时必须调用对应的API接口获取数据
   - 数据更新后必须重新调用API刷新页面显示

#### 🚨 字段映射一致性设计原则（强制执行）

项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确：

1. **数据层映射规范**：
   - 数据库表字段：统一使用snake_case命名（如：`sales_amount`, `order_date`, `customer_id`）
   - Java实体类属性：统一使用camelCase命名（如：`salesAmount`, `orderDate`, `customerId`）
   - MyBatis映射：确保column和property正确对应
   - 前端接口：与Java实体保持camelCase一致性

2. **字段映射验证机制**：
   - 开发前：验证数据库表结构与Java实体类的字段对应关系
   - 开发中：确保MyBatis XML映射文件的正确性
   - 开发后：测试前后端数据传输的字段匹配性
   - 部署前：执行完整的字段映射验证测试

3. **字段映射错误防护**：
   - 禁止在前端使用snake_case字段名
   - 禁止在MyBatis映射中使用错误的property名称
   - 禁止在API接口中使用不一致的字段命名
   - 建立字段映射检查工具和验证流程

## 概述

销售数据模块是YXRobot管理后台的核心业务管理功能，需要从零开始开发完整的前后端功能。该模块提供全面的销售数据管理功能，包括销售记录的录入、查询、统计分析、图表展示和数据导出等功能，访问地址为 http://localhost:8081/admin/business/sales。

**真实数据原则：**
- 系统不得在任何情况下插入或显示模拟销售数据
- 所有数据必须来自真实的用户操作和数据库查询
- 空数据状态必须正确处理，不得用模拟数据填充
- 数据初始化服务只创建必要的基础数据，不创建示例销售记录

## 架构设计

### 系统架构

```
前端层 (Vue.js)
├── SalesManagement.vue (主页面组件)
├── 销售记录列表展示
├── 筛选和搜索功能
├── 销售数据录入对话框
├── 统计分析面板
├── 图表展示组件
└── 分页和批量操作

API层 (Spring Boot)
├── SalesController (REST API控制器)
├── 销售记录CRUD接口
├── 统计分析接口
├── 图表数据接口
└── 数据导出接口

业务层 (Service)
├── SalesService (销售业务逻辑)
├── SalesStatsService (销售统计服务)
├── SalesAnalysisService (销售分析服务)
├── SalesExportService (数据导出服务)
└── SalesValidationService (数据验证服务)

数据层 (MyBatis + MySQL)
├── sales_records (销售记录主表)
├── customers (客户信息表)
├── products (产品信息表)
├── sales_staff (销售人员表)
└── sales_stats (销售统计表)
```

### 核心组件

#### 1. 前端组件结构
- **SalesManagement.vue**: 主页面组件，包含完整的销售数据管理功能
- **筛选区域**: 搜索框、时间范围选择器、产品类型选择器、销售人员选择器
- **销售记录列表**: 数据表格展示销售信息
- **空状态组件**: 当无销售数据时显示的引导界面
- **录入对话框**: 销售数据录入和编辑表单
- **统计面板**: 显示关键销售指标和统计数据
- **图表组件**: ECharts图表展示销售趋势和分析
- **分页组件**: 支持大数据量的分页显示

#### 2. 后端服务组件
- **SalesController**: 处理HTTP请求和响应
- **SalesService**: 核心业务逻辑处理，严禁返回模拟数据
- **SalesMapper**: 数据访问层映射，只查询真实数据
- **SalesValidator**: 数据验证组件
- **DataInitializationService**: 数据初始化服务，禁止插入示例销售数据

#### 3. 数据真实性保障组件
- **RealDataValidator**: 确保所有数据来源的真实性
- **EmptyStateHandler**: 处理空数据状态的专用组件
- **MockDataPrevention**: 防止模拟数据插入的安全机制

## 数据模型设计

**🔥 字段映射设计标准**

所有数据模型必须严格遵循四层字段映射标准：

```
数据库层 (snake_case) → Java层 (camelCase) → MyBatis映射 → 前端层 (camelCase)
     ↓                      ↓                    ↓                ↓
  sales_amount        →  salesAmount      →  column/property  →  salesAmount
  order_date          →  orderDate        →  column/property  →  orderDate  
  customer_id         →  customerId       →  column/property  →  customerId
```

### 销售记录主表 (sales_records)

```sql
CREATE TABLE `sales_records` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '销售记录ID，主键',
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号',
  `customer_id` BIGINT NOT NULL COMMENT '客户ID',
  `product_id` BIGINT NOT NULL COMMENT '产品ID',
  `sales_staff_id` BIGINT NOT NULL COMMENT '销售人员ID',
  `sales_amount` DECIMAL(12,2) NOT NULL COMMENT '销售金额',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '销售数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `discount_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '折扣金额',
  `order_date` DATE NOT NULL COMMENT '订单日期',
  `delivery_date` DATE COMMENT '交付日期',
  `status` ENUM('pending', 'confirmed', 'delivered', 'completed', 'cancelled') DEFAULT 'pending' COMMENT '订单状态',
  `payment_status` ENUM('unpaid', 'partial', 'paid', 'refunded') DEFAULT 'unpaid' COMMENT '付款状态',
  `payment_method` VARCHAR(50) COMMENT '付款方式',
  `region` VARCHAR(100) COMMENT '销售地区',
  `channel` VARCHAR(50) COMMENT '销售渠道',
  `notes` TEXT COMMENT '备注信息',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number` (`order_number`),
  INDEX `idx_customer_id` (`customer_id`),
  INDEX `idx_product_id` (`product_id`),
  INDEX `idx_sales_staff_id` (`sales_staff_id`),
  INDEX `idx_order_date` (`order_date`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售记录表';
```

### 客户信息表 (customers)

```sql
CREATE TABLE `customers` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '客户ID，主键',
  `customer_name` VARCHAR(200) NOT NULL COMMENT '客户名称',
  `customer_type` ENUM('individual', 'enterprise') NOT NULL COMMENT '客户类型：个人、企业',
  `contact_person` VARCHAR(100) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱地址',
  `address` TEXT COMMENT '地址',
  `region` VARCHAR(100) COMMENT '所在地区',
  `industry` VARCHAR(100) COMMENT '所属行业',
  `credit_level` ENUM('A', 'B', 'C', 'D') DEFAULT 'B' COMMENT '信用等级',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户信息表';
```

### 产品信息表 (products)

```sql
CREATE TABLE `products` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '产品ID，主键',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品名称',
  `product_code` VARCHAR(50) NOT NULL COMMENT '产品编码',
  `category` VARCHAR(100) NOT NULL COMMENT '产品类别',
  `brand` VARCHAR(100) COMMENT '品牌',
  `model` VARCHAR(100) COMMENT '型号',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `cost_price` DECIMAL(10,2) COMMENT '成本价',
  `stock_quantity` INT DEFAULT 0 COMMENT '库存数量',
  `unit` VARCHAR(20) DEFAULT '台' COMMENT '计量单位',
  `description` TEXT COMMENT '产品描述',
  `specifications` JSON COMMENT '产品规格（JSON格式）',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_code` (`product_code`),
  INDEX `idx_product_name` (`product_name`),
  INDEX `idx_category` (`category`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品信息表';
```

### 销售人员表 (sales_staff)

```sql
CREATE TABLE `sales_staff` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '销售人员ID，主键',
  `staff_name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `staff_code` VARCHAR(50) NOT NULL COMMENT '员工编号',
  `department` VARCHAR(100) COMMENT '部门',
  `position` VARCHAR(100) COMMENT '职位',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱地址',
  `hire_date` DATE COMMENT '入职日期',
  `sales_target` DECIMAL(12,2) COMMENT '销售目标',
  `commission_rate` DECIMAL(5,4) DEFAULT 0.05 COMMENT '提成比例',
  `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否在职',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_staff_code` (`staff_code`),
  INDEX `idx_staff_name` (`staff_name`),
  INDEX `idx_department` (`department`),
  INDEX `idx_is_active` (`is_active`),
  INDEX `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售人员表';
```

### 销售统计表 (sales_stats)

```sql
CREATE TABLE `sales_stats` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '统计ID，主键',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_type` ENUM('daily', 'weekly', 'monthly', 'yearly') NOT NULL COMMENT '统计类型',
  `total_sales_amount` DECIMAL(15,2) DEFAULT 0 COMMENT '总销售金额',
  `total_orders` INT DEFAULT 0 COMMENT '总订单数',
  `total_quantity` INT DEFAULT 0 COMMENT '总销售数量',
  `avg_order_amount` DECIMAL(10,2) DEFAULT 0 COMMENT '平均订单金额',
  `new_customers` INT DEFAULT 0 COMMENT '新客户数',
  `active_customers` INT DEFAULT 0 COMMENT '活跃客户数',
  `top_product_id` BIGINT COMMENT '销量最高产品ID',
  `top_staff_id` BIGINT COMMENT '业绩最高销售人员ID',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type` (`stat_date`, `stat_type`),
  INDEX `idx_stat_date` (`stat_date`),
  INDEX `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售统计表';
```

## 接口设计

### REST API 接口

#### 1. 销售记录管理接口

```typescript
// 获取销售记录列表
GET /api/sales/records
参数: {
  page?: number,
  pageSize?: number,
  startDate?: string,
  endDate?: string,
  customerId?: number,
  productId?: number,
  staffId?: number,
  status?: string,
  keyword?: string
}
响应: {
  code: 200,
  data: {
    list: SalesRecord[], // 真实数据，可能为空数组
    total: number,       // 真实总数，可能为0
    page: number,
    pageSize: number,
    isEmpty: boolean     // 明确标识是否为空状态
  }
}

// 空数据状态响应示例
响应 (无数据时): {
  code: 200,
  message: "查询成功",
  data: {
    list: [],        // 空数组，不是模拟数据
    total: 0,        // 真实的0
    page: 1,
    pageSize: 20,
    isEmpty: true    // 明确标识为空状态
  }
}

// 获取销售记录详情
GET /api/sales/records/{id}
响应: {
  code: 200,
  data: SalesRecord
}

// 创建销售记录
POST /api/sales/records
请求体: Partial<SalesRecord>
响应: {
  code: 200,
  data: SalesRecord
}

// 更新销售记录
PUT /api/sales/records/{id}
请求体: Partial<SalesRecord>
响应: {
  code: 200,
  data: SalesRecord
}

// 删除销售记录
DELETE /api/sales/records/{id}
响应: {
  code: 200,
  message: "删除成功"
}
```

#### 2. 统计分析接口

```typescript
// 获取销售统计数据
GET /api/sales/stats
参数: {
  startDate?: string,
  endDate?: string,
  groupBy?: 'day' | 'week' | 'month' | 'year'
}
响应: {
  code: 200,
  data: {
    totalSalesAmount: number,
    totalOrders: number,
    avgOrderAmount: number,
    growthRate: number,
    newCustomers: number,
    activeCustomers: number
  }
}

// 获取销售趋势数据
GET /api/sales/trends
参数: {
  startDate?: string,
  endDate?: string,
  type?: 'amount' | 'orders' | 'customers'
}
响应: {
  code: 200,
  data: {
    dates: string[],
    values: number[],
    comparison: number[] // 对比数据
  }
}

// 获取产品销售排行
GET /api/sales/product-ranking
参数: {
  startDate?: string,
  endDate?: string,
  limit?: number
}
响应: {
  code: 200,
  data: Array<{
    productId: number,
    productName: string,
    salesAmount: number,
    quantity: number,
    orderCount: number
  }>
}

// 获取销售人员业绩排行
GET /api/sales/staff-performance
参数: {
  startDate?: string,
  endDate?: string,
  limit?: number
}
响应: {
  code: 200,
  data: Array<{
    staffId: number,
    staffName: string,
    salesAmount: number,
    orderCount: number,
    targetCompletion: number
  }>
}
```

#### 3. 图表数据接口

```typescript
// 获取销售分布饼图数据
GET /api/sales/charts/distribution
参数: {
  type: 'product' | 'region' | 'channel',
  startDate?: string,
  endDate?: string
}
响应: {
  code: 200,
  data: Array<{
    name: string,
    value: number,
    percentage: number
  }>
}

// 获取月度销售柱状图数据
GET /api/sales/charts/monthly
参数: {
  year?: number,
  months?: number
}
响应: {
  code: 200,
  data: {
    months: string[],
    salesAmount: number[],
    orderCount: number[],
    targets: number[]
  }
}

// 获取销售漏斗图数据
GET /api/sales/charts/funnel
参数: {
  startDate?: string,
  endDate?: string
}
响应: {
  code: 200,
  data: Array<{
    stage: string,
    value: number,
    conversion: number
  }>
}
```

#### 4. 辅助功能接口

```typescript
// 获取客户列表
GET /api/sales/customers
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
GET /api/sales/products
参数: {
  keyword?: string,
  category?: string,
  active?: boolean
}
响应: {
  code: 200,
  data: Product[]
}

// 获取销售人员列表
GET /api/sales/staff
参数: {
  keyword?: string,
  department?: string,
  active?: boolean
}
响应: {
  code: 200,
  data: SalesStaff[]
}

// 导出销售数据
POST /api/sales/export
请求体: {
  format: 'excel' | 'csv' | 'pdf',
  fields: string[],
  filters: object
}
响应: {
  code: 200,
  data: { downloadUrl: string }
}

// 批量操作
POST /api/sales/batch
请求体: {
  ids: number[],
  operation: 'delete' | 'updateStatus' | 'export',
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

const salesEmptyStateConfig: EmptyStateConfig = {
  isEmpty: (salesList) => !salesList || salesList.length === 0,
  emptyStateProps: {
    title: "暂无销售数据",
    description: "还没有录入任何销售记录，点击下方按钮录入第一条销售数据",
    actionText: "录入第一条销售数据",
    actionHandler: () => openCreateSalesDialog()
  }
};
```

### 前端空状态处理

```vue
<template>
  <div class="sales-management">
    <!-- 正常数据显示 -->
    <div v-if="!isEmpty" class="sales-content">
      <!-- 统计面板 -->
      <div class="stats-panel">
        <el-row :gutter="20">
          <el-col :span="6" v-for="stat in statsData" :key="stat.key">
            <el-card class="stat-card">
              <div class="stat-content">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 图表区域 -->
      <div class="charts-section">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card title="销售趋势">
              <sales-trend-chart :data="trendData" />
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card title="产品销售分布">
              <sales-distribution-chart :data="distributionData" />
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 销售记录列表 -->
      <sales-list :data="salesList" />
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
import { getSalesRecords, getSalesStats } from '@/api/sales';

const salesList = ref([]);
const statsData = ref([]);
const trendData = ref([]);
const distributionData = ref([]);
const total = ref(0);
const loading = ref(false);

const isEmpty = computed(() => {
  return !salesList.value || salesList.value.length === 0;
});

// 加载销售数据
const loadSalesData = async () => {
  loading.value = true;
  try {
    const [recordsRes, statsRes] = await Promise.all([
      getSalesRecords(),
      getSalesStats()
    ]);
    
    // 绑定真实数据，可能为空
    salesList.value = recordsRes.data.list;
    total.value = recordsRes.data.total;
    
    // 只有在有数据时才加载统计和图表
    if (!isEmpty.value) {
      statsData.value = formatStatsData(statsRes.data);
      await loadChartsData();
    }
  } catch (error) {
    console.error('加载销售数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 严禁在空状态时显示模拟数据
const showMockData = false; // 永远为false

onMounted(() => {
  loadSalesData();
});
</script>
```

### 后端空数据响应

```java
@RestController
@RequestMapping("/api/sales")
public class SalesController {
    
    @GetMapping("/records")
    public ResponseEntity<Map<String, Object>> getSalesRecords(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "20") int pageSize,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) {
        
        // 查询真实数据
        List<SalesRecordDTO> salesList = salesService.getSalesRecords(page, pageSize, startDate, endDate);
        
        // 即使数据为空，也返回真实的空结果，不插入模拟数据
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", Map.of(
            "list", salesList, // 可能为空数组
            "total", salesService.getTotalCount(),
            "page", page,
            "pageSize", pageSize,
            "isEmpty", salesList.isEmpty() // 明确标识是否为空
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSalesStats() {
        // 只有在有销售数据时才返回统计信息
        SalesStatsDTO stats = salesService.getSalesStats();
        
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
    
    private void initializeSalesData() throws SQLException {
        // 只初始化必要的基础数据
        if (!hasExistingCustomers(connection)) {
            insertBasicCustomerTypes(connection);
        }
        
        if (!hasExistingProducts(connection)) {
            insertBasicProductCategories(connection);
        }
        
        if (!hasExistingSalesStaff(connection)) {
            insertBasicSalesStaffRoles(connection);
        }
        
        // 严禁插入示例销售记录
        // 确保sales_records表保持空状态，让用户手动录入真实数据
        logger.info("销售数据初始化完成 - 未插入任何示例销售记录，保持真实数据原则");
    }
    
    // 移除或禁用示例销售记录插入方法
    private void insertSampleSalesRecords(Connection connection) throws SQLException {
        // 此方法已被禁用 - 不插入任何示例销售记录
        logger.warn("示例销售记录插入已被禁用 - 遵循真实数据原则");
        return; // 直接返回，不执行任何插入操作
    }
}
```

## 业务逻辑设计

### 真实数据验证机制

```typescript
class RealDataValidator {
  // 验证数据是否为真实用户创建
  static validateDataSource(salesRecord: SalesRecord): boolean {
    // 检查是否包含模拟数据特征
    const mockDataIndicators = [
      '示例', '测试', 'demo', 'sample', 'mock', 'fake', 'test'
    ];
    
    const customerName = salesRecord.customerName?.toLowerCase() || '';
    const orderNumber = salesRecord.orderNumber?.toLowerCase() || '';
    
    const hasIndicators = mockDataIndicators.some(indicator => 
      customerName.includes(indicator) || orderNumber.includes(indicator)
    );
    
    if (hasIndicators) {
      console.warn('检测到可能的模拟数据:', salesRecord.orderNumber);
    }
    
    return !hasIndicators;
  }
  
  // 验证数据创建来源
  static validateCreationSource(salesRecord: SalesRecord): boolean {
    // 确保数据来自用户操作，而非系统自动生成
    return !salesRecord.orderNumber?.startsWith('SYS') && 
           !salesRecord.notes?.includes('系统生成');
  }
}
```

### 销售状态管理

```typescript
enum SalesStatus {
  PENDING = 'pending',      // 待确认
  CONFIRMED = 'confirmed',  // 已确认
  DELIVERED = 'delivered',  // 已交付
  COMPLETED = 'completed',  // 已完成
  CANCELLED = 'cancelled'   // 已取消
}

enum PaymentStatus {
  UNPAID = 'unpaid',       // 未付款
  PARTIAL = 'partial',     // 部分付款
  PAID = 'paid',           // 已付款
  REFUNDED = 'refunded'    // 已退款
}

// 状态转换规则
const statusTransitions = {
  pending: ['confirmed', 'cancelled'],
  confirmed: ['delivered', 'cancelled'],
  delivered: ['completed'],
  completed: [],
  cancelled: []
}
```

### 数据验证规则

```typescript
const salesValidationRules = {
  orderNumber: {
    required: true,
    pattern: /^[A-Z0-9]{8,20}$/,
    message: "订单号格式不正确，应为8-20位字母数字组合"
  },
  customerId: {
    required: true,
    message: "请选择客户"
  },
  productId: {
    required: true,
    message: "请选择产品"
  },
  salesAmount: {
    required: true,
    min: 0.01,
    max: 999999.99,
    message: "销售金额应在0.01-999999.99之间"
  },
  quantity: {
    required: true,
    min: 1,
    max: 9999,
    message: "销售数量应在1-9999之间"
  },
  orderDate: {
    required: true,
    message: "请选择订单日期"
  },
  salesStaffId: {
    required: true,
    message: "请选择销售人员"
  }
}
```

### 统计计算逻辑

```typescript
class SalesStatsCalculator {
  // 计算销售统计数据
  static calculateStats(salesRecords: SalesRecord[]): SalesStats {
    if (!salesRecords || salesRecords.length === 0) {
      return {
        totalSalesAmount: 0,
        totalOrders: 0,
        avgOrderAmount: 0,
        totalQuantity: 0,
        newCustomers: 0,
        activeCustomers: 0
      };
    }
    
    const totalSalesAmount = salesRecords.reduce((sum, record) => sum + record.salesAmount, 0);
    const totalOrders = salesRecords.length;
    const avgOrderAmount = totalSalesAmount / totalOrders;
    const totalQuantity = salesRecords.reduce((sum, record) => sum + record.quantity, 0);
    
    return {
      totalSalesAmount,
      totalOrders,
      avgOrderAmount,
      totalQuantity,
      newCustomers: this.calculateNewCustomers(salesRecords),
      activeCustomers: this.calculateActiveCustomers(salesRecords)
    };
  }
  
  // 计算销售趋势
  static calculateTrends(salesRecords: SalesRecord[], groupBy: 'day' | 'week' | 'month'): TrendData {
    // 按时间分组统计
    const groupedData = this.groupByTime(salesRecords, groupBy);
    
    return {
      dates: Object.keys(groupedData),
      values: Object.values(groupedData).map(records => 
        records.reduce((sum, record) => sum + record.salesAmount, 0)
      )
    };
  }
}
```

## 性能优化设计

### 数据库优化

1. **索引策略**
   - 主键索引：`id`
   - 复合索引：`(order_date, status, is_deleted)`
   - 客户索引：`customer_id`
   - 产品索引：`product_id`
   - 销售人员索引：`sales_staff_id`
   - 订单号唯一索引：`order_number`

2. **查询优化**
   - 分页查询使用LIMIT和OFFSET
   - 统计查询使用聚合函数和索引
   - 避免N+1查询问题
   - 使用JOIN优化关联查询

3. **数据分离**
   - 销售记录和统计数据分离
   - 客户、产品、销售人员信息独立存储
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
   - 缓存常用数据（客户、产品列表）

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
   - 销售记录创建权限
   - 销售记录编辑权限
   - 销售记录删除权限
   - 数据导出权限

2. **数据权限**
   - 按销售人员过滤
   - 按部门过滤
   - 敏感数据脱敏

## 错误处理设计

### 异常分类

```typescript
enum SalesErrorCode {
  SALES_RECORD_NOT_FOUND = 'SALES_RECORD_NOT_FOUND',
  INVALID_ORDER_NUMBER = 'INVALID_ORDER_NUMBER',
  CUSTOMER_NOT_FOUND = 'CUSTOMER_NOT_FOUND',
  PRODUCT_NOT_FOUND = 'PRODUCT_NOT_FOUND',
  INSUFFICIENT_STOCK = 'INSUFFICIENT_STOCK',
  INVALID_SALES_AMOUNT = 'INVALID_SALES_AMOUNT',
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
  // 创建真实的测试销售数据
  static createRealTestSalesRecord(): SalesRecord {
    return {
      orderNumber: `TEST${Date.now()}`,
      customerName: "真实测试客户",
      productName: "真实测试产品",
      salesAmount: 1000.00,
      quantity: 1,
      orderDate: new Date().toISOString().split('T')[0],
      salesStaff: "测试销售员",
      status: "pending"
    };
  }
  
  // 严禁创建模拟数据
  static createMockSalesRecord(): never {
    throw new Error("禁止创建模拟数据 - 请使用createRealTestSalesRecord()");
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
   - 数据导出功能测试
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
   - 销售数据录入量统计
   - 用户访问统计
   - 功能使用统计
   - 错误率监控

3. **数据质量监控**
   - 数据完整性检查
   - 数据一致性验证
   - 异常数据预警
   - 模拟数据检测