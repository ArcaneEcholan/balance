import Vue from 'vue';
import VueRouter, {RouteConfig} from 'vue-router';
import HelloWorld from '@/views/HelloWorld.vue';
import About from '@/views/About.vue';
import TestView from "@/views/Test.vue";

Vue.use(VueRouter);

const routes: Array<RouteConfig> = [
    {
        path: '/',
        redirect: '/test',
    },
    {
        path: '/test',
        name: 'Test',
        component: TestView,
    }
];

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes,
});

export default router;