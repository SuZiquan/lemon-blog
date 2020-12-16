<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <a-button type="primary">
        <router-link to="/pages/new">新建页面</router-link>
      </a-button>
      <a-table
        style="margin-top: 20px"
        bordered
        :row-key="record => record.id"
        :loading="loading"
        :data-source="dataSource"
        :columns="columns"
        :pagination="false">
        <template slot="name" slot-scope="text">
          <a>{{ text }}</a>
        </template>
        <template slot="icon" slot-scope="icon">
          <a-icon :type="icon" ></a-icon>
        </template>
        <template slot="status" slot-scope="status">
          {{ getPageStatusText(status) }}
        </template>
        <template slot="action" slot-scope="record">
          <span>
            <a-button type="primary" style="margin-right: 20px" @click="editPage(record.id)">编辑</a-button>
            <a-popconfirm
              title="确认删除该页面?"
              okText="确认"
              cancelText="取消"
              @confirm="() => removePage(record.id)"
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

import { pageStatus, listPages, deletePage } from '@/api/pages'

const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '名称',
    dataIndex: 'name',
    scopedSlots: { customRender: 'name' }
  },
  {
    title: '图标',
    dataIndex: 'icon',
    scopedSlots: { customRender: 'icon' }
  },
  {
    title: '链接',
    dataIndex: 'link'
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
    scopedSlots: { customRender: 'action' },
    width: '260px'
  }
]

export default {
  data () {
    return {
      columns,
      dataSource: [],
      loading: false
    }
  },
  mounted () {
    this.loading = true
    listPages().then(data => {
       this.dataSource = data
       this.loading = false
    })
  },

  methods: {
    getPageStatusText (statusCode) {
      return pageStatus[statusCode]
    },
    editPage (pageId) {
      this.$router.push({
        path: `/pages/edit/${pageId}`
      })
    },
    removePage (pageId) {
      const that = this
      deletePage(pageId).then(() => {
        const dataSource = [...that.dataSource]
        that.dataSource = dataSource.filter(item => item.id !== pageId)
        that.$message.success('删除成功')
      })
    }
  }

}
</script>
<style>
</style>
