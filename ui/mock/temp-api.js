module.exports = [
    {
        url: '/username/existence',
        type: 'get',
        response: (config) => {
            return {
                exist: true,
            };
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
