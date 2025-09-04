package com.yxrobot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * XML最终修复执行器
 */
public class XmlFinalFixRunner {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始最终修复XML文件中的替换字符...");
        System.out.println("项目路径: " + projectPath);
        System.out.println("映射文件路径: " + mapperPath);
        System.out.println("========================================");
        
        XmlFinalFixer fixer = new XmlFinalFixer();
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
        
        // 执行最终修复
        List<XmlFinalFixer.FixResult> results = fixer.fixMultipleFiles(xmlFiles);
        
        // 统计结果
        int totalFiles = results.size();
        int filesWithReplacementChars = 0;
        int filesFixed = 0;
        int totalOriginalChars = 0;
        int totalFixedChars = 0;
        
        System.out.println("========================================");
        System.out.println("最终修复结果统计:");
        
        for (XmlFinalFixer.FixResult result : results) {
            if (result.getOriginalReplacementCount() > 0) {
                filesWithReplacementChars++;
                totalOriginalChars += result.getOriginalReplacementCount();
                totalFixedChars += result.getFixedCount();
                
                if (result.getFixedCount() > 0) {
                    filesFixed++;
                }
                
                System.out.println(String.format("- %s: 修复了 %d 个替换字符", 
                    result.getFileName(), result.getFixedCount()));
            }
        }
        
        System.out.println("========================================");
        System.out.println("总体统计:");
        System.out.println("总文件数: " + totalFiles);
        System.out.println("包含替换字符的文件: " + filesWithReplacementChars);
        System.out.println("成功修复的文件: " + filesFixed);
        System.out.println("原始替换字符总数: " + totalOriginalChars);
        System.out.println("成功修复字符数: " + totalFixedChars);
        
        if (totalFixedChars > 0) {
            System.out.println("🎉 成功修复了 " + totalFixedChars + " 个替换字符！");
        } else {
            System.out.println("ℹ️ 没有发现需要修复的替换字符");
        }
        
        System.out.println("========================================");
        System.out.println("注意事项:");
        System.out.println("- 所有替换字符(�)已被替换为'射'字");
        System.out.println("- 请检查修复结果，确保语义正确");
        System.out.println("- 如有必要，请手动调整个别不正确的字符");
        System.out.println("建议: 请运行编译测试验证修复结果");
    }
}