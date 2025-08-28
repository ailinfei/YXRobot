<template>
  <span ref="countRef">{{ displayValue }}</span>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'

interface CountUpProps {
  value: number
  duration?: number
  decimals?: number
  prefix?: string
  suffix?: string
}

const props = withDefaults(defineProps<CountUpProps>(), {
  duration: 2000,
  decimals: 0,
  prefix: '',
  suffix: ''
})

const countRef = ref<HTMLElement>()
const displayValue = ref('')
const currentValue = ref(0)

// 数字动画函数
const animateValue = (start: number, end: number, duration: number) => {
  const startTime = Date.now()
  const difference = end - start
  
  const step = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    
    // 使用缓动函数
    const easeOutQuart = 1 - Math.pow(1 - progress, 4)
    const current = start + (difference * easeOutQuart)
    
    currentValue.value = current
    displayValue.value = props.prefix + current.toFixed(props.decimals) + props.suffix
    
    if (progress < 1) {
      requestAnimationFrame(step)
    } else {
      currentValue.value = end
      displayValue.value = props.prefix + end.toFixed(props.decimals) + props.suffix
    }
  }
  
  requestAnimationFrame(step)
}

// 监听值变化
watch(() => props.value, (newValue, oldValue) => {
  if (newValue !== oldValue) {
    animateValue(currentValue.value, newValue, props.duration)
  }
}, { immediate: true })

onMounted(() => {
  displayValue.value = props.prefix + props.value.toFixed(props.decimals) + props.suffix
  currentValue.value = props.value
})
</script>

<style scoped>
span {
  font-weight: inherit;
  color: inherit;
  font-size: inherit;
}
</style>