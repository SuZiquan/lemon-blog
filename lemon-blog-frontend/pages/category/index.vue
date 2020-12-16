<template>
  <content-layout>
    <template v-slot:content>
      <div class="boxed">
        <div id="categories" style="margin-bottom: 20px">
          <div
            v-for="category in categories"
            :key="category.id"
            :class="[
              'category',
              { 'category-selected': categorySelected(category) },
            ]"
            @click="onSelectCategory(category)"
          >
            {{ category.name }} ({{ category.articleCount }})
          </div>
        </div>
        <div>
          <a-empty v-if="!selectedCategory">
            <span slot="description">请选择一个分类</span>
          </a-empty>
          <a-spin v-else :spinning="articlesLoading">
            <div style="margin-left: 10px; font-size: 18px">
              分类
              <span class="currentCategory"> {{ selectedCategory.name }} </span>
              下的文章
            </div>
            <a-divider style="margin: 16px 0 8px 0; padding: 0"></a-divider>
            <article-list
              :articles="articles"
              :paging="articlePaging"
              :on-page-change="onPageChange"
            >
            </article-list>
          </a-spin>
        </div>
      </div>
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'
import ArticleList from '@/components/article/ArticleList'

import qs from 'qs'

export default {
  name: 'Category',
  components: { ContentLayout, ArticleList },
  async asyncData({ $axios, route, store }) {
    if (route.query.id) {
      const queryId = parseInt(route.query.id)
      const pageNum = route.query.pageNum ? parseInt(route.query.pageNum) : 1

      const selectedCategory = store.state.app.categories.find((item) => {
        return item.id === queryId
      })

      const articlePage = await $axios.$get('/articles', {
        params: {
          categoryIds: [queryId],
          pageNum,
        },
        paramsSerializer: (params) => {
          return qs.stringify(params)
        },
      })

      return {
        selectedCategory,
        articles: articlePage.data,
        articlePaging: {
          total: articlePage.total,
          pageNum: articlePage.pageNum,
          pageSize: articlePage.pageSize,
        },
      }
    } else {
      return {
        selectedCategory: null,
      }
    }
  },
  watchQuery: ['id', 'pageNum'],

  data() {
    return {
      articlesLoading: false,
      articles: [],
      articlePaging: {
        pageNum: 1,
        pageSize: 10,
        total: 10,
      },
    }
  },

  computed: {
    categories() {
      return this.$store.state.app.categories
    },
  },

  created() {
    this.$store.commit('app/setCurrentNav', '分类')
  },

  methods: {
    categorySelected(category) {
      return this.selectedCategory && this.selectedCategory.id === category.id
    },
    onSelectCategory(category) {
      this.$router.push({
        name: 'category',
        query: { id: category.id },
      })
    },
    onPageChange(pageNum) {
      this.$router.push({
        name: 'category',
        query: {
          id: this.selectedCategory.id,
          pageNum,
        },
      })
    },
  },

  head() {
    return {
      title: `分类`,
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.category {
  display: inline-block;
  border: 1px solid @border-color-base;
  border-radius: 3px;
  text-align: center;
  font-size: 16px;
  padding: 5px 10px 5px 10px;
  margin: 5px;

  &:hover,
  &-selected {
    background: @primary-color;
    color: white;
    cursor: pointer;
    border: 1px solid transparent;
  }
}

.currentCategory {
  color: @primary-color;
  font-weight: bold;
}
</style>
