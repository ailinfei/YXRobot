package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML恢复和修复执行器
 * 从备份文件恢复并正确修复编码问题
 */
public class XmlRestoreAndFixRunner {
    
    // 需要处理的文件列表
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
        String backupPath = projectPath + File.separator + "xml-backup";
        
        System.out.println("开始从备份恢复并修复XML文件编码...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("备份路径: " + backupPath);
        System.out.println("需要处理的文件数量: " + PROBLEMATIC_FILES.length);
        System.out.println("========================================");
        
        XmlEncodingFixerV2 fixer = new XmlEncodingFixerV2();
        List<XmlEncodingFixerV2.FileMapping> fileMappings = new ArrayList<>();
        
        // 创建文件映射
        for (String fileName : PROBLEMATIC_FILES) {
            File originalFile = new File(mapperPath, fileName);
            
            // 查找对应的备份文件
            File backupDir = new File(backupPath);
            File[] backupFiles = backupDir.listFiles((dir, name) -> 
                name.startsWith(fileName.replace(".xml", "_")) && name.endsWith(".xml.bak"));
            
            if (backupFiles != null && backupFiles.length > 0) {
                File backupFile = backupFiles[0]; // 取第一个匹配的备份文件
                fileMappings.add(new XmlEncodingFixerV2.FileMapping(originalFile, backupFile));
                System.out.println("映射: " + fileName + " <- " + backupFile.getName());
            } else {
                System.out.println("警告: 未找到备份文件 - " + fileName);
            }
        }
        
        System.out.println("找到 " + fileMappings.size() + " 个文件映射");
        System.out.println("----------------------------------------");
        
        // 执行恢复和修复
        List<XmlEncodingFixerV2.FixResult> results = fixer.restoreAndFixMultiple(fileMappings);
        
        // 统计结果
        int successCount = 0;
        int failureCount = 0;
        int replacementCharCount = 0;
        
        System.out.println("----------------------------------------");
        System.out.println("恢复修复结果详情:");
        
        for (XmlEncodingFixerV2.FixResult result : results) {
            if (result.isSuccess()) {
                successCount++;
                if (result.isHasReplacementChars()) {
                    replacementCharCount++;
                }
                System.out.println("✓ " + result.getFileName() + 
                    (result.isHasReplacementChars() ? " (包含替换字符)" : " (正常)"));
            } else {
                failureCount++;
                System.out.println("✗ " + result.getFileName() + " -> " + result.getMessage());
            }
        }
        
        System.out.println("========================================");
        System.out.println("恢复修复完成统计:");
        System.out.println("成功恢复: " + successCount + " 个文件");
        System.out.println("恢复失败: " + failureCount + " 个文件");
        System.out.println("包含替换字符: " + replacementCharCount + " 个文件");
        System.out.println("总计处理: " + results.size() + " 个文件");
        
        if (failureCount > 0) {
            System.out.println("警告: 有文件恢复失败，请检查错误信息");
        } else {
            System.out.println("所有文件恢复成功！");
            if (replacementCharCount > 0) {
                System.out.println("注意: " + replacementCharCount + " 个文件仍包含替换字符，可能需要手动修复");
            }
        }
        
        System.out.println("建议: 请运行编译测试验证修复结果");
    }
}