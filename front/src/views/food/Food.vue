<template>
  <div class="food-wrapper">
    <div class="header-row">
      <h2>식단 관리</h2>
    </div>

    <!-- 리스트/맛집 화면에서만 탭 노출 -->
    <q-tabs v-if="isListRoute" class="custom-tabs" dense>
      <q-route-tab :to="{ name: 'FoodRecipeList' }" label="레시피" />
      <q-route-tab :to="{ name: 'RestaurantList' }" label="맛집" />
    </q-tabs>

    <!-- 자식 라우트 렌더: 리스트/맛집은 캐싱, 상세는 비캐싱 -->
    <router-view v-slot="{ Component, route: r }">
      <keep-alive :include="CACHE_NAMES">
        <component :is="Component" :key="r.fullPath" />
      </keep-alive>
    </router-view>
  </div>
</template>


<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import RecipeVue from './Recipe.vue'
import RestaurantVue from './Restaurant.vue'

const tab = ref('recipeTap')
const viewType = ref('list')
const route = useRoute()

const isDetail = computed(() => route.name === 'FoodRecipeView')

const LIST_NAMES = ['FoodRecipeList','RestaurantList']
const isListRoute = computed(() => LIST_NAMES.includes(route.name || '')) // <== 추가

</script>

<style scoped>
.food-wrapper {
  max-width: 1200px;
  margin: 40px auto;
  padding: 32px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.header-row h2 {
  font-size: 30px;
  font-weight: 600;
  margin: 0;
}

.custom-tabs {
  background-color: #ffffff;
  border-bottom: 1px solid #e0e6f1;
  padding-left: 8px;
  margin-bottom: 16px;
}

.custom-tabs .q-tab {
  padding: 5px 25px;
  font-size: 18px;
  font-weight: 500;
  color: #555;
  border-bottom: 2px solid transparent;
  transition: all 0.2s ease-in-out;
}

.custom-tabs .q-tab.q-tab--active {
  border-bottom: 2px solid #3B82F6;
  color: #3B82F6;
  background-color: #e8f0fe;
  font-weight: 600;
  border-radius: 8px 8px 0 0;
}

.custom-tabs .q-tab:hover {
  background-color: #dbeafe;
  color: #2563EB;
  border-radius: 8px 8px 0 0;
}

.view-toggle {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-bottom: 12px;
}

.view-btn {
  background-color: #f1f5f9;
  color: #3B82F6;
  border: 1px solid #cbd5e1;
  padding: 6px 10px;
  font-size: 0.85rem;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s;
}

.view-btn.active {
  background-color: #3B82F6;
  color: white;
  border-color: #3B82F6;
  font-weight: 600;
}

.list-view ul {
  list-style: none;
  padding: 0;
}

.list-view li {
  padding: 12px;
  border-bottom: 1px solid #eee;
  font-size: 16px;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.grid-box {
  background-color: #f9fbfe;
  border: 1px solid #dbeafe;
  border-radius: 10px;
  padding: 12px;
  text-align: center;
}

.thumbnail {
  font-size: 40px;
  margin-bottom: 8px;
}

.thumb-title {
  font-size: 14px;
  color: #333;
}
</style>
