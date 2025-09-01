<template>
  <div class="schedule-wrapper">
    <div class="header-row">
      <h2>일정 관리</h2>
      <button class="add-button" @click="handleAddClick">+ 일정 추가</button>
    </div>

    <div class="main-layout">
      <!-- 왼쪽: 달력 -->
      <div class="calendar-area">
        <FullCalendar :options="calendarOptions" />
      </div>

      <!-- 오른쪽: 일정 상세 or 내용 보기 -->
      <div class="detail-area">
        <h3>
          {{ mode === 'edit' && selectedEvent?.schdSn ? '📝 일정 수정'
               : mode === 'edit' ? '📝 일정 추가'
               : '📝 일정 정보' }}
        </h3>
        <ScheduleForm
            v-if="showDetailForm"
            :initial-data="selectedEvent"
            :mode="mode"
            @schedule-added="addEventToCalendar"
            @edit-mode="handleEditMode"
        />
        <!-- 나중에 목록, 수정, 삭제 등 여기에 붙이면 됨 -->
      </div>
    </div>
  </div>
</template>

<script setup>
import ScheduleForm from './ScheduleForm.vue'

import FullCalendar from '@fullcalendar/vue3'
import dayGridPlugin from '@fullcalendar/daygrid'
import { ref, onMounted } from 'vue'
import axios from 'axios'

const events = ref([])
const selectedEvent = ref(null)
const mode = ref('edit')

const showDetailForm = ref(false)

const calendarOptions = ref ({
    plugins: [dayGridPlugin],
    initialView: 'dayGridMonth',
    events,
    eventClick: handleEventClick,
    dateClick: handleDateClick
})

/**
 * 일정 추가 버튼
 */
function handleAddClick() {
    selectedEvent.value = {
        schdSn: '',
        schdTypeSn: '',
        schdTtl: '',
        startDt: '',
        endDt: '',
        schdCn: '',
        location: '',
        ptcptTypeNm: '',
        schdPtcptSn: '',
        color: ''
    }
    mode.value = 'edit'
    showDetailForm.value = true;
}

/**
 * 일정 목록 출력
 */
async function selectSchdList() {
    try {
        const res = await axios.post('/api/schdList', {})
        const schdList = res.data
        console.log('✅일정 목록', schdList)

        //FullCalendar에 맞게 매핑
        events.value = schdList.map(item => ({
            id: item.schdSn,
            title: item.schdTtl,
            start: item.startDt,
            end: addOneDay(item.endDt),
            backgroundColor: item.color
        }))
    } catch (err) {
        console.error('등록 실패', err)
        alert('조회 실패')
    }
}

/**
 * 등록 후 바로 달력 적용
 */
function addEventToCalendar(newEvent) {
    const index = events.value.findIndex(ev => ev.id === newEvent.schdSn)

    const updatedEvent = {
        id: newEvent.schdSn,
        title: newEvent.schdTtl,
        start: newEvent.startDt,
        end: addOneDay(newEvent.endDt),
        backgroundColor: newEvent.color
    }

    // 이미 등록된 건을 수정하는 경우
    if (index !== -1) {
        events.value.splice(index, 1, updatedEvent)
    // 처음 등록하는 건일 경우
    } else {
        events.value.push(updatedEvent)
    }
    showDetailForm.value = false
}

// 달력 일정 클릭 후 등록폼 출력
function handleDateClick(info) {
    console.log('날짜 클릭', info.dateStr)
}

// 달력 일정 클릭시 조회
async function handleEventClick(info) {
    try {
        const schdSn = info.event.id
        const res = await axios.get(`/api/schd/${schdSn}`)
        selectedEvent.value = res.data
        mode.value = 'view'
        showDetailForm.value = true
    } catch (err) {
        console.error('상세 조회 실패', err)
        alert('조회 실패!')
    }
}

// END_DT 설정
function addOneDay(dateStr) {
    const date = new Date(dateStr)
    date.setDate(date.getDate() + 1)
    return date.toISOString().split('T')[0]
}

// 자식에서 '수정' 클릭 시
function handleEditMode() {
    mode.value = 'edit'
}

onMounted(() => {
    selectSchdList()
})

</script>







<style scoped>
.schedule-wrapper {
  max-width: 1200px;
  margin: 40px auto;
  padding: 32px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.05);
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.header-row h2 {
  font-size: 30px; /* 기존보다 작게 조절 */
  font-weight: 600;
  margin: 0;
}

.main-layout {
  display: flex;
  gap: 24px;
}

.calendar-area {
  flex: 2;
}

.detail-area {
  flex: 1;
  background-color: #f9fbfe;
  border-radius: 10px;
  padding: 20px;
  border: 1px solid #e0e6f1;
}

.detail-area h3 {
  font-size: 20px;
  font-weight: 500;
  margin-bottom: 12px;
}

.add-button {
  background-color: #e3edf8;
  color: #1a73e8;
  border: 1px solid #c3d9f5;
  padding: 7px 12px;
  border-radius: 6px;
  font-size: 0.92rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-button:hover {
  background-color: #d2e3f8;
  color: #1a5ec9;
}

</style>
