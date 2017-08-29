const express = require('express');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

let getUserMedia = require('getusermedia');


router.get('/', function (req, res) {
    let sess = req.session;
    let isConnected = false;
    if (sess.email)
        isConnected = true;
    res.render('index', {title: 'Slyx', isConnected: isConnected});
});

router.get('/sign', function (req, res) {
    res.render('sign', {
        title: 'Sign In or Sign Up',
        t1: 'Sign In',
        t2: 'Sign Up'
    });
});

router.get('/room/:from/:to/', function (req, res) {
    User.find({
        where: {userid: parseInt(req.params.from)}
    }).then(user1 => {
        return User.find({
            where: {userid: parseInt(req.params.to)}
        }).then(user2 => {
            peer.on('open', function(id) {
                console.log('My peer ID is: ' + id);
            });
            res.render('room', {
                title: 'Call between ' + user1.first_name + ' and ' + user2.first_name,
                u1: user1,
                u2: user2,
                RTC: require('rtc')
            });
        }).catch(err => {
            console.log('B');
            throw err;
        });
    }).catch(err => {
        console.log('A');
        throw err;
    });
});

module.exports = router;
