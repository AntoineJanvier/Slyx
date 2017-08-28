const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

module.exports = {
    getPendingContactRequests: function (socket, json) {
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
                    socket.write(JSON.stringify({
                        ACTION: 'GET_PENDING_CONTACT_REQUEST',
                        CONTACTS: resp
                    }) + '\n');
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockGetPendingContactRequests A'}) + '\n');
        });
    }
};