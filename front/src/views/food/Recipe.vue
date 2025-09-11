<template>
  <div>
    <div class="recipe-header">
      <h3>레시피 목록</h3>
      <button class="add-button" @click="handleAddRecipe">+ 레시피 추가</button>
    </div>
    <SearchArea v-model="recipeSearch"/>
    <TableArea v-model="recipeTable">
      <template #cell-thumb="{ row }">
        <img :src="`/api/file/download/${row.fileId}`" alt="thumb" class="thumb"
             @error="e => e.target.style.display = 'none'" />
      </template>
    </TableArea>

    <!-- 레시피 추가 모달 -->
    <q-dialog v-model="showAddModal" persistent>
      <q-card style="min-width: 500px;">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">🫒 레시피 추가 🫒</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section>
          <!-- FormArea로 교체 -->
          <FormArea v-model="recipeForm" @success="onCreated" />
        </q-card-section>

      </q-card>
    </q-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import TableArea from '../../components/TableArea.vue'
import FormArea from '../../components/FormArea.vue'
import SearchArea from '../../components/SearchArea.vue'
import { useFormArea } from '../../components/useFormArea.js'
import { MenuTypes } from '@/constants/menuTypes.js'

import axios from 'axios'

const router = useRouter()
const route  = useRoute()

// 모달 ON/OFF
const showAddModal = ref(false)
// 저장 상태
const saving = ref(false)
const tableKey = ref(0)

/**
 * 레시피 조회
 */
const recipeSearch = ref({
    model: {},
    fields: [
        {
            name: 'foodTypeSn',
            label: '음식종류명',
            col: 1,
            type: 'select',
            optionsUrl: '/api/foodType/list',
            optionLabel: 'typeNm',
            optionValue: 'foodTypeSn',
        },
        {
            name: 'recipeNm',
            label: '레시피명',
            col: 2,
            type: 'text',
        },
    ],
    events: {
        SUBMIT(evt) {
            recipeTable.value.search = evt?.value || {}
            recipeTable.value.getList?.()
        },
        RESET() {
            recipeTable.value.search = {}
            recipeTable.value.getList?.()
        }
    }
})

const star = v => {
    const n = Math.max(0, Math.min(5, Number(v) || 0))
    const filled = '<span style="color:#FFD700">★</span>'.repeat(n)
    const empty  = '<span style="color:#ddd">☆</span>'.repeat(5 - n)
    return filled + empty
}

/**
 * 레시피 목록 (TableArea 사용)
**/
const recipeTable = ref({
    url: '/api/recipe/list',
    columns: [
        {
          name: 'thumb', label: '', align: 'center',
          headerStyle:'width:80px;min-width:80px;max-width:50px;padding:80',
          style:'width:80px;min-width:180px;max-width:80px;padding:80'
        },
        { name: 'typeNm', label: '음식종류명', align: 'center' },
        { name: 'recipeNm', label: '레시피명', align: 'center'  },
        { name: 'recipeDate', label: '요리날짜', align: 'center'  },
        { name: 'difficulty', label: '난이도', align: 'center', format: star }
    ],
    items: [],
    options: { rowKey: 'recipeSn' },
    request: {
        method: 'post',
        headers: {},
        buildParams: s => s,
        buildBody: s => s
    },
    errors: {},
    events: {
        'row-click': ({ item }) => {
            console.log('item???', item)
            router.push({ name: 'FoodRecipeView', params: { recipeSn: item.recipeSn } })
        }
    }
})

/**
 * 레시피 등록 폼
 */
const recipeForm = useFormArea({
    name: 'recipeForm',
    submitButtons: true,
    model: {
        foodTypeSn: null,
        recipeNm: '',
        ingredients: '',
        recipeCn: '',
        difficulty: null,
        recipeDate: '',
    },
    fields: [
        { label: '레시피명', name: 'recipeNm', type: 'text', required: true },
        { label: '음식종류', name: 'foodTypeSn', type: 'select', required: true, options: [], placeholder: '선택하세요' },
        { label: '재료', name: 'ingredients', type: 'textarea', required: true },
        { label: '레시피내용', name: 'recipeCn', type: 'textarea', required: true },
        { label: '난이도', name: 'difficulty', type: 'star-rating', required: true },
        { label: '요리날짜', name: 'recipeDate', type: 'date', required: true, value: new Date().toISOString().split('T')[0] },
        {
            label: '이미지',
            name: 'fileId',
            type: 'file',
            value: '',
            accept: 'image/*',
            maxSizeMB: 50,
            required: true,
            props: { menuType: MenuTypes.RECIPE }
        }
    ],
})

/**
 * 레시피 저장 api
 */
async function saveRecipe(model) {
    console.log('[Recipe.submit] saveRecipe 호출')
    console.log('model?', model)
    try {
        if (model) {
            const { data } = await axios.post('/api/recipe/save', model)
            return data;
            console.log('등록 성공!')
        }
    } catch (err) {
        console.error('등록 실패', err)
    }
}
recipeForm.submit = saveRecipe

/**
 * 음식종류 데이터 호출
**/
async function loadFoodTypes() {
    try {
        const { data } = await axios.get('/api/foodType/list')
        const options = (data || []).map(d => ({ label: d.typeNm, value: d.foodTypeSn }))
        const field = recipeForm.fields.find(f => f.name === 'foodTypeSn')
        if (field) field.options = options
    } catch (err) {
        console.error('음식 종류 데이터 호출 실패')
    }
}

/**
 * 레시피 추가 버튼
**/
function handleAddRecipe() {
    showAddModal.value = true
    loadFoodTypes()
}

function onCreated(created) {
    console.log('✅ success 이벤트 도착:', created)
    showAddModal.value = false
    tableKey.value++
}

</script>

<style scoped>
.thumb{
  width: 100px;
  height: 100px;
  border-radius: 6px;
  object-fit: cover;
  display: block; margin: 0 auto;
}

.recipe-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  /* SearchArea/TableArea와 동일한 폭 & 정렬 */
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding: 0 8px; /* 양옆 여백 조금 주면 라인감 딱 맞음 */
  box-sizing: border-box;
}

.recipe-header h3 {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
    color: #333;
}

/* 상단 네이티브 .add-button만 교체 */
/* 레시피 추가 버튼 (코럴 오렌지 계열) */
.add-button {
  background-color: #f97316;   /* 오렌지-코럴 */
  border: 1px solid #f97316;
  color: #fff;
  padding: 8px 14px;
  border-radius: 8px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(249, 115, 22, 0.25);
  transition: background-color .15s ease, box-shadow .15s ease;
}
.add-button:hover {
  background-color: #ea580c;   /* hover 시 살짝 진하게 */
  box-shadow: 0 3px 8px rgba(234, 88, 12, 0.28);
}

/* 레시피 추가 버튼 (채도 낮춘 로지 레드) */
.add-button {
  background-color: #e65a54;   /* muted rosy red */
  border: 1px solid #e65a54;
  color: #fff;
  padding: 8px 14px;
  border-radius: 8px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(230, 90, 84, 0.20);
  transition: background-color .15s ease, box-shadow .15s ease;
}
.add-button:hover {
  background-color: #cc4f4a;   /* 한 톤 다운 */
  box-shadow: 0 3px 8px rgba(204, 79, 74, 0.24);
}

/* 조회 버튼 (딥 블루) */
.btn-search.q-btn {
  background-color: #2563eb;   /* primary blue */
  border: 1px solid #1d4ed8;
  color: #fff;
  border-radius: 8px;
  font-weight: 600;
  padding: 6px 16px;
  box-shadow: 0 1.5px 4px rgba(37, 99, 235, 0.18);
}
.btn-search.q-btn:hover { background-color: #1d4ed8; }
</style>