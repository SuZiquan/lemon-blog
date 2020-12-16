import request from '@/utils/request'

const userApi = {
  Login: '/auth/login'
}

export function login (parameter) {
  return request({
    url: userApi.Login,
    method: 'post',
    data: parameter
  })
}
