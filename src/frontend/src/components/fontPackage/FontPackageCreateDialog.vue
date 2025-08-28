<!--
  字体包创建对话框
  支持字体包的创建和基本配置
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="创建字体包"
    width="70%"
    :before-close="handleClose"
    class="font-package-create-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="create-form"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="字体包名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入字体包名称"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字体类型" prop="fontType">
              <el-select v-model="formData.fontType" placeholder="选择字体类型" style="width: 100%">
                <el-option label="楷书" value="kaishu" />
                <el-option label="行书" value="xingshu" />
                <el-option label="隶书" value="lishu" />
                <el-option label="篆书" value="zhuanshu" />
                <el-option label="草书" value="caoshu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="难度等级" prop="difficulty">
              <el-select v-model="formData.difficulty" placeholder="选择难度等级" style="width: 100%">
                <el-option label="简单 ★☆☆☆☆" :value="1" />
                <el-option label="较易 ★★☆☆☆" :value="2" />
                <el-option label="中等 ★★★☆☆" :value="3" />
                <el-option label="较难 ★★★★☆" :value="4" />
                <el-option label="困难 ★★★★★" :value="5" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input
                v-model="formData.version"
                placeholder="如：v1.0.0"
                maxlength="20"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="字体包描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入字体包描述..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </div>

      <!-- 目标字符设置 -->
      <div class="form-section">
        <h3 class="section-title">目标字符设置</h3>
        <el-form-item label="字符输入方式">
          <el-radio-group v-model="characterInputMode" @change="handleInputModeChange">
            <el-radio value="manual">手动输入</el-radio>
            <el-radio value="preset">预设字符集</el-radio>
            <el-radio value="file">文件导入</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 手动输入 -->
        <div v-if="characterInputMode === 'manual'" class="character-input-section">
          <el-form-item label="目标字符" prop="targetCharacters">
            <el-input
              v-model="characterInput"
              type="textarea"
              :rows="6"
              placeholder="请输入要生成的汉字，每个字符之间用空格或换行分隔..."
              @input="handleCharacterInput"
            />
          </el-form-item>
          <div class="character-preview">
            <div class="preview-header">
              <span>字符预览 ({{ formData.targetCharacters.length }}个)</span>
              <el-button size="small" @click="clearCharacters">清空</el-button>
            </div>
            <div class="character-list">
              <el-tag
                v-for="char in formData.targetCharacters"
                :key="char"
                closable
                @close="removeCharacter(char)"
                class="character-tag"
              >
                {{ char }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 预设字符集 -->
        <div v-else-if="characterInputMode === 'preset'" class="preset-section">
          <el-form-item label="选择字符集">
            <el-checkbox-group v-model="selectedPresets" @change="handlePresetChange">
              <el-checkbox value="common100">常用汉字100字</el-checkbox>
              <el-checkbox value="common500">常用汉字500字</el-checkbox>
              <el-checkbox value="primary">小学必学汉字</el-checkbox>
              <el-checkbox value="numbers">数字汉字</el-checkbox>
              <el-checkbox value="colors">颜色汉字</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </div>

        <!-- 文件导入 -->
        <div v-else-if="characterInputMode === 'file'" class="file-import-section">
          <el-form-item label="导入文件">
            <el-upload
              :before-upload="beforeFileUpload"
              :on-success="handleFileSuccess"
              :on-error="handleFileError"
              accept=".txt,.csv"
              :limit="1"
              :auto-upload="true"
              drag
            >
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或<em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 .txt 和 .csv 格式，每行一个汉字
                </div>
              </template>
            </el-upload>
          </el-form-item>
        </div>
      </div>

      <!-- 标签设置 -->
      <div class="form-section">
        <h3 class="section-title">标签设置</h3>
        <el-form-item label="字体包标签">
          <div class="tags-editor">
            <div class="current-tags">
              <el-tag
                v-for="tag in formData.tags"
                :key="tag"
                closable
                @close="removeTag(tag)"
                class="tag-item"
              >
                {{ tag }}
              </el-tag>
            </div>
            <div class="add-tag">
              <el-input
                v-model="newTag"
                placeholder="输入标签名称"
                size="small"
                style="width: 150px; margin-right: 8px;"
                @keyup.enter="addTag"
                maxlength="20"
              />
              <el-button size="small" @click="addTag" :disabled="!newTag.trim()">
                添加标签
              </el-button>
            </div>
            <div class="preset-tags">
              <span class="preset-label">推荐标签：</span>
              <el-tag
                v-for="preset in presetTags"
                :key="preset"
                size="small"
                type="info"
                @click="addPresetTag(preset)"
                class="preset-tag"
              >
                {{ preset }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleCreate"
          :loading="creating"
          :disabled="!isFormValid"
        >
          创建字体包
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import type { CreateFontPackageData } from '@/types/fontPackage'
import { mockFontPackageAPI } from '@/api/mock/fontPackage'

interface Props {
  modelValue: boolean
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref()
const creating = ref(false)
const characterInputMode = ref<'manual' | 'preset' | 'file'>('manual')
const characterInput = ref('')
const selectedPresets = ref<string[]>([])
const newTag = ref('')

// 预设标签
const presetTags = [
  'AI生成', '高质量', '专业', '教学', '练习',
  '标准', '美观', '实用', '推荐', '热门'
]

// 预设字符集
const presetCharacterSets: Record<string, string[]> = {
  common100: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '人', '大', '小', '上', '下', '左', '右', '中', '国', '家'], // 简化示例
  common500: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十'], // 简化示例
  primary: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十'], // 简化示例
  numbers: ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万'],
  colors: ['红', '黄', '蓝', '绿', '白', '黑', '灰', '紫', '粉', '橙']
}

// 表单数据
const formData = ref<CreateFontPackageData>({
  name: '',
  description: '',
  version: 'v1.0.0',
  fontType: 'kaishu',
  difficulty: 3,
  targetCharacters: [],
  tags: []
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入字体包名称', trigger: 'blur' },
    { min: 2, max: 50, message: '字体包名称长度在2-50个字符', trigger: 'blur' }
  ],
  fontType: [
    { required: true, message: '请选择字体类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度等级', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入字体包描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10-500个字符', trigger: 'blur' }
  ],
  targetCharacters: [
    { 
      validator: (rule: any, value: string[], callback: Function) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少添加一个目标字符'))
        } else if (value.length > 1000) {
          callback(new Error('目标字符数量不能超过1000个'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 计算属性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isFormValid = computed(() => {
  return formData.value.name && 
         formData.value.description && 
         formData.value.fontType &&
         formData.value.targetCharacters.length > 0
})

// 监听对话框显示状态
watch(dialogVisible, (newVisible) => {
  if (newVisible) {
    resetForm()
  }
})

// 方法
const resetForm = () => {
  formData.value = {
    name: '',
    description: '',
    version: 'v1.0.0',
    fontType: 'kaishu',
    difficulty: 3,
    targetCharacters: [],
    tags: []
  }
  characterInputMode.value = 'manual'
  characterInput.value = ''
  selectedPresets.value = []
  newTag.value = ''
}

const handleInputModeChange = () => {
  formData.value.targetCharacters = []
  characterInput.value = ''
  selectedPresets.value = []
}

const handleCharacterInput = () => {
  const chars = characterInput.value
    .split(/[\s\n,，]+/)
    .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
    .filter((char, index, arr) => arr.indexOf(char) === index) // 去重
  
  formData.value.targetCharacters = chars
}

const handlePresetChange = () => {
  const allChars = new Set<string>()
  
  selectedPresets.value.forEach(preset => {
    const chars = presetCharacterSets[preset] || []
    chars.forEach(char => allChars.add(char))
  })
  
  formData.value.targetCharacters = Array.from(allChars)
}

const removeCharacter = (char: string) => {
  const index = formData.value.targetCharacters.indexOf(char)
  if (index > -1) {
    formData.value.targetCharacters.splice(index, 1)
  }
}

const clearCharacters = () => {
  formData.value.targetCharacters = []
  characterInput.value = ''
}

// 标签管理
const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !formData.value.tags.includes(tag)) {
    formData.value.tags.push(tag)
    newTag.value = ''
  }
}

const addPresetTag = (tag: string) => {
  if (!formData.value.tags.includes(tag)) {
    formData.value.tags.push(tag)
  }
}

const removeTag = (tag: string) => {
  const index = formData.value.tags.indexOf(tag)
  if (index > -1) {
    formData.value.tags.splice(index, 1)
  }
}

// 文件上传
const beforeFileUpload = (file: File) => {
  const isValidType = file.type === 'text/plain' || file.name.endsWith('.csv')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传 .txt 或 .csv 格式的文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleFileSuccess = (response: any, file: File) => {
  // 这里应该处理文件上传成功后的字符解析
  ElMessage.success('文件上传成功，正在解析字符...')
  
  // 模拟文件解析
  const reader = new FileReader()
  reader.onload = (e) => {
    const content = e.target?.result as string
    const chars = content
      .split(/[\s\n,，]+/)
      .filter(char => char.trim() && /[\u4e00-\u9fa5]/.test(char))
      .filter((char, index, arr) => arr.indexOf(char) === index)
    
    formData.value.targetCharacters = chars
    ElMessage.success(`成功导入 ${chars.length} 个汉字`)
  }
  reader.readAsText(file.raw)
}

const handleFileError = () => {
  ElMessage.error('文件上传失败')
}

const handleCreate = async () => {
  try {
    await formRef.value.validate()
    
    creating.value = true
    
    // 调用API创建字体包
    await mockFontPackageAPI.createFontPackage(formData.value)
    
    ElMessage.success('字体包创建成功')
    emit('success')
    handleClose()
  } catch (error) {
    console.error('创建失败:', error)
    ElMessage.error('创建失败，请检查表单信息')
  } finally {
    creating.value = false
  }
}

const handleClose = () => {
  dialogVisible.value = false
}
</script>

<style lang="scss" scoped>
.font-package-create-dialog {
  .create-form {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;
  }

  .form-section {
    margin-bottom: 32px;
    
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
      padding-bottom: 8px;
      border-bottom: 2px solid var(--primary-color);
    }
  }

  .character-input-section {
    .character-preview {
      margin-top: 16px;
      padding: 16px;
      background: var(--bg-secondary);
      border-radius: 8px;
      
      .preview-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        font-weight: 500;
      }
      
      .character-list {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        
        .character-tag {
          font-size: 16px;
          padding: 4px 8px;
        }
      }
    }
  }

  .preset-section {
    .el-checkbox-group {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
  }

  .file-import-section {
    .el-upload-dragger {
      width: 100%;
      height: 180px;
    }
  }

  .tags-editor {
    .current-tags {
      margin-bottom: 16px;
      
      .tag-item {
        margin-right: 8px;
        margin-bottom: 8px;
      }
    }
    
    .add-tag {
      display: flex;
      align-items: center;
      margin-bottom: 16px;
    }
    
    .preset-tags {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
      
      .preset-label {
        font-size: 14px;
        color: var(--text-secondary);
        margin-right: 8px;
      }
      
      .preset-tag {
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          background: var(--primary-color);
          color: white;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding: 16px 0;
  }
}
</style>