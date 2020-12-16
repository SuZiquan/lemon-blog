import Vue from 'vue'

import {
  Button,
  Layout,
  Menu,
  Drawer,
  AutoComplete,
  Modal,
  Pagination,
  Row,
  Col,
  Icon,
  BackTop,
  Avatar,
  Select,
  Input,
  Divider,
  Card,
  Dropdown,
  Spin,
  Empty,
  Popconfirm,
  Affix,
  Tooltip,
  message,
} from 'ant-design-vue'

import 'ant-design-vue/lib/button/style'
import 'ant-design-vue/lib/row/style'
import 'ant-design-vue/lib/col/style'
import 'ant-design-vue/lib/auto-complete/style'
import 'ant-design-vue/lib/input/style'
import 'ant-design-vue/lib/menu/style'
import 'ant-design-vue/lib/drawer/style'
import 'ant-design-vue/lib/avatar/style'
import 'ant-design-vue/lib/divider/style'
import 'ant-design-vue/lib/card/style'
import 'ant-design-vue/lib/modal/style'
import 'ant-design-vue/lib/dropdown/style'
import 'ant-design-vue/lib/pagination/style'
import 'ant-design-vue/lib/spin/style'
import 'ant-design-vue/lib/message/style'
import 'ant-design-vue/lib/select/style'
import 'ant-design-vue/lib/empty/style'
import 'ant-design-vue/lib/popconfirm/style'
import 'ant-design-vue/lib/affix/style'
import 'ant-design-vue/lib/tooltip/style'
import 'ant-design-vue/lib/back-top/style'

Vue.config.productionTip = false

Vue.use(Button)
Vue.use(Layout)
Vue.use(Menu)
Vue.use(Drawer)
Vue.use(AutoComplete)
Vue.use(Modal)
Vue.use(Pagination)
Vue.use(Row)
Vue.use(Col)
Vue.use(Icon)
Vue.use(BackTop)
Vue.use(Avatar)
Vue.use(Select)
Vue.use(Input)
Vue.use(Divider)
Vue.use(Card)
Vue.use(Dropdown)
Vue.use(Spin)
Vue.use(Empty)
Vue.use(Popconfirm)
Vue.use(Affix)
Vue.use(Tooltip)

Vue.prototype.$message = message
