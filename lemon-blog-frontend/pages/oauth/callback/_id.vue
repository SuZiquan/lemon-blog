<template>
  <content-layout :show-sidebar="false">
    <template v-slot:content>
      <div class="boxed login-loading">
        <div><a-spin size="large" /></div>
        <div class="login-tip">正在登录中 [{{ path }}]</div>
      </div>
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'

export default {
  name: 'Callback',
  components: { ContentLayout },

  data() {
    return {
      path: this.$route.params.id,
    }
  },

  mounted() {
    const code = this.$route.query.code
    const state = this.$route.query.state

    this.$axios
      .get('/oauth/callback/github', {
        params: {
          code,
          state,
        },
      })
      .then((data) => {
        this.$message.success('认证成功')
      })
      .catch(() => {
        this.$message.error('认证失败')
      })
      .finally(() => {
        let path = this.$auth.getPathBeforeAuth()
        this.$auth.removePathBeforeAuth(this.$route.path)
        if (!path) {
          path = '/'
        }
        this.$router.push({
          path,
        })
      })
  },

  validate({ params }) {
    return ['github'].includes(params.id)
  },
}
</script>

<style scoped lang="less">
.login-loading {
  margin: auto;
  text-align: center;
  padding: 40px;

  .login-tip {
    font-size: 20px;
    padding-top: 20px;
  }
}
</style>
