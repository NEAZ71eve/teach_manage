<template>
  <div class="role-management">
    <h2>角色管理</h2>
    <div class="operation-bar">
      <el-button type="primary" @click="openAddDialog">新增角色</el-button>
    </div>
    <el-table :data="roles" style="width: 100%">
      <el-table-column prop="roleId" label="角色ID" width="80" />
      <el-table-column prop="roleName" label="角色名称" width="120" />
      <el-table-column prop="roleDesc" label="角色描述" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteRole(scope.row.roleId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="80px">
        <el-form-item label="角色名称" required>
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="formData.roleDesc" placeholder="请输入角色描述" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRole">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoles, addRole, updateRole, deleteRole as deleteRoleApi } from '../api/role'

const roles = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formData = ref({
  roleId: null,
  roleName: '',
  roleDesc: ''
})

// 获取角色列表
const loadRoles = async () => {
  try {
    const response = await getRoles()
    roles.value = response
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error('获取角色列表失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增角色'
  formData.value = {
    roleId: null,
    roleName: '',
    roleDesc: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (role) => {
  dialogTitle.value = '编辑角色'
  formData.value = { ...role }
  dialogVisible.value = true
}

// 保存角色
const saveRole = async () => {
  try {
    let response
    if (formData.value.roleId) {
      response = await updateRole(formData.value.roleId, formData.value)
    } else {
      response = await addRole(formData.value)
    }
    if (response > 0) {
      ElMessage.success(formData.value.roleId ? '更新角色成功' : '新增角色成功')
      dialogVisible.value = false
      loadRoles()
    } else {
      ElMessage.error(formData.value.roleId ? '更新角色失败' : '新增角色失败')
    }
  } catch (error) {
    ElMessage.error(formData.value.roleId ? '更新角色失败' : '新增角色失败')
    console.error('保存角色失败:', error)
  }
}

// 删除角色
const deleteRole = async (roleId) => {
  try {
    const response = await deleteRoleApi(roleId)
    if (response > 0) {
      ElMessage.success('删除角色成功')
      loadRoles()
    } else {
      ElMessage.error('删除角色失败')
    }
  } catch (error) {
    ElMessage.error('删除角色失败')
    console.error('删除角色失败:', error)
  }
}

// 页面加载时获取角色列表
onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}
</style>
