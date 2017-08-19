'use strict';

module.exports = (sequelize, DataTypes) => {
    const Message = sequelize.define('Message', {
        messageid: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        sent: {type: DataTypes.DATE},
        content: {type: DataTypes.TEXT}
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    Message.associate = function (models) {
        Message.belongsTo(models.Contact, {
            foreignKey: 'contact'
        });
    };
    Message.prototype.responsify = function () {
        return {
            type: 'MESSAGE',
            id: this.messageid,
            contact: this.contact,
            sent: (new Date(this.sent).getTime()).toString(),
            content: this.content
        };
    };
    return Message;
};