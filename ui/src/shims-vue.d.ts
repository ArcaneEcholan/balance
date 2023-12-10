declare module '*.vue' {
    import Vue from 'vue';
    Vue.prototype.$customFuncs = {
        getRef: (refName: string) => any,
        getHtmlElem: (refName: string) => HTMLElement,
    };
    export default Vue;
}
