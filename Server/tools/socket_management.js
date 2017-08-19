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
                } else {
                    console.log('Bad login input');
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockConnect C'}) + '\n');
                }
            } else {
                console.log('No user');
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockConnect B'}) + '\n');
            }
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockConnect A'}) + '\n');
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
                        resp.ACTION = "GET_CONTACTS";
                        socket.write(JSON.stringify({
                            ACTION: 'GET_CONTACTS',
                            CONTACTS: resp
                        }) + '\n');
                        // socket.Contacts = resp;
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetContacts D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetContacts C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetContacts B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetContacts A'}) + '\n');
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
                                }).then(contact => {
                                    let j = user_requested.responsify();
                                    j.ACTION = "CONTACT_REQUEST";
                                    send.toClient(clients, user_requested.userid, JSON.stringify(j));
                                }).catch(err => {
                                    console.log(err);
                                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact F'}) + '\n');});
                            } else
                                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact E'}) + '\n');
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact D'}) + '\n');
                        });
                    } else
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact C'}) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact B'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAddNewContact A'}) + '\n');
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
                    // resp.ACTION = 'GET_USERS_NOT_IN_CONTACT_LIST';
                    socket.write(JSON.stringify({
                        ACTION: 'GET_USERS_NOT_IN_CONTACT_LIST',
                        CONTACTS: resp
                    }) + '\n');
                    // socket.write(JSON.stringify(resp) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetUsersNotInContactList C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetUsersNotInContactList B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetUsersNotInContactList A'}) + '\n');
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
                    send.toClient(clients, user.userid, JSON.stringify(resp));
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetPendingContactRequests C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetPendingContactRequests B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetPendingContactRequests A'}) + '\n');
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
                    where: {
                        user: user1.userid,
                        contact: user2.userid
                    }
                }).then(contact1 => {
                    // let contactIDs = [];
                    // if (contact1)
                    //     for (let c of contact1)
                    //         contactIDs.push(c.contactid);
                    return Contact.find({
                        where: {
                            user: user2.userid,
                            contact: user1.userid
                        }
                    }).then(contact2 => {
                        // if (contact2)
                        //     for (let c of contact2)
                        //         contactIDs.push(c.contactid);
                        return Message.findAll({
                            where: {
                                contact: {
                                    $in: contactIDs
                                }
                            }
                        }).then(messages => {
                            let resp = [];
                            for (let m of messages) {
                                if (m.contact === contact1.contactid)
                                    m.inOrOut = 'OUT';
                                else if (m.contact === contact2.contactid)
                                    m.inOrOut = 'IN';
                                resp.push(m.responsify());
                            }
                            resp.ACTION = 'GET_MESSAGES_OF_CONTACT';
                            socket.write(JSON.stringify(resp) + '\n');
                            socket.Messages = resp;
                        }).catch(err => {
                            console.log(err);
                            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetMessagesOfContact D'}) + '\n');
                        });
                    })
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetMessagesOfContact C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetMessagesOfContact B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockGetMessagesOfContact A'}) + '\n');
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
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAcceptContactRequest D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAcceptContactRequest C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAcceptContactRequest B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockAcceptContactRequest A'}) + '\n');
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
                    // socket.write(JSON.stringify({request: 'OK'}));
                    return contact.destroy();
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockRejectContactRequest C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockRejectContactRequest B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockRejectContactRequest A'}) + '\n');
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
                        console.log('DATE => ' + json.sent);
                        send.toClient(clients, u2.userid, JSON.stringify({
                            ACTION: 'MESSAGE_INCOMING',
                            FROM: u1.userid,
                            CONTENT: message.content
                        }));
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockSendMessageToUser D'}) + '\n');
                    });
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockSendMessageToUser C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockSendMessageToUser B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'REFUSE_CONNECTION - sockSendMessageToUser A'}) + '\n');
        });
    }
};