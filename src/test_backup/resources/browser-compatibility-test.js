/**
 * 浏览器兼容性测试配置
 * 用于测试新闻管理系统在不同浏览器中的兼容性
 * 
 * @author YXRobot开发团队
 * @version 1.0.0
 * @since 2025-01-25
 */

// 测试配置
const testConfig = {
    // 测试URL
    baseUrl: 'http://localhost:8080',
    
    // 支持的浏览器列表
    browsers: [
        {
            name: 'Chrome',
            version: '>=90',
            userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36'
        },
        {
            name: 'Firefox',
            version: '>=88',
            userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0'
        },
        {
            name: 'Safari',
            version: '>=14',
            userAgent: 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15'
        },
        {
            name: 'Edge',
            version: '>=90',
            userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36 Edg/90.0.818.66'
        }
    ],
    
    // 移动端浏览器
    mobileBrowsers: [
        {
            name: 'Chrome Mobile',
            userAgent: 'Mozilla/5.0 (Linux; Android 11; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.210 Mobile Safari/537.36'
        },
        {
            name: 'Safari Mobile',
            userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Mobile/15E148 Safari/604.1'
        }
    ],
    
    // 测试用例
    testCases: [
        {
            name: '新闻管理页面加载',
            url: '/admin/content/news',
            selectors: [
                '.news-management',
                '.search-form',
                '.news-table',
                '.pagination'
            ],
            actions: [
                'checkPageLoad',
                'checkResponsiveLayout',
                'checkInteractiveElements'
            ]
        },
        {
            name: '新闻创建页面',
            url: '/admin/content/news/create',
            selectors: [
                '.news-editor',
                '.news-form',
                '.rich-editor',
                '.form-actions'
            ],
            actions: [
                'checkFormValidation',
                'checkRichEditor',
                'checkFileUpload'
            ]
        },
        {
            name: '新闻详情页面',
            url: '/admin/content/news/detail/1',
            selectors: [
                '.news-detail',
                '.news-content',
                '.news-meta',
                '.news-actions'
            ],
            actions: [
                'checkContentDisplay',
                'checkImageLoading',
                'checkSocialSharing'
            ]
        }
    ]
};

// 兼容性检查函数
const compatibilityChecks = {
    
    /**
     * 检查CSS特性支持
     */
    checkCSSFeatures: function() {
        const features = {
            flexbox: CSS.supports('display', 'flex'),
            grid: CSS.supports('display', 'grid'),
            customProperties: CSS.supports('--custom-property', 'value'),
            transforms: CSS.supports('transform', 'translateX(0)'),
            transitions: CSS.supports('transition', 'all 0.3s ease'),
            borderRadius: CSS.supports('border-radius', '5px'),
            boxShadow: CSS.supports('box-shadow', '0 2px 4px rgba(0,0,0,0.1)'),
            gradients: CSS.supports('background', 'linear-gradient(to right, #000, #fff)')
        };
        
        console.log('CSS特性支持情况:', features);
        return features;
    },
    
    /**
     * 检查JavaScript特性支持
     */
    checkJSFeatures: function() {
        const features = {
            es6Classes: typeof class {} === 'function',
            arrowFunctions: (() => true)(),
            promises: typeof Promise !== 'undefined',
            asyncAwait: (async () => {})().constructor.name === 'AsyncFunction',
            fetch: typeof fetch !== 'undefined',
            localStorage: typeof localStorage !== 'undefined',
            sessionStorage: typeof sessionStorage !== 'undefined',
            webWorkers: typeof Worker !== 'undefined',
            websockets: typeof WebSocket !== 'undefined'
        };
        
        console.log('JavaScript特性支持情况:', features);
        return features;
    },
    
    /**
     * 检查HTML5特性支持
     */
    checkHTML5Features: function() {
        const features = {
            canvas: !!document.createElement('canvas').getContext,
            video: !!document.createElement('video').canPlayType,
            audio: !!document.createElement('audio').canPlayType,
            localStorage: typeof localStorage !== 'undefined',
            sessionStorage: typeof sessionStorage !== 'undefined',
            webWorkers: typeof Worker !== 'undefined',
            applicationCache: typeof applicationCache !== 'undefined',
            geolocation: !!navigator.geolocation,
            dragAndDrop: 'draggable' in document.createElement('div'),
            fileAPI: typeof FileReader !== 'undefined'
        };
        
        console.log('HTML5特性支持情况:', features);
        return features;
    },
    
    /**
     * 检查响应式设计支持
     */
    checkResponsiveSupport: function() {
        const viewport = {
            width: window.innerWidth,
            height: window.innerHeight,
            devicePixelRatio: window.devicePixelRatio || 1,
            orientation: screen.orientation ? screen.orientation.type : 'unknown'
        };
        
        const mediaQueries = {
            mobile: window.matchMedia('(max-width: 768px)').matches,
            tablet: window.matchMedia('(min-width: 769px) and (max-width: 1024px)').matches,
            desktop: window.matchMedia('(min-width: 1025px)').matches,
            retina: window.matchMedia('(-webkit-min-device-pixel-ratio: 2)').matches
        };
        
        console.log('视口信息:', viewport);
        console.log('媒体查询匹配:', mediaQueries);
        
        return { viewport, mediaQueries };
    }
};

// 测试执行器
const testRunner = {
    
    /**
     * 运行所有兼容性测试
     */
    runAllTests: async function() {
        console.log('开始浏览器兼容性测试...');
        
        const results = {
            browser: this.getBrowserInfo(),
            cssFeatures: compatibilityChecks.checkCSSFeatures(),
            jsFeatures: compatibilityChecks.checkJSFeatures(),
            html5Features: compatibilityChecks.checkHTML5Features(),
            responsive: compatibilityChecks.checkResponsiveSupport(),
            pageTests: []
        };
        
        // 运行页面测试
        for (const testCase of testConfig.testCases) {
            try {
                const pageResult = await this.runPageTest(testCase);
                results.pageTests.push(pageResult);
            } catch (error) {
                console.error(`页面测试失败: ${testCase.name}`, error);
                results.pageTests.push({
                    name: testCase.name,
                    success: false,
                    error: error.message
                });
            }
        }
        
        // 生成测试报告
        this.generateReport(results);
        
        return results;
    },
    
    /**
     * 获取浏览器信息
     */
    getBrowserInfo: function() {
        const ua = navigator.userAgent;
        const browser = {
            userAgent: ua,
            name: 'Unknown',
            version: 'Unknown',
            engine: 'Unknown'
        };
        
        // 检测浏览器类型
        if (ua.includes('Chrome') && !ua.includes('Edg')) {
            browser.name = 'Chrome';
            browser.version = ua.match(/Chrome\/(\d+)/)?.[1] || 'Unknown';
            browser.engine = 'Blink';
        } else if (ua.includes('Firefox')) {
            browser.name = 'Firefox';
            browser.version = ua.match(/Firefox\/(\d+)/)?.[1] || 'Unknown';
            browser.engine = 'Gecko';
        } else if (ua.includes('Safari') && !ua.includes('Chrome')) {
            browser.name = 'Safari';
            browser.version = ua.match(/Version\/(\d+)/)?.[1] || 'Unknown';
            browser.engine = 'WebKit';
        } else if (ua.includes('Edg')) {
            browser.name = 'Edge';
            browser.version = ua.match(/Edg\/(\d+)/)?.[1] || 'Unknown';
            browser.engine = 'Blink';
        }
        
        return browser;
    },
    
    /**
     * 运行单个页面测试
     */
    runPageTest: async function(testCase) {
        console.log(`测试页面: ${testCase.name}`);
        
        const result = {
            name: testCase.name,
            url: testCase.url,
            success: true,
            checks: [],
            performance: {},
            errors: []
        };
        
        try {
            // 记录性能指标
            const startTime = performance.now();
            
            // 检查页面元素
            for (const selector of testCase.selectors) {
                const element = document.querySelector(selector);
                const check = {
                    selector: selector,
                    found: !!element,
                    visible: element ? this.isElementVisible(element) : false
                };
                result.checks.push(check);
                
                if (!check.found || !check.visible) {
                    result.success = false;
                    result.errors.push(`元素未找到或不可见: ${selector}`);
                }
            }
            
            // 执行特定动作
            for (const action of testCase.actions) {
                try {
                    await this.executeAction(action);
                } catch (error) {
                    result.success = false;
                    result.errors.push(`动作执行失败: ${action} - ${error.message}`);
                }
            }
            
            const endTime = performance.now();
            result.performance.loadTime = endTime - startTime;
            
        } catch (error) {
            result.success = false;
            result.errors.push(error.message);
        }
        
        return result;
    },
    
    /**
     * 检查元素是否可见
     */
    isElementVisible: function(element) {
        const rect = element.getBoundingClientRect();
        const style = window.getComputedStyle(element);
        
        return rect.width > 0 && 
               rect.height > 0 && 
               style.visibility !== 'hidden' && 
               style.display !== 'none' &&
               style.opacity !== '0';
    },
    
    /**
     * 执行测试动作
     */
    executeAction: async function(action) {
        switch (action) {
            case 'checkPageLoad':
                if (document.readyState !== 'complete') {
                    throw new Error('页面未完全加载');
                }
                break;
                
            case 'checkResponsiveLayout':
                // 检查响应式布局
                const viewport = window.innerWidth;
                if (viewport < 768) {
                    // 移动端检查
                    const mobileElements = document.querySelectorAll('.mobile-hidden');
                    mobileElements.forEach(el => {
                        if (this.isElementVisible(el)) {
                            throw new Error('移动端应隐藏的元素仍然可见');
                        }
                    });
                }
                break;
                
            case 'checkInteractiveElements':
                // 检查交互元素
                const buttons = document.querySelectorAll('button, .btn');
                buttons.forEach(btn => {
                    if (btn.disabled && !btn.classList.contains('disabled')) {
                        throw new Error('按钮状态不一致');
                    }
                });
                break;
                
            case 'checkFormValidation':
                // 检查表单验证
                const forms = document.querySelectorAll('form');
                forms.forEach(form => {
                    const requiredFields = form.querySelectorAll('[required]');
                    if (requiredFields.length === 0) {
                        throw new Error('表单缺少必填字段验证');
                    }
                });
                break;
                
            case 'checkRichEditor':
                // 检查富文本编辑器
                const editor = document.querySelector('.rich-editor, .ql-editor');
                if (!editor) {
                    throw new Error('富文本编辑器未找到');
                }
                break;
                
            case 'checkFileUpload':
                // 检查文件上传
                const fileInputs = document.querySelectorAll('input[type="file"]');
                if (fileInputs.length > 0 && !window.FileReader) {
                    throw new Error('浏览器不支持文件上传');
                }
                break;
                
            case 'checkContentDisplay':
                // 检查内容显示
                const content = document.querySelector('.news-content');
                if (content && content.innerHTML.trim() === '') {
                    throw new Error('新闻内容为空');
                }
                break;
                
            case 'checkImageLoading':
                // 检查图片加载
                const images = document.querySelectorAll('img');
                for (const img of images) {
                    if (!img.complete || img.naturalHeight === 0) {
                        throw new Error('图片加载失败');
                    }
                }
                break;
                
            case 'checkSocialSharing':
                // 检查社交分享
                const shareButtons = document.querySelectorAll('.share-btn, [data-share]');
                if (shareButtons.length > 0 && !navigator.share && !window.open) {
                    throw new Error('浏览器不支持分享功能');
                }
                break;
                
            default:
                console.warn(`未知的测试动作: ${action}`);
        }
    },
    
    /**
     * 生成测试报告
     */
    generateReport: function(results) {
        console.log('=== 浏览器兼容性测试报告 ===');
        console.log('浏览器信息:', results.browser);
        console.log('CSS特性支持:', results.cssFeatures);
        console.log('JavaScript特性支持:', results.jsFeatures);
        console.log('HTML5特性支持:', results.html5Features);
        console.log('响应式支持:', results.responsive);
        
        console.log('\n页面测试结果:');
        results.pageTests.forEach(test => {
            console.log(`- ${test.name}: ${test.success ? '✓ 通过' : '✗ 失败'}`);
            if (!test.success) {
                console.log(`  错误: ${test.errors.join(', ')}`);
            }
            if (test.performance.loadTime) {
                console.log(`  加载时间: ${test.performance.loadTime.toFixed(2)}ms`);
            }
        });
        
        // 计算总体兼容性评分
        const totalTests = results.pageTests.length;
        const passedTests = results.pageTests.filter(test => test.success).length;
        const compatibilityScore = totalTests > 0 ? (passedTests / totalTests * 100).toFixed(1) : 0;
        
        console.log(`\n兼容性评分: ${compatibilityScore}% (${passedTests}/${totalTests})`);
        
        // 保存报告到localStorage
        try {
            localStorage.setItem('browserCompatibilityReport', JSON.stringify(results));
            console.log('测试报告已保存到localStorage');
        } catch (error) {
            console.warn('无法保存测试报告:', error);
        }
    }
};

// 自动运行测试（如果页面已加载）
if (document.readyState === 'complete') {
    testRunner.runAllTests();
} else {
    window.addEventListener('load', () => {
        setTimeout(() => testRunner.runAllTests(), 1000);
    });
}

// 导出测试工具
window.BrowserCompatibilityTest = {
    config: testConfig,
    checks: compatibilityChecks,
    runner: testRunner
};