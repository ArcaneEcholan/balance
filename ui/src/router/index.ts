import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import HomeIndexView from "@/views/index/index/index.vue";
import EditRecordView from "@/views/index/index/edit/edit_record.vue";
import IndexIndexView from "@/views/index/index.vue";
import ManageLedgerView from "@/views/index/ledger.vue";
import StatisticIndexView from "@/views/index/statictics/index.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: IndexIndexView,
    },
    {
        path: '/home',
        name: 'home',
        component: HomeIndexView,
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

    },
    {
        path: '/statistics',
        name: 'statistics',
        component: StatisticIndexView,
        meta: {
        },
        children: [
            // {
            //     meta: {
            //     },
            //     name: 'edit_transaction',
            //     path: 'edit',
            //     component: EditRecordView,
            // }
        ]

    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
