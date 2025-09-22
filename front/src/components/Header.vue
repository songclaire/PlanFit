<template>
  <header class="header">
    <div class="container">
      <!-- 로고 -->
      <router-link to="/" class="logo">🧭 PlanFit</router-link>

      <!-- 중앙 메뉴 -->
      <nav class="nav-menu">
        <router-link to="/schedule" @click.native.prevent="goIfLogin('/schedule')"> 일정 </router-link>
        <router-link to="/learn" @click.native.prevent="goIfLogin('/learn')"> 학습 </router-link>
        <router-link to="/trip" @click.native.prevent="goIfLogin('/trip')"> 여행 </router-link>
        <router-link to="/food" @click.native.prevent="goIfLogin('/food')"> 식단 </router-link>
        <router-link to="/mypage" @click.native.prevent="goIfLogin('/mypage')"> 마이페이지 </router-link>
        <router-link v-if="roles.includes('admin')" to="/admin" @click.native.prevent="goIfLogin('/admin')"> 관리자페이지 </router-link>
      </nav>

      <!-- 우측 로그인 -->
      <div class="auth">
        <template v-if="isLogin">
          <span>{{ userName }}님, 환영합니다. </span>
          <button class="logout-button" @click="logout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/signup">회원가입</router-link>
          <span>|</span>
          <router-link to="/login">로그인</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const isLogin = ref(false)
const userName = ref('')
const roles = ref([])
const router = useRouter()

function checkAuth() {
  const token = localStorage.getItem('accessToken')
  if (token) {
    isLogin.value = true
    userName.value = localStorage.getItem('userName') || '사용자'
    roles.value = JSON.parse(localStorage.getItem('roles') || "[]")
  } else {
    isLogin.value = false
    userName.value = ''
  }
}

const goIfLogin = (path) => {
  if (isLogin.value) {
    router.push(path)
  } else {
    alert('로그인이 필요합니다.')
    router.push('/login')
  }
}

onMounted(() => {
  // 새로고침 시에도 로그인 상태 유지
  checkAuth()

  // login.vue에서 로그인 성공시 바로 반영
  window.addEventListener('login-success', () => {
    isLogin.value = true
    userName.value = localStorage.getItem('userName') || '사용자'
    roles.value = JSON.parse(localStorage.getItem('roles') || '[]')
  })
})

const logout = () => {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('userName')
    localStorage.removeItem('roles')
    isLogin.value = false
    roles.value = []
    router.push('/')
}

</script>

<style scoped>
.header {
  background-color: #f5f9ff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
  height: 80px;
  display: flex;
  align-items: center;
}

.container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}

.logo {
  font-size: 26px;
  font-weight: bold;
  color: #0e4b75;
  cursor: pointer;
  text-decoration: none; /* 기본 링크 밑줄 제거 */
}

.nav-menu {
  display: flex;
  gap: 40px;
  font-size: 17px;
  gap: 60px;
  font-weight: 500;
}

.nav-menu a {
  color: #222;
  text-decoration: none;
}

.nav-menu a:hover {
  color: #1a73e8;
}

.auth {
  font-size: 14px;
  color: #444;
  display: flex;
  gap: 8px;
}

.auth a {
  text-decoration: none;
  color: #1a1a1a;
}

.auth a:hover {
  color: #1a73e8;
}

.logout-button {
  background-color: #5b9bd5; /* 더 부드러운 파란색 */
  color: white;
  font-size: 0.85rem;
  padding: 2px 5px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.logout-button:hover {
  background-color: #407ec9; /* hover 시 약간 더 진한 파란색 */
}
</style>
