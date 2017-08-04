const http = require('http');
const querystring = require('querystring');
const sa = require('superagent');
const XMLHttpRequest = require('xmlhttprequest');
const passwordHash = require('password-hash');

const models = require('../models');
const User = models.User;


module.exports = {
    handleClientRequest: function (jsonObj) {
        switch (jsonObj.request) {
            case 'CONNECTION':
                User.find({
                    where: {'email': jsonObj.email}
                }).then(user => {
                    if (user) {
                        if (passwordHash.verify(jsonObj.password, user.pwd)) {
                            console.log();
                            console.log();
                            console.log(user.responsify());
                            console.log();
                            console.log();
                            return user.responsify();
                        } else
                            console.log('Bad login input');
                    } else
                        console.log('No user');
                }).catch(err => {
                    console.log(err);
                });
                break;
            default:
                return {};
        }
    }
};