import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/target-info',
      name: 'targetInfo',
      component: () => import('../views/TargetInfoForm.vue')
    },
    {
      path: '/dialog',
      name: 'dialog',
      component: () => import('../views/DialogView.vue')
    }
  ]
})

export default router