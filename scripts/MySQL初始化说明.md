# MySQL数据库初始化说明

## 使用指定MySQL路径初始化公益统计数据

### 方法一：使用批处理脚本

1. 确保MySQL服务已启动
2. 在命令行中执行：
```bash
cd E:\YXRobot\workspace\projects\YXRobot\scripts
mysql-init-charity.bat
```

### 方法二：直接使用MySQL命令行

1. 打开命令行，进入MySQL bin目录：
```bash
cd E:\YXRobot\mysql-9.3.0-winx64\bin
```

2. 连接到MySQL：
```bash
mysql.exe -hlocalhost -P3306 -uroot -p
```

3. 输入root密码后，执行以下命令：
```sql
USE yxrobot;
source E:\YXRobot\workspace\projects\YXRobot\scripts\simple-charity-init.sql
```

### 方法三：逐步执行SQL命令

1. 连接到MySQL后，复制并执行 `charity-init-commands.sql` 文件中的SQL命令

### 验证初始化结果

执行以下SQL验证数据是否正确插入：

```sql
USE yxrobot;

-- 检查charity_stats表
SELECT * FROM charity_stats WHERE deleted = 0;

-- 检查其他表的数据量
SELECT 
  (SELECT COUNT(*) FROM charity_institutions WHERE deleted = 0) as institutions_count,
  (SELECT COUNT(*) FROM charity_projects WHERE deleted = 0) as projects_count,
  (SELECT COUNT(*) FROM charity_activities WHERE deleted = 0) as activities_count;
```

### 预期结果

初始化成功后，应该看到：
- charity_stats表有1条记录，包含测试统计数据
- charity_institutions表有3条机构记录
- charity_projects表有2条项目记录  
- charity_activities表有2条活动记录

### 故障排除

1. **连接失败**：检查MySQL服务是否启动
2. **权限错误**：确认使用的用户有足够权限
3. **数据库不存在**：先创建yxrobot数据库：`CREATE DATABASE yxrobot;`
4. **字符编码问题**：确保MySQL配置支持utf8mb4字符集