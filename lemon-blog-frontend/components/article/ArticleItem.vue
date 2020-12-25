<template>
  <div class="article-wrapper">
    <nuxt-link :to="'/article/' + article.id">
      <div class="title">{{ article.title }}</div>
    </nuxt-link>
    <div class="properties">
      <span class="mobile-block">
        <span class="property-name">发布于：</span>
        {{ article.createdAt }}</span
      >
      <span
        v-if="article.categories && article.categories.length > 0"
        class="mobile-block"
      >
        <a-divider class="hidden-when-mobile" type="vertical"></a-divider>
        <span class="property-name"> 分类： </span>

        <nuxt-link
          v-for="category in article.categories"
          :key="'category-' + category.id"
          class="category-item"
          :to="{ name: 'category', query: { id: category.id } }"
        >
          {{ category.name }}
        </nuxt-link>
      </span>

      <span v-if="article.tags && article.tags.length > 0" class="mobile-block">
        <a-divider class="hidden-when-mobile" type="vertical"></a-divider>
        <span class="property-name"> 标签： </span>
        <nuxt-link
          v-for="tag in article.tags"
          :key="'tag-' + tag.id"
          class="tag-item"
          :to="{ name: 'tag', query: { id: tag.id } }"
        >
          {{ tag.name }}
        </nuxt-link>
      </span>
    </div>

    <p class="description">{{ article.description }}</p>
    <div class="read-more">
      <nuxt-link :to="'/article/' + article.id"> 阅读全文> </nuxt-link>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArticleItem',
  props: {
    article: {
      type: Object,
      default: () => {
        return {}
      },
    },
  },
  computed: {
    isMobile() {
      return this.$store.state.app.isMobile
    },
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.article-wrapper {
  padding: 16px 0 16px 0;

  .property-name {
    font-weight: bold;
  }

  .title {
    font-size: 18px;
    font-weight: bold;
    color: rgba(0, 0, 0, 0.65);

    &:hover {
      cursor: pointer;
      color: @link-color;
    }
  }

  .properties {
    padding-top: 5px;

    .category-item,
    .tag-item {
      margin: 0 2px 0 2px;
      padding: 0 6px 0 6px;
      font-size: 14px;
      display: inline-block;
      border: 1px solid @border-color-base;
      border-radius: 2px;
      color: @text-color;

      &:hover {
        background: #d8e6df;
        color: @primary-color;
      }
    }
  }

  .description {
    margin: 5px 0 5px 0;

    color: @text-color;
  }

  .read-more {
    height: 20px;
    a {
      float: right;
    }
  }
}
</style>
