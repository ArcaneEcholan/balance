import router from "@/router";

class StackItem {
    name: string | null = null;
    params: any;
}

class PageStack {

    private stack: StackItem[] = []

    constructor() {
    }

    pushPage(name: string, params: any) {
        this.stack.push({name, params})
    }

    popPage() {
        if (this.stack.length <= 0) {
            this.stack = [];
        } else {
            this.stack.pop();
        }
    }

    size() {
        return this.stack.length
    }

    empty() {
        return this.size() === 0
    }

    peek() {
        if (this.stack.length <= 0) {
            return null
        } else {
            return this.stack[this.stack.length - 1]
        }
    }

    clear() {
        this.stack = []
    }

}

let pageStack = new PageStack()

function enable() {
    document.body.style.overflow = 'auto';
    document.body.style.position = 'relative';
    // let scrollPosition = store.getters.scrollPosition
    // document.body.style.top = `-${scrollPosition}px`;
    // window.scrollTo(0, scrollPosition);
}

function disable() {
    document.body.style.removeProperty('overflow');
    document.body.style.removeProperty('position');
    document.body.style.removeProperty('top');
    // store.commit('setScrollPosition', window.pageYOffset);
}

export function pushPage(name: string, params: any) {
    // null params breaks the router.push and router.replace
    if (params == null) {
        params = {}
    }

    console.debug(`push page stack`)

    // go to the next page
    router.replace({name, params})

    // stack plus 1
    pageStack.pushPage(name, params)
    if (pageStack.size() == 2) {
        disable()
    }


    console.debug(`current stack size: `, pageStack.size())
}

export function popPage() {
    console.debug(`pop page stack`)

    // stack minus 1
    pageStack.popPage()

    if (pageStack.size() <= 1) {
        enable()
    }

    let top = pageStack.peek()
    if (top == null) {
        console.debug("page stack empty")
        return;
    }

    console.debug(`current stack size: `, pageStack.size())

    console.debug(`the current page: `, top)

    let pageName = top.name as string
    let params = top.params
    let option = {
        name: pageName,
        params: {}
    }
    if (params != null) {
        option.params = params
    }
    console.debug(`page into stack: `, option)
    // go back to the previous page
    router.replace(option)
}

export function replacePage(name: string, params: any) {
    // null params breaks the router.push and router.replace
    if (params == null) {
        params = {}
    }

    console.debug(`replace page stack`)

    // go to the next page
    router.replace({name, params})

    // stack plus 1
    pageStack.popPage()
    pageStack.pushPage(name, params)

    console.debug(`current stack size: `, pageStack.size())
}

function closeIt() {
    pageStack.clear()
}

window.onbeforeunload = closeIt;

export default pageStack;
