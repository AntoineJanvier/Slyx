const models = require('../../../models/index');
const User = models.User;
const Setting = models.Setting;

module.exports = {
    getSettings: function (socket, json) {
        User.find({
            where: {userid: json.me}
        }).then(user => {
            return Setting.find({
                where: {user: user.userid}
            }).then(setting => {
                socket.write(JSON.stringify({
                    ACTION: 'GET_SETTINGS',
                    SETTING: setting.responsify()
                }) + '\n');
            }).catch(err => {
                console.log(err);
                socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser A'}) + '\n');
            });
        }).catch(err => {
            console.log(err);
            socket.write(JSON.stringify({request: 'ERROR - sockSendMessageToUser A'}) + '\n');
        });
    }
};