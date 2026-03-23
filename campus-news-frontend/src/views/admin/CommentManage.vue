<template>
  <div>
    <div style="margin-bottom:16px">
      <el-radio-group v-model="filterStatus" @change="load">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button :label="0">待审核</el-radio-button>
        <el-radio-button :label="1">已通过</el-radio-button>
        <el-radio-button :label="2">已拒绝</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="nickname" label="用户" width="100" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="newsId" label="所属新闻ID" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="['warning','success','danger'][row.status]">{{ ['待审核','已通过','已拒绝'][row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" width="160">
        <template #default="{ row }">{{ row.createTime?.replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" size="small" type="success" @click="handleAudit(row.id, 1)">通过</el-button>
          <el-button v-if="row.status === 0" size="small" type="warning" @click="handleAudit(row.id, 2)">拒绝</el-button>
          <el-popconfirm title="确定删除？" @confirm="handleDelete(row.id)">
            <template #reference><el-button size="small" type="danger">删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="text-align:center;margin-top:16px">
      <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="10" v-model:current-page="page" @current-change="load" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllComments, auditComment, deleteComment } from '../../api/comment'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const filterStatus = ref('')
const page = ref(1)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAllComments({ status: filterStatus.value === '' ? undefined : filterStatus.value, page: page.value, size: 10 })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function handleAudit(id, status) {
  await auditComment(id, status)
  ElMessage.success('操作成功')
  load()
}

async function handleDelete(id) {
  await deleteComment(id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
