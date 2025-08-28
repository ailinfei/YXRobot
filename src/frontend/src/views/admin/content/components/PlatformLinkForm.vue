<template>
  <div class="platform-link-form">
    <el-form
      ref="formRef"
      :model="formData"
      :rules="formRules"
      label-width="120px"
    >
      <el-form-item label="平台名称" prop="platformName">
        <el-input
          v-model="formData.platformName"
          placeholder="请输入平台名称"
        />
      </el-form-item>

      <el-form-item label="平台类型" prop="platformType">
        <el-select
          v-model="formData.platformType"
          placeholder="请选择平台类型"
          style="width: 100%"
        >
          <el-option label="电商平台" value="ecommerce" />
          <el-option label="租赁平台" value="rental" />
        </el-select>
      </el-form-item>

      <el-form-item label="链接地址" prop="linkUrl">
        <el-input
          v-model="formData.linkUrl"
          placeholder="请输入链接地址"
          type="url"
        />
      </el-form-item>

      <el-form-item label="地区" prop="region">
        <el-select
          v-model="formData.region"
          placeholder="请选择地区"
          style="width: 100%"
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

      <el-form-item label="国家" prop="country">
        <el-select
          v-model="formData.country"
          placeholder="请选择国家"
          style="width: 100%"
        >
          <el-option
            v-for="country in availableCountries"
            :key="country.code"
            :label="country.name"
            :value="country.name"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="语言" prop="languageCode">
        <el-select
          v-model="formData.languageCode"
          placeholder="请选择语言"
          style="width: 100%"
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
        <el-switch
          v-model="formData.isEnabled"
          active-text="启用"
          inactive-text="禁用"
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

interface PlatformLinkFormData {
  platformName: string
  platformType: 'ecommerce' | 'rental'
  linkUrl: string
  region: string
  country: string
  languageCode: string
  isEnabled: boolean
}

interface RegionConfig {
  region: string
  country: string
  languages: Array<{
    name: string
    code: string
  }>
}

const props = defineProps<{
  modelValue: PlatformLinkFormData
  regions: RegionConfig[]
  editing?: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: PlatformLinkFormData]
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
  // 从所有地区中获取不重复的国家列表
  const countries = new Set<string>()
  props.regions.forEach(region => {
    if (region.country) {
      countries.add(region.country)
    }
  })
  return Array.from(countries).map(country => ({
    name: country,
    code: country
  }))
})

const availableLanguages = computed(() => {
  return selectedRegion.value?.languages || []
})

const formRules: FormRules = {
  platformName: [
    { required: true, message: '请输入平台名称', trigger: 'blur' }
  ],
  platformType: [
    { required: true, message: '请选择平台类型', trigger: 'change' }
  ],
  linkUrl: [
    { required: true, message: '请输入链接地址', trigger: 'blur' },
    { type: 'url', message: '请输入有效的URL地址', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择地区', trigger: 'change' }
  ],
  country: [
    { required: true, message: '请选择国家', trigger: 'change' }
  ],
  languageCode: [
    { required: true, message: '请选择语言', trigger: 'change' }
  ]
}

const handleRegionChange = () => {
  // 清空国家和语言选择
  formData.value.country = ''
  formData.value.languageCode = ''
}

const validate = async () => {
  if (!formRef.value) return false
  try {
    await formRef.value.validate()
    return true
  } catch {
    return false
  }
}

defineExpose({
  validate
})
</script>

<style lang="scss" scoped>
.platform-link-form {
  .el-form-item {
    margin-bottom: 20px;
  }
}
</style>