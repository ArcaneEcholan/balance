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

export function gotoPageWithName(popStack: boolean, name: string, processOption: any) {
    let option = {
        name: name,
    }
    processOption(option)
    // go back to the previous page
    router.push(option)
    if (popStack) {
        // stack minus 1
        pageStack.popPage()
        if (pageStack.empty()) {
            // if the page stack is only left with one page, this page should be able to scroll
            document.body.style.overflowY = 'auto'
        }
    }
}

export function pushPageWithName(name: string, params: any) {
    // go to the next page
    router.push({name, params})
    // stack plus 1
    pageStack.pushPage(name, params)
    // prevent the most basic page from scrolling
    document.body.style.overflowY = 'hidden'
}

function closeIt() {
    pageStack.clear()
    Notification.info("clear stack size")
}

window.onbeforeunload = closeIt;

export default pageStack;
