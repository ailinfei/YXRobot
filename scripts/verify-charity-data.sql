-- 公益项目管理系统数据验证脚本
-- 用于验证数据是否正确插入到数据库中

-- 设置字符集
SET NAMES utf8mb4;

-- 显示验证开始信息
SELECT '=== 公益项目管理系统数据验证 ===' as '验证开始';
SELECT NOW() as '验证时间';

-- 1. 验证表是否存在
SELECT '1. 验证表结构' as '验证项目';
SELECT 
    TABLE_NAME as '表名',
    TABLE_COMMENT as '表说明',
    TABLE_ROWS as '预估行数'
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME LIKE 'charity_%'
ORDER BY TABLE_NAME;

-- 2. 验证数据数量
SELECT '2. 验证数据数量' as '验证项目';
SELECT 
    '统计数据' as '数据类型',
    COUNT(*) as '记录数',
    '应为1条' as '预期'
FROM charity_stats
UNION ALL
SELECT 
    '合作机构' as '数据类型',
    COUNT(*) as '记录数',
    '应为17个' as '预期'
FROM charity_institutions
UNION ALL
SELECT 
    '公益项目' as '数据类型',
    COUNT(*) as '记录数',
    '应为12个' as '预期'
FROM charity_projects
UNION ALL
SELECT 
    '公益活动' as '数据类型',
    COUNT(*) as '记录数',
    '应为16个' as '预期'
FROM charity_activities
UNION ALL
SELECT 
    '更新日志' as '数据类型',
    COUNT(*) as '记录数',
    '应为4条' as '预期'
FROM charity_stats_logs;

-- 3. 验证统计数据
SELECT '3. 验证统计数据内容' as '验证项目';
SELECT 
    total_projects as '项目总数',
    active_projects as '进行中项目',
    completed_projects as '已完成项目',
    total_beneficiaries as '累计受益人数',
    total_raised as '累计筹集金额',
    total_donated as '累计捐赠金额',
    total_volunteers as '志愿者总数',
    total_institutions as '合作机构总数',
    cooperating_institutions as '活跃机构数',
    total_activities as '活动总数',
    this_month_activities as '本月活动数'
FROM charity_stats 
WHERE is_current = 1;

-- 4. 验证机构类型分布
SELECT '4. 验证机构类型分布' as '验证项目';
SELECT 
    type as '机构类型',
    COUNT(*) as '数量',
    CASE type
        WHEN 'school' THEN '学校'
        WHEN 'orphanage' THEN '孤儿院'
        WHEN 'community' THEN '社区'
        WHEN 'hospital' THEN '医院'
        WHEN 'library' THEN '图书馆'
        ELSE '其他'
    END as '类型说明'
FROM charity_institutions 
WHERE is_deleted = 0
GROUP BY type
ORDER BY COUNT(*) DESC;

-- 5. 验证机构状态分布
SELECT '5. 验证机构状态分布' as '验证项目';
SELECT 
    status as '机构状态',
    COUNT(*) as '数量',
    CASE status
        WHEN 'active' THEN '活跃'
        WHEN 'inactive' THEN '不活跃'
        WHEN 'pending' THEN '待确认'
        ELSE '其他'
    END as '状态说明'
FROM charity_institutions 
WHERE is_deleted = 0
GROUP BY status
ORDER BY COUNT(*) DESC;

-- 6. 验证项目类型分布
SELECT '6. 验证项目类型分布' as '验证项目';
SELECT 
    type as '项目类型',
    COUNT(*) as '数量',
    CASE type
        WHEN 'education' THEN '教育'
        WHEN 'donation' THEN '捐赠'
        WHEN 'volunteer' THEN '志愿服务'
        WHEN 'training' THEN '培训'
        ELSE '其他'
    END as '类型说明'
FROM charity_projects 
WHERE is_deleted = 0
GROUP BY type
ORDER BY COUNT(*) DESC;

-- 7. 验证项目状态分布
SELECT '7. 验证项目状态分布' as '验证项目';
SELECT 
    status as '项目状态',
    COUNT(*) as '数量',
    CASE status
        WHEN 'planning' THEN '规划中'
        WHEN 'active' THEN '进行中'
        WHEN 'completed' THEN '已完成'
        WHEN 'suspended' THEN '暂停'
        ELSE '其他'
    END as '状态说明'
FROM charity_projects 
WHERE is_deleted = 0
GROUP BY status
ORDER BY COUNT(*) DESC;

-- 8. 验证活动类型分布
SELECT '8. 验证活动类型分布' as '验证项目';
SELECT 
    type as '活动类型',
    COUNT(*) as '数量',
    CASE type
        WHEN 'visit' THEN '探访'
        WHEN 'training' THEN '培训'
        WHEN 'donation' THEN '捐赠'
        WHEN 'event' THEN '活动'
        ELSE '其他'
    END as '类型说明'
FROM charity_activities 
WHERE is_deleted = 0
GROUP BY type
ORDER BY COUNT(*) DESC;

-- 9. 验证活动状态分布
SELECT '9. 验证活动状态分布' as '验证项目';
SELECT 
    status as '活动状态',
    COUNT(*) as '数量',
    CASE status
        WHEN 'planned' THEN '计划中'
        WHEN 'ongoing' THEN '进行中'
        WHEN 'completed' THEN '已完成'
        WHEN 'cancelled' THEN '已取消'
        ELSE '其他'
    END as '状态说明'
FROM charity_activities 
WHERE is_deleted = 0
GROUP BY status
ORDER BY COUNT(*) DESC;

-- 10. 验证数据关联性
SELECT '10. 验证数据关联性' as '验证项目';
SELECT 
    '项目-活动关联' as '关联类型',
    COUNT(DISTINCT ca.project_id) as '关联项目数',
    COUNT(*) as '关联活动数'
FROM charity_activities ca
WHERE ca.project_id IS NOT NULL 
AND ca.is_deleted = 0;

-- 11. 验证日期数据
SELECT '11. 验证日期数据' as '验证项目';
SELECT 
    '最早合作日期' as '日期类型',
    MIN(cooperation_date) as '日期值'
FROM charity_institutions
WHERE is_deleted = 0
UNION ALL
SELECT 
    '最晚合作日期' as '日期类型',
    MAX(cooperation_date) as '日期值'
FROM charity_institutions
WHERE is_deleted = 0
UNION ALL
SELECT 
    '最早项目开始日期' as '日期类型',
    MIN(start_date) as '日期值'
FROM charity_projects
WHERE is_deleted = 0
UNION ALL
SELECT 
    '最晚项目开始日期' as '日期类型',
    MAX(start_date) as '日期值'
FROM charity_projects
WHERE is_deleted = 0
UNION ALL
SELECT 
    '最早活动日期' as '日期类型',
    MIN(date) as '日期值'
FROM charity_activities
WHERE is_deleted = 0
UNION ALL
SELECT 
    '最晚活动日期' as '日期类型',
    MAX(date) as '日期值'
FROM charity_activities
WHERE is_deleted = 0;

-- 12. 验证金额数据
SELECT '12. 验证金额数据' as '验证项目';
SELECT 
    '项目总预算' as '金额类型',
    CONCAT(FORMAT(SUM(target_amount), 2), ' 元') as '金额'
FROM charity_projects
WHERE is_deleted = 0
UNION ALL
SELECT 
    '项目已筹集' as '金额类型',
    CONCAT(FORMAT(SUM(raised_amount), 2), ' 元') as '金额'
FROM charity_projects
WHERE is_deleted = 0
UNION ALL
SELECT 
    '活动总预算' as '金额类型',
    CONCAT(FORMAT(SUM(budget), 2), ' 元') as '金额'
FROM charity_activities
WHERE is_deleted = 0
UNION ALL
SELECT 
    '活动实际费用' as '金额类型',
    CONCAT(FORMAT(SUM(actual_cost), 2), ' 元') as '金额'
FROM charity_activities
WHERE is_deleted = 0;

-- 13. 验证地区分布
SELECT '13. 验证地区分布' as '验证项目';
SELECT 
    location as '地区',
    COUNT(*) as '机构数量'
FROM charity_institutions
WHERE is_deleted = 0
GROUP BY location
ORDER BY COUNT(*) DESC
LIMIT 10;

-- 验证完成
SELECT '=== 数据验证完成 ===' as '验证结束';
SELECT 
    CASE 
        WHEN (SELECT COUNT(*) FROM charity_stats) >= 1
        AND (SELECT COUNT(*) FROM charity_institutions) >= 15
        AND (SELECT COUNT(*) FROM charity_projects) >= 10
        AND (SELECT COUNT(*) FROM charity_activities) >= 15
        AND (SELECT COUNT(*) FROM charity_stats_logs) >= 3
        THEN '✓ 数据验证通过'
        ELSE '✗ 数据验证失败，请检查数据插入'
    END as '验证结果';

-- 显示建议的下一步操作
SELECT '建议的下一步操作：' as '提示';
SELECT '1. 启动Spring Boot应用程序' as '步骤';
SELECT '2. 访问公益项目管理页面' as '步骤';
SELECT '3. 测试各项功能是否正常' as '步骤';
SELECT '4. 检查前后端数据交互' as '步骤';