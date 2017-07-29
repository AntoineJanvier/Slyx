const express = require('express');
let router = express.Router();

const models = require('../models');
const User = models.User;
const Contact = models.Contact;

router.get('/users', function(req, res) {
    res.type('json');
    let sess = req.session;
    if (sess.email) {
        User.find({
            where: { email: sess.email }
        }).then(user => {
            User.findAll({
                where: { email: { $ne: user.email } }
            }).then(users => {
                let resp = [];
                for (let u of users)
                    resp.push(u.responsify());
                res.json(resp);
            }).catch(err => { res.json({err: 'ERR_USER_FIND_ALL', content: err}); });
        }).catch(err => { res.json({err: 'ERR_USER_FIND', content: err}); });
    } else
        res.json({err: 'ERR_USER_NOT_CONNECTED', content: {}});
});

router.get('/contacts', function(req, res) {
    res.type('json');

    let sess = req.session;
    if (sess.email) {
        User.find({
            where: { email: sess.email }
        }).then(user => {
            Contact.findAll({
                user: user,
                status: 'ACCEPTED'
            }).then(contacts => {
                let resp = [];
                for (let c of contacts)
                    resp.push(c.responsify());
                res.json(resp);
            }).catch(err => { res.json({err: 'ERR_CONTACT_FIND', content: err}); });
        }).catch(err => { res.json({err: 'ERR_USER_FIND', content: err}); });
    } else
        res.json({err: 'ERR_USER_NOT_CONNECTED', content: {}});
});

module.exports = router;
