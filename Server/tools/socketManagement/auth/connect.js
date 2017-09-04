const passwordHash = require('password-hash');

const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;
const Setting = models.Setting;

const send = require('../../answerToSockets');

let jsonToReturn = {};

module.exports = {
    connect: function (socket, json, clients) {
        User.find({
            where: {'email': json.email}
        }).then(function (user) {
            if (user) {
                if (passwordHash.verify(json.password, user.pwd)) {
                    return Setting.find({
                        where: {user: user.userid}
                    }).then(setting => {
                        jsonToReturn = user.responsify();
                        if (setting) {
                            jsonToReturn.sounds = setting.sounds;
                            jsonToReturn.volume = setting.volume;
                            jsonToReturn.notifications = setting.notifications;
                            jsonToReturn.calls = setting.calls;
                            jsonToReturn.messages = setting.messages;
                            jsonToReturn.connections = setting.connections;
                        }
                        jsonToReturn.ACTION = "ACCEPT_CONNECTION";
                        socket.write(JSON.stringify(jsonToReturn) + '\n');
                        // send.toClient(clients, user.userid, JSON.stringify({
                        //     ACTION: 'DISCONNECT'
                        // }));

                        return Contact.findAll({
                            where: {user: user.userid}
                        }).then(contacts => {
                            let contactIDs = [];
                            for (let c of contacts)
                                contactIDs.push(c.contact);
                            return User.findAll({
                                where: {userid: {$in: contactIDs}}
                            }).then(users => {
                                let r = [];
                                for (let k of users)
                                    r.push(k.userid);

                                socket.User = user.responsify();

                                send.toClients(clients, r, JSON.stringify({
                                    ACTION: 'CONTACT_CONNECTION',
                                    CONTACT_ID: user.userid
                                }));
                                return user.update({connected: true}, {fields: ['connected']});
                            })
                        })
                    }).catch(err => {
                        console.log(err);
                        socket.write(JSON.stringify({request: 'ERROR - sockConnect D'}) + '\n');
                    });
                } else
                    socket.write(JSON.stringify({request: 'ERROR - sockConnect C'}) + '\n');
            } else
                socket.write(JSON.stringify({request: 'ERROR - sockConnect B'}) + '\n');
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockConnect A'}) + '\n');
        });
    }
};