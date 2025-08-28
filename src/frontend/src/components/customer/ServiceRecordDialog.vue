<template>
  <el-dialog
    v-model="visible"
    :title="mode === 'create' ? '添加服务记录' : '编辑服务记录'"
    width="60%"
    :before-close="handleClose"
    class="service-record-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      class="service-form"
    >
      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="服务类型" prop="type">
            <el-select v-model="formData.type" placeholder="请选择服务类型">
              <el-option label="设备维护" value="maintenance" />
              <el-option label="技术咨询" value="consultation" />
              <el-option label="客户投诉" value="complaint" />
              <el-option label="培训服务" value="training" />
              <el-option label="系统升级" value="upgrade" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="优先级" prop="priority">
            <el-select v-model="formData.priority" placeholder="请选择优先级">
              <el-option label="低" value="low" />
              <el-option label="中" value="medium" />
              <el-option label="高" value="high" />
              <el-option label="紧急" value="urgent" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="服务标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入服务标题"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="详细描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述服务内容..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-row :gutter="24">
        <el-col :span="12">
          <el-form-item label="负责人" prop="assignedTo">
            <el-select 
              v-model="formData.assignedTo" 
              placeholder="请选择负责人"
              filterable
              allow-create
            >
              <el-option 
                v-for="staff in staffList" 
                :key="staff.id"
                :label="staff.name" 
                :value="staff.name" 
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="服务状态" prop="status">
            <el-select v-model="formData.status" placeholder="请选择状态">
              <el-option label="待处理" value="pending" />
              <el-option label="处理中" value="processing" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="预计完成时间">
        <el-date-picker
          v-model="formData.expectedCompletionDate"
          type="datetime"
          placeholder="选择预计完成时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DDTHH:mm:ss.sssZ"
        />
      </el-form-item>

      <el-form-item label="相关设备">
        <el-select 
          v-model="formData.relatedDevices" 
          multiple
          placeholder="选择相关设备（可选）"
          style="width: 100%"
        >
          <el-option 
            v-for="device in customerDevices" 
            :key="device.id"
            :label="`${device.model} - ${device.serialNumber}`" 
            :value="device.id" 
          />
        </el-select>
      </el-form-item>

      <el-form-item label="附件上传">
        <el-upload
          v-model:file-list="fileList"
          :action="uploadUrl"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          multiple
          :limit="5"
        >
          <el-button :icon="Upload">上传附件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持上传图片、文档等文件，单个文件不超过10MB，最多5个文件
            </div>
          </template>
        </el-upload>
      </el-form-item>
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
          {{ mode === 'create' ? '创建记录' : '保存修改' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import type { ServiceRecord, CustomerDevice } from '@/types/customer'

interface Props {
  modelValue: boolean
  serviceRecord: ServiceRecord | null
  customerDevices: CustomerDevice[]
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
const saving = ref(false)
const fileList = ref([])

// 员工列表（实际应该从API获取）
const staffList = ref([
  { id: '1', name: '客服小王' },
  { id: '2', name: '技术小李' },
  { id: '3', name: '维修小张' },
  { id: '4', name: '培训小刘' },
  { id: '5', name: '主管小陈' }
])

// 上传地址
const uploadUrl = '/api/upload/service-attachments'

// 表单数据
const formData = ref({
  type: '',
  title: '',
  description: '',
  priority: 'medium',
  status: 'pending',
  assignedTo: '',
  expectedCompletionDate: '',
  relatedDevices: [] as string[],
  attachments: [] as string[]
})

// 表单验证规则
const formRules = {
  type: [
    { required: true, message: '请选择服务类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入服务标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入详细描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10-500个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择服务状态', trigger: 'change' }
  ]
}

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isFormValid = computed(() => {
  return formData.value.type && 
         formData.value.title && 
         formData.value.description &&
         formData.value.priority &&
         formData.value.status
})

// 监听服务记录数据变化
watch(
  () => props.serviceRecord,
  (newRecord) => {
    if (newRecord && props.mode === 'edit') {
      initFormData(newRecord)
    } else if (props.mode === 'create') {
      resetFormData()
    }
  },
  { immediate: true }
)

// 监听对话框显示状态
watch(visible, (newVisible) => {
  if (newVisible) {
    if (props.mode === 'create') {
      resetFormData()
    } else if (props.serviceRecord) {
      initFormData(props.serviceRecord)
    }
  }
})

// 方法
const initFormData = (record: ServiceRecord) => {
  formData.value = {
    type: record.type,
    title: record.title,
    description: record.description,
    priority: record.priority,
    status: record.status,
    assignedTo: record.assignedTo || '',
    expectedCompletionDate: '',
    relatedDevices: [],
    attachments: []
  }
}

const resetFormData = () => {
  formData.value = {
    type: '',
    title: '',
    description: '',
    priority: 'medium',
    status: 'pending',
    assignedTo: '',
    expectedCompletionDate: '',
    relatedDevices: [],
    attachments: []
  }
  fileList.value = []
}

const handleClose = () => {
  visible.value = false
}

// 文件上传相关
const beforeUpload = (file: File) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/gif', 'application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只能上传图片、PDF或Word文档!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response: any, file: any) => {
  ElMessage.success('文件上传成功')
  formData.value.attachments.push(response.url)
}

const handleUploadError = (error: any) => {
  ElMessage.error('文件上传失败')
  console.error('Upload error:', error)
}

const handleSave = async () => {
  try {
    // 表单验证
    await formRef.value.validate()
    
    saving.value = true
    
    // 准备保存数据
    const saveData = {
      ...formData.value,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    }
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (props.mode === 'create') {
      ElMessage.success('服务记录创建成功')
    } else {
      ElMessage.success('服务记录更新成功')
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
.service-record-dialog {
  :deep(.el-dialog__body) {
    padding: 20px;
  }
}

.service-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px 0;
}
</style>