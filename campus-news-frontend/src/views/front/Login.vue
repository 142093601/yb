<template>
  <div class="login-page">
    <div class="login-bg">
      <div class="bg-shape s1"></div>
      <div class="bg-shape s2"></div>
      <div class="bg-shape s3"></div>
    </div>
    <div class="login-container">
      <div class="login-left">
        <div class="brand-area">
          <div class="brand-icon">📰</div>
          <h1>校园新闻</h1>
          <p>关注校园每一份精彩</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <span class="feature-icon">✨</span>
            <span>实时校园动态</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">💬</span>
            <span>互动评论交流</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔔</span>
            <span>重要通知推送</span>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-card">
          <h2>欢迎回来 👋</h2>
          <p class="login-desc">登录您的账号继续使用</p>
          <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
            <el-form-item prop="username">
              <el-input v-model="form.username" size="large" placeholder="请输入用户名">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" size="large" placeholder="请输入密码" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" size="large" round class="submit-btn" @click="handleLogin">
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-footer">
            <span>还没有账号？</span>
            <el-button type="primary" text @click="$router.push('/register')">立即注册</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { login } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    userStore.setLogin(res.data)
    ElMessage.success('登录成功，欢迎回来！')
    router.push(res.data.role === 'ADMIN' ? '/admin' : '/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  position: relative;
  overflow: hidden;
}

/* Background shapes */
.login-bg {
  position: absolute;
  inset: 0;
  overflow: hidden;
}
.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.4;
}
.s1 {
  width: 500px;
  height: 500px;
  background: radial-gradient(circle, #4f6ef7 0%, transparent 70%);
  top: -150px;
  right: -100px;
}
.s2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #7c5bf5 0%, transparent 70%);
  bottom: -100px;
  left: -80px;
}
.s3 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, #a855f7 0%, transparent 70%);
  top: 50%;
  left: 30%;
}

/* Container */
.login-container {
  position: relative;
  z-index: 2;
  display: flex;
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  max-width: 900px;
  width: 90%;
}

/* Left panel */
.login-left {
  flex: 1;
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  padding: 48px 40px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand-area {
  margin-bottom: 40px;
}
.brand-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.brand-area h1 {
  font-size: 28px;
  font-weight: 800;
  margin: 0 0 8px;
}
.brand-area p {
  font-size: 15px;
  opacity: 0.8;
  margin: 0;
}
.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  font-size: 14px;
}
.feature-icon {
  font-size: 18px;
}

/* Right panel */
.login-right {
  flex: 1;
  padding: 48px 40px;
  display: flex;
  align-items: center;
}
.login-card {
  width: 100%;
}
.login-card h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 4px;
}
.login-desc {
  color: #999;
  font-size: 14px;
  margin: 0 0 28px;
}
.submit-btn {
  width: 100%;
  font-size: 16px;
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  border: none;
}
.submit-btn:hover {
  background: linear-gradient(135deg, #3d5ce5, #6a49e3);
}
.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #999;
}

@media (max-width: 768px) {
  .login-left {
    display: none;
  }
  .login-right {
    padding: 32px 24px;
  }
  .login-container {
    border-radius: 16px;
  }
}
</style>
