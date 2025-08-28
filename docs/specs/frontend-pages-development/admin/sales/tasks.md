# Admin Business - 销售数据模块实施任务

## 🚨 核心开发要求（强制执行）

### ⚠️ 前端页面与数据库数据完全绑定规范

**本任务文档的核心要求是实现前端页面与数据库数据的完全绑定，确保使用真实数据，并能在前端页面正常显示：**

#### 🔥 前端数据展示强制要求
1. **数据来源要求**：
   - ✅ **必须使用**：通过API接口从数据库获取的真实数据
   - ❌ **严禁使用**：在Vue组件中硬编码的模拟数据
   - ❌ **严禁使用**：在constants文件中定义的静态假数据
   - ❌ **严禁使用**：任何形式的mock数据或示例数据

2. **Vue数据绑定要求**：
   - 所有页面数据必须通过`reactive`或`ref`进行响应式绑定
   - 数据必须通过API调用动态加载，不能预设静态值
   - 页面加载时必须调用对应的API接口获取数据
   - 数据更新后必须重新调用API刷新页面显示

#### 🚨 开发过程中必须验证的检查点
- [ ] **API调用验证**：页面加载时是否正确调用后端API接口
- [ ] **数据绑定验证**：所有显示字段是否正确绑定到API返回的数据
- [ ] **响应式更新**：数据变更时页面是否自动更新显示
- [ ] **空数据处理**：当数据库为空时页面是否正确显示空状态
- [ ] **字段完整性**：前端页面显示的所有字段必须与数据库表字段完全对应
- [ ] **数据准确性**：页面显示的数据必须与数据库中存储的数据完全一致

#### ✅ 正确的前端数据绑定实现示例
```vue
<template>
  <div>
    <!-- ✅ 正确：绑定API数据 -->
    <el-table :data="salesList" v-loading="loading">
      <el-table-column prop="orderNumber" label="订单号" />
      <el-table-column prop="customerName" label="客户名称" />
      <el-table-column prop="salesAmount" label="销售金额" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getSalesRecords } from '@/api/sales'
import type { SalesRecord } from '@/types/sales'

// ✅ 正确：响应式数据绑定
const salesList = ref<SalesRecord[]>([])
const loading = ref(false)

// ✅ 正确：通过API获取真实数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await getSalesRecords()
    salesList.value = response.data.list // 绑定API返回的真实数据
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// ✅ 正确：页面加载时获取数据
onMounted(() => {
  loadData()
})
</script>
```

#### ❌ 错误的前端数据绑定实现（严禁使用）
```vue
<template>
  <div>
    <!-- ❌ 错误：使用硬编码数据 -->
    <el-table :data="mockSalesList">
      <el-table-column prop="orderNumber" label="订单号" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
// ❌ 错误：硬编码模拟数据
const mockSalesList = [
  { id: 1, orderNumber: 'SO001', customerName: '示例客户', salesAmount: 1000 },
  { id: 2, orderNumber: 'SO002', customerName: '测试客户', salesAmount: 2000 }
]
// ❌ 错误：不调用API获取真实数据
</script>
```

**🚨 字段映射一致性开发规范（每个任务开发时必须遵守）**

项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确：

**开发时强制执行的字段映射规范：**
- 数据库字段：snake_case（如：`sales_amount`, `order_date`, `customer_id`）
- Java实体类：camelCase（如：`salesAmount`, `orderDate`, `customerId`）
- MyBatis映射：`<result column="sales_amount" property="salesAmount"/>`
- 前端接口：camelCase（如：`salesAmount: number`, `orderDate: string`）

**每个任务开发时必须遵守的规范：**
1. **设计数据库表时**：使用snake_case命名，确保字段名清晰明确
2. **创建Java实体类时**：使用camelCase命名，与数据库字段对应
3. **编写MyBatis映射时**：确保column和property正确对应
4. **定义前端接口时**：与Java实体类保持camelCase一致性
5. **编写API接口时**：确保请求响应参数命名统一

**开发过程中的字段映射检查点：**
- 创建数据库表后：立即创建对应的Java实体类
- 编写实体类后：立即配置MyBatis映射文件
- 完成后端开发后：立即定义前端TypeScript接口
- 每个功能完成后：验证前后端数据传输的字段一致性

## 任务列表

- [ ] 1. 创建销售数据管理数据库表结构
  - 创建sales_records表用于存储销售记录信息
  - 创建customers表用于存储客户信息
  - 创建products表用于存储产品信息
  - 创建sales_staff表用于存储销售人员信息
  - 创建sales_stats表用于存储销售统计数据
  - 添加必要的索引和约束确保数据完整性和查询性能
  - 插入基础数据（客户类型、产品类别、销售人员角色等）
  - _需求: 1.1, 2.1, 3.1, 5.1, 11.1_

- [ ] 2. 实现销售数据实体类和DTO
  - 创建SalesRecord实体类映射sales_records表
  - 创建Customer实体类映射customers表
  - 创建Product实体类映射products表
  - 创建SalesStaff实体类映射sales_staff表
  - 创建SalesStats实体类映射sales_stats表
  - 实现对应的DTO类用于数据传输
  - 添加数据验证注解确保数据完整性
  - 定义销售状态和付款状态枚举
  - _需求: 5.1, 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 3. 创建MyBatis映射器和XML配置
  - 实现SalesRecordMapper接口和XML映射文件
  - 实现CustomerMapper接口和XML映射文件
  - 实现ProductMapper接口和XML映射文件
  - 实现SalesStaffMapper接口和XML映射文件
  - 实现SalesStatsMapper接口和XML映射文件
  - 编写复杂查询SQL支持搜索、筛选和统计功能
  - 实现分页查询和条件筛选功能
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5, 9.1, 9.2_

- [ ] 4. 实现销售记录管理服务层
  - 创建SalesService类处理销售记录管理业务逻辑
  - 实现getSalesRecords方法支持分页查询和条件筛选
  - 实现createSalesRecord方法创建新的销售记录
  - 实现updateSalesRecord方法更新销售记录信息
  - 实现deleteSalesRecord方法删除销售记录（软删除）
  - 实现getSalesRecordById方法获取销售记录详情
  - 添加数据验证逻辑确保销售数据的完整性
  - _需求: 3.1, 3.2, 4.1, 4.2, 5.1, 5.2, 5.3_

- [ ] 5. 实现客户管理服务功能
  - 创建CustomerService类处理客户管理业务逻辑
  - 实现getCustomers方法获取客户列表，支持搜索和筛选
  - 实现createCustomer方法创建新客户
  - 实现updateCustomer方法更新客户信息
  - 实现getCustomerById方法获取客户详情
  - 实现客户信用等级管理功能
  - _需求: 5.3, 5.4, 6.1_

- [ ] 6. 实现产品管理服务功能
  - 创建ProductService类处理产品管理业务逻辑
  - 实现getProducts方法获取产品列表，支持搜索和筛选
  - 实现createProduct方法创建新产品
  - 实现updateProduct方法更新产品信息
  - 实现getProductById方法获取产品详情
  - 实现产品库存管理功能
  - 实现产品价格计算逻辑
  - _需求: 5.4, 5.5, 6.2_

- [ ] 7. 实现销售人员管理服务功能
  - 创建SalesStaffService类处理销售人员管理业务逻辑
  - 实现getSalesStaff方法获取销售人员列表
  - 实现createSalesStaff方法创建新销售人员
  - 实现updateSalesStaff方法更新销售人员信息
  - 实现getSalesStaffById方法获取销售人员详情
  - 实现销售目标和提成管理功能
  - _需求: 5.5, 6.5_

- [ ] 8. 实现销售统计分析服务功能
  - 创建SalesStatsService类处理销售统计业务逻辑
  - 实现getSalesStats方法获取基础统计数据
  - 实现getSalesTrends方法获取销售趋势数据
  - 实现getProductRanking方法获取产品销售排行
  - 实现getStaffPerformance方法获取销售人员业绩
  - 实现按时间段统计功能（日、周、月、年）
  - 添加统计数据实时更新机制
  - _需求: 7.1, 7.2, 7.3, 7.4, 7.5, 11.1, 11.2_

- [ ] 9. 实现图表数据分析服务功能
  - 创建SalesAnalysisService类处理图表数据业务逻辑
  - 实现getDistributionData方法获取销售分布数据（饼图）
  - 实现getMonthlyData方法获取月度销售数据（柱状图）
  - 实现getTrendData方法获取销售趋势数据（折线图）
  - 实现getFunnelData方法获取销售漏斗数据
  - 支持多维度数据分析（产品、地区、渠道等）
  - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 10. 实现数据导出服务功能
  - 创建SalesExportService类处理数据导出业务逻辑
  - 实现exportToExcel方法导出Excel格式数据
  - 实现exportToCSV方法导出CSV格式数据
  - 实现exportToPDF方法导出PDF格式报表
  - 支持自定义导出字段和筛选条件
  - 实现大数据量分批导出功能
  - _需求: 13.1, 13.2, 13.3, 13.4, 13.5_

- [ ] 11. 实现销售记录管理控制器
  - 创建SalesController类处理HTTP请求
  - 实现GET /api/sales/records接口获取销售记录列表
  - 实现POST /api/sales/records接口创建销售记录
  - 实现PUT /api/sales/records/{id}接口更新销售记录
  - 实现DELETE /api/sales/records/{id}接口删除销售记录
  - 实现GET /api/sales/records/{id}接口获取销售记录详情
  - 添加请求参数验证和异常处理
  - _需求: 3.1, 3.2, 4.1, 4.2, 5.1, 5.2_

- [ ] 12. 实现统计分析控制器接口
  - 实现GET /api/sales/stats接口获取销售统计数据
  - 实现GET /api/sales/trends接口获取销售趋势数据
  - 实现GET /api/sales/product-ranking接口获取产品排行
  - 实现GET /api/sales/staff-performance接口获取人员业绩
  - 添加统计参数验证和缓存优化
  - 实现统计数据的实时更新
  - _需求: 7.1, 7.2, 7.3, 7.4, 11.3, 11.4_

- [ ] 13. 实现图表数据控制器接口
  - 实现GET /api/sales/charts/distribution接口获取分布图数据
  - 实现GET /api/sales/charts/monthly接口获取月度图表数据
  - 实现GET /api/sales/charts/funnel接口获取漏斗图数据
  - 实现GET /api/sales/charts/trends接口获取趋势图数据
  - 支持图表数据的实时刷新
  - 添加图表数据格式验证
  - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 14. 实现辅助功能控制器接口
  - 实现GET /api/sales/customers接口获取客户列表
  - 实现GET /api/sales/products接口获取产品列表
  - 实现GET /api/sales/staff接口获取销售人员列表
  - 实现POST /api/sales/export接口导出销售数据
  - 实现POST /api/sales/batch接口批量操作
  - 支持搜索和筛选功能
  - _需求: 5.3, 5.4, 5.5, 10.1, 10.2, 13.4, 13.5_

- [ ] 15. 实现数据验证和异常处理
  - 创建SalesException异常类处理业务异常
  - 实现销售记录数据格式验证逻辑
  - 实现客户和产品信息验证
  - 实现表单数据验证确保必填字段完整性
  - 创建全局异常处理器处理销售相关异常
  - 实现友好的错误信息返回给前端
  - _需求: 6.1, 6.2, 6.3, 6.4, 6.5, 12.5_

- [ ] 16. 实现搜索和筛选功能
  - 实现销售记录的多条件搜索功能
  - 实现按时间范围筛选功能
  - 实现按客户筛选功能
  - 实现按产品筛选功能
  - 实现按销售人员筛选功能
  - 实现按状态筛选功能
  - 优化搜索性能和索引策略
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ] 17. 编写单元测试
  - 创建SalesServiceTest类测试业务逻辑
  - 创建SalesControllerTest类测试API接口
  - 创建SalesMapperTest类测试数据访问层
  - 创建SalesValidationTest类测试数据验证功能
  - 测试统计分析功能的正确性
  - 测试异常处理的完整性
  - 确保测试覆盖率达到80%以上
  - _需求: 6.5, 12.4, 12.5_

- [ ] 18. 创建前端Vue页面组件
  - 创建SalesManagement.vue主页面组件
  - 实现销售记录列表展示功能
  - 实现销售数据录入对话框
  - 实现筛选和搜索功能区域
  - 实现统计面板显示关键指标
  - 实现空状态组件处理无数据情况
  - 确保所有数据通过API获取，不使用模拟数据
  - _需求: 1.1, 1.2, 1.3, 1.4, 3.1, 15.1, 15.2_

- [ ] 19. 实现图表展示组件
  - 创建SalesTrendChart.vue销售趋势图表组件
  - 创建SalesDistributionChart.vue销售分布饼图组件
  - 创建MonthlyBarChart.vue月度销售柱状图组件
  - 创建SalesFunnelChart.vue销售漏斗图组件
  - 使用ECharts实现各种图表类型
  - 支持图表数据的实时更新和交互
  - _需求: 8.1, 8.2, 8.3, 8.4, 8.5_

- [ ] 20. 创建前端TypeScript接口定义
  - 创建SalesRecord接口定义匹配后端实体类
  - 创建Customer接口定义匹配后端客户实体
  - 创建Product接口定义匹配后端产品实体
  - 创建SalesStaff接口定义匹配后端销售人员实体
  - 创建SalesStats接口定义匹配后端统计数据
  - 创建API请求和响应的类型定义
  - 确保前端接口与后端DTO完全一致
  - _需求: 3.1, 5.1, 7.1, 14.1, 14.2_

- [ ] 21. 实现前端API调用服务
  - 创建sales.ts API调用文件
  - 实现getSalesRecords API调用方法
  - 实现createSalesRecord API调用方法
  - 实现updateSalesRecord API调用方法
  - 实现deleteSalesRecord API调用方法
  - 实现getSalesStats API调用方法
  - 实现getChartsData API调用方法
  - 添加错误处理和重试机制
  - _需求: 3.1, 3.2, 4.1, 4.2, 7.1, 8.1, 14.1_

- [ ] 22. 配置前端路由和导航
  - 在路由配置中添加销售数据管理页面路由
  - 确保管理后台导航菜单包含销售数据管理入口
  - 配置路由路径为/admin/business/sales
  - 测试页面访问权限和路由跳转
  - 验证面包屑导航显示正确
  - 确保页面刷新后路由状态保持
  - _需求: 1.1, 1.5_

- [ ] 23. 实现数据导出功能
  - 实现前端导出功能按钮和对话框
  - 支持Excel、CSV、PDF格式导出
  - 实现自定义导出字段选择
  - 实现导出条件筛选功能
  - 添加导出进度提示和下载链接
  - 测试大数据量导出的性能
  - _需求: 13.1, 13.2, 13.3, 13.4, 13.5_

- [ ] 24. 实现批量操作功能
  - 实现销售记录的多选功能
  - 实现批量删除操作
  - 实现批量状态修改操作
  - 实现批量导出操作
  - 添加批量操作确认对话框
  - 实现操作结果提示和列表刷新
  - _需求: 10.1, 10.2, 10.3, 10.4, 10.5_

- [ ] 25. 完善全局异常处理
  - 在现有GlobalExceptionHandler中添加销售相关异常处理
  - 创建SalesRecordNotFoundException异常类
  - 创建SalesValidationException异常类
  - 创建CustomerNotFoundException异常类
  - 创建ProductNotFoundException异常类
  - 实现统一的错误响应格式
  - 添加异常日志记录和监控
  - _需求: 6.1, 6.2, 12.5, 15.1_

- [ ] 26. 实现实时仪表板功能
  - 创建SalesDashboard.vue仪表板组件
  - 实现关键指标卡片展示
  - 实现实时数据自动刷新机制
  - 实现销售目标进度显示
  - 实现异常数据预警功能
  - 实现数据对比和增长率计算
  - _需求: 17.1, 17.2, 17.3, 17.4, 17.5_

- [ ] 27. 数据初始化和真实数据验证
  - ⚠️ 修改DataInitializationService禁止插入示例销售数据
  - 创建客户类型、产品类别、销售人员角色基础数据
  - 创建模拟数据清理脚本remove-mock-sales-data.sql
  - 验证系统启动后sales_records表为空状态
  - 确保所有销售数据必须通过管理后台手动录入
  - 实现真实数据验证机制防止模拟数据插入
  - _需求: 1.1, 15.1, 15.2, 15.3, 15.4, 16.1, 16.2, 16.3, 16.4, 16.5_

- [ ] 28. 系统集成测试和部署验证
  - 执行完整的前后端集成测试
  - 验证所有API接口的正确性和性能
  - 测试图表功能在生产环境的可用性
  - 验证数据库连接和查询性能
  - 测试页面在不同浏览器的兼容性
  - 执行负载测试确保系统稳定性
  - ⚠️ 特别验证：确保系统在无销售数据时正常运行
  - 验证访问地址http://localhost:8081/admin/business/sales正常工作
  - _需求: 12.1, 12.2, 12.3, 12.4, 14.4, 14.5, 15.1, 15.2_