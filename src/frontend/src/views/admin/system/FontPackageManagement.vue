<!--
  AI字体包管理界面
  功能：字体包列表、样本上传、AI生成监控、结果预览、发布分发
  需求：10.9-10.20 - AI字体包管理与AI字体生成
-->
<template>
  <div class="font-package-management">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>AI字体包管理</h1>
      <p>管理字体包、样本上传、AI生成和发布分发</p>
    </div>

    <!-- 字体包统计概览 -->
    <div class="stats-section">
      <el-row :gutter="24" v-loading="loading">
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66B1FF);">
              <el-icon size="24"><Document /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.totalPackages }}</div>
              <div class="stat-title">字体包总数</div>
              <div class="stat-desc">包含所有状态的字体包</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #85CE61);">
              <el-icon size="24"><Check /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.publishedPackages }}</div>
              <div class="stat-title">已发布</div>
              <div class="stat-desc">可供下载使用的字体包</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #EEBE77);">
              <el-icon size="24"><Loading /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.generatingPackages }}</div>
              <div class="stat-title">生成中</div>
              <div class="stat-desc">AI正在训练生成的字体包</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #F78989);">
              <el-icon size="24"><Picture /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.totalSamples.toLocaleString() }}</div>
              <div class="stat-title">样本总数</div>
              <div class="stat-desc">用于训练的字符样本数量</div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 额外统计信息 -->
      <el-row :gutter="24" style="margin-top: 16px;">
        <el-col :xs="24" :sm="8" :md="8" :lg="8">
          <div class="stat-card secondary">
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.averageAccuracy.toFixed(1) }}%</div>
              <div class="stat-title">平均准确率</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8" :md="8" :lg="8">
          <div class="stat-card secondary">
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.recentActivity.newPackagesThisWeek }}</div>
              <div class="stat-title">本周新增</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8" :md="8" :lg="8">
          <div class="stat-card secondary">
            <div class="stat-content">
              <div class="stat-value">{{ fontStats.recentActivity.totalDownloadsThisWeek.toLocaleString() }}</div>
              <div class="stat-title">本周下载</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 字体包列表 -->
    <div class="table-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>字体包列表</span>
            <div class="header-actions">
              <el-button-group>
                <el-button type="primary" @click="showCreateDialog">
                  <el-icon><Plus /></el-icon>
                  {{ useWizard ? '智能创建' : '快速创建' }}
                </el-button>
                <el-button 
                  type="primary" 
                  @click="useWizard = !useWizard"
                  :title="useWizard ? '切换到传统模式' : '切换到智能向导'"
                >
                  <el-icon v-if="useWizard"><Star /></el-icon>
                  <el-icon v-else><Document /></el-icon>
                </el-button>
              </el-button-group>
            </div>
          </div>
        </template>

  

        <!-- 数据表格 -->
        <DataTable
          :data="fontPackageList"
          :columns="columns"
          :loading="loading"
          :pagination="pagination"
          @page-change="handlePageChange"
          @size-change="handleSizeChange"
        >
          <!-- 字体类型插槽 -->
          <template #font_type="{ row }">
            <div class="font-type-cell">
              <el-tag :type="getFontTypeTagType(row.fontType || row.font_type)" size="small">
                {{ getFontTypeName(row.fontType || row.font_type) }}
              </el-tag>
              <div class="difficulty-info">
                <span class="difficulty-level">
                  难度: {{ getDifficultyText(row.difficulty) }}
                </span>
              </div>
            </div>
          </template>

          <!-- 状态插槽 -->
          <template #status="{ row }">
            <div class="status-cell">
              <div class="status-info">
                <el-tag 
                  :type="getStatusTagType(row.status)" 
                  :effect="row.status === 'training' ? 'light' : 'dark'"
                  size="small"
                >
                  <el-icon v-if="row.status === 'training'" class="is-loading">
                    <Loading />
                  </el-icon>
                  <el-icon v-else-if="row.status === 'published'">
                    <Check />
                  </el-icon>
                  <el-icon v-else-if="row.status === 'failed'">
                    <Close />
                  </el-icon>
                  <el-icon v-else-if="row.status === 'completed'">
                    <CircleCheck />
                  </el-icon>
                  <el-icon v-else>
                    <Document />
                  </el-icon>
                  {{ getStatusName(row.status) }}
                </el-tag>
                <span v-if="row.qualityScore > 0" class="quality-score">
                  质量: {{ row.qualityScore }}分
                </span>
              </div>
              <el-progress
                v-if="row.status === 'training' && row.progress > 0"
                :percentage="row.progress"
                :stroke-width="6"
                :show-text="true"
                :format="(percentage) => `${percentage}%`"
                :color="getProgressColor(row.progress)"
                class="progress-bar animated-progress"
              />
              <div v-else-if="row.status === 'training' && row.progress === 0" class="progress-placeholder">
       
              <div v-if="row.status === 'training'" class="generation-info">
                <span class="time-info">
                  预计剩余: {{ getEstimatedTime(row) }}
                </span>
              </div>
            </div>
           </div>
          </template>

          <!-- 操作插槽 -->
          <template #actions="{ row }">
            <el-button text type="primary" size="small" @click="viewFontDetail(row)">
              查看详情
            </el-button>
            <el-button 
              text 
              type="primary" 
              size="small" 
              @click="uploadSamples(row)"
              :disabled="row.status === 'published'"
            >
              上传样本
            </el-button>
            <el-button 
              text 
              type="success" 
              size="small" 
              @click="startGeneration(row)"
              :disabled="!canStartGeneration(row)"
            >
              开始生成
            </el-button>
            <el-button 
              text 
              type="warning" 
              size="small" 
              @click="publishFont(row)"
              :disabled="row.status !== 'completed'"
            >
              发布
            </el-button>
            <el-button text type="danger" size="small" @click="deleteFont(row)">
              删除
            </el-button>
          </template>
        </DataTable>
      </el-card>
    </div>

    <!-- 字体包详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`字体包详情 - ${currentFontPackage?.name || ''}`"
      width="80%"
      :before-close="handleDetailClose"
      class="font-detail-dialog"
    >
      <div v-if="currentFontPackage" class="font-detail-content">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="basic">
            <div class="basic-info">
              <el-row :gutter="24">
                <el-col :span="12">
                  <div class="info-section">
                    <h3>基础信息</h3>
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="字体包名称">
                        {{ currentFontPackage.name }}
                      </el-descriptions-item>
                      <el-descriptions-item label="字体类型">
                        <el-tag :type="getFontTypeTagType(currentFontPackage.fontType)">
                          {{ getFontTypeName(currentFontPackage.fontType) }}
                        </el-tag>
                      </el-descriptions-item>
                      <el-descriptions-item label="版本号">
                        {{ currentFontPackage.version }}
                      </el-descriptions-item>
                      <el-descriptions-item label="状态">
                        <el-tag :type="getStatusTagType(currentFontPackage.status)">
                          {{ getStatusName(currentFontPackage.status) }}
                        </el-tag>
                      </el-descriptions-item>
                      <el-descriptions-item label="难度等级">
                        {{ getDifficultyText(currentFontPackage.difficulty) }}
                      </el-descriptions-item>
                      <el-descriptions-item label="质量评分">
                        <el-rate 
                          :model-value="currentFontPackage.qualityScore / 20" 
                          disabled 
                          show-score 
                          text-color="#ff9900"
                        />
                        <span style="margin-left: 8px;">{{ currentFontPackage.qualityScore }}分</span>
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="info-section">
                    <h3>生成统计</h3>
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="样本数量">
                        {{ currentFontPackage.sampleCount }}个
                      </el-descriptions-item>
                      <el-descriptions-item label="生成数量">
                        {{ currentFontPackage.generatedCount }}个
                      </el-descriptions-item>
                      <el-descriptions-item label="生成进度">
                        <el-progress 
                          :percentage="currentFontPackage.progress" 
                          :stroke-width="8"
                          :show-text="true"
                        />
                      </el-descriptions-item>
                      <el-descriptions-item label="准确率">
                        {{ currentFontPackage.accuracy }}%
                      </el-descriptions-item>
                      <el-descriptions-item label="文件大小">
                        {{ currentFontPackage.fileSize }}MB
                      </el-descriptions-item>
                      <el-descriptions-item label="下载次数">
                        {{ currentFontPackage.downloadCount }}次
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
              </el-row>
              
              <el-row :gutter="24" style="margin-top: 20px;">
                <el-col :span="24">
                  <div class="info-section">
                    <h3>描述信息</h3>
                    <p class="description-text">{{ currentFontPackage.description }}</p>
                  </div>
                </el-col>
              </el-row>

              <el-row :gutter="24" style="margin-top: 20px;">
                <el-col :span="12">
                  <div class="info-section">
                    <h3>时间信息</h3>
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="创建时间">
                        {{ formatDateTime(currentFontPackage.createdAt) }}
                      </el-descriptions-item>
                      <el-descriptions-item label="更新时间">
                        {{ formatDateTime(currentFontPackage.updatedAt) }}
                      </el-descriptions-item>
                      <el-descriptions-item label="发布时间">
                        {{ currentFontPackage.publishedAt ? formatDateTime(currentFontPackage.publishedAt) : '未发布' }}
                      </el-descriptions-item>
                      <el-descriptions-item label="创建者">
                        {{ currentFontPackage.createdBy }}
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="info-section">
                    <h3>标签</h3>
                    <div class="tags-container">
                      <el-tag 
                        v-for="tag in currentFontPackage.tags" 
                        :key="tag" 
                        style="margin: 4px;"
                        type="info"
                      >
                        {{ tag }}
                      </el-tag>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>

          <!-- 生成统计 -->
          <el-tab-pane label="生成统计" name="stats">
            <div class="stats-info">
              <el-row :gutter="24">
                <el-col :span="12">
                  <div class="info-section">
                    <h3>生成性能</h3>
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="总生成时间">
                        {{ formatDuration(currentFontPackage.generationStats?.totalTime) }}
                      </el-descriptions-item>
                      <el-descriptions-item label="平均每字符时间">
                        {{ currentFontPackage.generationStats?.averageTimePerChar }}秒
                      </el-descriptions-item>
                      <el-descriptions-item label="成功率">
                        {{ currentFontPackage.generationStats?.successRate }}%
                      </el-descriptions-item>
                      <el-descriptions-item label="重试次数">
                        {{ currentFontPackage.generationStats?.retryCount }}次
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="info-section">
                    <h3>使用统计</h3>
                    <el-descriptions :column="1" border>
                      <el-descriptions-item label="活跃设备数">
                        {{ currentFontPackage.usageStats?.activeDevices }}台
                      </el-descriptions-item>
                      <el-descriptions-item label="用户评分">
                        <el-rate 
                          :model-value="currentFontPackage.usageStats?.userRating" 
                          disabled 
                          show-score 
                          text-color="#ff9900"
                        />
                      </el-descriptions-item>
                      <el-descriptions-item label="反馈数量">
                        {{ currentFontPackage.usageStats?.feedbackCount }}条
                      </el-descriptions-item>
                      <el-descriptions-item label="最后使用时间">
                        {{ currentFontPackage.usageStats?.lastUsedAt ? formatDateTime(currentFontPackage.usageStats.lastUsedAt) : '未使用' }}
                      </el-descriptions-item>
                    </el-descriptions>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>

          <!-- 字符集信息 -->
          <el-tab-pane label="字符集" name="characters">
            <div class="characters-info">
              <el-row :gutter="24">
                <el-col :span="12">
                  <div class="info-section">
                    <h3>目标字符集 ({{ currentFontPackage.targetCharacters?.length || 0 }}个)</h3>
                    <div class="character-grid">
                      <span 
                        v-for="char in currentFontPackage.targetCharacters" 
                        :key="char" 
                        class="character-item target"
                      >
                        {{ char }}
                      </span>
                    </div>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="info-section">
                    <h3>已生成字符集 ({{ currentFontPackage.actualCharacters?.length || 0 }}个)</h3>
                    <div class="character-grid">
                      <span 
                        v-for="char in currentFontPackage.actualCharacters" 
                        :key="char" 
                        class="character-item generated"
                      >
                        {{ char }}
                      </span>
                    </div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </el-tab-pane>

          <!-- 维护信息 -->
          <el-tab-pane label="维护信息" name="maintenance">
            <div class="maintenance-info">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="最后更新时间">
                  {{ formatDateTime(currentFontPackage.maintenance?.lastUpdated) }}
                </el-descriptions-item>
                <el-descriptions-item label="下次审核时间">
                  {{ formatDateTime(currentFontPackage.maintenance?.nextReview) }}
                </el-descriptions-item>
                <el-descriptions-item label="维护者">
                  {{ currentFontPackage.maintenance?.maintainer }}
                </el-descriptions-item>
                <el-descriptions-item label="维护备注" :span="2">
                  {{ currentFontPackage.maintenance?.notes }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            v-if="currentFontPackage?.status === 'completed'" 
            type="primary" 
            @click="publishFont(currentFontPackage)"
          >
            发布字体包
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 字体包创建对话框 -->
    <FontPackageCreateDialog
      v-model="createDialogVisible"
      @success="handleCreateSuccess"
    />

    <!-- 智能字体包创建向导 -->
    <div v-if="wizardVisible" class="wizard-overlay" @click.self="wizardVisible = false">
      <div class="wizard-container">
        <div class="wizard-header">
          <h2>创建字体包 - 智能向导</h2>
          <el-button type="danger" @click="wizardVisible = false" circle>
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="wizard-content">
          <FontPackageWizard
            v-model="wizardVisible"
            @complete="handleWizardComplete"
            @cancel="handleWizardCancel"
            @save-draft="handleWizardSaveDraft"
          />
        </div>
      </div>
    </div>

    <!-- 样本上传弹窗 -->
    <el-dialog
      v-model="uploadDialogVisible"
      :title="`上传样本 - ${currentUploadPackage?.name || ''}`"
      width="70%"
      :before-close="handleUploadClose"
      class="upload-dialog"
    >
      <div v-if="currentUploadPackage" class="upload-content">
        <!-- 上传说明 -->
        <el-alert
          title="上传说明"
          type="info"
          :closable="false"
          show-icon
          class="upload-tips"
        >
          <template #default>
            <ul>
              <li>支持的文件格式：PNG, JPG, JPEG</li>
              <li>单个文件大小不超过 5MB</li>
              <li>建议图片尺寸：256x256 像素</li>
              <li>文件名应包含对应的汉字，如：一.png, 二.jpg</li>
              <li>支持批量上传，最多一次上传 100 个文件</li>
            </ul>
          </template>
        </el-alert>

        <!-- 文件上传区域 -->
        <div class="upload-area">
          <el-upload
            ref="uploadRef"
            v-model:file-list="uploadFileList"
            :auto-upload="false"
            :multiple="true"
            :limit="100"
            :accept="'.png,.jpg,.jpeg'"
            :before-upload="beforeUpload"
            :on-exceed="handleExceed"
            :on-remove="handleRemove"
            drag
            class="upload-dragger"
          >
            <el-icon class="el-icon--upload">
              <Upload />
            </el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或<em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持 PNG/JPG/JPEG 格式，单个文件不超过 5MB
              </div>
            </template>
          </el-upload>
        </div>

        <!-- 上传进度 -->
        <div v-if="isUploading" class="upload-progress">
          <h4>上传进度</h4>
          <el-progress
            :percentage="uploadProgress.percentage"
            :stroke-width="8"
            :show-text="true"
            :format="(percentage) => `${uploadProgress.completed}/${uploadProgress.total} (${percentage}%)`"
          />
          <div class="progress-info">
            <p>当前处理：{{ uploadProgress.current }}</p>
            <p>已完成：{{ uploadProgress.completed }}个</p>
            <p>失败：{{ uploadProgress.failed }}个</p>
          </div>
        </div>

        <!-- 上传错误列表 -->
        <div v-if="uploadProgress.errors.length > 0" class="upload-errors">
          <h4>上传失败的文件</h4>
          <el-table :data="uploadProgress.errors" size="small" max-height="200">
            <el-table-column prop="filename" label="文件名" />
            <el-table-column prop="error" label="错误信息" />
          </el-table>
        </div>

        <!-- 文件列表 -->
        <div v-if="uploadFileList.length > 0" class="file-list">
          <h4>待上传文件列表 ({{ uploadFileList.length }}个)</h4>
          <el-table :data="uploadFileList" size="small" max-height="300">
            <el-table-column label="预览" width="80">
              <template #default="{ row }">
                <el-image
                  :src="getFilePreviewUrl(row)"
                  :preview-src-list="[getFilePreviewUrl(row)]"
                  fit="cover"
                  style="width: 40px; height: 40px; border-radius: 4px;"
                />
              </template>
            </el-table-column>
            <el-table-column prop="name" label="文件名" />
            <el-table-column label="大小" width="100">
              <template #default="{ row }">
                {{ formatFileSize(row.size) }}
              </template>
            </el-table-column>
            <el-table-column label="识别字符" width="100">
              <template #default="{ row }">
                <span class="recognized-char">{{ extractCharFromFilename(row.name) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getFileStatusType(row)" size="small">
                  {{ getFileStatusText(row) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="uploadDialogVisible = false" :disabled="isUploading">
            取消
          </el-button>
          <el-button @click="clearUploadList" :disabled="isUploading || uploadFileList.length === 0">
            清空列表
          </el-button>
          <el-button 
            type="primary" 
            @click="startUpload" 
            :loading="isUploading"
            :disabled="uploadFileList.length === 0"
          >
            {{ isUploading ? '上传中...' : '开始上传' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { Plus, Search, Loading, Check, Close, CircleCheck, Document, Upload, Star } from '@element-plus/icons-vue'
import { DataCard, DataTable, StatusTag } from '@/components/common'
import { mockFontPackageAPI } from '@/api/mock/fontPackage'
import FontPackageCreateDialog from '@/components/fontPackage/FontPackageCreateDialog.vue'
import FontPackageWizard from '@/components/fontPackage/FontPackageWizard.vue'
import type { FontPackage } from '@/types/fontPackage'

// 响应式数据
const loading = ref(false)
const generationMonitorInterval = ref<NodeJS.Timeout | null>(null)
const searchKeyword = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const difficultyFilter = ref('')
const creatorFilter = ref('')
const showAdvancedFilter = ref(false)

// 高级筛选数据
const qualityRange = ref([0, 100])
const sampleRange = ref([0, 1000])
const dateRange = ref<[string, string] | null>(null)
const sortBy = ref('createdAt')
const sortOrder = ref('desc')

// 创建者列表
const creatorList = ref<string[]>([])

// 搜索防抖
let searchTimeout: NodeJS.Timeout | null = null

// 详情弹窗数据
const detailDialogVisible = ref(false)
const currentFontPackage = ref<any>(null)
const activeTab = ref('basic')
const fontSamples = ref([])
const generationTasks = ref([])
const fontPreviews = ref([])

// 创建字体包弹窗数据
const createDialogVisible = ref(false)
const wizardVisible = ref(false)
const useWizard = ref(true) // 控制是否使用新向导

// 样本上传弹窗数据
const uploadDialogVisible = ref(false)
const currentUploadPackage = ref<any>(null)
const uploadFileList = ref<any[]>([])
const uploadProgress = ref({
  total: 0,
  completed: 0,
  failed: 0,
  current: '',
  percentage: 0,
  errors: [] as Array<{ filename: string; error: string }>
})
const isUploading = ref(false)

// 字体包数据
const fontPackageList = ref<FontPackage[]>([])

// 表格列配置
const columns = ref([
  {
    prop: 'id',
    label: 'ID',
    width: 80,
    sortable: true
  },
  {
    prop: 'name',
    label: '字体包名称',
    minWidth: 200,
    sortable: true
  },
  {
    prop: 'fontType',
    label: '字体类型',
    width: 120,
    sortable: true
  },
  {
    prop: 'status',
    label: '状态',
    width: 150,
    sortable: true
  },
  {
    prop: 'sampleCount',
    label: '样本数量',
    width: 100,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'qualityScore',
    label: '质量评分',
    width: 100,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'createdBy',
    label: '创建者',
    width: 120,
    sortable: true
  },
  {
    prop: 'createdAt',
    label: '创建时间',
    width: 160,
    sortable: true,
    type: 'date'
  },
  {
    prop: 'actions',
    label: '操作',
    width: 300,
    fixed: 'right'
  }
])

const fontStats = ref({
  totalPackages: 0,
  publishedPackages: 0,
  generatingPackages: 0,
  failedPackages: 0,
  totalSamples: 0,
  totalCharacters: 0,
  averageAccuracy: 0,
  averageGenerationTime: 0,
  statusDistribution: {
    draft: 0,
    training: 0,
    completed: 0,
    published: 0,
    failed: 0
  },
  typeDistribution: {
    kaishu: 0,
    xingshu: 0,
    lishu: 0,
    zhuanshu: 0,
    caoshu: 0,
    other: 0
  },
  recentActivity: {
    newPackagesThisWeek: 0,
    completedThisWeek: 0,
    publishedThisWeek: 0,
    totalDownloadsThisWeek: 0
  }
})

// 分页数据
const pagination = ref({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 计算属性
const canStartGeneration = computed(() => {
  return (row: any) => {
    // 更严格的前置条件检查
    return (row.status === 'draft' || row.status === 'failed') && 
           row.sampleCount >= 50 && 
           row.targetCharacters && 
           row.targetCharacters.length > 0
  }
})

// 方法
const refreshData = async () => {
  loading.value = true
  try {
    // 并行加载数据以提高性能
    await Promise.all([
      loadFontPackages(),
      loadFontStats(),
      loadCreatorList()
    ])
    console.log('数据刷新完成')
    
    // 重启生成进度监控
    startGenerationMonitoring()
  } catch (error) {
    console.error('数据刷新失败:', error)
    ElMessage.error('数据刷新失败')
  } finally {
    loading.value = false
  }
}

const loadFontPackages = async () => {
  try {
    const response = await mockFontPackageAPI.getFontPackages({
      page: pagination.value.currentPage,
      pageSize: pagination.value.pageSize,
      keyword: searchKeyword.value,
      status: statusFilter.value,
      fontType: typeFilter.value,
      difficulty: difficultyFilter.value,
      creator: creatorFilter.value,
      qualityRange: qualityRange.value,
      sampleRange: sampleRange.value,
      dateRange: dateRange.value,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value
    })
    
    fontPackageList.value = response.data.list
    pagination.value.total = response.data.total
  } catch (error) {
    console.error('加载字体包失败:', error)
    ElMessage.error('加载字体包失败')
  }
}

const loadFontStats = async () => {
  try {
    const response = await mockFontPackageAPI.getFontPackageStats()
    if (response && response.data) {
      fontStats.value = response.data
      console.log('字体包统计数据加载成功:', response.data)
    } else {
      console.warn('统计数据响应格式异常:', response)
      ElMessage.warning('统计数据加载异常')
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  }
}

const loadCreatorList = async () => {
  try {
    const response = await mockFontPackageAPI.getFontPackages({ pageSize: 1000 })
    if (response && response.data && response.data.list) {
      const creators = [...new Set(response.data.list.map((item: any) => item.createdBy))]
      creatorList.value = creators.sort()
    }
  } catch (error) {
    console.error('加载创建者列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.value.currentPage = 1
  loadFontPackages()
}

const handleSearchDebounced = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  searchTimeout = setTimeout(() => {
    handleSearch()
  }, 300)
}

const handleFilter = () => {
  pagination.value.currentPage = 1
  loadFontPackages()
}

const handleSort = () => {
  loadFontPackages()
}

const resetFilters = () => {
  searchKeyword.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  difficultyFilter.value = ''
  creatorFilter.value = ''
  qualityRange.value = [0, 100]
  sampleRange.value = [0, 1000]
  dateRange.value = null
  sortBy.value = 'createdAt'
  sortOrder.value = 'desc'
  pagination.value.currentPage = 1
  loadFontPackages()
}

const toggleAdvancedFilter = () => {
  showAdvancedFilter.value = !showAdvancedFilter.value
}

const handlePageChange = (page: number) => {
  pagination.value.currentPage = page
  loadFontPackages()
}

const handleSizeChange = (size: number) => {
  pagination.value.pageSize = size
  pagination.value.currentPage = 1
  loadFontPackages()
}

const showCreateDialog = () => {
  console.log('showCreateDialog called, useWizard:', useWizard.value)
  console.log('wizardVisible before:', wizardVisible.value)
  console.log('createDialogVisible before:', createDialogVisible.value)
  
  if (useWizard.value) {
    console.log('Opening wizard dialog')
    wizardVisible.value = true
    console.log('wizardVisible after:', wizardVisible.value)
  } else {
    console.log('Opening create dialog')
    createDialogVisible.value = true
    console.log('createDialogVisible after:', createDialogVisible.value)
  }
}

const handleCreateSuccess = () => {
  createDialogVisible.value = false
  refreshData()
  ElMessage.success('字体包创建成功')
}

const handleWizardComplete = async (wizardData: any) => {
  try {
    // 使用向导数据创建字体包
    await mockFontPackageAPI.createFontPackageFromWizard(wizardData)
    
    wizardVisible.value = false
    ElMessage.success('字体包创建成功，AI训练已开始')
    refreshData()
  } catch (error) {
    console.error('创建字体包失败:', error)
    ElMessage.error('创建字体包失败，请重试')
  }
}

const handleWizardCancel = () => {
  wizardVisible.value = false
}

const handleWizardSaveDraft = (wizardData: any) => {
  // 保存草稿到服务器
  mockFontPackageAPI.saveWizardDraft(wizardData)
    .then(() => {
      ElMessage.success('草稿已保存')
    })
    .catch((error) => {
      console.error('保存草稿失败:', error)
      ElMessage.error('保存草稿失败')
    })
}

const viewFontDetail = async (row: any) => {
  try {
    loading.value = true
    
    // 获取字体包详情
    const fontPackage = await mockFontPackageAPI.getFontPackageById(row.id)
    if (fontPackage) {
      currentFontPackage.value = fontPackage
      activeTab.value = 'basic'
      detailDialogVisible.value = true
    } else {
      ElMessage.error('获取字体包详情失败')
    }
  } catch (error) {
    console.error('获取字体包详情失败:', error)
    ElMessage.error('获取字体包详情失败')
  } finally {
    loading.value = false
  }
}

const uploadSamples = (row: any) => {
  currentUploadPackage.value = row
  uploadFileList.value = []
  uploadProgress.value = {
    total: 0,
    completed: 0,
    failed: 0,
    current: '',
    percentage: 0,
    errors: []
  }
  isUploading.value = false
  uploadDialogVisible.value = true
}

const startGeneration = async (row: any) => {
  try {
    // 前置条件检查和详细确认
    const confirmMessage = `
      <div style="text-align: left;">
        <p><strong>字体包信息：</strong></p>
        <ul>
          <li>名称：${row.name}</li>
          <li>字体类型：${getFontTypeName(row.fontType)}</li>
          <li>样本数量：${row.sampleCount}个</li>
          <li>目标字符：${row.targetCharacters?.length || 0}个</li>
          <li>预计生成时间：${Math.round((row.targetCharacters?.length || 0) * 60 / 60)}分钟</li>
        </ul>
        <p style="color: #E6A23C; margin-top: 10px;">
          <i class="el-icon-warning"></i> 
          生成过程将消耗大量计算资源，请确认所有样本已上传完毕。
        </p>
      </div>
    `
    
    await ElMessageBox.confirm(confirmMessage, '确认开始生成', {
      confirmButtonText: '开始生成',
      cancelButtonText: '取消',
      type: 'warning',
      dangerouslyUseHTMLString: true,
      customClass: 'generation-confirm-dialog'
    })
    
    // 显示加载状态
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '正在启动生成任务...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      const response = await mockFontPackageAPI.startGeneration(row.id)
      loadingInstance.close()
      
      ElMessage({
        type: 'success',
        message: '生成任务已启动！',
        duration: 3000,
        showClose: true
      })
      
      // 显示生成任务详情
      ElMessageBox.alert(
        `<div style="text-align: left;">
          <p><strong>生成任务已创建：</strong></p>
          <ul>
            <li>任务ID：${response.data.task.id}</li>
            <li>预计完成时间：${response.data.task.estimatedTime}分钟</li>
            <li>目标字符数：${response.data.task.totalCharacters}个</li>
          </ul>
          <p style="color: #67C23A;">您可以在字体包详情中查看实时进度。</p>
        </div>`,
        '生成任务信息',
        {
          confirmButtonText: '知道了',
          dangerouslyUseHTMLString: true
        }
      )
      
      refreshData()
    } catch (apiError: any) {
      loadingInstance.close()
      throw apiError
    }
    
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('开始生成失败:', error)
      
      // 根据错误类型显示不同的错误信息
      let errorMessage = '开始生成失败'
      if (error.message) {
        errorMessage = error.message
      } else if (typeof error === 'string') {
        errorMessage = error
      }
      
      ElMessage({
        type: 'error',
        message: errorMessage,
        duration: 5000,
        showClose: true
      })
    }
  }
}

const publishFont = async (row: any) => {
  try {
    // 发布前质量检查和确认
    const qualityWarnings = []
    
    if (row.qualityScore < 70) {
      qualityWarnings.push(`质量评分较低（${row.qualityScore}分），建议70分以上`)
    }
    
    if (row.generationStats?.successRate < 80) {
      qualityWarnings.push(`生成成功率较低（${row.generationStats.successRate}%），建议80%以上`)
    }
    
    const completionRate = (row.actualCharacters?.length || 0) / (row.targetCharacters?.length || 1) * 100
    if (completionRate < 80) {
      qualityWarnings.push(`字符完成度较低（${Math.round(completionRate)}%），建议80%以上`)
    }
    
    let confirmMessage = `
      <div style="text-align: left;">
        <p><strong>发布信息确认：</strong></p>
        <ul>
          <li>字体包：${row.name}</li>
          <li>版本：${row.version}</li>
          <li>质量评分：${row.qualityScore}分</li>
          <li>生成成功率：${row.generationStats?.successRate || 0}%</li>
          <li>字符完成度：${Math.round(completionRate)}%</li>
          <li>文件大小：${row.fileSize}MB</li>
        </ul>
    `
    
    if (qualityWarnings.length > 0) {
      confirmMessage += `
        <div style="color: #E6A23C; margin-top: 10px; padding: 10px; background: #FDF6EC; border-radius: 4px;">
          <p><strong>⚠️ 质量提醒：</strong></p>
          <ul>
            ${qualityWarnings.map(warning => `<li>${warning}</li>`).join('')}
          </ul>
          <p>建议优化后再发布，或选择"强制发布"。</p>
        </div>
      `
    }
    
    confirmMessage += `
        <p style="color: #67C23A; margin-top: 10px;">
          发布后用户即可下载使用该字体包。
        </p>
      </div>
    `
    
    const action = await ElMessageBox.confirm(confirmMessage, '确认发布字体包', {
      confirmButtonText: qualityWarnings.length > 0 ? '强制发布' : '确认发布',
      cancelButtonText: '取消',
      type: qualityWarnings.length > 0 ? 'warning' : 'info',
      dangerouslyUseHTMLString: true,
      customClass: 'publish-confirm-dialog'
    })
    
    // 显示加载状态
    const loadingInstance = ElLoading.service({
      lock: true,
      text: '正在发布字体包...',
      background: 'rgba(0, 0, 0, 0.7)'
    })
    
    try {
      const publishOptions = {
        skipQualityCheck: qualityWarnings.length > 0,
        publishNotes: qualityWarnings.length > 0 ? 
          `管理员强制发布，质量提醒：${qualityWarnings.join('；')}` : 
          '质量检查通过，正常发布'
      }
      
      const response = await mockFontPackageAPI.publishFontPackage(row.id, publishOptions)
      loadingInstance.close()
      
      ElMessage({
        type: 'success',
        message: '字体包发布成功！',
        duration: 3000,
        showClose: true
      })
      
      // 显示发布成功详情
      ElMessageBox.alert(
        `<div style="text-align: left;">
          <p><strong>发布成功！</strong></p>
          <ul>
            <li>字体包：${row.name}</li>
            <li>发布时间：${new Date().toLocaleString()}</li>
            <li>下载地址：即将生效</li>
          </ul>
          <p style="color: #67C23A;">用户现在可以搜索并下载该字体包。</p>
        </div>`,
        '发布成功',
        {
          confirmButtonText: '知道了',
          dangerouslyUseHTMLString: true
        }
      )
      
      refreshData()
    } catch (apiError: any) {
      loadingInstance.close()
      throw apiError
    }
    
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('发布失败:', error)
      
      // 根据错误类型显示不同的错误信息
      let errorMessage = '发布失败'
      if (error.message) {
        errorMessage = error.message
      } else if (typeof error === 'string') {
        errorMessage = error
      }
      
      ElMessage({
        type: 'error',
        message: errorMessage,
        duration: 5000,
        showClose: true
      })
    }
  }
}

const deleteFont = async (row: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字体包 "${row.name}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await mockFontPackageAPI.deleteFontPackage(row.id)
    ElMessage.success('删除成功')
    refreshData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const getFontTypeTagType = (type: string) => {
  const types: Record<string, any> = {
    kaishu: 'primary',
    xingshu: 'success',
    lishu: 'warning',
    zhuanshu: 'info',
    caoshu: 'danger'
  }
  return types[type] || 'info'
}

const getFontTypeName = (type: string) => {
  const names: Record<string, string> = {
    kaishu: '楷书',
    xingshu: '行书',
    lishu: '隶书',
    zhuanshu: '篆书',
    caoshu: '草书'
  }
  return names[type] || type
}

/**
 * 获取状态标签类型，优化颜色显示
 * @param status 字体包状态
 * @returns 标签类型
 */
const getStatusTagType = (status: string) => {
  const statusTypes: Record<string, string> = {
    draft: 'info',        // 草稿 - 蓝色
    training: 'warning',  // 生成中 - 橙色
    completed: 'primary', // 已完成 - 主色调
    published: 'success', // 已发布 - 绿色
    failed: 'danger'      // 失败 - 红色
  }
  return statusTypes[status] || 'info'
}

const getStatusName = (status: string) => {
  const statusNames: Record<string, string> = {
    draft: '草稿',
    training: '生成中',
    completed: '已完成',
    published: '已发布',
    failed: '生成失败'
  }
  return statusNames[status] || status
}

const getEstimatedTime = (row: any) => {
  if (!row.generationStats || row.status !== 'training') {
    return '计算中...'
  }
  
  const { totalTime, averageTimePerChar } = row.generationStats
  const remainingChars = row.sampleCount - (row.generatedCount || 0)
  const estimatedMinutes = Math.round(remainingChars * averageTimePerChar / 60)
  
  if (estimatedMinutes < 60) {
    return `${estimatedMinutes}分钟`
  } else {
    const hours = Math.floor(estimatedMinutes / 60)
    const minutes = estimatedMinutes % 60
    return `${hours}小时${minutes > 0 ? minutes + '分钟' : ''}`
  }
}

// 开始监控生成进度
const startGenerationMonitoring = () => {
  if (generationMonitorInterval.value) {
    clearInterval(generationMonitorInterval.value)
  }
  
  generationMonitorInterval.value = setInterval(async () => {
    // 检查是否有正在生成的字体包
    const trainingPackages = fontPackageList.value.filter((pkg: any) => pkg.status === 'training')
    
    if (trainingPackages.length === 0) {
      // 没有正在生成的包，停止监控
      if (generationMonitorInterval.value) {
        clearInterval(generationMonitorInterval.value)
        generationMonitorInterval.value = null
      }
      return
    }
    
    // 更新正在生成的包的进度
    for (const pkg of trainingPackages) {
      try {
        const response = await mockFontPackageAPI.getGenerationProgress(pkg.id)
        if (response.data && response.data.progress) {
          // 更新包的进度信息
          const index = fontPackageList.value.findIndex((p: any) => p.id === pkg.id)
          if (index !== -1) {
            fontPackageList.value[index] = {
              ...fontPackageList.value[index],
              progress: response.data.progress.percentage,
              generatedCount: response.data.progress.percentage || 0
            }
          }
        }
      } catch (error) {
        console.warn(`获取字体包 ${pkg.id} 进度失败:`, error)
      }
    }
  }, 5000) // 每5秒更新一次
}

// 停止监控生成进度
const stopGenerationMonitoring = () => {
  if (generationMonitorInterval.value) {
    clearInterval(generationMonitorInterval.value)
    generationMonitorInterval.value = null
  }
}

const getDifficultyText = (difficulty: number) => {
  const difficultyTexts: Record<number, string> = {
    1: '★☆☆☆☆',
    2: '★★☆☆☆',
    3: '★★★☆☆',
    4: '★★★★☆',
    5: '★★★★★'
  }
  return difficultyTexts[difficulty] || '未知'
}

const formatDateTime = (dateString: string) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const formatDuration = (minutes: number) => {
  if (!minutes || minutes === 0) return '0分钟'
  
  if (minutes < 60) {
    return `${minutes}分钟`
  } else {
    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60
    return `${hours}小时${remainingMinutes > 0 ? remainingMinutes + '分钟' : ''}`
  }
}

const getProgressColor = (percentage: number) => {
  if (percentage < 30) {
    return '#f56c6c'
  } else if (percentage < 70) {
    return '#e6a23c'
  } else {
    return '#67c23a'
  }
}

const handleDetailClose = () => {
  detailDialogVisible.value = false
  currentFontPackage.value = null
  activeTab.value = 'basic'
}

// 样本上传相关函数
const handleUploadClose = () => {
  if (isUploading.value) {
    ElMessageBox.confirm('正在上传中，确定要关闭吗？', '确认关闭', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      uploadDialogVisible.value = false
      resetUploadState()
    }).catch(() => {
      // 用户取消关闭
    })
  } else {
    uploadDialogVisible.value = false
    resetUploadState()
  }
}

const resetUploadState = () => {
  currentUploadPackage.value = null
  uploadFileList.value = []
  uploadProgress.value = {
    total: 0,
    completed: 0,
    failed: 0,
    current: '',
    percentage: 0,
    errors: []
  }
  isUploading.value = false
}

const beforeUpload = (file: any) => {
  // 检查文件类型
  const isValidType = ['image/png', 'image/jpeg', 'image/jpg'].includes(file.type)
  if (!isValidType) {
    ElMessage.error('只支持 PNG、JPG、JPEG 格式的图片文件')
    return false
  }

  // 检查文件大小
  const isValidSize = file.size / 1024 / 1024 < 5
  if (!isValidSize) {
    ElMessage.error('文件大小不能超过 5MB')
    return false
  }

  // 检查文件名是否包含汉字
  const char = extractCharFromFilename(file.name)
  if (!char) {
    ElMessage.warning(`文件 ${file.name} 无法识别汉字，请确保文件名包含对应的汉字`)
  }

  return true
}

const handleExceed = (files: any[], fileList: any[]) => {
  ElMessage.warning(`最多只能上传 100 个文件，当前选择了 ${files.length + fileList.length} 个文件`)
}

const handleRemove = (file: any, fileList: any[]) => {
  uploadFileList.value = fileList
}

const getFilePreviewUrl = (file: any) => {
  if (file.url) {
    return file.url
  }
  if (file.raw) {
    return URL.createObjectURL(file.raw)
  }
  return ''
}

const formatFileSize = (size: number) => {
  if (size < 1024) {
    return `${size} B`
  } else if (size < 1024 * 1024) {
    return `${(size / 1024).toFixed(1)} KB`
  } else {
    return `${(size / 1024 / 1024).toFixed(1)} MB`
  }
}

const extractCharFromFilename = (filename: string) => {
  // 从文件名中提取汉字
  const match = filename.match(/[\u4e00-\u9fa5]/g)
  return match ? match[0] : ''
}

const getFileStatusType = (file: any) => {
  if (file.status === 'success') return 'success'
  if (file.status === 'error') return 'danger'
  if (file.status === 'uploading') return 'warning'
  return 'info'
}

const getFileStatusText = (file: any) => {
  switch (file.status) {
    case 'success': return '上传成功'
    case 'error': return '上传失败'
    case 'uploading': return '上传中'
    default: return '待上传'
  }
}

const clearUploadList = () => {
  uploadFileList.value = []
  uploadProgress.value.errors = []
}

const startUpload = async () => {
  if (uploadFileList.value.length === 0) {
    ElMessage.warning('请先选择要上传的文件')
    return
  }

  isUploading.value = true
  uploadProgress.value = {
    total: uploadFileList.value.length,
    completed: 0,
    failed: 0,
    current: '',
    percentage: 0,
    errors: []
  }

  try {
    for (let i = 0; i < uploadFileList.value.length; i++) {
      const file = uploadFileList.value[i]
      uploadProgress.value.current = file.name
      
      try {
        // 模拟上传过程
        await simulateUpload(file)
        
        file.status = 'success'
        uploadProgress.value.completed++
        
        ElMessage.success(`${file.name} 上传成功`)
      } catch (error) {
        file.status = 'error'
        uploadProgress.value.failed++
        uploadProgress.value.errors.push({
          filename: file.name,
          error: error instanceof Error ? error.message : '上传失败'
        })
        
        ElMessage.error(`${file.name} 上传失败`)
      }
      
      uploadProgress.value.percentage = Math.round(
        ((uploadProgress.value.completed + uploadProgress.value.failed) / uploadProgress.value.total) * 100
      )
    }

    // 上传完成
    const successCount = uploadProgress.value.completed
    const failedCount = uploadProgress.value.failed
    
    if (failedCount === 0) {
      ElMessage.success(`所有文件上传成功！共 ${successCount} 个文件`)
    } else {
      ElMessage.warning(`上传完成！成功 ${successCount} 个，失败 ${failedCount} 个`)
    }
    
    // 刷新数据
    refreshData()
    
  } catch (error) {
    console.error('批量上传失败:', error)
    ElMessage.error('批量上传失败')
  } finally {
    isUploading.value = false
    uploadProgress.value.current = ''
  }
}

const simulateUpload = (file: any): Promise<void> => {
  return new Promise((resolve, reject) => {
    // 模拟上传时间
    const uploadTime = Math.random() * 2000 + 500 // 0.5-2.5秒
    
    setTimeout(() => {
      // 模拟上传成功率（90%成功率）
      if (Math.random() > 0.1) {
        resolve()
      } else {
        reject(new Error('网络错误或文件格式不正确'))
      }
    }, uploadTime)
  })
}

onMounted(() => {
  refreshData()
  startGenerationMonitoring()
})

onUnmounted(() => {
  stopGenerationMonitoring()
})
</script>

<style scoped lang="scss">
.font-package-management {
  padding: 24px;
  background: #f5f5f5;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;

    h1 {
      margin: 0 0 8px 0;
      color: #262626;
      font-size: 24px;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: #8c8c8c;
      font-size: 14px;
    }
  }

  .stats-section {
    margin-bottom: 24px;
    
    .stat-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      transition: all 0.3s ease;
      border: 1px solid #f0f0f0;
      
      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
      }
      
      &.secondary {
        padding: 16px;
        background: linear-gradient(135deg, #f8f9fa, #ffffff);
        
        .stat-content {
          text-align: center;
          
          .stat-value {
            font-size: 24px;
            font-weight: 700;
            color: #409eff;
            margin-bottom: 4px;
          }
          
          .stat-title {
            font-size: 13px;
            color: #666;
            font-weight: 500;
          }
        }
      }
      
      .stat-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 16px;
        color: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }
      
      .stat-content {
        .stat-value {
          font-size: 32px;
          font-weight: 700;
          color: #262626;
          margin-bottom: 4px;
          line-height: 1;
        }
        
        .stat-title {
          font-size: 16px;
          color: #262626;
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .stat-desc {
          font-size: 12px;
          color: #8c8c8c;
          line-height: 1.4;
        }
      }
    }
  }

  .table-section {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }

    .search-section {
      margin-bottom: 20px;
      
      .filter-container {
        background: #fff;
        border-radius: 8px;
        padding: 16px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        border: 1px solid #f0f0f0;
        
        .filter-row {
          display: flex;
          align-items: center;
          gap: 12px;
          flex-wrap: wrap;
          
          .filter-item {
            flex: 1;
            min-width: 160px;
            
            &.search-item {
              flex: 2;
              min-width: 280px;
            }
            
            .search-input {
              width: 100%;
            }
            
            .filter-select {
              width: 100%;
            }
          }
          
          .filter-actions {
            display: flex;
            gap: 8px;
            flex-shrink: 0;
            
            .action-btn {
              min-width: 60px;
            }
          }
        }
      }
      
      .advanced-filter {
        background: #f8f9fa;
        border: 1px solid #e9ecef;
        border-radius: 8px;
        padding: 20px;
        margin-top: 16px;
        
        .advanced-row {
          .filter-group {
            label {
              display: block;
              font-size: 13px;
              color: #606266;
              margin-bottom: 10px;
              font-weight: 600;
            }
            
            .range-text {
              font-size: 12px;
              color: #909399;
              margin-top: 8px;
              display: block;
              color: #999;
              margin-top: 4px;
              display: block;
            }
            
            .el-slider {
              margin: 8px 0;
            }
          }
        }
      }
    }

    .status-cell {
      .status-info {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;
        
        .el-tag {
          display: flex;
          align-items: center;
          gap: 4px;
          
          .el-icon {
            font-size: 12px;
            
            &.is-loading {
              animation: rotating 2s linear infinite;
            }
          }
        }
        
        .quality-score {
          font-size: 12px;
          color: #909399;
          background: #f4f4f5;
          padding: 2px 6px;
          border-radius: 4px;
        }
      }
      
      .progress-bar {
        margin: 4px 0;
        
        :deep(.el-progress-bar__outer) {
          background-color: #f0f2f5;
        }
        
        :deep(.el-progress-bar__inner) {
          background: linear-gradient(90deg, #409eff 0%, #67c23a 100%);
        }
      }
      
      .generation-info {
        .time-info {
          font-size: 11px;
          color: #909399;
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
    
    .font-type-cell {
      .difficulty-info {
        margin-top: 4px;
        
        .difficulty-level {
          font-size: 11px;
          color: #f56c6c;
          font-weight: 500;
        }
      }
    }
    
    @keyframes rotating {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }
  }
}

// 字体包详情弹窗样式
:deep(.font-detail-dialog) {
  .el-dialog__body {
    padding: 20px;
  }
  
  .font-detail-content {
    .info-section {
      margin-bottom: 20px;
      
      h3 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 16px;
        font-weight: 600;
        border-bottom: 2px solid #409eff;
        padding-bottom: 8px;
      }
      
      .description-text {
        line-height: 1.6;
        color: #606266;
        background: #f8f9fa;
        padding: 12px;
        border-radius: 4px;
        margin: 0;
      }
      
      .tags-container {
        min-height: 40px;
        padding: 8px;
        background: #f8f9fa;
        border-radius: 4px;
      }
    }
    
    .character-grid {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      max-height: 200px;
      overflow-y: auto;
      padding: 12px;
      background: #f8f9fa;
      border-radius: 4px;
      
      .character-item {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 32px;
        border-radius: 4px;
        font-size: 16px;
        font-weight: 500;
        
        &.target {
          background: #e1f3d8;
          color: #67c23a;
          border: 1px solid #b3d8a4;
        }
        
        &.generated {
          background: #d9ecff;
          color: #409eff;
          border: 1px solid #a0cfff;
        }
      }
    }
    
    .el-descriptions {
      :deep(.el-descriptions__label) {
        font-weight: 600;
        color: #303133;
      }
      
      :deep(.el-descriptions__content) {
        color: #606266;
      }
    }
    
    .el-rate {
      :deep(.el-rate__text) {
        color: #606266;
      }
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }
}

// 智能向导弹窗样式
.wizard-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  
  .wizard-container {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
    width: 90vw;
    max-width: 1200px;
    height: 85vh;
    max-height: 800px;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    
    .wizard-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20px 24px;
      border-bottom: 1px solid #e4e7ed;
      background: #f8f9fa;
      
      h2 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }
      
      .el-button {
        margin-left: 16px;
      }
    }
    
    .wizard-content {
      flex: 1;
      overflow: auto;
      padding: 0;
    }
  }
}

:deep(.wizard-dialog) {
  .el-dialog {
    margin-top: 5vh;
    margin-bottom: 5vh;
    height: 90vh;
    max-height: 90vh;
    display: flex;
    flex-direction: column;
  }
  
  .el-dialog__header {
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-color, #e4e7ed);
    
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: var(--text-primary, #303133);
    }
  }
  
  .el-dialog__body {
    flex: 1;
    padding: 0;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }
  
  .el-dialog__footer {
    padding: 0;
    border-top: none;
  }
}

// 样本上传弹窗样式
:deep(.upload-dialog) {
  .el-dialog__body {
    padding: 20px;
  }
  
  .upload-content {
    .upload-tips {
      margin-bottom: 20px;
      
      ul {
        margin: 0;
        padding-left: 20px;
        
        li {
          margin-bottom: 4px;
          color: #606266;
        }
      }
    }
    
    .upload-area {
      margin-bottom: 20px;
      
      .upload-dragger {
        .el-upload-dragger {
          width: 100%;
          height: 180px;
          border: 2px dashed #d9d9d9;
          border-radius: 6px;
          cursor: pointer;
          position: relative;
          overflow: hidden;
          transition: border-color 0.2s;
          
          &:hover {
            border-color: #409eff;
          }
          
          .el-icon--upload {
            font-size: 67px;
            color: #c0c4cc;
            margin: 40px 0 16px;
            line-height: 50px;
          }
          
          .el-upload__text {
            color: #606266;
            font-size: 14px;
            text-align: center;
            
            em {
              color: #409eff;
              font-style: normal;
            }
          }
        }
        
        .el-upload__tip {
          font-size: 12px;
          color: #606266;
          margin-top: 7px;
        }
      }
    }
    
    .upload-progress {
      margin-bottom: 20px;
      padding: 16px;
      background: #f8f9fa;
      border-radius: 6px;
      
      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 14px;
      }
      
      .progress-info {
        margin-top: 12px;
        
        p {
          margin: 4px 0;
          font-size: 12px;
          color: #606266;
        }
      }
    }
    
    .upload-errors {
      margin-bottom: 20px;
      
      h4 {
        margin: 0 0 12px 0;
        color: #f56c6c;
        font-size: 14px;
      }
    }
    
    .file-list {
      h4 {
        margin: 0 0 12px 0;
        color: #303133;
        font-size: 14px;
      }
      
      .recognized-char {
        font-size: 16px;
        font-weight: 600;
        color: #409eff;
        background: #ecf5ff;
        padding: 2px 6px;
        border-radius: 4px;
      }
    }
  }
}
</style>