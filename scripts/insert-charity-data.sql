-- 公益项目管理测试数据插入脚本
-- 创建时间: 2024-12-18
-- 维护人员: YXRobot开发团队
-- 说明: 该脚本用于插入公益项目管理系统的测试数据

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================
-- 1. 插入公益统计数据
-- ========================================
INSERT INTO `charity_stats` (
  `total_projects`, `active_projects`, `completed_projects`, 
  `total_beneficiaries`, `total_raised`, `total_donated`,
  `total_volunteers`, `active_volunteers`, `total_institutions`, 
  `cooperating_institutions`, `total_activities`, `this_month_activities`,
  `update_reason`, `updated_by`, `is_current`
) VALUES (
  156, 42, 89, 
  28650, 18500000.00, 15200000.00,
  285, 168, 342, 
  198, 456, 28,
  '系统初始化数据', '系统管理员', 1
) ON DUPLICATE KEY UPDATE 
  `total_projects` = VALUES(`total_projects`),
  `active_projects` = VALUES(`active_projects`),
  `completed_projects` = VALUES(`completed_projects`),
  `total_beneficiaries` = VALUES(`total_beneficiaries`),
  `total_raised` = VALUES(`total_raised`),
  `total_donated` = VALUES(`total_donated`),
  `total_volunteers` = VALUES(`total_volunteers`),
  `active_volunteers` = VALUES(`active_volunteers`),
  `total_institutions` = VALUES(`total_institutions`),
  `cooperating_institutions` = VALUES(`cooperating_institutions`),
  `total_activities` = VALUES(`total_activities`),
  `this_month_activities` = VALUES(`this_month_activities`),
  `updated_at` = CURRENT_TIMESTAMP;

-- ========================================
-- 2. 插入合作机构数据
-- ========================================
INSERT INTO `charity_institutions` (
  `name`, `type`, `location`, `address`, `contact_person`, `contact_phone`, 
  `email`, `student_count`, `cooperation_date`, `status`, `device_count`, 
  `last_visit_date`, `notes`, `created_by`
) VALUES 
-- 学校类机构
('北京市朝阳区希望小学', 'school', '北京市朝阳区', '北京市朝阳区建国路88号', '张明华', '010-85234567', 'zhangmh@hope-school.edu.cn', 450, '2023-03-15', 'active', 5, '2024-11-20', '重点合作学校，设备运行良好', 1),
('上海市浦东新区阳光小学', 'school', '上海市浦东新区', '上海市浦东新区世纪大道1000号', '李晓红', '021-58901234', 'lixh@sunshine-school.edu.cn', 380, '2023-05-20', 'active', 3, '2024-12-05', '新合作学校，需要更多设备支持', 1),
('广州市天河区育才中学', 'school', '广州市天河区', '广州市天河区体育西路200号', '王建国', '020-38765432', 'wangjg@yucai-middle.edu.cn', 1200, '2022-09-10', 'active', 8, '2024-10-15', '大型中学，合作项目较多', 1),
('深圳市南山区科技小学', 'school', '深圳市南山区', '深圳市南山区科技园南区18号', '陈美玲', '0755-26789012', 'chenml@tech-school.edu.cn', 520, '2023-01-08', 'active', 6, '2024-11-28', '科技特色学校，设备使用率高', 1),
('成都市武侯区实验小学', 'school', '成都市武侯区', '成都市武侯区人民南路三段45号', '刘志强', '028-85432109', 'liuzq@wuhou-exp.edu.cn', 680, '2023-07-12', 'active', 4, '2024-09-22', '实验学校，教学质量优秀', 1),

-- 孤儿院类机构
('北京市儿童福利院', 'orphanage', '北京市丰台区', '北京市丰台区南三环西路100号', '赵慧敏', '010-67890123', 'zhaohm@bjchildren.org', 150, '2022-12-01', 'active', 2, '2024-11-10', '市级福利院，儿童教育需求大', 1),
('上海市儿童关爱中心', 'orphanage', '上海市徐汇区', '上海市徐汇区漕溪北路300号', '孙丽娟', '021-64567890', 'sunlj@shcare.org', 95, '2023-02-18', 'active', 3, '2024-12-01', '专业儿童关爱机构', 1),
('广州市社会福利院', 'orphanage', '广州市白云区', '广州市白云区机场路500号', '马晓峰', '020-36789012', 'maxf@gzwelfare.org', 120, '2023-04-25', 'active', 2, '2024-10-08', '综合性福利院', 1),

-- 社区类机构
('朝阳区建国门社区服务中心', 'community', '北京市朝阳区', '北京市朝阳区建国门外大街甲8号', '田雨萍', '010-85678901', 'tianyp@jgm-community.org', 0, '2023-06-30', 'active', 1, '2024-11-15', '社区老年人服务较多', 1),
('浦东新区陆家嘴社区中心', 'community', '上海市浦东新区', '上海市浦东新区陆家嘴环路1000号', '周建华', '021-58234567', 'zhoujh@ljz-community.org', 0, '2023-08-15', 'active', 2, '2024-12-10', '金融区社区，居民素质较高', 1),
('天河区珠江新城社区', 'community', '广州市天河区', '广州市天河区珠江新城花城大道85号', '黄志明', '020-38901234', 'huangzm@zjxc-community.org', 0, '2023-09-20', 'active', 1, '2024-09-30', '高端社区，公益活动积极性高', 1),

-- 医院类机构
('北京儿童医院', 'hospital', '北京市西城区', '北京市西城区南礼士路56号', '李主任', '010-59616161', 'lzr@bch.com.cn', 0, '2023-10-12', 'active', 1, '2024-11-25', '儿童专科医院，康复设备需求', 1),
('上海市第一人民医院', 'hospital', '上海市虹口区', '上海市虹口区海宁路100号', '张副院长', '021-63240090', 'zfyz@firsthospital.sh.cn', 0, '2023-11-08', 'active', 2, '2024-10-20', '综合性医院，儿科合作项目', 1),

-- 图书馆类机构
('北京市朝阳区图书馆', 'library', '北京市朝阳区', '北京市朝阳区春秀路12号', '文馆长', '010-84469777', 'wgz@cylib.org.cn', 0, '2023-05-15', 'active', 3, '2024-12-02', '区级图书馆，儿童阅读推广', 1),
('上海少年儿童图书馆', 'library', '上海市长宁区', '上海市长宁区南京西路962号', '童馆长', '021-62751156', 'tgz@sscl.org.cn', 0, '2023-07-20', 'active', 4, '2024-11-18', '专业少儿图书馆，教育资源丰富', 1),

-- 暂停合作的机构
('深圳市宝安区某小学', 'school', '深圳市宝安区', '深圳市宝安区新安街道100号', '李校长', '0755-27890123', 'lxz@baoan-school.edu.cn', 300, '2022-06-01', 'inactive', 2, '2024-06-15', '因学校搬迁暂停合作', 1),
('广州市越秀区社区中心', 'community', '广州市越秀区', '广州市越秀区中山三路50号', '陈主任', '020-83456789', 'czr@yuexiu-community.org', 0, '2022-11-20', 'inactive', 1, '2024-03-10', '社区重组，暂停合作', 1);

-- ========================================
-- 3. 插入公益项目数据
-- ========================================
INSERT INTO `charity_projects` (
  `name`, `description`, `type`, `status`, `start_date`, `end_date`, 
  `target_amount`, `raised_amount`, `beneficiaries`, `location`, 
  `organizer`, `contact_person`, `contact_phone`, `images`, `tags`, `created_by`
) VALUES 
-- 教育类项目
('智慧教育进校园', '为偏远地区学校提供智能教育设备和技术支持，提升教学质量', 'education', 'active', '2024-01-15', '2024-12-31', 500000.00, 320000.00, 1500, '北京、上海、广州等地', 'YXRobot公益基金会', '张明华', '010-85234567', '["https://example.com/project1_1.jpg", "https://example.com/project1_2.jpg"]', '["教育", "科技", "智能设备"]', 1),
('乡村图书角建设', '在乡村学校建设图书角，提供优质图书资源', 'education', 'active', '2024-03-01', '2024-11-30', 200000.00, 150000.00, 800, '河南、安徽、江西等地', 'YXRobot公益基金会', '李晓红', '021-58901234', '["https://example.com/project2_1.jpg"]', '["教育", "阅读", "乡村"]', 1),
('特殊儿童康复训练', '为特殊需要儿童提供康复训练设备和专业指导', 'education', 'active', '2024-02-20', '2024-10-20', 300000.00, 180000.00, 200, '北京、上海、深圳', 'YXRobot公益基金会', '王建国', '020-38765432', '["https://example.com/project3_1.jpg", "https://example.com/project3_2.jpg"]', '["康复", "特殊儿童", "训练"]', 1),

-- 捐赠类项目
('温暖冬日行动', '为贫困地区儿童提供冬季保暖用品', 'donation', 'completed', '2023-11-01', '2024-02-29', 150000.00, 165000.00, 1200, '西藏、新疆、内蒙古', 'YXRobot公益基金会', '陈美玲', '0755-26789012', '["https://example.com/project4_1.jpg"]', '["捐赠", "保暖", "儿童"]', 1),
('爱心午餐计划', '为偏远山区学校提供营养午餐', 'donation', 'active', '2024-04-01', '2024-12-31', 400000.00, 280000.00, 2000, '贵州、云南、四川', 'YXRobot公益基金会', '刘志强', '028-85432109', '["https://example.com/project5_1.jpg", "https://example.com/project5_2.jpg"]', '["营养", "午餐", "山区"]', 1),
('医疗设备援助', '为基层医疗机构提供医疗设备', 'donation', 'active', '2024-05-15', '2024-12-15', 800000.00, 450000.00, 5000, '全国各地基层医院', 'YXRobot公益基金会', '赵慧敏', '010-67890123', '["https://example.com/project6_1.jpg"]', '["医疗", "设备", "基层"]', 1),

-- 志愿服务类项目
('关爱空巢老人', '组织志愿者定期探访空巢老人，提供生活帮助', 'volunteer', 'active', '2024-01-01', '2024-12-31', 50000.00, 35000.00, 500, '北京、上海、广州社区', 'YXRobot公益基金会', '孙丽娟', '021-64567890', '["https://example.com/project7_1.jpg"]', '["志愿服务", "老人", "关爱"]', 1),
('环保小卫士', '组织青少年参与环保活动，培养环保意识', 'volunteer', 'active', '2024-06-01', '2024-11-30', 80000.00, 60000.00, 1000, '全国各大城市', 'YXRobot公益基金会', '马晓峰', '020-36789012', '["https://example.com/project8_1.jpg", "https://example.com/project8_2.jpg"]', '["环保", "青少年", "教育"]', 1),

-- 培训类项目
('乡村教师培训', '为乡村教师提供现代化教学技能培训', 'training', 'active', '2024-07-01', '2024-12-31', 250000.00, 120000.00, 300, '河南、湖南、江西', 'YXRobot公益基金会', '田雨萍', '010-85678901', '["https://example.com/project9_1.jpg"]', '["培训", "教师", "乡村"]', 1),
('青少年科技创新营', '为青少年提供科技创新培训和实践机会', 'training', 'planning', '2025-01-15', '2025-06-30', 180000.00, 0.00, 200, '北京、深圳、杭州', 'YXRobot公益基金会', '周建华', '021-58234567', '["https://example.com/project10_1.jpg"]', '["科技", "创新", "青少年"]', 1),

-- 已完成项目
('春节送温暖', '春节期间为困难家庭送去慰问品和祝福', 'donation', 'completed', '2024-01-20', '2024-02-20', 100000.00, 105000.00, 800, '全国各地', 'YXRobot公益基金会', '黄志明', '020-38901234', '["https://example.com/project11_1.jpg"]', '["春节", "慰问", "困难家庭"]', 1),
('儿童节快乐行动', '六一儿童节为孤儿院儿童举办庆祝活动', 'volunteer', 'completed', '2024-05-25', '2024-06-05', 30000.00, 32000.00, 150, '北京、上海、广州', 'YXRobot公益基金会', '李主任', '010-59616161', '["https://example.com/project12_1.jpg"]', '["儿童节", "孤儿院", "庆祝"]', 1);

-- ========================================
-- 4. 插入公益活动数据
-- ========================================
INSERT INTO `charity_activities` (
  `project_id`, `title`, `description`, `type`, `date`, `location`, 
  `participants`, `organizer`, `status`, `budget`, `actual_cost`, 
  `photos`, `feedback`, `created_by`
) VALUES 
-- 探访类活动
(1, '北京希望小学设备安装', '为北京市朝阳区希望小学安装智能教育设备', 'visit', '2024-11-20', '北京市朝阳区希望小学', 8, 'YXRobot技术团队', 'completed', 5000.00, 4800.00, '["https://example.com/activity1_1.jpg", "https://example.com/activity1_2.jpg"]', '设备安装顺利，师生反响热烈', 1),
(1, '上海阳光小学设备调试', '对上海阳光小学的教育设备进行调试和培训', 'visit', '2024-12-05', '上海市浦东新区阳光小学', 6, 'YXRobot技术团队', 'completed', 3000.00, 2800.00, '["https://example.com/activity2_1.jpg"]', '教师培训效果良好，设备运行正常', 1),
(3, '特殊儿童康复设备演示', '为康复中心演示新型康复训练设备', 'visit', '2024-10-15', '北京儿童医院康复科', 12, 'YXRobot康复团队', 'completed', 2000.00, 1900.00, '["https://example.com/activity3_1.jpg"]', '医护人员对设备功能很满意', 1),

-- 培训类活动
(9, '乡村教师智能教学培训', '为河南地区乡村教师提供智能教学设备使用培训', 'training', '2024-09-22', '河南省郑州市培训中心', 45, 'YXRobot教育团队', 'completed', 15000.00, 14200.00, '["https://example.com/activity4_1.jpg", "https://example.com/activity4_2.jpg"]', '教师学习积极性很高，掌握了基本操作', 1),
(9, '湖南乡村教师进阶培训', '为湖南地区乡村教师提供进阶教学技能培训', 'training', '2024-11-10', '湖南省长沙市教育学院', 38, 'YXRobot教育团队', 'completed', 12000.00, 11500.00, '["https://example.com/activity5_1.jpg"]', '培训内容实用，教师反馈很好', 1),
(10, '青少年编程启蒙课', '为中学生提供编程基础知识培训', 'training', '2024-12-15', '深圳市南山区科技小学', 60, 'YXRobot科技团队', 'planned', 8000.00, 0.00, '[]', '', 1),

-- 捐赠类活动
(4, '西藏地区冬衣发放', '为西藏偏远地区儿童发放冬季保暖衣物', 'donation', '2024-01-15', '西藏拉萨市及周边地区', 25, 'YXRobot志愿者团队', 'completed', 25000.00, 24500.00, '["https://example.com/activity6_1.jpg", "https://example.com/activity6_2.jpg"]', '孩子们收到衣物非常开心，家长很感谢', 1),
(5, '贵州山区营养午餐配送', '为贵州山区学校配送营养午餐食材', 'donation', '2024-11-28', '贵州省黔东南州各乡村学校', 15, 'YXRobot物流团队', 'completed', 18000.00, 17200.00, '["https://example.com/activity7_1.jpg"]', '食材新鲜，学校很满意配送服务', 1),
(6, '基层医院设备捐赠仪式', '向县级医院捐赠医疗康复设备', 'donation', '2024-10-08', '安徽省六安市人民医院', 20, 'YXRobot公益团队', 'completed', 10000.00, 9500.00, '["https://example.com/activity8_1.jpg"]', '医院领导和医护人员表示感谢', 1),

-- 活动类
(7, '社区老人智能设备体验', '为社区老人介绍和体验智能健康设备', 'event', '2024-11-15', '北京朝阳区建国门社区', 35, 'YXRobot志愿者团队', 'completed', 3000.00, 2800.00, '["https://example.com/activity9_1.jpg"]', '老人们对智能设备很感兴趣', 1),
(8, '青少年环保知识竞赛', '组织青少年参加环保知识竞赛活动', 'event', '2024-09-30', '广州市天河区珠江新城社区', 80, 'YXRobot环保团队', 'completed', 5000.00, 4600.00, '["https://example.com/activity10_1.jpg", "https://example.com/activity10_2.jpg"]', '活动气氛热烈，青少年环保意识增强', 1),
(11, '春节慰问困难家庭', '春节期间走访慰问社区困难家庭', 'event', '2024-02-08', '全国各地社区', 120, 'YXRobot志愿者团队', 'completed', 15000.00, 14800.00, '["https://example.com/activity11_1.jpg"]', '困难家庭很感动，社会反响良好', 1),
(12, '六一儿童节联欢会', '为孤儿院儿童举办六一儿童节联欢会', 'event', '2024-06-01', '北京市儿童福利院', 45, 'YXRobot志愿者团队', 'completed', 8000.00, 7500.00, '["https://example.com/activity12_1.jpg", "https://example.com/activity12_2.jpg"]', '孩子们度过了快乐的儿童节', 1),

-- 计划中的活动
(2, '乡村图书角揭牌仪式', '为新建成的乡村图书角举行揭牌仪式', 'event', '2024-12-25', '河南省信阳市某乡村小学', 30, 'YXRobot教育团队', 'planned', 6000.00, 0.00, '[]', '', 1),
(5, '云南山区学校回访', '回访云南山区学校，了解午餐计划执行情况', 'visit', '2024-12-30', '云南省昆明市及周边山区', 10, 'YXRobot项目团队', 'planned', 8000.00, 0.00, '[]', '', 1),
(7, '新年关爱老人活动', '新年期间为空巢老人送去关怀和祝福', 'event', '2025-01-01', '上海市各社区', 50, 'YXRobot志愿者团队', 'planned', 10000.00, 0.00, '[]', '', 1);

-- ========================================
-- 5. 插入统计数据更新日志
-- ========================================
INSERT INTO `charity_stats_logs` (
  `stats_id`, `operation_type`, `field_changes`, `update_reason`, 
  `operator_id`, `operator_name`, `ip_address`, `user_agent`
) VALUES 
(1, 'CREATE', '{"action": "initial_create", "all_fields": "created"}', '系统初始化创建统计数据', 1, '系统管理员', '127.0.0.1', 'System/1.0'),
(1, 'UPDATE', '{"total_beneficiaries": {"old": 28500, "new": 28650}, "this_month_activities": {"old": 25, "new": 28}}', '月度数据统计更新', 1, '数据管理员', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
(1, 'UPDATE', '{"total_raised": {"old": 18200000.00, "new": 18500000.00}, "total_donated": {"old": 15000000.00, "new": 15200000.00}}', '资金数据月度更新', 1, '财务管理员', '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'),
(1, 'UPDATE', '{"active_projects": {"old": 40, "new": 42}, "completed_projects": {"old": 87, "new": 89}}', '项目状态数据更新', 1, '项目管理员', '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36');

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示插入结果统计
SELECT '数据插入完成' as status;
SELECT 
  (SELECT COUNT(*) FROM charity_stats) as '统计数据条数',
  (SELECT COUNT(*) FROM charity_institutions) as '合作机构数量',
  (SELECT COUNT(*) FROM charity_projects) as '公益项目数量',
  (SELECT COUNT(*) FROM charity_activities) as '公益活动数量',
  (SELECT COUNT(*) FROM charity_stats_logs) as '日志记录数量';

-- 显示各类型机构分布
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

-- 显示各状态项目分布
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