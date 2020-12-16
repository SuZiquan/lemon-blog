const cookieparser = process.server ? require('cookieparser') : undefined

export const actions = {
  async nuxtServerInit({ commit, dispatch, $axios }, { req, res }) {
    let token = null
    if (req.headers.cookie) {
      const parsed = cookieparser.parse(req.headers.cookie)
      try {
        token = parsed.token
        commit('auth/setToken', token)
        await dispatch('auth/getUserInfo')
      } catch (err) {
        // No valid cookie found
      }
    }
    await dispatch('app/overview')
  },
}
