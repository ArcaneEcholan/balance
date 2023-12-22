import Vue from 'vue';
import App from './App.vue';
import router from './ts/router';
import store from './ts/store';
import './permission'; // permission control

import '@/assets/icon-fonts/iconfont.css';

import i18n from '@/ts/lang';

Vue.config.productionTip = false;

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
    Tag,
} from 'vant';
{
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
}

import PageEventbusRegistrationMixin from '@/page-eventbus-registration-mixin';
Vue.mixin(PageEventbusRegistrationMixin);

import '@/import-style-vars';

new Vue({
    router,
    store,
    // @ts-ignore
    i18n,
    render: (h) => h(App),
}).$mount('#app');
