const express = require('express');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

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

module.exports = router;
