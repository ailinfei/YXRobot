<!--
  公益动态管理组件
  支持公益动态的发布、编辑和管理
-->
<template>
  <div class="charity-news-manager">
    <!-- 操作栏 -->
    <div class="manager-header">
      <div class="header-left">
        <h3>公益动态管理</h3>
        <p class="header-subtitle">发布和管理公益项目动态、成果展示</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          发布动态
        </el-button>
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-select v-model="typeFilter" placeholder="动态类型" clearable @change="handleFilter">
        <el-option label="全部类型" value="" />
        <el-option label="项目进展" value="progress" />
        <el-option label="成果展示" value="achievement" />
        <el-option label="感谢信件" value="thanks" />
        <el-option label="活动报告" value="report" />
        <el-option label="志愿者故事" value="story" />
      </el-select>
      <el-select v-model="statusFilter" placeholder="发布状态" clearable @change="handleFilter">
        <el-option label="全部状态" value="" />
        <el-option label="草稿" value="draft" />
        <el-option label="已发布" value="published" />
        <el-option label="已下线" value="archived" />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索标题或内容"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
        style="width: 200px;"
      />
    </div>

    <!-- 动态列表 -->
    <div class="news-list" v-loading="loading">
      <div
        v-for="news in filteredNews"
        :key="news.id"
        class="news-card"
      >
        <div class="card-header">
          <div class="news-meta">
            <el-tag :type="getTypeTagType(news.type)" size="small">
              {{ getTypeText(news.type) }}
            </el-tag>
            <el-tag :type="getStatusTagType(news.status)" size="small" style="margin-left: 8px;">
              {{ getStatusText(news.status) }}
            </el-tag>
            <span class="publish-date">{{ formatDate(news.publishedAt || news.createdAt) }}</span>
          </div>
          <el-dropdown @command="(command) => handleNewsAction(command, news)" trigger="click">
            <el-button text>
              <el-icon><MoreFilled /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑</el-dropdown-item>
                <el-dropdown-item command="preview">预览</el-dropdown-item>
                <el-dropdown-item command="publish" v-if="news.status === 'draft'">发布</el-dropdown-item>
                <el-dropdown-item command="archive" v-if="news.status === 'published'">下线</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="card-content">
          <div class="news-image" v-if="news.coverImage">
            <el-image
              :src="news.coverImage"
              :alt="news.title"
              fit="cover"
              class="cover-image"
            />
          </div>
          <div class="news-info">
            <h4 class="news-title">{{ news.title }}</h4>
            <p class="news-summary">{{ news.summary }}</p>
            <div class="news-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ news.viewCount }} 浏览
              </span>
              <span class="stat-item">
                <el-icon><Star /></el-icon>
                {{ news.likeCount }} 点赞
              </span>
              <span class="stat-item">
                <el-icon><User /></el-icon>
                {{ news.author }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredNews.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无公益动态">
          <el-button type="primary" @click="showCreateDialog">发布第一条动态</el-button>
        </el-empty>
      </div>
    </div>

    <!-- 创建/编辑动态对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="isEdit ? '编辑动态' : '发布动态'"
      width="80%"
      :before-close="handleEditClose"
      class="news-edit-dialog"
    >
      <el-form
        ref="newsFormRef"
        :model="newsForm"
        :rules="newsRules"
        label-width="100px"
        class="news-form"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="动态类型" prop="type">
              <el-select v-model="newsForm.type" placeholder="选择动态类型" style="width: 100%">
                <el-option label="项目进展" value="progress" />
                <el-option label="成果展示" value="achievement" />
                <el-option label="感谢信件" value="thanks" />
                <el-option label="活动报告" value="report" />
                <el-option label="志愿者故事" value="story" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布状态" prop="status">
              <el-select v-model="newsForm.status" placeholder="选择发布状态" style="width: 100%">
                <el-option label="草稿" value="draft" />
                <el-option label="立即发布" value="published" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="动态标题" prop="title">
          <el-input
            v-model="newsForm.title"
            placeholder="请输入动态标题"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="内容摘要" prop="summary">
          <el-input
            v-model="newsForm.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入内容摘要"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="封面图片">
          <el-upload
            ref="coverUploadRef"
            v-model:file-list="coverFileList"
            :auto-upload="false"
            :limit="1"
            accept="image/*"
            :before-upload="beforeCoverUpload"
            :on-exceed="handleCoverExceed"
            list-type="picture-card"
          >
            <el-icon><Plus /></el-icon>
            <template #tip>
              <div class="el-upload__tip">
                建议尺寸 800x400，支持 JPG/PNG 格式
              </div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="动态内容" prop="content">
          <div class="editor-container">
            <el-input
              v-model="newsForm.content"
              type="textarea"
              :rows="12"
              placeholder="请输入动态内容，支持 Markdown 格式"
              class="content-editor"
            />
            <div class="editor-toolbar">
              <el-button size="small" @click="insertTemplate('progress')">
                插入进展模板
              </el-button>
              <el-button size="small" @click="insertTemplate('achievement')">
                插入成果模板
              </el-button>
              <el-button size="small" @click="insertTemplate('thanks')">
                插入感谢模板
              </el-button>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="标签">
          <el-select
            v-model="newsForm.tags"
            multiple
            filterable
            allow-create
            placeholder="选择或创建标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleEditClose">取消</el-button>
          <el-button @click="saveDraft" :loading="saveLoading">
            保存草稿
          </el-button>
          <el-button type="primary" @click="publishNews" :loading="publishLoading">
            {{ isEdit ? '更新' : '发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="动态预览"
      width="70%"
      class="news-preview-dialog"
    >
      <div v-if="previewNews" class="news-preview">
        <div class="preview-header">
          <h2 class="preview-title">{{ previewNews.title }}</h2>
          <div class="preview-meta">
            <el-tag :type="getTypeTagType(previewNews.type)" size="small">
              {{ getTypeText(previewNews.type) }}
            </el-tag>
            <span class="preview-author">作者：{{ previewNews.author }}</span>
            <span class="preview-date">{{ formatDateTime(previewNews.publishedAt || previewNews.createdAt) }}</span>
          </div>
        </div>

        <div class="preview-cover" v-if="previewNews.coverImage">
          <el-image :src="previewNews.coverImage" :alt="previewNews.title" fit="cover" />
        </div>

        <div class="preview-summary">
          <p>{{ previewNews.summary }}</p>
        </div>

        <div class="preview-content">
          <div class="markdown-content" v-html="formatContent(previewNews.content)"></div>
        </div>

        <div class="preview-tags" v-if="previewNews.tags && previewNews.tags.length > 0">
          <el-tag
            v-for="tag in previewNews.tags"
            :key="tag"
            size="small"
            style="margin-right: 8px; margin-bottom: 8px;"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template><sc
ript setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Refresh,
  Search,
  MoreFilled,
  View,
  Star,
  User
} from '@element-plus/icons-vue'

// 接口定义
interface CharityNews {
  id: string
  title: string
  summary: string
  content: string
  type: string
  status: string
  coverImage?: string
  author: string
  viewCount: number
  likeCount: number
  tags: string[]
  createdAt: string
  publishedAt?: string
  updatedAt?: string
}

// 响应式数据
const loading = ref(false)
const typeFilter = ref('')
const statusFilter = ref('')
const searchKeyword = ref('')
const editDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const saveLoading = ref(false)
const publishLoading = ref(false)
const isEdit = ref(false)

// 动态数据
const newsList = ref<CharityNews[]>([])
const previewNews = ref<CharityNews | null>(null)

// 表单数据
const newsForm = ref({
  title: '',
  summary: '',
  content: '',
  type: '',
  status: 'draft',
  tags: [] as string[]
})

const newsRules = {
  title: [{ required: true, message: '请输入动态标题', trigger: 'blur' }],
  summary: [{ required: true, message: '请输入内容摘要', trigger: 'blur' }],
  content: [{ required: true, message: '请输入动态内容', trigger: 'blur' }],
  type: [{ required: true, message: '请选择动态类型', trigger: 'change' }]
}

const newsFormRef = ref()
const coverUploadRef = ref()
const coverFileList = ref<any[]>([])
const currentEditId = ref<string | null>(null)

// 可用标签
const availableTags = ref([
  '教育公益', '儿童关爱', '社区服务', '志愿者', '捐赠',
  '成果展示', '感谢信', '项目进展', '合作伙伴', '媒体报道'
])

// 计算属性
const filteredNews = computed(() => {
  let filtered = newsList.value

  if (typeFilter.value) {
    filtered = filtered.filter(news => news.type === typeFilter.value)
  }

  if (statusFilter.value) {
    filtered = filtered.filter(news => news.status === statusFilter.value)
  }

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(news =>
      news.title.toLowerCase().includes(keyword) ||
      news.summary.toLowerCase().includes(keyword) ||
      news.content.toLowerCase().includes(keyword)
    )
  }

  return filtered.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
})

// 方法
const loadNews = async () => {
  loading.value = true
  try {
    // 模拟数据
    newsList.value = [
      {
        id: '1',
        title: '北京市第一小学练字机器人项目启动',
        summary: '我们很高兴地宣布，北京市第一小学的练字机器人项目正式启动，将为200名学生提供智能化练字指导。',
        content: `# 项目启动仪式

今天，我们在北京市第一小学举行了练字机器人项目的启动仪式。这个项目将为学校的200名学生提供个性化的练字指导。

## 项目亮点

- 智能化练字指导
- 个性化学习方案
- 实时进度跟踪
- 专业教师指导

## 预期成果

我们期待通过这个项目，能够显著提升学生们的书写水平，培养他们对中华传统文化的兴趣。`,
        type: 'progress',
        status: 'published',
        coverImage: 'https://picsum.photos/800/400?random=10',
        author: '项目团队',
        viewCount: 1250,
        likeCount: 89,
        tags: ['教育公益', '项目进展', '北京'],
        createdAt: '2024-01-15T10:00:00Z',
        publishedAt: '2024-01-15T10:00:00Z'
      },
      {
        id: '2',
        title: '感谢上海浦东社区中心的大力支持',
        summary: '感谢上海浦东社区中心为我们的公益项目提供场地和设备支持，让更多孩子受益。',
        content: `# 感谢信

亲爱的上海浦东社区中心的朋友们：

感谢您们为我们的练字机器人公益项目提供的大力支持！

## 您们的帮助

- 提供了宽敞的活动场地
- 协助设备安装和调试
- 组织社区儿童参与活动
- 提供志愿者支持

## 项目成果

在您们的帮助下，我们成功为50名社区儿童提供了免费的练字指导服务。

再次感谢您们的支持！`,
        type: 'thanks',
        status: 'published',
        coverImage: 'https://picsum.photos/800/400?random=11',
        author: '公益团队',
        viewCount: 856,
        likeCount: 67,
        tags: ['感谢信', '社区服务', '上海'],
        createdAt: '2024-01-20T14:00:00Z',
        publishedAt: '2024-01-20T14:00:00Z'
      },
      {
        id: '3',
        title: '2024年第一季度公益成果报告',
        summary: '回顾第一季度的公益活动成果，我们共服务了500名学生，覆盖10个城市。',
        content: `# 2024年第一季度公益成果报告

## 数据概览

- 服务学生：500名
- 覆盖城市：10个
- 合作机构：15家
- 志愿者参与：120人次

## 主要成果

### 教育成果
- 学生书写水平平均提升30%
- 95%的学生表示对练字产生了兴趣
- 80%的老师认为项目效果显著

### 社会影响
- 媒体报道：20篇
- 社交媒体互动：10000+
- 家长满意度：98%

## 下季度计划

我们将继续扩大项目覆盖范围，预计服务更多学生。`,
        type: 'report',
        status: 'draft',
        author: '数据分析团队',
        viewCount: 0,
        likeCount: 0,
        tags: ['成果展示', '季度报告', '数据分析'],
        createdAt: '2024-02-01T09:00:00Z'
      }
    ]
  } catch (error) {
    console.error('加载动态数据失败:', error)
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const refreshData = () => {
  loadNews()
}

const handleFilter = () => {
  // 筛选逻辑已在计算属性中处理
}

const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const showCreateDialog = () => {
  isEdit.value = false
  currentEditId.value = null
  newsForm.value = {
    title: '',
    summary: '',
    content: '',
    type: '',
    status: 'draft',
    tags: []
  }
  coverFileList.value = []
  editDialogVisible.value = true
}

const handleNewsAction = async (command: string, news: CharityNews) => {
  switch (command) {
    case 'edit':
      isEdit.value = true
      currentEditId.value = news.id
      newsForm.value = {
        title: news.title,
        summary: news.summary,
        content: news.content,
        type: news.type,
        status: news.status,
        tags: [...news.tags]
      }
      if (news.coverImage) {
        coverFileList.value = [{
          name: 'cover.jpg',
          url: news.coverImage
        }]
      } else {
        coverFileList.value = []
      }
      editDialogVisible.value = true
      break
    case 'preview':
      previewNews.value = news
      previewDialogVisible.value = true
      break
    case 'publish':
      try {
        news.status = 'published'
        news.publishedAt = new Date().toISOString()
        ElMessage.success('动态已发布')
      } catch (error) {
        ElMessage.error('发布失败')
      }
      break
    case 'archive':
      try {
        news.status = 'archived'
        ElMessage.success('动态已下线')
      } catch (error) {
        ElMessage.error('下线失败')
      }
      break
    case 'delete':
      try {
        await ElMessageBox.confirm('确定要删除这条动态吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const index = newsList.value.findIndex(n => n.id === news.id)
        if (index > -1) {
          newsList.value.splice(index, 1)
        }
        ElMessage.success('动态已删除')
      } catch {
        // 用户取消
      }
      break
  }
}

const beforeCoverUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleCoverExceed = () => {
  ElMessage.warning('只能上传一张封面图片')
}

const insertTemplate = (type: string) => {
  const templates = {
    progress: `# 项目进展更新

## 本期亮点
- 

## 数据统计
- 

## 下期计划
- `,
    achievement: `# 成果展示

## 主要成果
- 

## 数据对比
- 

## 用户反馈
- `,
    thanks: `# 感谢信

亲爱的朋友们：

感谢您们对我们公益项目的支持！

## 您们的帮助
- 

## 项目成果
- 

再次感谢您们的支持！`
  }

  const template = templates[type as keyof typeof templates]
  if (template) {
    newsForm.value.content = template
  }
}

const saveDraft = async () => {
  try {
    await newsFormRef.value?.validate()
    saveLoading.value = true
    
    // 模拟保存
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newsData = {
      ...newsForm.value,
      status: 'draft'
    }
    
    if (isEdit.value && currentEditId.value) {
      // 更新现有动态
      const index = newsList.value.findIndex(n => n.id === currentEditId.value)
      if (index > -1) {
        Object.assign(newsList.value[index], newsData, {
          updatedAt: new Date().toISOString()
        })
      }
    } else {
      // 创建新动态
      const newNews: CharityNews = {
        id: Date.now().toString(),
        ...newsData,
        author: '管理员',
        viewCount: 0,
        likeCount: 0,
        createdAt: new Date().toISOString()
      }
      newsList.value.unshift(newNews)
    }
    
    ElMessage.success('草稿已保存')
    handleEditClose()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    saveLoading.value = false
  }
}

const publishNews = async () => {
  try {
    await newsFormRef.value?.validate()
    publishLoading.value = true
    
    // 模拟发布
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newsData = {
      ...newsForm.value,
      status: 'published',
      publishedAt: new Date().toISOString()
    }
    
    if (isEdit.value && currentEditId.value) {
      // 更新现有动态
      const index = newsList.value.findIndex(n => n.id === currentEditId.value)
      if (index > -1) {
        Object.assign(newsList.value[index], newsData, {
          updatedAt: new Date().toISOString()
        })
      }
    } else {
      // 创建新动态
      const newNews: CharityNews = {
        id: Date.now().toString(),
        ...newsData,
        author: '管理员',
        viewCount: 0,
        likeCount: 0,
        createdAt: new Date().toISOString()
      }
      newsList.value.unshift(newNews)
    }
    
    ElMessage.success(isEdit.value ? '动态已更新' : '动态已发布')
    handleEditClose()
  } catch (error) {
    console.error('发布失败:', error)
  } finally {
    publishLoading.value = false
  }
}

const handleEditClose = () => {
  editDialogVisible.value = false
  newsForm.value = {
    title: '',
    summary: '',
    content: '',
    type: '',
    status: 'draft',
    tags: []
  }
  coverFileList.value = []
  currentEditId.value = null
  isEdit.value = false
}

// 工具方法
const getTypeTagType = (type: string) => {
  const types: Record<string, any> = {
    progress: 'primary',
    achievement: 'success',
    thanks: 'warning',
    report: 'info',
    story: 'default'
  }
  return types[type] || 'default'
}

const getTypeText = (type: string) => {
  const texts: Record<string, string> = {
    progress: '项目进展',
    achievement: '成果展示',
    thanks: '感谢信件',
    report: '活动报告',
    story: '志愿者故事'
  }
  return texts[type] || type
}

const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    draft: 'info',
    published: 'success',
    archived: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    draft: '草稿',
    published: '已发布',
    archived: '已下线'
  }
  return texts[status] || status
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatContent = (content: string) => {
  // 简单的 Markdown 转 HTML
  return content
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^\- (.*$)/gim, '<li>$1</li>')
    .replace(/\n/g, '<br>')
}

// 生命周期
onMounted(() => {
  loadNews()
})
</script>

<style lang="scss" scoped>
.charity-news-manager {
  .manager-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      h3 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 18px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
      }
    }
    
    .header-right {
      display: flex;
      gap: 12px;
    }
  }

  .filter-bar {
    display: flex;
    gap: 12px;
    align-items: center;
    margin-bottom: 24px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
  }

  .news-list {
    display: grid;
    gap: 20px;

    .news-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px;
        border-bottom: 1px solid #f0f0f0;

        .news-meta {
          display: flex;
          align-items: center;
          gap: 8px;

          .publish-date {
            color: #666;
            font-size: 12px;
            margin-left: 8px;
          }
        }
      }

      .card-content {
        display: flex;
        padding: 16px;
        gap: 16px;

        .news-image {
          flex-shrink: 0;
          width: 200px;
          height: 120px;
          border-radius: 8px;
          overflow: hidden;

          .cover-image {
            width: 100%;
            height: 100%;
          }
        }

        .news-info {
          flex: 1;

          .news-title {
            margin: 0 0 8px 0;
            font-size: 16px;
            font-weight: 600;
            color: #262626;
            line-height: 1.4;
          }

          .news-summary {
            margin: 0 0 12px 0;
            color: #666;
            font-size: 14px;
            line-height: 1.6;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          .news-stats {
            display: flex;
            gap: 16px;

            .stat-item {
              display: flex;
              align-items: center;
              gap: 4px;
              color: #999;
              font-size: 12px;
            }
          }
        }
      }
    }

    .empty-state {
      padding: 40px;
      text-align: center;
    }
  }

  .news-edit-dialog {
    .news-form {
      .editor-container {
        position: relative;

        .content-editor {
          font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
        }

        .editor-toolbar {
          margin-top: 8px;
          display: flex;
          gap: 8px;
        }
      }
    }

    .dialog-footer {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
    }
  }

  .news-preview-dialog {
    .news-preview {
      .preview-header {
        margin-bottom: 24px;

        .preview-title {
          margin: 0 0 12px 0;
          font-size: 24px;
          font-weight: 600;
          color: #262626;
        }

        .preview-meta {
          display: flex;
          align-items: center;
          gap: 12px;
          color: #666;
          font-size: 14px;
        }
      }

      .preview-cover {
        margin-bottom: 24px;
        border-radius: 8px;
        overflow: hidden;

        .el-image {
          width: 100%;
          height: 300px;
        }
      }

      .preview-summary {
        margin-bottom: 24px;
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        border-left: 4px solid #409eff;

        p {
          margin: 0;
          color: #666;
          font-size: 16px;
          line-height: 1.6;
        }
      }

      .preview-content {
        margin-bottom: 24px;

        .markdown-content {
          line-height: 1.8;
          color: #262626;

          :deep(h1) {
            font-size: 20px;
            margin: 24px 0 16px 0;
            color: #262626;
          }

          :deep(h2) {
            font-size: 18px;
            margin: 20px 0 12px 0;
            color: #262626;
          }

          :deep(h3) {
            font-size: 16px;
            margin: 16px 0 8px 0;
            color: #262626;
          }

          :deep(li) {
            margin: 4px 0;
            list-style: disc;
            margin-left: 20px;
          }
        }
      }

      .preview-tags {
        padding-top: 16px;
        border-top: 1px solid #f0f0f0;
      }
    }
  }
}

@media (max-width: 768px) {
  .charity-news-manager {
    .filter-bar {
      flex-direction: column;
      align-items: stretch;
      gap: 8px;
    }

    .news-list {
      .news-card {
        .card-content {
          flex-direction: column;

          .news-image {
            width: 100%;
            height: 200px;
          }
        }
      }
    }
  }
}
</style>