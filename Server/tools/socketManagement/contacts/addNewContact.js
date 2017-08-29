const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

const send = require('../../answerToSockets');

module.exports = {
    addNewContact: function (socket, json, clients) {
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
                                    let j = user.responsify();
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
    }
};