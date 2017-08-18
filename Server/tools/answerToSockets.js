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
        console.log('SEND TO MANY CLIENTS');
        for (let c of clients) {
            if (c.User.userid in clientsArray) {
                c.write(toSend + '\n');
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