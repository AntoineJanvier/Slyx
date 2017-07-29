const express = require('express');
const path = require('path');
const favicon = require('serve-favicon');
const logger = require('morgan');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const express_session = require('express-session');

const models = require('./models');
models.sequelize.sync();

const server_routes = {
    index: require('./routes/index'),
    users: require('./routes/users'),
    stats: require('./routes/stats'),
    api: require('./routes/api'),
    api_auth: require('./routes/api_auth'),
    api_contacts: require('./routes/api_contacts'),
    fill: require('./routes/fill')
};

let app = express();

app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

// app.use(favicon(path.join(__dirname, 'public/images', 'icon.jpg')));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use(express_session({
    secret: 'qqgrQEGeqgEQGEQTRZgtstrsGsrGDSNYN',
    resave: true,
    saveUninitialized: true
}));

app.use('/', server_routes.index);
app.use('/users', server_routes.users);
app.use('/stats', server_routes.stats);
app.use('/api', server_routes.api);
app.use('/api/auth', server_routes.api_auth);
app.use('/api/contact', server_routes.api_contacts);
app.use('/api/fill', server_routes.fill);

app.use(function (req, res, next) {
    let err = new Error('Not Found');
    err.status = 404;
    next(err);
});

app.use(function (err, req, res) {
    // set locals, only providing error in development
    res.locals.message = err.message;
    res.locals.error = req.app.get('env') === 'development' ? err : {};

    // render the error page
    res.status(err.status || 500);
    res.render('error', {error: err});
});

module.exports = app;
