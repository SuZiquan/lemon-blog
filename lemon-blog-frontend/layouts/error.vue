<template>
  <div>
    <content-layout :show-sidebar="false">
      <template v-slot:content>
        <div class="boxed error-wrapper">
          <h2>错误码: {{ error.statusCode }} 错误信息：{{ error.message }}</h2>
          <NuxtLink to="/">回到首页</NuxtLink>
        </div>
      </template>
    </content-layout>
  </div>
</template>

<script>
const Cookie = process.client ? require('js-cookie') : undefined

export default {
  props: {
    error: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },

  mounted() {
    if (this.error.statusCode === 401) {
      Cookie.remove('token')
      this.$store.commit('auth/logout')
      this.$router.go(-1)
    }
  },
}
</script>

<style scoped lang="less">
.error-wrapper {
  margin: auto;
  text-align: center;
  padding: 40px;
}
</style>
