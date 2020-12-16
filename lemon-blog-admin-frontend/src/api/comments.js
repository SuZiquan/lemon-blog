import request from '@/utils/request'

export const commentStatus = {
  0: '未审核',
  1: '审核通过',
  2: '审核不通过'
}

export function listComments (query) {
  return request({
    url: '/comments',
    method: 'get',
    params: query
  })
}

export function createComment (params) {
  return request({
    url: '/comments',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function auditComment (id, status) {
  return request({
    url: '/comments/' + id + '/audit/' + status,
    method: 'post'
  })
}

export function countComment (params) {
  return request({
    url: '/comments/count',
    method: 'get',
    params: params
  })
}

export function deleteComment (id) {
  return request({
    url: '/comments/' + id,
    method: 'delete'
  })
}
