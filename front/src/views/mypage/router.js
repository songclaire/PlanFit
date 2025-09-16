export default [
  {
    path: '/mypage',
    component: () => import('./MyPage.vue'), // 부모 레이아웃
    children: [
    ]
  }
]
