import request from '@/utils/request'

export const pageStatus = {
  0: '隐藏',
  1: '公开'
}

export function getPage (id) {
  return request({
    url: '/pages/' + id,
    method: 'get'
  })
}

export function listPages () {
  return request({
    url: '/pages',
    method: 'get'
  })
}

export function createPage (params) {
  return request({
    url: '/pages',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function updatePage (id, params) {
  return request({
    url: '/pages/' + id,
    method: 'put',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function deletePage (id) {
  return request({
    url: '/pages/' + id,
    method: 'delete'
  })
}
