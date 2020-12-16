<template>
  <div class="article-list-wrapper">
    <a-empty v-if="articles.length === 0">
      <span slot="description">没有数据</span>
    </a-empty>
    <div v-for="article in articles" v-else :key="'article-' + article.id">
      <article-item :article="article"></article-item>
      <a-divider style="margin: 0; padding: 0"></a-divider>
    </div>
    <div>
      <a-pagination
        v-if="articles.length !== 0"
        class="pagination"
        :default-current="paging.pageNum"
        :page-size="paging.pageSize"
        :total="paging.total"
        :show-total="
          (total, range) =>
            `第${range[0]} - ${range[1]} 条，共 ${paging.total} 条`
        "
        @change="onPaginationChange"
      />
    </div>
  </div>
</template>

<script>
import ArticleItem from '@/components/article/ArticleItem'

export default {
  name: 'ArticleList',
  components: {
    ArticleItem,
  },
  props: {
    articles: {
      type: Array,
      default: () => [],
    },
    paging: {
      type: Object,
      default: () => {
        return {
          pageNum: 1,
          pageSize: 10,
          total: 10,
        }
      },
    },
    onPageChange: {
      type: Function,
      default: () => {},
    },
  },
  methods: {
    onPaginationChange(pageNum, pageSize) {
      this.onPageChange(pageNum, pageSize)
    },
  },
}
</script>

<style scoped lang="less">
.article-list-wrapper {
  padding: 0 16px 10px 16px;
  .pagination {
    margin-top: 16px;
  }
}
</style>
