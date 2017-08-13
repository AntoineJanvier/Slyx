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
            }).then(contacts1 => {

                return Contact.findAll({
                    where : {
                        user: user.userid,
                        status: 'ACCEPTED'
                    }
                }).then(contacts2 => {
                    let userIDs = [];
                    for (let c of contacts1)
                        userIDs.push(c.contact);
                    for (let c of contacts2)
                        userIDs.push(c.contact);

                    return User.findAll({
                        where: {userid: {$in: userIDs}}
                    }).then(userContacts => {
                        let resp = [];
                        for (let uc of userContacts)
                            resp.push(uc.responsify());
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
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION'}) + '\n');
        });
    },
    sockAddNewContact: function (socket, json) {
        User.find({
            where: {userid: json.me}
        }).then(user => {
            return User.find({
                where: { userid: json.userid }
            }).then(user_requested => {
                return Contact.find({
                    where: { user: user.userid, contact: user_requested.userid }
                }).then(contact => {
                    if (!contact) {
                        return Contact.find({
                            where: { user: user_requested.userid, contact: user.userid }
                        }).then(contact => {
                            if (!contact) {
                                return Contact.create({
                                    user: user.userid,
                                    contact: user_requested.userid,
                                    status: 'PENDING'
                                }).then(contact => {
                                    socket.write(JSON.stringify({request: 'OK'}) + '\n');
                                }).catch(err => {
                                    console.log(err);
                                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_F'}) + '\n');});
                            } else
                                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_E'}) + '\n');
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_D'}) + '\n');
                        });
                    } else
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_C'}) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_B'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_A'}) + '\n');
            });
        });
    },
    sockGetUsersNotInContactList: function (socket, json) {
        User.find({
            where: { userid: json.userid }
        }).then(user => {
            return Contact.findAll({
                where : {user: user.userid, status: {$in: ['ACCEPTED', 'PENDING']}}
            }).then(contacts => {
                let userIDs = [];
                for (let c of contacts)
                    userIDs.push(c.contact);
                userIDs.push(user.userid);
                return User.findAll({
                    where: {userid: {$notIn: userIDs}}
                }).then(userContacts => {
                    let resp = [];
                    for (let uc of userContacts)
                        resp.push(uc.responsify());
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
    sockGetPendingContactRequests: function (socket, json) {
        User.find({
            where: { userid: json.userid }
        }).then(user => {
            return Contact.findAll({
                where : {contact: user.userid, status: 'PENDING'}
            }).then(contacts => {
                let userIDs = [];
                for (let c of contacts)
                    userIDs.push(c.user);
                return User.findAll({
                    where: {userid: {$in: userIDs}}
                }).then(userContacts => {
                    let resp = [];
                    for (let uc of userContacts)
                        resp.push(uc.responsify());
                    socket.write(JSON.stringify(resp) + '\n');
                    socket.Contacts = resp;
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_A'}) + '\n');
        });
    },
    sockGetMessagesOfContact: function (socket, json) {
        /**
         * TODO : Get UserA, UserB, Contact, THEN, messages linked to Contact
         */
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
                            for (let m of messages)
                                resp.push(m.responsify());
                            socket.write(JSON.stringify(resp) + '\n');
                            socket.Messages = resp;
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_E'}) + '\n');
                        });
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_A'}) + '\n');
        });
    },
    sockAcceptContactRequest: function (socket, json) {
        User.find({
            where: {userid: json.u1userid}
        }).then(u1 => {
            return User.find({
                where: {userid: json.u2userid}
            }).then(u2 => {
                return Contact.find({
                    where: {user: u2.userid, contact: u1.userid, status: 'PENDING'}
                }).then(contact => {
                    return contact.update({
                        status: 'ACCEPTED'
                    }, {
                        fields: ['status']
                    }).then(() => {
                        socket.write(JSON.stringify({request: 'OK'}));
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_A'}) + '\n');
        });
    },
    sockRejectContactRequest: function (socket, json) {
        console.log('A');
        User.find({
            where: {userid: json.u1userid}
        }).then(u1 => {
            console.log('B');
            return User.find({
                where: {userid: json.u2userid}
            }).then(u2 => {
                console.log('C');
                return Contact.find({
                    where: {user: u1.userid, contact: u2.userid, status: 'PENDING'}
                }).then(contact => {
                    console.log('D');
                    socket.write(JSON.stringify({request: 'OK'}));
                    return contact.destroy();
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION_A'}) + '\n');
        });
    }
};