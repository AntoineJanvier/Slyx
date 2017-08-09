'use strict';

module.exports = (sequelize, DataTypes) => {
    const Call = sequelize.define('Call', {
        callid: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        begin: {type: DataTypes.DATE},
        end: {type: DataTypes.DATE},
        duration: {type: DataTypes.DATE},
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    Call.associate = function (models) {
        Call.belongsTo(models.Contact, {
            foreignKey: 'contact'
        });
    };
    Call.prototype.responsify = function () {
        return {
            type: 'CALL',
            id: this.callid,
            contact: this.contact.responsify(),
            begin: this.begin,
            end: this.end,
            duration: this.duration
        };
    };
    return Call;
};