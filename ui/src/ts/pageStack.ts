import router from "@/router";
import {Notification} from "element-ui";

class StackItem {
    name: string | null = null;
    params: any;
}

class PageStack {
    private stackSize: number = 0

    private stack: StackItem[] = []

    constructor() {
        this.stackSize = 0;
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


    push() {
        this.stackSize++;
    }

    pop() {
        if (this.stackSize <= 0) {
            this.stackSize = 0;
        } else {
            this.stackSize--;
        }
    }

    getStackSize() {
        return this.stackSize;
    }

    isBottom() {
        return this.stackSize === 0;
    }

    clear() {
        this.stackSize = 0;
        this.stack = []
    }

}

let pageStack = new PageStack()

export function gotoPage(popStack: boolean, name: string, params: any) {
    // null params breaks the router.push and router.replace
    if (params == null) {
        params = {}
    }
    if (popStack) {
        // stack minus 1
        pageStack.popPage()

        let top = pageStack.peek()
        if (top == null) {
            return;
        }

        let pageName = top.name as string
        let params = top.params
        let option = {
            name: pageName,
            params: {}
        }
        if (params != null) {
            option.params = params
        }
        // go back to the previous page
        router.replace(option)
    } else {

        if (params != null) {
            // go to the next page
            router.replace({name, params})

            // stack plus 1
            pageStack.pushPage(name, params)
        } else {
            // go to the next page
            router.replace({name})

            // stack plus 1
            pageStack.pushPage(name, null)
        }

    }

    if (pageStack.size() <= 1) {
        // if the page stack is only left with one page, this page should be able to scroll
        document.body.style.overflowY = 'auto'
    }

    if (pageStack.size() >= 2) {
        // prevent the most basic page from scrolling
        document.body.style.overflowY = 'hidden'
    }

    console.debug(`pagestack:${pageStack.size()}`)
}

function closeIt() {
    pageStack.clear()
    Notification.info("clear stack size")
}

window.onbeforeunload = closeIt;

export default pageStack;
