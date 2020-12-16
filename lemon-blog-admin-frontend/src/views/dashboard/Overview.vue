<template>
  <div>
    <a-row :gutter="24">
      <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <a-card
          :loading="loading"
          style="margin-bottom: 24px;"
          :bordered="false"
          title="站点概览"
          :body-style="{ padding: 0 }">
          <div>
            <a-col
              v-for="item in resources"
              :key="item.id"
              :xl="4"
              :lg="6"
              :md="6"
              :sm="6"
              :xs="6">
              <a class="resource-count-item" :href="item.link">
                <div class="resource-name">{{ item.name }}</div>
                <div class="resource-count">{{ resourceCount[item.id] ? resourceCount[item.id] : 0 }}</div>
              </a>
            </a-col>
          </div>
        </a-card>

        <a-card :loading="loading" title="动态" :bordered="false">
          <div v-if="unauditedCommentCount !== 0"><span style="font-size: 16px;">有{{ unauditedCommentCount
                                                  }}条未审核评论</span>
            <a style="margin-left: 10px" @click="onViewUnauditedComment">查看</a></div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import { PageHeaderWrapper } from '@ant-design-vue/pro-layout'
import { getOverview } from '@/api/overview'
import { countComment } from '@/api/comments'

export default {
  name: 'Overview',
  components: {
    PageHeaderWrapper
  },
  data () {
    return {
      loading: false,
      resources: [
        {
          id: 'article',
          name: '文章',
          link: '/articles/list'
        },
        {
          id: 'comment',
          name: '评论',
          link: '/comments'
        },
        {
          id: 'category',
          name: '分类',
          link: '/categories'
        },
        {
          id: 'tag',
          name: '标签',
          link: '/tags'
        }
      ],
      resourceCount: {},
      unauditedCommentCount: 0
    }
  },
  mounted () {
    this.loading = true

    Promise.all([getOverview(), countComment({ status: 0 })])
      .then(data => {
        this.resourceCount = data[0].resourceCount
        this.unauditedCommentCount = data[1]
      }).finally(() => {
      this.loading = false
    })
  },
  methods: {
    onViewUnauditedComment () {
      this.$router.push(
        { name: 'Comments', params: { status: '0' } }
      )
    }
  }
}
</script>

<style lang="less" scoped>
@import '../../../node_modules/ant-design-vue/es/style/themes/default.less';

.resource-count-item {
  display: block;
  padding-top: 20px;
  padding-bottom: 40px;
  color: @text-color;

  .resource-name {
    font-size: 20px;
    text-align: center;
    padding-bottom: 10px;
  }

  .resource-count {
    font-size: 20px;
    text-align: center;
  }

  &:hover {
    color: @primary-color;
  }

}

</style>
