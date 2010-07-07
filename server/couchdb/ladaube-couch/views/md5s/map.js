function(doc) {
  if (doc.type == 'track')
    emit(doc.md5, doc.userId);
}