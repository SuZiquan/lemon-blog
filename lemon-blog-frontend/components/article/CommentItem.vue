<template>
  <div>
    <div>
      <a-avatar
        v-if="comment.user.avatarUrl"
        style="width: 40px; height: 40px"
        :src="comment.user.avatarUrl"
      />
      <a-avatar v-else style="width: 40px; height: 40px" :src="ownerAvatarUrl">
      </a-avatar>
      <a>{{ comment.user.username }}</a>
    </div>
    <div style="padding-top: 10px; margin-bottom: 10px">
      <p v-if="comment.status === 1" style="margin-bottom: 0">
        <span v-if="comment.replyToUser">
          回复<a>{{ comment.replyToUser.username }}</a
          >：
        </span>
        {{ comment.content }}
      </p>
      <div v-else-if="userInfo && userInfo.id === comment.user.id">
        <div>
          <span style="background: #e8e8e8; padding: 0 4px 0 4px">
            <i>评论待审核</i>
          </span>
        </div>
        <span v-if="comment.replyToUser">
          回复<a>{{ comment.replyToUser.username }}</a
          >：
        </span>
        {{ comment.content }}
      </div>
      <span v-else style="background: #e8e8e8; padding: 0 4px 0 4px">
        <i>评论待审核后显示</i>
      </span>
    </div>
    <div style="height: 20px">
      <span style="color: grey">{{ comment.createdAt }}</span>
      <span v-if="logined && !replyInputVisible" style="float: right">
        <a @click="replyInputVisible = true">回复</a>
        <a-popconfirm
          title="确认删除该评论？"
          ok-text="确认"
          cancel-text="取消"
          @confirm="onDeleteComment"
        >
          <a
            v-if="userInfo && userInfo.id === comment.user.id"
            style="color: red; margin-left: 10px"
            >删除</a
          >
        </a-popconfirm>
      </span>
    </div>
    <div v-if="replyInputVisible" style="padding: 10px 0 0 0">
      <a-textarea
        v-model="newCommentContent"
        placeholder="请输入评论"
        :rows="4"
      />
      <div style="text-align: right; margin-top: 10px">
        <a-button type="primary" @click="onCreateComment(comment)"
          >评论
        </a-button>
        <a-button @click="replyInputVisible = false">取消</a-button>
      </div>
    </div>
    <a-divider style="padding: 0; margin: 10px 0 10px 0"></a-divider>
    <slot name="subComments"></slot>
  </div>
</template>

<script>
export default {
  name: 'CommentItem',
  props: {
    articleId: {
      type: Number,
      default: null,
    },

    parentId: {
      type: Number,
      default: null,
    },

    comment: {
      type: Object,
      default: () => {
        return {}
      },
    },
    createNewComment: {
      type: Function,
      default: () => {},
    },

    deleteComment: {
      type: Function,
      default: () => {},
    },
  },
  data() {
    return {
      newCommentContent: null,
      replyInputVisible: false,
    }
  },
  computed: {
    logined() {
      return this.$store.getters['auth/logined']
    },
    userInfo() {
      return this.$store.state.auth.userInfo
    },
    ownerAvatarUrl() {
      return this.$store.state.app.ownerAvatarUrl
    },
  },
  methods: {
    onDeleteComment() {
      this.deleteComment(this.comment.id)
    },
    onCreateComment() {
      this.createNewComment(
        {
          article: {
            id: this.articleId,
          },
          content: this.newCommentContent,
          parentId: this.parentId,
          replyToUser: {
            id: this.comment.user.id,
          },
        },
        () => {
          this.newCommentContent = null
          this.replyInputVisible = false
        }
      )
    },
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';
</style>
