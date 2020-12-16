<template>
  <a-modal v-model="visible" class="login-modal" title="登录">
    <div class="login-types">
      <div class="login-type" @click="toLogin">
        <a-icon style="font-size: 80px; padding: 4px 0 4px 0" type="github" />
        <div>GitHub授权登录</div>
      </div>
    </div>
    <template slot="footer">
      <a-button key="back" @click="visible = false"> 取消 </a-button>
    </template>
  </a-modal>
</template>

<script>
export default {
  name: 'LoginModal',
  data() {
    return {
      visible: false,
    }
  },
  methods: {
    show() {
      this.visible = true
    },
    toLogin() {
      this.$axios({
        method: 'get',
        url: '/oauth/login/github',
      }).then((res) => {
        this.$auth.setPathBeforeAuth(this.$route.path)
        window.location.href = res.data.authorizeUrl
      })
    },
  },
}
</script>

<style scoped lang="less">
@import '../../assets/css/main.less';

/deep/ .ant-modal {
  width: 320px !important;
}

.login-types {
  display: flex;
  justify-content: center;
  align-items: center;

  .login-type {
    padding: 0 8px 4px 8px;
    border: 2px solid #aaaaaa;
    border-radius: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    &:hover {
      border: 2px solid @primary-color;
      color: @primary-color;
      cursor: pointer;
    }
  }
}
</style>
