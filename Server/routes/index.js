const express = require('express');
let router = express.Router();
// const Peer = require('peerjs');


const models = require('../models');
const User = models.User;
const Contact = models.Contact;

let getUserMedia = require('getusermedia');

// let peer = new Peer({key: 'y4hmxzxfng7m0a4i'});


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

router.get('/call/:fromUser/:toUser/', function (req, res) {
    User.find({
        where: {userid: parseInt(req.params.fromUser)}
    }).then(user1 => {
        return User.find({
            where: {userid: parseInt(req.params.toUser)}
        }).then(user2 => {
            res.redirect('/html/index.html');
            // peer.on('open', function(id) {
            //     console.log('My peer ID is: ' + id);
            // });
            // res.render('room', {
            //     title: 'Call between ' + user1.first_name + ' and ' + user2.first_name,
            //     u1: user1,
            //     u2: user2
            // });
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
