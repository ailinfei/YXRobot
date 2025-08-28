<template>
  <div class="customer-device-list">
    <div class="device-header">
      <div class="header-left">
        <h4>设备管理</h4>
        <div class="connection-status">
          <el-icon :class="wsConnected ? 'connected' : 'disconnected'">
            <CircleCheckFilled />
          </el-icon>
          <span class="status-text">
            {{ wsConnected ? '实时监控中' : '离线模式' }}
          </span>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="handleAddDevice">
          <el-icon><Plus /></el-icon>
          添加设备
        </el-button>
        <el-button size="small" @click="refreshDevices">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 设备统计 -->
    <div class="device-stats">
      <div class="stat-item">
        <div class="stat-value">{{ deviceStats.total }}</div>
        <div class="stat-label">设备总数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ deviceStats.purchased }}</div>
        <div class="stat-label">购买设备</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ deviceStats.rental }}</div>
        <div class="stat-label">租赁设备</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ deviceStats.active }}</div>
        <div class="stat-label">在线设备</div>
      </div>
    </div>

    <!-- 设备列表 -->
    <div class="device-table">
      <el-table :data="deviceList" v-loading="loading" stripe>
        <el-table-column prop="serialNumber" label="设备序列号" width="150" />
        <el-table-column prop="model" label="设备型号" width="120" />
        <el-table-column prop="type" label="设备类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'purchased' ? 'success' : 'warning'" size="small">
              {{ row.type === 'purchased' ? '购买' : '租赁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="设备状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getDeviceStatusTagType(row.status)" size="small">
              {{ getDeviceStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="activatedAt" label="激活时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.activatedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastOnlineAt" label="最后在线" width="150">
          <template #default="{ row }">
            <div class="online-status">
              <el-icon 
                :class="getOnlineStatusClass(row.lastOnlineAt)"
                class="status-icon"
              >
                <CircleCheckFilled />
              </el-icon>
              {{ formatRelativeTime(row.lastOnlineAt) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="100" />
        <el-table-column prop="healthScore" label="健康度" width="100">
          <template #default="{ row }">
            <div class="health-score">
              <el-progress
                :percentage="row.healthScore"
                :color="getHealthColor(row.healthScore)"
                :stroke-width="6"
                :show-text="false"
              />
              <span class="score-text">{{ row.healthScore }}%</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="使用统计" width="150">
          <template #default="{ row }">
            <div class="usage-stats">
              <div class="stat-line">
                <span class="stat-label">使用时长:</span>
                <span class="stat-value">{{ row.usageStats?.totalUsageHours || 0 }}h</span>
              </div>
              <div class="stat-line">
                <span class="stat-label">完成课程:</span>
                <span class="stat-value">{{ row.usageStats?.coursesCompleted || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewDeviceDetail(row)">
              详情
            </el-button>
            <el-button text type="primary" size="small" @click="editDevice(row)">
              编辑
            </el-button>
            <el-button text type="danger" size="small" @click="removeDevice(row)">
              移除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加设备对话框 -->
    <el-dialog
      v-model="addDeviceDialogVisible"
      title="添加设备"
      width="600px"
    >
      <el-form
        ref="deviceFormRef"
        :model="deviceForm"
        :rules="deviceFormRules"
        label-width="100px"
      >
        <el-form-item label="设备序列号" prop="serialNumber">
          <el-input
            v-model="deviceForm.serialNumber"
            placeholder="请输入设备序列号"
            maxlength="50"
          />
        </el-form-item>
        
        <el-form-item label="设备型号" prop="model">
          <el-select v-model="deviceForm.model" placeholder="请选择设备型号">
            <el-option label="YX-Robot-V1" value="YX-Robot-V1" />
            <el-option label="YX-Robot-V2" value="YX-Robot-V2" />
            <el-option label="YX-Robot-Pro" value="YX-Robot-Pro" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="设备类型" prop="type">
          <el-radio-group v-model="deviceForm.type">
            <el-radio value="purchased">购买</el-radio>
            <el-radio value="rental">租赁</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="设备状态" prop="status">
          <el-select v-model="deviceForm.status" placeholder="请选择设备状态">
            <el-option label="待激活" value="pending" />
            <el-option label="使用中" value="active" />
            <el-option label="离线" value="offline" />
            <el-option label="维护中" value="maintenance" />
            <el-option label="已退役" value="retired" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="激活时间" prop="activatedAt" v-if="deviceForm.status !== 'pending'">
          <el-date-picker
            v-model="deviceForm.activatedAt"
            type="datetime"
            placeholder="选择激活时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        
        <el-form-item label="备注" prop="notes">
          <el-input
            v-model="deviceForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="addDeviceDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAddDeviceSubmit" :loading="addDeviceLoading">
            添加
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 设备详情对话框 -->
    <DeviceDetailDialog
      v-model="deviceDetailDialogVisible"
      :device="selectedDevice"
      @edit="handleEditDevice"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  Plus,
  Refresh,
  CircleCheckFilled
} from '@element-plus/icons-vue'
import { mockCustomerAPI } from '@/api/mock/customer'
import type { CustomerDevice } from '@/types/customer'
import DeviceDetailDialog from './DeviceDetailDialog.vue'

// Props
interface Props {
  customerId: string
  devices: CustomerDevice[]
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'refresh': []
}>()

// 响应式数据
const loading = ref(false)
const deviceList = ref<CustomerDevice[]>([])
const addDeviceDialogVisible = ref(false)
const addDeviceLoading = ref(false)
const deviceFormRef = ref<FormInstance>()

// WebSocket连接
let websocket: WebSocket | null = null
const wsConnected = ref(false)

// 设备表单
const deviceForm = reactive({
  serialNumber: '',
  model: '',
  type: 'purchased',
  status: 'pending',
  activatedAt: '',
  notes: ''
})

// 表单验证规则
const deviceFormRules: FormRules = {
  serialNumber: [
    { required: true, message: '请输入设备序列号', trigger: 'blur' },
    { min: 5, max: 50, message: '设备序列号长度在5-50个字符', trigger: 'blur' }
  ],
  model: [
    { required: true, message: '请选择设备型号', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择设备类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择设备状态', trigger: 'change' }
  ]
}

// 计算属性
const deviceStats = computed(() => {
  const stats = {
    total: deviceList.value.length,
    purchased: 0,
    rental: 0,
    active: 0
  }
  
  deviceList.value.forEach(device => {
    if (device.type === 'purchased') stats.purchased++
    if (device.type === 'rental') stats.rental++
    if (device.status === 'active') stats.active++
  })
  
  return stats
})

// 方法
const loadDevices = async () => {
  loading.value = true
  try {
    const response = await mockCustomerAPI.getCustomerDevices(props.customerId)
    deviceList.value = response.data
  } catch (error) {
    console.error('加载设备列表失败:', error)
    ElMessage.error('加载设备列表失败')
  } finally {
    loading.value = false
  }
}

const refreshDevices = () => {
  loadDevices()
  emit('refresh')
}

const handleAddDevice = () => {
  // 重置表单
  Object.assign(deviceForm, {
    serialNumber: '',
    model: '',
    type: 'purchased',
    status: 'pending',
    activatedAt: '',
    notes: ''
  })
  addDeviceDialogVisible.value = true
}

const handleAddDeviceSubmit = async () => {
  if (!deviceFormRef.value) return
  
  try {
    await deviceFormRef.value.validate()
    
    addDeviceLoading.value = true
    
    const deviceData = {
      customerId: props.customerId,
      serialNumber: deviceForm.serialNumber,
      model: deviceForm.model,
      type: deviceForm.type,
      status: deviceForm.status,
      activatedAt: deviceForm.activatedAt || undefined,
      notes: deviceForm.notes
    }
    
    await mockCustomerAPI.addCustomerDevice(deviceData)
    
    ElMessage.success('设备添加成功')
    addDeviceDialogVisible.value = false
    refreshDevices()
    
  } catch (error) {
    console.error('添加设备失败:', error)
    ElMessage.error('添加设备失败')
  } finally {
    addDeviceLoading.value = false
  }
}

const deviceDetailDialogVisible = ref(false)
const selectedDevice = ref<CustomerDevice | null>(null)

const viewDeviceDetail = (device: CustomerDevice) => {
  selectedDevice.value = device
  deviceDetailDialogVisible.value = true
}

const editDevice = (device: CustomerDevice) => {
  ElMessage.info('编辑设备功能开发中...')
}

const handleEditDevice = (device: CustomerDevice) => {
  // 从设备详情对话框触发的编辑操作
  editDevice(device)
}

const removeDevice = async (device: CustomerDevice) => {
  try {
    await ElMessageBox.confirm(
      `确定要移除设备 "${device.serialNumber}" 吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await mockCustomerAPI.removeCustomerDevice(props.customerId, device.id)
    ElMessage.success('设备移除成功')
    refreshDevices()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('移除设备失败:', error)
      ElMessage.error('移除设备失败')
    }
  }
}

// 工具方法
const getDeviceStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    active: 'success',
    offline: 'warning',
    maintenance: 'primary',
    retired: 'danger'
  }
  return types[status] || 'info'
}

const getDeviceStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待激活',
    active: '使用中',
    offline: '离线',
    maintenance: '维护中',
    retired: '已退役'
  }
  return texts[status] || status
}

const getOnlineStatusClass = (lastOnlineAt?: string) => {
  if (!lastOnlineAt) return 'offline'
  
  const now = new Date()
  const lastOnline = new Date(lastOnlineAt)
  const diffMinutes = (now.getTime() - lastOnline.getTime()) / (1000 * 60)
  
  if (diffMinutes < 5) return 'online'
  if (diffMinutes < 60) return 'warning'
  return 'offline'
}

const getHealthColor = (score: number) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatRelativeTime = (dateStr?: string) => {
  if (!dateStr) return '从未在线'
  
  const now = new Date()
  const date = new Date(dateStr)
  const diffMs = now.getTime() - date.getTime()
  const diffMinutes = Math.floor(diffMs / (1000 * 60))
  
  if (diffMinutes < 1) return '刚刚在线'
  if (diffMinutes < 5) return '在线'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  
  const diffHours = Math.floor(diffMinutes / 60)
  if (diffHours < 24) return `${diffHours}小时前`
  
  const diffDays = Math.floor(diffHours / 24)
  return `${diffDays}天前`
}

// WebSocket连接管理
const initWebSocket = () => {
  try {
    // 模拟WebSocket连接 - 在实际环境中应该连接到真实的WebSocket服务器
    const wsUrl = `ws://localhost:8080/ws/devices/${props.customerId}`
    websocket = new WebSocket(wsUrl)
    
    websocket.onopen = () => {
      console.log('设备状态WebSocket连接已建立')
      wsConnected.value = true
    }
    
    websocket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data)
        handleDeviceStatusUpdate(data)
      } catch (error) {
        console.error('解析WebSocket消息失败:', error)
      }
    }
    
    websocket.onclose = () => {
      console.log('设备状态WebSocket连接已关闭')
      wsConnected.value = false
      // 5秒后尝试重连
      setTimeout(() => {
        if (!websocket || websocket.readyState === WebSocket.CLOSED) {
          initWebSocket()
        }
      }, 5000)
    }
    
    websocket.onerror = (error) => {
      console.error('WebSocket连接错误:', error)
      wsConnected.value = false
    }
  } catch (error) {
    console.error('初始化WebSocket失败:', error)
    // 如果WebSocket连接失败，使用轮询方式更新设备状态
    startPollingDeviceStatus()
  }
}

// 处理设备状态更新
const handleDeviceStatusUpdate = (data: any) => {
  const { deviceId, status, lastOnlineAt, healthScore, usageStats } = data
  
  const deviceIndex = deviceList.value.findIndex(device => device.id === deviceId)
  if (deviceIndex !== -1) {
    // 更新设备状态
    deviceList.value[deviceIndex] = {
      ...deviceList.value[deviceIndex],
      status,
      lastOnlineAt,
      healthScore: healthScore || deviceList.value[deviceIndex].healthScore,
      usageStats: usageStats || deviceList.value[deviceIndex].usageStats
    }
    
    console.log(`设备 ${deviceId} 状态已更新:`, status)
  }
}

// 轮询设备状态（WebSocket连接失败时的备用方案）
let pollingTimer: NodeJS.Timeout | null = null

const startPollingDeviceStatus = () => {
  if (pollingTimer) return
  
  pollingTimer = setInterval(async () => {
    try {
      // 模拟设备状态更新
      deviceList.value.forEach((device, index) => {
        // 随机更新设备在线状态
        if (Math.random() > 0.8) {
          const now = new Date().toISOString()
          deviceList.value[index] = {
            ...device,
            lastOnlineAt: now,
            healthScore: Math.max(60, Math.min(100, device.healthScore + (Math.random() - 0.5) * 10))
          }
        }
      })
    } catch (error) {
      console.error('轮询设备状态失败:', error)
    }
  }, 30000) // 每30秒更新一次
}

const stopPollingDeviceStatus = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

// 关闭WebSocket连接
const closeWebSocket = () => {
  if (websocket) {
    websocket.close()
    websocket = null
  }
  wsConnected.value = false
  stopPollingDeviceStatus()
}

onMounted(() => {
  deviceList.value = props.devices
  // 初始化WebSocket连接
  initWebSocket()
})

onUnmounted(() => {
  closeWebSocket()
})
</script>

<style lang="scss" scoped>
.customer-device-list {
  .device-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      h4 {
        margin: 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
      }
      
      .connection-status {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 4px 8px;
        border-radius: 4px;
        background: #F5F7FA;
        
        .el-icon {
          font-size: 8px;
          
          &.connected {
            color: #67C23A;
          }
          
          &.disconnected {
            color: #F56C6C;
          }
        }
        
        .status-text {
          font-size: 12px;
          color: #606266;
          font-weight: 500;
        }
      }
    }
    
    .header-actions {
      display: flex;
      gap: 8px;
    }
  }
  
  .device-stats {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    margin-bottom: 20px;
    
    .stat-item {
      text-align: center;
      padding: 16px;
      background: #F5F7FA;
      border-radius: 6px;
      
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #303133;
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 12px;
        color: #606266;
      }
    }
  }
  
  .device-table {
    .online-status {
      display: flex;
      align-items: center;
      gap: 6px;
      
      .status-icon {
        font-size: 8px;
        
        &.online {
          color: #67C23A;
        }
        
        &.warning {
          color: #E6A23C;
        }
        
        &.offline {
          color: #F56C6C;
        }
      }
    }
    
    .health-score {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .el-progress {
        flex: 1;
      }
      
      .score-text {
        font-size: 12px;
        color: #606266;
        min-width: 35px;
      }
    }
    
    .usage-stats {
      .stat-line {
        display: flex;
        justify-content: space-between;
        font-size: 12px;
        margin-bottom: 2px;
        
        .stat-label {
          color: #909399;
        }
        
        .stat-value {
          color: #303133;
          font-weight: 500;
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

@media (max-width: 768px) {
  .customer-device-list {
    .device-stats {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }
    
    .device-header {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;
      
      .header-actions {
        justify-content: center;
      }
    }
  }
}
</style>