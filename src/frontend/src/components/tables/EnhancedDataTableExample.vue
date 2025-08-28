<template>
  <div class="table-example">
    <el-card class="example-card">
      <template #header>
        <div class="card-header">
          <span>增强型数据表格示例</span>
          <el-button type="primary" @click="generateMockData">生成测试数据</el-button>
        </div>
      </template>

      <!-- 配置面板 -->
      <div class="config-panel">
        <el-row :gutter="16">
          <el-col :span="6">
            <el-checkbox v-model="tableConfig.searchable">启用搜索</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="tableConfig.selectable">启用选择</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="tableConfig.showIndex">显示序号</el-checkbox>
          </el-col>
          <el-col :span="6">
            <el-checkbox v-model="tableConfig.stripe">斑马纹</el-checkbox>
          </el-col>
        </el-row>
        <el-row :gutter="16" style="margin-top: 10px;">
          <el-col :span="6">
            <el-select v-model="tableConfig.size" placeholder="表格尺寸">
              <el-option label="大" value="large" />
              <el-option label="默认" value="default" />
              <el-option label="小" value="small" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-input-number
              v-model="tableConfig.pageSize"
              :min="5"
              :max="100"
              :step="5"
            />
            <span style="margin-left: 8px;">每页条数</span>
          </el-col>
          <el-col :span="6">
            <el-switch
              v-model="tableConfig.loading"
              active-text="加载中"
              inactive-text="正常"
            />
          </el-col>
          <el-col :span="6">
            <el-button @click="clearSelection">清空选择</el-button>
          </el-col>
        </el-row>
      </div>

      <!-- 数据表格 -->
      <EnhancedDataTable
        ref="tableRef"
        :data="tableData"
        :columns="tableColumns"
        :loading="tableConfig.loading"
        :pagination="true"
        :total-count="totalCount"
        :current-page="currentPage"
        :page-size="tableConfig.pageSize"
        :searchable="tableConfig.searchable"
        :selectable="tableConfig.selectable"
        :show-index="tableConfig.showIndex"
        :stripe="tableConfig.stripe"
        :size="tableConfig.size"
        :actions="tableActions"
        :batch-actions="batchActions"
        :quick-filters="quickFilters"
        :exportable="true"
        :refreshable="true"
        :column-configurable="true"
        @refresh="handleRefresh"
        @search="handleSearch"
        @selection-change="handleSelectionChange"
        @action-click="handleActionClick"
        @batch-action="handleBatchAction"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
        @export="handleExport"
        @row-click="handleRowClick"
      />

      <!-- 操作日志 -->
      <div class="action-log" v-if="actionLogs.length > 0">
        <el-divider>操作日志</el-divider>
        <div class="log-container">
          <div
            v-for="(log, index) in actionLogs.slice(-5)"
            :key="index"
            class="log-item"
          >
            <span class="log-time">{{ log.time }}</span>
            <span class="log-action">{{ log.action }}</span>
            <span class="log-detail">{{ log.detail }}</span>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Edit, Delete, View, Setting } from '@element-plus/icons-vue'
import EnhancedDataTable from './EnhancedDataTable.vue'
import { exportTableData } from '@/utils/exportUtils'

// 响应式数据
const tableRef = ref()
const tableData = ref<any[]>([])
const totalCount = ref(0)
const currentPage = ref(1)
const actionLogs = ref<any[]>([])

// 表格配置
const tableConfig = reactive({
  searchable: true,
  selectable: true,
  showIndex: true,
  stripe: true,
  size: 'default' as 'large' | 'default' | 'small',
  pageSize: 20,
  loading: false
})

// 表格列定义
const tableColumns = [
  {
    key: 'id',
    label: 'ID',
    width: 80,
    sortable: true
  },
  {
    key: 'name',
    label: '姓名',
    width: 120,
    sortable: true
  },
  {
    key: 'email',
    label: '邮箱',
    width: 200,
    showOverflowTooltip: true
  },
  {
    key: 'department',
    label: '部门',
    width: 120,
    sortable: true
  },
  {
    key: 'position',
    label: '职位',
    width: 150
  },
  {
    key: 'salary',
    label: '薪资',
    width: 100,
    align: 'right' as const,
    formatter: (value: number) => `¥${value?.toLocaleString() || 0}`
  },
  {
    key: 'status',
    label: '状态',
    width: 100,
    align: 'center' as const,
    formatter: (value: string) => {
      const statusMap: Record<string, string> = {
        active: '在职',
        inactive: '离职',
        pending: '待入职'
      }
      return statusMap[value] || value
    }
  },
  {
    key: 'joinDate',
    label: '入职日期',
    width: 120,
    sortable: true,
    formatter: (value: string) => {
      return value ? new Date(value).toLocaleDateString() : ''
    }
  },
  {
    key: 'performance',
    label: '绩效评分',
    width: 100,
    align: 'center' as const,
    formatter: (value: number) => `${value || 0}/100`
  }
]

// 表格操作
const tableActions = [
  {
    key: 'view',
    label: '查看',
    type: 'button' as const,
    buttonType: 'primary' as const,
    icon: View
  },
  {
    key: 'edit',
    label: '编辑',
    type: 'button' as const,
    buttonType: 'warning' as const,
    icon: Edit
  },
  {
    key: 'more',
    label: '更多',
    type: 'dropdown' as const,
    items: [
      {
        key: 'settings',
        label: '设置',
        icon: Setting
      },
      {
        key: 'delete',
        label: '删除',
        icon: Delete
      }
    ]
  }
]

// 批量操作
const batchActions = [
  {
    key: 'batchEdit',
    label: '批量编辑',
    type: 'primary' as const,
    icon: Edit
  },
  {
    key: 'batchDelete',
    label: '批量删除',
    type: 'danger' as const,
    icon: Delete,
    confirm: true,
    confirmText: '确定要删除选中的记录吗？此操作不可撤销。'
  }
]

// 快速筛选
const quickFilters = [
  {
    key: 'active',
    label: '在职员工',
    filter: (data: any[]) => data.filter(item => item.status === 'active')
  },
  {
    key: 'highPerformance',
    label: '高绩效员工',
    filter: (data: any[]) => data.filter(item => item.performance >= 80)
  },
  {
    key: 'newEmployees',
    label: '新员工',
    filter: (data: any[]) => {
      const sixMonthsAgo = new Date()
      sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6)
      return data.filter(item => new Date(item.joinDate) > sixMonthsAgo)
    }
  }
]

// 生成模拟数据
const generateMockData = () => {
  const departments = ['技术部', '产品部', '市场部', '销售部', '人事部', '财务部']
  const positions = ['工程师', '产品经理', '设计师', '分析师', '专员', '主管', '经理']
  const statuses = ['active', 'inactive', 'pending']
  
  const mockData = []
  for (let i = 1; i <= 100; i++) {
    mockData.push({
      id: i,
      name: `员工${i.toString().padStart(3, '0')}`,
      email: `employee${i}@company.com`,
      department: departments[Math.floor(Math.random() * departments.length)],
      position: positions[Math.floor(Math.random() * positions.length)],
      salary: Math.floor(Math.random() * 50000) + 5000,
      status: statuses[Math.floor(Math.random() * statuses.length)],
      joinDate: new Date(2020 + Math.floor(Math.random() * 4), 
                        Math.floor(Math.random() * 12), 
                        Math.floor(Math.random() * 28) + 1).toISOString().split('T')[0],
      performance: Math.floor(Math.random() * 40) + 60
    })
  }
  
  tableData.value = mockData
  totalCount.value = mockData.length
  
  addActionLog('生成数据', `生成了${mockData.length}条测试数据`)
}

// 事件处理
const handleRefresh = () => {
  addActionLog('刷新', '刷新表格数据')
  generateMockData()
}

const handleSearch = (keyword: string) => {
  addActionLog('搜索', `搜索关键词: ${keyword}`)
}

const handleSelectionChange = (selection: any[]) => {
  addActionLog('选择变更', `选中${selection.length}条记录`)
}

const handleActionClick = (action: string, row: any, index: number) => {
  addActionLog('行操作', `对${row.name}执行${action}操作`)
  
  switch (action) {
    case 'view':
      ElMessage.info(`查看员工: ${row.name}`)
      break
    case 'edit':
      ElMessage.warning(`编辑员工: ${row.name}`)
      break
    case 'delete':
      ElMessage.error(`删除员工: ${row.name}`)
      break
    case 'settings':
      ElMessage.info(`设置员工: ${row.name}`)
      break
  }
}

const handleBatchAction = (action: string, rows: any[]) => {
  addActionLog('批量操作', `对${rows.length}条记录执行${action}操作`)
  
  switch (action) {
    case 'batchEdit':
      ElMessage.success(`批量编辑${rows.length}条记录`)
      break
    case 'batchDelete':
      ElMessage.success(`批量删除${rows.length}条记录`)
      // 实际删除逻辑
      const ids = rows.map(row => row.id)
      tableData.value = tableData.value.filter(item => !ids.includes(item.id))
      totalCount.value = tableData.value.length
      break
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  addActionLog('分页', `切换到第${page}页`)
}

const handleSizeChange = (size: number) => {
  tableConfig.pageSize = size
  addActionLog('分页大小', `每页显示${size}条`)
}

const handleExport = (format: string, data: any[]) => {
  addActionLog('导出', `导出${format}格式，共${data.length}条记录`)
  
  exportTableData(
    data,
    tableColumns.filter(col => col.key !== 'actions'),
    format as any,
    `employee-data.${format}`
  )
  
  ElMessage.success(`导出${format}格式成功`)
}

const handleRowClick = (row: any) => {
  addActionLog('行点击', `点击了${row.name}的行`)
}

const clearSelection = () => {
  tableRef.value?.clearSelection()
  addActionLog('清空选择', '清空了所有选择')
}

const addActionLog = (action: string, detail: string) => {
  actionLogs.value.push({
    time: new Date().toLocaleTimeString(),
    action,
    detail
  })
}

// 组件挂载
onMounted(() => {
  generateMockData()
})
</script>

<style lang="scss" scoped>
.table-example {
  padding: 20px;

  .example-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .config-panel {
    margin-bottom: 20px;
    padding: 16px;
    background: var(--bg-secondary);
    border-radius: var(--radius-md);
  }

  .action-log {
    margin-top: 20px;

    .log-container {
      max-height: 200px;
      overflow-y: auto;
      background: var(--bg-tertiary);
      border-radius: var(--radius-md);
      padding: 12px;

      .log-item {
        display: flex;
        gap: 12px;
        padding: 4px 0;
        font-size: 12px;
        border-bottom: 1px solid var(--border-light);

        &:last-child {
          border-bottom: none;
        }

        .log-time {
          color: var(--text-light);
          min-width: 80px;
        }

        .log-action {
          color: var(--primary-color);
          font-weight: 500;
          min-width: 60px;
        }

        .log-detail {
          color: var(--text-secondary);
          flex: 1;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .table-example {
    padding: 10px;
    
    .config-panel {
      .el-row {
        .el-col {
          margin-bottom: 10px;
        }
      }
    }
  }
}
</style>