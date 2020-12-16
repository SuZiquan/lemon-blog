import request from '@/utils/request'

export function saveConfig (params) {
  return request({
    url: '/configs',
    method: 'put',
    data: params
  })
}

export function listConfigs () {
  return request({
    url: '/configs',
    method: 'get'
  })
}
