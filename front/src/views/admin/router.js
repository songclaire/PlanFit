export default [
  {
    path: '/admin',
    component: () => import('./Admin.vue'),
    children: [
      { path: '', redirect: { name: 'AdminMember' } },
      { path: 'member', name: 'AdminMember', component: () => import('./Member.vue') },
    ]
  }
]
