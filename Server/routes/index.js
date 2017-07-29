const express = require('express');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

router.get('/', function (req, res) {
    res.render('index', {title: 'Slyx'});
});

module.exports = router;
