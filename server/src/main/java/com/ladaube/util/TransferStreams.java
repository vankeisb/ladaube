package com.ladaube.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class TransferStreams {

    public static int transfer(InputStream in, OutputStream out)
            throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        int totalRead = 0;

        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
            totalRead += len;
        }

        out.flush();

        return totalRead;
    }

}
