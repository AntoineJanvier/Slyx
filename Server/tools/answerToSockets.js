'use strict';
module.exports = {
    toClient: function (clients, clientID, toSend) {
        for (let c of clients) {
            if (c.User.id === clientID) {
                console.log('TO CLIENT ' + clientID + ' ' + toSend);
                c.write(toSend + '\n');
            }
        }
    },
    toClients: function (clients, clientsArray, toSend) {
        console.log('SEND TO MANY CLIENTS : ' + clientsArray.length);
        for (let i = 0; i < clients.length; i++) {
            console.log('ID => ' + clients[i].User.id);
        }
        for (let i = 0; i < clients.length; i++) {
            for (let j = 0; j < clientsArray.length; j++) {
                console.log('IF ' + clients[i].User.id + ' == ' + clientsArray[j]);
                if (clients[i].User.id === clientsArray[j]) {
                    console.log('\nEQUALS\n');
                    clients[i].write(toSend + '\n');
                }
            }
        }
    },
    toAll: function (clients, toSend) {
        console.log('SEND TO ALL CLIENTS');
        for (let c of clients) {
            c.write(toSend + '\n');
        }
    },
    toAllExcept: function (clients, except, toSend) {
        for (let c of clients) {
            if (c.User.userid !== except) {
                c.write(toSend + '\n');
            }
        }
    }
};