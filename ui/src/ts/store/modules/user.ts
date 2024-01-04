// @ts-ignore
import Cookies from 'js-cookie';

const state = {
    token: Cookies.get('token'),
    username: Cookies.get('username'),
};

const mutations = {
    SET_TOKEN(state: any, token: string | null) {
        state.token = token;
        Cookies.set('token', token);
    },
    SET_USERNAME(state: any, username: string | null) {
        state.username = username;
        Cookies.set('username', username);
    },
    LOGOUT(state: any) {
        state.token = null;
        state.username = null;
        Cookies.remove('token');
        Cookies.remove('username');
    },
};

const actions = {};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
