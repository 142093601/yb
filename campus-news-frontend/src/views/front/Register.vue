<template>
  <div class="register-page">
    <div class="reg-bg">
      <div class="bg-shape s1"></div>
      <div class="bg-shape s2"></div>
      <div class="bg-shape s3"></div>
    </div>
    <div class="register-container">
      <div class="reg-left">
        <div class="brand-area">
          <div class="brand-icon">📰</div>
          <h1>加入我们</h1>
          <p>成为校园新闻社区的一员</p>
        </div>
        <div class="benefits">
          <div class="benefit-item">
            <span class="benefit-icon">📖</span>
            <span>浏览最新校园资讯</span>
          </div>
          <div class="benefit-item">
            <span class="benefit-icon">💬</span>
            <span>发表评论参与讨论</span>
          </div>
          <div class="benefit-item">
            <span class="benefit-icon">❤️</span>
            <span>点赞收藏精彩内容</span>
          </div>
          <div class="benefit-item">
            <span class="benefit-icon">🔔</span>
            <span>获取重要通知提醒</span>
          </div>
        </div>
      </div>
      <div class="reg-right">
        <div class="reg-card">
          <h2>创建账号 ✨</h2>
          <p class="reg-desc">填写以下信息完成注册</p>
          <el-form :model="form" :rules="rules" ref="formRef">
            <el-form-item prop="username">
              <el-input v-model="form.username" size="large" placeholder="用户名（3-20位）">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="form.password" type="password" size="large" placeholder="密码（6-20位）" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" size="large" placeholder="确认密码" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="nickname">
              <el-input v-model="form.nickname" size="large" placeholder="昵称（选填）">
                <template #prefix><el-icon><Avatar /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" size="large" round class="submit-btn" @click="handleRegister">
                {{ loading ? '注册中...' : '注 册' }}
              </el-button>
            </el-form-item>
          </el-form>
          <div class="reg-footer">
            <span>已有账号？</span>
            <el-button type="primary" text @click="$router.push('/login')">去登录</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../../api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: '', password: '', confirmPassword: '', nickname: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) callback(new Error('两次密码不一致'))
  else callback()
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '长度3-20位', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度6-20位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirm, trigger: 'blur' }]
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录 ✌️')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  position: relative;
  overflow: hidden;
}

.reg-bg {
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
  background: radial-gradient(circle, #10b981 0%, transparent 70%);
  top: -150px;
  left: -100px;
}
.s2 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, #06b6d4 0%, transparent 70%);
  bottom: -100px;
  right: -80px;
}
.s3 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, #8b5cf6 0%, transparent 70%);
  top: 60%;
  right: 30%;
}

.register-container {
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

.reg-left {
  flex: 1;
  background: linear-gradient(135deg, #10b981, #06b6d4);
  padding: 48px 40px;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.brand-area {
  margin-bottom: 36px;
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
.benefits {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.benefit-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  font-size: 14px;
}
.benefit-icon {
  font-size: 18px;
}

.reg-right {
  flex: 1;
  padding: 40px 40px;
  display: flex;
  align-items: center;
}
.reg-card {
  width: 100%;
}
.reg-card h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 4px;
}
.reg-desc {
  color: #999;
  font-size: 14px;
  margin: 0 0 24px;
}
.submit-btn {
  width: 100%;
  font-size: 16px;
  background: linear-gradient(135deg, #10b981, #06b6d4);
  border: none;
}
.submit-btn:hover {
  background: linear-gradient(135deg, #059669, #0891b2);
}
.reg-footer {
  text-align: center;
  margin-top: 12px;
  font-size: 14px;
  color: #999;
}

@media (max-width: 768px) {
  .reg-left {
    display: none;
  }
  .reg-right {
    padding: 32px 24px;
  }
  .register-container {
    border-radius: 16px;
  }
}
</style>
