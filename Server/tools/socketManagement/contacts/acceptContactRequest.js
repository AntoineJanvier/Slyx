const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;
const send = require('../../answerToSockets');

module.exports = {
    acceptContactRequest: function (socket, json, clients) {
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
    }
};