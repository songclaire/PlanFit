<template>
    <div class="mypage-wrapper">
        <div class="header-row">
            <h2>마이페이지</h2>
        </div>

        <div class="grid-2col">
            <!-- 왼쪽: 프로필 -->
            <section class="card profile-card">
                <h3 class="card-title">프로필</h3>

                <div class="photo-circle">
                    <img
                        v-if="user.fileId && !imgError"
                        :src="`/api/file/download/${user.fileId}`"
                        alt="profile"
                        class="photo-img"
                        @error="() => (imgError = true)"
                    />
                    <span v-else class="photo-initials">{{ initials }}</span>
                </div>

                <div class="btn-row" v-if="editMode">
                    <AttachFile
                        v-model="user.fileId"
                        accept="image/*"
                        :max-size-m-b="5"
                        :menuType="'PROFILE'"
                        ui="button"
                        label="사진 추가"
                        class="profile-upload"
                    />
                    <!-- 기존: btn btn-ghost  → 통일: btn btn-outline -->
                    <button class="btn btn-outline btn-delete" @click="user.fileId = null">
                            사진 삭제
                        </button>
                </div>

            </section>

            <!-- 오른쪽: 기본정보 카드 -->
            <section class="card info-card">
                <h3 class="card-title">기본정보</h3>

                <div class="info-list">
                    <div class="info-row">
                        <span class="info-label">아이디</span>
                        <span class="info-value">
                            <span class="chip">{{ currentId }}</span>
                        </span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">이름</span>
                        <span class="info-value" v-if="!editMode">{{ user.userName || '-' }}</span>
                        <input v-else class="input-like" type="text" v-model="user.userName" />
                    </div>
                    <div class="info-row">
                        <span class="info-label">이메일</span>
                        <span class="info-value" v-if="!editMode">{{ user.email || '-' }}</span>
                        <input v-else class="input-like" type="email" v-model="user.email" />

                    </div>
                    <div class="info-row">
                        <span class="info-label">연락처</span>
                        <span class="info-value" v-if="!editMode">{{ user.telNo || '-' }}</span>
                        <input v-else class="input-like" type="tel" v-model="user.telNo" />
                    </div>
                    <div class="info-row">
                        <span class="info-label">주소</span>
                        <!-- 보기 모드 -->
                        <span class="info-value" v-if="!editMode">
                            {{ user.roadNmAddr || '-' }}
                            <template v-if="user.roadNmDaddr">, {{ user.roadNmDaddr }}</template>
                        </span>

                        <!-- 수정 모드 -->
                        <div v-else class="addr-edit">
                            <input class="input-like" type="text" v-model="user.roadNmAddr" placeholder="도로명 주소" />
                            <input class="input-like" type="text" v-model="user.roadNmDaddr" placeholder="상세 주소" />
                        </div>
                    </div>
                </div>

                <div class="btn-right" v-if="!editMode">
                    <button class="btn btn-primary" @click="editMode = true">정보 수정</button>
                </div>
                <div class="btn-right" v-else>
                    <button class="btn btn-primary" @click="onSave">저장</button>
                    <button class="btn btn-outline" @click="onCancel">취소</button>
                </div>
            </section>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios' // 실제 연동 시 주석 해제
import AttachFile from '@/components/AttachFile.vue'

const currentId = localStorage.getItem('userId')

// 정보수정 모드
const editMode = ref(false)

const user = ref({
    userId: '',
    userName: '',
    email: '',
    telNo: '',
    roadNmAddr: '',
    roadNmDaddr: '',
    fileId: null
})

const imgError = ref(false)

const initials = computed(() => {
    const name = (user.value.userName || '').trim()
    if (!name) return '사진'
    const parts = name.split(/\s+/).filter(Boolean)
    return ((parts[0]?.[0] || '') + (parts[1]?.[0] || '')).toUpperCase()
})

/**
 * 정보 수정 api
 */
async function onSave() {
    try {
        const payload = {
            userId: user.value.userId?.trim() ?? null,
            userName: user.value.userName?.trim() ?? null,
            email: user.value.email?.trim() ?? null,
            telNo: user.value.telNo?.trim() ?? null,
            roadNmAddr: user.value.roadNmAddr?.trim() ?? null,
            roadNmDaddr: user.value.roadNmDaddr?.trim() ?? null,
            fileId: user.value.fileId ?? null
        }
        const { data } = await axios.post('/api/user/updateUser', payload)
        alert('업데이트 성공!');
        editMode.value = false
    } catch (err) {
        console.error('저장 실패')
    }
}

// 취소
async function onCancel() {
    editMode.value = false
    await loadMyInfo()
}

// 사용자 정보 호출
async function loadMyInfo() {
    try {
        const { data } = await axios.get('/api/user/selectUser')
         Object.assign(user.value, data)
    } catch (err) {
        console.error('마이페이지 호출 오류')
    }
}

onMounted(() => {
    loadMyInfo()
})
</script>

<style scoped>
/* ===== Layout ===== */
.mypage-wrapper {
    max-width: 1100px;
    margin: 40px auto;
    padding: 24px;
    background: #fff;
    border-radius: 16px;
    box-shadow: 0 4px 24px rgba(2, 6, 23, 0.06);
}
.header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 18px;
}
.header-row h2 {
    font-size: 28px;
    margin: 0;
    font-weight: 800;
    letter-spacing: -0.02em;
    color: #0f172a;
}
.grid-2col {
    display: grid;
    grid-template-columns: 360px 1fr;
    gap: 22px;
}
.card {
    background: #ffffff;
    border: 1px solid #e6ebf5;
    border-radius: 14px;
    padding: 18px;
}
.card-title {
    font-size: 18px;
    font-weight: 700;
    margin: 0 0 14px 0;
    color: #0b2447;
}

/* ===== Profile ===== */
.profile-card .photo-circle {
    width: 220px;
    height: 220px;
    margin: 4px auto 18px;
    border-radius: 50%;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafc;
    box-shadow: 0 6px 20px rgba(15, 23, 42, 0.08);
    border: 8px solid #60a5fa;
}
.photo-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    border-radius: 50%;
}
.photo-initials {
    color: #6b7280;
    font-size: 36px;
    font-weight: 800;
    letter-spacing: 0.5px;
}

/* ===== Info list ===== */
.info-card {
    background: linear-gradient(180deg, #ffffff 0%, #fbfdff 100%);
}
.info-list {
    display: flex;
    flex-direction: column;
    gap: 22px;
    padding: 8px 16px 8px 26px;
}
.info-row {
    display: grid;
    grid-template-columns: 160px 1fr;
    align-items: center;
    gap: 14px;
    padding-bottom: 12px;
    border-bottom: 1px dashed #e5e7eb;
}
.info-row:last-child {
    border-bottom: none;
    padding-bottom: 0;
}
.info-label {
    color: #334155;
    font-weight: 700;
    letter-spacing: -0.01em;
}
.info-value {
    color: #0f172a;
    font-weight: 400;
    letter-spacing: -0.01em;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}
.chip {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 9999px;
    background: #eef2ff;
    color: #3730a3;
    font-weight: 700;
    border: 1px solid #e0e7ff;
}

/* ===== Buttons (공통 베이스 + 변형) ===== */
.btn {
    border: 1px solid #cbd5e1;
    border-radius: 10px;
    padding: 9px 14px;
    cursor: pointer;
    font-weight: 700;
    transition: transform .05s ease, box-shadow .15s ease, background .15s ease, color .15s ease, border-color .15s ease;
}
.btn:active { transform: translateY(1px); }

/* primary */
.btn-primary {
    background: #60a5fa;
    border-color: #3b82f6;
    color: #fff;
    box-shadow: 0 3px 8px rgba(96, 165, 250, 0.35);
}
.btn-primary:hover { background: #3b82f6; }

/* outline/ghost 통일(시각 동일) */
.btn-outline,
.btn-ghost {
    background: #fff;
    color: #334155;
}
.btn-outline:hover,
.btn-ghost:hover {
    background: #f1f5f9;
    box-shadow: 0 2px 6px rgba(0,0,0,0.06);
}

/* 버튼 행: 중앙 정렬 & 늘어짐 방지 */
.btn-row {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 12px;
    margin-top: 12px;
}
.btn-row > * { flex: 0 0 auto; }

/* ===== AttachFile 버튼 모드 보정 ===== */
:deep(.profile-upload) {
    /* AttachFile 루트 기본 width:100%를 버튼 크기만큼만 차지하게 */
    width: auto !important;
    max-width: none !important;
    display: inline-flex;
}
/* AttachFile 내부 버튼과 삭제 버튼을 동일 룩으로 */
:deep(.profile-upload .btn.btn-outline) {
    background: #fff;
    border: 1px solid #cbd5e1;
    border-radius: 10px;
    padding: 9px 14px;
    color: #334155;
    font-weight: 700;
    transition: background .15s ease, box-shadow .15s ease;
}
:deep(.profile-upload .btn.btn-outline:hover) {
    background: #f1f5f9;
    box-shadow: 0 2px 6px rgba(0,0,0,0.06);
}

/* ===== Right button row ===== */
.btn-right {
    display: flex;
    justify-content: flex-end;
    margin-top: 14px;
}

/* [수정] 입력창을 컴팩트하게 */
.input-like {
    box-sizing: border-box;   /* 높이에 패딩 포함 */
    height: 34px;             /* ← 원하는 값(32~40px 권장) */
    padding: 4px 10px;        /* 세로 패딩 줄이기 */
    line-height: 1.2;         /* 텍스트 세로 정렬 안정화 */
    background: #fff;
    border: 1px solid #cbd5e1;
    border-radius: 10px;
    font-weight: 500;
    width: 100%;
}
.input-like:focus {
    outline: none;
    box-shadow: 0 0 0 3px rgba(59,130,246,.15);
    border-color: #3b82f6;
}

.addr-edit {
    display: grid;
    gap: 6px;   /* 기존 8px → 6px 등으로 조절 */
}

</style>
