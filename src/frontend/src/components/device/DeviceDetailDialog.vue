<!--
  设备详情对话框
  显示设备的完整信息，包括基本信息、技术参数、使用统计、维护记录等
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`设备详情 - ${device?.serialNumber || ''}`"
    width="90%"
    :before-close="handleClose"
    class="device-detail-dialog"
  >
    <div v-if="device" class="device-detail-content">
      <!-- 设备状态和操作 -->
      <div class="device-header">
        <div class="device-status">
          <el-tag :type="getStatusTagType(device.status)" size="large">
            <el-icon>
              <CircleCheckFilled v-if="device.status === 'online'" />
              <CircleCloseFilled v-else-if="device.status === 'offline'" />
              <WarningFilled v-else-if="device.status === 'error'" />
              <Tools v-else />
            </el-icon>
            {{ getStatusText(device.status) }}
          </el-tag>
          <span class="firmware-version">固件版本: {{ device.firmwareVersion }}</span>
        </div>
        <div class="device-actions">
          <el-button type="primary" @click="handleEdit">
            <el-icon><Edit /></el-icon>
            编辑设备
          </el-button>
          <el-dropdown @command="handleDeviceAction" trigger="click">
            <el-button type="success">
              设备操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="reboot" v-if="device.status === 'online'">
                  重启设备
                </el-dropdown-item>
                <el-dropdown-item command="maintenance" v-if="device.status !== 'maintenance'">
                  进入维护
                </el-dropdown-item>
                <el-dropdown-item command="activate" v-if="device.status === 'offline'">
                  激活设备
                </el-dropdown-item>
                <el-dropdown-item command="firmware">
                  推送固件
                </el-dropdown-item>
                <el-dropdown-item command="logs">
                  查看日志
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 设备信息标签页 -->
      <el-tabs v-model="activeTab" type="border-card" class="device-tabs">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="tab-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <div class="info-section">
                  <h3>设备信息</h3>
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="设备序列号">
                      {{ device.serialNumber }}
                    </el-descriptions-item>
                    <el-descriptions-item label="设备型号">
                      <el-tag :type="getModelTagType(device.model)">
                        {{ getModelName(device.model) }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="设备状态">
                      <el-tag :type="getStatusTagType(device.status)">
                        {{ getStatusText(device.status) }}
                      </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="固件版本">
                      {{ device.firmwareVersion }}
                    </el-descriptions-item>
                    <el-descriptions-item label="创建时间">
                      {{ formatDateTime(device.createdAt) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="激活时间">
                      {{ formatDateTime(device.activatedAt) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="最后在线">
                      {{ formatDateTime(device.lastOnlineAt) }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="info-section">
                  <h3>客户信息</h3>
                  <el-descriptions :column="1" border>
                    <el-descriptions-item label="客户姓名">
                      {{ device.customerName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="联系电话">
                      {{ device.customerPhone }}
                    </el-descriptions-item>
                    <el-descriptions-item label="客户ID">
                      {{ device.customerId }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </el-col>
            </el-row>

            <!-- 位置信息 -->
            <el-row v-if="device.location" :gutter="24" style="margin-top: 24px;">
              <el-col :span="24">
                <div class="info-section">
                  <h3>位置信息</h3>
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="地址">
                      {{ device.location.address }}
                    </el-descriptions-item>
                    <el-descriptions-item label="坐标">
                      {{ device.location.latitude.toFixed(6) }}, {{ device.location.longitude.toFixed(6) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="位置更新时间" :span="2">
                      {{ formatDateTime(device.location.lastUpdated) }}
                    </el-descriptions-item>
                  </el-descriptions>
                </div>
              </el-col>
            </el-row>

            <!-- 备注信息 -->
            <el-row v-if="device.notes" :gutter="24" style="margin-top: 24px;">
              <el-col :span="24">
                <div class="info-section">
                  <h3>备注信息</h3>
                  <div class="notes-content">
                    {{ device.notes }}
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>

        <!-- 技术参数 -->
        <el-tab-pane label="技术参数" name="specs">
          <div class="tab-content">
            <div class="specs-section">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="处理器">
                  {{ device.specifications.cpu }}
                </el-descriptions-item>
                <el-descriptions-item label="内存">
                  {{ device.specifications.memory }}
                </el-descriptions-item>
                <el-descriptions-item label="存储">
                  {{ device.specifications.storage }}
                </el-descriptions-item>
                <el-descriptions-item label="显示屏">
                  {{ device.specifications.display }}
                </el-descriptions-item>
                <el-descriptions-item label="电池">
                  {{ device.specifications.battery }}
                </el-descriptions-item>
                <el-descriptions-item label="连接方式">
                  <div class="connectivity-tags">
                    <el-tag
                      v-for="conn in device.specifications.connectivity"
                      :key="conn"
                      size="small"
                      style="margin: 2px;"
                    >
                      {{ conn }}
                    </el-tag>
                  </div>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-tab-pane>

        <!-- 使用统计 -->
        <el-tab-pane label="使用统计" name="usage">
          <div class="tab-content">
            <div class="usage-section">
              <el-row :gutter="24">
                <el-col :span="12">
                  <div class="usage-card">
                    <div class="usage-icon">
                      <el-icon><Clock /></el-icon>
                    </div>
                    <div class="usage-info">
                      <div class="usage-value">{{ formatDuration(device.usageStats.totalRuntime) }}</div>
                      <div class="usage-label">总运行时间</div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="usage-card">
                    <div class="usage-icon">
                      <el-icon><DataLine /></el-icon>
                    </div>
                    <div class="usage-info">
                      <div class="usage-value">{{ device.usageStats.usageCount }}</div>
                      <div class="usage-label">使用次数</div>
                    </div>
                  </div>
                </el-col>
              </el-row>
              <el-row :gutter="24" style="margin-top: 16px;">
                <el-col :span="12">
                  <div class="usage-card">
                    <div class="usage-icon">
                      <el-icon><Timer /></el-icon>
                    </div>
                    <div class="usage-info">
                      <div class="usage-value">{{ formatDuration(device.usageStats.averageSessionTime) }}</div>
                      <div class="usage-label">平均使用时长</div>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="usage-card">
                    <div class="usage-icon">
                      <el-icon><Calendar /></el-icon>
                    </div>
                    <div class="usage-info">
                      <div class="usage-value">{{ formatDateTime(device.usageStats.lastUsedAt) }}</div>
                      <div class="usage-label">最后使用时间</div>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </el-tab-pane>

        <!-- 维护记录 -->
        <el-tab-pane label="维护记录" name="maintenance">
          <div class="tab-content">
            <div class="maintenance-section">
              <el-table :data="device.maintenanceRecords" border>
                <el-table-column prop="type" label="维护类型" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getMaintenanceTypeTagType(row.type)" size="small">
                      {{ getMaintenanceTypeText(row.type) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" min-width="150" />
                <el-table-column prop="technician" label="技术员" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getMaintenanceStatusTagType(row.status)" size="small">
                      {{ getMaintenanceStatusText(row.status) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="startTime" label="开始时间" width="150">
                  <template #default="{ row }">
                    {{ formatDateTime(row.startTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="150">
                  <template #default="{ row }">
                    {{ formatDateTime(row.endTime) }}
                  </template>
                </el-table-column>
                <el-table-column prop="cost" label="费用" width="100">
                  <template #default="{ row }">
                    {{ row.cost ? `¥${row.cost}` : '-' }}
                  </template>
                </el-table-column>
                <el-table-column prop="parts" label="更换配件" min-width="120">
                  <template #default="{ row }">
                    <div v-if="row.parts && row.parts.length > 0">
                      <el-tag
                        v-for="part in row.parts"
                        :key="part"
                        size="small"
                        style="margin: 1px;"
                      >
                        {{ part }}
                      </el-tag>
                    </div>
                    <span v-else>-</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>

        <!-- 设备配置 -->
        <el-tab-pane label="设备配置" name="config">
          <div class="tab-content">
            <div class="config-section">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="系统语言">
                  {{ getLanguageName(device.configuration.language) }}
                </el-descriptions-item>
                <el-descriptions-item label="时区">
                  {{ device.configuration.timezone }}
                </el-descriptions-item>
                <el-descriptions-item label="自动更新">
                  <el-tag :type="device.configuration.autoUpdate ? 'success' : 'info'" size="small">
                    {{ device.configuration.autoUpdate ? '已启用' : '已禁用' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="调试模式">
                  <el-tag :type="device.configuration.debugMode ? 'warning' : 'info'" size="small">
                    {{ device.configuration.debugMode ? '已启用' : '已禁用' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>

              <div class="custom-settings" style="margin-top: 24px;">
                <h4>自定义设置</h4>
                <el-descriptions :column="3" border>
                  <el-descriptions-item
                    v-for="(value, key) in device.configuration.customSettings"
                    :key="key"
                    :label="getSettingLabel(key)"
                  >
                    {{ getSettingValue(key, value) }}
                  </el-descriptions-item>
                </el-descriptions>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">编辑设备</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Edit,
  ArrowDown,
  CircleCheckFilled,
  CircleCloseFilled,
  WarningFilled,
  Tools,
  Clock,
  DataLine,
  Timer,
  Calendar
} from '@element-plus/icons-vue'
import type { Device } from '@/types/device'
import { DEVICE_STATUS_TEXT, DEVICE_MODEL_TEXT, MAINTENANCE_TYPE_TEXT } from '@/types/device'
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
  edit: [device: Device]
  'status-change': []
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const activeTab = ref('basic')

// 方法
const handleEdit = () => {
  if (props.device) {
    emit('edit', props.device)
  }
}

const handleDeviceAction = async (command: string) => {
  if (!props.device) return

  try {
    let message = ''

    switch (command) {
      case 'reboot':
        await mockDeviceAPI.rebootDevice(props.device.id)
        message = '设备重启指令已发送'
        break
      case 'maintenance':
        await mockDeviceAPI.updateDeviceStatus(props.device.id, 'maintenance')
        message = '设备已进入维护模式'
        break
      case 'activate':
        await mockDeviceAPI.activateDevice(props.device.id)
        message = '设备激活指令已发送'
        break
      case 'firmware':
        await mockDeviceAPI.pushFirmware(props.device.id)
        message = '固件推送已启动'
        break
      case 'logs':
        ElMessage.info('查看设备日志功能开发中')
        return
    }

    ElMessage.success(message)
    emit('status-change')
  } catch (error) {
    console.error('设备操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
}

// 工具方法
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    online: 'success',
    offline: 'info',
    error: 'danger',
    maintenance: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  return DEVICE_STATUS_TEXT[status as keyof typeof DEVICE_STATUS_TEXT] || status
}

const getModelTagType = (model: string) => {
  const types: Record<string, any> = {
    'YX-EDU-2024': 'primary',
    'YX-HOME-2024': 'success',
    'YX-PRO-2024': 'warning'
  }
  return types[model] || 'info'
}

const getModelName = (model: string) => {
  return DEVICE_MODEL_TEXT[model as keyof typeof DEVICE_MODEL_TEXT] || model
}

const getMaintenanceTypeTagType = (type: string) => {
  const types: Record<string, any> = {
    repair: 'danger',
    upgrade: 'primary',
    inspection: 'info',
    replacement: 'warning'
  }
  return types[type] || 'info'
}

const getMaintenanceTypeText = (type: string) => {
  return MAINTENANCE_TYPE_TEXT[type as keyof typeof MAINTENANCE_TYPE_TEXT] || type
}

const getMaintenanceStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    cancelled: 'info'
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

const getLanguageName = (code: string) => {
  const names: Record<string, string> = {
    'zh-CN': '简体中文',
    'en-US': 'English',
    'ja-JP': '日本語',
    'ko-KR': '한국어'
  }
  return names[code] || code
}

const getSettingLabel = (key: string) => {
  const labels: Record<string, string> = {
    brightness: '屏幕亮度',
    volume: '音量',
    sleepTimeout: '休眠时间'
  }
  return labels[key] || key
}

const getSettingValue = (key: string, value: any) => {
  if (key === 'brightness' || key === 'volume') {
    return `${value}%`
  } else if (key === 'sleepTimeout') {
    return `${value}分钟`
  }
  return value
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
.device-detail-dialog {
  .device-detail-content {
    .device-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;
      padding: 20px;
      background: #f8f9fa;
      border-radius: 8px;

      .device-status {
        display: flex;
        align-items: center;
        gap: 16px;

        .firmware-version {
          color: #666;
          font-size: 14px;
        }
      }

      .device-actions {
        display: flex;
        gap: 12px;
      }
    }

    .device-tabs {
      .tab-content {
        padding: 20px;

        .info-section {
          margin-bottom: 24px;

          h3 {
            margin: 0 0 16px 0;
            font-size: 16px;
            font-weight: 600;
            color: #262626;
          }

          .notes-content {
            padding: 12px;
            background: #f8f9fa;
            border-radius: 6px;
            color: #666;
            line-height: 1.6;
          }
        }

        .specs-section {
          .connectivity-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
          }
        }

        .usage-section {
          .usage-card {
            display: flex;
            align-items: center;
            gap: 16px;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 8px;
            border: 1px solid #e8e8e8;

            .usage-icon {
              width: 48px;
              height: 48px;
              border-radius: 8px;
              background: linear-gradient(135deg, #409EFF, #66B1FF);
              color: white;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 20px;
            }

            .usage-info {
              flex: 1;

              .usage-value {
                font-size: 20px;
                font-weight: 600;
                color: #262626;
                margin-bottom: 4px;
              }

              .usage-label {
                font-size: 14px;
                color: #666;
              }
            }
          }
        }

        .maintenance-section {
          // 维护记录表格样式
        }

        .config-section {
          .custom-settings {
            h4 {
              margin: 0 0 16px 0;
              font-size: 14px;
              font-weight: 600;
              color: #262626;
            }
          }
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
  .device-detail-dialog {
    .device-detail-content {
      .device-header {
        flex-direction: column;
        gap: 16px;
        align-items: stretch;

        .device-actions {
          justify-content: center;
        }
      }
    }
  }
}
</style>