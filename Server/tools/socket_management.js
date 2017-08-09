const passwordHash = require('password-hash');

const models = require('../models');
const User = models.User;
const Contact = models.Contact;
const Message = models.Message;

let jsonToReturn = {};

/*
TODO : Make errors to return (in catch or else) to print it in the app
 */

module.exports = {
    sockGetUpdate: function (socket) {
        jsonToReturn.request = "GET_VERSION";
        socket.write(JSON.stringify({version: "1.0.0"}) + '\n');
    },
    sockConnect: function (socket, json) {
        User.find({
            where: {'email': json.email}
        }).then(function (user) {
            if (user) {
                if (passwordHash.verify(json.password, user.pwd)) {

                    jsonToReturn = user.responsify();
                    jsonToReturn.request = "ACCEPT_CONNECTION";

                    socket.write(JSON.stringify(jsonToReturn) + '\n');
                    socket.User = user.responsify();

                } else {
                    console.log('Bad login input');
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
                }
            } else {
                console.log('No user');
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
            }
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
        });
    },
    sockGetContacts: function (socket, json) {
        User.find({
            where: { email: json.email }
        }).then(user => {
            return Contact.findAll({
                user: user,
                status: 'ACCEPTED'
            }).then(contacts => {
                let userIDs = [];
                for (let c of contacts) {
                    userIDs.push(c.contact);
                }

                return User.findAll({
                    where: {userid: {$in: userIDs}}
                }).then(userContacts => {
                    let resp = [];
                    for (let uc of userContacts) {
                        resp.push(uc.responsify());
                    }
                    socket.write(JSON.stringify(resp) + '\n');
                    socket.Contacts = resp;
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
                });
                let j = {
                    nbContacts: 2,
                    1: {
                        firstname: 'Titi',
                        lastname: 'Toto',
                        email: '',
                        age: 21
                    },
                    2: {
                        firstname: 'Titi',
                        lastname: 'Toto',
                        email: '',
                        age: 21
                    }
                };

                socket.write(JSON.stringify(userIDs) + '\n');
                socket.Contacts = userIDs;
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
        });
    },
    sockGetMessagesOfContact: function (socket, json) {
        User.find({
            where: { userid: json.u1_userid }
        }).then(user1 => {
            return User.find({
                where: { userid: json.u2_userid }
            }).then(user2 => {
                return Contact.find({
                    where: {user: user1, contact: user2}
                }).then(contact => {
                    return Message.findAll({
                        users: contact
                    }).then(messages => {
                        let resp = {};
                        for (let m of messages) {
                            let msgResp = m.responsify();
                            let n = msgResp.messageid;
                            resp.n = msgResp;

                            socket.write(resp + '\n');
                            socket.Messages = resp;
                        }
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
        });
    }
};