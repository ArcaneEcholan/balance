import Vue from 'vue';
import Vuex from 'vuex';
import development from './modules/development';

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        scrollPosition: 0,
    },
    getters: {
        debugView: (state) => {
            // @ts-ignore
            return state.development.debugView;
        },
        logEnabled: (state) => {
            // @ts-ignore
            return state.development.logEnabled;
        },
        scrollPosition: (state) => {
            // @ts-ignore
            return state.scrollPosition;
        }
    },
    mutations: {
        setScrollPosition(state, scrollPosition) {
            state.scrollPosition = scrollPosition;
        }
    },
    actions: {},
    modules: {
        development,
    },
});
