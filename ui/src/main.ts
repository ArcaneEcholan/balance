import Vue from 'vue';
import App from './App.vue';
import router from './ts/router';
import store from './ts/store';
import './permission'; // permission control

import '@/assets/icon-fonts/iconfont.css';

import i18n from '@/ts/lang';

Vue.config.productionTip = false;
Vue.config.devtools = true;

import '@/assets/custom-icon-fonts/icons.css';
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
    Form,
    Dialog,
    Toast,
} from 'vant';
import { Checkbox, CheckboxGroup } from 'vant';
{
    Vue.use(Checkbox);
    Vue.use(CheckboxGroup);
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
    Vue.use(Form);
    Vue.use(Dialog);
    Vue.use(Toast);
}

import '@/style/common-style.scss';
import '@/style/style-specification.scss';

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
