import router from '@/ts/router/index';
import store from '@/ts/store';

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

    next();
});
