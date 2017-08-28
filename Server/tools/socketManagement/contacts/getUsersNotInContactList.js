const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

module.exports = {
    getUsersNotInContactList: function (socket, json) {
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
    }
};