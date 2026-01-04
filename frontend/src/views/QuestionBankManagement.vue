<template>
  <div class="question-bank-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <!-- 仅改标题：标题 + 副标题（其余不动） -->
          <div class="title-wrap">
            <div class="page-title">题库管理</div>
            <div class="page-subtitle">按题型/难度/分类/知识点筛选，支持批量操作与题目维护</div>
          </div>

          <div class="header-actions">
            <el-select
                v-model="searchParams.questionType"
                placeholder="选择题型"
                style="width: 120px; margin-right: 10px"
            >
              <el-option label="全部题型" value="" />
              <el-option label="单选题" :value="1" />
              <el-option label="多选题" :value="2" />
              <el-option label="判断题" :value="3" />
              <el-option label="填空题" :value="4" />
              <el-option label="简答题" :value="5" />
            </el-select>

            <el-select
                v-model="searchParams.difficulty"
                placeholder="选择难度"
                style="width: 120px; margin-right: 10px"
            >
              <el-option label="全部难度" value="" />
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>

            <el-select
                v-model="searchParams.categoryId"
                placeholder="选择分类"
                style="width: 120px; margin-right: 10px"
            >
              <el-option label="全部分类" value="" />
              <el-option
                  v-for="category in categories"
                  :key="category.categoryId"
                  :label="category.categoryName"
                  :value="category.categoryId"
              />
            </el-select>

            <el-select
                v-model="searchParams.kpId"
                placeholder="选择知识点"
                style="width: 180px; margin-right: 10px"
                clearable
            >
              <el-option label="全部知识点" value="" />
              <el-option
                  v-for="kp in knowledgePoints"
                  :key="kp.kpId"
                  :label="kp.kpName"
                  :value="kp.kpId"
              />
            </el-select>

            <el-input
                v-model="searchParams.keyword"
                placeholder="关键词搜索"
                style="width: 200px; margin-right: 10px"
                clearable
            />

            <el-button
                type="danger"
                @click="handleBatchDelete"
                :disabled="selectedQuestionIds.length === 0"
            >
              <el-icon><Delete /></el-icon>
              批量删除
            </el-button>

            <el-button type="primary" @click="handleAddQuestion">
              <el-icon><Plus /></el-icon>
              添加题目
            </el-button>
          </div>
        </div>
      </template>

      <el-table
          :data="questions"
          border
          stripe
          @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="questionId" label="题目ID" width="80" />
        <el-table-column prop="questionType" label="题型" width="100" />
        <el-table-column prop="courseName" label="所属学科" width="150" />
        <el-table-column prop="kpName" label="知识点" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />

        <!-- 修复：点击题目内容查看（原 @click 写法无效） -->
        <el-table-column prop="questionContent" label="题目内容" min-width="300">
          <template #default="scope">
            <span
                class="clickable-content"
                @click="handleViewQuestion(scope.row)"
                :title="scope.row.questionContent"
            >
              {{ scope.row.questionContent }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="score" label="分值" width="80" />

        <!-- ✅ 已删除：状态列 -->

        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="scope">
            <el-space :size="4" wrap="nowrap">
              <el-tooltip content="查看" placement="top">
                <el-button
                    circle
                    size="small"
                    type="primary"
                    @click="handleViewQuestion(scope.row)"
                >
                  <el-icon><View /></el-icon>
                </el-button>
              </el-tooltip>

              <el-tooltip content="编辑" placement="top">
                <el-button
                    circle
                    size="small"
                    type="success"
                    @click="handleEditQuestion(scope.row)"
                >
                  <el-icon><Edit /></el-icon>
                </el-button>
              </el-tooltip>

              <el-tooltip content="删除" placement="top">
                <el-button
                    circle
                    size="small"
                    type="danger"
                    @click="handleDeleteQuestion(scope.row.questionId)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-tooltip>
            </el-space>
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

    <!-- 题目表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="questionForm" label-width="120px">
        <el-form-item label="题型" required>
          <el-select v-model="questionForm.questionType" placeholder="请选择题型">
            <el-option label="单选题" :value="1" />
            <el-option label="多选题" :value="2" />
            <el-option label="判断题" :value="3" />
            <el-option label="填空题" :value="4" />
            <el-option label="简答题" :value="5" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属课程" required>
          <el-select v-model="questionForm.courseId" placeholder="请选择课程">
            <el-option
                v-for="course in courses"
                :key="course.courseId"
                :label="course.courseName"
                :value="course.courseId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="所属知识点" required>
          <el-select v-model="questionForm.kpId" placeholder="请选择知识点">
            <el-option
                v-for="point in knowledgePoints"
                :key="point.kpId"
                :label="point.kpName"
                :value="point.kpId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="题目分类" v-if="categories.length > 0">
          <el-select
              v-model="questionForm.categoryId"
              placeholder="请选择分类"
              clearable
          >
            <el-option
                v-for="category in categories"
                :key="category.categoryId"
                :label="category.categoryName"
                :value="category.categoryId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="题目内容" required>
          <el-input
              v-model="questionForm.questionContent"
              type="textarea"
              placeholder="请输入题目内容"
              :rows="4"
          />
        </el-form-item>

        <!-- 选项（仅客观题显示） -->
        <el-form-item label="选项" v-if="[1, 2].includes(questionForm.questionType)">
          <div v-for="(option, index) in options" :key="index" class="option-item">
            <el-input v-model="option.optionText" placeholder="请输入选项内容">
              <template #prefix>
                <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
              </template>
            </el-input>

            <el-checkbox
                v-model="option.isCorrect"
                :label="index"
                style="margin-left: 10px"
            >
              正确答案
            </el-checkbox>

            <el-button
                type="danger"
                size="small"
                @click="options.splice(index, 1)"
                v-if="options.length > 2"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>

          <el-button
              type="primary"
              size="small"
              @click="addOption"
              v-if="options.length < 10"
          >
            <el-icon><Plus /></el-icon>
            添加选项
          </el-button>
        </el-form-item>

        <el-form-item label="正确答案" required>
          <el-input
              v-model="questionForm.correctAnswer"
              placeholder="请输入正确答案"
              v-if="[1, 4, 5].includes(questionForm.questionType)"
          />
          <el-radio-group
              v-model="questionForm.correctAnswer"
              v-else-if="questionForm.questionType === 3"
          >
            <el-radio label="1" />对 <el-radio label="0" />错
          </el-radio-group>
        </el-form-item>

        <el-form-item label="答案解析">
          <el-input
              v-model="questionForm.analysis"
              type="textarea"
              placeholder="请输入答案解析"
              :rows="3"
          />
        </el-form-item>

        <el-form-item label="难度" required>
          <el-select v-model="questionForm.difficulty" placeholder="请选择难度">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>

        <el-form-item label="分值" required>
          <el-input
              v-model.number="questionForm.score"
              placeholder="请输入分值"
              type="number"
              min="0.5"
              step="0.5"
          />
        </el-form-item>

        <el-form-item label="标签" v-if="tags.length > 0">
          <el-select v-model="selectedTags" multiple placeholder="请选择标签" clearable>
            <el-option
                v-for="tag in tags"
                :key="tag.tagId"
                :label="tag.tagName"
                :value="tag.tagId"
            />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveQuestion">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看题目详情对话框 -->
    <el-dialog v-model="viewDialogVisible" title="题目详情" width="800px">
      <div class="question-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="题目ID">{{ currentQuestion?.questionId }}</el-descriptions-item>
          <el-descriptions-item label="题型">{{ getQuestionTypeName(currentQuestion?.questionType) }}</el-descriptions-item>
          <el-descriptions-item label="所属学科">{{ currentQuestion?.courseName }}</el-descriptions-item>
          <el-descriptions-item label="知识点">{{ currentQuestion?.kpName }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ currentQuestion?.categoryName }}</el-descriptions-item>

          <el-descriptions-item label="题目内容">
            <div v-html="currentQuestion?.questionContent"></div>
          </el-descriptions-item>

          <el-descriptions-item label="选项" v-if="[1, 2].includes(currentQuestion?.questionType)">
            <div v-if="currentQuestion?.options">
              <div
                  v-for="(option, index) in currentQuestion.options"
                  :key="index"
                  class="option-item"
              >
                <span class="option-label">{{ String.fromCharCode(65 + index) }}.</span>
                <span>{{ option.optionText }}</span>
                <el-tag
                    v-if="option.isCorrect"
                    type="success"
                    size="small"
                    style="margin-left: 10px"
                >
                  正确
                </el-tag>
              </div>
            </div>
          </el-descriptions-item>

          <el-descriptions-item label="正确答案">
            {{ formatAnswer(currentQuestion?.correctAnswer, currentQuestion?.questionType) }}
          </el-descriptions-item>

          <el-descriptions-item label="答案解析">
            {{ currentQuestion?.analysis || "无" }}
          </el-descriptions-item>

          <el-descriptions-item label="难度">{{ currentQuestion?.difficulty }}</el-descriptions-item>
          <el-descriptions-item label="分值">{{ currentQuestion?.score }}</el-descriptions-item>

          <!-- ✅ 已删除：状态 -->

          <el-descriptions-item label="标签" v-if="currentQuestion?.tags">
            <el-tag
                v-for="tag in currentQuestion.tags"
                :key="tag.tagId"
                style="margin-right: 5px; margin-bottom: 5px"
            >
              {{ tag.tagName }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="使用情况" v-if="currentQuestion?.statistics">
            <div>使用次数: {{ currentQuestion.statistics.usageCount }}</div>
            <div>正确率: {{ currentQuestion.statistics.correctRate }}%</div>
            <div>答对次数: {{ currentQuestion.statistics.correctCount }}</div>
            <div>答错次数: {{ currentQuestion.statistics.incorrectCount }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, watch } from "vue";
import { Plus, Edit, Delete, View } from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAllCourses } from "../api/course";
import { getAllKnowledgePoints, getKnowledgePointsByCourseId } from "../api/knowledgePoint";
import {
  getQuestions,
  addQuestion,
  updateQuestion,
  deleteQuestion,
  deleteQuestionBatch,
  getCategories,
  getTags,
} from "../api/question";

// 表格数据
const questions = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedQuestionIds = ref([]);

// 搜索参数
const searchParams = reactive({
  questionType: "",
  difficulty: "",
  categoryId: "",
  kpId: "",
  keyword: "",
});

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("添加题目");
const questionForm = reactive({
  questionId: null,
  courseId: null,
  kpId: null,
  categoryId: null,
  questionType: 1,
  questionContent: "",
  correctAnswer: "",
  analysis: "",
  difficulty: "中等",
  score: 5,
  // status 字段保留：如果后端仍需要，不影响前端不显示
  status: "待审核",
  creatorId: 1, // 示例创建者ID，实际应从登录用户获取
});

// 查看题目详情对话框
const viewDialogVisible = ref(false);
const currentQuestion = ref(null);

// 课程和知识点数据
const courses = ref([]);
const knowledgePoints = ref([]);
const categories = ref([]);
const tags = ref([]);

// 选项管理
const options = ref([
  { optionText: "", isCorrect: false },
  { optionText: "", isCorrect: false },
]);
const selectedTags = ref([]);

// 获取课程列表
const fetchCourses = async () => {
  try {
    const response = await getAllCourses();
    courses.value = response || [];
    if (courses.value.length > 0) {
      questionForm.courseId = courses.value[0].courseId;
    }
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 获取知识点列表（所有课程）
const fetchKnowledgePoints = async () => {
  try {
    const response = await getAllKnowledgePoints();
    knowledgePoints.value = response || [];
    if (knowledgePoints.value.length > 0) {
      questionForm.kpId = knowledgePoints.value[0].kpId;
    }
  } catch (error) {
    ElMessage.error("获取知识点列表失败");
    console.error("获取知识点列表失败:", error);
  }
};

// 根据当前课程获取知识点
const fetchKnowledgePointsByCourse = async () => {
  try {
    const response = await getKnowledgePointsByCourseId(questionForm.courseId);
    knowledgePoints.value = response || [];
    if (knowledgePoints.value.length > 0) {
      questionForm.kpId = knowledgePoints.value[0].kpId;
    } else {
      questionForm.kpId = null;
    }
  } catch (error) {
    ElMessage.error("获取知识点列表失败");
    console.error("获取知识点列表失败:", error);
  }
};

// 获取题目列表
const fetchQuestions = async () => {
  try {
    const response = await getQuestions(currentPage.value, pageSize.value, searchParams);
    questions.value = response.records || [];
    total.value = response.total || 0;
  } catch (error) {
    ElMessage.error("获取题目列表失败");
    console.error("获取题目列表失败:", error);
  }
};

// 获取题目分类
const fetchCategories = async () => {
  try {
    const data = await getCategories();
    categories.value = data || [];
    if (categories.value.length > 0) {
      questionForm.categoryId = categories.value[0].categoryId;
    }
  } catch (error) {
    console.error("获取题目分类失败:", error);
  }
};

// 获取题目标签
const fetchTags = async () => {
  try {
    const data = await getTags();
    tags.value = data || [];
  } catch (error) {
    console.error("获取题目标签失败:", error);
  }
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  fetchQuestions();
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  fetchQuestions();
};

// 选择处理
const handleSelectionChange = (selection) => {
  selectedQuestionIds.value = (selection || []).map((item) => item.questionId);
};

// 添加选项
const addOption = () => {
  options.value.push({ optionText: "", isCorrect: false });
};

// 题型变化时重置相关字段
watch(
    () => questionForm.questionType,
    (newType) => {
      if ([1, 2].includes(newType)) {
        options.value = [
          { optionText: "", isCorrect: false },
          { optionText: "", isCorrect: false },
        ];
      } else if (newType === 3) {
        questionForm.correctAnswer = "1";
      } else {
        questionForm.correctAnswer = "";
      }
    }
);

// 课程变化时重新加载知识点
watch(
    () => questionForm.courseId,
    async (newCourseId) => {
      if (newCourseId) {
        try {
          const response = await getKnowledgePointsByCourseId(newCourseId);
          knowledgePoints.value = response || [];
          if (knowledgePoints.value.length > 0) {
            questionForm.kpId = knowledgePoints.value[0].kpId;
          } else {
            questionForm.kpId = null;
            ElMessage.warning("该课程暂无知识点，请选择其他课程或先添加知识点");
          }
        } catch (error) {
          ElMessage.error("获取知识点列表失败");
          console.error("获取知识点列表失败:", error);
        }
      } else {
        knowledgePoints.value = [];
        questionForm.kpId = null;
      }
    }
);

// 监听搜索参数变化，自动触发查询
watch(
    searchParams,
    () => {
      currentPage.value = 1;
      fetchQuestions();
    },
    { deep: true }
);

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedQuestionIds.value.length} 道题目吗？`,
        "删除确认",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
        }
    );

    await deleteQuestionBatch(selectedQuestionIds.value);
    ElMessage.success("批量删除成功");
    selectedQuestionIds.value = [];
    fetchQuestions();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("批量删除失败");
      console.error("批量删除失败:", error);
    }
  }
};

// 添加题目
const handleAddQuestion = async () => {
  dialogTitle.value = "添加题目";
  await resetQuestionForm();
  dialogVisible.value = true;
};

// 编辑题目
const handleEditQuestion = (row) => {
  dialogTitle.value = "编辑题目";
  Object.assign(questionForm, row);

  if ([1, 2].includes(row.questionType)) {
    options.value = row.options || [
      { optionText: "", isCorrect: false },
      { optionText: "", isCorrect: false },
    ];
  }

  selectedTags.value = row.tags ? row.tags.map((tag) => tag.tagId) : [];
  dialogVisible.value = true;
};

// 保存题目
const handleSaveQuestion = async () => {
  try {
    if (!questionForm.kpId) {
      ElMessage.error("请选择知识点");
      return;
    }
    if (!questionForm.questionContent) {
      ElMessage.error("请输入题目内容");
      return;
    }
    if (!questionForm.correctAnswer) {
      ElMessage.error("请输入正确答案");
      return;
    }

    const questionData = { ...questionForm };

    if ([1, 2].includes(questionForm.questionType)) {
      questionData.options = options.value
          .filter((opt) => opt.optionText)
          .map((opt) => ({
            ...opt,
            isCorrect: opt.isCorrect ? 1 : 0,
          }));
    }

    questionData.tags = selectedTags.value.map((tagId) => ({ tagId }));

    console.log('保存题目数据:', questionData);
    
    if (questionForm.questionId) {
      const result = await updateQuestion(questionForm.questionId, questionData);
      console.log('更新题目结果:', result);
      ElMessage.success("题目更新成功");
    } else {
      const result = await addQuestion(questionData);
      console.log('添加题目结果:', result);
      ElMessage.success("题目添加成功");
    }

    dialogVisible.value = false;
    fetchQuestions();
  } catch (error) {
    console.error('保存题目失败:', error);
    console.error('错误详情:', error.response);
    ElMessage.error(questionForm.questionId ? "题目更新失败: " + (error.response?.data?.message || error.message) : "题目添加失败: " + (error.response?.data?.message || error.message));
  }
};

// 删除题目
const handleDeleteQuestion = async (questionId) => {
  try {
    await ElMessageBox.confirm("确定要删除该题目吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteQuestion(questionId);
    ElMessage.success("题目删除成功");
    fetchQuestions();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("题目删除失败");
      console.error("删除题目失败:", error);
    }
  }
};

// 重置题目表单
const resetQuestionForm = async () => {
  const defaultCourseId = courses.value.length > 0 ? courses.value[0].courseId : null;

  Object.assign(questionForm, {
    questionId: null,
    courseId: defaultCourseId,
    kpId: null,
    categoryId: categories.value.length > 0 ? categories.value[0].categoryId : null,
    questionType: 1,
    questionContent: "",
    correctAnswer: "",
    analysis: "",
    difficulty: "中等",
    score: 5,
    status: "待审核",
    creatorId: 1,
  });

  if (defaultCourseId) {
    await fetchKnowledgePointsByCourse();
  } else {
    knowledgePoints.value = [];
  }

  options.value = [
    { optionText: "", isCorrect: false },
    { optionText: "", isCorrect: false },
  ];
  selectedTags.value = [];
};

// 查看题目详情
const handleViewQuestion = (row) => {
  currentQuestion.value = row;
  viewDialogVisible.value = true;
};

// 获取题型名称
const getQuestionTypeName = (type) => {
  const typeMap = { 1: "单选题", 2: "多选题", 3: "判断题", 4: "填空题", 5: "简答题" };
  return typeMap[type] || "未知题型";
};

// 格式化答案
const formatAnswer = (answer, type) => {
  if (type === 3) return answer === "1" ? "对" : "错";
  return answer;
};

onMounted(async () => {
  await fetchCourses();
  if (questionForm.courseId) {
    await fetchKnowledgePointsByCourse();
  } else {
    await fetchKnowledgePoints();
  }
  await fetchCategories();
  await fetchTags();
  await fetchQuestions();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.header-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
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

.mb-4 {
  margin-bottom: 16px;
}

.mt-4 {
  margin-top: 16px;
  display: flex;
  justify-content: center;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  width: 20px;
  text-align: center;
}

.clickable-content {
  cursor: pointer;
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.clickable-content:hover {
  text-decoration: underline;
}
</style>
