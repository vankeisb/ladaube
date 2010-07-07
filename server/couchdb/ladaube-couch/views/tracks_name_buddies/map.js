function(doc) {
  if (doc.type == 'track') {
      var bds = doc.buddies;
      emit([doc.userId, doc.name], null);
      for (var i = 0 ; i < bds.length ; i++) {
          emit([bds[i], doc.name], null);
      }
  }
}