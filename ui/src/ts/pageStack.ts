import router from "@/router";
import {Notification} from "element-ui";

class PageStack {
    private stackSize: number = 0

    constructor() {
        this.stackSize = 0;
    }

    push() {
        this.stackSize++;
    }

    pop() {
        this.stackSize--;
    }

    getStackSize() {
        return this.stackSize;
    }

    isBottom() {
        return this.stackSize === 0;
    }

    clear() {
        this.stackSize = 0;
    }

}

let pageStack = new PageStack()

function popPage() {

    // go back to the previous page
    router.go(-1)
    // stack minus 1
    pageStack.pop()
    if (pageStack.isBottom()) {
        // if the page stack is only left with one page, this page should be able to scroll
        document.body.style.overflowY = 'auto'
    }
}

function pushPage(path: string) {

    // go to the next page
    router.push(path)
    // stack plus 1
    pageStack.push()
    // prevent the most basic page from scrolling
    document.body.style.overflowY = 'hidden'
}

function closeIt() {
    pageStack.clear()
    Notification.info("clear stack size")
}

window.onbeforeunload = closeIt;

export default pageStack;

export {popPage, pushPage};
