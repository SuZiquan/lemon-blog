const Cookie = process.client ? require('js-cookie') : undefined

export const state = () => ({
  token: null,
  userInfo: null,
})

export const actions = {
  async getUserInfo({ commit }) {
    try {
      const { data } = await this.$axios.get('/auth/userInfo')
      commit('setUserInfo', data)
    } catch (error) {
      commit('logout')
    }
  },
}

export const mutations = {
  logout(state) {
    if (Cookie) {
      Cookie.remove('token')
    }
    state.token = null
    state.userInfo = null
  },

  setToken(state, token) {
    state.token = token
  },

  setUserInfo(state, userInfo) {
    state.userInfo = userInfo
  },
}

export const getters = {
  logined(state) {
    return state.token && state.userInfo
  },
}
