package com.supersoft.internusa.helper.uploadservice.schemehandlers;

import android.content.Context;

import com.supersoft.internusa.helper.uploadservice.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Handler for normal file paths, wraps java.io.File
 * @author stephentuso
 */
class FileSchemeHandler implements SchemeHandler {

    final File file;

    public FileSchemeHandler(String path) throws FileNotFoundException {
        this.file = new File(path.replace("file://", ""));
    }

    @Override
    public long getLength(Context context) {
        return file.length();
    }

    @Override
    public InputStream getInputStream(Context context) throws FileNotFoundException {
        return new FileInputStream(file);
    }

    @Override
    public String getContentType(Context context) {
        return ContentType.autoDetect(file.getAbsolutePath());
    }

    @Override
    public String getName(Context context) {
        return file.getName();
    }
}
