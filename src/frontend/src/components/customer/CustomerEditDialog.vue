<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'create' ? '添加客户' : '编辑客户'"
    width="80%"
    :before-close="handleClose"
    class="customer-edit-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="customer-form"
    >
      <el-tabs v-model="activeTab" class="form-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="form-section">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="客户姓名" prop="name">
                  <el-input
                    v-model="formData.name"
                    placeholder="请输入客户姓名"
                    maxlength="50"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="客户等级" prop="level">
                  <el-select v-model="formData.level" placeholder="请选择客户等级">
                    <el-option label="普通客户" value="regular" />
                    <el-option label="VIP客户" value="vip" />
                    <el-option label="高级客户" value="premium" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="手机号码" prop="phone">
                  <el-input
                    v-model="formData.phone"
                    placeholder="请输入手机号码"
                    maxlength="20"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="邮箱地址" prop="email">
                  <el-input
                    v-model="formData.email"
                    placeholder="请输入邮箱地址"
                    maxlength="100"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 地址信息 -->
        <el-tab-pane label="地址信息" name="address">
          <div class="form-section">
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="国家" prop="address.country">
                  <el-select v-model="formData.address.country" placeholder="请选择国家">
                    <el-option label="中国" value="中国" />
                    <el-option label="美国" value="美国" />
                    <el-option label="日本" value="日本" />
                    <el-option label="韩国" value="韩国" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="省份" prop="address.province">
                  <el-select v-model="formData.address.province" placeholder="请选择省份">
                    <el-option label="北京市" value="北京市" />
                    <el-option label="上海市" value="上海市" />
                    <el-option label="广东省" value="广东省" />
                    <el-option label="浙江省" value="浙江省" />
                    <el-option label="江苏省" value="江苏省" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="城市" prop="address.city">
                  <el-input
                    v-model="formData.address.city"
                    placeholder="请输入城市"
                    maxlength="50"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item label="区县" prop="address.district">
                  <el-input
                    v-model="formData.address.district"
                    placeholder="请输入区县"
                    maxlength="50"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="邮政编码" prop="address.zipCode">
                  <el-input
                    v-model="formData.address.zipCode"
                    placeholder="请输入邮政编码"
                    maxlength="10"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-form-item label="详细地址" prop="address.street">
              <el-input
                v-model="formData.address.street"
                placeholder="请输入详细地址"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 标签和备注 -->
        <el-tab-pane label="标签备注" name="tags">
          <div class="form-section">
            <el-form-item label="客户标签">
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
                  <span class="preset-label">常用标签：</span>
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

            <el-form-item label="客户备注">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="6"
                placeholder="请输入客户备注信息..."
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-tab-pane>

        <!-- 头像上传 -->
        <el-tab-pane label="头像设置" name="avatar">
          <div class="form-section">
            <div class="avatar-upload-section">
              <div class="current-avatar">
                <img 
                  :src="formData.avatar || defaultAvatar" 
                  alt="客户头像"
                  @error="handleAvatarError"
                />
              </div>
              <div class="upload-controls">
                <el-upload
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :http-request="handleAvatarUpload"
                  accept="image/*"
                  class="avatar-uploader"
                >
                  <el-button type="primary" :icon="Upload">
                    上传头像
                  </el-button>
                </el-upload>
                <el-button @click="removeAvatar" v-if="formData.avatar">
                  移除头像
                </el-button>
              </div>
              <div class="upload-tips">
                <p>• 支持 JPG、PNG 格式</p>
                <p>• 建议尺寸：200x200 像素</p>
                <p>• 文件大小不超过 2MB</p>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleSave"
          :loading="saving"
          :disabled="!isFormValid"
        >
          {{ mode === 'create' ? '创建客户' : '保存修改' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import type { Customer, CreateCustomerData, UpdateCustomerData } from '@/types/customer'
import { customerApi } from '@/api/customer'

interface Props {
  modelValue: boolean
  customer: Customer | null
  mode: 'create' | 'edit'
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const formRef = ref()
const activeTab = ref('basic')
const saving = ref(false)
const newTag = ref('')

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSIxMDAiIGN5PSIxMDAiIHI9IjEwMCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjEwMCIgeT0iMTEwIiBmb250LXNpemU9IjQwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBmaWxsPSIjOTk5Ij5VU0VSPC90ZXh0Pjwvc3ZnPg=='

// 预设标签
const presetTags = [
  '教育机构', '培训机构', '家庭用户', '企业客户',
  '长期客户', '新客户', 'VIP客户', '推荐客户',
  '批量采购', '租赁客户', '重点关注', '优质客户'
]

// 表单数据
const formData = ref<CreateCustomerData & { avatar?: string }>({
  name: '',
  email: '',
  phone: '',
  address: {
    country: '中国',
    province: '',
    city: '',
    district: '',
    street: '',
    zipCode: ''
  },
  level: 'regular',
  tags: [],
  notes: '',
  avatar: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入客户姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '客户姓名长度在2-50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  'address.province': [
    { required: true, message: '请选择省份', trigger: 'change' }
  ],
  'address.city': [
    { required: true, message: '请输入城市', trigger: 'blur' }
  ]
}

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isFormValid = computed(() => {
  return formData.value.name && 
         formData.value.phone && 
         formData.value.email &&
         formData.value.address.province &&
         formData.value.address.city
})

// 监听客户数据变化
watch(
  () => props.customer,
  (newCustomer) => {
    if (newCustomer && props.mode === 'edit') {
      initFormData(newCustomer)
    } else if (props.mode === 'create') {
      resetFormData()
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(visible, (newVisible) => {
  if (newVisible) {
    activeTab.value = 'basic'
    if (props.mode === 'create') {
      resetFormData()
    } else if (props.customer) {
      initFormData(props.customer)
    }
  }
})

// 方法
const initFormData = (customer: Customer) => {
  formData.value = {
    name: customer.name,
    email: customer.email,
    phone: customer.phone,
    address: { ...customer.address },
    level: customer.level,
    tags: [...customer.tags],
    notes: customer.notes || '',
    avatar: customer.avatar || ''
  }
}

const resetFormData = () => {
  formData.value = {
    name: '',
    email: '',
    phone: '',
    address: {
      country: '中国',
      province: '',
      city: '',
      district: '',
      street: '',
      zipCode: ''
    },
    level: 'regular',
    tags: [],
    notes: '',
    avatar: ''
  }
}

const handleClose = () => {
  visible.value = false
}

const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = defaultAvatar
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

// 头像管理
const beforeAvatarUpload = (file: File) => {
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

const handleAvatarUpload = async (options: any) => {
  try {
    // 这里应该调用实际的上传API
    // const response = await uploadApi.uploadAvatar(options.file)
    
    // 模拟上传成功
    formData.value.avatar = URL.createObjectURL(options.file)
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败')
  }
}

const removeAvatar = () => {
  formData.value.avatar = ''
  ElMessage.success('头像已移除')
}

const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate()
    
    saving.value = true
    
    // 准备保存数据
    const saveData: CreateCustomerData | UpdateCustomerData = {
      name: formData.value.name,
      email: formData.value.email,
      phone: formData.value.phone,
      address: formData.value.address,
      level: formData.value.level,
      tags: formData.value.tags,
      notes: formData.value.notes
    }
    
    if (props.mode === 'create') {
      await customerApi.createCustomer(saveData as CreateCustomerData)
      ElMessage.success('客户创建成功')
    } else if (props.customer) {
      await customerApi.updateCustomer(props.customer.id, saveData as UpdateCustomerData)
      ElMessage.success('客户更新成功')
    }
    
    emit('success')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请检查表单信息')
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.customer-edit-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.customer-form {
  .form-tabs {
    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 24px;
      background: var(--bg-secondary);
      border-bottom: 1px solid var(--border-color);
    }
    
    :deep(.el-tabs__content) {
      padding: 24px;
    }
  }
  
  .form-section {
    .el-form-item {
      margin-bottom: 24px;
    }
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

.avatar-upload-section {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  
  .current-avatar {
    img {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      object-fit: cover;
      border: 3px solid var(--border-color);
    }
  }
  
  .upload-controls {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .avatar-uploader {
      :deep(.el-upload) {
        border: 1px dashed var(--border-color);
        border-radius: 6px;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: var(--el-transition-duration-fast);
        
        &:hover {
          border-color: var(--primary-color);
        }
      }
    }
  }
  
  .upload-tips {
    color: var(--text-secondary);
    font-size: 12px;
    line-height: 1.5;
    
    p {
      margin: 4px 0;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-secondary);
}
</style>