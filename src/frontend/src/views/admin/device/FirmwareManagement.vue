<!--
  固件管理主页面
  功能：固件版本管理、设备固件状态监控、更新任务管理
-->
<template>
  <div class="firmware-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>固件管理</h2>
        <p class="header-subtitle">设备固件版本管理与更新 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showUploadDialog">
          <el-icon><Upload /></el-icon>
          上传固件
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon total">
          <el-icon><Files /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.totalVersions }}</div>
          <div class="stat-label">固件版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon stable">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.stableVersions }}</div>
          <div class="stat-label">稳定版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon testing">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.testingVersions }}</div>
          <div class="stat-label">测试版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon downloads">
          <el-icon><Download /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.totalDownloads }}</div>
          <div class="stat-label">总下载量</div>
        </div>
      </div>
    </div>

    <!-- 主要内容标签页 -->
    <el-tabs v-model="activeTab" type="border-card" class="main-tabs">
      <!-- 固件版本管理 -->
      <el-tab-pane label="固件版本" name="versions">
        <FirmwareVersionList />
      </el-tab-pane>

      <!-- 设备固件状态 -->
      <el-tab-pane label="设备状态" name="devices">
        <DeviceFirmwareStatus />
      </el-tab-pane>

      <!-- 更新任务 -->
      <el-tab-pane label="更新任务" name="tasks">
        <FirmwareUpdateTasks />
      </el-tab-pane>
    </el-tabs>

    <!-- 固件上传对话框 -->
    <FirmwareUploadDialog
      v-model="uploadDialogVisible"
      @upload-success="handleUploadSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Upload,
  Files,
  CircleCheckFilled,
  Warning,
  Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import type { FirmwareStats } from '@/types/firmware'
import FirmwareVersionList from '@/components/firmware/FirmwareVersionList.vue'
import DeviceFirmwareStatus from '@/components/firmware/DeviceFirmwareStatus.vue'
import FirmwareUpdateTasks from '@/components/firmware/FirmwareUpdateTasks.vue'
import FirmwareUploadDialog from '@/components/firmware/FirmwareUploadDialog.vue'

// 响应式数据
const activeTab = ref('versions')

// 统计数据
const firmwareStats = ref<FirmwareStats>({
  totalVersions: 0,
  stableVersions: 0,
  testingVersions: 0,
  draftVersions: 0,
  deprecatedVersions: 0,
  totalDownloads: 0,
  deviceDistribution: {}
})

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await mockFirmwareAPI.getFirmwareStats()
    firmwareStats.value = response.data
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}



// 上传对话框状态
const uploadDialogVisible = ref(false)

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
}

// 上传成功处理
const handleUploadSuccess = (firmware: any) => {
  ElMessage.success(`固件 ${firmware.version} 上传成功`)
  uploadDialogVisible.value = false
  // 刷新数据
  loadStats()
}

// 生命周期
onMounted(() => {
  loadStats()
})
</script>

<style lang="scss" scoped>
.firmware-management {
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

        &.stable {
          background: linear-gradient(135deg, #67C23A, #85CE61);
          color: white;
        }

        &.testing {
          background: linear-gradient(135deg, #E6A23C, #EEBE77);
          color: white;
        }

        &.downloads {
          background: linear-gradient(135deg, #909399, #B1B3B8);
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

  .main-tabs {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    :deep(.el-tabs__content) {
      padding: 20px;
    }
  }
}

@media (max-width: 1200px) {
  .firmware-management {
    .stats-cards {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: 768px) {
  .firmware-management {
    padding: 16px;

    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .header-right {
        justify-content: center;
      }
    }

    .stats-cards {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
}
</style>