<template>
    <div class="search-area">
        <!-- 스텝 6: 카드형 박스로 시각적 구분 -->
        <div class="sa-card">
            <div class="sa-header" v-if="config.title">
                <h4 class="sa-title">{{ config.title }}</h4>
            </div>

            <!-- 스텝 3. 조회/초기화 버튼 + 폼 이벤트 -->
            <q-form @submit.prevent="onSubmit" @reset="onClear" class="sa-form">

                <div class="sa-grid">
                    <div
                        v-for="(field, i) in config.fields || []"
                        :key="field.name || i"
                        class="sa-cell"
                        :style="{ gridColumn: 'span ' + getSpan(field) }"
                    >
                        <div class="sa-field">
                            <label class="sa-label">{{ field.label }}</label>
                            <q-select
                                v-if="field.type === 'select'"
                                v-model="model[field.name]"
                                :options="field._options || []"
                                option-label="label"
                                option-value="value"
                                :emit-value="true"
                                :map-options="true"
                                :clearable="true"
                                :use-input="!!field.searchable"
                                :input-debounce="200"
                                dense
                                outlined
                                class="sa-input"
                                :loading="!!field._loading"
                                :placeholder="field.placeholder || '선택하세요'"
                                @filter="(val, update) => onFilter(field, val, update)"
                            />
                            <q-input
                                v-else-if="field.type === 'text' || !field.type"
                                v-model="model[field.name]"
                                dense
                                clearable
                                class="sa-input"
                                :label="null"
                                placeholder="값을 입력하세요"
                            />
                        </div>
                    </div>
                </div>

                <!-- 조회 / 초기화 버튼 -->
                <div class="sa-actions">
                    <q-btn type="reset" class="btn-reset q-mr-sm">초기화</q-btn>
                    <q-btn type="submit" class="btn-search">조회</q-btn>
                </div>
            </q-form>
        </div>
    </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'

import axios from 'axios'

// 스텝 1. emit, config 세팅
const props = defineProps({
    modelValue: { type: Object, required: true }
})
const emit = defineEmits(['update:modelValue', 'submit'])

const config = computed({
    get: () => props.modelValue || {},
    set: v  => emit('update:modelValue', v ?? {})
})

// 스텝 2. config.model 보정. 그릇( model )을 항상 비어있는 그릇이라도 준비해 둔다
const model = computed({
    get: () => {
        const c = config.value
        if (!c.model || typeof c.model !== 'object') {
            c.model = {}
            emit('update:modelValue', { ...c })
        }
        return c.model
    },
    set: (v) => {
        const c = config.value
        c.model = v && typeof v === 'object' ? v : {}
        emit('update:modelValue', { ...c })
    }
})

/**
 * col 세팅하기
 */
const gridCols = computed(() => {
    const n = Number(config.value?.grid ?? 4)
    return Number.isFinite(n) && n >= 1 ? Math.floor(n) : 4
})
function getSpan(field) {
    const n = Number(field?.col ?? 1)
    if (!Number.isFinite(n)) return 1
    return Math.min(Math.max(1, Math.floor(n)), gridCols.value)
}

/**
 * select 박스 형식 세팅하기
 */
async function loadSelectOptions(field, keyword) {
    try {
        field._loading = true
        let list = []

        if (field.optionsUrl) {
            const token = localStorage.getItem('accessToken')
            const headers = token ? { Authorization: `Bearer ${token}` } : {}

            // 쿼리 파라미터 구성(검색어/추가파라미터)
            const params = { ...(field.params || {}) }
            if (keyword && field.searchParam) {
                params[field.searchParam] = keyword
            }

            const { data } = await axios.get(field.optionsUrl, { params, headers })
            console.log('data???', data)
            // 맵핑 규칙: optionLabel/optionValue
            const labelKey = field.optionLabel || 'label'
            const valueKey = field.optionValue || 'value'
            list = (Array.isArray(data) ? data : (data?.list || []))
                .map(d => ({
                    label: d[labelKey] ?? d.name ?? d.label ?? String(d[valueKey] ?? ''),
                    value: d[valueKey] ?? d.id ?? d.value
                }))
        } else if (Array.isArray(field.options)) {
            // 정적 옵션도 지원
            list = field.options.map(o => ({
                label: o.label ?? String(o.value ?? o),
                value: o.value ?? o
            }))
        }

        field._options = list
    } catch (e) {
        console.error('[SearchArea] 옵션 로딩 실패:', field.name, e)
        field._options = Array.isArray(field.options)
            ? field.options
            : []
    } finally {
        field._loading = false
    }
}
function onFilter(field, val, update) {
    if (!field.searchable || !field.searchParam || !field.optionsUrl) {
        return update()
    }
    // 디바운스는 컴포넌트 쪽에서 200ms 적용됨
    loadSelectOptions(field, val).then(() => update(() => {
        /* options는 field._options에 이미 담김 */
    }))
}
async function initSelects() {
    const fields = config.value?.fields || []
    const selectFields = fields.filter(f => f.type === 'select')
    await Promise.all(selectFields.map(f => loadSelectOptions(f)))
}

// 스텝 3 + 스텝 5. 조회/초기화 동작
function onSubmit() {
    // 1. 현재 모델 복사
    const payload = { ...(model.value || {}) }
    // 2. '' -> null 치환
    for (const k in payload) {
        if (Object.prototype.hasOwnProperty.call(payload, k) && payload[k] === '') {
            payload[k] = null
        }
    }
    // 3. 부모의 SUBMIT 이벤트 실행
    config.value?.events?.SUBMIT?.({
        value: payload
    })
}
function onClear() {
    // 모델을 초기화(빈 객체로 교체) → 부모와 동기화
    model.value = {}
    config.value?.events?.RESET?.()
}

onMounted(() => {
    initSelects()
})
</script>

<style scoped>
.search-area {
    width: 100%;
    max-width: 1000px;   /* TableArea와 동일 수치 */
    margin: 0 auto 30px; /* 가운데 정렬 + 아래 간격 */
    box-sizing: border-box;
}

.sa-card {
    width: 100%;         /* 카드가 컨테이너 폭을 꽉 채우도록 */
}

/* 카드형 박스 */
.sa-card {
    border: 1px solid #e6e9ee;
    border-radius: 12px;
    background: #ffffff;
    padding: 16px 18px 14px;
    margin-bottom: 18px;
    box-shadow: 0 4px 14px rgba(2, 6, 23, 0.06);
}

/* 제목 영역 */
.sa-header {
    margin-bottom: 12px;
    padding-bottom: 10px;
    border-bottom: 1px dashed #e8edf5;
}
.sa-title {
    margin: 0 0 2px 0;
    font-size: 1.08rem;
    font-weight: 700;
    color: #1e293b;
    letter-spacing: -0.01em;
}

/* 폼 레이아웃 */
.sa-form {
    display: flex;
    flex-direction: column;
    gap: 14px;
}

/* 그리드 */
.sa-grid {
    display: grid;
    grid-template-columns: repeat(var(--sa-grid-cols, 4), 1fr);
    column-gap: 16px;
    row-gap: 14px;
    margin-top: 15px;
    margin-bottom: 10px;
}
@media (min-width: 1280px) {
    .sa-grid { grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); }
}

/* 좌라벨·우인풋 */
.sa-field {
    display: flex;
    align-items: center;
    gap: 10px;
    min-height: 38px;
}
.sa-label {
    width: 92px;
    text-align: right;
    color: #374151;
    font-weight: 600;
    letter-spacing: -0.01em;
    flex-shrink: 0;
}
.sa-input { flex: 1; }

/* 인풋 룩앤필 */
/* 스텝 6-C: 한 줄 입력칸 텍스트/커서 완전 중앙 정렬 */
.sa-input {
    /* 여기 숫자만 바꾸면 전체 높이 일괄 변경 가능 (38~44 권장) */
    --sa-input-h: 40px;
}

/* 컨트롤 래퍼 높이/정렬 */
.sa-input :deep(.q-field__control) {
    height: var(--sa-input-h);
    min-height: var(--sa-input-h);
    display: flex;
    align-items: center;               /* 수직 중앙 */
    border-radius: 10px;
    background: #f9fafb;
    border: 1px solid #e5e7eb;
    transition: background .15s ease, border-color .15s ease, box-shadow .15s ease;
}

/* 아이콘/접미사 영역도 높이 맞춰 중앙정렬 */
.sa-input :deep(.q-field__marginal) {
    height: var(--sa-input-h);
    display: flex;
    align-items: center;               /* 수직 중앙 */
}

/* 실제 input 요소(한 줄) 중앙 정렬 핵심: line-height = height */
.sa-input :deep(input.q-field__native) {
    height: var(--sa-input-h);
    line-height: var(--sa-input-h);    /* 글자/커서가 가운데로 */
    padding-top: 0;
    padding-bottom: 0;
    padding-left: 10px;
    box-sizing: border-box;
}

/* hover/focus 룩앤필(옵션) */
.sa-input :deep(.q-field:hover .q-field__control) {
    background: #ffffff;
    border-color: #d5d9e0;
}
.sa-input :deep(.q-field--focused .q-field__control) {
    background: #ffffff;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59,130,246,0.15);
}

/* textarea는 여러 줄이니 중앙정렬 대신 자연스러운 패딩으로 */
.sa-input :deep(textarea.q-field__native) {
    height: auto;
    line-height: 1.4;
    padding-top: 8px;
    padding-bottom: 8px;
}


/* 하단 버튼 정렬 */
.sa-actions {
  display: flex;
  justify-content: center;   /* 중앙 정렬 */
  gap: 10px;
  margin-top: 10px;
  margin-bottom: 10px;
  padding-top: 25px;
  border-top: 1px solid #f1f5f9;
}


/* 모바일 */
@media (max-width: 640px) {
    .sa-field {
        flex-direction: column;
        align-items: stretch;
        gap: 6px;
    }
    .sa-label {
        width: auto;
        text-align: left;
        font-weight: 700;
    }
}

/* 조회 버튼 (톤다운 블루) */
.btn-search.q-btn {
  background-color: #60a5fa;   /* 연한 하늘빛 블루 */
  border: 1px solid #3b82f6;
  color: #fff;
  border-radius: 8px;
  font-weight: 600;
  padding: 6px 23px;
  box-shadow: 0 1.5px 4px rgba(96, 165, 250, 0.25);
}
.btn-search.q-btn:hover { background-color: #3b82f6; }

/* 초기화 버튼 (라이트 그레이) */
.btn-reset.q-btn {
  background-color: #f9fafb;
  color: #374151;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-weight: 500;
  padding: 6px 18px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}
.btn-reset.q-btn:hover { background-color: #f3f4f6; }
</style>

