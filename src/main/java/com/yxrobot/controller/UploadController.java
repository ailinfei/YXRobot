package com.yxrobot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 * 处理产品封面图片等文件上传
 */
@RestController
@RequestMapping("/api/v1/upload")

public class UploadController {
    
    @Value("${file.upload.path:uploads}")
    private String uploadPath;
    
    @Value("${server.port:8081}")
    private String serverPort;
    
    /**
     * 上传产品封面图片
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/product/cover")
    public ResponseEntity<Map<String, Object>> uploadProductCover(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("message", "文件不能为空");
                return ResponseEntity.ok(response);
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                response.put("code", 400);
                response.put("message", "只支持图片文件");
                return ResponseEntity.ok(response);
            }
            
            // 验证文件大小 (5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("code", 400);
                response.put("message", "文件大小不能超过5MB");
                return ResponseEntity.ok(response);
            }
            
            // 创建上传目录
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + "/products/covers/" + dateFolder;
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadDirPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL
            String fileUrl = "/uploads/products/covers/" + dateFolder + "/" + fileName;
            String fullUrl = "http://localhost:" + serverPort + fileUrl;
            
            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("fullUrl", fullUrl);
            data.put("fileName", fileName);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());
            data.put("contentType", contentType);
            
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "文件保存失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 上传通用文件
     * @param file 上传的文件
     * @param type 文件类型 (product, news, charity等)
     * @return 上传结果
     */
    @PostMapping("/{type}")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                response.put("code", 400);
                response.put("message", "文件不能为空");
                return ResponseEntity.ok(response);
            }
            
            // 验证文件大小 (10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                response.put("code", 400);
                response.put("message", "文件大小不能超过10MB");
                return ResponseEntity.ok(response);
            }
            
            // 创建上传目录
            String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + "/" + type + "/" + dateFolder;
            Path uploadDirPath = Paths.get(uploadDir);
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
            
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 保存文件
            Path filePath = uploadDirPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            // 生成访问URL
            String fileUrl = "/uploads/" + type + "/" + dateFolder + "/" + fileName;
            String fullUrl = "http://localhost:" + serverPort + fileUrl;
            
            // 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("url", fileUrl);
            data.put("fullUrl", fullUrl);
            data.put("fileName", fileName);
            data.put("originalName", originalFilename);
            data.put("size", file.getSize());
            data.put("contentType", file.getContentType());
            
            response.put("code", 200);
            response.put("message", "上传成功");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "文件保存失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "上传失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 删除结果
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteFile(@RequestParam String filePath) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 安全检查，确保文件路径在上传目录内
            Path file = Paths.get(uploadPath, filePath);
            Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path fileAbsolutePath = file.toAbsolutePath().normalize();
            
            if (!fileAbsolutePath.startsWith(uploadDir)) {
                response.put("code", 400);
                response.put("message", "非法的文件路径");
                return ResponseEntity.ok(response);
            }
            
            if (Files.exists(fileAbsolutePath)) {
                Files.delete(fileAbsolutePath);
                response.put("code", 200);
                response.put("message", "删除成功");
            } else {
                response.put("code", 404);
                response.put("message", "文件不存在");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败：" + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}