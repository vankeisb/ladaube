function(doc) {
    if (doc.type == 'playlist') {
        emit(doc.userId,doc);    
    }
}