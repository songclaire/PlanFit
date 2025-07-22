<template>
  <div class="home">
    <div class="home-box">
      <h1>PlanFit에 오신 걸 환영합니다</h1>
      <p>계획을 세우고, 목표를 이루세요.</p>
      <router-link to="/schedule">
        <button class="home-button">일정 시작하기</button>
      </router-link>
    </div>
  </div>
</template>

<script setup>
// 특별한 로직은 없음
</script>

<style scoped>
.home {
  display: flex;
  justify-content: center;
  align-items: flex-start; /* 위로 올림 */
  min-height: 100vh;
  background-color: #f0f4f8;
  padding-top: 100px; /* 위쪽 여백 */
}

.home-box {
  background: white;
  padding: 3rem 4rem; /* 상하좌우 더 큼 */
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 600px; /* 더 넓게 */
}

.home-box h1 {
  font-size: 2rem;
  color: #205295;
  margin-bottom: 1rem;
}

.home-box p {
  font-size: 1.1rem;
  color: #444;
  margin-bottom: 2rem;
}

.home-button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.home-button:hover {
  background-color: #2563eb;
}
</style>
