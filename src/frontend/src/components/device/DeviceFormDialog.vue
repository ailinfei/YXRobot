<!--
  设备表单对话框组件
  功能：设备创建和编辑
-->
<template>
  <el-dialog
    v-model="visible"
    :title="isEdit ? '编辑设备' : '添加设备'"
    width="800px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      label-position="left"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <div class="form-grid">
          <el-form-item label="设备序列号" prop="serialNumber">
            <el-input 
              v-model="formData.serialNumber" 
              placeholder="请输入设备序列号"
              :disabled="isEdit"
            />
          </el-form-item>
          
          <el-form-item label="设备型号" prop="model">
            <el-select v-model="formData.model" placeholder="请选择设备型号">
              <el-option label="教育版 (YX-EDU-2024)" value="YX-EDU-2024" />
              <el-option label="家庭版 (YX-HOME-2024)" value="YX-HOME-2024" />
              <el-option label="专业版 (YX-PRO-2024)" value="YX-PRO-2024" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="所属客户" prop="customerId">
            <el-select 
              v-model="formData.customerId" 
              placeholder="请选择客户"
              filterable
              remote
              :remote-method="searchCustomers"
              :loading="customerLoading"
            >
              <el-option
                v-for="customer in customerOptions"
                :key="customer.id"
                :label="customer.name"
                :value="customer.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="固件版本" prop="firmwareVersion">
            <el-input 
              v-model="formData.firmwareVersion" 
              placeholder="请输入固件版本，如：1.0.0"
            />
          </el-form-item>
        </div>
      </div>

      <!-- 技术参数 -->
      <div class="form-section">
        <h3 class="section-title">技术参数</h3>
        <div class="form-grid">
          <el-form-item label="处理器" prop="specifications.cpu">
            <el-input 
              v-model="formData.specifications.cpu" 
              placeholder="请输入处理器信息"
            />
          </el-form-item>
          
          <el-form-item label="内存" prop="specifications.memory">
            <el-input 
              v-model="formData.specifications.memory" 
              placeholder="请输入内存信息"
            />
          </el-form-item>
          
          <el-form-item label="存储" prop="specifications.storage">
            <el-input 
              v-model="formData.specifications.storage" 
              placeholder="请输入存储信息"
            />
          </el-form-item>
          
          <el-form-item label="显示屏" prop="specifications.display">
            <el-input 
              v-model="formData.specifications.display" 
              placeholder="请输入显示屏信息"
            />
          </el-form-item>
          
          <el-form-item label="电池" prop="specifications.battery">
            <el-input 
              v-model="formData.specifications.battery" 
              placeholder="请输入电池信息"
            />
          </el-form-item>
          
          <el-form-item label="连接性" prop="specifications.connectivity">
            <el-select 
              v-model="formData.specifications.connectivity" 
              multiple 
              placeholder="请选择连接方式"
              style="width: 100%"
            >
              <el-option label="WiFi 6" value="WiFi 6" />
              <el-option label="WiFi 5" value="WiFi 5" />
              <el-option label="Bluetooth 5.0" value="Bluetooth 5.0" />
              <el-option label="Bluetooth 4.2" value="Bluetooth 4.2" />
              <el-option label="4G LTE" value="4G LTE" />
              <el-option label="5G" value="5G" />
              <el-option label="以太网" value="Ethernet" />
              <el-option label="USB-C" value="USB-C" />
            </el-select>
          </el-form-item>
        </div>
      </div>

      <!-- 设备配置 -->
      <div class="form-section">
        <h3 class="section-title">设备配置</h3>
        <div class="form-grid">
          <el-form-item label="语言" prop="configuration.language">
            <el-select v-model="formData.configuration.language" placeholder="请选择语言">
              <el-option label="简体中文" value="zh-CN" />
              <el-option label="English" value="en-US" />
              <el-option label="日本語" value="ja-JP" />
              <el-option label="한국어" value="ko-KR" />
              <el-option label="Español" value="es-ES" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="时区" prop="configuration.timezone">
            <el-select v-model="formData.configuration.timezone" placeholder="请选择时区">
              <el-option label="北京时间 (Asia/Shanghai)" value="Asia/Shanghai" />
              <el-option label="东京时间 (Asia/Tokyo)" value="Asia/Tokyo" />
              <el-option label="首尔时间 (Asia/Seoul)" value="Asia/Seoul" />
              <el-option label="纽约时间 (America/New_York)" value="America/New_York" />
              <el-option label="伦敦时间 (Europe/London)" value="Europe/London" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="自动更新">
            <el-switch v-model="formData.configuration.autoUpdate" />
          </el-form-item>
          
          <el-form-item label="调试模式">
            <el-switch v-model="formData.configuration.debugMode" />
          </el-form-item>
        </div>
      </div>

      <!-- 位置信息 -->
      <div class="form-section">
        <h3 class="section-title">位置信息 <span class="optional">(可选)</span></h3>
        <div class="form-grid">
          <el-form-item label="地址" prop="location.address">
            <el-input 
              v-model="formData.location.address" 
              placeholder="请输入设备地址"
              type="textarea"
              :rows="2"
            />
          </el-form-item>
          
          <el-form-item label="纬度" prop="location.latitude">
            <el-input-number 
              v-model="formData.location.latitude" 
              placeholder="请输入纬度"
              :precision="6"
              :min="-90"
              :max="90"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="经度" prop="location.longitude">
            <el-input-number 
              v-model="formData.location.longitude" 
              placeholder="请输入经度"
              :precision="6"
              :min="-180"
              :max="180"
              style="width: 100%"
            />
          </el-form-item>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="form-section">
        <h3 class="section-title">备注信息 <span class="optional">(可选)</span></h3>
        <el-form-item prop="notes">
          <el-input 
            v-model="formData.notes" 
            placeholder="请输入设备备注信息"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { 
  ManagedDevice, 
  CreateManagedDeviceData, 
  UpdateManagedDeviceData,
  DeviceModel,
  DeviceStatus,
  CustomerOption
} from '@/types/managedDevice'
import { managedDeviceAPI } from '@/api/managedDevice'

interface Props {
  modelValue: boolean
  device: ManagedDevice | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'success'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const customerLoading = ref(false)
const customerOptions = ref<Array<{ id: string; name: string }>>([])

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const isEdit = computed(() => !!props.device)

// 表单数据
const defaultFormData = (): CreateManagedDeviceData => ({
  serialNumber: '',
  model: DeviceModel.YX_EDU_2024,
  customerId: '',
  firmwareVersion: '1.0.0',
  specifications: {
    cpu: '',
    memory: '',
    storage: '',
    display: '',
    battery: '',
    connectivity: []
  },
  configuration: {
    language: 'zh-CN',
    timezone: 'Asia/Shanghai',
    autoUpdate: true,
    debugMode: false,
    customSettings: {}
  },
  location: {
    address: '',
    latitude: 0,
    longitude: 0,
    lastUpdated: new Date().toISOString()
  },
  notes: ''
})

const formData = ref<CreateManagedDeviceData>(defaultFormData())

// 表单验证规则
const formRules: FormRules = {
  serialNumber: [
    { required: true, message: '请输入设备序列号', trigger: 'blur' },
    { min: 6, max: 50, message: '序列号长度应在6-50个字符之间', trigger: 'blur' },
    { pattern: /^[A-Z0-9-]+$/, message: '序列号只能包含大写字母、数字和连字符', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请选择设备型号', trigger: 'change' }
  ],
  customerId: [
    { required: true, message: '请选择所属客户', trigger: 'change' }
  ],
  firmwareVersion: [
    { required: true, message: '请输入固件版本', trigger: 'blur' },
    { pattern: /^\d+\.\d+\.\d+$/, message: '固件版本格式应为 x.y.z', trigger: 'blur' }
  ],
  'specifications.cpu': [
    { required: true, message: '请输入处理器信息', trigger: 'blur' }
  ],
  'specifications.memory': [
    { required: true, message: '请输入内存信息', trigger: 'blur' }
  ],
  'specifications.storage': [
    { required: true, message: '请输入存储信息', trigger: 'blur' }
  ],
  'specifications.display': [
    { required: true, message: '请输入显示屏信息', trigger: 'blur' }
  ],
  'specifications.battery': [
    { required: true, message: '请输入电池信息', trigger: 'blur' }
  ],
  'configuration.language': [
    { required: true, message: '请选择语言', trigger: 'change' }
  ],
  'configuration.timezone': [
    { required: true, message: '请选择时区', trigger: 'change' }
  ]
}

// 监听设备变化，填充表单
watch(() => props.device, (device) => {
  if (device) {
    formData.value = {
      serialNumber: device.serialNumber,
      model: device.model,
      customerId: device.customerId,
      firmwareVersion: device.firmwareVersion || '1.0.0',
      specifications: device.specifications ? { ...device.specifications } : {
        cpu: '',
        memory: '',
        storage: '',
        display: '',
        battery: '',
        connectivity: []
      },
      configuration: device.configuration ? { ...device.configuration } : {
        language: 'zh-CN',
        timezone: 'Asia/Shanghai',
        autoUpdate: true,
        debugMode: false,
        customSettings: {}
      },
      location: device.location ? { ...device.location } : {
        address: '',
        latitude: 0,
        longitude: 0,
        lastUpdated: new Date().toISOString()
      },
      notes: device.notes || ''
    }
  } else {
    formData.value = defaultFormData()
  }
}, { immediate: true })

// 监听对话框显示，加载客户选项
watch(visible, (show) => {
  if (show) {
    loadCustomerOptions()
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  }
})

// 加载客户选项
const loadCustomerOptions = async () => {
  try {
    customerLoading.value = true
    const customers = await managedDeviceAPI.getCustomerOptions()
    customerOptions.value = customers
  } catch (error) {
    console.error('加载客户选项失败:', error)
  } finally {
    customerLoading.value = false
  }
}

// 搜索客户
const searchCustomers = async (query: string) => {
  if (query) {
    // 这里可以实现客户搜索功能
    // 暂时使用现有的客户列表进行过滤
    const filtered = customerOptions.value.filter(customer => 
      customer.name.toLowerCase().includes(query.toLowerCase())
    )
    customerOptions.value = filtered
  } else {
    loadCustomerOptions()
  }
}

// 关闭对话框
const handleClose = () => {
  visible.value = false
  formData.value = defaultFormData()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    submitLoading.value = true
    
    // 清理空的位置信息
    const submitData = { ...formData.value }
    if (!submitData.location?.address && !submitData.location?.latitude && !submitData.location?.longitude) {
      delete submitData.location
    }
    
    if (isEdit.value && props.device) {
      // 更新设备
      await managedDeviceAPI.updateDevice(props.device.id, submitData as UpdateManagedDeviceData)
      ElMessage.success('设备更新成功')
    } else {
      // 创建设备
      await managedDeviceAPI.createDevice(submitData)
      ElMessage.success('设备创建成功')
    }
    
    emit('success')
    handleClose()
  } catch (error: any) {
    console.error('提交失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.form-section {
  margin-bottom: 24px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .section-title {
    margin: 0 0 16px 0;
    padding-bottom: 8px;
    border-bottom: 2px solid #e6f7ff;
    color: #1890ff;
    font-size: 16px;
    font-weight: 600;
    
    .optional {
      font-size: 12px;
      color: #8c8c8c;
      font-weight: 400;
    }
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 16px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #262626;
}

:deep(.el-input__wrapper) {
  border-radius: 6px;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  border-radius: 6px;
}
</style>