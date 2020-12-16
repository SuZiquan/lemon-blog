<template>
  <div>
    <a-card style="font-size: 20px" :body-style="{padding: '24px 32px'}" :bordered="false">
      <div class="user-info-item">
        <span class="user-info-label">用户ID:</span> {{ user.id }}
      </div>
      <div class="user-info-item">
        <span class="user-info-label">用户名: </span>{{ user.username }}
      </div>
      <div class="user-info-item">
        <span class="user-info-label">用户头像: </span><img
          style="width: 40px; height: 40px;"
          :src="user.avatarUrl"
          alt="user.avatarUrl">
      </div>
      <div class="user-info-item">
        <span class="user-info-label"> 第三方登录: </span> {{ user.socialSource }}
      </div>
      <div class="user-info-item">
        <span class="user-info-label">邮箱: </span><a :href="'mailto:'+ user.email">{{ user.email }}</a>
      </div>
      <div class="user-info-item">
        <span class="user-info-label">上次登录: </span>{{ user.lastLogin }}
      </div>
    </a-card>
  </div>
</template>

<script>

import { getUser } from '@/api/users'

export default {
  data () {
    return {
      loading: false,
      user: {
        id: this.$route.params.id
      }
    }
  },
  mounted () {
    this.loadUserInfo()
  },

  methods: {
    loadUserInfo () {
      this.loading = true
      getUser(this.user.id
      ).then(data => {
        this.user = data
      }).finally(() => {
        this.loading = false
      })
    }
  }

}
</script>
<style lang="less">
.user-info-item {
  padding: 10px;
  font-size: 16px;

  span.user-info-label {
    display: inline-block;
    min-width: 120px;
  }
}

</style>
