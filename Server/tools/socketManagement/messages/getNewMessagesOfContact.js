const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;
const Message = models.Message;

module.exports = {
    getMessagesOfContact: function (socket, json) {
        User.find({
            where: { userid: json.me }
        }).then(user1 => {
            return User.find({
                where: { userid: json.contact }
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
                            where: {
                                contact: {
                                    $in: contactIDs
                                },
                                messageid: {
                                    $gt: json.idOfLastMessage
                                }
                            }
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
                            resp.ACTION = 'GET_NEW_MESSAGES_OF_CONTACT';
                            socket.write(JSON.stringify({
                                ACTION: 'GET_MESSAGES_OF_CONTACT',
                                CONTACT_ID: user2.userid,
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
    }
};