<template>
  <div id="sider">
    <a-card :bordered="true" style="width: 300px">
      <div class="owner-info-wrapper">
        <div class="avatar">
          <img :src="ownerAvatarUrl" alt="ownerAvatar" />
        </div>
        <div class="username" style="font-weight: bold">
          {{ ownerUsername }}
        </div>
        <!-- eslint-disable -->
        <p class="bio" v-html="ownerBio"></p>
        <!-- eslint-enable -->
      </div>
      <div class="resources">
        <div class="resource">
          <div class="resource-name">文章</div>
          <div class="resource-count">{{ resourceCount.article }}</div>
        </div>
        <div class="resource">
          <div class="resource-name">分类</div>
          <div class="resource-count">{{ resourceCount.category }}</div>
        </div>
        <div class="resource">
          <div class="resource-name">标签</div>
          <div class="resource-count">{{ resourceCount.tag }}</div>
        </div>
      </div>
    </a-card>
    <br />
    <a-card
      size="small"
      :body-style="{ padding: '10px 10px 0 10px' }"
      style="width: 300px"
    >
      <template v-slot:title>
        <a-icon
          type="bars"
          style="font-size: 16px; padding: 0 8px 0 4px"
        ></a-icon
        ><span style="font-weight: bold; font-size: 16px">分类</span>
      </template>
      <ul class="category-list">
        <li v-for="category in categories" :key="'category-' + category.id">
          <nuxt-link
            :to="{
              name: 'category',
              query: { id: category.id },
            }"
          >
            <div class="category-item">
              <div class="category-name">{{ category.name }}</div>
              <div class="category-article-count">
                {{ category.articleCount }}
              </div>
            </div>
          </nuxt-link>
        </li>
      </ul>
    </a-card>
    <br />
    <a-card
      size="small"
      :body-style="{ padding: '10px 10px 0 10px' }"
      style="width: 300px"
    >
      <template v-slot:title>
        <a-icon
          type="tags"
          style="font-size: 16px; padding: 0 8px 0 4px"
        ></a-icon
        ><span style="font-size: 16px; font-weight: bold">标签</span>
      </template>
      <ul class="tag-list">
        <li
          v-for="tag in tags"
          :key="'tag-' + tag.id"
          class="tag-item"
          style="font-size: 16px"
        >
          <nuxt-link
            :to="{
              name: 'tag',
              query: { id: tag.id },
            }"
          >
            {{ tag.name }} ({{ tag.articleCount }})
          </nuxt-link>
        </li>
      </ul>
    </a-card>
    <br />
  </div>
</template>

<script>
export default {
  computed: {
    resourceCount() {
      return this.$store.state.app.resourceCount
    },
    categories() {
      return this.$store.state.app.categories
    },
    tags() {
      return this.$store.state.app.tags
    },
    ownerUsername() {
      return this.$store.state.app.ownerUsername
    },
    ownerAvatarUrl() {
      return this.$store.state.app.ownerAvatarUrl
    },
    ownerBio() {
      return this.$store.state.app.ownerBio
    },
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

#sider {
  width: 100%;
  height: 100%;
  min-height: 100%;
  transition: 0.3s;

  .owner-info-wrapper {
    text-align: center;
    margin-bottom: 24px;

    .avatar {
      margin: 0 auto 20px auto;
      width: 104px;
      height: 104px;
      border: @border-color-base solid 2px;
      border-radius: 50%;
      display: block;
      transition: all 1s;

      &:hover {
        transform: rotate(360deg);
      }

      img {
        height: 100%;
        width: 100%;
        border-radius: 50%;
      }
    }
    .username {
      color: rgba(0, 0, 0, 0.85);
      font-size: 20px;
      line-height: 28px;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .bio {
      text-align: center;
    }
  }

  .resources {
    display: flex;
    justify-content: center;
    height: 60px;
    .resource {
      flex: 1;
      display: flex;
      justify-content: center;
      align-items: center;

      flex-direction: column;
      text-align: center;

      .resource-name {
        font-weight: bold;
        font-size: 18px;
      }
      .resource-count {
        font-size: 16px;
      }
    }
  }

  .category-list {
    list-style: none;
    padding-left: 0;
    font-size: 16px;
    .category-item {
      padding: 6px 12px 6px 4px;

      .category-name {
        color: @text-color;

        padding-left: 8px;
        display: inline-block;
        font-weight: bold;
      }

      .category-article-count {
        float: right;
        display: inline-block;
        background: #999999;
        color: white;
        min-width: 26px;
        text-align: center;
      }

      &:hover {
        background: #d8e6df;
        cursor: pointer;

        .category-name {
          color: @primary-color;

          padding-left: 8px;
          display: inline-block;
          font-weight: bold;
        }

        .category-article-count {
          background: @primary-color;
        }
      }
    }
  }
  .tag-list {
    margin: 2px 2px 16px 2px;
    padding: 0;
    list-style: none;
    .tag-item {
      margin: 5px;
      display: inline-block;
      border: 1px solid @border-color-base;
      border-radius: 2px;
      a {
        padding: 2px 6px 2px 6px;
        color: @text-color;
      }

      &:hover {
        background: #d8e6df;

        a {
          color: @primary-color;
        }
      }
    }
  }
}
</style>
