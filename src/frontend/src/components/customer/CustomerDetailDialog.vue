<template>
  <el-dialog
    v-model="visible"
    :title="`客户详情 - ${customer?.name || ''}`"
    width="90%"
    :before-close="handleClose"
    class="customer-detail-dialog"
  >
    <div class="customer-detail-container" v-if="customer">
      <!-- 客户基本信息 -->
      <div class="customer-header">
        <div class="customer-avatar">
          <img 
            :src="customer.avatar || defaultAvatar" 
            :alt="customer.name"
            @error="handleAvatarError"
          />
        </div>
        <div class="customer-info">
          <div class="customer-name">
            {{ customer.name }}
            <el-tag :type="getLevelType(customer.level)" size="large">
              {{ getLevelText(customer.level) }}
            </el-tag>
          </div>
          <div class="customer-meta">
            <div class="meta-item">
              <el-icon><Phone /></el-icon>
              <span>{{ customer.phone }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Message /></el-icon>
              <span>{{ customer.email }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Location /></el-icon>
              <span>{{ getFullAddress(customer.address) }}</span>
            </div>
            <div class="meta-item">
              <el-icon><Calendar /></el-icon>
              <span>注册于 {{ formatDate(customer.registeredAt) }}</span>
            </div>
          </div>
          <div class="customer-tags" v-if="customer.tags.length > 0">
            <el-tag 
              v-for="tag in customer.tags" 
              :key="tag"
              size="small"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
        <div class="customer-stats">
          <div class="stat-item">
            <div class="stat-value">{{ customer.deviceCount.total }}</div>
            <div class="stat-label">设备总数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">¥{{ customer.totalSpent.toLocaleString() }}</div>
            <div class="stat-label">总消费</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ customer.customerValue }}</div>
            <div class="stat-label">客户价值</div>
          </div>
        </div>
      </div>

      <!-- 详情标签页 -->
      <el-tabs v-model="activeTab" class="detail-tabs">
        <!-- 设备管理 -->
        <el-tab-pane label="设备管理" name="devices">
          <div class="devices-section">
            <div class="section-header">
              <h4>设备列表 ({{ customer.devices.length }}台)</h4>
              <div class="device-summary">
                <el-tag type="success">购买: {{ customer.deviceCount.purchased }}台</el-tag>
                <el-tag type="warning">租赁: {{ customer.deviceCount.rental }}台</el-tag>
                <el-tag :type="getOnlineDevicesType()">
                  在线: {{ getOnlineDevicesCount() }}台
                </el-tag>
              </div>
            </div>
            
            <!-- 设备状态监控 -->
            <DeviceStatusMonitor
              :devices="customer.devices"
              :customer-id="customer.id"
              @device-action="handleDeviceAction"
              @refresh="handleDeviceRefresh"
            />
          </div>
        </el-tab-pane>

        <!-- 订单历史 -->
        <el-tab-pane label="订单历史" name="orders">
          <div class="orders-section">
            <div class="section-header">
              <h4>订单历史 ({{ customer.orders.length }}笔)</h4>
            </div>
            
            <div class="orders-list">
              <div 
                v-for="order in customer.orders" 
                :key="order.id"
                class="order-card"
              >
                <div class="order-header">
                  <div class="order-info">
                    <h5>{{ order.orderNumber }}</h5>
                    <div class="order-meta">
                      <el-tag :type="order.type === 'purchase' ? 'success' : 'warning'" size="small">
                        {{ order.type === 'purchase' ? '购买订单' : '租赁订单' }}
                      </el-tag>
                      <el-tag :type="getOrderStatusType(order.status)" size="small">
                        {{ getOrderStatusText(order.status) }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="order-amount">
                    <div class="amount">¥{{ order.totalAmount.toLocaleString() }}</div>
                    <div class="date">{{ formatDate(order.createdAt) }}</div>
                  </div>
                </div>
                
                <div class="order-items">
                  <div 
                    v-for="item in order.items" 
                    :key="item.id"
                    class="order-item"
                  >
                    <span class="item-name">{{ item.productName }}</span>
                    <span class="item-model">{{ item.productModel }}</span>
                    <span class="item-quantity">x{{ item.quantity }}</span>
                    <span class="item-price">¥{{ item.totalPrice.toLocaleString() }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 服务记录 -->
        <el-tab-pane label="服务记录" name="service">
          <div class="service-section">
            <div class="section-header">
              <h4>服务记录 ({{ customer.serviceRecords.length }}条)</h4>
              <div class="service-actions">
                <el-button type="primary" size="small" @click="handleCreateService">
                  添加服务记录
                </el-button>
                <el-button size="small" @click="handleExportServiceRecords">
                  导出记录
                </el-button>
              </div>
            </div>
            
            <!-- 服务记录统计 -->
            <div class="service-stats">
              <div class="stat-item">
                <div class="stat-value">{{ getServiceStatsByStatus('pending') }}</div>
                <div class="stat-label">待处理</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ getServiceStatsByStatus('processing') }}</div>
                <div class="stat-label">处理中</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ getServiceStatsByStatus('completed') }}</div>
                <div class="stat-label">已完成</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ getServiceStatsByPriority('urgent') }}</div>
                <div class="stat-label">紧急</div>
              </div>
            </div>
            
            <div class="service-timeline">
              <div 
                v-for="record in customer.serviceRecords" 
                :key="record.id"
                class="service-item"
              >
                <div class="service-icon">
                  <el-icon><component :is="getServiceIcon(record.type)" /></el-icon>
                </div>
                <div class="service-content">
                  <div class="service-header">
                    <h5>{{ record.title }}</h5>
                    <div class="service-meta">
                      <el-tag :type="getServiceTypeColor(record.type)" size="small">
                        {{ getServiceTypeText(record.type) }}
                      </el-tag>
                      <el-tag :type="getPriorityColor(record.priority)" size="small">
                        {{ getPriorityText(record.priority) }}
                      </el-tag>
                      <el-tag :type="getServiceStatusColor(record.status)" size="small">
                        {{ getServiceStatusText(record.status) }}
                      </el-tag>
                    </div>
                    <div class="service-operations">
                      <el-button size="small" @click="handleEditService(record)">
                        编辑
                      </el-button>
                      <el-dropdown @command="(command) => handleServiceAction(command, record)">
                        <el-button size="small" :icon="MoreFilled" />
                        <template #dropdown>
                          <el-dropdown-menu>
                            <el-dropdown-item command="complete" v-if="record.status !== 'completed'">
                              完成服务
                            </el-dropdown-item>
                            <el-dropdown-item command="cancel" v-if="record.status === 'pending'">
                              取消服务
                            </el-dropdown-item>
                            <el-dropdown-item command="duplicate">
                              复制记录
                            </el-dropdown-item>
                            <el-dropdown-item command="delete" divided>
                              删除记录
                            </el-dropdown-item>
                          </el-dropdown-menu>
                        </template>
                      </el-dropdown>
                    </div>
                  </div>
                  <div class="service-description">{{ record.description }}</div>
                  <div class="service-footer">
                    <span class="assignee">负责人: {{ record.assignedTo || '未分配' }}</span>
                    <span class="date">{{ formatDate(record.createdAt) }}</span>
                    <span v-if="record.resolvedAt" class="resolved-date">
                      完成于: {{ formatDate(record.resolvedAt) }}
                    </span>
                  </div>
                </div>
              </div>
              
              <!-- 空状态 -->
              <div v-if="customer.serviceRecords.length === 0" class="service-empty">
                <el-empty description="暂无服务记录">
                  <el-button type="primary" @click="handleCreateService">
                    添加第一条服务记录
                  </el-button>
                </el-empty>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 客户备注 -->
        <el-tab-pane label="客户备注" name="notes">
          <div class="notes-section">
            <el-input
              v-model="customerNotes"
              type="textarea"
              :rows="8"
              placeholder="添加客户备注信息..."
              maxlength="1000"
              show-word-limit
            />
            <div class="notes-actions">
              <el-button @click="resetNotes">重置</el-button>
              <el-button type="primary" @click="saveNotes" :loading="savingNotes">
                保存备注
              </el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          编辑客户
        </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 服务记录对话框 -->
  <ServiceRecordDialog
    v-model="serviceRecordVisible"
    :service-record="selectedServiceRecord"
    :customer-devices="customer?.devices || []"
    :mode="serviceRecordMode"
    @success="handleServiceRecordSuccess"
  />
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Phone, 
  Message, 
  Location, 
  Calendar,
  Tools,
  ChatDotRound,
  QuestionFilled,
  WarningFilled,
  Setting,
  MoreFilled
} from '@element-plus/icons-vue'
import DeviceStatusMonitor from './DeviceStatusMonitor.vue'
import ServiceRecordDialog from './ServiceRecordDialog.vue'
import type { Customer, CustomerDevice, ServiceRecord } from '@/types/customer'
import { formatDate } from '@/utils/dateTime'

interface Props {
  modelValue: boolean
  customer: Customer | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'refresh'): void
  (e: 'edit', customer: Customer): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const activeTab = ref('devices')
const customerNotes = ref('')
const savingNotes = ref(false)

// 默认头像
const defaultAvatar = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI2Y1ZjVmNSIvPjx0ZXh0IHg9IjUwIiB5PSI1NSIgZm9udC1zaXplPSIyMCIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZmlsbD0iIzk5OSI+VVNFUjwvdGV4dD48L3N2Zz4='

// 计算属性
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听客户数据变化
watch(
  () => props.customer,
  (newCustomer) => {
    if (newCustomer) {
      customerNotes.value = newCustomer.notes || ''
      activeTab.value = 'devices'
    }
  },
  { immediate: true }
)

// 方法
const handleClose = () => {
  visible.value = false
}

const handleEdit = () => {
  if (props.customer) {
    emit('edit', props.customer)
  }
}

const handleAvatarError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = defaultAvatar
}

const getLevelType = (level: string): string => {
  const typeMap = {
    regular: 'info',
    vip: 'warning',
    premium: 'danger'
  }
  return typeMap[level as keyof typeof typeMap] || 'info'
}

const getLevelText = (level: string): string => {
  const textMap = {
    regular: '普通客户',
    vip: 'VIP客户',
    premium: '高级客户'
  }
  return textMap[level as keyof typeof textMap] || level
}

const getFullAddress = (address: any): string => {
  return `${address.province} ${address.city} ${address.district}`
}



const getOrderStatusType = (status: string): string => {
  const typeMap = {
    completed: 'success',
    processing: 'warning',
    pending: 'info',
    cancelled: 'danger'
  }
  return typeMap[status as keyof typeof typeMap] || 'info'
}

const getOrderStatusText = (status: string): string => {
  const textMap = {
    completed: '已完成',
    processing: '处理中',
    pending: '待处理',
    cancelled: '已取消'
  }
  return textMap[status as keyof typeof textMap] || status
}

const getServiceIcon = (type: string) => {
  const iconMap = {
    maintenance: Tools,
    consultation: ChatDotRound,
    complaint: WarningFilled,
    training: QuestionFilled,
    upgrade: Setting
  }
  return iconMap[type as keyof typeof iconMap] || Tools
}

const getServiceTypeColor = (type: string): string => {
  const colorMap = {
    maintenance: 'warning',
    consultation: 'info',
    complaint: 'danger',
    training: 'success',
    upgrade: 'primary'
  }
  return colorMap[type as keyof typeof colorMap] || 'info'
}

const getServiceTypeText = (type: string): string => {
  const textMap = {
    maintenance: '维护',
    consultation: '咨询',
    complaint: '投诉',
    training: '培训',
    upgrade: '升级'
  }
  return textMap[type as keyof typeof textMap] || type
}

const getPriorityColor = (priority: string): string => {
  const colorMap = {
    low: 'info',
    medium: 'warning',
    high: 'danger',
    urgent: 'danger'
  }
  return colorMap[priority as keyof typeof colorMap] || 'info'
}

const getPriorityText = (priority: string): string => {
  const textMap = {
    low: '低',
    medium: '中',
    high: '高',
    urgent: '紧急'
  }
  return textMap[priority as keyof typeof textMap] || priority
}

const getServiceStatusColor = (status: string): string => {
  const colorMap = {
    pending: 'info',
    processing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return colorMap[status as keyof typeof colorMap] || 'info'
}

const getServiceStatusText = (status: string): string => {
  const textMap = {
    pending: '待处理',
    processing: '处理中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status as keyof typeof textMap] || status
}

const handleDeviceDetail = (device: CustomerDevice) => {
  ElMessage.info(`查看设备详情: ${device.serialNumber}`)
}

const handleRemoteControl = (device: CustomerDevice) => {
  ElMessage.info(`远程控制设备: ${device.serialNumber}`)
}

// 服务记录相关数据
const serviceRecordVisible = ref(false)
const selectedServiceRecord = ref<ServiceRecord | null>(null)
const serviceRecordMode = ref<'create' | 'edit'>('create')

// 服务记录统计方法
const getServiceStatsByStatus = (status: string): number => {
  if (!props.customer) return 0
  return props.customer.serviceRecords.filter(record => record.status === status).length
}

const getServiceStatsByPriority = (priority: string): number => {
  if (!props.customer) return 0
  return props.customer.serviceRecords.filter(record => record.priority === priority).length
}

// 服务记录操作方法
const handleCreateService = () => {
  selectedServiceRecord.value = null
  serviceRecordMode.value = 'create'
  serviceRecordVisible.value = true
}

const handleEditService = (record: ServiceRecord) => {
  selectedServiceRecord.value = record
  serviceRecordMode.value = 'edit'
  serviceRecordVisible.value = true
}

const handleServiceAction = async (command: string, record: ServiceRecord) => {
  try {
    let confirmMessage = ''
    let successMessage = ''
    
    switch (command) {
      case 'complete':
        confirmMessage = `确定要完成服务记录"${record.title}"吗？`
        successMessage = '服务记录已标记为完成'
        break
      case 'cancel':
        confirmMessage = `确定要取消服务记录"${record.title}"吗？`
        successMessage = '服务记录已取消'
        break
      case 'duplicate':
        ElMessage.success('服务记录已复制')
        return
      case 'delete':
        confirmMessage = `确定要删除服务记录"${record.title}"吗？此操作不可恢复。`
        successMessage = '服务记录已删除'
        break
      default:
        return
    }
    
    if (confirmMessage) {
      await ElMessageBox.confirm(confirmMessage, '操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: command === 'delete' ? 'error' : 'warning'
      })
    }
    
    // 模拟API操作
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success(successMessage)
    emit('refresh')
    
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败，请稍后重试')
    }
  }
}

const handleExportServiceRecords = async () => {
  try {
    // 模拟导出过程
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('服务记录导出成功')
  } catch (error) {
    ElMessage.error('导出失败，请稍后重试')
  }
}

const handleServiceRecordSuccess = () => {
  serviceRecordVisible.value = false
  ElMessage.success(serviceRecordMode.value === 'create' ? '服务记录创建成功' : '服务记录更新成功')
  emit('refresh')
}

const resetNotes = () => {
  customerNotes.value = props.customer?.notes || ''
}

// 设备相关方法
const getOnlineDevicesCount = (): number => {
  if (!props.customer) return 0
  return props.customer.devices.filter(device => device.status === 'active').length
}

const getOnlineDevicesType = (): string => {
  const onlineCount = getOnlineDevicesCount()
  const totalCount = props.customer?.devices.length || 0
  
  if (onlineCount === totalCount) return 'success'
  if (onlineCount > totalCount / 2) return 'warning'
  return 'danger'
}

const handleDeviceAction = (action: string, device: CustomerDevice) => {
  ElMessage.success(`设备操作: ${action} - ${device.serialNumber}`)
  // 这里可以添加具体的设备操作逻辑
  emit('refresh')
}

const handleDeviceRefresh = () => {
  ElMessage.info('设备状态已刷新')
  emit('refresh')
}

const saveNotes = async () => {
  try {
    savingNotes.value = true
    // 模拟保存过程
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('备注保存成功')
    emit('refresh')
  } catch (error) {
    ElMessage.error('备注保存失败')
  } finally {
    savingNotes.value = false
  }
}
</script>

<style lang="scss" scoped>
.customer-detail-dialog {
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.customer-detail-container {
  .customer-header {
    display: flex;
    align-items: flex-start;
    gap: 24px;
    padding: 24px;
    border-bottom: 1px solid var(--border-color);
    background: var(--bg-secondary);
    
    .customer-avatar {
      img {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        object-fit: cover;
        border: 3px solid white;
        box-shadow: var(--shadow-sm);
      }
    }
    
    .customer-info {
      flex: 1;
      
      .customer-name {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 24px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
      }
      
      .customer-meta {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 8px;
        margin-bottom: 16px;
        
        .meta-item {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 14px;
          color: var(--text-secondary);
          
          .el-icon {
            color: var(--primary-color);
          }
        }
      }
      
      .customer-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
      }
    }
    
    .customer-stats {
      display: flex;
      gap: 24px;
      
      .stat-item {
        text-align: center;
        
        .stat-value {
          font-size: 20px;
          font-weight: 700;
          color: var(--primary-color);
          line-height: 1;
        }
        
        .stat-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
  }
  
  .detail-tabs {
    :deep(.el-tabs__header) {
      margin: 0;
      padding: 0 24px;
      background: white;
    }
    
    :deep(.el-tabs__content) {
      padding: 24px;
    }
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      color: var(--text-primary);
    }
    
    .device-summary {
      display: flex;
      gap: 8px;
    }
  }
  
  .devices-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    
    .device-card {
      background: white;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-lg);
      padding: 20px;
      transition: all 0.3s ease;
      
      &:hover {
        box-shadow: var(--shadow-md);
        transform: translateY(-2px);
      }
      
      .device-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .device-info {
          h5 {
            margin: 0 0 4px 0;
            color: var(--text-primary);
          }
          
          .device-serial {
            font-size: 12px;
            color: var(--text-light);
          }
        }
        
        .device-status {
          display: flex;
          flex-direction: column;
          gap: 4px;
          align-items: flex-end;
        }
      }
      
      .device-details {
        margin-bottom: 16px;
        
        .detail-row {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          font-size: 14px;
          
          .label {
            color: var(--text-secondary);
          }
          
          .value {
            color: var(--text-primary);
            display: flex;
            align-items: center;
            gap: 8px;
          }
        }
      }
      
      .device-actions {
        display: flex;
        gap: 8px;
        
        .el-button {
          flex: 1;
        }
      }
    }
  }
  
  .orders-list {
    .order-card {
      background: white;
      border: 1px solid var(--border-color);
      border-radius: var(--radius-lg);
      padding: 20px;
      margin-bottom: 16px;
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .order-info {
          h5 {
            margin: 0 0 8px 0;
            color: var(--text-primary);
          }
          
          .order-meta {
            display: flex;
            gap: 8px;
          }
        }
        
        .order-amount {
          text-align: right;
          
          .amount {
            font-size: 18px;
            font-weight: 600;
            color: var(--primary-color);
          }
          
          .date {
            font-size: 12px;
            color: var(--text-light);
            margin-top: 4px;
          }
        }
      }
      
      .order-items {
        .order-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-top: 1px solid var(--border-color);
          font-size: 14px;
          
          .item-name {
            flex: 1;
            color: var(--text-primary);
          }
          
          .item-model {
            color: var(--text-secondary);
            margin-right: 16px;
          }
          
          .item-quantity {
            color: var(--text-secondary);
            margin-right: 16px;
          }
          
          .item-price {
            color: var(--primary-color);
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .service-section {
    .service-actions {
      display: flex;
      gap: 8px;
    }
    
    .service-stats {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      margin-bottom: 24px;
      
      .stat-item {
        text-align: center;
        padding: 16px;
        background: var(--bg-secondary);
        border-radius: var(--radius-md);
        
        .stat-value {
          font-size: 20px;
          font-weight: 700;
          color: var(--primary-color);
          line-height: 1;
        }
        
        .stat-label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
    }
    
    .service-empty {
      text-align: center;
      padding: 40px 20px;
    }
  }
  
  .service-timeline {
    .service-item {
      display: flex;
      gap: 16px;
      margin-bottom: 24px;
      
      .service-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: var(--primary-light);
        color: var(--primary-color);
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }
      
      .service-content {
        flex: 1;
        background: white;
        border: 1px solid var(--border-color);
        border-radius: var(--radius-lg);
        padding: 16px;
        
        .service-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 12px;
          
          h5 {
            margin: 0;
            color: var(--text-primary);
            flex: 1;
          }
          
          .service-meta {
            display: flex;
            gap: 8px;
            margin-right: 12px;
          }
          
          .service-operations {
            display: flex;
            gap: 4px;
            flex-shrink: 0;
          }
        }
        
        .service-description {
          color: var(--text-secondary);
          margin-bottom: 12px;
          line-height: 1.5;
        }
        
        .service-footer {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          color: var(--text-light);
          
          .resolved-date {
            color: var(--success-color);
            font-weight: 500;
          }
        }
      }
    }
  }
  
  .notes-section {
    .notes-actions {
      margin-top: 16px;
      text-align: right;
      
      .el-button {
        margin-left: 12px;
      }
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

// 响应式适配
@media (max-width: 768px) {
  .customer-detail-container {
    .customer-header {
      flex-direction: column;
      align-items: center;
      text-align: center;
      
      .customer-info .customer-meta {
        grid-template-columns: 1fr;
      }
      
      .customer-stats {
        justify-content: center;
      }
    }
    
    .devices-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>