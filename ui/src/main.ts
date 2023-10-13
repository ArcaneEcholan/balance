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
import {Button} from 'vant';
import {Notify} from 'vant';
import { NavBar } from 'vant';
import {Icon} from "vant";
import { List } from 'vant';
import { Tag } from 'vant';
import { DropdownMenu, DropdownItem } from 'vant'
import { SwipeCell } from 'vant';
import { ActionSheet } from 'vant';
Vue.use(Notify);
Vue.use(Button);
Vue.use(Popup);
Vue.use(Cell);
Vue.use(Field);
Vue.use(CellGroup);
Vue.use(NavBar);
Vue.use(Icon);
Vue.use(List);
Vue.use(Tag);
Vue.use(DropdownMenu);
Vue.use(DropdownItem);
Vue.use(ActionSheet);

Vue.use(SwipeCell);

new Vue({
    router,
    store,
    render: (h) => h(App),
}).$mount('#app');
