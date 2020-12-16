<template>
  <div>
    <a-card
      :body-style="{padding: '24px 32px'}"
      :bordered="false">

      <a-row>
        <a-col :span="4">
          <a-input placeholder="新分类名" v-model="newCategoryName" />
        </a-col>
        <a-col :span="4">
          <a-button type="primary" @click="onCreateCategory(newCategoryName)" style="margin-left: 10px;">创建</a-button>
        </a-col>
      </a-row>

      <div style="padding-top: 10px;">
        <span>
          共 {{ categories.length }} 个分类
        </span>
      </div>

      <div style="padding-top: 20px;">
        <span
          v-for="item in categories"
          :key="item.id"
          class="category-item"
        >
          <span style="font-size: 16px; display: inline-block; margin-bottom: 20px;">  <span
                                                                                         class="category-link"> {{ item.name }}({{ item.articleCount }}) </span>
            <a-popconfirm
              title="确认删除该分类?"
              @confirm="() => onDeleteCategory(item.id)"
              okText="确定"
              cancelText="取消"
            >
              <span v-if="item.articleCount === 0">
                <a href="javascript:;" class="delete-category-link"><a-icon type="close" /></a>
              </span>
            </a-popconfirm>

          </span>
        </span>
      </div>

    </a-card>
  </div>
</template>

<script>

import { listCategories, createCategory, deleteCategory } from '@/api/categories'

export default {
  name: 'Categories',
  components: {},

  data () {
    return {
      newCategoryName: '',
      categories: []
    }
  },

  mounted () {
    this.onListCategory()
  },

  methods: {
    onListCategory () {
      listCategories().then(data => {
        this.categories = data
      })
    },
    onDeleteCategory (idToDelete) {
      deleteCategory(idToDelete).then(() => {
        this.categories = this.categories.filter(({ id }) => id !== idToDelete)
      })
    },
    onCreateCategory (name) {
      createCategory({ name }).then(() => {
        this.onListCategory()
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

.category-item {
  background: @primary-color;
  line-height: 30px;
  padding: 10px;
  margin-right: 20px;
  color: white;
  border-radius: 5px}

.category-link {
  &:hover {
    text-decoration: underline white;

    cursor: pointer;
  }
}

.delete-category-link {
  color: white;

  &:hover {
    font-weight: bolder;
  }
}
</style>
