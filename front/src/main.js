//⭐3. Axios 인터셉터 설정. localStorage에서 꺼낸 token을 header에 넣어줌
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

import 'quasar/src/css/index.sass'
import { Quasar, Notify } from 'quasar'
import quasarLang from 'quasar/lang/ko-KR'
import '@quasar/extras/material-icons/material-icons.css'

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
app.use(Quasar, {
  plugins: { Notify }, // 필요한 플러그인 넣을 수 있음
  lang: quasarLang
})

const token = localStorage.getItem('accessToken')
//if (token) axios.defaults.headers.common.Authorization = `Bearer ${token}`

app.mount('#app')
