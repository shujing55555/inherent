import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

/** Vite 配置：开发时 /api、/pictures、/uploads 代理到后端 8080 */
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8080', changeOrigin: true },
      '/pictures': { target: 'http://localhost:8080', changeOrigin: true },
      '/uploads': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
})
