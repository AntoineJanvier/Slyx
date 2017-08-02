const s_url = 'http://127.0.0.1:3000/api';

module.exports = {
    contacts: function (id) {
        let xmlHttp = new XMLHttpRequest();
        xmlHttp.open( "GET", s_url + '/contacts', false ); // false for synchronous request
        xmlHttp.send( null );
        return xmlHttp.responseText;
    }
};