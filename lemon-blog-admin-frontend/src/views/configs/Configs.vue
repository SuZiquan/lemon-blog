<template>
  <div>
    <a-card :body-style="{padding: '24px 32px'}" :bordered="false">
      <div style="margin-bottom: 20px">
        <a-button type="primary" @click="changeUsernameModalVisible = true">
          修改用户名
        </a-button>
        <a-button style="margin-left: 20px" type="primary" @click="changePasswordModalVisible = true">
          修改密码
        </a-button>
      </div>

      <a-modal
        title="修改用户名"
        :visible="changeUsernameModalVisible"
        @ok="onChangeUsername"
        @cancel="changeUsernameModalVisible = false"
        ok-text="保存"
        cancel-text="取消"
      >
        <a-form :form="changeUsernameForm" :layout="formLayout">
          <a-form-item
            label="新用户名"
            :label-col="formItemLayout.labelCol"
            :wrapper-col="formItemLayout.wrapperCol"
          >
            <a-input
              name="newUsername"
              placeholder="new username"
              v-decorator="[
                'newUsername',
                {rules: [{ required: true, message: '请输入新用户名' }]}
              ]"

            />
          </a-form-item>
        </a-form>
      </a-modal>

      <a-modal
        title="修改密码"
        :visible="changePasswordModalVisible"
        @ok="onChangePassword"
        @cancel="changePasswordModalVisible = false"
        ok-text="保存"
        cancel-text="取消"
      >
        <a-form :form="changePasswordForm" :layout="formLayout">

          <a-form-item
            label="旧密码"
            :label-col="formItemLayout.labelCol"
            :wrapper-col="formItemLayout.wrapperCol"
          >
            <a-input-password
              name="oldPassword"
              placeholder="old password"
              v-decorator="[
                'oldPassword',
                {rules: [{ required: true, message: '请输入旧密码' }]}
              ]" />
          </a-form-item>
          <a-form-item
            label="新密码"
            :label-col="formItemLayout.labelCol"
            :wrapper-col="formItemLayout.wrapperCol"
          >
            <a-input-password
              name="newPassword"
              placeholder="new password"
              v-decorator="[
                'newPassword',
                {rules: [{ required: true, message: '请输入新密码' }]}
              ]" />
          </a-form-item>
          <a-form-item
            label="重复新密码"
            :label-col="formItemLayout.labelCol"
            :wrapper-col="formItemLayout.wrapperCol"
            :validate-status="confirmNewPasswordValidateStatus"
            :help="confirmNewPasswordValidateHelp"
          >
            <a-input-password
              name="confirmNewPassword"
              placeholder="repeat new password"
              v-decorator="[
                'confirmNewPassword',
                {rules: [{ required: true, message: '请重复输入新密码' }]}
              ]"
            />
          </a-form-item>
        </a-form>
      </a-modal>

      <div>

        <a-table
          :row-key="record => record.name"
          :columns="columns"
          :data-source="configs"
          bordered>
          <template slot="title">
            配置项
          </template>
          <template
            slot="name"
            slot-scope="text"
          >
            {{ text }}
          </template>

          <template
            slot="value"
            slot-scope="text, record"
          >
            <div>
              <a-input
                v-if="record.editable"
                style="margin: -5px 0"
                v-model="editingConfigValue"
              />
              <template v-else>
                <span>
                  {{ text }}
                </span>
              </template>
            </div>
          </template>
          <template slot="operation" slot-scope="text, record">
            <div class="editable-row-operations">
              <span v-if="record.editable">
                <a @click="() => save(record.name)">保存</a>
                <a @click="() => cancel(record.name)">取消</a>
              </span>
              <span v-else>
                <a :disabled="editingConfigName !== ''" @click="() => edit(record.name)">编辑</a>
              </span>
            </div>
          </template>
        </a-table>

      </div>

    </a-card>
  </div>
</template>

<script>
import { baseMixin } from '@/store/app-mixin'
import { listConfigs, saveConfig } from '@/api/configs'
import { changePassword, changeUsername } from '@/api/account'

const columns = [
  {
    title: '名称',
    dataIndex: 'name',
    scopedSlots: { customRender: 'name' },
    width: '200px'
  },
  {
    title: '值',
    dataIndex: 'value',
    scopedSlots: { customRender: 'value' }
  },
  {
    title: '操作',
    dataIndex: 'operation',
    scopedSlots: { customRender: 'operation' },
    width: '200px'
  }
]
const configNames = ['indexTitle', 'bannerLogoUrl', 'bannerTitle', 'ownerAvatarUrl', 'ownerUsername', 'ownerBio', 'siteFooter']
const configs = []
for (const configName of configNames) {
  configs.push({
    name: configName,
    id: null,
    value: null,
    editable: false

  })
}

export default {
  name: 'ConfigManage',
  mixins: [baseMixin],
  components: {},
  data () {
    return {
      changeUsernameModalVisible: false,
      changeUsernameForm: this.$form.createForm(this),
      changePasswordModalVisible: false,
      changePasswordForm: this.$form.createForm(this),
      formLayout: 'horizontal',
      configs,
      columns,
      editingConfigName: '',
      editingConfigValue: '',
      confirmNewPasswordValidateStatus: '',
      confirmNewPasswordValidateHelp: ''
    }
  },
  mounted () {
    listConfigs().then(data => {
      for (const item of data) {
        const config = configs.find((config) => {
          return config.name === item.name
        })
        config.value = item.value
      }
    })
  },
  computed: {
    formItemLayout () {
      return {
        labelCol: { span: 6 },
        wrapperCol: { span: 12 }
      }
    }
  },
  methods: {
    onChangeUsername () {
      this.changeUsernameForm.validateFields((err, values) => {
        if (!err) {
          changeUsername(values.newUsername).then(() => {
            this.$message.success('保存成功')
            this.changeUsernameModalVisible = false
            this.changeUsernameForm.resetFields()
          })
        }
      })
    },

    onChangePassword () {
      this.changePasswordForm.validateFields((err, values) => {
        if (!err) {
          if (values.newPassword !== values.confirmNewPassword) {
            this.confirmNewPasswordValidateStatus = 'error'
            this.confirmNewPasswordValidateHelp = '新密码不一致'
          } else {
            this.confirmNewPasswordValidateStatus = ''
            this.confirmNewPasswordValidateHelp = ''
            changePassword(values.oldPassword, values.newPassword).then(() => {
              this.$message.success('保存成功')
              this.changePasswordModalVisible = false
              this.changePasswordForm.resetFields()
            })
          }
        }
      })
    },

    edit (name) {
      const newData = [...this.configs]
      const target = newData.filter(item => name === item.name)[0]
      if (target) {
        this.editingConfigName = name
        this.editingConfigValue = target.value
        target.editable = true
        this.configs = newData
      }
    },
    save (name) {
      saveConfig({
        name: this.editingConfigName,
        value: this.editingConfigValue
      }).then(() => {
        const newData = [...this.configs]
        const target = newData.filter(item => name === item.name)[0]
        if (target) {
          delete target.editable
          target.value = this.editingConfigValue
          this.configs = newData
        }
        this.editingConfigName = ''
      })
    },
    cancel (name) {
      const newData = [...this.configs]
      const target = newData.filter(item => name === item.name)[0]
      if (target) {
        delete target.editable
        this.configs = newData
      }
      this.editingConfigName = ''
    }
  }
}
</script>

<style lang="less" scoped>
.editable-row-operations a {
  margin-right: 8px;
}
</style>
