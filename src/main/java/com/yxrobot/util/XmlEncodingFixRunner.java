package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML编码修复执行器
 * 执行XML文件编码修复操作
 */
public class XmlEncodingFixRunner {
    
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
        
        System.out.println("开始修复XML文件编码问题...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("需要修复的文件数量: " + PROBLEMATIC_FILES.length);
        System.out.println("========================================");
        
        XmlEncodingFixer fixer = new XmlEncodingFixer();
        List<File> filesToFix = new ArrayList<>();
        
        // 收集需要修复的文件
        for (String fileName : PROBLEMATIC_FILES) {
            File xmlFile = new File(mapperPath, fileName);
            if (xmlFile.exists()) {
                filesToFix.add(xmlFile);
            } else {
                System.out.println("警告: 文件不存在 - " + fileName);
            }
        }
        
        System.out.println("找到 " + filesToFix.size() + " 个需要修复的文件");
        System.out.println("----------------------------------------");
        
        // 执行修复
        List<XmlEncodingFixer.FixResult> results = fixer.fixMultipleFiles(filesToFix);
        
        // 统计结果
        int successCount = 0;
        int failureCount = 0;
        
        System.out.println("----------------------------------------");
        System.out.println("修复结果详情:");
        
        for (XmlEncodingFixer.FixResult result : results) {
            if (result.isSuccess()) {
                successCount++;
                System.out.println("✓ " + result.getFileName() + 
                    " (" + result.getOriginalEncoding() + " -> " + result.getNewEncoding() + ")");
            } else {
                failureCount++;
                System.out.println("✗ " + result.getFileName() + " -> " + result.getMessage());
            }
        }
        
        System.out.println("========================================");
        System.out.println("修复完成统计:");
        System.out.println("成功修复: " + successCount + " 个文件");
        System.out.println("修复失败: " + failureCount + " 个文件");
        System.out.println("总计处理: " + results.size() + " 个文件");
        
        if (failureCount > 0) {
            System.out.println("警告: 有文件修复失败，请检查错误信息");
            System.exit(1);
        } else {
            System.out.println("所有文件编码修复成功！");
            System.out.println("建议: 请运行编译测试验证修复结果");
        }
    }
}