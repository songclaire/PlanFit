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
          {{ showDetailForm ? '📝 일정 추가' : '📝 일정 정보' }}
        </h3>
        <ScheduleForm v-if="showDetailForm" @schedule-added="addEventToCalendar" />
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

const showDetailForm = ref(false)

const calendarOptions = ref({
  plugins: [dayGridPlugin],
  initialView: 'dayGridMonth',
  events
})

/**
 * 일정 추가 버튼
 */
function handleAddClick() {
  showDetailForm.value = true;
}

/**
 * 일정 목록 출력
 */
async function selectSchdList() {
    try {
        const schdList = await axios.post('/api/schdList')
        console.log( 'schdList', schdList)
        alert('조회됐따!')
    } catch (err) {
        console.error('등록 실패', err)
        alert('조회 실패')
    }
}

/**
 * 등록 후 달력 적용
 */
function addEventToCalendar(newEvent) {
    console.log('@@@@@ new Event??', newEvent);
    events.value = [
        ...events.value,
        {
            title: newEvent.schdTtl,
            start: newEvent.startDt,
            end: newEvent.endDt,
        },
    ];
    showDetailForm.value = false;
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
