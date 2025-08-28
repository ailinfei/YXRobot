/**
 * 新闻管理系统常量定义
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

import { NewsStatus, InteractionType } from '@/types/news'
import type { NewsStatusOption, NewsActionButton, NewsTableColumn, NewsFilter } from '@/types/news'

// ==================== 新闻状态相关常量 ====================

/**
 * 新闻状态选项
 */
export const NEWS_STATUS_OPTIONS: NewsStatusOption[] = [
  {
    label: '草稿',
    value: NewsStatus.DRAFT,
    color: '#909399',
    description: '新闻尚未发布，仅作者可见'
  },
  {
    label: '已发布',
    value: NewsStatus.PUBLISHED,
    color: '#67C23A',
    description: '新闻已发布，用户可见'
  },
  {
    label: '已下线',
    value: NewsStatus.OFFLINE,
    color: '#F56C6C',
    description: '新闻已下线，用户不可见'
  }
]

/**
 * 新闻状态颜色映射
 */
export const NEWS_STATUS_COLORS = {
  [NewsStatus.DRAFT]: '#909399',
  [NewsStatus.PUBLISHED]: '#67C23A',
  [NewsStatus.OFFLINE]: '#F56C6C'
} as const

/**
 * 新闻状态类型映射（Element Plus）
 */
export const NEWS_STATUS_TYPES = {
  [NewsStatus.DRAFT]: 'info',
  [NewsStatus.PUBLISHED]: 'success',
  [NewsStatus.OFFLINE]: 'danger'
} as const

// ==================== 互动类型相关常量 ====================

/**
 * 互动类型选项
 */
export const INTERACTION_TYPE_OPTIONS = [
  {
    label: '浏览',
    value: InteractionType.VIEW,
    icon: 'View',
    color: '#409EFF'
  },
  {
    label: '点赞',
    value: InteractionType.LIKE,
    icon: 'Like',
    color: '#F56C6C'
  },
  {
    label: '评论',
    value: InteractionType.COMMENT,
    icon: 'ChatDotRound',
    color: '#E6A23C'
  },
  {
    label: '分享',
    value: InteractionType.SHARE,
    icon: 'Share',
    color: '#67C23A'
  }
]

// ==================== 分页相关常量 ====================

/**
 * 默认分页配置
 */
export const DEFAULT_PAGE_CONFIG = {
  page: 1,
  pageSize: 20,
  pageSizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper'
}

/**
 * 新闻列表每页大小选项
 */
export const NEWS_PAGE_SIZES = [10, 20, 50, 100]

// ==================== 表单相关常量 ====================

/**
 * 新闻表单默认值
 */
export const DEFAULT_NEWS_FORM = {
  title: '',
  excerpt: '',
  content: '',
  categoryId: 0,
  author: '',
  status: NewsStatus.DRAFT,
  coverImage: '',
  publishTime: '',
  isFeatured: false,
  sortOrder: 0,
  tagIds: []
}

/**
 * 新闻表单验证规则
 */
export const NEWS_FORM_RULES = {
  title: [
    { required: true, message: '请输入新闻标题', trigger: 'blur' },
    { min: 5, max: 200, message: '标题长度应在5-200个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入新闻内容', trigger: 'blur' },
    { min: 50, message: '内容长度不能少于50个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择新闻分类', trigger: 'change' }
  ],
  author: [
    { required: true, message: '请输入作者姓名', trigger: 'blur' },
    { max: 100, message: '作者姓名不能超过100个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择新闻状态', trigger: 'change' }
  ],
  excerpt: [
    { min: 10, max: 500, message: '摘要长度应在10-500个字符之间', trigger: 'blur' }
  ]
}

// ==================== 表格相关常量 ====================

/**
 * 新闻列表表格列配置
 */
export const NEWS_TABLE_COLUMNS: NewsTableColumn[] = [
  {
    prop: 'id',
    label: 'ID',
    width: 80,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'title',
    label: '标题',
    minWidth: 200,
    sortable: true
  },
  {
    prop: 'author',
    label: '作者',
    width: 120,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'categoryName',
    label: '分类',
    width: 120,
    align: 'center'
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    align: 'center'
  },
  {
    prop: 'views',
    label: '浏览量',
    width: 100,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'likes',
    label: '点赞数',
    width: 100,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'isFeatured',
    label: '推荐',
    width: 80,
    align: 'center'
  },
  {
    prop: 'publishTime',
    label: '发布时间',
    width: 180,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'createdAt',
    label: '创建时间',
    width: 180,
    sortable: true,
    align: 'center'
  },
  {
    prop: 'actions',
    label: '操作',
    width: 200,
    align: 'center'
  }
]

/**
 * 新闻操作按钮配置
 */
export const NEWS_ACTION_BUTTONS: NewsActionButton[] = [
  {
    label: '编辑',
    type: 'primary',
    icon: 'Edit',
    action: 'edit',
    visible: () => true
  },
  {
    label: '发布',
    type: 'success',
    icon: 'Upload',
    action: 'publish',
    visible: (news) => news.status === NewsStatus.DRAFT
  },
  {
    label: '下线',
    type: 'warning',
    icon: 'Download',
    action: 'offline',
    visible: (news) => news.status === NewsStatus.PUBLISHED
  },
  {
    label: '删除',
    type: 'danger',
    icon: 'Delete',
    action: 'delete',
    visible: () => true
  }
]

// ==================== 筛选相关常量 ====================

/**
 * 新闻筛选器配置
 */
export const NEWS_FILTERS: NewsFilter[] = [
  {
    key: 'keyword',
    label: '关键词',
    type: 'input',
    placeholder: '请输入标题或内容关键词',
    clearable: true
  },
  {
    key: 'categoryId',
    label: '分类',
    type: 'select',
    placeholder: '请选择分类',
    clearable: true,
    options: [] // 动态加载
  },
  {
    key: 'status',
    label: '状态',
    type: 'select',
    placeholder: '请选择状态',
    clearable: true,
    options: NEWS_STATUS_OPTIONS.map(item => ({
      label: item.label,
      value: item.value
    }))
  },
  {
    key: 'author',
    label: '作者',
    type: 'input',
    placeholder: '请输入作者姓名',
    clearable: true
  },
  {
    key: 'isFeatured',
    label: '推荐',
    type: 'select',
    placeholder: '是否推荐',
    clearable: true,
    options: [
      { label: '推荐', value: true },
      { label: '普通', value: false }
    ]
  },
  {
    key: 'publishTimeRange',
    label: '发布时间',
    type: 'daterange',
    placeholder: '请选择发布时间范围',
    clearable: true
  }
]

// ==================== 排序相关常量 ====================

/**
 * 新闻排序选项
 */
export const NEWS_SORT_OPTIONS = [
  { label: '创建时间', value: 'createdAt' },
  { label: '发布时间', value: 'publishTime' },
  { label: '浏览量', value: 'views' },
  { label: '点赞数', value: 'likes' },
  { label: '评论数', value: 'comments' },
  { label: '标题', value: 'title' },
  { label: '作者', value: 'author' },
  { label: '排序权重', value: 'sortOrder' }
]

/**
 * 排序方向选项
 */
export const SORT_ORDER_OPTIONS = [
  { label: '降序', value: 'desc' },
  { label: '升序', value: 'asc' }
]

// ==================== 文件上传相关常量 ====================

/**
 * 图片上传配置
 */
export const IMAGE_UPLOAD_CONFIG = {
  // 允许的文件类型
  allowedTypes: ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'],
  // 最大文件大小（字节）
  maxSize: 5 * 1024 * 1024, // 5MB
  // 图片最大尺寸
  maxWidth: 1920,
  maxHeight: 1080,
  // 缩略图尺寸
  thumbnailWidth: 300,
  thumbnailHeight: 200
}

/**
 * 附件上传配置
 */
export const ATTACHMENT_UPLOAD_CONFIG = {
  // 允许的文件类型
  allowedTypes: [
    'application/pdf',
    'application/msword',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'application/vnd.ms-excel',
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-powerpoint',
    'application/vnd.openxmlformats-officedocument.presentationml.presentation'
  ],
  // 最大文件大小（字节）
  maxSize: 10 * 1024 * 1024 // 10MB
}

// ==================== 富文本编辑器相关常量 ====================

/**
 * 富文本编辑器配置
 */
export const RICH_EDITOR_CONFIG = {
  // 工具栏配置
  toolbar: [
    'bold', 'italic', 'underline', 'strike',
    '|',
    'header', 'blockquote', 'code-block',
    '|',
    'list', 'bullet', 'indent', 'outdent',
    '|',
    'link', 'image', 'video',
    '|',
    'align', 'color', 'background',
    '|',
    'clean'
  ],
  // 占位符
  placeholder: '请输入新闻内容...',
  // 主题
  theme: 'snow',
  // 模块配置
  modules: {
    toolbar: true,
    history: {
      delay: 1000,
      maxStack: 50
    }
  }
}

// ==================== 标签相关常量 ====================

/**
 * 标签颜色选项
 */
export const TAG_COLOR_OPTIONS = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C',
  '#909399', '#C71585', '#FF6347', '#32CD32',
  '#1E90FF', '#FF69B4', '#8A2BE2', '#00CED1'
]

/**
 * 默认标签颜色
 */
export const DEFAULT_TAG_COLOR = '#409EFF'

// ==================== 统计相关常量 ====================

/**
 * 统计图表颜色
 */
export const CHART_COLORS = [
  '#409EFF', '#67C23A', '#E6A23C', '#F56C6C',
  '#909399', '#C71585', '#FF6347', '#32CD32'
]

/**
 * 统计时间范围选项
 */
export const STATS_TIME_RANGE_OPTIONS = [
  { label: '今日', value: 'today' },
  { label: '昨日', value: 'yesterday' },
  { label: '本周', value: 'thisWeek' },
  { label: '上周', value: 'lastWeek' },
  { label: '本月', value: 'thisMonth' },
  { label: '上月', value: 'lastMonth' },
  { label: '最近7天', value: 'last7Days' },
  { label: '最近30天', value: 'last30Days' }
]

// ==================== 权限相关常量 ====================

/**
 * 新闻管理权限
 */
export const NEWS_PERMISSIONS = {
  // 查看权限
  VIEW: 'news:view',
  // 创建权限
  CREATE: 'news:create',
  // 编辑权限
  EDIT: 'news:edit',
  // 删除权限
  DELETE: 'news:delete',
  // 发布权限
  PUBLISH: 'news:publish',
  // 下线权限
  OFFLINE: 'news:offline',
  // 统计权限
  STATS: 'news:stats'
} as const

// ==================== 缓存相关常量 ====================

/**
 * 缓存键名
 */
export const CACHE_KEYS = {
  // 新闻列表
  NEWS_LIST: 'news:list',
  // 新闻详情
  NEWS_DETAIL: 'news:detail',
  // 新闻分类
  NEWS_CATEGORIES: 'news:categories',
  // 新闻标签
  NEWS_TAGS: 'news:tags',
  // 新闻统计
  NEWS_STATS: 'news:stats'
} as const

/**
 * 缓存过期时间（毫秒）
 */
export const CACHE_EXPIRE_TIME = {
  // 短期缓存（5分钟）
  SHORT: 5 * 60 * 1000,
  // 中期缓存（30分钟）
  MEDIUM: 30 * 60 * 1000,
  // 长期缓存（2小时）
  LONG: 2 * 60 * 60 * 1000
} as const