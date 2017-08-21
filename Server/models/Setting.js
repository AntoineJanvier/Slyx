'use strict';

module.exports = (sequelize, DataTypes) => {
    const Setting = sequelize.define('Setting', {
            settingid: {
                type: DataTypes.BIGINT,
                autoIncrement: true,
                primaryKey: true
            },
            sounds: {type: DataTypes.BOOLEAN},
            volume: {type: DataTypes.INTEGER},
            notifications: {type: DataTypes.BOOLEAN},
            calls: {type: DataTypes.BOOLEAN},
            messages: {type: DataTypes.BOOLEAN},
            connections: {type: DataTypes.BOOLEAN}
        },
        {
            paranoid: true,
            underscored: true,
            freezeTableName: true
        });
    Setting.associate = function (models) {
        Setting.belongsTo(models.User, {
            foreignKey: 'user'
        });
    };
    Setting.prototype.responsify = function () {
        return {
            type: 'SETTINGS',
            id: this.settingid,
            sounds: this.sounds,
            volume: this.volume,
            notifications: this.notifications,
            calls: this.calls,
            messages: this.messages,
            connections: this.connections
        };
    };
    return Setting;
};