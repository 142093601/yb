<template>
  <div>
    <div style="display:flex;justify-content:space-between;margin-bottom:16px">
      <div style="display:flex;gap:8px">
        <el-input v-model="keyword" placeholder="搜索标题..." prefix-icon="Search" style="width:200px" clearable @clear="load" @keyup.enter="load" />
        <el-select v-model="status" placeholder="状态" clearable style="width:120px" @change="load">
          <el-option label="草稿" :value="0" />
          <el-option label="已发布" :value="1" />
          <el-option label="已下架" :value="2" />
        </el-select>
      </div>
      <el-button type="primary" @click="$router.push('/admin/news/edit')">+ 新建新闻</el-button>
    </div>

    <el-table :data="list" v-loading="loading" stripe>
      <el-table-column prop="title" label="标题" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="分类" width="100" />
      <el-table-column prop="authorName" label="作者" width="100" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="['info','success','warning'][row.status]">{{ ['草稿','已发布','已下架'][row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="置顶" width="60">
        <template #default="{ row }">{{ row.isTop ? '📌' : '' }}</template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="70" />
      <el-table-column prop="publishTime" label="发布时间" width="160">
        <template #default="{ row }">{{ row.publishTime?.replace('T', ' ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="$router.push(`/admin/news/edit/${row.id}`)">编辑</el-button>
          <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" @click="togglePublish(row)">
            {{ row.status === 1 ? '下架' : '发布' }}
          </el-button>
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
import { getAdminNewsList, deleteNews, publishNews } from '../../api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const keyword = ref('')
const status = ref(null)
const page = ref(1)
const total = ref(0)

async function load() {
  loading.value = true
  try {
    const res = await getAdminNewsList({ keyword: keyword.value || undefined, status: status.value, page: page.value, size: 10 })
    list.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

async function togglePublish(row) {
  const newStatus = row.status === 1 ? 2 : 1
  await publishNews(row.id, newStatus)
  ElMessage.success(newStatus === 1 ? '已发布' : '已下架')
  load()
}

async function handleDelete(id) {
  await deleteNews(id)
  ElMessage.success('已删除')
  load()
}

onMounted(load)
</script>
