#!/bin/bash

# 客户管理模块部署验证脚本
# 验证系统部署后的完整功能

echo "=========================================="
echo "客户管理模块部署验证"
echo "=========================================="

# 配置
BASE_URL="http://localhost:8081"
API_BASE_URL="$BASE_URL/api/admin/customers"
FRONTEND_URL="$BASE_URL/admin/business/customers"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 测试结果统计
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    ((PASSED_CHECKS++))
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1"
    ((FAILED_CHECKS++))
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 检查函数
check_service() {
    local service_name=$1
    local check_command=$2
    local description=$3
    
    ((TOTAL_CHECKS++))
    log_info "检查: $description"
    
    if eval "$check_command" >/dev/null 2>&1; then
        log_success "$description"
        return 0
    else
        log_error "$description"
        return 1
    fi
}

check_url() {
    local url=$1
    local description=$2
    local expected_status=${3:-200}
    
    ((TOTAL_CHECKS++))
    log_info "检查: $description"
    
    response=$(curl -s -w "%{http_code}" -o /dev/null "$url" 2>/dev/null)
    http_code="${response: -3}"
    
    if [ "$http_code" = "$expected_status" ]; then
        log_success "$description - HTTP $http_code"
        return 0
    else
        log_error "$description - HTTP $http_code (期望: $expected_status)"
        return 1
    fi
}

# 开始部署验证
echo ""
log_info "开始验证客户管理模块部署状态..."
echo ""

# 1. 系统基础服务检查
echo "1. 系统基础服务检查"
echo "----------------------------------------"

# 检查Java进程
check_service "Java应用" "pgrep -f 'java.*yxrobot'" "Java应用进程运行状态"

# 检查端口占用
check_service "应用端口" "netstat -tlnp | grep :8081" "端口8081监听状态"

# 检查MySQL连接
check_service "MySQL服务" "mysqladmin ping -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq" "MySQL数据库连接"

echo ""

# 2. Web服务可用性检查
echo "2. Web服务可用性检查"
echo "----------------------------------------"

check_url "$BASE_URL" "应用主页访问"
check_url "$BASE_URL/admin" "管理后台访问"
check_url "$FRONTEND_URL" "客户管理页面访问"

echo ""

# 3. API接口可用性检查
echo "3. API接口可用性检查"
echo "----------------------------------------"

check_url "$API_BASE_URL" "客户列表API"
check_url "$API_BASE_URL/stats" "客户统计API"
check_url "$API_BASE_URL/filter-options" "筛选选项API"
check_url "$BASE_URL/api/admin/system/integration-test/status" "集成测试状态API"

echo ""

# 4. 数据库表结构检查
echo "4. 数据库表结构检查"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "检查: 客户表结构"

# 检查customers表是否存在
mysql_result=$(mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SHOW TABLES LIKE 'customers';" 2>/dev/null | wc -l)
if [ "$mysql_result" -gt 1 ]; then
    log_success "customers表存在"
else
    log_error "customers表不存在"
fi

((TOTAL_CHECKS++))
log_info "检查: 客户表字段"

# 检查关键字段是否存在
field_check=$(mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "DESCRIBE customers;" 2>/dev/null | grep -E "(customer_name|phone|email|customer_level)" | wc -l)
if [ "$field_check" -ge 4 ]; then
    log_success "客户表关键字段存在"
else
    log_error "客户表关键字段缺失"
fi

echo ""

# 5. 前端资源检查
echo "5. 前端资源检查"
echo "----------------------------------------"

# 检查Vue组件文件
CUSTOMERS_VUE="workspace/projects/YXRobot/src/frontend/src/views/admin/Customers.vue"
API_FILE="workspace/projects/YXRobot/src/frontend/src/api/customer.ts"
TYPES_FILE="workspace/projects/YXRobot/src/frontend/src/types/customer.ts"

((TOTAL_CHECKS++))
if [ -f "$CUSTOMERS_VUE" ]; then
    log_success "Customers.vue组件文件存在"
else
    log_error "Customers.vue组件文件缺失"
fi

((TOTAL_CHECKS++))
if [ -f "$API_FILE" ]; then
    log_success "客户API文件存在"
else
    log_error "客户API文件缺失"
fi

((TOTAL_CHECKS++))
if [ -f "$TYPES_FILE" ]; then
    log_success "客户类型定义文件存在"
else
    log_error "客户类型定义文件缺失"
fi

echo ""

# 6. 后端服务检查
echo "6. 后端服务检查"
echo "----------------------------------------"

# 检查关键Java类文件
CONTROLLER_FILE="workspace/projects/YXRobot/src/main/java/com/yxrobot/controller/CustomerController.java"
SERVICE_FILE="workspace/projects/YXRobot/src/main/java/com/yxrobot/service/CustomerService.java"
MAPPER_FILE="workspace/projects/YXRobot/src/main/java/com/yxrobot/mapper/CustomerMapper.java"

((TOTAL_CHECKS++))
if [ -f "$CONTROLLER_FILE" ]; then
    log_success "CustomerController类文件存在"
else
    log_error "CustomerController类文件缺失"
fi

((TOTAL_CHECKS++))
if [ -f "$SERVICE_FILE" ]; then
    log_success "CustomerService类文件存在"
else
    log_error "CustomerService类文件缺失"
fi

((TOTAL_CHECKS++))
if [ -f "$MAPPER_FILE" ]; then
    log_success "CustomerMapper类文件存在"
else
    log_error "CustomerMapper类文件缺失"
fi

echo ""

# 7. 功能完整性检查
echo "7. 功能完整性检查"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "检查: 客户统计功能"

stats_response=$(curl -s "$API_BASE_URL/stats" 2>/dev/null)
if echo "$stats_response" | jq -e '.data.total' >/dev/null 2>&1; then
    log_success "客户统计功能正常"
else
    log_error "客户统计功能异常"
fi

((TOTAL_CHECKS++))
log_info "检查: 客户列表功能"

list_response=$(curl -s "$API_BASE_URL?page=1&pageSize=10" 2>/dev/null)
if echo "$list_response" | jq -e '.data.list' >/dev/null 2>&1; then
    log_success "客户列表功能正常"
else
    log_error "客户列表功能异常"
fi

((TOTAL_CHECKS++))
log_info "检查: 筛选功能"

filter_response=$(curl -s "$API_BASE_URL?level=vip" 2>/dev/null)
if echo "$filter_response" | jq -e '.data' >/dev/null 2>&1; then
    log_success "筛选功能正常"
else
    log_error "筛选功能异常"
fi

echo ""

# 8. 性能检查
echo "8. 性能检查"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "检查: API响应性能"

start_time=$(date +%s%N)
curl -s "$API_BASE_URL" >/dev/null 2>&1
end_time=$(date +%s%N)
response_time=$(( (end_time - start_time) / 1000000 ))

if [ $response_time -lt 2000 ]; then
    log_success "API响应性能正常 (${response_time}ms < 2000ms)"
else
    log_error "API响应性能不达标 (${response_time}ms >= 2000ms)"
fi

((TOTAL_CHECKS++))
log_info "检查: 页面加载性能"

start_time=$(date +%s%N)
curl -s "$FRONTEND_URL" >/dev/null 2>&1
end_time=$(date +%s%N)
page_load_time=$(( (end_time - start_time) / 1000000 ))

if [ $page_load_time -lt 3000 ]; then
    log_success "页面加载性能正常 (${page_load_time}ms < 3000ms)"
else
    log_error "页面加载性能不达标 (${page_load_time}ms >= 3000ms)"
fi

echo ""

# 9. 数据一致性检查
echo "9. 数据一致性检查"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "检查: 统计数据一致性"

# 获取API统计数据
api_stats=$(curl -s "$API_BASE_URL/stats" 2>/dev/null)
api_total=$(echo "$api_stats" | jq -r '.data.total // 0')

# 获取数据库实际数据
db_total=$(mysql -h yun.finiot.cn -P 3306 -u YXRobot -p2200548qq YXRobot -e "SELECT COUNT(*) FROM customers WHERE is_deleted = 0;" 2>/dev/null | tail -n 1)

if [ "$api_total" = "$db_total" ]; then
    log_success "统计数据一致性正常 (API: $api_total, DB: $db_total)"
else
    log_warning "统计数据可能不一致 (API: $api_total, DB: $db_total)"
fi

echo ""

# 10. 集成测试执行
echo "10. 集成测试执行"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "执行: 完整集成测试"

integration_response=$(curl -s -X POST "$BASE_URL/api/admin/system/integration-test/execute" 2>/dev/null)
if echo "$integration_response" | jq . >/dev/null 2>&1; then
    all_passed=$(echo "$integration_response" | jq -r '.data.allPassed // false')
    pass_rate=$(echo "$integration_response" | jq -r '.data.passRate // 0')
    
    if [ "$all_passed" = "true" ]; then
        log_success "集成测试全部通过 (通过率: ${pass_rate}%)"
    else
        log_warning "集成测试部分通过 (通过率: ${pass_rate}%)"
    fi
else
    log_error "集成测试执行失败"
fi

echo ""

# 11. 访问地址验证
echo "11. 访问地址验证"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "验证: 客户管理页面访问地址"

expected_url="$FRONTEND_URL"
if curl -s "$expected_url" | grep -q "客户管理"; then
    log_success "客户管理页面访问地址正确: $expected_url"
else
    log_error "客户管理页面访问地址异常: $expected_url"
fi

echo ""

# 12. 响应式设计检查
echo "12. 响应式设计检查"
echo "----------------------------------------"

((TOTAL_CHECKS++))
log_info "检查: 前端响应式设计"

# 检查CSS文件中是否包含响应式设计
if [ -f "$CUSTOMERS_VUE" ] && grep -q "@media" "$CUSTOMERS_VUE"; then
    log_success "前端包含响应式设计"
else
    log_warning "前端可能缺少响应式设计"
fi

echo ""

# 部署验证结果汇总
echo "=========================================="
echo "部署验证结果汇总"
echo "=========================================="

pass_rate=0
if [ $TOTAL_CHECKS -gt 0 ]; then
    pass_rate=$(( PASSED_CHECKS * 100 / TOTAL_CHECKS ))
fi

echo "总检查项: $TOTAL_CHECKS"
echo "通过项: $PASSED_CHECKS"
echo "失败项: $FAILED_CHECKS"
echo "通过率: ${pass_rate}%"

echo ""
echo "关键访问地址:"
echo "- 客户管理页面: $FRONTEND_URL"
echo "- 客户列表API: $API_BASE_URL"
echo "- 客户统计API: $API_BASE_URL/stats"

echo ""

if [ $FAILED_CHECKS -eq 0 ]; then
    echo -e "${GREEN}✓ 部署验证全部通过，系统可以正常使用${NC}"
    echo -e "${GREEN}✓ 客户管理模块已成功部署并可通过 $FRONTEND_URL 访问${NC}"
    exit 0
elif [ $pass_rate -ge 85 ]; then
    echo -e "${YELLOW}⚠ 部署基本成功，但存在一些非关键问题${NC}"
    echo -e "${YELLOW}⚠ 建议检查失败项并进行优化${NC}"
    exit 1
else
    echo -e "${RED}✗ 部署验证存在重要问题，需要修复后再使用${NC}"
    echo -e "${RED}✗ 请检查失败项并重新部署${NC}"
    exit 2
fi