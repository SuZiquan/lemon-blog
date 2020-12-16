<template>
  <div>
    <content-layout :show-sidebar="false">
      <template v-slot:content>
        <div class="boxed error-wrapper">
          <h2>错误码: {{ statusCode }} 错误信息：{{ message }}</h2>
          <NuxtLink to="/">回到首页</NuxtLink>
        </div>
      </template>
    </content-layout>
  </div>
</template>

<script>
const Cookie = process.client ? require('js-cookie') : undefined

export default {
  data() {
    return {
      statusCode: this.$route.query.statusCode,
      message: this.$route.query.message,
    }
  },
  created() {
    if (this.statusCode === 401) {
      Cookie.remove('token')
      this.$store.commit('auth/logout')
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
