<template>
  <div class="drag-test">
    <h2>拖拽功能测试</h2>
    
    <div class="test-section">
      <h3>基础拖拽测试</h3>
      <div class="drag-buttons">
        <el-button type="primary" @click="initBasicDrag">启用基础拖拽</el-button>
        <el-button @click="resetOrder">重置顺序</el-button>
      </div>
      
      <div ref="basicDragContainer" class="basic-drag-container">
        <div
          v-for="(item, index) in testItems"
          :key="item.id"
          :data-id="item.id"
          class="drag-item"
        >
          <div class="drag-handle">
            <el-icon><Rank /></el-icon>
          </div>
          <div class="item-content">
            <div class="item-text">{{ item.text }}</div>
            <div class="item-order">顺序: {{ index + 1 }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="test-section">
      <h3>汉字拖拽测试</h3>
      <div class="drag-buttons">
        <el-button type="success" @click="initCharacterDrag">启用汉字拖拽</el-button>
        <el-button @click="saveCharacterOrder">保存顺序</el-button>
      </div>
      
      <div ref="characterDragContainer" class="character-drag-container">
        <div
          v-for="(char, index) in testCharacters"
          :key="char.id"
          :data-id="char.id"
          class="character-drag-item"
        >
          <div class="drag-handle">
            <el-icon><Rank /></el-icon>
          </div>
          <div class="character-display">{{ char.character }}</div>
          <div class="character-order">{{ index + 1 }}</div>
          <div class="character-info">
            <div class="pinyin">{{ char.pinyin }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="debug-info">
      <h3>调试信息</h3>
      <pre>{{ debugInfo }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Rank } from '@element-plus/icons-vue'

interface TestItem {
  id: number
  text: string
}

interface TestCharacter {
  id: number
  character: string
  pinyin: string
  order: number
}

const basicDragContainer = ref<HTMLElement>()
const characterDragContainer = ref<HTMLElement>()
let basicSortableInstance: any = null
let characterSortableInstance: any = null

const testItems = ref<TestItem[]>([
  { id: 1, text: '第一项' },
  { id: 2, text: '第二项' },
  { id: 3, text: '第三项' },
  { id: 4, text: '第四项' },
  { id: 5, text: '第五项' }
])

const testCharacters = ref<TestCharacter[]>([
  { id: 1, character: '一', pinyin: 'yī', order: 1 },
  { id: 2, character: '二', pinyin: 'èr', order: 2 },
  { id: 3, character: '三', pinyin: 'sān', order: 3 },
  { id: 4, character: '四', pinyin: 'sì', order: 4 },
  { id: 5, character: '五', pinyin: 'wǔ', order: 5 }
])

const debugInfo = ref('')

const initBasicDrag = async () => {
  if (!basicDragContainer.value) {
    ElMessage.error('容器未找到')
    return
  }

  try {
    const { default: Sortable } = await import('sortablejs')
    
    if (basicSortableInstance) {
      basicSortableInstance.destroy()
    }

    await nextTick()

    basicSortableInstance = new Sortable(basicDragContainer.value, {
      animation: 300,
      handle: '.drag-handle',
      ghostClass: 'sortable-ghost',
      chosenClass: 'sortable-chosen',
      dragClass: 'sortable-drag',
      onStart: (evt) => {
        debugInfo.value = `开始拖拽: ${evt.oldIndex}`
        ElMessage.info('开始拖拽')
      },
      onEnd: (evt) => {
        const { oldIndex, newIndex } = evt
        debugInfo.value = `拖拽结束: ${oldIndex} -> ${newIndex}`
        
        if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
          // 更新数组顺序
          const movedItem = testItems.value.splice(oldIndex, 1)[0]
          testItems.value.splice(newIndex, 0, movedItem)
          
          ElMessage.success(`移动成功: ${oldIndex} -> ${newIndex}`)
        }
      }
    })

    ElMessage.success('基础拖拽已启用')
  } catch (error) {
    console.error('拖拽初始化失败:', error)
    ElMessage.error('拖拽初始化失败')
  }
}

const initCharacterDrag = async () => {
  if (!characterDragContainer.value) {
    ElMessage.error('汉字容器未找到')
    return
  }

  try {
    const { default: Sortable } = await import('sortablejs')
    
    if (characterSortableInstance) {
      characterSortableInstance.destroy()
    }

    await nextTick()

    characterSortableInstance = new Sortable(characterDragContainer.value, {
      animation: 300,
      handle: '.drag-handle',
      ghostClass: 'sortable-ghost',
      chosenClass: 'sortable-chosen',
      dragClass: 'sortable-drag',
      onStart: (evt) => {
        debugInfo.value = `汉字拖拽开始: ${evt.oldIndex}`
        document.body.style.cursor = 'grabbing'
      },
      onEnd: (evt) => {
        document.body.style.cursor = ''
        const { oldIndex, newIndex } = evt
        debugInfo.value = `汉字拖拽结束: ${oldIndex} -> ${newIndex}`
        
        if (oldIndex !== undefined && newIndex !== undefined && oldIndex !== newIndex) {
          // 更新数组顺序
          const movedItem = testCharacters.value.splice(oldIndex, 1)[0]
          testCharacters.value.splice(newIndex, 0, movedItem)
          
          // 更新order字段
          testCharacters.value.forEach((char, index) => {
            char.order = index + 1
          })
          
          ElMessage.success(`汉字移动成功: ${oldIndex} -> ${newIndex}`)
        }
      }
    })

    ElMessage.success('汉字拖拽已启用')
  } catch (error) {
    console.error('汉字拖拽初始化失败:', error)
    ElMessage.error('汉字拖拽初始化失败')
  }
}

const resetOrder = () => {
  testItems.value = [
    { id: 1, text: '第一项' },
    { id: 2, text: '第二项' },
    { id: 3, text: '第三项' },
    { id: 4, text: '第四项' },
    { id: 5, text: '第五项' }
  ]
  ElMessage.info('顺序已重置')
}

const saveCharacterOrder = () => {
  const orderInfo = testCharacters.value.map(char => `${char.character}(${char.order})`).join(', ')
  ElMessage.success(`当前汉字顺序: ${orderInfo}`)
  debugInfo.value = `保存的汉字顺序: ${JSON.stringify(testCharacters.value.map(c => ({ character: c.character, order: c.order })), null, 2)}`
}
</script>

<style lang="scss" scoped>
.drag-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;

  h2 {
    color: #303133;
    margin-bottom: 30px;
  }

  .test-section {
    margin-bottom: 40px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    h3 {
      color: #606266;
      margin-bottom: 20px;
    }

    .drag-buttons {
      margin-bottom: 20px;
      display: flex;
      gap: 12px;
    }
  }

  .basic-drag-container {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .drag-item {
      display: flex;
      align-items: center;
      padding: 16px;
      background: #f5f7fa;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      cursor: move;
      transition: all 0.3s ease;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
      }

      .drag-handle {
        margin-right: 12px;
        color: #909399;
        cursor: grab;
        padding: 4px;

        &:hover {
          color: #409eff;
        }

        &:active {
          cursor: grabbing;
        }
      }

      .item-content {
        flex: 1;

        .item-text {
          font-size: 16px;
          color: #303133;
          margin-bottom: 4px;
        }

        .item-order {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }

  .character-drag-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;

    .character-drag-item {
      position: relative;
      background: white;
      border: 2px solid #e4e7ed;
      border-radius: 8px;
      padding: 16px;
      cursor: move;
      transition: all 0.3s ease;

      &:hover {
        border-color: #409eff;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
        transform: translateY(-2px);
      }

      .drag-handle {
        position: absolute;
        top: 8px;
        right: 8px;
        color: #909399;
        cursor: grab;
        padding: 4px;

        &:hover {
          color: #409eff;
        }

        &:active {
          cursor: grabbing;
        }
      }

      .character-display {
        font-size: 48px;
        font-weight: bold;
        text-align: center;
        color: #303133;
        margin-bottom: 8px;
      }

      .character-order {
        position: absolute;
        top: 8px;
        left: 8px;
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

      .character-info {
        text-align: center;

        .pinyin {
          font-size: 16px;
          color: #606266;
        }
      }
    }
  }

  // Sortable.js 拖拽状态样式
  .sortable-ghost {
    opacity: 0.4;
    background: #f5f7fa;
    border: 2px dashed #409eff !important;
    transform: rotate(5deg);
  }

  .sortable-chosen {
    border-color: #409eff !important;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3) !important;
    transform: scale(1.05);
    z-index: 1000;
  }

  .sortable-drag {
    opacity: 0.8;
    transform: rotate(5deg);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2) !important;
  }

  .debug-info {
    background: #f5f7fa;
    padding: 20px;
    border-radius: 8px;
    margin-top: 20px;

    h3 {
      color: #606266;
      margin-bottom: 12px;
    }

    pre {
      background: white;
      padding: 12px;
      border-radius: 4px;
      font-size: 12px;
      color: #303133;
      overflow-x: auto;
    }
  }
}
</style>