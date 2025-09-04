package com.yxrobot.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * XML问号修复工具
 * 修复"映射?"等问题
 */
public class XmlQuestionMarkFixer {
    
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String mapperPath = projectPath + File.separator + "src" + File.separator + 
                           "main" + File.separator + "resources" + File.separator + "mapper";
        
        System.out.println("开始修复XML文件中的问号问题...");
        System.out.println("========================================");
        
        File mapperDir = new File(mapperPath);
        if (mapperDir.exists() && mapperDir.isDirectory()) {
            File[] files = mapperDir.listFiles((dir, name) -> name.endsWith(".xml"));
            if (files != null) {
                int totalFixed = 0;
                for (File file : files) {
                    try {
                        String content = Files.readString(Paths.get(file.getAbsolutePath()), StandardCharsets.UTF_8);
                        String originalContent = content;
                        
                        // 修复各种问号问题
                        content = content.replace("映射?", "映射");
                        content = content.replace("结果映射?", "结果映射");
                        content = content.replace("查询?", "查询");
                        content = content.replace("插入?", "插入");
                        content = content.replace("更新?", "更新");
                        content = content.replace("删除?", "删除");
                        content = content.replace("统计?", "统计");
                        
                        if (!content.equals(originalContent)) {
                            Files.writeString(Paths.get(file.getAbsolutePath()), content, StandardCharsets.UTF_8);
                            System.out.println("✓ 修复: " + file.getName());
                            totalFixed++;
                        }
                        
                    } catch (Exception e) {
                        System.err.println("✗ 处理失败: " + file.getName() + " - " + e.getMessage());
                    }
                }
                
                System.out.println("========================================");
                System.out.println("修复完成，共处理 " + totalFixed + " 个文件");
            }
        }
    }
}