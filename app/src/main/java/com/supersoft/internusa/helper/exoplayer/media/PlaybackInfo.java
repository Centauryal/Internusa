package com.supersoft.internusa.helper.exoplayer.media;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Centaury on 19/04/2018.
 */
public class PlaybackInfo implements Parcelable {

    public static final long TIME_UNSET = Long.MIN_VALUE + 1;

    public static final int INDEX_UNSET = -1;

    private int resumeWindow;
    private long resumePosition;

    public PlaybackInfo(int resumeWindow, long resumePosition) {
        this.resumeWindow = resumeWindow;
        this.resumePosition = resumePosition;
    }

    public PlaybackInfo() {
        this(INDEX_UNSET, TIME_UNSET);
    }

    public PlaybackInfo(PlaybackInfo other) {
        this(other.getResumeWindow(), other.getResumePosition());
    }

    protected PlaybackInfo(Parcel in) {
        resumeWindow = in.readInt();
        resumePosition = in.readLong();
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(resumeWindow);
        dest.writeLong(resumePosition);
    }

    @Override public int describeContents() {
        return 0;
    }

    public static final Creator<PlaybackInfo> CREATOR = new ClassLoaderCreator<PlaybackInfo>() {
        @Override public PlaybackInfo createFromParcel(Parcel source, ClassLoader loader) {
            return new PlaybackInfo(source);
        }

        @Override public PlaybackInfo createFromParcel(Parcel source) {
            return new PlaybackInfo(source);
        }

        @Override public PlaybackInfo[] newArray(int size) {
            return new PlaybackInfo[size];
        }
    };

    public int getResumeWindow() {
        return resumeWindow;
    }

    public void setResumeWindow(int resumeWindow) {
        this.resumeWindow = resumeWindow;
    }

    public long getResumePosition() {
        return resumePosition;
    }

    public void setResumePosition(long resumePosition) {
        this.resumePosition = resumePosition;
    }

    public void reset() {
        resumeWindow = INDEX_UNSET;
        resumePosition = TIME_UNSET;
    }

    @Override public String toString() {
        return "State{" + "window=" + resumeWindow + ", position=" + resumePosition + '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaybackInfo)) return false;

        PlaybackInfo that = (PlaybackInfo) o;

        if (resumeWindow != that.resumeWindow) return false;
        return resumePosition == that.resumePosition;
    }

    @Override public int hashCode() {
        int result = resumeWindow;
        result = 31 * result + (int) (resumePosition ^ (resumePosition >>> 32));
        return result;
    }
}
