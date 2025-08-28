<!--
  设备管理页面
  功能：设备列表、设备状态监控、设备信息管理、固件版本管理
-->
<template>
  <div class="device-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>设备管理</h2>
        <p class="header-subtitle">机器人设备监控与管理 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDeviceDialog">
          <el-icon><Plus /></el-icon>
          添加设备
        </el-button>
      </div>
    </div>

    <!-- 设备统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ deviceStats.total }}</div>
          <div class="stat-label">设备总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon online">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ deviceStats.online }}</div>
          <div class="stat-label">在线设备</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon offline">
          <el-icon><CircleCloseFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ deviceStats.offline }}</div>
          <div class="stat-label">离线设备</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon error">
          <el-icon><WarningFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ deviceStats.error }}</div>
          <div class="stat-label">故障设备</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-filters">
      <div class="filters-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索设备序列号、客户姓名、设备型号"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          style="width: 350px;"
        />
        <el-select v-model="statusFilter" placeholder="设备状态" clearable @change="handleFilter">
          <el-option label="全部状态" value="" />
          <el-option label="在线" value="online" />
          <el-option label="离线" value="offline" />
          <el-option label="故障" value="error" />
          <el-option label="维护中" value="maintenance" />
        </el-select>
        <el-select v-model="modelFilter" placeholder="设备型号" clearable @change="handleFilter">
          <el-option label="全部型号" value="" />
          <el-option label="教育版" value="YX-EDU-2024" />
          <el-option label="家庭版" value="YX-HOME-2024" />
          <el-option label="专业版" value="YX-PRO-2024" />
        </el-select>
        <el-select v-model="customerFilter" placeholder="所属客户" clearable @change="handleFilter" filterable>
          <el-option label="全部客户" value="" />
          <el-option
            v-for="customer in customerOptions"
            :key="customer.id"
            :label="customer.name"
            :value="customer.id"
          />
        </el-select>
      </div>
      <div class="filters-right">
     
        <el-dropdown @command="handleBatchAction" v-if="selectedDevices.length > 0">
      
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="updateStatus">更新状态</el-dropdown-item>
              <el-dropdown-item command="pushFirmware">推送固件</el-dropdown-item>
              <el-dropdown-item command="reboot">重启设备</el-dropdown-item>
              <el-dropdown-item command="delete" divided>批量删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
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
        <el-table-column prop="serialNumber" label="设备序列号" width="180" />
        <el-table-column prop="model" label="设备型号" width="150">
          <template #default="{ row }">
            <el-tag :type="getModelTagType(row.model)" size="small">
              {{ getModelName(row.model) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="所属客户" width="150">
          <template #default="{ row }">
            <div class="customer-info">
              <div class="customer-name">{{ row.customerName }}</div>
              <div class="customer-phone">{{ row.customerPhone }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="设备状态" width="120">
          <template #default="{ row }">
            <div class="status-cell">
              <el-icon 
                :class="getStatusIconClass(row.status)"
                class="status-icon"
              >
                <CircleCheckFilled v-if="row.status === 'online'" />
                <CircleCloseFilled v-else-if="row.status === 'offline'" />
                <WarningFilled v-else-if="row.status === 'error'" />
                <Tools v-else />
              </el-icon>
              <span class="status-text">{{ getStatusText(row.status) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="firmwareVersion" label="固件版本" width="120" />
        <el-table-column prop="lastOnlineAt" label="最后在线时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastOnlineAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="activatedAt" label="激活时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.activatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="使用统计" width="150">
          <template #default="{ row }">
            <div class="usage-stats">
              <div class="usage-item">
                <span class="label">运行时长:</span>
                <span class="value">{{ formatDuration(row.totalRuntime) }}</span>
              </div>
              <div class="usage-item">
                <span class="label">使用次数:</span>
                <span class="value">{{ row.usageCount }}次</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewDeviceDetail(row)">
              查看详情
            </el-button>
            <el-button text type="primary" size="small" @click="editDevice(row)">
              编辑
            </el-button>
            <el-dropdown @command="(command) => handleDeviceAction(command, row)" trigger="click">
              <el-button text type="primary" size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="reboot" v-if="row.status === 'online'">
                    重启设备
                  </el-dropdown-item>
                  <el-dropdown-item command="maintenance" v-if="row.status !== 'maintenance'">
                    进入维护
                  </el-dropdown-item>
                  <el-dropdown-item command="activate" v-if="row.status === 'offline'">
                    激活设备
                  </el-dropdown-item>
                  <el-dropdown-item command="firmware">
                    推送固件
                  </el-dropdown-item>
                  <el-dropdown-item command="logs">
                    查看日志
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    删除设备
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

    <!-- 设备详情对话框 -->
    <DeviceDetailDialog
      v-model="detailDialogVisible"
      :device="selectedDevice"
      @edit="handleEditFromDetail"
      @status-change="handleStatusChange"
    />

    <!-- 设备编辑对话框 -->
    <DeviceFormDialog
      v-model="editDialogVisible"
      :device="editingDevice"
      @success="handleDeviceEditSuccess"
    />

    <!-- 设备日志对话框 -->
    <DeviceLogsDialog
      v-model="logsDialogVisible"
      :device="selectedDevice"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  Plus,
  Search,
  ArrowDown,
  Monitor,
  CircleCheckFilled,
  CircleCloseFilled,
  WarningFilled,
  Tools
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mockDeviceAPI } from '@/api/mock/device'
import type { Device, DeviceStats } from '@/types/device'
import DeviceDetailDialog from '@/components/device/DeviceDetailDialog.vue'
import DeviceFormDialog from '@/components/device/DeviceFormDialog.vue'
import DeviceLogsDialog from '@/components/device/DeviceLogsDialog.vue'

// 响应式数据
const tableLoading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const modelFilter = ref('')
const customerFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 设备数据
const deviceList = ref<Device[]>([])
const selectedDevices = ref<Device[]>([])
const selectedDevice = ref<Device | null>(null)
const deviceStats = ref<DeviceStats>({
  total: 0,
  online: 0,
  offline: 0,
  error: 0,
  maintenance: 0
})

// 客户选项
const customerOptions = ref<any[]>([])

// 弹窗状态
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const logsDialogVisible = ref(false)
const editingDevice = ref<Device | null>(null)

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
const handleSelectionChange = (selection: Device[]) => {
  selectedDevices.value = selection
}

// 加载设备数据
const loadDeviceData = async () => {
  tableLoading.value = true
  try {
    const response = await mockDeviceAPI.getDevices({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      status: statusFilter.value as any,
      model: modelFilter.value,
      customerId: customerFilter.value
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

// 加载客户选项
const loadCustomerOptions = async () => {
  try {
    const response = await mockDeviceAPI.getCustomerOptions()
    customerOptions.value = response.data
  } catch (error) {
    console.error('加载客户选项失败:', error)
  }
}

// 查看设备详情
const viewDeviceDetail = (device: Device) => {
  selectedDevice.value = device
  detailDialogVisible.value = true
}

// 编辑设备
const editDevice = (device: Device) => {
  editingDevice.value = device
  editDialogVisible.value = true
}

// 从详情页编辑
const handleEditFromDetail = (device: Device) => {
  detailDialogVisible.value = false
  editDevice(device)
}

// 显示添加设备弹窗
const showAddDeviceDialog = () => {
  editingDevice.value = null
  editDialogVisible.value = true
}

// 设备编辑成功处理
const handleDeviceEditSuccess = () => {
  loadDeviceData()
  editingDevice.value = null
}

// 设备操作处理
const handleDeviceAction = async (command: string, device: Device) => {
  try {
    switch (command) {
      case 'reboot':
        await mockDeviceAPI.rebootDevice(device.id)
        ElMessage.success('设备重启指令已发送')
        break
      case 'maintenance':
        await mockDeviceAPI.updateDeviceStatus(device.id, 'maintenance')
        ElMessage.success('设备已进入维护模式')
        break
      case 'activate':
        await mockDeviceAPI.activateDevice(device.id)
        ElMessage.success('设备激活指令已发送')
        break
      case 'firmware':
        await mockDeviceAPI.pushFirmware(device.id)
        ElMessage.success('固件推送已启动')
        break
      case 'logs':
        selectedDevice.value = device
        logsDialogVisible.value = true
        return
      case 'delete':
        await ElMessageBox.confirm('确定要删除这个设备吗？此操作不可恢复。', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await mockDeviceAPI.deleteDevice(device.id)
        ElMessage.success('设备已删除')
        break
    }
    loadDeviceData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('设备操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 批量操作处理
const handleBatchAction = async (command: string) => {
  const deviceIds = selectedDevices.value.map(device => device.id)
  
  try {
    switch (command) {
      case 'updateStatus':
        // 这里可以弹出状态选择对话框
        ElMessage.info('批量状态更新功能开发中')
        break
      case 'pushFirmware':
        await mockDeviceAPI.batchPushFirmware(deviceIds)
        ElMessage.success(`已向 ${deviceIds.length} 个设备推送固件`)
        break
      case 'reboot':
        await mockDeviceAPI.batchRebootDevices(deviceIds)
        ElMessage.success(`已向 ${deviceIds.length} 个设备发送重启指令`)
        break
      case 'delete':
        await ElMessageBox.confirm(`确定要删除选中的 ${deviceIds.length} 个设备吗？此操作不可恢复。`, '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await mockDeviceAPI.batchDeleteDevices(deviceIds)
        ElMessage.success(`已删除 ${deviceIds.length} 个设备`)
        break
    }
    loadDeviceData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 状态变更处理
const handleStatusChange = () => {
  loadDeviceData()
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

onMounted(() => {
  loadDeviceData()
  loadCustomerOptions()
})
</script>

<style lang="scss" scoped>
.device-management {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      h2 {
        margin: 0 0 4px 0;
        color: #262626;
        font-size: 24px;
        font-weight: 600;
      }
      
      .header-subtitle {
        margin: 0;
        color: #8c8c8c;
        font-size: 14px;
        font-weight: 400;
      }
    }
    
    .header-right {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
    margin-bottom: 24px;

    .stat-card {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;

        &.total {
          background: linear-gradient(135deg, #409EFF, #66B1FF);
          color: white;
        }

        &.online {
          background: linear-gradient(135deg, #67C23A, #85CE61);
          color: white;
        }

        &.offline {
          background: linear-gradient(135deg, #909399, #B1B3B8);
          color: white;
        }

        &.error {
          background: linear-gradient(135deg, #F56C6C, #F78989);
          color: white;
        }
      }

      .stat-content {
        flex: 1;

        .stat-value {
          font-size: 24px;
          font-weight: 700;
          color: #303133;
          line-height: 1.2;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #606266;
        }
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

    .customer-info {
      .customer-name {
        font-weight: 500;
        color: #262626;
        margin-bottom: 4px;
      }

      .customer-phone {
        font-size: 12px;
        color: #8c8c8c;
      }
    }

    .status-cell {
      display: flex;
      align-items: center;
      gap: 8px;

      .status-icon {
        &.online {
          color: #67C23A;
        }

        &.offline {
          color: #909399;
        }

        &.error {
          color: #F56C6C;
        }

        &.maintenance {
          color: #E6A23C;
        }
      }

      .status-text {
        font-size: 13px;
        font-weight: 500;
      }
    }

    .usage-stats {
      .usage-item {
        display: flex;
        justify-content: space-between;
        margin-bottom: 4px;
        font-size: 12px;

        &:last-child {
          margin-bottom: 0;
        }

        .label {
          color: #8c8c8c;
        }

        .value {
          color: #262626;
          font-weight: 500;
        }
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}


</style>