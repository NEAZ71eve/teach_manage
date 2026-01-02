import request from "./request";

/**
 * 获取知识点列表（带分页）
 * @param {number} page - 当前页码
 * @param {number} pageSize - 每页大小
 * @returns {Promise}
 */
export const getKnowledgePoints = (page, pageSize) => {
  return request({
    url: `/knowledge-point/page?page=${page}&limit=${pageSize}`,
    method: "GET",
  });
};

/**
 * 获取所有知识点
 * @returns {Promise}
 */
export const getAllKnowledgePoints = () => {
  return request({
    url: "/knowledge-point",
    method: "GET",
  });
};

/**
 * 获取知识点详情
 * @param {number} pointId - 知识点ID
 * @returns {Promise}
 */
export const getKnowledgePointById = (pointId) => {
  return request({
    url: `/knowledge-point/${pointId}`,
    method: "GET",
  });
};

/**
 * 根据课程ID获取知识点
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getKnowledgePointsByCourseId = (courseId) => {
  return request({
    url: `/knowledge-point/course/${courseId}`,
    method: "GET",
  });
};

/**
 * 根据父ID获取知识点
 * @param {number} parentId - 父知识点ID
 * @returns {Promise}
 */
export const getKnowledgePointsByParentId = (parentId) => {
  return request({
    url: `/knowledge-point/parent/${parentId}`,
    method: "GET",
  });
};

/**
 * 添加知识点
 * @param {object} point - 知识点信息
 * @returns {Promise}
 */
export const addKnowledgePoint = (point) => {
  return request({
    url: "/knowledge-point",
    method: "POST",
    data: point,
  });
};

/**
 * 更新知识点
 * @param {number} pointId - 知识点ID
 * @param {object} point - 知识点信息
 * @returns {Promise}
 */
export const updateKnowledgePoint = (pointId, point) => {
  return request({
    url: `/knowledge-point/${pointId}`,
    method: "PUT",
    data: point,
  });
};

/**
 * 删除知识点
 * @param {number} pointId - 知识点ID
 * @returns {Promise}
 */
export const deleteKnowledgePoint = (pointId) => {
  return request({
    url: `/knowledge-point/${pointId}`,
    method: "DELETE",
  });
};

/**
 * 批量删除知识点
 * @param {Array} ids - 知识点ID数组
 * @returns {Promise}
 */
export const deleteKnowledgePointBatch = (ids) => {
  return request({
    url: "/knowledge-point/batch",
    method: "DELETE",
    data: ids,
  });
};

/**
 * 根据课程ID获取知识点树结构
 * @param {number} courseId - 课程ID
 * @returns {Promise}
 */
export const getKnowledgePointTree = (courseId) => {
  return request({
    url: `/knowledge-point/tree/${courseId}`,
    method: "GET",
  });
};

/**
 * 获取所有知识点树结构
 * @returns {Promise}
 */
export const getAllKnowledgePointTree = () => {
  return request({
    url: "/knowledge-point/tree",
    method: "GET",
  });
};
