<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <div class="logo-group" @click="$router.push('/')">
          <div class="logo-icon">📰</div>
          <div class="logo-text">
            <span class="logo-title">校园新闻</span>
            <span class="logo-subtitle">Campus News</span>
          </div>
        </div>
        <nav class="nav-links">
          <router-link to="/" class="nav-item" :class="{ active: $route.path === '/' }">
            <el-icon><HomeFilled /></el-icon>首页
          </router-link>
          <router-link to="/search" class="nav-item" :class="{ active: $route.path === '/search' }">
            <el-icon><Search /></el-icon>搜索
          </router-link>
          <router-link to="/archive" class="nav-item" :class="{ active: $route.path === '/archive' }">
            <el-icon><Collection /></el-icon>归档
          </router-link>
        </nav>
        <div class="nav-actions">
          <template v-if="!userStore.isLoggedIn">
            <el-button class="btn-login" text @click="$router.push('/login')">登录</el-button>
            <el-button class="btn-register" type="primary" round @click="$router.push('/register')">注册</el-button>
          </template>
          <template v-else>
            <el-button v-if="userStore.isAdmin" class="btn-admin" type="warning" round @click="$router.push('/admin')">
              <el-icon><Setting /></el-icon>管理后台
            </el-button>
            <el-dropdown trigger="click" class="user-dropdown">
              <div class="user-avatar-btn">
                <el-avatar :size="36" class="avatar-circle">
                  {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '?')[0] }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item disabled>
                    <el-tag :type="roleTag" size="small">{{ roleLabel }}</el-tag>
                  </el-dropdown-item>
                  <el-dropdown-item @click="$router.push('/profile')">
                    <el-icon><User /></el-icon>个人中心
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </div>
      </div>
    </header>
    <main class="main">
      <router-view />
    </main>
    <footer class="footer">
      <div class="footer-inner">
        <div class="footer-brand">
          <span class="footer-logo">📰 校园新闻</span>
          <p class="footer-desc">关注校园动态，传递校园声音</p>
        </div>
        <div class="footer-links">
          <div class="footer-col">
            <h4>快速导航</h4>
            <a href="#">校园动态</a>
            <a href="#">通知公告</a>
            <a href="#">学术科研</a>
          </div>
          <div class="footer-col">
            <h4>校园服务</h4>
            <a href="#">就业实习</a>
            <a href="#">文体活动</a>
            <a href="#">教务系统</a>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2026 校园新闻系统 · 仅供学习交流使用</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', EDITOR: '编辑', USER: '普通用户' }
  return map[userStore.userInfo?.role] || '用户'
})
const roleTag = computed(() => {
  const map = { ADMIN: 'danger', EDITOR: 'warning', USER: 'info' }
  return map[userStore.userInfo?.role] || 'info'
})

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

/* Header */
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  box-shadow: 0 1px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.logo-group {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s;
}
.logo-group:hover {
  transform: scale(1.02);
}
.logo-icon {
  font-size: 28px;
  line-height: 1;
}
.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}
.logo-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  letter-spacing: 1px;
}
.logo-subtitle {
  font-size: 11px;
  color: #999;
  letter-spacing: 2px;
  text-transform: uppercase;
}

.nav-links {
  display: flex;
  gap: 4px;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 16px;
  border-radius: 20px;
  color: #555;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}
.nav-item:hover,
.nav-item.active {
  background: #eef2ff;
  color: #4f6ef7;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.btn-login {
  font-size: 14px;
  color: #555;
}
.btn-register {
  font-size: 14px;
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  border: none;
  padding: 8px 20px;
}
.btn-register:hover {
  background: linear-gradient(135deg, #3d5ce5, #6a49e3);
}
.btn-admin {
  font-size: 13px;
}

.user-dropdown {
  cursor: pointer;
}
.user-avatar-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 24px;
  transition: background 0.2s;
}
.user-avatar-btn:hover {
  background: #f5f5f5;
}
.avatar-circle {
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}
.user-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* Main */
.main {
  flex: 1;
  max-width: 1280px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

/* Footer */
.footer {
  background: #1a1a2e;
  color: #ccc;
  margin-top: 40px;
}
.footer-inner {
  max-width: 1280px;
  margin: 0 auto;
  padding: 40px 24px 20px;
}
.footer-brand {
  margin-bottom: 24px;
}
.footer-logo {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}
.footer-desc {
  margin: 6px 0 0;
  font-size: 13px;
  color: #888;
}
.footer-links {
  display: flex;
  gap: 60px;
  margin-bottom: 24px;
}
.footer-col h4 {
  color: #fff;
  font-size: 14px;
  margin: 0 0 12px;
}
.footer-col a {
  display: block;
  color: #999;
  text-decoration: none;
  font-size: 13px;
  margin-bottom: 8px;
  transition: color 0.2s;
}
.footer-col a:hover {
  color: #4f6ef7;
}
.footer-bottom {
  border-top: 1px solid #2a2a4a;
  padding-top: 16px;
}
.footer-bottom p {
  margin: 0;
  font-size: 12px;
  color: #666;
}
</style>
