package com.ladaube.upload

import org.apache.commons.httpclient.methods.multipart.FilePart
import org.apache.commons.httpclient.methods.multipart.PartSource
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.Log

class FilePartWithProgress extends FilePart {

    private static final Log LOG = LogFactory.getLog(FilePartWithProgress.class)

    private long totalSize = -1
    private long uploaded = -1
    private int postEventId
    private Uploader uploader
    private String fileName

    FilePartWithProgress(String name, File file, int postEventId, Uploader uploader) throws FileNotFoundException{
        super(name, file)
        this.postEventId = postEventId
        this.uploader = uploader
        this.fileName = file.getAbsolutePath()
    }

    @Override
    protected void sendData(OutputStream out) {
        totalSize = lengthOfData()
        uploaded = 0
        LOG.trace("enter sendData(OutputStream out)");
        if (totalSize == 0) {

            // this file contains no data, so there is nothing to send.
            // we don't want to create a zero length buffer as this will
            // cause an infinite loop when reading.
            LOG.debug("No data to send.");
            return;
        }

        byte[] tmp = new byte[4096];
        try {
            InputStream instream = source.createInputStream();
            try {
                int len;
                while ((len = instream.read(tmp)) >= 0) {
                    out.write(tmp, 0, len);
                    uploaded += len;
                    onProgress();
                }
            } finally {
                // we're done with the stream, close it
                instream.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e)
        }
    }

    void onProgress() {
        uploader.notifyListeners(new DataUploadedEvent(
                postEventId: postEventId,
                totalSize: totalSize,
                uploaded: uploaded,
                fileName: fileName
        ))
    }

    long getTotalSize() {
        return totalSize
    }

    long getUploaded() {
        return uploaded
    }
}
