<template>
  <div class="service-record-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="100px"
      @submit.prevent="handleSubmit"
    >
      <el-form-item label="服务类型" prop="type">
        <el-select v-model="formData.type" placeholder="请选择服务类型">
          <el-option label="维护服务" value="maintenance" />
          <el-option label="升级服务" value="upgrade" />
          <el-option label="咨询服务" value="consultation" />
          <el-option label="投诉处理" value="complaint" />
          <el-option label="培训服务" value="training" />
        </el-select>
      </el-form-item>

      <el-form-item label="服务标题" prop="title">
        <el-input
          v-model="formData.title"
          placeholder="请输入服务标题"
          maxlength="100"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="优先级" prop="priority">
        <el-select v-model="formData.priority" placeholder="请选择优先级">
          <el-option label="低" value="low" />
          <el-option label="中" value="medium" />
          <el-option label="高" value="high" />
          <el-option label="紧急" value="urgent" />
        </el-select>
      </el-form-item>

      <el-form-item label="负责人" prop="assignedTo">
        <el-select v-model="formData.assignedTo" placeholder="请选择负责人" filterable>
          <el-option
            v-for="staff in staffList"
            :key="staff.id"
            :label="staff.name"
            :value="staff.name"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="服务描述" prop="description">
        <el-input
          v-model="formData.description"
          type="textarea"
          :rows="4"
          placeholder="请详细描述服务内容、问题现象、处理要求等"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>

      <el-form-item label="相关设备" prop="deviceId">
        <el-select v-model="formData.deviceId" placeholder="请选择相关设备（可选）" clearable>
          <el-option
            v-for="device in customerDevices"
            :key="device.id"
            :label="`${device.model} (${device.serialNumber})`"
            :value="device.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="预期完成时间" prop="expectedCompletionDate">
        <el-date-picker
          v-model="formData.expectedCompletionDate"
          type="datetime"
          placeholder="请选择预期完成时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>

      <el-form-item label="附件上传">
        <el-upload
          v-model:file-list="fileList"
          :action="uploadUrl"
          :headers="uploadHeaders"
          :before-upload="beforeUpload"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          multiple
          :limit="5"
          accept=".jpg,.jpeg,.png,.pdf,.doc,.docx"
        >
          <el-button type="primary">
            <el-icon><Upload /></el-icon>
            上传附件
          </el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持jpg/png/pdf/doc格式，单个文件不超过10MB，最多5个文件
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <div class="form-actions">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">
        提交
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import type { CustomerDevice } from '@/types/customer'

interface ServiceRecordFormProps {
  customerId: string
  customerDevices?: CustomerDevice[]
}

const props = withDefaults(defineProps<ServiceRecordFormProps>(), {
  customerDevices: () => []
})

const emit = defineEmits<{
  submit: [data: any]
  cancel: []
}>()

// 响应式数据
const formRef = ref<FormInstance>()
const submitting = ref(false)
const fileList = ref<any[]>([])

const formData = reactive({
  type: '',
  title: '',
  description: '',
  priority: 'medium',
  assignedTo: '',
  deviceId: '',
  expectedCompletionDate: ''
})

// 表单验证规则
const formRules: FormRules = {
  type: [
    { required: true, message: '请选择服务类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入服务标题', trigger: 'blur' },
    { min: 2, max: 100, message: '标题长度在2-100个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入服务描述', trigger: 'blur' },
    { min: 10, max: 500, message: '描述长度在10-500个字符', trigger: 'blur' }
  ],
  priority: [
    { required: true, message: '请选择优先级', trigger: 'change' }
  ]
}

// 员工列表（实际应该从API获取）
const staffList = ref([
  { id: '1', name: '客服小王', department: '客服部' },
  { id: '2', name: '技术小李', department: '技术部' },
  { id: '3', name: '维修小张', department: '维修部' },
  { id: '4', name: '培训小陈', department: '培训部' },
  { id: '5', name: '销售小刘', department: '销售部' }
])

// 上传配置
const uploadUrl = computed(() => '/api/upload/service-attachments')
const uploadHeaders = computed(() => ({
  'Authorization': `Bearer ${localStorage.getItem('token') || ''}`
}))

// 方法
const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    const submitData = {
      ...formData,
      customerId: props.customerId,
      attachments: fileList.value.map(file => ({
        name: file.name,
        url: file.response?.data?.url || file.url,
        size: file.size
      })),
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      status: 'pending'
    }

    emit('submit', submitData)
    ElMessage.success('服务记录创建成功')
    
    // 重置表单
    formRef.value.resetFields()
    fileList.value = []
    
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  emit('cancel')
}

// 文件上传处理
const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isValidType = [
    'image/jpeg',
    'image/png',
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
  ].includes(file.type)
  
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('只支持jpg/png/pdf/doc格式的文件')
    return false
  }
  
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB')
    return false
  }
  
  return true
}

const handleUploadSuccess = (response: any, file: any) => {
  ElMessage.success(`${file.name} 上传成功`)
}

const handleUploadError = (error: any, file: any) => {
  ElMessage.error(`${file.name} 上传失败`)
  console.error('上传错误:', error)
}
</script>

<style lang="scss" scoped>
.service-record-form {
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
    padding-top: 16px;
    border-top: 1px solid #e4e7ed;
  }

  :deep(.el-upload__tip) {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }

  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #606266;
  }

  :deep(.el-textarea__inner) {
    resize: vertical;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .service-record-form {
    :deep(.el-form-item) {
      .el-form-item__label {
        width: 80px !important;
      }
    }

    .form-actions {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>