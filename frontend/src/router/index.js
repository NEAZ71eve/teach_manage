import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/courses'
    },
    {
      path: '/courses',
      name: 'courses',
      component: () => import('../views/CourseManagement.vue')
    },
    {
      path: '/training-programs',
      name: 'training-programs',
      component: () => import('../views/TrainingProgramManagement.vue')
    },
    {
      path: '/knowledge-points',
      name: 'knowledge-points',
      component: () => import('../views/KnowledgePointManagement.vue')
    },
    {
      path: '/question-bank',
      name: 'question-bank',
      component: () => import('../views/QuestionBankManagement.vue')
    },
    {
      path: '/exam-papers',
      name: 'exam-papers',
      component: () => import('../views/ExamPaperManagement.vue')
    },
    {
      path: '/statistics',
      name: 'statistics',
      component: () => import('../views/Statistics.vue')
    },
    {
      path: '/practice-projects',
      name: 'practice-projects',
      component: () => import('../views/PracticeProjectManagement.vue')
    },
    {
      path: '/semester-management',
      name: 'semester-management',
      component: () => import('../views/SemesterManagement.vue')
    },
    {
      path: '/semester-schedule',
      name: 'semester-schedule',
      component: () => import('../views/SemesterSchedule.vue')
    },
    {
      path: '/users',
      name: 'users',
      component: () => import('../views/UserManagement.vue')
    },
    {
      path: '/roles',
      name: 'roles',
      component: () => import('../views/RoleManagement.vue')
    },
    {
      path: '/permissions',
      name: 'permissions',
      component: () => import('../views/PermissionManagement.vue')
    }
  ]
})

export default router
