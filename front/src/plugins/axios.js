// front/src/plugins/axios.js

import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',  // 너 Vite proxy 쓰니까 이걸로 충분함
  withCredentials: true // CORS에서 쿠키 쓸 거면 true
})

// 요청 보내기 전에 토큰 자동으로 헤더에 추가
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

export default instance
