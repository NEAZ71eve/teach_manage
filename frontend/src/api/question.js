import request from "./request";

/**
 * 获取题目列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @param {object} searchParams - 搜索参数
 * @returns {Promise}
 */
export const getQuestions = (page, pageSize, searchParams) => {
  // 构建查询字符串，只包含有值的参数
  const params = new URLSearchParams({
    page,
    limit: pageSize,
  });

  if (searchParams.questionType)
    params.append("questionType", searchParams.questionType);
  if (searchParams.difficulty)
    params.append("difficulty", searchParams.difficulty);
  if (searchParams.kpId)
    params.append("kpId", searchParams.kpId);
  if (searchParams.courseId)
    params.append("courseId", searchParams.courseId);
  if (searchParams.keyword)
    params.append("keyword", searchParams.keyword);

  return request({
    url: `/question/page?${params.toString()}`,
    method: "GET",
  });
};

/**
 * 获取所有题目
 * @returns {Promise}
 */
export const getAllQuestions = () => {
  return request({
    url: "/question",
    method: "GET",
  });
};

/**
 * 获取题目详情
 * @param {number} questionId - 题目ID
 * @returns {Promise}
 */
export const getQuestionById = (questionId) => {
  return request({
    url: `/question/${questionId}`,
    method: "GET",
  });
};

/**
 * 根据课程ID获取题目
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getQuestionsByCourseId = (courseId) => {
  return request({
    url: `/question/course/${courseId}`,
    method: "GET",
  });
};

/**
 * 根据知识点ID获取题目
 * @param {number} pointId - 知识点ID
 * @returns {Promise}
 */
export const getQuestionsByPointId = (pointId) => {
  return request({
    url: `/question/point/${pointId}`,
    method: "GET",
  });
};

/**
 * 根据题型获取题目
 * @param {string} questionType - 题型
 * @returns {Promise}
 */
export const getQuestionsByType = (questionType) => {
  return request({
    url: `/question/type/${questionType}`,
    method: "GET",
  });
};

/**
 * 根据难度获取题目
 * @param {string} difficulty - 难度
 * @returns {Promise}
 */
export const getQuestionsByDifficulty = (difficulty) => {
  return request({
    url: `/question/difficulty/${difficulty}`,
    method: "GET",
  });
};

/**
 * 添加题目
 * @param {object} question - 题目信息
 * @returns {Promise}
 */
export const addQuestion = (question) => {
  return request({
    url: "/question",
    method: "POST",
    data: question,
  });
};

/**
 * 更新题目
 * @param {number} questionId - 题目ID
 * @param {object} question - 题目信息
 * @returns {Promise}
 */
export const updateQuestion = (questionId, question) => {
  return request({
    url: `/question/${questionId}`,
    method: "PUT",
    data: question,
  });
};

/**
 * 删除题目
 * @param {number} questionId - 题目ID
 * @returns {Promise}
 */
export const deleteQuestion = (questionId) => {
  return request({
    url: `/question/${questionId}`,
    method: "DELETE",
  });
};

/**
 * 批量删除题目
 * @param {Array} ids - 题目ID数组
 * @returns {Promise}
 */
export const deleteQuestionBatch = (ids) => {
  return request({
    url: "/question/batch",
    method: "DELETE",
    data: ids,
  });
};

/**
 * 获取所有题目分类
 * @returns {Promise}
 */
export const getCategories = () => {
  return request({
    url: "/question/categories",
    method: "GET",
  });
};

/**
 * 获取所有题目标签
 * @returns {Promise}
 */
export const getTags = () => {
  return request({
    url: "/question/tags",
    method: "GET",
  });
};
