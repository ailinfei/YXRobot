<template>
  <div class="charity-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h2>公益慈善管理</h2>
        <p class="header-subtitle">14公益慈善活动管理 · 练字机器人管理系统</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleUpdateCharityData">
          更新公益数据
        </el-button>
    
      </div>
    </div>

    <!-- 公益统计数据展示 -->
    <CharityStatsOverview ref="statsOverviewRef" />

    <!-- 主要内容区域 -->
    <div class="charity-tabs">
      <el-tabs v-model="activeTab" type="border-card">


        <!-- 合作机构 -->
        <el-tab-pane label="合作机构" name="institutions">
          <InstitutionManagement ref="institutionManagementRef" />
        </el-tab-pane>

        <!-- 公益活动 -->
        <el-tab-pane label="公益活动" name="activities">
          <ActivityManagement ref="activityManagementRef" />
        </el-tab-pane>

        <!-- 统计数据 -->
        <el-tab-pane label="统计数据" name="statistics">
          <div class="statistics-section">
            <div class="section-header">
              <h3>公益慈善统计</h3>
            </div>
            
            <!-- 图表区域 -->
            <div class="charts-section">
              <div class="charts-row">
                <!-- 项目状态分布 -->
                <div class="chart-card">
                  <div class="chart-header">
                    <h4>项目状态分布</h4>
                  </div>
                  <div class="chart-content">
                    <div ref="projectStatusChart" class="chart-container" style="height: 300px;"></div>
                  </div>
                </div>

                <!-- 资金筹集趋势 -->
                <div class="chart-card">
                  <div class="chart-header">
                    <h4>资金筹集趋势</h4>
                  </div>
                  <div class="chart-content">
                    <div ref="fundingTrendChart" class="chart-container" style="height: 300px;"></div>
                  </div>
                </div>
              </div>

              <div class="charts-row">
                <!-- 地区分布 -->
                <div class="chart-card">
                  <div class="chart-header">
                    <h4>项目地区分布</h4>
                  </div>
                  <div class="chart-content">
                    <div ref="regionDistributionChart" class="chart-container" style="height: 300px;"></div>
                  </div>
                </div>

                <!-- 志愿者活动统计 -->
                <div class="chart-card">
                  <div class="chart-header">
                    <h4>志愿者活动统计</h4>
                  </div>
                  <div class="chart-content">
                    <div ref="volunteerActivityChart" class="chart-container" style="height: 300px;"></div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 统计数据概览已移至页面顶部 -->
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 公益数据录入对话框 -->
    <el-dialog
      v-model="dataUpdateDialogVisible"
      title="更新公益数据"
      width="800px"
      :before-close="handleDataUpdateDialogClose"
    >
      <div class="data-update-form">
        <el-form
          ref="dataUpdateFormRef"
          :model="charityDataForm"
          :rules="charityDataRules"
          label-width="140px"
        >
          <!-- 基础统计数据 -->
          <div class="form-section">
            <h3>基础统计数据</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="累计受益人数" prop="totalBeneficiaries">
                  <el-input-number
                    v-model="charityDataForm.totalBeneficiaries"
                    :min="0"
                    :max="1000000"
                    :step="100"
                    placeholder="请输入累计受益人数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.totalBeneficiaries }}人</div>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="合作机构总数" prop="totalInstitutions">
                  <el-input-number
                    v-model="charityDataForm.totalInstitutions"
                    :min="0"
                    :max="10000"
                    :step="1"
                    placeholder="请输入合作机构总数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.totalInstitutions }}家</div>
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="活跃合作机构" prop="cooperatingInstitutions">
                  <el-input-number
                    v-model="charityDataForm.cooperatingInstitutions"
                    :min="0"
                    :max="10000"
                    :step="1"
                    placeholder="请输入活跃合作机构数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.cooperatingInstitutions }}家</div>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="志愿者总数" prop="totalVolunteers">
                  <el-input-number
                    v-model="charityDataForm.totalVolunteers"
                    :min="0"
                    :max="100000"
                    :step="10"
                    placeholder="请输入志愿者总数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.totalVolunteers }}人</div>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 资金统计数据 -->
          <div class="form-section">
            <h3>资金统计数据</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="累计筹集金额" prop="totalRaised">
                  <el-input-number
                    v-model="charityDataForm.totalRaised"
                    :min="0"
                    :max="100000000"
                    :step="10000"
                    placeholder="请输入累计筹集金额"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: ¥{{ currentStats.totalRaised?.toLocaleString() }}</div>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="累计捐赠金额" prop="totalDonated">
                  <el-input-number
                    v-model="charityDataForm.totalDonated"
                    :min="0"
                    :max="100000000"
                    :step="10000"
                    placeholder="请输入累计捐赠金额"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: ¥{{ currentStats.totalDonated?.toLocaleString() }}</div>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 项目统计数据 -->
          <div class="form-section">
            <h3>项目统计数据</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="项目总数" prop="totalProjects">
                  <el-input-number
                    v-model="charityDataForm.totalProjects"
                    :min="0"
                    :max="10000"
                    :step="1"
                    placeholder="请输入项目总数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.totalProjects }}个</div>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="进行中项目" prop="activeProjects">
                  <el-input-number
                    v-model="charityDataForm.activeProjects"
                    :min="0"
                    :max="10000"
                    :step="1"
                    placeholder="请输入进行中项目数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.activeProjects }}个</div>
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="已完成项目" prop="completedProjects">
                  <el-input-number
                    v-model="charityDataForm.completedProjects"
                    :min="0"
                    :max="10000"
                    :step="1"
                    placeholder="请输入已完成项目数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.completedProjects }}个</div>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 活动统计数据 -->
          <div class="form-section">
            <h3>活动统计数据</h3>
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="活动总数" prop="totalActivities">
                  <el-input-number
                    v-model="charityDataForm.totalActivities"
                    :min="0"
                    :max="100000"
                    :step="10"
                    placeholder="请输入活动总数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.totalActivities }}个</div>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="本月活动数" prop="thisMonthActivities">
                  <el-input-number
                    v-model="charityDataForm.thisMonthActivities"
                    :min="0"
                    :max="1000"
                    :step="1"
                    placeholder="请输入本月活动数"
                    style="width: 100%"
                  />
                  <div class="form-tip">当前: {{ currentStats.thisMonthActivities }}个</div>
                </el-form-item>
              </el-col>
            </el-row>
          </div>

          <!-- 更新说明 -->
          <div class="form-section">
            <h3>更新说明</h3>
            <el-form-item label="更新原因" prop="updateReason">
              <el-input
                v-model="charityDataForm.updateReason"
                type="textarea"
                :rows="3"
                placeholder="请输入数据更新的原因或说明"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </div>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDataUpdateDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmitDataUpdate" :loading="dataUpdateLoading">
            更新数据
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import CharityStatsOverview from '@/components/charity/CharityStatsOverview.vue'
import InstitutionManagement from '@/components/charity/InstitutionManagement.vue'
import ActivityManagement from '@/components/charity/ActivityManagement.vue'
import CharityAPI from '@/api/charity'
import * as echarts from 'echarts'

// 响应式数据
const activeTab = ref('institutions')
const loading = ref(true)

// 组件引用
const statsOverviewRef = ref()
const institutionManagementRef = ref()
const activityManagementRef = ref()

// 数据录入对话框状态
const dataUpdateDialogVisible = ref(false)
const dataUpdateLoading = ref(false)
const dataUpdateFormRef = ref()

// 图表引用
const projectStatusChart = ref<HTMLElement>()
const fundingTrendChart = ref<HTMLElement>()
const regionDistributionChart = ref<HTMLElement>()
const volunteerActivityChart = ref<HTMLElement>()

// 图表实例
let projectStatusChartInstance: echarts.ECharts | null = null
let fundingTrendChartInstance: echarts.ECharts | null = null
let regionDistributionChartInstance: echarts.ECharts | null = null
let volunteerActivityChartInstance: echarts.ECharts | null = null

// 当前统计数据
const currentStats = ref({
  totalBeneficiaries: 28650,
  totalInstitutions: 342,
  cooperatingInstitutions: 198,
  totalVolunteers: 285,
  totalRaised: 18500000,
  totalDonated: 15200000,
  totalProjects: 156,
  activeProjects: 42,
  completedProjects: 89,
  totalActivities: 456,
  thisMonthActivities: 28
})

// 公益数据表单
const charityDataForm = reactive({
  totalBeneficiaries: 0,
  totalInstitutions: 0,
  cooperatingInstitutions: 0,
  totalVolunteers: 0,
  totalRaised: 0,
  totalDonated: 0,
  totalProjects: 0,
  activeProjects: 0,
  completedProjects: 0,
  totalActivities: 0,
  thisMonthActivities: 0,
  updateReason: ''
})

// 表单验证规则
const charityDataRules = {
  totalBeneficiaries: [
    { required: true, message: '请输入累计受益人数', trigger: 'blur' },
    { type: 'number', min: 0, message: '受益人数不能小于0', trigger: 'blur' }
  ],
  totalInstitutions: [
    { required: true, message: '请输入合作机构总数', trigger: 'blur' },
    { type: 'number', min: 0, message: '机构总数不能小于0', trigger: 'blur' }
  ],
  cooperatingInstitutions: [
    { required: true, message: '请输入活跃合作机构数', trigger: 'blur' },
    { type: 'number', min: 0, message: '活跃机构数不能小于0', trigger: 'blur' }
  ],
  totalVolunteers: [
    { required: true, message: '请输入志愿者总数', trigger: 'blur' },
    { type: 'number', min: 0, message: '志愿者总数不能小于0', trigger: 'blur' }
  ],
  totalRaised: [
    { required: true, message: '请输入累计筹集金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '筹集金额不能小于0', trigger: 'blur' }
  ],
  totalDonated: [
    { required: true, message: '请输入累计捐赠金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '捐赠金额不能小于0', trigger: 'blur' }
  ],
  totalProjects: [
    { required: true, message: '请输入项目总数', trigger: 'blur' },
    { type: 'number', min: 0, message: '项目总数不能小于0', trigger: 'blur' }
  ],
  activeProjects: [
    { required: true, message: '请输入进行中项目数', trigger: 'blur' },
    { type: 'number', min: 0, message: '进行中项目数不能小于0', trigger: 'blur' }
  ],
  completedProjects: [
    { required: true, message: '请输入已完成项目数', trigger: 'blur' },
    { type: 'number', min: 0, message: '已完成项目数不能小于0', trigger: 'blur' }
  ],
  totalActivities: [
    { required: true, message: '请输入活动总数', trigger: 'blur' },
    { type: 'number', min: 0, message: '活动总数不能小于0', trigger: 'blur' }
  ],
  thisMonthActivities: [
    { required: true, message: '请输入本月活动数', trigger: 'blur' },
    { type: 'number', min: 0, message: '本月活动数不能小于0', trigger: 'blur' }
  ],
  updateReason: [
    { required: true, message: '请输入更新原因', trigger: 'blur' },
    { min: 5, max: 200, message: '更新原因长度在5到200个字符之间', trigger: 'blur' }
  ]
}





// 方法





// 公益数据更新相关方法
const handleUpdateCharityData = async () => {
  try {
    // 加载当前统计数据
    const response = await CharityAPI.getEnhancedCharityStats()
    if (response.code === 200) {
      currentStats.value = response.data
      
      // 初始化表单数据为当前数据
      Object.assign(charityDataForm, {
        totalBeneficiaries: currentStats.value.totalBeneficiaries,
        totalInstitutions: currentStats.value.totalInstitutions,
        cooperatingInstitutions: currentStats.value.cooperatingInstitutions,
        totalVolunteers: currentStats.value.totalVolunteers,
        totalRaised: currentStats.value.totalRaised,
        totalDonated: currentStats.value.totalDonated,
        totalProjects: currentStats.value.totalProjects,
        activeProjects: currentStats.value.activeProjects,
        completedProjects: currentStats.value.completedProjects,
        totalActivities: currentStats.value.totalActivities,
        thisMonthActivities: currentStats.value.thisMonthActivities,
        updateReason: ''
      })
    }
    
    dataUpdateDialogVisible.value = true
  } catch (error) {
    console.error('加载当前统计数据失败:', error)
    ElMessage.error('加载当前数据失败，请稍后重试')
  }
}

const handleDataUpdateDialogClose = () => {
  // 重置表单
  if (dataUpdateFormRef.value) {
    dataUpdateFormRef.value.resetFields()
  }
  
  // 重置表单数据
  Object.assign(charityDataForm, {
    totalBeneficiaries: 0,
    totalInstitutions: 0,
    cooperatingInstitutions: 0,
    totalVolunteers: 0,
    totalRaised: 0,
    totalDonated: 0,
    totalProjects: 0,
    activeProjects: 0,
    completedProjects: 0,
    totalActivities: 0,
    thisMonthActivities: 0,
    updateReason: ''
  })
  
  dataUpdateDialogVisible.value = false
}

const handleSubmitDataUpdate = async () => {
  if (!dataUpdateFormRef.value) return
  
  try {
    // 表单验证
    await dataUpdateFormRef.value.validate()
    
    dataUpdateLoading.value = true
    
    // 数据验证逻辑
    const validationErrors = validateCharityData()
    if (validationErrors.length > 0) {
      ElMessage.error(validationErrors[0])
      return
    }
    
    // 模拟API调用更新数据
    await updateCharityData()
    
    ElMessage.success('公益数据更新成功！')
    
    // 更新当前统计数据
    Object.assign(currentStats.value, {
      totalBeneficiaries: charityDataForm.totalBeneficiaries,
      totalInstitutions: charityDataForm.totalInstitutions,
      cooperatingInstitutions: charityDataForm.cooperatingInstitutions,
      totalVolunteers: charityDataForm.totalVolunteers,
      totalRaised: charityDataForm.totalRaised,
      totalDonated: charityDataForm.totalDonated,
      totalProjects: charityDataForm.totalProjects,
      activeProjects: charityDataForm.activeProjects,
      completedProjects: charityDataForm.completedProjects,
      totalActivities: charityDataForm.totalActivities,
      thisMonthActivities: charityDataForm.thisMonthActivities
    })
    
    // 刷新统计数据概览组件
    if (statsOverviewRef.value) {
      await statsOverviewRef.value.refresh()
    }
    
    // 如果当前在统计页面，刷新图表
    if (activeTab.value === 'statistics') {
      await nextTick()
      initCharts()
    }
    
    // 关闭对话框
    handleDataUpdateDialogClose()
    
  } catch (error) {
    console.error('更新公益数据失败:', error)
    ElMessage.error('更新数据失败，请检查输入信息')
  } finally {
    dataUpdateLoading.value = false
  }
}

// 数据验证逻辑
const validateCharityData = () => {
  const errors: string[] = []
  
  // 验证活跃机构数不能超过总机构数
  if (charityDataForm.cooperatingInstitutions > charityDataForm.totalInstitutions) {
    errors.push('活跃合作机构数不能超过合作机构总数')
  }
  
  // 验证项目数量逻辑
  if (charityDataForm.activeProjects + charityDataForm.completedProjects > charityDataForm.totalProjects) {
    errors.push('进行中项目数和已完成项目数之和不能超过项目总数')
  }
  
  // 验证捐赠金额不能超过筹集金额
  if (charityDataForm.totalDonated > charityDataForm.totalRaised) {
    errors.push('累计捐赠金额不能超过累计筹集金额')
  }
  
  // 验证本月活动数不能超过总活动数
  if (charityDataForm.thisMonthActivities > charityDataForm.totalActivities) {
    errors.push('本月活动数不能超过活动总数')
  }
  
  return errors
}

// 更新数据API调用
const updateCharityData = async () => {
  try {
    const response = await CharityAPI.updateCharityStats(charityDataForm)
    
    if (response.code !== 200) {
      throw new Error(response.message || '更新失败')
    }
    
    console.log('公益数据更新成功:', response.data)
    return response
  } catch (error) {
    console.error('更新公益数据失败:', error)
    throw error
  }
}



// 图表数据
const chartData = ref<any>(null)

// 加载图表数据
const loadChartData = async () => {
  try {
    const response = await CharityAPI.getCharityChartData()
    chartData.value = response.data
    console.log('图表数据已加载:', chartData.value)
  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

// 图表初始化方法
const initCharts = async () => {
  await nextTick()
  
  if (activeTab.value === 'statistics') {
    // 确保图表数据已加载
    if (!chartData.value) {
      await loadChartData()
    }
    
    initProjectStatusChart()
    initFundingTrendChart()
    initRegionDistributionChart()
    initVolunteerActivityChart()
  }
}

// 项目状态分布图表
const initProjectStatusChart = () => {
  if (!projectStatusChart.value || !chartData.value) return
  
  if (projectStatusChartInstance) {
    projectStatusChartInstance.dispose()
  }
  
  projectStatusChartInstance = echarts.init(projectStatusChart.value)
  
  // 处理后端返回的数据格式
  const projectStatusData = chartData.value.projectStatusData
  let statusData = []
  
  if (projectStatusData && projectStatusData.series && Array.isArray(projectStatusData.series)) {
    // 后端返回的格式：{ series: [{ name, value }], categories: [...] }
    statusData = projectStatusData.series.map((item, index) => ({
      name: item.name,
      value: item.value,
      color: ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399'][index % 5]
    }))
  } else {
    // 默认数据
    statusData = [
      { name: '进行中', value: 42, color: '#409EFF' },
      { name: '已完成', value: 89, color: '#67C23A' },
      { name: '规划中', value: 25, color: '#E6A23C' }
    ]
  }
  
  const option = {
    title: {
      text: '项目状态分布',
      left: 'center',
      textStyle: {
        fontSize: 16,
        color: '#303133',
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c}个 ({d}%)',
      backgroundColor: 'rgba(50,50,50,0.7)',
      borderColor: '#333',
      textStyle: {
        color: '#fff'
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'middle',
      data: statusData.map(item => item.name),
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '项目状态',
        type: 'pie',
        radius: ['30%', '60%'],
        center: ['60%', '50%'],
        data: statusData.map(item => ({
          value: item.value,
          name: item.name,
          itemStyle: {
            color: item.color
          }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          show: true,
          formatter: '{b}: {c}个'
        }
      }
    ]
  }
  
  projectStatusChartInstance.setOption(option)
}

// 资金筹集趋势图表
const initFundingTrendChart = () => {
  if (!fundingTrendChart.value) return
  
  if (fundingTrendChartInstance) {
    fundingTrendChartInstance.dispose()
  }
  
  fundingTrendChartInstance = echarts.init(fundingTrendChart.value)
  
  // 生成模拟的月度数据
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  const raisedData = months.map(() => Math.floor(Math.random() * 50000) + 20000)
  const donatedData = months.map(() => Math.floor(Math.random() * 40000) + 15000)
  
  const option = {
    title: {
      text: '资金筹集趋势',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['筹集金额', '捐赠金额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}元'
      }
    },
    series: [
      {
        name: '筹集金额',
        type: 'line',
        stack: 'Total',
        data: raisedData,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '捐赠金额',
        type: 'line',
        stack: 'Total',
        data: donatedData,
        itemStyle: {
          color: '#67C23A'
        }
      }
    ]
  }
  
  fundingTrendChartInstance.setOption(option)
}

// 地区分布图表
const initRegionDistributionChart = () => {
  if (!regionDistributionChart.value) return
  
  if (regionDistributionChartInstance) {
    regionDistributionChartInstance.dispose()
  }
  
  regionDistributionChartInstance = echarts.init(regionDistributionChart.value)
  
  const regions = ['华北', '华东', '华南', '华中', '西南', '西北', '东北']
  const data = regions.map(region => ({
    name: region,
    value: Math.floor(Math.random() * 20) + 5
  }))
  
  const option = {
    title: {
      text: '项目地区分布',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'item'
    },
    series: [
      {
        name: '项目数量',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '30',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }
  
  regionDistributionChartInstance.setOption(option)
}

// 志愿者活动统计图表
const initVolunteerActivityChart = () => {
  if (!volunteerActivityChart.value) return
  
  if (volunteerActivityChartInstance) {
    volunteerActivityChartInstance.dispose()
  }
  
  volunteerActivityChartInstance = echarts.init(volunteerActivityChart.value)
  
  const months = ['1月', '2月', '3月', '4月', '5月', '6月']
  const volunteerData = months.map(() => Math.floor(Math.random() * 100) + 50)
  const activityData = months.map(() => Math.floor(Math.random() * 20) + 10)
  
  const option = {
    title: {
      text: '志愿者活动统计',
      left: 'center',
      textStyle: {
        fontSize: 14,
        color: '#303133'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['志愿者人数', '活动次数']
    },
    xAxis: [
      {
        type: 'category',
        data: months,
        axisPointer: {
          type: 'shadow'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '志愿者人数',
        min: 0,
        axisLabel: {
          formatter: '{value} 人'
        }
      },
      {
        type: 'value',
        name: '活动次数',
        min: 0,
        axisLabel: {
          formatter: '{value} 次'
        }
      }
    ],
    series: [
      {
        name: '志愿者人数',
        type: 'bar',
        data: volunteerData,
        itemStyle: {
          color: '#409EFF'
        }
      },
      {
        name: '活动次数',
        type: 'line',
        yAxisIndex: 1,
        data: activityData,
        itemStyle: {
          color: '#E6A23C'
        }
      }
    ]
  }
  
  volunteerActivityChartInstance.setOption(option)
}

// 监听activeTab变化，初始化图表
watch(activeTab, async (newTab) => {
  if (newTab === 'statistics') {
    await nextTick()
    initCharts()
  }
})

// 生命周期
onMounted(async () => {
  loading.value = true
  try {
    // 如果默认显示统计页面，初始化图表
    if (activeTab.value === 'statistics') {
      await initCharts()
    }
  } catch (error) {
    console.error('页面初始化失败:', error)
  } finally {
    loading.value = false
  }
})
</script>

<style lang="scss" scoped>
.charity-management {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    .header-left {
      h2 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 24px;
        font-weight: 600;
      }

      .header-subtitle {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }

    .header-right {
      display: flex;
      gap: 12px;
    }
  }



  .charity-tabs {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 20px;

    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }

    .statistics-section {
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 24px;

        h3 {
          margin: 0;
          color: #303133;
          font-size: 18px;
          font-weight: 600;
        }
      }

      .charts-section {
        margin-bottom: 32px;

        .charts-row {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
          gap: 24px;
          margin-bottom: 24px;

          &:last-child {
            margin-bottom: 0;
          }

          .chart-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            overflow: hidden;

            .chart-header {
              padding: 16px 20px;
              background: #f8f9fa;
              border-bottom: 1px solid #e9ecef;

              h4 {
                margin: 0;
                color: #303133;
                font-size: 16px;
                font-weight: 600;
              }
            }

            .chart-content {
              padding: 20px;

              .chart-container {
                width: 100%;
                min-height: 300px;
              }
            }
          }
        }
      }

      .stats-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
        gap: 32px;

        .stats-category {
          h4 {
            margin: 0 0 16px 0;
            color: #409EFF;
            font-size: 16px;
            font-weight: 600;
            border-bottom: 2px solid #409EFF;
            padding-bottom: 8px;
          }

          .stats-items {
            display: grid;
            gap: 12px;

            .stat-item {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 12px 16px;
              background: #f8f9fa;
              border-radius: 6px;
              border-left: 4px solid #409EFF;

              .label {
                color: #606266;
                font-size: 14px;
              }

              .value {
                color: #303133;
                font-size: 16px;
                font-weight: 600;
              }
            }
          }
        }
      }
    }

    .institutions-section,
    .news-section {
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        h3 {
          margin: 0;
          color: #303133;
          font-size: 18px;
          font-weight: 600;
        }
      }
    }

    .works-section {
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        h3 {
          margin: 0;
          color: #303133;
          font-size: 18px;
          font-weight: 600;
        }
      }

      .works-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 20px;

        .work-item {
          background: white;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          transition: transform 0.2s, box-shadow 0.2s;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
          }

          .work-image {
            width: 100%;
            height: 200px;
            object-fit: cover;
          }

          .work-info {
            padding: 16px;

            .work-title {
              margin: 0 0 8px 0;
              color: #303133;
              font-size: 16px;
              font-weight: 600;
              line-height: 1.4;
            }

            .work-meta {
              margin: 0 0 12px 0;
              color: #909399;
              font-size: 14px;
            }

            .work-actions {
              display: flex;
              gap: 8px;
              flex-wrap: wrap;
            }
          }
        }
      }
    }
  }

  .debug-info {
    margin-bottom: 24px;
    
    .el-alert {
      p {
        margin: 4px 0;
        font-size: 14px;
      }
    }
  }
}

// 公益数据录入对话框样式
.data-update-form {
  .form-section {
    margin-bottom: 32px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid #409EFF;

    &:last-child {
      margin-bottom: 0;
    }

    h3 {
      margin: 0 0 20px 0;
      color: #303133;
      font-size: 16px;
      font-weight: 600;
      display: flex;
      align-items: center;

      &::before {
        content: '';
        width: 4px;
        height: 16px;
        background: #409EFF;
        margin-right: 8px;
        border-radius: 2px;
      }
    }

    .form-tip {
      margin-top: 4px;
      color: #909399;
      font-size: 12px;
      line-height: 1.4;
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 20px;

    .el-form-item__label {
      color: #606266;
      font-weight: 500;
    }

    .el-input-number {
      .el-input__inner {
        text-align: left;
      }
    }

    .el-textarea {
      .el-textarea__inner {
        resize: vertical;
        min-height: 80px;
      }
    }
  }

  :deep(.el-row) {
    .el-col {
      &:not(:last-child) {
        padding-right: 12px;
      }
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

// 响应式设计
@media (max-width: 768px) {
  .charity-management {
    padding: 16px;

    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;

      .header-right {
        width: 100%;
        justify-content: flex-end;
      }
    }

    .charity-overview {
      grid-template-columns: 1fr;
    }

    .charity-tabs {
      padding: 16px;

      .statistics-section .stats-grid {
        grid-template-columns: 1fr;
      }

      .works-section .works-grid {
        grid-template-columns: 1fr;
      }
    }

    // 数据更新对话框响应式
    .data-update-form {
      .form-section {
        padding: 16px;
        margin-bottom: 20px;

        :deep(.el-row) {
          .el-col {
            padding-right: 0;
            margin-bottom: 16px;

            &:last-child {
              margin-bottom: 0;
            }
          }
        }
      }
    }
  }
}
</style>