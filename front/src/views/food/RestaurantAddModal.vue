<template>
    <q-dialog v-model="innerShow" persistent>
        <q-card style="min-width:1200px; max-width:1600px;">
            <!-- 헤더 -->
            <q-card-section class="search-row q-pt-sm q-pb-xs">
                <div class="text-h6">🍜 맛집 등록</div>
                <q-space />
                <q-btn icon="close" flat round dense @click="close()" />
            </q-card-section>

            <!-- 본문: 좌/우 2단 -->
            <q-card-section>
                <div class="modal-split">
                    <!-- 왼쪽: 검색/지도/결과 -->
                    <div class="left-pane">
                        <div class="search-row">
                            <q-input
                                v-model="keyword"
                                dense
                                outlined
                                placeholder="가게명, 주소 등으로 검색"
                                @keyup.enter="doSearch"
                                class="search-input"
                            />
                            <q-btn color="primary" label="검색" class="search-btn" @click="doSearch" />
                        </div>

                        <div class="map-pane">
                            <div ref="addMapEl" class="map-box"></div>
                        </div>

                        <div class="result-pane">
                            <div class="result-head">
                                <div class="result-count">검색 결과 <b>{{ results.length }}</b>건</div>
                            </div>
                            <div v-if="loading" class="result-empty">검색 중…</div>
                            <div v-else-if="!results.length" class="result-empty">검색 결과가 없습니다.</div>

                            <ul v-else class="result-list">
                                <li
                                    v-for="(item, i) in results"
                                    :key="item.id || i"
                                    :class="['result-item', { active: activeIndex === i }]"
                                    @click="focusResult(item,i)"
                                >
                                    <div class="ri-left">
                                        <div class="ri-marker">{{ i + 1 }}</div>
                                    </div>
                                    <div class="ri-main">
                                        <div class="ri-top">
                                            <div class="ri-title" :title="item.place_name">{{ item.place_name }}</div>
                                            <div class="ri-tags">
                                                <span class="chip">{{ item.category_group_name || item.category_name }}</span>
                                            </div>
                                        </div>
                                        <div class="ri-sub" :title="item.road_address_name || item.address_name">
                                            {{ item.road_address_name || item.address_name }}
                                        </div>
                                    </div>
                                </li>
                            </ul>

                            <div v-if="totalPages > 1" class="pager">
                                <q-btn flat dense label="이전" :disable="page <= 1" @click="prevPage" />
                                <span class="pager-info">페이지 {{ page }} / {{ totalPages }}</span>
                                <q-btn flat dense label="다음" :disable="page >= totalPages" @click="nextPage" />
                            </div>
                        </div>
                    </div>

                    <!-- 구분선 -->
                    <div class="divider"></div>

                    <!-- 오른쪽: 상세 + 폼 -->
                    <div class="right-pane">
                        <template v-if="selectedPlace">
                            <div class="detail-card">
                                <div class="dc-head">
                                    <div class="dc-title-row">
                                        <h4 class="dc-title" :title="selectedPlace.place_name">
                                            {{ selectedPlace.place_name }}
                                        </h4>
                                        <span v-if="selectedPlace.category_group_name || selectedPlace.category_name"
                                              class="chip chip-inline">
                                            {{ selectedPlace.category_group_name || selectedPlace.category_name }}
                                        </span>
                                        <div class="dc-actions">
                                            <a class="dc-link" :href="selectedPlace.place_url" target="_blank" rel="noopener">
                                                상세보기
                                            </a>
                                        </div>
                                    </div>

                                </div>

                                <div class="dc-body">
                                    <div class="dc-row">
                                        <span class="dc-label">도로명</span>
                                        <span class="dc-value">{{ selectedPlace.road_address_name || '-' }}</span>
                                    </div>
                                    <div class="dc-row" v-if="selectedPlace.phone">
                                        <span class="dc-label">전화번호</span>
                                        <span class="dc-value">{{ selectedPlace.phone }}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="visit-card">
                                <div class="visit-row">
                                    <label class="visit-label">만족도</label>
                                    <div class="star-rating">
                                        <span
                                            v-for="star in 5"
                                            :key="star"
                                            class="star"
                                            :class="{ active: star <= visitInfo.rating }"
                                            @click="visitInfo.rating = star"
                                        >
                                            {{ star <= visitInfo.rating ? '★' : '☆' }}
                                        </span>
                                        <span class="rating-text">{{ visitInfo.rating }}/5</span>
                                    </div>
                                </div>

                                <!-- 추가: 메모 입력창 -->
                                <div class="visit-row">
                                    <label class="visit-label">메모</label>
                                    <q-input
                                        v-model="visitInfo.memo"
                                        type="textarea"
                                        dense
                                        autogrow
                                        class="visit-input memo-input"
                                    />
                                </div>
                            </div>

                            <div class="menu-section">
                                <div class="menu-head">
                                    <h4 class="form-title">메뉴 등록 ({{ menuItems.length }}/3)</h4>
                                </div>

                                <div class="menu-grid">
                                    <!-- **[추가] 메뉴 카드 리스트** -->
                                    <div
                                        v-for="(m, idx) in menuItems"
                                        :key="m.id"
                                        class="menu-card"
                                    >
                                        <!-- ✅ [추가] 좌상단 따봉 버튼 -->
                                        <div class="menu-card-top-left">
                                        <!-- [추가] 추천 토글 버튼 -->
                                        <q-btn
                                            round
                                            dense
                                            flat
                                            ripple="false"
                                            icon="thumb_up"
                                            class="menu-card-like"
                                            @click.stop="m.recommended = !m.recommended"
                                            :class="{ 'is-recommended': m.recommended }"
                                        />
                                        </div>


                                        <div class="menu-card-top">
                                            <q-btn
                                                flat
                                                round
                                                dense
                                                icon="close"
                                                class="menu-card-del"
                                                @click="removeMenu(idx)"
                                                :title="'삭제'"
                                            />
                                        </div>

                                        <div class="menu-field">
                                            <label class="menuLabel">메뉴</label>
                                            <q-input
                                                v-model="m.name"
                                                dense
                                                outlined
                                                class="menu-input"
                                            />
                                        </div>

                                        <div class="menu-field">
                                            <label class="priceLabel">가격</label>
                                            <q-input
                                                v-model.number="m.price"
                                                type="number"
                                                dense
                                                outlined
                                                :min="0"
                                                class="menu-input"
                                            />
                                        </div>

                                        <div class="menu-photo">
                                            <div class="photo-box" @click="openFile(idx)">
                                                <img v-if="m.previewUrl" :src="m.previewUrl" alt="preview" />
                                                <div v-else class="photo-empty">사진 첨부</div>
                                            </div>
                                            <input
                                                class="file-hidden"
                                                type="file"
                                                accept="image/*"
                                                :ref="el => fileInputs[idx] = el"
                                                @change="onFileChange(idx, $event)"
                                            />
                                        </div>
                                    </div>

                                    <!-- **[추가] 빈 슬롯 카드 (3개 미만일 때 클릭으로 추가)** -->
                                    <div
                                        v-if="menuItems.length < MAX_MENUS"
                                        class="menu-card-empty"
                                        @click="addMenuAfter(menuItems.length - 1)"
                                    >
                                        <div class="empty-plus">+</div>
                                    </div>
                                </div>
                            </div>

                            <q-btn color="primary" label="저장" class="q-mt-sm" @click="save()" />
                        </template>

                        <div v-else class="placeholder">👉 왼쪽에서 맛집을 검색해서 클릭해주세요.</div>
                    </div>
                </div>
            </q-card-section>
        </q-card>
    </q-dialog>
</template>

<script setup>
import { ref, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useFormArea } from '../../components/useFormArea.js'
import FormArea from '../../components/FormArea.vue'

defineOptions({ name: 'RestaurantAddModal' })
/** Props / Emits */
const props = defineProps({
    modelValue: { type: Boolean, required: true },
    initialCenter: { type: Object, default: () => ({ lat: 37.5665, lng: 126.9780 }) }
})
const emit = defineEmits(['update:modelValue', 'saved'])

/** Dialog state (v-model proxy) */
const innerShow = ref(props.modelValue)
watch(() => props.modelValue, v => (innerShow.value = v))
watch(innerShow, v => emit('update:modelValue', v))

/** Kakao map refs */
const addMapEl = ref(null)
let addMap = null
let addMarker = null
let addMapClickHandler = null
let ps = null
let markers = []

/** Search state */
const keyword = ref('')
const results = ref([])
const loading = ref(false)
const activeIndex = ref(-1)
const page = ref(1)
const totalPages = ref(1)
let kakaoPagination = null

/** Selection */
const selectedPlace = ref(null)
const selectedPos = ref(null) // {lat,lng}

/** Open/close lifecycle */
watch(innerShow, async open => {
    if (!open) {
        cleanup()
        return
    }
    await nextTick()
    if (!addMapEl.value || !window.kakao) return

    window.kakao.maps.load(() => {
        const center = new window.kakao.maps.LatLng(props.initialCenter.lat, props.initialCenter.lng)
        setTimeout(() => {
            addMap = new window.kakao.maps.Map(addMapEl.value, { center, level: 5 })
            ps = new window.kakao.maps.services.Places()

            addMarker = new window.kakao.maps.Marker({ position: center, draggable: true })
            addMarker.setMap(addMap)

            addMapClickHandler = e => {
                const ll = e.latLng
                addMarker.setPosition(ll)
                selectedPos.value = { lat: ll.getLat(), lng: ll.getLng() }
            }
            window.kakao.maps.event.addListener(addMap, 'click', addMapClickHandler)

            addMap.relayout()
            addMap.setCenter(center)
            selectedPos.value = { lat: center.getLat(), lng: center.getLng() }
        }, 120)
    })
})

function doSearch() {
    if (!ps || !keyword.value) return
    loading.value = true
    page.value = 1
    ps.keywordSearch(
        keyword.value,
        (data, status, pagination) => {
            loading.value = false
            if (status !== window.kakao.maps.services.Status.OK || !data?.length) {
                results.value = []
                clearResultMarkers()
                totalPages.value = 1
                kakaoPagination = null
                return
            }
            results.value = data
            drawResultMarkers(data)
            kakaoPagination = pagination
            page.value = pagination.current
            totalPages.value = pagination.last
        },
        { size: 3, page: page.value }
    )
}

function focusResult(item, i) {
    selectedPlace.value = item
    activeIndex.value = i
    const pos = new window.kakao.maps.LatLng(item.y, item.x)
    addMarker.setPosition(pos)
    addMap.setCenter(pos)
    selectedPos.value = { lat: parseFloat(item.y), lng: parseFloat(item.x) }
}

function drawResultMarkers(list) {
    clearResultMarkers()
    const bounds = new window.kakao.maps.LatLngBounds()
    list.forEach((item, i) => {
        const pos = new window.kakao.maps.LatLng(item.y, item.x)
        const m = new window.kakao.maps.Marker({ position: pos, map: addMap })
        window.kakao.maps.event.addListener(m, 'click', () => focusResult(item, i))
        markers.push(m)
        bounds.extend(pos)
    })
    if (!bounds.isEmpty()) addMap.setBounds(bounds)
}

function clearResultMarkers() {
    markers.forEach(m => m.setMap(null))
    markers = []
}

// 페이지네이션
function gotoPage(n) {
    if (kakaoPagination && n >= 1 && n <= kakaoPagination.last) kakaoPagination.gotoPage(n)
}
function prevPage() {
    gotoPage(page.value - 1)
}
function nextPage() {
    gotoPage(page.value + 1)
}

function save() {
    if (!selectedPos.value) {
        alert('지도를 클릭하거나 검색 결과를 선택해 위치를 지정해 주세요.')
        return
    }
    const invalid = menuItems.value.some(m => !m.name?.trim() || (m.price ?? -1) < 0)
    if (invalid) {
        alert('메뉴명과 0 이상 가격을 입력해 주세요.')
        return
    }
    const payload = {
        position: selectedPos.value,
        place: selectedPlace.value,
        visitInfo: { ...visitInfo.value },
        menuItems: menuItems.value.map(m => ({
            name: m.name?.trim(),
            price: Number(m.price ?? 0),
            file: m.file || null,
            recommended: !!m.recommended
        }))
    }
    emit('saved', payload)
    innerShow.value = false
}

function close() {
    innerShow.value = false
}

function cleanup() {
    try {
        if (addMap && addMapClickHandler) {
            window.kakao.maps.event.removeListener(addMap, 'click', addMapClickHandler)
        }
        clearResultMarkers()
    } finally {
        menuItems.value.forEach(m => m.previewUrl && URL.revokeObjectURL(m.previewUrl))
        menuItems.value = []
        fileInputs.value = []
        visitInfo.value = { visitDate: new Date().toISOString().split('T')[0], rating: 0 }

        addMap = null
        addMarker = null
        addMapClickHandler = null
        results.value = []
        selectedPlace.value = null
        kakaoPagination = null
        page.value = 1
        totalPages.value = 1
    }
}


const MAX_MENUS = 3

const visitInfo = ref({
    visitDate: new Date().toISOString().split('T')[0],
    rating: 0
})

const menuItems = ref([])          // { id, name, price, file, previewUrl }
const fileInputs = ref([])

watch(innerShow, open => {
    if (open && menuItems.value.length === 0) {
        addMenuAfter(-1)
    }
})

function uid() {
    return Math.random().toString(36).slice(2, 10)
}

function addMenuAfter(index) {
    if (menuItems.value.length >= MAX_MENUS) return
    const newItem = {
        id: uid(),
        name: '',
        price: null,
        file: null,
        previewUrl: '',
        recommended: false
    }
    // index 뒤에 삽입 (index=-1이면 맨 앞에)
    const pos = Math.max(-1, index) + 1
    menuItems.value.splice(pos, 0, newItem)
}

function removeMenu(idx) {
    const it = menuItems.value[idx]
    if (it?.previewUrl) URL.revokeObjectURL(it.previewUrl)
    menuItems.value.splice(idx, 1)
    fileInputs.value.splice(idx, 1)
}

function openFile(idx) {
    const el = fileInputs.value[idx]
    el && el.click()
}

function onFileChange(idx, e) {
    const file = e.target.files?.[0]
    if (!file) return
    const it = menuItems.value[idx]
    if (!it) return
    if (it.previewUrl) URL.revokeObjectURL(it.previewUrl)
    it.file = file
    it.previewUrl = URL.createObjectURL(file)
}

onBeforeUnmount(cleanup)
</script>



<style scoped>
/* =========================
   레이아웃 (Layout)
   ========================= */
.modal-split {
    display: grid;
    grid-template-columns: 1fr 1px 1fr;
    min-height: 600px;
}
.left-pane,
.right-pane {
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.divider {
    width: 1px;
    background: #e2e8f0;
}

/* =========================
   지도/검색 (Map & Search)
   ========================= */
.map-pane {
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    overflow: hidden;
}
.map-box {
    width: 100%;
    height: 400px;
}
.search-row {
    display: flex;
    gap: 8px;
    align-items: center;
    margin-top: 15px;
}
.search-input { flex: 1; }
.search-btn { height: 40px; }

/* =========================
   검색 결과 리스트 (Results)
   ========================= */
.result-pane {
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    background: #ffffff;
    overflow: auto;
    padding: 8px;
}
.result-list {
    list-style: none;
    margin: 0;
    padding: 0;
    display: flex;
    flex-direction: column;
    gap: 8px;
}
.result-item {
    display: flex;
    gap: 10px;
    padding: 12px 16px;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    background: #ffffff;
    cursor: pointer;
    transition: transform 0.12s,
                box-shadow 0.12s,
                border-color 0.12s,
                background 0.12s;
}
.result-item:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(2, 6, 23, 0.06);
    border-color: #dbeafe;
}
.result-item.active {
    border-color: #2563eb;
    background: #f8fbff;
    box-shadow: 0 6px 18px rgba(37, 99, 235, 0.10);
}
.ri-left { flex: 0 0 auto; }
.ri-marker {
    width: 26px;
    height: 26px;
    border-radius: 8px;
    display: grid;
    place-items: center;
    font-size: 12px;
    font-weight: 700;
    color: #1e3a8a;
    background: #e0ecff;
    border: 1px solid #bfdbfe;
}
.ri-main {
    flex: 1 1 auto;
    min-width: 0;
}
.ri-top {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;
}
.ri-title {
    font-size: 15px;
    font-weight: 700;
    color: #0f172a;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 160px;
}
.ri-sub {
    font-size: 13px;
    color: #475569;
    line-height: 1.3;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.chip {
    display: inline-flex;
    align-items: center;
    padding: 4px 8px;
    border-radius: 999px;
    background: #eef2ff;
    color: #3730a3;
    border: 1px solid #e0e7ff;
    font-size: 11px;
    font-weight: 600;
}
.chip-ghost {
    background: #f8fafc;
    color: #334155;
    border: 1px solid #e2e8f0;
}

/* =========================
   상세 카드 (Detail Card)
   ========================= */
.detail-card {
    border: 1px solid #e5e7eb;
    border-radius: 14px;
    background: radial-gradient(1200px 200px at 100% -20%, rgba(59, 130, 246, 0.08), transparent 60%),
                #ffffff;
    box-shadow: 0 6px 22px rgba(2, 6, 23, 0.06);
    overflow: hidden;
}
.dc-head {
    padding: 16px 18px 12px;
    border-bottom: 1px solid #eef2ff;
    background: linear-gradient(180deg, #f8fbff 0%, #ffffff 60%);
}
.dc-title-row {
    display: flex;
    align-items: center;
    justify-content: space-between; /* 왼쪽: 상호명+칩 / 오른쪽: 상세보기 */
}
.dc-title {
    margin: 0;
    font-size: 18px;
    font-weight: 800;
    color: #0f172a;
    letter-spacing: -0.2px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.dc-actions { margin-left: auto; }
.dc-link {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 8px 10px;
    border-radius: 10px;
    border: 1px solid #dbeafe;
    background: #eff6ff;
    color: #1d4ed8;
    text-decoration: none;
    font-weight: 700;
    font-size: 12px;
}
.dc-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-top: 10px;
}
.dc-body {
    display: grid;
    grid-template-columns: 80px 1fr;
    column-gap: 10px;
    row-gap: 10px;
    padding: 16px 18px 18px;
}
.dc-row { display: contents; } /* div 래퍼 제거 후 grid에 직접 배치 */
.dc-label {
    color: #334155;
    font-weight: 600;
    font-size: 13px;
}
.dc-value {
    color: #0f172a;
    font-size: 14px;
    font-weight: 500;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.chip-inline {
    font-size: 12px;
    margin-left: 6px;
    background: #f1f5f9;
    color: #334155;
    border: 1px solid #e2e8f0;
}

/* =========================
   페이징/플레이스홀더
   ========================= */
.pager {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding-top: 10px;
}
.pager-info {
    font-size: 12px;
    color: #64748b;
}
.placeholder {
    border: 2px dashed #cbd5e1;
    border-radius: 12px;
    padding: 28px;
    color: #64748b;
    text-align: center;
}

/* =========================
   폼 타이틀 (Form headings)
   ========================= */
.form-card { padding-top: 8px; }
.form-head {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 0 6px;
    padding-bottom: 10px;
    padding-left: 5px;
}
.form-title {
    margin: 0;
    font-size: 17px;
    font-weight: 800;
    color: #1e293b;
    letter-spacing: -0.2px;
    line-height: 1.3;
}
.form-card :deep(.q-btn) { margin-top: 8px; }

/* =========================
   방문 정보 (만족도/메모)
   ========================= */
.visit-card {
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    padding: 12px;
    background: #fff;
    display: grid;
    grid-template-columns: 70px 1fr;
    gap: 10px 12px;
}
.visit-row { display: contents; }
.visit-label {
    padding-top: 6px;
    padding-left: 6px;
    font-weight: 600;
    color: #334155;
}
.visit-input {
    width: 220px;
    padding: 8px 10px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 14px;
}
.star-rating {
    display: inline-flex;
    align-items: center;
    gap: 6px;
}
.star {
    cursor: pointer;
    font-size: 22px;
    color: #ddd;
    transition: color .15s;
}
.star.active { color: #ffd700; }
.rating-text {
    font-size: 13px;
    color: #64748b;
    margin-left: 4px;
}

.visit-card .memo-input.q-field {
    width: 100% !important;
    max-width: none;
}

/* =========================
   메뉴 섹션/카드 (Menu)
   ========================= */
.menu-section { margin-top: 6px; }
.menu-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 4px 2px 8px;
}
.menu-grid {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
}
.menu-card {
    position: relative;
    border: 1px solid #e5e7eb;
    border-radius: 12px;
    background: #fff;
    padding: 10px;
    padding-top: 49px;
    min-height: 220px;
    display: grid;
    grid-template-rows: 1fr 1fr;
    grid-auto-rows: auto;
    gap: 8px;
    box-shadow: 0 4px 14px rgba(2, 6, 23, 0.04);
}
.menu-card .menu-field {
    display: grid;
    grid-template-columns: 30px 1fr;    /* 라벨 고정폭, 입력칸 유연 */
    align-items: center;
    column-gap: 8px;
}
.menu-card .menu-field label {
    margin: 0;
    padding: 0 0 0 3px;                 /* 좌측 살짝만 */
}
.menu-card .menu-input.q-field {
    width: 100%;
    min-width: 0;                       /* flex/grid 수축 허용 */
}
.menu-card .menu-photo {
    grid-column: 1 / -1;
}
@media (max-width: 720px) {
    .menu-card {
        grid-template-columns: 1fr;     /* 한 컬럼으로 복귀 */
    }
    .menu-card .menu-photo {
        grid-column: auto;              /* 기본 배치 */
    }
}
.menu-card-top {
    position: absolute;
    top: 6px;
    right: 6px;
}
.menu-card-del {
    width: 20px !important;
    height: 20px !important;
    padding: 0 !important;
    border-radius: 10px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    transition: background .12s, transform .12s, box-shadow .12s;
}
.menu-card-del:hover { opacity: 1; }
.menu-field label {
    display: block;
    font-size: 12px;
    color: #64748b;
    margin-bottom: 4px;
    font-weight: 600;
}
.menuLabel { padding-left: 3px; }
.priceLabel { padding-left: 3px; }
.menu-photo { margin-top: 2px; }
.photo-box {
    width: 100%;
    aspect-ratio: 1 / 1;
    border: 1px dashed #cbd5e1;
    border-radius: 10px;
    display: grid;
    place-items: center;
    overflow: hidden;
    cursor: pointer;
    background: #f8fafc;
}
.photo-box img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
.photo-empty {
    font-size: 13px;
    color: #94a3b8;
}
.file-hidden { display: none; }
.menu-card-empty {
    border: 2px dashed #cbd5e1;
    border-radius: 12px;
    display: grid;
    place-items: center;
    min-height: 220px;
    background: #fafafa;
    cursor: pointer;
    transition: background .12s, border-color .12s;
    width: 50px;
}
.menu-card-empty:hover {
    background: #f5faff;
    border-color: #bfdbfe;
}
.empty-plus {
    font-size: 30px;
    line-height: 1;
    color: #93c5fd;
    margin-bottom: 6px;
}
.menu-input :deep(input) {
    font-size: 12px;
    font-weight: 600;
    color: #1e293b;
    font-family: 'Noto Sans KR', sans-serif;
}

@media (pointer: fine) {
    .menu-card-del {
        width: 24px !important;
        height: 24px !important;
        min-width: 24px !important;
    }
    .menu-card-del :deep(.q-icon) {
        font-size: 14px;
    }
}

@media (pointer: fine) {
    .menu-card {
        padding-top: 42px;
    }
}


/* =========================
   반응형 (Responsive)
   ========================= */
@media (max-width: 900px) {
    .modal-split { grid-template-columns: 1fr; }
    .divider { display: none; }
    .dc-body { grid-template-columns: 1fr; }
    .form-title { font-size: 14px; }
}

/* =========================
   따봉
   ========================= */
/* 좌상단 추천 토글 버튼 위치 */
.menu-card-top-left {
    position: absolute;
    top: 6px;
    left: 12px;
    z-index: 2;
}

/* 따봉 버튼 사이즈/인터랙션 */
.menu-card-like {
    background: transparent !important;
    box-shadow: none !important;
}
.menu-card-like:hover,
.menu-card-like:active,
.menu-card-like:focus {
    background: transparent !important;
    box-shadow: none !important;
}
@media (pointer: fine) {
    .menu-card-like {
        width: 10px !important;
        height: 10px !important;
        min-width: 20px !important;
    }
}
.menu-card-like :deep(.q-icon) {
    font-size: 20px;
    color: #9ca3af;
}
.menu-card-like.is-recommended :deep(.q-icon) {
    color: #F87171;
}

.menu-card-like:hover {
    background: transparent !important; /* hover에도 투명 유지 */
}
.menu-card-like:active {
    background: transparent !important; /* 클릭할 때도 투명 */
}
.menu-card-like:focus {
    box-shadow: none !important; /* 포커스 테두리 제거 */
}


/* 사진 위 '추천' 배지 + 오버레이 기준점 */
.photo-box {
    position: relative;
}

.photo-badge {
    position: absolute;
    top: 8px;
    left: 8px;
    z-index: 1;
    padding: 3px 8px;
    font-size: 11px;
    font-weight: 700;
    border-radius: 999px;
    background: #eff6ff;
    color: #1d4ed8;
    border: 1px solid #bfdbfe;
    pointer-events: none;
}



</style>

