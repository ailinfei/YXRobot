<template>
  <div class="image-container" :class="containerClass">
    <img
      v-if="!isLoading || currentSrc !== placeholder"
      :src="currentSrc"
      :alt="alt"
      :class="[imgClass, { 'image-error': hasError, 'image-loaded': !isLoading }]"
      @error="handleError"
      @load="handleLoad"
      v-bind="$attrs"
    />
    <div v-if="isLoading && showLoader" class="image-loader">
      <div class="loader-spinner"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { checkImageExists } from '@/utils/imageOptimizer'

interface Props {
  src: string
  alt: string
  fallback?: string
  placeholder?: string
  imgClass?: string
  containerClass?: string
  showLoader?: boolean
  lazy?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  fallback: 'https://picsum.photos/200/150?random=fallback',
  placeholder: 'https://picsum.photos/200/150?random=loading',
  imgClass: '',
  containerClass: '',
  showLoader: true,
  lazy: false
})

const hasError = ref(false)
const isLoading = ref(true)
const imageRef = ref<HTMLImageElement>()

const currentSrc = computed(() => {
  if (hasError.value) {
    return props.fallback
  }
  if (isLoading.value && props.showLoader) {
    return props.placeholder
  }
  return props.src
})

const handleError = async () => {
  // 尝试使用fallback图片
  if (props.src !== props.fallback) {
    const fallbackExists = await checkImageExists(props.fallback)
    if (fallbackExists) {
      hasError.value = true
      isLoading.value = false
      return
    }
  }
  
  // 如果fallback也失败，使用默认状态图片
  hasError.value = true
  isLoading.value = false
}

const handleLoad = () => {
  isLoading.value = false
  hasError.value = false
}

const loadImage = async () => {
  if (!props.src) {
    hasError.value = true
    isLoading.value = false
    return
  }

  isLoading.value = true
  hasError.value = false

  try {
    const exists = await checkImageExists(props.src)
    if (!exists) {
      await handleError()
    }
  } catch (error) {
    console.warn('Image validation failed:', error)
    await handleError()
  }
}

// 监听src变化
watch(() => props.src, () => {
  if (props.src) {
    loadImage()
  }
}, { immediate: true })

onMounted(() => {
  if (props.lazy) {
    // 实现懒加载逻辑
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          loadImage()
          observer.disconnect()
        }
      })
    })
    
    if (imageRef.value) {
      observer.observe(imageRef.value)
    }
  } else {
    loadImage()
  }
})
</script>

<style scoped>
.image-container {
  position: relative;
  display: inline-block;
  overflow: hidden;
}

img {
  transition: all 0.3s ease;
  max-width: 100%;
  height: auto;
}

.image-loaded {
  opacity: 1;
}

.image-error {
  opacity: 0.7;
  filter: grayscale(0.5);
}

.image-loader {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loader-spinner {
  width: 24px;
  height: 24px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #6366f1;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式图片 */
@media (max-width: 768px) {
  img {
    width: 100%;
    height: auto;
  }
}

/* 图片悬停效果 */
.image-container:hover img:not(.image-error) {
  transform: scale(1.02);
}

/* 加载状态的占位效果 */
img[src*="loading"] {
  opacity: 0.6;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

/* 错误状态样式 */
img[src*="no-data"] {
  opacity: 0.8;
  filter: grayscale(0.3);
  border: 1px dashed #ddd;
}
</style>