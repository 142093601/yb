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

    <!-- 置顶新闻轮播 -->
    <section class="featured-section" v-if="topNews.length">
      <div class="section-header">
        <h2><span class="section-icon">🔥</span> 热点推荐</h2>
      </div>
      <div class="featured-grid">
        <div
          v-for="(item, index) in topNews.slice(0, 3)"
          :key="item.id"
          class="featured-card"
          :class="{ 'featured-main': index === 0 }"
          @click="$router.push(`/news/${item.id}`)"
        >
          <div class="featured-img" v-if="item.coverImage">
            <img :src="item.coverImage" />
          </div>
          <div class="featured-img placeholder" v-else>
            <span>{{ item.categoryName }}</span>
          </div>
          <div class="featured-overlay">
            <el-tag type="danger" size="small" effect="dark">置顶</el-tag>
            <h3>{{ item.title }}</h3>
            <p v-if="index === 0" class="featured-summary">{{ item.summary }}</p>
            <span class="featured-meta">{{ formatTime(item.publishTime) }} · 👁 {{ item.viewCount }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 主内容区：侧边栏 + 新闻列表 -->
    <div class="content-wrapper">
      <div class="main-content">
        <!-- 分类筛选 -->
        <div class="filter-section">
          <div class="category-tabs">
            <button
              class="cat-tab"
              :class="{ active: activeCategory === '' }"
              @click="activeCategory = ''"
            >
              <span class="cat-icon">📋</span> 全部
            </button>
            <button
              v-for="c in categories"
              :key="c.id"
              class="cat-tab"
              :class="{ active: activeCategory === c.id }"
              @click="activeCategory = c.id"
            >
              <span class="cat-icon">{{ categoryIcon(c.name) }}</span>
              {{ c.name }}
            </button>
          </div>
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
        <div v-loading="loading" class="news-grid">
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

      <!-- 侧边栏 -->
      <aside class="sidebar">
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

        <!-- 分类列表 -->
        <div class="sidebar-card">
          <h3>📁 新闻分类</h3>
          <ul class="sidebar-categories">
            <li v-for="c in categories" :key="c.id" @click="activeCategory = c.id">
              <span class="cat-name">{{ categoryIcon(c.name) }} {{ c.name }}</span>
              <el-icon><ArrowRight /></el-icon>
            </li>
          </ul>
        </div>

        <!-- 热门文章 -->
        <div class="sidebar-card" v-if="hotNews.length">
          <h3>🔥 热门文章</h3>
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
  [...newsList.value].sort((a, b) => b.viewCount - a.viewCount).slice(0, 5)
)

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
  margin-bottom: 32px;
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
.hero-dot {
  color: #fbbf24;
}
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

/* ===== Featured Section ===== */
.featured-section {
  margin-bottom: 32px;
}
.section-header {
  margin-bottom: 16px;
}
.section-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}
.featured-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 16px;
}
.featured-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  height: 220px;
  transition: transform 0.3s, box-shadow 0.3s;
}
.featured-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}
.featured-main {
  grid-row: span 1;
  height: 220px;
}
.featured-img {
  width: 100%;
  height: 100%;
}
.featured-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.featured-img.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: rgba(255, 255, 255, 0.3);
  font-size: 24px;
  font-weight: 700;
}
.featured-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: #fff;
}
.featured-overlay h3 {
  margin: 6px 0 4px;
  font-size: 16px;
  font-weight: 600;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.featured-summary {
  font-size: 13px;
  opacity: 0.8;
  margin: 4px 0 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.featured-meta {
  font-size: 12px;
  opacity: 0.6;
  margin-top: 4px;
  display: inline-block;
}

/* ===== Content Wrapper ===== */
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
}

/* ===== Filter Section ===== */
.filter-section {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}
.category-tabs {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.cat-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border: 1.5px solid #e8e8e8;
  border-radius: 24px;
  background: #fff;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}
.cat-tab:hover {
  border-color: #4f6ef7;
  color: #4f6ef7;
}
.cat-tab.active {
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  border-color: transparent;
  color: #fff;
}
.cat-icon {
  font-size: 14px;
}
.search-box {
  flex-shrink: 0;
}
.search-input {
  width: 240px;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

/* ===== News Grid ===== */
.news-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}
.news-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  display: flex;
  flex-direction: column;
}
.news-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}
.card-cover {
  height: 160px;
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
  font-size: 18px;
  font-weight: 700;
}
.card-body {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
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
  margin: 0 0 8px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-summary {
  font-size: 13px;
  color: #777;
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
  gap: 12px;
  font-size: 12px;
  color: #aaa;
}
.card-author,
.card-views {
  display: flex;
  align-items: center;
  gap: 3px;
}

/* ===== Pagination ===== */
.pagination-wrap {
  text-align: center;
  margin-top: 24px;
  padding-bottom: 8px;
}

/* ===== Sidebar ===== */
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
  margin: 0 0 16px;
}

/* Stats card */
.stats-card .stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.stat-block {
  text-align: center;
  padding: 12px;
  background: #f5f7ff;
  border-radius: 12px;
}
.stat-block-num {
  display: block;
  font-size: 24px;
  font-weight: 800;
  color: #4f6ef7;
}
.stat-block-label {
  font-size: 12px;
  color: #888;
}

/* Sidebar categories */
.sidebar-categories {
  list-style: none;
  margin: 0;
  padding: 0;
}
.sidebar-categories li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 14px;
  color: #444;
}
.sidebar-categories li:hover {
  background: #f5f7ff;
  color: #4f6ef7;
}
.cat-name {
  display: flex;
  align-items: center;
  gap: 6px;
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
}
.hot-list li:last-child {
  border-bottom: none;
}
.hot-list li:hover .hot-title {
  color: #4f6ef7;
}
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

/* Empty state */
.empty-state {
  grid-column: span 2;
  text-align: center;
  padding: 60px 0;
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
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  .sidebar {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
  }
}
@media (max-width: 768px) {
  .hero { padding: 32px 24px; }
  .hero-title { font-size: 28px; }
  .hero-stats { gap: 20px; }
  .featured-grid { grid-template-columns: 1fr; }
  .news-grid { grid-template-columns: 1fr; }
  .sidebar { grid-template-columns: 1fr; }
  .filter-section { flex-direction: column; align-items: stretch; }
  .search-input { width: 100%; }
}
</style>
