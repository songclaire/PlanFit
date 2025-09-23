<template>
    <div class="r-wrap">
        <!-- 헤더/툴바 -->
        <div class="r-toolbar">
            <h3 class="r-title">맛집 지도</h3>
            <div class="r-actions">
                <button class="btn-primary" @click="showAddModal = true">+ 맛집 추가</button>
            </div>
        </div>

        <!-- 필터 라인 (가짜 입력칸) -->
        <SearchArea v-model="restaurantSearch"/>

        <!-- 지도 박스 -->
        <div class="r-map">
            <!-- <div class="map-placeholder">지도 영역 (추후 카카오/네이버/구글)</div> -->
            <div id="map"></div>
        </div>

        <!-- 바텀시트: 마커 클릭 시 정보 노출 영역 -->
        <div class="r-bottomsheet">
            <div class="bs-thumb"></div>
            <div class="bs-info">
                <div class="line title">[가게명 자리]</div>
                <div class="line">[주소 자리]</div>
                <div class="line">[요약/태그 자리]</div>
                <div class="bs-actions">
                    <button class="btn-outline">상세 보기</button>
                    <button class="btn-outline">길찾기</button>
                </div>
            </div>
        </div>

        <q-dialog v-model="showAddModal" persistent>
            <!-- 🔽 모달 크기 확장 -->
            <q-card style="min-width:1200px; max-width:1600px;">
                <!-- 모달 상단 -->
                <q-card-section class="search-row q-pt-sm q-pb-xs">
                    <div class="text-h6">🍜 맛집 등록</div>
                    <q-space />
                    <q-btn icon="close" flat round dense v-close-popup />
                </q-card-section>

                <!-- 🔽 메인 영역: 좌/우 2단 -->
                <q-card-section>
                    <div class="modal-split">
                        <!-- 왼쪽: 지도 + 검색 -->
                        <div class="left-pane">
                            <!-- 검색창 -->
                            <div class="search-row">
                                <q-input
                                    v-model="keyword"
                                    dense
                                    outlined
                                    placeholder="가게명, 주소 등으로 검색"
                                    @keyup.enter="doSearch"
                                    class="search-input"
                                />
                                <q-btn
                                    color="primary"
                                    label="검색"
                                    class="search-btn"
                                    @click="doSearch"
                                />
                            </div>

                            <!-- 지도 -->
                            <div class="map-pane">
                                <div ref="addMapEl" class="map-box"></div>
                            </div>

                            <!-- 결과 리스트 -->
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

                                <!-- 페이지네이션 -->
                                <div v-if="totalPages > 1" class="pager">
                                    <q-btn flat dense label="이전" :disable="page <= 1" @click="prevPage" />
                                    <span class="pager-info">페이지 {{ page }} / {{ totalPages }}</span>
                                    <q-btn flat dense label="다음" :disable="page >= totalPages" @click="nextPage" />
                                </div>
                            </div>
                        </div>

                        <!-- 구분선 -->
                        <div class="divider"></div>

                        <!-- 오른쪽: 추후 상세/폼 영역 -->
                        <div class="right-pane">
                            <template v-if="selectedPlace">
                                <!-- 1) 가게 정보 카드 -->
                                <div class="detail-card">
                                    <div class="dc-head">
                                        <div class="dc-title-row">
                                            <h4 class="dc-title" :title="selectedPlace.place_name">
                                                {{ selectedPlace.place_name }}
                                            </h4>
                                            <div class="dc-actions">
                                                <a
                                                    class="dc-link"
                                                    :href="selectedPlace.place_url"
                                                    target="_blank"
                                                    rel="noopener"
                                                >상세보기</a>
                                            </div>
                                        </div>
                                        <div class="dc-chips">
                                            <span class="chip">
                                                {{ selectedPlace.category_group_name || selectedPlace.category_name || '분류없음' }}
                                            </span>
                                            <span v-if="selectedPlace.phone" class="chip chip-ghost">
                                                {{ selectedPlace.phone }}
                                            </span>
                                        </div>
                                    </div>

                                    <div class="dc-body">
                                        <div class="dc-row">
                                            <span class="dc-label">도로명</span>
                                            <span class="dc-value">
                                                {{ selectedPlace.road_address_name || '-' }}
                                            </span>
                                        </div>

                                        <div class="dc-row" v-if="selectedPlace.phone">
                                            <span class="dc-label">전화번호</span>
                                            <span class="dc-value">
                                                {{ selectedPlace.phone }}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <!-- 2) 입력 폼 섹션 -->
                                <div class="form-card">
                                    <div class="form-head">
                                        <h4 class="form-title">정보입력</h4>
                                    </div>
                                    <FormArea v-model="restaurantForm" />
                                </div>
                            </template>

                            <div v-else class="placeholder">
                                👉 식당 선택 시 상세정보 / 입력폼 들어올 자리
                            </div>
                        </div>

                    </div>
                </q-card-section>
            </q-card>
        </q-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { useFormArea } from '../../components/useFormArea.js'
import SearchArea from '../../components/SearchArea.vue'
import FormArea from '../../components/FormArea.vue'


defineOptions({ name: 'RestaurantList' })
let map

// 모달 ON/OFF
const showAddModal = ref(false)
// 모달 지도 관련
const addMapEl = ref(null)        // 모달 지도 DOM
let addMap = null                 // 모달 지도 인스턴스
let addMarker = null              // 드래그 가능한 마커
let addMapClickHandler = null     // 클릭 핸들러 레퍼런스
const selectedPos = ref(null)     // { lat, lng }

// 모달 검색 상태
const keyword = ref('')
const results = ref([])          // 검색 결과
const loading = ref(false)
const activeIndex = ref(-1)
let ps = null
let markers = []                 // 결과용 마커들

const page = ref(1)
const totalPages = ref(1)
let kakaoPagination = null

// 선택된 식당 변수
const selectedPlace = ref(null)

// 모달_ 맛집 저장
function saveRestaurant() {
    if (!selectedPos.value) return alert('지도를 클릭하거나 검색 결과를 선택해 위치를 지정해 주세요.')
    console.log('선택 좌표:', selectedPos.value)
    showAddModal.value = false
}

// 모달_ 열릴 때 지도 초기화
watch(showAddModal, async (open) => {
    if (!open) {
        if (addMap && addMapClickHandler) {
            window.kakao.maps.event.removeListener(addMap, 'click', addMapClickHandler)
            addMapClickHandler = null
            results.value = []
            page.value = 1
            totalPages.value = 1
            kakaoPagination = null
            return
        }
        addMap = null
        addMarker = null
        return
    }

    await nextTick()

    // 🔒 DOM 존재 확인 가드
    if (!addMapEl.value) {
        console.error('모달 지도 DOM이 없습니다.')
        return
    }
    if (!window.kakao) {
        console.error('카카오 SDK가 로드되지 않음')
        return
    }

    window.kakao.maps.load(() => {
        const center = map ? map.getCenter() : new window.kakao.maps.LatLng(37.5665, 126.9780)

        setTimeout(() => {
            addMap = new window.kakao.maps.Map(addMapEl.value, { center, level: 5 })
            ps = new window.kakao.maps.services.Places()

            addMarker = new window.kakao.maps.Marker({ position: center, draggable: true })
            addMarker.setMap(addMap)

            addMapClickHandler = (e) => {
                const ll = e.latLng
                addMarker.setPosition(ll)
                selectedPos.value = { lat: ll.getLat(), lng: ll.getLng() }
            }
            window.kakao.maps.event.addListener(addMap, 'click', addMapClickHandler)

            // 최종 레이아웃 보정
            addMap.relayout()
            addMap.setCenter(center)
        }, 120) // Quasar 트랜지션 여유
    })
})

// 모달_ 지도 검색
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

            // Kakao 페이지네이션 객체 사용
            kakaoPagination = pagination
            page.value = pagination.current
            totalPages.value = pagination.last
        },
        { size: 3, page: page.value }
    )
}

// 페이지 이동 함수들
function gotoPage(n) {
    if (!kakaoPagination) return
    if (n < 1 || n > kakaoPagination.last) return
    kakaoPagination.gotoPage(n)
}
function prevPage() { gotoPage(page.value - 1) }
function nextPage() { gotoPage(page.value + 1) }

/*
 * 결과 마커 표시
 */
function drawResultMarkers(list) {
    clearResultMarkers()
    const bounds = new window.kakao.maps.LatLngBounds()
    list.forEach((item, i) => {
        const pos = new window.kakao.maps.LatLng(item.y, item.x)
        const m = new window.kakao.maps.Marker({ position: pos, map: addMap })
        window.kakao.maps.event.addListener(m, 'click', () => focusResult(i))
        markers.push(m)
        bounds.extend(pos)
    })
    if (!bounds.isEmpty()) addMap.setBounds(bounds)
}

/*
 * 결과 마커 제거
 */
function clearResultMarkers() {
    markers.forEach(m => m.setMap(null))
    markers = []
}

/*
 * 결과 클릭 포커스
 */
function focusResult(item, i) {
    selectedPlace.value = item

    activeIndex.value = i
    const pos = new window.kakao.maps.LatLng(item.y, item.x)
    addMarker.setPosition(pos)
    addMap.setCenter(pos)
    selectedPos.value = { lat: parseFloat(item.y), lng: parseFloat(item.x) }
}


/*
 * 식당 조회
 */
const restaurantSearch = ref({
    model: {},
    fields: [
        {
            name: '',
            label: '음식종류',
        },
        {
            name: '',
            label: '평점',
        },
        {
            name: '',
            label: '검색어',
        },
    ],
    events: {}
})


/**
 * 레시피 등록 폼 (레시피 추가)
 */
const restaurantForm = useFormArea({
    name: 'restaurantForm',
    submitButtons: true,
    model: {
        foodTypeSn: null,
        recipeNm: '',
        ingredients: '',
        recipeCn: '',
        recipeDate: '',
    },
    fields: [
        { label: '메뉴', name: 'ingredients', type: 'textarea', required: true },
        { label: '가격', name: 'recipeCn', type: 'textarea', required: true },
        { label: '만족도', name: 'difficulty', type: 'star-rating', required: true },
        { label: '사진', name: 'recipeDate', type: 'date', required: true, value: new Date().toISOString().split('T')[0] },
    ],
})

/*
 * Kakao 맵 API 호출
 */
onMounted(() => {
    if (!window.kakao) return console.error('카카오 SDK 스크립트가 로드되지 않았습니다.')

    window.kakao.maps.load(() => {
        const container = document.getElementById('map')
        const center = new window.kakao.maps.LatLng(37.5665, 126.9780)

        map = new window.kakao.maps.Map(container, { center, level: 7 })
        new window.kakao.maps.Marker({ position: center, map })

        // 1) 첫 렌더 후 강제 relayout (탭/transition 뒤 늦게 보일 때 대비)
        requestAnimationFrame(() => {
            map.relayout()
            map.setCenter(center)
        })

        // 2) 창 크기 변하면 지도 프레임에 맞게 재배치
        const onResize = () => { map.relayout(); map.setCenter(center) }
        window.addEventListener('resize', onResize)
        // 정리
        onBeforeUnmount(() => window.removeEventListener('resize', onResize))
    })
})
</script>

<style scoped>
/* ────────────────────────────── */
/* 기본 레이아웃 */
/* ────────────────────────────── */
.r-wrap {
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.search-area {
    margin-bottom: 1px !important;
}

/* ────────────────────────────── */
/* 툴바 */
/* ────────────────────────────── */
.r-toolbar {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    width: 100%;
    max-width: 1000px;
    margin: 0 auto 16px;
    padding: 0 8px;
    box-sizing: border-box;
}
.r-title {
    margin: 0;
    font-size: 24px;
    font-weight: 600;
    color: #333;
}
.r-actions {
    margin-left: auto;
    display: flex;
    gap: 8px;
}

/* ────────────────────────────── */
/* 필터 라인 */
/* ────────────────────────────── */
.r-filters {
    display: flex;
    gap: 8px;
    align-items: center;
}
.fake-input {
    min-width: 110px;
    padding: 10px 12px;
    border: 1px dashed #cbd5e1;
    background: #f8fafc;
    border-radius: 8px;
    font-size: 13px;
    color: #64748b;
}
.fake-input.grow {
    flex: 1;
}

/* ────────────────────────────── */
/* 지도 */
/* ────────────────────────────── */
.r-map {
    height: 420px;
    border: 2px solid #dbeafe;
    border-radius: 12px;
    overflow: hidden;
    background: #fff;
    width: 100%;
    max-width: 1000px;
    margin: 0 auto;
}
#map {
    width: 100%;
    height: 100%;
}

/* ────────────────────────────── */
/* 바텀시트 */
/* ────────────────────────────── */
.r-bottomsheet {
    position: sticky;
    bottom: 0;
    display: flex;
    gap: 12px;
    align-items: center;
    padding: 12px;
    border: 1px solid #e2e8f0;
    border-radius: 12px;
    background: #fff;
    box-shadow: 0 -4px 16px rgba(0,0,0,0.05);
}
.bs-thumb {
    width: 88px;
    height: 88px;
    border-radius: 10px;
    background: #e2e8f0;
    flex-shrink: 0;
}
.bs-info {
    display: grid;
    gap: 6px;
    width: 100%;
}
.bs-info .line {
    height: 16px;
    background: #f1f5f9;
    border-radius: 6px;
}
.bs-info .line.title {
    height: 20px;
    width: 60%;
    background: #e2e8f0;
}
.bs-actions {
    margin-top: 6px;
    display: flex;
    gap: 8px;
}

/* ────────────────────────────── */
/* 버튼 */
/* ────────────────────────────── */
.btn-primary {
    background-color: #2563eb;
    border: 1px solid #2563eb;
    color: #fff;
    padding: 8px 14px;
    border-radius: 8px;
    font-weight: 600;
    box-shadow: 0 2px 6px rgba(230, 90, 84, 0.20);
    transition: background-color .15s ease, box-shadow .15s ease;
    cursor: pointer;
}
.btn-outline {
    padding: 8px 12px;
    border-radius: 8px;
    border: 1px solid #cbd5e1;
    background: #fff;
    color: #334155;
    cursor: pointer;
    font-weight: 600;
}
.btn-outline:hover {
    background: #f8fafc;
}

/* ────────────────────────────── */
/* 모달: 지도 + 리스트 */
/* ────────────────────────────── */
.modal-grid {
    display: grid;
    grid-template-columns: 400px 1fr;  /* 좌: 리스트, 우: 지도 */
    gap: 16px;
    min-height: 420px;
}
.modal-vertical {
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.map-pane {
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    overflow: hidden;
}
.map-box {
    width: 100%;
    height: 400px;
}

/* ────────────────────────────── */
/* 결과 리스트 */
/* ────────────────────────────── */
.result-pane {
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    background: #fff;
    overflow: auto;
    padding: 8px;
}
.result-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 6px 8px 10px;
}
.result-count {
    font-size: 12px;
    color: #475569;
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
    align-items: flex-start;
    padding: 12px 16px;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    background: #fff;
    transition: transform .12s ease, box-shadow .12s ease, border-color .12s ease, background .12s ease;
    cursor: pointer;
}
.result-item:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 14px rgba(2,6,23,.06);
    border-color: #dbeafe;
}
.result-item.active {
    border-color: #2563eb;
    background: #f8fbff;
    box-shadow: 0 6px 18px rgba(37,99,235,.10);
}

/* ────────────── 모달 2단 레이아웃 ────────────── */
.modal-split {
    display: grid;
    grid-template-columns: 1fr 1px 1fr; /* 좌/구분선/우 */
    gap: 0;
    min-height: 600px;
}
.left-pane {
    padding: 12px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}
.right-pane {
    padding: 12px;
}
.divider {
    background: #e2e8f0;
    width: 1px;
}

/* 오른쪽 임시 placeholder */
.placeholder {
    border: 2px dashed #cbd5e1;
    border-radius: 8px;
    padding: 24px;
    color: #64748b;
    text-align: center;
}





/* 번호 배지 */
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

/* 본문 */
.ri-main { flex: 1 1 auto; min-width: 0; }
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
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 160px; /* ← 이름이 너무 길면 잘리게 */
}
.ri-sub {
    font-size: 13px;
    color: #475569;
    line-height: 1.3;
    margin-bottom: 6px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

/* 태그칩 */
.ri-tags {
    display: flex;
    gap: 4px;
    flex-shrink: 0;
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

/* 빈 상태 */
.result-empty {
    display: grid;
    place-items: center;
    height: 160px;
    color: #64748b;
    font-size: 13px;
    border: 1px dashed #e5e7eb;
    border-radius: 10px;
}

/* 페이지네이션 */
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

/* ────────────────────────────── */
/* 검색창 */
/* ────────────────────────────── */
.search-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 15px;
}
.search-input { flex: 1; }
.search-btn {
    flex-shrink: 0;
    height: 40px;
}







/* ────────────────────────────── */
/* 오른쪽 상세 카드 (가게 정보)  */
/* ────────────────────────────── */
.right-pane {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;
}

/* 카드 프레임 */
.detail-card {
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  background:
    radial-gradient(1200px 200px at 100% -20%, rgba(59,130,246,.08), transparent 60%),
    #ffffff;
  box-shadow: 0 6px 22px rgba(2,6,23,.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 헤더 */
.dc-head {
  padding: 16px 18px 12px;
  border-bottom: 1px solid #eef2ff;
  background:
    linear-gradient(180deg, #f8fbff 0%, #ffffff 60%);
}

.dc-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dc-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: #0f172a;
  letter-spacing: -0.2px;
  line-height: 1.2;
  flex: 1 1 auto;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dc-actions {
  display: flex;
  gap: 6px;
}

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
  transition: transform .1s ease, box-shadow .1s ease, background .15s ease;
}
.dc-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(29,78,216,.18);
  background: #eaf2ff;
}

/* 카테고리/전화 칩 */
.dc-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 10px;
}
.dc-chips .chip {
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  border: 1px solid #e0e7ff;
  background: #eef2ff;
  color: #3730a3;
}
.dc-chips .chip-ghost {
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  color: #334155;
}

/* 본문 */
.dc-body {
  display: grid;
  grid-template-columns: 80px 1fr;
  row-gap: 10px;
  column-gap: 10px;
  padding: 16px 18px 18px;
}

.dc-row {
  display: contents; /* 레이블/값을 그리드 라인에 붙임 */
}

.dc-label {
  align-self: center;
  justify-self: start;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 800;
  color: #1e3a8a;
  background: #eff6ff;
  border: 1px solid #dbeafe;
  border-radius: 8px;
}

.dc-value {
  align-self: center;
  padding: 6px 10px;
  min-height: 34px;
  border: 1px dashed #e5e7eb;
  border-radius: 8px;
  color: #0f172a;
  background: #ffffff;
  display: flex;
  align-items: center;
  line-height: 1.3;
  word-break: break-word;
}

/* 구분선(필요 시) */
.dc-sep {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  margin: 4px 0 10px;
}

/* 비선택 상태 플레이스홀더 */
.placeholder {
  border: 2px dashed #cbd5e1;
  border-radius: 12px;
  padding: 28px;
  color: #64748b;
  text-align: center;
  background:
    repeating-linear-gradient(
      -45deg,
      #f8fafc,
      #f8fafc 10px,
      #ffffff 10px,
      #ffffff 20px
    );
}

/* 반응형 */
@media (max-width: 1200px) {
  .dc-body {
    grid-template-columns: 100px 1fr;
  }
}
@media (max-width: 900px) {
  .modal-split {
    grid-template-columns: 1fr; /* 모바일: 단일 컬럼 */
  }
  .divider { display: none; }
  .dc-body {
    grid-template-columns: 1fr;
  }
  .dc-label {
    justify-self: stretch;
  }
}

/* ────────────────────────────── */
/* 오른쪽 상세 카드 내 Form 영역 - 화이트, 네모 하나만 */
/* ────────────────────────────── */
.dc-form {
    /* 박스/회색 배경/구분선 제거 → 카드 한 덩어리 */
    background: #fff;
    border: 0;
    border-radius: 0;

    /* 카드 내부 패딩과 정렬만 유지 */
    margin-top: 8px;
    padding: 0 18px 18px;

    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* Quasar 필드 간격만 가볍게 */
.dc-form :deep(.q-field) {
    margin-bottom: 10px;
}

/* 제출/저장 버튼은 오른쪽 정렬 */
.dc-form :deep(.q-btn) {
    align-self: flex-end;
}

.form-card {
    border: none;
    background: transparent;
    box-shadow: none;

    padding: 8px 0 0;
    display: flex;
    flex-direction: column;
    gap: 2px;
}

/* 타이틀 영역 */
.form-head {
    padding: 0 0 4px 8px;
}
.form-title {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: #0f172a;
}

/* Quasar 필드 기본 간격 제거 */
.form-card :deep(.q-field) {
    margin: 0;
    width: 100%;
}

/* 버튼은 오른쪽 정렬 */
.form-card :deep(.q-btn) {
    align-self: flex-end;
}

</style>


