<template>
  <div class="batch-update-form">
    <el-form
      ref="formRef"
      :model="formData"
      label-width="120px"
    >
      <el-form-item label="平台类型">
        <el-select
          v-model="formData.platformType"
          placeholder="请选择平台类型（可选）"
          style="width: 100%"
          clearable
        >
          <el-option label="电商平台" value="ecommerce" />
          <el-option label="租赁平台" value="rental" />
        </el-select>
      </el-form-item>

      <el-form-item label="地区">
        <el-select
          v-model="formData.region"
          placeholder="请选择地区（可选）"
          style="width: 100%"
          clearable
          @change="handleRegionChange"
        >
          <el-option
            v-for="region in regions"
            :key="region.region"
            :label="region.region"
            :value="region.region"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="国家">
        <el-select
          v-model="formData.country"
          placeholder="请选择国家（可选）"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="country in availableCountries"
            :key="country.code"
            :label="country.name"
            :value="country.name"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="语言">
        <el-select
          v-model="formData.languageCode"
          placeholder="请选择语言（可选）"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="language in availableLanguages"
            :key="language.code"
            :label="language.name"
            :value="language.code"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="启用状态">
        <el-select
          v-model="formData.isEnabled"
          placeholder="请选择启用状态（可选）"
          style="width: 100%"
          clearable
        >
          <el-option label="启用" :value="true" />
          <el-option label="禁用" :value="false" />
        </el-select>
      </el-form-item>
    </el-form>

    <div class="form-tip">
      <el-alert
        title="批量更新说明"
        description="只有选择了值的字段才会被更新，未选择的字段保持原值不变"
        type="info"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { FormInstance } from 'element-plus'

interface BatchUpdateFormData {
  platformType?: 'ecommerce' | 'rental'
  region?: string
  country?: string
  languageCode?: string
  isEnabled?: boolean
}

interface RegionConfig {
  region: string
  countries: Array<{
    name: string
    code: string
  }>
  languages: Array<{
    name: string
    code: string
  }>
}

const props = defineProps<{
  modelValue: BatchUpdateFormData
  regions: RegionConfig[]
}>()

const emit = defineEmits<{
  'update:modelValue': [value: BatchUpdateFormData]
}>()

const formRef = ref<FormInstance>()

const formData = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const selectedRegion = computed(() => {
  return props.regions.find(r => r.region === formData.value.region)
})

const availableCountries = computed(() => {
  return selectedRegion.value?.countries || []
})

const availableLanguages = computed(() => {
  return selectedRegion.value?.languages || []
})

const handleRegionChange = () => {
  // 清空国家和语言选择
  formData.value.country = undefined
  formData.value.languageCode = undefined
}

const validate = async () => {
  // 批量更新表单不需要验证，因为所有字段都是可选的
  return true
}

defineExpose({
  validate
})
</script>

<style lang="scss" scoped>
.batch-update-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  .form-tip {
    margin-top: 20px;
  }
}
</style>