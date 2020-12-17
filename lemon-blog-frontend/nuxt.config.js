import path from 'path'
import webpack from 'webpack'

export default {
  server: {
    port: 3000, // default: 3000
    host: 'localhost', // default: localhost,
  },

  // Global page headers (https://go.nuxtjs.dev/config-head)
  head: {
    title: 'Lemon Blog',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: '' },
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
  },

  // Global CSS (https://go.nuxtjs.dev/config-css)
  css: ['@assets/css/main.less'],

  // Plugins to run before rendering page (https://go.nuxtjs.dev/config-plugins)
  plugins: ['@/plugins/antd-ui', '@/plugins/axios', '@/plugins/auth'],

  // Auto import components (https://go.nuxtjs.dev/config-components)
  components: true,

  // Modules for dev and build (recommended) (https://go.nuxtjs.dev/config-modules)
  buildModules: [
    // https://go.nuxtjs.dev/eslint
    '@nuxtjs/eslint-module',
  ],

  // Modules (https://go.nuxtjs.dev/config-modules)
  modules: ['@nuxtjs/axios', '@nuxtjs/proxy', 'nuxt-compress'],

  'nuxt-compress': {
    gzip: {
      cache: true,
    },
    brotli: {
      threshold: 10240,
    },
  },

  // Build Configuration (https://go.nuxtjs.dev/config-build)
  build: {
    standalone: true,

    extend(config, ctx) {
      config.resolve.alias['@ant-design/icons/lib/dist$'] = path.resolve(
        __dirname,
        './assets/icon/antd-icon.js'
      )
    },

    optimization: {
      splitChunks: {
        minSize: 10000,
        maxSize: 250000,
      },
    },

    plugins: [new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/)],

    loaders: {
      less: {
        lessOptions: {
          modifyVars: {
            'primary-color': '#1DA57A',
            'link-color': '#1DA57A',
            'border-radius-base': '2px',
          },
          javascriptEnabled: true,
        },
      },
    },
  },

  axios: {
    proxy: true,
    prefix: '/api',
    withCredentials: true,
  },

  loading: {
    color: '#1DA57A',
  },

  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
