<template>
  <content-layout>
    <template v-slot:content>
      <div class="boxed">
        <article-list
          :articles="articles"
          :paging="paging"
          :on-page-change="onPageChange"
        >
        </article-list>
      </div>
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'
import ArticleList from '@/components/article/ArticleList'

export default {
  name: 'Article',
  watchQuery(newQuery) {
    return newQuery.pageNum
  },
  components: { ArticleList, ContentLayout },
  async asyncData({ $axios }) {
    const articlePage = await $axios.$get('/articles')
    return {
      paging: {
        total: articlePage.total,
        pageNum: articlePage.pageNum,
        pageSize: articlePage.pageSize,
      },
      articles: articlePage.data,
    }
  },
  created() {
    this.$store.commit('app/setCurrentNav', '主页')
  },
  methods: {
    onPageChange(pageNum) {
      this.$router.push({
        name: 'article',
        query: { pageNum },
      })
    },
  },
  head() {
    return {
      title: this.$store.state.app.indexTitle,
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';
</style>
