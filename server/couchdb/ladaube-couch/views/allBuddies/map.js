// view for cleaning, used in tests
function(doc) {
  if (doc.type == 'buddies') {
    emit([doc.user1, doc.user2], doc._id);
  }
}