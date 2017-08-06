const passwordHash = require('password-hash');

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

let jsonToReturn = {};

module.exports = {
    sockGetUpdate: function (socket) {
        jsonToReturn.request = "GET_VERSION";
        socket.write(JSON.stringify({
            version: "1.0.0"
        }) + '\n');
    },
    sockConnect: function (socket, json) {
        return User.find({
            where: {'email': json.email}
        }).then(function (user) {
            if (user) {
                if (passwordHash.verify(json.password, user.pwd)) {

                    jsonToReturn = user.responsify();
                    jsonToReturn.request = "ACCEPT_CONNECTION";
                    console.log(JSON.stringify(jsonToReturn));
                    socket.write(JSON.stringify(jsonToReturn) + '\n');

                    socket.User = user.responsify();

                } else {
                    console.log('Bad login input');
                    socket.write(JSON.stringify({
                        request: 'REFUSE_CONNECTION'
                    }) + '\n');
                }
            } else {
                console.log('No user');
                socket.write(JSON.stringify({
                    request: 'REFUSE_CONNECTION'
                }) + '\n');
            }
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({
                request: 'REFUSE_CONNECTION'
            }) + '\n');
        });
    }
};