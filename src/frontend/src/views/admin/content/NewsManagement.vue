<template>
  <div class="news-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <h1 class="page-title">新闻管理</h1>
          <p class="page-description">444管理官网新闻内容，包括发布、编辑和删除新闻文章</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="showCreateDialog" :icon="Plus">
            发布新闻
          </el-button>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-card>
        <div class="filter-content">
          <div class="filter-left">
            <el-input
              v-model="searchQuery"
              placeholder="搜索新闻标题或内容"
              :prefix-icon="Search"
              clearable
              class="search-input"
              @input="handleSearch"
            />
            <el-select
              v-model="filterCategory"
              placeholder="选择分类"
              clearable
              class="category-select"
              @change="handleFilter"
            >
              <el-option label="全部分类" value="" />
              <el-option 
                v-for="category in categories" 
                :key="category.id" 
                :label="category.name" 
                :value="category.id" 
              />
            </el-select>
            <el-select
              v-model="filterStatus"
              placeholder="选择状态"
              clearable
              class="status-select"
              @change="handleFilter"
            >
              <el-option label="全部状态" value="" />
              <el-option label="已发布" value="PUBLISHED" />
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已下线" value="OFFLINE" />
            </el-select>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 新闻列表 -->
    <div class="news-list">
      <el-card>
        <el-table
          :data="paginatedNews"
          v-loading="loading"
          stripe
          class="news-table"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column label="新闻信息" min-width="300">
            <template #default="{ row }">
              <div class="news-info">
                <div class="news-image">
                  <img :src="row.coverImage || row.image" :alt="row.title" />
                </div>
                <div class="news-content">
                  <h4 class="news-title">{{ row.title }}</h4>
                  <p class="news-excerpt">{{ row.excerpt }}</p>
                  <div class="news-meta">
                    <el-tag :type="getCategoryTagType(row.categoryName || row.category)" size="small">
                      {{ row.categoryName || row.category }}
                    </el-tag>
                    <span class="news-author">作者：{{ row.author }}</span>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="数据统计" width="120" align="center">
            <template #default="{ row }">
              <div class="news-stats">
                <div class="stat-item">
                  <el-icon><View /></el-icon>
                  <span>{{ row.views }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><ChatRound /></el-icon>
                  <span>{{ row.comments }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ row.likes }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="发布时间" width="120" align="center">
            <template #default="{ row }">
              <div class="date-info">
                <div>{{ formatDate(row.publishTime || row.createdAt) }}</div>
                <div class="time-text">{{ formatTime(row.publishTime || row.createdAt) }}</div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  @click="editNews(row)"
                  :icon="Edit"
                >
                  编辑
                </el-button>
                <el-dropdown @command="(command) => handleAction(command, row)">
                  <el-button size="small" :icon="More" />
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="view" :icon="View">
                        预览
                      </el-dropdown-item>
                      <el-dropdown-item 
                        :command="(row.status === 'PUBLISHED' || row.status === 'published') ? 'offline' : 'publish'"
                        :icon="(row.status === 'PUBLISHED' || row.status === 'published') ? Hide : Upload"
                      >
                        {{ (row.status === 'PUBLISHED' || row.status === 'published') ? '下线' : '发布' }}
                      </el-dropdown-item>
                      <el-dropdown-item command="duplicate" :icon="CopyDocument">
                        复制
                      </el-dropdown-item>
                      <el-dropdown-item command="delete" :icon="Delete" divided>
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalNews"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 新闻编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑新闻' : '发布新闻'"
      width="80%"
      :close-on-click-modal="false"
    >
      <el-form
        ref="newsFormRef"
        :model="newsForm"
        :rules="newsFormRules"
        label-width="100px"
        class="news-form"
      >
        <el-row :gutter="20">
          <el-col :span="16">
            <el-form-item label="新闻标题" prop="title">
              <el-input
                v-model="newsForm.title"
                placeholder="请输入新闻标题"
                maxlength="100"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="新闻摘要" prop="excerpt">
              <el-input
                v-model="newsForm.excerpt"
                type="textarea"
                :rows="3"
                placeholder="请输入新闻摘要"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item label="新闻内容" prop="content">
              <div class="editor-container">
                <el-input
                  v-model="newsForm.content"
                  type="textarea"
                  :rows="15"
                  placeholder="请输入新闻内容（支持HTML格式）"
                />
              </div>
            </el-form-item>
          </el-col>
          
          <el-col :span="8">
            <el-form-item label="新闻分类" prop="categoryId">
              <el-select v-model="newsForm.categoryId" placeholder="选择分类" class="full-width">
                <el-option 
                  v-for="category in categories" 
                  :key="category.id" 
                  :label="category.name" 
                  :value="category.id" 
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="作者" prop="author">
              <el-input v-model="newsForm.author" placeholder="请输入作者姓名" />
            </el-form-item>
            
            <el-form-item label="发布状态" prop="status">
              <el-radio-group v-model="newsForm.status">
                <el-radio label="DRAFT">草稿</el-radio>
                <el-radio label="PUBLISHED">发布</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="封面图片" prop="coverImage">
              <div class="image-upload">
                <el-upload
                  class="image-uploader"
                  :show-file-list="false"
                  :on-success="handleImageSuccess"
                  :before-upload="beforeImageUpload"
                  action="#"
                  :http-request="uploadImage"
                >
                  <img v-if="newsForm.coverImage || newsForm.image" :src="newsForm.coverImage || newsForm.image" class="uploaded-image" />
                  <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="upload-tip">
                  建议尺寸：800x500px，支持JPG、PNG格式
                </div>
              </div>
            </el-form-item>
            
            <el-form-item label="标签" prop="tags">
              <el-select
                v-model="newsForm.tags"
                multiple
                filterable
                allow-create
                placeholder="选择或输入标签"
                class="full-width"
              >
                <el-option
                  v-for="tag in tags"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.name"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="发布时间">
              <el-date-picker
                v-model="newsForm.publishTime"
                type="datetime"
                placeholder="选择发布时间"
                class="full-width"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button @click="saveDraft">保存草稿</el-button>
          <el-button type="primary" @click="saveNews">
            {{ isEditing ? '更新' : '发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Plus,
  Search,
  Edit,
  View,
  More,
  Hide,
  Upload,
  CopyDocument,
  Delete,
  ChatRound,
  Star
} from '@element-plus/icons-vue'
import {
  getNewsList,
  getNewsDetail,
  createNews,
  updateNews,
  deleteNews,
  publishNews,
  offlineNews,
  getNewsCategories,
  getNewsTags,
  uploadNewsImage
} from '@/api/news'
import type { News, NewsForm } from '@/types/news'

// 响应式数据
const loading = ref(false)
const dialogVisible = ref(false)
const isEditing = ref(false)
const searchQuery = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 表单相关
const newsFormRef = ref<FormInstance>()
const newsForm = ref({
  id: null as number | null,
  title: '',
  excerpt: '',
  content: '',
  categoryId: null as number | null,
  category: '', // 用于显示
  author: '',
  status: 'DRAFT',
  coverImage: '',
  image: '', // 兼容字段
  tags: [] as string[],
  tagIds: [] as number[],
  publishTime: null as Date | null,
  isFeatured: false,
  sortOrder: 0
})

// 表单验证规则
const newsFormRules: FormRules = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应在5-100个字符之间', trigger: 'blur' }
  ],
  excerpt: [
    { required: true, message: '请输入新闻摘要', trigger: 'blur' },
    { min: 10, max: 200, message: '摘要长度应在10-200个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    { min: 50, message: '内容长度不能少于50个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  author: [
    { required: true, message: '请输入作者姓名', trigger: 'blur' }
  ]
}

// API数据加载方法
const loadNewsList = async () => {
  try {
    loading.value = true
    console.log('开始加载新闻列表...')
    
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchQuery.value || undefined,
      categoryId: filterCategory.value || undefined,
      status: filterStatus.value || undefined
    }
    
    console.log('请求参数:', params)
    
    const response = await getNewsList(params)
    console.log('API响应:', response)
    
    if (response.code === 200) {
      newsList.value = response.data.list || []
      totalNews.value = response.data.total || 0
      console.log(`成功加载 ${newsList.value.length} 条新闻，总数: ${totalNews.value}`)
    } else {
      console.error('API返回错误:', response)
      ElMessage.error(response.message || '获取新闻列表失败')
    }
  } catch (error) {
    console.error('获取新闻列表失败:', error)
    ElMessage.error(`获取新闻列表失败: ${error.message || '未知错误'}`)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await getNewsCategories()
    if (response.data.code === 200) {
      categories.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

const loadTags = async () => {
  try {
    const response = await getNewsTags()
    if (response.data.code === 200) {
      tags.value = response.data.data || []
    }
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

// 新闻数据 - 从API获取
const newsList = ref([])
const totalNews = ref(0)
const categories = ref([])
const tags = ref([])

// 计算属性 - 直接使用API返回的数据
const paginatedNews = computed(() => {
  return newsList.value
})

// 方法
const showCreateDialog = () => {
  isEditing.value = false
  resetForm()
  dialogVisible.value = true
}

const editNews = (news: any) => {
  isEditing.value = true
  newsForm.value = {
    id: news.id,
    title: news.title,
    excerpt: news.excerpt,
    content: news.content,
    categoryId: news.categoryId,
    category: news.categoryName || news.category,
    author: news.author,
    status: news.status,
    coverImage: news.coverImage || news.image,
    image: news.coverImage || news.image,
    tags: news.tags?.map((tag: any) => tag.name || tag) || [],
    tagIds: news.tags?.map((tag: any) => tag.id) || [],
    publishTime: news.publishTime ? new Date(news.publishTime) : null,
    isFeatured: news.isFeatured || false,
    sortOrder: news.sortOrder || 0
  }
  dialogVisible.value = true
}

const resetForm = () => {
  newsForm.value = {
    id: null,
    title: '',
    excerpt: '',
    content: '',
    categoryId: null,
    category: '',
    author: '新闻编辑部',
    status: 'DRAFT',
    coverImage: '',
    image: '',
    tags: [],
    tagIds: [],
    publishTime: new Date(),
    isFeatured: false,
    sortOrder: 0
  }
  newsFormRef.value?.clearValidate()
}

const handleSearch = () => {
  currentPage.value = 1
  loadNewsList()
}

const handleFilter = () => {
  currentPage.value = 1
  loadNewsList()
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  currentPage.value = 1
  loadNewsList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadNewsList()
}

const getCategoryTagType = (category: string) => {
  const typeMap: Record<string, string> = {
    '公司新闻': 'primary',
    '技术更新': 'success',
    '合作动态': 'warning',
    '行业资讯': 'info',
    '公益活动': 'danger'
  }
  return typeMap[category] || 'default'
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    'PUBLISHED': 'success',
    'DRAFT': 'warning',
    'OFFLINE': 'info',
    // 兼容旧格式
    'published': 'success',
    'draft': 'warning',
    'offline': 'info'
  }
  return typeMap[status] || 'default'
}

const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    'PUBLISHED': '已发布',
    'DRAFT': '草稿',
    'OFFLINE': '已下线',
    // 兼容旧格式
    'published': '已发布',
    'draft': '草稿',
    'offline': '已下线'
  }
  return textMap[status] || status
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const formatTime = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { 
    hour: '2-digit', 
    minute: '2-digit' 
  })
}

const handleAction = async (command: string, news: any) => {
  switch (command) {
    case 'view':
      // 预览新闻
      window.open(`/website/news/${news.id}`, '_blank')
      break
    case 'publish':
      await publishNewsItem(news)
      break
    case 'offline':
      await offlineNewsItem(news)
      break
    case 'duplicate':
      duplicateNews(news)
      break
    case 'delete':
      await deleteNewsItem(news)
      break
  }
}

const publishNewsItem = async (news: any) => {
  try {
    await ElMessageBox.confirm('确定要发布这篇新闻吗？', '发布确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await publishNews(news.id)
    if (response.data.code === 200) {
      ElMessage.success('新闻发布成功')
      loadNewsList() // 重新加载列表
    } else {
      ElMessage.error(response.data.message || '发布失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布新闻失败:', error)
      ElMessage.error('发布失败')
    }
  }
}

const offlineNewsItem = async (news: any) => {
  try {
    await ElMessageBox.confirm('确定要下线这篇新闻吗？', '下线确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response = await offlineNews(news.id)
    if (response.data.code === 200) {
      ElMessage.success('新闻已下线')
      loadNewsList() // 重新加载列表
    } else {
      ElMessage.error(response.data.message || '下线失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下线新闻失败:', error)
      ElMessage.error('下线失败')
    }
  }
}

const duplicateNews = (news: any) => {
  const duplicated = {
    ...news,
    id: Date.now(),
    title: `${news.title} - 副本`,
    status: 'draft',
    date: new Date().toISOString(),
    views: 0,
    comments: 0,
    likes: 0
  }
  newsList.value.unshift(duplicated)
  ElMessage.success('新闻复制成功')
}

const deleteNewsItem = async (news: any) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇新闻吗？删除后无法恢复。', '删除确认', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    const response = await deleteNews(news.id)
    if (response.data.code === 200) {
      ElMessage.success('新闻删除成功')
      loadNewsList() // 重新加载列表
    } else {
      ElMessage.error(response.data.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除新闻失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const saveDraft = async () => {
  if (!newsFormRef.value) return
  
  try {
    await newsFormRef.value.validate()
    newsForm.value.status = 'DRAFT'
    await saveNews()
  } catch {
    ElMessage.error('请完善必填信息')
  }
}

const saveNews = async () => {
  if (!newsFormRef.value) return
  
  try {
    await newsFormRef.value.validate()
    
    const newsData = {
      title: newsForm.value.title,
      excerpt: newsForm.value.excerpt,
      content: newsForm.value.content,
      categoryId: newsForm.value.categoryId,
      author: newsForm.value.author,
      status: newsForm.value.status,
      coverImage: newsForm.value.coverImage || newsForm.value.image,
      tagIds: newsForm.value.tagIds,
      publishTime: newsForm.value.publishTime?.toISOString(),
      isFeatured: newsForm.value.isFeatured,
      sortOrder: newsForm.value.sortOrder
    }
    
    let response
    if (isEditing.value && newsForm.value.id) {
      // 更新现有新闻
      response = await updateNews(newsForm.value.id, newsData)
    } else {
      // 创建新新闻
      response = await createNews(newsData)
    }
    
    if (response.data.code === 200) {
      ElMessage.success(isEditing.value ? '新闻更新成功' : '新闻创建成功')
      dialogVisible.value = false
      resetForm()
      loadNewsList() // 重新加载列表
    } else {
      ElMessage.error(response.data.message || '保存失败')
    }
  } catch (error) {
    console.error('保存新闻失败:', error)
    ElMessage.error('保存失败')
  }
}

const handleImageSuccess = (response: any, file: any) => {
  // 如果是通过API上传成功，使用返回的URL
  if (response && response.url) {
    newsForm.value.coverImage = response.url
    newsForm.value.image = response.url
  } else {
    // 否则使用本地预览URL
    newsForm.value.coverImage = URL.createObjectURL(file.raw)
    newsForm.value.image = newsForm.value.coverImage
  }
}

const beforeImageUpload = (file: any) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

const uploadImage = async (options: any) => {
  try {
    const response = await uploadNewsImage(options.file)
    if (response.data.code === 200) {
      newsForm.value.coverImage = response.data.data.url
      newsForm.value.image = response.data.data.url
      options.onSuccess(response.data.data)
    } else {
      options.onError(new Error(response.data.message || '上传失败'))
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    options.onError(error)
  }
}

onMounted(async () => {
  console.log('NewsManagement组件已挂载，开始加载数据...')
  
  try {
    // 页面初始化 - 并行加载数据
    await Promise.all([
      loadNewsList(),
      loadCategories(),
      loadTags()
    ])
    console.log('所有数据加载完成')
  } catch (error) {
    console.error('数据加载失败:', error)
    ElMessage.error('页面数据加载失败，请刷新重试')
  }
})
</script>

<style lang="scss" scoped>
.news-management {
  .page-header {
    margin-bottom: 24px;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      
      .header-left {
        .page-title {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 8px;
        }
        
        .page-description {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
    }
  }
  
  .filter-section {
    margin-bottom: 24px;
    
    .filter-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
      
      .filter-left {
        display: flex;
        gap: 16px;
        flex: 1;
        
        .search-input {
          width: 300px;
        }
        
        .category-select,
        .status-select {
          width: 150px;
        }
      }
    }
  }
  
  .news-list {
    .news-table {
      .news-info {
        display: flex;
        gap: 12px;
        
        .news-image {
          width: 80px;
          height: 60px;
          border-radius: 6px;
          overflow: hidden;
          flex-shrink: 0;
          
          img {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        
        .news-content {
          flex: 1;
          
          .news-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
            margin-bottom: 4px;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 1;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .news-excerpt {
            font-size: 12px;
            color: var(--text-secondary);
            line-height: 1.4;
            margin-bottom: 8px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .news-meta {
            display: flex;
            align-items: center;
            gap: 8px;
            
            .news-author {
              font-size: 12px;
              color: var(--text-light);
            }
          }
        }
      }
      
      .news-stats {
        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
          margin-bottom: 4px;
          font-size: 12px;
          color: var(--text-secondary);
          
          .el-icon {
            font-size: 14px;
          }
        }
      }
      
      .date-info {
        font-size: 12px;
        text-align: center;
        
        .time-text {
          color: var(--text-secondary);
          margin-top: 2px;
        }
      }
      
      .action-buttons {
        display: flex;
        gap: 8px;
        justify-content: center;
      }
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

// 对话框样式
.news-form {
  .full-width {
    width: 100%;
  }
  
  .editor-container {
    border: 1px solid var(--border-color);
    border-radius: 4px;
    
    :deep(.el-textarea__inner) {
      border: none;
      box-shadow: none;
    }
  }
  
  .image-upload {
    .image-uploader {
      :deep(.el-upload) {
        border: 1px dashed var(--border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
        width: 200px;
        height: 120px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        &:hover {
          border-color: var(--primary-color);
        }
      }
      
      .image-uploader-icon {
        font-size: 28px;
        color: #8c939d;
      }
      
      .uploaded-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
    
    .upload-tip {
      font-size: 12px;
      color: var(--text-secondary);
      margin-top: 8px;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  .news-management {
    .page-header {
      .header-content {
        flex-direction: column;
        gap: 16px;
        align-items: stretch;
      }
    }
    
    .filter-section {
      .filter-content {
        flex-direction: column;
        align-items: stretch;
        
        .filter-left {
          flex-direction: column;
          
          .search-input,
          .category-select,
          .status-select {
            width: 100%;
          }
        }
      }
    }
    
    .news-table {
      :deep(.el-table__body-wrapper) {
        overflow-x: auto;
      }
    }
  }
}
</style>