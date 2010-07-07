function(doc) {
  if (doc.type == 'buddies') {
    emit(doc.user1, doc.user2);
    emit(doc.user2, doc.user1);
  }
}