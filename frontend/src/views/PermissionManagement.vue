<template>
  <div class="permission-management">
    <h2>权限管理</h2>
    <div class="operation-bar">
      <el-button type="primary" @click="openAddDialog">新增权限</el-button>
    </div>
    <el-table :data="permissions" style="width: 100%">
      <el-table-column prop="permissionId" label="权限ID" width="80" />
      <el-table-column prop="permissionName" label="权限名称" width="150" />
      <el-table-column prop="permissionCode" label="权限编码" width="150" />
      <el-table-column prop="url" label="访问路径" width="200" />
      <el-table-column prop="method" label="请求方法" width="100" />
      <el-table-column prop="description" label="权限描述" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button type="primary" size="small" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="deletePermission(scope.row.permissionId)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="权限名称" required>
          <el-input v-model="formData.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" required>
          <el-input v-model="formData.permissionCode" placeholder="请输入权限编码，如：user:list" />
        </el-form-item>
        <el-form-item label="访问路径">
          <el-input v-model="formData.url" placeholder="请输入访问路径，如：/users" />
        </el-form-item>
        <el-form-item label="请求方法">
          <el-select v-model="formData.method" placeholder="请选择请求方法">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限描述">
          <el-input v-model="formData.description" placeholder="请输入权限描述" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePermission">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissions, addPermission, updatePermission, deletePermission as deletePermissionApi } from '../api/permission'

const permissions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增权限')
const formData = ref({
  permissionId: null,
  permissionName: '',
  permissionCode: '',
  url: '',
  method: '',
  description: ''
})

// 获取权限列表
const loadPermissions = async () => {
  try {
    const response = await getPermissions()
    permissions.value = response
  } catch (error) {
    ElMessage.error('获取权限列表失败')
    console.error('获取权限列表失败:', error)
  }
}

// 打开新增对话框
const openAddDialog = () => {
  dialogTitle.value = '新增权限'
  formData.value = {
    permissionId: null,
    permissionName: '',
    permissionCode: '',
    url: '',
    method: '',
    description: ''
  }
  dialogVisible.value = true
}

// 打开编辑对话框
const openEditDialog = (permission) => {
  dialogTitle.value = '编辑权限'
  formData.value = { ...permission }
  dialogVisible.value = true
}

// 保存权限
const savePermission = async () => {
  try {
    let response
    if (formData.value.permissionId) {
      response = await updatePermission(formData.value.permissionId, formData.value)
    } else {
      response = await addPermission(formData.value)
    }
    if (response > 0) {
      ElMessage.success(formData.value.permissionId ? '更新权限成功' : '新增权限成功')
      dialogVisible.value = false
      loadPermissions()
    } else {
      ElMessage.error(formData.value.permissionId ? '更新权限失败' : '新增权限失败')
    }
  } catch (error) {
    ElMessage.error(formData.value.permissionId ? '更新权限失败' : '新增权限失败')
    console.error('保存权限失败:', error)
  }
}

// 删除权限
const deletePermission = async (permissionId) => {
  try {
    const response = await deletePermissionApi(permissionId)
    if (response > 0) {
      ElMessage.success('删除权限成功')
      loadPermissions()
    } else {
      ElMessage.error('删除权限失败')
    }
  } catch (error) {
    ElMessage.error('删除权限失败')
    console.error('删除权限失败:', error)
  }
}

// 页面加载时获取权限列表
onMounted(() => {
  loadPermissions()
})
</script>

<style scoped>
.permission-management {
  padding: 20px;
}

.operation-bar {
  margin-bottom: 20px;
}
</style>
