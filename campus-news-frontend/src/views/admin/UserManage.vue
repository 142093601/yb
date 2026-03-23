<template>
  <div>
    <div style="margin-bottom:16px">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称..." prefix-icon="Search" style="width:240px" clearable @clear="load" @keyup.enter="load" />
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'EDITOR' ? 'warning' : 'info'">
            {{ { ADMIN: '管理员', EDITOR: '编辑', USER: '用户' }[row.role] }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="160">
        <template #default="{ row }">{{ row.createTime?.replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-popconfirm :title="row.status === 1 ? '确定禁用该用户？' : '确定启用该用户？'" @confirm="handleToggle(row.id)">
            <template #reference>
              <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="text-align:center;margin-top:16px">
      <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑用户" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role">
            <el-option label="普通用户" value="USER" />
            <el-option label="编辑" value="EDITOR" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUserList, updateUser, toggleUserStatus } from '../../api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const total = ref(0)
const editVisible = ref(false)
const editForm = reactive({ id: null, nickname: '', email: '', role: '' })

async function load() {
  loading.value = true
  try {
    const res = await getUserList({ keyword: keyword.value || undefined, page: page.value, size: 10 })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function openEdit(row) {
  Object.assign(editForm, { id: row.id, nickname: row.nickname, email: row.email, role: row.role })
  editVisible.value = true
}

async function handleEdit() {
  await updateUser(editForm)
  ElMessage.success('更新成功')
  editVisible.value = false
  load()
}

async function handleToggle(id) {
  await toggleUserStatus(id)
  ElMessage.success('操作成功')
  load()
}

onMounted(load)
</script>
