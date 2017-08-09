'use strict';

module.exports = (sequelize, DataTypes) => {
    const Contact = sequelize.define('Contact', {
        contactid: {type: DataTypes.BIGINT, autoIncrement: true, primaryKey: true},
        status: {type: DataTypes.STRING}
    }, {
        paranoid: true,
        underscored: true,
        freezeTableName: true,
    });
    Contact.associate = function (models) {
        Contact.belongsTo(models.User, {
            as: 'user1',
            foreignKey: 'user'
        });
        Contact.belongsTo(models.User, {
            as: 'user2',
            foreignKey: 'contact'
        });
    };
    Contact.prototype.responsify = function () {
        return {
            type: 'CONTACT',
            id: this.contactid,
            user: this.user,
            contact: this.contact,
            status: this.status
        };
    };
    return Contact;
};