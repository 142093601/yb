<template>
  <div class="home">
    <!-- 置顶新闻 -->
    <el-carousel v-if="topNews.length" height="200px" style="margin-bottom:20px;border-radius:8px;overflow:hidden">
      <el-carousel-item v-for="item in topNews" :key="item.id" @click="$router.push(`/news/${item.id}`)">
        <div class="carousel-item">
          <img v-if="item.coverImage" :src="item.coverImage" />
          <div class="carousel-overlay">
            <h3>{{ item.title }}</h3>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类筛选 -->
    <div class="filter-bar">
      <el-radio-group v-model="activeCategory" size="small">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button v-for="c in categories" :key="c.id" :label="c.id">{{ c.name }}</el-radio-button>
      </el-radio-group>
      <el-input v-model="keyword" placeholder="搜索新闻..." prefix-icon="Search" style="width:240px" clearable @clear="loadNews" @keyup.enter="loadNews" />
    </div>

    <!-- 新闻列表 -->
    <div v-loading="loading">
      <div v-if="newsList.length === 0" style="text-align:center;padding:60px 0;color:#999">暂无新闻</div>
      <el-card v-for="item in newsList" :key="item.id" class="news-card" shadow="hover" @click="$router.push(`/news/${item.id}`)">
        <div class="news-item">
          <img v-if="item.coverImage" :src="item.coverImage" class="news-cover" />
          <div class="news-info">
            <div class="news-title">
              <el-tag v-if="item.isTop" type="danger" size="small" style="margin-right:6px">置顶</el-tag>
              {{ item.title }}
            </div>
            <p class="news-summary">{{ item.summary }}</p>
            <div class="news-meta">
              <el-tag size="small">{{ item.categoryName }}</el-tag>
              <span>{{ item.authorName }}</span>
              <span>{{ formatTime(item.publishTime) }}</span>
              <span>👁 {{ item.viewCount }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div style="text-align:center;margin-top:20px" v-if="total > 0">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="10" v-model:current-page="page" @current-change="loadNews" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
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

function formatTime(t) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 16)
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
.carousel-item { height: 100%; position: relative; cursor: pointer; }
.carousel-item img { width: 100%; height: 100%; object-fit: cover; }
.carousel-overlay { position: absolute; bottom: 0; left: 0; right: 0; background: linear-gradient(transparent, rgba(0,0,0,0.7)); padding: 20px; color: #fff; }
.carousel-overlay h3 { margin: 0; font-size: 18px; }
.filter-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.news-card { margin-bottom: 12px; cursor: pointer; }
.news-card:hover { transform: translateY(-2px); transition: transform 0.2s; }
.news-item { display: flex; gap: 16px; }
.news-cover { width: 160px; height: 100px; object-fit: cover; border-radius: 4px; flex-shrink: 0; }
.news-info { flex: 1; min-width: 0; }
.news-title { font-size: 16px; font-weight: 600; margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-summary { color: #666; font-size: 14px; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.news-meta { display: flex; gap: 12px; align-items: center; color: #999; font-size: 13px; }
</style>
