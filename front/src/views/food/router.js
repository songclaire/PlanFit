export default [
  {
    path: '/food',
    component: () => import('./Food.vue'), // 부모 레이아웃
    children: [
      { path: '', redirect: { name: 'FoodRecipeList' } },
      { path: 'recipe', name: 'FoodRecipeList', component: () => import('./Recipe.vue') },
      { path: 'recipe/:recipeSn', name: 'FoodRecipeView', component: () => import('./RecipeView.vue'), props: true },
      { path: 'restaurant', name: 'RestaurantList', component: () => import('./Restaurant.vue') }
    ]
  }
]
