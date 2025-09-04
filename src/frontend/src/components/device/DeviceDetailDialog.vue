<!--
  设备详情对话框组件
  功能：显示设备完整详细信息
-->
<template>
  <el-dialog
    v-model="visible"
    :title="`设备详情 - ${device?.serialNumber || ''}`"
    width="900px"
    :before-close="handleClose"
  >
    <div v-if="device" class="device-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>设备序列号</label>
            <span>{{ device.serialNumber }}</span>
          </div>
          <div class="info-item">
            <label>设备型号</label>
            <el-tag :type="getModelTagType(device.model)" size="small">
              {{ getModelName(device.model) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>设备状态</label>
            <div class="status-display">
              <el-icon :class="getStatusIconClass(device.status)">
                <CircleCheckFilled v-if="device.status === 'online'" />
                <CircleCloseFilled v-else-if="device.status === 'offline'" />
                <WarningFilled v-else-if="device.status === 'error'" />
                <Tools v-else />
              </el-icon>
              <span>{{ getStatusText(device.status) }}</span>
            </div>
          </div>
          <div class="info-item">
            <label>固件版本</label>
            <span>{{ device.firmwareVersion }}</span>
          </div>
          <div class="info-item">
            <label>所属客户</label>
            <div>
              <div class="customer-name">{{ device.customerName }}</div>
              <div class="customer-phone">{{ device.customerPhone }}</div>
            </div>
          </div>
          <div class="info-item">
            <label>激活时间</label>
            <span>{{ formatDateTime(device.activatedAt) }}</span>
          </div>
          <div class="info-item">
            <label>最后在线</label>
            <span>{{ formatDateTime(device.lastOnlineAt) }}</span>
          </div>
          <div class="info-item">
            <label>创建时间</label>
            <span>{{ formatDateTime(device.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 技术参数 -->
      <div class="detail-section" v-if="device.specifications">
        <h3 class="section-title">技术参数</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>处理器</label>
            <span>{{ device.specifications.cpu }}</span>
          </div>
          <div class="info-item">
            <label>内存</label>
            <span>{{ device.specifications.memory }}</span>
          </div>
          <div class="info-item">
            <label>存储</label>
            <span>{{ device.specifications.storage }}</span>
          </div>
          <div class="info-item">
            <label>显示屏</label>
            <span>{{ device.specifications.display }}</span>
          </div>
          <div class="info-item">
            <label>电池</label>
            <span>{{ device.specifications.battery }}</span>
          </div>
          <div class="info-item">
            <label>连接性</label>
            <div class="connectivity-tags">
              <el-tag 
                v-for="conn in device.specifications.connectivity" 
                :key="conn" 
                size="small"
                class="connectivity-tag"
              >
                {{ conn }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 使用统计 -->
      <div class="detail-section" v-if="device.usageStats">
        <h3 class="section-title">使用统计</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>总运行时长</label>
            <span>{{ formatDuration(device.usageStats.totalRuntime) }}</span>
          </div>
          <div class="info-item">
            <label>使用次数</label>
            <span>{{ device.usageStats.usageCount }}次</span>
          </div>
          <div class="info-item">
            <label>平均使用时长</label>
            <span>{{ formatDuration(device.usageStats.averageSessionTime) }}</span>
          </div>
          <div class="info-item">
            <label>最后使用时间</label>
            <span>{{ formatDateTime(device.usageStats.lastUsedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 维护记录 -->
      <div class="detail-section" v-if="device.maintenanceRecords && device.maintenanceRecords.length > 0">
        <h3 class="section-title">维护记录</h3>
        <div class="maintenance-list">
          <div 
            v-for="record in device.maintenanceRecords" 
            :key="record.id"
            class="maintenance-item"
          >
            <div class="maintenance-header">
              <el-tag :type="getMaintenanceTypeTag(record.type)" size="small">
                {{ getMaintenanceTypeText(record.type) }}
              </el-tag>
              <span class="maintenance-time">{{ formatDateTime(record.startTime) }}</span>
              <el-tag 
                :type="getMaintenanceStatusTag(record.status)" 
                size="small"
              >
                {{ getMaintenanceStatusText(record.status) }}
              </el-tag>
            </div>
            <div class="maintenance-content">
              <p class="maintenance-desc">{{ record.description }}</p>
              <div class="maintenance-details">
                <span class="technician">技术员：{{ record.technician }}</span>
                <span v-if="record.cost" class="cost">费用：¥{{ record.cost }}</span>
              </div>
              <div v-if="record.parts && record.parts.length > 0" class="parts">
                <span class="parts-label">更换部件：</span>
                <el-tag 
                  v-for="part in record.parts" 
                  :key="part" 
                  size="small"
                  class="part-tag"
                >
                  {{ part }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 设备配置 -->
      <div class="detail-section" v-if="device.configuration">
        <h3 class="section-title">设备配置</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>语言</label>
            <span>{{ device.configuration.language }}</span>
          </div>
          <div class="info-item">
            <label>时区</label>
            <span>{{ device.configuration.timezone }}</span>
          </div>
          <div class="info-item">
            <label>自动更新</label>
            <el-tag :type="device.configuration.autoUpdate ? 'success' : 'info'" size="small">
              {{ device.configuration.autoUpdate ? '已启用' : '已禁用' }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>调试模式</label>
            <el-tag :type="device.configuration.debugMode ? 'warning' : 'info'" size="small">
              {{ device.configuration.debugMode ? '已启用' : '已禁用' }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 位置信息 -->
      <div class="detail-section" v-if="device.location">
        <h3 class="section-title">位置信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>地址</label>
            <span>{{ device.location.address }}</span>
          </div>
          <div class="info-item">
            <label>坐标</label>
            <span>{{ device.location.latitude }}, {{ device.location.longitude }}</span>
          </div>
          <div class="info-item">
            <label>最后更新</label>
            <span>{{ formatDateTime(device.location.lastUpdated) }}</span>
          </div>
        </div>
      </div>

      <!-- 备注信息 -->
      <div class="detail-section" v-if="device.notes">
        <h3 class="section-title">备注信息</h3>
        <div class="notes-content">
          {{ device.notes }}
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑设备</el-button>
        <el-dropdown @command="handleAction" trigger="click">
          <el-button type="primary">
            设备操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="reboot" v-if="device?.status === 'online'">
                重启设备
              </el-dropdown-item>
              <el-dropdown-item command="maintenance" v-if="device?.status !== 'maintenance'">
                进入维护
              </el-dropdown-item>
              <el-dropdown-item command="activate" v-if="device?.status === 'offline'">
                激活设备
              </el-dropdown-item>
              <el-dropdown-item command="firmware">
                推送固件
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { 
  CircleCheckFilled, 
  CircleCloseFilled, 
  WarningFilled, 
  Tools,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { ManagedDevice } from '@/types/managedDevice'
import { managedDeviceAPI } from '@/api/managedDevice'

interface Props {
  modelValue: boolean
  device: ManagedDevice | null
}

interface Emits {
  (e: 'update:modelValue', value: boolean): void
  (e: 'edit', device: ManagedDevice): void
  (e: 'status-change'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 关闭对话框
const handleClose = () => {
  visible.value = false
}

// 编辑设备
const handleEdit = () => {
  if (props.device) {
    emit('edit', props.device)
  }
}

// 设备操作
const handleAction = async (command: string) => {
  if (!props.device) return
  
  try {
    switch (command) {
      case 'reboot':
        await managedDeviceAPI.rebootDevice(props.device.id)
        ElMessage.success('设备重启指令已发送')
        break
      case 'maintenance':
        await managedDeviceAPI.updateDeviceStatus(props.device.id, 'maintenance' as any)
        ElMessage.success('设备已进入维护模式')
        emit('status-change')
        break
      case 'activate':
        await managedDeviceAPI.activateDevice(props.device.id)
        ElMessage.success('设备激活指令已发送')
        emit('status-change')
        break
      case 'firmware':
        await managedDeviceAPI.pushFirmware(props.device.id)
        ElMessage.success('固件推送已启动')
        break
    }
  } catch (error: any) {
    console.error('设备操作失败:', error)
    ElMessage.error(error?.message || '操作失败')
  }
}

// 工具方法
const getModelTagType = (model: string) => {
  const types: Record<string, any> = {
    'YX-EDU-2024': 'primary',
    'YX-HOME-2024': 'success',
    'YX-PRO-2024': 'warning'
  }
  return types[model] || 'info'
}

const getModelName = (model: string) => {
  const names: Record<string, string> = {
    'YX-EDU-2024': '教育版',
    'YX-HOME-2024': '家庭版',
    'YX-PRO-2024': '专业版'
  }
  return names[model] || model
}

const getStatusIconClass = (status: string) => {
  const classes: Record<string, string> = {
    online: 'online',
    offline: 'offline',
    error: 'error',
    maintenance: 'maintenance'
  }
  return classes[status] || 'offline'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    online: '在线',
    offline: '离线',
    error: '故障',
    maintenance: '维护中'
  }
  return texts[status] || status
}

const getMaintenanceTypeTag = (type: string) => {
  const types: Record<string, any> = {
    repair: 'danger',
    upgrade: 'success',
    inspection: 'info',
    replacement: 'warning'
  }
  return types[type] || 'info'
}

const getMaintenanceTypeText = (type: string) => {
  const texts: Record<string, string> = {
    repair: '维修',
    upgrade: '升级',
    inspection: '检查',
    replacement: '更换'
  }
  return texts[type] || type
}

const getMaintenanceStatusTag = (status: string) => {
  const types: Record<string, any> = {
    pending: 'info',
    in_progress: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

const getMaintenanceStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待处理',
    in_progress: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

const formatDateTime = (dateStr?: string) => {
  if (!dateStr) return '无'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatDuration = (minutes?: number) => {
  if (!minutes) return '0分钟'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
}
</script>

<style lang="scss" scoped>
.device-detail {
  .detail-section {
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
    }
    
    .info-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
      gap: 16px;
      
      .info-item {
        display: flex;
        flex-direction: column;
        gap: 4px;
        
        label {
          font-size: 12px;
          color: #8c8c8c;
          font-weight: 500;
        }
        
        span {
          font-size: 14px;
          color: #262626;
        }
        
        .customer-name {
          font-weight: 500;
          color: #262626;
        }
        
        .customer-phone {
          font-size: 12px;
          color: #8c8c8c;
        }
        
        .status-display {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .el-icon {
            &.online { color: #67C23A; }
            &.offline { color: #909399; }
            &.error { color: #F56C6C; }
            &.maintenance { color: #E6A23C; }
          }
        }
        
        .connectivity-tags {
          display: flex;
          flex-wrap: wrap;
          gap: 4px;
          
          .connectivity-tag {
            margin: 0;
          }
        }
      }
    }
    
    .maintenance-list {
      .maintenance-item {
        border: 1px solid #f0f0f0;
        border-radius: 6px;
        padding: 16px;
        margin-bottom: 12px;
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .maintenance-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 8px;
          
          .maintenance-time {
            font-size: 12px;
            color: #8c8c8c;
            flex: 1;
          }
        }
        
        .maintenance-content {
          .maintenance-desc {
            margin: 0 0 8px 0;
            color: #262626;
            font-size: 14px;
          }
          
          .maintenance-details {
            display: flex;
            gap: 16px;
            margin-bottom: 8px;
            font-size: 12px;
            color: #8c8c8c;
          }
          
          .parts {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;
            
            .parts-label {
              font-size: 12px;
              color: #8c8c8c;
            }
            
            .part-tag {
              margin: 0;
            }
          }
        }
      }
    }
    
    .notes-content {
      padding: 12px;
      background: #fafafa;
      border-radius: 6px;
      color: #262626;
      font-size: 14px;
      line-height: 1.5;
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>