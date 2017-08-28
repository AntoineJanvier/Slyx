const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;
const Message = models.Message;

const send = require('../../answerToSockets');

module.exports = {
    sendMessageToUser: function (socket, json, clients) {
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