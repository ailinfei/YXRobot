-- 测试数据初始化
-- 设备管理模块测试数据

-- 插入测试客户数据
INSERT INTO customers (id, name, phone, email) VALUES
(1, '[测试客户1]', '[测试电话1]', '[测试邮箱1]'),
(2, '[测试客户2]', '[测试电话2]', '[测试邮箱2]'),
(3, '[测试客户3]', '[测试电话3]', '[测试邮箱3]');

-- 插入测试设备数据
INSERT INTO managed_devices (id, serial_number, model, status, firmware_version, customer_id, customer_name, customer_phone, created_by, notes, is_deleted) VALUES
(1, 'YX-TEST-001', 'YX-EDU-2024', 'online', '1.0.0', 1, '[测试客户1]', '[测试电话1]', 'test-admin', '测试设备1', 0),
(2, 'YX-TEST-002', 'YX-HOME-2024', 'offline', '1.0.0', 2, '[测试客户2]', '[测试电话2]', 'test-admin', '测试设备2', 0),
(3, 'YX-TEST-003', 'YX-PRO-2024', 'error', '1.1.0', 3, '[测试客户3]', '[测试电话3]', 'test-admin', '测试设备3', 0),
(4, 'YX-TEST-004', 'YX-EDU-2024', 'maintenance', '1.0.0', 1, '[测试客户1]', '[测试电话1]', 'test-admin', '测试设备4', 0),
(5, 'YX-TEST-005', 'YX-HOME-2024', 'online', '1.1.0', 2, '[测试客户2]', '[测试电话2]', 'test-admin', '测试设备5', 0);

-- 插入设备技术参数数据
INSERT INTO managed_device_specifications (device_id, cpu, memory, storage, display, battery, connectivity) VALUES
(1, 'ARM Cortex-A72', '4GB DDR4', '64GB eMMC', '10.1" IPS 1920x1200', '8000mAh', '{"wifi": "802.11ac", "bluetooth": "5.0", "ethernet": "10/100M"}'),
(2, 'ARM Cortex-A53', '2GB DDR3', '32GB eMMC', '8" IPS 1280x800', '5000mAh', '{"wifi": "802.11n", "bluetooth": "4.2"}'),
(3, 'ARM Cortex-A76', '8GB DDR4', '128GB eMMC', '12.3" IPS 2560x1600', '10000mAh', '{"wifi": "802.11ax", "bluetooth": "5.2", "ethernet": "1000M", "4g": "LTE"}'),
(4, 'ARM Cortex-A72', '4GB DDR4', '64GB eMMC', '10.1" IPS 1920x1200', '8000mAh', '{"wifi": "802.11ac", "bluetooth": "5.0", "ethernet": "10/100M"}'),
(5, 'ARM Cortex-A53', '2GB DDR3', '32GB eMMC', '8" IPS 1280x800', '5000mAh', '{"wifi": "802.11n", "bluetooth": "4.2"}');

-- 插入设备使用统计数据
INSERT INTO managed_device_usage_stats (device_id, total_runtime, usage_count, average_session_time) VALUES
(1, 1200, 45, 27),
(2, 800, 32, 25),
(3, 2400, 78, 31),
(4, 600, 20, 30),
(5, 1500, 55, 27);

-- 插入设备维护记录数据
INSERT INTO managed_device_maintenance_records (device_id, type, description, technician, start_time, end_time, status, cost) VALUES
(1, 'inspection', '定期检查', '[技术员1]', '2024-01-01 10:00:00', '2024-01-01 11:00:00', 'completed', 100.00),
(2, 'repair', '屏幕更换', '[技术员2]', '2024-01-02 14:00:00', '2024-01-02 16:00:00', 'completed', 300.00),
(3, 'upgrade', '固件升级', '[技术员1]', '2024-01-03 09:00:00', NULL, 'in_progress', 0.00),
(4, 'maintenance', '清洁保养', '[技术员3]', '2024-01-04 15:00:00', '2024-01-04 15:30:00', 'completed', 50.00);

-- 插入设备配置数据
INSERT INTO managed_device_configurations (device_id, language, timezone, auto_update, debug_mode) VALUES
(1, 'zh-CN', 'Asia/Shanghai', 1, 0),
(2, 'en-US', 'America/New_York', 1, 0),
(3, 'ja-JP', 'Asia/Tokyo', 0, 1),
(4, 'zh-CN', 'Asia/Shanghai', 1, 0),
(5, 'ko-KR', 'Asia/Seoul', 1, 0);

-- 插入设备位置信息数据
INSERT INTO managed_device_locations (device_id, latitude, longitude, address) VALUES
(1, 39.9042, 116.4074, '北京市朝阳区测试地址1'),
(2, 31.2304, 121.4737, '上海市浦东新区测试地址2'),
(3, 22.3193, 114.1694, '深圳市南山区测试地址3'),
(4, 30.5728, 104.0668, '成都市高新区测试地址4'),
(5, 32.0603, 118.7969, '南京市鼓楼区测试地址5');

-- 插入设备日志数据
INSERT INTO managed_device_logs (device_id, timestamp, level, category, message, details) VALUES
(1, '2024-01-01 08:00:00', 'info', 'system', '设备启动', '{"boot_time": "15s", "version": "1.0.0"}'),
(1, '2024-01-01 08:01:00', 'info', 'user', '用户登录', '{"user": "[用户1]", "login_time": "2024-01-01 08:01:00"}'),
(1, '2024-01-01 10:30:00', 'warning', 'hardware', '温度过高', '{"temperature": "65°C", "threshold": "60°C"}'),
(2, '2024-01-02 09:00:00', 'info', 'system', '设备启动', '{"boot_time": "18s", "version": "1.0.0"}'),
(2, '2024-01-02 14:30:00', 'error', 'network', '网络连接失败', '{"error": "timeout", "retry_count": 3}'),
(3, '2024-01-03 07:45:00', 'info', 'system', '设备启动', '{"boot_time": "12s", "version": "1.1.0"}'),
(3, '2024-01-03 09:15:00', 'error', 'hardware', 'CPU过载', '{"cpu_usage": "95%", "threshold": "80%"}'),
(4, '2024-01-04 11:00:00', 'info', 'system', '进入维护模式', '{"maintenance_type": "scheduled", "duration": "2h"}'),
(5, '2024-01-05 13:20:00', 'info', 'software', '应用更新', '{"app": "练字助手", "version": "2.1.0"}'),
(5, '2024-01-05 15:45:00', 'debug', 'system', '性能监控', '{"cpu": "25%", "memory": "60%", "disk": "45%"}');