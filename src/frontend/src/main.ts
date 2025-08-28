import { createApp } from 'vue'

// Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'

// 全局样式
import './styles/global.scss'

// 性能监控和优化
import { initializePerformanceMonitoring } from '@/utils/performance'
import { initializeCacheCleanup } from '@/utils/cache'
import { initializeSmartPreloading } from '@/utils/lazyLoading'

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus)

// 初始化性能监控和优化
initializePerformanceMonitoring()
initializeCacheCleanup()

app.mount('#app')

// 应用挂载后初始化智能预加载
initializeSmartPreloading()
