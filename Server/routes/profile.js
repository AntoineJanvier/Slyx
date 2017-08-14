let express = require('express');
let router = express.Router();
const models = require('../models');
const passwordHash = require('password-hash');
const User = models.User,
    Call = models.Call,
    Message = models.Message;

router.get('/', function (req, res) {
    let sess = req.session;
    let isConnected = false;
    if (sess.email)
        isConnected = true;
    if (isConnected) {
        User.find({
            where: {email: sess.email}
        }).then(user => {
            res.render('profile', {
                title: 'Manage your profile',
                isConnected: isConnected,
                User: user.responsify()
            });
        }).catch(error => {
            res.render('error', {
                message: 'Error on finding user',
                error: error
            });
        });
    } else {
        res.render('error', {
            message: 'Not connected',
            error: {
                status: 'HANDLED',
                stack: ''
            }
        })
    }
});

router.post('/change_icon', function (req, res) {

    let new_picture = req.body.icon;

    let sess = req.session;
    let isConnected = false;
    if (sess.email)
        isConnected = true;
    if (isConnected && new_picture)
        User.find({
            where: {email: sess.email}
        }).then(user => {
            return user.update({picture: new_picture}, {fields: ['picture']
            }).then(u => {
                res.render('profile', {
                    title: 'Icon updated',
                    isConnected: isConnected,
                    User: u.responsify()
                });
            });
        }).catch(err => {
            res.json({err: 'ERR_USER_FIND_AND_COUNT_ALL', content: err});
        });
    else
        res.redirect('/');
});


router.post('/change_password', function (req, res) {

    let p1 = req.body.password1;
    let p2 = req.body.password2;

    let new_password = undefined;
    if (p1 === p2)
        new_password = passwordHash.generate(p1);

    let sess = req.session;
    let isConnected = false;
    if (sess.email)
        isConnected = true;
    if (isConnected && new_password)
        User.find({
            where: {email: sess.email}
        }).then(user => {
            return user.update({password: new_password}, {fields: ['password']
            }).then(u => {
                res.render('profile', {
                    title: 'Password updated',
                    isConnected: isConnected,
                    User: u.responsify()
                });
            });
        }).catch(err => {
            res.json({err: 'ERR_USER_FIND_AND_COUNT_ALL', content: err});
        });
    else
        res.redirect('/');
});

module.exports = router;
