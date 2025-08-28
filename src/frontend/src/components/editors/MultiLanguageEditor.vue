<template>
  <div class="multi-language-editor">
    <!-- 语言标签页 -->
    <div class="language-tabs">
      <div class="tabs-header">
        <div class="tabs-nav">
          <div
            v-for="language in enabledLanguages"
            :key="language.code"
            :class="[
              'tab-item',
              { active: activeLanguage === language.code }
            ]"
            @click="switchLanguage(language.code)"
          >
            <img
              v-if="language.flag"
              :src="language.flag"
              :alt="language.name"
              class="language-flag"
            />
            <span class="language-name">{{ language.name }}</span>
            <el-badge
              v-if="showProgress"
              :value="getLanguageProgress(language.code)"
              :type="getProgressType(language.code)"
              class="progress-badge"
            />
            <el-icon
              v-if="hasUnsavedChanges(language.code)"
              class="unsaved-indicator"
            >
              <Warning />
            </el-icon>
          </div>
        </div>
        
        <div class="tabs-actions">
          <el-dropdown @command="handleLanguageAction">
            <el-button size="small" :icon="Setting" circle />
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="add">添加语言</el-dropdown-item>
                <el-dropdown-item command="remove">移除语言</el-dropdown-item>
                <el-dropdown-item command="import">导入翻译</el-dropdown-item>
                <el-dropdown-item command="export">导出翻译</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>

    <!-- 编辑器内容 -->
    <div class="editor-content">
      <div class="editor-toolbar" v-if="showToolbar">
        <div class="toolbar-left">
          <span class="char-count" v-if="showCharCount">
            {{ getCurrentCharCount() }} / {{ maxLength || '∞' }}
          </span>
          <el-tag
            v-if="requiredLanguages.includes(activeLanguage)"
            type="danger"
            size="small"
          >
            必填
          </el-tag>
        </div>
        
        <div class="toolbar-right">
          <el-button
            v-if="autoSave"
            size="small"
            :loading="saving"
            @click="saveContent"
          >
            {{ saving ? '保存中...' : '保存' }}
          </el-button>
          <el-button
            v-if="showHistory"
            size="small"
            @click="showVersionHistory"
          >
            历史版本
          </el-button>
        </div>
      </div>

      <!-- 文本编辑器 -->
      <div class="editor-wrapper">
        <el-input
          v-if="editorType === 'textarea'"
          v-model="currentContent"
          type="textarea"
          :placeholder="getPlaceholder()"
          :maxlength="maxLength"
          :show-word-limit="showCharCount"
          :rows="rows"
          :disabled="disabled"
          @input="handleContentChange"
          @blur="handleBlur"
          class="text-editor"
        />
        
        <!-- 富文本编辑器占位 -->
        <div
          v-else-if="editorType === 'rich'"
          class="rich-editor"
          ref="richEditorRef"
        >
          <!-- 这里可以集成富文本编辑器如Quill、TinyMCE等 -->
          <div class="rich-editor-placeholder">
            富文本编辑器 (待集成)
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Warning } from '@element-plus/icons-vue'

// 定义接口
interface Language {
  code: string
  name: string
  nativeName?: string
  flag?: string
  enabled: boolean
}

interface VersionHistory {
  id: string
  content: string
  timestamp: number
  author?: string
}

interface MultiLanguageEditorProps {
  modelValue: Record<string, string>
  languages: Language[]
  requiredLanguages?: string[]
  maxLength?: number
  placeholder?: string
  disabled?: boolean
  editorType?: 'textarea' | 'rich' | 'markdown'
  rows?: number
  showCharCount?: boolean
  showProgress?: boolean
  showToolbar?: boolean
  showHistory?: boolean
  autoSave?: boolean
  autoSaveInterval?: number
}

// Props定义
const props = withDefaults(defineProps<MultiLanguageEditorProps>(), {
  modelValue: () => ({}),
  languages: () => [],
  requiredLanguages: () => [],
  placeholder: '请输入内容',
  disabled: false,
  editorType: 'textarea',
  rows: 4,
  showCharCount: true,
  showProgress: true,
  showToolbar: true,
  showHistory: false,
  autoSave: false,
  autoSaveInterval: 30000
})

// Emits定义
const emit = defineEmits([
  'update:modelValue',
  'language-change',
  'content-change',
  'save',
  'blur'
])

// 响应式数据
const activeLanguage = ref('')
const saving = ref(false)
const unsavedChanges = ref<Set<string>>(new Set())
const versionHistory = ref<Record<string, VersionHistory[]>>({})
const richEditorRef = ref()

// 计算属性
const enabledLanguages = computed(() => {
  return props.languages.filter(lang => lang.enabled)
})

const currentContent = computed({
  get: () => props.modelValue[activeLanguage.value] || '',
  set: (value: string) => {
    const newValue = { ...props.modelValue }
    newValue[activeLanguage.value] = value
    emit('update:modelValue', newValue)
  }
})

// 方法
const switchLanguage = (languageCode: string) => {
  if (languageCode === activeLanguage.value) return
  
  activeLanguage.value = languageCode
  emit('language-change', languageCode)
  
  nextTick(() => {
    // 切换语言后的处理
  })
}

const getCurrentCharCount = () => {
  return currentContent.value.length
}

const getLanguageProgress = (languageCode: string) => {
  const content = props.modelValue[languageCode] || ''
  if (!content.trim()) return '0%'
  
  // 简单的进度计算：基于字符数
  const progress = Math.min(100, Math.round((content.length / (props.maxLength || 100)) * 100))
  return `${progress}%`
}

const getProgressType = (languageCode: string) => {
  const content = props.modelValue[languageCode] || ''
  if (!content.trim()) return 'info'
  if (props.requiredLanguages.includes(languageCode) && !content.trim()) return 'danger'
  return 'success'
}

const hasUnsavedChanges = (languageCode: string) => {
  return unsavedChanges.value.has(languageCode)
}

const getPlaceholder = () => {
  const currentLang = enabledLanguages.value.find(lang => lang.code === activeLanguage.value)
  if (currentLang) {
    return `请输入${currentLang.name}内容`
  }
  return props.placeholder
}

const handleContentChange = (value: string) => {
  unsavedChanges.value.add(activeLanguage.value)
  emit('content-change', activeLanguage.value, value)
  
  if (props.autoSave) {
    debounceAutoSave()
  }
}

const handleBlur = () => {
  emit('blur', activeLanguage.value, currentContent.value)
}

const saveContent = async () => {
  saving.value = true
  
  try {
    // 保存到版本历史
    saveToHistory()
    
    // 触发保存事件
    emit('save', props.modelValue)
    
    // 清除未保存标记
    unsavedChanges.value.clear()
    
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
    console.error('Save error:', error)
  } finally {
    saving.value = false
  }
}

const saveToHistory = () => {
  const languageCode = activeLanguage.value
  const content = currentContent.value
  
  if (!versionHistory.value[languageCode]) {
    versionHistory.value[languageCode] = []
  }
  
  const history: VersionHistory = {
    id: Date.now().toString(),
    content,
    timestamp: Date.now(),
    author: 'Current User' // 可以从用户状态获取
  }
  
  versionHistory.value[languageCode].unshift(history)
  
  // 限制历史版本数量
  if (versionHistory.value[languageCode].length > 10) {
    versionHistory.value[languageCode] = versionHistory.value[languageCode].slice(0, 10)
  }
}

const showVersionHistory = () => {
  // 显示版本历史对话框
  ElMessageBox.alert('版本历史功能待实现', '提示')
}

const handleLanguageAction = (command: string) => {
  switch (command) {
    case 'add':
      ElMessage.info('添加语言功能待实现')
      break
    case 'remove':
      ElMessage.info('移除语言功能待实现')
      break
    case 'import':
      ElMessage.info('导入翻译功能待实现')
      break
    case 'export':
      ElMessage.info('导出翻译功能待实现')
      break
  }
}

// 防抖自动保存
let autoSaveTimer: NodeJS.Timeout | null = null
const debounceAutoSave = () => {
  if (autoSaveTimer) {
    clearTimeout(autoSaveTimer)
  }
  
  autoSaveTimer = setTimeout(() => {
    saveContent()
  }, props.autoSaveInterval)
}

// 初始化
onMounted(() => {
  if (enabledLanguages.value.length > 0) {
    activeLanguage.value = enabledLanguages.value[0].code
  }
})

// 监听语言变化
watch(() => props.languages, (newLanguages) => {
  const enabled = newLanguages.filter(lang => lang.enabled)
  if (enabled.length > 0 && !activeLanguage.value) {
    activeLanguage.value = enabled[0].code
  }
}, { immediate: true })

// 暴露方法
defineExpose({
  switchLanguage,
  saveContent,
  getCurrentContent: () => currentContent.value,
  getActiveLanguage: () => activeLanguage.value,
  hasUnsavedChanges: () => unsavedChanges.value.size > 0
})
</script>

<style lang="scss" scoped>
.multi-language-editor {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;

  .language-tabs {
    .tabs-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: var(--bg-secondary);
      border-bottom: 1px solid var(--border-color);
      padding: 0 16px;

      .tabs-nav {
        display: flex;
        gap: 2px;

        .tab-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 16px;
          cursor: pointer;
          border-radius: var(--radius-md) var(--radius-md) 0 0;
          transition: var(--transition-normal);
          position: relative;

          &:hover {
            background: rgba(255, 255, 255, 0.5);
          }

          &.active {
            background: white;
            border-bottom: 2px solid var(--primary-color);
          }

          .language-flag {
            width: 20px;
            height: 15px;
            object-fit: cover;
            border-radius: 2px;
          }

          .language-name {
            font-size: 14px;
            font-weight: 500;
          }

          .progress-badge {
            :deep(.el-badge__content) {
              font-size: 10px;
              padding: 0 4px;
              height: 16px;
              line-height: 16px;
            }
          }

          .unsaved-indicator {
            color: var(--primary-color);
            font-size: 12px;
          }
        }
      }

      .tabs-actions {
        display: flex;
        align-items: center;
      }
    }
  }

  .editor-content {
    .editor-toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-bottom: 1px solid var(--border-light);
      background: var(--bg-tertiary);

      .toolbar-left {
        display: flex;
        align-items: center;
        gap: 12px;

        .char-count {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }

      .toolbar-right {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .editor-wrapper {
      padding: 16px;

      .text-editor {
        :deep(.el-textarea__inner) {
          border: none;
          box-shadow: none;
          resize: vertical;
          font-family: inherit;
          line-height: 1.6;

          &:focus {
            border: none;
            box-shadow: none;
          }
        }
      }

      .rich-editor {
        min-height: 200px;
        border: 1px solid var(--border-color);
        border-radius: var(--radius-md);

        .rich-editor-placeholder {
          display: flex;
          align-items: center;
          justify-content: center;
          height: 200px;
          color: var(--text-light);
          font-size: 14px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .multi-language-editor {
    .language-tabs {
      .tabs-header {
        flex-direction: column;
        gap: 12px;
        padding: 12px;

        .tabs-nav {
          flex-wrap: wrap;
          justify-content: center;

          .tab-item {
            padding: 8px 12px;
            font-size: 12px;

            .language-flag {
              width: 16px;
              height: 12px;
            }
          }
        }
      }
    }

    .editor-content {
      .editor-toolbar {
        flex-direction: column;
        gap: 8px;
        align-items: stretch;

        .toolbar-left,
        .toolbar-right {
          justify-content: center;
        }
      }
    }
  }
}
</style>