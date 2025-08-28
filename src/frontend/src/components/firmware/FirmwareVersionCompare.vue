<!--
  固件版本比较组件
  功能：多版本差异对比、功能对比、兼容性对比
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="固件版本比较"
    width="1200px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="firmware-version-compare" v-loading="loading">
      <!-- 版本选择 -->
      <div class="version-selector">
        <div class="selector-item">
          <label>基准版本</label>
          <el-select
            v-model="baseVersionId"
            placeholder="选择基准版本"
            @change="handleVersionChange"
            style="width: 100%"
          >
            <el-option
              v-for="version in availableVersions"
              :key="version.id"
              :label="`${version.version} (${getStatusText(version.status)})`"
              :value="version.id"
            />
          </el-select>
        </div>

        <div class="selector-item">
          <label>对比版本</label>
          <el-select
            v-model="compareVersionId"
            placeholder="选择对比版本"
            @change="handleVersionChange"
            style="width: 100%"
          >
            <el-option
              v-for="version in availableVersions"
              :key="version.id"
              :label="`${version.version} (${getStatusText(version.status)})`"
              :value="version.id"
              :disabled="version.id === baseVersionId"
            />
          </el-select>
        </div>

        <div class="selector-actions">
          <el-button
            type="primary"
            @click="performComparison"
            :disabled="!baseVersionId || !compareVersionId"
            :loading="comparing"
          >
            <el-icon><Operation /></el-icon>
            开始比较
          </el-button>
          <el-button @click="swapVersions" :disabled="!baseVersionId || !compareVersionId">
            <el-icon><Sort /></el-icon>
            交换版本
          </el-button>
        </div>
      </div>

      <!-- 比较结果 -->
      <div v-if="comparisonResult" class="comparison-result">
        <!-- 基本信息对比 -->
        <div class="comparison-section">
          <div class="section-header">
            <h3>基本信息对比</h3>
          </div>
          <div class="comparison-table">
            <table class="compare-table">
              <thead>
                <tr>
                  <th class="property-column">属性</th>
                  <th class="version-column base">
                    {{ comparisonResult.version1.version }}
                    <el-tag :type="getStatusTagType(comparisonResult.version1.status)" size="small">
                      {{ getStatusText(comparisonResult.version1.status) }}
                    </el-tag>
                  </th>
                  <th class="version-column compare">
                    {{ comparisonResult.version2.version }}
                    <el-tag :type="getStatusTagType(comparisonResult.version2.status)" size="small">
                      {{ getStatusText(comparisonResult.version2.status) }}
                    </el-tag>
                  </th>
                  <th class="diff-column">差异</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td class="property-name">发布时间</td>
                  <td class="version-value">{{ formatDateTime(comparisonResult.version1.releaseDate) }}</td>
                  <td class="version-value">{{ formatDateTime(comparisonResult.version2.releaseDate) }}</td>
                  <td class="diff-value">
                    <el-tag :type="getTimeDiffType(comparisonResult.version1.releaseDate, comparisonResult.version2.releaseDate)" size="small">
                      {{ getTimeDiff(comparisonResult.version1.releaseDate, comparisonResult.version2.releaseDate) }}
                    </el-tag>
                  </td>
                </tr>
                <tr>
                  <td class="property-name">文件大小</td>
                  <td class="version-value">{{ formatFileSize(comparisonResult.version1.fileSize) }}</td>
                  <td class="version-value">{{ formatFileSize(comparisonResult.version2.fileSize) }}</td>
                  <td class="diff-value">
                    <el-tag :type="getSizeDiffType(comparisonResult.differences.fileSize)" size="small">
                      {{ formatSizeDiff(comparisonResult.differences.fileSize) }}
                    </el-tag>
                  </td>
                </tr>
                <tr>
                  <td class="property-name">下载次数</td>
                  <td class="version-value">{{ comparisonResult.version1.downloadCount.toLocaleString() }}</td>
                  <td class="version-value">{{ comparisonResult.version2.downloadCount.toLocaleString() }}</td>
                  <td class="diff-value">
                    <el-tag :type="getCountDiffType(comparisonResult.version1.downloadCount, comparisonResult.version2.downloadCount)" size="small">
                      {{ getCountDiff(comparisonResult.version1.downloadCount, comparisonResult.version2.downloadCount) }}
                    </el-tag>
                  </td>
                </tr>
                <tr>
                  <td class="property-name">适用设备</td>
                  <td class="version-value">
                    <div class="device-list">
                      <el-tag v-for="model in comparisonResult.version1.deviceModel" :key="model" size="small">
                        {{ getModelName(model) }}
                      </el-tag>
                    </div>
                  </td>
                  <td class="version-value">
                    <div class="device-list">
                      <el-tag v-for="model in comparisonResult.version2.deviceModel" :key="model" size="small">
                        {{ getModelName(model) }}
                      </el-tag>
                    </div>
                  </td>
                  <td class="diff-value">
                    <div class="device-diff">
                      <div v-if="getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).added.length > 0">
                        <span class="diff-label">新增:</span>
                        <el-tag v-for="model in getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).added" :key="model" type="success" size="small">
                          {{ getModelName(model) }}
                        </el-tag>
                      </div>
                      <div v-if="getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).removed.length > 0">
                        <span class="diff-label">移除:</span>
                        <el-tag v-for="model in getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).removed" :key="model" type="danger" size="small">
                          {{ getModelName(model) }}
                        </el-tag>
                      </div>
                      <div v-if="getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).added.length === 0 && getDeviceModelDiff(comparisonResult.version1.deviceModel, comparisonResult.version2.deviceModel).removed.length === 0">
                        <el-tag type="info" size="small">无变化</el-tag>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- 更新日志对比 -->
        <div class="comparison-section">
          <div class="section-header">
            <h3>更新日志对比</h3>
          </div>
          <div class="changelog-comparison">
            <div class="changelog-column">
              <h4>{{ comparisonResult.version1.version }}</h4>
              <ul class="changelog-list">
                <li v-for="(item, index) in comparisonResult.version1.changelog" :key="index" class="changelog-item">
                  <el-icon class="changelog-icon"><CircleCheckFilled /></el-icon>
                  <span>{{ item }}</span>
                </li>
              </ul>
            </div>
            <div class="changelog-column">
              <h4>{{ comparisonResult.version2.version }}</h4>
              <ul class="changelog-list">
                <li v-for="(item, index) in comparisonResult.version2.changelog" :key="index" class="changelog-item">
                  <el-icon class="changelog-icon"><CircleCheckFilled /></el-icon>
                  <span>{{ item }}</span>
                </li>
              </ul>
            </div>
          </div>
          <div class="changelog-diff">
            <h4>差异分析</h4>
            <div class="diff-items">
              <div v-if="comparisonResult.differences.changelog.length > 0">
                <div v-for="(item, index) in comparisonResult.differences.changelog" :key="index" class="diff-item">
                  <el-icon class="diff-icon"><Plus /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
              <div v-else class="no-diff">
                <el-icon><InfoFilled /></el-icon>
                <span>更新日志无显著差异</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 兼容性对比 -->
        <div class="comparison-section">
          <div class="section-header">
            <h3>兼容性对比</h3>
          </div>
          <div class="compatibility-comparison">
            <div class="compatibility-column">
              <h4>{{ comparisonResult.version1.version }}</h4>
              <div class="compatibility-list">
                <div v-for="(item, index) in comparisonResult.version1.compatibility" :key="index" class="compatibility-item">
                  <el-icon class="compatibility-icon"><CircleCheckFilled /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
            </div>
            <div class="compatibility-column">
              <h4>{{ comparisonResult.version2.version }}</h4>
              <div class="compatibility-list">
                <div v-for="(item, index) in comparisonResult.version2.compatibility" :key="index" class="compatibility-item">
                  <el-icon class="compatibility-icon"><CircleCheckFilled /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
            </div>
          </div>
          <div class="compatibility-diff">
            <h4>兼容性变化</h4>
            <div class="diff-items">
              <div v-if="comparisonResult.differences.compatibility.length > 0">
                <div v-for="(item, index) in comparisonResult.differences.compatibility" :key="index" class="diff-item">
                  <el-icon class="diff-icon"><Plus /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
              <div v-else class="no-diff">
                <el-icon><InfoFilled /></el-icon>
                <span>兼容性信息无变化</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 已知问题对比 -->
        <div class="comparison-section" v-if="comparisonResult.version1.knownIssues.length > 0 || comparisonResult.version2.knownIssues.length > 0">
          <div class="section-header">
            <h3>已知问题对比</h3>
          </div>
          <div class="issues-comparison">
            <div class="issues-column">
              <h4>{{ comparisonResult.version1.version }}</h4>
              <div class="issues-list">
                <el-alert
                  v-for="(issue, index) in comparisonResult.version1.knownIssues"
                  :key="index"
                  :title="issue"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="issue-alert"
                />
                <div v-if="comparisonResult.version1.knownIssues.length === 0" class="no-issues">
                  <el-icon><CircleCheckFilled /></el-icon>
                  <span>无已知问题</span>
                </div>
              </div>
            </div>
            <div class="issues-column">
              <h4>{{ comparisonResult.version2.version }}</h4>
              <div class="issues-list">
                <el-alert
                  v-for="(issue, index) in comparisonResult.version2.knownIssues"
                  :key="index"
                  :title="issue"
                  type="warning"
                  show-icon
                  :closable="false"
                  class="issue-alert"
                />
                <div v-if="comparisonResult.version2.knownIssues.length === 0" class="no-issues">
                  <el-icon><CircleCheckFilled /></el-icon>
                  <span>无已知问题</span>
                </div>
              </div>
            </div>
          </div>
          <div class="issues-diff">
            <h4>问题变化</h4>
            <div class="diff-items">
              <div v-if="comparisonResult.differences.knownIssues.length > 0">
                <div v-for="(item, index) in comparisonResult.differences.knownIssues" :key="index" class="diff-item">
                  <el-icon class="diff-icon"><Plus /></el-icon>
                  <span>{{ item }}</span>
                </div>
              </div>
              <div v-else class="no-diff">
                <el-icon><InfoFilled /></el-icon>
                <span>已知问题无变化</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button v-if="comparisonResult" type="primary" @click="exportComparison">
          <el-icon><Download /></el-icon>
          导出比较结果
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import {
  Operation,
  Sort,
  Download,
  CircleCheckFilled,
  Plus,
  InfoFilled
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FirmwareVersion, FirmwareComparison } from '@/types/firmware'
import { FIRMWARE_STATUS_TEXT, formatFileSize } from '@/types/firmware'
import { mockFirmwareAPI } from '@/api/mock/firmware'

// Props
interface Props {
  modelValue: boolean
  currentVersion?: FirmwareVersion
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'compare-complete': [result: FirmwareComparison]
}>()

// 响应式数据
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const loading = ref(false)
const comparing = ref(false)
const availableVersions = ref<FirmwareVersion[]>([])
const baseVersionId = ref('')
const compareVersionId = ref('')
const comparisonResult = ref<FirmwareComparison | null>(null)

// 加载可用版本
const loadAvailableVersions = async () => {
  loading.value = true
  try {
    const response = await mockFirmwareAPI.getFirmwareVersions({
      page: 1,
      pageSize: 100
    })
    availableVersions.value = response.data.list
    
    // 如果有当前版本，设置为基准版本
    if (props.currentVersion) {
      baseVersionId.value = props.currentVersion.id
    }
  } catch (error) {
    console.error('加载版本列表失败:', error)
    ElMessage.error('加载版本列表失败')
  } finally {
    loading.value = false
  }
}

// 处理版本选择变化
const handleVersionChange = () => {
  comparisonResult.value = null
}

// 交换版本
const swapVersions = () => {
  const temp = baseVersionId.value
  baseVersionId.value = compareVersionId.value
  compareVersionId.value = temp
  comparisonResult.value = null
}

// 执行版本比较
const performComparison = async () => {
  if (!baseVersionId.value || !compareVersionId.value) return

  comparing.value = true
  try {
    const baseVersion = availableVersions.value.find(v => v.id === baseVersionId.value)
    const compareVersion = availableVersions.value.find(v => v.id === compareVersionId.value)

    if (!baseVersion || !compareVersion) {
      ElMessage.error('版本信息不完整')
      return
    }

    // 模拟比较过程
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 生成比较结果
    const result: FirmwareComparison = {
      version1: baseVersion,
      version2: compareVersion,
      differences: {
        changelog: compareVersion.changelog.filter(item => !baseVersion.changelog.includes(item)),
        compatibility: compareVersion.compatibility.filter(item => !baseVersion.compatibility.includes(item)),
        knownIssues: compareVersion.knownIssues.filter(item => !baseVersion.knownIssues.includes(item)),
        fileSize: compareVersion.fileSize - baseVersion.fileSize
      }
    }

    comparisonResult.value = result
    emit('compare-complete', result)
    ElMessage.success('版本比较完成')
  } catch (error) {
    console.error('版本比较失败:', error)
    ElMessage.error('版本比较失败')
  } finally {
    comparing.value = false
  }
}

// 导出比较结果
const exportComparison = () => {
  if (!comparisonResult.value) return

  ElMessage.success('比较结果导出功能开发中')
}

// 关闭对话框
const handleClose = () => {
  emit('update:modelValue', false)
  comparisonResult.value = null
  baseVersionId.value = ''
  compareVersionId.value = ''
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
    minute: '2-digit'
  })
}

const getTimeDiff = (date1: string, date2: string) => {
  const diff = new Date(date2).getTime() - new Date(date1).getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (days === 0) return '同一天'
  if (days > 0) return `晚 ${days} 天`
  return `早 ${Math.abs(days)} 天`
}

const getTimeDiffType = (date1: string, date2: string) => {
  const diff = new Date(date2).getTime() - new Date(date1).getTime()
  if (diff === 0) return 'info'
  return diff > 0 ? 'success' : 'warning'
}

const formatSizeDiff = (sizeDiff: number) => {
  if (sizeDiff === 0) return '无变化'
  const absSize = Math.abs(sizeDiff)
  const sign = sizeDiff > 0 ? '+' : '-'
  return `${sign}${formatFileSize(absSize)}`
}

const getSizeDiffType = (sizeDiff: number) => {
  if (sizeDiff === 0) return 'info'
  return sizeDiff > 0 ? 'warning' : 'success'
}

const getCountDiff = (count1: number, count2: number) => {
  const diff = count2 - count1
  if (diff === 0) return '无变化'
  const sign = diff > 0 ? '+' : ''
  return `${sign}${diff.toLocaleString()}`
}

const getCountDiffType = (count1: number, count2: number) => {
  const diff = count2 - count1
  if (diff === 0) return 'info'
  return diff > 0 ? 'success' : 'warning'
}

const getDeviceModelDiff = (models1: string[], models2: string[]) => {
  const added = models2.filter(model => !models1.includes(model))
  const removed = models1.filter(model => !models2.includes(model))
  return { added, removed }
}

// 生命周期
onMounted(() => {
  if (dialogVisible.value) {
    loadAvailableVersions()
  }
})

// 监听对话框显示状态
watch(dialogVisible, (visible) => {
  if (visible) {
    loadAvailableVersions()
  }
})
</script>

<style lang="scss" scoped>
.firmware-version-compare {
  .version-selector {
    display: grid;
    grid-template-columns: 1fr 1fr auto;
    gap: 16px;
    align-items: end;
    margin-bottom: 32px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;

    .selector-item {
      display: flex;
      flex-direction: column;
      gap: 8px;

      label {
        font-size: 14px;
        font-weight: 500;
        color: #606266;
      }
    }

    .selector-actions {
      display: flex;
      gap: 8px;
    }
  }

  .comparison-result {
    .comparison-section {
      margin-bottom: 32px;

      .section-header {
        margin-bottom: 16px;
        padding-bottom: 8px;
        border-bottom: 1px solid #ebeef5;

        h3 {
          margin: 0;
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }

      .comparison-table {
        .compare-table {
          width: 100%;
          border-collapse: collapse;
          border: 1px solid #ebeef5;
          border-radius: 6px;
          overflow: hidden;

          th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ebeef5;
          }

          th {
            background: #f8f9fa;
            font-weight: 600;
            color: #303133;

            &.property-column {
              width: 120px;
            }

            &.version-column {
              width: 300px;

              &.base {
                background: #f0f9ff;
              }

              &.compare {
                background: #f0f9ff;
              }
            }

            &.diff-column {
              width: 200px;
              background: #fff7e6;
            }
          }

          td {
            &.property-name {
              font-weight: 500;
              color: #606266;
              background: #fafafa;
            }

            &.version-value {
              color: #303133;
            }

            &.diff-value {
              background: #fffbf0;
            }
          }

          .device-list {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
          }

          .device-diff {
            display: flex;
            flex-direction: column;
            gap: 4px;

            .diff-label {
              font-size: 12px;
              color: #909399;
              margin-right: 4px;
            }
          }
        }
      }

      .changelog-comparison,
      .compatibility-comparison,
      .issues-comparison {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin-bottom: 20px;

        .changelog-column,
        .compatibility-column,
        .issues-column {
          h4 {
            margin: 0 0 12px 0;
            font-size: 14px;
            font-weight: 600;
            color: #303133;
            padding: 8px 12px;
            background: #f0f9ff;
            border-radius: 4px;
          }

          .changelog-list {
            margin: 0;
            padding: 0;
            list-style: none;

            .changelog-item {
              display: flex;
              align-items: flex-start;
              gap: 8px;
              padding: 6px 0;
              font-size: 14px;
              line-height: 1.4;
              color: #606266;

              .changelog-icon {
                color: #67c23a;
                margin-top: 2px;
                flex-shrink: 0;
              }
            }
          }

          .compatibility-list {
            display: flex;
            flex-direction: column;
            gap: 6px;

            .compatibility-item {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 6px 10px;
              background: #f0f9ff;
              border-radius: 4px;
              font-size: 14px;
              color: #606266;

              .compatibility-icon {
                color: #409eff;
                flex-shrink: 0;
              }
            }
          }

          .issues-list {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .issue-alert {
              margin: 0;
            }

            .no-issues {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 12px;
              background: #f0f9ff;
              border-radius: 4px;
              color: #67c23a;
              font-size: 14px;
            }
          }
        }
      }

      .changelog-diff,
      .compatibility-diff,
      .issues-diff {
        h4 {
          margin: 0 0 12px 0;
          font-size: 14px;
          font-weight: 600;
          color: #303133;
        }

        .diff-items {
          .diff-item {
            display: flex;
            align-items: flex-start;
            gap: 8px;
            padding: 8px 12px;
            margin-bottom: 6px;
            background: #f0f9ff;
            border-left: 3px solid #409eff;
            border-radius: 4px;
            font-size: 14px;
            color: #606266;

            .diff-icon {
              color: #409eff;
              margin-top: 2px;
              flex-shrink: 0;
            }
          }

          .no-diff {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 12px;
            background: #f8f9fa;
            border-radius: 4px;
            color: #909399;
            font-size: 14px;
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

@media (max-width: 1024px) {
  .firmware-version-compare {
    .version-selector {
      grid-template-columns: 1fr;
      gap: 16px;

      .selector-actions {
        justify-content: center;
      }
    }

    .comparison-result {
      .comparison-section {
        .comparison-table {
          overflow-x: auto;
        }

        .changelog-comparison,
        .compatibility-comparison,
        .issues-comparison {
          grid-template-columns: 1fr;
          gap: 16px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .firmware-version-compare {
    .dialog-footer {
      flex-direction: column-reverse;

      .el-button {
        width: 100%;
      }
    }
  }
}
</style>