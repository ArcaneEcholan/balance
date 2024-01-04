import router from '@/ts/router/index';
import store from '@/ts/store';
import request from '@/ts/request';

router.beforeEach(async (to, from, next) => {
    let whiteList = ['/login'];
    if (whiteList.indexOf(to.path) !== -1) {
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
    }).then((resp) => {
        store.commit('user/SET_CONFIGS', resp.data.configs);
        next();
    });
});
