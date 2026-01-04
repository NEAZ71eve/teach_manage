<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <div class="operation-bar">
      <el-button type="primary" @click="openAddDialog">新增用户</el-button>
    </div>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="roleName" label="用户类型" width="140">
        <template #default="scope">
          {{ displayRoleName(scope.row.roleName) }}
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="programId" label="负责专业" width="140">
        <template #default="scope">
          {{ getProgramName(scope.row.programId) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? "启用" : "禁用" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="scope">
          <el-button
            type="primary"
            size="small"
            @click="openEditDialog(scope.row)"
            >编辑</el-button
          >
          <el-button
            type="danger"
            size="small"
            @click="deleteUser(scope.row.userId)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item
          label="密码"
          required
          :rules="[
            {
              required: !formData.userId,
              message: '请输入密码',
              trigger: 'blur',
            },
          ]"
        >
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="请输入密码"
          />
        </el-form-item>
        <el-form-item label="姓名" required>
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input
            v-model="formData.email"
            placeholder="请输入邮箱"
            type="email"
          />
        </el-form-item>
        <el-form-item label="电话" required>
          <el-input v-model="formData.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="用户类型" required>
          <el-select
            v-model="formData.roleId"
            placeholder="请选择用户类型"
            style="width: 100%"
            @change="handleRoleChange"
            :rules="[
              { required: true, message: '请选择用户类型', trigger: 'blur' },
            ]"
          >
            <el-option
              v-for="role in availableRoles"
              :key="role.roleId"
              :label="displayRoleName(role.roleName)"
              :value="role.roleId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="负责专业" v-if="isProgramTeacher">
          <el-select
            v-model="formData.programId"
            placeholder="请选择负责的专业（培养方案）"
            style="width: 100%"
          >
            <el-option
              v-for="program in programs"
              :key="program.programId"
              :label="program.majorName"
              :value="program.programId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import {
  getUsers,
  addUser,
  updateUser,
  deleteUser as deleteUserApi,
  getUserRoles,
  assignRoles,
} from "../api/user";
import { getRoles } from "../api/role";
import { getAllTrainingPrograms } from "../api/trainingProgram";

const users = ref([]);
const roles = ref([]);
const programs = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref("新增用户");
const formData = ref({
  userId: null,
  username: "",
  password: "",
  realName: "",
  email: "",
  phone: "",
  roleId: null,
  programId: null,
  status: 1,
});

const isProgramTeacher = computed(() => {
  const role = roles.value.find((r) => r.roleId === formData.value.roleId);
  return role && role.roleName === "学院管理员";
});

const availableRoles = computed(() => {
  return roles.value.filter((role) =>
    ["系统管理员", "学院管理员", "教师"].includes(role.roleName)
  );
});

const displayRoleName = (roleName) => {
  if (roleName === "学院管理员") return "专业负责教师";
  if (roleName === "教师") return "普通老师";
  return roleName || "未分配";
};

// 获取专业名称
const getProgramName = (programId) => {
  if (!programId) return "-";
  const program = programs.value.find((p) => p.programId === programId);
  return program ? program.majorName : "未知专业";
};

// 获取用户列表
const loadUsers = async () => {
  try {
    const response = await getUsers();
    users.value = response;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
    console.error("获取用户列表失败:", error);
  }
};

// 获取角色列表
const loadRoles = async () => {
  try {
    const response = await getRoles();
    roles.value = response;
  } catch (error) {
    ElMessage.error("获取角色列表失败");
    console.error("获取角色列表失败:", error);
  }
};

// 获取培养方案列表
const loadPrograms = async () => {
  try {
    const response = await getAllTrainingPrograms();
    programs.value = response;
  } catch (error) {
    ElMessage.error("获取专业列表失败");
    console.error("获取专业列表失败:", error);
  }
};

// 获取用户角色
const loadUserRoles = async (userId) => {
  try {
    const response = await getUserRoles(userId);
    formData.value.roleId = response[0]?.roleId ?? null;
  } catch (error) {
    ElMessage.error("获取用户角色失败");
    console.error("获取用户角色失败:", error);
  }
};

// 角色变化时处理
const handleRoleChange = async () => {
  if (!isProgramTeacher.value) {
    formData.value.programId = null;
  }
};

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = "新增用户";
  formData.value = {
    userId: null,
    username: "",
    password: "",
    realName: "",
    email: "",
    phone: "",
    roleId: null,
    programId: null,
    status: 1,
  };
  dialogVisible.value = true;
};

// 打开编辑对话框
const openEditDialog = async (user) => {
  dialogTitle.value = "编辑用户";
  formData.value = {
    ...user,
    password: "", // 编辑时清空密码，如需修改密码则重新输入
    roleId: user.roleId || null,
    programId: user.programId || null,
  };
  dialogVisible.value = true;

  // 获取用户角色
  await loadUserRoles(user.userId);

};

// 保存用户
const saveUser = async () => {
  try {
    if (!formData.value.roleId) {
      ElMessage.error("请选择用户类型");
      return;
    }
    if (isProgramTeacher.value && !formData.value.programId) {
      ElMessage.error("专业负责教师必须选择负责专业");
      return;
    }
    let userId;
    if (formData.value.userId) {
      // 更新用户
      await updateUser(formData.value.userId, {
        username: formData.value.username,
        realName: formData.value.realName,
        email: formData.value.email,
        phone: formData.value.phone,
        password: formData.value.password,
        programId: formData.value.programId,
        status: formData.value.status,
      });
      userId = formData.value.userId;
    } else {
      // 新增用户
      const result = await addUser({
        username: formData.value.username,
        password: formData.value.password,
        realName: formData.value.realName,
        email: formData.value.email,
        phone: formData.value.phone,
        programId: formData.value.programId,
        status: formData.value.status,
      });
      userId = result;
    }

    // 分配角色
    await assignRoles(userId, [formData.value.roleId]);

    ElMessage.success(formData.value.userId ? "更新用户成功" : "新增用户成功");
    dialogVisible.value = false;
    loadUsers();
  } catch (error) {
    // 处理错误信息
    let errorMsg = formData.value.userId ? "更新用户失败" : "新增用户失败";
    if (error.response && error.response.data) {
      errorMsg = error.response.data.message || errorMsg;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
    console.error("保存用户失败:", error);
  }
};

// 删除用户
const deleteUser = async (userId) => {
  try {
    const response = await deleteUserApi(userId);
    if (response > 0) {
      ElMessage.success("删除用户成功");
      loadUsers();
    } else {
      ElMessage.error("删除用户失败");
    }
  } catch (error) {
    ElMessage.error("删除用户失败");
    console.error("删除用户失败:", error);
  }
};

// 页面加载时获取用户列表、角色列表和培养方案列表
onMounted(() => {
  loadUsers();
  loadRoles();
  loadPrograms();
});
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}
</style>
