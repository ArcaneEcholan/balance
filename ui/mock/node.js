const WebSocket = require('websocket').server;
const http = require('http');
const server = http.createServer();

server.listen(11005, () => {
    console.log('WebSocket server is listening on port 11005');
});

const wsServer = new WebSocket({
    httpServer: server,
    autoAcceptConnections: true,
});

wsServer.on('connect', (connection) => {
    console.log('WebSocket connection established');

    // 监听消息事件
    connection.on('message', (message) => {
        console.log(`Received message: ${message.utf8Data}`);

        if (!message.utf8Data) {
            console.log('接收到空数据');
        }
        try {
            const receiveData = JSON.parse(message.utf8Data);
            if (receiveData.type == 'response') {
                connection.send(
                    JSON.stringify({
                        task_id: '11111',
                        content: '2222',
                        content_type: 'content_type',
                        type: 'http_response',
                    }),
                );
            } else {
                connection.send(
                    JSON.stringify({
                        task_id: '11111',
                        content: '2222',
                        type: 'ukey_request',
                    }),
                );
            }
        } catch (error) {
            console.log('error', error);
        }
    });

    // 监听关闭事件
    connection.on('close', () => {
        console.log('WebSocket connection closed');
    });
});
