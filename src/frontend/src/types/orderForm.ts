/**
 * 订单表单相关类型定义
 * 专门用于订单创建和编辑表单
 * 任务18：创建前端TypeScript接口定义
 */

import type { OrderItem, Order, OrderType, OrderStatus, PaymentStatus } from './order'

// 订单表单数据接口
export interface OrderFormData {
  // 基本信息
  orderNumber: string
  type: OrderType
  status?: OrderStatus
  
  // 客户信息
  customerId?: string | number
  customerName: string
  customerPhone: string
  customerEmail?: string
  deliveryAddress: string
  
  // 商品信息
  items: OrderFormItem[]
  
  // 金额信息
  subtotal: number
  shippingFee: number
  discount: number
  totalAmount: number
  currency: string
  
  // 支付信息
  paymentStatus?: PaymentStatus
  paymentMethod?: string
  paymentTime?: string
  
  // 其他信息
  expectedDeliveryDate?: string
  salesPerson?: string
  notes?: string
  
  // 租赁信息（仅租赁订单）
  rentalStartDate?: string
  rentalEndDate?: string
  rentalDays?: number
  rentalNotes?: string
}

// 订单表单商品项接口
export interface OrderFormItem {
  id?: string | number
  productId: string | number
  productName: string
  productModel?: string
  quantity: number
  unitPrice: number
  totalPrice: number
  notes?: string
}

// 订单表单验证规则接口
export interface OrderFormRules {
  orderNumber: Array<{
    required?: boolean
    message: string
    trigger?: string
    pattern?: RegExp
    validator?: (rule: any, value: any, callback: Function) => void
  }>
  type: Array<{
    required?: boolean
    message: string
    trigger?: string
  }>
  customerName: Array<{
    required?: boolean
    message: string
    trigger?: string
    min?: number
    max?: number
  }>
  customerPhone: Array<{
    required?: boolean
    message: string
    trigger?: string
    pattern?: RegExp
  }>
  customerEmail?: Array<{
    type?: string
    message: string
    trigger?: string
  }>
  deliveryAddress: Array<{
    required?: boolean
    message: string
    trigger?: string
    min?: number
    max?: number
  }>
  items: Array<{
    required?: boolean
    message: string
    trigger?: string
    validator?: (rule: any, value: any, callback: Function) => void
  }>
  totalAmount: Array<{
    required?: boolean
    message: string
    trigger?: string
    type?: string
    min?: number
  }>
  // 租赁相关验证规则
  rentalStartDate?: Array<{
    required?: boolean
    message: string
    trigger?: string
    type?: string
  }>
  rentalEndDate?: Array<{
    required?: boolean
    message: string
    trigger?: string
    type?: string
  }>
}

// 订单表单状态接口
export interface OrderFormState {
  loading: boolean
  submitting: boolean
  validating: boolean
  errors: Record<string, string[]>
  warnings: Record<string, string[]>
  isDirty: boolean
  isValid: boolean
}

// 订单表单配置接口
export interface OrderFormConfig {
  mode: 'create' | 'edit' | 'view'
  showRentalFields: boolean
  showPaymentFields: boolean
  showShippingFields: boolean
  allowedStatuses?: OrderStatus[]
  requiredFields: string[]
  readonlyFields: string[]
  hiddenFields: string[]
}

// 客户选择选项接口
export interface CustomerOption {
  id: string | number
  name: string
  phone: string
  email?: string
  address?: string
  level?: string
  disabled?: boolean
}

// 产品选择选项接口
export interface ProductOption {
  id: string | number
  name: string
  model?: string
  price: number
  stock?: number
  image?: string
  disabled?: boolean
  description?: string
}

// 订单表单步骤接口
export interface OrderFormStep {
  key: string
  title: string
  description?: string
  icon?: string
  status: 'wait' | 'process' | 'finish' | 'error'
  disabled?: boolean
  optional?: boolean
}

// 订单表单向导接口
export interface OrderFormWizard {
  currentStep: number
  steps: OrderFormStep[]
  canGoNext: boolean
  canGoPrev: boolean
  canSubmit: boolean
}

// 订单表单事件接口
export interface OrderFormEvents {
  onSubmit: (data: OrderFormData) => Promise<void>
  onCancel: () => void
  onReset: () => void
  onFieldChange: (field: string, value: any) => void
  onCustomerSelect: (customer: CustomerOption) => void
  onProductSelect: (product: ProductOption, index: number) => void
  onItemAdd: () => void
  onItemRemove: (index: number) => void
  onCalculate: () => void
  onValidate: (field?: string) => Promise<boolean>
}

// 订单表单工具函数类型
export interface OrderFormUtils {
  generateOrderNumber: (type: OrderType) => string
  calculateTotal: (items: OrderFormItem[], shippingFee: number, discount: number) => number
  calculateRentalDays: (startDate: string, endDate: string) => number
  validateForm: (data: OrderFormData) => Promise<{ isValid: boolean; errors: Record<string, string[]> }>
  formatCurrency: (amount: number, currency?: string) => string
  formatDate: (date: string | Date) => string
}

// 订单表单默认值接口
export interface OrderFormDefaults {
  type: OrderType
  status: OrderStatus
  currency: string
  shippingFee: number
  discount: number
  paymentStatus: PaymentStatus
  items: OrderFormItem[]
}

// 订单表单本地化接口
export interface OrderFormLocale {
  labels: Record<string, string>
  placeholders: Record<string, string>
  messages: Record<string, string>
  buttons: Record<string, string>
  tooltips: Record<string, string>
}

// 订单表单主题接口
export interface OrderFormTheme {
  primaryColor: string
  successColor: string
  warningColor: string
  errorColor: string
  borderRadius: string
  spacing: Record<string, string>
  typography: Record<string, any>
}

// 订单表单组件属性接口
export interface OrderFormProps {
  modelValue: boolean
  order?: Order | null
  config?: Partial<OrderFormConfig>
  locale?: Partial<OrderFormLocale>
  theme?: Partial<OrderFormTheme>
  customerOptions?: CustomerOption[]
  productOptions?: ProductOption[]
  loading?: boolean
  disabled?: boolean
}

// 订单表单组件事件接口
export interface OrderFormEmits {
  'update:modelValue': (value: boolean) => void
  'submit': (data: OrderFormData) => void
  'cancel': () => void
  'success': (order: Order) => void
  'error': (error: any) => void
  'change': (data: Partial<OrderFormData>) => void
  'validate': (result: { isValid: boolean; errors: Record<string, string[]> }) => void
}

// 订单表单组件实例接口
export interface OrderFormInstance {
  validate: () => Promise<boolean>
  resetFields: () => void
  clearValidate: (fields?: string[]) => void
  submit: () => Promise<void>
  getFormData: () => OrderFormData
  setFormData: (data: Partial<OrderFormData>) => void
}