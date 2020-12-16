<template>
  <!-- eslint-disable -->
  <div id="footer" v-html="siteFooter"></div>
  <!-- eslint-enable -->
</template>

<script>
export default {
  computed: {
    siteFooter() {
      return this.$store.state.app.siteFooter
    },
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

#footer {
  height: @footer-height;
  background: white;
  text-align: center;
  border-top: 1px solid #ddd;
  padding: 5px;
}
</style>
