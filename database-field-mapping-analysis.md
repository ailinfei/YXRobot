# 数据库字段映射问题分析报告

## 问题概述

通过对数据库表结构和MyBatis映射器的分析，发现了多个字段映射不一致的问题，这些问题导致SQL查询失败，特别是"Unknown column 'created_at' in 'field list'"错误。

## 详细分析

### 1. charity_activities表

**数据库实际字段：**
- `create_time` (datetime) - 创建时间
- `update_time` (datetime) - 更新时间
- `deleted` (tinyint) - 软删除标记

**映射器期望字段：**
- `created_at` - 映射到 `createTime` 属性
- `updated_at` - 映射到 `updateTime` 属性
- `is_deleted` - 映射到 `deleted` 属性

**问题：**
- ❌ 映射器查询使用 `created_at`，但数据库字段是 `create_time`
- ❌ 映射器查询使用 `updated_at`，但数据库字段是 `update_time`
- ❌ 映射器查询使用 `is_deleted`，但数据库字段是 `deleted`

### 2. charity_projects表

**数据库实际字段：**
- `created_at` (datetime) - 创建时间
- `updated_at` (datetime) - 更新时间
- `is_deleted` (tinyint) - 软删除标记

**映射器期望字段：**
- `created_at` - 映射到 `createTime` 属性
- `updated_at` - 映射到 `updateTime` 属性
- `is_deleted` - 映射到 `deleted` 属性

**问题：**
- ✅ 时间戳字段命名一致
- ✅ 软删除字段命名一致

### 3. charity_institutions表

**数据库实际字段：**
- `created_at` (datetime) - 创建时间
- `updated_at` (datetime) - 更新时间
- `is_deleted` (tinyint) - 软删除标记

**映射器期望字段：**
- `created_at` - 映射到 `createTime` 属性（通过别名）
- `updated_at` - 映射到 `updateTime` 属性（通过别名）
- `is_deleted` - 映射到 `deleted` 属性（通过别名）

**问题：**
- ✅ 时间戳字段命名一致
- ✅ 软删除字段命名一致

### 4. charity_stats表

**数据库实际字段：**
- `created_at` (datetime) - 创建时间
- `updated_at` (datetime) - 更新时间
- `deleted` (tinyint) - 软删除标记

**映射器期望字段：**
- `created_at` - 通过别名映射到 `createTime` 属性
- `updated_at` - 通过别名映射到 `updateTime` 属性
- `deleted` - 映射到 `deleted` 属性

**问题：**
- ✅ 时间戳字段通过别名正确映射
- ✅ 软删除字段命名一致

## 字段命名不一致性总结

### 时间戳字段命名模式：
1. **charity_activities**: `create_time` / `update_time`
2. **charity_projects**: `created_at` / `updated_at`
3. **charity_institutions**: `created_at` / `updated_at`
4. **charity_stats**: `created_at` / `updated_at`

### 软删除字段命名模式：
1. **charity_activities**: `deleted`
2. **charity_projects**: `is_deleted`
3. **charity_institutions**: `is_deleted`
4. **charity_stats**: `deleted`

## 根本原因分析

1. **数据库设计不一致**：不同表使用了不同的字段命名约定
2. **映射器配置错误**：CharityActivityMapper.xml使用了错误的字段名
3. **缺乏统一的命名规范**：项目中没有统一的数据库字段命名标准

## 影响范围

### 受影响的功能：
1. 公益活动列表查询 - 无法加载数据
2. 公益活动详情查询 - 返回空结果
3. 公益活动创建和更新 - 时间戳字段无法正确设置
4. 公益活动软删除 - 删除标记字段映射错误

### 错误日志示例：
```
java.sql.SQLSyntaxErrorException: Unknown column 'created_at' in 'field list'
java.sql.SQLSyntaxErrorException: Unknown column 'updated_at' in 'field list'
java.sql.SQLSyntaxErrorException: Unknown column 'is_deleted' in 'field list'
```

## 解决方案建议

### 方案A：修改数据库表结构（不推荐）
- 将 `charity_activities` 表的字段改为 `created_at` / `updated_at` / `is_deleted`
- 风险：需要数据迁移，可能影响现有数据

### 方案B：修改映射器配置（推荐）
- 更新 `CharityActivityMapper.xml` 使用正确的数据库字段名
- 风险：较低，只需修改配置文件

### 方案C：统一所有表的字段命名（长期方案）
- 制定统一的字段命名规范
- 逐步迁移所有表到统一命名
- 风险：工作量大，需要全面测试

## 推荐实施步骤

1. **立即修复**：更新 CharityActivityMapper.xml 中的字段映射
2. **验证修复**：运行测试确保查询正常工作
3. **制定规范**：建立统一的数据库字段命名规范
4. **长期规划**：逐步统一所有表的字段命名

## 需要修复的具体文件

1. `CharityActivityMapper.xml` - 更新结果映射和SQL查询
2. 可能需要更新的实体类属性映射
3. 相关的单元测试和集成测试

## 验证方法

1. 运行数据库架构验证工具
2. 执行公益活动相关的API测试
3. 检查应用日志确认无SQL错误
4. 验证前端页面能正常显示数据