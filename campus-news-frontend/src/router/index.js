import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/front/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/front/Register.vue')
  },
  {
    path: '/',
    component: () => import('../views/front/Layout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/front/Home.vue') },
      { path: 'news/:id', name: 'NewsDetail', component: () => import('../views/front/NewsDetail.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/front/Profile.vue'), meta: { requiresAuth: true } }
    ]
  },
  {
    path: '/admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      { path: '', redirect: '/admin/news' },
      { path: 'news', name: 'AdminNews', component: () => import('../views/admin/NewsManage.vue') },
      { path: 'news/edit', name: 'NewsEdit', component: () => import('../views/admin/NewsEdit.vue') },
      { path: 'news/edit/:id', name: 'NewsEditById', component: () => import('../views/admin/NewsEdit.vue') },
      { path: 'category', name: 'AdminCategory', component: () => import('../views/admin/CategoryManage.vue') },
      { path: 'comment', name: 'AdminComment', component: () => import('../views/admin/CommentManage.vue') },
      { path: 'user', name: 'AdminUser', component: () => import('../views/admin/UserManage.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && userInfo?.role !== 'ADMIN') {
    next('/')
  } else {
    next()
  }
})

export default router
