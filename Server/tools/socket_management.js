const passwordHash = require('password-hash');

const models = require('../models');
const User = models.User;
const Contact = models.Contact;
const Message = models.Message;

const send = require('./answerToSockets');

let jsonToReturn = {};

/*
TODO : Make errors to return (in catch or else) to print it in the app
 */

module.exports = {
    sockGetUpdate: function (socket) {
        socket.write(JSON.stringify({ACTION: 'GET_VERSION_OF_SLYX', version: "1.0.0"}) + '\n');
    },
    sockConnect: function (socket, json) {
        User.find({
            where: {'email': json.email}
        }).then(function (user) {
            if (user) {
                if (passwordHash.verify(json.password, user.pwd)) {
                    jsonToReturn = user.responsify();
                    jsonToReturn.ACTION = "ACCEPT_CONNECTION";
                    socket.write(JSON.stringify(jsonToReturn) + '\n');
                    socket.User = user.responsify();
                } else
                    socket.write(JSON.stringify({request: 'ERROR - sockConnect C'}) + '\n');
            } else
                socket.write(JSON.stringify({request: 'ERROR - sockConnect B'}) + '\n');
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockConnect A'}) + '\n');
        });
    },
    sockGetContacts: function (socket, json) {
        User.find({
            where: { userid: json.userid }
        }).then(user => {
            return Contact.findAll({
                where : {user: user.userid, status: 'ACCEPTED'}
            }).then(contacts1 => {
                return Contact.findAll({
                    where : {user: user.userid, status: 'ACCEPTED'}
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
                        if (resp.length > 0)
                            socket.write(JSON.stringify({
                                ACTION: 'GET_CONTACTS',
                                CONTACTS: resp
                            }) + '\n');
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'ERROR - sockGetContacts D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockGetContacts C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockGetContacts B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetContacts A'}) + '\n');
        });
    },
    sockAddNewContact: function (socket, json, clients) {
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
                                }).then(() => {
                                    let j = user_requested.responsify();
                                    j.ACTION = "CONTACT_REQUEST";
                                    send.toClient(clients, user_requested.userid, JSON.stringify(j));
                                }).catch(err => {
                                    console.log(err);
                                    socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact F'}) + '\n');});
                            } else
                                socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact E'}) + '\n');
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact D'}) + '\n');
                        });
                    } else
                        socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact C'}) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact B'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockAddNewContact A'}) + '\n');
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
                    if (resp.length > 0)
                        socket.write(JSON.stringify({
                            ACTION: 'GET_USERS_NOT_IN_CONTACT_LIST',
                            CONTACTS: resp
                        }) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockGetUsersNotInContactList C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockGetUsersNotInContactList B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetUsersNotInContactList A'}) + '\n');
        });
    },
    sockGetPendingContactRequests: function (socket, json, clients) {
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
                    if (resp.length > 0)
                        socket.write(JSON.stringify({
                            ACTION: 'GET_PENDING_CONTACT_REQUEST',
                            CONTACTS: resp
                        }) + '\n');
                    // send.toClient(clients, user.userid, JSON.stringify(resp));
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests A'}) + '\n');
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
                    where: {user: user1.userid, contact: user2.userid}
                }).then(contact1 => {
                    let contactIDs = [];
                    if (contact1) contactIDs.push(contact1.contactid);
                    return Contact.find({
                        where: {user: user2.userid, contact: user1.userid}
                    }).then(contact2 => {
                        if (contact2) contactIDs.push(contact2.contactid);
                        return Message.findAll({
                            where: {contact: {$in: contactIDs}}
                        }).then(messages => {
                            let resp = [], ior;
                            for (let m of messages) {
                                if (m.contact === contact1.contactid) ior = 'OUT';
                                else if (m.contact === contact2.contactid) ior = 'IN';
                                else ior = 'NONE';
                                let re = m.responsify();
                                re.inOrOut = ior;
                                resp.push(re);
                            }
                            resp.ACTION = 'GET_MESSAGES_OF_CONTACT';
                            socket.write(JSON.stringify({
                                ACTION: 'GET_MESSAGES_OF_CONTACT',
                                MESSAGES: resp
                            }) + '\n');
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'ERROR - sockGetMessagesOfContact D'}) + '\n');
                        });
                    })
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockGetMessagesOfContact C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockGetMessagesOfContact B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetMessagesOfContact A'}) + '\n');
        });
    },
    sockAcceptContactRequest: function (socket, json, clients) {
        User.find({
            where: {userid: json.u1userid}
        }).then(u1 => {
            return User.find({
                where: {userid: json.u2userid}
            }).then(u2 => {
                return Contact.find({
                    where: {user: u2.userid, contact: u1.userid, status: 'PENDING'}
                }).then(contact => {
                    return Contact.create({
                        user: u1.userid, contact: u2.userid, status: 'ACCEPTED'
                    }).then(n_contact => {
                        return contact.update({status: 'ACCEPTED'}, {fields: ['status']
                        }).then(c => {
                            let resp = u2.responsify();
                            resp.ACTION = 'CONTACT_REQUEST_ACCEPTED';
                            send.toClient(clients, u2.userid, JSON.stringify(resp));
                        });
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'ERROR - sockAcceptContactRequest D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockAcceptContactRequest C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockAcceptContactRequest B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockAcceptContactRequest A'}) + '\n');
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
                    return contact.destroy();
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest A'}) + '\n');
        });
    },
    sockSendMessageToUser: function (socket, json, clients) {
        User.find({
            where: {userid: json.from}
        }).then(u1 => {
            return User.find({
                where: {userid: json.to}
            }).then(u2 => {
                return Contact.find({
                    where: {user: u1.userid, contact: u2.userid}
                }).then(contact => {
                    return Message.create({
                        sent: new Date(json.sent), content: json.content, contact: contact.contactid
                    }).then(message => {
                        send.toClient(clients, u2.userid, JSON.stringify({
                            ACTION: 'MESSAGE_INCOMING',
                            MESSAGE_ID: message.messageid,
                            FROM: u1.userid,
                            CONTENT: message.content
                        }));
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser A'}) + '\n');
        });
    }
};