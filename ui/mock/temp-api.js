module.exports = [
    {
        url: '/user/password_set',
        type: 'post',
        response: (config) => {
            return {
                set: false,
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
        response: (config, response) => {
            response.status(500);
            console.log(response);
            return {
                token: 'test_token',
            };
        },
    },
    {
        url: 'c',
        type: 'post',
        response: (config, response) => {
            return {
                token: 'test_token',
            };
        },
    },
];
