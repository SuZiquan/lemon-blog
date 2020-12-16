<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <a-modal v-model="replyModalVisible" title="回复评论" ok-text="确认" cancel-text="取消" @ok="onReplyComment">
        <a-textarea v-model="replyContent" placeholder="请输入评论内容" :rows="4" />
      </a-modal>
      <a-modal v-model="auditModalVisible" title="审核评论" ok-text="确认" cancel-text="取消" @ok="onAuditComment">
        <div style="text-align: center">
          <a-radio-group v-model="auditStatus">
            <a-radio :value="1">
              通过
            </a-radio>
            <a-radio :value="2">
              不通过
            </a-radio>
          </a-radio-group>
        </div>
      </a-modal>

      <div style="padding-bottom: 20px; font-size: 20px">当前文章： {{ articleTitle }}</div>
      <a-button type="primary" @click="newCommentDialogVisible = true">
        新增评论
      </a-button>
      <a-modal v-model="newCommentDialogVisible" title="新增评论" ok-text="确认" cancel-text="取消" @ok="onCreateComment">
        <a-textarea v-model="newCommentValue" placeholder="请输入评论内容" :rows="4" />
      </a-modal>
      <a-table
        style="margin-top: 20px"
        bordered
        :expandedRowKeys="expandedRowKeys"
        :row-key="record => record.id"
        :loading="loading"
        :data-source="dataSource"
        :columns="columns"
        :pagination="pagination"
        :showTotal="true"
        :childrenColumnName="childrenColumnName"
        @change="onQueryChange"
      >
        <template slot="user" slot-scope="user">
          <span>
            <router-link :to="'/users/' + user.id "> {{ user.username }}</router-link>
          </span>
        </template>
        <template slot="replyToUser" slot-scope="replyToUser">
          <span>
            <router-link v-if="replyToUser" :to="'/users/' + replyToUser.id "> {{ replyToUser.username }}</router-link>
          </span>
        </template>
        <template slot="status" slot-scope="status">
          <span :class="getCommentStatusStyleClass(status)">
            {{ getCommentStatusText(status) }}
          </span>
        </template>
        <template slot="article" slot-scope="article">
          <router-link :to="'/articles/' + article.id "> {{ article.title }}</router-link>
        </template>
        <template slot="action" slot-scope="record">
          <span>
            <a-button type="primary" style="margin-right: 20px" @click="onOpenReplyModal(record)">回复</a-button>
            <a-button type="primary" style="margin-right: 20px" @click="onOpenAuditModal(record)">审核</a-button>

            <a-popconfirm
              title="确认删除该评论?"
              okText="确认"
              cancelText="取消"
              @confirm="() => removeComment(record.id)"
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

import { auditComment, commentStatus, createComment, deleteComment } from '@/api/comments'
import { getArticle, listArticleComments } from '@/api/articles'

const columns = [
  {
    title: '#',
    dataIndex: 'id',
    key: 'id'
  },
  {
    title: '用户',
    dataIndex: 'user',
    scopedSlots: { customRender: 'user' }
  },
  {
    title: '内容',
    dataIndex: 'content'
  },
  {
    title: '回复给',
    dataIndex: 'replyToUser',
    scopedSlots: { customRender: 'replyToUser' }
  },
  {
    title: '状态',
    key: 'status',
    dataIndex: 'status',
    scopedSlots: { customRender: 'status' }
  },
  {
    title: '时间',
    key: 'createdAt',
    dataIndex: 'createdAt'
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
      newCommentDialogVisible: false,
      newCommentValue: '',
      columns,
      expandedRowKeys: [],
      childrenColumnName: 'subComments',
      dataSource: [],
      loading: false,
      pagination: {
        current: 1,
        pageSize: 10
      },
      articleId: null,
      articleTitle: null,

      replyModalVisible: false,
      auditModalVisible: false,

      replyParentId: null,
      replyReplyToUser: null,
      replyContent: '',

      auditCommentId: null,
      auditStatus: 1

    }
  },
  mounted () {
    this.loadArticle()
    this.loadComments()
  },
  created () {
    this.articleId = this.$route.params.id
  },

  methods: {
    onQueryChange (pagination) {
      this.pagination = pagination
      this.loadComments()
    },
    loadArticle () {
      getArticle(this.articleId).then(data => {
        this.$nextTick(() => {
          this.articleTitle = data.title
        })
      })
    },
    loadComments () {
      this.loading = true
      listArticleComments(
        this.articleId,
        {
          pageNum: this.pagination.current,
          pageSize: this.pagination.pageSize
        }
      ).then(data => {
        const pagination = { ...this.pagination }
        pagination.total = data.total
        pagination.current = data.pageNum
        pagination.pageSize = data.pageSize
        pagination.showTotal = () => '共' + data.total + '条记录'
        this.dataSource = data.data
        this.pagination = pagination
        this.expandedRowKeys = data.data.map(value => {
          return value.id
        })
      }).finally(() => {
        this.loading = false
      })
    },

    getCommentStatusStyleClass (statusCode) {
      if (statusCode === 1) {
        return 'audited'
      } else if (statusCode === 2) {
        return 'rejected'
      }
      return 'unaudited'
    },

    getCommentStatusText (statusCode) {
      return commentStatus[statusCode]
    },

    onOpenReplyModal (comment) {
      this.replyReplyToUser = comment.user
      if (comment.parentId) {
        this.replyParentId = comment.parentId
      } else {
        this.replyParentId = comment.id
      }
      this.replyModalVisible = true
    },

    onReplyComment () {
      const params = {
        content: this.replyContent,
        article: { id: this.articleId },
        parentId: this.replyParentId,
        replyToUser: this.replyReplyToUser
      }
      createComment(params)
        .then(() => {
            this.replyModalVisible = false
            this.replyContent = ''
            this.$message.success('回复成功')
            this.loadComments()
          }
        )
    },

    onOpenAuditModal (comment) {
      this.auditCommentId = comment.id
      this.auditModalVisible = true
    },

    onAuditComment () {
      auditComment(this.auditCommentId, this.auditStatus)
        .then(() => {
            this.auditModalVisible = false
            this.$message.success('审核成功')
            this.loadComments()
          }
        )
    },

    onCreateComment () {
      const params = {
        content: this.newCommentValue,
        article: { id: this.articleId }
      }
      createComment(params)
        .then(() => {
            this.newCommentDialogVisible = false
            this.newCommentValue = ''
            this.$message.success('评论成功')
            this.loadComments()
          }
        )
    },
    removeComment (commentId) {
      deleteComment(commentId).then(() => {
        this.loadComments()
        this.$message.success('删除成功')
      })
    }
  }

}
</script>
<style scoped lang="less">
@import "~ant-design-vue/es/style/themes/default.less";

.unaudited {
  color: @warning-color;
}

.audited {
  color: @success-color;
}

.rejected {
  color: @error-color;
}

</style>
