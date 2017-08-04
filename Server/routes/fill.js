const express = require('express');
let passwordHash = require('password-hash');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;
const Call = models.Call;
const Message = models.Message;

router.get('/fill_a', function (req, res) {
    res.type('json');

    User.create({
        first_name: 'Antoine',
        last_name: 'Janvier',
        age: 21,
        email: 'antoine@janvier.com',
        pwd: passwordHash.generate('tototiti')
    }).then(u1 => {
        User.create({
            first_name: 'Toto',
            last_name: 'Titi',
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

router.get('/fill_calls', function (req, res) {
    res.type('json');

    User.find({
        where: {userid: 4}
    }).then(u1 => {
        return User.find({
            where: {userid: 3}
        }).then(u2 => {
            let d1 = new Date();
            let d2 = d1.getHours() + 1;
            return Call.create({
                from: u1,
                to: u2,
                begin: d1,
                end: d2,
                duration: d2 - d1
            }).then(() => {
                res.json({
                    status: 'ok'
                })
            })
        })
    })
});

router.get('/fill_msg', function (req, res) {
    res.type('json');

    User.find({
        where: {userid: 4}
    }).then(u1 => {
        return User.find({
            where: {userid: 3}
        }).then(u2 => {
            let d1 = new Date();
            let d2 = d1.getHours() + 1;
            return Message.create({
                from: u1,
                to: u2,
                content: 'test 1 : blablabla',
                end: d2,
                duration: d2 - d1
            }).then(() => {
                return Message.create({
                    from: u1,
                    to: u2,
                    content: 'test 1 : blablabla',
                    end: d2,
                    duration: d2 - d1
                }).then(() => {
                    return Message.create({
                        from: u1,
                        to: u2,
                        content: 'test 1 : blablabla',
                        end: d2,
                        duration: d2 - d1
                    }).then(() => {
                        res.json({
                            status: 'ok'
                        });
                    })
                })

            })
        })
    })
});

module.exports = router;
