import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import IndexView from "@/views/index/index/index.vue";
import EditRecordView from "@/views/index/edit/edit_record.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        name: 'home',
        redirect: '/index',
    },
    {
        path: '/index',
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
