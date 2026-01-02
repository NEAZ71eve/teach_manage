import request from './request';

/**
 * 获取所有实践项目
 */
export const getPracticeProjects = () => {
  return request({
    url: '/practice-project',
    method: 'GET'
  });
}

/**
 * 添加实践项目
 */
export const addPracticeProject = (data) => {
  return request({
    url: '/practice-project',
    method: 'POST',
    data
  });
}

/**
 * 更新实践项目
 */
export const updatePracticeProject = (projectId, data) => {
  return request({
    url: `/practice-project/${projectId}`,
    method: 'PUT',
    data
  });
}

/**
 * 删除实践项目
 */
export const deletePracticeProject = (projectId) => {
  return request({
    url: `/practice-project/${projectId}`,
    method: 'DELETE'
  });
}
