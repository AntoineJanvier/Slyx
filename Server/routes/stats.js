let express = require('express');
let router = express.Router();

router.get('/', function (req, res) {
    res.render('stats', {title: 'Statistics'});
});

module.exports = router;
