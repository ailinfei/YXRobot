<!--
  设备固件状态组件
  功能：显示设备固件状态，支持批量更新操作
-->
<template>
  <div class="device-firmware-status">
    <!-- 更新提醒组件 -->
    <FirmwareUpdateNotification />

    <!-- 统计卡片和图表 -->
    <div class="status-overview">
      <!-- 统计卡片 -->
      <div class="status-stats">
        <div class="stat-item up-to-date">
          <div class="stat-icon">
            <el-icon><CircleCheckFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ deviceStats.upToDate }}</div>
            <div class="stat-label">最新版本</div>
            <div class="stat-percentage">{{ getStatusPercentage('upToDate') }}%</div>
          </div>
        </div>
        
        <div class="stat-item update-available">
          <div class="stat-icon">
            <el-icon><Warning /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ deviceStats.updateAvailable }}</div>
            <div class="stat-label">有可用更新</div>
            <div class="stat-percentage">{{ getStatusPercentage('updateAvailable') }}%</div>
          </div>
        </div>
        
        <div class="stat-item updating">
          <div class="stat-icon">
            <el-icon><Loading /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ deviceStats.updating }}</div>
            <div class="stat-label">更新中</div>
            <div class="stat-percentage">{{ getStatusPercentage('updating') }}%</div>
          </div>
        </div>
        
        <div class="stat-item failed">
          <div class="stat-icon">
            <el-icon><CircleCloseFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ deviceStats.failed }}</div>
            <div class="stat-label">更新失败</div>
            <div class="stat-percentage">{{ getStatusPercentage('failed') }}%</div>
          </div>
        </div>
      </div>

      <!-- 状态分布图表 -->
      <div class="status-chart">
        <div class="chart-header">
          <h3>设备状态分布</h3>
          <el-button text type="primary" size="small" @click="refreshChart">
            <el-icon><Refresh /></el-icon>
            刷新图表
          </el-button>
        </div>
        <div ref="chartContainer" class="chart-container"></div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filters">
      <div class="filters-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设备序列号或名称"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 300px;"
        />
        <el-select v-model="statusFilter" placeholder="更新状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="最新版本" value="up-to-date" />
          <el-option label="有可用更新" value="update-available" />
          <el-option label="更新中" value="updating" />
          <el-option label="更新失败" value="failed" />
        </el-select>
        <el-select v-model="modelFilter" placeholder="设备型号" clearable @change="handleFilter">
          <el-option label="全部型号" value="" />
          <el-option label="教育版" value="YX-EDU-2024" />
          <el-option label="家庭版" value="YX-HOME-2024" />
          <el-option label="专业版" value="YX-PRO-2024" />
        </el-select>
      </div>
      <div class="filters-right">
        <el-button 
          type="primary" 
          @click="showBatchUpdateDialog" 
          :disabled="selectedDevices.length === 0"
        >
          <el-icon><Upload /></el-icon>
          批量更新 ({{ selectedDevices.length }})
        </el-button>
        <el-button @click="checkForUpdates" :loading="checkLoading">
          <el-icon><Refresh /></el-icon>
          检查更新
        </el-button>
      </div>
    </div>

    <!-- 设备列表表格 -->
    <div class="table-section">
      <el-table 
        :data="deviceList" 
        v-loading="tableLoading" 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="deviceSerialNumber" label="设备序列号" width="150" />
        <el-table-column prop="deviceName" label="设备名称" width="150" />
        <el-table-column prop="deviceModel" label="设备型号" width="120">
          <template #default="{ row }">
            <el-tag size="small">
              {{ getModelName(row.deviceModel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="currentVersion" label="当前版本" width="120" />
        <el-table-column prop="latestVersion" label="最新版本" width="120">
          <template #default="{ row }">
            <span :class="{ 'version-highlight': row.updateAvailable }">
              {{ row.latestVersion }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="updateStatus" label="更新状态" width="120">
          <template #default="{ row }">
            <div class="status-cell">
              <el-icon :class="getStatusIconClass(row.updateStatus)">
                <CircleCheckFilled v-if="row.updateStatus === 'up-to-date'" />
                <Warning v-else-if="row.updateStatus === 'update-available'" />
                <Loading v-else-if="row.updateStatus === 'updating'" />
                <CircleCloseFilled v-else />
              </el-icon>
              <span class="status-text">{{ getUpdateStatusText(row.updateStatus) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="lastUpdateCheck" label="最后检查" width="150">
          <template #default="{ row }">
            {{ formatDateTime(row.lastUpdateCheck) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              text 
              type="primary" 
              size="small" 
              @click="updateSingleDevice(row)"
              :disabled="!row.updateAvailable || row.updateStatus === 'updating'"
            >
              更新
            </el-button>
            <el-button text type="success" size="small" @click="viewUpdateHistory(row)">
              历史
            </el-button>
            <el-dropdown @command="(command) => handleDeviceAction(command, row)" trigger="click">
              <el-button text type="primary" size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="check">
                    检查更新
                  </el-dropdown-item>
                  <el-dropdown-item command="rollback" v-if="row.updateHistory.length > 0">
                    版本回滚
                  </el-dropdown-item>
                  <el-dropdown-item command="logs">
                    查看日志
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 批量更新对话框 -->
    <FirmwareBatchUpdateDialog
      v-model="batchUpdateDialogVisible"
      :available-devices="selectedDevices.map(device => ({
        deviceId: device.deviceId,
        deviceName: device.deviceName,
        deviceSerialNumber: device.deviceSerialNumber,
        deviceModel: device.deviceModel,
        currentVersion: device.currentVersion,
        targetVersion: device.latestVersion,
        priority: calculateDevicePriority(device),
        isOnline: device.isOnline,
        signalStrength: device.signalStrength
      }))"
      @update-complete="handleBatchUpdateComplete"
    />
  </div>
</template><script
 setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import {
  Search,
  Upload,
  Refresh,
  CircleCheckFilled,
  Warning,
  Loading,
  CircleCloseFilled,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import type { DeviceFirmwareStatus } from '@/types/firmware'
import { DEVICE_UPDATE_STATUS_TEXT } from '@/types/firmware'

import FirmwareUpdateNotification from './FirmwareUpdateNotification.vue'
import FirmwareBatchUpdateDialog from './FirmwareBatchUpdateDialog.vue'

// 响应式数据
const tableLoading = ref(false)
const checkLoading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const modelFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 设备数据
const deviceList = ref<DeviceFirmwareStatus[]>([])
const selectedDevices = ref<DeviceFirmwareStatus[]>([])
const deviceStats = ref({
  total: 0,
  upToDate: 0,
  updateAvailable: 0,
  updating: 0,
  failed: 0
})

// 图表相关
const chartContainer = ref<HTMLElement>()
let chartInstance: echarts.ECharts | null = null

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  loadDeviceData()
}

// 筛选处理
const handleFilter = () => {
  currentPage.value = 1
  loadDeviceData()
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadDeviceData()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadDeviceData()
}

// 选择处理
const handleSelectionChange = (selection: DeviceFirmwareStatus[]) => {
  selectedDevices.value = selection
}

// 加载设备数据
const loadDeviceData = async () => {
  tableLoading.value = true
  try {
    const response = await mockFirmwareAPI.getDeviceFirmwareStatus({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      updateStatus: statusFilter.value as any,
      deviceModel: modelFilter.value
    })
    
    deviceList.value = response.data.list
    total.value = response.data.total
    deviceStats.value = response.data.stats
  } catch (error) {
    console.error('加载设备数据失败:', error)
    ElMessage.error('加载设备数据失败')
  } finally {
    tableLoading.value = false
  }
}

// 检查更新
const checkForUpdates = async () => {
  checkLoading.value = true
  try {
    // 模拟检查更新
    await new Promise(resolve => setTimeout(resolve, 2000))
    await loadDeviceData()
    ElMessage.success('更新检查完成')
  } finally {
    checkLoading.value = false
  }
}

// 单个设备更新
const updateSingleDevice = (device: DeviceFirmwareStatus) => {
  ElMessage.info(`开始更新设备：${device.deviceSerialNumber}`)
}

// 批量更新对话框状态
const batchUpdateDialogVisible = ref(false)

// 批量更新
const showBatchUpdateDialog = () => {
  if (selectedDevices.value.length === 0) {
    ElMessage.warning('请先选择要更新的设备')
    return
  }
  
  // 转换设备数据格式
  const availableDevices = selectedDevices.value.map(device => ({
    deviceId: device.deviceId,
    deviceName: device.deviceName,
    deviceSerialNumber: device.deviceSerialNumber,
    deviceModel: device.deviceModel,
    currentVersion: device.currentVersion,
    targetVersion: device.latestVersion,
    priority: calculateDevicePriority(device),
    isOnline: device.isOnline,
    signalStrength: device.signalStrength
  }))
  
  batchUpdateDialogVisible.value = true
}

// 计算设备更新优先级
const calculateDevicePriority = (device: DeviceFirmwareStatus): 'high' | 'medium' | 'low' => {
  if (device.updateStatus === 'failed') return 'high'
  if (!device.isOnline) return 'low'
  if (device.signalStrength && device.signalStrength < 30) return 'low'
  
  // 根据版本差异判断优先级
  const versionDiff = device.latestVersion.localeCompare(device.currentVersion)
  if (versionDiff > 0) {
    // 检查是否是主版本更新
    const currentMajor = device.currentVersion.split('.')[0]
    const latestMajor = device.latestVersion.split('.')[0]
    if (currentMajor !== latestMajor) return 'high'
    return 'medium'
  }
  
  return 'low'
}

// 处理批量更新完成
const handleBatchUpdateComplete = (result: any) => {
  ElMessage.success(`批量更新完成: ${result.successCount}/${result.totalDevices} 设备更新成功`)
  // 刷新设备数据
  loadDeviceData()
}

// 查看更新历史
const viewUpdateHistory = (device: DeviceFirmwareStatus) => {
  ElMessage.info(`查看设备更新历史：${device.deviceSerialNumber}`)
}

// 设备操作处理
const handleDeviceAction = async (command: string, device: DeviceFirmwareStatus) => {
  try {
    switch (command) {
      case 'check':
        ElMessage.info(`检查设备更新：${device.deviceSerialNumber}`)
        break
      case 'rollback':
        ElMessage.info('版本回滚功能开发中')
        break
      case 'logs':
        ElMessage.info('查看设备日志功能开发中')
        break
    }
  } catch (error) {
    console.error('设备操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 工具方法
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
    'up-to-date': 'status-success',
    'update-available': 'status-warning',
    'updating': 'status-info',
    'failed': 'status-danger'
  }
  return classes[status] || 'status-info'
}

const getUpdateStatusText = (status: string) => {
  return DEVICE_UPDATE_STATUS_TEXT[status as keyof typeof DEVICE_UPDATE_STATUS_TEXT] || status
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 计算状态百分比
const getStatusPercentage = (statusKey: keyof typeof deviceStats.value) => {
  if (deviceStats.value.total === 0) return 0
  const value = deviceStats.value[statusKey]
  return Math.round((value / deviceStats.value.total) * 100)
}

// 初始化图表
const initChart = async () => {
  if (!chartContainer.value) return

  await nextTick()
  
  if (chartInstance) {
    chartInstance.dispose()
  }

  chartInstance = echarts.init(chartContainer.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      data: ['最新版本', '有可用更新', '更新中', '更新失败']
    },
    series: [
      {
        name: '设备状态',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          {
            value: deviceStats.value.upToDate,
            name: '最新版本',
            itemStyle: { color: '#67c23a' }
          },
          {
            value: deviceStats.value.updateAvailable,
            name: '有可用更新',
            itemStyle: { color: '#e6a23c' }
          },
          {
            value: deviceStats.value.updating,
            name: '更新中',
            itemStyle: { color: '#409eff' }
          },
          {
            value: deviceStats.value.failed,
            name: '更新失败',
            itemStyle: { color: '#f56c6c' }
          }
        ]
      }
    ]
  }

  chartInstance.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance?.resize()
  })
}

// 刷新图表
const refreshChart = () => {
  initChart()
}

onMounted(async () => {
  await loadDeviceData()
  await initChart()
})
</script>

<style lang="scss" scoped>
.device-firmware-status {
  .status-overview {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 20px;
    margin-bottom: 20px;

    .status-stats {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        background: white;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .stat-icon {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
        }

        &.up-to-date .stat-icon {
          background: linear-gradient(135deg, #67c23a, #85ce61);
          color: white;
        }

        &.update-available .stat-icon {
          background: linear-gradient(135deg, #e6a23c, #eebe77);
          color: white;
        }

        &.updating .stat-icon {
          background: linear-gradient(135deg, #409eff, #66b1ff);
          color: white;
        }

        &.failed .stat-icon {
          background: linear-gradient(135deg, #f56c6c, #f78989);
          color: white;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 20px;
            font-weight: 700;
            color: #303133;
            margin-bottom: 2px;
            line-height: 1.2;
          }

          .stat-label {
            font-size: 12px;
            color: #606266;
            margin-bottom: 2px;
          }

          .stat-percentage {
            font-size: 11px;
            color: #909399;
            font-weight: 500;
          }
        }
      }
    }

    .status-chart {
      background: white;
      border-radius: 8px;
      border: 1px solid #e4e7ed;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.06);
      padding: 16px;

      .chart-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        h3 {
          margin: 0;
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }
      }

      .chart-container {
        width: 100%;
        height: 200px;
      }
    }
  }

  .search-filters {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;
    margin-bottom: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .filters-left {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }

    .filters-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .table-section {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;

    .version-highlight {
      color: #e6a23c;
      font-weight: 500;
    }

    .status-cell {
      display: flex;
      align-items: center;
      gap: 6px;

      .status-success {
        color: #67c23a;
      }

      .status-warning {
        color: #e6a23c;
      }

      .status-info {
        color: #409eff;
      }

      .status-danger {
        color: #f56c6c;
      }

      .status-text {
        font-size: 12px;
        font-weight: 500;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

@media (max-width: 1024px) {
  .device-firmware-status {
    .status-overview {
      grid-template-columns: 1fr;
      gap: 16px;

      .status-stats {
        grid-template-columns: repeat(2, 1fr);
      }

      .status-chart {
        .chart-container {
          height: 180px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .device-firmware-status {
    .status-overview {
      .status-stats {
        grid-template-columns: 1fr;
        gap: 12px;
      }

      .status-chart {
        .chart-container {
          height: 160px;
        }
      }
    }

    .search-filters {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .filters-left {
        justify-content: center;
      }

      .filters-right {
        justify-content: center;
      }
    }
  }
}
</style>