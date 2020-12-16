import request from '@/utils/request'

export function getOverview () {
  return request({
    url: '/overview',
    method: 'get'
  })
}
