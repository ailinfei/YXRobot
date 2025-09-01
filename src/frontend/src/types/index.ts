// 统一类型定义导出文件

// 客户相关类型
export * from './customer'

// API响应相关类型
export * from './api'

// 通用工具类型
export interface SelectOption {
  label: string
  value: string | number
  disabled?: boolean
  children?: SelectOption[]
}

export interface TableColumn {
  key: string
  label: string
  width?: string
  sortable?: boolean
  filterable?: boolean
  component?: string
  formatter?: (value: any, row?: any) => string
  align?: 'left' | 'center' | 'right'
  fixed?: 'left' | 'right'
  resizable?: boolean
}

export interface TableAction {
  key: string
  label: string
  icon?: any
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info' | 'text'
  disabled?: boolean | ((row: any) => boolean)
  visible?: boolean | ((row: any) => boolean)
  loading?: boolean
}

export interface TableFilter {
  key: string
  label: string
  type: 'input' | 'select' | 'date' | 'daterange' | 'number'
  options?: SelectOption[]
  placeholder?: string
  multiple?: boolean
  clearable?: boolean
}

export interface PaginationConfig {
  page: number
  pageSize: number
  total: number
  pageSizes?: number[]
  layout?: string
  background?: boolean
  small?: boolean
}

export interface FormRule {
  required?: boolean
  message?: string
  trigger?: 'blur' | 'change' | 'submit'
  min?: number
  max?: number
  pattern?: RegExp
  validator?: (rule: any, value: any, callback: Function) => void
}

export interface FormItem {
  prop: string
  label: string
  type?: 'input' | 'select' | 'date' | 'textarea' | 'number' | 'switch' | 'radio' | 'checkbox'
  placeholder?: string
  options?: SelectOption[]
  rules?: FormRule[]
  span?: number
  offset?: number
  disabled?: boolean
  readonly?: boolean
  clearable?: boolean
  multiple?: boolean
}

export interface DialogConfig {
  title: string
  width?: string | number
  fullscreen?: boolean
  modal?: boolean
  modalAppendToBody?: boolean
  appendToBody?: boolean
  lockScroll?: boolean
  customClass?: string
  closeOnClickModal?: boolean
  closeOnPressEscape?: boolean
  showClose?: boolean
  beforeClose?: (done: Function) => void
}

export interface LoadingConfig {
  target?: string | HTMLElement
  body?: boolean
  fullscreen?: boolean
  lock?: boolean
  text?: string
  spinner?: string
  background?: string
  customClass?: string
}

export interface MessageConfig {
  message: string
  type?: 'success' | 'warning' | 'info' | 'error'
  dangerouslyUseHTMLString?: boolean
  customClass?: string
  duration?: number
  showClose?: boolean
  center?: boolean
  onClose?: () => void
}

export interface NotificationConfig {
  title: string
  message?: string
  type?: 'success' | 'warning' | 'info' | 'error'
  dangerouslyUseHTMLString?: boolean
  customClass?: string
  duration?: number
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
  showClose?: boolean
  onClick?: () => void
  onClose?: () => void
}

// 路由相关类型
export interface RouteConfig {
  path: string
  name?: string
  component?: any
  redirect?: string
  alias?: string | string[]
  children?: RouteConfig[]
  meta?: RouteMeta
}

export interface RouteMeta {
  title?: string
  icon?: string
  roles?: string[]
  permissions?: string[]
  hidden?: boolean
  alwaysShow?: boolean
  breadcrumb?: boolean
  activeMenu?: string
  noCache?: boolean
  affix?: boolean
}

// 用户相关类型
export interface UserInfo {
  id: string | number
  username: string
  nickname?: string
  email?: string
  phone?: string
  avatar?: string
  roles: string[]
  permissions: string[]
  lastLoginTime?: string
  status: 'active' | 'inactive' | 'locked'
}

export interface LoginForm {
  username: string
  password: string
  captcha?: string
  rememberMe?: boolean
}

export interface LoginResponse {
  token: string
  refreshToken?: string
  expiresIn: number
  userInfo: UserInfo
}

// 菜单相关类型
export interface MenuItem {
  id: string | number
  title: string
  path?: string
  icon?: string
  component?: string
  redirect?: string
  children?: MenuItem[]
  meta?: {
    roles?: string[]
    permissions?: string[]
    hidden?: boolean
    alwaysShow?: boolean
    noCache?: boolean
    breadcrumb?: boolean
    activeMenu?: string
  }
}

// 文件相关类型
export interface FileInfo {
  id: string
  name: string
  size: number
  type: string
  url: string
  thumbnailUrl?: string
  uploadTime: string
  uploader?: string
}

export interface UploadConfig {
  action: string
  method?: 'post' | 'put'
  headers?: Record<string, string>
  data?: Record<string, any>
  name?: string
  withCredentials?: boolean
  showFileList?: boolean
  drag?: boolean
  accept?: string
  multiple?: boolean
  limit?: number
  fileSize?: number
  autoUpload?: boolean
  beforeUpload?: (file: File) => boolean | Promise<boolean>
  onProgress?: (event: ProgressEvent, file: File) => void
  onSuccess?: (response: any, file: File) => void
  onError?: (error: Error, file: File) => void
}

// 图表相关类型
export interface ChartData {
  labels: string[]
  datasets: Array<{
    label: string
    data: number[]
    backgroundColor?: string | string[]
    borderColor?: string | string[]
    borderWidth?: number
    fill?: boolean
  }>
}

export interface ChartOptions {
  responsive?: boolean
  maintainAspectRatio?: boolean
  plugins?: {
    legend?: {
      display?: boolean
      position?: 'top' | 'bottom' | 'left' | 'right'
    }
    tooltip?: {
      enabled?: boolean
      mode?: 'index' | 'dataset' | 'point' | 'nearest'
    }
  }
  scales?: {
    x?: {
      display?: boolean
      title?: {
        display?: boolean
        text?: string
      }
    }
    y?: {
      display?: boolean
      beginAtZero?: boolean
      title?: {
        display?: boolean
        text?: string
      }
    }
  }
}

// 主题相关类型
export interface ThemeConfig {
  mode: 'light' | 'dark' | 'auto'
  primaryColor: string
  fontSize: 'small' | 'medium' | 'large'
  borderRadius: 'none' | 'small' | 'medium' | 'large'
  layout: 'vertical' | 'horizontal' | 'mix'
  sidebarCollapsed: boolean
  showBreadcrumb: boolean
  showTabs: boolean
  showFooter: boolean
}

// 设置相关类型
export interface AppSettings {
  theme: ThemeConfig
  language: string
  timezone: string
  dateFormat: string
  timeFormat: string
  currency: string
  notifications: {
    desktop: boolean
    email: boolean
    sms: boolean
  }
  privacy: {
    showOnlineStatus: boolean
    allowDataCollection: boolean
  }
}

// 错误相关类型
export interface ErrorInfo {
  code: string
  message: string
  details?: any
  timestamp: string
  path?: string
  method?: string
  userAgent?: string
  userId?: string
}

// 日志相关类型
export interface LogEntry {
  id: string
  timestamp: string
  level: 'debug' | 'info' | 'warn' | 'error'
  message: string
  category: string
  userId?: string
  ip?: string
  userAgent?: string
  context?: Record<string, any>
}

// 搜索相关类型
export interface SearchConfig {
  placeholder?: string
  debounceTime?: number
  minLength?: number
  maxResults?: number
  showHistory?: boolean
  showSuggestions?: boolean
  caseSensitive?: boolean
  highlightMatches?: boolean
}

export interface SearchResult<T = any> {
  items: T[]
  total: number
  searchTime: number
  suggestions?: string[]
  hasMore: boolean
}

// 导出相关类型
export interface ExportConfig {
  format: 'excel' | 'csv' | 'pdf' | 'json'
  filename?: string
  fields?: string[]
  filters?: Record<string, any>
  options?: {
    includeHeaders?: boolean
    dateFormat?: string
    encoding?: string
    delimiter?: string
  }
}

export interface ImportConfig {
  format: 'excel' | 'csv' | 'json'
  mapping?: Record<string, string>
  validation?: {
    required?: string[]
    unique?: string[]
    format?: Record<string, RegExp>
  }
  options?: {
    skipRows?: number
    maxRows?: number
    encoding?: string
    delimiter?: string
  }
}