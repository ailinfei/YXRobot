<template>
  <div class="activity-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="search-section">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索活动标题、地点或组织者"
          style="width: 300px"
          clearable
          @input="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="searchForm.type"
          placeholder="活动类型"
          style="width: 150px"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部类型" value="" />
          <el-option label="实地探访" value="visit" />
          <el-option label="培训活动" value="training" />
          <el-option label="捐赠活动" value="donation" />
          <el-option label="特殊活动" value="event" />
        </el-select>
        
        <el-select
          v-model="searchForm.status"
          placeholder="活动状态"
          style="width: 150px"
          clearable
          @change="handleSearch"
        >
          <el-option label="全部状态" value="" />
          <el-option label="已计划" value="planned" />
          <el-option label="进行中" value="ongoing" />
          <el-option label="已完成" value="completed" />
          <el-option label="已取消" value="cancelled" />
        </el-select>

        <el-date-picker
          v-model="searchForm.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleSearch"
        />
      </div>
      
      <div class="action-buttons">
        <el-button type="primary" @click="handleAddActivity">
          <el-icon><Plus /></el-icon>
          添加活动
        </el-button>
     
      </div>
    </div>

    <!-- 活动列表 -->
    <div class="activity-list">
      <div class="list-view-toggle">
        <el-radio-group v-model="viewMode" @change="handleViewModeChange">
          <el-radio-button value="card">卡片视图</el-radio-button>
          <el-radio-button value="table">表格视图</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 卡片视图 -->
      <div v-if="viewMode === 'card'" class="card-view" v-loading="loading">
        <div class="activity-cards">
          <div
            v-for="activity in activities"
            :key="activity.id"
            class="activity-card"
            @click="handleViewActivity(activity)"
          >
            <div class="card-header">
              <div class="activity-type">
                <el-tag :type="getTypeTagType(activity.type)" size="small">
                  {{ getTypeText(activity.type) }}
                </el-tag>
              </div>
              <div class="activity-status">
                <el-tag :type="getStatusTagType(activity.status)" size="small">
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="card-content">
              <h4 class="activity-title">{{ activity.title }}</h4>
              <p class="activity-description">{{ activity.description }}</p>
              
              <div class="activity-info">
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(activity.date) }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location }}</span>
                </div>
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.participants }}人参与</span>
                </div>
                <div class="info-item">
                  <el-icon><Money /></el-icon>
                  <span>预算 ¥{{ activity.budget.toLocaleString() }}</span>
                </div>
              </div>
            </div>
            
            <div class="card-footer">
              <div class="organizer">
                <span>主办：{{ activity.organizer }}</span>
              </div>
              <div class="card-actions">
                <el-button type="primary" size="small" text @click.stop="handleEditActivity(activity)">
                  编辑
                </el-button>

                <el-button type="danger" size="small" text @click.stop="handleDeleteActivity(activity)">
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <el-empty v-if="activities.length === 0 && !loading" description="暂无活动数据" />
      </div>

      <!-- 表格视图 -->
      <div v-else class="table-view" v-loading="loading">
        <el-table
          :data="activities"
          stripe
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="title" label="活动标题" width="200">
            <template #default="{ row }">
              <div class="activity-title-cell">
                <span class="title-text">{{ row.title }}</span>
                <el-tag :type="getTypeTagType(row.type)" size="small">
                  {{ getTypeText(row.type) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="date" label="活动时间" width="120">
            <template #default="{ row }">
              {{ formatDate(row.date) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="location" label="活动地点" width="150" />
          
          <el-table-column prop="organizer" label="主办方" width="150" />
          
          <el-table-column prop="participants" label="参与人数" width="100">
            <template #default="{ row }">
              <span class="participant-count">{{ row.participants }}人</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="budget" label="预算金额" width="120">
            <template #default="{ row }">
              <span class="budget-amount">¥{{ row.budget.toLocaleString() }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="actualCost" label="实际花费" width="120">
            <template #default="{ row }">
              <span class="actual-cost">¥{{ row.actualCost.toLocaleString() }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="活动状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" text @click="handleViewActivity(row)">
                查看
              </el-button>
              <el-button type="primary" size="small" text @click="handleEditActivity(row)">
                编辑
              </el-button>

              <el-button type="danger" size="small" text @click="handleDeleteActivity(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[12, 24, 48, 96]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑活动对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      @close="handleDialogClose"
    >
      <ActivityForm
        ref="activityFormRef"
        :activity="currentActivity"
        :mode="dialogMode"
        @submit="handleFormSubmit"
        @cancel="handleDialogClose"
      />
    </el-dialog>

    <!-- 活动详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="活动详情"
      width="1000px"
    >
      <ActivityDetail
        :activity="currentActivity"
        @edit="handleEditFromDetail"
        @close="detailDialogVisible = false"
      />
    </el-dialog>


  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Plus,
  Calendar,
  Location,
  User,
  Money
} from '@element-plus/icons-vue'
import ActivityForm from './ActivityForm.vue'
import ActivityDetail from './ActivityDetail.vue'

import CharityAPI from '@/api/charity'
import type { CharityActivity } from '@/api/mock/charity'

// 响应式数据
const loading = ref(false)
const activities = ref<CharityActivity[]>([])
const selectedActivities = ref<CharityActivity[]>([])
const viewMode = ref<'card' | 'table'>('card')

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  status: '',
  dateRange: [] as string[]
})

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 12,
  total: 0
})

// 对话框状态
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)

const dialogMode = ref<'add' | 'edit'>('add')
const currentActivity = ref<CharityActivity | null>(null)

// 组件引用
const activityFormRef = ref()

// 计算属性
const dialogTitle = computed(() => {
  return dialogMode.value === 'add' ? '添加公益活动' : '编辑活动信息'
})

// 方法
const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      type: searchForm.type,
      status: searchForm.status,
      startDate: searchForm.dateRange[0],
      endDate: searchForm.dateRange[1]
    }
    
    const response = await CharityAPI.getCharityActivities(params)
    activities.value = response.data.list
    pagination.total = response.data.total
  } catch (error) {
    console.error('加载活动列表失败:', error)
    ElMessage.error('加载活动列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadActivities()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadActivities()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadActivities()
}

const handleViewModeChange = () => {
  // 调整分页大小
  if (viewMode.value === 'card') {
    pagination.pageSize = 12
  } else {
    pagination.pageSize = 20
  }
  pagination.page = 1
  loadActivities()
}

const handleSelectionChange = (selection: CharityActivity[]) => {
  selectedActivities.value = selection
}

const handleAddActivity = () => {
  currentActivity.value = null
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const handleEditActivity = (activity: CharityActivity) => {
  currentActivity.value = { ...activity }
  dialogMode.value = 'edit'
  dialogVisible.value = true
}

const handleViewActivity = (activity: CharityActivity) => {
  currentActivity.value = activity
  detailDialogVisible.value = true
}

const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  dialogMode.value = 'edit'
  dialogVisible.value = true
}





const handleDeleteActivity = async (activity: CharityActivity) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除活动 "${activity.title}" 吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除API
    await CharityAPI.deleteCharityActivity(activity.id)
    
    ElMessage.success('活动删除成功')
    
    // 清除相关缓存并重新加载数据
    const { clearCache } = await import('@/utils/request')
    clearCache('/admin/charity/activities')
    
    await loadActivities()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除活动失败:', error)
      ElMessage.error('删除活动失败，请稍后重试')
    }
  }
}



const handleFormSubmit = async (formData: any) => {
  try {
    if (dialogMode.value === 'add') {
      // 调用创建API
      await CharityAPI.createCharityActivity(formData)
      ElMessage.success('活动添加成功')
    } else {
      // 调用更新API
      await CharityAPI.updateCharityActivity(formData.id, formData)
      ElMessage.success('活动信息更新成功')
    }
    
    dialogVisible.value = false
    
    // 清除相关缓存并重新加载数据
    const { clearCache } = await import('@/utils/request')
    clearCache('/admin/charity/activities')
    
    await loadActivities()
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请稍后重试')
  }
}

const handleDialogClose = () => {
  dialogVisible.value = false
  currentActivity.value = null
}

// 工具方法
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: '实地探访',
    training: '培训活动',
    donation: '捐赠活动',
    event: '特殊活动'
  }
  return typeMap[type] || type
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: 'primary',
    training: 'success',
    donation: 'warning',
    event: 'info'
  }
  return typeMap[type] || ''
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    planned: '已计划',
    ongoing: '进行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const getStatusTagType = (status: string) => {
  const typeMap: Record<string, string> = {
    planned: 'info',
    ongoing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return typeMap[status] || ''
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadActivities()
})
</script>

<style lang="scss" scoped>
.activity-management {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .search-section {
      display: flex;
      gap: 12px;
      align-items: center;
      flex-wrap: wrap;
    }

    .action-buttons {
      display: flex;
      gap: 12px;
    }
  }

  .activity-list {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    overflow: hidden;

    .list-view-toggle {
      padding: 16px 20px;
      border-bottom: 1px solid #f0f0f0;
      display: flex;
      justify-content: flex-end;
    }

    .card-view {
      padding: 20px;

      .activity-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
        gap: 20px;

        .activity-card {
          background: white;
          border: 1px solid #e4e7ed;
          border-radius: 12px;
          padding: 20px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
            border-color: #409EFF;
          }

          .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
          }

          .card-content {
            margin-bottom: 16px;

            .activity-title {
              margin: 0 0 8px 0;
              font-size: 16px;
              font-weight: 600;
              color: #303133;
              line-height: 1.4;
            }

            .activity-description {
              margin: 0 0 16px 0;
              font-size: 14px;
              color: #606266;
              line-height: 1.6;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            .activity-info {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 8px;

              .info-item {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 13px;
                color: #606266;

                .el-icon {
                  color: #409EFF;
                  font-size: 14px;
                }
              }
            }
          }

          .card-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding-top: 16px;
            border-top: 1px solid #f0f0f0;

            .organizer {
              font-size: 12px;
              color: #909399;
            }

            .card-actions {
              display: flex;
              gap: 8px;
            }
          }
        }
      }
    }

    .table-view {
      .activity-title-cell {
        display: flex;
        flex-direction: column;
        gap: 4px;

        .title-text {
          font-weight: 500;
          color: #303133;
        }
      }

      .participant-count {
        font-weight: 500;
        color: #409EFF;
      }

      .budget-amount {
        font-weight: 500;
        color: #E6A23C;
      }

      .actual-cost {
        font-weight: 500;
        color: #67C23A;
      }
    }

    .pagination-wrapper {
      padding: 20px;
      display: flex;
      justify-content: center;
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .activity-management {
    .action-bar {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;

      .search-section {
        justify-content: center;
      }

      .action-buttons {
        justify-content: center;
      }
    }

    .activity-list {
      .card-view {
        .activity-cards {
          grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
          gap: 16px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .activity-management {
    .action-bar {
      padding: 12px 16px;

      .search-section {
        flex-direction: column;
        gap: 8px;
        width: 100%;

        .el-input,
        .el-select,
        .el-date-picker {
          width: 100% !important;
        }
      }

      .action-buttons {
        flex-direction: column;
        width: 100%;

        .el-button {
          width: 100%;
        }
      }
    }

    .activity-list {
      .list-view-toggle {
        padding: 12px 16px;
      }

      .card-view {
        padding: 16px;

        .activity-cards {
          grid-template-columns: 1fr;
          gap: 12px;

          .activity-card {
            padding: 16px;

            .card-content {
              .activity-info {
                grid-template-columns: 1fr;
              }
            }

            .card-footer {
              flex-direction: column;
              gap: 12px;
              align-items: stretch;

              .card-actions {
                justify-content: center;
              }
            }
          }
        }
      }
    }
  }
}
</style>