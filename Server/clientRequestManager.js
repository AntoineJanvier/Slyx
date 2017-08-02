const http = require('http');
const querystring = require('querystring');
const sa = require('superagent');
const XMLHttpRequest = require('xmlhttprequest');


module.exports = {
    handleClientRequest: function (jsonObj) {

        switch (jsonObj.request) {
            case 'CONNECTION':
                let r = [];
                let data = querystring.stringify({
                    email: jsonObj.email,
                    pwd: jsonObj.password
                });
                let options = {
                    host: '127.0.0.1',
                    port: 3000,
                    path: '/api/auth/sign_in',
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'Content-Length': Buffer.byteLength(data)
                    }
                };
                let httpreq = http.request(options, function (response) {
                    let c;
                    response.setEncoding('utf8');
                    response.on('data', function (chunk) {
                        c = chunk;
                    });
                    response.on('end', function () {
                        return c;
                    });
                });
                httpreq.write(data);
                httpreq.end();
                return httpreq;
                // return httpreq;
        }
    }
}