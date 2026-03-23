<template>
  <el-container class="admin-layout">
    <el-aside width="200px">
      <div class="logo">📰 后台管理</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/admin/news"><el-icon><Document /></el-icon><span>新闻管理</span></el-menu-item>
        <el-menu-item index="/admin/category"><el-icon><FolderOpened /></el-icon><span>分类管理</span></el-menu-item>
        <el-menu-item index="/admin/comment"><el-icon><ChatDotRound /></el-icon><span>评论管理</span></el-menu-item>
        <el-menu-item index="/admin/user"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="display:flex;align-items:center;justify-content:flex-end">
        <el-button text @click="$router.push('/')">返回前台</el-button>
        <el-dropdown>
          <span style="cursor:pointer;margin-left:12px">{{ userStore.userInfo?.nickname }}</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useUserStore } from '../../stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.logo { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; background: #263445; }
.el-aside { background: #304156; }
.el-header { background: #fff; box-shadow: 0 1px 4px rgba(0,0,0,0.08); border-bottom: 1px solid #eee; }
</style>
