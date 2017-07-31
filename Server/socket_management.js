module.exports = {
    socketOnData: function (data, socket) {
        console.log('SOCKET_ON_DATA');
        console.log(JSON.parse(data));
        socket.write("bonjour");
    },
    socketOnClose: function() {
        console.log('SOCKET_ON_CLOSE');
    },
    socketOnEnd: function() {
        console.log('SOCKET_ON_END');
    },
    socketOnError: function(err) {
        console.log('SOCKET_ON_ERROR');
        throw err;
    }
};