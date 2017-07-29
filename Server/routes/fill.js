const express = require('express');
let passwordHash = require('password-hash');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

router.get('/fill_a', function (req, res) {
    res.type('json');

    User.create({
        firstname: 'Antoine',
        lastname: 'Janvier',
        age: 21,
        email: 'antoine@janvier.com',
        pwd: passwordHash.generate('tototiti')
    }).then(u1 => {
        User.create({
            firstname: 'Toto',
            lastname: 'Titi',
            age: 12,
            email: 'toto@titi.com',
            pwd: passwordHash.generate('tototiti')
        }).then(u2 => {
            Contact.create({
                user: u1,
                contact: u2,
                status: 'ACCEPTED'
            });
        });
    });


    res.json({msg: 'OK'});
});

module.exports = router;
