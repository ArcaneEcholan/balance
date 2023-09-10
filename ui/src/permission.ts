import router from '@/router/index'
import pageStack from "@/ts/pageStack";
import pageConfig from "@/ts/pageConfig";


let count = 0;

function isThisTheFirstNav() {
    return count === 0;
}

router.beforeEach(async (to, from, next) => {
    // let title = to.meta?.title
    // if (title) {
    //     pageConfig.setTitle(title)
    // } else {
    //     pageConfig.setTitle("title")
    // }
    // console.log(pageConfig)
    // first nav means user refresh the page, which represent user closing the app
    if (isThisTheFirstNav()) {
        // ok, it's the first time the app starts up
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
        if(to.path === "/" || to.path === "/index") {
            pageStack.clear()
        }
        count++
        next()
    }
})


