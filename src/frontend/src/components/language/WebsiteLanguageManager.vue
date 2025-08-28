<!--
  官网多语言管理组件
  功能：管理官网内容的多语言版本，支持页面内容的翻译和更新
-->
<template>
  <div class="website-language-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <el-button type="primary" @click="showAddContentDialog">
          <el-icon><Plus /></el-icon>
          添加内容
        </el-button>
        <el-button @click="toggleView">
          <el-icon><Grid /></el-icon>
          {{ viewMode === 'list' ? '编辑器视图' : '列表视图' }}
        </el-button>
      </div>
      <div class="right-actions">
        <el-select v-model="selectedLanguage" placeholder="选择语言" style="width: 150px;">
          <el-option
            v-for="lang in supportedLanguages"
            :key="lang.code"
            :label="lang.name"
            :value="lang.code"
          />
        </el-select>
        <el-button type="success" @click="showPublishDialog" :disabled="selectedContents.length === 0">
          <el-icon><Upload /></el-icon>
          发布到官网
        </el-button>
      </div>
    </div>

    <!-- 内容概览 -->
    <div class="content-overview">
      <div class="overview-header">
        <h3>内容概览</h3>
        <div class="overview-stats">
          <el-tag>总计: {{ websiteContents.length }}</el-tag>
          <el-tag type="success">已完成: {{ completedContents }}</el-tag>
          <el-tag type="warning">进行中: {{ inProgressContents }}</el-tag>
          <el-tag type="danger">待翻译: {{ pendingContents }}</el-tag>
        </div>
      </div>
      
      <div class="pages-grid">
        <div
          v-for="page in pageGroups"
          :key="page.name"
          class="page-card"
          @click="filterByPage(page.name)"
        >
          <div class="page-header">
            <div class="page-info">
              <h4>{{ page.displayName }}</h4>
              <div class="page-path">{{ page.path }}</div>
            </div>
            <el-tag :type="getPageStatusType(page.progress)">
              {{ page.progress }}%
            </el-tag>
          </div>
          <div class="page-progress">
            <div class="progress-info">
              <span>翻译进度</span>
              <span>{{ page.completed }}/{{ page.total }}</span>
            </div>
            <el-progress
              :percentage="page.progress"
              :color="getProgressColor(page.progress)"
              :stroke-width="6"
            />
          </div>
          <div class="page-languages">
            <div class="language-badges">
              <el-tag
                v-for="lang in page.languages.slice(0, 4)"
                :key="lang.code"
                size="small"
                :type="lang.completed ? 'success' : 'info'"
              >
                {{ lang.name }}
              </el-tag>
              <span v-if="page.languages.length > 4" class="more-languages">
                +{{ page.languages.length - 4 }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 内容列表 -->
    <div class="content-list">
      <div class="list-header">
        <h3>内容管理</h3>
        <div class="header-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索内容"
            clearable
            style="width: 250px;"
          />
          <el-select v-model="pageFilter" placeholder="页面筛选" clearable style="width: 150px;">
            <el-option label="全部页面" value="" />
            <el-option
              v-for="page in uniquePages"
              :key="page"
              :label="getPageDisplayName(page)"
              :value="page"
            />
          </el-select>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="list-view">
        <el-table
          :data="filteredContents"
          v-loading="tableLoading"
          @selection-change="handleSelectionChange"
          stripe
          :header-cell-style="{ 
            background: '#f8f9fa', 
            color: '#303133', 
            fontWeight: '600',
            fontSize: '14px',
            padding: '12px 0'
          }"
          :cell-style="{ padding: '16px 0' }"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="key" label="内容键名" min-width="220" sortable>
            <template #default="{ row }">
              <div class="content-key-cell">
                <div class="key-info">
                  <code class="content-key-code">{{ row.key }}</code>
                  <el-tag 
                    size="small" 
                    :type="getContentTypeTagType(row.type)"
                    class="content-type-tag"
                  >
                    {{ row.type }}
                  </el-tag>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="page" label="所属页面" width="120" align="center">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">{{ getPageDisplayName(row.page) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" min-width="180">
            <template #default="{ row }">
              <div class="description-cell">
                {{ row.description }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="翻译状态" width="180" align="center">
            <template #default="{ row }">
              <div class="translation-status-cell">
                <div class="progress-wrapper">
                  <el-progress
                    :percentage="getContentProgress(row)"
                    :color="getProgressColor(getContentProgress(row))"
                    :stroke-width="8"
                    :show-text="false"
                  />
                  <span class="progress-text">
                    {{ getContentProgress(row) }}%
                  </span>
                </div>
                <div class="language-count">
                  {{ getTranslatedLanguagesCount(row) }}/{{ supportedLanguages.length }} 语言
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="最后更新" width="140" align="center">
            <template #default="{ row }">
              <div class="update-time">
                {{ formatDateTime(row.updatedAt) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right" align="center">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button 
                  size="small" 
                  type="primary" 
                  link 
                  @click="handleEditContent(row)"
                  class="edit-btn"
                >
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button 
                  size="small" 
                  type="danger" 
                  link 
                  @click="handleDeleteContent(row)"
                  class="delete-btn"
                >
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 编辑器视图 -->
      <div v-else class="editor-view">
        <div class="editor-sidebar">
          <div class="content-tree">
            <el-tree
              :data="treeData"
              :props="{ label: 'label', children: 'children' }"
              node-key="id"
              :expand-on-click-node="false"
              @node-click="handleTreeNodeClick"
            >
              <template #default="{ node, data }">
                <div class="tree-node">
                  <span class="node-label">{{ node.label }}</span>
                  <div class="node-progress">
                    <el-progress
                      v-if="data.progress !== undefined"
                      :percentage="data.progress"
                      :stroke-width="4"
                      :show-text="false"
                      :color="getProgressColor(data.progress)"
                    />
                  </div>
                </div>
              </template>
            </el-tree>
          </div>
        </div>
        
        <div class="editor-main">
          <div v-if="selectedContent" class="content-editor">
            <div class="editor-header">
              <div class="content-info">
                <h4>{{ selectedContent.key }}</h4>
                <p>{{ selectedContent.description }}</p>
              </div>
              <div class="editor-actions">
                <el-button size="small" @click="saveContent" :loading="saveLoading">
                  保存
                </el-button>
                <el-button size="small" @click="previewContent">
                  预览
                </el-button>
              </div>
            </div>
            
            <div class="editor-content">
              <MultiLanguageEditor
                v-model="selectedContent.translations"
                :languages="supportedLanguages"
                :editor-type="getEditorType(selectedContent.type)"
                @save="handleSaveTranslations"
              />
            </div>
          </div>
          
          <div v-else class="empty-editor">
            <el-empty description="请从左侧选择要编辑的内容" />
          </div>
        </div>
      </div>

      <!-- 批量操作 -->
      <div v-if="selectedContents.length > 0" class="batch-actions">
        <div class="selected-info">
          已选择 {{ selectedContents.length }} 项内容
        </div>
        <div class="batch-buttons">
          <el-button size="small" @click="batchTranslate">
            批量翻译
          </el-button>
          <el-button size="small" @click="batchExport">
            导出选中
          </el-button>
          <el-button size="small" type="danger" @click="batchDelete">
            批量删除
          </el-button>
        </div>
      </div>
    </div>

    <!-- 添加内容对话框 -->
    <el-dialog
      v-model="addContentDialogVisible"
      title="添加新内容"
      width="600px"
    >
      <el-form
        ref="addContentFormRef"
        :model="newContentForm"
        :rules="contentFormRules"
        label-width="100px"
      >
        <el-form-item label="内容键名" prop="key">
          <el-input v-model="newContentForm.key" placeholder="例如: home.hero.title" />
        </el-form-item>
        <el-form-item label="所属页面" prop="page">
          <el-select v-model="newContentForm.page" placeholder="选择页面" style="width: 100%">
            <el-option
              v-for="page in uniquePages"
              :key="page"
              :label="page"
              :value="page"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容类型" prop="type">
          <el-select v-model="newContentForm.type" placeholder="选择类型" style="width: 100%">
            <el-option label="标题" value="title" />
            <el-option label="内容" value="content" />
            <el-option label="按钮" value="button" />
            <el-option label="导航" value="navigation" />
            <el-option label="SEO" value="seo" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="newContentForm.description"
            type="textarea"
            :rows="3"
            placeholder="内容描述"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addContentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddContent" :loading="addLoading">
            添加
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 发布对话框 -->
    <el-dialog
      v-model="publishDialogVisible"
      title="发布到官网"
      width="500px"
    >
      <div class="publish-content">
        <el-alert
          title="发布确认"
          type="info"
          show-icon
        >
          <template #default>
            <p>确定要将选中的内容发布到官网吗？</p>
            <p>发布后，官网前端将自动更新显示最新的多语言内容。</p>
          </template>
        </el-alert>
        
        <div class="publish-options">
          <el-checkbox v-model="publishOptions.clearCache">
            清除缓存
          </el-checkbox>
          <el-checkbox v-model="publishOptions.notifyUsers">
            通知相关用户
          </el-checkbox>
        </div>
        
        <div class="publish-summary">
          <h4>发布内容摘要：</h4>
          <ul>
            <li v-for="content in selectedContents.slice(0, 5)" :key="content.id">
              {{ content.key }} - {{ content.page }}
            </li>
            <li v-if="selectedContents.length > 5">
              还有 {{ selectedContents.length - 5 }} 项内容...
            </li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePublishContent" :loading="publishLoading">
            确认发布
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Grid,
  Upload,
  Edit,
  Delete
} from '@element-plus/icons-vue'
import MultiLanguageEditor from './MultiLanguageEditor.vue'

// 接口定义
interface WebsiteContent {
  id: string
  key: string
  page: string
  type: 'title' | 'content' | 'button' | 'navigation' | 'seo'
  description: string
  translations: Record<string, string>
  createdAt: string
  updatedAt: string
}

interface Language {
  code: string
  name: string
  flag: string
}

// 响应式数据
const refreshLoading = ref(false)
const tableLoading = ref(false)
const saveLoading = ref(false)
const addLoading = ref(false)
const publishLoading = ref(false)
const viewMode = ref<'list' | 'editor'>('list')
const searchKeyword = ref('')
const pageFilter = ref('')
const selectedLanguage = ref('zh-CN')

// 内容数据
const websiteContents = ref<WebsiteContent[]>([])
const selectedContents = ref<WebsiteContent[]>([])
const selectedContent = ref<WebsiteContent | null>(null)

// 对话框状态
const addContentDialogVisible = ref(false)
const publishDialogVisible = ref(false)

// 表单数据
const newContentForm = reactive({
  key: '',
  page: '',
  type: 'title' as WebsiteContent['type'],
  description: ''
})

const publishOptions = reactive({
  clearCache: true,
  notifyUsers: false
})

// 支持的语言
const supportedLanguages: Language[] = [
  { code: 'zh-CN', name: '简体中文', flag: '🇨🇳' },
  { code: 'en-US', name: 'English', flag: '🇺🇸' },
  { code: 'ja-JP', name: '日本語', flag: '🇯🇵' },
  { code: 'ko-KR', name: '한국어', flag: '🇰🇷' },
  { code: 'fr-FR', name: 'Français', flag: '🇫🇷' },
  { code: 'de-DE', name: 'Deutsch', flag: '🇩🇪' },
  { code: 'es-ES', name: 'Español', flag: '🇪🇸' }
]

// 表单验证规则
const contentFormRules = {
  key: [{ required: true, message: '请输入内容键名', trigger: 'blur' }],
  page: [{ required: true, message: '请选择所属页面', trigger: 'change' }],
  type: [{ required: true, message: '请选择内容类型', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}

// 计算属性
const filteredContents = computed(() => {
  let filtered = websiteContents.value

  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(content =>
      content.key.toLowerCase().includes(keyword) ||
      content.description.toLowerCase().includes(keyword)
    )
  }

  if (pageFilter.value) {
    filtered = filtered.filter(content => content.page === pageFilter.value)
  }

  return filtered
})

const uniquePages = computed(() => {
  const pages = [...new Set(websiteContents.value.map(content => content.page))]
  return pages.sort()
})

const pageGroups = computed(() => {
  const groups = uniquePages.value.map(page => {
    const pageContents = websiteContents.value.filter(content => content.page === page)
    const total = pageContents.length
    const completed = pageContents.filter(content => 
      supportedLanguages.every(lang => 
        content.translations[lang.code] && content.translations[lang.code].trim() !== ''
      )
    ).length
    
    const progress = total > 0 ? Math.round((completed / total) * 100) : 0
    
    const languages = supportedLanguages.map(lang => ({
      ...lang,
      completed: hasLanguageTranslation(page, lang.code)
    }))

    return {
      name: page,
      displayName: getPageDisplayName(page),
      path: `/${page}`,
      total,
      completed,
      progress,
      languages
    }
  })

  return groups
})

const completedContents = computed(() => {
  return websiteContents.value.filter(content =>
    supportedLanguages.every(lang =>
      content.translations[lang.code] && content.translations[lang.code].trim() !== ''
    )
  ).length
})

const inProgressContents = computed(() => {
  return websiteContents.value.filter(content => {
    const translatedCount = supportedLanguages.filter(lang =>
      content.translations[lang.code] && content.translations[lang.code].trim() !== ''
    ).length
    return translatedCount > 0 && translatedCount < supportedLanguages.length
  }).length
})

const pendingContents = computed(() => {
  return websiteContents.value.filter(content =>
    supportedLanguages.every(lang =>
      !content.translations[lang.code] || content.translations[lang.code].trim() === ''
    )
  ).length
})

const treeData = computed(() => {
  const tree: any[] = []
  const pageMap = new Map()

  websiteContents.value.forEach(content => {
    if (!pageMap.has(content.page)) {
      pageMap.set(content.page, {
        id: content.page,
        label: getPageDisplayName(content.page),
        children: [],
        progress: 0
      })
    }

    const pageNode = pageMap.get(content.page)
    pageNode.children.push({
      id: content.id,
      label: content.key,
      content: content,
      progress: getContentProgress(content)
    })
  })

  // 计算页面进度
  pageMap.forEach(pageNode => {
    if (pageNode.children.length > 0) {
      const totalProgress = pageNode.children.reduce((sum: number, child: any) => sum + child.progress, 0)
      pageNode.progress = Math.round(totalProgress / pageNode.children.length)
    }
    tree.push(pageNode)
  })

  return tree
})

// 方法
const loadWebsiteContents = async () => {
  tableLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    websiteContents.value = [
      {
        id: '1',
        key: 'home.hero.title',
        page: 'home',
        type: 'title',
        description: '首页主标题',
        translations: {
          'zh-CN': '智能练字机器人',
          'en-US': 'Smart Calligraphy Robot',
          'ja-JP': 'スマート書道ロボット'
        },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-15T10:30:00Z'
      },
      {
        id: '2',
        key: 'home.hero.subtitle',
        page: 'home',
        type: 'content',
        description: '首页副标题',
        translations: {
          'zh-CN': '让每个孩子都能写出一手好字',
          'en-US': 'Help every child write beautiful characters',
          'ja-JP': ''
        },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-15T10:30:00Z'
      },
      {
        id: '3',
        key: 'products.title',
        page: 'products',
        type: 'title',
        description: '产品页标题',
        translations: {
          'zh-CN': '我们的产品',
          'en-US': 'Our Products',
          'ja-JP': '私たちの製品'
        },
        createdAt: '2024-01-01T00:00:00Z',
        updatedAt: '2024-01-15T10:30:00Z'
      }
    ]
  } catch (error) {
    console.error('加载官网内容失败:', error)
    ElMessage.error('加载官网内容失败')
  } finally {
    tableLoading.value = false
  }
}

const refreshData = async () => {
  refreshLoading.value = true
  try {
    await loadWebsiteContents()
  } finally {
    setTimeout(() => {
      refreshLoading.value = false
    }, 1000)
  }
}

const toggleView = () => {
  viewMode.value = viewMode.value === 'list' ? 'editor' : 'list'
}

const filterByPage = (page: string) => {
  pageFilter.value = page
  viewMode.value = 'list'
}

const handleSelectionChange = (selection: WebsiteContent[]) => {
  selectedContents.value = selection
}

const handleEditContent = (content: WebsiteContent) => {
  selectedContent.value = content
  viewMode.value = 'editor'
}

const handleDeleteContent = async (content: WebsiteContent) => {
  try {
    await ElMessageBox.confirm('确定要删除这个内容吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const index = websiteContents.value.findIndex(item => item.id === content.id)
    if (index > -1) {
      websiteContents.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  } catch {
    // 用户取消删除
  }
}

const handleTreeNodeClick = (data: any) => {
  if (data.content) {
    selectedContent.value = data.content
  }
}

const saveContent = async () => {
  if (!selectedContent.value) return
  
  saveLoading.value = true
  try {
    // 模拟保存API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    selectedContent.value.updatedAt = new Date().toISOString()
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saveLoading.value = false
  }
}

const previewContent = () => {
  ElMessage.info('预览功能开发中')
}

const handleSaveTranslations = (translations: Record<string, string>) => {
  if (selectedContent.value) {
    selectedContent.value.translations = translations
    selectedContent.value.updatedAt = new Date().toISOString()
  }
}

const showAddContentDialog = () => {
  Object.assign(newContentForm, {
    key: '',
    page: '',
    type: 'title',
    description: ''
  })
  addContentDialogVisible.value = true
}

const handleAddContent = async () => {
  addLoading.value = true
  try {
    // 模拟添加API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newContent: WebsiteContent = {
      id: Date.now().toString(),
      key: newContentForm.key,
      page: newContentForm.page,
      type: newContentForm.type,
      description: newContentForm.description,
      translations: {},
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    websiteContents.value.push(newContent)
    addContentDialogVisible.value = false
    ElMessage.success('添加成功')
  } catch (error) {
    console.error('添加失败:', error)
    ElMessage.error('添加失败')
  } finally {
    addLoading.value = false
  }
}

const showPublishDialog = () => {
  publishDialogVisible.value = true
}

const handlePublishContent = async () => {
  publishLoading.value = true
  try {
    // 模拟发布API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    publishDialogVisible.value = false
    ElMessage.success(`成功发布 ${selectedContents.value.length} 项内容到官网`)
    selectedContents.value = []
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败')
  } finally {
    publishLoading.value = false
  }
}

const batchTranslate = () => {
  ElMessage.info('批量翻译功能开发中')
}

const batchExport = () => {
  ElMessage.info('批量导出功能开发中')
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedContents.value.length} 项内容吗？`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const selectedIds = selectedContents.value.map(content => content.id)
    websiteContents.value = websiteContents.value.filter(content => !selectedIds.includes(content.id))
    selectedContents.value = []
    ElMessage.success('批量删除成功')
  } catch {
    // 用户取消删除
  }
}

// 工具方法
const getPageDisplayName = (page: string) => {
  const pageNames: Record<string, string> = {
    home: '首页',
    products: '产品页',
    about: '关于我们',
    contact: '联系我们',
    charity: '公益项目'
  }
  return pageNames[page] || page
}

const getPageStatusType = (progress: number) => {
  if (progress >= 90) return 'success'
  if (progress >= 70) return 'warning'
  return 'danger'
}

const getContentTypeTagType = (type: string) => {
  const types: Record<string, any> = {
    title: 'primary',
    content: 'success',
    button: 'warning',
    navigation: 'info',
    seo: 'danger'
  }
  return types[type] || 'info'
}

const hasLanguageTranslation = (page: string, langCode: string) => {
  return websiteContents.value
    .filter(content => content.page === page)
    .some(content => 
      content.translations[langCode] && content.translations[langCode].trim() !== ''
    )
}

const getContentProgress = (content: WebsiteContent) => {
  const totalLanguages = supportedLanguages.length
  const translatedLanguages = supportedLanguages.filter(
    lang => content.translations[lang.code] && content.translations[lang.code].trim() !== ''
  ).length
  
  return Math.round((translatedLanguages / totalLanguages) * 100)
}

const getTranslatedLanguagesCount = (content: WebsiteContent) => {
  return supportedLanguages.filter(
    lang => content.translations[lang.code] && content.translations[lang.code].trim() !== ''
  ).length
}

const getProgressColor = (percentage: number) => {
  if (percentage >= 90) return '#67C23A'
  if (percentage >= 70) return '#E6A23C'
  if (percentage >= 50) return '#409EFF'
  return '#F56C6C'
}

const getEditorType = (contentType: string) => {
  if (contentType === 'content') return 'rich'
  return 'text'
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadWebsiteContents()
})
</script>

<style lang="scss" scoped>
.website-language-manager {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .left-actions,
    .right-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .content-overview {
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .overview-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      h3 {
        margin: 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .overview-stats {
        display: flex;
        gap: 8px;
      }
    }

    .pages-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;

      .page-card {
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          transform: translateY(-2px);
        }

        .page-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 12px;

          .page-info {
            h4 {
              margin: 0 0 4px 0;
              color: #303133;
              font-size: 16px;
              font-weight: 600;
            }

            .page-path {
              font-size: 12px;
              color: #909399;
              font-family: monospace;
            }
          }
        }

        .page-progress {
          margin-bottom: 12px;

          .progress-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            font-size: 12px;
            color: #606266;
          }
        }

        .page-languages {
          .language-badges {
            display: flex;
            gap: 4px;
            align-items: center;
            flex-wrap: wrap;

            .more-languages {
              font-size: 11px;
              color: #909399;
            }
          }
        }
      }
    }
  }

  .content-list {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid #f0f0f0;

      h3 {
        margin: 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .header-actions {
        display: flex;
        gap: 12px;
        align-items: center;
      }
    }

    .list-view {
      padding: 0;

      // 表格行样式优化
      :deep(.el-table) {
        border-radius: 0;
        
        .el-table__header-wrapper {
          .el-table__header {
            th {
              border-bottom: 2px solid #e4e7ed;
            }
          }
        }

        .el-table__body-wrapper {
          .el-table__body {
            tr {
              &:hover {
                background-color: #f8f9fa;
              }
              
              td {
                border-bottom: 1px solid #f0f0f0;
                vertical-align: middle;
              }
            }
          }
        }
      }

      // 内容键名单元格
      .content-key-cell {
        .key-info {
          display: flex;
          flex-direction: column;
          gap: 6px;
          align-items: flex-start;

          .content-key-code {
            background: #f1f3f4;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 12px;
            color: #1f2937;
            font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
            font-weight: 500;
            border: 1px solid #e5e7eb;
          }

          .content-type-tag {
            font-size: 11px;
            height: 20px;
            line-height: 18px;
            padding: 0 6px;
            border-radius: 10px;
          }
        }
      }

      // 描述单元格
      .description-cell {
        color: #606266;
        font-size: 13px;
        line-height: 1.4;
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      // 翻译状态单元格
      .translation-status-cell {
        display: flex;
        flex-direction: column;
        gap: 6px;
        align-items: center;

        .progress-wrapper {
          display: flex;
          align-items: center;
          gap: 8px;
          width: 100%;

          :deep(.el-progress) {
            flex: 1;
            
            .el-progress-bar {
              .el-progress-bar__outer {
                background-color: #f0f0f0;
                border-radius: 4px;
              }
            }
          }

          .progress-text {
            font-size: 12px;
            font-weight: 600;
            color: #303133;
            min-width: 35px;
            text-align: right;
          }
        }

        .language-count {
          font-size: 11px;
          color: #909399;
          text-align: center;
        }
      }

      // 更新时间单元格
      .update-time {
        font-size: 12px;
        color: #606266;
        text-align: center;
      }

      // 操作按钮单元格
      .action-buttons {
        display: flex;
        flex-direction: column;
        gap: 4px;
        align-items: center;

        .edit-btn,
        .delete-btn {
          font-size: 12px;
          padding: 4px 8px;
          height: auto;
          min-height: 24px;
          
          .el-icon {
            margin-right: 4px;
            font-size: 12px;
          }
        }

        .edit-btn {
          color: #409eff;
          
          &:hover {
            color: #66b1ff;
            background-color: #ecf5ff;
          }
        }

        .delete-btn {
          color: #f56c6c;
          
          &:hover {
            color: #f78989;
            background-color: #fef0f0;
          }
        }
      }
    }

    .editor-view {
      display: flex;
      height: 600px;

      .editor-sidebar {
        width: 300px;
        border-right: 1px solid #e4e7ed;
        background: #f8f9fa;

        .content-tree {
          padding: 16px;
          height: 100%;
          overflow-y: auto;

          .tree-node {
            display: flex;
            justify-content: space-between;
            align-items: center;
            width: 100%;

            .node-label {
              flex: 1;
              font-size: 13px;
            }

            .node-progress {
              width: 60px;
            }
          }
        }
      }

      .editor-main {
        flex: 1;
        display: flex;
        flex-direction: column;

        .content-editor {
          height: 100%;
          display: flex;
          flex-direction: column;

          .editor-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            padding: 16px 20px;
            border-bottom: 1px solid #e4e7ed;

            .content-info {
              h4 {
                margin: 0 0 4px 0;
                color: #303133;
                font-size: 16px;
                font-weight: 600;
              }

              p {
                margin: 0;
                color: #606266;
                font-size: 13px;
              }
            }

            .editor-actions {
              display: flex;
              gap: 8px;
            }
          }

          .editor-content {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
          }
        }

        .empty-editor {
          flex: 1;
          display: flex;
          align-items: center;
          justify-content: center;
        }
      }
    }

    .batch-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 20px;
      background: #f8f9fa;
      border-top: 1px solid #e4e7ed;

      .selected-info {
        color: #606266;
        font-size: 14px;
      }

      .batch-buttons {
        display: flex;
        gap: 8px;
      }
    }
  }

  .publish-content {
    .publish-options {
      margin-top: 16px;
    }

    .publish-summary {
      margin-top: 16px;

      h4 {
        margin: 0 0 8px 0;
        font-size: 14px;
        color: #303133;
      }

      ul {
        margin: 0;
        padding-left: 20px;
        color: #606266;
        font-size: 13px;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .website-language-manager {
    .content-overview {
      .pages-grid {
        grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      }
    }

    .content-list {
      .editor-view {
        .editor-sidebar {
          width: 250px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .website-language-manager {
    .action-bar {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .left-actions,
      .right-actions {
        justify-content: center;
        flex-wrap: wrap;
      }
    }

    .content-overview {
      .pages-grid {
        grid-template-columns: 1fr;
      }
    }

    .content-list {
      .list-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;

        .header-actions {
          justify-content: center;
          flex-wrap: wrap;
        }
      }

      .editor-view {
        flex-direction: column;
        height: auto;

        .editor-sidebar {
          width: 100%;
          height: 200px;
          border-right: none;
          border-bottom: 1px solid #e4e7ed;
        }

        .editor-main {
          .content-editor {
            .editor-header {
              flex-direction: column;
              gap: 12px;
              align-items: stretch;
            }
          }
        }
      }

      .batch-actions {
        flex-direction: column;
        gap: 8px;
        align-items: stretch;

        .batch-buttons {
          justify-content: center;
        }
      }
    }
  }
}
</style>