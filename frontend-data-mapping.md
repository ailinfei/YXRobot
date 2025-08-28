# 前端数据映射解决方案

## 问题描述
前端发送的公益活动数据包含中文值，但后端验证要求英文值，导致400错误。

## 数据映射规则

### 活动类型 (type)
前端显示值 → 后端API值：
- "捐赠" → "donation"
- "志愿服务" → "volunteer" 
- "筹款" → "fundraising"
- "教育培训" → "education"
- "医疗援助" → "medical"
- "其他" → "other"
- "特殊活动" → "other"

### 活动状态 (status)
前端显示值 → 后端API值：
- "计划中" → "planned"
- "已计划" → "planned"
- "进行中" → "ongoing"
- "已完成" → "completed"
- "已取消" → "cancelled"
- "已延期" → "postponed"

## 前端修复建议

### 1. 创建数据转换函数

```javascript
// 活动类型映射
const typeMapping = {
  '捐赠': 'donation',
  '志愿服务': 'volunteer',
  '筹款': 'fundraising', 
  '教育培训': 'education',
  '医疗援助': 'medical',
  '其他': 'other',
  '特殊活动': 'other'
};

// 活动状态映射
const statusMapping = {
  '计划中': 'planned',
  '已计划': 'planned',
  '进行中': 'ongoing', 
  '已完成': 'completed',
  '已取消': 'cancelled',
  '已延期': 'postponed'
};

// 数据转换函数
function transformActivityData(formData) {
  return {
    ...formData,
    type: typeMapping[formData.type] || formData.type,
    status: statusMapping[formData.status] || formData.status
  };
}
```

### 2. 在API调用前转换数据

```javascript
// 创建活动
async function createActivity(formData) {
  const transformedData = transformActivityData(formData);
  return await CharityAPI.createCharityActivity(transformedData);
}

// 更新活动  
async function updateActivity(id, formData) {
  const transformedData = transformActivityData(formData);
  return await CharityAPI.updateCharityActivity(id, transformedData);
}
```

### 3. 日期格式处理

确保日期格式正确：
```javascript
function formatDate(dateValue) {
  if (!dateValue) return null;
  
  // 如果是日期对象，转换为 YYYY-MM-DD 格式
  if (dateValue instanceof Date) {
    return dateValue.toISOString().split('T')[0];
  }
  
  // 如果是字符串，提取日期部分
  if (typeof dateValue === 'string') {
    return dateValue.split(' ')[0];
  }
  
  return dateValue;
}
```

## 测试验证

使用正确的数据格式测试：
```json
{
  "projectId": 1,
  "title": "测试活动",
  "description": "测试描述", 
  "type": "other",
  "date": "2025-08-21",
  "location": "测试地点",
  "participants": 50,
  "targetParticipants": 100,
  "organizer": "测试组织",
  "status": "planned",
  "budget": 10000.00,
  "manager": "测试负责人",
  "managerContact": "13800138000",
  "volunteerNeeded": 10,
  "notes": "测试备注"
}
```

## 后端验证规则

当前后端验证规则：
- type: `^(donation|volunteer|fundraising|education|medical|other)$`
- status: `^(planned|ongoing|completed|cancelled|postponed)$`
- 所有必填字段不能为空
- 字符串长度限制
- 数字字段不能为负数