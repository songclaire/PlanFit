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
                    <span class="cmt__count">{{ comments.length }}</span> <!-- 추후 바인딩 -->
                </div>

                <!-- 작성 영역 자리 (나중에 v-model/버튼 붙임) -->
                <div class="cmt__editor">
                    <div class="cmt__user">
                        <span class="cmt__user-id"> {{ userId }} </span> <!-- 추후 실제 ID 바인딩 -->
                    </div>

                    <div class="cmt__inputbox">
                        <textarea
                            v-model="commentCn"
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
                <div class="cmt__list">
                    <div class="cmt__item"
                        v-for="c in comments"
                        :key="c.commentId"
                        :style="{ marginLeft: (c.parentId ? 16 : 0) + 'px' }">
                        <div class="cmt__item-header">
                            <span class="cmt__avatar">
                                <img
                                    v-if="c.fileId && !avatarErr[c.regId]"
                                    :src="`/api/file/download/${c.fileId}`"
                                    alt="avatar"
                                    @error="() => onAvatarError(c.regId)"
                                />
                                <span v-else class="cmt__avatar-initials">
                                    {{ initialsOf(c.userName || c.regId) }}
                                </span>
                            </span>
                            <span class="cmt__user-id">{{ c.regId }}</span>
                            <span class="cmt__date">{{ c.regDt }}</span>
                        </div>
                        <div class="cmt__item-body" v-if="editTargetId === c.commentId">
                            <div class="cmt__edit-editor">
                                <textarea v-model="editText" class="cmt__input" maxlength="4000"></textarea>
                                <div class="cmt__btns">
                                    <button class="btn btn-ghost" @click="editTargetId=null; editText=''">취소</button>
                                    <button class="btn btn-primary" @click="updateCommentReply(c)">수정</button>
                                </div>
                            </div>
                        </div>
                        <div class="cmt__item-body" v-else>
                            <p class="cmt__text">{{ c.commentCn }}</p>
                        </div>

                        <div class="cmt__item-actions">
                            <button class="btn btn-ghost" @click="onClickReply(c.commentId)" v-if="!c.parentId && editTargetId !== c.commentId">답글</button>
                            <button class="btn btn-ghost" @click="onClickEdit(c)">수정</button>
                            <button class="btn btn-ghost" @click="deleteComment(c.commentId)">삭제</button>
                        </div>

                        <div
                            v-if="replyTargetId === c.commentId && editTargetId !== c.commentId"
                            class="cmt__reply-editor"
                        >
                            <textarea v-model="replyText" placeholder="답글 입력"></textarea>
                            <div class="cmt__btns">
                                <button class="btn btn-ghost" @click="replyTargetId=null">취소</button>
                                <button class="btn btn-primary" @click="saveReply(c.commentId)">등록</button>
                            </div>
                        </div>
                    </div>

                    <div v-if="comments.length === 0" class="cmt__empty">
                        아직 댓글이 없습니다.
                    </div>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import FormArea from '../../components/FormArea.vue'   // FormArea 사용

const route = useRoute()
const router = useRouter()
const recipeSn = route.params.recipeSn

// 댓글
const commentCn = ref('')
const comments = ref([])
// 대댓글
const replyTargetId = ref(null)   // 현재 답글창이 열려있는 댓글 ID
const replyText = ref('')
// 댓글 수정
const editTargetId = ref(null)
const editText = ref('')
// 댓글 이미지 로딩 실패 여부
const avatarErr = reactive({})

// 이미지 실패 핸들러
function onAvatarError(userId) {
    if (userId) avatarErr[userId] = true
}

// 이름이나 아이디에서 이니셜 생성
function initialsOf(nameOrId) {
    const s = String(nameOrId || '').trim()
    if (!s) return 'NA'
    const clean = s.replace(/[^\p{L}\p{N}]/gu, '')
    return clean.slice(0, 2).toUpperCase()
}

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
        const payload = {
            userId: userId,
            commentCn: commentCn.value,
            menuType: 'RECIPE',
            menuId: recipeSn
        }
        const { data } = await axios.post('/api/comment/save', payload)
        commentCn.value = ''
        await selectComments()
        console.log('저장 성공', data)
    } catch(err) {
        console.error('댓글 등록 실패', err)
    }
}

/**
 * 댓글 조회 api
 */
async function selectComments() {
    try {
        const { data } = await axios.get('/api/comment/list', {
            params: {
                menuType: 'RECIPE',
                menuId: recipeSn
            }
        })
        comments.value = data
    } catch (err) {
        console.error('댓글 조회 실패', err)
    }
}


// 대댓글 창 열기 함수
function onClickReply(commentId) {
    replyTargetId.value = (replyTargetId.value === commentId) ? null : commentId
    replyText.value = ''
}

/**
 * 대댓글 저장 api
 */
async function saveReply(parentId) {
    try {
        const payload = {
            userId,
            commentCn: replyText.value,
            menuType: 'RECIPE',
            menuId: recipeSn,
            parentId: parentId
        }
        await axios.post('/api/comment/save', payload)
        replyText.value = ''
        replyTargetId.value = null
        await selectComments()
    } catch (err) {
        console.error('답글 등록 실패')
    }
}

/**
 * 댓글, 대댓글 수정 클릭
 */
function onClickEdit(c) {
    editTargetId.value = c.commentId
    editText.value = c.commentCn
}


/**
 * 댓글, 대댓글 수정 api
 */
async function updateCommentReply(c) {
    try {
        const payload = {
            commentId: c.commentId,
            commentCn: editText.value,
        }
        await axios.post('/api/comment/update', payload)
        editText.value = ''
        editTargetId.value = null
        await selectComments()
    } catch (err) {
        console.error('댓글 수정 실패')
    }
}

/**
 * 댓글, 대댓글 삭제 api
 */
async function deleteComment(commentId) {
    try {
        alert('댓글을 삭제하겠습니다.')
        await axios.delete('/api/comment/delete', {
            params: { commentId }
        })
        await selectComments()
    } catch (err) {
        console.error('댓글 삭제 실패')
    }
}


if (recipeSn == 'new') {
    const recipeModel = {
        recipeSn: null,
    }
} else {
    getData(recipeSn)
}

onMounted(() => {
    selectComments()
})

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
    line-height: 1;
    cursor: pointer;
}

.btn:disabled {
    cursor: not-allowed;
    opacity: .6;
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

/* =====================================
   댓글 공통 색상 변수
   ===================================== */
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

/* 카드 */
.cmt__card {
    background: #f8fafc;
    border: 1px solid var(--pf-card-border, #e5e7eb);
    border-radius: 12px;
    padding: 16px;
    box-shadow: 0 2px 10px rgba(0,0,0,.04);
}

/* 헤더 */
.cmt__header {
    display: flex;
    align-items: baseline;
    gap: 8px;
    margin-bottom: 12px;
}
.cmt__title {
    margin: 0;
    font-size: 16px;
    font-weight: 700;
    color: var(--pf-title);
}
.cmt__count {
    font-size: 12px;
    background: #eef2ff;
    color: #1e40af;
    border: 1px solid #dbeafe;
    padding: 2px 8px;
    border-radius: 999px;
}

/* 댓글 작성 에디터 */
.cmt__editor {
    min-height: 72px;
}
.cmt__user {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
}
.cmt__user-id {
    font-size: 12px;
    font-weight: 700;
    color: #111827;
}

.cmt__editor .cmt__user-id {
    margin-left: 10px;
    display: inline-block;
}

.cmt__inputbox {
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 10px 12px;
    transition: border-color .15s ease;
}
.cmt__inputbox:focus-within {
    border-color: #60a5fa;
    box-shadow: 0 0 0 3px rgba(96,165,250,.15);
}
.cmt__input {
    width: 100%;
    min-height: 20px;
    border: none;
    resize: vertical;
    outline: none;
    font-size: 14px;
    color: #111827;
    line-height: 1.5;
}
.cmt__input::placeholder {
    color: #9ca3af;
}

/* 에디터 하단 액션 */
.cmt__actions {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 10px;
}
.cmt__counter {
    font-size: 12px;
    color: var(--pf-muted, #6b7280);
}
.cmt__btns {
    display: flex;
    gap: 8px;
}

/* 구분선 */
.cmt__divider {
    height: 2px;
    background: #cbd5e1;
    margin: 16px 0;
}

/* 댓글 리스트 */
.cmt__list {
    min-height: 80px;
    padding-left: 15px;
    padding-right: 15px;
}
.cmt__item {
    position: relative;
    padding: 12px 0;             /* 좌우 패딩 빼서 블록 느낌 최소화 */
    background: transparent;     /* 행 배경 제거 */
    border: none;                /* 보더 제거 */
    border-radius: 0;            /* 라운드 제거 */
    box-shadow: inset 0 -1px 0 #eef2f7;  /* 아랫줄만 그리는 인셋 디바이더 */
}
.cmt__item:hover {
    background: transparent;     /* 기존 #f9fafb 제거 */
}

/* 댓글 메타정보 */
.cmt__item-header {
    display: flex;
    align-items: center;
}
.cmt__date {
    margin-left: auto;
    color: #6b7280;
    font-size: 12px;
}

/* 댓글 본문 */
.cmt__item-body .cmt__text {
    color: #0f172a;
    line-height: 1.6;
    white-space: pre-wrap;
    margin-left: 49px;
}

/* 액션 버튼 */
.cmt__item-actions {
    display: flex;
    gap: 6px;
    justify-content: flex-end;  /* 버튼들을 오른쪽 끝으로 */
    opacity: 0;
    pointer-events: none;
    transition: opacity .12s ease;
    margin-top: 6px;
}
.cmt__item:hover .cmt__item-actions {
    opacity: 1;
    pointer-events: auto;
}
.cmt__item-actions .btn {
    padding: 6px 8px;
    font-weight: 600;
    border-radius: 6px;
    font-size: 12px;
}
.cmt__item-actions .btn-ghost {
    background: #f3f4f6;
    border-color: #e5e7eb;
}
.cmt__item-actions .btn-ghost:hover {
    background: #e5e7eb;
}

/* 대댓글 표시 */
.cmt__item[style*="margin-left: 16px"] {
    padding-left: 20px;   /* 들여쓰기만 유지 */
    box-shadow: inset 0 -1px 0 #eef2f7;  /* 아래줄 구분은 그대로 */
}
.cmt__item[style*="margin-left: 16px"]::before {
    content: "↪";          /* 화살표 문자 (원하는 이모지나 기호로 교체 가능) */
    position: absolute;
    left: 0;               /* 아이템의 제일 왼쪽 */
    top: 8px;              /* 글자 높이에 맞춰 살짝 내려줌 */
    font-size: 14px;
    color: #9ca3af;        /* 은은한 회색 */
}

/* 빈 상태 */
.cmt__empty {
    color: #6b7280;
    background: #f8fafc;
    border: 1px dashed #cbd5e1;
    border-radius: 10px;
    padding: 16px;
    text-align: center;
}

/* 버튼 컬러 재정의 */
.btn-primary {
    background: #2563eb;
    color: #fff;
    border-color: #2563eb;
}
.btn-primary:hover:not(:disabled) {
    background: #1d4ed8;
    border-color: #1d4ed8;
}
.btn-ghost {
    background: #f3f4f6;
    color: #111827;
    border-color: #e5e7eb;
}
.btn-ghost:not(:disabled):hover {
    background: #e5e7eb;
}


/* 대댓글 */

.cmt__reply-editor {
    margin-top: 8px;            /* 본문과 간격 */
    margin-left: 20px;          /* 댓글보다 한 칸 들여쓰기 */
    background: #ffffff;        /* 메인 입력칸과 톤 맞춤 */
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 10px 12px;
}

.cmt__reply-editor textarea {
    width: 100%;
    min-height: 30px;
    border: none;
    outline: none;
    resize: vertical;
    font-size: 14px;
    color: #111827;
    line-height: 1.5;
}

.cmt__reply-editor:focus-within {
    border-color: #60a5fa;
    box-shadow: 0 0 0 3px rgba(96,165,250,.15);
}

.cmt__reply-editor .cmt__btns {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
}

.cmt__item[style*="margin-left: 16px"] .cmt__reply-editor {
    margin-left: 18px; /* 화살표 폭만큼 추가 보정 */
}

@media (max-width: 480px) {
  .cmt__reply-editor {
      margin-left: 12px;
      padding: 8px 10px;
  }
}

/* 대댓글 전용 버튼 */
.cmt__reply-editor .btn {
    padding: 5px 7px;
    font-size: 12px;
    border-radius: 7px;
}

.cmt__reply-editor .btn-primary {
    background: #6b7280;
    border-color: #6b7280;
}
.cmt__reply-editor .btn-primary:hover:not(:disabled) {
    background: #4b5563;
    border-color: #4b5563;
}

:deep(.form-area .btn-edit) {
  background: #60a5fa;
  color: #fff;
  border-color: #5ca0f5;
}

:deep(.form-area .btn-edit:hover:not(:disabled)) {
  background: #3b82f6;   /* hover 시 blue-500 진한 파랑 */
  border-color: #3b82f6; /* 테두리도 동일 */
}

/* 댓글 수정 입력창 */
.cmt__edit-editor {
    margin-top: 8px;        /* 본문과 간격만 유지 */
    background: #ffffff;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 10px 12px;
}

.cmt__edit-editor textarea {
    width: 100%;
    min-height: 30px;
    border: none;
    outline: none;
    resize: vertical;
    font-size: 14px;
    color: #111827;
    line-height: 1.5;
}

.cmt__edit-editor:focus-within {
    border-color: #60a5fa;
    box-shadow: 0 0 0 3px rgba(96,165,250,.15);
}

.cmt__edit-editor .cmt__btns {
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 8px;
}

/* 수정창 버튼도 대댓글 버튼 스타일 재활용 */
.cmt__edit-editor .btn {
    padding: 5px 7px;
    font-size: 12px;
    border-radius: 7px;
}

.cmt__edit-editor .btn-primary {
    background: #6b7280;
    border-color: #6b7280;
}
.cmt__edit-editor .btn-primary:hover:not(:disabled) {
    background: #4b5563;
    border-color: #4b5563;
}

.cmt__avatar {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 40px;       /* 아바타 너비 */
    height: 40px;      /* 아바타 높이 */
    border-radius: 50%;/* 원형 */
    overflow: hidden;
    background: #e5e7eb; /* 사진 없을 때 배경 */
    margin-right: 10px;  /* ID랑 간격 */
}

.cmt__avatar img {
    width: 100%;
    height: 100%;
    object-fit: cover; /* 비율 깨지지 않게 */
}

.cmt__avatar-initials {
    font-size: 12px;
    font-weight: 600;
    color: #374151;
}

</style>

