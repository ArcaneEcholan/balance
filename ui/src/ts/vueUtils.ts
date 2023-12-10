export function getRef(vue: any, refname: string) {
    return vue.$refs[refname];
}

export function getHtmlElem(vue: any, refname: string): HTMLElement {
    return vue.$refs[refname] as HTMLElement;
}

export function getVueEl(vue: any, refname: string): HTMLElement {
    return vue.$refs[refname].$el as HTMLElement;
}
