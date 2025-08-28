<template>
  <div class="enhanced-data-table">
    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <!-- 搜索框 -->
        <el-input
          v-if="searchable"
          v-model="searchKeyword"
          :placeholder="searchPlaceholder"
          :prefix-icon="Search"
          clearable
          @input="handleSearch"
          class="search-input"
        />
        
        <!-- 快速筛选 -->
        <el-select
          v-if="quickFilters.length > 0"
          v-model="activeQuickFilter"
          placeholder="快速筛选"
          clearable
          @change="handleQuickFilter"
          class="quick-filter"
        >
          <el-option
            v-for="filter in quickFilters"
            :key="filter.key"
            :label="filter.label"
            :value="filter.key"
          />
        </el-select>
      </div>

      <div class="toolbar-right">
        <!-- 列设置 -->
        <el-popover
          v-if="columnConfigurable"
          placement="bottom-end"
          :width="300"
          trigger="click"
        >
          <template #reference>
            <el-button :icon="Setting" circle />
          </template>
          <div class="column-config">
            <div class="config-header">列显示设置</div>
            <el-checkbox-group v-model="visibleColumns">
              <div
                v-for="column in configurableColumns"
                :key="column.key"
                class="column-item"
              >
                <el-checkbox :label="column.key">
                  {{ column.label }}
                </el-checkbox>
              </div>
            </el-checkbox-group>
          </div>
        </el-popover>

        <!-- 导出按钮 -->
        <el-dropdown v-if="exportable" @command="handleExport">
          <el-button :icon="Download">
            导出 <el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="excel">导出Excel</el-dropdown-item>
              <el-dropdown-item command="csv">导出CSV</el-dropdown-item>
              <el-dropdown-item command="json">导出JSON</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 刷新按钮 -->
        <el-button
          v-if="refreshable"
          :icon="Refresh"
          @click="handleRefresh"
          :loading="refreshing"
          circle
        />
      </div>
    </div>

    <!-- 批量操作栏 -->
    <div class="batch-actions" v-if="batchActions.length > 0 && selectedRows.length > 0">
      <div class="batch-info">
        已选择 {{ selectedRows.length }} 项
      </div>
      <div class="batch-buttons">
        <el-button
          v-for="action in batchActions"
          :key="action.key"
          :type="action.type || 'default'"
          :icon="action.icon"
          @click="handleBatchAction(action)"
        >
          {{ action.label }}
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table
      ref="tableRef"
      :data="displayData"
      :loading="loading"
      :height="tableHeight"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="size"
      :row-key="rowKey"
      :default-sort="defaultSort"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
      @row-dblclick="handleRowDblClick"
      class="data-table"
      v-loading="loading"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="selectable"
        type="selection"
        width="55"
        :reserve-selection="true"
      />

      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        :index="getRowIndex"
      />

      <!-- 数据列 -->
      <el-table-column
        v-for="column in displayColumns"
        :key="column.key"
        :prop="column.key"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :fixed="column.fixed"
        :sortable="column.sortable"
        :show-overflow-tooltip="column.showOverflowTooltip !== false"
        :align="column.align || 'left'"
        :header-align="column.headerAlign || column.align || 'left'"
        :resizable="resizable"
      >
        <template #default="{ row, column: tableColumn, $index }">
          <!-- 自定义渲染 -->
          <component
            v-if="column.component"
            :is="column.component"
            :row="row"
            :column="column"
            :index="$index"
            :value="getColumnValue(row, column.key)"
          />
          <!-- 格式化显示 -->
          <span v-else-if="column.formatter">
            {{ column.formatter(getColumnValue(row, column.key), row, $index) }}
          </span>
          <!-- 默认显示 -->
          <span v-else>
            {{ getColumnValue(row, column.key) }}
          </span>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column
        v-if="actions.length > 0"
        label="操作"
        :width="actionColumnWidth"
        :fixed="actionColumnFixed"
        align="center"
      >
        <template #default="{ row, $index }">
          <div class="action-buttons">
            <template v-for="action in getRowActions(row, $index)" :key="action.key">
              <!-- 按钮类型操作 -->
              <el-button
                v-if="action.type === 'button'"
                :type="action.buttonType || 'primary'"
                :size="action.size || 'small'"
                :icon="action.icon"
                :disabled="action.disabled && action.disabled(row, $index)"
                @click="handleAction(action, row, $index)"
                link
              >
                {{ action.label }}
              </el-button>
              
              <!-- 下拉菜单类型操作 -->
              <el-dropdown
                v-else-if="action.type === 'dropdown'"
                @command="(command) => handleDropdownAction(command, row, $index)"
              >
                <el-button :size="action.size || 'small'" link>
                  {{ action.label }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item
                      v-for="item in action.items"
                      :key="item.key"
                      :command="item.key"
                      :disabled="item.disabled && item.disabled(row, $index)"
                    >
                      <el-icon v-if="item.icon" class="dropdown-icon">
                        <component :is="item.icon" />
                      </el-icon>
                      {{ item.label }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </div>
        </template>
      </el-table-column>

      <!-- 空数据提示 -->
      <template #empty>
        <div class="empty-data">
          <el-empty :description="emptyText" />
        </div>
      </template>
    </el-table>

    <!-- 分页器 -->
    <div class="table-pagination" v-if="pagination && totalCount > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="totalCount"
        :page-sizes="pageSizes"
        :layout="paginationLayout"
        :background="true"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Setting,
  Download,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'

// 定义接口
interface TableColumn {
  key: string
  label: string
  width?: string | number
  minWidth?: string | number
  fixed?: boolean | 'left' | 'right'
  sortable?: boolean | 'custom'
  align?: 'left' | 'center' | 'right'
  headerAlign?: 'left' | 'center' | 'right'
  showOverflowTooltip?: boolean
  formatter?: (value: any, row: any, index: number) => string
  component?: any
}

interface TableAction {
  key: string
  label: string
  type?: 'button' | 'dropdown'
  buttonType?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  size?: 'large' | 'default' | 'small'
  icon?: any
  disabled?: (row: any, index: number) => boolean
  visible?: (row: any, index: number) => boolean
  items?: Array<{
    key: string
    label: string
    icon?: any
    disabled?: (row: any, index: number) => boolean
  }>
}

interface BatchAction {
  key: string
  label: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  icon?: any
  confirm?: boolean
  confirmText?: string
}

interface QuickFilter {
  key: string
  label: string
  filter: (data: any[]) => any[]
}

interface EnhancedDataTableProps {
  data: any[]
  columns: TableColumn[]
  loading?: boolean
  pagination?: boolean
  totalCount?: number
  currentPage?: number
  pageSize?: number
  pageSizes?: number[]
  searchable?: boolean
  searchPlaceholder?: string
  selectable?: boolean
  showIndex?: boolean
  stripe?: boolean
  border?: boolean
  size?: 'large' | 'default' | 'small'
  height?: string | number
  maxHeight?: string | number
  rowKey?: string
  defaultSort?: { prop: string; order: 'ascending' | 'descending' }
  actions?: TableAction[]
  batchActions?: BatchAction[]
  quickFilters?: QuickFilter[]
  exportable?: boolean
  refreshable?: boolean
  columnConfigurable?: boolean
  resizable?: boolean
  showToolbar?: boolean
  actionColumnWidth?: string | number
  actionColumnFixed?: boolean | 'left' | 'right'
  emptyText?: string
  paginationLayout?: string
}

// Props定义
const props = withDefaults(defineProps<EnhancedDataTableProps>(), {
  data: () => [],
  columns: () => [],
  loading: false,
  pagination: true,
  totalCount: 0,
  currentPage: 1,
  pageSize: 20,
  pageSizes: () => [10, 20, 50, 100],
  searchable: true,
  searchPlaceholder: '请输入搜索关键词',
  selectable: false,
  showIndex: false,
  stripe: true,
  border: true,
  size: 'default',
  rowKey: 'id',
  actions: () => [],
  batchActions: () => [],
  quickFilters: () => [],
  exportable: true,
  refreshable: true,
  columnConfigurable: true,
  resizable: true,
  showToolbar: true,
  actionColumnWidth: 150,
  actionColumnFixed: 'right',
  emptyText: '暂无数据',
  paginationLayout: 'total, sizes, prev, pager, next, jumper'
})

// Emits定义
const emit = defineEmits([
  'refresh',
  'search',
  'sort-change',
  'selection-change',
  'row-click',
  'row-dblclick',
  'action-click',
  'batch-action',
  'page-change',
  'size-change',
  'export'
])

// 响应式数据
const tableRef = ref()
const searchKeyword = ref('')
const selectedRows = ref<any[]>([])
const refreshing = ref(false)
const activeQuickFilter = ref('')
const visibleColumns = ref<string[]>([])

// 计算属性
const configurableColumns = computed(() => {
  return props.columns.filter(col => col.key !== 'actions')
})

const displayColumns = computed(() => {
  if (!props.columnConfigurable) {
    return props.columns
  }
  return props.columns.filter(col => 
    visibleColumns.value.includes(col.key) || col.key === 'actions'
  )
})

const displayData = computed(() => {
  let result = [...props.data]
  
  // 搜索过滤
  if (searchKeyword.value && props.searchable) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(row => {
      return props.columns.some(col => {
        const value = getColumnValue(row, col.key)
        return String(value).toLowerCase().includes(keyword)
      })
    })
  }
  
  // 快速筛选
  if (activeQuickFilter.value) {
    const filter = props.quickFilters.find(f => f.key === activeQuickFilter.value)
    if (filter) {
      result = filter.filter(result)
    }
  }
  
  return result
})

const tableHeight = computed(() => {
  if (props.height) {
    return props.height
  }
  // 自动计算高度，预留工具栏和分页器空间
  const toolbarHeight = props.showToolbar ? 60 : 0
  const paginationHeight = props.pagination ? 60 : 0
  const batchActionsHeight = selectedRows.value.length > 0 ? 50 : 0
  return `calc(100vh - 200px - ${toolbarHeight + paginationHeight + batchActionsHeight}px)`
})

// 方法
const getColumnValue = (row: any, key: string) => {
  return key.split('.').reduce((obj, k) => obj?.[k], row)
}

const getRowIndex = (index: number) => {
  return (props.currentPage - 1) * props.pageSize + index + 1
}

const getRowActions = (row: any, index: number) => {
  return props.actions.filter(action => 
    !action.visible || action.visible(row, index)
  )
}

const handleSearch = () => {
  emit('search', searchKeyword.value)
}

const handleQuickFilter = () => {
  // 快速筛选逻辑已在计算属性中处理
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
  emit('selection-change', selection)
}

const handleSortChange = (sortInfo: any) => {
  emit('sort-change', sortInfo)
}

const handleRowClick = (row: any, column: any, event: Event) => {
  emit('row-click', row, column, event)
}

const handleRowDblClick = (row: any, column: any, event: Event) => {
  emit('row-dblclick', row, column, event)
}

const handleAction = (action: TableAction, row: any, index: number) => {
  emit('action-click', action.key, row, index)
}

const handleDropdownAction = (command: string, row: any, index: number) => {
  emit('action-click', command, row, index)
}

const handleBatchAction = async (action: BatchAction) => {
  if (action.confirm) {
    try {
      await ElMessageBox.confirm(
        action.confirmText || `确定要执行"${action.label}"操作吗？`,
        '确认操作',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
    } catch {
      return
    }
  }
  
  emit('batch-action', action.key, selectedRows.value)
}

const handleRefresh = () => {
  refreshing.value = true
  emit('refresh')
  setTimeout(() => {
    refreshing.value = false
  }, 1000)
}

const handleExport = (format: string) => {
  emit('export', format, displayData.value)
}

const handleSizeChange = (size: number) => {
  emit('size-change', size)
}

const handleCurrentChange = (page: number) => {
  emit('page-change', page)
}

// 公开方法
const clearSelection = () => {
  tableRef.value?.clearSelection()
}

const toggleRowSelection = (row: any, selected?: boolean) => {
  tableRef.value?.toggleRowSelection(row, selected)
}

const setCurrentRow = (row: any) => {
  tableRef.value?.setCurrentRow(row)
}

const sort = (prop: string, order: string) => {
  tableRef.value?.sort(prop, order)
}

// 初始化
onMounted(() => {
  // 初始化可见列
  visibleColumns.value = props.columns.map(col => col.key)
})

// 暴露方法
defineExpose({
  clearSelection,
  toggleRowSelection,
  setCurrentRow,
  sort,
  getTableRef: () => tableRef.value
})
</script>

<style lang="scss" scoped>
.enhanced-data-table {
  background: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-color);
    background: var(--bg-secondary);

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 12px;

      .search-input {
        width: 300px;
      }

      .quick-filter {
        width: 150px;
      }
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .batch-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 20px;
    background: #f0f9ff;
    border-bottom: 1px solid var(--border-color);

    .batch-info {
      color: var(--text-secondary);
      font-size: 14px;
    }

    .batch-buttons {
      display: flex;
      gap: 8px;
    }
  }

  .data-table {
    .action-buttons {
      display: flex;
      align-items: center;
      gap: 8px;
      justify-content: center;

      .dropdown-icon {
        margin-right: 4px;
      }
    }

    .empty-data {
      padding: 40px 0;
    }
  }

  .table-pagination {
    display: flex;
    justify-content: flex-end;
    padding: 16px 20px;
    border-top: 1px solid var(--border-color);
    background: var(--bg-secondary);
  }

  .column-config {
    .config-header {
      font-weight: 500;
      margin-bottom: 12px;
      padding-bottom: 8px;
      border-bottom: 1px solid var(--border-color);
    }

    .column-item {
      padding: 4px 0;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .enhanced-data-table {
    .table-toolbar {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .toolbar-left,
      .toolbar-right {
        justify-content: center;
      }

      .search-input {
        width: 100%;
      }
    }

    .batch-actions {
      flex-direction: column;
      gap: 8px;
      text-align: center;
    }

    .table-pagination {
      justify-content: center;
      
      :deep(.el-pagination) {
        flex-wrap: wrap;
        justify-content: center;
      }
    }
  }
}
</style>