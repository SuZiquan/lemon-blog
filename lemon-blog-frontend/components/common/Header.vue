<template>
  <div class="header-wrapper">
    <div id="header">
      <a-row type="flex" class="top-bar" justify="space-between" align="middle">
        <login-modal ref="loginModal"></login-modal>
        <a-col flex="start" class="drawer-toggle">
          <a-icon type="menu" @click="sidebarVisible = true" />
        </a-col>
        <a-col flex="auto" class="banner">
          <NuxtLink to="/">
            <img
              v-if="bannerLogoUrl"
              :src="bannerLogoUrl"
              class="banner-logo"
            />
            <span class="banner-title">{{ bannerTitle }}</span>
          </NuxtLink>
        </a-col>
        <a-col flex="end" class="search-wrapper">
          <a-auto-complete
            v-model="searchKeyword"
            class="search"
            dropdown-class-name="search-dropdown"
            :dropdown-match-select-width="true"
            style="width: 100%"
            option-label-prop="value"
          >
            <template slot="dataSource">
              <a-select-opt-group
                v-if="searchKeyword && searchKeyword.length !== 0"
                key="articles"
              >
                <div slot="label">
                  <span v-if="searchResultLoading">
                    <a-spin>搜索中...</a-spin></span
                  >
                  <span v-else-if="searchResult.length === 0">
                    没有相关结果
                  </span>
                  <span v-else>
                    包含 <a>{{ searchResultKeyword }} </a> 的文章
                  </span>
                </div>
                <div slot="notFoundContent">没有相关结果</div>

                <a-select-option
                  v-for="item in searchResult"
                  :key="item.title"
                  :value="searchResultKeyword"
                  style="padding: 0"
                >
                  <nuxt-link :to="'/article/' + item.id">
                    <a-tooltip v-if="!isMobile">
                      <template slot="title"> {{ item.title }} </template>
                      <span class="search-result-item">
                        {{ item.title }}
                      </span>
                    </a-tooltip>
                    <span v-else class="search-result-item">
                      {{ item.title }}
                    </span>
                  </nuxt-link>
                </a-select-option>
                <a-select-option
                  v-show="
                    searchResultKeyword &&
                    !searchResultLoading &&
                    searchResult.length !== 0
                  "
                  key="all"
                  :value="searchResultKeyword"
                  disabled
                  style="padding: 0"
                >
                  <nuxt-link
                    :to="{
                      name: 'search',
                      query: { queryString: searchKeyword },
                    }"
                  >
                    <span class="search-result-item search-result-show-all">
                      共{{ searchResultTotal }}条相关结果，查看更多
                    </span>
                  </nuxt-link>
                </a-select-option>
              </a-select-opt-group>
            </template>
            <a-input>
              <a-icon slot="suffix" type="search" class="icon" />
            </a-input>
          </a-auto-complete>
        </a-col>

        <a-col flex="end" class="top-menu">
          <a-menu
            v-if="!isMobile"
            theme="light"
            mode="horizontal"
            :selected-keys="currentNav"
          >
            <a-menu-item v-for="item in menuItems" :key="item.name">
              <nuxt-link :to="item.link">
                <span> <a-icon :type="item.icon" /> {{ item.name }} </span>
              </nuxt-link>
            </a-menu-item>
          </a-menu>
        </a-col>

        <a-col flex="end" class="login-wrapper">
          <a v-if="!logined" class="login-link" @click="onLogin"> 登录 </a>
          <span v-else>
            <a-dropdown>
              <span class="login-user">
                <a-avatar
                  class="user-avatar"
                  :src="userInfo.avatarUrl"
                  alt="avatar"
                />
                <span class="username">
                  {{ userInfo.username }}
                </span>
              </span>

              <a-menu slot="overlay">
                <a-menu-item>
                  <a @click="onLogout">退出登录</a>
                </a-menu-item>
              </a-menu>
            </a-dropdown>
          </span>
        </a-col>

        <a-drawer
          v-if="isMobile"
          class="left-drawer"
          placement="left"
          :closable="false"
          :visible="sidebarVisible"
          :body-style="{ padding: '0px' }"
          @close="sidebarVisible = false"
        >
          <div class="banner">
            <img
              v-if="bannerLogoUrl"
              :src="bannerLogoUrl"
              class="banner-logo"
            />
            <span class="banner-title">{{ bannerTitle }}</span>
          </div>
          <a-menu :selected-keys="currentNav" mode="inline" class="drawer-menu">
            <a-menu-item
              v-for="item in menuItems"
              :key="item.name"
              @click="sidebarVisible = false"
            >
              <nuxt-link :to="item.link">
                <span> <a-icon :type="item.icon" /> {{ item.name }} </span>
              </nuxt-link>
            </a-menu-item>
          </a-menu>
        </a-drawer>
      </a-row>
    </div>
  </div>
</template>

<script>
import LoginModal from '@/components/common/LoginModal'
import debounce from 'lodash/debounce'

export default {
  name: 'Header',

  components: {
    LoginModal,
  },

  data() {
    return {
      sidebarVisible: false,

      searchKeyword: null,
      searchResultLoading: true,
      searchResultKeyword: null,
      searchResult: [],
      searchResultTotal: null,
    }
  },
  computed: {
    logined() {
      return this.$store.getters['auth/logined']
    },
    userInfo() {
      return this.$store.state.auth.userInfo
    },
    isMobile() {
      return this.$store.state.app.isMobile
    },
    currentNav() {
      return [this.$store.state.app.currentNav]
    },
    menuItems() {
      return this.$store.state.app.menuItems
    },
    bannerLogoUrl() {
      return this.$store.state.app.bannerLogoUrl
    },
    bannerTitle() {
      return this.$store.state.app.bannerTitle
    },
  },

  watch: {
    searchKeyword: debounce(function (newVal) {
      this.searchResultLoading = true
      this.searchResult = []

      if (!newVal) {
        this.searchResultLoading = false
        return
      }

      this.$axios
        .get('/articles/search', {
          params: {
            pageSize: 5,
            queryString: newVal,
          },
        })
        .then((data) => {
          const articlePage = data.data
          this.searchResultKeyword = newVal
          this.searchResultLoading = false
          this.searchResult = articlePage.data
          this.searchResultTotal = articlePage.total
        })
    }, 1000),
  },
  methods: {
    onLogout() {
      this.$store.commit('auth/logout')
      this.$message.success('退出成功')
    },
    onLogin() {
      this.$refs.loginModal.show()
    },
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.header-wrapper {
  background: white;
  box-sizing: border-box;
  box-shadow: 0 1px 6px 0 rgba(0, 0, 0, 0.2);
  height: @header-height;
  display: flex;
  align-items: center;

  #header {
    flex: auto;
    max-width: 1200px;
    margin: auto;

    .drawer-toggle {
      display: none;
      @media @mobile {
        display: flex;
        align-items: center;
        font-size: 20px;
        margin: auto 10px auto 20px;
        &:hover {
          color: @primary-color;
        }
      }
    }
  }

  .top-bar {
    .banner {
      display: flex;
      align-items: center;
      margin-left: 10px;

      &:hover {
        cursor: pointer;
        color: @primary-color;
      }

      .banner-logo {
        width: 30px;
        height: 30px;
        margin-right: 5px;
      }

      .banner-title {
        font-size: 22px;
        font-weight: bold;
        text-align: center;
        color: @heading-color;
        &:hover {
          color: @link-color;
        }
      }
    }

    .search-wrapper {
      display: flex;
      align-items: center;
      margin-right: 10px;
    }

    .top-menu {
      @media @mobile {
        display: none;
      }

      .ant-menu-horizontal {
        border-bottom-color: transparent;

        .ant-menu-item {
          padding: 0 10px;
          margin: 0 10px 5px;
          font-size: 14px;
          line-height: 32px;

          border-bottom: 4px solid transparent;
        }

        .ant-menu-item-active,
        .ant-menu-item-selected {
          border-bottom-color: @primary-color;
          a {
            color: @primary-color;
          }
        }
      }
    }

    .login-wrapper {
      margin-right: 10px;
      .login-link {
        font-size: 14px;
      }

      .login-user {
        &:hover {
          cursor: pointer;
          color: @primary-color;
        }
        .user-avatar {
          width: 30px;
          height: 30px;
        }

        .username {
          font-size: 12px;
          font-weight: bold;
        }
      }
    }
  }
}

.search-result-item {
  display: block;
  padding: 4px 12px 4px 12px;
  overflow: hidden;
  text-overflow: ellipsis;

  width: 100%;
  color: rgba(0, 0, 0, 0.65);

  &:hover {
    color: @primary-color;
  }
}

.search-result-show-all {
  text-align: center;
  color: @link-color;
  font-size: 12px;
}

.left-drawer {
  .banner {
    text-align: center;
    padding: 20px 0 20px 0;

    .banner-logo {
      width: 30px;
      height: 30px;
      margin-right: 5px;
    }

    .banner-title {
      font-size: 18px;
      font-weight: bold;
      overflow: hidden;
    }
  }

  .drawer-menu {
    border: none;
  }
}
</style>
