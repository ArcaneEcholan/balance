import router from '@/router/index'
import pageStack from "@/ts/pageStack";


let count = 0;

function isThisTheFirstNav() {
    return count === 0;
}

router.beforeEach(async (to, from, next) => {
    if (isThisTheFirstNav()) {
        // ok, it's the first time the app starts up

        // we clean up the stack size
        pageStack.clear()

        count ++

        // we abandon the non index nav
        if (to.path === "/" || to.path === "/index") {
            next()
        } else {
            // if the start up action is not index, we redirect to index
            count = 0
            next("/")
        }
    } else {
        // it's not the first time the app starts up
        count++
        next()
    }
})


