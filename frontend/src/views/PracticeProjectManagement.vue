<template>
  <div class="practice-project-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <!-- 仅改标题：标题 + 副标题（其余不动） -->
          <div class="title-wrap">
            <div class="page-title">实践项目管理</div>
            <div class="page-subtitle">维护实践课程设计与实习项目，支持按学期管理</div>
          </div>

          <el-button type="primary" @click="openDialog">
            <el-icon><Plus /></el-icon> 添加项目
          </el-button>
        </div>
      </template>

      <el-table :data="projects" style="width: 100%">
        <el-table-column prop="id" label="项目ID" width="100" />
        <el-table-column prop="courseCode" label="课号" width="140" />
        <el-table-column prop="projectName" label="名称" min-width="200" />
        <el-table-column label="负责教师" width="160">
          <template #default="scope">
            {{ formatTeacherName(scope.row.teacherId) }}
          </template>
        </el-table-column>
        <el-table-column
            prop="remarks"
            label="备注"
            min-width="300"
            show-overflow-tooltip
        />
        <el-table-column prop="semester" label="学期" width="80" />
        <el-table-column prop="weeks" label="周数" width="80" />
        <el-table-column prop="credit" label="学分" width="80" />
        <el-table-column
            prop="createTime"
            label="创建时间"
            width="200"
            :formatter="formatDateTime"
        />
        <el-table-column
            prop="updateTime"
            label="更新时间"
            width="200"
            :formatter="formatDateTime"
        />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">
              编辑
            </el-button>
            <el-button
                type="danger"
                size="small"
                @click="handleDelete(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 项目表单对话框 -->
      <el-dialog v-model="dialogVisible" title="项目表单" width="600px">
        <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="课号" prop="courseCode">
            <el-input v-model="form.courseCode" placeholder="请输入课号" />
          </el-form-item>
          <el-form-item label="名称" prop="projectName">
            <el-input v-model="form.projectName" placeholder="请输入名称" />
          </el-form-item>
          <el-form-item label="学期" prop="semester">
            <el-select v-model="form.semester" placeholder="选择学期">
              <el-option v-for="n in 8" :key="n" :label="`第${n}学期`" :value="n" />
            </el-select>
          </el-form-item>
          <el-form-item label="负责教师" prop="teacherId">
            <el-select v-model="form.teacherId" placeholder="选择普通教师">
              <el-option
                v-for="teacher in teachers"
                :key="teacher.userId"
                :label="formatTeacherOption(teacher)"
                :value="teacher.userId"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="周数" prop="weeks">
            <el-input v-model.number="form.weeks" type="number" placeholder="请输入周数" />
          </el-form-item>
          <el-form-item label="学分" prop="credit">
            <el-input v-model.number="form.credit" type="number" placeholder="请输入学分" />
          </el-form-item>
          <el-form-item label="备注" prop="remarks">
            <el-input
                v-model="form.remarks"
                type="textarea"
                rows="4"
                placeholder="请输入备注"
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
import { ref, onMounted, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getPracticeProjects,
  getPracticeProjectTeachers,
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
  id: null,
  courseCode: '',
  projectName: '',
  semester: null,
  teacherId: null,
  weeks: null,
  credit: null,
  remarks: ''
})
// 表单验证规则
const rules = ref({
  courseCode: [{ required: true, message: '请输入课号', trigger: 'blur' }],
  projectName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }],
  teacherId: [{ required: true, message: '请选择负责教师', trigger: 'change' }],
  weeks: [{ required: true, message: '请输入周数', trigger: 'blur' }],
  credit: [{ required: true, message: '请输入学分', trigger: 'blur' }]
})

const teachers = ref([])
const teacherNameMap = computed(() => {
  const map = {}
  teachers.value.forEach((teacher) => {
    map[teacher.userId] = teacher.realName || teacher.username
  })
  return map
})
const formatTeacherOption = (teacher) => {
  const name = teacher.realName || teacher.username
  return name ? `${name}（${teacher.username}）` : teacher.username
}
const formatTeacherName = (teacherId) => {
  if (!teacherId) return '未分配'
  return teacherNameMap.value[teacherId] || `教师${teacherId}`
}

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
      id: null,
      courseCode: '',
      projectName: '',
      semester: null,
      teacherId: null,
      weeks: null,
      credit: null,
      remarks: ''
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
  ElMessageBox.confirm('确定要删除这个项目吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
      .then(() => {
        deletePracticeProject(projectId)
            .then(() => {
              ElMessage.success('删除成功')
              loadProjects()
            })
            .catch(() => {
              ElMessage.error('删除失败')
            })
      })
      .catch(() => {
        // 取消删除
      })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.value.id) {
        // 更新项目
        updatePracticeProject(form.value.id, form.value)
            .then(() => {
              ElMessage.success('更新成功')
              dialogVisible.value = false
              loadProjects()
            })
            .catch(() => {
              ElMessage.error('更新失败')
            })
      } else {
        // 新增项目
        addPracticeProject(form.value)
            .then(() => {
              ElMessage.success('添加成功')
              dialogVisible.value = false
              loadProjects()
            })
            .catch(() => {
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

const loadTeachers = () => {
  const userStr = localStorage.getItem('user')
  const currentUser = userStr ? JSON.parse(userStr) : null
  const programId = currentUser?.programId || null
  getPracticeProjectTeachers(programId).then((res) => {
    teachers.value = res || []
  })
}

// 组件挂载时加载数据
onMounted(() => {
  loadProjects()
  loadTeachers()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 新增：仅标题风格（不影响其它区域） */
.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.page-title {
  font-size: 16px;
  font-weight: 900;
  color: rgba(0, 0, 0, 0.86);
  line-height: 1.2;
}

.page-subtitle {
  font-size: 12px;
  color: #909399;
  line-height: 1.2;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
