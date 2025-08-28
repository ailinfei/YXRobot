<template>
  <div class="robot-language-manager">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <el-button type="primary" @click="handleAddTextItem">
          <el-icon><Plus /></el-icon>
          添加文字条目
        </el-button>
      </div>

    </div>

    <!-- 语言进度概览 -->
    <div class="language-progress">
      <div class="progress-header">
        <h3>翻译进度</h3>
      </div>
      <div class="progress-grid">
        <div
          v-for="lang in supportedLanguages"
          :key="lang.code"
          class="progress-item"
        >
          <div class="language-info">
            <div class="language-name">{{ lang.name }}</div>
            <div class="language-code">{{ lang.code }}</div>
          </div>
          <div class="progress-bar">
            <el-progress
              :percentage="getLanguageProgress(lang.code)"
              :stroke-width="8"
              :show-text="true"
              :color="getProgressColor(getLanguageProgress(lang.code))"
            />
          </div>
          <div class="progress-stats">
            <span>{{ getTranslatedCount(lang.code) }}/{{ totalTextItems }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 文字条目列表 -->
    <div class="text-items-list">
      <div class="list-header">
        <h3>文字条目管理</h3>
        <div class="view-controls">
          <el-radio-group v-model="viewMode" @change="handleViewModeChange">
            <el-radio-button label="table">表格视图</el-radio-button>
            <el-radio-button label="card">卡片视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 表格视图 -->
      <div v-if="viewMode === 'table'" class="table-view" v-loading="loading">
        <el-table
          :data="filteredTextItems"
          @selection-change="handleSelectionChange"
          row-key="key"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="key" label="键名" width="200" sortable>
            <template #default="{ row }">
              <div class="key-cell">
                <code>{{ row.key }}</code>
                <el-tag :type="getCategoryTagType(row.category)" size="small">
                  {{ getCategoryText(row.category) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" width="200" />
          <el-table-column label="翻译状态" width="150">
            <template #default="{ row }">
              <div class="translation-status">
                <el-progress
                  :percentage="getItemProgress(row)"
                  :stroke-width="6"
                  :show-text="false"
                  :color="getProgressColor(getItemProgress(row))"
                />
                <span class="status-text">
                  {{ getTranslatedLanguages(row).length }}/{{ supportedLanguages.length }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="翻译内容" min-width="400">
            <template #default="{ row }">
              <div class="translations-cell">
                <div
                  v-for="lang in supportedLanguages"
                  :key="lang.code"
                  class="translation-item"
                  :class="{ empty: !row.translations[lang.code] }"
                >
                  <div class="lang-label">{{ lang.code }}:</div>
                  <div class="translation-text">
                    {{ row.translations[lang.code] || '未翻译' }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" text @click="handleEditTextItem(row)">
                编辑
              </el-button>
              <el-button size="small" text @click="handleTranslateItem(row)">
                翻译
              </el-button>
              <el-button size="small" text type="danger" @click="handleDeleteTextItem(row.key)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else class="card-view" v-loading="loading">
        <div class="text-items-grid">
          <div
            v-for="item in filteredTextItems"
            :key="item.key"
            class="text-item-card"
            :class="{ selected: selectedItems.includes(item.key) }"
            @click="handleSelectItem(item)"
          >
            <div class="card-header">
              <div class="item-info">
                <div class="item-key">{{ item.key }}</div>
                <el-tag :type="getCategoryTagType(item.category)" size="small">
                  {{ getCategoryText(item.category) }}
                </el-tag>
              </div>
              <div class="item-progress">
                <el-progress
                  type="circle"
                  :percentage="getItemProgress(item)"
                  :width="40"
                  :stroke-width="4"
                  :color="getProgressColor(getItemProgress(item))"
                />
              </div>
            </div>
            
            <div class="card-content">
              <div class="item-description">{{ item.description }}</div>
              <div class="translations-preview">
                <div
                  v-for="lang in supportedLanguages.slice(0, 3)"
                  :key="lang.code"
                  class="translation-preview"
                  :class="{ empty: !item.translations[lang.code] }"
                >
                  <span class="lang-code">{{ lang.code }}:</span>
                  <span class="translation-text">
                    {{ item.translations[lang.code] || '未翻译' }}
                  </span>
                </div>
                <div v-if="supportedLanguages.length > 3" class="more-translations">
                  +{{ supportedLanguages.length - 3 }}种语言
                </div>
              </div>
            </div>
            
            <div class="card-actions">
              <el-button size="small" text @click.stop="handleEditTextItem(item)">
                编辑
              </el-button>
              <el-button size="small" text @click.stop="handleTranslateItem(item)">
                翻译
              </el-button>
              <el-button size="small" text type="danger" @click.stop="handleDeleteTextItem(item.key)">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 批量操作栏 -->
      <div v-if="selectedItems.length > 0" class="batch-actions">
        <div class="selected-info">
          已选择 {{ selectedItems.length }} 个条目
        </div>
        <div class="batch-buttons">
          <el-button size="small" @click="handleBatchTranslateSelected">
            批量翻译
          </el-button>
          <el-button size="small" @click="handleBatchDelete" type="danger">
            批量删除
          </el-button>
          <el-button size="small" @click="clearSelection">
            取消选择
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑文字条目对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="editingItem.key ? '编辑文字条目' : '添加文字条目'"
      width="800px"
      @close="handleEditDialogClose"
    >
      <el-form
        ref="textItemFormRef"
        :model="editingItem"
        :rules="textItemRules"
        label-width="100px"
      >
        <el-form-item label="键名" prop="key">
          <el-input
            v-model="editingItem.key"
            placeholder="如: menu.home, button.save"
            :disabled="!!editingItem.originalKey"
          />
        </el-form-item>
        
        <el-form-item label="分类" prop="category">
          <el-select v-model="editingItem.category" style="width: 100%">
            <el-option label="菜单" value="menu" />
            <el-option label="按钮" value="button" />
            <el-option label="提示信息" value="message" />
            <el-option label="错误信息" value="error" />
            <el-option label="系统信息" value="system" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="editingItem.description"
            placeholder="描述这个文字条目的用途"
          />
        </el-form-item>
        
        <el-form-item label="翻译内容">
          <MultiLanguageEditor
            v-model="editingItem.translations"
            :languages="supportedLanguages"
            :show-progress="true"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleEditDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSaveTextItem" :loading="saveLoading">
            保存
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量翻译对话框 -->
    <el-dialog
      v-model="batchTranslateDialogVisible"
      title="批量翻译"
      width="600px"
    >
      <div class="batch-translate-content">
        <div class="translate-options">
          <h4>翻译选项</h4>
          <el-form label-width="120px">
            <el-form-item label="源语言">
              <el-select v-model="batchTranslateForm.sourceLanguage">
                <el-option
                  v-for="lang in supportedLanguages"
                  :key="lang.code"
                  :label="lang.name"
                  :value="lang.code"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="目标语言">
              <el-select
                v-model="batchTranslateForm.targetLanguages"
                multiple
                placeholder="选择要翻译的语言"
              >
                <el-option
                  v-for="lang in supportedLanguages"
                  :key="lang.code"
                  :label="lang.name"
                  :value="lang.code"
                  :disabled="lang.code === batchTranslateForm.sourceLanguage"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="翻译服务">
              <el-select v-model="batchTranslateForm.translationService">
                <el-option label="Google 翻译" value="google" />
                <el-option label="百度翻译" value="baidu" />
                <el-option label="腾讯翻译" value="tencent" />
                <el-option label="DeepL" value="deepl" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="覆盖已有翻译">
              <el-switch v-model="batchTranslateForm.overwriteExisting" />
            </el-form-item>
          </el-form>
        </div>
        
        <div class="translate-preview">
          <h4>待翻译条目 ({{ selectedItems.length }}个)</h4>
          <div class="preview-list">
            <div
              v-for="key in selectedItems.slice(0, 5)"
              :key="key"
              class="preview-item"
            >
              <code>{{ key }}</code>
            </div>
            <div v-if="selectedItems.length > 5" class="more-items">
              还有 {{ selectedItems.length - 5 }} 个条目...
            </div>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchTranslateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleConfirmBatchTranslate" :loading="translateLoading">
            开始翻译
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
  Plus
} from '@element-plus/icons-vue'
import MultiLanguageEditor from '@/components/editors/MultiLanguageEditor.vue'

// 接口定义
interface TextItem {
  key: string
  category: 'menu' | 'button' | 'message' | 'error' | 'system'
  description: string
  translations: Record<string, string>
  createdAt: string
  updatedAt: string
}

interface Language {
  code: string
  name: string
  nativeName: string
}

// Props
const emit = defineEmits<{
  update: []
}>()

// 响应式数据
const loading = ref(false)
const saveLoading = ref(false)
const translateLoading = ref(false)

const viewMode = ref<'table' | 'card'>('table')
const selectedItems = ref<string[]>([])

// 文字条目数据
const textItems = ref<TextItem[]>([])
const totalTextItems = computed(() => textItems.value.length)

// 支持的语言
const supportedLanguages: Language[] = [
  { code: 'zh', name: '中文', nativeName: '中文' },
  { code: 'en', name: 'English', nativeName: 'English' },
  { code: 'ja', name: '日本語', nativeName: '日本語' },
  { code: 'ko', name: '한국어', nativeName: '한국어' },
  { code: 'es', name: 'Español', nativeName: 'Español' },
  { code: 'fr', name: 'Français', nativeName: 'Français' },
  { code: 'de', name: 'Deutsch', nativeName: 'Deutsch' },
  { code: 'ru', name: 'Русский', nativeName: 'Русский' }
]

// 对话框状态
const editDialogVisible = ref(false)
const batchTranslateDialogVisible = ref(false)

// 编辑数据
const editingItem = reactive<Partial<TextItem> & { originalKey?: string }>({
  key: '',
  category: 'menu',
  description: '',
  translations: {}
})

// 批量翻译表单
const batchTranslateForm = reactive({
  sourceLanguage: 'zh',
  targetLanguages: [] as string[],
  translationService: 'google',
  overwriteExisting: false
})

// 表单验证
const textItemRules = {
  key: [
    { required: true, message: '请输入键名', trigger: 'blur' },
    { pattern: /^[a-zA-Z][a-zA-Z0-9._]*$/, message: '键名格式不正确', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ]
}

const textItemFormRef = ref()

// 计算属性
const filteredTextItems = computed(() => {
  return [...textItems.value]
})

// 方法
const loadTextItems = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    textItems.value = [
      {
        key: 'menu.home',
        category: 'menu',
        description: '主页菜单项',
        translations: {
          zh: '主页',
          en: 'Home',
          ja: 'ホーム',
          ko: '홈',
          es: 'Inicio',
          fr: 'Accueil'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      },
      {
        key: 'button.save',
        category: 'button',
        description: '保存按钮',
        translations: {
          zh: '保存',
          en: 'Save',
          ja: '保存',
          ko: '저장',
          es: 'Guardar'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      },
      {
        key: 'message.success',
        category: 'message',
        description: '成功提示信息',
        translations: {
          zh: '操作成功',
          en: 'Operation successful',
          ja: '操作が成功しました'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载文字条目失败')
  } finally {
    loading.value = false
  }
}

const handleViewModeChange = () => {
  selectedItems.value = []
}

const handleSelectionChange = (selection: TextItem[]) => {
  selectedItems.value = selection.map(item => item.key)
}

const handleSelectItem = (item: TextItem) => {
  const index = selectedItems.value.indexOf(item.key)
  if (index > -1) {
    selectedItems.value.splice(index, 1)
  } else {
    selectedItems.value.push(item.key)
  }
}

const clearSelection = () => {
  selectedItems.value = []
}

const handleAddTextItem = () => {
  Object.assign(editingItem, {
    key: '',
    category: 'menu',
    description: '',
    translations: {},
    originalKey: undefined
  })
  
  // 初始化翻译对象
  supportedLanguages.forEach(lang => {
    editingItem.translations![lang.code] = ''
  })
  
  editDialogVisible.value = true
}

const handleEditTextItem = (item: TextItem) => {
  Object.assign(editingItem, { ...item, originalKey: item.key })
  
  // 确保所有语言都有对应的翻译字段
  supportedLanguages.forEach(lang => {
    if (!editingItem.translations![lang.code]) {
      editingItem.translations![lang.code] = ''
    }
  })
  
  editDialogVisible.value = true
}

const handleSaveTextItem = async () => {
  if (!textItemFormRef.value) return
  
  try {
    await textItemFormRef.value.validate()
    
    saveLoading.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (editingItem.originalKey) {
      // 更新现有条目
      const index = textItems.value.findIndex(item => item.key === editingItem.originalKey)
      if (index > -1) {
        textItems.value[index] = {
          ...editingItem,
          updatedAt: new Date().toISOString()
        } as TextItem
      }
      ElMessage.success('文字条目更新成功')
    } else {
      // 添加新条目
      const newItem = {
        ...editingItem,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      } as TextItem
      
      textItems.value.push(newItem)
      ElMessage.success('文字条目添加成功')
    }
    
    editDialogVisible.value = false
    emit('update')
  } catch (error) {
    ElMessage.error('保存文字条目失败')
  } finally {
    saveLoading.value = false
  }
}

const handleDeleteTextItem = async (key: string) => {
  try {
    await ElMessageBox.confirm('确定要删除这个文字条目吗？', '确认删除', {
      type: 'warning'
    })
    
    const index = textItems.value.findIndex(item => item.key === key)
    if (index > -1) {
      textItems.value.splice(index, 1)
      ElMessage.success('文字条目删除成功')
      emit('update')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除文字条目失败')
    }
  }
}

const handleTranslateItem = (item: TextItem) => {
  selectedItems.value = [item.key]
  handleBatchTranslate()
}

const handleBatchTranslate = () => {
  if (selectedItems.value.length === 0) {
    ElMessage.warning('请先选择要翻译的条目')
    return
  }
  
  batchTranslateForm.targetLanguages = []
  batchTranslateDialogVisible.value = true
}

const handleBatchTranslateSelected = () => {
  handleBatchTranslate()
}

const handleConfirmBatchTranslate = async () => {
  if (batchTranslateForm.targetLanguages.length === 0) {
    ElMessage.warning('请选择目标语言')
    return
  }
  
  translateLoading.value = true
  try {
    // 模拟翻译API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟翻译结果
    selectedItems.value.forEach(key => {
      const item = textItems.value.find(item => item.key === key)
      if (item) {
        const sourceText = item.translations[batchTranslateForm.sourceLanguage]
        if (sourceText) {
          batchTranslateForm.targetLanguages.forEach(targetLang => {
            if (batchTranslateForm.overwriteExisting || !item.translations[targetLang]) {
              // 这里应该调用真实的翻译API
              item.translations[targetLang] = `[${targetLang}] ${sourceText}`
            }
          })
          item.updatedAt = new Date().toISOString()
        }
      }
    })
    
    ElMessage.success('批量翻译完成')
    batchTranslateDialogVisible.value = false
    selectedItems.value = []
    emit('update')
  } catch (error) {
    ElMessage.error('批量翻译失败')
  } finally {
    translateLoading.value = false
  }
}

const handleBatchDelete = async () => {
  if (selectedItems.value.length === 0) return
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedItems.value.length} 个文字条目吗？`,
      '确认批量删除',
      { type: 'warning' }
    )
    
    textItems.value = textItems.value.filter(
      item => !selectedItems.value.includes(item.key)
    )
    
    selectedItems.value = []
    ElMessage.success('批量删除成功')
    emit('update')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleEditDialogClose = () => {
  editDialogVisible.value = false
  textItemFormRef.value?.resetFields()
}



// 工具方法
const getCategoryTagType = (category: string) => {
  const typeMap = {
    menu: 'primary',
    button: 'success',
    message: 'info',
    error: 'danger',
    system: 'warning'
  }
  return typeMap[category as keyof typeof typeMap] || 'info'
}

const getCategoryText = (category: string) => {
  const textMap = {
    menu: '菜单',
    button: '按钮',
    message: '提示',
    error: '错误',
    system: '系统'
  }
  return textMap[category as keyof typeof textMap] || category
}

const getLanguageProgress = (langCode: string) => {
  const totalItems = textItems.value.length
  if (totalItems === 0) return 0
  
  const translatedItems = textItems.value.filter(
    item => item.translations[langCode] && item.translations[langCode].trim() !== ''
  ).length
  
  return Math.round((translatedItems / totalItems) * 100)
}

const getTranslatedCount = (langCode: string) => {
  return textItems.value.filter(
    item => item.translations[langCode] && item.translations[langCode].trim() !== ''
  ).length
}

const getItemProgress = (item: TextItem) => {
  const totalLanguages = supportedLanguages.length
  const translatedLanguages = supportedLanguages.filter(
    lang => item.translations[lang.code] && item.translations[lang.code].trim() !== ''
  ).length
  
  return Math.round((translatedLanguages / totalLanguages) * 100)
}

const getTranslatedLanguages = (item: TextItem) => {
  return supportedLanguages.filter(
    lang => item.translations[lang.code] && item.translations[lang.code].trim() !== ''
  )
}

const getProgressColor = (percentage: number) => {
  if (percentage >= 90) return '#67C23A'
  if (percentage >= 70) return '#E6A23C'
  if (percentage >= 50) return '#409EFF'
  return '#F56C6C'
}

// 生命周期
onMounted(() => {
  loadTextItems()
})
</script>

<style lang="scss" scoped>
.robot-language-manager {
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

  .language-progress {
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .progress-header {
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
    }

    .progress-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 16px;

      .progress-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 12px;
        background: #f8f9fa;
        border-radius: 6px;

        .language-info {
          min-width: 80px;

          .language-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .language-code {
            font-size: 12px;
            color: #909399;
          }
        }

        .progress-bar {
          flex: 1;
        }

        .progress-stats {
          min-width: 60px;
          text-align: right;
          font-size: 12px;
          color: #606266;
        }
      }
    }
  }

  .text-items-list {
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
    }

    .table-view {
      padding: 20px;

      .key-cell {
        display: flex;
        flex-direction: column;
        gap: 4px;

        code {
          background: #f8f9fa;
          padding: 2px 6px;
          border-radius: 3px;
          font-size: 12px;
          color: #e83e8c;
        }
      }

      .translation-status {
        display: flex;
        align-items: center;
        gap: 8px;

        .status-text {
          font-size: 12px;
          color: #606266;
        }
      }

      .translations-cell {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .translation-item {
          display: flex;
          gap: 8px;
          font-size: 12px;

          .lang-label {
            min-width: 30px;
            color: #909399;
            font-weight: 500;
          }

          .translation-text {
            color: #606266;
          }

          &.empty .translation-text {
            color: #c0c4cc;
            font-style: italic;
          }
        }
      }
    }

    .card-view {
      padding: 20px;

      .text-items-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: 16px;

        .text-item-card {
          border: 1px solid #e4e7ed;
          border-radius: 8px;
          padding: 16px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            transform: translateY(-2px);
          }

          &.selected {
            border-color: #409eff;
            background: #f0f9ff;
          }

          .card-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 12px;

            .item-info {
              flex: 1;

              .item-key {
                font-family: 'Monaco', 'Menlo', monospace;
                font-size: 14px;
                font-weight: 600;
                color: #e83e8c;
                margin-bottom: 4px;
              }
            }
          }

          .card-content {
            margin-bottom: 12px;

            .item-description {
              font-size: 13px;
              color: #606266;
              margin-bottom: 8px;
            }

            .translations-preview {
              .translation-preview {
                display: flex;
                gap: 8px;
                margin-bottom: 2px;
                font-size: 12px;

                .lang-code {
                  min-width: 30px;
                  color: #909399;
                  font-weight: 500;
                }

                .translation-text {
                  color: #606266;
                }

                &.empty .translation-text {
                  color: #c0c4cc;
                  font-style: italic;
                }
              }

              .more-translations {
                font-size: 11px;
                color: #909399;
                margin-top: 4px;
              }
            }
          }

          .card-actions {
            display: flex;
            justify-content: flex-end;
            gap: 8px;
          }
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

  .batch-translate-content {
    .translate-options {
      margin-bottom: 24px;

      h4 {
        margin: 0 0 16px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }
    }

    .translate-preview {
      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }

      .preview-list {
        max-height: 200px;
        overflow-y: auto;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
        padding: 8px;

        .preview-item {
          padding: 4px 8px;
          margin-bottom: 4px;
          background: #f8f9fa;
          border-radius: 3px;

          code {
            font-size: 12px;
            color: #e83e8c;
          }
        }

        .more-items {
          padding: 4px 8px;
          color: #909399;
          font-size: 12px;
          font-style: italic;
        }
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
  .robot-language-manager {
    .language-progress {
      .progress-grid {
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      }
    }

    .text-items-list {
      .card-view {
        .text-items-grid {
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .robot-language-manager {
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

    .language-progress {
      .progress-grid {
        grid-template-columns: 1fr;
      }
    }

    .text-items-list {
      .list-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
      }

      .card-view {
        .text-items-grid {
          grid-template-columns: 1fr;
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