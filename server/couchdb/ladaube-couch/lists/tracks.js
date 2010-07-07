function(head, req) {
    start({
        "headers": {
            "Content-Type": "text/html"
        }
    });
    send('{"totalCount":' + head.total_rows + ',"data":[');
    var row = getRow();
    while (row != null) {
        var doc = row.doc;
        // doc is null if include_docs not provided
        if (doc) {
            send(JSON.stringify(doc));
        }
        row = getRow();
        if (row) {
            send(',');
        }
    }
    send(']}');

}