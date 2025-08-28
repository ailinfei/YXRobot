<template>
  <div class="institution-management">
    <!-- 搜索和筛选区域 -->
    <div class="search-filter-section">
      <el-card class="filter-card">
        <div class="filter-row">
          <div class="filter-item">
            <el-input
              v-model="searchForm.keyword"
              placeholder="搜索机构名称、联系人或地区"
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
              placeholder="机构类型"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部类型" value="" />
              <el-option label="学校" value="school" />
              <el-option label="孤儿院" value="orphanage" />
              <el-option label="社区中心" value="community" />
              <el-option label="医院" value="hospital" />
              <el-option label="图书馆" value="library" />
            </el-select>
          </div>
          
          <div class="filter-item">
            <el-select
              v-model="searchForm.status"
              placeholder="合作状态"
              clearable
              @change="handleSearch"
            >
              <el-option label="全部状态" value="" />
              <el-option label="活跃合作" value="active" />
              <el-option label="非活跃" value="inactive" />
              <el-option label="待处理" value="pending" />
            </el-select>
          </div>
          
          <div class="filter-actions">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button type="success" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              添加机构
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 机构列表 -->
    <div class="institution-list">
      <el-card>
        <template #header>
          <div class="card-header">
            <span class="title">合作机构列表</span>
          
          </div>
        </template>

        <el-table
          ref="tableRef"
          :data="institutionList"
          :loading="loading"
          stripe
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="55" />
          
          <el-table-column
            prop="name"
            label="机构名称"
            min-width="200"
            sortable="custom"
          >
            <template #default="{ row }">
              <div class="institution-name">
                <el-avatar
                  :size="32"
                  :style="{ backgroundColor: getTypeColor(row.type) }"
                >
                  {{ row.name.charAt(0) }}
                </el-avatar>
                <div class="name-info">
                  <div class="name">{{ row.name }}</div>
                  <div class="id">ID: {{ row.id }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column
            prop="type"
            label="机构类型"
            width="120"
            sortable="custom"
          >
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)" size="small">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column
            prop="location"
            label="所在地区"
            width="150"
            sortable="custom"
          />
          
          <el-table-column
            prop="studentCount"
            label="受益学生"
            width="100"
            sortable="custom"
          >
            <template #default="{ row }">
              <span class="student-count">{{ row.studentCount }}人</span>
            </template>
          </el-table-column>
          
          <el-table-column
            prop="deviceCount"
            label="设备数量"
            width="100"
            sortable="custom"
          >
            <template #default="{ row }">
              <span class="device-count">{{ row.deviceCount }}台</span>
            </template>
          </el-table-column>
          
          <el-table-column
            prop="cooperationDate"
            label="合作时间"
            width="120"
            sortable="custom"
          >
            <template #default="{ row }">
              {{ formatDate(row.cooperationDate) }}
            </template>
          </el-table-column>
          
          <el-table-column
            prop="status"
            label="合作状态"
            width="100"
            sortable="custom"
          >
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)" size="small">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="handleView(row)"
                >
                  查看
                </el-button>
                <el-button
                  type="primary"
                  size="small"
                  text
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 添加机构对话框 -->
    <el-dialog
      v-model="showAddDialog"
      title="添加合作机构"
      width="800px"
      :before-close="handleDialogClose"
    >
      <InstitutionForm
        :mode="'add'"
        @submit="handleFormSubmit"
        @cancel="handleDialogClose"
      />
    </el-dialog>

    <!-- 编辑机构对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑合作机构"
      width="800px"
      :before-close="handleDialogClose"
    >
      <InstitutionForm
        :institution="currentInstitution"
        :mode="'edit'"
        @submit="handleFormSubmit"
        @cancel="handleDialogClose"
      />
    </el-dialog>

    <!-- 查看机构详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="机构详情"
      width="900px"
      :before-close="handleDialogClose"
    >
      <InstitutionDetail
        v-if="currentInstitution"
        :institution="currentInstitution"
        @edit="handleEditFromDetail"
        @close="handleDialogClose"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  Download
} from '@element-plus/icons-vue'
import CharityAPI from '@/api/charity'
import type { CharityInstitution } from '@/api/mock/charity'
import InstitutionForm from './InstitutionForm.vue'
import InstitutionDetail from './InstitutionDetail.vue'

// 响应式数据
const loading = ref(false)
const tableRef = ref()
const institutionList = ref<CharityInstitution[]>([])
const selectedInstitutions = ref<CharityInstitution[]>([])

// 对话框状态
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showDetailDialog = ref(false)
const currentInstitution = ref<CharityInstitution | null>(null)

// 搜索表单
const searchForm = reactive({
  keyword: '',
  type: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 排序数据
const sortData = reactive({
  prop: '',
  order: ''
})

// 方法
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    school: '学校',
    orphanage: '孤儿院',
    community: '社区中心',
    hospital: '医院',
    library: '图书馆'
  }
  return typeMap[type] || type
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    active: '活跃合作',
    inactive: '非活跃',
    pending: '待处理'
  }
  return statusMap[status] || status
}

const getTypeTagType = (type: string) => {
  const typeMap: Record<string, string> = {
    school: 'primary',
    orphanage: 'success',
    community: 'info',
    hospital: 'warning',
    library: 'danger'
  }
  return typeMap[type] || ''
}

const getStatusTagType = (status: string) => {
  const statusMap: Record<string, string> = {
    active: 'success',
    inactive: 'info',
    pending: 'warning'
  }
  return statusMap[status] || ''
}

const getTypeColor = (type: string) => {
  const colorMap: Record<string, string> = {
    school: '#409EFF',
    orphanage: '#67C23A',
    community: '#909399',
    hospital: '#E6A23C',
    library: '#F56C6C'
  }
  return colorMap[type] || '#909399'
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

const loadInstitutions = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      type: searchForm.type,
      status: searchForm.status,
      sortBy: sortData.prop,
      sortOrder: sortData.order
    }

    const response = await CharityAPI.getCharityInstitutions(params)
    institutionList.value = response.data.list
    pagination.total = response.data.total
  } catch (error) {
    console.error('加载机构列表失败:', error)
    ElMessage.error('加载机构列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadInstitutions()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.type = ''
  searchForm.status = ''
  sortData.prop = ''
  sortData.order = ''
  pagination.page = 1
  loadInstitutions()
}

const handleAdd = () => {
  showAddDialog.value = true
}

const handleView = (institution: CharityInstitution) => {
  currentInstitution.value = institution
  showDetailDialog.value = true
}

const handleEdit = (institution: CharityInstitution) => {
  currentInstitution.value = institution
  showEditDialog.value = true
}

const handleEditFromDetail = (institution: CharityInstitution) => {
  showDetailDialog.value = false
  currentInstitution.value = institution
  showEditDialog.value = true
}

const handleDialogClose = () => {
  showAddDialog.value = false
  showEditDialog.value = false
  showDetailDialog.value = false
  currentInstitution.value = null
}

const handleFormSubmit = async (formData: any) => {
  try {
    if (formData.id) {
      // 编辑机构
      await CharityAPI.updateCharityInstitution(formData.id, formData)
      ElMessage.success('机构信息更新成功')
    } else {
      // 添加机构
      await CharityAPI.createCharityInstitution(formData)
      ElMessage.success('机构添加成功')
    }
    
    handleDialogClose()
    loadInstitutions()
  } catch (error) {
    console.error('保存机构信息失败:', error)
    ElMessage.error('保存机构信息失败')
  }
}

const handleDelete = async (institution: CharityInstitution) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除机构"${institution.name}"吗？此操作不可恢复。`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 调用删除API
    await CharityAPI.deleteCharityInstitution(institution.id)
    
    ElMessage.success('删除成功')
    
    // 清除相关缓存并重新加载数据
    const { clearCache } = await import('@/utils/request')
    clearCache('/admin/charity/institutions')
    
    loadInstitutions()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除机构失败:', error)
      ElMessage.error('删除机构失败，请稍后重试')
    }
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

const handleSelectionChange = (selection: CharityInstitution[]) => {
  selectedInstitutions.value = selection
}

const handleSortChange = ({ prop, order }: any) => {
  sortData.prop = prop
  sortData.order = order === 'ascending' ? 'asc' : order === 'descending' ? 'desc' : ''
  loadInstitutions()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  pagination.page = 1
  loadInstitutions()
}

const handleCurrentChange = (page: number) => {
  pagination.page = page
  loadInstitutions()
}

// 生命周期
onMounted(() => {
  loadInstitutions()
})

// 暴露方法给父组件
defineExpose({
  refresh: loadInstitutions,
  getSelectedInstitutions: () => selectedInstitutions.value
})
</script>

<style lang="scss" scoped>
.institution-management {
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

  .institution-list {
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

    .institution-name {
      display: flex;
      align-items: center;
      gap: 12px;

      .name-info {
        .name {
          font-weight: 500;
          color: #303133;
          margin-bottom: 2px;
        }

        .id {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .student-count,
    .device-count {
      font-weight: 500;
      color: #606266;
    }

    .action-buttons {
      display: flex;
      gap: 8px;
      align-items: center;
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 20px;
    }
  }
}

@media (max-width: 768px) {
  .institution-management {
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
  }
}
</style>