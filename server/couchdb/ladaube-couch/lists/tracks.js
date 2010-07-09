function(head, req) {
    start({
        "headers": {
            "Content-Type": "text/html"
        }
    });
    send('{"totalCount":' + head.total_rows + ',"data":[');
    var row = getRow(), doc, buf='';
    while (row != null) {
        doc = row.doc;
        // doc is null if include_docs not provided
        if (doc) {
            buf += JSON.stringify(doc);
        }
        row = getRow();
        if (row) {
            buf += ',';
        }
        if (buf.length>50000) {
            send(buf);
            buf = '';
        }
    }
    send(']}');

}