<template>
  <div class="signup-container">
    <h2>회원가입</h2>
    <form @submit.prevent="handleSignup">
      <div class="form-group">
        <label for="userId">아이디</label>
        <input type="text" id="userId" v-model="userId" required />
      </div>
      <div class="form-group">
        <label for="userName">이름</label>
        <input type="text" id="userName" v-model="userName" required />
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label for="password">비밀번호</label>
          <input type="password" id="password" v-model="password" required />
        </div>
        <div class="form-group half">
          <label for="confirmPassword">비밀번호 확인</label>
          <input type="password" id="confirmPassword" v-model="confirmPassword" required />
        </div>
      </div>

      <div class="form-group">
        <label for="email">이메일</label>
        <input type="email" id="email" v-model="email" required />
      </div>

      <div class="form-row">
        <div class="form-group half">
          <label for="birthDate">생년월일</label>
          <input type="date" id="birthDate" v-model="birthDate" required />
        </div>
        <div class="form-group half">
          <label for="telNo">전화번호</label>
          <input type="tel" id="telNo" v-model="telNo" required />
        </div>
      </div>

      <div class="form-group">
        <label for="roadNmAddr">도로명 주소</label>
        <input type="text" id="roadNmAddr" v-model="roadNmAddr" required />
      </div>

      <div class="form-group">
        <label for="roadNmDaddr">상세 주소</label>
        <input type="text" id="roadNmDaddr" v-model="roadNmDaddr" required />
      </div>

      <button type="submit">회원가입</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const userId = ref('')
const userName = ref('')
const password = ref('')
const confirmPassword = ref('')
const email = ref('')
const birthDate = ref('')
const telNo = ref('')
const roadNmAddr = ref('')
const roadNmDaddr = ref('')
const router = useRouter()

const handleSignup = async () => {
    try {
        const response = await axios.post('/api/signup', {
              userId: userId.value,
              userName: userName.value,
              password: password.value,
              confirmPassword: confirmPassword.value,
              email: email.value,
              birthDate: birthDate.value,
              telNo: telNo.value,
              roadNmAddr: roadNmAddr.value,
              roadNmDaddr: roadNmDaddr.value,
        })
        console.log('성공!', response.data)
        alert('회원가입 완료')
        router.push('/')
    } catch (err) {
        console.error('에러', err)
        alert('회원가입 실패')
    }
}


//const handleSignup = () => {
//  console.log('회원가입 데이터:', {
//    userId: userId.value,
//    password: password.value,
//    confirmPassword: confirmPassword.value,
//    email: email.value,
//    birth: birth.value,
//    phone: phone.value,
//    address: address.value,
//    detailAddress: detailAddress.value,
//  })
//}
</script>

<style scoped>
.signup-container {
  max-width: 500px;
  margin: 60px auto;
  padding: 2rem;
  border: 1px solid #ddd;
  border-radius: 12px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}
.signup-container h2 {
  font-size: 30px; /* or 22px, 취향껏 */
  font-weight: 600;
  line-height: 1.3;
  margin: 0 0 1.5rem;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #205295;
}

.form-group {
  margin-bottom: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-group.half {
  flex: 1;
}

label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 600;
  color: #333;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 0.9rem;
  background-color: #205295;
  color: white;
  font-weight: bold;
  font-size: 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 0.5rem;
}

button:hover {
  background-color: #003c85;
}
</style>
