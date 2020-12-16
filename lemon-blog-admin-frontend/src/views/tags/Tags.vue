<template>
  <div>
    <a-card
      :body-style="{padding: '24px 32px'}"
      :bordered="false">

      <a-row>
        <a-col :span="4">
          <a-input placeholder="新标签名" v-model="newTagName" />
        </a-col>
        <a-col :span="4">
          <a-button type="primary" @click="onCreateTag(newTagName)" style="margin-left: 10px;">创建</a-button>
        </a-col>
      </a-row>

      <div style="padding-top: 10px;">
        <span>
          共 {{ tags.length }} 个标签
        </span>
      </div>

      <div style="padding-top: 20px;">
        <span
          v-for="item in tags"
          :key="item.id"
          class="tag-item"
        >
          <span style="font-size: 16px; display: inline-block; margin-bottom: 20px;">  <span
                                                                                         class="tag-link"> {{ item.name }}({{ item.articleCount }}) </span>
            <a-popconfirm
              title="确认删除该标签?"
              @confirm="() => onDeleteTag(item.id)"
              okText="确定"
              cancelText="取消"
            >
              <span v-if="item.articleCount === 0">
                <a href="javascript:;" class="delete-tag-link"><a-icon type="close" /></a>
              </span>
            </a-popconfirm>

          </span>
        </span>
      </div>

    </a-card>
  </div>
</template>

<script>

import { listTags, createTag, deleteTag } from '@/api/tags'

export default {
  name: 'Tags',
  components: {},

  data () {
    return {
      newTagName: '',
      tags: []
    }
  },

  mounted () {
    this.onListTag()
  },

  methods: {
    onListTag () {
      listTags().then(data => {
        this.tags = data
      })
    },
    onDeleteTag (idToDelete) {
      deleteTag(idToDelete).then(() => {
        this.tags = this.tags.filter(({ id }) => id !== idToDelete)
      })
    },
    onCreateTag (name) {
      createTag({ name }).then(() => {
        this.onListTag()
      })
    }
  }
}
</script>

<style lang="less" scoped>

@import "../../../node_modules/ant-design-vue/es/style/themes/default.less";

.extra-wrapper {
  line-height: 55px;
  padding-right: 24px;

  .extra-item {
    display: inline-block;
    margin-right: 24px;

    a {
      margin-left: 24px;
    }
  }
}

.antd-pro-pages-dashboard-analysis-twoColLayout {
  position: relative;
  display: flex;
  display: block;
  flex-flow: row wrap;
}

.antd-pro-pages-dashboard-analysis-salesCard {
  height: calc(100% - 24px);

  /deep/ .ant-card-head {
    position: relative;
  }
}

.dashboard-analysis-iconGroup {
  i {
    margin-left: 16px;
    color: rgba(0, 0, 0, .45);
    cursor: pointer;
    transition: color .32s;
    color: black;
  }
}

.analysis-salesTypeRadio {
  position: absolute;
  right: 54px;
  bottom: 12px;
}

.tag-item {
  background: @primary-color;
  line-height: 30px;
  padding: 10px;
  margin-right: 20px;
  color: white;
  border-radius: 5px}

.tag-link {
  &:hover {
    text-decoration: underline white;
    cursor: pointer;
  }
}

.delete-tag-link {
  color: white;

  &:hover {
    font-weight: bolder;
  }
}
</style>
