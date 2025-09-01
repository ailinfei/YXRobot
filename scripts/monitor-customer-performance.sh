#!/bin/bash

# 客户管理模块性能监控脚本
# 用于实时监控API性能和系统资源使用情况

# 配置
BASE_URL="http://localhost:8081"
MONITOR_INTERVAL=30  # 监控间隔（秒）
LOG_DIR="logs/performance-monitor"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="${LOG_DIR}/customer_performance_monitor_${TIMESTAMP}.log"

# 性能阈值
CPU_THRESHOLD=80
MEMORY_THRESHOLD=80
DISK_THRESHOLD=90
API_RESPONSE_THRESHOLD=2000

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 创建日志目录
mkdir -p ${LOG_DIR}

echo "========================================"
echo "客户管理模块性能监控"
echo "========================================"
echo "开始时间: $(date)"
echo "监控间隔: ${MONITOR_INTERVAL}秒"
echo "日志文件: ${LOG_FILE}"
echo "========================================"

# 记录到文件
{
    echo "========================================"
    echo "客户管理模块性能监控日志"
    echo "========================================"
    echo "开始时间: $(date)"
    echo "监控间隔: ${MONITOR_INTERVAL}秒"
    echo "========================================"
} > ${LOG_FILE}

# 信号处理
cleanup() {
    echo ""
    echo -e "${YELLOW}收到停止信号，正在清理...${NC}"
    {
        echo ""
        echo "========================================"
        echo "监控结束时间: $(date)"
        echo "========================================"
    } >> ${LOG_FILE}
    exit 0
}

trap cleanup SIGINT SIGTERM

# 检查系统资源
check_system_resources() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # CPU使用率
    local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | awk -F'%' '{print $1}')
    cpu_usage=${cpu_usage%.*}  # 去掉小数部分
    
    # 内存使用率
    local memory_info=$(free | grep Mem)
    local total_mem=$(echo $memory_info | awk '{print $2}')
    local used_mem=$(echo $memory_info | awk '{print $3}')
    local memory_usage=$((used_mem * 100 / total_mem))
    
    # 磁盘使用率
    local disk_usage=$(df -h / | awk 'NR==2 {print $5}' | sed 's/%//')
    
    # 输出系统资源状态
    echo -e "${BLUE}[${timestamp}] 系统资源监控${NC}"
    
    # CPU状态
    if [ ${cpu_usage} -gt ${CPU_THRESHOLD} ]; then
        echo -e "  CPU使用率: ${RED}${cpu_usage}%${NC} (超过阈值 ${CPU_THRESHOLD}%)"
    else
        echo -e "  CPU使用率: ${GREEN}${cpu_usage}%${NC}"
    fi
    
    # 内存状态
    if [ ${memory_usage} -gt ${MEMORY_THRESHOLD} ]; then
        echo -e "  内存使用率: ${RED}${memory_usage}%${NC} (超过阈值 ${MEMORY_THRESHOLD}%)"
    else
        echo -e "  内存使用率: ${GREEN}${memory_usage}%${NC}"
    fi
    
    # 磁盘状态
    if [ ${disk_usage} -gt ${DISK_THRESHOLD} ]; then
        echo -e "  磁盘使用率: ${RED}${disk_usage}%${NC} (超过阈值 ${DISK_THRESHOLD}%)"
    else
        echo -e "  磁盘使用率: ${GREEN}${disk_usage}%${NC}"
    fi
    
    # 记录到日志文件
    {
        echo "[${timestamp}] 系统资源监控"
        echo "  CPU使用率: ${cpu_usage}%"
        echo "  内存使用率: ${memory_usage}%"
        echo "  磁盘使用率: ${disk_usage}%"
    } >> ${LOG_FILE}
    
    # 检查是否有资源告警
    if [ ${cpu_usage} -gt ${CPU_THRESHOLD} ] || [ ${memory_usage} -gt ${MEMORY_THRESHOLD} ] || [ ${disk_usage} -gt ${DISK_THRESHOLD} ]; then
        echo -e "${RED}⚠️  系统资源告警！${NC}"
        {
            echo "  [ALERT] 系统资源告警 - CPU: ${cpu_usage}%, 内存: ${memory_usage}%, 磁盘: ${disk_usage}%"
        } >> ${LOG_FILE}
    fi
}

# 检查API性能
check_api_performance() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    echo -e "${BLUE}[${timestamp}] API性能监控${NC}"
    
    # 测试主要API接口
    local apis=(
        "GET:${BASE_URL}/api/admin/customers/stats:客户统计API"
        "GET:${BASE_URL}/api/admin/customers?page=1&pageSize=10:客户列表API"
        "GET:${BASE_URL}/api/admin/customers/filter-options:筛选选项API"
    )
    
    local total_response_time=0
    local api_count=0
    local failed_apis=0
    
    for api_info in "${apis[@]}"; do
        IFS=':' read -r method url description <<< "$api_info"
        
        # 执行API请求
        local response_time=$(curl -o /dev/null -s -w "%{time_total}" -X ${method} "${url}" 2>/dev/null)
        local curl_exit_code=$?
        
        if [ ${curl_exit_code} -eq 0 ]; then
            # 转换为毫秒
            local response_time_ms=$(echo "${response_time} * 1000" | bc 2>/dev/null)
            response_time_ms=${response_time_ms%.*}  # 去掉小数部分
            
            total_response_time=$((total_response_time + response_time_ms))
            api_count=$((api_count + 1))
            
            # 检查响应时间
            if [ ${response_time_ms} -gt ${API_RESPONSE_THRESHOLD} ]; then
                echo -e "  ${description}: ${RED}${response_time_ms}ms${NC} (超过阈值 ${API_RESPONSE_THRESHOLD}ms)"
                {
                    echo "  [SLOW] ${description}: ${response_time_ms}ms"
                } >> ${LOG_FILE}
            else
                echo -e "  ${description}: ${GREEN}${response_time_ms}ms${NC}"
            fi
            
            # 记录到日志
            {
                echo "  ${description}: ${response_time_ms}ms"
            } >> ${LOG_FILE}
        else
            echo -e "  ${description}: ${RED}请求失败${NC}"
            failed_apis=$((failed_apis + 1))
            {
                echo "  [ERROR] ${description}: 请求失败"
            } >> ${LOG_FILE}
        fi
    done
    
    # 计算平均响应时间
    if [ ${api_count} -gt 0 ]; then
        local avg_response_time=$((total_response_time / api_count))
        echo -e "  平均响应时间: ${avg_response_time}ms"
        {
            echo "  平均响应时间: ${avg_response_time}ms"
        } >> ${LOG_FILE}
        
        if [ ${avg_response_time} -gt ${API_RESPONSE_THRESHOLD} ]; then
            echo -e "${RED}⚠️  API性能告警！平均响应时间过长${NC}"
            {
                echo "  [ALERT] API性能告警 - 平均响应时间: ${avg_response_time}ms"
            } >> ${LOG_FILE}
        fi
    fi
    
    # 检查失败的API
    if [ ${failed_apis} -gt 0 ]; then
        echo -e "${RED}⚠️  API可用性告警！${failed_apis}个API请求失败${NC}"
        {
            echo "  [ALERT] API可用性告警 - ${failed_apis}个API请求失败"
        } >> ${LOG_FILE}
    fi
}

# 检查数据库连接
check_database_connection() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    echo -e "${BLUE}[${timestamp}] 数据库连接监控${NC}"
    
    # 尝试连接数据库（这里需要根据实际配置调整）
    local db_check_url="${BASE_URL}/api/admin/customers/performance/health"
    local response=$(curl -s -o /dev/null -w "%{http_code}" "${db_check_url}" 2>/dev/null)
    
    if [ "$response" = "200" ]; then
        echo -e "  数据库连接: ${GREEN}正常${NC}"
        {
            echo "  数据库连接: 正常"
        } >> ${LOG_FILE}
    else
        echo -e "  数据库连接: ${RED}异常 (HTTP ${response})${NC}"
        {
            echo "  [ERROR] 数据库连接异常 - HTTP ${response}"
        } >> ${LOG_FILE}
    fi
}

# 检查日志文件大小
check_log_files() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    echo -e "${BLUE}[${timestamp}] 日志文件监控${NC}"
    
    # 检查主要日志文件
    local log_files=(
        "logs/customer/customer.log"
        "logs/customer/customer-error.log"
        "logs/customer/customer-performance.log"
    )
    
    for log_file in "${log_files[@]}"; do
        if [ -f "${log_file}" ]; then
            local file_size=$(du -h "${log_file}" | cut -f1)
            local file_size_mb=$(du -m "${log_file}" | cut -f1)
            
            echo -e "  ${log_file}: ${file_size}"
            
            # 检查文件大小是否过大
            if [ ${file_size_mb} -gt 100 ]; then
                echo -e "    ${YELLOW}⚠️  日志文件较大，建议清理${NC}"
                {
                    echo "  [WARNING] ${log_file} 文件较大: ${file_size}"
                } >> ${LOG_FILE}
            fi
        else
            echo -e "  ${log_file}: ${YELLOW}文件不存在${NC}"
        fi
    done
}

# 生成性能报告
generate_performance_report() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    
    # 获取性能报告
    local performance_report=$(curl -s "${BASE_URL}/api/admin/customers/performance/report" 2>/dev/null)
    
    if [ $? -eq 0 ] && [ -n "$performance_report" ]; then
        echo -e "${BLUE}[${timestamp}] 性能报告摘要${NC}"
        
        # 解析JSON响应（简单解析）
        local total_requests=$(echo "$performance_report" | grep -o '"totalRequests":[0-9]*' | cut -d':' -f2)
        local slow_requests=$(echo "$performance_report" | grep -o '"slowRequests":[0-9]*' | cut -d':' -f2)
        
        if [ -n "$total_requests" ] && [ -n "$slow_requests" ]; then
            echo -e "  总请求数: ${total_requests}"
            echo -e "  慢请求数: ${slow_requests}"
            
            if [ ${total_requests} -gt 0 ]; then
                local slow_rate=$((slow_requests * 100 / total_requests))
                if [ ${slow_rate} -gt 10 ]; then
                    echo -e "  慢请求率: ${RED}${slow_rate}%${NC} (超过10%)"
                else
                    echo -e "  慢请求率: ${GREEN}${slow_rate}%${NC}"
                fi
            fi
            
            {
                echo "[${timestamp}] 性能报告摘要"
                echo "  总请求数: ${total_requests}"
                echo "  慢请求数: ${slow_requests}"
                echo "  慢请求率: ${slow_rate}%"
            } >> ${LOG_FILE}
        fi
    else
        echo -e "${YELLOW}无法获取性能报告${NC}"
    fi
}

# 主监控循环
echo "开始监控循环..."
echo ""

while true; do
    # 执行各项检查
    check_system_resources
    echo ""
    
    check_api_performance
    echo ""
    
    check_database_connection
    echo ""
    
    check_log_files
    echo ""
    
    generate_performance_report
    echo ""
    
    echo "----------------------------------------"
    echo -e "${GREEN}下次检查将在 ${MONITOR_INTERVAL} 秒后进行...${NC}"
    echo "按 Ctrl+C 停止监控"
    echo ""
    
    # 等待下次检查
    sleep ${MONITOR_INTERVAL}
done