<!--
  固件版本列表组件
  功能：显示固件版本列表，支持搜索、筛选、排序
-->
<template>
  <div class="firmware-version-list">


    <!-- 版本列表表格 -->
    <div class="table-section">
      <el-table 
        :data="versionList" 
        v-loading="tableLoading" 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="version" label="版本号" width="120" sortable />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="适用设备" width="200">
          <template #default="{ row }">
            <div class="device-models">
              <el-tag
                v-for="model in row.deviceModel"
                :key="model"
                size="small"
                style="margin: 2px;"
              >
                {{ getModelName(model) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="fileSize" label="文件大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100" sortable />
        <el-table-column prop="releaseDate" label="发布时间" width="150" sortable>
          <template #default="{ row }">
            {{ formatDateTime(row.releaseDate) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button text type="primary" size="small" @click="viewVersionDetail(row)">
              查看详情
            </el-button>
            <el-button text type="success" size="small" @click="downloadFirmware(row)">
              下载
            </el-button>
            <el-dropdown @command="(command) => handleVersionAction(command, row)" trigger="click">
              <el-button text type="primary" size="small">
                更多<el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="compare">
                    版本比较
                  </el-dropdown-item>
                  <el-dropdown-item command="update" v-if="row.status === 'stable'">
                    推送更新
                  </el-dropdown-item>
                  <el-dropdown-item command="edit">
                    编辑信息
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
                    删除版本
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

    <!-- 版本详情对话框 -->
    <FirmwareVersionDetail
      v-model="detailDialogVisible"
      :version-id="selectedVersionId"
      @version-deleted="handleVersionDeleted"
    />
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import {
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import type { FirmwareVersion } from '@/types/firmware'
import { FIRMWARE_STATUS_TEXT, formatFileSize } from '@/types/firmware'
import FirmwareVersionDetail from './FirmwareVersionDetail.vue'

// 响应式数据
const tableLoading = ref(false)
const refreshLoading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 版本数据
const versionList = ref<FirmwareVersion[]>([])
const selectedVersions = ref<FirmwareVersion[]>([])

// 刷新数据
const refreshData = async () => {
  refreshLoading.value = true
  try {
    await loadVersionData()
    ElMessage.success('数据刷新成功')
  } finally {
    setTimeout(() => {
      refreshLoading.value = false
    }, 1000)
  }
}

// 分页处理
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  loadVersionData()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadVersionData()
}

// 选择处理
const handleSelectionChange = (selection: FirmwareVersion[]) => {
  selectedVersions.value = selection
}

// 加载版本数据
const loadVersionData = async () => {
  tableLoading.value = true
  try {
    const response = await mockFirmwareAPI.getFirmwareVersions({
      page: currentPage.value,
      pageSize: pageSize.value
    })
    
    versionList.value = response.data.list
    total.value = response.data.total
  } catch (error) {
    console.error('加载版本数据失败:', error)
    ElMessage.error('加载版本数据失败')
  } finally {
    tableLoading.value = false
  }
}

// 版本详情对话框状态
const detailDialogVisible = ref(false)
const selectedVersionId = ref('')

// 查看版本详情
const viewVersionDetail = (version: FirmwareVersion) => {
  selectedVersionId.value = version.id
  detailDialogVisible.value = true
}

// 下载固件
const downloadFirmware = (version: FirmwareVersion) => {
  ElMessage.success(`开始下载固件：${version.fileName}`)
}

// 版本操作处理
const handleVersionAction = async (command: string, version: FirmwareVersion) => {
  try {
    switch (command) {
      case 'compare':
        ElMessage.info('版本比较功能开发中')
        break
      case 'update':
        ElMessage.info('推送更新功能开发中')
        break
      case 'edit':
        ElMessage.info('编辑版本信息功能开发中')
        break
      case 'delete':
        await ElMessageBox.confirm('确定要删除这个固件版本吗？此操作不可恢复。', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await mockFirmwareAPI.deleteFirmware(version.id)
        ElMessage.success('固件版本已删除')
        loadVersionData()
        break
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('版本操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}



// 工具方法
const getStatusTagType = (status: string) => {
  const types: Record<string, any> = {
    stable: 'success',
    testing: 'warning',
    draft: 'info',
    deprecated: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  return FIRMWARE_STATUS_TEXT[status as keyof typeof FIRMWARE_STATUS_TEXT] || status
}

const getModelName = (model: string) => {
  const names: Record<string, string> = {
    'YX-EDU-2024': '教育版',
    'YX-HOME-2024': '家庭版',
    'YX-PRO-2024': '专业版'
  }
  return names[model] || model
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 处理版本删除
const handleVersionDeleted = (versionId: string) => {
  // 刷新版本列表
  loadVersionData()
  detailDialogVisible.value = false
}

onMounted(() => {
  loadVersionData()
})
</script>

<style lang="scss" scoped>
.firmware-version-list {
  .action-buttons {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;
    margin-bottom: 20px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .table-section {
    background: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    border: 1px solid #f0f0f0;
    padding: 20px;

    .device-models {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

@media (max-width: 768px) {
  .firmware-version-list {
    .action-buttons {
      justify-content: center;
    }
  }
}
</style>