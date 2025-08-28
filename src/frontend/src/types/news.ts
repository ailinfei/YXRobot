/**
 * 新闻管理系统 TypeScript 接口定义
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

// ==================== 枚举定义 ====================

/**
 * 新闻状态枚举
 */
export enum NewsStatus {
  /** 草稿 */
  DRAFT = 'DRAFT',
  /** 已发布 */
  PUBLISHED = 'PUBLISHED',
  /** 已下线 */
  OFFLINE = 'OFFLINE'
}

/**
 * 互动类型枚举
 */
export enum InteractionType {
  /** 浏览 */
  VIEW = 'VIEW',
  /** 点赞 */
  LIKE = 'LIKE',
  /** 评论 */
  COMMENT = 'COMMENT',
  /** 分享 */
  SHARE = 'SHARE'
}

// ==================== 基础接口定义 ====================

/**
 * 新闻实体接口
 */
export interface News {
  /** 新闻ID */
  id: number;
  /** 新闻标题 */
  title: string;
  /** 新闻摘要 */
  excerpt?: string;
  /** 新闻内容 */
  content: string;
  /** 分类ID */
  categoryId: number;
  /** 分类名称（只读） */
  categoryName?: string;
  /** 作者 */
  author: string;
  /** 新闻状态 */
  status: NewsStatus;
  /** 状态描述 */
  statusText?: string;
  /** 封面图片URL */
  coverImage?: string;
  /** 发布时间 */
  publishTime?: string;
  /** 浏览量 */
  views: number;
  /** 评论数 */
  comments: number;
  /** 点赞数 */
  likes: number;
  /** 是否推荐 */
  isFeatured: boolean;
  /** 排序权重 */
  sortOrder: number;
  /** 创建时间 */
  createdAt: string;
  /** 更新时间 */
  updatedAt: string;
  /** 关联标签 */
  tags?: NewsTag[];
  /** 标签ID列表（用于表单提交） */
  tagIds?: number[];
}

/**
 * 新闻表单接口
 */
export interface NewsForm {
  /** 新闻ID（编辑时使用） */
  id?: number;
  /** 新闻标题 */
  title: string;
  /** 新闻摘要 */
  excerpt?: string;
  /** 新闻内容 */
  content: string;
  /** 分类ID */
  categoryId: number;
  /** 作者 */
  author: string;
  /** 新闻状态 */
  status: NewsStatus;
  /** 封面图片URL */
  coverImage?: string;
  /** 发布时间 */
  publishTime?: string;
  /** 是否推荐 */
  isFeatured: boolean;
  /** 排序权重 */
  sortOrder: number;
  /** 标签ID列表 */
  tagIds?: number[];
}

/**
 * 新闻分类接口
 */
export interface NewsCategory {
  /** 分类ID */
  id: number;
  /** 分类名称 */
  name: string;
  /** 分类描述 */
  description?: string;
  /** 排序权重 */
  sortOrder: number;
  /** 是否启用 */
  isEnabled: boolean;
  /** 该分类下的新闻数量 */
  newsCount?: number;
  /** 创建时间 */
  createdAt: string;
  /** 更新时间 */
  updatedAt: string;
}

/**
 * 新闻标签接口
 */
export interface NewsTag {
  /** 标签ID */
  id: number;
  /** 标签名称 */
  name: string;
  /** 标签颜色 */
  color?: string;
  /** 使用次数 */
  usageCount?: number;
  /** 创建时间 */
  createdAt: string;
  /** 更新时间 */
  updatedAt: string;
}

/**
 * 新闻统计接口
 */
export interface NewsStats {
  /** 总新闻数 */
  totalNews: number;
  /** 已发布新闻数 */
  publishedNews: number;
  /** 草稿新闻数 */
  draftNews: number;
  /** 已下线新闻数 */
  offlineNews: number;
  /** 总浏览量 */
  totalViews: number;
  /** 总评论数 */
  totalComments: number;
  /** 总点赞数 */
  totalLikes: number;
  /** 今日新增新闻数 */
  todayNews: number;
  /** 今日浏览量 */
  todayViews: number;
  /** 本周新增新闻数 */
  weekNews: number;
  /** 本月新增新闻数 */
  monthNews: number;
  /** 推荐新闻数 */
  featuredNews: number;
  /** 发布率 */
  publishRate?: number;
  /** 平均浏览量 */
  averageViews?: number;
}

/**
 * 新闻互动记录接口
 */
export interface NewsInteraction {
  /** 记录ID */
  id: number;
  /** 新闻ID */
  newsId: number;
  /** 用户ID */
  userId?: number;
  /** 互动类型 */
  interactionType: InteractionType;
  /** 用户IP地址 */
  userIp?: string;
  /** 用户代理 */
  userAgent?: string;
  /** 创建时间 */
  createdAt: string;
}

// ==================== 请求参数接口 ====================

/**
 * 新闻列表查询参数
 */
export interface NewsListParams {
  /** 页码 */
  page?: number;
  /** 每页大小 */
  pageSize?: number;
  /** 分类ID */
  categoryId?: number;
  /** 状态 */
  status?: NewsStatus;
  /** 作者 */
  author?: string;
  /** 关键词 */
  keyword?: string;
  /** 是否推荐 */
  isFeatured?: boolean;
  /** 开始日期 */
  startDate?: string;
  /** 结束日期 */
  endDate?: string;
  /** 排序字段 */
  sortBy?: string;
  /** 排序方向 */
  sortOrder?: 'asc' | 'desc';
}

/**
 * 新闻搜索参数
 */
export interface NewsSearchParams {
  /** 搜索关键词 */
  keyword?: string;
  /** 分类ID */
  categoryId?: number;
  /** 标签ID列表 */
  tagIds?: number[];
  /** 作者 */
  author?: string;
  /** 状态 */
  status?: NewsStatus;
  /** 是否推荐 */
  isFeatured?: boolean;
  /** 发布时间范围 */
  publishTimeRange?: [string, string];
  /** 浏览量范围 */
  viewsRange?: [number, number];
  /** 页码 */
  page?: number;
  /** 每页大小 */
  pageSize?: number;
}

/**
 * 批量操作参数
 */
export interface NewsBatchOperationParams {
  /** 新闻ID列表 */
  ids: number[];
  /** 操作类型 */
  operation: 'publish' | 'offline' | 'delete' | 'feature' | 'unfeature';
  /** 操作原因（可选） */
  reason?: string;
}

/**
 * 新闻状态更新参数
 */
export interface NewsStatusUpdateParams {
  /** 新闻ID */
  id: number;
  /** 目标状态 */
  status: NewsStatus;
  /** 发布时间（发布时使用） */
  publishTime?: string;
  /** 操作原因 */
  reason?: string;
}

// ==================== 响应接口定义 ====================

/**
 * 通用API响应格式
 */
export interface ApiResponse<T = any> {
  /** 响应码 */
  code: number;
  /** 响应消息 */
  message: string;
  /** 响应数据 */
  data?: T;
  /** 时间戳 */
  timestamp?: number;
}

/**
 * 分页响应格式
 */
export interface PageResponse<T = any> {
  /** 数据列表 */
  list: T[];
  /** 总记录数 */
  total: number;
  /** 当前页码 */
  page: number;
  /** 每页大小 */
  pageSize: number;
  /** 总页数 */
  totalPages: number;
}

/**
 * 新闻列表响应
 */
export interface NewsListResponse extends PageResponse<News> {}

/**
 * 文件上传响应
 */
export interface FileUploadResponse {
  /** 文件URL */
  url: string;
  /** 文件名 */
  filename: string;
  /** 文件大小 */
  size: number;
  /** 文件类型 */
  type: string;
}

/**
 * 新闻验证结果
 */
export interface NewsValidationResult {
  /** 是否有效 */
  isValid: boolean;
  /** 错误信息列表 */
  errors: Array<{
    field: string;
    message: string;
  }>;
  /** 警告信息列表 */
  warnings: Array<{
    field: string;
    message: string;
  }>;
}

// ==================== 表单验证规则 ====================

/**
 * 新闻表单验证规则
 */
export interface NewsFormRules {
  title: Array<{
    required?: boolean;
    min?: number;
    max?: number;
    message: string;
    trigger?: string;
  }>;
  content: Array<{
    required?: boolean;
    min?: number;
    message: string;
    trigger?: string;
  }>;
  categoryId: Array<{
    required?: boolean;
    message: string;
    trigger?: string;
  }>;
  author: Array<{
    required?: boolean;
    max?: number;
    message: string;
    trigger?: string;
  }>;
  status: Array<{
    required?: boolean;
    message: string;
    trigger?: string;
  }>;
  excerpt: Array<{
    max?: number;
    message: string;
    trigger?: string;
  }>;
}

// ==================== 工具类型 ====================

/**
 * 新闻状态选项
 */
export interface NewsStatusOption {
  label: string;
  value: NewsStatus;
  color: string;
  description: string;
}

/**
 * 新闻操作按钮配置
 */
export interface NewsActionButton {
  label: string;
  type: 'primary' | 'success' | 'warning' | 'danger' | 'info';
  icon?: string;
  action: string;
  visible: (news: News) => boolean;
  disabled?: (news: News) => boolean;
}

/**
 * 新闻表格列配置
 */
export interface NewsTableColumn {
  prop: string;
  label: string;
  width?: number;
  minWidth?: number;
  sortable?: boolean;
  formatter?: (row: News, column: any, cellValue: any) => string;
  align?: 'left' | 'center' | 'right';
}

/**
 * 新闻筛选器配置
 */
export interface NewsFilter {
  key: string;
  label: string;
  type: 'select' | 'input' | 'date' | 'daterange';
  options?: Array<{
    label: string;
    value: any;
  }>;
  placeholder?: string;
  clearable?: boolean;
}

// ==================== 常量定义 ====================

/**
 * 新闻状态选项列表
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
];

/**
 * 互动类型选项列表
 */
export const INTERACTION_TYPE_OPTIONS = [
  {
    label: '浏览',
    value: InteractionType.VIEW,
    icon: 'view'
  },
  {
    label: '点赞',
    value: InteractionType.LIKE,
    icon: 'like'
  },
  {
    label: '评论',
    value: InteractionType.COMMENT,
    icon: 'comment'
  },
  {
    label: '分享',
    value: InteractionType.SHARE,
    icon: 'share'
  }
];

/**
 * 默认分页配置
 */
export const DEFAULT_PAGE_CONFIG = {
  page: 1,
  pageSize: 20,
  pageSizes: [10, 20, 50, 100]
};

/**
 * 新闻表单默认值
 */
export const DEFAULT_NEWS_FORM: Partial<NewsForm> = {
  status: NewsStatus.DRAFT,
  isFeatured: false,
  sortOrder: 0,
  tagIds: []
};