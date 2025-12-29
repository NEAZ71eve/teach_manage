<template>
  <div class="knowledge-point-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>知识点管理</span>
          <div class="header-actions">
            <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px; margin-right: 10px;">
              <el-option label="全部课程" value="0" />
              <el-option v-for="course in courses" :key="course.courseId" :label="course.courseName" :value="course.courseId" />
            </el-select>
            <el-button
              type="danger"
              @click="handleBatchDelete"
              :disabled="selectedPointIds.length === 0"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>
            <el-button type="primary" @click="handleAddPoint">
              <el-icon><Plus /></el-icon>
              添加知识点
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="knowledgePoints"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="pointId" label="知识点ID" width="120" />
        <el-table-column prop="pointName" label="知识点名称" width="180" />
        <el-table-column prop="courseId" label="课程ID" width="100" />
        <el-table-column prop="parentId" label="父知识点ID" width="120">
          <template #default="scope">
            <span v-if="scope.row.parentId">{{ scope.row.parentId }}</span>
            <span v-else>无</span>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" formatter="formatDate" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditPoint(scope.row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDeletePoint(scope.row.pointId)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
            <el-button type="success" size="small" @click="handleAddChildPoint(scope.row)">
              <el-icon><Plus /></el-icon>
              添加子知识点
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

    <!-- 知识点表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="pointForm" label-width="120px">
        <el-form-item label="知识点名称" required>
          <el-input v-model="pointForm.pointName" placeholder="请输入知识点名称" />
        </el-form-item>
        <el-form-item label="所属课程" required>
          <el-select v-model="pointForm.courseId" placeholder="请选择课程">
            <el-option v-for="course in courses" :key="course.courseId" :label="course.courseName" :value="course.courseId" />
          </el-select>
        </el-form-item>
        <el-form-item label="父知识点">
          <el-select v-model="pointForm.parentId" placeholder="请选择父知识点">
            <el-option label="无父知识点" value="0" />
            <el-option v-for="point in allKnowledgePoints" :key="point.pointId" :label="point.pointName" :value="point.pointId" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="pointForm.difficulty" placeholder="请选择难度">
            <el-option label="易" value="易" />
            <el-option label="中" value="中" />
            <el-option label="难" value="难" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="pointForm.description" type="textarea" placeholder="请输入知识点描述" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePoint">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAllCourses } from '../api/course'
import { 
  getKnowledgePoints,
  getAllKnowledgePoints, 
  addKnowledgePoint, 
  updateKnowledgePoint, 
  deleteKnowledgePoint,
  deleteKnowledgePointBatch
} from '../api/knowledgePoint'

// 表格数据
const knowledgePoints = ref([])
const allKnowledgePoints = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const selectedPointIds = ref([])
const selectedCourse = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加知识点')
const pointForm = reactive({
  pointId: null,
  pointName: '',
  courseId: null,
  parentId: 0,
  difficulty: '中',
  description: ''
})

// 课程数据
const courses = ref([])

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await getAllCourses()
    courses.value = response
    if (response.length > 0) {
      pointForm.courseId = response[0].courseId
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
    console.error('获取课程列表失败:', error)
  }
}

// 获取知识点列表
const fetchKnowledgePoints = async () => {
  try {
    // 获取所有知识点用于下拉选择
    const allPoints = await getAllKnowledgePoints()
    allKnowledgePoints.value = allPoints
    
    // 过滤课程
    let filteredPoints = allPoints
    if (selectedCourse.value && selectedCourse.value !== '0') {
      filteredPoints = allPoints.filter(point => point.courseId === selectedCourse.value)
    }
    
    // 手动实现分页
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    knowledgePoints.value = filteredPoints.slice(startIndex, endIndex)
    total.value = filteredPoints.length
  } catch (error) {
    ElMessage.error('获取知识点列表失败')
    console.error('获取知识点列表失败:', error)
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  fetchKnowledgePoints()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  fetchKnowledgePoints()
}

// 选择处理
const handleSelectionChange = (selection) => {
  selectedPointIds.value = selection.map((item) => item.pointId)
}

// 格式化日期
const formatDate = (row, column, cellValue, index) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString()
}

// 添加知识点
const handleAddPoint = () => {
  dialogTitle.value = '添加知识点'
  resetPointForm()
  dialogVisible.value = true
}

// 添加子知识点
const handleAddChildPoint = (parentPoint) => {
  dialogTitle.value = '添加子知识点'
  resetPointForm()
  pointForm.parentId = parentPoint.pointId
  pointForm.courseId = parentPoint.courseId
  dialogVisible.value = true
}

// 编辑知识点
const handleEditPoint = (point) => {
  dialogTitle.value = '编辑知识点'
  Object.assign(pointForm, point)
  dialogVisible.value = true
}

// 删除知识点
const handleDeletePoint = async (pointId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该知识点及其所有子知识点吗？',
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteKnowledgePoint(pointId)
    ElMessage.success('知识点删除成功')
    fetchKnowledgePoints()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('知识点删除失败')
      console.error('删除知识点失败:', error)
    }
  }
}

// 批量删除知识点
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedPointIds.value.length} 个知识点吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await deleteKnowledgePointBatch(selectedPointIds.value)
    ElMessage.success('批量删除成功')
    selectedPointIds.value = []
    fetchKnowledgePoints()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
      console.error('批量删除知识点失败:', error)
    }
  }
}

// 保存知识点
const handleSavePoint = async () => {
  try {
    if (pointForm.parentId === 0) {
      pointForm.parentId = null
    }
    
    if (pointForm.pointId) {
      // 更新知识点
      await updateKnowledgePoint(pointForm.pointId, pointForm)
      ElMessage.success('知识点更新成功')
    } else {
      // 添加知识点
      await addKnowledgePoint(pointForm)
      ElMessage.success('知识点添加成功')
    }
    dialogVisible.value = false
    fetchKnowledgePoints()
  } catch (error) {
    ElMessage.error(pointForm.pointId ? '知识点更新失败' : '知识点添加失败')
    console.error('保存知识点失败:', error)
  }
}

// 重置知识点表单
const resetPointForm = () => {
  Object.assign(pointForm, {
    pointId: null,
    pointName: '',
    courseId: courses.value.length > 0 ? courses.value[0].courseId : null,
    parentId: 0,
    difficulty: '中',
    description: ''
  })
}

// 课程选择变化
const handleCourseChange = () => {
  fetchKnowledgePoints()
}

// 页面挂载时获取数据
onMounted(async () => {
  await fetchCourses()
  await fetchKnowledgePoints()
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
  gap: 10px;
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
