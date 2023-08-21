import router from "@/router";

class PageStack {
    private stackSize: number = 0

    constructor() {
        let stackSize = window.localStorage.getItem("pageStackSize")

        if (stackSize === null) {
            stackSize = '0';
        }

        this.stackSize = parseInt(stackSize);
    }

    push() {
        this.stackSize++;
        window.localStorage.setItem("pageStackSize", this.stackSize.toString());

    }

    pop() {
        this.stackSize--;
        window.localStorage.setItem("pageStackSize", this.stackSize.toString());
    }

    getStackSize() {
        return this.stackSize;
    }

    isBottom() {
        return this.stackSize === 0;
    }

    clear() {
        this.stackSize = 0;
        window.localStorage.setItem("pageStackSize", this.stackSize.toString());
    }

}

let pageStack = new PageStack()

function popPage() {
    debugger
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
    debugger
    // go to the next page
    router.push(path)
    // stack plus 1
    pageStack.push()
    // prevent the most basic page from scrolling
    document.body.style.overflowY = 'hidden'
}

export default pageStack;

export {popPage, pushPage};
