package com.yxrobot.service;

import com.yxrobot.exception.NewsValidationException;
import com.yxrobot.exception.NewsOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 新闻文件上传服务类
 * 负责处理新闻相关的文件上传功能
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */
@Service
public class NewsFileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsFileUploadService.class);
    
    // 允许的图片格式
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif"
    );
    
    // 允许的文件扩展名
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif"
    );
    
    // 最大文件大小（2MB）
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
    
    // 缩略图尺寸配置
    private static final Map<String, int[]> THUMBNAIL_SIZES = new HashMap<>();
    
    static {
        THUMBNAIL_SIZES.put("large", new int[]{800, 600});
        THUMBNAIL_SIZES.put("medium", new int[]{400, 300});
        THUMBNAIL_SIZES.put("small", new int[]{200, 150});
    }
    
    @Value("${app.upload.news.path:/uploads/news/}")
    private String uploadBasePath;
    
    @Value("${app.upload.news.url-prefix:/uploads/news/}")
    private String urlPrefix;
    
    @Value("${app.upload.news.max-size:2097152}")
    private long maxFileSize;
    
    /**
     * 上传新闻封面图片
     * 
     * @param file 上传的文件
     * @return 上传结果信息
     */
    public Map<String, Object> uploadNewsImage(MultipartFile file) {
        logger.info("上传新闻图片 - 文件名: {}, 大小: {} bytes", file.getOriginalFilename(), file.getSize());
        
        // 文件验证
        validateImageFile(file);
        
        try {
            // 生成文件路径
            String relativePath = generateFilePath(file);
            Path fullPath = Paths.get(uploadBasePath, relativePath);
            
            // 确保目录存在
            Files.createDirectories(fullPath.getParent());
            
            // 保存原始文件
            Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 生成缩略图
            Map<String, String> thumbnails = generateThumbnails(fullPath, relativePath);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("originalUrl", urlPrefix + relativePath);
            result.put("thumbnails", thumbnails);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("uploadTime", LocalDateTime.now());
            
            logger.info("上传新闻图片成功 - 路径: {}", relativePath);
            return result;
            
        } catch (IOException e) {
            logger.error("上传新闻图片失败 - 文件名: {}, 错误: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new NewsOperationException("上传图片", "文件保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传新闻图片
     * 
     * @param files 上传的文件列表
     * @return 上传结果列表
     */
    public List<Map<String, Object>> batchUploadNewsImages(List<MultipartFile> files) {
        logger.info("批量上传新闻图片 - 数量: {}", files.size());
        
        if (files == null || files.isEmpty()) {
            throw new NewsValidationException("files", files, "上传文件列表不能为空");
        }
        
        if (files.size() > 10) {
            throw new NewsValidationException("files", files, "单次最多只能上传10个文件");
        }
        
        return files.stream()
                .map(this::uploadNewsImage)
                .toList();
    }
    
    /**
     * 删除新闻图片
     * 
     * @param relativePath 相对路径
     * @return 是否删除成功
     */
    public boolean deleteNewsImage(String relativePath) {
        logger.info("删除新闻图片 - 路径: {}", relativePath);
        
        if (!StringUtils.hasText(relativePath)) {
            throw new NewsValidationException("relativePath", relativePath, "文件路径不能为空");
        }
        
        try {
            Path fullPath = Paths.get(uploadBasePath, relativePath);
            
            // 删除原始文件
            boolean deleted = Files.deleteIfExists(fullPath);
            
            // 删除缩略图
            deleteThumbnails(relativePath);
            
            logger.info("删除新闻图片{} - 路径: {}", deleted ? "成功" : "失败", relativePath);
            return deleted;
            
        } catch (IOException e) {
            logger.error("删除新闻图片失败 - 路径: {}, 错误: {}", relativePath, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 获取图片信息
     * 
     * @param relativePath 相对路径
     * @return 图片信息
     */
    public Map<String, Object> getImageInfo(String relativePath) {
        logger.info("获取图片信息 - 路径: {}", relativePath);
        
        if (!StringUtils.hasText(relativePath)) {
            throw new NewsValidationException("relativePath", relativePath, "文件路径不能为空");
        }
        
        try {
            Path fullPath = Paths.get(uploadBasePath, relativePath);
            
            if (!Files.exists(fullPath)) {
                throw new NewsValidationException("file", relativePath, "文件不存在: " + relativePath);
            }
            
            // 读取图片信息
            BufferedImage image = ImageIO.read(fullPath.toFile());
            if (image == null) {
                throw new NewsValidationException("file", relativePath, "无法读取图片文件: " + relativePath);
            }
            
            Map<String, Object> info = new HashMap<>();
            info.put("width", image.getWidth());
            info.put("height", image.getHeight());
            info.put("size", Files.size(fullPath));
            info.put("lastModified", Files.getLastModifiedTime(fullPath).toInstant());
            info.put("url", urlPrefix + relativePath);
            
            logger.info("获取图片信息成功 - 尺寸: {}x{}", image.getWidth(), image.getHeight());
            return info;
            
        } catch (IOException e) {
            logger.error("获取图片信息失败 - 路径: {}, 错误: {}", relativePath, e.getMessage(), e);
            throw new NewsOperationException("获取图片信息", "读取文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 清理未使用的图片文件
     * 
     * @param usedPaths 正在使用的文件路径列表
     * @return 清理的文件数量
     */
    public int cleanupUnusedImages(List<String> usedPaths) {
        logger.info("清理未使用的图片文件 - 使用中的文件数: {}", usedPaths.size());
        
        try {
            Path uploadDir = Paths.get(uploadBasePath);
            if (!Files.exists(uploadDir)) {
                return 0;
            }
            
            int deletedCount = 0;
            
            // 遍历上传目录
            Files.walk(uploadDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> isImageFile(path.toString()))
                    .forEach(path -> {
                        String relativePath = uploadDir.relativize(path).toString().replace("\\", "/");
                        if (!usedPaths.contains(relativePath)) {
                            try {
                                Files.delete(path);
                                logger.debug("删除未使用的图片文件: {}", relativePath);
                            } catch (IOException e) {
                                logger.warn("删除文件失败: {}, 错误: {}", relativePath, e.getMessage());
                            }
                        }
                    });
            
            logger.info("清理未使用的图片文件完成 - 删除数量: {}", deletedCount);
            return deletedCount;
            
        } catch (IOException e) {
            logger.error("清理未使用的图片文件失败 - 错误: {}", e.getMessage(), e);
            throw new NewsOperationException("清理图片文件", "遍历目录失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证上传的图片文件
     * 
     * @param file 上传的文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new NewsValidationException("file", null, "上传文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > maxFileSize) {
            throw new NewsValidationException("fileSize", file.getSize(), 
                    String.format("文件大小不能超过 %d MB", maxFileSize / 1024 / 1024));
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new NewsValidationException("contentType", contentType, 
                    "不支持的文件类型，只支持: " + String.join(", ", ALLOWED_IMAGE_TYPES));
        }
        
        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new NewsValidationException("fileName", originalFilename, "文件名不能为空");
        }
        
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new NewsValidationException("extension", extension, 
                    "不支持的文件扩展名，只支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }
    
    /**
     * 生成文件保存路径
     * 
     * @param file 上传的文件
     * @return 相对路径
     */
    private String generateFilePath(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        
        // 按日期分目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        
        // 生成唯一文件名
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        return dateDir + "/" + uniqueFileName;
    }
    
    /**
     * 生成缩略图
     * 
     * @param originalPath 原始文件路径
     * @param relativePath 相对路径
     * @return 缩略图URL映射
     */
    private Map<String, String> generateThumbnails(Path originalPath, String relativePath) {
        Map<String, String> thumbnails = new HashMap<>();
        
        try {
            BufferedImage originalImage = ImageIO.read(originalPath.toFile());
            if (originalImage == null) {
                logger.warn("无法读取图片文件，跳过缩略图生成: {}", originalPath);
                return thumbnails;
            }
            
            String baseName = getFileNameWithoutExtension(relativePath);
            String extension = getFileExtension(relativePath);
            
            for (Map.Entry<String, int[]> entry : THUMBNAIL_SIZES.entrySet()) {
                String sizeName = entry.getKey();
                int[] dimensions = entry.getValue();
                
                try {
                    // 生成缩略图
                    BufferedImage thumbnail = resizeImage(originalImage, dimensions[0], dimensions[1]);
                    
                    // 保存缩略图
                    String thumbnailPath = baseName + "_" + sizeName + extension;
                    Path thumbnailFullPath = Paths.get(uploadBasePath, thumbnailPath);
                    
                    String formatName = extension.substring(1); // 去掉点号
                    ImageIO.write(thumbnail, formatName, thumbnailFullPath.toFile());
                    
                    thumbnails.put(sizeName, urlPrefix + thumbnailPath);
                    
                } catch (Exception e) {
                    logger.warn("生成{}缩略图失败: {}, 错误: {}", sizeName, relativePath, e.getMessage());
                }
            }
            
        } catch (IOException e) {
            logger.warn("生成缩略图失败: {}, 错误: {}", relativePath, e.getMessage());
        }
        
        return thumbnails;
    }
    
    /**
     * 删除缩略图
     * 
     * @param relativePath 原始文件相对路径
     */
    private void deleteThumbnails(String relativePath) {
        String baseName = getFileNameWithoutExtension(relativePath);
        String extension = getFileExtension(relativePath);
        
        for (String sizeName : THUMBNAIL_SIZES.keySet()) {
            try {
                String thumbnailPath = baseName + "_" + sizeName + extension;
                Path thumbnailFullPath = Paths.get(uploadBasePath, thumbnailPath);
                Files.deleteIfExists(thumbnailFullPath);
            } catch (IOException e) {
                logger.warn("删除{}缩略图失败: {}, 错误: {}", sizeName, relativePath, e.getMessage());
            }
        }
    }
    
    /**
     * 调整图片尺寸
     * 
     * @param originalImage 原始图片
     * @param targetWidth 目标宽度
     * @param targetHeight 目标高度
     * @return 调整后的图片
     */
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // 计算缩放比例，保持宽高比
        double scaleX = (double) targetWidth / originalImage.getWidth();
        double scaleY = (double) targetHeight / originalImage.getHeight();
        double scale = Math.min(scaleX, scaleY);
        
        int scaledWidth = (int) (originalImage.getWidth() * scale);
        int scaledHeight = (int) (originalImage.getHeight() * scale);
        
        BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedImage.createGraphics();
        
        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        
        return resizedImage;
    }
    
    /**
     * 获取文件扩展名
     * 
     * @param fileName 文件名
     * @return 扩展名（包含点号）
     */
    private String getFileExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }
    
    /**
     * 获取不包含扩展名的文件名
     * 
     * @param filePath 文件路径
     * @return 不包含扩展名的文件名
     */
    private String getFileNameWithoutExtension(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return "";
        }
        
        int lastDotIndex = filePath.lastIndexOf('.');
        return lastDotIndex > 0 ? filePath.substring(0, lastDotIndex) : filePath;
    }
    
    /**
     * 判断是否为图片文件
     * 
     * @param fileName 文件名
     * @return 是否为图片文件
     */
    private boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}