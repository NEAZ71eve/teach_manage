<template>
  <div class="practice-project-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>实践项目管理</span>
          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 添加项目
          </el-button>
        </div>
      </template>

      <el-table :data="projects" style="width: 100%">
        <el-table-column prop="projectId" label="项目ID" width="100" />
        <el-table-column prop="title" label="项目标题" min-width="200" />
        <el-table-column prop="description" label="项目描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="publisher" label="发布者" width="150" />
        <el-table-column prop="createTime" label="创建时间" width="200" :formatter="formatDateTime" />
        <el-table-column prop="deadline" label="截止时间" width="200" :formatter="formatDateTime" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.projectId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 项目表单对话框 -->
      <el-dialog
        v-model="dialogVisible"
        title="项目表单"
        width="600px"
      >
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="项目标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入项目标题" />
          </el-form-item>
          <el-form-item label="项目描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              rows="4"
              placeholder="请输入项目描述"
            />
          </el-form-item>
          <el-form-item label="发布者" prop="publisher">
            <el-input v-model="form.publisher" placeholder="请输入发布者" />
          </el-form-item>
          <el-form-item label="截止时间" prop="deadline">
            <el-date-picker
              v-model="form.deadline"
              type="datetime"
              placeholder="选择截止时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">提交</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  getPracticeProjects,
  addPracticeProject,
  updatePracticeProject,
  deletePracticeProject
} from '@/api/practiceProject'

// 项目列表
const projects = ref([])
// 对话框可见性
const dialogVisible = ref(false)
// 表单引用
const formRef = ref(null)
// 表单数据
const form = ref({
  projectId: null,
  title: '',
  description: '',
  publisher: '',
  deadline: null
})
// 表单验证规则
const rules = ref({
  title: [{ required: true, message: '请输入项目标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入项目描述', trigger: 'blur' }],
  publisher: [{ required: true, message: '请输入发布者', trigger: 'blur' }],
  deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }]
})

// 格式化日期时间
const formatDateTime = (row, column, cellValue) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString()
}

// 打开对话框
const openDialog = (project = null) => {
  if (project) {
    // 编辑模式
    form.value = { ...project }
  } else {
    // 新增模式
    form.value = {
      projectId: null,
      title: '',
      description: '',
      publisher: '',
      deadline: null
    }
  }
  dialogVisible.value = true
}

// 编辑项目
const handleEdit = (project) => {
  openDialog(project)
}

// 删除项目
const handleDelete = (projectId) => {
  ElMessage.confirm('确定要删除这个项目吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deletePracticeProject(projectId).then(() => {
      ElMessage.success('删除成功')
      loadProjects()
    }).catch(() => {
      ElMessage.error('删除失败')
    })
  }).catch(() => {
    // 取消删除
  })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.value.projectId) {
        // 更新项目
        updatePracticeProject(form.value.projectId, form.value).then(() => {
          ElMessage.success('更新成功')
          dialogVisible.value = false
          loadProjects()
        }).catch(() => {
          ElMessage.error('更新失败')
        })
      } else {
        // 新增项目
        addPracticeProject(form.value).then(() => {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          loadProjects()
        }).catch(() => {
          ElMessage.error('添加失败')
        })
      }
    }
  })
}

// 加载项目列表
const loadProjects = () => {
  getPracticeProjects().then((res) => {
    projects.value = res
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>