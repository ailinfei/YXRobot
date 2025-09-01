#!/bin/bash

# 客户管理API性能测试脚本
# 用于验证API响应时间是否满足性能要求

# 配置
BASE_URL="http://localhost:8080"
API_PREFIX="/api/admin/customers"
TEST_RESULTS_DIR="performance-test-results"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
RESULTS_FILE="${TEST_RESULTS_DIR}/customer_api_performance_${TIMESTAMP}.log"

# 性能阈值（毫秒）
CUSTOMER_LIST_THRESHOLD=2000
CUSTOMER_STATS_THRESHOLD=1000
CUSTOMER_DETAIL_THRESHOLD=1500
DEFAULT_THRESHOLD=3000

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 创建结果目录
mkdir -p ${TEST_RESULTS_DIR}

echo "========================================"
echo "客户管理API性能测试"
echo "========================================"
echo "开始时间: $(date)"
echo "基础URL: ${BASE_URL}"
echo "结果文件: ${RESULTS_FILE}"
echo "========================================"

# 记录到文件
{
    echo "========================================"
    echo "客户管理API性能测试结果"
    echo "========================================"
    echo "开始时间: $(date)"
    echo "基础URL: ${BASE_URL}"
    echo "========================================"
} > ${RESULTS_FILE}

# 测试函数
test_api() {
    local api_name=$1
    local url=$2
    local threshold=$3
    local method=${4:-GET}
    local data=${5:-""}
    
    echo -e "${BLUE}测试 ${api_name}...${NC}"
    
    # 执行多次测试取平均值
    local total_time=0
    local test_count=5
    local success_count=0
    
    for i in $(seq 1 ${test_count}); do
        if [ "$method" = "GET" ]; then
            response_time=$(curl -o /dev/null -s -w "%{time_total}" "${url}")
        else
            response_time=$(curl -o /dev/null -s -w "%{time_total}" -X ${method} -H "Content-Type: application/json" -d "${data}" "${url}")
        fi
        
        # 检查curl是否成功
        if [ $? -eq 0 ]; then
            # 转换为毫秒
            response_time_ms=$(echo "${response_time} * 1000" | bc)
            total_time=$(echo "${total_time} + ${response_time_ms}" | bc)
            success_count=$((success_count + 1))
            echo "  第${i}次测试: ${response_time_ms}ms"
        else
            echo "  第${i}次测试: 失败"
        fi
        
        # 测试间隔
        sleep 1
    done
    
    if [ ${success_count} -gt 0 ]; then
        # 计算平均响应时间
        avg_time=$(echo "scale=2; ${total_time} / ${success_count}" | bc)
        avg_time_int=$(echo "${avg_time}" | cut -d. -f1)
        
        # 判断是否通过性能测试
        if [ ${avg_time_int} -le ${threshold} ]; then
            echo -e "${GREEN}✓ ${api_name}: 平均响应时间 ${avg_time}ms (阈值: ${threshold}ms) - 通过${NC}"
            result="PASS"
        else
            echo -e "${RED}✗ ${api_name}: 平均响应时间 ${avg_time}ms (阈值: ${threshold}ms) - 超时${NC}"
            result="FAIL"
        fi
        
        # 记录到文件
        {
            echo "API: ${api_name}"
            echo "URL: ${url}"
            echo "方法: ${method}"
            echo "成功次数: ${success_count}/${test_count}"
            echo "平均响应时间: ${avg_time}ms"
            echo "性能阈值: ${threshold}ms"
            echo "测试结果: ${result}"
            echo "----------------------------------------"
        } >> ${RESULTS_FILE}
        
        return $([ "$result" = "PASS" ] && echo 0 || echo 1)
    else
        echo -e "${RED}✗ ${api_name}: 所有测试都失败了${NC}"
        {
            echo "API: ${api_name}"
            echo "URL: ${url}"
            echo "方法: ${method}"
            echo "测试结果: ALL_FAILED"
            echo "----------------------------------------"
        } >> ${RESULTS_FILE}
        return 1
    fi
}

# 测试计数器
total_tests=0
passed_tests=0

echo ""
echo "开始API性能测试..."
echo ""

# 1. 测试客户统计API
echo "1. 客户统计API测试"
test_api "客户统计" "${BASE_URL}${API_PREFIX}/stats" ${CUSTOMER_STATS_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 2. 测试客户列表API
echo "2. 客户列表API测试"
test_api "客户列表" "${BASE_URL}${API_PREFIX}?page=1&pageSize=20" ${CUSTOMER_LIST_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 3. 测试客户列表API（带搜索）
echo "3. 客户列表API测试（带搜索）"
test_api "客户列表搜索" "${BASE_URL}${API_PREFIX}?page=1&pageSize=20&keyword=test" ${CUSTOMER_LIST_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 4. 测试客户列表API（带筛选）
echo "4. 客户列表API测试（带筛选）"
test_api "客户列表筛选" "${BASE_URL}${API_PREFIX}?page=1&pageSize=20&level=vip&status=active" ${CUSTOMER_LIST_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 5. 测试客户详情API（假设存在ID为1的客户）
echo "5. 客户详情API测试"
test_api "客户详情" "${BASE_URL}${API_PREFIX}/1" ${CUSTOMER_DETAIL_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 6. 测试客户设备API
echo "6. 客户设备API测试"
test_api "客户设备" "${BASE_URL}${API_PREFIX}/1/devices" ${DEFAULT_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 7. 测试客户订单API
echo "7. 客户订单API测试"
test_api "客户订单" "${BASE_URL}${API_PREFIX}/1/orders" ${DEFAULT_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 8. 测试客户服务记录API
echo "8. 客户服务记录API测试"
test_api "客户服务记录" "${BASE_URL}${API_PREFIX}/1/service-records" ${DEFAULT_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 9. 测试筛选选项API
echo "9. 筛选选项API测试"
test_api "筛选选项" "${BASE_URL}${API_PREFIX}/filter-options" ${DEFAULT_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 10. 测试客户搜索API
echo "10. 客户搜索API测试"
test_api "客户搜索" "${BASE_URL}${API_PREFIX}/search?keyword=test&limit=10" ${DEFAULT_THRESHOLD}
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 11. 测试客户创建API（POST）
echo "11. 客户创建API测试"
create_data='{"customerName":"测试客户","phone":"13800138000","email":"test@example.com","customerLevel":"REGULAR","customerStatus":"ACTIVE"}'
test_api "客户创建" "${BASE_URL}${API_PREFIX}" ${DEFAULT_THRESHOLD} "POST" "${create_data}"
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 并发测试
echo "12. 并发性能测试"
echo -e "${BLUE}执行并发测试（10个并发请求）...${NC}"

concurrent_test() {
    local url="${BASE_URL}${API_PREFIX}/stats"
    local concurrent_count=10
    local temp_file="/tmp/concurrent_test_$$"
    
    # 启动并发请求
    for i in $(seq 1 ${concurrent_count}); do
        {
            response_time=$(curl -o /dev/null -s -w "%{time_total}" "${url}")
            response_time_ms=$(echo "${response_time} * 1000" | bc)
            echo "${response_time_ms}" >> ${temp_file}
        } &
    done
    
    # 等待所有请求完成
    wait
    
    # 计算统计信息
    if [ -f ${temp_file} ]; then
        local total=0
        local count=0
        local max=0
        local min=999999
        
        while read -r time; do
            total=$(echo "${total} + ${time}" | bc)
            count=$((count + 1))
            
            # 更新最大值和最小值
            if (( $(echo "${time} > ${max}" | bc -l) )); then
                max=${time}
            fi
            if (( $(echo "${time} < ${min}" | bc -l) )); then
                min=${time}
            fi
        done < ${temp_file}
        
        if [ ${count} -gt 0 ]; then
            local avg=$(echo "scale=2; ${total} / ${count}" | bc)
            echo "  并发请求数: ${concurrent_count}"
            echo "  成功请求数: ${count}"
            echo "  平均响应时间: ${avg}ms"
            echo "  最快响应时间: ${min}ms"
            echo "  最慢响应时间: ${max}ms"
            
            # 记录到文件
            {
                echo "并发性能测试结果:"
                echo "并发请求数: ${concurrent_count}"
                echo "成功请求数: ${count}"
                echo "平均响应时间: ${avg}ms"
                echo "最快响应时间: ${min}ms"
                echo "最慢响应时间: ${max}ms"
                echo "----------------------------------------"
            } >> ${RESULTS_FILE}
            
            # 判断并发测试是否通过
            avg_int=$(echo "${avg}" | cut -d. -f1)
            if [ ${avg_int} -le ${CUSTOMER_STATS_THRESHOLD} ]; then
                echo -e "${GREEN}✓ 并发测试: 通过${NC}"
                rm -f ${temp_file}
                return 0
            else
                echo -e "${RED}✗ 并发测试: 平均响应时间超过阈值${NC}"
                rm -f ${temp_file}
                return 1
            fi
        else
            echo -e "${RED}✗ 并发测试: 所有请求都失败了${NC}"
            rm -f ${temp_file}
            return 1
        fi
    else
        echo -e "${RED}✗ 并发测试: 无法创建临时文件${NC}"
        return 1
    fi
}

concurrent_test
if [ $? -eq 0 ]; then passed_tests=$((passed_tests + 1)); fi
total_tests=$((total_tests + 1))
echo ""

# 测试结果汇总
echo "========================================"
echo "性能测试结果汇总"
echo "========================================"
echo "总测试数: ${total_tests}"
echo "通过测试: ${passed_tests}"
echo "失败测试: $((total_tests - passed_tests))"
echo "通过率: $(echo "scale=2; ${passed_tests} * 100 / ${total_tests}" | bc)%"
echo "结束时间: $(date)"
echo "========================================"

# 记录汇总到文件
{
    echo ""
    echo "========================================"
    echo "性能测试结果汇总"
    echo "========================================"
    echo "总测试数: ${total_tests}"
    echo "通过测试: ${passed_tests}"
    echo "失败测试: $((total_tests - passed_tests))"
    echo "通过率: $(echo "scale=2; ${passed_tests} * 100 / ${total_tests}" | bc)%"
    echo "结束时间: $(date)"
    echo "========================================"
} >> ${RESULTS_FILE}

# 根据测试结果设置退出码
if [ ${passed_tests} -eq ${total_tests} ]; then
    echo -e "${GREEN}🎉 所有性能测试都通过了！${NC}"
    exit 0
else
    echo -e "${RED}⚠️  部分性能测试失败，请检查API性能。${NC}"
    echo "详细结果请查看: ${RESULTS_FILE}"
    exit 1
fi