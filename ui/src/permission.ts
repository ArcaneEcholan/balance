import router from '@/ts/router/index';
import store from '@/ts/store';

let count = 0;

function isThisTheFirstNav() {
    return count === 0;
}

router.beforeEach(async (to, from, next) => {
    let whiteList = ['/login'];
    if (whiteList.indexOf(to.path) !== -1) {
        next();
        return;
    }

    let token = store.getters.token;
    let username = store.getters.username;
    if (token == null || username == null) {
        next('/login');
        return;
    }

    next();
    // // first nav means user refresh the page, which represent user closing the app
    // if (isThisTheFirstNav()) {
    //     // ok, it's the first time the app starts up
    //     count ++
    //
    //     // we abandon the non index nav
    //     if (to.path === "/") {
    //         next()
    //     } else {
    //         // if the start up action is not index, we redirect to index
    //         count = 0
    //         next("/")
    //     }
    // } else {
    //     count++
    //     next()
    // }
});
