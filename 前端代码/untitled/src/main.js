import Vue from 'vue';
import App from './App.vue';

// 引入 Element UI
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

// 引入 OpenLayers 样式
import 'ol/ol.css';

// 引入 OpenLayers 部分必要类
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';

// 使用 Element UI
Vue.use(ElementUI);

// 将 OpenLayers 的部分重要类添加到 Vue 的原型链上
Vue.prototype.$ol = { Map, View, TileLayer };

new Vue({
    el: '#app',
    render: h => h(App)
});
