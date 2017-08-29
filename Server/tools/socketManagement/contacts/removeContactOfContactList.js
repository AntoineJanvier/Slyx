const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

const send = require('../../answerToSockets');

module.exports = {
    removeContactOfContactList: function (socket, json, clients) {
        User.find({
            where: {userid: json.me}
        }).then(user => {
            return User.find({
                where: {userid: json.userToRemove}
            }).then(userToRemove => {
                return Contact.find({
                    where: {user: user.userid, contact: userToRemove.userid}
                }).then(contact1 => {
                    return contact1.destroy().then(() => {
                        return Contact.find({
                            where: {user: userToRemove.userid, contact: user.userid}
                        }).then(contact2 => {
                            return contact2.destroy().then(() => {
                                let r = [user.userid, userToRemove.userid];
                                send.toClients(clients, r, JSON.stringify({
                                    ACTION: 'CONTACT_REMOVE',
                                    USER_A: user.userid,
                                    USER_B: userToRemove.userid
                                }));
                            })
                        })
                    })
                })
            })
        })
    }
};