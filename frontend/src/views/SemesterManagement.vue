<template>
  <div class="semester-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>学期管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="handleAddSemester">
              <el-icon><Plus /></el-icon>
              添加学期
            </el-button>
          </div>
        </div>
      </template>
      
      <el-table 
        :data="semesters" 
        border 
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="semesterId" label="学期ID" width="80" />
        <el-table-column prop="semesterName" label="学期名称" width="200" />
        <el-table-column prop="startDate" label="开始日期" width="150" />
        <el-table-column prop="endDate" label="结束日期" width="150" />
        <el-table-column prop="isCurrent" label="是否当前学期" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.isCurrent ? 'success' : 'info'">
              {{ scope.row.isCurrent ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditSemester(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteSemester(scope.row.semesterId)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination mt-4">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 学期表单对话框 -->
    <el-dialog v-model="dialogVisible" title="学期信息" width="600px">
      <el-form :model="semesterForm" label-width="120px">
        <el-form-item label="学期名称" required>
          <el-input v-model="semesterForm.semesterName" placeholder="请输入学期名称，如：2024-2025学年第一学期" />
        </el-form-item>
        <el-form-item label="开始日期" required>
          <el-date-picker
            v-model="semesterForm.startDate"
            type="date"
            placeholder="选择开始日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期" required>
          <el-date-picker
            v-model="semesterForm.endDate"
            type="date"
            placeholder="选择结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否当前学期">
          <el-switch v-model="semesterForm.isCurrent" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveSemester">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSemesters, addSemester, updateSemester, deleteSemester, deleteBatchSemesters } from '../api/semester'

// 表格数据
const semesters = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedSemesterIds = ref([])

// 对话框
const dialogVisible = ref(false)
const semesterForm = reactive({
  semesterId: null,
  semesterName: '',
  startDate: '',
  endDate: '',
  isCurrent: false
})

// 获取学期列表
const fetchSemesters = async () => {
  try {
    const response = await getSemesters(currentPage.value, pageSize.value)
    semesters.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('获取学期列表失败')
    console.error('获取学期列表失败:', error)
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedSemesterIds.value = selection.map(item => item.semesterId)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchSemesters()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  fetchSemesters()
}

// 添加学期
const handleAddSemester = () => {
  Object.assign(semesterForm, {
    semesterId: null,
    semesterName: '',
    startDate: '',
    endDate: '',
    isCurrent: false
  })
  dialogVisible.value = true
}

// 编辑学期
const handleEditSemester = (row) => {
  Object.assign(semesterForm, row)
  dialogVisible.value = true
}

// 保存学期
const handleSaveSemester = async () => {
  try {
    if (semesterForm.semesterId) {
      // 更新学期
      await updateSemester(semesterForm.semesterId, semesterForm)
      ElMessage.success('学期更新成功')
    } else {
      // 添加学期
      await addSemester(semesterForm)
      ElMessage.success('学期添加成功')
    }
    dialogVisible.value = false
    fetchSemesters()
  } catch (error) {
    ElMessage.error(semesterForm.semesterId ? '学期更新失败' : '学期添加失败')
    console.error('保存学期失败:', error)
  }
}

// 删除学期
const handleDeleteSemester = async (semesterId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该学期记录吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteSemester(semesterId)
    ElMessage.success('学期删除成功')
    fetchSemesters()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('学期删除失败')
      console.error('删除学期失败:', error)
    }
  }
}

// 批量删除学期
const handleBatchDelete = async () => {
  if (selectedSemesterIds.value.length === 0) {
    ElMessage.warning('请选择要删除的学期')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedSemesterIds.value.length} 个学期吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteBatchSemesters(selectedSemesterIds.value)
    ElMessage.success('学期删除成功')
    fetchSemesters()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('学期删除失败')
      console.error('批量删除学期失败:', error)
    }
  }
}

// 页面挂载时获取学期列表
onMounted(async () => {
  await fetchSemesters()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}
</style>