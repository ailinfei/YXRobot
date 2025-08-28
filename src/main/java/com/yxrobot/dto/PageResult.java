package com.yxrobot.dto;

import java.util.List;

/**
 * 分页结果封装类
 * 用于封装分页查询的结果数据
 * 
 * @author YXRobot开发团队
 * @version 1.0
 * @since 2024-12-19
 * @param <T> 数据类型
 */
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer size;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    // 默认构造函数
    public PageResult() {}
    
    // 带参构造函数
    public PageResult(List<T> list, Long total, Integer page, Integer size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = (int) Math.ceil((double) total / size);
    }
    
    /**
     * 静态工厂方法，创建分页结果
     * @param list 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页数量
     * @param <T> 数据类型
     * @return 分页结果对象
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer size) {
        return new PageResult<>(list, total, page, size);
    }
    
    /**
     * 静态工厂方法，创建空的分页结果
     * @param page 当前页码
     * @param size 每页数量
     * @param <T> 数据类型
     * @return 空的分页结果对象
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return new PageResult<>(List.of(), 0L, page, size);
    }
    
    /**
     * 判断是否有数据
     * @return true-有数据，false-无数据
     */
    public boolean hasData() {
        return list != null && !list.isEmpty();
    }
    
    /**
     * 判断是否为第一页
     * @return true-是第一页，false-不是第一页
     */
    public boolean isFirstPage() {
        return page == 1;
    }
    
    /**
     * 判断是否为最后一页
     * @return true-是最后一页，false-不是最后一页
     */
    public boolean isLastPage() {
        return page.equals(totalPages);
    }
    
    // Getter 和 Setter 方法
    public List<T> getList() {
        return list;
    }
    
    public void setList(List<T> list) {
        this.list = list;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public Integer getSize() {
        return size;
    }
    
    public void setSize(Integer size) {
        this.size = size;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    @Override
    public String toString() {
        return "PageResult{" +
                "list=" + list +
                ", total=" + total +
                ", page=" + page +
                ", size=" + size +
                ", totalPages=" + totalPages +
                '}';
    }
}