<template>
  <div>
    <Header></Header>
    <main>
      <Nuxt />
      <a-back-top></a-back-top>
    </main>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from '@/components/common/Header'
import Footer from '@/components/common/Footer'

export default {
  components: {
    Header,
    Footer,
  },
  mounted() {
    this.handleMediaQuery()
    window.addEventListener('resize', this.handleMediaQuery)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleMediaQuery)
  },
  methods: {
    handleMediaQuery() {
      const width = document.documentElement.clientWidth
      const newIsMobile = width < 960
      if (this.isMobile !== newIsMobile) {
        this.isMobile = newIsMobile
        this.$store.commit('app/setIsMobile', this.isMobile)
      }
    },
  },
}
</script>
<style lang="less">
@import 'assets/css/main.less';
html body {
  background: #f7f7f7;
  main {
    max-width: 1200px;
    margin-left: auto;
    margin-right: auto;
    min-height: calc(100vh - @header-height - @footer-height);
  }
}
</style>
