#!/bin/bash

# 客户管理前端集成验证脚本
# 验证前端页面功能和后端API集成

echo "=========================================="
echo "客户管理前端集成验证"
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
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    ((PASSED_TESTS++))
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1"
    ((FAILED_TESTS++))
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

# 测试函数
test_api_endpoint() {
    local endpoint=$1
    local description=$2
    local expected_status=${3:-200}
    
    ((TOTAL_TESTS++))
    log_info "测试: $description"
    
    response=$(curl -s -w "%{http_code}" -o /tmp/api_response.json "$endpoint" 2>/dev/null)
    http_code="${response: -3}"
    
    if [ "$http_code" = "$expected_status" ]; then
        log_success "$description - HTTP $http_code"
        return 0
    else
        log_error "$description - HTTP $http_code (期望: $expected_status)"
        return 1
    fi
}

test_api_response_format() {
    local endpoint=$1
    local description=$2
    
    ((TOTAL_TESTS++))
    log_info "测试: $description"
    
    response=$(curl -s "$endpoint" 2>/dev/null)
    
    # 检查是否为有效JSON
    if echo "$response" | jq . >/dev/null 2>&1; then
        # 检查响应格式
        code=$(echo "$response" | jq -r '.code // empty')
        message=$(echo "$response" | jq -r '.message // empty')
        data=$(echo "$response" | jq -r '.data // empty')
        
        if [ -n "$code" ] && [ -n "$message" ]; then
            log_success "$description - 响应格式正确"
            return 0
        else
            log_error "$description - 响应格式不正确，缺少必要字段"
            return 1
        fi
    else
        log_error "$description - 响应不是有效JSON"
        return 1
    fi
}

# 开始测试
echo ""
log_info "开始验证客户管理前端集成..."
echo ""

# 1. 测试后端API可用性
echo "1. 后端API可用性测试"
echo "----------------------------------------"

test_api_endpoint "$API_BASE_URL" "客户列表API"
test_api_endpoint "$API_BASE_URL/stats" "客户统计API"
test_api_endpoint "$API_BASE_URL/filter-options" "筛选选项API"

echo ""

# 2. 测试API响应格式
echo "2. API响应格式测试"
echo "----------------------------------------"

test_api_response_format "$API_BASE_URL" "客户列表响应格式"
test_api_response_format "$API_BASE_URL/stats" "客户统计响应格式"

echo ""

# 3. 测试前端页面可访问性
echo "3. 前端页面可访问性测试"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: 前端页面访问"

frontend_response=$(curl -s -w "%{http_code}" -o /tmp/frontend_response.html "$FRONTEND_URL" 2>/dev/null)
frontend_http_code="${frontend_response: -3}"

if [ "$frontend_http_code" = "200" ]; then
    log_success "前端页面访问 - HTTP $frontend_http_code"
else
    log_error "前端页面访问 - HTTP $frontend_http_code"
fi

echo ""

# 4. 测试前端页面内容
echo "4. 前端页面内容验证"
echo "----------------------------------------"

if [ -f "/tmp/frontend_response.html" ]; then
    ((TOTAL_TESTS++))
    log_info "测试: 前端页面内容"
    
    # 检查关键元素
    if grep -q "客户管理" /tmp/frontend_response.html; then
        log_success "页面包含客户管理标题"
    else
        log_error "页面缺少客户管理标题"
    fi
    
    ((TOTAL_TESTS++))
    if grep -q "新增客户" /tmp/frontend_response.html; then
        log_success "页面包含新增客户按钮"
    else
        log_error "页面缺少新增客户按钮"
    fi
fi

echo ""

# 5. 测试API数据结构
echo "5. API数据结构验证"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: 客户列表数据结构"

customers_response=$(curl -s "$API_BASE_URL" 2>/dev/null)
if echo "$customers_response" | jq . >/dev/null 2>&1; then
    # 检查数据结构
    has_list=$(echo "$customers_response" | jq -r '.data.list // empty')
    has_total=$(echo "$customers_response" | jq -r '.data.total // empty')
    has_page=$(echo "$customers_response" | jq -r '.data.page // empty')
    
    if [ -n "$has_list" ] && [ -n "$has_total" ]; then
        log_success "客户列表数据结构正确"
    else
        log_error "客户列表数据结构不完整"
    fi
else
    log_error "客户列表API响应格式错误"
fi

((TOTAL_TESTS++))
log_info "测试: 客户统计数据结构"

stats_response=$(curl -s "$API_BASE_URL/stats" 2>/dev/null)
if echo "$stats_response" | jq . >/dev/null 2>&1; then
    # 检查统计数据字段
    has_total=$(echo "$stats_response" | jq -r '.data.total // empty')
    has_regular=$(echo "$stats_response" | jq -r '.data.regular // empty')
    has_vip=$(echo "$stats_response" | jq -r '.data.vip // empty')
    has_premium=$(echo "$stats_response" | jq -r '.data.premium // empty')
    has_active_devices=$(echo "$stats_response" | jq -r '.data.activeDevices // empty')
    has_total_revenue=$(echo "$stats_response" | jq -r '.data.totalRevenue // empty')
    
    if [ -n "$has_total" ] && [ -n "$has_regular" ] && [ -n "$has_vip" ] && [ -n "$has_premium" ] && [ -n "$has_active_devices" ] && [ -n "$has_total_revenue" ]; then
        log_success "客户统计数据结构正确"
    else
        log_error "客户统计数据结构不完整"
    fi
else
    log_error "客户统计API响应格式错误"
fi

echo ""

# 6. 测试API性能
echo "6. API性能测试"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: API响应时间"

start_time=$(date +%s%N)
curl -s "$API_BASE_URL" >/dev/null 2>&1
end_time=$(date +%s%N)
response_time=$(( (end_time - start_time) / 1000000 )) # 转换为毫秒

if [ $response_time -lt 2000 ]; then
    log_success "API响应时间: ${response_time}ms (< 2000ms)"
else
    log_error "API响应时间: ${response_time}ms (>= 2000ms)"
fi

echo ""

# 7. 执行后端集成测试
echo "7. 后端集成测试"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: 后端系统集成测试"

integration_test_response=$(curl -s -X POST "$BASE_URL/api/admin/system/integration-test/execute" 2>/dev/null)
if echo "$integration_test_response" | jq . >/dev/null 2>&1; then
    test_code=$(echo "$integration_test_response" | jq -r '.code // empty')
    all_passed=$(echo "$integration_test_response" | jq -r '.data.allPassed // false')
    pass_rate=$(echo "$integration_test_response" | jq -r '.data.passRate // 0')
    
    if [ "$test_code" = "200" ] && [ "$all_passed" = "true" ]; then
        log_success "后端集成测试全部通过 (通过率: ${pass_rate}%)"
    elif [ "$test_code" = "200" ]; then
        log_warning "后端集成测试部分通过 (通过率: ${pass_rate}%)"
    else
        log_error "后端集成测试执行失败"
    fi
else
    log_error "后端集成测试API调用失败"
fi

echo ""

# 8. 验证前端Vue组件
echo "8. 前端Vue组件验证"
echo "----------------------------------------"

CUSTOMERS_VUE_PATH="workspace/projects/YXRobot/src/frontend/src/views/admin/Customers.vue"

if [ -f "$CUSTOMERS_VUE_PATH" ]; then
    ((TOTAL_TESTS++))
    log_info "测试: Customers.vue组件存在"
    log_success "Customers.vue组件文件存在"
    
    ((TOTAL_TESTS++))
    log_info "测试: Vue组件API集成"
    
    # 检查是否使用了真实API调用
    if grep -q "customerApi" "$CUSTOMERS_VUE_PATH"; then
        log_success "Vue组件包含API调用"
    else
        log_error "Vue组件缺少API调用"
    fi
    
    ((TOTAL_TESTS++))
    # 检查是否移除了硬编码数据
    if grep -q "mockCustomers" "$CUSTOMERS_VUE_PATH"; then
        log_warning "Vue组件仍包含模拟数据引用"
    else
        log_success "Vue组件已移除硬编码数据"
    fi
else
    ((TOTAL_TESTS++))
    log_error "Customers.vue组件文件不存在"
fi

echo ""

# 9. 验证路由配置
echo "9. 前端路由验证"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: 前端路由配置"

# 检查路由是否正确配置
if curl -s "$FRONTEND_URL" | grep -q "客户管理"; then
    log_success "前端路由配置正确"
else
    log_error "前端路由配置可能有问题"
fi

echo ""

# 10. 数据库连接验证
echo "10. 数据库连接验证"
echo "----------------------------------------"

((TOTAL_TESTS++))
log_info "测试: 数据库连接状态"

health_check_response=$(curl -s "$BASE_URL/api/admin/system/integration-test/health-check" 2>/dev/null)
if echo "$health_check_response" | jq . >/dev/null 2>&1; then
    db_status=$(echo "$health_check_response" | jq -r '.data.database // empty')
    
    if [ "$db_status" = "connected" ]; then
        log_success "数据库连接正常"
    else
        log_error "数据库连接异常"
    fi
else
    log_error "数据库健康检查API调用失败"
fi

echo ""

# 测试结果汇总
echo "=========================================="
echo "测试结果汇总"
echo "=========================================="

pass_rate=0
if [ $TOTAL_TESTS -gt 0 ]; then
    pass_rate=$(( PASSED_TESTS * 100 / TOTAL_TESTS ))
fi

echo "总测试数: $TOTAL_TESTS"
echo "通过数: $PASSED_TESTS"
echo "失败数: $FAILED_TESTS"
echo "通过率: ${pass_rate}%"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}✓ 所有测试通过，系统集成正常${NC}"
    exit 0
elif [ $pass_rate -ge 80 ]; then
    echo -e "${YELLOW}⚠ 大部分测试通过，但存在一些问题${NC}"
    exit 1
else
    echo -e "${RED}✗ 多项测试失败，系统集成存在问题${NC}"
    exit 2
fi