<template>
  <content-layout>
    <template v-slot:content>
      <div class="boxed">
        <div>
          <div style="margin-top: 10px; margin-left: 10px; font-size: 18px">
            包含
            <span class="queryString"> {{ queryString }} </span>
            的文章
          </div>
          <a-divider style="margin: 16px 0 8px 0; padding: 0"></a-divider>
          <article-list
            :articles="articles"
            :paging="articlePaging"
            :on-page-change="onPageChange"
          >
          </article-list>
        </div>
      </div>
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'
import ArticleList from '@/components/article/ArticleList'

export default {
  name: 'Search',
  components: { ContentLayout, ArticleList },
  async asyncData({ $axios, route, store }) {
    if (route.query.queryString) {
      const queryString = route.query.queryString
      const pageNum = route.query.pageNum ? parseInt(route.query.pageNum) : 1

      const articlePage = await $axios.$get('/articles/search', {
        params: {
          queryString: route.query.queryString,
          pageNum,
        },
      })

      return {
        queryString,
        articles: articlePage.data,
        articlePaging: {
          total: articlePage.total,
          pageNum: articlePage.pageNum,
          pageSize: articlePage.pageSize,
        },
      }
    } else {
      return {}
    }
  },
  watchQuery: ['queryString', 'pageNum'],

  validate({ query }) {
    return query.queryString
  },

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

  created() {
    this.$store.commit('app/setCurrentNav', '主页')
  },

  methods: {
    onPageChange(pageNum) {
      this.$router.push({
        name: 'search',
        query: {
          id: this.queryString,
          pageNum,
        },
      })
    },
  },

  head() {
    return {
      title: this.queryString,
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.queryString {
  color: @primary-color;
  font-weight: bold;
}
</style>
