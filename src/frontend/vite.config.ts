import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  base: '/',
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    host: true,
    open: true,
    cors: true,
    // 配置API代理，用于前后端联调
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      }
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    minify: 'esbuild',
    target: 'es2015',
    rollupOptions: {
      output: {
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]',
        manualChunks: {
          // 将Vue相关库打包到一个chunk
          vue: ['vue', 'vue-router', 'pinia'],
          // 将Element Plus打包到一个chunk
          'element-plus': ['element-plus', '@element-plus/icons-vue'],
          // 将图表库打包到一个chunk
          charts: ['echarts', 'vue-echarts'],
          // 将工具库打包到一个chunk
          utils: ['axios', 'vuedraggable']
        }
      }
    },
    // 启用gzip压缩
    reportCompressedSize: true,
    // 设置chunk大小警告限制
    chunkSizeWarningLimit: 1000
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  }
})
