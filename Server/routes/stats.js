let express = require('express');
let router = express.Router();
const models = require('../models');
const User = models.User,
    Call = models.Call,
    Message = models.Message;

router.get('/', function (req, res) {
    let sess = req.session;
    let isConnected = false;
    if (sess.email)
        isConnected = true;
    if (isConnected)
        User.findAll().then(users => {
            return Call.findAll().then(calls => {
                return Message.findAll().then(messages => {
                    res.render('stats', {
                        title: 'Statistics',
                        users: users,
                        calls: calls,
                        messages: messages,
                        nbUsers: users.length,
                        nbCalls: calls.length,
                        nbMessages: messages.length,
                        isConnected: isConnected
                    });
                }).catch(err => {
                    res.json({err: 'ERR_MESSAGE_FIND_AND_COUNT_ALL', content: err});
                });
            }).catch(err => {
                res.json({err: 'ERR_CALL_FIND_AND_COUNT_ALL', content: err});
            });
        }).catch(err => {
            res.json({err: 'ERR_USER_FIND_AND_COUNT_ALL', content: err});
        });
    else
        res.redirect('/');
});

module.exports = router;
