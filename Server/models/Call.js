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
        Call.belongsTo(models.User, {
            foreignKey: 'from'
        });
        Call.belongsTo(models.User, {
            foreignKey: 'to'
        });
    };
    Call.prototype.responsify = function () {
        return {
            type: 'CALL',
            from: this.from.responsify(),
            to: this.to.responsify(),
            begin: this.begin,
            end: this.end,
            duration: this.duration
        };
    };
    Call.prototype.inlineResponse = function () {
        return 'CALL;' +
            this.id + ';' +
            this.begin + ';' +
            this.end + ';' +
            this.duration + '\n';
    };
    return Call;
};