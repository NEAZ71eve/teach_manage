<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <div class="operation-bar">
      <el-button type="primary" @click="openAddDialog">新增用户</el-button>
    </div>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="userId" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="realName" label="真实姓名" width="120" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="电话" width="120" />
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
        <el-form-item label="真实姓名" required>
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
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
        <el-form-item label="角色" required>
          <el-select
            v-model="formData.roleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <el-option
              v-for="role in roles"
              :key="role.roleId"
              :label="role.roleName"
              :value="role.roleId"
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
import { ref, onMounted } from "vue";
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

const users = ref([]);
const roles = ref([]);
const dialogVisible = ref(false);
const dialogTitle = ref("新增用户");
const formData = ref({
  userId: null,
  username: "",
  password: "",
  realName: "",
  email: "",
  phone: "",
  roleIds: [],
  status: 1,
});

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

// 获取用户角色
const loadUserRoles = async (userId) => {
  try {
    const response = await getUserRoles(userId);
    formData.value.roleIds = response.map((item) => item.roleId);
  } catch (error) {
    ElMessage.error("获取用户角色失败");
    console.error("获取用户角色失败:", error);
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
    roleIds: [],
    status: 1,
  };
  dialogVisible.value = true;
};

// 打开编辑对话框
const openEditDialog = (user) => {
  dialogTitle.value = "编辑用户";
  formData.value = {
    ...user,
    password: "", // 编辑时清空密码，如需修改密码则重新输入
    roleIds: [],
  };
  dialogVisible.value = true;
  // 获取用户角色
  loadUserRoles(user.userId);
};

// 保存用户
const saveUser = async () => {
  try {
    let userId;

    // 准备保存的数据
    const userData = {
      ...formData.value,
    };

    if (formData.value.userId) {
      // 更新用户
      await updateUser(formData.value.userId, userData);
      userId = formData.value.userId;
    } else {
      // 新增用户
      const response = await addUser(userData);
      if (response > 0) {
        userId = response;
      } else {
        ElMessage.error("新增用户失败");
        return;
      }
    }

    // 分配角色
    await assignRoles(userId, formData.value.roleIds);

    ElMessage.success(formData.value.userId ? "更新用户成功" : "新增用户成功");
    dialogVisible.value = false;
    loadUsers();
  } catch (error) {
    ElMessage.error(formData.value.userId ? "更新用户失败" : "新增用户失败");
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

// 页面加载时获取用户列表和角色列表
onMounted(() => {
  loadUsers();
  loadRoles();
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
