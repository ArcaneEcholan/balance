// @ts-ignore

const state = {
    token: localStorage.getItem('token'),
    username: localStorage.getItem('username'),
    configs: localStorage.getItem('configs'),
};

let setLocalStorage = (key: string, value: any) => {
    if (value == null) {
        localStorage.removeItem(key);
    } else {
        localStorage.setItem(key, value);
    }
};

const mutations = {
    SET_CONFIGS(state: any, configs: any) {
        state.configs = configs;
        setLocalStorage('configs', configs);
    },
    SET_TOKEN(state: any, token: string | null) {
        state.token = token;
        setLocalStorage('token', token);
    },
    SET_USERNAME(state: any, username: string | null) {
        state.username = username;
        setLocalStorage('username', username);
    },
    LOGOUT(state: any) {
        state.token = null;
        state.username = null;
        state.configs = [];
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('configs');
    },
};

const actions = {};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
