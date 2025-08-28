<template>
  <div class="visit-records">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="info-section">
        <h4>{{ institution?.name }} - 探访记录</h4>
        <p class="institution-info">
          {{ getTypeText(institution?.type) }} · {{ institution?.location }}
        </p>
      </div>
      <div class="action-buttons">
        <el-button type="primary" @click="handleAddVisit">
          <el-icon><Plus /></el-icon>
          添加探访记录
        </el-button>
      </div>
    </div>

    <!-- 探访记录列表 -->
    <div class="records-list" v-loading="loading">
      <el-timeline v-if="visitRecords.length > 0">
        <el-timeline-item
          v-for="(record, index) in visitRecords"
          :key="record.id"
          :timestamp="formatDateTime(record.visitDate)"
          :type="getRecordType(record.type)"
          size="large"
        >
          <div class="record-card">
            <div class="record-header">
              <div class="record-title">
                <el-tag :type="getRecordTagType(record.type)" size="small">
                  {{ getRecordTypeText(record.type) }}
                </el-tag>
                <h5>{{ record.title }}</h5>
              </div>
              <div class="record-actions">
                <el-button type="primary" size="small" text @click="handleViewRecord(record)">
                  查看详情
                </el-button>
                <el-button type="primary" size="small" text @click="handleEditRecord(record)">
                  编辑
                </el-button>
                <el-button type="danger" size="small" text @click="handleDeleteRecord(record)">
                  删除
                </el-button>
              </div>
            </div>
            
            <div class="record-content">
              <div class="record-info">
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>探访人员：{{ record.visitors.join(', ') }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>探访时长：{{ record.duration }}小时</span>
                </div>
                <div class="info-item">
                  <el-icon><UserFilled /></el-icon>
                  <span>参与人数：{{ record.participantCount }}人</span>
                </div>
              </div>
              
              <div class="record-description">
                <p>{{ record.description }}</p>
              </div>
              
              <div class="record-photos" v-if="record.photos && record.photos.length > 0">
                <div class="photos-label">探访照片：</div>
                <div class="photos-grid">
                  <div
                    v-for="(photo, photoIndex) in record.photos.slice(0, 4)"
                    :key="photoIndex"
                    class="photo-item"
                    @click="handlePreviewPhoto(record.photos, photoIndex)"
                  >
                    <img :src="photo" :alt="`探访照片${photoIndex + 1}`" />
                    <div v-if="photoIndex === 3 && record.photos.length > 4" class="photo-more">
                      +{{ record.photos.length - 4 }}
                    </div>
                  </div>
                </div>
              </div>
              
              <div class="record-feedback" v-if="record.feedback">
                <div class="feedback-label">反馈意见：</div>
                <div class="feedback-content">{{ record.feedback }}</div>
              </div>
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
      
      <el-empty v-else description="暂无探访记录" />
    </div>

    <!-- 添加/编辑探访记录对话框 -->
    <el-dialog
      v-model="recordDialogVisible"
      :title="recordDialogTitle"
      width="700px"
      @close="handleRecordDialogClose"
    >
      <VisitRecordForm
        ref="recordFormRef"
        :record="currentRecord"
        :mode="recordDialogMode"
        :institution="institution"
        @submit="handleRecordSubmit"
        @cancel="handleRecordDialogClose"
      />
    </el-dialog>

    <!-- 探访记录详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="探访记录详情"
      width="800px"
    >
      <VisitRecordDetail
        :record="currentRecord"
        @edit="handleEditFromDetail"
        @close="detailDialogVisible = false"
      />
    </el-dialog>

    <!-- 图片预览 -->
    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewImages"
      :initial-index="previewIndex"
      @close="previewVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  User,
  Clock,
  UserFilled
} from '@element-plus/icons-vue'
import VisitRecordForm from './VisitRecordForm.vue'
import VisitRecordDetail from './VisitRecordDetail.vue'
import type { CharityInstitution } from '@/api/mock/charity'

// 探访记录接口
interface VisitRecord {
  id: number
  institutionId: number
  title: string
  type: 'routine' | 'special' | 'emergency' | 'evaluation'
  visitDate: string
  visitors: string[]
  duration: number
  participantCount: number
  description: string
  photos?: string[]
  feedback?: string
  createdAt: string
  updatedAt: string
}

interface VisitRecordsProps {
  institution: CharityInstitution | null
}

const props = defineProps<VisitRecordsProps>()

const emit = defineEmits<{
  close: []
}>()

// 响应式数据
const loading = ref(false)
const visitRecords = ref<VisitRecord[]>([])

// 对话框状态
const recordDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const recordDialogMode = ref<'add' | 'edit'>('add')
const currentRecord = ref<VisitRecord | null>(null)

// 图片预览
const previewVisible = ref(false)
const previewImages = ref<string[]>([])
const previewIndex = ref(0)

// 组件引用
const recordFormRef = ref()

// 计算属性
const recordDialogTitle = computed(() => {
  return recordDialogMode.value === 'add' ? '添加探访记录' : '编辑探访记录'
})

// 方法
const loadVisitRecords = async () => {
  if (!props.institution) return
  
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 生成模拟数据
    visitRecords.value = generateMockVisitRecords(props.institution.id)
  } catch (error) {
    console.error('加载探访记录失败:', error)
    ElMessage.error('加载探访记录失败')
  } finally {
    loading.value = false
  }
}

const generateMockVisitRecords = (institutionId: number): VisitRecord[] => {
  const records: VisitRecord[] = []
  const types: Array<'routine' | 'special' | 'emergency' | 'evaluation'> = ['routine', 'special', 'emergency', 'evaluation']
  const visitors = ['张志愿', '李爱心', '王公益', '陈助学', '刘奉献']
  
  for (let i = 1; i <= 8; i++) {
    const visitDate = new Date()
    visitDate.setDate(visitDate.getDate() - (i * 15))
    
    const type = types[Math.floor(Math.random() * types.length)]
    const recordVisitors = visitors.slice(0, Math.floor(Math.random() * 3) + 1)
    
    records.push({
      id: i,
      institutionId,
      title: `第${i}次探访活动`,
      type,
      visitDate: visitDate.toISOString(),
      visitors: recordVisitors,
      duration: Math.floor(Math.random() * 4) + 2,
      participantCount: Math.floor(Math.random() * 50) + 20,
      description: `这是第${i}次探访活动的详细描述，包含了探访的目的、过程和收获。通过这次探访，我们深入了解了机构的运营情况和需求。`,
      photos: [
        `https://via.placeholder.com/400x300/f3e5f5/9c27b0?text=Visit+${i}+Photo+1`,
        `https://via.placeholder.com/400x300/e8eaf6/673ab7?text=Visit+${i}+Photo+2`,
        `https://via.placeholder.com/400x300/f3e5f5/9c27b0?text=Visit+${i}+Photo+3`
      ],
      feedback: `探访反馈：机构运营良好，学生学习积极性高，设备使用情况正常。建议继续保持现有合作模式。`,
      createdAt: visitDate.toISOString(),
      updatedAt: visitDate.toISOString()
    })
  }
  
  return records.sort((a, b) => new Date(b.visitDate).getTime() - new Date(a.visitDate).getTime())
}

const handleAddVisit = () => {
  currentRecord.value = null
  recordDialogMode.value = 'add'
  recordDialogVisible.value = true
}

const handleEditRecord = (record: VisitRecord) => {
  currentRecord.value = { ...record }
  recordDialogMode.value = 'edit'
  recordDialogVisible.value = true
}

const handleViewRecord = (record: VisitRecord) => {
  currentRecord.value = record
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  recordDialogMode.value = 'edit'
  recordDialogVisible.value = true
}

const handleDeleteRecord = async (record: VisitRecord) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除探访记录 "${record.title}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 这里应该调用删除API
    visitRecords.value = visitRecords.value.filter(r => r.id !== record.id)
    ElMessage.success('探访记录删除成功')
  } catch (error) {
    // 用户取消删除
  }
}

const handleRecordSubmit = async (formData: any) => {
  try {
    if (recordDialogMode.value === 'add') {
      // 调用创建API
      const newRecord: VisitRecord = {
        id: Date.now(),
        institutionId: props.institution!.id,
        ...formData,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      }
      visitRecords.value.unshift(newRecord)
      ElMessage.success('探访记录添加成功')
    } else {
      // 调用更新API
      const index = visitRecords.value.findIndex(r => r.id === currentRecord.value!.id)
      if (index !== -1) {
        visitRecords.value[index] = {
          ...visitRecords.value[index],
          ...formData,
          updatedAt: new Date().toISOString()
        }
      }
      ElMessage.success('探访记录更新成功')
    }
    
    recordDialogVisible.value = false
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleRecordDialogClose = () => {
  recordDialogVisible.value = false
  currentRecord.value = null
}

const handlePreviewPhoto = (photos: string[], index: number) => {
  previewImages.value = photos
  previewIndex.value = index
  previewVisible.value = true
}

// 工具方法
const getTypeText = (type?: string) => {
  const typeMap: Record<string, string> = {
    school: '学校',
    community: '社区中心',
    orphanage: '福利院',
    library: '图书馆',
    hospital: '医院'
  }
  return typeMap[type || ''] || type || ''
}

const getRecordType = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: 'primary',
    special: 'success',
    emergency: 'danger',
    evaluation: 'warning'
  }
  return typeMap[type] || 'primary'
}

const getRecordTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: '常规探访',
    special: '特殊活动',
    emergency: '紧急探访',
    evaluation: '评估探访'
  }
  return typeMap[type] || type
}

const getRecordTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    routine: 'primary',
    special: 'success',
    emergency: 'danger',
    evaluation: 'warning'
  }
  return typeMap[type] || ''
}

const formatDateTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(() => {
  loadVisitRecords()
})
</script>

<style lang="scss" scoped>
.visit-records {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;

    .info-section {
      h4 {
        margin: 0 0 8px 0;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
      }

      .institution-info {
        margin: 0;
        font-size: 14px;
        color: #606266;
      }
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }
  }

  .records-list {
    min-height: 400px;

    .record-card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      margin-bottom: 16px;

      .record-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;

        .record-title {
          display: flex;
          align-items: center;
          gap: 12px;

          h5 {
            margin: 0;
            font-size: 16px;
            font-weight: 600;
            color: #303133;
          }
        }

        .record-actions {
          display: flex;
          gap: 8px;
        }
      }

      .record-content {
        .record-info {
          display: flex;
          flex-wrap: wrap;
          gap: 24px;
          margin-bottom: 16px;

          .info-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 14px;
            color: #606266;

            .el-icon {
              color: #409EFF;
            }
          }
        }

        .record-description {
          margin-bottom: 16px;

          p {
            margin: 0;
            font-size: 14px;
            color: #606266;
            line-height: 1.6;
          }
        }

        .record-photos {
          margin-bottom: 16px;

          .photos-label {
            font-size: 14px;
            color: #303133;
            font-weight: 500;
            margin-bottom: 8px;
          }

          .photos-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
            gap: 8px;
            max-width: 360px;

            .photo-item {
              position: relative;
              width: 80px;
              height: 80px;
              border-radius: 8px;
              overflow: hidden;
              cursor: pointer;
              transition: all 0.3s ease;

              &:hover {
                transform: scale(1.05);
              }

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
              }

              .photo-more {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0, 0, 0, 0.6);
                color: white;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 14px;
                font-weight: 600;
              }
            }
          }
        }

        .record-feedback {
          .feedback-label {
            font-size: 14px;
            color: #303133;
            font-weight: 500;
            margin-bottom: 8px;
          }

          .feedback-content {
            padding: 12px;
            background: #f0f9ff;
            border-radius: 8px;
            border-left: 4px solid #409EFF;
            font-size: 14px;
            color: #606266;
            line-height: 1.6;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .visit-records {
    .action-bar {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .action-buttons {
        justify-content: center;

        .el-button {
          flex: 1;
        }
      }
    }

    .records-list {
      .record-card {
        padding: 16px;

        .record-header {
          flex-direction: column;
          gap: 12px;
          align-items: stretch;

          .record-actions {
            justify-content: flex-end;
          }
        }

        .record-content {
          .record-info {
            flex-direction: column;
            gap: 8px;
          }

          .photos-grid {
            grid-template-columns: repeat(4, 1fr);
            max-width: 100%;

            .photo-item {
              width: 100%;
              height: 60px;
            }
          }
        }
      }
    }
  }
}
</style>