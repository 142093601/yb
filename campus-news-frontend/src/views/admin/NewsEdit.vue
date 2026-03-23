<template>
  <div>
    <el-button text @click="$router.back()" style="margin-bottom:12px">← 返回列表</el-button>
    <el-card>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入新闻标题" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width:200px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="封面">
          <el-upload :http-request="handleUpload" :show-file-list="false" accept="image/*">
            <el-button size="small">上传封面</el-button>
          </el-upload>
          <img v-if="form.coverImage" :src="form.coverImage" style="height:80px;margin-left:12px;border-radius:4px" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="新闻摘要（选填）" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="请输入正文内容（支持HTML）" />
        </el-form-item>
        <el-form-item>
          <el-button @click="save(0)">保存草稿</el-button>
          <el-button type="primary" @click="save(1)">直接发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCategories } from '../../api/category'
import { getAdminNewsList, saveNews, uploadFile } from '../../api/admin'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const categories = ref([])

const form = reactive({ id: null, title: '', categoryId: null, coverImage: '', summary: '', content: '', status: 0 })
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

async function handleUpload({ file }) {
  const res = await uploadFile(file)
  form.coverImage = res.data.url
  ElMessage.success('封面上传成功')
}

async function save(status) {
  form.status = status
  await formRef.value.validate()
  await saveNews(form)
  ElMessage.success(status === 1 ? '已发布' : '草稿已保存')
  router.push('/admin/news')
}

onMounted(async () => {
  const catRes = await getCategories()
  categories.value = catRes.data

  if (route.params.id) {
    const res = await getAdminNewsList({ page: 1, size: 1 })
    const news = res.data.records.find(n => n.id === Number(route.params.id))
    if (news) {
      Object.assign(form, { id: news.id, title: news.title, categoryId: news.categoryId, coverImage: news.coverImage, summary: news.summary, content: news.content })
    }
  }
})
</script>
