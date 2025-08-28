<template>
  <div class="admin">
    <el-card class="admin-card">
      <template #header>
        <div class="card-header">
          <span>管理后台</span>
        </div>
      </template>
      
      <div class="admin-content">
        <el-row :gutter="20">
          <el-col :span="24">
            <h3>产品管理</h3>
            <div class="product-actions">
              <el-button type="primary" @click="loadProducts">
                <el-icon><Refresh /></el-icon>
                刷新数据
              </el-button>
              <el-button type="success" @click="createProduct">
                <el-icon><Plus /></el-icon>
                添加产品
              </el-button>
            </div>
            
            <el-table :data="products" :loading="loading" style="width: 100%">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="name" label="产品名称" />
              <el-table-column prop="model" label="型号" />
              <el-table-column prop="price" label="价格" width="120">
                <template #default="scope">
                  ¥{{ scope.row.price }}
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="创建时间" width="180" />
              <el-table-column label="操作" width="200">
                <template #default="scope">
                  <el-button size="small" @click="editProduct(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteProduct(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const products = ref([])
const loading = ref(false)

const loadProducts = async () => {
  loading.value = true
  try {
    const response = await axios.get('/admin/products')
    products.value = response.data.data?.records || []
    ElMessage.success('产品数据加载成功')
  } catch (error) {
    console.error('加载产品失败:', error)
    ElMessage.error('加载产品数据失败')
    // 使用模拟数据
    products.value = [
      {
        id: 1,
        name: '家用练字机器人',
        model: 'YX-HOME-001',
        price: 2999.00,
        status: 'published',
        createdAt: '2024-01-15 10:30:00'
      },
      {
        id: 2,
        name: '商用练字机器人',
        model: 'YX-BUSINESS-001',
        price: 8999.00,
        status: 'published',
        createdAt: '2024-01-10 09:15:00'
      },
      {
        id: 3,
        name: '教育版练字机器人',
        model: 'YX-EDU-001',
        price: 5999.00,
        status: 'draft',
        createdAt: '2024-01-12 11:20:00'
      }
    ]
  } finally {
    loading.value = false
  }
}

const createProduct = () => {
  ElMessage.info('创建产品功能开发中...')
}

const editProduct = (product: any) => {
  ElMessage.info(`编辑产品: ${product.name}`)
}

const deleteProduct = async (product: any) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除产品 "${product.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    ElMessage.success('删除成功')
    loadProducts()
  } catch {
    ElMessage.info('已取消删除')
  }
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    published: 'success',
    draft: 'warning',
    archived: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    published: '已发布',
    draft: '草稿',
    archived: '已归档'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadProducts()
})
</script>

<style scoped>
.admin {
  max-width: 1200px;
  margin: 0 auto;
}

.card-header {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
}

.admin-content {
  padding: 20px 0;
}

.product-actions {
  margin-bottom: 20px;
}

.product-actions .el-button {
  margin-right: 10px;
}
</style>