<template>
    <div class="recipe-view">
        <div class="recipe-header">
            <h3>레시피 상세화면</h3>
        </div>

        <FormArea v-model="recipeForm" />

        <div class="cmt">
            <div class="cmt__card">
                <div class="cmt__header">
                    <h4 class="cmt__title">댓글</h4>
                    <span class="cmt__count">0</span> <!-- 추후 바인딩 -->
                </div>

                <!-- 작성 영역 자리 (나중에 v-model/버튼 붙임) -->
                <div class="cmt__editor">
                    <div class="cmt__user">
                        <span class="cmt__user-id"> {{ userId }} </span> <!-- 추후 실제 ID 바인딩 -->
                    </div>

                    <div class="cmt__inputbox">
                        <textarea
                            class="cmt__input"
                            placeholder="댓글 입력"
                            maxlength="4000"
                        ></textarea>

                        <div class="cmt__actions">
                            <span class="cmt__counter">0 / 4000</span> <!-- 추후 동적 카운트 -->
                            <div class="cmt__btns">
                                <button type="button" class="btn btn-ghost">취소</button>
                                <button type="button" class="btn btn-primary" @click="saveComment">등록</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 구분선 -->
                <div class="cmt__divider"></div>

                <!-- 목록 영역 자리 (나중에 v-for로 렌더링) -->
                <div class="cmt__list"><!-- 빈 자리 --></div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import FormArea from '../../components/FormArea.vue'   // FormArea 사용

const route = useRoute()
const router = useRouter()
const recipeSn = route.params.recipeSn

// 수정-취소시 데이터 백업용
const originalDetail = ref(null)
const isEdit = ref(false)
const userId = localStorage.getItem('userId')

// 서버값 → 필드에 채워넣는 헬퍼
function applyFields(detail) {
    for (const f of recipeForm.value.fields) {
        f.value = detail?.[f.name] ?? f.value
    }
}

/**
 * 레시피 상세정보 Form
 */
const recipeForm = ref({
    show: true,
    loading: false,
    submitButtons: false,
    buttons: ['list', 'edit'],
    async submit(model) {
        const { data } = await axios.post('/api/recipe/update', { ...model, recipeSn })
        return !!data
    },
    reset() { load() },
    fields: [
        { label: '이미지',     name: 'fileId',      type: 'file',        value: '',  accept: 'image/*', maxSizeMB: 50, errors: [], show: true },
        { label: '레시피명',   name: 'recipeNm',    type: 'text',        value: '',  errors: [], show: true },
        { label: '음식종류',   name: 'typeNm',      type: 'text',        value: '',  errors: [], show: true },
        { label: '음식종류', name: 'foodTypeSn', type: 'select', value: '', options: [], required: true, errors: [], show: false },
        { label: '난이도',     name: 'difficulty',  type: 'star-rating', value: 0,   errors: [], show: true },
        { label: '요리날짜',   name: 'recipeDate',  type: 'date',        value: '',  errors: [], show: true },
        { label: '재료',       name: 'ingredients', type: 'textarea',    value: '',  errors: [], show: true },
        { label: '레시피내용', name: 'recipeCn',    type: 'textarea',    value: '',  errors: [], show: true },
    ],
    setModel(model) {
        for (const f of this.fields) {
            const v = model?.[f.name]
            if (v !== undefined) f.value = v
        }
    },
    events: {
        buttons: {
            list:  { click: () => goList() },
            edit:  { click: () => onClickEdit() },
            save:  { click: ({ value }) => onClickSave(value) },
            cancel:{ click: ({ reset }) => onClickCancel(reset) },
        }
    },
})

// 데이터 가져오기
async function getData(recipeSn) {
    try {
        const token = localStorage.getItem('accessToken')
        const { data } = await axios.get('/api/recipe/detail', {
            params: { recipeSn },
            headers: token ? { Authorization: `Bearer ${token}` } : {}
        })
        recipeForm.value.setModel(data)
        originalDetail.value = JSON.parse(JSON.stringify(data))
        lockForm(true)
    } catch (e) {
        console.error('[detail] 실패', e?.response?.status, e?.response?.data)
    }
}

// 보기와 수정을 한 위치의 버튼으로 조절할 수 있는 중앙스위치
function lockForm(lock = true) {
    for (const f of recipeForm.value.fields) {
        // 텍스트/텍스트에어리어/숫자/이메일/날짜는 readonly만 켜기
        if (['text','email','number','date','textarea'].includes(f.type)) {
            f.readonly = lock            // ← 보기 모드
            f.disabled = false           // ← 회색 안 되게
        }
        // 별점/체크박스는 클릭 막아야 하니 disabled
        else if (['star-rating','checkbox'].includes(f.type)) {
            f.readonly = lock
            f.disabled = lock
        }
        // 파일 업로드는 disabled
        else if (f.type === 'file') {
            f.readonly = lock
            f.disabled = lock
        } else {
            f.readonly = lock
            f.disabled = false
        }
    }

    const fTypeNm = recipeForm.value.fields.find(f => f.name === 'typeNm')
    const fTypeSn = recipeForm.value.fields.find(f => f.name === 'foodTypeSn')

    if (fTypeNm && fTypeSn) {
        fTypeNm.show = lock
        fTypeSn.show = !lock
        fTypeSn.disabled = lock
    }

    recipeForm.value.buttons = lock ? ['list', 'edit'] : ['cancel', 'save']
    recipeForm.value.submitButtons = false
    isEdit.value = !lock
}

// 음식 종류 출력
async function loadFoodTypes() {
    const { data } = await axios.get('/api/foodType/list')
    const opts = (data || []).map(d => ({ label: d.typeNm, value: d.foodTypeSn }))
    const selectField = recipeForm.value.fields.find(f => f.name === 'foodTypeSn')
    if (selectField) selectField.options = opts
}

// 목록 클릭
function goList() {
    router.push({ name: 'FoodRecipeList' })
}

// 수정 클릭
async function onClickEdit() {
    originalDetail.value = JSON.parse(JSON.stringify(
        Object.fromEntries(recipeForm.value.fields.map(f => [f.name, f.value]))
    ))
    await loadFoodTypes()

    const fTypeSn = recipeForm.value.fields.find(f => f.name === 'foodTypeSn')
    const fTypeNm = recipeForm.value.fields.find(f => f.name === 'typeNm')
    if (fTypeSn) {
        if (originalDetail.value?.foodTypeSn) {
            fTypeSn.value = originalDetail.value.foodTypeSn
        } else if (fTypeNm?.value) {
            const hit = (fTypeSn.options || []).find(o => o.label === fTypeNm.value)
            if (hit) fTypeSn.value = hit.value
        }
    }
    lockForm(false)
}

// 취소 클릭
function onClickCancel(reset) {
    // 원본으로 롤백
    if (originalDetail.value) {
        recipeForm.value.setModel(originalDetail.value)
    } else if (typeof reset === 'function') {
        reset()
    }
    lockForm(true)
}

/**
 * 레시피 저장 api
 */
async function onClickSave(model) {
    try {
        if (!model) return
        const payload = { ...model, recipeSn }

        const { data } = await axios.post('/api/recipe/save', payload)
        const fTypeSn = recipeForm.value.fields.find(f => f.name === 'foodTypeSn')
        const fTypeNm = recipeForm.value.fields.find(f => f.name === 'typeNm')
        if (fTypeSn && fTypeNm) {
            const hit = (fTypeSn.options || []).find(o => o.value === fTypeSn.value)
            if (hit) fTypeNm.value = hit.label
        }
        originalDetail.value = JSON.parse(JSON.stringify(model))
        lockForm(true)
        return data
    } catch (err) {
        console.error('등록 실패', err)
    }
}


/**
 * 댓글 저장 api
 */
async function saveComment() {
    try {

    } catch(err) {
        console.error('댓글 등록 실패', err)
    }
}



if (recipeSn == 'new') {
    const recipeModel = {
        recipeSn: null,
    }
} else {
    getData(recipeSn)
}

</script>

<style scoped>
/* 레이아웃 박스 */
.recipe-view {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 8px;
    box-sizing: border-box;
}
.recipe-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 16px 0;
}
.recipe-header h3 {
    font-size: 24px;
    font-weight: 600;
    margin: 0;
    color: #333;
}

/* 버튼 기본 스타일 */
.btn {
    padding: 8px 12px;
    border-radius: 8px;
    font-weight: 600;
    border: 1px solid transparent;
    cursor: pointer;
    line-height: 1;
}

/* 버튼 변형: 목록(list) */
.btn-list {
    background: #f1f5f9;
    color: #0f172a;
    border-color: #e2e8f0;
}
.btn-list:hover {
    background: #e2e8f0;
}

/* 버튼 변형: 수정(edit) */
.btn-edit {
    background: #2563eb;
    color: #fff;
    border-color: #1d4ed8;
}
.btn-edit:hover:not(:disabled) {
    background: #1d4ed8;
}
.btn-edit:disabled {
    opacity: .6;
    cursor: not-allowed;
}
:deep(.form-area .custom-buttons) {
    justify-content: flex-end;
}

/* STYLE: 기존 <style scoped> 맨 아래에 추가 */
:root {
    --pf-card-bg: #fff;
    --pf-card-border: #e5e7eb;
    --pf-title: #111827;
    --pf-muted: #6b7280;
}

.cmt {
    width: 100%;
    margin-top: 16px;
    --pf-card-bg: #fff;
    --pf-card-border: #e5e7eb;
    --pf-title: #111827;
    --pf-muted: #6b7280;
}

.cmt__card {
    background: #f8fafc;
    border: 1px solid var(--pf-card-border, #e5e7eb);
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 2px 8px rgba(0,0,0,.03);
}

.cmt__header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}

.cmt__title {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: var(--pf-title);
}

.cmt__count {
    font-size: 12px;
    color: var(--pf-muted);
    padding: 2px 8px;
    border-radius: 999px;
    background: #f3f4f6;
}

.cmt__editor {
    min-height: 72px;
}

.cmt__divider {
    height: 1px;
    background: #f3f4f6;
    margin: 12px 0;
}

.cmt__list {
    min-height: 80px;
}

/* 작성칸 상단: 사용자 표시 */
.cmt__user {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}
.cmt__user-label {
    font-size: 12px;
    color: var(--pf-muted, #6b7280);
    background: #f3f4f6;
    border-radius: 999px;
    padding: 2px 8px;
}
.cmt__user-id {
    font-size: 12px;
    font-weight: 700;
    color: #111827;
    background: #eef2ff;
    border: 1px solid #dbeafe;
    border-radius: 999px;
    padding: 2px 8px;
}

/* 입력 상자 */
.cmt__inputbox {
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 10px 12px;
}
.cmt__input {
    width: 100%;
    min-height: 10px;
    border: none;
    resize: vertical;
    outline: none;
    font-size: 14px;
    color: #111827;
}
.cmt__input::placeholder { color: #9ca3af; }

/* 하단 액션영역 */
.cmt__actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 8px;
}
.cmt__counter {
    font-size: 12px;
    color: var(--pf-muted, #6b7280);
}
.cmt__btns { display: flex; gap: 8px; }

/* 버튼 재사용 */
.btn { padding: 8px 12px; border-radius: 8px; font-weight: 600; border: 1px solid transparent; cursor: not-allowed; }
.btn-primary { background: #60a5fa; color: #fff; border-color: #60a5fa; }
.btn-ghost { background: #f3f4f6; color: #111827; border-color: #e5e7eb; }
.btn:not(:disabled) { cursor: pointer; }
.btn-ghost:not(:disabled):hover { background: #3b82f6; }




</style>

