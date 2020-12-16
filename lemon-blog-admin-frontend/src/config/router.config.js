import { UserLayout, BasicLayout } from '@/layouts'

const RouteView = {
  name: 'RouteView',
  render: (h) => h('router-view')
}

export const asyncRouterMap = [

  {
    path: '/',
    name: 'index',
    component: BasicLayout,
    meta: { title: '首页' },
    redirect: '/overview',
    children: [
      {
        path: '/overview',
        name: 'Overview',
        component: () => import('@/views/dashboard/Overview'),
        meta: { title: '博客总览', icon: 'dashboard', keepAlive: true }
      },

      {
        path: '/articles',
        name: 'Articles',
        component: RouteView,
        redirect: '/articles/list',
        hideChildrenInMenu: true,
        meta: { title: '文章管理', icon: 'profile', keepAlive: true },
        children: [
          {
            path: '/articles/list',
            name: '文章列表',
            hideInMenu: true,
            component: () => import('@/views/articles/Articles'),
            meta: { title: '文章列表', hidden: true, hide: true, keepAlive: true }

          },
          {
            path: '/articles/edit/:id',
            hideInMenu: true,
            component: () => import('@/views/articles/ArticleEdit'),
            meta: { title: '编辑文章', hidden: true, hide: true, keepAlive: true }
          },
          {
            path: '/articles/new',
            hideInMenu: true,
            component: () => import('@/views/articles/ArticleNew'),
            meta: { title: '新建文章', hidden: true, hide: true, keepAlive: true }
          },
          {
            path: '/articles/:id/comments',
            hideInMenu: true,
            component: () => import('@/views/articles/ArticleComments'),
            meta: { title: '文章评论', hidden: true, hide: true, keepAlive: true }
          }
          ]
      },

      {
        path: '/comments',
        name: 'Comments',
        component: () => import('@/views/comments/Comments'),
        meta: { title: '评论管理', icon: 'message', keepAlive: true }
      },

      {
        path: '/categories',
        name: 'Categories',
        component: () => import('@/views/categories/Categories'),
        meta: { title: '分类管理', icon: 'appstore', keepAlive: true }
      },

      {
        path: '/tags',
        name: 'Tags',
        component: () => import('@/views/tags/Tags'),
        meta: { title: '标签管理', icon: 'tag', keepAlive: true }
      },

      {
        path: '/pages',
        name: 'Pages',
        component: RouteView,
        redirect: '/pages/list',
        hideChildrenInMenu: true,
        meta: { title: '页面管理', icon: 'file', keepAlive: true },
        children: [
          {
            path: '/pages/list',
            name: '页面列表',
            hideInMenu: true,
            component: () => import('@/views/pages/Pages'),
            meta: { title: '页面列表', hidden: true }

          },
          {
            path: '/pages/new',
            hideInMenu: true,
            component: () => import('@/views/pages/PageNew'),
            meta: { title: '新建页面', hidden: true, keepAlive: true }
          },
          {
            path: '/pages/edit/:id',
            hideInMenu: true,
            component: () => import('@/views/pages/PageEdit'),
            meta: { title: '编辑页面', hidden: true, keepAlive: true }
          }
        ]
      },

      {
        path: '/users',
        name: 'Users',
        component: RouteView,
        redirect: '/users/list',
        hideChildrenInMenu: true,
        meta: { title: '用户管理', icon: 'user', keepAlive: true },
        children: [
          {
            path: '/users/list',
            name: '用户列表',
            hideInMenu: true,
            component: () => import('@/views/users/Users'),
            meta: { title: '用户列表', hidden: true }
          },
          {
            path: '/users/:id',
            hideInMenu: true,
            component: () => import('@/views/users/User'),
            meta: { title: '用户详情', hidden: true, hide: true, keepAlive: true }
          }
        ]
      },
      {
        path: '/configs',
        name: 'Config',
        component: () => import('@/views/configs/Configs'),
        meta: { title: '配置管理', icon: 'setting' }
      }
    ]
  },
  {
    path: '*', redirect: '/404', hidden: true
  }
]

/**
 * 基础路由
 * @type { *[] }
 */
export const constantRouterMap = [
  {
    path: '/user',
    component: UserLayout,
    redirect: '/user/login',
    hidden: true,
    children: [
      {
        path: 'login',
        name: 'login',
        component: () => import(/* webpackChunkName: "login" */ '@/views/login/Login')
      }
    ]
  },

  {
    path: '/404',
    component: () => import(/* webpackChunkName: "fail" */ '@/views/exception/404')
  }

]
