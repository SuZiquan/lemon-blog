<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <a-form @submit="handleSubmit" :form="form">
        <a-form-item
          label="标题"
          :labelCol="labelCol"
          :wrapperCol="wideWrapperCol">
          <a-input
            name="title"
            v-decorator="[
              'title',
              {rules: [{ required: true, message: '请输入标题' }]}
            ]"
            placeholder="请输入标题" />
        </a-form-item>
        <a-form-item
          label="分类"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          <a-select
            name="categories"
            mode="tags"
            placeholder="不存在的分类会自动创建"
            v-decorator="[
              'categories',
              {rules: [{ required: true, message: '至少选择一个分类' }]}
            ]"
          >
            <a-select-option v-for="item in categories" :key="item.name">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="标签"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol">
          <a-select
            name="tags"
            mode="tags"
            placeholder="不存在的标签会自动创建"
            v-decorator="[
              'tags',
              {rules: [{ required: true, message: '至少选择一个标签' }]}
            ]"
          >
            <a-select-option v-for="item in tags" :key="item.name">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item
          label="描述"
          :labelCol="labelCol"
          :wrapperCol="wideWrapperCol">

          <a-textarea
            name="description"
            placeholder="请输入描述"
            :auto-size="{ minRows: 3, maxRows: 5 }"
            v-decorator="[
              'description',
              {rules: [{ required: true, message: '请输入描述' }]}
            ]"
          />
        </a-form-item>
        <a-form-item
          label="内容"
          :labelCol="labelCol"
          :wrapperCol="wideWrapperCol"
          :required="true">
          <div class="mavon-editor">
            <mavon-editor
              v-decorator="[
                'content',
                { initialValue: '' },
                {rules: [{ required: true, message: '请输入内容' }]}
              ]"
              name="content"
              style="min-height: 400px;" />
          </div>
        </a-form-item>

        <a-form-item
          label="创建时间"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          :required="true"
        >
          <a-date-picker
            v-decorator="[
              'createDate',
              {rules: [{ required: true, message: '创建日期不能为空' }]},
            ]"
            :locale="locale"
            :format="dateFormat"
          />
          <a-time-picker
            v-decorator="[
              'createTime',
              {rules: [{ required: true, message: '创建时间不能为空' }]}
            ]"
            style="padding-left: 5px"
            :locale="locale"
            :format="timeFormat"
          />
        </a-form-item>

        <a-form-item
          label="修改时间"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          :required="true"
          :format="dateFormat"
        >
          <a-date-picker
            v-decorator="[
              'updateDate',
              {rules: [{ required: true, message: '更新日期不能为空' }]},
            ]"
            :locale="locale" />
          <a-time-picker
            v-decorator="[
              'updateTime',
              {rules: [{ required: true, message: '更新时间不能为空' }]}
            ]"
            style="padding-left: 5px"
            :locale="locale"
            :format="timeFormat"
          />
        </a-form-item>

        <a-form-item
          label="状态"
          :labelCol="labelCol"
          :wrapperCol="wrapperCol"
          :required="true"
        >
          <a-radio-group
            name="status"
            v-decorator="['status', { initialValue: 0 }]"
          >
            <a-radio :value="0">隐藏</a-radio>
            <a-radio :value="1">公开</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item
          :wrapperCol="{lg: {offset: 2}, sm: {offset: 2}}"
        >

          <a-button htmlType="submit" type="primary">创建</a-button>
        </a-form-item>
      </a-form>

    </a-card>
  </div>
</template>

<script>
import locale from 'ant-design-vue/es/date-picker/locale/zh_CN'

import { listTags } from '@/api/tags'
import { listCategories } from '@/api/categories'
import moment from 'moment'
import 'moment/locale/zh-cn'
import { createArticle } from '@/api/articles'
moment.locale('zh-cn')
export default {
  name: 'BaseForm',
  data () {
    return {
      dateFormat: 'YYYY-MM-DD',
      timeFormat: 'HH:mm',
      locale,
      categories: [],
      labelCol: { lg: { span: 2 }, sm: { span: 3 } },
      wrapperCol: { lg: { span: 5 }, sm: { span: 8 } },
      wideWrapperCol: { lg: { span: 20 }, sm: { span: 20 } },
      tags: [],
      form: this.$form.createForm(this)
    }
  },
  mounted () {
      this.onListCategories()
      this.onListTag()
  },
  created () {
    this.$nextTick(() => {
      const date = new Date()
      this.form.setFieldsValue({
        createDate: moment(date, this.dateFormat),
        createTime: moment(date, this.timeFormat),
        updateDate: moment(date, this.dateFormat),
        updateTime: moment(date, this.timeFormat)
      })
    })
  },
  methods: {
    moment,
    onListTag () {
      listTags().then(data => {
        this.tags = data
      })
    },
    onListCategories () {
      listCategories().then(data => {
        this.categories = data
      })
    },
    handleSubmit (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          const params = {
            title: values.title,
            categories: values.categories.map(value => {
              return {
                name: value
              }
            }),
            tags: values.tags.map(value => {
              return {
                name: value
              }
            }),
            description: values.description,
            content: values.content,
            status: values.status,
            createdAt: values.createDate.format(this.dateFormat) + ' ' + values.createTime.format(this.timeFormat),
            updatedAt: values.updateDate.format(this.dateFormat) + ' ' + values.updateTime.format(this.timeFormat)
          }
          createArticle(params).then(() => {
              this.$message.success('创建成功')
              this.$router.push({
                path: `/articles`
              })
            })
        }
      })
    }
  }
}
</script>
<style scoped>
.mavon-editor {
  width: 100%;
  height: 100%;
}

div.v-note-wrapper {
  z-index: 900 !important;
}

</style>
