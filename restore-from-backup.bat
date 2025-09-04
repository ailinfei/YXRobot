@echo off
echo 开始从备份恢复所有XML文件...
echo ========================================

set BACKUP_DIR=xml-backup
set MAPPER_DIR=src\main\resources\mapper

echo 恢复XML映射文件...

copy "%BACKUP_DIR%\CharityActivityMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CharityActivityMapper.xml"
copy "%BACKUP_DIR%\CharityInstitutionMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CharityInstitutionMapper.xml"
copy "%BACKUP_DIR%\CharityProjectMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CharityProjectMapper.xml"
copy "%BACKUP_DIR%\CharityStatsLogMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CharityStatsLogMapper.xml"
copy "%BACKUP_DIR%\CharityStatsMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CharityStatsMapper.xml"
copy "%BACKUP_DIR%\CustomerMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CustomerMapper.xml"
copy "%BACKUP_DIR%\CustomerOrderMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CustomerOrderMapper.xml"
copy "%BACKUP_DIR%\CustomerServiceRecordMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CustomerServiceRecordMapper.xml"
copy "%BACKUP_DIR%\CustomerStatsMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\CustomerStatsMapper.xml"
copy "%BACKUP_DIR%\LinkClickLogMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\LinkClickLogMapper.xml"
copy "%BACKUP_DIR%\LinkValidationLogMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\LinkValidationLogMapper.xml"
copy "%BACKUP_DIR%\NewsCategoryMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsCategoryMapper.xml"
copy "%BACKUP_DIR%\NewsInteractionMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsInteractionMapper.xml"
copy "%BACKUP_DIR%\NewsMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsMapper.xml"
copy "%BACKUP_DIR%\NewsStatusLogMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsStatusLogMapper.xml"
copy "%BACKUP_DIR%\NewsTagMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsTagMapper.xml"
copy "%BACKUP_DIR%\NewsTagRelationMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\NewsTagRelationMapper.xml"
copy "%BACKUP_DIR%\PlatformLinkMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\PlatformLinkMapper.xml"
copy "%BACKUP_DIR%\ProductMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\ProductMapper.xml"
copy "%BACKUP_DIR%\ProductMediaMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\ProductMediaMapper.xml"
copy "%BACKUP_DIR%\RegionConfigMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\RegionConfigMapper.xml"
copy "%BACKUP_DIR%\RentalCustomerMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\RentalCustomerMapper.xml"
copy "%BACKUP_DIR%\RentalDeviceMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\RentalDeviceMapper.xml"
copy "%BACKUP_DIR%\RentalRecordMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\RentalRecordMapper.xml"
copy "%BACKUP_DIR%\SalesProductMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\SalesProductMapper.xml"
copy "%BACKUP_DIR%\SalesRecordMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\SalesRecordMapper.xml"
copy "%BACKUP_DIR%\SalesStatsMapper_20250901-192935.xml.bak" "%MAPPER_DIR%\SalesStatsMapper.xml"

echo ========================================
echo 所有文件恢复完成！
echo 建议运行编译测试验证结果
pause