import request from '@/utils/request'

export function listTags () {
  return request({
    url: '/tags',
    method: 'get'
  })
}

export function createTag (params) {
  return request({
    url: '/tags',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function deleteTag (id) {
  return request({
    url: '/tags/' + id,
    method: 'delete'
  })
}
