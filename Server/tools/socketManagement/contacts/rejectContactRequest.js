const models = require('../../../models/index');
const User = models.User;
const Contact = models.Contact;

module.exports = {
    rejectContactRequest: function (socket, json) {
        User.find({
            where: {userid: json.u1userid}
        }).then(u1 => {
            return User.find({
                where: {userid: json.u2userid}
            }).then(u2 => {
                return Contact.find({
                    where: {user: u2.userid, contact: u1.userid, status: 'PENDING'}
                }).then(contact => {
                    return contact.destroy();
                }).catch(err => {
                    console.log(err);
                    socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest C'}) + '\n');
                });
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest B'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockRejectContactRequest A'}) + '\n');
        });
    }
};