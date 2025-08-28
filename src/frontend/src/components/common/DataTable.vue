<template>
  <div class="data-table">


    <!-- 数据表格 -->
    <el-table
      ref="tableRef"
      :data="filteredData"
      :loading="loading"
      :height="tableHeight"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="size"
      :empty-text="emptyText"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
      @row-dblclick="handleRowDblClick"
      class="data-table-main"
    >
      <!-- 选择列 -->
      <el-table-column 
        v-if="showSelection" 
        type="selection" 
        width="55" 
        align="center"
        fixed="left"
      />
      
      <!-- 序号列 -->
      <el-table-column 
        v-if="showIndex" 
        type="index" 
        label="序号" 
        width="80" 
        align="center"
        fixed="left"
        :index="getIndex"
      />
      
      <!-- 动态列 -->
      <template v-for="column in visibleColumns" :key="column.prop">
        <el-table-column
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :min-width="column.minWidth"
          :fixed="column.fixed"
          :sortable="column.sortable"
          :align="column.align || 'left'"
          :show-overflow-tooltip="column.showOverflowTooltip !== false"
        >
          <template #default="{ row, column: col, $index }">
            <slot 
              :name="column.prop" 
              :row="row" 
              :column="col" 
              :index="$index"
              :value="row[column.prop]"
            >
              <!-- 默认渲染 -->
              <template v-if="column.type === 'tag'">
                <el-tag 
                  :type="getTagType(row[column.prop], column.tagMap)"
                  :size="size"
                >
                  {{ getTagText(row[column.prop], column.tagMap) }}
                </el-tag>
              </template>
              
              <template v-else-if="column.type === 'image'">
                <el-image
                  :src="row[column.prop]"
                  :preview-src-list="[row[column.prop]]"
                  fit="cover"
                  class="table-image"
                />
              </template>
              
              <template v-else-if="column.type === 'date'">
                {{ formatDate(row[column.prop], column.format) }}
              </template>
              
              <template v-else-if="column.type === 'currency'">
                ¥{{ formatCurrency(row[column.prop]) }}
              </template>
              
              <template v-else>
                {{ row[column.prop] }}
              </template>
            </slot>
          </template>
        </el-table-column>
      </template>
      
      <!-- 操作列 -->
      <el-table-column 
        v-if="showActions" 
        label="操作" 
        :width="actionWidth"
        fixed="right"
        align="center"
      >
        <template #default="{ row, $index }">
          <slot name="actions" :row="row" :index="$index">
            <el-button 
              v-if="showEdit"
              type="primary" 
              size="small" 
              text
              @click="handleEdit(row, $index)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="showDelete"
              type="danger" 
              size="small" 
              text
              @click="handleDelete(row, $index)"
            >
              删除
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        :total="total"
        :layout="paginationLayout"
        :background="true"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'


// 定义接口
interface TableColumn {
  prop: string
  label: string
  width?: number
  minWidth?: number
  fixed?: string | boolean
  sortable?: boolean | string
  align?: string
  showOverflowTooltip?: boolean
  type?: 'text' | 'tag' | 'image' | 'date' | 'currency'
  format?: string
  tagMap?: Record<string, { text: string; type: string }>
}

interface TableProps {
  data: any[]
  columns: TableColumn[]
  loading?: boolean

  showSelection?: boolean
  showIndex?: boolean
  showActions?: boolean
  showEdit?: boolean
  showDelete?: boolean
  showPagination?: boolean
  tableHeight?: string | number
  maxHeight?: string | number
  stripe?: boolean
  border?: boolean
  size?: 'large' | 'default' | 'small'
  emptyText?: string
  actionWidth?: number
  pageSizes?: number[]
  paginationLayout?: string
}

// Props定义
const props = withDefaults(defineProps<TableProps>(), {
  data: () => [],
  columns: () => [],
  loading: false,

  showSelection: false,
  showIndex: true,
  showActions: true,
  showEdit: true,
  showDelete: true,
  showPagination: true,
  stripe: true,
  border: true,
  size: 'default',
  emptyText: '暂无数据',
  actionWidth: 150,
  pageSizes: () => [10, 20, 50, 100],
  paginationLayout: 'total, sizes, prev, pager, next, jumper'
})

// Emits定义
const emit = defineEmits([
  'edit',
  'delete',
  'selection-change',
  'sort-change',
  'row-click',
  'row-dblclick',
  'page-change',
  'size-change'
])

// 响应式数据
const tableRef = ref()
const selectedRows = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const sortField = ref('')
const sortOrder = ref('')

// 计算属性
const visibleColumns = computed(() => {
  return props.columns.filter(col => col.prop !== 'actions')
})

const total = computed(() => {
  return props.data.length
})

const filteredData = computed(() => {
  let result = [...props.data]
  

  
  // 排序
  if (sortField.value && sortOrder.value) {
    result.sort((a, b) => {
      const aVal = a[sortField.value]
      const bVal = b[sortField.value]
      
      if (sortOrder.value === 'ascending') {
        return aVal > bVal ? 1 : -1
      } else {
        return aVal < bVal ? 1 : -1
      }
    })
  }
  
  // 分页
  if (props.showPagination) {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    result = result.slice(start, end)
  }
  
  return result
})

// 方法
const getIndex = (index: number) => {
  return (currentPage.value - 1) * pageSize.value + index + 1
}

const getTagType = (value: any, tagMap?: Record<string, { text: string; type: string }>) => {
  return tagMap?.[value]?.type || 'info'
}

const getTagText = (value: any, tagMap?: Record<string, { text: string; type: string }>) => {
  return tagMap?.[value]?.text || value
}

const formatDate = (date: string | Date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

const formatCurrency = (amount: number) => {
  if (!amount) return '0.00'
  return amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 事件处理
const handleEdit = (row: any, index: number) => {
  emit('edit', row, index)
}

const handleDelete = async (row: any, index: number) => {
  try {
    await ElMessageBox.confirm('确定要删除这条记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    emit('delete', row, index)
  } catch {
    // 用户取消删除
  }
}

const handleSelectionChange = (selection: any[]) => {
  selectedRows.value = selection
  emit('selection-change', selection)
}

const handleSortChange = ({ column, prop, order }: any) => {
  sortField.value = prop
  sortOrder.value = order
  emit('sort-change', { column, prop, order })
}

const handleRowClick = (row: any, column: any, event: Event) => {
  emit('row-click', row, column, event)
}

const handleRowDblClick = (row: any, column: any, event: Event) => {
  emit('row-dblclick', row, column, event)
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  emit('page-change', page)
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  emit('size-change', size)
}



// 暴露方法给父组件
defineExpose({
  clearSelection: () => tableRef.value?.clearSelection(),
  toggleRowSelection: (row: any, selected?: boolean) => tableRef.value?.toggleRowSelection(row, selected),
  setCurrentRow: (row: any) => tableRef.value?.setCurrentRow(row)
})
</script>

<style lang="scss" scoped>
.data-table {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}



.data-table-main {
  :deep(.el-table__header) {
    background: var(--bg-tertiary);
    
    th {
      background: var(--bg-tertiary);
      color: var(--text-primary);
      font-weight: 600;
    }
  }
  
  :deep(.el-table__row) {
    &:hover {
      background: var(--bg-secondary);
    }
  }
  
  .table-image {
    width: 40px;
    height: 40px;
    border-radius: var(--radius-sm);
  }
}

.table-pagination {
  display: flex;
  justify-content: flex-end;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
  background: var(--bg-secondary);
}

// 响应式设计
@media (max-width: 768px) {
  .table-pagination {
    justify-content: center;
    
    :deep(.el-pagination) {
      flex-wrap: wrap;
      justify-content: center;
    }
  }
}
</style>