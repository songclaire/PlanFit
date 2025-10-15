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
        <SearchArea v-model="restaurantSearch" />

        <!-- 지도 박스 -->
        <div class="r-map">
            <div id="map"></div>
        </div>

        <RestaurantAddModal
            v-model="showAddModal"
            :initial-center="getCurrentCenter()"
            @saved="onSaved"
        />
    </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import SearchArea from '../../components/SearchArea.vue'
import RestaurantAddModal from './RestaurantAddModal.vue'

defineOptions({ name: 'RestaurantList' })

let map

/** 모달 ON/OFF */
const showAddModal = ref(false)

/** 검색 바인딩 (필요 시 구조 맞춰서 확장) */
const restaurantSearch = ref({
    model: {},
    fields: [
        { name: '', label: '음식종류' },
        { name: '', label: '평점' },
        { name: '', label: '검색어' }
    ],
    events: {}
})

/** 현재 지도 중심 반환 → 모달 초기 중심에 사용 */
function getCurrentCenter() {
    if (map) {
        const c = map.getCenter()
        return { lat: c.getLat(), lng: c.getLng() }
    }
    return { lat: 37.5665, lng: 126.9780 } // 서울 기본
}

/** 모달 저장 결과 콜백 */
function onSaved(payload) {
    // payload: { position:{lat,lng}, place, form }
    console.log('맛집 저장:', payload)
    // TODO: API 저장/목록 새로고침 등 후속 처리
}

/** Kakao 맵 초기화 (부모의 메인 지도만) */
onMounted(() => {
    if (!window.kakao) {
        console.error('카카오 SDK 스크립트가 로드되지 않았습니다.')
        return
    }

    window.kakao.maps.load(() => {
        const container = document.getElementById('map')
        const center = new window.kakao.maps.LatLng(37.5665, 126.9780)

        map = new window.kakao.maps.Map(container, { center, level: 7 })
        new window.kakao.maps.Marker({ position: center, map })

        // 첫 렌더 후 강제 relayout
        requestAnimationFrame(() => {
            map.relayout()
            map.setCenter(center)
        })

        // 리사이즈 대응
        const onResize = () => {
            map.relayout()
            map.setCenter(center)
        }
        window.addEventListener('resize', onResize)
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
/* 바텀시트 (필요시 사용) */
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
    box-shadow: 0 -4px 16px rgba(0, 0, 0, 0.05);
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
    transition: background-color 0.15s ease, box-shadow 0.15s ease;
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

</style>
