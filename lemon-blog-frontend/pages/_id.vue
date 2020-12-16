<template>
  <content-layout>
    <template v-slot:content>
      <!-- eslint-disable -->
      <div class="content-wrapper" v-html="content"></div>
      <!-- eslint-enable -->
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'
import marked from 'marked'

export default {
  components: { ContentLayout },

  async asyncData({ route, store, $axios, error }) {
    const menuItems = store.state.app.menuItems

    const menuItem = menuItems.find((item) => {
      return item.link === route.path
    })
    if (!menuItem) {
      error({ statusCode: 404, message: 'not found' })
      return
    }
    store.commit('app/setCurrentNav', menuItem.name)
    const page = await $axios.$get('/pages/' + menuItem.pageId)

    const markedOptions = {
      gfm: true,
      tables: true,
    }

    const mdRender = new marked.Renderer()

    return {
      title: page.name,
      content: marked(page.content, {
        renderer: mdRender,
        ...markedOptions,
      }),
    }
  },

  computed: {
    menuItems() {
      return this.$store.state.app.menuItems
    },
  },
  methods: {
    storeToken(token) {
      if (process.browser) {
        localStorage.setItem('authToken', token)
      }
    },
  },

  head() {
    return {
      title: this.title,
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.content-wrapper {
  .boxed();
  padding: 20px 30px 20px 30px;
}
</style>
