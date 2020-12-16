<template>
  <content-layout>
    <template v-slot:content>
      <div class="boxed archive-wrapper">
        <div v-for="date in dateList" :key="date" class="archive-group">
          <h1 class="group-title">{{ date }}</h1>
          <a-divider></a-divider>
          <div>
            <nuxt-link
              v-for="article in articleDateMap[date]"
              :key="article.id"
              :to="'/article/' + article.id"
            >
              <div class="archive-item">
                [{{ article.date }}]
                {{ article.title }}
              </div>
            </nuxt-link>
          </div>
        </div>
      </div>
    </template>
  </content-layout>
</template>

<script>
import ContentLayout from '@/components/common/ContentLayout'

import moment from 'moment'

export default {
  name: 'Archive',
  components: { ContentLayout },
  async asyncData({ $axios }) {
    const articles = await $axios.$get('/articles/all')
    const dateList = []
    const articleDateMap = {}

    for (const article of articles) {
      const date = moment(article.createdAt).format('YYYY年MM月')
      article.date = moment(article.createdAt).format('MM/DD')
      if (!dateList.includes(date)) {
        dateList.push(date)
      }
      if (!articleDateMap[date]) {
        articleDateMap[date] = []
      }
      articleDateMap[date].push(article)
    }
    return {
      dateList,
      articleDateMap,
    }
  },

  created() {
    this.$store.commit('app/setCurrentNav', '归档')
  },
  head() {
    return {
      title: '归档',
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.archive-wrapper {
  padding: 20px 30px 0 30px;

  .archive-group {
    margin-bottom: 30px;
    .group-title {
      color: @primary-color;
      font-size: 20px;
      font-weight: bold;
    }

    .ant-divider,
    .ant-divider-horizontal {
      padding: 0;
      margin: 0;
    }

    .archive-item {
      color: @text-color;

      margin-top: 6px;
      font-size: 16px;
      font-weight: bold;

      &:hover {
        cursor: pointer;
        color: @link-color;
      }
    }
  }
}
</style>
