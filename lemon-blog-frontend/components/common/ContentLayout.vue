<template>
  <a-row id="main">
    <a-col id="content" :span="4">
      <slot name="content"></slot>
    </a-col>
    <a-col v-if="showSidebar" id="sidebar" :span="4">
      <slot name="sidebar">
        <SiteOverview></SiteOverview>
      </slot>
    </a-col>
  </a-row>
</template>

<script>
import SiteOverview from '@/components/common/SiteOverview'

export default {
  components: { SiteOverview },
  props: {
    showSidebar: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {}
  },
}
</script>

<style scoped lang="less">
@import 'assets/css/main.less';

#main {
  padding: 10px 0 10px 0;

  @media @mobile {
    padding: 0;
  }

  max-width: 1200px;
  margin: auto;
  display: flex;

  #content {
    flex: 1;
    margin: 0 5px 0 5px;
    height: 100%;

    @media @mobile {
      margin: 0;
    }
  }

  #sidebar {
    width: 300px;
    margin: 0 5px 0 5px;

    @media @mobile {
      display: none;
    }
  }
}
</style>
