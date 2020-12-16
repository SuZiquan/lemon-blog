<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <div>
        <a-button type="primary">
          <router-link to="/articles/new">新建文章</router-link>
        </a-button>
      </div>

      <div style="margin: 20px 20px 0 0;">
        <span style="margin-right: 10px">标题</span>
        <a-input style="width: 400px" v-model="titleInputValue"></a-input>
        <a-button type="primary" style="margin-left: 10px" @click="onSearch"> 搜索</a-button>
        <a-button type="normal" style="margin-left: 10px" @click="onSearchReset"> 重置</a-button>
      </div>

      <a-table
        style="margin-top: 20px"
        bordered
        :row-key="record => record.id"
        :loading="loading"
        :data-source="dataSource"
        :columns="columns"
        :pagination="pagination"
        :showTotal="true"
        @change="onQueryChange"
      >
        <template slot="article_title" slot-scope="title">
          <span>
            <a> {{ title }}</a>
          </span>
        </template>
        <template slot="tags" slot-scope="tags">
          <a-tag
            v-for="item in tags"
            :key="item.id">
            {{ item.name }}
          </a-tag>
        </template>
        <template slot="categories" slot-scope="categories">
          <a-tag
            v-for="item in categories"
            :key="item.id">
            {{ item.name }}
          </a-tag>
        </template>
        <template slot="status" slot-scope="status">
          {{ getPageStatusText(status) }}
        </template>
        <template slot="action" slot-scope="record">
          <span>
            <a-button type="primary" style="margin-right: 20px" @click="manageComment(record.id)">管理评论</a-button>

            <a-button type="primary" style="margin-right: 20px" @click="editPage(record.id)">编辑</a-button>

            <a-popconfirm
              title="确认删除该文章?"
              okText="确认"
              cancelText="取消"
              @confirm="() => removeArticle(record.id)"
            >
              <a-button type="danger">删除</a-button>
            </a-popconfirm>
          </span>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script>

import { articleStatus, deleteArticle, listArticles } from '@/api/articles'

const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '标题',
    dataIndex: 'title',
    scopedSlots: { customRender: 'article_title' }
  },
  {
    title: '分类',
    dataIndex: 'categories',
    scopedSlots: { customRender: 'categories' }
  },
  {
    title: '标签',
    dataIndex: 'tags',
    scopedSlots: { customRender: 'tags' }
  },
  {
    title: '状态',
    dataIndex: 'status',
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '创建时间',
    dataIndex: 'createdAt'
  },
  {
    title: '更新时间',
    dataIndex: 'updatedAt'
  },
  {
    title: '操作',
    key: 'action',
    scopedSlots: { customRender: 'action' }
  }
]

export default {
  data () {
    return {
      columns,
      dataSource: [],
      loading: false,
      pagination: {
        current: 1,
        pageSize: 10
      },
      titleInputValue: '',
      searchCondition: {
      }
    }
  },
  mounted () {
    this.loadArticles()
  },

  methods: {
    onSearch () {
      this.searchCondition.title = this.titleInputValue
      this.loadArticles()
    },

    onSearchReset () {
      this.titleInputValue = ''
      this.searchCondition = {}
      this.loadArticles()
    },

    onQueryChange (pagination) {
      this.pagination = pagination
      this.loadArticles()
    },

    loadArticles () {
      this.loading = true
      listArticles(
        {
          pageNum: this.pagination.current,
          pageSize: this.pagination.pageSize,
          title: this.searchCondition.title
        }, {}
      ).then(data => {
        this.loading = false
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.pageNum
        pagination.pageSize = data.pageSize
        pagination.showTotal = () => '共' + data.total + '条记录'
        this.loading = false
        this.dataSource = data.data
        this.pagination = pagination
      })
    },

    getPageStatusText (statusCode) {
      return articleStatus[statusCode]
    },
    editPage (articleId) {
      this.$router.push({
        path: `/articles/edit/${articleId}`
      })
    },
    manageComment (articleId) {
      this.$router.push({
        path: `/articles/${articleId}/comments`
      })
    },
    removeArticle (articleId) {
      deleteArticle(articleId).then(() => {
        this.loadArticles()
        this.$message.success('删除成功')
      })
    }
  }

}
</script>
<style>
</style>
