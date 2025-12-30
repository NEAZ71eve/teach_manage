import { createRouter, createWebHistory } from "vue-router";
import { ElMessage } from "element-plus";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      redirect: "/login",
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/courses",
      name: "courses",
      component: () => import("../views/CourseManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "course:list",
          "course:add",
          "course:edit",
          "course:delete",
        ],
      },
    },
    {
      path: "/training-programs",
      name: "training-programs",
      component: () => import("../views/TrainingProgramManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "training-program:list",
          "training-program:add",
          "training-program:edit",
          "training-program:delete",
        ],
      },
    },
    {
      path: "/knowledge-points",
      name: "knowledge-points",
      component: () => import("../views/KnowledgePointManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "knowledge-point:list",
          "knowledge-point:add",
          "knowledge-point:edit",
          "knowledge-point:delete",
        ],
      },
    },
    {
      path: "/question-bank",
      name: "question-bank",
      component: () => import("../views/QuestionBankManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "question:list",
          "question:add",
          "question:edit",
          "question:delete",
        ],
      },
    },
    {
      path: "/exam-papers",
      name: "exam-papers",
      component: () => import("../views/ExamPaperManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "exam-paper:list",
          "exam-paper:add",
          "exam-paper:edit",
          "exam-paper:delete",
        ],
      },
    },
    {
      path: "/statistics",
      name: "statistics",
      component: () => import("../views/Statistics.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["statistics:view"],
      },
    },
    {
      path: "/practice-projects",
      name: "practice-projects",
      component: () => import("../views/PracticeProjectManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "practice-project:list",
          "practice-project:add",
          "practice-project:edit",
          "practice-project:delete",
        ],
      },
    },
    {
      path: "/semester-management",
      name: "semester-management",
      component: () => import("../views/SemesterManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "semester:list",
          "semester:add",
          "semester:edit",
          "semester:delete",
        ],
      },
    },
    {
      path: "/semester-schedule",
      name: "semester-schedule",
      component: () => import("../views/SemesterSchedule.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["semester-schedule:view"],
      },
    },
    {
      path: "/users",
      name: "users",
      component: () => import("../views/UserManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["user:list", "user:add", "user:edit", "user:delete"],
      },
    },
    {
      path: "/roles",
      name: "roles",
      component: () => import("../views/RoleManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: ["role:list", "role:add", "role:edit", "role:delete"],
      },
    },
    {
      path: "/permissions",
      name: "permissions",
      component: () => import("../views/PermissionManagement.vue"),
      meta: {
        requiresAuth: true,
        permissions: [
          "permission:list",
          "permission:add",
          "permission:edit",
          "permission:delete",
        ],
      },
    },
  ],
});

// 检查用户是否有访问该路由的权限
const hasPermission = (to, permissions) => {
  // 如果路由没有配置权限要求，则允许访问
  if (!to.meta.permissions || to.meta.permissions.length === 0) {
    return true;
  }

  // 如果用户没有权限信息，则不允许访问
  if (!permissions || permissions.length === 0) {
    return false;
  }

  // 检查用户是否有路由要求的任一权限
  const permissionCodes = permissions.map((p) => p.permissionCode);
  return to.meta.permissions.some((requiredPermission) => {
    return permissionCodes.includes(requiredPermission);
  });
};

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // 检查是否登录
    const token = localStorage.getItem("token");
    const user = localStorage.getItem("user");
    if (!token && !user) {
      // 未登录，跳转到登录页
      next({
        path: "/login",
        query: { redirect: to.fullPath },
      });
      return;
    }

    // 已登录，检查权限
    const permissionsStr = localStorage.getItem("permissions");
    const permissions = permissionsStr ? JSON.parse(permissionsStr) : [];

    if (hasPermission(to, permissions)) {
      // 有权限，继续访问
      next();
    } else {
      // 无权限，跳转到登录页或显示错误信息
      ElMessage.error("您没有访问该页面的权限");
      // 避免无限重定向，直接放行但显示错误信息
      next(false);
    }
  } else {
    // 不需要认证的路由，直接访问
    next();
  }
});

export default router;
