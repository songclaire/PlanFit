<template>
  <form class="schedule-form" @submit.prevent="submitForm">
    <!-- 일정 종류 -->
    <div class="type-color-row">
      <div class="input-group">
        <label>일정 종류</label>
        <select v-model="form.schdTypeSn" :disabled="isViewMode">
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

      <div class="input-group">
        <label>색상</label>
        <div class="color-options">
          <div
            v-for="color in colorOptions"
            :key="color.value"
            :style="{ backgroundColor: color.value, cursor: isViewMode ? 'default' : 'pointer', border: form.color === color.value ? '2px solid #000' : '1px solid #ccc' }"
            class="color-circle"
            :title="color.label"
            @click="!isViewMode && (form.color = color.value)"
          />
        </div>
        <div v-if="isViewMode" style="margin-top: 6px; font-size: 13px;">
        </div>
      </div>
    </div>

    <!-- 일정 제목 -->
    <div class="form-row">
      <label>일정 제목</label>
      <input type="text" v-model="form.schdTtl" required :readOnly="isViewMode" :required="!isViewMode" />
    </div>

    <!-- 시작일 / 종료일 -->
    <div class="form-row date-row">
      <div class="date-field">
        <label>시작일</label>
        <input type="date" v-model="form.startDt" required :disabled="isViewMode" />
      </div>
      <div class="date-field">
        <label>종료일</label>
        <input type="date" v-model="form.endDt" required :disabled="isViewMode" />
      </div>
    </div>

    <!-- 내용 -->
    <div class="form-row">
      <label>일정 내용</label>
      <textarea v-model="form.schdCn" rows="3"
        :required="!isViewMode"
        :readonly="isViewMode" />
    </div>

    <!-- 참여자 -->
    <div class="form-row">
      <label>참여자</label>
      <div class="participant-row">
        <select v-model="form.schdPtcptSn"
            :required="!isViewMode"
            :disabled="isViewMode">
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
      <input type="text" v-model="form.location" required :disabled="isViewMode" />
    </div>

    <div class="form-row button-row">
      <button v-if="isViewMode" type="button" class="edit-button" @click="$emit('edit-mode')">수정</button>
      <button v-if="!isViewMode || isViewMode" type="submit" class="submit-button">등록</button>
    </div>
  </form>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from 'axios'

const emit = defineEmits(['schedule-added'])
const isViewMode = computed(() => props.mode === 'view')

const props = defineProps({
    initialData: {
        type: Object,
        default: () => ({})
    },
    mode: {
        type: String,
        default: 'edit'
    }
})

console.log('props????', props)

const form = ref({
    schdSn: '',
    schdTypeSn: '',
    schdTtl: '',
    startDt: '',
    endDt: '',
    schdCn: '',
    location: '',
    ptcptTypeNm: '',
    schdPtcptSn: '',
    color: '#4F86C6'
})

const colorOptions = [
  { value: '#4F86C6', label: '파스텔 블루' },
  { value: '#81C784', label: '세이지 그린' },
  { value: '#FFD54F', label: '소프트 옐로우' },
  { value: '#FF8A65', label: '살구 오렌지' },
  { value: '#BA68C8', label: '라벤더 퍼플' }
]

// 일정 종류 목록
const schdTypeList = ref([])
// 일정 참가자 목록
const schdPtcptList = ref([])

// 부모에서 데이터 변경될 때 감지용
watch(
    () => props.initialData, // 부모 데이터(감시대상)
    (newVal) => {
        if (newVal) {
            form.value = { ...form.value, ...newVal }
        }
    },
    { immediate: true }
)

/**
 * 일정 저장
 */
async function submitForm() {
    try {
        if (form.value.schdSn) {
            await axios.post('/api/editSchd/' + form.value.schdSn, form.value)
            alert('일정이 수정되었습니다')
        } else {
            await axios.post('/api/saveSchd', form.value)
            alert('일정이 등록되었습니다')
        }

        // 등록된 일정 정보를 emit
        emit('schedule-added', {
//            schdTtl: form.value.schdTtl,
//            startDt: form.value.startDt,
//            endDt: form.value.endDt
            ...form.value
        })
    } catch (err) {
        console.error('등록 실패', err)
        alert('일정이 등록 실패')
    }
}

// 일정 종류 목록 출력
async function loadSchdTypeList() {
    try {
        const schdTypeRes = await axios.get('/api/schdTypeList')
        schdTypeList.value = schdTypeRes.data
    } catch (err) {
        console.error('일정종류 불러오기 실패', err)
    }
}

// 참여자 목록 출력
async function loadSchdPtcptList() {
    try {
        const schdPtcptRes = await axios.get('/api/schdPtcptList')
        schdPtcptList.value = schdPtcptRes.data
    } catch (err) {
        console.error('일정종류 불러오기 실패', err)
    }
}

// 선택된 색상 표기
function getColorLabel(colorValue) {
    const match = colorOptions.find(opt => opt.value === colorValue)
    return match ? match.label : colorValue
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

.button-row {
  display: flex;
  flex-direction: row;
  gap: 10px;
  justify-content: flex-end;
}

.edit-button {
  background-color: #9e9e9e;
  color: white;
  font-weight: 500;
  cursor: pointer;
  border: none;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.edit-button:hover {
  background-color: #757575;
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
.type-color-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.input-group {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.color-options {
  display: flex;
  gap: 6px;
  margin-top: 6px;
}

.color-circle {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  cursor: pointer;
  border: none;
  transition: border 0.2s;
}

.color-circle.selected {
  border: none;
}

</style>
