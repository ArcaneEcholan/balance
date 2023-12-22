import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import HomeIndexView from '@/views/index/index/index.vue';
import IndexIndexView from '@/views/index/index.vue';
import StatisticIndexView from '@/views/index/statictics/index.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: IndexIndexView,
        redirect: '/home',
        children: [
            {
                path: '/home',
                name: 'home',
                component: HomeIndexView,
            },
            {
                path: '/statistics',
                name: 'statistics',
                component: StatisticIndexView,
            },
        ],
    },
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
