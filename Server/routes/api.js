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

router.get('/contact/request/:id_user_to_request', function(req, res) {
    res.type('json');

    let idUser = req.body.id_user_to_request || 0;
    if (idUser === 0)
        res.json({err: 'ERR_ID_USER_NOT_PROVIDED', content: {}});

    let sess = req.session;
    if (sess.email) {
        User.find({
            where: { email: sess.email }
        }).then(user => {
            User.find({
                where: { userid: idUser }
            }).then(user_requested => {
                Contact.create({
                    user: user,
                    contact: user_requested,
                    status: 'PENDING'
                }).then(contact => {
                    res.json({ USER_A: contact.user, USER_B: contact.contact, STATUS: contact.status });
                }).catch(err => { res.json({err: 'ERR_CONTACT_CREATE', content: err}); });
            }).catch(err => { res.json({err: 'ERR_USER_FIND_ALL', content: err}); });
        }).catch(err => { res.json({err: 'ERR_USER_FIND', content: err}); });
    } else
        res.json({err: 'ERR_USER_NOT_CONNECTED', content: {}});
});

router.get('/contact/accept/:id_user_to_accept', function(req, res) {
    res.type('json');

    let idUser = req.body.id_user_to_accept || 0;
    if (idUser === 0)
        res.json({err: 'ERR_ID_USER_NOT_PROVIDED', content: {}});

    let sess = req.session;
    if (sess.email) {
        User.find({
            where: { email: sess.email }
        }).then(user => {
            User.find({
                where: { userid: idUser }
            }).then(user_to_accept => {
                Contact.find({
                    user: user_to_accept,
                    contact: user,
                    status: 'PENDING'
                }).then(contact => {
                    contact.update({
                        status: 'ACCEPTED'
                    }).then(() => {
                        res.json(contact.responsify());
                    }).catch(err => { res.json({err: 'ERR_CONTACT_UPDATE', content: err}); });
                }).catch(err => { res.json({err: 'ERR_CONTACT_FIND', content: err}); });
            }).catch(err => { res.json({err: 'ERR_USER_FIND_ALL', content: err}); });
        }).catch(err => { res.json({err: 'ERR_USER_FIND', content: err}); });
    } else
        res.json({err: 'ERR_USER_NOT_CONNECTED', content: {}});
});

router.get('/contact/get/:id_user_for_contact', function(req, res) {
    res.type('json');

    let idUser = req.body.id_user_for_contact || 0;
    if (idUser === 0)
        res.json({err: 'ERR_ID_USER_NOT_PROVIDED', content: {}});

    let sess = req.session;
    if (sess.email) {
        User.find({
            where: { email: sess.email }
        }).then(user => {
            User.find({
                where: { userid: idUser }
            }).then(user_contact => {
                Contact.find({
                    user: user,
                    contact: user_contact,
                    status: 'ACCEPTED'
                }).then(contact => {
                    res.json(contact.responsify());
                }).catch(err => { res.json({err: 'ERR_CONTACT_FIND', content: err}); });
            }).catch(err => { res.json({err: 'ERR_USER_FIND_ALL', content: err}); });
        }).catch(err => { res.json({err: 'ERR_USER_FIND', content: err}); });
    } else
        res.json({err: 'ERR_USER_NOT_CONNECTED', content: {}});
});

module.exports = router;
