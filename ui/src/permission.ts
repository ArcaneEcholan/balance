import router from '@/ts/router/index';
import store from '@/ts/store';
import request from '@/ts/request';
import { Notify } from 'vant';

router.beforeEach(async (to, from, next) => {
    let whiteList = ['/login'];
    if (whiteList.indexOf(to.path) !== -1) {
        if (to.path === '/login') {
            // if already login, redirect to home
            let token = store.getters.token;
            if (token != null) {
                next('/home');
                return;
            }
        }
        next();
        return;
    }

    let token = store.getters.token;
    // let username = store.getters.username;
    if (token == null) {
        next('/login');
        return;
    }

    // update user info
    request({
        url: '/user/info',
        method: 'get',
        headers: {
            'entity-token': token,
        },
    })
        .then((resp) => {
            store.commit('user/SET_CONFIGS', resp.data.configs);
            next();
        })
        .catch((err) => {
            Notify({
                message: err.response.data,
                type: 'danger',
            });
            store.commit('user/LOGOUT');
            next('/login');
        });
});
