<template>
  <div class="language-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>多语言管理</h2>
        <p class="header-subtitle">统一管理官网和机器人界面的多语言内容</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddLanguageDialog">
          <el-icon><Plus /></el-icon>
          添加语言
        </el-button>
      </div>
    </div>

    <!-- 翻译进度概览 -->
    <div class="progress-overview">
      <el-row :gutter="20">
        <el-col :span="6" v-for="lang in mockLanguages" :key="lang.languageCode">
          <el-card class="progress-card">
            <div class="language-progress">
              <div class="lang-header">
                <img :src="lang.flagIcon" :alt="lang.nativeName" class="flag-icon" />
                <div class="lang-info">
                  <h4>{{ lang.nativeName }}</h4>
                  <span class="lang-code">{{ lang.languageCode.toUpperCase() }}</span>
                </div>
              </div>
              <div class="progress-stats">
                <div class="progress-item">
                  <span class="label">官网内容</span>
                  <el-progress 
                    :percentage="lang.websiteProgress" 
                    :status="lang.websiteProgress === 100 ? 'success' : 'active'"
                    :stroke-width="6"
                  />
                  <span class="stats">{{ lang.websiteTranslated }}/{{ lang.websiteTotal }}</span>
                </div>
                <div class="progress-item">
                  <span class="label">机器人界面</span>
                  <el-progress 
                    :percentage="lang.robotProgress" 
                    :status="lang.robotProgress === 100 ? 'success' : 'active'"
                    :stroke-width="6"
                  />
                  <span class="stats">{{ lang.robotTranslated }}/{{ lang.robotTotal }}</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 内容类型切换 -->
    <div class="content-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="官网内容" name="website">
          <div class="tab-header">
            <div class="tab-info">
              <el-icon><Location /></el-icon>
              <span>管理官网页面的多语言内容</span>
            </div>
            <div class="tab-actions">
              <el-button @click="showAddContentDialog('website')">
                <el-icon><Plus /></el-icon>
                添加内容
              </el-button>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="机器人界面" name="robot_ui">
          <div class="tab-header">
            <div class="tab-info">
              <el-icon><Monitor /></el-icon>
              <span>管理机器人界面的多语言文字</span>
            </div>
            <div class="tab-actions">
              <el-button @click="showAddContentDialog('robot_ui')">
                <el-icon><Plus /></el-icon>
                添加界面文字
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>   
 <!-- 内容管理表格 -->
    <div class="content-table">
      <!-- 筛选器 -->
      <div class="table-filters">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索内容键名或内容"
          :prefix-icon="Search"
          clearable
          style="width: 300px;"
          @input="handleSearch"
        />
        <el-select v-model="categoryFilter" placeholder="选择分类" clearable @change="handleFilter">
          <el-option label="全部分类" value="" />
          <el-option 
            v-for="category in currentCategories" 
            :key="category" 
            :label="category" 
            :value="category" 
          />
        </el-select>
        <el-select v-model="statusFilter" placeholder="翻译状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="待翻译" value="pending" />
          <el-option label="已翻译" value="translated" />
          <el-option label="已审核" value="reviewed" />
          <el-option label="已发布" value="published" />
        </el-select>
      </div>

      <!-- 内容表格 -->
      <el-table :data="mockContentList" v-loading="tableLoading" stripe>
        <el-table-column prop="contentKey" label="内容键名" width="200" fixed="left">
          <template #default="{ row }">
            <div class="content-key">
              <el-tag size="small" :type="getCategoryTagType(row.category)">
                {{ row.category }}
              </el-tag>
              <span class="key-text">{{ row.contentKey }}</span>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column prop="defaultContent" label="默认内容(中文)" width="250">
          <template #default="{ row }">
            <div class="default-content">
              <span class="content-text">{{ row.defaultContent }}</span>
              <el-button 
                text 
                type="primary" 
                size="small" 
                @click="editDefaultContent(row)"
              >
                编辑
              </el-button>
            </div>
          </template>
        </el-table-column>

        <!-- 动态语言列 -->
        <el-table-column 
          v-for="lang in mockLanguages.filter(l => l.languageCode !== 'zh')" 
          :key="lang.languageCode"
          :label="lang.nativeName"
          width="250"
        >
          <template #header>
            <div class="lang-header">
              <img :src="lang.flagIcon" :alt="lang.nativeName" class="flag-icon-small" />
              <span>{{ lang.nativeName }}</span>
            </div>
          </template>
          <template #default="{ row }">
            <div class="translation-cell">
              <el-input 
                v-model="row.translations[lang.languageCode]"
                type="textarea"
                :rows="2"
                :placeholder="`请输入${lang.nativeName}翻译`"
                @blur="updateTranslation(row.id, lang.languageCode, row.translations[lang.languageCode])"
              />
              <div class="translation-meta">
                <el-tag 
                  :type="getStatusTagType(row.translationStatus[lang.languageCode])"
                  size="small"
                >
                  {{ getStatusText(row.translationStatus[lang.languageCode]) }}
                </el-tag>
                <el-button 
                  text 
                  type="success" 
                  size="small"
                  @click="publishTranslation(row.id, lang.languageCode)"
                  v-if="row.translationStatus[lang.languageCode] === 'translated'"
                >
                  发布
                </el-button>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="editContent(row)">
              编辑
            </el-button>
            <el-button text type="danger" size="small" @click="deleteContent(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加语言对话框 -->
    <el-dialog
      v-model="addLanguageDialogVisible"
      title="添加语言"
      width="500px"
    >
      <el-form :model="newLanguage" :rules="languageRules" ref="languageFormRef" label-width="100px">
        <el-form-item label="语言代码" prop="languageCode">
          <el-input v-model="newLanguage.languageCode" placeholder="如: en, ja, ko" />
        </el-form-item>
        <el-form-item label="语言名称" prop="languageName">
          <el-input v-model="newLanguage.languageName" placeholder="如: English, 日本語" />
        </el-form-item>
        <el-form-item label="本地名称" prop="nativeName">
          <el-input v-model="newLanguage.nativeName" placeholder="如: English, 日本語" />
        </el-form-item>
        <el-form-item label="国旗图标" prop="flagIcon">
          <el-input v-model="newLanguage.flagIcon" placeholder="国旗图标URL" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="newLanguage.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="启用状态">
          <el-switch v-model="newLanguage.isEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addLanguageDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addLanguage">确定</el-button>
      </template>
    </el-dialog>

    <!-- 添加内容对话框 -->
    <el-dialog
      v-model="addContentDialogVisible"
      :title="activeTab === 'website' ? '添加官网内容' : '添加机器人界面文字'"
      width="600px"
    >
      <el-form :model="newContent" :rules="contentRules" ref="contentFormRef" label-width="100px">
        <el-form-item label="内容键名" prop="contentKey">
          <el-input 
            v-model="newContent.contentKey" 
            :placeholder="activeTab === 'website' ? '如: home.hero.title' : '如: menu.settings'" 
          />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="newContent.category" placeholder="选择分类" filterable allow-create>
            <el-option 
              v-for="category in currentCategories" 
              :key="category" 
              :label="category" 
              :value="category" 
            />
          </el-select>
        </el-form-item>
        <el-form-item label="默认内容" prop="defaultContent">
          <el-input 
            v-model="newContent.defaultContent" 
            type="textarea" 
            :rows="3"
            placeholder="请输入中文内容" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addContentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addContent">确定</el-button>
      </template>
    </el-dialog>

    <!-- 编辑内容对话框 -->
    <el-dialog
      v-model="editContentDialogVisible"
      title="编辑内容"
      width="800px"
      :before-close="handleEditDialogClose"
    >
      <el-form :model="editingContent" :rules="contentRules" ref="editContentFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="内容键名" prop="contentKey">
              <el-input 
                v-model="editingContent.contentKey" 
                placeholder="内容键名"
                :disabled="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="category">
              <el-select v-model="editingContent.category" placeholder="选择分类" filterable allow-create>
                <el-option 
                  v-for="category in currentCategories" 
                  :key="category" 
                  :label="category" 
                  :value="category" 
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="默认内容(中文)" prop="defaultContent">
          <el-input 
            v-model="editingContent.defaultContent" 
            type="textarea" 
            :rows="3"
            placeholder="请输入中文内容" 
          />
        </el-form-item>

        <!-- 多语言翻译编辑 -->
        <div class="translations-section">
          <h4>多语言翻译</h4>
          <div 
            v-for="lang in mockLanguages.filter(l => l.languageCode !== 'zh')" 
            :key="lang.languageCode"
            class="translation-item"
          >
            <div class="translation-header">
              <img :src="lang.flagIcon" :alt="lang.nativeName" class="flag-icon-small" />
              <span class="lang-name">{{ lang.nativeName }}</span>
              <el-tag 
                :type="getStatusTagType(editingContent.translationStatus[lang.languageCode])"
                size="small"
              >
                {{ getStatusText(editingContent.translationStatus[lang.languageCode]) }}
              </el-tag>
            </div>
            <el-input 
              v-model="editingContent.translations[lang.languageCode]"
              type="textarea"
              :rows="2"
              :placeholder="`请输入${lang.nativeName}翻译`"
              class="translation-input"
            />
            <div class="translation-actions">
              <el-button 
                text 
                type="success" 
                size="small"
                @click="markAsTranslated(lang.languageCode)"
                v-if="editingContent.translationStatus[lang.languageCode] === 'pending'"
              >
                标记为已翻译
              </el-button>
              <el-button 
                text 
                type="primary" 
                size="small"
                @click="markAsReviewed(lang.languageCode)"
                v-if="editingContent.translationStatus[lang.languageCode] === 'translated'"
              >
                标记为已审核
              </el-button>
              <el-button 
                text 
                type="warning" 
                size="small"
                @click="publishSingleTranslation(lang.languageCode)"
                v-if="editingContent.translationStatus[lang.languageCode] === 'reviewed'"
              >
                发布翻译
              </el-button>
            </div>
          </div>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="editContentDialogVisible = false">取消</el-button>
          <el-button @click="saveAllTranslations" type="info">保存所有翻译</el-button>
          <el-button type="primary" @click="saveEditContent">保存并关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Plus,
  Search,
  Location,
  Monitor
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 响应式数据
const activeTab = ref('website')
const searchKeyword = ref('')
const categoryFilter = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const tableLoading = ref(false)

// 对话框状态
const addLanguageDialogVisible = ref(false)
const addContentDialogVisible = ref(false)
const editContentDialogVisible = ref(false)

// 表单数据
const newLanguage = ref({
  languageCode: '',
  languageName: '',
  nativeName: '',
  flagIcon: '',
  sortOrder: 0,
  isEnabled: true
})

const newContent = ref({
  contentKey: '',
  category: '',
  defaultContent: '',
  contentType: 'website'
})

const editingContent = ref({
  id: null,
  contentKey: '',
  category: '',
  defaultContent: '',
  translations: {},
  translationStatus: {}
})

// 模拟数据
const mockLanguages = ref([
  {
    languageCode: 'zh',
    languageName: '中文',
    nativeName: '中文',
    flagIcon: 'https://flagcdn.com/w40/cn.png',
    websiteProgress: 100,
    websiteTranslated: 156,
    websiteTotal: 156,
    robotProgress: 100,
    robotTranslated: 89,
    robotTotal: 89
  },
  {
    languageCode: 'en',
    languageName: 'English',
    nativeName: 'English',
    flagIcon: 'https://flagcdn.com/w40/us.png',
    websiteProgress: 85,
    websiteTranslated: 132,
    websiteTotal: 156,
    robotProgress: 92,
    robotTranslated: 82,
    robotTotal: 89
  },
  {
    languageCode: 'ja',
    languageName: '日本語',
    nativeName: '日本語',
    flagIcon: 'https://flagcdn.com/w40/jp.png',
    websiteProgress: 78,
    websiteTranslated: 122,
    websiteTotal: 156,
    robotProgress: 88,
    robotTranslated: 78,
    robotTotal: 89
  },
  {
    languageCode: 'ko',
    languageName: '한국어',
    nativeName: '한국어',
    flagIcon: 'https://flagcdn.com/w40/kr.png',
    websiteProgress: 65,
    websiteTranslated: 101,
    websiteTotal: 156,
    robotProgress: 71,
    robotTranslated: 63,
    robotTotal: 89
  }
])

// 模拟内容数据
const mockContentList = computed(() => {
  if (activeTab.value === 'website') {
    return [
      {
        id: 1,
        contentKey: 'home.hero.title',
        category: 'hero',
        defaultContent: '艺学，让每个人写好汉字。',
        translations: {
          en: 'YiXue, helping everyone write beautiful Chinese characters.',
          ja: 'YiXue、誰もが美しい漢字を書けるように。',
          ko: 'YiXue, 모든 사람이 아름다운 한자를 쓸 수 있도록.'
        },
        translationStatus: {
          en: 'published',
          ja: 'translated',
          ko: 'pending'
        }
      },
      {
        id: 2,
        contentKey: 'home.hero.subtitle',
        category: 'hero',
        defaultContent: '专业的AI练字机器人，结合传统书法艺术与现代科技',
        translations: {
          en: 'Professional AI calligraphy robot combining traditional art with modern technology',
          ja: '伝統的な書道芸術と現代技術を組み合わせた専門的なAI練字ロボット',
          ko: '전통 서예 예술과 현대 기술을 결합한 전문 AI 연습 로봇'
        },
        translationStatus: {
          en: 'published',
          ja: 'reviewed',
          ko: 'translated'
        }
      }
    ]
  } else {
    return [
      {
        id: 101,
        contentKey: 'menu.settings',
        category: 'menu',
        defaultContent: '设置',
        translations: {
          en: 'Settings',
          ja: '設定',
          ko: '설정'
        },
        translationStatus: {
          en: 'published',
          ja: 'published',
          ko: 'published'
        }
      },
      {
        id: 102,
        contentKey: 'menu.courses',
        category: 'menu',
        defaultContent: '课程',
        translations: {
          en: 'Courses',
          ja: 'コース',
          ko: '과정'
        },
        translationStatus: {
          en: 'published',
          ja: 'published',
          ko: 'translated'
        }
      }
    ]
  }
})

// 计算属性
const currentCategories = computed(() => {
  if (activeTab.value === 'website') {
    return ['hero', 'features', 'charity', 'products', 'news', 'contact', 'navigation']
  } else {
    return ['menu', 'button', 'message', 'error', 'dialog', 'form', 'status']
  }
})

// 表单验证规则
const languageRules = {
  languageCode: [
    { required: true, message: '请输入语言代码', trigger: 'blur' }
  ],
  languageName: [
    { required: true, message: '请输入语言名称', trigger: 'blur' }
  ],
  nativeName: [
    { required: true, message: '请输入本地名称', trigger: 'blur' }
  ]
}

const contentRules = {
  contentKey: [
    { required: true, message: '请输入内容键名', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  defaultContent: [
    { required: true, message: '请输入默认内容', trigger: 'blur' }
  ]
}

// 方法
const handleTabChange = (tab) => {
  activeTab.value = tab.name
  total.value = mockContentList.value.length
}

const handleSearch = () => {
  // 模拟搜索
  console.log('搜索:', searchKeyword.value)
}

const handleFilter = () => {
  // 模拟筛选
  console.log('筛选:', { categoryFilter: categoryFilter.value, statusFilter: statusFilter.value })
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (page) => {
  currentPage.value = page
}

// 工具方法
const getCategoryTagType = (category) => {
  const types = {
    hero: 'primary',
    features: 'success',
    charity: 'warning',
    menu: 'primary',
    button: 'success',
    message: 'info'
  }
  return types[category] || 'info'
}

const getStatusTagType = (status) => {
  const types = {
    pending: 'info',
    translated: 'warning',
    reviewed: 'primary',
    published: 'success'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待翻译',
    translated: '已翻译',
    reviewed: '已审核',
    published: '已发布'
  }
  return texts[status] || '未知'
}

// 对话框方法
const showAddLanguageDialog = () => {
  newLanguage.value = {
    languageCode: '',
    languageName: '',
    nativeName: '',
    flagIcon: '',
    sortOrder: 0,
    isEnabled: true
  }
  addLanguageDialogVisible.value = true
}

const showAddContentDialog = (contentType) => {
  newContent.value = {
    contentKey: '',
    category: '',
    defaultContent: '',
    contentType: contentType
  }
  addContentDialogVisible.value = true
}

const addLanguage = async () => {
  try {
    console.log('添加语言:', newLanguage.value)
    ElMessage.success('语言添加成功')
    addLanguageDialogVisible.value = false
  } catch (error) {
    ElMessage.error('添加语言失败')
  }
}

const addContent = async () => {
  try {
    console.log('添加内容:', newContent.value)
    ElMessage.success('内容添加成功')
    addContentDialogVisible.value = false
  } catch (error) {
    ElMessage.error('添加内容失败')
  }
}

const editContent = (row) => {
  console.log('编辑内容:', row)
  
  // 复制数据到编辑表单
  editingContent.value = {
    id: row.id,
    contentKey: row.contentKey,
    category: row.category,
    defaultContent: row.defaultContent,
    translations: { ...row.translations },
    translationStatus: { ...row.translationStatus }
  }
  
  // 打开编辑对话框
  editContentDialogVisible.value = true
}

const editDefaultContent = (row) => {
  console.log('编辑默认内容:', row)
  // 可以直接调用编辑内容功能
  editContent(row)
}

// 编辑对话框相关方法
const handleEditDialogClose = () => {
  editContentDialogVisible.value = false
  // 清空编辑数据
  editingContent.value = {
    id: null,
    contentKey: '',
    category: '',
    defaultContent: '',
    translations: {},
    translationStatus: {}
  }
}

const markAsTranslated = (languageCode) => {
  if (editingContent.value.translationStatus[languageCode]) {
    editingContent.value.translationStatus[languageCode] = 'translated'
    ElMessage.success(`${languageCode.toUpperCase()} 翻译已标记为已翻译`)
  }
}

const markAsReviewed = (languageCode) => {
  if (editingContent.value.translationStatus[languageCode]) {
    editingContent.value.translationStatus[languageCode] = 'reviewed'
    ElMessage.success(`${languageCode.toUpperCase()} 翻译已标记为已审核`)
  }
}

const publishSingleTranslation = (languageCode) => {
  if (editingContent.value.translationStatus[languageCode]) {
    editingContent.value.translationStatus[languageCode] = 'published'
    ElMessage.success(`${languageCode.toUpperCase()} 翻译已发布`)
  }
}

const saveAllTranslations = async () => {
  try {
    console.log('保存所有翻译:', editingContent.value)
    
    // 这里应该调用API保存翻译
    // await api.saveTranslations(editingContent.value)
    
    ElMessage.success('所有翻译已保存')
  } catch (error) {
    console.error('保存翻译失败:', error)
    ElMessage.error('保存翻译失败')
  }
}

const saveEditContent = async () => {
  try {
    console.log('保存编辑内容:', editingContent.value)
    
    // 这里应该调用API保存内容
    // await api.updateContent(editingContent.value)
    
    // 更新本地数据（在实际项目中应该重新获取数据）
    const currentList = mockContentList.value
    const index = currentList.findIndex(item => item.id === editingContent.value.id)
    if (index !== -1) {
      // 更新数据
      Object.assign(currentList[index], editingContent.value)
    }
    
    ElMessage.success('内容保存成功')
    editContentDialogVisible.value = false
    
    // 清空编辑数据
    editingContent.value = {
      id: null,
      contentKey: '',
      category: '',
      defaultContent: '',
      translations: {},
      translationStatus: {}
    }
  } catch (error) {
    console.error('保存内容失败:', error)
    ElMessage.error('保存内容失败')
  }
}

const updateTranslation = async (contentId, languageCode, content) => {
  try {
    console.log('更新翻译:', { contentId, languageCode, content })
    ElMessage.success('翻译更新成功')
  } catch (error) {
    ElMessage.error('更新翻译失败')
  }
}

const publishTranslation = async (contentId, languageCode) => {
  try {
    console.log('发布翻译:', { contentId, languageCode })
    ElMessage.success('翻译发布成功')
  } catch (error) {
    ElMessage.error('发布翻译失败')
  }
}

const deleteContent = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这个内容吗？', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    console.log('删除内容:', row.id)
    ElMessage.success('内容删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除内容失败')
    }
  }
}

onMounted(() => {
  total.value = mockContentList.value.length
})
</script>

<style lang="scss" scoped>
.language-management {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      h2 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 24px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
        font-weight: 400;
      }
    }
  }

  .progress-overview {
    margin-bottom: 24px;

    .progress-card {
      height: 100%;
      
      .language-progress {
        .lang-header {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 16px;

          .flag-icon {
            width: 24px;
            height: 16px;
            border-radius: 2px;
            object-fit: cover;
          }

          .lang-info {
            h4 {
              margin: 0;
              font-size: 16px;
              font-weight: 600;
              color: #262626;
            }

            .lang-code {
              font-size: 12px;
              color: #8c8c8c;
            }
          }
        }

        .progress-stats {
          .progress-item {
            margin-bottom: 12px;

            &:last-child {
              margin-bottom: 0;
            }

            .label {
              display: block;
              font-size: 12px;
              color: #8c8c8c;
              margin-bottom: 4px;
            }

            .stats {
              font-size: 12px;
              color: #595959;
              margin-top: 4px;
              display: block;
            }
          }
        }
      }
    }
  }

  .content-tabs {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;
    margin-bottom: 20px;

    .tab-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .tab-info {
        display: flex;
        align-items: center;
        gap: 8px;
        color: #595959;
        font-size: 14px;
      }
    }

    :deep(.el-tabs__header) {
      margin-bottom: 0;
    }

    :deep(.el-tabs__content) {
      padding-top: 0;
    }
  }

  .content-table {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;

    .table-filters {
      display: flex;
      gap: 12px;
      margin-bottom: 16px;
      align-items: center;
    }

    .content-key {
      .key-text {
        display: block;
        margin-top: 4px;
        font-family: 'Monaco', 'Menlo', monospace;
        font-size: 12px;
        color: #262626;
      }
    }

    .default-content {
      .content-text {
        display: block;
        margin-bottom: 4px;
        line-height: 1.4;
      }
    }

    .lang-header {
      display: flex;
      align-items: center;
      gap: 6px;

      .flag-icon-small {
        width: 16px;
        height: 12px;
        border-radius: 2px;
        object-fit: cover;
      }
    }

    .translation-cell {
      .translation-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 8px;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

:deep(.el-dialog) {
  .el-dialog__body {
    padding: 20px;
  }
}

:deep(.el-table) {
  .el-table__cell {
    padding: 12px 0;
  }
}

:deep(.el-progress) {
  .el-progress__text {
    font-size: 12px !important;
  }
}

// 编辑内容对话框样式
.translations-section {
  margin-top: 24px;
  
  h4 {
    margin: 0 0 16px 0;
    color: #303133;
    font-size: 16px;
    font-weight: 600;
    padding-bottom: 8px;
    border-bottom: 2px solid #409eff;
  }
  
  .translation-item {
    margin-bottom: 20px;
    padding: 16px;
    background: #f8f9fa;
    border-radius: 8px;
    border: 1px solid #e9ecef;
    
    .translation-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;
      
      .flag-icon-small {
        width: 20px;
        height: 14px;
        border-radius: 2px;
        object-fit: cover;
      }
      
      .lang-name {
        font-weight: 500;
        color: #303133;
        flex: 1;
      }
    }
    
    .translation-input {
      margin-bottom: 8px;
    }
    
    .translation-actions {
      display: flex;
      gap: 8px;
      justify-content: flex-end;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>