//⭐3. Axios 인터셉터 설정. localStorage에서 꺼낸 token을 header에 넣어줌
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// Axios 인터셉터 설정 (요청마다 토큰 헤더 자동 추가)
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, error => {
  return Promise.reject(error);
});

const app = createApp(App)
app.use(router)
app.mount('#app')
