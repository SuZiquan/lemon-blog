const Cookie = process.client ? require('js-cookie') : undefined

export default function ({ $axios, redirect, store, error }) {
  $axios.onRequest((config) => {
    if (store.state.auth.token) {
      config.headers.common.Authorization = store.state.auth.token
    } else if (config.headers.common.Authorization) {
      delete config.headers.common.Authorization
    }
  })

  $axios.onError((err) => {
    if (process.client) {
      return Promise.reject(err.response)
    }
    const code = parseInt(err.response && err.response.status)
    if (code === 401) {
      error({ statusCode: 401, message: '认证失败' })
    } else {
      redirect('/error', {
        statusCode: code,
        message: err.response.data.message,
      })
    }
  })

  $axios.onResponse((response) => {
    if (process.client && response.headers.authorization) {
      const token = response.headers.authorization
      Cookie.set('token', token)
      store.commit('auth/setToken', token)
      store.dispatch('auth/getUserInfo')
    }

    if (response.status === 200) {
      return response.data
    }
  })
}
