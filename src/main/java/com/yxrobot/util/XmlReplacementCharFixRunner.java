package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML替换字符修复执行器
 */
public class XmlReplacementCharFixRunner {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始修复XML文件中的替换字符问题...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("========================================");
        
        XmlReplacementCharFixer fixer = new XmlReplacementCharFixer();
        List<File> xmlFiles = new ArrayList<>();
        
        // 收集所有XML文件
        File mapperDir = new File(mapperPath);
        if (mapperDir.exists() && mapperDir.isDirectory()) {
            File[] files = mapperDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                for (File file : files) {
                    xmlFiles.add(file);
                }
            }
        }
        
        System.out.println("找到 " + xmlFiles.size() + " 个XML文件");
        System.out.println("----------------------------------------");
        
        // 执行修复
        List<XmlReplacementCharFixer.FixResult> results = fixer.fixMultipleFiles(xmlFiles);
        
        // 统计结果
        int totalFiles = results.size();
        int filesWithReplacementChars = 0;
        int filesFixed = 0;
        int totalReplacementChars = 0;
        int totalFixedChars = 0;
        
        System.out.println("----------------------------------------");
        System.out.println("修复结果统计:");
        
        for (XmlReplacementCharFixer.FixResult result : results) {
            if (result.getOriginalReplacementCount() > 0) {
                filesWithReplacementChars++;
                totalReplacementChars += result.getOriginalReplacementCount();
                
                if (result.getFixedCount() > 0) {
                    filesFixed++;
                    totalFixedChars += result.getFixedCount();
                }
            }
        }
        
        System.out.println("========================================");
        System.out.println("总体统计:");
        System.out.println("总文件数: " + totalFiles);
        System.out.println("包含替换字符的文件: " + filesWithReplacementChars);
        System.out.println("成功修复的文件: " + filesFixed);
        System.out.println("总替换字符数: " + totalReplacementChars);
        System.out.println("成功修复字符数: " + totalFixedChars);
        
        if (filesWithReplacementChars > filesFixed) {
            System.out.println("注意: 仍有 " + (filesWithReplacementChars - filesFixed) + " 个文件包含无法自动修复的替换字符");
            System.out.println("建议手动检查这些文件并修复剩余的替换字符");
        }
        
        System.out.println("建议: 请运行编译测试验证修复结果");
    }
}