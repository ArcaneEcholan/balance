// @ts-ignore
import Cookies from 'js-cookie';

const state = {
    token: Cookies.get('token'),
    username: Cookies.get('username'),
    configs: Cookies.get('configs'),
};

let setCookie = (key: string, value: any) => {
    if (value == null) {
        Cookies.remove(key);
    } else {
        Cookies.set(key, value);
    }
};

const mutations = {
    SET_CONFIGS(state: any, configs: any) {
        state.configs = configs;
        setCookie('configs', configs);
    },
    SET_TOKEN(state: any, token: string | null) {
        state.token = token;
        setCookie('token', token);
    },
    SET_USERNAME(state: any, username: string | null) {
        state.username = username;
        setCookie('username', username);
    },
    LOGOUT(state: any) {
        state.token = null;
        state.username = null;
        state.configs = [];
        Cookies.remove('token');
        Cookies.remove('username');
        Cookies.remove('configs');
    },
};

const actions = {};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
