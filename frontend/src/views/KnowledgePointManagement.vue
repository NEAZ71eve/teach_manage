<template>
  <div class="knowledge-point-page">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="title-wrap">
            <div class="page-title">知识点管理</div>
            <div class="page-subtitle">
              支持按课程筛选、树形维护知识点层级、批量勾选删除
            </div>
          </div>

          <div class="header-actions">
            <el-space :size="10" wrap>
              <el-select
                v-model="selectedCourse"
                placeholder="选择课程"
                style="width: 220px"
                @change="handleCourseChange"
              >
                <el-option label="全部课程" :value="0" />
                <el-option
                  v-for="course in courses"
                  :key="course.courseId"
                  :label="course.courseName"
                  :value="course.courseId"
                />
              </el-select>

              <el-button
                type="danger"
                :disabled="selectedPointIds.length === 0"
                @click="handleBatchDelete"
              >
                <el-icon><Delete /></el-icon>
                批量删除
                <span v-if="selectedPointIds.length" class="btn-badge">
                  {{ selectedPointIds.length }}
                </span>
              </el-button>

              <el-button type="primary" @click="handleAddPoint">
                <el-icon><Plus /></el-icon>
                添加知识点
              </el-button>
            </el-space>
          </div>
        </div>
      </template>

      <div class="tree-shell">
        <div class="tree-toolbar">
          <div class="toolbar-left">
            <span class="toolbar-hint">
              勾选节点可批量删除；悬浮节点右侧显示操作按钮
            </span>
          </div>
          <div class="toolbar-right">
            <el-button size="small" plain @click="expandAll">
              展开全部
            </el-button>
            <el-button size="small" plain @click="collapseAll">
              收起全部
            </el-button>
            <el-button
              size="small"
              plain
              @click="clearChecked"
              :disabled="selectedPointIds.length === 0"
            >
              清空勾选
            </el-button>
          </div>
        </div>

        <div class="tree-container">
          <el-tree
            ref="treeRef"
            :data="knowledgePointTree"
            :props="treeProps"
            show-checkbox
            node-key="pointId"
            highlight-current
            :default-expanded-keys="[]"
            @check-change="handleCheckChange"
            @node-click="handleNodeClick"
            class="kp-tree"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <div class="node-main">
                  <div class="node-title" :title="node.label">
                    {{ node.label }}
                  </div>

                  <div class="node-meta">
                    <el-tag
                      v-if="courseNameMap[data.courseId]"
                      size="small"
                      type="info"
                      class="meta-tag"
                      effect="plain"
                    >
                      {{ courseNameMap[data.courseId] }}
                    </el-tag>

                    <el-tag
                      v-if="data.difficulty"
                      size="small"
                      :type="difficultyTagType(data.difficulty)"
                      class="meta-tag"
                      effect="plain"
                    >
                      难度：{{ data.difficulty }}
                    </el-tag>

                    <span
                      v-if="data.description"
                      class="meta-desc"
                      :title="data.description"
                    >
                      {{ data.description }}
                    </span>
                  </div>
                </div>

                <div class="node-actions" @click.stop>
                  <el-tooltip content="编辑" placement="top">
                    <el-button
                      circle
                      size="small"
                      type="primary"
                      @click.stop="handleEditPoint(data)"
                    >
                      <el-icon><Edit /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip content="删除" placement="top">
                    <el-button
                      circle
                      size="small"
                      type="danger"
                      @click.stop="handleDeletePoint(data.pointId)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tooltip>

                  <el-tooltip content="添加子知识点" placement="top">
                    <el-button
                      circle
                      size="small"
                      type="success"
                      @click.stop="handleAddChildPoint(data)"
                    >
                      <el-icon><Plus /></el-icon>
                    </el-button>
                  </el-tooltip>
                </div>
              </div>
            </template>
          </el-tree>

          <el-empty
            v-if="knowledgePointTree.length === 0"
            description="暂无知识点数据"
          />
        </div>
      </div>
    </el-card>

    <!-- 知识点表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      class="kp-dialog"
      destroy-on-close
    >
      <el-form :model="pointForm" label-width="120px" class="kp-form">
        <el-form-item label="知识点名称" required>
          <el-input
            v-model="pointForm.pointName"
            placeholder="请输入知识点名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="所属课程" required>
          <el-select
            v-model="pointForm.courseId"
            placeholder="请选择课程"
            style="width: 100%"
          >
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="父知识点">
          <el-select
            v-model="pointForm.parentId"
            placeholder="请选择父知识点"
            style="width: 100%"
            clearable
          >
            <el-option label="无父知识点" :value="0" />
            <el-option
              v-for="point in getFilteredKnowledgePoints()"
              :key="point.pointId"
              :label="point.pointName"
              :value="point.pointId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="难度">
          <el-select
            v-model="pointForm.difficulty"
            placeholder="请选择难度"
            style="width: 100%"
          >
            <el-option label="易" value="易" />
            <el-option label="中" value="中" />
            <el-option label="难" value="难" />
          </el-select>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
            v-model="pointForm.description"
            type="textarea"
            placeholder="请输入知识点描述（可选）"
            :rows="3"
            maxlength="200"
            show-word-limit
          />
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
import { ref, onMounted, reactive, computed } from "vue";
import { Plus, Edit, Delete } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAllCourses } from "../api/course";
import {
  getAllKnowledgePoints,
  getKnowledgePointsByCourseId,
  addKnowledgePoint,
  updateKnowledgePoint,
  deleteKnowledgePoint,
  deleteKnowledgePointBatch,
} from "../api/knowledgePoint";

// Tree组件实例
const treeRef = ref(null);

// 树形结构数据
const knowledgePointTree = ref([]);
const allKnowledgePoints = ref([]);
const selectedPointIds = ref([]);
const selectedCourse = ref(0);

// 树形结构配置
const treeProps = {
  label: "pointName",
  children: "children",
};

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("添加知识点");
const pointForm = reactive({
  pointId: null,
  pointName: "",
  courseId: null,
  parentId: 0,
  difficulty: "中",
  description: "",
});

// 课程数据
const courses = ref([]);

// 课程 id -> name 映射（用于树节点 Tag 展示）
const courseNameMap = computed(() => {
  const map = {};
  courses.value.forEach((c) => {
    map[c.courseId] = c.courseName;
  });
  return map;
});

const difficultyTagType = (difficulty) => {
  if (difficulty === "易") return "success";
  if (difficulty === "中") return "warning";
  if (difficulty === "难") return "danger";
  return "info";
};

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await getAllCourses();
    courses.value = response;
    if (response.length > 0) {
      pointForm.courseId = response[0].courseId;
    }
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 获取知识点树结构
const fetchKnowledgePointTree = async () => {
  try {
    let response;
    let allPoints;

    if (selectedCourse.value === 0) {
      response = await getAllKnowledgePoints();
      allPoints = response;
    } else {
      response = await getKnowledgePointsByCourseId(selectedCourse.value);
      allPoints = await getAllKnowledgePoints();
    }

    // 字段名转换：kpId/kpName -> pointId/pointName
    const convertPoints = (points) => {
      return (points || []).map((point) => ({
        pointId: point.kpId,
        pointName: point.kpName,
        courseId: point.courseId,
        parentId: point.parentId,
        description: point.description,
        difficulty: point.difficulty,
        kpOrder: point.kpOrder,
        isActive: point.isActive,
        createTime: point.createTime,
        updateTime: point.updateTime,
      }));
    };

    knowledgePointTree.value = buildTree(convertPoints(response));
    allKnowledgePoints.value = convertPoints(allPoints);

    // 课程切换后，清理已勾选
    selectedPointIds.value = [];
    if (treeRef.value) treeRef.value.setCheckedKeys([]);
  } catch (error) {
    ElMessage.error("获取知识点列表失败");
    console.error("获取知识点列表失败:", error);
  }
};

// 筛选指定课程的所有知识点（用于父知识点选择）
const getFilteredKnowledgePoints = () => {
  if (selectedCourse.value === 0) return allKnowledgePoints.value;
  return allKnowledgePoints.value.filter(
    (point) => point.courseId === selectedCourse.value
  );
};

// 将扁平数据转换为树形结构
const buildTree = (list) => {
  const map = new Map();
  const roots = [];

  list.forEach((item) => {
    item.children = [];
    map.set(item.pointId, item);
  });

  list.forEach((item) => {
    const parentId = item.parentId;
    if (parentId === null) {
      roots.push(item);
    } else {
      const parent = map.get(parentId);
      if (parent) parent.children.push(item);
    }
  });

  return roots;
};

// 复选框变化处理
const handleCheckChange = () => {
  if (treeRef.value) {
    selectedPointIds.value = treeRef.value.getCheckedKeys();
  }
};

// 节点点击事件
const handleNodeClick = () => {
  // 保留：你如果需要点击展示详情/右侧抽屉，可在这里扩展
};

// 添加知识点
const handleAddPoint = () => {
  dialogTitle.value = "添加知识点";
  resetPointForm();
  if (selectedCourse.value !== 0) {
    pointForm.courseId = selectedCourse.value;
  }
  dialogVisible.value = true;
};

// 添加子知识点
const handleAddChildPoint = (parentPoint) => {
  dialogTitle.value = "添加子知识点";
  resetPointForm();
  pointForm.parentId = parentPoint.pointId;
  pointForm.courseId = parentPoint.courseId;
  dialogVisible.value = true;
};

// 编辑知识点
const handleEditPoint = (point) => {
  dialogTitle.value = "编辑知识点";
  Object.assign(pointForm, point);
  if (pointForm.parentId === null) pointForm.parentId = 0;
  dialogVisible.value = true;
};

// 删除知识点
const handleDeletePoint = async (pointId) => {
  try {
    await ElMessageBox.confirm(
      "确定要删除该知识点及其所有子知识点吗？",
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteKnowledgePoint(pointId);
    ElMessage.success("知识点删除成功");
    fetchKnowledgePointTree();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("知识点删除失败");
      console.error("删除知识点失败:", error);
    }
  }
};

// 批量删除知识点
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedPointIds.value.length} 个知识点吗？`,
      "删除确认",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }
    );

    await deleteKnowledgePointBatch(selectedPointIds.value);
    ElMessage.success("批量删除成功");
    selectedPointIds.value = [];
    fetchKnowledgePointTree();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
      console.error("批量删除知识点失败:", error);
    }
  }
};

// 保存知识点
const handleSavePoint = async () => {
  try {
    const backendPoint = {
      kpId: pointForm.pointId,
      kpName: pointForm.pointName,
      courseId: pointForm.courseId,
      parentId: pointForm.parentId === 0 ? null : pointForm.parentId,
      description: pointForm.description,
      difficulty: pointForm.difficulty,
      kpOrder: 0,
      isActive: 1,
    };

    if (backendPoint.kpId) {
      await updateKnowledgePoint(backendPoint.kpId, backendPoint);
      ElMessage.success("知识点更新成功");
    } else {
      await addKnowledgePoint(backendPoint);
      ElMessage.success("知识点添加成功");
    }

    dialogVisible.value = false;
    fetchKnowledgePointTree();
  } catch (error) {
    ElMessage.error(pointForm.pointId ? "知识点更新失败" : "知识点添加失败");
    console.error("保存知识点失败:", error);
  }
};

// 重置知识点表单
const resetPointForm = () => {
  Object.assign(pointForm, {
    pointId: null,
    pointName: "",
    courseId: courses.value.length > 0 ? courses.value[0].courseId : null,
    parentId: 0,
    difficulty: "中",
    description: "",
  });
};

// 课程选择变化
const handleCourseChange = () => {
  fetchKnowledgePointTree();
};

// ====== Tree 辅助操作（美化体验，不影响业务） ======
const expandAll = () => {
  if (!treeRef.value) return;
  // element-plus tree 没有官方 expandAll API，这里通过遍历节点展开
  const nodesMap = treeRef.value.store?.nodesMap || {};
  Object.keys(nodesMap).forEach((k) => {
    nodesMap[k].expanded = true;
  });
};

const collapseAll = () => {
  if (!treeRef.value) return;
  const nodesMap = treeRef.value.store?.nodesMap || {};
  Object.keys(nodesMap).forEach((k) => {
    nodesMap[k].expanded = false;
  });
};

const clearChecked = () => {
  if (!treeRef.value) return;
  treeRef.value.setCheckedKeys([]);
  selectedPointIds.value = [];
};

// 页面挂载时获取数据
onMounted(async () => {
  await fetchCourses();
  await fetchKnowledgePointTree();
});
</script>

<style scoped>
/* ===== 页面顶层卡片（与统计页同风格：玻璃 + 圆角阴影） ===== */
.knowledge-point-page {
  width: 100%;
}

.page-card {
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.76);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.08);
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.title-wrap {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.page-title {
  font-size: 16px;
  font-weight: 900;
  color: rgba(0, 0, 0, 0.86);
}

.page-subtitle {
  font-size: 12px;
  color: #909399;
}

.header-actions {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

/* 按钮右侧小徽标 */
.btn-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 18px;
  min-width: 18px;
  padding: 0 6px;
  border-radius: 999px;
  margin-left: 6px;
  font-size: 12px;
  line-height: 18px;
  color: #fff;
  background: rgba(0, 0, 0, 0.25);
}

/* ===== Tree 外壳 ===== */
.tree-shell {
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  background: rgba(255, 255, 255, 0.65);
  backdrop-filter: blur(10px);
  padding: 12px;
}

.tree-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 6px 6px 10px;
}

.toolbar-hint {
  font-size: 12px;
  color: #909399;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.tree-container {
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  padding: 10px;
  max-height: 620px;
  overflow: auto;
}

/* ===== Tree 节点布局 ===== */
.tree-node {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 6px 8px;
  border-radius: 12px;
  transition: background 0.18s ease;
}

.tree-node:hover {
  background: rgba(0, 0, 0, 0.03);
}

.node-main {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0; /* 允许省略号 */
}

.node-title {
  font-weight: 800;
  color: rgba(0, 0, 0, 0.82);
  max-width: 560px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.node-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  flex-wrap: wrap;
}

.meta-tag {
  border-radius: 999px;
}

.meta-desc {
  font-size: 12px;
  color: #909399;
  max-width: 520px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧操作按钮：默认隐藏，hover 出现，不占空间 */
.node-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  opacity: 0;
  transform: translateX(4px);
  transition: opacity 0.18s ease, transform 0.18s ease;
  flex: 0 0 auto;
}

.tree-node:hover .node-actions {
  opacity: 1;
  transform: translateX(0);
}

/* Tree 默认节点内容高度更舒服 */
:deep(.el-tree-node__content) {
  height: auto;
  padding: 2px 0;
}

/* Tree checkbox 对齐更好看 */
:deep(.el-tree-node__content .el-checkbox) {
  margin-right: 6px;
}

/* 高亮当前节点更柔和 */
:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: rgba(64, 158, 255, 0.1);
  border-radius: 12px;
}

/* dialog 内表单 */
.kp-form :deep(.el-input__wrapper),
.kp-form :deep(.el-textarea__inner) {
  border-radius: 10px;
}

/* 响应式 */
@media (max-width: 860px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  .header-actions {
    justify-content: flex-start;
  }
  .node-title,
  .meta-desc {
    max-width: 260px;
  }
}
</style>
