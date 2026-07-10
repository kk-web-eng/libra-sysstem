import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

// 创建 Vue 应用：App.vue 是整个前端的根组件。
const app = createApp(App)
// 注册 Pinia：用于保存管理员账号、姓名和角色等共享状态。
app.use(createPinia())
// 注册路由：让不同 URL 显示不同页面，并执行登录与权限守卫。
app.use(router)
// 注册 Element Plus：表格、表单、按钮和弹窗来自这个组件库。
app.use(ElementPlus)

// 全局注册图标后，模板中可以直接使用 <Reading />、<User /> 等组件。
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 挂载到 index.html 的 #app 元素，前端从这里开始运行。
app.mount('#app')
