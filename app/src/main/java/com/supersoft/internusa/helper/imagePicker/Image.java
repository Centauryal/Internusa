package com.supersoft.internusa.helper.imagePicker;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Centaury on 19/04/2018.
 */
public class Image implements Parcelable {
    public long id;
    public String name;
    public String path;
    public String mimeType;
    public boolean isSelected;

    public Image(long id, String name, String path, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
        this.mimeType = getMimeType(new File(this.path));
    }

    public Image(long id, String name, String path, boolean isSelected, String mimeType) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.isSelected = isSelected;
        this.mimeType = mimeType;
    }

    @NonNull
    static String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "image/*"; // fallback type. You might set it to */*
        }
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(mimeType);
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    private Image(Parcel in) {
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        mimeType = in.readString();
    }
}
