<template>
  <div class="knowledge-point-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>知识点管理</span>
          <div class="header-actions">
            <el-select v-model="selectedCourse" placeholder="选择课程" style="width: 200px; margin-right: 10px;" @change="handleCourseChange">
              <el-option label="全部课程" :value="0" />
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

      <div class="tree-container">
        <el-tree
          ref="treeRef"
          :data="knowledgePointTree"
          :props="treeProps"
          show-checkbox
          node-key="pointId"
          @check-change="handleCheckChange"
          @node-click="handleNodeClick"
          style="width: 100%"
        >
          <template #default="{ node, data }">
            <span class="tree-node-content">
              <span>{{ node.label }}</span>
              <span class="node-actions">
                <el-button type="primary" size="small" @click.stop="handleEditPoint(data)">
                  <el-icon><Edit /></el-icon>
                  编辑
                </el-button>
                <el-button type="danger" size="small" @click.stop="handleDeletePoint(data.pointId)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-button>
                <el-button type="success" size="small" @click.stop="handleAddChildPoint(data)">
                  <el-icon><Plus /></el-icon>
                  添加子知识点
                </el-button>
              </span>
            </span>
          </template>
        </el-tree>
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
            <el-option v-for="point in getFilteredKnowledgePoints()" :key="point.pointId" :label="point.pointName" :value="point.pointId" />
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
  getAllKnowledgePoints, 
  getKnowledgePointsByCourseId,
  addKnowledgePoint, 
  updateKnowledgePoint, 
  deleteKnowledgePoint,
  deleteKnowledgePointBatch
} from '../api/knowledgePoint'

// Tree组件实例
const treeRef = ref(null)

// 树形结构数据
const knowledgePointTree = ref([])
const allKnowledgePoints = ref([])
const selectedPointIds = ref([])
const selectedCourse = ref(0)

// 树形结构配置
const treeProps = {
  label: 'pointName',
  children: 'children'
}

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

// 获取知识点树结构
const fetchKnowledgePointTree = async () => {
  try {
    let response
    let allPoints
    if (selectedCourse.value === 0) {
      // 获取所有课程的知识点树
      response = await getAllKnowledgePoints()
      allPoints = response
    } else {
      // 获取指定课程的知识点列表（扁平结构）
      response = await getKnowledgePointsByCourseId(selectedCourse.value)
      allPoints = await getAllKnowledgePoints()
    }
    
    // 字段名转换：将后端的kpId和kpName转换为前端需要的pointId和pointName
    const convertPoints = (points) => {
      return points.map(point => ({
        pointId: point.kpId,
        pointName: point.kpName,
        courseId: point.courseId,
        parentId: point.parentId,
        description: point.description,
        difficulty: point.difficulty,
        kpOrder: point.kpOrder,
        isActive: point.isActive,
        createTime: point.createTime,
        updateTime: point.updateTime
      }))
    }
    
    // 转换并构建树形结构
    knowledgePointTree.value = buildTree(convertPoints(response))
    
    // 获取所有知识点用于下拉选择（始终获取，确保数据最新）
    allKnowledgePoints.value = convertPoints(allPoints)
  } catch (error) {
    ElMessage.error('获取知识点列表失败')
    console.error('获取知识点列表失败:', error)
  }
}

// 筛选指定课程的所有知识点（用于父知识点选择）
const getFilteredKnowledgePoints = () => {
  if (selectedCourse.value === 0) {
    return allKnowledgePoints.value
  } else {
    return allKnowledgePoints.value.filter(point => point.courseId === selectedCourse.value)
  }
}

// 将扁平数据转换为树形结构
const buildTree = (list) => {
  const map = new Map()
  const roots = []
  
  // 构建节点映射
  list.forEach(item => {
    item.children = []
    map.set(item.pointId, item)
  })
  
  // 构建树形结构
  list.forEach(item => {
    const parentId = item.parentId
    if (parentId === null) {
      // 根节点
      roots.push(item)
    } else {
      // 子节点，添加到父节点的children中
      const parent = map.get(parentId)
      if (parent) {
        parent.children.push(item)
      }
    }
  })
  
  return roots
}

// 复选框变化处理
const handleCheckChange = () => {
  // 使用Tree组件的getCheckedKeys方法获取所有选中的节点ID
  if (treeRef.value) {
    selectedPointIds.value = treeRef.value.getCheckedKeys()
  }
}

// 节点点击事件
const handleNodeClick = (data, node) => {
  // 节点点击事件，可以用于展开/折叠节点
}

// 添加知识点
const handleAddPoint = () => {
  dialogTitle.value = '添加知识点'
  resetPointForm()
  // 如果选择了特定课程，自动分配该课程
  if (selectedCourse.value !== 0) {
    pointForm.courseId = selectedCourse.value
  }
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
  // 将null转换为0，以便在下拉框中显示
  if (pointForm.parentId === null) {
    pointForm.parentId = 0
  }
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
    fetchKnowledgePointTree()
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
    fetchKnowledgePointTree()
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
    // 字段名转换：将前端的pointId和pointName转换为后端需要的kpId和kpName
    const backendPoint = {
      kpId: pointForm.pointId,
      kpName: pointForm.pointName,
      courseId: pointForm.courseId,
      parentId: pointForm.parentId === 0 ? null : pointForm.parentId,
      description: pointForm.description,
      difficulty: pointForm.difficulty,
      kpOrder: 0, // 默认排序
      isActive: 1 // 默认激活
    }
    
    if (backendPoint.kpId) {
      // 更新知识点
      await updateKnowledgePoint(backendPoint.kpId, backendPoint)
      ElMessage.success('知识点更新成功')
    } else {
      // 添加知识点
      await addKnowledgePoint(backendPoint)
      ElMessage.success('知识点添加成功')
    }
    dialogVisible.value = false
    fetchKnowledgePointTree()
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
  fetchKnowledgePointTree()
}

// 页面挂载时获取数据
onMounted(async () => {
  await fetchCourses()
  await fetchKnowledgePointTree()
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

.tree-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
}

.tree-node-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  height: 32px;
  line-height: 32px;
}

.node-actions {
  display: flex;
  gap: 5px;
  opacity: 0;
  transition: opacity 0.3s;
}

.tree-node-content:hover .node-actions {
  opacity: 1;
}

:deep(.el-tree-node__content) {
  height: auto;
  padding: 4px 0;
}

:deep(.el-tree-node) {
  margin: 4px 0;
}
</style>
