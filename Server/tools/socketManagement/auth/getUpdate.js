module.exports = {
    getUpdate: function (socket) {
        socket.write(JSON.stringify({ACTION: 'GET_VERSION_OF_SLYX', version: "1.0.0"}) + '\n');
    }
};