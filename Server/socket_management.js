function socketOnData(data) {
    console.log('SOCKET_ON_DATA');
    console.log(data);
}

function socketOnClose(data) {
    console.log('SOCKET_ON_CLOSE');
    console.log(data);
}

function socketOnError(data) {
    console.log('SOCKET_ON_ERROR');
    console.log(data);
}