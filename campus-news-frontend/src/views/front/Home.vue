<template>
  <div class="home">
    <!-- Hero Banner -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">校园新闻<span class="hero-dot">.</span></h1>
        <p class="hero-subtitle">关注校园每一份精彩，不错过任何重要动态</p>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-num">{{ total }}</span>
            <span class="stat-label">篇新闻</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">{{ categories.length }}</span>
            <span class="stat-label">个分类</span>
          </div>
          <div class="stat-item">
            <span class="stat-num">4</span>
            <span class="stat-label">位用户</span>
          </div>
        </div>
      </div>
      <div class="hero-decoration">
        <div class="deco-circle c1"></div>
        <div class="deco-circle c2"></div>
        <div class="deco-circle c3"></div>
      </div>
    </section>

    <!-- 轮播图：热点推荐 -->
    <section class="carousel-section" v-if="topNews.length">
      <el-carousel :interval="4000" height="340px" indicator-position="outside" arrow="hover" class="news-carousel">
        <el-carousel-item v-for="item in topNews" :key="item.id">
          <div class="carousel-slide" @click="$router.push(`/news/${item.id}`)">
            <div class="carousel-bg" :style="item.coverImage ? { backgroundImage: `url(${item.coverImage})` } : {}">
              <div class="carousel-overlay"></div>
            </div>
            <div class="carousel-content">
              <el-tag type="danger" effect="dark" size="small">🔥 热点</el-tag>
              <h2 class="carousel-title">{{ item.title }}</h2>
              <p class="carousel-summary" v-if="item.summary">{{ item.summary }}</p>
              <div class="carousel-meta">
                <span>{{ item.categoryName }}</span>
                <span>{{ formatTime(item.publishTime) }}</span>
                <span>👁 {{ item.viewCount }}</span>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 主内容区 -->
    <div class="content-wrapper">
      <!-- 左侧边栏 -->
      <aside class="sidebar sidebar-left">
        <!-- 分类导航 -->
        <div class="sidebar-card">
          <h3>📁 新闻分类</h3>
          <ul class="sidebar-categories">
            <li :class="{ active: activeCategory === '' }" @click="activeCategory = ''">
              <span class="cat-name">📋 全部新闻</span>
            </li>
            <li v-for="c in categories" :key="c.id"
                :class="{ active: activeCategory === c.id }"
                @click="activeCategory = c.id">
              <span class="cat-name">{{ categoryIcon(c.name) }} {{ c.name }}</span>
            </li>
          </ul>
        </div>

        <!-- 统计卡片 -->
        <div class="sidebar-card stats-card">
          <h3>📊 数据统计</h3>
          <div class="stats-grid">
            <div class="stat-block">
              <span class="stat-block-num">{{ total }}</span>
              <span class="stat-block-label">新闻总数</span>
            </div>
            <div class="stat-block">
              <span class="stat-block-num">{{ categories.length }}</span>
              <span class="stat-block-label">新闻分类</span>
            </div>
          </div>
        </div>
      </aside>

      <!-- 中间：新闻列表 -->
      <div class="main-content">
        <!-- 搜索 -->
        <div class="filter-bar">
          <h2 class="section-title">
            <span class="section-icon">{{ activeCategory ? categoryIcon(activeCategoryName) : '📰' }}</span>
            {{ activeCategoryName || '全部新闻' }}
          </h2>
          <div class="search-box">
            <el-input
              v-model="keyword"
              placeholder="搜索新闻标题..."
              prefix-icon="Search"
              clearable
              @clear="loadNews"
              @keyup.enter="loadNews"
              class="search-input"
            />
          </div>
        </div>

        <!-- 新闻卡片列表 -->
        <div v-loading="loading" class="news-list">
          <div v-if="newsList.length === 0 && !loading" class="empty-state">
            <div class="empty-icon">📭</div>
            <p>暂无新闻</p>
          </div>
          <article
            v-for="item in newsList"
            :key="item.id"
            class="news-card"
            @click="$router.push(`/news/${item.id}`)"
          >
            <div class="card-cover" v-if="item.coverImage">
              <img :src="item.coverImage" :alt="item.title" />
            </div>
            <div class="card-cover placeholder" v-else>
              <span>{{ item.categoryName }}</span>
            </div>
            <div class="card-body">
              <div class="card-tags">
                <el-tag v-if="item.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
                <el-tag type="info" size="small" effect="plain">{{ item.categoryName }}</el-tag>
              </div>
              <h3 class="card-title">{{ item.title }}</h3>
              <p class="card-summary">{{ item.summary }}</p>
              <div class="card-footer">
                <span class="card-author">
                  <el-icon><User /></el-icon>{{ item.authorName }}
                </span>
                <span class="card-time">{{ formatTime(item.publishTime) }}</span>
                <span class="card-views">
                  <el-icon><View /></el-icon>{{ item.viewCount }}
                </span>
                <span class="card-likes">👍 {{ item.likeCount }}</span>
              </div>
            </div>
          </article>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrap" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next, total"
            :total="total"
            :page-size="10"
            v-model:current-page="page"
            @current-change="loadNews"
          />
        </div>
      </div>

      <!-- 右侧边栏 -->
      <aside class="sidebar sidebar-right">
        <!-- 热门文章 -->
        <div class="sidebar-card" v-if="hotNews.length">
          <h3>🔥 热门排行</h3>
          <ul class="hot-list">
            <li v-for="(item, index) in hotNews" :key="item.id" @click="$router.push(`/news/${item.id}`)">
              <span class="hot-rank" :class="{ 'rank-top': index < 3 }">{{ index + 1 }}</span>
              <div class="hot-info">
                <span class="hot-title">{{ item.title }}</span>
                <span class="hot-views">{{ item.viewCount }} 阅读</span>
              </div>
            </li>
          </ul>
        </div>

        <!-- 最新评论 / 公告 -->
        <div class="sidebar-card">
          <h3>📢 公告</h3>
          <div class="notice-list">
            <div class="notice-item">
              <span class="notice-dot"></span>
              <span>欢迎来到校园新闻系统！</span>
            </div>
            <div class="notice-item">
              <span class="notice-dot"></span>
              <span>登录后可发表评论和点赞</span>
            </div>
            <div class="notice-item">
              <span class="notice-dot"></span>
              <span>管理员可进入后台管理新闻</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getNewsList, getTopNews } from '../../api/news'
import { getCategories } from '../../api/category'

const categories = ref([])
const activeCategory = ref('')
const keyword = ref('')
const newsList = ref([])
const topNews = ref([])
const loading = ref(false)
const page = ref(1)
const total = ref(0)

const hotNews = computed(() =>
  [...newsList.value].sort((a, b) => b.viewCount - a.viewCount).slice(0, 8)
)

const activeCategoryName = computed(() => {
  if (!activeCategory.value) return ''
  const cat = categories.value.find(c => c.id === activeCategory.value)
  return cat ? cat.name : ''
})

function categoryIcon(name) {
  const icons = { '校园动态': '🏫', '通知公告': '📢', '学术科研': '🔬', '文体活动': '⚽', '就业实习': '💼' }
  return icons[name] || '📄'
}

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 10)
}

async function loadNews() {
  loading.value = true
  try {
    const res = await getNewsList({
      categoryId: activeCategory.value || undefined,
      keyword: keyword.value || undefined,
      page: page.value,
      size: 10
    })
    newsList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

watch(activeCategory, () => { page.value = 1; loadNews() })

onMounted(async () => {
  const [catRes, topRes] = await Promise.all([getCategories(), getTopNews()])
  categories.value = catRes.data
  topNews.value = topRes.data
  loadNews()
})
</script>

<style scoped>
/* ===== Hero ===== */
.hero {
  position: relative;
  background: linear-gradient(135deg, #4f6ef7 0%, #7c5bf5 50%, #a855f7 100%);
  border-radius: 20px;
  padding: 48px 40px;
  margin-bottom: 24px;
  overflow: hidden;
  color: #fff;
}
.hero-content {
  position: relative;
  z-index: 2;
}
.hero-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 8px;
  letter-spacing: -0.5px;
}
.hero-dot { color: #fbbf24; }
.hero-subtitle {
  font-size: 16px;
  opacity: 0.85;
  margin: 0 0 28px;
}
.hero-stats {
  display: flex;
  gap: 32px;
}
.stat-item {
  display: flex;
  flex-direction: column;
}
.stat-num {
  font-size: 28px;
  font-weight: 800;
}
.stat-label {
  font-size: 13px;
  opacity: 0.75;
}
.hero-decoration {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 40%;
  overflow: hidden;
}
.deco-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.08);
}
.c1 { width: 200px; height: 200px; top: -40px; right: -30px; }
.c2 { width: 120px; height: 120px; bottom: -20px; right: 80px; }
.c3 { width: 80px; height: 80px; top: 30px; right: 180px; background: rgba(255, 255, 255, 0.05); }

/* ===== Carousel ===== */
.carousel-section {
  margin-bottom: 24px;
}
.news-carousel {
  border-radius: 20px;
  overflow: hidden;
}
.news-carousel :deep(.el-carousel__container) {
  border-radius: 20px;
}
.news-carousel :deep(.el-carousel__indicator button) {
  background-color: rgba(255, 255, 255, 0.4);
}
.news-carousel :deep(.el-carousel__indicator.is-active button) {
  background-color: #4f6ef7;
}
.carousel-slide {
  position: relative;
  height: 100%;
  cursor: pointer;
}
.carousel-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  background-size: cover;
  background-position: center;
}
.carousel-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.75) 0%, rgba(0, 0, 0, 0.2) 50%, rgba(0, 0, 0, 0.1) 100%);
}
.carousel-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 48px;
  color: #fff;
  z-index: 2;
}
.carousel-title {
  font-size: 28px;
  font-weight: 700;
  margin: 10px 0 8px;
  line-height: 1.3;
  max-width: 700px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}
.carousel-summary {
  font-size: 15px;
  opacity: 0.85;
  margin: 0 0 12px;
  max-width: 600px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.carousel-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  opacity: 0.7;
}

/* ===== Content Wrapper: 3-column ===== */
.content-wrapper {
  display: grid;
  grid-template-columns: 220px 1fr 280px;
  gap: 20px;
}

/* ===== Sidebar (shared) ===== */
.sidebar {
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
  margin: 0 0 14px;
}

/* Sidebar categories */
.sidebar-categories {
  list-style: none;
  margin: 0;
  padding: 0;
}
.sidebar-categories li {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #555;
  margin-bottom: 2px;
}
.sidebar-categories li:hover {
  background: #f5f7ff;
  color: #4f6ef7;
}
.sidebar-categories li.active {
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  color: #fff;
  font-weight: 500;
}

/* Stats card */
.stats-card .stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.stat-block {
  text-align: center;
  padding: 12px 8px;
  background: #f5f7ff;
  border-radius: 12px;
}
.stat-block-num {
  display: block;
  font-size: 22px;
  font-weight: 800;
  color: #4f6ef7;
}
.stat-block-label {
  font-size: 11px;
  color: #888;
}

/* Hot list */
.hot-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.hot-list li {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
  transition: background 0.15s;
}
.hot-list li:last-child { border-bottom: none; }
.hot-list li:hover .hot-title { color: #4f6ef7; }
.hot-rank {
  flex-shrink: 0;
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 700;
  background: #f0f0f0;
  color: #999;
}
.hot-rank.rank-top {
  background: linear-gradient(135deg, #f59e0b, #ef4444);
  color: #fff;
}
.hot-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.hot-title {
  font-size: 13px;
  color: #333;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.2s;
}
.hot-views {
  font-size: 11px;
  color: #bbb;
}

/* Notice list */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.notice-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 13px;
  color: #555;
  line-height: 1.5;
}
.notice-dot {
  flex-shrink: 0;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #4f6ef7;
  margin-top: 6px;
}

/* ===== Main Content ===== */
.main-content {
  min-width: 0;
}

/* Filter bar */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  gap: 16px;
}
.section-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}
.search-box { flex-shrink: 0; }
.search-input { width: 220px; }
.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

/* News list */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.news-card {
  display: flex;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.news-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.07);
}
.card-cover {
  width: 200px;
  min-height: 140px;
  flex-shrink: 0;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}
.news-card:hover .card-cover img {
  transform: scale(1.05);
}
.card-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #6366f1;
  font-size: 16px;
  font-weight: 700;
}
.card-body {
  padding: 16px 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}
.card-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-summary {
  font-size: 13px;
  color: #888;
  margin: 0 0 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.card-footer {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 12px;
  color: #aaa;
}
.card-author,
.card-views,
.card-likes {
  display: flex;
  align-items: center;
  gap: 3px;
}

/* Pagination */
.pagination-wrap {
  text-align: center;
  margin-top: 20px;
  padding-bottom: 8px;
}

/* Empty state */
.empty-state {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 16px;
}
.empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
}
.empty-state p {
  color: #999;
  font-size: 15px;
}

/* ===== Responsive ===== */
@media (max-width: 1200px) {
  .content-wrapper {
    grid-template-columns: 1fr 260px;
  }
  .sidebar-left {
    display: none;
  }
}
@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  .sidebar-left,
  .sidebar-right {
    display: none;
  }
  .hero { padding: 32px 24px; }
  .hero-title { font-size: 28px; }
  .carousel-title { font-size: 20px; }
  .carousel-content { padding: 24px; }
  .news-carousel :deep(.el-carousel__container) { height: 260px !important; }
  .news-card { flex-direction: column; }
  .card-cover { width: 100%; min-height: 160px; }
  .filter-bar { flex-direction: column; align-items: stretch; }
  .search-input { width: 100%; }
}
</style>
