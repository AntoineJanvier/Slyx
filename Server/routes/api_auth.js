const express = require('express');
let passwordHash = require('password-hash');
let router = express.Router();

const models = require('../models');
const User = models.User;

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
                User.create({
                    first_name: u.firstname, last_name: u.lastname, age: u.age, email: u.email, pwd: u.pwd
                }).then(user => {
                    if (user) {
                        res.json({User: user.responsify()});
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
                        res.json({User: user.responsify()});
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
    res.type('json');
    let sess = req.session;
    if (!sess.email)
        res.json({err: 'ERR_NOT_CONNECTED', content: {}});
    else
        req.session.destroy(err => {
            if (err)
                res.json({err: 'ERR_ON_DISCONNECTION', content: err});
            else
                res.json({msg: 'Disconnection OK',});
        });
});

module.exports = router;
