<template>
  <div class="profile-page">
    <div class="profile-container">
      <!-- 顶部个人信息卡片 -->
      <div class="profile-header">
        <div class="header-bg"></div>
        <div class="header-content">
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <img v-if="form.avatar" :src="form.avatar" class="avatar-img" />
              <div v-else class="avatar-placeholder">
                {{ (form.nickname || form.username || '?').charAt(0) }}
              </div>
            </div>
            <div class="user-basic">
              <h2 class="user-name">{{ form.nickname || form.username }}</h2>
              <div class="user-meta">
                <el-tag :type="roleTagType" size="small" effect="dark">{{ roleLabel }}</el-tag>
                <span class="user-id">@{{ form.username }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="profile-body">
        <!-- 左侧：信息卡片 -->
        <div class="info-cards">
          <!-- 基本信息 -->
          <div class="info-card">
            <div class="card-header">
              <h3>📋 基本信息</h3>
            </div>
            <div class="info-list">
              <div class="info-item">
                <span class="info-label">用户名</span>
                <span class="info-value readonly">{{ form.username }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">昵称</span>
                <span class="info-value">{{ form.nickname || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{ form.email || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">手机号</span>
                <span class="info-value">{{ form.phone || '未设置' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">角色</span>
                <span class="info-value">
                  <el-tag :type="roleTagType" size="small">{{ roleLabel }}</el-tag>
                </span>
              </div>
            </div>
          </div>

          <!-- 账号安全 -->
          <div class="info-card">
            <div class="card-header">
              <h3>🔐 账号安全</h3>
            </div>
            <div class="security-list">
              <div class="security-item">
                <div class="security-info">
                  <span class="security-title">登录密码</span>
                  <span class="security-desc">定期修改密码有助于保护账号安全</span>
                </div>
                <el-button type="primary" plain size="small" @click="showPasswordDialog = true">
                  修改密码
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：编辑表单 -->
        <div class="edit-card">
          <div class="card-header">
            <h3>✏️ 编辑资料</h3>
          </div>
          <el-form :model="form" label-position="top" class="edit-form">
            <el-form-item label="头像链接">
              <el-input v-model="form.avatar" placeholder="输入头像图片URL" clearable>
                <template #prefix>
                  <el-icon><Picture /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="昵称">
              <el-input v-model="form.nickname" placeholder="设置一个昵称" maxlength="20" show-word-limit>
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="example@campus.edu" clearable>
                <template #prefix>
                  <el-icon><Message /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="输入手机号" clearable>
                <template #prefix>
                  <el-icon><Phone /></el-icon>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="save-btn" :loading="saving" @click="handleSave">
                保存修改
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="showPasswordDialog" title="修改密码" width="420px" :close-on-click-modal="false">
      <el-form :model="pwdForm" label-position="top" ref="pwdFormRef">
        <el-form-item label="当前密码" prop="oldPassword"
          :rules="[{ required: true, message: '请输入当前密码', trigger: 'blur' }]">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword"
          :rules="[
            { required: true, message: '请输入新密码', trigger: 'blur' },
            { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
          ]">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-20位密码">
            <template #prefix><el-icon><Key /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword"
          :rules="[
            { required: true, message: '请再次输入新密码', trigger: 'blur' },
            { validator: validateConfirm, trigger: 'blur' }
          ]">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码">
            <template #prefix><el-icon><Key /></el-icon></template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" :loading="changingPwd" @click="handlePasswordChange">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateProfile, changePassword } from '../../api/auth'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()

const form = ref({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})

const saving = ref(false)
const showPasswordDialog = ref(false)
const changingPwd = ref(false)
const pwdFormRef = ref(null)
const pwdForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const roleLabel = computed(() => {
  const map = { ADMIN: '管理员', EDITOR: '编辑', USER: '普通用户' }
  return map[userStore.userInfo?.role] || '用户'
})

const roleTagType = computed(() => {
  const map = { ADMIN: 'danger', EDITOR: 'warning', USER: '' }
  return map[userStore.userInfo?.role] || 'info'
})

function validateConfirm(rule, value, callback) {
  if (value !== pwdForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

async function loadUserInfo() {
  try {
    const res = await getUserInfo()
    const data = res.data
    form.value = {
      username: data.username || '',
      nickname: data.nickname || '',
      email: data.email || '',
      phone: data.phone || '',
      avatar: data.avatar || ''
    }
  } catch {
    ElMessage.error('获取用户信息失败')
  }
}

async function handleSave() {
  saving.value = true
  try {
    const res = await updateProfile({
      nickname: form.value.nickname,
      email: form.value.email,
      phone: form.value.phone,
      avatar: form.value.avatar
    })
    // 同步更新 store
    userStore.updateUserInfo({
      nickname: form.value.nickname,
      avatar: form.value.avatar
    })
    ElMessage.success(res.message || '保存成功')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function handlePasswordChange() {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return

  changingPwd.value = true
  try {
    const res = await changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    ElMessage.success(res.message || '密码修改成功')
    showPasswordDialog.value = false
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {
    ElMessage.error(e.message || '密码修改失败')
  } finally {
    changingPwd.value = false
  }
}

onMounted(loadUserInfo)
</script>

<style scoped>
.profile-page {
  min-height: calc(100vh - 200px);
  padding: 24px 0;
}

.profile-container {
  max-width: 960px;
  margin: 0 auto;
}

/* ===== Header ===== */
.profile-header {
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 24px;
}
.header-bg {
  height: 140px;
  background: linear-gradient(135deg, #4f6ef7 0%, #7c5bf5 40%, #a855f7 100%);
}
.header-content {
  position: relative;
  padding: 0 32px 28px;
  margin-top: -40px;
}
.avatar-section {
  display: flex;
  align-items: flex-end;
  gap: 20px;
}
.avatar-wrapper {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #fff;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  flex-shrink: 0;
  background: #f0f0f0;
}
.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  color: #fff;
  font-size: 36px;
  font-weight: 700;
}
.user-name {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 6px;
}
.user-meta {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-id {
  font-size: 13px;
  color: #999;
}

/* ===== Body ===== */
.profile-body {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* ===== Cards ===== */
.info-card,
.edit-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
}
.card-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

/* Info list */
.info-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}
.info-label {
  font-size: 14px;
  color: #888;
}
.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}
.info-value.readonly {
  color: #aaa;
  font-family: monospace;
}

/* Security list */
.security-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 12px;
}
.security-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}
.security-desc {
  display: block;
  font-size: 12px;
  color: #aaa;
  margin-top: 2px;
}

/* Edit form */
.edit-form {
  margin-top: 4px;
}
.edit-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #555;
}
.edit-form :deep(.el-input__wrapper) {
  border-radius: 10px;
}
.save-btn {
  width: 100%;
  height: 42px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .profile-body {
    grid-template-columns: 1fr;
  }
  .header-content {
    padding: 0 20px 24px;
  }
  .avatar-wrapper {
    width: 68px;
    height: 68px;
  }
  .avatar-placeholder {
    font-size: 28px;
  }
  .user-name {
    font-size: 20px;
  }
}
</style>
