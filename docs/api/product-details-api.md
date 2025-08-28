# 产品详情API文档

## 接口概述

获取指定产品的详细信息，包括产品的所有字段数据。

## 接口信息

- **接口路径**: `/api/v1/products/{id}`
- **请求方法**: `GET`
- **接口描述**: 根据产品ID获取产品的完整详情信息

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 是 | 产品ID，必须为正整数 |

### 请求示例

```http
GET /api/v1/products/1 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
```

## 响应结果

### 成功响应 (200)

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "家用练字机器人",
    "model": "YX-HOME-001",
    "description": "适合家庭使用的智能练字机器人，支持多种字体和练习模式",
    "price": 2999.00,
    "coverImageUrl": "https://via.placeholder.com/150x150",
    "status": "published",
    "createdAt": "2024-01-15 10:30:00",
    "updatedAt": "2024-01-20 14:20:00"
  },
  "timestamp": "2024-12-19T10:00:00Z"
}
```

### 产品不存在 (404)

```json
{
  "code": 404,
  "message": "产品不存在",
  "data": null,
  "timestamp": "2024-12-19T10:00:00Z"
}
```

### 参数错误 (400)

```json
{
  "code": 400,
  "message": "产品ID不能为空",
  "data": null,
  "timestamp": "2024-12-19T10:00:00Z"
}
```

### 系统错误 (500)

```json
{
  "code": 500,
  "message": "获取产品详情失败",
  "data": null,
  "timestamp": "2024-12-19T10:00:00Z"
}
```

## 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 产品详情数据 |
| data.id | Long | 产品ID |
| data.name | String | 产品名称 |
| data.model | String | 产品型号 |
| data.description | String | 产品描述 |
| data.price | BigDecimal | 产品价格（元） |
| data.coverImageUrl | String | 封面图片URL |
| data.status | String | 产品状态：draft-草稿，published-已发布，archived-已归档 |
| data.createdAt | DateTime | 创建时间 |
| data.updatedAt | DateTime | 更新时间 |
| timestamp | DateTime | 响应时间戳 |

## 错误处理

### 参数验证

- 产品ID必须为正整数
- 产品ID不能为空或null

### 业务逻辑验证

- 检查产品是否存在
- 检查产品是否已被删除（软删除）

### 异常处理

- 数据库连接异常
- 数据查询异常
- 系统内部错误

## 使用示例

### cURL 示例

```bash
# 获取ID为1的产品详情
curl -X GET "http://localhost:8080/api/v1/products/1" \
     -H "Content-Type: application/json"

# 获取不存在的产品
curl -X GET "http://localhost:8080/api/v1/products/999" \
     -H "Content-Type: application/json"
```

### JavaScript 示例

```javascript
// 使用 fetch API
async function getProductDetails(productId) {
  try {
    const response = await fetch(`/api/v1/products/${productId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (result.code === 200) {
      console.log('产品详情:', result.data);
      return result.data;
    } else {
      console.error('获取失败:', result.message);
      return null;
    }
  } catch (error) {
    console.error('请求异常:', error);
    return null;
  }
}

// 使用示例
getProductDetails(1).then(product => {
  if (product) {
    console.log(`产品名称: ${product.name}`);
    console.log(`产品型号: ${product.model}`);
    console.log(`产品价格: ¥${product.price}`);
  }
});
```

### Vue.js 示例

```vue
<template>
  <div class="product-details">
    <div v-if="loading">加载中...</div>
    <div v-else-if="product">
      <h2>{{ product.name }}</h2>
      <p>型号: {{ product.model }}</p>
      <p>价格: ¥{{ product.price }}</p>
      <p>状态: {{ getStatusText(product.status) }}</p>
      <img v-if="product.coverImageUrl" :src="product.coverImageUrl" alt="产品封面" />
      <p>{{ product.description }}</p>
    </div>
    <div v-else-if="error">
      <p class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProductDetails',
  props: {
    productId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      product: null,
      loading: false,
      error: null
    };
  },
  async mounted() {
    await this.loadProduct();
  },
  methods: {
    async loadProduct() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await fetch(`/api/v1/products/${this.productId}`);
        const result = await response.json();
        
        if (result.code === 200) {
          this.product = result.data;
        } else {
          this.error = result.message;
        }
      } catch (err) {
        this.error = '获取产品详情失败';
      } finally {
        this.loading = false;
      }
    },
    getStatusText(status) {
      const statusMap = {
        'draft': '草稿',
        'published': '已发布',
        'archived': '已归档'
      };
      return statusMap[status] || status;
    }
  }
};
</script>
```

## 性能说明

- 接口响应时间: < 100ms（正常情况下）
- 支持并发请求数: 1000+
- 数据库查询优化: 使用索引优化查询性能
- 缓存策略: 可配置Redis缓存提升性能

## 版本历史

| 版本 | 日期 | 变更说明 |
|------|------|----------|
| v1.0 | 2024-12-19 | 初始版本，实现基础产品详情查询功能 |

## 注意事项

1. 产品ID必须为有效的正整数
2. 已删除的产品不会返回详情信息
3. 建议在前端实现适当的错误处理和用户提示
4. 对于高频访问的产品，建议启用缓存机制
5. 接口支持跨域访问（CORS）