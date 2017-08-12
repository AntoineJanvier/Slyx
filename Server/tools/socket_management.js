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
            where: { userid: json.userid }
        }).then(user => {
            return Contact.findAll({
                where : {
                    user: user.userid,
                    status: 'ACCEPTED'
                }
            }).then(contacts => {
                let userIDs = [];
                for (let c of contacts) {
                    userIDs.push(c.contact);
                }
                console.log('IDS => ' + userIDs);

                return User.findAll({
                    where: {userid: {$in: userIDs}}
                }).then(userContacts => {
                    let resp = [];
                    let nb = 0;
                    for (let uc of userContacts) {
                        nb++;
                        resp.push(uc.responsify());
                    }
                    socket.write(JSON.stringify(resp) + '\n');
                    socket.Contacts = resp;
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
    },
    sockGetMessagesOfContact: function (socket, json) {
        User.find({
            where: { userid: json.u1userid }
        }).then(user1 => {
            return User.find({
                where: { userid: json.u2userid }
            }).then(user2 => {
                return Contact.find({
                    where: {user: user1, contact: user2}
                }).then(contact => {

                    return Contact.find({
                        where: {user: user2, contact: user1}
                    }).then(contact2 => {

                        let contactIDs = [];
                        if (contact)
                            contactIDs.push(contact.contactid);
                        if (contact2)
                            contactIDs.push(contact2.contactid);

                        return Message.findAll({
                            where: {users: {$in: contactIDs}}
                        }).then(messages => {

                            let resp = [];
                            let nb = 0;
                            for (let m of messages) {
                                nb++;
                                resp.push(m.responsify());
                            }
                            socket.write(JSON.stringify(resp) + '\n');
                            socket.Messages = resp;
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
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
        });
    }
};