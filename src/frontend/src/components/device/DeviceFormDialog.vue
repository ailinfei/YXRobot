<!--
  设备创建/编辑对话框
  支持设备基本信息的创建和编辑
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="isEdit ? '编辑设备' : '添加设备'"
    width="70%"
    :before-close="handleClose"
    class="device-form-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
      class="device-form"
    >
      <!-- 基本信息 -->
      <div class="form-section">
        <h3 class="section-title">基本信息</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="设备序列号" prop="serialNumber">
              <el-input v-model="formData.serialNumber" placeholder="请输入设备序列号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备型号" prop="model">
              <el-select v-model="formData.model" placeholder="选择设备型号" style="width: 100%">
                <el-option label="教育版练字机器人" value="YX-EDU-2024" />
                <el-option label="家庭版练字机器人" value="YX-HOME-2024" />
                <el-option label="专业版练字机器人" value="YX-PRO-2024" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="所属客户" prop="customerId">
              <el-select
                v-model="formData.customerId"
                placeholder="选择客户"
                filterable
                remote
                :remote-method="searchCustomers"
                :loading="customerLoading"
                style="width: 100%"
              >
                <el-option
                  v-for="customer in customerOptions"
                  :key="customer.id"
                  :label="`${customer.name} (${customer.phone || ''})`"
                  :value="customer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="isEdit">
            <el-form-item label="设备状态" prop="status">
              <el-select v-model="formData.status" placeholder="选择设备状态" style="width: 100%">
                <el-option label="在线" value="online" />
                <el-option label="离线" value="offline" />
                <el-option label="故障" value="error" />
                <el-option label="维护中" value="maintenance" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24" v-if="isEdit">
          <el-col :span="12">
            <el-form-item label="固件版本" prop="firmwareVersion">
              <el-input v-model="formData.firmwareVersion" placeholder="固件版本" />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 技术参数 -->
      <div class="form-section">
        <h3 class="section-title">技术参数</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="处理器" prop="specifications.cpu">
              <el-input v-model="formData.specifications.cpu" placeholder="处理器型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="内存" prop="specifications.memory">
              <el-input v-model="formData.specifications.memory" placeholder="内存大小" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="存储" prop="specifications.storage">
              <el-input v-model="formData.specifications.storage" placeholder="存储容量" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示屏" prop="specifications.display">
              <el-input v-model="formData.specifications.display" placeholder="显示屏规格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="电池" prop="specifications.battery">
              <el-input v-model="formData.specifications.battery" placeholder="电池规格" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="连接方式" prop="specifications.connectivity">
              <el-select
                v-model="formData.specifications.connectivity"
                multiple
                placeholder="选择连接方式"
                style="width: 100%"
              >
                <el-option label="WiFi 6" value="WiFi 6" />
                <el-option label="WiFi 5" value="WiFi 5" />
                <el-option label="Bluetooth 5.0" value="Bluetooth 5.0" />
                <el-option label="Bluetooth 4.2" value="Bluetooth 4.2" />
                <el-option label="4G LTE" value="4G LTE" />
                <el-option label="5G" value="5G" />
                <el-option label="USB-C" value="USB-C" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 设备配置 -->
      <div class="form-section">
        <h3 class="section-title">设备配置</h3>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="系统语言" prop="configuration.language">
              <el-select v-model="formData.configuration.language" placeholder="选择语言" style="width: 100%">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
                <el-option label="日本語" value="ja-JP" />
                <el-option label="한국어" value="ko-KR" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="时区" prop="configuration.timezone">
              <el-select v-model="formData.configuration.timezone" placeholder="选择时区" style="width: 100%">
                <el-option label="Asia/Shanghai" value="Asia/Shanghai" />
                <el-option label="Asia/Tokyo" value="Asia/Tokyo" />
                <el-option label="Asia/Seoul" value="Asia/Seoul" />
                <el-option label="America/New_York" value="America/New_York" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="自动更新">
              <el-switch v-model="formData.configuration.autoUpdate" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调试模式">
              <el-switch v-model="formData.configuration.debugMode" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="屏幕亮度">
              <el-slider
                v-model="formData.configuration.customSettings.brightness"
                :min="0"
                :max="100"
                show-input
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="音量">
              <el-slider
                v-model="formData.configuration.customSettings.volume"
                :min="0"
                :max="100"
                show-input
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="休眠时间(分钟)">
              <el-input-number
                v-model="formData.configuration.customSettings.sleepTimeout"
                :min="1"
                :max="120"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <!-- 位置信息 -->
      <div class="form-section">
        <h3 class="section-title">
          位置信息
          <el-switch v-model="enableLocation" style="margin-left: 16px;" />
        </h3>
        <div v-if="enableLocation">
          <el-row :gutter="24">
            <el-col :span="8">
              <el-form-item label="纬度" prop="location.latitude">
                <el-input-number
                  v-model="formData.location.latitude"
                  :precision="6"
                  :step="0.000001"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="经度" prop="location.longitude">
                <el-input-number
                  v-model="formData.location.longitude"
                  :precision="6"
                  :step="0.000001"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-button @click="getCurrentLocation" :loading="locationLoading">
                获取当前位置
              </el-button>
            </el-col>
          </el-row>
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="地址" prop="location.address">
                <el-input v-model="formData.location.address" placeholder="详细地址" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="form-section">
        <h3 class="section-title">备注信息</h3>
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="设备备注" prop="notes">
              <el-input
                v-model="formData.notes"
                type="textarea"
                :rows="3"
                placeholder="设备备注信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新设备' : '添加设备' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import type { Device, CreateDeviceData } from '@/types/device'
import { mockDeviceAPI } from '@/api/mock/device'

// Props
interface Props {
  modelValue: boolean
  device?: Device | null
}

const props = withDefaults(defineProps<Props>(), {
  device: null
})

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  success: []
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const formRef = ref()
const submitLoading = ref(false)
const customerLoading = ref(false)
const locationLoading = ref(false)
const enableLocation = ref(false)

// 表单数据
const defaultFormData = (): CreateDeviceData & {
  status?: Device['status']
  firmwareVersion?: string
} => ({
  serialNumber: '',
  model: 'YX-HOME-2024',
  customerId: '',
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
    customSettings: {
      brightness: 80,
      volume: 50,
      sleepTimeout: 10
    }
  },
  location: {
    latitude: 0,
    longitude: 0,
    address: '',
    lastUpdated: new Date().toISOString()
  },
  notes: '',
  status: 'offline',
  firmwareVersion: '1.0.0'
})

const formData = ref(defaultFormData())

// 计算属性
const isEdit = computed(() => !!props.device)

// 客户选项
const customerOptions = ref<any[]>([])

// 表单验证规则
const formRules = {
  serialNumber: [{ required: true, message: '请输入设备序列号', trigger: 'blur' }],
  model: [{ required: true, message: '请选择设备型号', trigger: 'change' }],
  customerId: [{ required: true, message: '请选择所属客户', trigger: 'change' }],
  'specifications.cpu': [{ required: true, message: '请输入处理器型号', trigger: 'blur' }],
  'specifications.memory': [{ required: true, message: '请输入内存大小', trigger: 'blur' }],
  'specifications.storage': [{ required: true, message: '请输入存储容量', trigger: 'blur' }],
  'specifications.display': [{ required: true, message: '请输入显示屏规格', trigger: 'blur' }],
  'specifications.battery': [{ required: true, message: '请输入电池规格', trigger: 'blur' }],
  'configuration.language': [{ required: true, message: '请选择系统语言', trigger: 'change' }],
  'configuration.timezone': [{ required: true, message: '请选择时区', trigger: 'change' }]
}

// 监听器
watch(() => props.modelValue, (visible) => {
  if (visible) {
    initForm()
    loadCustomerOptions()
  }
})

watch(() => props.device, (device) => {
  if (device && props.modelValue) {
    initForm()
  }
})

watch(enableLocation, (enabled) => {
  if (!enabled) {
    formData.value.location = undefined
  } else if (!formData.value.location) {
    formData.value.location = {
      latitude: 0,
      longitude: 0,
      address: '',
      lastUpdated: new Date().toISOString()
    }
  }
})

// 方法
const initForm = () => {
  if (props.device) {
    // 编辑模式
    formData.value = {
      ...props.device,
      location: props.device.location || {
        latitude: 0,
        longitude: 0,
        address: '',
        lastUpdated: new Date().toISOString()
      }
    }
    enableLocation.value = !!props.device.location
  } else {
    // 创建模式
    formData.value = defaultFormData()
    enableLocation.value = false
  }
  
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

const loadCustomerOptions = async () => {
  customerLoading.value = true
  try {
    const response = await mockDeviceAPI.getCustomerOptions()
    customerOptions.value = response.data
  } catch (error) {
    console.error('加载客户选项失败:', error)
  } finally {
    customerLoading.value = false
  }
}

const searchCustomers = async (query: string) => {
  if (!query) return
  // 这里可以实现客户搜索逻辑
  // 暂时使用现有的客户选项
}

const getCurrentLocation = () => {
  locationLoading.value = true
  
  if ('geolocation' in navigator) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        if (formData.value.location) {
          formData.value.location.latitude = position.coords.latitude
          formData.value.location.longitude = position.coords.longitude
          formData.value.location.lastUpdated = new Date().toISOString()
        }
        ElMessage.success('位置获取成功')
        locationLoading.value = false
      },
      (error) => {
        console.error('获取位置失败:', error)
        ElMessage.error('获取位置失败，请手动输入')
        locationLoading.value = false
      }
    )
  } else {
    ElMessage.error('浏览器不支持地理位置获取')
    locationLoading.value = false
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    
    submitLoading.value = true
    
    const submitData = { ...formData.value }
    if (!enableLocation.value) {
      delete submitData.location
    }
    
    if (isEdit.value) {
      // 更新设备
      await mockDeviceAPI.updateDevice(props.device!.id, submitData)
      ElMessage.success('设备更新成功')
    } else {
      // 创建设备
      await mockDeviceAPI.createDevice(submitData)
      ElMessage.success('设备添加成功')
    }
    
    emit('success')
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
}
</script>

<style lang="scss" scoped>
.device-form-dialog {
  .device-form {
    max-height: 70vh;
    overflow-y: auto;
    padding-right: 10px;
  }

  .form-section {
    margin-bottom: 32px;
    padding: 20px;
    background: #fafafa;
    border-radius: 8px;

    .section-title {
      margin: 0 0 20px 0;
      font-size: 16px;
      font-weight: 600;
      color: #262626;
      display: flex;
      align-items: center;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .device-form-dialog {
    .device-form {
      .form-section {
        padding: 16px;
      }
    }
  }
}
</style>