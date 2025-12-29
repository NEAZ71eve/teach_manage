import request from './request'

/**
 * 获取角色列表
 * @returns {Promise}
 */
export const getRoles = () => {
  return request({
    url: '/roles',
    method: 'GET'
  })
}

/**
 * 根据ID获取角色
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const getRoleById = (roleId) => {
  return request({
    url: `/roles/${roleId}`,
    method: 'GET'
  })
}

/**
 * 新增角色
 * @param {object} role - 角色信息
 * @returns {Promise}
 */
export const addRole = (role) => {
  return request({
    url: '/roles',
    method: 'POST',
    data: role
  })
}

/**
 * 更新角色
 * @param {number} roleId - 角色ID
 * @param {object} role - 角色信息
 * @returns {Promise}
 */
export const updateRole = (roleId, role) => {
  return request({
    url: `/roles/${roleId}`,
    method: 'PUT',
    data: role
  })
}

/**
 * 删除角色
 * @param {number} roleId - 角色ID
 * @returns {Promise}
 */
export const deleteRole = (roleId) => {
  return request({
    url: `/roles/${roleId}`,
    method: 'DELETE'
  })
}
