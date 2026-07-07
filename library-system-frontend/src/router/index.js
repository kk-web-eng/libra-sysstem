import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/client',
    name: 'Client',
    component: () => import('@/views/Client.vue'),
    meta: { title: '馆藏查询' }
  },
  {
    path: '/user/login',
    name: 'UserLogin',
    component: () => import('@/views/UserLogin.vue'),
    meta: { public: true, title: '用户登录' }
  },
  {
    path: '/user',
    name: 'UserHome',
    component: () => import('@/views/UserHome.vue'),
    meta: { user: true, title: '馆藏借阅' }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '数据总览' } },
      { path: 'books', name: 'Books', component: () => import('@/views/Books.vue'), meta: { title: '图书管理' } },
      { path: 'readers', name: 'Readers', component: () => import('@/views/Readers.vue'), meta: { title: '用户管理' } },
      { path: 'borrows', name: 'Borrows', component: () => import('@/views/Borrows.vue'), meta: { title: '借阅管理' } },
      { path: 'admins', name: 'Admins', component: () => import('@/views/Admins.vue'), meta: { title: '管理员管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userId = sessionStorage.getItem('userId')
  const readerId = sessionStorage.getItem('readerId')
  if (to.meta.user && !readerId) {
    next('/user/login')
  } else if (!to.meta.public && !to.meta.user && to.path !== '/login' && !userId) {
    next('/login')
  } else {
    next()
  }
})

export default router
