<template>
  <div class="component-test">
    <h2>通用组件测试页面</h2>
    
    <!-- 数据表格组件测试 -->
    <div class="test-section">
      <h3>数据表格组件 (DataTable)</h3>
      <DataTable
        :data="tableData"
        :columns="tableColumns"
        :loading="false"
        @add="handleAdd"
        @edit="handleEdit"
        @delete="handleDelete"
      />
    </div>
    
    <!-- 图表组件测试 -->
    <div class="test-section">
      <h3>图表组件 (ChartContainer)</h3>
      <ChartContainer
        title="销售数据统计"
        subtitle="最近30天销售趋势"
        :option="chartOption"
        :loading="false"
        height="300px"
      />
    </div>
    
    <!-- 文件上传组件测试 -->
    <div class="test-section">
      <h3>文件上传组件 (FileUpload)</h3>
      <FileUpload
        v-model="uploadFiles"
        :multiple="true"
        :drag="true"
        accept="image/*,video/*,.pdf,.doc,.docx"
        :max-size="10"
        :limit="5"
        upload-text="上传文件"
        upload-hint="支持拖拽上传，支持图片、视频、文档格式"
      />
    </div>
    
    <!-- 表单验证组件测试 -->
    <div class="test-section">
      <h3>表单验证组件 (FormValidator)</h3>
      <FormValidator
        v-model="formData"
        :rules="formRules"
        @submit="handleSubmit"
        @reset="handleReset"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="formData.age" :min="1" :max="120" />
        </el-form-item>
      </FormValidator>
    </div>
    
    <!-- 通用弹窗组件测试 -->
    <div class="test-section">
      <h3>通用弹窗组件 (CommonDialog)</h3>
      <el-button type="primary" @click="showDialog = true">打开弹窗</el-button>
      <CommonDialog
        v-model="showDialog"
        title="测试弹窗"
        width="600px"
        @confirm="handleDialogConfirm"
        @cancel="handleDialogCancel"
      >
        <p>这是一个测试弹窗的内容。</p>
        <p>可以在这里放置任何内容。</p>
      </CommonDialog>
    </div>
    
    <!-- 其他辅助组件测试 -->
    <div class="test-section">
      <h3>其他辅助组件</h3>
      
      <!-- 状态标签 -->
      <div class="component-demo">
        <h4>状态标签 (StatusTag)</h4>
        <div class="tag-demo">
          <StatusTag status="active" />
          <StatusTag status="pending" />
          <StatusTag status="failed" />
          <StatusTag status="online" />
          <StatusTag status="offline" />
        </div>
      </div>
      
      <!-- 数据卡片 -->
      <div class="component-demo">
        <h4>数据卡片 (DataCard)</h4>
        <div class="card-demo">
          <DataCard
            title="总销售额"
            type="number"
            :value="125680"
            unit="元"
            :change="12.5"
            change-type="percent"
            theme="primary"
          />
          <DataCard
            title="完成进度"
            type="progress"
            :value="75"
            progress-label="项目进度"
          />
        </div>
      </div>
      
      <!-- 搜索过滤器 -->
      <div class="component-demo">
        <h4>搜索过滤器 (SearchFilter)</h4>
        <SearchFilter
          :filters="filterConfig"
          @search="handleSearch"
          @filter="handleFilter"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  DataTable,
  ChartContainer,
  FileUpload,
  FormValidator,
  CommonDialog,
  StatusTag,
  DataCard,
  SearchFilter
} from './index'
import type { TableColumn, FilterConfig } from './index'

// 数据表格测试数据
const tableData = ref([
  { id: 1, name: '张三', email: 'zhangsan@example.com', status: 'active', createTime: '2024-01-01' },
  { id: 2, name: '李四', email: 'lisi@example.com', status: 'pending', createTime: '2024-01-02' },
  { id: 3, name: '王五', email: 'wangwu@example.com', status: 'inactive', createTime: '2024-01-03' }
])

const tableColumns: TableColumn[] = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'name', label: '姓名', minWidth: 100 },
  { prop: 'email', label: '邮箱', minWidth: 200 },
  { 
    prop: 'status', 
    label: '状态', 
    width: 100,
    type: 'tag',
    tagMap: {
      active: { text: '激活', type: 'success' },
      pending: { text: '待审核', type: 'warning' },
      inactive: { text: '禁用', type: 'danger' }
    }
  },
  { prop: 'createTime', label: '创建时间', width: 150, type: 'date' }
]

// 图表测试数据
const chartOption = ref({
  title: {
    text: '销售趋势'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月']
  },
  yAxis: {
    type: 'value'
  },
  series: [{
    data: [120, 200, 150, 80, 70, 110],
    type: 'line',
    smooth: true
  }]
})

// 文件上传测试数据
const uploadFiles = ref([])

// 表单验证测试数据
const formData = reactive({
  username: '',
  email: '',
  age: null
})

const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' },
    { type: 'number', min: 1, max: 120, message: '年龄必须在 1 到 120 之间', trigger: 'blur' }
  ]
}

// 弹窗测试数据
const showDialog = ref(false)

// 搜索过滤器测试数据
const filterConfig: FilterConfig[] = [
  {
    key: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '激活', value: 'active' },
      { label: '待审核', value: 'pending' },
      { label: '禁用', value: 'inactive' }
    ]
  },
  {
    key: 'createTime',
    label: '创建时间',
    type: 'daterange'
  },
  {
    key: 'age',
    label: '年龄范围',
    type: 'numberrange',
    min: 0,
    max: 120
  }
]

// 事件处理函数
const handleAdd = () => {
  ElMessage.success('点击了新增按钮')
}

const handleEdit = (row: any) => {
  ElMessage.info(`编辑用户: ${row.name}`)
}

const handleDelete = (row: any) => {
  ElMessage.warning(`删除用户: ${row.name}`)
}

const handleSubmit = (data: any) => {
  ElMessage.success('表单提交成功')
  console.log('表单数据:', data)
}

const handleReset = () => {
  ElMessage.info('表单已重置')
}

const handleDialogConfirm = () => {
  ElMessage.success('确认操作')
  showDialog.value = false
}

const handleDialogCancel = () => {
  ElMessage.info('取消操作')
}

const handleSearch = (keyword: string) => {
  ElMessage.info(`搜索关键词: ${keyword}`)
}

const handleFilter = (filters: any) => {
  ElMessage.info('应用过滤条件')
  console.log('过滤条件:', filters)
}
</script>

<style lang="scss" scoped>
.component-test {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  
  h2 {
    color: var(--text-primary);
    margin-bottom: 30px;
    text-align: center;
  }
  
  .test-section {
    margin-bottom: 40px;
    padding: 20px;
    background: white;
    border-radius: var(--radius-lg);
    box-shadow: var(--shadow-sm);
    border: 1px solid var(--border-color);
    
    h3 {
      color: var(--text-primary);
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid var(--primary-color);
    }
  }
  
  .component-demo {
    margin-bottom: 20px;
    
    h4 {
      color: var(--text-secondary);
      margin-bottom: 10px;
    }
    
    .tag-demo {
      display: flex;
      gap: 10px;
      flex-wrap: wrap;
    }
    
    .card-demo {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
    }
  }
}
</style>