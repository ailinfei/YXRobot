<!--
  固件版本详情组件
  功能：版本信息展示、更新日志、兼容性说明、版本比较、下载删除操作
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`固件版本详情 - ${versionInfo?.version || ''}`"
    width="900px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="firmware-version-detail" v-loading="loading">
      <div v-if="versionInfo" class="detail-content">
        <!-- 版本基本信息 -->
        <div class="info-section">
          <div class="section-header">
            <h3>基本信息</h3>
            <div class="header-actions">
              <el-button type="primary" size="small" @click="downloadFirmware">
                <el-icon><Download /></el-icon>
                下载固件
              </el-button>
              <el-button type="success" size="small" @click="showCompareDialog">
                <el-icon><Operation /></el-icon>
                版本比较
              </el-button>
              <el-dropdown @command="handleAction" trigger="click">
                <el-button size="small">
                  更多操作<el-icon class="el-icon--right"><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">
                      <el-icon><Edit /></el-icon>
                      编辑信息
                    </el-dropdown-item>
                    <el-dropdown-item command="copy">
                      <el-icon><CopyDocument /></el-icon>
                      复制版本
                    </el-dropdown-item>
                    <el-dropdown-item command="update" v-if="versionInfo.status === 'stable'">
                      <el-icon><Promotion /></el-icon>
                      推送更新
                    </el-dropdown-item>
                    <el-dropdown-item command="delete" divided>
                      <el-icon><Delete /></el-icon>
                      删除版本
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>

          <div class="info-grid">
            <div class="info-item">
              <label>版本号</label>
              <div class="value">
                <el-tag size="large" :type="getStatusTagType(versionInfo.status)">
                  {{ versionInfo.version }}
                </el-tag>
              </div>
            </div>
            
            <div class="info-item">
              <label>状态</label>
              <div class="value">
                <el-tag :type="getStatusTagType(versionInfo.status)">
                  {{ getStatusText(versionInfo.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="info-item">
              <label>文件大小</label>
              <div class="value">{{ formatFileSize(versionInfo.fileSize) }}</div>
            </div>
            
            <div class="info-item">
              <label>下载次数</label>
              <div class="value">{{ versionInfo.downloadCount.toLocaleString() }}</div>
            </div>
            
            <div class="info-item">
              <label>发布时间</label>
              <div class="value">{{ formatDateTime(versionInfo.releaseDate) }}</div>
            </div>
            
            <div class="info-item">
              <label>创建者</label>
              <div class="value">{{ versionInfo.createdBy }}</div>
            </div>
            
            <div class="info-item full-width">
              <label>文件名</label>
              <div class="value">
                <el-input :value="versionInfo.fileName" readonly>
                  <template #append>
                    <el-button @click="copyToClipboard(versionInfo.fileName)">
                      <el-icon><CopyDocument /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </div>
            </div>
            
            <div class="info-item full-width">
              <label>校验和</label>
              <div class="value">
                <el-input :value="versionInfo.checksum" readonly>
                  <template #append>
                    <el-button @click="copyToClipboard(versionInfo.checksum)">
                      <el-icon><CopyDocument /></el-icon>
                    </el-button>
                  </template>
                </el-input>
              </div>
            </div>
          </div>
        </div>

        <!-- 适用设备 -->
        <div class="info-section">
          <div class="section-header">
            <h3>适用设备</h3>
          </div>
          <div class="device-models">
            <el-tag
              v-for="model in versionInfo.deviceModel"
              :key="model"
              size="large"
              class="model-tag"
            >
              <el-icon><Monitor /></el-icon>
              {{ getModelName(model) }}
            </el-tag>
          </div>
        </div>

        <!-- 版本描述 -->
        <div class="info-section">
          <div class="section-header">
            <h3>版本描述</h3>
          </div>
          <div class="description-content">
            <p>{{ versionInfo.description }}</p>
          </div>
        </div>

        <!-- 更新日志 -->
        <div class="info-section">
          <div class="section-header">
            <h3>更新日志</h3>
          </div>
          <div class="changelog-content">
            <ul class="changelog-list">
              <li v-for="(item, index) in versionInfo.changelog" :key="index" class="changelog-item">
                <el-icon class="changelog-icon"><CircleCheckFilled /></el-icon>
                <span>{{ item }}</span>
              </li>
            </ul>
          </div>
        </div>

        <!-- 兼容性信息 -->
        <div class="info-section">
          <div class="section-header">
            <h3>兼容性信息</h3>
          </div>
          <div class="compatibility-content">
            <div class="compatibility-list">
              <div
                v-for="(item, index) in versionInfo.compatibility"
                :key="index"
                class="compatibility-item"
              >
                <el-icon class="compatibility-icon"><CircleCheckFilled /></el-icon>
                <span>{{ item }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 已知问题 -->
        <div class="info-section" v-if="versionInfo.knownIssues.length > 0">
          <div class="section-header">
            <h3>已知问题</h3>
          </div>
          <div class="issues-content">
            <el-alert
              v-for="(issue, index) in versionInfo.knownIssues"
              :key="index"
              :title="issue"
              type="warning"
              show-icon
              :closable="false"
              class="issue-alert"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 版本比较对话框 -->
    <FirmwareVersionCompare
      v-model="compareDialogVisible"
      :current-version="versionInfo"
      @compare-complete="handleCompareComplete"
    />

    <!-- 底部操作按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="downloadFirmware">
          <el-icon><Download /></el-icon>
          下载固件
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import {
  Download,
  Operation,
  ArrowDown,
  Edit,
  CopyDocument,
  Promotion,
  Delete,
  Monitor,
  CircleCheckFilled
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FirmwareVersion } from '@/types/firmware'
import { FIRMWARE_STATUS_TEXT, formatFileSize } from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'
import FirmwareVersionCompare from './FirmwareVersionCompare.vue'

// Props
interface Props {
  modelValue: boolean
  versionId?: string
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'version-updated': []
  'version-deleted': [id: string]
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const loading = ref(false)
const versionInfo = ref<FirmwareVersion | null>(null)
const compareDialogVisible = ref(false)

// 加载版本详情
const loadVersionDetail = async () => {
  if (!props.versionId) return

  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 从mock数据中获取版本信息
    const response = await mockFirmwareAPI.getFirmwareVersions({
      page: 1,
      pageSize: 100
    })
    
    const version = response.data.list.find(v => v.id === props.versionId)
    if (version) {
      versionInfo.value = version
    } else {
      ElMessage.error('未找到指定的固件版本')
      handleClose()
    }
  } catch (error) {
    console.error('加载版本详情失败:', error)
    ElMessage.error('加载版本详情失败')
  } finally {
    loading.value = false
  }
}

// 下载固件
const downloadFirmware = () => {
  if (!versionInfo.value) return

  ElMessage.success(`开始下载固件：${versionInfo.value.fileName}`)
  
  // 模拟下载
  const link = document.createElement('a')
  link.href = '#'
  link.download = versionInfo.value.fileName
  link.click()
}

// 显示版本比较对话框
const showCompareDialog = () => {
  compareDialogVisible.value = true
}

// 处理版本比较完成
const handleCompareComplete = (result: any) => {
  ElMessage.success('版本比较完成')
}

// 处理操作命令
const handleAction = async (command: string) => {
  if (!versionInfo.value) return

  try {
    switch (command) {
      case 'edit':
        ElMessage.info('编辑版本信息功能开发中')
        break
      case 'copy':
        ElMessage.info('复制版本功能开发中')
        break
      case 'update':
        ElMessage.info('推送更新功能开发中')
        break
      case 'delete':
        await ElMessageBox.confirm(
          '确定要删除这个固件版本吗？此操作不可恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        await mockFirmwareAPI.deleteFirmware(versionInfo.value.id)
        ElMessage.success('固件版本已删除')
        emit('version-deleted', versionInfo.value.id)
        handleClose()
        break
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  }
}

// 复制到剪贴板
const copyToClipboard = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('已复制到剪贴板')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败')
  }
}

// 关闭对话框
const handleClose = () => {
  emit('update:modelValue', false)
  versionInfo.value = null
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
    'YX-PRO-2024': '专业版',
    'YX-MINI-2024': '迷你版'
  }
  return names[model] || model
}

const formatDateTime = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 监听版本ID变化
watch(() => props.versionId, (newId) => {
  if (newId && dialogVisible.value) {
    loadVersionDetail()
  }
})

// 监听对话框显示状态
watch(dialogVisible, (visible) => {
  if (visible && props.versionId) {
    loadVersionDetail()
  }
})
</script>

<style lang="scss" scoped>
.firmware-version-detail {
  .detail-content {
    .info-section {
      margin-bottom: 32px;

      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 16px;
        padding-bottom: 8px;
        border-bottom: 1px solid #ebeef5;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }

        .header-actions {
          display: flex;
          gap: 8px;
        }
      }

      .info-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 16px;

        .info-item {
          display: flex;
          flex-direction: column;
          gap: 8px;

          &.full-width {
            grid-column: 1 / -1;
          }

          label {
            font-size: 14px;
            font-weight: 500;
            color: #606266;
          }

          .value {
            font-size: 14px;
            color: #303133;
          }
        }
      }

      .device-models {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;

        .model-tag {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 12px;
          font-size: 14px;
        }
      }

      .description-content {
        p {
          margin: 0;
          font-size: 14px;
          line-height: 1.6;
          color: #606266;
          background: #f8f9fa;
          padding: 16px;
          border-radius: 6px;
          border-left: 4px solid #409eff;
        }
      }

      .changelog-content {
        .changelog-list {
          margin: 0;
          padding: 0;
          list-style: none;

          .changelog-item {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            padding: 8px 0;
            font-size: 14px;
            line-height: 1.5;
            color: #606266;

            .changelog-icon {
              color: #67c23a;
              margin-top: 2px;
              flex-shrink: 0;
            }
          }
        }
      }

      .compatibility-content {
        .compatibility-list {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .compatibility-item {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background: #f0f9ff;
            border-radius: 6px;
            font-size: 14px;
            color: #606266;

            .compatibility-icon {
              color: #409eff;
              flex-shrink: 0;
            }
          }
        }
      }

      .issues-content {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .issue-alert {
          margin: 0;
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
  .firmware-version-detail {
    .detail-content {
      .info-section {
        .section-header {
          flex-direction: column;
          gap: 12px;
          align-items: stretch;

          .header-actions {
            justify-content: center;
          }
        }

        .info-grid {
          grid-template-columns: 1fr;

          .info-item {
            &.full-width {
              grid-column: 1;
            }
          }
        }

        .device-models {
          justify-content: center;
        }
      }
    }

    .dialog-footer {
      flex-direction: column-reverse;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>