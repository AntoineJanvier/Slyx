const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

const send = require('../../answerToSockets');

module.exports = {
    disconnectUser: function(socket, json, clients) {
        User.find({
            where: { userid: json.me }
        }).then(user => {
            return Contact.findAll({
                where : {user: user.userid, status: 'ACCEPTED'}
            }).then(contacts => {
                if (contacts && contacts.length > 0) {
                    let r1 = [];
                    for (let k of contacts)
                        r1.push(k.contact);
                    return User.findAll({
                        where: {userid: {$in: r1}}
                    }).then(users => {
                        let r = [];
                        for (let k of users)
                            r.push(k.userid);
                        send.toClients(clients, r, JSON.stringify({
                            ACTION: 'CONTACT_DISCONNECTION',
                            CONTACT_ID: user.userid
                        }));
                        return user.update({connected: true}, {fields: ['connected']});
                    })
                }
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockDisconnectUser B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockDisconnectUser A'}) + '\n');
        });
    }
};