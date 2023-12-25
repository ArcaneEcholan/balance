module.exports = [
    {
        url: '/user/password_set',
        type: 'get',
        response: (config) => {
            return {
                exist: false,
            };
        },
    },
    {
        url: '/mail_delivery/register_code',
        type: 'post',
        response: (config) => {
            return {};
        },
    },
    {
        url: '/user/auth',
        type: 'post',
        response: (config) => {
            return {
                token: 'test_token',
            };
        },
    },
];
