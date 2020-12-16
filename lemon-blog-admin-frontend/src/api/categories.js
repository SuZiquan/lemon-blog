import request from '@/utils/request'

export function listCategories () {
  return request({
    url: '/categories',
    method: 'get'
  })
}

export function createCategory (params) {
  return request({
    url: '/categories',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function deleteCategory (id) {
  return request({
    url: '/categories/' + id,
    method: 'delete'
  })
}
