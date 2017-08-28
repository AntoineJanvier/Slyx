module.exports = {
    acceptContactRequest: require('./contacts/acceptContactRequest'),
    addNewContact: require('./contacts/addNewContact'),
    connect: require('./auth/connect'),
    disconnectUser: require('./auth/disconnectUser'),
    getContacts: require('./contacts/getContacts'),
    getMessagesOfContact: require('./messages/getMessagesOfContact'),
    getPendingContactRequests: require('./contacts/getPendingContactRequests'),
    getSettings: require('./settings/getSettings'),
    getUpdate: require('./auth/getUpdate'),
    getUsersNotInContactList: require('./contacts/getUsersNotInContactList'),
    rejectContactRequest: require('./contacts/rejectContactRequest'),
    removeContactOfContactList: require('./contacts/removeContactOfContactList'),
    sendMessageToUser: require('./messages/sendMessageToUser'),
    updateSettings: require('./settings/updateSettings')
};