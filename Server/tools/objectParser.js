module.exports = {
    responseParser: function (json, typeOfParse) {
        let jsoned = JSON.parse(json);
        let res = {};
        switch (typeOfParse) {
            case 'CONTACTS':
                for (let s of jsoned) {
                    res.push({
                        type: 'USER',
                        userid: s.userid,
                        firstname: s.first_name,
                        lastname: s.last_name,
                        age: s.age,
                        email: s.email
                    });
                }
                break;
            case 'USER':
                res = {
                    type: 'USER',
                    userid: jsoned.userid,
                    firstname: jsoned.first_name,
                    lastname: jsoned.last_name,
                    age: jsoned.age,
                    email: jsoned.email
                };
                break;
            default:
                res = {};
        }
        return res;
    }
};