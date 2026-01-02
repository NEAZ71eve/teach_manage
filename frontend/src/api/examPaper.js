import request from './request'

/**
 * 获取试卷列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @returns {Promise}
 */
export const getExamPapers = (page, pageSize) => {
  return request({
    url: `/exam-paper/page?page=${page}&limit=${pageSize}`,
    method: 'GET'
  })
}

/**
 * 获取所有试卷
 * @returns {Promise}
 */
export const getAllExamPapers = () => {
  return request({
    url: '/exam-paper',
    method: 'GET'
  })
}

/**
 * 获取试卷详情
 * @param {number} paperId - 试卷ID
 * @returns {Promise}
 */
export const getExamPaperById = (paperId) => {
  return request({
    url: `/exam-paper/${paperId}`,
    method: 'GET'
  })
}

/**
 * 根据课程ID获取试卷
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getExamPapersByCourseId = (courseId) => {
  return request({
    url: `/exam-paper/course/${courseId}`,
    method: 'GET'
  })
}

/**
 * 添加试卷
 * @param {object} paper - 试卷信息
 * @returns {Promise}
 */
export const addExamPaper = (paper) => {
  return request({
    url: '/exam-paper',
    method: 'POST',
    data: paper
  })
}

/**
 * 更新试卷
 * @param {number} paperId - 试卷ID
 * @param {object} paper - 试卷信息
 * @returns {Promise}
 */
export const updateExamPaper = (paperId, paper) => {
  return request({
    url: `/exam-paper/${paperId}`,
    method: 'PUT',
    data: paper
  })
}

/**
 * 删除试卷
 * @param {number} paperId - 试卷ID
 * @returns {Promise}
 */
export const deleteExamPaper = (paperId) => {
  return request({
    url: `/exam-paper/${paperId}`,
    method: 'DELETE'
  })
}

/**
 * 批量删除试卷
 * @param {Array} ids - 试卷ID数组
 * @returns {Promise}
 */
export const deleteExamPaperBatch = (ids) => {
  return request({
    url: '/exam-paper/batch',
    method: 'DELETE',
    data: ids
  })
}

/**
 * 获取试卷的题目列表
 * @param {number} paperId - 试卷ID
 * @returns {Promise}
 */
export const getPaperQuestions = (paperId) => {
  return request({
    url: `/exam-paper/${paperId}/questions`,
    method: 'GET'
  })
}

/**
 * 添加题目到试卷
 * @param {object} examPaperQuestion - 试卷题目关联信息
 * @returns {Promise}
 */
export const addQuestionToPaper = (examPaperQuestion) => {
  return request({
    url: '/exam-paper/question',
    method: 'POST',
    data: examPaperQuestion
  })
}

/**
 * 从试卷中删除题目
 * @param {number} id - 试卷题目关联ID
 * @returns {Promise}
 */
export const removeQuestionFromPaper = (id) => {
  return request({
    url: `/exam-paper/question/${id}`,
    method: 'DELETE'
  })
}

/**
 * 清空试卷题目
 * @param {number} paperId - 试卷ID
 * @returns {Promise}
 */
export const clearPaperQuestions = (paperId) => {
  return request({
    url: `/exam-paper/${paperId}/questions`,
    method: 'DELETE'
  })
}

/**
 * 自动组卷
 * @param {object} params - 组卷参数
 * @returns {Promise}
 */
export const autoGeneratePaper = (params) => {
  return request({
    url: '/exam-paper/auto-generate',
    method: 'POST',
    data: params
  })
}
