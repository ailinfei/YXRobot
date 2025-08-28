#!/bin/bash

# 新闻管理系统部署验证脚本
# 
# @author YXRobot开发团队
# @version 1.0.0
# @since 2025-01-25

set -e  # 遇到错误立即退出

# 配置变量
BASE_URL="${BASE_URL:-http://localhost:8080}"
API_BASE="${BASE_URL}/api"
TIMEOUT=30
MAX_RETRIES=3

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# HTTP请求函数
make_request() {
    local method=$1
    local url=$2
    local data=$3
    local expected_status=${4:-200}
    
    local response
    local status_code
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            -H "Content-Type: application/json" \
            -d "$data" \
            --connect-timeout $TIMEOUT \
            "$url")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" \
            --connect-timeout $TIMEOUT \
            "$url")
    fi
    
    status_code=$(echo "$response" | tail -n1)
    response_body=$(echo "$response" | head -n -1)
    
    if [ "$status_code" -eq "$expected_status" ]; then
        echo "$response_body"
        return 0
    else
        log_error "请求失败: $method $url, 期望状态码: $expected_status, 实际状态码: $status_code"
        echo "$response_body"
        return 1
    fi
}

# 等待服务启动
wait_for_service() {
    log_info "等待服务启动..."
    
    local retries=0
    while [ $retries -lt $MAX_RETRIES ]; do
        if curl -s --connect-timeout 5 "$BASE_URL/actuator/health" > /dev/null 2>&1; then
            log_success "服务已启动"
            return 0
        fi
        
        retries=$((retries + 1))
        log_info "等待服务启动... ($retries/$MAX_RETRIES)"
        sleep 10
    done
    
    log_error "服务启动超时"
    return 1
}

# 验证数据库连接
validate_database() {
    log_info "验证数据库连接..."
    
    local response
    response=$(make_request "GET" "$API_BASE/admin/system/health")
    
    if echo "$response" | grep -q '"healthy":true'; then
        log_success "数据库连接正常"
        return 0
    else
        log_error "数据库连接异常"
        echo "$response"
        return 1
    fi
}

# 验证新闻分类API
validate_news_categories() {
    log_info "验证新闻分类API..."
    
    local response
    response=$(make_request "GET" "$API_BASE/news/categories")
    
    if echo "$response" | grep -q '"code":200'; then
        local count
        count=$(echo "$response" | jq '.data | length' 2>/dev/null || echo "0")
        if [ "$count" -gt 0 ]; then
            log_success "新闻分类API验证通过，共 $count 个分类"
            return 0
        else
            log_warning "新闻分类数据为空"
            return 1
        fi
    else
        log_error "新闻分类API验证失败"
        echo "$response"
        return 1
    fi
}

# 验证新闻标签API
validate_news_tags() {
    log_info "验证新闻标签API..."
    
    local response
    response=$(make_request "GET" "$API_BASE/news/tags")
    
    if echo "$response" | grep -q '"code":200'; then
        local count
        count=$(echo "$response" | jq '.data | length' 2>/dev/null || echo "0")
        if [ "$count" -gt 0 ]; then
            log_success "新闻标签API验证通过，共 $count 个标签"
            return 0
        else
            log_warning "新闻标签数据为空"
            return 1
        fi
    else
        log_error "新闻标签API验证失败"
        echo "$response"
        return 1
    fi
}

# 验证新闻CRUD操作
validate_news_crud() {
    log_info "验证新闻CRUD操作..."
    
    # 1. 创建新闻
    local create_data='{
        "title": "部署验证测试新闻",
        "excerpt": "这是部署验证的测试新闻摘要",
        "content": "这是部署验证的测试新闻内容，用于验证系统部署后的功能完整性。",
        "categoryId": 1,
        "author": "部署验证",
        "status": "DRAFT",
        "isFeatured": false,
        "sortOrder": 0
    }'
    
    local create_response
    create_response=$(make_request "POST" "$API_BASE/news" "$create_data")
    
    if ! echo "$create_response" | grep -q '"code":200'; then
        log_error "创建新闻失败"
        echo "$create_response"
        return 1
    fi
    
    local news_id
    news_id=$(echo "$create_response" | jq -r '.data.id' 2>/dev/null)
    
    if [ "$news_id" = "null" ] || [ -z "$news_id" ]; then
        log_error "无法获取创建的新闻ID"
        return 1
    fi
    
    log_success "新闻创建成功，ID: $news_id"
    
    # 2. 获取新闻详情
    local get_response
    get_response=$(make_request "GET" "$API_BASE/news/$news_id")
    
    if ! echo "$get_response" | grep -q '"code":200'; then
        log_error "获取新闻详情失败"
        echo "$get_response"
        return 1
    fi
    
    log_success "新闻详情获取成功"
    
    # 3. 更新新闻
    local update_data='{
        "title": "更新后的部署验证测试新闻",
        "excerpt": "更新后的测试新闻摘要",
        "content": "更新后的测试新闻内容",
        "categoryId": 1,
        "author": "部署验证",
        "status": "DRAFT",
        "isFeatured": true,
        "sortOrder": 10
    }'
    
    local update_response
    update_response=$(make_request "PUT" "$API_BASE/news/$news_id" "$update_data")
    
    if ! echo "$update_response" | grep -q '"code":200'; then
        log_error "更新新闻失败"
        echo "$update_response"
        return 1
    fi
    
    log_success "新闻更新成功"
    
    # 4. 发布新闻
    local publish_response
    publish_response=$(make_request "POST" "$API_BASE/news/$news_id/publish")
    
    if ! echo "$publish_response" | grep -q '"code":200'; then
        log_error "发布新闻失败"
        echo "$publish_response"
        return 1
    fi
    
    log_success "新闻发布成功"
    
    # 5. 下线新闻
    local offline_response
    offline_response=$(make_request "POST" "$API_BASE/news/$news_id/offline")
    
    if ! echo "$offline_response" | grep -q '"code":200'; then
        log_error "下线新闻失败"
        echo "$offline_response"
        return 1
    fi
    
    log_success "新闻下线成功"
    
    # 6. 删除新闻
    local delete_response
    delete_response=$(make_request "DELETE" "$API_BASE/news/$news_id")
    
    if ! echo "$delete_response" | grep -q '"code":200'; then
        log_error "删除新闻失败"
        echo "$delete_response"
        return 1
    fi
    
    log_success "新闻删除成功"
    
    # 7. 验证新闻已删除
    local verify_response
    verify_response=$(make_request "GET" "$API_BASE/news/$news_id" "" 404)
    
    if echo "$verify_response" | grep -q '"code":404'; then
        log_success "新闻删除验证通过"
        return 0
    else
        log_error "新闻删除验证失败"
        echo "$verify_response"
        return 1
    fi
}

# 验证新闻列表和搜索
validate_news_list_and_search() {
    log_info "验证新闻列表和搜索功能..."
    
    # 1. 获取新闻列表
    local list_response
    list_response=$(make_request "GET" "$API_BASE/news?page=1&pageSize=10")
    
    if ! echo "$list_response" | grep -q '"code":200'; then
        log_error "获取新闻列表失败"
        echo "$list_response"
        return 1
    fi
    
    log_success "新闻列表获取成功"
    
    # 2. 搜索新闻
    local search_response
    search_response=$(make_request "GET" "$API_BASE/news?keyword=测试&page=1&pageSize=10")
    
    if ! echo "$search_response" | grep -q '"code":200'; then
        log_error "搜索新闻失败"
        echo "$search_response"
        return 1
    fi
    
    log_success "新闻搜索功能正常"
    
    # 3. 按分类筛选
    local filter_response
    filter_response=$(make_request "GET" "$API_BASE/news?categoryId=1&page=1&pageSize=10")
    
    if ! echo "$filter_response" | grep -q '"code":200'; then
        log_error "按分类筛选失败"
        echo "$filter_response"
        return 1
    fi
    
    log_success "新闻分类筛选功能正常"
    
    return 0
}

# 验证新闻统计
validate_news_stats() {
    log_info "验证新闻统计功能..."
    
    local stats_response
    stats_response=$(make_request "GET" "$API_BASE/news/stats")
    
    if echo "$stats_response" | grep -q '"code":200'; then
        if echo "$stats_response" | grep -q '"totalNews"'; then
            log_success "新闻统计功能正常"
            return 0
        else
            log_error "新闻统计数据格式异常"
            echo "$stats_response"
            return 1
        fi
    else
        log_error "获取新闻统计失败"
        echo "$stats_response"
        return 1
    fi
}

# 验证异常处理
validate_error_handling() {
    log_info "验证异常处理..."
    
    # 1. 获取不存在的新闻
    local not_found_response
    not_found_response=$(make_request "GET" "$API_BASE/news/99999" "" 404)
    
    if echo "$not_found_response" | grep -q '"code":404'; then
        log_success "404错误处理正常"
    else
        log_error "404错误处理异常"
        echo "$not_found_response"
        return 1
    fi
    
    # 2. 创建无效新闻
    local invalid_data='{"title": ""}'
    local invalid_response
    invalid_response=$(make_request "POST" "$API_BASE/news" "$invalid_data" 400)
    
    if echo "$invalid_response" | grep -q '"code":400'; then
        log_success "400错误处理正常"
    else
        log_error "400错误处理异常"
        echo "$invalid_response"
        return 1
    fi
    
    return 0
}

# 验证性能指标
validate_performance() {
    log_info "验证性能指标..."
    
    local start_time
    start_time=$(date +%s%3N)
    
    # 执行一系列API调用
    make_request "GET" "$API_BASE/news/categories" > /dev/null
    make_request "GET" "$API_BASE/news/tags" > /dev/null
    make_request "GET" "$API_BASE/news?page=1&pageSize=10" > /dev/null
    make_request "GET" "$API_BASE/news/stats" > /dev/null
    
    local end_time
    end_time=$(date +%s%3N)
    
    local duration
    duration=$((end_time - start_time))
    
    if [ $duration -lt 5000 ]; then  # 5秒内完成
        log_success "性能测试通过，耗时: ${duration}ms"
        return 0
    else
        log_warning "性能测试超时，耗时: ${duration}ms"
        return 1
    fi
}

# 生成验证报告
generate_report() {
    local total_tests=$1
    local passed_tests=$2
    local failed_tests=$3
    
    echo ""
    echo "=================================="
    echo "       部署验证报告"
    echo "=================================="
    echo "测试时间: $(date)"
    echo "服务地址: $BASE_URL"
    echo "总测试数: $total_tests"
    echo "通过测试: $passed_tests"
    echo "失败测试: $failed_tests"
    echo "成功率: $(( passed_tests * 100 / total_tests ))%"
    echo "=================================="
    
    if [ $failed_tests -eq 0 ]; then
        log_success "所有验证测试通过！系统部署成功。"
        return 0
    else
        log_error "有 $failed_tests 个测试失败，请检查系统配置。"
        return 1
    fi
}

# 主函数
main() {
    echo "开始新闻管理系统部署验证..."
    echo "服务地址: $BASE_URL"
    echo ""
    
    local total_tests=0
    local passed_tests=0
    local failed_tests=0
    
    # 测试列表
    local tests=(
        "wait_for_service:等待服务启动"
        "validate_database:验证数据库连接"
        "validate_news_categories:验证新闻分类API"
        "validate_news_tags:验证新闻标签API"
        "validate_news_crud:验证新闻CRUD操作"
        "validate_news_list_and_search:验证新闻列表和搜索"
        "validate_news_stats:验证新闻统计"
        "validate_error_handling:验证异常处理"
        "validate_performance:验证性能指标"
    )
    
    # 执行测试
    for test in "${tests[@]}"; do
        local test_func="${test%%:*}"
        local test_name="${test##*:}"
        
        total_tests=$((total_tests + 1))
        
        echo ""
        log_info "执行测试: $test_name"
        
        if $test_func; then
            passed_tests=$((passed_tests + 1))
        else
            failed_tests=$((failed_tests + 1))
        fi
    done
    
    # 生成报告
    generate_report $total_tests $passed_tests $failed_tests
}

# 检查依赖
check_dependencies() {
    local missing_deps=()
    
    if ! command -v curl &> /dev/null; then
        missing_deps+=("curl")
    fi
    
    if ! command -v jq &> /dev/null; then
        log_warning "jq未安装，某些功能可能受限"
    fi
    
    if [ ${#missing_deps[@]} -gt 0 ]; then
        log_error "缺少依赖: ${missing_deps[*]}"
        exit 1
    fi
}

# 脚本入口
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
    check_dependencies
    main "$@"
fi