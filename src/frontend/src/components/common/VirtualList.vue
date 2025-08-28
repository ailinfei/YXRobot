<template>
  <div class="virtual-list" ref="containerRef" @scroll="handleScroll">
    <div class="virtual-list-phantom" :style="{ height: totalHeight + 'px' }"></div>
    <div class="virtual-list-content" :style="contentStyle">
      <div
        v-for="item in visibleItems"
        :key="getItemKey(item.data)"
        :style="{ height: itemHeight + 'px' }"
        class="virtual-list-item"
      >
        <slot :item="item.data" :index="item.index"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'

interface VirtualListProps {
  items: any[]
  itemHeight: number
  containerHeight?: number
  overscan?: number
  keyField?: string
}

interface VisibleItem {
  data: any
  index: number
}

const props = withDefaults(defineProps<VirtualListProps>(), {
  containerHeight: 400,
  overscan: 5,
  keyField: 'id'
})

const emit = defineEmits<{
  scroll: [scrollTop: number]
  itemsChange: [visibleItems: VisibleItem[]]
}>()

// 响应式数据
const containerRef = ref<HTMLElement>()
const scrollTop = ref(0)
const containerHeight = ref(props.containerHeight)

// 计算属性
const totalHeight = computed(() => props.items.length * props.itemHeight)

const visibleRange = computed(() => {
  const start = Math.floor(scrollTop.value / props.itemHeight)
  const end = Math.min(
    start + Math.ceil(containerHeight.value / props.itemHeight),
    props.items.length
  )
  
  return {
    start: Math.max(0, start - props.overscan),
    end: Math.min(props.items.length, end + props.overscan)
  }
})

const visibleItems = computed(() => {
  const items: VisibleItem[] = []
  for (let i = visibleRange.value.start; i < visibleRange.value.end; i++) {
    items.push({
      data: props.items[i],
      index: i
    })
  }
  return items
})

const contentStyle = computed(() => ({
  transform: `translateY(${visibleRange.value.start * props.itemHeight}px)`
}))

// 方法
const getItemKey = (item: any) => {
  return item[props.keyField] || item.id || JSON.stringify(item)
}

const handleScroll = (event: Event) => {
  const target = event.target as HTMLElement
  scrollTop.value = target.scrollTop
  emit('scroll', scrollTop.value)
}

const scrollToIndex = (index: number, behavior: ScrollBehavior = 'smooth') => {
  if (!containerRef.value) return
  
  const targetScrollTop = index * props.itemHeight
  containerRef.value.scrollTo({
    top: targetScrollTop,
    behavior
  })
}

const scrollToTop = (behavior: ScrollBehavior = 'smooth') => {
  scrollToIndex(0, behavior)
}

const scrollToBottom = (behavior: ScrollBehavior = 'smooth') => {
  scrollToIndex(props.items.length - 1, behavior)
}

// 监听容器尺寸变化
const resizeObserver = ref<ResizeObserver>()

const updateContainerHeight = () => {
  if (containerRef.value) {
    containerHeight.value = containerRef.value.clientHeight
  }
}

// 生命周期
onMounted(() => {
  updateContainerHeight()
  
  if (containerRef.value && 'ResizeObserver' in window) {
    resizeObserver.value = new ResizeObserver(() => {
      updateContainerHeight()
    })
    resizeObserver.value.observe(containerRef.value)
  }
})

onUnmounted(() => {
  if (resizeObserver.value) {
    resizeObserver.value.disconnect()
  }
})

// 监听可见项变化
watch(visibleItems, (newItems) => {
  emit('itemsChange', newItems)
}, { deep: true })

// 暴露方法
defineExpose({
  scrollToIndex,
  scrollToTop,
  scrollToBottom,
  getVisibleRange: () => visibleRange.value
})
</script>

<style lang="scss" scoped>
.virtual-list {
  position: relative;
  overflow: auto;
  height: 100%;

  .virtual-list-phantom {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    z-index: -1;
  }

  .virtual-list-content {
    position: relative;
    z-index: 1;
  }

  .virtual-list-item {
    box-sizing: border-box;
  }
}
</style>