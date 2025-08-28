<template>
  <div class="editor-example">
    <el-card class="example-card">
      <template #header>
        <div class="card-header">
          <span>多语言编辑器示例</span>
          <el-button type="primary" @click="resetContent">重置内容</el-button>
        </div>
      </template>

      <!-- 配置面板 -->
      <div class="config-panel">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-checkbox v-model="editorConfig.showCharCount">显示字符计数</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="editorConfig.showProgress">显示进度</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="editorConfig.showToolbar">显示工具栏</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="editorConfig.autoSave">自动保存</el-checkbox>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top: 10px;">
          <el-col :span="6">
            <el-input-number
              v-model="editorConfig.maxLength"
              :min="0"
              :max="1000"
              :step="50"
            />
            <span style="margin-left: 8px;">最大长度</span>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="editorConfig.rows"
              :min="2"
              :max="10"
              :step="1"
            />
            <span style="margin-left: 8px;">行数</span>
          </el-col>
          <el-col :span="6">
            <el-switch
              v-model="editorConfig.disabled"
              active-text="禁用"
              inactive-text="启用"
            />
          </el-col>
          <el-col :span="6">
            <el-button @click="toggleLanguage">切换语言</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 多语言编辑器 -->
      <MultiLanguageEditor
        ref="editorRef"
        v-model="editorContent"
        :languages="availableLanguages"
        :required-languages="requiredLanguages"
        :max-length="editorConfig.maxLength"
        :disabled="editorConfig.disabled"
        :rows="editorConfig.rows"
        :show-char-count="editorConfig.showCharCount"
        :show-progress="editorConfig.showProgress"
        :show-toolbar="editorConfig.showToolbar"
        :auto-save="editorConfig.autoSave"
        @language-change="handleLanguageChange"
        @content-change="handleContentChange"
        @save="handleSave"
        @blur="handleBlur"
      />

      <!-- 内容预览 -->
      <div class="content-preview">
        <el-divider>内容预览</el-divider>
        <el-tabs v-model="previewTab" type="card">
          <el-tab-pane
            v-for="language in enabledLanguages"
            :key="language.code"
            :label="language.name"
            :name="language.code"
          >
            <div class="preview-content">
              <div class="preview-header">
                <span class="language-info">
                  {{ language.name }} ({{ language.code }})
                </span>
                <span class="char-info">
                  字符数: {{ (editorContent[language.code] || '').length }}
                </span>
              </div>
              <div class="preview-text">
                {{ editorContent[language.code] || '暂无内容' }}
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 操作日志 -->
      <div class="action-log" v-if="actionLogs.length > 0">
        <el-divider>操作日志</el-divider>
        <div class="log-container">
          <div
            v-for="(log, index) in actionLogs.slice(-5)"
            :key="index"
            class="log-item"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-action">{{ log.action }}</span>
            <span class="log-detail">{{ log.detail }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import MultiLanguageEditor from './MultiLanguageEditor.vue'

// 响应式数据
const editorRef = ref()
const previewTab = ref('zh-CN')
const actionLogs = ref<any[]>([])

// 编辑器配置
const editorConfig = reactive({
  showCharCount: true,
  showProgress: true,
  showToolbar: true,
  autoSave: false,
  maxLength: 200,
  rows: 4,
  disabled: false
})

// 可用语言
const availableLanguages = ref([
  {
    code: 'zh-CN',
    name: '中文',
    nativeName: '中文',
    flag: 'https://flagcdn.com/w80/cn.png',
    enabled: true
  },
  {
    code: 'en-US',
    name: '英文',
    nativeName: 'English',
    flag: 'https://flagcdn.com/w80/us.png',
    enabled: true
  },
  {
    code: 'ja-JP',
    name: '日文',
    nativeName: '日本語',
    flag: 'https://flagcdn.com/w80/jp.png',
    enabled: true
  },
  {
    code: 'ko-KR',
    name: '韩文',
    nativeName: '한국어',
    flag: 'https://flagcdn.com/w80/kr.png',
    enabled: false
  },
  {
    code: 'es-ES',
    name: '西班牙文',
    nativeName: 'Español',
    flag: 'https://flagcdn.com/w80/es.png',
    enabled: false
  }
])

// 必填语言
const requiredLanguages = ref(['zh-CN', 'en-US'])

// 编辑器内容
const editorContent = ref({
  'zh-CN': '这是中文内容示例。您可以在这里输入中文文本。',
  'en-US': 'This is an English content example. You can input English text here.',
  'ja-JP': 'これは日本語のコンテンツ例です。ここに日本語のテキストを入力できます。'
})

// 计算属性
const enabledLanguages = computed(() => {
  return availableLanguages.value.filter(lang => lang.enabled)
})

// 方法
const handleLanguageChange = (languageCode: string) => {
  previewTab.value = languageCode
  addActionLog('语言切换', `切换到${getLanguageName(languageCode)}`)
}

const handleContentChange = (languageCode: string, content: string) => {
  addActionLog('内容变更', `${getLanguageName(languageCode)}: ${content.length}字符`)
}

const handleSave = (content: Record<string, string>) => {
  addActionLog('保存', `保存了${Object.keys(content).length}种语言的内容`)
  ElMessage.success('内容已保存')
}

const handleBlur = (languageCode: string, content: string) => {
  addActionLog('失去焦点', `${getLanguageName(languageCode)}: ${content.length}字符`)
}

const resetContent = () => {
  editorContent.value = {
    'zh-CN': '',
    'en-US': '',
    'ja-JP': ''
  }
  addActionLog('重置', '清空了所有语言的内容')
  ElMessage.info('内容已重置')
}

const toggleLanguage = () => {
  const korean = availableLanguages.value.find(lang => lang.code === 'ko-KR')
  if (korean) {
    korean.enabled = !korean.enabled
    addActionLog('语言切换', `${korean.enabled ? '启用' : '禁用'}了${korean.name}`)
  }
}

const getLanguageName = (code: string) => {
  const language = availableLanguages.value.find(lang => lang.code === code)
  return language?.name || code
}

const addActionLog = (action: string, detail: string) => {
  actionLogs.value.push({
    time: new Date().toLocaleTimeString(),
    action,
    detail
  })
}
</script>

<style lang="scss" scoped>
.editor-example {
  padding: 20px;

  .example-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .config-panel {
    margin-bottom: 20px;
    padding: 16px;
    background: var(--bg-secondary);
    border-radius: var(--radius-md);
  }

  .content-preview {
    margin-top: 20px;

    .preview-content {
      .preview-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        padding-bottom: 8px;
        border-bottom: 1px solid var(--border-light);

        .language-info {
          font-weight: 500;
          color: var(--text-primary);
        }

        .char-info {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }

      .preview-text {
        padding: 12px;
        background: var(--bg-tertiary);
        border-radius: var(--radius-md);
        min-height: 80px;
        line-height: 1.6;
        color: var(--text-primary);
        white-space: pre-wrap;
      }
    }
  }

  .action-log {
    margin-top: 20px;

    .log-container {
      max-height: 200px;
      overflow-y: auto;
      background: var(--bg-tertiary);
      border-radius: var(--radius-md);
      padding: 12px;

      .log-item {
        display: flex;
        gap: 12px;
        padding: 4px 0;
        font-size: 12px;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .log-time {
          color: var(--text-light);
          min-width: 80px;
        }

        .log-action {
          color: var(--primary-color);
          font-weight: 500;
          min-width: 60px;
        }

        .log-detail {
          color: var(--text-secondary);
          flex: 1;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .editor-example {
    padding: 10px;
    
    .config-panel {
      .el-row {
        .el-col {
          margin-bottom: 10px;
        }
      }
    }
  }
}
</style>