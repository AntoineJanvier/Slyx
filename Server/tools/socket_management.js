const db = require('./goGetInDB');

module.exports = {
    socketOnData: function (data, socket) {
        console.log('SOCKET_ON_DATA');
        let o = JSON.parse(data);
        console.log(o);
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