import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './permission'; // permission control
Vue.use(ElementUI);

Vue.config.productionTip = false;


import 'vant/lib/index.css';

import {Cell, Field, CellGroup} from 'vant';
import {Popup} from 'vant';

Vue.use(Popup);
Vue.use(Cell);
Vue.use(Field);
Vue.use(CellGroup);

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount('#app');
