// @ts-ignore
import Cookies from 'js-cookie';

const state = {
    token: Cookies.get('token'),
    username: Cookies.get('username'),
};

const mutations = {
    SET_TOKEN(state: any, token: string) {
        state.token = token;
        Cookies.set('token', token);
    },
    SET_USERNAME(state: any, username: string) {
        state.username = username;
        Cookies.set('username', username);
    },
};

const actions = {};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
