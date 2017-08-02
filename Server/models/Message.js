'use strict';

module.exports = (sequelize, DataTypes) => {
    const Message = sequelize.define('Message', {
        id: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        sent: {type: DataTypes.DATE},
        content: {type: DataTypes.TEXT}
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    // Message.associate = function (models) {
    //     Message.hasOne(models.User, {
    //         foreignKey: 'from'
    //     });
    //     Message.hasOne(models.User, {
    //         foreignKey: 'to'
    //     });
    // };
    Message.prototype.responsify = function () {
        return {
            type: 'message',
            from: this.from.responsify(),
            to: this.to.responsify(),
            sent: this.sent,
            content: this.content
        };
    };
    Message.prototype.inlineResponse = function () {
        return 'MESSAGE;' +
            this.id + ';' +
            this.sent + ';' +
            this.content + ';' +
            this.from.userid + ';' +
            this.to.userid + '\n';
    };
    return Message;
};