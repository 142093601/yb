<template>
  <div class="detail" v-loading="loading">
    <template v-if="news">
      <el-button text @click="$router.back()" style="margin-bottom:12px">← 返回列表</el-button>
      <h1 style="margin:0 0 16px">{{ news.title }}</h1>
      <div class="meta">
        <el-tag size="small">{{ news.categoryName }}</el-tag>
        <span>{{ news.authorName }}</span>
        <span>{{ formatTime(news.publishTime) }}</span>
        <span>👁 {{ news.viewCount }}</span>
        <span>❤ {{ news.likeCount }}</span>
      </div>
      <el-divider />
      <div class="content" v-html="news.content"></div>
      <div style="text-align:center;margin:24px 0">
        <el-button type="danger" :disabled="liked" @click="handleLike">
          {{ liked ? '已点赞 ❤️' : '👍 点个赞' }}
        </el-button>
      </div>

      <!-- 评论区 -->
      <el-divider>评论 ({{ commentTotal }})</el-divider>
      <div v-if="userStore.isLoggedIn" class="comment-input">
        <el-input v-model="commentText" type="textarea" :rows="3" placeholder="写点什么..." maxlength="500" show-word-limit />
        <el-button type="primary" style="margin-top:8px" @click="submitComment" :loading="commenting">发表评论</el-button>
      </div>
      <el-alert v-else title="登录后才能发表评论" type="info" :closable="false" show-icon style="margin-bottom:16px" />

      <div v-for="c in comments" :key="c.id" class="comment-item">
        <div class="comment-header">
          <strong>{{ c.nickname }}</strong>
          <span class="time">{{ formatTime(c.createTime) }}</span>
        </div>
        <p>{{ c.content }}</p>
      </div>
      <div v-if="commentTotal > 10" style="text-align:center;margin-top:12px">
        <el-button text @click="commentPage++; loadComments()">加载更多</el-button>
      </div>
    </template>
    <el-empty v-else description="新闻不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { getNewsDetail, likeNews } from '../../api/news'
import { getComments, addComment } from '../../api/comment'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const loading = ref(true)
const news = ref(null)
const liked = ref(false)
const comments = ref([])
const commentTotal = ref(0)
const commentPage = ref(1)
const commentText = ref('')
const commenting = ref(false)

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
}

async function handleLike() {
  await likeNews(route.params.id)
  liked.value = true
  news.value.likeCount++
  ElMessage.success('点赞成功')
}

async function loadComments() {
  const res = await getComments(route.params.id, { page: commentPage.value, size: 10 })
  comments.value = commentPage.value === 1 ? res.data.records : [...comments.value, ...res.data.records]
  commentTotal.value = res.data.total
}

async function submitComment() {
  if (!commentText.value.trim()) return ElMessage.warning('请输入评论内容')
  commenting.value = true
  try {
    await addComment({ newsId: route.params.id, content: commentText.value })
    commentText.value = ''
    ElMessage.success('评论成功，等待审核')
  } finally {
    commenting.value = false
  }
}

onMounted(async () => {
  try {
    const res = await getNewsDetail(route.params.id)
    news.value = res.data
    loadComments()
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.detail { max-width: 900px; margin: 0 auto; }
.meta { display: flex; gap: 16px; align-items: center; color: #999; font-size: 14px; }
.content { line-height: 1.8; font-size: 16px; }
.content :deep(img) { max-width: 100%; border-radius: 4px; }
.comment-input { margin-bottom: 16px; }
.comment-item { padding: 12px 0; border-bottom: 1px solid #f0f0f0; }
.comment-header { display: flex; justify-content: space-between; margin-bottom: 4px; }
.comment-header .time { color: #999; font-size: 13px; }
.comment-item p { margin: 0; color: #333; line-height: 1.6; }
</style>
