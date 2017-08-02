'use strict';

module.exports = (sequelize, DataTypes) => {
    const User = sequelize.define('User', {
            userid: {
                type: DataTypes.BIGINT,
                autoIncrement: true,
                primaryKey: true
            },
            first_name: {type: DataTypes.STRING},
            last_name: {type: DataTypes.STRING},
            age: {type: DataTypes.BIGINT},
            email: {type: DataTypes.STRING},
            pwd: {type: DataTypes.STRING}
        },
        {
            paranoid: true,
            underscored: true,
            freezeTableName: true
        });
    User.prototype.responsify = function () {
        return {
            type: 'USER',
            id: this.userid,
            firstname: this.first_name,
            lastname: this.last_name,
            age: this.age,
            email: this.email
        };
    };
    User.prototype.inlineResponse = function () {
        return 'USER;' +
            this.id + ';' +
            this.first_name + ';' +
            this.last_name + ';' +
            this.age + ';' +
            this.email + '\n';
    };
    return User;
};