<template>
  <div class="login-container">
    <h2>로그인</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="userId">아이디</label>
        <input type="text" id="userId" v-model="userId" required />
      </div>
      <div class="form-group">
        <label for="password">비밀번호</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button type="submit">로그인</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const userId = ref('')
const password = ref('')
const router = useRouter()

const handleLogin = async () => {
    try {
        const res = await axios.post('/api/login', {
            userId : userId.value,
            password : password.value
        });

        //⭐2. 토큰 꺼내서 localStorage에 저장
        // AuthController에서 보낸 token 겟
        const token = res.data.token;

        // JWT 토큰을 localStorage에 저장 + 권한
        localStorage.setItem('accessToken', token);
        localStorage.setItem('userId', userId.value);
        localStorage.setItem('userName', res.data.userName);
        localStorage.setItem('roles', JSON.stringify(res.data.roles));

         // ⭐ 로그인 성공 시 로그인 상태 reactive 상태 변경 (event 또는 전역 상태 필요)
        window.dispatchEvent(new Event('login-success'))

        // 로그인 성공 후 이동할 페이지
        alert('로그인 성공!');
        router.push('/');
    } catch (error) {
        alert('로그인 실패');
        console.error(error);
    }

}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 80px auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.login-container h2 {
  font-size: 30px;
  font-weight: 600;
  margin-bottom: 1.5rem;
  text-align: center;
  line-height: 1.2;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #205295;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #333;
}

input {
  width: 100%;
  padding: 0.7rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box; /* ✅ 추가해 */
}

button {
  width: 100%;
  padding: 0.8rem;
  background-color: #205295;
  color: white;
  font-weight: bold;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
}

button:hover {
  background-color: #003c85;
}
</style>
