package com.yxrobot.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品创建数据传输对象
 * 用于接收产品创建请求的数据，支持文件上传
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 */
public class ProductCreateDTO {
    
    /**
     * 产品名称（必填）
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    private String name;
    
    /**
     * 产品型号（必填）
     */
    @NotBlank(message = "产品型号不能为空")
    @Size(max = 100, message = "产品型号长度不能超过100个字符")
    private String model;
    
    /**
     * 产品描述
     */
    @Size(max = 5000, message = "产品描述长度不能超过5000个字符")
    private String description;
    
    /**
     * 销售价格（必填）
     */
    @NotNull(message = "产品价格不能为空")
    @DecimalMin(value = "0.00", message = "产品价格不能小于0")
    @Digits(integer = 8, fraction = 2, message = "价格格式不正确，最多8位整数和2位小数")
    private BigDecimal price;
    
    /**
     * 产品状态（必填）
     */
    @NotBlank(message = "产品状态不能为空")
    @Pattern(regexp = "^(draft|published|archived)$", message = "产品状态必须是draft、published或archived")
    private String status;
    
    /**
     * 封面图片文件列表
     * 支持上传多个文件，但只会使用第一个作为封面
     */
    private List<MultipartFile> coverFiles;
    
    // 默认构造函数
    public ProductCreateDTO() {}
    
    // 带参构造函数
    public ProductCreateDTO(String name, String model, String description, 
                           BigDecimal price, String status) {
        this.name = name;
        this.model = model;
        this.description = description;
        this.price = price;
        this.status = status;
    }
    
    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<MultipartFile> getCoverFiles() {
        return coverFiles;
    }
    
    public void setCoverFiles(List<MultipartFile> coverFiles) {
        this.coverFiles = coverFiles;
    }
    
    /**
     * 获取第一个封面文件
     * 
     * @return 封面文件，如果没有则返回null
     */
    public MultipartFile getCoverFile() {
        return (coverFiles != null && !coverFiles.isEmpty()) ? coverFiles.get(0) : null;
    }
    
    /**
     * 检查是否有封面文件
     * 
     * @return true-有封面文件，false-没有
     */
    public boolean hasCoverFile() {
        return coverFiles != null && !coverFiles.isEmpty() && 
               coverFiles.get(0) != null && !coverFiles.get(0).isEmpty();
    }
    
    @Override
    public String toString() {
        return "ProductCreateDTO{" +
                "name='" + name + '\'' +
                ", model='" + model + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", hasCoverFile=" + hasCoverFile() +
                '}';
    }
}