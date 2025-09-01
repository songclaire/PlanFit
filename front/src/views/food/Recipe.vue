<template>
  <div class="food-wrapper">
    <div class="header-row">
        <h2>레시피</h2>
    </div>
    <q-tabs v-model="tab" @update:model-value="(e) => changeTab(e)">
    </q-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RecipeVue from './Recipe.vue'

const showDetailForm = ref(false)

const dummyList = ref([
  { id: 1, name: '김치찌개' },
  { id: 2, name: '된장찌개' },
  { id: 3, name: '불고기' }
])
</script>

<style scoped>
.food-wrapper {
  max-width: 1200px; /* 일정관리와 동일하게 늘림 */
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

.main-layout {
  display: flex;
  gap: 24px;
}

.menu-list-area {
  flex: 2;
}

.detail-area {
  flex: 1;
  background-color: #f9fbfe;
  border-radius: 10px;
  padding: 20px;
  border: 1px solid #e0e6f1;
}

.add-button {
  background-color: #fce4ec;
  color: #d81b60;
  border: 1px solid #f8bbd0;
  padding: 7px 12px;
  border-radius: 6px;
  font-size: 0.92rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-button:hover {
  background-color: #f8bbd0;
  color: #ad1457;
}

</style>
