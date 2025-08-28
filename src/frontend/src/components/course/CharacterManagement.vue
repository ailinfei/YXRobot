<template>
  <div class="character-management">
    <!-- 操作栏 -->
    <div class="action-bar">
      <div class="left-actions">
        <el-button type="primary" @click="handleAddCharacter">
          <el-icon><Plus /></el-icon>
          添加汉字
        </el-button>
      </div>
      <div class="right-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索汉字、拼音或部首"
          :prefix-icon="Search"
          clearable
          style="width: 250px;"
          @input="handleSearch"
        />
        <el-button @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 汉字列表 -->
    <div class="character-list">
      <div class="list-header">
        <div class="sort-controls">
          <span>排序方式：</span>
          <el-select v-model="sortBy" @change="handleSort" style="width: 120px;">
            <el-option label="默认顺序" value="order" />
            <el-option label="笔画数" value="strokeCount" />
            <el-option label="拼音" value="pinyin" />
            <el-option label="难度" value="difficulty" />
          </el-select>
          <el-button 
            :icon="sortOrder === 'asc' ? SortUp : SortDown"
            @click="toggleSortOrder"
            text
          />
        </div>
        <div class="view-controls">
          <el-button 
            type="primary" 
            size="small" 
            @click="enterDragSortMode"
            :disabled="characters.length === 0"
          >
            <el-icon><Rank /></el-icon>
            拖拽排序
          </el-button>
        </div>
      </div>  
    <!-- 拖拽排序模式 -->
      <div v-if="dragSortMode" class="drag-sort-mode">
        <div class="drag-sort-header">
          <div class="mode-info">
            <el-icon><Rank /></el-icon>
            <span>拖拽排序模式</span>
            <el-tag type="info" size="small">拖拽汉字卡片来调整学习顺序</el-tag>
          </div>
          <div class="mode-actions">
            <el-button type="success" @click="saveDragSort">保存排序</el-button>
            <el-button @click="cancelDragSort">取消</el-button>
          </div>
        </div>
        
        <!-- 可拖拽的汉字网格 -->
        <div 
          ref="sortableContainer" 
          class="sortable-character-grid"
          v-loading="loading"
        >
          <div
            v-for="(character, index) in sortableCharacters"
            :key="`sortable-${character.id}`"
            :data-id="character.id"
            class="sortable-character-card"
          >
            <div class="drag-handle" title="拖拽调整顺序">
              <el-icon><Rank /></el-icon>
            </div>
            <div class="character-display">{{ character.character }}</div>
            <div class="character-order">{{ index + 1 }}</div>
            <div class="character-info">
              <div class="pinyin">{{ character.pinyin }}</div>
              <div class="stroke-count">{{ character.strokeCount }}画</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 普通汉字网格 -->
      <div v-else class="character-grid" v-loading="loading">
        <div
          v-for="(character, index) in filteredCharacters"
          :key="character.id"
          class="character-card"
        >
          <div class="card-header">
            <div class="character-display">{{ character.character }}</div>
            <div class="character-order">{{ character.order }}</div>
          </div>
          <div class="card-content">
            <div class="character-info">
              <div class="pinyin">{{ character.pinyin }}</div>
              <div class="stroke-count">{{ character.strokeCount }}画</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Upload,
  Download,
  Search,
  Refresh,
  Edit,
  Delete,
  VideoPlay,
  SortUp,
  SortDown,
  UploadFilled,
  Rank
} from '@element-plus/icons-vue'

// 接口定义
interface Character {
  id: number
  character: string
  pinyin: string
  strokeCount: number
  radical: string
  difficulty: 'easy' | 'medium' | 'hard'
  order: number
  meanings: Record<string, string>
  pronunciationUrl?: string
  strokeOrderUrl?: string
  createdAt: string
  updatedAt: string
}

// Props
interface CharacterManagementProps {
  courseId: number
}

const props = defineProps<CharacterManagementProps>()
const emit = defineEmits<{
  update: []
}>()

// 响应式数据
const loading = ref(false)
const searchKeyword = ref('')
const sortBy = ref('order')
const sortOrder = ref<'asc' | 'desc'>('asc')

// 拖拽排序相关
const dragSortMode = ref(false)
const sortableCharacters = ref<Character[]>([])
const sortableContainer = ref<HTMLElement>()
let sortableInstance: any = null

// 汉字数据
const characters = ref<Character[]>([])

// 计算属性
const filteredCharacters = computed(() => {
  let result = [...characters.value]
  
  // 搜索过滤
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(char =>
      char.character.includes(keyword) ||
      char.pinyin.toLowerCase().includes(keyword) ||
      char.radical.includes(keyword)
    )
  }
  
  return result
})// 方法
const loadCharacters = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    characters.value = [
      {
        id: 1,
        character: '一',
        pinyin: 'yī',
        strokeCount: 1,
        radical: '一',
        difficulty: 'easy',
        order: 1,
        meanings: {
          zh: '数字一，表示单一、第一',
          en: 'One, the number 1',
          ja: '一つ、数字の1',
          ko: '하나, 숫자 1'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      },
      {
        id: 2,
        character: '二',
        pinyin: 'èr',
        strokeCount: 2,
        radical: '二',
        difficulty: 'easy',
        order: 2,
        meanings: {
          zh: '数字二，表示两个',
          en: 'Two, the number 2',
          ja: '二つ、数字の2',
          ko: '둘, 숫자 2'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      },
      {
        id: 3,
        character: '三',
        pinyin: 'sān',
        strokeCount: 3,
        radical: '一',
        difficulty: 'easy',
        order: 3,
        meanings: {
          zh: '数字三，表示三个',
          en: 'Three, the number 3',
          ja: '三つ、数字の3',
          ko: '셋, 숫자 3'
        },
        createdAt: '2024-01-01 10:00:00',
        updatedAt: '2024-01-01 10:00:00'
      }
    ]
  } catch (error) {
    ElMessage.error('加载汉字列表失败')
  } finally {
    loading.value = false
  }
}
const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleSort = () => {
  // 排序逻辑已在计算属性中处理
}

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
}

const handleAddCharacter = () => {
  ElMessage.info('添加汉字功能开发中...')
}



const handleExportCharacters = () => {
  ElMessage.info('导出功能开发中...')
}

const refreshData = () => {
  loadCharacters()
}

// 拖拽排序相关方法
const enterDragSortMode = () => {
  dragSortMode.value = true
  sortableCharacters.value = [...characters.value].sort((a, b) => a.order - b.order)
  
  // 下一个tick后初始化Sortable
  nextTick(() => {
    initSortable()
  })
}

const initSortable = async () => {
  if (!sortableContainer.value) {
    console.error('Sortable container not found')
    return
  }
  
  try {
    // 动态导入Sortable.js
    const { default: Sortable } = await import('sortablejs')
    
    // 销毁之前的实例
    if (sortableInstance) {
      sortableInstance.destroy()
      sortableInstance = null
    }
    
    // 等待DOM更新
    await nextTick()
    
    sortableInstance = new Sortable(sortableContainer.value, {
      animation: 200,
      handle: '.drag-handle',
      ghostClass: 'sortable-ghost',
      chosenClass: 'sortable-chosen',
      dragClass: 'sortable-drag',
      forceFallback: true,
      fallbackClass: 'sortable-fallback',
      fallbackOnBody: true,
      swapThreshold: 0.65,
      scroll: true,
      scrollSensitivity: 30,
      scrollSpeed: 10,
      bubbleScroll: true,
      onStart: (evt) => {
        console.log('Drag started:', evt)
        document.body.style.cursor = 'grabbing'
        // 添加拖拽开始时的样式
        evt.item.style.opacity = '0.8'
      },
      onEnd: (evt) => {
        console.log('Drag ended:', evt)
        document.body.style.cursor = ''
        // 恢复样式
        evt.item.style.opacity = ''
        
        const { oldIndex, newIndex } = evt
        if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
          // 更新排序
          const movedItem = sortableCharacters.value.splice(oldIndex, 1)[0]
          sortableCharacters.value.splice(newIndex, 0, movedItem)
          
          // 更新order字段
          sortableCharacters.value.forEach((char, index) => {
            char.order = index + 1
          })
          
          console.log('Updated order:', sortableCharacters.value.map(c => ({ id: c.id, character: c.character, order: c.order })))
          
          // 显示提示信息
          ElMessage.info('拖拽完成，请点击"保存排序"按钮保存更改')
        }
      },
      onMove: (evt) => {
        return true
      }
    })
    
    console.log('Sortable initialized successfully')
    ElMessage.success('拖拽排序模式已启用')
  } catch (error) {
    console.error('Failed to load Sortable.js:', error)
    ElMessage.error('拖拽排序功能加载失败，请检查网络连接')
    dragSortMode.value = false
  }
}
const saveDragSort = async () => {
  try {
    loading.value = true
    
    // 模拟API调用保存新的排序
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新原始数据
    characters.value = [...sortableCharacters.value]
    
    ElMessage.success('汉字排序已保存')
    exitDragSortMode()
    emit('update')
  } catch (error) {
    ElMessage.error('保存排序失败')
  } finally {
    loading.value = false
  }
}

const cancelDragSort = () => {
  exitDragSortMode()
}

const exitDragSortMode = () => {
  dragSortMode.value = false
  sortableCharacters.value = []
  
  if (sortableInstance) {
    sortableInstance.destroy()
    sortableInstance = null
  }
}

// 生命周期
onMounted(() => {
  loadCharacters()
})

// 组件卸载时清理
onUnmounted(() => {
  if (sortableInstance) {
    sortableInstance.destroy()
    sortableInstance = null
  }
})
</script><style
 lang="scss" scoped>
.character-management {
  .action-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 16px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .left-actions,
    .right-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .character-list {
    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding: 16px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .sort-controls {
        display: flex;
        align-items: center;
        gap: 12px;

        span {
          color: #606266;
          font-size: 14px;
        }
      }

      .view-controls {
        display: flex;
        align-items: center;
        gap: 12px;
      }
    }

    .character-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 16px;
      padding: 20px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

      .character-card {
        position: relative;
        background: #f8f9fa;
        border: 2px solid #e9ecef;
        border-radius: 12px;
        padding: 16px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          border-color: #409eff;
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
          transform: translateY(-2px);
        }

        .card-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          margin-bottom: 12px;

          .character-display {
            font-size: 48px;
            font-weight: bold;
            color: #303133;
            line-height: 1;
          }

          .character-order {
            background: #409eff;
            color: white;
            border-radius: 50%;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            font-weight: bold;
          }
        }

        .card-content {
          .character-info {
            margin-bottom: 12px;

            .pinyin {
              font-size: 16px;
              color: #606266;
              margin-bottom: 4px;
            }

            .stroke-count {
              font-size: 14px;
              color: #909399;
            }
          }
        }
      }
    }

    // 拖拽排序模式样式 - 简化版
    .drag-sort-mode {
      background: #f8fbff;
      border: 2px solid #e1f3ff;
      border-radius: 12px;
      box-shadow: 0 4px 16px rgba(64, 158, 255, 0.1);
      padding: 20px;

      .drag-sort-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding: 16px 20px;
        background: white;
        border-radius: 8px;
        border: 1px solid #e4e7ed;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

        .mode-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .el-icon {
            color: #409eff;
            font-size: 18px;
          }

          span {
            font-weight: 600;
            color: #303133;
            font-size: 16px;
          }

          .el-tag {
            background: #409eff;
            color: white;
            border: none;
            font-weight: 500;
          }
        }

        .mode-actions {
          display: flex;
          gap: 12px;
        }
      }

      .sortable-character-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
        gap: 20px;
        min-height: 200px;
        position: relative;
        z-index: 1;

        .sortable-character-card {
          position: relative;
          background: linear-gradient(145deg, #ffffff 0%, #f8f9fa 100%);
          border: 2px solid #e4e7ed;
          border-radius: 16px;
          padding: 20px;
          cursor: move;
          transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
          user-select: none;
          overflow: hidden;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

          // 顶部装饰条
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #409eff, #67c23a, #e6a23c, #f56c6c);
            opacity: 0;
            transition: opacity 0.3s ease;
          }

          // 悬浮光效
          &::after {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: radial-gradient(circle, rgba(64, 158, 255, 0.1) 0%, transparent 70%);
            opacity: 0;
            transition: opacity 0.3s ease;
            pointer-events: none;
          }

          &:hover {
            border-color: #409eff;
            box-shadow: 0 6px 16px rgba(64, 158, 255, 0.15);
            transform: translateY(-2px);

            .drag-handle {
              opacity: 1;
              background: #409eff;
              color: white;
            }

            .character-display {
              color: #409eff;
            }
          }

          .drag-handle {
            position: absolute;
            top: 12px;
            right: 12px;
            color: #c0c4cc;
            cursor: grab;
            padding: 10px;
            border-radius: 10px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            opacity: 0.7;
            background: rgba(255, 255, 255, 0.9);
            backdrop-filter: blur(8px);
            border: 1px solid rgba(192, 196, 204, 0.3);

            &:hover {
              background: #409eff;
              color: white;
              box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
              transform: scale(1.2) rotate(-5deg);
            }

            &:active {
              cursor: grabbing;
              transform: scale(0.9);
            }
          }

          .character-display {
            font-size: 56px;
            font-weight: 900;
            text-align: center;
            color: #303133;
            margin-bottom: 12px;
            line-height: 1;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
          }

          .character-order {
            position: absolute;
            top: 12px;
            left: 12px;
            background: linear-gradient(135deg, #409eff, #67c23a);
            color: white;
            border-radius: 14px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: bold;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            border: 2px solid rgba(255, 255, 255, 0.8);
          }

          .character-info {
            text-align: center;
            margin-top: 12px;

            .pinyin {
              font-size: 18px;
              color: #606266;
              margin-bottom: 8px;
              font-weight: 500;
              letter-spacing: 1px;
              transition: color 0.3s ease;
            }

            .stroke-count {
              font-size: 14px;
              color: #909399;
              background: rgba(144, 147, 153, 0.1);
              padding: 6px 12px;
              border-radius: 16px;
              display: inline-block;
              font-weight: 500;
              transition: all 0.3s ease;
            }
          }
        }

        // Sortable.js 拖拽状态样式 - 简化版
        .sortable-ghost {
          opacity: 0.5 !important;
          background: #f0f8ff !important;
          border: 2px dashed #409eff !important;
          transform: scale(0.98) !important;
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2) !important;
        }

        .sortable-chosen {
          border-color: #409eff !important;
          box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3) !important;
          transform: scale(1.02) !important;
          z-index: 1000;
          background: #ffffff !important;

          .character-display {
            color: #409eff !important;
          }

          .drag-handle {
            background: #409eff !important;
            color: white !important;
            opacity: 1 !important;
          }
        }

        .sortable-drag {
          opacity: 0.8 !important;
          transform: scale(1.05) !important;
          box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15) !important;
          z-index: 1001;
          background: #ffffff !important;
          border-color: #409eff !important;
        }

        .sortable-fallback {
          opacity: 0.8 !important;
          background: #ffffff !important;
          border: 2px solid #409eff !important;
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
          transform: scale(1.02) !important;
          border-radius: 16px !important;
        }
      }
    }

    // 动画定义
    @keyframes backgroundFloat {
      0%, 100% {
        transform: translate(0, 0) rotate(0deg);
      }
      33% {
        transform: translate(10px, -10px) rotate(1deg);
      }
      66% {
        transform: translate(-5px, 5px) rotate(-1deg);
      }
    }

    @keyframes dragIconPulse {
      0%, 100% {
        transform: scale(1);
        opacity: 1;
      }
      50% {
        transform: scale(1.1);
        opacity: 0.8;
      }
    }

    @keyframes ghostPulse {
      0%, 100% {
        opacity: 0.5;
      }
      50% {
        opacity: 0.3;
      }
    }

    @keyframes rainbowFlow {
      0% {
        background-position: 0% 50%;
      }
      100% {
        background-position: 100% 50%;
      }
    }

    @keyframes chosenPulse {
      0%, 100% {
        transform: scale(1);
      }
      50% {
        transform: scale(1.02);
      }
    }

    @keyframes handleSpin {
      0% {
        transform: scale(1.3) rotate(10deg);
      }
      100% {
        transform: scale(1.3) rotate(370deg);
      }
    }

    @keyframes orderBounce {
      0%, 100% {
        transform: scale(1.2);
      }
      50% {
        transform: scale(1.3);
      }
    }

    @keyframes dragFloat {
      0% {
        transform: translateY(0);
      }
      100% {
        transform: translateY(-3px);
      }
    }

    @keyframes dragHandleRotate {
      0% {
        transform: rotate(0deg);
      }
      100% {
        transform: rotate(360deg);
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .character-management {
    .character-list {
      .character-grid {
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
      }
    }

    .drag-sort-mode {
      .sortable-character-grid {
        grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
      }
    }
  }
}

@media (max-width: 768px) {
  .character-management {
    .action-bar {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;

      .left-actions,
      .right-actions {
        justify-content: center;
      }
    }

    .character-list {
      .list-header {
        flex-direction: column;
        gap: 12px;
        align-items: stretch;
      }

      .character-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 12px;
        padding: 16px;
      }
    }

    .drag-sort-mode {
      .sortable-character-grid {
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      }
    }
  }
}
</style>