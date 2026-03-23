<template>
  <div class="layout">
    <header class="header">
      <div class="header-inner">
        <h1 class="logo" @click="$router.push('/')">📰 校园新闻</h1>
        <div class="nav">
          <el-button v-if="!userStore.isLoggedIn" type="primary" text @click="$router.push('/login')">登录</el-button>
          <template v-else>
            <el-button v-if="userStore.isAdmin" text @click="$router.push('/admin')">管理后台</el-button>
            <el-dropdown>
              <span class="user-info">
                {{ userStore.userInfo?.nickname || userStore.userInfo?.username }}
                <el-icon><arrow-down /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
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
      <p>© 2024 校园新闻系统 · 仅供学习交流使用</p>
    </footer>
  </div>
</template>

<script setup>
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.layout { min-height: 100vh; display: flex; flex-direction: column; }
.header { background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.08); position: sticky; top: 0; z-index: 100; }
.header-inner { max-width: 1200px; margin: 0 auto; padding: 0 20px; height: 60px; display: flex; align-items: center; justify-content: space-between; }
.logo { font-size: 20px; cursor: pointer; color: #409eff; margin: 0; }
.nav { display: flex; align-items: center; gap: 12px; }
.user-info { cursor: pointer; display: flex; align-items: center; gap: 4px; color: #606266; }
.main { flex: 1; max-width: 1200px; margin: 0 auto; padding: 20px; width: 100%; box-sizing: border-box; }
.footer { text-align: center; padding: 20px; color: #999; font-size: 14px; border-top: 1px solid #eee; }
</style>
