function(doc) {
  if (doc.type == 'track')
    emit(doc.userId, null);
}