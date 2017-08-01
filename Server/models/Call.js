'use strict';

module.exports = (sequelize, DataTypes) => {
    const Call = sequelize.define('Call', {
        id: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        begin: {type: DataTypes.DATE},
        end: {type: DataTypes.DATE},
        duration: {type: DataTypes.DATE},
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    Call.associate = function (models) {
        Call.hasOne(models.User, {
            foreignKey: 'from'
        });
        Call.hasOne(models.User, {
            foreignKey: 'to'
        });
    };
    Call.prototype.responsify = function () {
        return {
            type: 'call',
            from: this.from.responsify(),
            to: this.to.responsify(),
            begin: this.begin,
            end: this.end,
            duration: this.duration
        };
    };
    return Call;
};