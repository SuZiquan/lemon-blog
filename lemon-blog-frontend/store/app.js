export const state = () => ({
  isMobile: false,
  currentNav: '',
  resourceCount: {},

  menuItems: [],
  tags: [],
  categories: [],

  indexTitle: '跑起来就有风',
  bannerLogoUrl: null,
  bannerTitle: 'Lemon Blog',

  ownerAvatarUrl:
    'https://himg.bdimg.com/sys/portrait/item/pp.1.f17969e8.dPl7NlpA2yiDOEnd0yJj_A?_t=1607674057299',
  ownerUsername: 'Lemon Blog',
  ownerBio: 'Good good study, day day up!',

  siteFooter:
    'Copyright &copy;2020 Lemon Blog<br/><span style="margin-right: 6px">备案号:</span><a href="http://beian.miit.gov.cn">这里放置备案号</a>',
})

export const actions = {
  async overview({ commit }) {
    const { data } = await this.$axios.get('/overview')
    commit('setItem', { key: 'resourceCount', value: data.resourceCount })
    commit('setItem', { key: 'categories', value: data.categories })
    commit('setItem', { key: 'tags', value: data.tags })
    commit('setConfig', data.configs)

    const menuItemList = [
      { name: '主页', link: '/', icon: 'home' },
      { name: '归档', link: '/archive', icon: 'database' },
      { name: '分类', link: '/category', icon: 'bars' },
      { name: '标签', link: '/tag', icon: 'tags' },
    ]

    for (const page of data.pages) {
      menuItemList.push({
        pageId: page.id,
        name: page.name,
        link: page.link,
        icon: page.icon,
      })
    }
    commit('setItem', { key: 'menuItems', value: menuItemList })
  },
}

export const mutations = {
  setItem(state, { key, value }) {
    state[key] = value
  },

  setConfig(state, configs) {
    for (const config of configs) {
      if (config.value) {
        state[config.name] = config.value
      }
    }
  },

  setIsMobile(state, isMobile) {
    state.isMobile = isMobile
  },

  setCurrentNav(state, currentNav) {
    state.currentNav = currentNav
  },
}
