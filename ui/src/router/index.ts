import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import IndexView from "@/views/index/index/index.vue";
import EditRecordView from "@/views/index/edit/edit_record.vue";
import IndexIndexView from "@/views/index/index.vue";
import ManageLedgerView from "@/views/index/ledger.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: IndexIndexView,
    },
    {
        path: '/index',
        name: 'home',
        component: IndexView,
        meta: {
        },
        children: [
            // {
            //     path: '',
            //     component: IndexView,
            // },
            {
                meta: {
                },
                name: 'edit_transaction',
                path: 'edit',
                component: EditRecordView,
            },{
                meta: {
                },
                name: 'manage_ledger',
                path: 'manage_ledger',
                component: ManageLedgerView
            },
        ]

    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
