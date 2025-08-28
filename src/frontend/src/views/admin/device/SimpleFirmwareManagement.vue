<template>
  <div class="simple-firmware-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-left">
        <h2>固件管理</h2>
        <p class="header-subtitle">设备固件版本管理与更新</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showUploadDialog">
          <el-icon><Upload /></el-icon>
          上传固件
        </el-button>
        <el-button @click="refreshData" :loading="refreshLoading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Files /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.totalVersions }}</div>
          <div class="stat-label">固件版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><CircleCheckFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.stableVersions }}</div>
          <div class="stat-label">稳定版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.testingVersions }}</div>
          <div class="stat-label">测试版本</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ firmwareStats.devicesNeedUpdate }}</div>
          <div class="stat-label">待更新设备</div>
        </div>
      </div>
    </div>

    <!-- 固件版本列表 -->
    <div class="firmware-list">
      <el-card>
        <template #header>
          <div class="card-header">
            <span class="title">固件版本列表</span>
          </div>
        </template>

        <el-table :data="firmwareVersions" :loading="loading" stripe>
          <el-table-column prop="version" label="版本号" width="120" />
          <el-table-column prop="deviceModel" label="适用设备" width="200">
            <template #default="{ row }">
              <el-tag v-for="model in row.deviceModel" :key="model" size="small" style="margin-right: 4px;">
                {{ model }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="releaseDate" label="发布日期" width="120">
            <template #default="{ row }">
              {{ formatDate(row.releaseDate) }}
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="文件大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.fileSize) }}
            </template>
          </el-table-column>
          <el-table-column prop="description" label="描述" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" @click="viewDetails(row)">详情</el-button>
              <el-button size="small" type="primary" @click="updateDevices(row)">更新设备</el-button>
              <el-button size="small" type="danger" @click="deleteVersion(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 上传对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传固件" width="600px">
      <el-form :model="uploadForm" label-width="120px">
        <el-form-item label="版本号">
          <el-input v-model="uploadForm.version" placeholder="请输入版本号，如: 2.1.0" />
        </el-form-item>
        <el-form-item label="适用设备">
          <el-select v-model="uploadForm.deviceModel" multiple placeholder="选择适用设备型号">
            <el-option label="YX-EDU-2024" value="YX-EDU-2024" />
            <el-option label="YX-PRO-2024" value="YX-PRO-2024" />
            <el-option label="YX-ENT-2024" value="YX-ENT-2024" />
          </el-select>
        </el-form-item>
        <el-form-item label="版本描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="固件文件">
          <el-upload
            :auto-upload="false"
            :show-file-list="true"
            accept=".bin,.hex,.fw"
          >
            <el-button>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Upload,
  Refresh,
  Files,
  CircleCheckFilled,
  Warning,
  Monitor,
  Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const loading = ref(false)
const refreshLoading = ref(false)
const uploadDialogVisible = ref(false)

// 固件统计数据
const firmwareStats = ref({
  totalVersions: 12,
  stableVersions: 8,
  testingVersions: 3,
  devicesNeedUpdate: 25
})

// 固件版本列表
const firmwareVersions = ref([
  {
    id: '1',
    version: '2.1.0',
    deviceModel: ['YX-EDU-2024', 'YX-PRO-2024'],
    status: 'stable',
    releaseDate: '2024-02-15',
    fileSize: 15728640,
    description: '修复了字体渲染问题，优化了性能'
  },
  {
    id: '2',
    version: '2.0.5',
    deviceModel: ['YX-EDU-2024'],
    status: 'stable',
    releaseDate: '2024-01-20',
    fileSize: 14680064,
    description: '安全更新，修复了网络连接问题'
  },
  {
    id: '3',
    version: '2.2.0-beta',
    deviceModel: ['YX-PRO-2024', 'YX-ENT-2024'],
    status: 'testing',
    releaseDate: '2024-02-20',
    fileSize: 16777216,
    description: '新增AI优化功能，测试版本'
  }
])

// 上传表单
const uploadForm = ref({
  version: '',
  deviceModel: [],
  description: '',
  file: null
})

// 方法
const showUploadDialog = () => {
  uploadDialogVisible.value = true
}

const refreshData = async () => {
  refreshLoading.value = true
  try {
    // 模拟刷新数据
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('数据刷新成功')
  } finally {
    refreshLoading.value = false
  }
}

const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

const viewDetails = (firmware: any) => {
  ElMessage.info(`查看固件详情: ${firmware.version}`)
}

const updateDevices = (firmware: any) => {
  ElMessage.info(`开始更新设备到版本: ${firmware.version}`)
}

const deleteVersion = (firmware: any) => {
  ElMessage.info(`删除固件版本: ${firmware.version}`)
}

const submitUpload = () => {
  if (!uploadForm.value.version) {
    ElMessage.error('请输入版本号')
    return
  }
  
  ElMessage.success('固件上传成功')
  uploadDialogVisible.value = false
  
  // 重置表单
  uploadForm.value = {
    version: '',
    deviceModel: [],
    description: '',
    file: null
  }
}

const getStatusType = (status: string) => {
  const types: Record<string, any> = {
    stable: 'success',
    testing: 'warning',
    draft: 'info',
    deprecated: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    stable: '稳定版',
    testing: '测试版',
    draft: '草稿',
    deprecated: '已废弃'
  }
  return texts[status] || status
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatFileSize = (bytes: number) => {
  const sizes = ['B', 'KB', 'MB', 'GB']
  if (bytes === 0) return '0 B'
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

onMounted(() => {
  // 页面加载时的初始化
  console.log('固件管理页面已加载')
})
</script>

<style lang="scss" scoped>
.simple-firmware-management {
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

      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 8px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        background: linear-gradient(135deg, #409EFF, #66B1FF);
        color: white;
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

  .firmware-list {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }

      .header-actions {
        display: flex;
        gap: 8px;
      }
    }
  }
}
</style>