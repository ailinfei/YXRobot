# YXRobot管理后台Mock数据系统

## 📋 概述

这是一个为YXRobot管理后台提供的完整Mock数据系统，包含11个主要业务模块的模拟数据和API接口。系统提供了丰富、真实的测试数据，支持前端开发和测试工作。

## 🚀 快速开始

### 初始化Mock数据系统

```typescript
import { initializeMockData, getMockDataStats } from '@/api/mock'

// 初始化Mock数据系统
initializeMockData()

// 获取数据统计
const stats = getMockDataStats()
console.log('Mock数据统计:', stats)
```

### 使用单个模块的Mock API

```typescript
import { 
  mockDashboardAPI,
  mockSalesAPI,
  mockDeviceAPI 
} from '@/api/mock'

// 获取数据看板统计
const dashboardStats = await mockDashboardAPI.getDashboardStats()

// 获取销售订单列表
const salesOrders = await mockSalesAPI.getSalesOrders({
  page: 1,
  pageSize: 10,
  status: 'delivered'
})

// 获取设备列表
const devices = await mockDeviceAPI.getDevices({
  page: 1,
  pageSize: 20,
  status: 'online'
})
```

### 使用综合管理后台API

```typescript
import { mockAdminDashboardAPI } from '@/api/mock'

// 获取完整的管理后台数据
const completeData = await mockAdminDashboardAPI.getCompleteAdminData()

// 获取数据看板汇总
const dashboardSummary = await mockAdminDashboardAPI.getDashboardSummary()

// 获取系统概览（用于首页展示）
const systemOverview = await mockAdminDashboardAPI.getSystemOverview()
```

## 📊 模块说明

### 1. 数据看板 (mockDashboardAPI)
- `getDashboardStats()` - 获取仪表板统计数据
- `getRecentActivities(params)` - 获取最近活动
- `getTrendData(params)` - 获取趋势数据
- `getTopPerformers()` - 获取顶级表现者数据

### 2. 销售管理 (mockSalesAPI)
- `getSalesStats()` - 获取销售统计数据
- `getSalesOrders(params)` - 获取销售订单列表
- `getRegionSalesData()` - 获取地区销售分布
- `getChannelSalesData()` - 获取渠道销售数据
- `getSalesTrendData(params)` - 获取销售趋势数据
- `getProductSalesRanking()` - 获取产品销售排行

### 3. 租赁管理 (mockRentalAPI)
- `getRentalStats()` - 获取租赁统计数据
- `getRentalOrders(params)` - 获取租赁订单列表
- `getDeviceUtilizationData(params)` - 获取设备利用率数据
- `getRentalTrendData(params)` - 获取租赁趋势数据
- `getRentalRevenueAnalysis(params)` - 获取租赁收入分析数据

### 4. 客户管理 (mockCustomerAPI)
- `getCustomers(params)` - 获取客户列表
- `getCustomerDetail(customerId)` - 获取客户详情
- `getCustomerDevices(customerId)` - 获取客户设备关联
- `getCustomerOrders(customerId, params)` - 获取客户订单历史
- `createCustomer(customerData)` - 创建客户
- `updateCustomer(customerId, customerData)` - 更新客户
- `deleteCustomer(customerId)` - 删除客户

### 5. 设备管理 (mockDeviceAPI)
- `getDevices(params)` - 获取设备列表
- `getDeviceDetail(deviceId)` - 获取设备详情
- `getDeviceStats()` - 获取设备统计数据
- `getDeviceAlerts(params)` - 获取设备告警列表
- `getDeviceUsageData(deviceId, params)` - 获取设备使用数据
- `getFirmwareVersions()` - 获取固件版本列表
- `remoteControlDevice(deviceId, action, params)` - 远程控制设备
- `updateDeviceFirmware(deviceId, firmwareVersion)` - 更新设备固件

### 6. 产品管理 (mockProductAPI)
- `getProducts(params)` - 获取产品列表
- `getProductDetail(productId)` - 获取产品详情
- `getProductReviews(productId, params)` - 获取产品评价
- `getProductCategories()` - 获取产品分类
- `createProduct(productData)` - 创建产品
- `updateProduct(productId, productData)` - 更新产品
- `deleteProduct(productId)` - 删除产品

### 7. 公益管理 (mockCharityAPI)
- `getCharityStats()` - 获取公益统计数据
- `getCharityProjects(params)` - 获取公益项目列表
- `getCharityInstitutions(params)` - 获取合作机构列表
- `getCharityActivities(params)` - 获取公益活动列表
- `getCharityDonations(params)` - 获取捐赠记录列表
- `getCharityVolunteers(params)` - 获取志愿者列表

### 8. 平台链接管理 (mockPlatformLinkAPI)
- `getPlatformLinks(params)` - 获取平台链接列表
- `getPlatformLinkDetail(linkId)` - 获取平台链接详情
- `getPlatformRegions()` - 获取平台地区列表
- `getLinkAnalytics(linkId, params)` - 获取链接分析数据
- `getLinkStatsSummary()` - 获取链接统计汇总
- `checkLinkValidity(linkId)` - 检查链接有效性

### 9. 课程管理 (mockCourseAPI)
- `getCourses(params)` - 获取课程列表
- `getCourseDetail(courseId)` - 获取课程详情
- `getCourseCharacters(courseId)` - 获取课程汉字列表
- `getCharacterTranslations(characterId)` - 获取汉字多语言释义
- `createCourse(courseData)` - 创建课程
- `updateCourse(courseId, courseData)` - 更新课程

### 10. AI字体包管理 (mockFontPackageAPI)
- `getFontPackages(params)` - 获取字体包列表
- `getFontPackageDetail(packageId)` - 获取字体包详情
- `getFontSamples(packageId, params)` - 获取字体样本列表
- `getGenerationTasks(packageId)` - 获取生成任务列表
- `getFontPreviews(packageId, params)` - 获取字体预览列表
- `createFontPackage(packageData)` - 创建字体包
- `startGenerationTask(packageId, taskData)` - 开始生成任务

### 11. 多语言管理 (mockLanguageAPI)
- `getLanguages()` - 获取语言配置列表
- `getRobotInterfaceTexts(params)` - 获取机器人界面文字列表
- `getWebsiteContents(params)` - 获取官网多语言内容列表
- `getTranslationProgress()` - 获取翻译进度
- `updateLanguage(languageId, languageData)` - 更新语言配置
- `batchTranslate(translateData)` - 批量翻译

## 🔧 配置选项

### Mock配置

```typescript
export const mockConfig = {
  // 是否启用Mock数据
  enabled: true,
  
  // API延迟配置（毫秒）
  delay: {
    default: 500,
    short: 200,
    long: 1000,
    upload: 2000
  },
  
  // 分页配置
  pagination: {
    defaultPageSize: 10,
    maxPageSize: 100
  },
  
  // 数据生成配置
  generation: {
    customers: 200,
    courses: 100,
    fontPackages: 50,
    robotTexts: 50,
    websiteContents: 100,
    products: 30,
    platformLinks: 60,
    devices: 150,
    charityProjects: 50,
    salesOrders: 500,
    rentalOrders: 200
  }
}
```

### 数据生成规模

```typescript
// 获取数据统计
const stats = getMockDataStats()

// 输出示例：
{
  dashboard: { totalModules: 11, totalAPIs: 50, totalDataPoints: 1000 },
  business: {
    sales: { orders: 500, revenue: '8M-15M', regions: 20, channels: 8 },
    rental: { orders: 200, devices: 150, utilization: '68-88%' },
    customers: { total: 200, segments: 4, levels: 4 }
  },
  content: {
    products: { total: 30, categories: 7, reviews: '10-50 per product' },
    charity: { projects: 50, institutions: 80, activities: 150, volunteers: 120 },
    platformLinks: { total: 60, regions: 10, platforms: 16 }
  },
  system: {
    courses: { total: 100, characters: '15-50 per course', translations: '3-6 languages per character' },
    fontPackages: { total: 50, samples: '30-100 per package', accuracy: '88-96%' },
    languages: { supported: 12, robotTexts: 50, websiteContents: 100, completeness: '75-92%' },
    devices: { total: 150, statuses: 5, alerts: '15-50', firmwareVersions: 10 }
  },
  totals: { dataRecords: 1660, apiEndpoints: 50, mockServices: 11, dataCategories: 4 }
}
```

## 🧪 测试

### 运行测试

```typescript
import { mockTestSuite } from '@/api/mock/test'

// 完整测试所有API
const result = await mockTestSuite.testAllAPIs()

// 性能测试
const perfResult = await mockTestSuite.testPerformance()

// 快速测试
const quickResult = await mockTestSuite.quickTest()
```

### 测试结果示例

```
🧪 开始测试YXRobot管理后台Mock数据系统...
✅ 数据看板API测试通过
✅ 销售管理API测试通过
✅ 租赁管理API测试通过
✅ 客户管理API测试通过
✅ 设备管理API测试通过
✅ 产品管理API测试通过
✅ 公益管理API测试通过
✅ 平台链接API测试通过
✅ 课程管理API测试通过
✅ 字体包管理API测试通过
✅ 多语言管理API测试通过
✅ 综合管理后台API测试通过
🎉 所有Mock API测试完成！
```

## 📈 数据特点

### 数据真实性
- 模拟真实业务场景的数据分布
- 包含合理的数据关联关系
- 提供多样化的状态和类型

### 数据丰富性
- 总计超过1660个数据记录
- 涵盖11个主要业务模块
- 50个API接口
- 1000+个数据点

### 数据一致性
- 各模块数据之间保持逻辑关联
- 统计数据与详细数据保持一致
- 时间序列数据具有合理的趋势

## 🔍 API响应格式

所有Mock API都遵循统一的响应格式：

```typescript
{
  code: 200,           // 状态码
  message: 'success',  // 响应消息
  data: any,          // 响应数据
  timestamp: number   // 时间戳
}
```

### 分页响应格式

```typescript
{
  code: 200,
  message: 'success',
  data: {
    list: any[],        // 数据列表
    total: number,      // 总数量
    page: number,       // 当前页码
    pageSize: number,   // 每页大小
    totalPages: number  // 总页数
  },
  timestamp: number
}
```

## 🛠 工具函数

### 通用工具

```typescript
import { mockUtils } from '@/api/mock'

// 生成随机数
const randomNum = mockUtils.randomBetween(1, 100)

// 生成随机浮点数
const randomFloat = mockUtils.randomFloat(1.0, 10.0, 2)

// 生成随机日期
const randomDate = mockUtils.generateRandomDate(30)

// 模拟API延迟
await mockUtils.mockDelay(1000)

// 创建Mock响应
const response = mockUtils.createMockResponse(data, 200, 'success')

// 应用分页
const paginatedData = mockUtils.applyPagination(dataArray, 1, 10)

// 应用关键词筛选
const filteredData = mockUtils.applyKeywordFilter(dataArray, 'keyword', ['name', 'description'])
```

## 📝 注意事项

1. **性能考虑**：完整数据生成可能需要1-2秒时间，建议按需使用
2. **内存使用**：完整数据集约占用50-100MB内存
3. **数据更新**：每次调用API都会生成新的随机数据
4. **类型安全**：所有接口都提供了完整的TypeScript类型定义
5. **错误处理**：Mock API包含了错误情况的模拟

## 🚀 扩展开发

### 添加新的Mock服务

1. 创建新的Mock服务文件
2. 定义数据接口和生成器
3. 实现Mock API函数
4. 在index.ts中导出
5. 更新配置和测试

### 自定义数据生成

```typescript
// 自定义数据生成器
class CustomMockService {
  static generateCustomData(count: number) {
    // 实现自定义数据生成逻辑
    return []
  }
}

// 自定义Mock API
export const mockCustomAPI = {
  async getCustomData(params: any) {
    await mockDelay()
    const data = CustomMockService.generateCustomData(params.count || 10)
    return createMockResponse(data)
  }
}
```

## 📞 支持

如有问题或建议，请联系开发团队或提交Issue。

---

**YXRobot管理后台Mock数据系统** - 为前端开发提供完整、真实、丰富的测试数据支持。