import request from '@/utils/request'

export function getUser (id) {
  return request({
    url: '/users/' + id,
    method: 'get'
  })
}

export function listUsers (query, data) {
  return request({
    url: '/users',
    method: 'get',
    params: query,
    data: data
  })
}
