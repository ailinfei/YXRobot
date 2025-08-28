<template>
  <div class="search-filter">
    <!-- 搜索栏 -->
    <div class="search-bar" v-if="showSearch">
      <el-input
        v-model="searchKeyword"
        :placeholder="searchPlaceholder"
        :prefix-icon="Search"
        clearable
        @input="handleSearch"
        @clear="handleClear"
        class="search-input"
      />
      <el-button 
        type="primary" 
        :icon="Search" 
        @click="handleSearch"
        :loading="searching"
      >
        搜索
      </el-button>
    </div>

    <!-- 过滤器 -->
    <div class="filter-section" v-if="showFilters && filters.length > 0">
      <div class="filter-items">
        <template v-for="filter in filters" :key="filter.key">
          <!-- 选择器过滤 -->
          <div v-if="filter.type === 'select'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <el-select
              v-model="filterValues[filter.key]"
              :placeholder="filter.placeholder || '请选择'"
              clearable
              @change="handleFilterChange"
              class="filter-select"
            >
              <el-option
                v-for="option in filter.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </div>

          <!-- 日期范围过滤 -->
          <div v-else-if="filter.type === 'daterange'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <el-date-picker
              v-model="filterValues[filter.key]"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              @change="handleFilterChange"
              class="filter-date"
            />
          </div>

          <!-- 数字范围过滤 -->
          <div v-else-if="filter.type === 'numberrange'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <div class="number-range">
              <el-input-number
                v-model="filterValues[filter.key + '_min']"
                :placeholder="filter.minPlaceholder || '最小值'"
                :min="filter.min"
                :max="filter.max"
                :step="filter.step || 1"
                @change="handleFilterChange"
                class="range-input"
              />
              <span class="range-separator">-</span>
              <el-input-number
                v-model="filterValues[filter.key + '_max']"
                :placeholder="filter.maxPlaceholder || '最大值'"
                :min="filter.min"
                :max="filter.max"
                :step="filter.step || 1"
                @change="handleFilterChange"
                class="range-input"
              />
            </div>
          </div>

          <!-- 多选过滤 -->
          <div v-else-if="filter.type === 'checkbox'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <el-checkbox-group
              v-model="filterValues[filter.key]"
              @change="handleFilterChange"
              class="filter-checkbox"
            >
              <el-checkbox
                v-for="option in filter.options"
                :key="option.value"
                :label="option.value"
              >
                {{ option.label }}
              </el-checkbox>
            </el-checkbox-group>
          </div>

          <!-- 单选过滤 -->
          <div v-else-if="filter.type === 'radio'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <el-radio-group
              v-model="filterValues[filter.key]"
              @change="handleFilterChange"
              class="filter-radio"
            >
              <el-radio
                v-for="option in filter.options"
                :key="option.value"
                :label="option.value"
              >
                {{ option.label }}
              </el-radio>
            </el-radio-group>
          </div>

          <!-- 输入框过滤 -->
          <div v-else-if="filter.type === 'input'" class="filter-item">
            <label class="filter-label">{{ filter.label }}:</label>
            <el-input
              v-model="filterValues[filter.key]"
              :placeholder="filter.placeholder || '请输入'"
              clearable
              @input="handleFilterChange"
              class="filter-input"
            />
          </div>
        </template>
      </div>

      <!-- 过滤器操作 -->
      <div class="filter-actions">
        <el-button 
          type="primary" 
          @click="handleApplyFilters"
          :loading="filtering"
        >
          应用过滤
        </el-button>
      </div>
    </div>

    <!-- 活跃过滤器标签 -->
    <div class="active-filters" v-if="showActiveTags && activeFilterTags.length > 0">
      <span class="active-filters-label">当前过滤:</span>
      <el-tag
        v-for="tag in activeFilterTags"
        :key="tag.key"
        closable
        @close="removeFilter(tag.key)"
        class="filter-tag"
      >
        {{ tag.label }}: {{ tag.value }}
      </el-tag>
      <el-button 
        type="danger" 
        size="small" 
        text
        @click="clearAllFilters"
      >
        清空全部
      </el-button>
    </div>

    <!-- 快速过滤按钮 -->
    <div class="quick-filters" v-if="showQuickFilters && quickFilters.length > 0">
      <span class="quick-filters-label">快速过滤:</span>
      <el-button
        v-for="quick in quickFilters"
        :key="quick.key"
        :type="isQuickFilterActive(quick) ? 'primary' : 'default'"
        size="small"
        @click="applyQuickFilter(quick)"
      >
        {{ quick.label }}
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'

// 定义过滤器接口
interface FilterOption {
  label: string
  value: any
}

interface FilterConfig {
  key: string
  label: string
  type: 'select' | 'daterange' | 'numberrange' | 'checkbox' | 'radio' | 'input'
  placeholder?: string
  minPlaceholder?: string
  maxPlaceholder?: string
  options?: FilterOption[]
  min?: number
  max?: number
  step?: number
}

interface QuickFilter {
  key: string
  label: string
  filters: Record<string, any>
}

interface SearchFilterProps {
  showSearch?: boolean
  showFilters?: boolean
  showActiveTags?: boolean
  showQuickFilters?: boolean
  searchPlaceholder?: string
  filters?: FilterConfig[]
  quickFilters?: QuickFilter[]
  defaultValues?: Record<string, any>
  autoApply?: boolean
  debounceTime?: number
}

// Props定义
const props = withDefaults(defineProps<SearchFilterProps>(), {
  showSearch: true,
  showFilters: true,
  showActiveTags: true,
  showQuickFilters: false,
  searchPlaceholder: '请输入搜索关键词',
  filters: () => [],
  quickFilters: () => [],
  defaultValues: () => ({}),
  autoApply: true,
  debounceTime: 300
})

// Emits定义
const emit = defineEmits([
  'search',
  'filter',
  'reset',
  'clear'
])

// 响应式数据
const searchKeyword = ref('')
const filterValues = ref<Record<string, any>>({})
const searching = ref(false)
const filtering = ref(false)
let searchTimer: NodeJS.Timeout | null = null

// 计算属性
const activeFilterTags = computed(() => {
  const tags: Array<{ key: string; label: string; value: string }> = []
  
  props.filters.forEach(filter => {
    const value = filterValues.value[filter.key]
    if (value !== undefined && value !== null && value !== '' && 
        !(Array.isArray(value) && value.length === 0)) {
      
      let displayValue = value
      
      // 处理不同类型的显示值
      if (filter.type === 'select' || filter.type === 'radio') {
        const option = filter.options?.find(opt => opt.value === value)
        displayValue = option?.label || value
      } else if (filter.type === 'checkbox' && Array.isArray(value)) {
        const labels = value.map(v => {
          const option = filter.options?.find(opt => opt.value === v)
          return option?.label || v
        })
        displayValue = labels.join(', ')
      } else if (filter.type === 'daterange' && Array.isArray(value)) {
        displayValue = `${value[0]} 至 ${value[1]}`
      } else if (filter.type === 'numberrange') {
        const min = filterValues.value[filter.key + '_min']
        const max = filterValues.value[filter.key + '_max']
        if (min !== undefined && max !== undefined) {
          displayValue = `${min} - ${max}`
        } else if (min !== undefined) {
          displayValue = `≥ ${min}`
        } else if (max !== undefined) {
          displayValue = `≤ ${max}`
        }
      }
      
      tags.push({
        key: filter.key,
        label: filter.label,
        value: String(displayValue)
      })
    }
  })
  
  return tags
})

// 方法
const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  
  searchTimer = setTimeout(() => {
    searching.value = true
    emit('search', searchKeyword.value)
    setTimeout(() => {
      searching.value = false
    }, 500)
  }, props.debounceTime)
}

const handleClear = () => {
  searchKeyword.value = ''
  emit('clear')
}

const handleFilterChange = () => {
  if (props.autoApply) {
    applyFilters()
  }
}

const handleApplyFilters = () => {
  applyFilters()
}

const applyFilters = () => {
  filtering.value = true
  
  // 清理空值
  const cleanFilters: Record<string, any> = {}
  Object.keys(filterValues.value).forEach(key => {
    const value = filterValues.value[key]
    if (value !== undefined && value !== null && value !== '' && 
        !(Array.isArray(value) && value.length === 0)) {
      cleanFilters[key] = value
    }
  })
  
  emit('filter', {
    search: searchKeyword.value,
    filters: cleanFilters
  })
  
  setTimeout(() => {
    filtering.value = false
  }, 500)
}

const handleReset = () => {
  searchKeyword.value = ''
  filterValues.value = { ...props.defaultValues }
  emit('reset')
  
  if (props.autoApply) {
    applyFilters()
  }
}

const removeFilter = (key: string) => {
  if (key.endsWith('_min') || key.endsWith('_max')) {
    delete filterValues.value[key]
  } else {
    filterValues.value[key] = undefined
  }
  
  if (props.autoApply) {
    applyFilters()
  }
}

const clearAllFilters = () => {
  filterValues.value = { ...props.defaultValues }
  
  if (props.autoApply) {
    applyFilters()
  }
}

const isQuickFilterActive = (quick: QuickFilter) => {
  return Object.keys(quick.filters).every(key => {
    return filterValues.value[key] === quick.filters[key]
  })
}

const applyQuickFilter = (quick: QuickFilter) => {
  // 如果已经激活，则清除
  if (isQuickFilterActive(quick)) {
    Object.keys(quick.filters).forEach(key => {
      filterValues.value[key] = undefined
    })
  } else {
    // 应用快速过滤
    Object.assign(filterValues.value, quick.filters)
  }
  
  if (props.autoApply) {
    applyFilters()
  }
}

// 初始化
onMounted(() => {
  filterValues.value = { ...props.defaultValues }
})

// 暴露方法给父组件
defineExpose({
  search: handleSearch,
  applyFilters,
  reset: handleReset,
  clearFilters: clearAllFilters,
  getSearchKeyword: () => searchKeyword.value,
  getFilterValues: () => filterValues.value
})
</script>

<style lang="scss" scoped>
.search-filter {
  background: white;
  border-radius: var(--radius-lg);
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  
  .search-input {
    flex: 1;
    max-width: 400px;
  }
}

.filter-section {
  .filter-items {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 16px;
    margin-bottom: 16px;
    
    .filter-item {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .filter-label {
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary);
      }
      
      .filter-select,
      .filter-date,
      .filter-input {
        width: 100%;
      }
      
      .number-range {
        display: flex;
        align-items: center;
        gap: 8px;
        
        .range-input {
          flex: 1;
        }
        
        .range-separator {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .filter-checkbox,
      .filter-radio {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        
        :deep(.el-checkbox),
        :deep(.el-radio) {
          margin-right: 0;
        }
      }
    }
  }
  
  .filter-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid var(--border-color);
  }
}

.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
  
  .active-filters-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-secondary);
    margin-right: 8px;
  }
  
  .filter-tag {
    margin-right: 0;
  }
}

.quick-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
  
  .quick-filters-label {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-secondary);
    margin-right: 8px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .search-filter {
    padding: 16px;
  }
  
  .search-bar {
    flex-direction: column;
    
    .search-input {
      max-width: none;
    }
  }
  
  .filter-section {
    .filter-items {
      grid-template-columns: 1fr;
      gap: 12px;
    }
    
    .filter-actions {
      flex-direction: column;
      
      .el-button {
        width: 100%;
      }
    }
  }
  
  .active-filters,
  .quick-filters {
    flex-direction: column;
    align-items: flex-start;
    
    .active-filters-label,
    .quick-filters-label {
      margin-bottom: 8px;
    }
  }
}
</style>