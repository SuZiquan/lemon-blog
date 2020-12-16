import axios from 'axios'
import store from '@/store'
import storage from 'store'
import notification from 'ant-design-vue/es/notification'
import { VueAxios } from './axios'
import { ACCESS_TOKEN } from '@/store/mutation-types'

// 创建 axios 实例
const request = axios.create({
  // API 请求的默认前缀
  baseURL: process.env.VUE_APP_API_BASE_URL,
  timeout: 6000 // 请求超时时间
})

// 异常拦截处理器
const errorHandler = (error) => {
  if (error.response) {
    displayErrorMessage(error)
    if (error.response.status === 401) {
      if (storage.get(ACCESS_TOKEN)) {
        store.dispatch('Logout').then(() => {
          setTimeout(() => {
            window.location.reload()
          }, 1500)
        })
      }
    }
  }
  return Promise.reject(error)
}

const displayErrorMessage = (error) => {
  let message = '错误'
  const data = error.response.data
  const status = error.response.status
  if (status === 401) {
    message = '认证失败'
  } else if (status === 403) {
    message = '没有权限'
  } else if (status === 500) {
    message = '服务器内部错误'
  }

  notification.error({
    message: message,
    description: '错误码: ' + data.code + ' 错误信息：' + data.message
  })
}

// request interceptor
request.interceptors.request.use(config => {
  const token = storage.get(ACCESS_TOKEN)
  if (token) {
    config.headers['authorization'] = token
  }
  return config
}, errorHandler)

// response interceptor
request.interceptors.response.use((response) => {
  if (response.headers['authorization']) {
    const token = response.headers['authorization']
    storage.set(ACCESS_TOKEN, token, 60 * 60 * 1000)
    store.commit('SET_TOKEN', token)
  }
  return response.data.data
}, errorHandler)

const installer = {
  vm: {},
  install (Vue) {
    Vue.use(VueAxios, request)
  }
}

export default request

export {
  installer as VueAxios,
  request as axios
}
