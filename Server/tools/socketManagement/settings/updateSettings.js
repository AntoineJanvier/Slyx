const models = require('../../../models/index');
const User = models.User;
const Setting = models.Setting;

module.exports = {
    updateSettings: function (socket, json) {
        User.find({
            where: {
                userid: json.me
            }
        }).then(user => {
            return Setting.find({
                where: {user: user.userid}
            }).then(setting => {
                return setting.update({
                    sounds: json.sounds,
                    volume: json.volume,
                    notifications: json.notifications,
                    calls: json.calls,
                    messages: json.messages,
                    connections: json.connections,
                }, {
                    fields: ['sounds', 'volume', 'notifications', 'calls', 'messages', 'connections']
                });
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