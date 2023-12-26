import Vue from 'vue';
import VueRouter, { RouteConfig } from 'vue-router';
import HomeIndexView from '@/views/index/index/index.vue';
import IndexIndexView from '@/views/index/index.vue';
import StatisticIndexView from '@/views/index/statictics/index.vue';
import LoginView from '@/views/index/index/login.vue';
import ProfileIndexView from '@/views/index/profile/index.vue';

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        component: IndexIndexView,
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
            {
                path: '/profile',
                name: 'profile',
                component: ProfileIndexView,
            },
        ],
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView,
    },
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;
