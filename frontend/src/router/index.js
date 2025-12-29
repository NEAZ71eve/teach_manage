import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/courses',
      name: 'courses',
      component: () => import('../views/CourseManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/training-programs',
      name: 'training-programs',
      component: () => import('../views/TrainingProgramManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/knowledge-points',
      name: 'knowledge-points',
      component: () => import('../views/KnowledgePointManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/question-bank',
      name: 'question-bank',
      component: () => import('../views/QuestionBankManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/exam-papers',
      name: 'exam-papers',
      component: () => import('../views/ExamPaperManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/Statistics.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/practice-projects',
      name: 'practice-projects',
      component: () => import('../views/PracticeProjectManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/semester-management',
      name: 'semester-management',
      component: () => import('../views/SemesterManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/semester-schedule',
      name: 'semester-schedule',
      component: () => import('../views/SemesterSchedule.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UserManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/roles',
      name: 'roles',
      component: () => import('../views/RoleManagement.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/permissions',
      name: 'permissions',
      component: () => import('../views/PermissionManagement.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查是否登录
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 已登录，继续访问
      next()
    }
  } else {
    // 不需要认证的路由，直接访问
    next()
  }
})

export default router
