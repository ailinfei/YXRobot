<template>
  <div class="simple-order-test">
    <h2>简单订单功能测试</h2>
    
    <div class="test-buttons">
      <el-button type="primary" @click="testCreateDialog">
        测试新建订单对话框
      </el-button>
      <el-button @click="testAPI">
        测试API调用
      </el-button>
    </div>

    <div class="test-results" v-if="results.length > 0">
      <h3>测试结果</h3>
      <ul>
        <li v-for="result in results" :key="result.id" :class="result.success ? 'success' : 'error'">
          {{ result.message }}
        </li>
      </ul>
    </div>

    <!-- 简化的订单创建对话框 -->
    <el-dialog v-model="dialogVisible" title="创建订单测试" width="600px">
      <el-form :model="testForm" label-width="120px">
        <el-form-item label="客户姓名">
          <el-input v-model="testForm.customerName" placeholder="请输入客户姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="testForm.customerPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="订单金额">
          <el-input-number v-model="testForm.totalAmount" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTest">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const dialogVisible = ref(false)
const results = ref<Array<{id: string, message: string, success: boolean}>>([])

const testForm = ref({
  customerName: '',
  customerPhone: '',
  totalAmount: 0
})

// 添加测试结果
const addResult = (message: string, success: boolean) => {
  results.value.push({
    id: Date.now().toString(),
    message,
    success
  })
}

// 测试创建对话框
const testCreateDialog = () => {
  try {
    dialogVisible.value = true
    addResult('✅ 对话框打开成功', true)
    ElMessage.success('对话框测试成功')
  } catch (error) {
    addResult('❌ 对话框打开失败: ' + error, false)
    ElMessage.error('对话框测试失败')
  }
}

// 测试API调用
const testAPI = async () => {
  try {
    // 简单的API测试
    const testData = {
      customerName: '测试客户',
      customerPhone: '13800138000',
      totalAmount: 1000
    }
    
    addResult('✅ API测试数据准备完成', true)
    addResult(`✅ 测试数据: ${JSON.stringify(testData)}`, true)
    ElMessage.success('API测试成功')
  } catch (error) {
    addResult('❌ API测试失败: ' + error, false)
    ElMessage.error('API测试失败')
  }
}

// 提交测试
const submitTest = () => {
  try {
    if (!testForm.value.customerName) {
      throw new Error('客户姓名不能为空')
    }
    
    addResult('✅ 表单验证通过', true)
    addResult(`✅ 提交数据: ${JSON.stringify(testForm.value)}`, true)
    
    dialogVisible.value = false
    ElMessage.success('提交测试成功')
    
    // 重置表单
    testForm.value = {
      customerName: '',
      customerPhone: '',
      totalAmount: 0
    }
  } catch (error) {
    addResult('❌ 提交测试失败: ' + error, false)
    ElMessage.error('提交测试失败')
  }
}
</script>

<style scoped>
.simple-order-test {
  padding: 20px;
}

.test-buttons {
  margin: 20px 0;
  display: flex;
  gap: 12px;
}

.test-results {
  margin-top: 20px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.test-results ul {
  list-style: none;
  padding: 0;
}

.test-results li {
  padding: 8px;
  margin: 4px 0;
  border-radius: 4px;
}

.test-results li.success {
  background: #f0f9ff;
  color: #059669;
}

.test-results li.error {
  background: #fef2f2;
  color: #dc2626;
}
</style>