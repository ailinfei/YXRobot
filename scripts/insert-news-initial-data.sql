-- 新闻管理系统初始数据脚本
-- 
-- @author YXRobot开发团队
-- @version 1.0.0
-- @since 2025-01-25

-- 插入新闻分类数据
INSERT INTO news_categories (id, name, description, sort_order, is_enabled, created_at, updated_at) VALUES
(1, '产品动态', '练字机器人产品相关的最新动态和更新', 1, true, NOW(), NOW()),
(2, '公司新闻', '公司发展、合作、活动等相关新闻', 2, true, NOW(), NOW()),
(3, '行业资讯', '教育科技行业的最新资讯和趋势', 3, true, NOW(), NOW()),
(4, '技术分享', '技术开发、创新应用等技术相关内容', 4, true, NOW(), NOW()),
(5, '用户故事', '用户使用体验、成功案例等故事分享', 5, true, NOW(), NOW()),
(6, '公益活动', '公益项目、社会责任等相关活动', 6, true, NOW(), NOW());

-- 插入新闻标签数据
INSERT INTO news_tags (id, name, color, created_at, updated_at) VALUES
(1, '产品发布', '#409EFF', NOW(), NOW()),
(2, '功能更新', '#67C23A', NOW(), NOW()),
(3, '合作伙伴', '#E6A23C', NOW(), NOW()),
(4, '教育科技', '#F56C6C', NOW(), NOW()),
(5, 'AI技术', '#909399', NOW(), NOW()),
(6, '用户体验', '#C71585', NOW(), NOW()),
(7, '社会责任', '#FF6347', NOW(), NOW()),
(8, '行业趋势', '#32CD32', NOW(), NOW()),
(9, '创新应用', '#1E90FF', NOW(), NOW()),
(10, '成功案例', '#FF69B4', NOW(), NOW()),
(11, '技术突破', '#8A2BE2', NOW(), NOW()),
(12, '市场拓展', '#00CED1', NOW(), NOW());

-- 插入示例新闻数据
INSERT INTO news (id, title, excerpt, content, category_id, author, status, views, comments, likes, is_featured, cover_image, publish_time, sort_order, created_at, updated_at) VALUES
(1, 
 '练字机器人3.0正式发布：AI技术助力汉字学习新突破', 
 '经过两年的技术研发和产品迭代，练字机器人3.0版本正式发布。新版本采用最新的AI技术，在字体识别、笔画纠正、学习路径规划等方面实现了重大突破。',
 '<h2>产品亮点</h2><p>练字机器人3.0版本在以下几个方面实现了重大突破：</p><ul><li><strong>智能字体识别</strong>：采用深度学习算法，识别准确率提升至99.5%</li><li><strong>实时笔画纠正</strong>：毫秒级响应，实时指导用户正确书写</li><li><strong>个性化学习</strong>：根据用户水平制定专属学习计划</li><li><strong>多字体支持</strong>：支持楷书、行书、隶书等多种字体</li></ul><h2>技术创新</h2><p>本次更新采用了最新的计算机视觉和自然语言处理技术，结合传统书法教学理念，为用户提供更加智能化的学习体验。</p><h2>用户反馈</h2><p>在内测阶段，超过1000名用户参与了产品测试，用户满意度达到98%，学习效率平均提升40%。</p>',
 1, '产品团队', 'PUBLISHED', 1250, 15, 89, true, '/images/news/robot-3.0-release.jpg', '2025-01-20 10:00:00', 100, NOW(), NOW()),

(2,
 '携手清华大学：共建智能书法教育实验室',
 '我们很荣幸地宣布与清华大学计算机系达成战略合作，共同建立智能书法教育实验室，致力于推动传统书法与现代科技的深度融合。',
 '<h2>合作背景</h2><p>随着人工智能技术的快速发展，传统书法教育面临着新的机遇和挑战。此次合作旨在结合清华大学在人工智能领域的学术优势和我们在书法教育产品方面的实践经验。</p><h2>合作内容</h2><ul><li>共同研发下一代智能书法教学系统</li><li>建立书法大数据分析平台</li><li>培养书法教育与AI结合的复合型人才</li><li>推动相关技术标准的制定</li></ul><h2>预期成果</h2><p>预计在未来三年内，实验室将产出多项重要研究成果，包括新的算法模型、教学方法和产品原型。</p>',
 2, '市场部', 'PUBLISHED', 890, 8, 56, true, '/images/news/tsinghua-cooperation.jpg', '2025-01-18 14:30:00', 95, NOW(), NOW()),

(3,
 '2024年度用户满意度调查报告发布',
 '我们完成了2024年度用户满意度调查，收集了来自全国各地超过10000名用户的反馈。调查结果显示，用户整体满意度达到96.8%，创历史新高。',
 '<h2>调查概况</h2><p>本次调查历时3个月，覆盖了全国31个省市自治区，参与用户包括学生、教师、家长等不同群体。</p><h2>主要发现</h2><ul><li><strong>产品满意度</strong>：96.8%的用户对产品表示满意</li><li><strong>学习效果</strong>：89%的用户认为学习效果显著</li><li><strong>操作体验</strong>：94%的用户认为操作简单易用</li><li><strong>客服服务</strong>：92%的用户对客服服务表示满意</li></ul><h2>用户建议</h2><p>用户主要建议集中在以下几个方面：增加更多字体选择、优化移动端体验、丰富学习内容等。我们将在后续版本中逐步改进。</p>',
 5, '用户研究团队', 'PUBLISHED', 650, 12, 34, false, '/images/news/user-survey-2024.jpg', '2025-01-15 09:00:00', 90, NOW(), NOW()),

(4,
 '人工智能在书法教育中的应用前景',
 '随着AI技术的不断发展，人工智能在教育领域的应用越来越广泛。本文探讨了AI技术在传统书法教育中的应用现状和未来发展前景。',
 '<h2>技术现状</h2><p>目前，AI在书法教育中的应用主要集中在以下几个方面：</p><ul><li>字体识别与评估</li><li>笔画轨迹分析</li><li>个性化学习推荐</li><li>智能纠错与指导</li></ul><h2>发展趋势</h2><p>未来几年，我们预计将看到以下发展趋势：</p><ol><li><strong>更精准的识别技术</strong>：基于深度学习的字体识别将更加精准</li><li><strong>沉浸式学习体验</strong>：VR/AR技术将为书法学习带来全新体验</li><li><strong>智能化教学助手</strong>：AI将成为每个学习者的专属书法老师</li></ol><h2>挑战与机遇</h2><p>虽然技术发展迅速，但如何平衡传统文化传承与现代技术创新仍是一个重要课题。</p>',
 3, '技术研发部', 'PUBLISHED', 420, 6, 28, false, '/images/news/ai-calligraphy-future.jpg', '2025-01-12 16:20:00', 85, NOW(), NOW()),

(5,
 '全国中小学书法教育数字化转型研讨会成功举办',
 '由教育部指导、我公司协办的"全国中小学书法教育数字化转型研讨会"在北京成功举办，来自全国各地的200多名教育专家和一线教师参加了会议。',
 '<h2>会议主题</h2><p>本次研讨会以"传承与创新：书法教育的数字化未来"为主题，深入探讨了数字技术在书法教育中的应用。</p><h2>重要议题</h2><ul><li>数字化书法教学模式创新</li><li>AI技术在书法评估中的应用</li><li>书法教育资源的数字化建设</li><li>师资培训与能力提升</li></ul><h2>会议成果</h2><p>会议形成了《书法教育数字化转型指导意见》，为全国中小学书法教育的数字化发展提供了重要指导。</p><h2>我们的贡献</h2><p>作为协办方，我们在会议上分享了在智能书法教育方面的实践经验，获得了与会专家的高度认可。</p>',
 2, '教育合作部', 'PUBLISHED', 780, 9, 45, true, '/images/news/education-conference.jpg', '2025-01-10 11:15:00', 88, NOW(), NOW()),

(6,
 '春节特别活动：免费书法课程开放报名',
 '为了让更多人在春节期间感受传统书法的魅力，我们特别推出春节免费书法课程活动。活动期间，所有用户都可以免费体验我们的精品课程。',
 '<h2>活动详情</h2><p>活动时间：2025年1月25日 - 2025年2月15日</p><p>活动内容：</p><ul><li>免费开放所有基础课程</li><li>每日一字挑战活动</li><li>春联书写指导</li><li>名师在线答疑</li></ul><h2>参与方式</h2><ol><li>下载练字机器人APP</li><li>注册并完成实名认证</li><li>选择感兴趣的课程开始学习</li></ol><h2>特色课程</h2><p>本次活动特别推出"春节主题书法课程"，包括春联书写、福字书法、节日祝福语等内容。</p>',
 6, '活动策划部', 'PUBLISHED', 1580, 25, 156, true, '/images/news/spring-festival-activity.jpg', '2025-01-08 08:30:00', 92, NOW(), NOW()),

(7,
 '技术分享：如何实现毫秒级笔画识别',
 '在书法学习中，实时的笔画识别是提升用户体验的关键技术。本文将分享我们在毫秒级笔画识别方面的技术实现和优化经验。',
 '<h2>技术挑战</h2><p>实现毫秒级笔画识别面临以下主要挑战：</p><ul><li>海量数据的实时处理</li><li>复杂笔画的准确识别</li><li>不同设备的兼容性</li><li>网络延迟的优化</li></ul><h2>解决方案</h2><p>我们采用了以下技术方案：</p><ol><li><strong>边缘计算</strong>：将部分计算任务下沉到设备端</li><li><strong>模型压缩</strong>：使用知识蒸馏技术压缩模型大小</li><li><strong>并行处理</strong>：采用多线程并行处理提升效率</li><li><strong>缓存优化</strong>：智能缓存常用笔画模式</li></ol><h2>性能表现</h2><p>经过优化，我们的笔画识别延迟降低到了50毫秒以内，准确率达到99.2%。</p>',
 4, '算法工程师', 'PUBLISHED', 320, 4, 18, false, '/images/news/stroke-recognition-tech.jpg', '2025-01-05 14:45:00', 80, NOW(), NOW()),

(8,
 '用户故事：从零基础到书法达人的蜕变之路',
 '今天我们要分享的是用户李小明的学习故事。他从完全不会写毛笔字的零基础学员，通过6个月的坚持学习，成为了社区里的书法达人。',
 '<h2>初识书法</h2><p>李小明是一名程序员，平时工作繁忙，对传统文化了解不多。偶然的机会接触到我们的产品，被智能化的学习方式所吸引。</p><h2>学习历程</h2><p>第一个月：从基本笔画开始，每天练习30分钟</p><p>第二个月：开始学习简单汉字，掌握了基本结构</p><p>第三个月：挑战复杂字体，学习章法布局</p><p>第四到六个月：创作完整作品，参加线上比赛</p><h2>学习成果</h2><p>经过6个月的学习，李小明不仅掌握了基本的书法技能，还在公司年会上表演书法，获得了同事们的一致好评。</p><h2>学习心得</h2><p>"坚持是最重要的，智能化的指导让我少走了很多弯路。" —— 李小明</p>',
 5, '用户运营', 'PUBLISHED', 890, 18, 67, false, '/images/news/user-story-lixiaoming.jpg', '2025-01-03 10:20:00', 75, NOW(), NOW());

-- 插入新闻标签关联数据
INSERT INTO news_tag_relations (news_id, tag_id) VALUES
-- 新闻1的标签
(1, 1), (1, 2), (1, 5), (1, 11),
-- 新闻2的标签  
(2, 3), (2, 4), (2, 9),
-- 新闻3的标签
(3, 6), (3, 10),
-- 新闻4的标签
(4, 4), (4, 5), (4, 8), (4, 9),
-- 新闻5的标签
(5, 4), (5, 12),
-- 新闻6的标签
(6, 7), (6, 10),
-- 新闻7的标签
(7, 5), (7, 11),
-- 新闻8的标签
(8, 6), (8, 10);

-- 插入新闻互动数据（模拟用户浏览、点赞等行为）
INSERT INTO news_interactions (news_id, interaction_type, user_ip, user_agent, created_at) VALUES
-- 新闻1的互动数据
(1, 'VIEW', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-01-20 10:30:00'),
(1, 'VIEW', '192.168.1.101', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2025-01-20 11:15:00'),
(1, 'LIKE', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-01-20 10:35:00'),
(1, 'LIKE', '192.168.1.102', 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15', '2025-01-20 12:20:00'),

-- 新闻2的互动数据
(2, 'VIEW', '192.168.1.103', 'Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0', '2025-01-18 15:00:00'),
(2, 'VIEW', '192.168.1.104', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-01-18 16:30:00'),
(2, 'LIKE', '192.168.1.103', 'Mozilla/5.0 (Android 11; Mobile; rv:68.0) Gecko/68.0 Firefox/88.0', '2025-01-18 15:05:00'),

-- 新闻6的互动数据（春节活动，互动较多）
(6, 'VIEW', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-01-08 09:00:00'),
(6, 'VIEW', '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2025-01-08 09:30:00'),
(6, 'LIKE', '192.168.1.105', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', '2025-01-08 09:10:00'),
(6, 'LIKE', '192.168.1.107', 'Mozilla/5.0 (iPad; CPU OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15', '2025-01-08 10:15:00'),
(6, 'SHARE', '192.168.1.106', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', '2025-01-08 09:45:00');

-- 插入新闻状态变更日志
INSERT INTO news_status_logs (news_id, old_status, new_status, reason, operator, created_at) VALUES
(1, 'DRAFT', 'PUBLISHED', '产品发布，正式上线', '产品经理', '2025-01-20 09:55:00'),
(2, 'DRAFT', 'PUBLISHED', '合作新闻，及时发布', '市场经理', '2025-01-18 14:25:00'),
(3, 'DRAFT', 'PUBLISHED', '调查报告完成，发布分享', '运营经理', '2025-01-15 08:55:00'),
(4, 'DRAFT', 'PUBLISHED', '技术文章，分享经验', '技术经理', '2025-01-12 16:15:00'),
(5, 'DRAFT', 'PUBLISHED', '会议报道，及时发布', '公关经理', '2025-01-10 11:10:00'),
(6, 'DRAFT', 'PUBLISHED', '春节活动，重点推广', '活动经理', '2025-01-08 08:25:00'),
(7, 'DRAFT', 'PUBLISHED', '技术分享，内容优质', '技术总监', '2025-01-05 14:40:00'),
(8, 'DRAFT', 'PUBLISHED', '用户故事，正面宣传', '用户运营', '2025-01-03 10:15:00');

-- 更新序列值（如果使用自增ID）
-- 这部分根据实际数据库类型调整
-- MySQL: ALTER TABLE news AUTO_INCREMENT = 9;
-- PostgreSQL: SELECT setval('news_id_seq', 9);

-- 插入完成提示
SELECT '新闻管理系统初始数据插入完成！' as message,
       (SELECT COUNT(*) FROM news_categories) as categories_count,
       (SELECT COUNT(*) FROM news_tags) as tags_count,
       (SELECT COUNT(*) FROM news) as news_count,
       (SELECT COUNT(*) FROM news_tag_relations) as tag_relations_count,
       (SELECT COUNT(*) FROM news_interactions) as interactions_count,
       (SELECT COUNT(*) FROM news_status_logs) as status_logs_count;