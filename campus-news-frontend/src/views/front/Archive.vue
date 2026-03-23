<template>
  <div class="archive-page">
    <div class="archive-header">
      <h1>📅 新闻归档</h1>
      <p class="archive-desc">按时间浏览所有校园新闻</p>
    </div>

    <!-- 分类筛选 -->
    <div class="archive-filter">
      <button
        class="filter-btn"
        :class="{ active: activeCategory === '' }"
        @click="activeCategory = ''"
      >全部</button>
      <button
        v-for="c in categories"
        :key="c.id"
        class="filter-btn"
        :class="{ active: activeCategory === c.id }"
        @click="activeCategory = c.id"
      >{{ categoryIcon(c.name) }} {{ c.name }}</button>
    </div>

    <div v-loading="loading" class="archive-body">
      <div v-if="Object.keys(groupedNews).length === 0 && !loading" class="empty-state">
        <div class="empty-icon">📭</div>
        <p>暂无新闻</p>
      </div>

      <!-- 按月分组的时间线 -->
      <div v-for="(items, month) in groupedNews" :key="month" class="month-group">
        <div class="month-header">
          <span class="month-label">{{ month }}</span>
          <span class="month-count">{{ items.length }} 篇</span>
        </div>
        <div class="timeline">
          <div
            v-for="item in items"
            :key="item.id"
            class="timeline-item"
            @click="$router.push(`/news/${item.id}`)"
          >
            <div class="timeline-dot"></div>
            <div class="timeline-card">
              <div class="card-left">
                <div class="card-cover" v-if="item.coverImage">
                  <img :src="item.coverImage" :alt="item.title" />
                </div>
                <div class="card-cover placeholder" v-else>
                  <span>{{ item.categoryName?.charAt(0) || '?' }}</span>
                </div>
              </div>
              <div class="card-right">
                <div class="card-tags">
                  <el-tag v-if="item.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
                  <el-tag type="info" size="small" effect="plain">{{ item.categoryName }}</el-tag>
                </div>
                <h3 class="card-title">{{ item.title }}</h3>
                <p class="card-summary" v-if="item.summary">{{ item.summary }}</p>
                <div class="card-meta">
                  <span>{{ item.authorName }}</span>
                  <span>{{ formatDay(item.publishTime) }}</span>
                  <span>👁 {{ item.viewCount }}</span>
                  <span>👍 {{ item.likeCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载更多 -->
    <div class="load-more" v-if="hasMore">
      <el-button :loading="loading" @click="loadMore" round>加载更多</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getNewsList } from '../../api/news'
import { getCategories } from '../../api/category'

const categories = ref([])
const activeCategory = ref('')
const allNews = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = 30
const hasMore = ref(true)

function categoryIcon(name) {
  const icons = { '校园动态': '🏫', '通知公告': '📢', '学术科研': '🔬', '文体活动': '⚽', '就业实习': '💼' }
  return icons[name] || '📄'
}

function formatDay(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 10)
}

function formatMonth(dateStr) {
  if (!dateStr) return '未知'
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  return `${y} 年 ${m} 月`
}

const groupedNews = computed(() => {
  const groups = {}
  for (const item of allNews.value) {
    const month = formatMonth(item.publishTime)
    if (!groups[month]) groups[month] = []
    groups[month].push(item)
  }
  return groups
})

async function loadNews(reset = false) {
  if (reset) {
    page.value = 1
    allNews.value = []
    hasMore.value = true
  }
  loading.value = true
  try {
    const res = await getNewsList({
      categoryId: activeCategory.value || undefined,
      page: page.value,
      size: pageSize
    })
    const records = res.data.records
    allNews.value.push(...records)
    hasMore.value = records.length >= pageSize
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value++
  loadNews()
}

watch(activeCategory, () => loadNews(true))

onMounted(async () => {
  const [catRes] = await Promise.all([getCategories(), loadNews()])
  categories.value = catRes.data
})
</script>

<style scoped>
.archive-page {
  max-width: 860px;
  margin: 0 auto;
}

/* Header */
.archive-header {
  text-align: center;
  padding: 32px 0 24px;
}
.archive-header h1 {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0 0 6px;
}
.archive-desc {
  font-size: 14px;
  color: #999;
  margin: 0;
}

/* Filter */
.archive-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 28px;
  justify-content: center;
}
.filter-btn {
  padding: 7px 16px;
  border: 1.5px solid #e8e8e8;
  border-radius: 20px;
  background: #fff;
  font-size: 13px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
}
.filter-btn:hover {
  border-color: #4f6ef7;
  color: #4f6ef7;
}
.filter-btn.active {
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  border-color: transparent;
  color: #fff;
}

/* Empty */
.empty-state {
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

/* Month group */
.month-group {
  margin-bottom: 32px;
}
.month-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
  padding-left: 24px;
}
.month-label {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
}
.month-count {
  font-size: 12px;
  color: #bbb;
  background: #f0f0f0;
  padding: 2px 10px;
  border-radius: 10px;
}

/* Timeline */
.timeline {
  position: relative;
  padding-left: 24px;
}
.timeline::before {
  content: '';
  position: absolute;
  left: 6px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #e8e8f0;
  border-radius: 1px;
}
.timeline-item {
  position: relative;
  margin-bottom: 14px;
}
.timeline-dot {
  position: absolute;
  left: -21px;
  top: 24px;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #fff;
  border: 2.5px solid #4f6ef7;
  z-index: 1;
}
.timeline-card {
  display: flex;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  margin-left: 12px;
}
.timeline-card:hover {
  transform: translateX(4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.07);
}
.card-left {
  flex-shrink: 0;
}
.card-cover {
  width: 160px;
  height: 120px;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}
.timeline-card:hover .card-cover img {
  transform: scale(1.05);
}
.card-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #6366f1;
  font-size: 24px;
  font-weight: 700;
}
.card-right {
  padding: 14px 18px;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.card-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 6px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 4px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-summary {
  font-size: 12px;
  color: #999;
  margin: 0 0 8px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex: 1;
}
.card-meta {
  display: flex;
  gap: 12px;
  font-size: 11px;
  color: #bbb;
}

/* Load more */
.load-more {
  text-align: center;
  padding: 16px 0 32px;
}

/* Responsive */
@media (max-width: 768px) {
  .timeline-card { flex-direction: column; margin-left: 8px; }
  .card-cover { width: 100%; height: 140px; }
  .timeline-dot { left: -19px; }
}
</style>
