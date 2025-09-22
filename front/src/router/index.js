import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue';

import authRoutes from '../views/auth/router';
import scheduleRoutes from '../views/schedule/router';
import foodRoutes from '../views/food/router';
import myPageRoutes from '../views/mypage/router';
import adminRoutes from '../views/admin/router';

const routes = [
  { path: '/', component: Home },
  ...authRoutes,
  ...scheduleRoutes,
  ...foodRoutes,
  ...myPageRoutes,
  ...adminRoutes,
];

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 로그인 필수 페이지들 걸러냄
 */
router.beforeEach((to, from, next) => {
    const isAuthenticated = !!localStorage.getItem('accessToken')

    // 공개 페이지들만 예외 허용
    const publicPages = ['/', '/login', '/signup']

    const isPublic = publicPages.includes(to.path)

    if (!isPublic && !isAuthenticated) {
        alert('로그인이 필요합니다.')
        next('/login')
    } else {
        next()
    }

})

export default router