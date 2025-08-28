<template>
  <div class="activity-list">
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <el-card class="filter-card">
        <div class="filter-row">
          <div class="filter-item">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索活动标题、地点或组织者"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
          
          <div class="filter-item">
            <el-select
              v-model="searchForm.type"
              placeholder="活动类型"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部类型" value="" />
              <el-option label="探访活动" value="visit" />
              <el-option label="培训活动" value="training" />
              <el-option label="捐赠活动" value="donation" />
              <el-option label="庆祝活动" value="event" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <el-select
              v-model="searchForm.status"
              placeholder="活动状态"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部状态" value="" />
              <el-option label="已计划" value="planned" />
              <el-option label="进行中" value="ongoing" />
              <el-option label="已完成" value="completed" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </div>
          
          <div class="filter-actions">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>   
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加活动
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 活动列表 -->
    <div class="activity-cards">
      <el-row :gutter="20" v-loading="loading">
        <el-col
          v-for="activity in activityList"
          :key="activity.id"
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          :xl="6"
        >
          <el-card class="activity-card" shadow="hover">
            <!-- 活动图片 -->
            <div class="activity-image">
              <el-image
                :src="activity.photos?.[0] || 'https://picsum.photos/400/300?random=activity'"
                fit="cover"
                class="image"
              >
                <template #error>
                  <div class="image-slot">
                    <el-icon><Picture /></el-icon>
                    <span>图片加载失败</span>
                  </div>
                </template>
              </el-image>
              
              <!-- 状态标签 -->
              <div class="status-badge">
                <el-tag :type="getStatusTagType(activity.status)" size="small">
                  {{ getStatusText(activity.status) }}
                </el-tag>
              </div>
              
              <!-- 类型标签 -->
              <div class="type-badge">
                <el-tag :type="getTypeTagType(activity.type)" size="small">
                  {{ getTypeText(activity.type) }}
                </el-tag>
              </div>
            </div>

            <!-- 活动信息 -->
            <div class="activity-info">
              <h4 class="activity-title" :title="activity.title">
                {{ activity.title }}
              </h4>
              
              <div class="activity-meta">
                <div class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  <span>{{ formatDate(activity.date) }}</span>
                </div>
                
                <div class="meta-item">
                  <el-icon><Location /></el-icon>
                  <span>{{ activity.location }}</span>
                </div>
                
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>{{ activity.participants }}人参与</span>
                </div>
                
                <div class="meta-item">
                  <el-icon><Money /></el-icon>
                  <span>预算 ¥{{ activity.budget.toLocaleString() }}</span>
                </div>
              </div>
              
              <div class="activity-description">
                {{ activity.description }}
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="activity-actions">
              <el-button size="small" @click="handleView(activity)">
                查看详情
              </el-button>
              <el-button type="primary" size="small" @click="handleEdit(activity)">
                编辑
              </el-button>
              <el-dropdown @command="(command) => handleMoreAction(command, activity)">
                <el-button size="small">
                  更多<el-icon><ArrowDown /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="photos">管理照片</el-dropdown-item>
                    <el-dropdown-item command="participants">参与者</el-dropdown-item>
                    <el-dropdown-item command="feedback">反馈记录</el-dropdown-item>
                    <el-dropdown-item command="duplicate">复制活动</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除活动</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty v-if="!loading && activityList.length === 0" description="暂无活动数据" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="activityList.length > 0">
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
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Picture,
  Calendar,
  Location,
  User,
  Money,
  ArrowDown
} from '@element-plus/icons-vue'
import { enhancedCharityTestAPI } from '@/api/mock/charityTestData'
import type { CharityActivity } from '@/api/mock/charity'

// 响应式数据
const loading = ref(false)
const activityList = ref<CharityActivity[]>([])

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 12,
  total: 0
})

// 方法
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: '探访',
    training: '培训',
    donation: '捐赠',
    event: '活动'
  }
  return typeMap[type] || type
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

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    visit: 'primary',
    training: 'success',
    donation: 'warning',
    event: 'info'
  }
  return typeMap[type] || ''
}

const getStatusTagType = (status: string) => {
  const statusMap: Record<string, string> = {
    planned: 'info',
    ongoing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || ''
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const loadActivities = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      type: searchForm.type,
      status: searchForm.status
    }

    const response = await enhancedCharityTestAPI.getDetailedCharityActivities(params)
    activityList.value = response.data.list
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

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.status = ''
  pagination.page = 1
  loadActivities()
}

const handleAdd = () => {
  ElMessage.info('添加活动功能开发中...')
}

const handleView = (activity: CharityActivity) => {
  ElMessage.info(`查看活动：${activity.title}`)
}

const handleEdit = (activity: CharityActivity) => {
  ElMessage.info(`编辑活动：${activity.title}`)
}

const handleMoreAction = async (command: string, activity: CharityActivity) => {
  switch (command) {
    case 'photos':
      ElMessage.info('管理照片功能开发中...')
      break
    case 'participants':
      ElMessage.info('参与者管理功能开发中...')
      break
    case 'feedback':
      ElMessage.info('反馈记录功能开发中...')
      break
    case 'duplicate':
      ElMessage.info('复制活动功能开发中...')
      break
    case 'delete':
      await handleDelete(activity)
      break
  }
}

const handleDelete = async (activity: CharityActivity) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除活动"${activity.title}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    ElMessage.success('删除成功')
    loadActivities()
  } catch (error) {
    // 用户取消删除
  }
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

// 生命周期
onMounted(() => {
  loadActivities()
})

// 暴露方法给父组件
defineExpose({
  refresh: loadActivities
})
</script>

<style lang="scss" scoped>
.activity-list {
  .search-filter-section {
    margin-bottom: 20px;

    .filter-card {
      .filter-row {
        display: flex;
        align-items: center;
        gap: 16px;
        flex-wrap: wrap;

        .filter-item {
          min-width: 200px;
          flex: 1;

          .el-input,
          .el-select {
            width: 100%;
          }
        }

        .filter-actions {
          display: flex;
          gap: 8px;
          margin-left: auto;
        }
      }
    }
  }

  .activity-cards {
    margin-bottom: 20px;

    .activity-card {
      margin-bottom: 20px;
      height: 100%;
      display: flex;
      flex-direction: column;

      :deep(.el-card__body) {
        padding: 0;
        display: flex;
        flex-direction: column;
        height: 100%;
      }

      .activity-image {
        position: relative;
        height: 200px;
        overflow: hidden;

        .image {
          width: 100%;
          height: 100%;
        }

        .image-slot {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background: #f5f7fa;
          color: #909399;
          font-size: 30px;
        }

        .status-badge {
          position: absolute;
          top: 8px;
          right: 8px;
        }

        .type-badge {
          position: absolute;
          top: 8px;
          left: 8px;
        }
      }

      .activity-info {
        padding: 16px;
        flex: 1;

        .activity-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
          margin: 0 0 12px 0;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .activity-meta {
          display: flex;
          flex-direction: column;
          gap: 8px;
          margin-bottom: 12px;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 12px;
            color: #606266;

            .el-icon {
              font-size: 14px;
              color: #909399;
            }
          }
        }

        .activity-description {
          font-size: 13px;
          color: #909399;
          line-height: 1.4;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }
      }

      .activity-actions {
        padding: 12px 16px;
        border-top: 1px solid #f0f0f0;
        display: flex;
        gap: 8px;
        justify-content: space-between;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }
}

@media (max-width: 768px) {
  .activity-list {
    .search-filter-section {
      .filter-row {
        flex-direction: column;
        align-items: stretch;

        .filter-item {
          min-width: auto;
        }

        .filter-actions {
          justify-content: center;
          margin-left: 0;
        }
      }
    }

    .activity-cards {
      .activity-card {
        .activity-actions {
          flex-direction: column;
          gap: 8px;
        }
      }
    }
  }
}
</style>