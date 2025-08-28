<template>
  <div class="multi-currency-price">
    <div class="price-header">
      <h4>多币种价格配置</h4>
      <el-button 
        type="primary" 
        size="small" 
        :icon="Plus"
        @click="addCurrency"
      >
        添加币种
      </el-button>
    </div>

    <div class="price-list">
      <div 
        v-for="(price, index) in prices" 
        :key="index"
        class="price-item"
      >
        <el-card class="price-card">
          <div class="price-form">
            <el-row :gutter="16">
              <el-col :span="6">
                <el-form-item label="币种" :prop="`prices.${index}.currency`">
                  <el-select 
                    v-model="price.currency" 
                    placeholder="选择币种"
                    @change="handleCurrencyChange(index)"
                  >
                    <el-option
                      v-for="currency in availableCurrencies"
                      :key="currency.code"
                      :label="`${currency.code} - ${currency.name}`"
                      :value="currency.code"
                      :disabled="isCurrencyUsed(currency.code, index)"
                    >
                      <div class="currency-option">
                        <span class="currency-flag">{{ currency.flag }}</span>
                        <span class="currency-code">{{ currency.code }}</span>
                        <span class="currency-name">{{ currency.name }}</span>
                      </div>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              
              <el-col :span="5">
                <el-form-item label="当前价格" :prop="`prices.${index}.amount`">
                  <el-input-number
                    v-model="price.amount"
                    :min="0"
                    :precision="getCurrencyPrecision(price.currency)"
                    :step="getCurrencyStep(price.currency)"
                    placeholder="当前价格"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              
              <el-col :span="5">
                <el-form-item label="原价">
                  <el-input-number
                    v-model="price.originalAmount"
                    :min="0"
                    :precision="getCurrencyPrecision(price.currency)"
                    :step="getCurrencyStep(price.currency)"
                    placeholder="原价（可选）"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              
              <el-col :span="4">
                <el-form-item label="折扣">
                  <el-input
                    :value="getDiscountText(price)"
                    disabled
                    placeholder="自动计算"
                  />
                </el-form-item>
              </el-col>
              
              <el-col :span="4">
                <el-form-item label="操作">
                  <div class="price-actions">
                    <el-button
                      type="success"
                      size="small"
                      @click="updateExchangeRate(index)"
                      :loading="updatingRates[index]"
                      title="更新汇率"
                    >
                      更新
                    </el-button>
                    <el-button
                      type="danger"
                      size="small"
                      :icon="Delete"
                      @click="removeCurrency(index)"
                      :disabled="prices.length <= 1"
                      title="删除币种"
                    />
                  </div>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 价格预览 -->
            <div class="price-preview">
              <div class="preview-item">
                <span class="preview-label">显示价格：</span>
                <span class="preview-price current">
                  {{ formatPrice(price.amount, price.currency) }}
                </span>
                <span 
                  v-if="price.originalAmount && price.originalAmount > price.amount"
                  class="preview-price original"
                >
                  {{ formatPrice(price.originalAmount, price.currency) }}
                </span>
                <span 
                  v-if="price.originalAmount && price.originalAmount > price.amount"
                  class="preview-discount"
                >
                  立省{{ formatPrice(price.originalAmount - price.amount, price.currency) }}
                </span>
              </div>
              
              <div class="preview-item" v-if="price.currency !== 'CNY'">
                <span class="preview-label">参考人民币：</span>
                <span class="preview-price reference">
                  ≈ {{ formatPrice(convertToCNY(price.amount, price.currency), 'CNY') }}
                </span>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 汇率信息 -->
    <div class="exchange-rate-info">
      <div class="rate-header">
        <h5>当前汇率信息</h5>
        <div class="rate-update">
          <span class="update-time">更新时间：{{ formatDate(lastUpdateTime) }}</span>
          <el-button 
            size="small" 
            @click="updateAllRates"
            :loading="updatingAllRates"
          >
            更新所有汇率
          </el-button>
        </div>
      </div>
      
      <div class="rate-list">
        <div 
          v-for="rate in exchangeRates" 
          :key="rate.currency"
          class="rate-item"
        >
          <span class="rate-currency">{{ rate.currency }}</span>
          <span class="rate-value">1 {{ rate.currency }} = {{ rate.rate.toFixed(4) }} CNY</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { formatDate } from '@/utils/dateTime'
import type { ProductPrice } from '@/types/product'

interface Currency {
  code: string
  name: string
  flag: string
  precision: number
  step: number
}

interface ExchangeRate {
  currency: string
  rate: number
}

interface Props {
  modelValue: ProductPrice[]
}

interface Emits {
  (e: 'update:modelValue', prices: ProductPrice[]): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

// 响应式数据
const updatingRates = ref<Record<number, boolean>>({})
const updatingAllRates = ref(false)
const lastUpdateTime = ref(new Date().toISOString())

// 支持的币种
const availableCurrencies: Currency[] = [
  { code: 'CNY', name: '人民币', flag: '🇨🇳', precision: 2, step: 1 },
  { code: 'USD', name: '美元', flag: '🇺🇸', precision: 2, step: 1 },
  { code: 'EUR', name: '欧元', flag: '🇪🇺', precision: 2, step: 1 },
  { code: 'JPY', name: '日元', flag: '🇯🇵', precision: 0, step: 1 },
  { code: 'GBP', name: '英镑', flag: '🇬🇧', precision: 2, step: 1 },
  { code: 'KRW', name: '韩元', flag: '🇰🇷', precision: 0, step: 10 },
  { code: 'HKD', name: '港币', flag: '🇭🇰', precision: 2, step: 1 },
  { code: 'SGD', name: '新加坡元', flag: '🇸🇬', precision: 2, step: 1 }
]

// Mock汇率数据（实际应该从API获取）
const exchangeRates = ref<ExchangeRate[]>([
  { currency: 'USD', rate: 7.2 },
  { currency: 'EUR', rate: 7.8 },
  { currency: 'JPY', rate: 0.048 },
  { currency: 'GBP', rate: 9.1 },
  { currency: 'KRW', rate: 0.0054 },
  { currency: 'HKD', rate: 0.92 },
  { currency: 'SGD', rate: 5.3 }
])

// 计算属性
const prices = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

// 监听价格变化，自动计算折扣
watch(
  prices,
  (newPrices) => {
    newPrices.forEach(price => {
      if (price.originalAmount && price.originalAmount > price.amount) {
        price.discount = Math.round(((price.originalAmount - price.amount) / price.originalAmount) * 100)
      } else {
        price.discount = undefined
      }
    })
  },
  { deep: true }
)

// 方法
const getCurrencyInfo = (currencyCode: string): Currency => {
  return availableCurrencies.find(c => c.code === currencyCode) || availableCurrencies[0]
}

const getCurrencyPrecision = (currencyCode: string): number => {
  return getCurrencyInfo(currencyCode).precision
}

const getCurrencyStep = (currencyCode: string): number => {
  return getCurrencyInfo(currencyCode).step
}

const isCurrencyUsed = (currencyCode: string, currentIndex: number): boolean => {
  return prices.value.some((price, index) => 
    price.currency === currencyCode && index !== currentIndex
  )
}

const formatPrice = (amount: number, currency: string): string => {
  const currencyInfo = getCurrencyInfo(currency)
  const formatter = new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: currencyInfo.precision,
    maximumFractionDigits: currencyInfo.precision
  })
  return formatter.format(amount)
}

const getDiscountText = (price: ProductPrice): string => {
  if (price.originalAmount && price.originalAmount > price.amount) {
    const discount = Math.round(((price.originalAmount - price.amount) / price.originalAmount) * 100)
    return `${discount}%`
  }
  return '-'
}

const convertToCNY = (amount: number, fromCurrency: string): number => {
  if (fromCurrency === 'CNY') return amount
  
  const rate = exchangeRates.value.find(r => r.currency === fromCurrency)
  return rate ? amount * rate.rate : amount
}

const addCurrency = () => {
  // 找到第一个未使用的币种
  const usedCurrencies = prices.value.map(p => p.currency)
  const availableCurrency = availableCurrencies.find(c => !usedCurrencies.includes(c.code))
  
  if (!availableCurrency) {
    ElMessage.warning('所有币种都已添加')
    return
  }
  
  const newPrice: ProductPrice = {
    currency: availableCurrency.code,
    amount: 0,
    originalAmount: undefined,
    discount: undefined
  }
  
  prices.value.push(newPrice)
}

const removeCurrency = (index: number) => {
  if (prices.value.length <= 1) {
    ElMessage.warning('至少需要保留一个币种')
    return
  }
  
  prices.value.splice(index, 1)
}

const handleCurrencyChange = (index: number) => {
  // 币种变更时，可以自动转换价格
  const price = prices.value[index]
  if (price.amount > 0) {
    // 这里可以实现自动汇率转换逻辑
    ElMessage.info('币种已更改，请重新设置价格')
  }
}

const updateExchangeRate = async (index: number) => {
  const price = prices.value[index]
  if (price.currency === 'CNY') {
    ElMessage.info('人民币无需更新汇率')
    return
  }
  
  updatingRates.value[index] = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 这里应该调用实际的汇率API
    // const newRate = await fetchExchangeRate(price.currency)
    
    ElMessage.success(`${price.currency} 汇率更新成功`)
  } catch (error) {
    ElMessage.error('汇率更新失败')
  } finally {
    updatingRates.value[index] = false
  }
}

const updateAllRates = async () => {
  updatingAllRates.value = true
  
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    lastUpdateTime.value = new Date().toISOString()
    ElMessage.success('所有汇率更新成功')
  } catch (error) {
    ElMessage.error('汇率更新失败')
  } finally {
    updatingAllRates.value = false
  }
}

// 初始化默认价格
if (prices.value.length === 0) {
  prices.value.push({
    currency: 'CNY',
    amount: 0,
    originalAmount: undefined,
    discount: undefined
  })
}
</script>

<style lang="scss" scoped>
.multi-currency-price {
  .price-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h4 {
      margin: 0;
      color: var(--text-primary);
    }
  }
  
  .price-list {
    .price-item {
      margin-bottom: 20px;
      
      .price-card {
        :deep(.el-card__body) {
          padding: 20px;
        }
      }
      
      .price-form {
        .price-actions {
          display: flex;
          gap: 8px;
        }
        
        .price-preview {
          margin-top: 16px;
          padding: 16px;
          background: var(--bg-secondary);
          border-radius: var(--radius-md);
          
          .preview-item {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;
            
            &:last-child {
              margin-bottom: 0;
            }
            
            .preview-label {
              font-size: 14px;
              color: var(--text-secondary);
              min-width: 80px;
            }
            
            .preview-price {
              font-weight: 600;
              
              &.current {
                font-size: 18px;
                color: var(--primary-color);
              }
              
              &.original {
                font-size: 14px;
                color: var(--text-light);
                text-decoration: line-through;
              }
              
              &.reference {
                font-size: 14px;
                color: var(--text-secondary);
              }
            }
            
            .preview-discount {
              background: var(--success-color);
              color: white;
              padding: 2px 6px;
              border-radius: var(--radius-sm);
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
  
  .exchange-rate-info {
    margin-top: 32px;
    padding: 20px;
    background: var(--bg-secondary);
    border-radius: var(--radius-lg);
    
    .rate-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      
      h5 {
        margin: 0;
        color: var(--text-primary);
      }
      
      .rate-update {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .update-time {
          font-size: 12px;
          color: var(--text-light);
        }
      }
    }
    
    .rate-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 12px;
      
      .rate-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 12px;
        background: white;
        border-radius: var(--radius-md);
        
        .rate-currency {
          font-weight: 600;
          color: var(--text-primary);
        }
        
        .rate-value {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }
  }
}

.currency-option {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .currency-flag {
    font-size: 16px;
  }
  
  .currency-code {
    font-weight: 600;
    color: var(--text-primary);
  }
  
  .currency-name {
    color: var(--text-secondary);
    font-size: 12px;
  }
}
</style>