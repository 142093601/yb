<template>
  <div class="detail" v-loading="loading">
    <template v-if="news">
      <!-- 面包屑导航 -->
      <div class="breadcrumb">
        <span class="bc-link" @click="$router.push('/')">首页</span>
        <el-icon><ArrowRight /></el-icon>
        <span class="bc-link" @click="$router.push('/')">{{ news.categoryName }}</span>
        <el-icon><ArrowRight /></el-icon>
        <span class="bc-current">正文</span>
      </div>

      <div class="detail-layout">
        <!-- 主内容区 -->
        <article class="article">
          <header class="article-header">
            <div class="article-tags">
              <el-tag type="danger" size="small" effect="dark" v-if="news.isTop">置顶</el-tag>
              <el-tag size="small" effect="plain">{{ news.categoryName }}</el-tag>
            </div>
            <h1 class="article-title">{{ news.title }}</h1>
            <div class="article-meta">
              <span class="meta-item author">
                <el-icon><User /></el-icon>{{ news.authorName }}
              </span>
              <span class="meta-item">
                <el-icon><Clock /></el-icon>{{ formatTime(news.publishTime) }}
              </span>
              <span class="meta-item">
                <el-icon><View /></el-icon>{{ news.viewCount }} 次阅读
              </span>
              <span class="meta-item">
                <el-icon><Star /></el-icon>{{ news.likeCount }} 点赞
              </span>
            </div>
          </header>

          <el-divider />

          <div class="article-content" v-html="news.content"></div>

          <!-- 点赞区 -->
          <div class="like-section">
            <button class="like-btn" :class="{ liked: liked }" @click="handleLike">
              <span class="like-icon">{{ liked ? '❤️' : '🤍' }}</span>
              <span class="like-text">{{ liked ? '已点赞' : '为这篇文章点赞' }}</span>
              <span class="like-count">{{ news.likeCount }}</span>
            </button>
          </div>

          <!-- 评论区 -->
          <section class="comment-section">
            <div class="comment-header">
              <h2>💬 评论 ({{ commentTotal }})</h2>
            </div>

            <div v-if="userStore.isLoggedIn" class="comment-form">
              <el-avatar :size="40" class="comment-avatar">
                {{ (userStore.userInfo?.nickname || userStore.userInfo?.username || '?')[0] }}
              </el-avatar>
              <div class="comment-input-wrap">
                <el-input
                  v-model="commentText"
                  type="textarea"
                  :rows="3"
                  placeholder="写下你的想法..."
                  maxlength="500"
                  show-word-limit
                  resize="none"
                />
                <div class="comment-submit">
                  <el-button type="primary" round @click="submitComment" :loading="commenting">
                    发表评论
                  </el-button>
                </div>
              </div>
            </div>
            <div v-else class="comment-login-tip">
              <div class="login-tip-icon">🔒</div>
              <p>登录后即可参与讨论</p>
              <el-button type="primary" round @click="$router.push('/login')">去登录</el-button>
            </div>

            <div class="comment-list">
              <div v-for="c in comments" :key="c.id" class="comment-item">
                <el-avatar :size="40" class="comment-avatar">
                  {{ (c.nickname || '?')[0] }}
                </el-avatar>
                <div class="comment-body">
                  <div class="comment-info">
                    <span class="comment-author">{{ c.nickname }}</span>
                    <span class="comment-time">{{ formatTime(c.createTime) }}</span>
                  </div>
                  <p class="comment-text">{{ c.content }}</p>
                </div>
              </div>
              <div v-if="comments.length === 0 && !loading" class="comment-empty">
                <p>还没有评论，来抢沙发吧 😄</p>
              </div>
            </div>

            <div v-if="commentTotal > comments.length" class="load-more">
              <el-button text @click="commentPage++; loadComments()">加载更多评论</el-button>
            </div>
          </section>
        </article>

        <!-- 侧边栏 -->
        <aside class="detail-sidebar">
          <div class="sidebar-card">
            <h3>📰 文章信息</h3>
            <ul class="article-info-list">
              <li><span class="info-label">分类</span><span class="info-value">{{ news.categoryName }}</span></li>
              <li><span class="info-label">作者</span><span class="info-value">{{ news.authorName }}</span></li>
              <li><span class="info-label">发布</span><span class="info-value">{{ formatDate(news.publishTime) }}</span></li>
              <li><span class="info-label">浏览</span><span class="info-value">{{ news.viewCount }}</span></li>
              <li><span class="info-label">点赞</span><span class="info-value">{{ news.likeCount }}</span></li>
            </ul>
          </div>
          <div class="sidebar-card">
            <el-button type="primary" round class="back-btn" @click="$router.push('/')">
              <el-icon><Back /></el-icon>返回列表
            </el-button>
          </div>
        </aside>
      </div>
    </template>
    <div v-else class="not-found">
      <div class="nf-icon">📄</div>
      <h2>文章不存在</h2>
      <p>该文章可能已被删除或不存在</p>
      <el-button type="primary" round @click="$router.push('/')">返回首页</el-button>
    </div>
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
function formatDate(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 10)
}

async function handleLike() {
  if (liked.value) return
  await likeNews(route.params.id)
  liked.value = true
  news.value.likeCount++
  ElMessage.success('点赞成功 ❤️')
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
.detail {
  max-width: 1200px;
  margin: 0 auto;
}

/* Breadcrumb */
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #999;
  margin-bottom: 20px;
}
.bc-link {
  cursor: pointer;
  transition: color 0.2s;
}
.bc-link:hover {
  color: #4f6ef7;
}
.bc-current {
  color: #555;
}

/* Layout */
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}

/* Article */
.article {
  background: #fff;
  border-radius: 20px;
  padding: 32px;
}
.article-header {
  margin-bottom: 4px;
}
.article-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}
.article-title {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0 0 16px;
  line-height: 1.35;
}
.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  color: #888;
}
.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
.meta-item.author {
  color: #4f6ef7;
  font-weight: 500;
}

/* Content */
.article-content {
  line-height: 2;
  font-size: 16px;
  color: #333;
  padding: 8px 0;
}
.article-content :deep(p) {
  margin: 0 0 16px;
}
.article-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
  margin: 12px 0;
}
.article-content :deep(strong) {
  color: #1a1a2e;
}
.article-content :deep(h2),
.article-content :deep(h3) {
  color: #1a1a2e;
  margin: 24px 0 12px;
}
.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 20px;
}

/* Like */
.like-section {
  text-align: center;
  margin: 32px 0;
}
.like-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 32px;
  border: 2px solid #eee;
  border-radius: 30px;
  background: #fff;
  font-size: 15px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}
.like-btn:hover {
  border-color: #ff6b6b;
  color: #ff6b6b;
  transform: scale(1.05);
}
.like-btn.liked {
  border-color: #ff6b6b;
  background: #fff5f5;
  color: #ff6b6b;
}
.like-icon {
  font-size: 20px;
}
.like-count {
  background: #f5f5f5;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}
.like-btn.liked .like-count {
  background: #ffe0e0;
}

/* Comment Section */
.comment-section {
  border-top: 1px solid #f0f0f0;
  padding-top: 24px;
}
.comment-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 20px;
}
.comment-form {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 16px;
}
.comment-avatar {
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  color: #fff;
  font-weight: 600;
  flex-shrink: 0;
}
.comment-input-wrap {
  flex: 1;
}
.comment-submit {
  text-align: right;
  margin-top: 8px;
}
.comment-login-tip {
  text-align: center;
  padding: 32px;
  background: #fafafa;
  border-radius: 16px;
  margin-bottom: 24px;
}
.login-tip-icon {
  font-size: 32px;
  margin-bottom: 8px;
}
.comment-login-tip p {
  color: #888;
  margin: 0 0 12px;
}

/* Comment List */
.comment-list {
  display: flex;
  flex-direction: column;
}
.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}
.comment-item:last-child {
  border-bottom: none;
}
.comment-body {
  flex: 1;
  min-width: 0;
}
.comment-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}
.comment-author {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}
.comment-time {
  font-size: 12px;
  color: #bbb;
}
.comment-text {
  margin: 0;
  font-size: 14px;
  color: #555;
  line-height: 1.6;
}
.comment-empty {
  text-align: center;
  padding: 32px 0;
  color: #999;
}
.load-more {
  text-align: center;
  margin-top: 12px;
}

/* Sidebar */
.detail-sidebar {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.sidebar-card {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
}
.sidebar-card h3 {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 16px;
}
.article-info-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.article-info-list li {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
  font-size: 13px;
}
.article-info-list li:last-child {
  border-bottom: none;
}
.info-label {
  color: #999;
}
.info-value {
  color: #333;
  font-weight: 500;
}
.back-btn {
  width: 100%;
}

/* Not Found */
.not-found {
  text-align: center;
  padding: 80px 0;
}
.nf-icon {
  font-size: 64px;
  margin-bottom: 16px;
}
.not-found h2 {
  font-size: 22px;
  color: #1a1a2e;
  margin: 0 0 8px;
}
.not-found p {
  color: #999;
  margin: 0 0 20px;
}

/* Responsive */
@media (max-width: 768px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
  .detail-sidebar {
    order: -1;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
  }
  .article {
    padding: 20px;
  }
  .article-title {
    font-size: 22px;
  }
}
</style>
