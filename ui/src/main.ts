import Vue from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import './permission'; // permission control
import 'vant/lib/index.css';

import {
    ActionSheet,
    Button,
    Cell,
    CellGroup,
    DatetimePicker,
    DropdownItem,
    DropdownMenu,
    Field,
    Icon,
    List,
    Loading,
    NavBar,
    Notify,
    Popup,
    SwipeCell,
    Tabbar,
    TabbarItem,
    Tag
} from 'vant';
import PageEventbusRegistrationMixin from "@/page-eventbus-registration-mixin";
import "@/assets/icon-fonts/iconfont.css"

import i18n from "@/ts/lang";

Vue.config.productionTip = false;
import { NumberKeyboard } from 'vant';
Vue.use(NumberKeyboard);

Vue.use(DatetimePicker);
Vue.use(Loading);
Vue.use(Tabbar);
Vue.use(TabbarItem);
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

Vue.mixin(PageEventbusRegistrationMixin)

import FastClick from 'fastclick'
// @ts-ignore
FastClick.attach(document.body)

// Vue.prototype.$customFuncs = {
//     getRef,
//     getHtmlElem
// }

new Vue({
    router,
    store,
    // @ts-ignore
    i18n,
    render: (h) => h(App),
}).$mount('#app');
