import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import 'bootstrap/dist/css/bootstrap.css'

// 配置axios
axios.defaults.baseURL = '/api'
axios.defaults.timeout = 10000

// 创建应用实例
const app = createApp(App)

// 使用插件
app.use(createPinia())
app.use(router)

// 全局属性
app.config.globalProperties.$axios = axios

// 挂载应用
app.mount('#app')