# Admin Content - 新闻管理页面实施任务

## 🚨 核心开发要求（强制执行）

### ⚠️ 前端页面与数据库数据完全绑定规范

**本需求文档的核心要求是实现前端页面与数据库数据的完全绑定，确保使用真实数据，并能在前端页面正常显示：**

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
    <el-table :data="newsList" v-loading="loading">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="publishTime" label="发布时间" />
      <el-table-column prop="status" label="状态" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getNewsList } from '@/api/news'
import type { NewsItem } from '@/types/news'

// ✅ 正确：响应式数据绑定
const newsList = ref<NewsItem[]>([])
const loading = ref(false)

// ✅ 正确：通过API获取真实数据
const loadData = async () => {
  loading.value = true
  try {
    const response = await getNewsList()
    newsList.value = response.data // 绑定API返回的真实数据
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
    <el-table :data="mockNewsList">
      <el-table-column prop="title" label="标题" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
// ❌ 错误：硬编码模拟数据
const mockNewsList = [
  { id: 1, title: '示例新闻1', status: 'published' },
  { id: 2, title: '示例新闻2', status: 'draft' }
]
// ❌ 错误：不调用API获取真实数据
</script>
```

**🚨 字段映射一致性开发规范（每个任务开发时必须遵守）**

项目开发过程中前端使用的字段名和后端的字段名要匹配，字段映射要正确：

**开发时强制执行的字段映射规范：**
- 数据库字段：snake_case（如：`news_title`, `publish_time`, `is_featured`）
- Java实体类：camelCase（如：`newsTitle`, `publishTime`, `isFeatured`）
- MyBatis映射：`<result column="news_title" property="newsTitle"/>`
- 前端接口：camelCase（如：`newsTitle: string`, `publishTime: string`）

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

- [x] 1. 创建新闻管理数据库表结构







  - 创建news表用于存储新闻信息
  - 创建news_categories表用于存储新闻分类
  - 创建news_tags表用于存储新闻标签




  - 创建news_tag_relations表用于存储新闻标签关联关系
  - 创建news_interactions表用于存储新闻互动数据
  - 添加必要的索引和约束确保数据完整性
  - 插入初始分类和标签数据
  - _需求: 1.1, 2.1, 8.1, 12.1_

- [x] 2. 实现新闻实体类和DTO


  - 创建News实体类映射news表





  - 创建NewsCategory实体类映射news_categories表
  - 创建NewsTag实体类映射news_tags表



  - 创建NewsTagRelation实体类映射news_tag_relations表
  - 创建NewsInteraction实体类映射news_interactions表
  - 实现对应的DTO类用于数据传输
  - 添加数据验证注解确保数据完整性
  - 定义新闻状态和互动类型枚举
  - _需求: 5.1, 6.1, 6.2, 6.3, 6.4, 6.5_



- [x] 3. 创建MyBatis映射器和XML配置
  - 实现NewsMapper接口和XML映射文件
  - 实现NewsCategoryMapper接口和XML映射文件
  - 实现NewsTagMapper接口和XML映射文件
  - 实现NewsTagRelationMapper接口和XML映射文件
  - 实现NewsInteractionMapper接口和XML映射文件
  - 编写复杂查询SQL支持搜索、筛选和统计功能
  - 实现分页查询和条件筛选功能
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5, 10.1, 10.2_

- [x] 4. 实现新闻管理服务层
  - 创建NewsService类处理新闻管理业务逻辑
  - 实现getNewsList方法支持分页查询和条件筛选
  - 实现createNews方法创建新的新闻
  - 实现updateNews方法更新新闻信息
  - 实现deleteNews方法删除新闻（软删除）
  - 实现getNewsById方法获取新闻详情
  - 添加数据验证逻辑确保新闻数据的完整性
  - _需求: 3.1, 3.2, 4.1, 4.2, 5.1, 5.2, 5.3_

- [x] 5. 实现新闻状态管理功能
  - 创建NewsStatusService类处理新闻状态管理业务逻辑
  - 实现publishNews方法发布新闻
  - 实现offlineNews方法下线新闻
  - 实现状态转换验证逻辑
  - 实现批量状态更新功能
  - 添加状态变更日志记录
  - _需求: 4.3, 4.4, 4.5, 9.1, 9.2, 11.1, 11.2_

- [x] 6. 实现新闻分类和标签管理
  - 创建NewsCategoryService类处理分类管理业务逻辑
  - 实现getNewsCategories方法获取分类列表
  - 创建NewsTagService类处理标签管理业务逻辑
  - 实现getNewsTags方法获取标签列表
  - 实现createTag方法创建新标签
  - 实现标签使用次数统计功能
  - _需求: 2.3, 8.1, 8.2, 8.3, 8.4, 8.5_

- [x] 7. 实现文件上传服务功能
  - 创建NewsFileUploadService类处理新闻图片上传
  - 实现uploadNewsImage方法上传新闻封面图片
  - 实现图片格式和大小验证
  - 实现图片缩略图生成功能


  - 添加文件存储路径管理
  - 实现文件删除和清理功能
  - _需求: 7.1, 7.2, 7.3, 7.4, 7.5_

- [x] 8. 实现新闻统计服务功能
  - 创建NewsStatsService类处理新闻统计业务逻辑
  - 实现getNewsStats方法获取基础统计数据
  - 实现getNewsViewStats方法获取浏览量统计

  - 实现getNewsInteractionStats方法获取互动统计

  - 实现按时间段统计功能
  - 添加统计数据缓存支持提升查询性能
  - _需求: 12.1, 12.2, 12.3, 12.4, 15.1, 15.2_

- [x] 9. 实现新闻互动数据跟踪

  - 创建NewsInteractionService类处理互动数据业务逻辑
  - 实现recordView方法记录新闻浏览事件

  - 实现recordLike方法记录新闻点赞事件
  - 实现recordComment方法记录新闻评论事件
  - 实现getInteractionStats方法获取互动统计
  - 支持按用户、时间等维度统计
  - _需求: 15.1, 15.2, 15.3, 15.4, 15.5_


- [x] 10. 实现新闻管理控制器
  - 创建NewsController类处理HTTP请求
  - 实现GET /api/news接口获取新闻列表
  - 实现POST /api/news接口创建新闻
  - 实现PUT /api/news/{id}接口更新新闻
  - 实现DELETE /api/news/{id}接口删除新闻
  - 实现GET /api/news/{id}接口获取新闻详情
  - 添加请求参数验证和异常处理
  - _需求: 3.1, 3.2, 4.1, 4.2, 5.1, 5.2, 5.3_

- [x] 11. 实现新闻状态管理控制器接口


  - 实现POST /api/news/{id}/publish接口发布新闻
  - 实现POST /api/news/{id}/offline接口下线新闻
  - 实现POST /api/news/batch接口批量操作新闻
  - 添加状态转换的参数验证和权限检查
  - 实现状态变更后的统计数据更新
  - _需求: 4.3, 4.4, 4.5, 9.3, 9.4, 11.3, 11.4_




- [x] 12. 实现辅助功能控制器接口
  - 实现GET /api/news/categories接口获取新闻分类
  - 实现GET /api/news/stats接口获取统计数据
  - 实现POST /api/news/upload-image接口上传图片
  - 实现GET /api/news/hot接口获取热门新闻
  - 实现GET /api/news/{id}/related接口获取相关新闻
  - 支持统计数据的实时更新
  - _需求: 2.3, 7.5, 12.1, 12.4, 14.1, 14.2_

- [x] 13. 实现新闻互动控制器接口
  - 实现POST /api/news/{id}/view接口记录浏览量





  - 实现POST /api/news/{id}/like接口记录点赞
  - 实现POST /api/news/{id}/collect接口收藏新闻
  - 实现互动数据的实时统计更新
  - 添加防刷机制和频率限制
  - _需求: 15.1, 15.2, 15.3, 15.4, 15.5_


- [x] 14. 实现数据验证和异常处理
  - 创建NewsException异常类处理业务异常
  - 实现新闻标题和内容格式验证逻辑
  - 实现新闻状态转换验证
  - 实现表单数据验证确保必填字段完整性
  - 创建全局异常处理器处理新闻相关异常
  - 实现友好的错误信息返回给前端
  - _需求: 6.1, 6.2, 6.3, 6.4, 6.5, 13.5_

- [x] 15. 实现搜索和筛选功能
  - 实现新闻标题和内容的全文搜索
  - 实现按分类筛选功能


  - 实现按状态筛选功能
  - 实现按作者筛选功能
  - 实现按发布时间范围筛选
  - 优化搜索性能和索引策略
  - _需求: 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 16. 编写单元测试


  - 创建NewsServiceTest类测试业务逻辑
  - 创建NewsControllerTest类测试API接口
  - 创建NewsMapperTest类测试数据访问层
  - 创建NewsValidationTest类测试数据验证功能
  - 测试文件上传功能的正确性
  - 测试异常处理的完整性


  - 确保测试覆盖率达到80%以上
  - _需求: 6.5, 13.4, 13.5_





- [x] 18. 创建前端TypeScript接口定义
  - 创建NewsItem接口定义匹配后端News实体类
  - 创建NewsCategory接口定义匹配后端分类实体
  - 创建NewsTag接口定义匹配后端标签实体
  - 创建NewsStats接口定义匹配后端统计数据
  - 创建API请求和响应的类型定义
  - 确保前端接口与后端DTO完全一致
  - _需求: 3.1, 5.1, 12.1, 16.1, 16.2_

- [x] 19. 配置前端路由和导航
  - 验证新闻管理页面路由配置正确
  - 确保管理后台导航菜单包含新闻管理入口
  - 测试页面访问权限和路由跳转
  - 验证面包屑导航显示正确
  - 确保页面刷新后路由状态保持
  - _需求: 1.1, 1.5, 16.5_

- [x] 20. 完善全局异常处理
  - 在现有GlobalExceptionHandler中添加新闻相关异常处理
  - 创建NewsNotFoundException异常类
  - 创建NewsValidationException异常类
  - 创建NewsStatusException异常类
  - 实现统一的错误响应格式
  - 添加异常日志记录和监控
  - _需求: 6.1, 6.2, 13.5, 14.1_

- [x] 22. 数据初始化和真实数据验证
  - ⚠️ 修改DataInitializationService禁止插入示例新闻数据
  - 创建新闻分类和标签基础数据（仅必要的分类和标签）
  - 创建模拟数据清理脚本remove-mock-news-data.sql
  - 验证系统启动后news表为空状态
  - 确保所有新闻数据必须通过管理后台手动创建
  - 实现真实数据验证机制防止模拟数据插入
  - _需求: 1.1, 17.1, 17.2, 17.3, 17.4, 18.1, 18.2, 18.3, 18.4, 18.5_



- [x] 17. 系统集成测试和部署验证
  - 执行完整的前后端集成测试
  - 验证所有API接口的正确性和性能
  - 测试文件上传功能在生产环境的可用性
  - 验证数据库连接和查询性能
  - 测试页面在不同浏览器的兼容性
  - 执行负载测试确保系统稳定性
  - ⚠️ 特别验证：确保系统在无新闻数据时正常运行
  - _需求: 13.1, 13.2, 13.3, 13.4, 16.4, 16.5, 17.1, 17.2_