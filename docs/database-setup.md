# YXRobot 数据库设置指南

## 概述

本文档描述了如何设置 YXRobot 产品管理系统的数据库环境。

## 前置要求

- MySQL 8.0 或更高版本
- 具有创建数据库权限的 MySQL 用户

## 数据库初始化步骤

### 1. 创建数据库

运行数据库初始化脚本：

```bash
mysql -u root -p < scripts/init-database.sql
```

或者手动执行以下 SQL：

```sql
CREATE DATABASE IF NOT EXISTS `yxrobot` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;
```

### 2. 配置数据库连接

编辑 `src/main/resources/application.yml` 文件，更新数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://yun.finiot.cn:3306/YXRobot?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: YXRobot
    password: 2200548qq
```

### 3. 数据表创建

应用启动时，Flyway 会自动执行数据库迁移脚本：

- `V001__create_products_tables.sql` - 创建产品相关数据表
- `V002__insert_test_products_data.sql` - 插入测试数据

## 数据表结构

### products 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 产品ID，主键 |
| name | VARCHAR(200) | 产品名称 |
| model | VARCHAR(100) | 产品型号 |
| description | TEXT | 产品描述 |
| price | DECIMAL(10,2) | 销售价格 |
| cover_image_url | VARCHAR(500) | 封面图片URL |
| status | VARCHAR(20) | 产品状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| is_deleted | TINYINT | 是否已删除 |

### product_media 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 媒体ID，主键 |
| product_id | BIGINT | 产品ID，外键 |
| media_type | VARCHAR(20) | 媒体类型 |
| media_url | VARCHAR(500) | 媒体文件URL |
| sort_order | INT | 排序权重 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 测试数据

系统会自动插入以下测试产品：

1. 家用练字机器人 (YX-HOME-001) - 已发布
2. 商用练字机器人 (YX-BUSINESS-001) - 已发布  
3. 教育版练字机器人 (YX-EDU-001) - 草稿
4. 便携式练字机器人 (YX-PORTABLE-001) - 已发布
5. 专业版练字机器人 (YX-PRO-001) - 已归档

## 索引优化

系统创建了以下索引以优化查询性能：

- `idx_name` - 产品名称索引
- `idx_model` - 产品型号索引
- `idx_status` - 产品状态索引
- `idx_price` - 价格索引
- `idx_products_search` - 复合搜索索引
- `idx_product_media_product_type` - 媒体查询索引

## 故障排除

### 连接问题

如果遇到数据库连接问题，请检查：

1. MySQL 服务是否正在运行
2. 数据库连接参数是否正确
3. 用户权限是否足够
4. 防火墙设置是否允许连接

### 字符编码问题

确保数据库和表都使用 `utf8mb4` 字符集：

```sql
ALTER DATABASE yxrobot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 时区问题

如果遇到时区相关错误，请在连接字符串中添加：

```
serverTimezone=Asia/Shanghai
```

## 维护建议

1. 定期备份数据库
2. 监控数据库性能
3. 定期清理日志文件
4. 更新统计信息以优化查询计划

## 联系支持

如有问题，请联系开发团队或查看项目文档。