import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import HelloWorld from '@/views/HelloWorld.vue';
import About from '@/views/About.vue';
import TestView from "@/views/index/index/index.vue";
import EditRecordView from "@/views/index/edit/edit_record.vue";
import IndexView from "@/views/index/index/index.vue";
import IndexIndexView from "@/views/index/index.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        redirect: '/index',
    },
    {
        path: '/index',
        component: IndexView,
        children: [
            // {
            //     path: '',
            //     component: IndexView,
            // },
            {
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
