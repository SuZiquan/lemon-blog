import request from '@/utils/request'

export function changeUsername (username) {
  return request({
    url: '/account/username',
    method: 'post',
    data: {
      username
    }
  })
}

export function changePassword (oldPassword, newPassword) {
  return request({
    url: '/account/password',
    method: 'post',
    data: {
      password: newPassword,
      oldPassword: oldPassword
    }
  })
}
