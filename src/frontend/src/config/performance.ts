/**
 * 性能优化配置
 */

// 缓存配置
export const CACHE_CONFIG = {
  // 内存缓存配置
  memory: {
    maxSize: 200,
    defaultTTL: 10 * 60 * 1000, // 10分钟
    cleanupInterval: 5 * 60 * 1000 // 5分钟清理一次
  },
  
  // 本地存储缓存配置
  localStorage: {
    prefix: 'yxrobot_cache_',
    defaultTTL: 24 * 60 * 60 * 1000 // 24小时
  },
  
  // 请求缓存配置
  request: {
    // API缓存时间配置（毫秒）
    apiTTL: {
      // 静态数据缓存时间较长
      products: 30 * 60 * 1000, // 30分钟
      charity: 15 * 60 * 1000,  // 15分钟
      languages: 60 * 60 * 1000, // 1小时
      
      // 动态数据缓存时间较短
      customers: 5 * 60 * 1000,  // 5分钟
      devices: 2 * 60 * 1000,    // 2分钟
      orders: 3 * 60 * 1000,     // 3分钟
      
      // 实时数据缓存时间很短
      dashboard: 1 * 60 * 1000,  // 1分钟
      monitoring: 30 * 1000,     // 30秒
    }
  }
}

// 懒加载配置
export const LAZY_LOADING_CONFIG = {
  // 图片懒加载配置
  image: {
    rootMargin: '50px',
    threshold: 0.1
  },
  
  // 组件懒加载配置
  component: {
    delay: 200,
    timeout: 30000,
    suspensible: false
  },
  
  // 路由懒加载配置
  route: {
    preloadDelay: 1000, // 预加载延迟
    prefetchOnHover: true, // 鼠标悬停时预加载
    prefetchOnVisible: true // 可见时预加载
  }
}

// 虚拟滚动配置
export const VIRTUAL_SCROLL_CONFIG = {
  // 默认配置
  default: {
    itemHeight: 60,
    overscan: 5,
    threshold: 1000 // 超过1000条数据启用虚拟滚动
  },
  
  // 不同场景的配置
  scenarios: {
    // 客户列表
    customers: {
      itemHeight: 80,
      overscan: 3,
      threshold: 500
    },
    
    // 设备列表
    devices: {
      itemHeight: 70,
      overscan: 5,
      threshold: 800
    },
    
    // 订单列表
    orders: {
      itemHeight: 90,
      overscan: 3,
      threshold: 600
    },
    
    // 日志列表
    logs: {
      itemHeight: 40,
      overscan: 10,
      threshold: 2000
    }
  }
}

// 防抖节流配置
export const DEBOUNCE_THROTTLE_CONFIG = {
  // 搜索防抖
  search: 300,
  
  // 输入防抖
  input: 500,
  
  // 窗口大小调整节流
  resize: 100,
  
  // 滚动节流
  scroll: 16, // 约60fps
  
  // API请求防抖
  apiRequest: 1000,
  
  // 保存操作防抖
  save: 2000
}

// 性能监控配置
export const PERFORMANCE_MONITOR_CONFIG = {
  // 是否启用性能监控
  enabled: process.env.NODE_ENV === 'development',
  
  // 监控指标
  metrics: {
    // 页面加载时间
    pageLoad: true,
    
    // 首次内容绘制
    firstContentfulPaint: true,
    
    // 最大内容绘制
    largestContentfulPaint: true,
    
    // 首次输入延迟
    firstInputDelay: true,
    
    // 累积布局偏移
    cumulativeLayoutShift: true,
    
    // 自定义性能标记
    customMarks: true
  },
  
  // 性能阈值（毫秒）
  thresholds: {
    pageLoad: 3000,
    firstContentfulPaint: 1500,
    largestContentfulPaint: 2500,
    firstInputDelay: 100,
    cumulativeLayoutShift: 0.1
  },
  
  // 采样率
  sampleRate: 0.1 // 10%的用户
}

// 资源预加载配置
export const PRELOAD_CONFIG = {
  // 关键资源预加载
  critical: [
    // 关键CSS - 使用网络CDN
    'https://cdn.jsdelivr.net/npm/normalize.css@8.0.1/normalize.min.css',
    
    // 关键字体 - 使用Google Fonts
    'https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap',
    
    // 关键图片
    'https://picsum.photos/200/60?random=logo'
  ],
  
  // 组件预加载策略
  components: {
    // 高优先级组件（立即预加载）
    high: [
      'Dashboard',
      'CustomerManagement',
      'DeviceManagement'
    ],
    
    // 中优先级组件（空闲时预加载）
    medium: [
      'ProductManagement',
      'CharityManagement',
      'OrderManagement'
    ],
    
    // 低优先级组件（用户交互时预加载）
    low: [
      'LanguageManagement',
      'FontPackageManagement',
      'SystemSettings'
    ]
  },
  
  // 预加载时机
  timing: {
    // 页面加载完成后延迟
    afterLoad: 2000,
    
    // 空闲时间预加载
    onIdle: true,
    
    // 鼠标悬停预加载
    onHover: true,
    
    // 视口内预加载
    onVisible: true
  }
}

// 代码分割配置
export const CODE_SPLITTING_CONFIG = {
  // 按路由分割
  routes: {
    // 管理后台
    admin: [
      'dashboard',
      'content',
      'business',
      'device',
      'system'
    ],
    
    // 官网
    website: [
      'home',
      'products',
      'charity',
      'support',
      'contact'
    ]
  },
  
  // 按功能分割
  features: {
    // 图表库
    charts: ['echarts', 'chart-components'],
    
    // 地图组件
    maps: ['map-visualization', 'geo-data'],
    
    // 富文本编辑器
    editor: ['rich-text-editor', 'markdown-editor'],
    
    // 文件上传
    upload: ['file-upload', 'image-processing'],
    
    // 多语言
    i18n: ['language-manager', 'translation-tools']
  },
  
  // 第三方库分割
  vendors: {
    // 核心库
    core: ['vue', 'vue-router', 'pinia'],
    
    // UI库
    ui: ['element-plus', '@element-plus/icons-vue'],
    
    // 工具库
    utils: ['axios', 'dayjs', 'lodash-es'],
    
    // 图表库
    charts: ['echarts'],
    
    // 其他
    others: ['crypto-js', 'file-saver']
  }
}

// 内存优化配置
export const MEMORY_OPTIMIZATION_CONFIG = {
  // 组件缓存配置
  componentCache: {
    // 最大缓存组件数
    maxSize: 20,
    
    // 缓存策略
    strategy: 'lru', // lru | fifo
    
    // 排除缓存的组件
    exclude: [
      'DeviceMonitoring', // 实时监控组件不缓存
      'LiveChat',         // 实时聊天组件不缓存
    ]
  },
  
  // 数据清理配置
  dataCleanup: {
    // 定时清理间隔（毫秒）
    interval: 5 * 60 * 1000, // 5分钟
    
    // 清理策略
    strategies: [
      'expired-cache',    // 清理过期缓存
      'unused-data',      // 清理未使用数据
      'large-objects',    // 清理大对象
      'event-listeners'   // 清理事件监听器
    ]
  },
  
  // 内存监控
  monitoring: {
    // 是否启用内存监控
    enabled: true,
    
    // 内存使用阈值（MB）
    threshold: 100,
    
    // 监控间隔（毫秒）
    interval: 30 * 1000, // 30秒
    
    // 内存泄漏检测
    leakDetection: true
  }
}

// 网络优化配置
export const NETWORK_OPTIMIZATION_CONFIG = {
  // HTTP缓存配置
  httpCache: {
    // 静态资源缓存时间
    static: {
      images: '1y',      // 图片缓存1年
      fonts: '1y',       // 字体缓存1年
      css: '1y',         // CSS缓存1年
      js: '1y',          // JS缓存1年
    },
    
    // API缓存配置
    api: {
      'GET /api/products': '30m',     // 产品数据缓存30分钟
      'GET /api/charity': '15m',      // 公益数据缓存15分钟
      'GET /api/customers': '5m',     // 客户数据缓存5分钟
      'GET /api/devices': '2m',       // 设备数据缓存2分钟
    }
  },
  
  // 请求优化
  request: {
    // 并发请求限制
    concurrency: 6,
    
    // 请求超时时间
    timeout: 10000,
    
    // 重试配置
    retry: {
      times: 3,
      delay: 1000
    },
    
    // 请求合并
    batching: {
      enabled: true,
      delay: 50,
      maxSize: 10
    }
  },
  
  // 资源压缩
  compression: {
    // Gzip压缩
    gzip: true,
    
    // Brotli压缩
    brotli: true,
    
    // 图片压缩
    images: {
      quality: 85,
      progressive: true,
      webp: true
    }
  }
}

// 导出所有配置
export const PERFORMANCE_CONFIG = {
  cache: CACHE_CONFIG,
  lazyLoading: LAZY_LOADING_CONFIG,
  virtualScroll: VIRTUAL_SCROLL_CONFIG,
  debounceThrottle: DEBOUNCE_THROTTLE_CONFIG,
  performanceMonitor: PERFORMANCE_MONITOR_CONFIG,
  preload: PRELOAD_CONFIG,
  codeSplitting: CODE_SPLITTING_CONFIG,
  memoryOptimization: MEMORY_OPTIMIZATION_CONFIG,
  networkOptimization: NETWORK_OPTIMIZATION_CONFIG
}

export default PERFORMANCE_CONFIG