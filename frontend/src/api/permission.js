import request from './request'

/**
 * 获取权限列表
 * @returns {Promise}
 */
export const getPermissions = () => {
  return request({
    url: '/permissions',
    method: 'GET'
  })
}

/**
 * 根据ID获取权限
 * @param {number} permissionId - 权限ID
 * @returns {Promise}
 */
export const getPermissionById = (permissionId) => {
  return request({
    url: `/permissions/${permissionId}`,
    method: 'GET'
  })
}

/**
 * 新增权限
 * @param {object} permission - 权限信息
 * @returns {Promise}
 */
export const addPermission = (permission) => {
  return request({
    url: '/permissions',
    method: 'POST',
    data: permission
  })
}

/**
 * 更新权限
 * @param {number} permissionId - 权限ID
 * @param {object} permission - 权限信息
 * @returns {Promise}
 */
export const updatePermission = (permissionId, permission) => {
  return request({
    url: `/permissions/${permissionId}`,
    method: 'PUT',
    data: permission
  })
}

/**
 * 删除权限
 * @param {number} permissionId - 权限ID
 * @returns {Promise}
 */
export const deletePermission = (permissionId) => {
  return request({
    url: `/permissions/${permissionId}`,
    method: 'DELETE'
  })
}
