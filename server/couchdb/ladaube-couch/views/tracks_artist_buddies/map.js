function(doc) {
  if (doc.type == 'track') {
      var bds = doc.buddies;
      emit([doc.userId, doc.artist], null);
      for (var i = 0 ; i < bds.length ; i++) {
          emit([bds[i], doc.artist], null);
      }
  }
}