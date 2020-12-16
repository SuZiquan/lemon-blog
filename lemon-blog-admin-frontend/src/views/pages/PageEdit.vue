<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <a-form @submit="handleSubmit" :form="form">
        <a-form-item
          label="名称"
          :labelCol="{lg: {span: 2}, sm: {span: 2}}"
          :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-input
            v-decorator="[
              'name',
              {rules: [{ required: true, message: '请输入名称' }]}
            ]"
            name="name"
            placeholder="page name" />
        </a-form-item>
        <a-form-item
          label="图标"
          :labelCol="{lg: {span: 2}, sm: {span: 2}}"
          :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-input
            v-decorator="[
              'icon',
              {rules: [{ required: true, message: '请输入图标类型（Ant Design Vue）' }]}
            ]"
            name="icon"
            placeholder="ant-design-vue icon-type" />
        </a-form-item>
        <a-form-item
          label="链接"
          :labelCol="{lg: {span: 2}, sm: {span: 2}}"
          :wrapperCol="{lg: {span: 10}, sm: {span: 17} }">
          <a-input
            v-decorator="[
              'link',
              {rules: [{ required: true, message: '请输入链接' }]}
            ]"
            name="link"
            placeholder="/link" />
        </a-form-item>
        <a-form-item
          label="内容"
          :labelCol="{lg: {span: 2}, sm: {span: 2}}"
          :wrapperCol="{lg: {span: 20}, sm: {span: 22} }"
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
          label="状态"
          :labelCol="{lg: {span: 2}, sm: {span: 2}}"
          :wrapperCol="{lg: {span: 10}, sm: {span: 17} }"
          :required="true"
        >
          <a-radio-group v-decorator="['status', { initialValue: 0 }]">
            <a-radio :value="0">隐藏</a-radio>
            <a-radio :value="1">公开</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item
          :wrapperCol="{lg: {offset: 2}, sm: {offset: 2}}"
        >
          <a-button htmlType="submit" type="primary">保存</a-button>
        </a-form-item>
      </a-form>

    </a-card>
  </div>
</template>

<script>

import { getPage, updatePage } from '@/api/pages'

export default {
  name: 'BaseForm',
  data () {
    return {
      form: this.$form.createForm(this)
    }
  },
  created () {
    const pageId = this.$route.params.id
    getPage(pageId).then(data => {
      this.$nextTick(() => {
        this.form.setFieldsValue({
          name: data['name'],
          icon: data['icon'],
          link: data['link'],
          content: data['content'],
          status: data['status']
        })
      })
    })
  },
  methods: {
    handleSubmit (e) {
      e.preventDefault()
      this.form.validateFields((err, values) => {
        if (!err) {
          updatePage(this.$route.params.id, {
            name: values.name,
            icon: values.icon,
            link: values.link,
            content: values.content,
            status: values.status
          }).then(() => {
            this.$message.success('保存成功')
            this.$router.push({
              path: `/pages`
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
</style>
