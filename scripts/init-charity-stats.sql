-- 初始化公益统计数据
-- 执行时间: 2025-08-20

USE YXRobot;

-- 插入初始统计数据到 charity_stats 表
INSERT INTO `charity_stats` (
    `total_projects`, `active_projects`, `completed_projects`, 
    `total_beneficiaries`, `total_raised`, `total_donated`,
    `total_volunteers`, `active_volunteers`, `total_institutions`, 
    `cooperating_institutions`, `total_activities`, `this_month_activities`,
    `update_reason`, `updated_by`, `is_current`,
    `created_at`, `updated_at`
) VALUES (
    156, 42, 89, 
    28650, 18500000.00, 15200000.00,
    285, 168, 342, 
    198, 456, 28,
    '系统初始化数据', '系统管理员', 1,
    NOW(), NOW()
) ON DUPLICATE KEY UPDATE 
    `updated_at` = NOW();

-- 验证数据插入
SELECT COUNT(*) as total_stats FROM charity_stats WHERE is_current = 1;
SELECT * FROM charity_stats WHERE is_current = 1 ORDER BY created_at DESC LIMIT 1;