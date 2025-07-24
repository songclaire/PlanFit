<template>
  <form class="schedule-form" @submit.prevent="submitForm">
    <!-- 일정 종류 -->
    <div class="form-row">
      <label>일정 종류</label>
      <select v-model="form.schdTypeSn">
        <option value=""> --- 선택 --- </option>
        <option
          v-for="type in schdTypeList"
          :key="type.schdTypeSn"
          :value="type.schdTypeSn"
        >
          {{ type.schdTypeNm }}
        </option>

      </select>
    </div>

    <!-- 일정 제목 -->
    <div class="form-row">
      <label>일정 제목</label>
      <input type="text" v-model="form.schdTtl" required />
    </div>

    <!-- 시작일 / 종료일 -->
    <div class="form-row date-row">
      <div class="date-field">
        <label>시작일</label>
        <input type="date" v-model="form.startDt" required />
      </div>
      <div class="date-field">
        <label>종료일</label>
        <input type="date" v-model="form.endDt" required />
      </div>
    </div>

    <!-- 내용 -->
    <div class="form-row">
      <label>일정 내용</label>
      <textarea v-model="form.schdCn" rows="3" />
    </div>

    <!-- 참여자 -->
    <div class="form-row">
      <label>참여자</label>
      <div class="participant-row">
        <select v-model="form.schdPtcptSn">
            <option value=""> -- 선택 -- </option>
            <option
                v-for="ptcpt in schdPtcptList"
                :key="ptcpt.schdPtcptSn"
                :value="ptcpt.schdPtcptSn"
            >
            {{ ptcpt.ptcptTypeNm }}
            </option>
        </select>
      </div>
    </div>

    <!-- 장소 -->
    <div class="form-row">
      <label>장소</label>
      <input type="text" v-model="form.location" />
    </div>

    <div class="form-row">
      <button type="submit" class="submit-button">등록</button>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const emit = defineEmits(['schedule-added'])

const form = ref({
  schdTypeSn: '',
  schdTtl: '',
  startDt: '',
  endDt: '',
  schdCn: '',
  location: '',
  ptcptTypeNm: '',
  schdPtcptSn: '',
})

// 일정 종류 목록
const schdTypeList = ref([])
// 일정 참가자 목록
const schdPtcptList = ref([])

/**
 * 일정 저장
 */
async function submitForm() {
    try {
        await axios.post('/api/saveSchd', form.value)
        // 달력에 적용하기
        emit('schedule-added', { ...form.value })
        alert('일정이 등록되었습니다')
    } catch (err) {
        console.error('등록 실패', err)
        alert('일정이 등록 실패')
    }
}

async function loadSchdTypeList() {
    try {
        const schdTypeRes = await axios.get('/api/schdTypeList')
        schdTypeList.value = schdTypeRes.data
    } catch (err) {
        console.error('일정종류 불러오기 실패', err)
    }
}

async function loadSchdPtcptList() {
    try {
        const schdPtcptRes = await axios.get('/api/schdPtcptList')
        schdPtcptList.value = schdPtcptRes.data
    } catch (err) {
        console.error('일정종류 불러오기 실패', err)
    }
}

onMounted( async () => {
    loadSchdTypeList()
    loadSchdPtcptList()
})


</script>

<style scoped>
.schedule-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 480px;
  width: 100%;
  margin: 0 auto;
}

.form-row {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.form-row label {
  font-weight: bold;
  margin-bottom: 6px;
  font-size: 14px;
}

input,
select,
textarea,
button {
  width: 100%;
  box-sizing: border-box;
  padding: 8px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

textarea {
  resize: vertical;
}

.date-row {
  flex-direction: row;
  gap: 16px;
}

.date-field {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.submit-button {
  background-color: #1a73e8;
  color: white;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s ease;
}

.submit-button:hover {
  background-color: #155fc2;
}

.participant-row {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.participant-row select,
.participant-row input {  /* input도 포함 */
  flex: 1;
}

.date-row {
  flex-direction: row;  /* 여기도 display: flex가 있어야 하는데... */
  gap: 10px;
}
</style>
