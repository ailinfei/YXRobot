# 公益活动时间字段数据绑定修复总结

## 问题描述
在公益活动管理页面中，打开活动详情进行编辑时，活动时间字段没有正确显示数据，出现空白的情况。

## 问题原因分析
1. **数据结构不匹配**：后端DTO中使用了三个时间字段：
   - `date`: LocalDate类型，格式为"yyyy-MM-dd"
   - `startTime`: LocalDateTime类型，格式为"yyyy-MM-dd HH:mm:ss"  
   - `endTime`: LocalDateTime类型，格式为"yyyy-MM-dd HH:mm:ss"

2. **前端字段不匹配**：前端ActivityForm组件中只有一个`date`字段，期望datetime类型，无法正确映射后端的三个时间字段。

## 修复方案

### 1. 更新前端表单结构
将原来的单个时间字段拆分为三个字段：
- 活动日期（date）：使用el-date-picker，type="date"
- 开始时间（startTime）：使用el-time-picker
- 结束时间（endTime）：使用el-time-picker

### 2. 更新表单数据结构
```javascript
const formData = reactive({
  // ... 其他字段
  date: '',        // 活动日期 YYYY-MM-DD
  startTime: '',   // 开始时间 HH:mm:ss
  endTime: '',     // 结束时间 HH:mm:ss
  // ... 其他字段
})
```

### 3. 更新数据映射逻辑
在`initFormData`方法中添加时间字段的正确解析：
```javascript
// 从后端的 "2024-12-18 09:00:00" 格式中提取时间部分
if (props.activity.startTime) {
  const startTimeParts = props.activity.startTime.split(' ')
  if (startTimeParts.length > 1) {
    startTimeStr = startTimeParts[1] // "09:00:00"
  }
}
```

### 4. 更新数据提交逻辑
在`handleSubmit`方法中重新组装时间数据：
```javascript
// 组装时间数据
let startTime = ''
let endTime = ''

if (formData.date && formData.startTime) {
  startTime = `${formData.date} ${formData.startTime}`
}

if (formData.date && formData.endTime) {
  endTime = `${formData.date} ${formData.endTime}`
}
```

### 5. 更新表单验证规则
添加对新时间字段的验证：
```javascript
date: [
  { required: true, message: '请选择活动日期', trigger: 'change' }
],
startTime: [
  { required: true, message: '请选择开始时间', trigger: 'change' }
],
endTime: [
  { required: true, message: '请选择结束时间', trigger: 'change' }
],
```

## 修复的文件
- `src/frontend/src/components/charity/ActivityForm.vue`

## 修复效果
1. ✅ 编辑活动时，时间字段能正确显示后端数据
2. ✅ 时间字段分离为日期、开始时间、结束时间，更符合用户使用习惯
3. ✅ 数据提交时能正确组装为后端期望的格式
4. ✅ 表单验证正常工作

## 测试建议
1. 测试新建活动时时间字段的输入和保存
2. 测试编辑现有活动时时间字段的正确显示和修改
3. 测试时间字段的表单验证功能
4. 测试不同时间格式的兼容性

## 注意事项
- 确保后端API能正确处理分离的时间字段数据
- 注意时区处理，确保前后端时间一致性
- 考虑添加时间合理性验证（如结束时间应晚于开始时间）