<template>
  <div class="comment-wrapper">
    <login-modal ref="loginModal"></login-modal>
    <span>
      <span style="font-size: 20px; margin-right: 10px">评论</span>
      <span v-if="!logined" style="font-size: 10px"
        ><a @click="openLoginModal">登录</a>后评论</span
      >
    </span>
    <div v-if="logined" style="padding: 10px 0 0 0">
      <a-textarea
        v-model="newCommentContent"
        placeholder="请输入评论"
        :rows="4"
      />
      <div style="text-align: right; margin-top: 10px">
        <a-button type="primary" @click="onCreateComment">评论</a-button>
      </div>
    </div>

    <a-divider style="padding: 0; margin: 10px 0 10px 0"></a-divider>
    <div>
      <a-spin :spinning="loading">
        <a-empty v-if="comments.length === 0">
          <span slot="description">没有评论</span>
        </a-empty>
        <div v-for="comment in comments" v-else :key="'comment-' + comment.id">
          <comment-item
            :article-id="articleId"
            :comment="comment"
            :parent-id="comment.id"
            :create-new-comment="createNewComment"
            :delete-comment="deleteComment"
          >
            <template v-if="comment.subComments" v-slot:subComments>
              <div
                v-for="subComment in comment.subComments"
                :key="'comment-' + subComment.id"
                style="padding-left: 40px"
              >
                <comment-item
                  :article-id="articleId"
                  :comment="subComment"
                  :parent-id="comment.id"
                  :create-new-comment="createNewComment"
                  :delete-comment="deleteComment"
                ></comment-item>
              </div>
            </template>
          </comment-item>
        </div>
      </a-spin>
    </div>

    <div>
      <a-pagination
        v-if="comments.length > 0"
        :default-current="paging.pageNum"
        :page-size="paging.pageSize"
        :total="paging.total"
        @change="onChange"
      />
    </div>
  </div>
</template>

<script>
import CommentItem from '@/components/article/CommentItem'
import LoginModal from '@/components/common/LoginModal'

export default {
  name: 'CommentList',
  components: {
    LoginModal,
    CommentItem,
  },
  props: {
    articleId: {
      type: Number,
      default: null,
    },
  },

  data() {
    return {
      loading: false,
      newCommentContent: null,
      comments: [],
      paging: {
        pageNum: 1,
        pageSize: 10,
        total: 10,
      },
    }
  },

  computed: {
    logined() {
      return this.$store.getters['auth/logined']
    },
  },
  mounted() {
    this.loadComments()
  },
  methods: {
    onCreateComment() {
      this.createNewComment(
        {
          article: {
            id: this.articleId,
          },
          content: this.newCommentContent,
        },
        () => {
          this.newCommentContent = null
        }
      )
    },

    createNewComment(comment, successCallback) {
      if (!comment.content) {
        this.$message.error('评论内容不能为空')
        return
      }

      this.$axios
        .post('/comments', comment)
        .then(() => {
          this.$message.success('评论成功')
          this.loadComments()
          if (successCallback) {
            successCallback()
          }
        })
        .catch((error) => {
          this.$message.error('评论失败: ' + error.data.message)
        })
    },

    deleteComment(commentId) {
      this.$axios
        .delete('/comments/' + commentId)
        .then(() => {
          this.$message.success('删除成功')
          this.loadComments()
        })
        .catch((error) => {
          this.$message.error('删除失败: ' + error.data.message)
        })
    },

    openLoginModal() {
      this.$refs.loginModal.show()
    },

    async loadComments() {
      this.loading = true
      const commentPage = await this.$axios.$get(
        '/articles/' + this.articleId + '/comments',
        {
          params: {
            pageNum: this.paging.pageNum,
          },
        }
      )

      this.comments = commentPage.data

      this.paging = {
        total: commentPage.total,
        pageNum: commentPage.pageNum,
        pageSize: commentPage.pageSize,
      }

      this.loading = false
    },
    onChange(pageNum) {
      this.paging.pageNum = pageNum
      this.loadComments()
    },
  },
}
</script>

<style scoped lang="less">
.comment-wrapper {
  padding: 20px 28px 20px 28px;
  background: white;
  border: 1px solid rgb(232, 232, 232);
}
</style>
