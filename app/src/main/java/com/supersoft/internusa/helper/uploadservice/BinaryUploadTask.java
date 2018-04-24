package com.supersoft.internusa.helper.uploadservice;

import com.supersoft.internusa.helper.uploadservice.http.HttpConnection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Implements a binary file upload task.
 *
 * @author cankov
 * @author gotev (Aleksandar Gotev)
 */
public class BinaryUploadTask extends HttpUploadTask {

    @Override
    protected long getBodyLength() throws UnsupportedEncodingException {
        return params.getFiles().get(0).length(service);
    }


    protected void writeBody(HttpConnection connection) throws IOException {
        writeStream(params.getFiles().get(0).getStream(service));
    }

    @Override
    protected void onSuccessfulUpload() {
        addSuccessfullyUploadedFile(params.getFiles().get(0).getPath());
        params.getFiles().clear();
    }
}
