<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">

      <div style="margin: 20px 20px 0 0;">
        <span style="margin-right: 10px; ">用户名</span>
        <a-input style="width: 200px" v-model="usernameInputValue"></a-input>

        <span style="margin-left: 20px; margin-right: 10px">邮箱</span>
        <a-input style="width: 200px" v-model="emailInputValue"></a-input>

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
        <template slot="avatar" slot-scope="avatarUrl">
          <img style="width: 40px; height: 40px; margin: -16px 0;" :src="avatarUrl" :alt="avatarUrl">
        </template>

        <template slot="username" slot-scope="username, record">
          <span>
            <router-link :to="'/users/' + record.id "> {{ username }}</router-link>
          </span>
        </template>

        <template slot="email" slot-scope="email">
          <span>
            <a :href="'mailto:' + email">{{ email }}</a>
          </span>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script>

import { listUsers } from '@/api/users'

const columns = [
  {
    title: '#',
    dataIndex: 'id'
  },
  {
    title: '头像',
    dataIndex: 'avatarUrl',
    scopedSlots: { customRender: 'avatar' },
    width: '120px'
  },
  {
    title: '用户名',
    dataIndex: 'username',
    scopedSlots: { customRender: 'username' }
  },
  {
    title: '第三方登录',
    dataIndex: 'socialSource'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    scopedSlots: { customRender: 'email' }
  },
  {
    title: '上次登录',
    dataIndex: 'lastLogin'
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
      usernameInputValue: '',
      emailInputValue: '',
      searchCondition: {}
    }
  },
  mounted () {
    this.loadUsers()
  },

  methods: {
    onSearch () {
      this.searchCondition.username = this.usernameInputValue
      this.searchCondition.email = this.emailInputValue
      this.loadUsers()
    },

    onSearchReset () {
      this.usernameInputValue = ''
      this.emailInputValue = ''
      this.searchCondition = {}
      this.loadUsers()
    },

    onQueryChange (pagination) {
      this.pagination = pagination
      this.loadUsers()
    },

    loadUsers () {
      this.loading = true
      listUsers(
        {
          pageNum: this.pagination.current,
          pageSize: this.pagination.pageSize,
          username: this.usernameInputValue,
          email: this.emailInputValue
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
    }
  }

}
</script>
<style>
</style>
