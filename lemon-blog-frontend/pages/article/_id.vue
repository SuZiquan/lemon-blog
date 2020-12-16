<template>
  <content-layout :show-sidebar="toc.length > 0 && tocSidebarVisible">
    <template v-slot:content>
      <div class="article-wrapper">
        <div class="title">
          {{ article.title }}
        </div>
        <div
          v-if="
            toc.length > 0 && (!isMobile || (isMobile && !tocDrawerVisible))
          "
          class="tools"
        >
          <a-tooltip placement="left">
            <template slot="title">
              {{ tocShow ? '隐藏目录' : '显示目录' }}</template
            >
            <a-icon
              class="toggle-icon"
              :type="tocShow ? 'menu-unfold' : 'menu-fold'"
              @click="onTocToggle"
            />
          </a-tooltip>
        </div>
        <div class="properties">
          <span class="created-time">发布于：{{ article.createdAt }}</span>
          <span v-if="article.updatedAt" class="updated-time"
            >最后更新于：{{ article.updatedAt }}</span
          >
        </div>
        <div class="properties">
          <span v-if="article.categories.length > 0">
            分类：
            <nuxt-link
              v-for="category in article.categories"
              :key="'category-' + category.id"
              class="category-item"
              :to="{ name: 'category', query: { id: category.id } }"
            >
              {{ category.name }}
            </nuxt-link>
            <a-divider type="vertical"></a-divider>
          </span>

          <span v-if="article.tags.length > 0">
            标签：
            <nuxt-link
              v-for="tag in article.tags"
              :key="'tag-' + tag.id"
              class="tag-item"
              :to="{ name: 'tag', query: { id: tag.id } }"
            >
              {{ tag.name }}
            </nuxt-link>
          </span>
        </div>

        <p class="description">
          摘要：
          {{ article.description }}
        </p>

        <!-- eslint-disable -->
        <div class="content" v-html="article.content"></div>
        <!-- eslint-enable -->

        <a-divider style="margin: 20px 0 20px 0"></a-divider>
        <div style="height: 30px">
          <span v-if="article.previous"
            >上一篇：
            <nuxt-link :to="'/article/' + article.previous.id">
              {{ article.previous.title }}</nuxt-link
            ></span
          >
          <span v-if="article.next" style="float: right"
            >下一篇：
            <nuxt-link :to="'/article/' + article.next.id">{{
              article.next.title
            }}</nuxt-link></span
          >
        </div>
      </div>

      <div class="comments">
        <comment-list :article-id="article.id"> </comment-list>
      </div>

      <a-drawer
        v-if="isMobile && toc.length > 0"
        placement="right"
        :closable="false"
        style="z-index: 999"
        :visible="tocDrawerVisible"
        :body-style="{ padding: '0px' }"
        @close="onTocDrawerClose"
      >
        <div style="border: 1px solid #e8e8e8">
          <div class="toc">
            <div class="toc-title">文章目录</div>
            <ul>
              <li
                v-for="tocNav in toc"
                :key="tocNav.id"
                :class="[
                  'toc-level-' + tocNav.level,
                  activeNav === tocNav.id ? 'active' : 'unactive',
                ]"
                @click="scrollTo(tocNav.id)"
              >
                {{ tocNav.text }}
              </li>
            </ul>
          </div>
        </div>
      </a-drawer>
    </template>

    <template v-slot:sidebar>
      <a-affix>
        <div style="border: 1px solid #e8e8e8">
          <div v-if="toc.length > 0 && tocSidebarVisible" class="toc">
            <div class="toc-title">文章目录</div>
            <ul>
              <li
                v-for="tocNav in toc"
                :key="tocNav.id"
                :class="[
                  'toc-level-' + tocNav.level,
                  activeNav === tocNav.id ? 'active' : 'unactive',
                ]"
                @click="scrollTo(tocNav.id)"
              >
                {{ tocNav.text }}
              </li>
            </ul>
          </div>
        </div>
      </a-affix>
    </template>
  </content-layout>
</template>

<script>
import 'highlight.js/styles/idea.css'

import marked from 'marked'
// import hljs from 'highlight.js'

import hljs from 'highlight.js/lib/core.js'
hljs.registerLanguage('java', require('highlight.js/lib/languages/java'))
hljs.registerLanguage('kotlin', require('highlight.js/lib/languages/kotlin'))
hljs.registerLanguage('python', require('highlight.js/lib/languages/python'))
hljs.registerLanguage('go', require('highlight.js/lib/languages/go'))
hljs.registerLanguage('c', require('highlight.js/lib/languages/c-like'))
hljs.registerLanguage('cpp', require('highlight.js/lib/languages/c-like'))
hljs.registerLanguage('csharp', require('highlight.js/lib/languages/csharp'))
hljs.registerLanguage('shell', require('highlight.js/lib/languages/shell'))
hljs.registerLanguage('bash', require('highlight.js/lib/languages/bash'))
hljs.registerLanguage('sql', require('highlight.js/lib/languages/sql'))
hljs.registerLanguage(
  'javascript',
  require('highlight.js/lib/languages/javascript')
)

const tocMaxLevel = 3

export default {
  name: 'Article',
  components: {},

  async asyncData({ $axios, route }) {
    const markedOptions = {
      gfm: true,
      tables: true,
      highlight(code, lang) {
        if (lang) {
          return hljs.highlight(lang, code, true).value
        } else {
          return hljs.highlightAuto(code).value
        }
      },
    }

    const article = await $axios.$get('/articles/' + route.params.id)

    const mdRender = new marked.Renderer()
    let anchor = 0

    const toc = []

    mdRender.heading = function (text, level) {
      anchor += 1
      const id = `toc-nav-${anchor}`
      if (level <= tocMaxLevel) {
        toc.push({
          id,
          level,
          text,
        })
      }
      return `<h${level}>${text}</h${level}><span id="${id}" style="display: block;height: 115px;margin-top: -115px; visibility: hidden;"></span>`
    }

    article.content = marked(article.content, {
      renderer: mdRender,
      ...markedOptions,
    })

    return {
      toc,
      article,
    }
  },
  data() {
    return {
      tocDrawerVisible: false,
      tocSidebarVisible: true,
      activeNav: 'toc-nav-1',
    }
  },

  computed: {
    tocShow() {
      if (this.isMobile()) {
        return this.tocDrawerVisible
      } else {
        return this.tocSidebarVisible
      }
    },
  },

  created() {
    this.$store.commit('app/setCurrentNav', '主页')
  },

  mounted() {
    this.$nextTick(() => {
      const codeBlocks = document.querySelectorAll('code')
      for (const codeBlock of codeBlocks) {
        const copyButton = document.createElement('a')
        copyButton.textContent = '复制代码'
        copyButton.style.fontSize = '14px'
        copyButton.style.color = '#1DA57A'
        copyButton.style.position = 'absolute'
        copyButton.style.right = '40px'

        copyButton.setAttribute('copy-src-data', codeBlock.textContent)
        copyButton.onclick = () => {
          document.execCommand('copy')
        }
        const codeToolbar = document.createElement('div')
        codeToolbar.appendChild(copyButton)

        const preBlock = codeBlock.parentNode
        preBlock.insertBefore(codeToolbar, preBlock.firstChild)
      }
    })

    window.addEventListener('scroll', this.onScroll)

    window.addEventListener('copy', this.onCopy)
  },

  beforeDestroy() {
    window.removeEventListener('scroll', this.onScroll)
    window.removeEventListener('copy', this.onCopy)
  },

  methods: {
    isMobile() {
      return this.$store.state.app.isMobile
    },

    onTocDrawerClose() {
      this.tocDrawerVisible = false
    },

    onTocToggle() {
      if (this.isMobile()) {
        this.tocDrawerVisible = !this.tocDrawerVisible
      } else {
        this.tocSidebarVisible = !this.tocSidebarVisible
      }
    },
    onCopy(e) {
      if (e.target.hasAttribute('copy-src-data')) {
        e.clipboardData.setData(
          'text/plain',
          e.target.getAttribute('copy-src-data')
        )
        e.preventDefault()
      }
      this.$message.success('复制成功', 2)
    },
    onScroll() {
      this.$nextTick(() => {
        if (process.client) {
          const scrollTop =
            document.documentElement.scrollTop || document.body.scrollTop
          for (let i = 0; i < this.toc.length; i++) {
            const item = this.toc[i]
            const content = document.querySelector('#' + item.id)
            if (content && scrollTop > content.offsetTop) {
              this.activeNav = item.id
            } else {
              break
            }
          }
        }
      })
    },
    scrollTo(headId) {
      const element = document.querySelector('#' + headId)
      element.scrollIntoView({ behavior: 'smooth' })
    },
  },

  head() {
    const keywords = []
    for (const category of this.article.categories) {
      keywords.push(category.name)
    }
    for (const tag of this.article.tags) {
      keywords.push(tag.name)
    }
    return {
      title: this.article.title,
      meta: [
        {
          hid: 'description',
          name: 'description',
          content: this.article.description,
        },
        {
          hid: 'keywords',
          name: 'keywords',
          content: keywords.join(),
        },
      ],
    }
  },

  destroy() {
    window.removeEventListener('scroll', this.onScroll)
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

.article-wrapper {
  .boxed();

  padding: 20px 28px 20px 28px;

  @media @mobile {
    padding: 20px;
  }

  .title {
    font-size: 20px;
    font-weight: bolder;
  }

  .properties {
    font-size: 14px;
    padding-top: 10px;

    .created-time {
    }

    .updated-time {
      padding-left: 20px;
    }

    .category-item,
    .tag-item {
      border: 1px solid @border-color-base;
      color: @text-color;
      padding: 0 4px 0 4px;
      border-radius: 2px;
      text-align: center;
      margin: 0 2px 0 2px;

      &:hover {
        background: #d8e6df;
        color: @primary-color;
      }
    }
  }

  .description {
    margin: 20px 0;
    padding: 10px;
    font-size: 14px;
    color: white;
    background: #aaa;
    border: #888;
  }

  .content {
    font-size: 16px;
  }
}

.tools {
  position: fixed;
  right: 0;
  padding: 10px;
  background: @primary-color;
  border-radius: 2px;
  z-index: 999;

  .toggle-icon {
    font-size: 20px;
    color: white;
  }
}

.comments {
  margin-top: 10px;
  @media @mobile {
    margin-top: 0;
  }
}

.toc {
  background: white;

  padding: 20px;

  .toc-title {
    font-size: 18px;
    font-weight: bold;
    padding: 10px;
  }

  ul {
    padding-left: 5px;

    li {
      font-size: 16px;
      font-weight: 500;
      border-left: 2px solid #f7f7f7;
      list-style: none;
      padding: 5px;

      &.toc-level-1 {
        padding-left: 10px;
      }

      &.toc-level-2 {
        padding-left: 20px;
      }

      &.toc-level-3 {
        padding-left: 30px;
      }

      &:hover,
      &.active {
        cursor: pointer;
        color: @primary-color;
        border-left: 2px solid @primary-color;
      }
    }
  }
}

/deep/ pre {
  background: #e8e8e8;
  padding: 10px;
}
</style>
