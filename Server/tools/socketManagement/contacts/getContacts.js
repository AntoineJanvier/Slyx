const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

module.exports = {
    getContacts: function (socket, json) {
        User.find({
            where: { userid: json.userid }
        }).then(user => {
            return Contact.findAll({
                where : {user: user.userid, status: 'ACCEPTED'}
            }).then(contacts => {
                let userIDs = [];
                for (let c of contacts)
                    userIDs.push(c.contact);
                return User.findAll({
                    where: {userid: {$in: userIDs}}
                }).then(userContacts => {
                    let resp = [];
                    for (let uc of userContacts)
                        resp.push(uc.responsify());
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
                socket.write(JSON.stringify({request: 'ERROR - sockGetContacts B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetContacts A'}) + '\n');
        });
    }
};