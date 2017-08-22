const express = require('express');
let passwordHash = require('password-hash');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Setting = models.Setting;

router.post('/sign_up', (req, res) => {
    res.type('json');
    let u = {
        email: req.body.email,
        firstname: req.body.firstname,
        lastname: req.body.lastname,
        age: req.body.age,
        pwd: passwordHash.generate(req.body.pwd)
    };
    if (u.email && u.firstname && u.lastname && u.age && u.pwd) {
        User.find({
            attributes: ['email'], where: {email: u.email}
        }).then(user => {
            if (user)
                res.json({err: 'ERR_USER_ALREADY_CREATED', content: {}});
            else {
                return User.create({
                    first_name: u.firstname, last_name: u.lastname, age: u.age, email: u.email, pwd: u.pwd,
                    picture: 'http://www.freeiconspng.com/uploads/user-icon-png-person-user-profile-icon-20.png'
                }).then(user => {
                    if (user) {
                        return Setting.create({
                            user: user.userid,
                            sounds: true, volume: 100,
                            notifications: true, calls: true, messages: true, connections: true
                        }).then(settings => {
                            res.redirect('/');
                        });
                    } else
                        res.json({err: 'ERR_USER_FIND', content: {}});
                }).catch(err => {
                    throw err;
                });
            }
        }).catch(err => {
            res.json({err: 'ERR_USER_FIND', content: err});
        });
    } else
        res.json({err: 'ERR_POST_INFORMATION_INCORRECT', content: {}});
});

router.post('/change_icon', (req, res) => {

});

router.post('/sign_in', (req, res) => {
    res.type('json');
    let sess = req.session;
    if (sess.email)
        res.json({err: 'ERR_USER_ALREADY_CONNECTED', content: {}});
    else {
        let u = {
            email: req.body.email,
            password: req.body.pwd
        };
        if (u.email && u.password) {
            User.find({
                where: {'email': u.email}
            }).then(user => {
                if (user) {
                    if (passwordHash.verify(u.password, user.pwd)) {
                        sess.userid = user.userid;
                        sess.first_name = user.first_name;
                        sess.last_name = user.last_name;
                        sess.age = user.age;
                        sess.email = user.email;
                        // res.json(user.responsify());
                        res.redirect('/');
                    } else
                        res.json({err: 'ERR_BAD_LOGIN_INPUT', content: {}});
                } else
                    res.json({err: 'ERR_USER_NOT_FOUND', content: {}});
            }).catch(err => {
                res.json({err: 'ERR_USER_FIND', content: err});
            });
        } else
            res.json({err: 'ERR_POST_INFORMATION_INCORRECT', content: {}});
    }
});

router.get('/sign_out', (req, res) => {
    let sess = req.session;
    if (!sess.email)
        res.json({err: 'ERR_NOT_CONNECTED', content: {}});
    else
        req.session.destroy(err => {
            if (err)
                res.json({err: 'ERR_ON_DISCONNECTION', content: err});
            else
                res.render('sign', {title: 'Home'});
        });
});

module.exports = router;
