<template>
  <div class="search-page">
    <!-- 搜索区域 -->
    <div class="search-hero">
      <h1 class="search-title">搜索新闻</h1>
      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="输入关键词搜索新闻..."
          size="large"
          clearable
          @keyup.enter="doSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon :size="20"><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="doSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div v-if="hasSearched" class="results-section">
      <div class="results-header">
        <h2>
          <span v-if="keyword">「{{ keyword }}」的搜索结果</span>
          <span v-else>全部新闻</span>
        </h2>
        <span class="results-count">共 {{ total }} 条</span>
      </div>
      <div v-loading="loading" class="results-list">
        <div v-if="newsList.length === 0 && !loading" class="empty-state">
          <div class="empty-icon">🔍</div>
          <p>没有找到相关内容，换个关键词试试</p>
        </div>
        <article
          v-for="item in newsList"
          :key="item.id"
          class="result-card"
          @click="$router.push(`/news/${item.id}`)"
        >
          <div class="result-cover" v-if="item.coverImage">
            <img :src="item.coverImage" :alt="item.title" />
          </div>
          <div class="result-body">
            <div class="result-tags">
              <el-tag v-if="item.isTop" type="danger" size="small" effect="dark">置顶</el-tag>
              <el-tag type="info" size="small" effect="plain">{{ item.categoryName }}</el-tag>
            </div>
            <h3 class="result-title" v-html="highlight(item.title)"></h3>
            <p class="result-summary" v-if="item.summary">{{ item.summary }}</p>
            <div class="result-meta">
              <span>{{ item.authorName }}</span>
              <span>{{ formatTime(item.publishTime) }}</span>
              <span>👁 {{ item.viewCount }}</span>
              <span>👍 {{ item.likeCount }}</span>
            </div>
          </div>
        </article>
      </div>
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          background
          layout="prev, pager, next, total"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="page"
          @current-change="doSearch"
        />
      </div>
    </div>

    <!-- 热点排行（未搜索时展示） -->
    <div v-else class="hot-section">
      <div class="section-header">
        <h2>🔥 热点排行</h2>
        <span class="section-desc">当前最受关注的校园新闻</span>
      </div>
      <div v-loading="loadingHot" class="hot-grid">
        <div
          v-for="(item, index) in hotList"
          :key="item.id"
          class="hot-card"
          @click="$router.push(`/news/${item.id}`)"
        >
          <div class="hot-rank-badge" :class="{ 'rank-top': index < 3 }">{{ index + 1 }}</div>
          <div class="hot-cover" v-if="item.coverImage">
            <img :src="item.coverImage" :alt="item.title" />
          </div>
          <div class="hot-cover placeholder" v-else>
            <span>{{ item.categoryName }}</span>
          </div>
          <div class="hot-body">
            <el-tag type="info" size="small" effect="plain">{{ item.categoryName }}</el-tag>
            <h3 class="hot-title">{{ item.title }}</h3>
            <p class="hot-summary" v-if="item.summary">{{ item.summary }}</p>
            <div class="hot-stats">
              <span class="hot-stat">
                <el-icon><View /></el-icon> {{ item.viewCount }} 阅读
              </span>
              <span class="hot-stat">
                👍 {{ item.likeCount }}
              </span>
              <span class="hot-stat">
                {{ formatTime(item.publishTime) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNewsList } from '../../api/news'

const keyword = ref('')
const newsList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10
const loading = ref(false)
const hasSearched = ref(false)

const hotList = ref([])
const loadingHot = ref(false)

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 10)
}

function highlight(text) {
  if (!keyword.value || !text) return text
  const escaped = keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return text.replace(new RegExp(escaped, 'gi'), match => `<mark>${match}</mark>`)
}

async function doSearch() {
  hasSearched.value = true
  loading.value = true
  try {
    const res = await getNewsList({
      keyword: keyword.value || undefined,
      page: page.value,
      size: pageSize
    })
    newsList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadHot() {
  loadingHot.value = true
  try {
    // 获取所有新闻，按浏览量排序
    const res = await getNewsList({ page: 1, size: 20 })
    hotList.value = [...res.data.records].sort((a, b) => b.viewCount - a.viewCount).slice(0, 15)
  } finally {
    loadingHot.value = false
  }
}

onMounted(loadHot)
</script>

<style scoped>
.search-page {
  max-width: 900px;
  margin: 0 auto;
}

/* ===== Search Hero ===== */
.search-hero {
  text-align: center;
  padding: 40px 0 32px;
}
.search-title {
  font-size: 28px;
  font-weight: 800;
  color: #1a1a2e;
  margin: 0 0 20px;
}
.search-bar {
  max-width: 640px;
  margin: 0 auto;
}
.search-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  padding: 4px 4px 4px 16px;
  box-shadow: 0 4px 20px rgba(79, 110, 247, 0.12);
}
.search-input :deep(.el-input-group__append) {
  border-radius: 0 12px 12px 0;
  background: linear-gradient(135deg, #4f6ef7, #7c5bf5);
  color: #fff;
  border: none;
  padding: 0 24px;
  box-shadow: none;
}
.search-input :deep(.el-input-group__append .el-button) {
  color: #fff;
  font-weight: 600;
}

/* ===== Results ===== */
.results-section {
  padding-bottom: 24px;
}
.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.results-header h2 {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0;
}
.results-count {
  font-size: 13px;
  color: #999;
}

.results-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.result-card {
  display: flex;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}
.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.07);
}
.result-cover {
  width: 180px;
  min-height: 130px;
  flex-shrink: 0;
}
.result-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.result-body {
  padding: 16px 20px;
  flex: 1;
  min-width: 0;
}
.result-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 8px;
}
.result-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 6px;
  line-height: 1.4;
}
.result-title :deep(mark) {
  background: #fef08a;
  color: inherit;
  padding: 0 2px;
  border-radius: 2px;
}
.result-summary {
  font-size: 13px;
  color: #888;
  margin: 0 0 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.result-meta {
  display: flex;
  gap: 14px;
  font-size: 12px;
  color: #aaa;
}

/* Empty */
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

/* Pagination */
.pagination-wrap {
  text-align: center;
  margin-top: 20px;
}

/* ===== Hot Section ===== */
.hot-section {
  padding-bottom: 24px;
}
.section-header {
  margin-bottom: 20px;
}
.section-header h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 4px;
}
.section-desc {
  font-size: 13px;
  color: #999;
}

.hot-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.hot-card {
  display: flex;
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
  position: relative;
}
.hot-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.07);
}
.hot-rank-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  backdrop-filter: blur(4px);
}
.hot-rank-badge.rank-top {
  background: linear-gradient(135deg, #f59e0b, #ef4444);
}
.hot-cover {
  width: 200px;
  min-height: 140px;
  flex-shrink: 0;
  overflow: hidden;
}
.hot-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}
.hot-card:hover .hot-cover img {
  transform: scale(1.05);
}
.hot-cover.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e0e7ff, #c7d2fe);
  color: #6366f1;
  font-size: 16px;
  font-weight: 700;
}
.hot-body {
  padding: 16px 20px;
  flex: 1;
  min-width: 0;
}
.hot-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 8px 0 6px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hot-summary {
  font-size: 13px;
  color: #888;
  margin: 0 0 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.hot-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.hot-stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #aaa;
}

/* ===== Responsive ===== */
@media (max-width: 768px) {
  .search-hero { padding: 24px 0 20px; }
  .search-title { font-size: 22px; }
  .result-card,
  .hot-card { flex-direction: column; }
  .result-cover,
  .hot-cover { width: 100%; min-height: 160px; }
  .hot-rank-badge { top: 8px; left: 8px; }
}
</style>
