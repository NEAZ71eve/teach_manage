<template>
  <div class="question-bank-management">
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span>题库管理</span>
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
        <el-table-column
          prop="questionContent"
          label="题目内容"
          min-width="300"
          @click="handleViewQuestion(scope.row)"
        />
        <el-table-column prop="difficulty" label="难度" width="80" />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">{{
              scope.row.status
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleViewQuestion(scope.row)"
            >
              <el-icon><View /></el-icon>
              查看
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleEditQuestion(scope.row)"
            >
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteQuestion(scope.row.questionId)"
            >
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

    <!-- 题目表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="questionForm" label-width="120px">
        <el-form-item label="题型" required>
          <el-select
            v-model="questionForm.questionType"
            placeholder="请选择题型"
          >
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
        <el-form-item label="题目分类" required>
          <el-select v-model="questionForm.categoryId" placeholder="请选择分类">
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
        <el-form-item
          label="选项"
          v-if="[1, 2].includes(questionForm.questionType)"
        >
          <div
            v-for="(option, index) in options"
            :key="index"
            class="option-item"
          >
            <el-input
              v-model="option.optionText"
              placeholder="请输入选项内容"
              prefix-icon="el-icon--right"
            >
              <template #prefix>
                <span class="option-label"
                  >{{ String.fromCharCode(65 + index) }}.</span
                >
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

        <el-form-item label="标签">
          <el-select v-model="selectedTags" multiple placeholder="请选择标签">
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
          <el-descriptions-item label="题目ID">{{
            currentQuestion?.questionId
          }}</el-descriptions-item>
          <el-descriptions-item label="题型">{{
            getQuestionTypeName(currentQuestion?.questionType)
          }}</el-descriptions-item>
          <el-descriptions-item label="所属学科">{{
            currentQuestion?.courseName
          }}</el-descriptions-item>
          <el-descriptions-item label="知识点">{{
            currentQuestion?.kpName
          }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{
            currentQuestion?.categoryName
          }}</el-descriptions-item>
          <el-descriptions-item label="题目内容">
            <div v-html="currentQuestion?.questionContent"></div>
          </el-descriptions-item>
          <el-descriptions-item
            label="选项"
            v-if="[1, 2].includes(currentQuestion?.questionType)"
          >
            <div v-if="currentQuestion?.options">
              <div
                v-for="(option, index) in currentQuestion.options"
                :key="index"
                class="option-item"
              >
                <span class="option-label"
                  >{{ String.fromCharCode(65 + index) }}.</span
                >
                <span>{{ option.optionText }}</span>
                <el-tag
                  v-if="option.isCorrect"
                  type="success"
                  size="small"
                  style="margin-left: 10px"
                  >正确</el-tag
                >
              </div>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="正确答案">{{
            formatAnswer(
              currentQuestion?.correctAnswer,
              currentQuestion?.questionType
            )
          }}</el-descriptions-item>
          <el-descriptions-item label="答案解析">{{
            currentQuestion?.analysis || "无"
          }}</el-descriptions-item>
          <el-descriptions-item label="难度">{{
            currentQuestion?.difficulty
          }}</el-descriptions-item>
          <el-descriptions-item label="分值">{{
            currentQuestion?.score
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusTagType(currentQuestion?.status)">{{
              currentQuestion?.status
            }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="标签" v-if="currentQuestion?.tags">
            <el-tag
              v-for="tag in currentQuestion.tags"
              :key="tag.tagId"
              style="margin-right: 5px; margin-bottom: 5px"
            >
              {{ tag.tagName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item
            label="使用情况"
            v-if="currentQuestion?.statistics"
          >
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
import {
  getAllKnowledgePoints,
  getKnowledgePointsByCourseId,
} from "../api/knowledgePoint";
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
    courses.value = response;
    if (response.length > 0) {
      questionForm.courseId = response[0].courseId;
    }
  } catch (error) {
    ElMessage.error("获取课程列表失败");
    console.error("获取课程列表失败:", error);
  }
};

// 获取知识点列表
const fetchKnowledgePoints = async () => {
  try {
    const response = await getAllKnowledgePoints();
    knowledgePoints.value = response;
    if (response.length > 0) {
      questionForm.kpId = response[0].kpId;
    }
  } catch (error) {
    ElMessage.error("获取知识点列表失败");
    console.error("获取知识点列表失败:", error);
  }
};

// 获取题目列表
const fetchQuestions = async () => {
  try {
    const response = await getQuestions(
      currentPage.value,
      pageSize.value,
      searchParams
    );
    questions.value = response.records;
    total.value = response.total;
  } catch (error) {
    ElMessage.error("获取题目列表失败");
    console.error("获取题目列表失败:", error);
  }
};

// 获取题目分类
const fetchCategories = async () => {
  try {
    const data = await getCategories();
    categories.value = data;
    if (data.length > 0) {
      questionForm.categoryId = data[0].categoryId;
    }
  } catch (error) {
    console.error("获取题目分类失败:", error);
  }
};

// 获取题目标签
const fetchTags = async () => {
  try {
    const data = await getTags();
    tags.value = data;
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
  selectedQuestionIds.value = selection.map((item) => item.questionId);
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
        knowledgePoints.value = response;
        if (response.length > 0) {
          questionForm.kpId = response[0].kpId;
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
  (newParams) => {
    currentPage.value = 1; // 重置到第一页
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
const handleAddQuestion = () => {
  dialogTitle.value = "添加题目";
  resetQuestionForm();
  dialogVisible.value = true;
};

// 编辑题目
const handleEditQuestion = (row) => {
  dialogTitle.value = "编辑题目";
  Object.assign(questionForm, row);

  // 处理选项
  if ([1, 2].includes(row.questionType)) {
    options.value = row.options || [
      { optionText: "", isCorrect: false },
      { optionText: "", isCorrect: false },
    ];
  }

  // 处理标签
  selectedTags.value = row.tags ? row.tags.map((tag) => tag.tagId) : [];

  dialogVisible.value = true;
};

// 保存题目
const handleSaveQuestion = async () => {
  try {
    // 表单验证
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

    // 准备题目数据
    const questionData = { ...questionForm };

    // 处理选项
    if ([1, 2].includes(questionForm.questionType)) {
      questionData.options = options.value
        .filter((opt) => opt.optionText)
        .map((opt) => ({
          ...opt,
          isCorrect: opt.isCorrect ? 1 : 0, // 将布尔值转换为整数
        }));
    }

    // 处理标签
    questionData.tags = selectedTags.value.map((tagId) => ({ tagId }));

    if (questionForm.questionId) {
      // 更新题目
      await updateQuestion(questionForm.questionId, questionData);
      ElMessage.success("题目更新成功");
    } else {
      // 添加题目
      await addQuestion(questionData);
      ElMessage.success("题目添加成功");
    }
    dialogVisible.value = false;
    fetchQuestions();
  } catch (error) {
    ElMessage.error(questionForm.questionId ? "题目更新失败" : "题目添加失败");
    console.error("保存题目失败:", error);
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
const resetQuestionForm = () => {
  Object.assign(questionForm, {
    questionId: null,
    courseId: courses.value.length > 0 ? courses.value[0].courseId : null,
    kpId:
      knowledgePoints.value.length > 0 ? knowledgePoints.value[0].kpId : null,
    categoryId:
      categories.value.length > 0 ? categories.value[0].categoryId : null,
    questionType: 1,
    questionContent: "",
    correctAnswer: "",
    analysis: "",
    difficulty: "中等",
    score: 5,
    status: "待审核",
    creatorId: 1,
  });
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

// 获取状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case "待审核":
      return "warning";
    case "已审核":
      return "success";
    case "已拒绝":
      return "danger";
    default:
      return "info";
  }
};

// 获取题型名称
const getQuestionTypeName = (type) => {
  const typeMap = {
    1: "单选题",
    2: "多选题",
    3: "判断题",
    4: "填空题",
    5: "简答题",
  };
  return typeMap[type] || "未知题型";
};

// 格式化答案
const formatAnswer = (answer, type) => {
  if (type === 3) {
    return answer === "1" ? "对" : "错";
  }
  return answer;
};

// 页面挂载时获取数据
onMounted(async () => {
  await fetchCourses();
  await fetchKnowledgePoints();
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

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-label {
  width: 20px;
  text-align: center;
}
</style>
