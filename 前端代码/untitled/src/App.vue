<template>
  <div id="app">
    <HomePage />
  </div>
</template>

<script>
// 导入 HomePage 组件
import HomePage from './views/HomePage.vue';

export default {
  name: 'App',
  components: {
    // 在组件中注册 HomePage
    HomePage,
  }
};
</script>