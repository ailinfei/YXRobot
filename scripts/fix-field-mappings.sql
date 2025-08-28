-- 修复数据库字段映射不一致问题
-- 主要解决实体类字段名与数据库表字段名不匹配的问题

-- 1. 检查并修复charity_stats表字段映射问题
-- 实体类使用createTime/updateTime，但数据库表使用created_at/updated_at
-- 实体类使用deleted，但数据库表使用is_deleted

-- 检查当前charity_stats表结构
DESCRIBE charity_stats;

-- 2. 检查并修复charity_institutions表字段映射问题
-- 实体类使用createTime/updateTime/createBy/updateBy/deleted
-- 但数据库表使用created_at/updated_at/created_by/updated_by/is_deleted

-- 检查当前charity_institutions表结构
DESCRIBE charity_institutions;

-- 3. 为了保持一致性，我们需要确保mapper文件中的字段映射正确
-- 以下是需要在mapper中正确映射的字段：

-- charity_stats表字段映射：
-- created_at -> createTime
-- updated_at -> updateTime  
-- is_deleted -> deleted

-- charity_institutions表字段映射：
-- created_at -> createTime
-- updated_at -> updateTime
-- created_by -> createBy
-- updated_by -> updateBy
-- is_deleted -> deleted

-- 4. 验证所有必要的索引是否存在
-- 检查charity_stats表索引
SHOW INDEX FROM charity_stats;

-- 检查charity_institutions表索引
SHOW INDEX FROM charity_institutions;

-- 检查charity_activities表索引
SHOW INDEX FROM charity_activities;

-- 检查charity_projects表索引
SHOW INDEX FROM charity_projects;

-- 检查charity_stats_logs表索引
SHOW INDEX FROM charity_stats_logs;

-- 5. 如果缺少必要的索引，添加它们
-- 为charity_stats表添加性能索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_charity_stats_is_current ON charity_stats(is_current);
CREATE INDEX IF NOT EXISTS idx_charity_stats_created_at ON charity_stats(created_at);
CREATE INDEX IF NOT EXISTS idx_charity_stats_is_deleted ON charity_stats(is_deleted);

-- 为charity_institutions表添加性能索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_charity_institutions_type ON charity_institutions(type);
CREATE INDEX IF NOT EXISTS idx_charity_institutions_status ON charity_institutions(status);
CREATE INDEX IF NOT EXISTS idx_charity_institutions_location ON charity_institutions(location);
CREATE INDEX IF NOT EXISTS idx_charity_institutions_cooperation_date ON charity_institutions(cooperation_date);
CREATE INDEX IF NOT EXISTS idx_charity_institutions_is_deleted ON charity_institutions(is_deleted);

-- 为charity_activities表添加性能索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_charity_activities_project_id ON charity_activities(project_id);
CREATE INDEX IF NOT EXISTS idx_charity_activities_type ON charity_activities(type);
CREATE INDEX IF NOT EXISTS idx_charity_activities_status ON charity_activities(status);
CREATE INDEX IF NOT EXISTS idx_charity_activities_date ON charity_activities(date);
CREATE INDEX IF NOT EXISTS idx_charity_activities_location ON charity_activities(location);
CREATE INDEX IF NOT EXISTS idx_charity_activities_is_deleted ON charity_activities(is_deleted);

-- 为charity_projects表添加性能索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_charity_projects_type ON charity_projects(type);
CREATE INDEX IF NOT EXISTS idx_charity_projects_status ON charity_projects(status);
CREATE INDEX IF NOT EXISTS idx_charity_projects_start_date ON charity_projects(start_date);
CREATE INDEX IF NOT EXISTS idx_charity_projects_location ON charity_projects(location);
CREATE INDEX IF NOT EXISTS idx_charity_projects_organizer ON charity_projects(organizer);
CREATE INDEX IF NOT EXISTS idx_charity_projects_is_deleted ON charity_projects(is_deleted);

-- 为charity_stats_logs表添加性能索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_charity_stats_logs_stats_id ON charity_stats_logs(stats_id);
CREATE INDEX IF NOT EXISTS idx_charity_stats_logs_operation_type ON charity_stats_logs(operation_type);
CREATE INDEX IF NOT EXISTS idx_charity_stats_logs_operator_id ON charity_stats_logs(operator_id);
CREATE INDEX IF NOT EXISTS idx_charity_stats_logs_created_at ON charity_stats_logs(created_at);

-- 6. 验证数据完整性约束
-- 检查是否有无效的状态值
SELECT DISTINCT status FROM charity_institutions WHERE status NOT IN ('active', 'inactive', 'suspended');
SELECT DISTINCT type FROM charity_institutions WHERE type NOT IN ('school', 'hospital', 'community', 'ngo', 'other');
SELECT DISTINCT status FROM charity_activities WHERE status NOT IN ('planned', 'ongoing', 'completed', 'cancelled');
SELECT DISTINCT type FROM charity_activities WHERE type NOT IN ('visit', 'training', 'donation', 'event');
SELECT DISTINCT status FROM charity_projects WHERE status NOT IN ('planning', 'active', 'completed', 'suspended');
SELECT DISTINCT type FROM charity_projects WHERE type NOT IN ('education', 'donation', 'volunteer', 'training');

-- 7. 检查数据关联完整性
-- 检查charity_activities表中是否有无效的project_id引用
SELECT ca.id, ca.project_id 
FROM charity_activities ca 
LEFT JOIN charity_projects cp ON ca.project_id = cp.id 
WHERE ca.project_id IS NOT NULL AND cp.id IS NULL;

-- 检查charity_stats_logs表中是否有无效的stats_id引用
SELECT csl.id, csl.stats_id 
FROM charity_stats_logs csl 
LEFT JOIN charity_stats cs ON csl.stats_id = cs.id 
WHERE csl.stats_id IS NOT NULL AND cs.id IS NULL;