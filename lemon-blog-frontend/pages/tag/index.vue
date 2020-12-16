<template>
  <content-layout>
    <template v-slot:content>
      <div class="boxed">
        <div id="tags" style="margin-bottom: 20px">
          <div
            v-for="tag in tags"
            :key="tag.id"
            :class="['tag', { 'tag-selected': tagSelected(tag) }]"
            @click="onSelectTag(tag)"
          >
            {{ tag.name }}({{ tag.articleCount }})
          </div>
        </div>
        <div>
          <a-empty v-if="!selectedTag">
            <span slot="description">请选择一个标签</span>
          </a-empty>
          <a-spin v-else :spinning="articlesLoading">
            <div style="margin-left: 10px; font-size: 18px">
              标签
              <span class="currentTag"> {{ selectedTag.name }} </span>
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
  name: 'Tag',
  components: { ContentLayout, ArticleList },
  async asyncData({ $axios, route, store }) {
    if (route.query.id) {
      const queryId = parseInt(route.query.id)
      const pageNum = route.query.pageNum ? parseInt(route.query.pageNum) : 1

      const selectedTag = store.state.app.tags.find((item) => {
        return item.id === queryId
      })

      const articlePage = await $axios.$get('/articles', {
        params: {
          tagIds: [queryId],
          pageNum,
        },
        paramsSerializer: (params) => {
          return qs.stringify(params)
        },
      })

      return {
        selectedTag,
        articles: articlePage.data,
        articlePaging: {
          total: articlePage.total,
          pageNum: articlePage.pageNum,
          pageSize: articlePage.pageSize,
        },
      }
    } else {
      return {
        selectedTag: null,
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
    tags() {
      return this.$store.state.app.tags
    },
  },

  created() {
    this.$store.commit('app/setCurrentNav', '标签')
  },

  methods: {
    tagSelected(tag) {
      return this.selectedTag && this.selectedTag.id === tag.id
    },
    onSelectTag(tag) {
      this.$router.push({
        name: 'tag',
        query: { id: tag.id },
      })
    },
    onPageChange(pageNum) {
      this.$router.push({
        name: 'tag',
        query: {
          id: this.selectedTag.id,
          pageNum,
        },
      })
    },
  },

  head() {
    return {
      title: '标签',
    }
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.tag {
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

.currentTag {
  color: @primary-color;
  font-weight: bold;
}
</style>
