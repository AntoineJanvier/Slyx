'use strict';

module.exports = (sequelize, DataTypes) => {
    const Contact = sequelize.define('Contact', {
        id: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        status: {type: DataTypes.STRING},
        user: {type: DataTypes.BIGINT, foreignKey: true}
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    Contact.associate = function (models) {
        Contact.belongsTo(models.User, {
            foreignKey: 'contact'
        });
    };
    Contact.prototype.responsify = function () {
        return {
            type: 'contact',
            USER: this.user.responsify(),
            CONTACT: this.contact.responsify(),
            STATUS: this.status
        };
    };
    return Contact;
};