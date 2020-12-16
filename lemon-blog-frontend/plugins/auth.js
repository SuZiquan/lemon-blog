import Vue from 'vue'

const auth = {
  setPathBeforeAuth(pathBeforeAuth) {
    if (process.browser) {
      localStorage.setItem('pathBeforeAuth', pathBeforeAuth)
    }
  },

  getPathBeforeAuth() {
    if (process.browser) {
      return localStorage.getItem('pathBeforeAuth')
    }
  },

  removePathBeforeAuth(pathBeforeAuth) {
    if (process.browser) {
      localStorage.removeItem('pathBeforeAuth')
    }
  },
}

Vue.prototype.$auth = auth
