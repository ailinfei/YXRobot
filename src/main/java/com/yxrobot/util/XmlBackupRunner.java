package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XML备份执行器
 * 根据扫描报告中识别的问题文件执行备份操作
 */
public class XmlBackupRunner {
    
    // 根据扫描报告，这些是需要修复的文件
    private static final String[] PROBLEMATIC_FILES = {
        "CharityActivityMapper.xml",
        "CharityInstitutionMapper.xml", 
        "CharityProjectMapper.xml",
        "CharityStatsLogMapper.xml",
        "CharityStatsMapper.xml",
        "CustomerMapper.xml",
        "CustomerOrderMapper.xml",
        "CustomerServiceRecordMapper.xml",
        "CustomerStatsMapper.xml",
        "LinkClickLogMapper.xml",
        "LinkValidationLogMapper.xml",
        "NewsCategoryMapper.xml",
        "NewsInteractionMapper.xml",
        "NewsMapper.xml",
        "NewsStatusLogMapper.xml",
        "NewsTagMapper.xml",
        "NewsTagRelationMapper.xml",
        "PlatformLinkMapper.xml",
        "ProductMapper.xml",
        "ProductMediaMapper.xml",
        "RegionConfigMapper.xml",
        "RentalCustomerMapper.xml",
        "RentalDeviceMapper.xml",
        "RentalRecordMapper.xml",
        "SalesProductMapper.xml",
        "SalesRecordMapper.xml",
        "SalesStatsMapper.xml"
    };
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始备份有编码问题的XML文件...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("需要备份的文件数量: " + PROBLEMATIC_FILES.length);
        System.out.println("----------------------------------------");
        
        XmlBackupService backupService = new XmlBackupService(projectPath);
        List<File> filesToBackup = new ArrayList<>();
        
        // 收集需要备份的文件
        for (String fileName : PROBLEMATIC_FILES) {
            File xmlFile = new File(mapperPath, fileName);
            if (xmlFile.exists()) {
                filesToBackup.add(xmlFile);
            } else {
                System.out.println("警告: 文件不存在 - " + fileName);
            }
        }
        
        System.out.println("找到 " + filesToBackup.size() + " 个需要备份的文件");
        System.out.println("----------------------------------------");
        
        // 执行备份
        List<XmlBackupService.BackupResult> results = backupService.backupFiles(filesToBackup);
        
        // 统计结果
        int successCount = 0;
        int failureCount = 0;
        
        System.out.println("备份结果:");
        for (XmlBackupService.BackupResult result : results) {
            if (result.isSuccess()) {
                successCount++;
                System.out.println("✓ " + new File(result.getOriginalFile()).getName() + " -> 备份成功");
            } else {
                failureCount++;
                System.out.println("✗ " + new File(result.getOriginalFile()).getName() + " -> " + result.getMessage());
            }
        }
        
        System.out.println("----------------------------------------");
        System.out.println("备份完成统计:");
        System.out.println("成功: " + successCount + " 个文件");
        System.out.println("失败: " + failureCount + " 个文件");
        System.out.println("备份目录: " + backupService.getBackupDirectory());
        
        if (failureCount > 0) {
            System.out.println("警告: 有文件备份失败，请检查错误信息");
            System.exit(1);
        } else {
            System.out.println("所有文件备份成功！");
        }
    }
}