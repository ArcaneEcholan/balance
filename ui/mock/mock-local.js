const express = require('express');
const cors = require('cors');
const app = express();
// app.use(cors()) // 使用 cors 中间件，允许所有来源的跨域请求

// 手动设置响应头
app.use((req, res, next) => {
    // google需要配置，否则报错cors error
    res.setHeader('Access-Control-Allow-Credentials', 'true');
    // 允许的地址,http://127.0.0.1:9000这样的格式
    res.setHeader('Access-Control-Allow-Origin', req.get('Origin'));
    // 允许跨域请求的方法
    res.setHeader(
        'Access-Control-Allow-Methods',
        'POST, GET, OPTIONS, DELETE, PUT',
    );
    // 允许跨域请求header携带哪些东西
    res.header(
        'Access-Control-Allow-Headers',
        'Origin, X-Requested-With, Content-Type, Accept, If-Modified-Since',
    );
    next();
});

// 引入 mock 中间件
const mockMiddleware = require('./mock-server');

// 将 mock 中间件注册到 Express 应用程序中
mockMiddleware(app);

// 启动 Express 服务器
app.listen(3000, () => {
    console.log('Mock server is running on port 3000');
});
