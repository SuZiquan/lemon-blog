import request from '@/utils/request'

export const articleStatus = {
  0: '隐藏',
  1: '公开'
}

export function listArticleComments (id, query) {
  return request({
    url: '/articles/' + id + '/comments',
    method: 'get',
    params: query
  })
}

export function getArticle (id) {
  return request({
    url: '/articles/' + id,
    method: 'get'
  })
}

export function listArticles (query, data) {
  return request({
    url: '/articles',
    method: 'get',
    params: query,
    data: data
  })
}

export function createArticle (params) {
  return request({
    url: '/articles',
    method: 'post',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function updateArticle (id, params) {
  return request({
    url: '/articles/' + id,
    method: 'put',
    data: params,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

export function deleteArticle (id) {
  return request({
    url: '/articles/' + id,
    method: 'delete'
  })
}
