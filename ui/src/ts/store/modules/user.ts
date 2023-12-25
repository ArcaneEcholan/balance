const state = {
    token: null,
};

const mutations = {
    SET_TOKEN(state: any, token: string) {
        state.token = token;
    },
};

const actions = {};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
