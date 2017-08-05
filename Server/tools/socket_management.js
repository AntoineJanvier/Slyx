const passwordHash = require('password-hash');

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

module.exports = {
    sockGetUpdate: function (socket) {
        socket.write(JSON.stringify({
            version: "1.0.0"
        }));
    },
    sockConnect: function (socket, json) {
        return User.find({
            where: {'email': json.email}
        }).then(function (user) {
            if (user) {
                if (passwordHash.verify(json.password, user.pwd)) {
                    socket.write(JSON.stringify(user.responsify()) + '\n');
                } else {
                    console.log('Bad login input');
                    socket.write({} + '\n');
                }
            } else {
                console.log('No user');
                socket.write({} + '\n');
            }
        }).catch(err => {
            console.log(err);
            socket.write({} + '\n');
        });
    }
};