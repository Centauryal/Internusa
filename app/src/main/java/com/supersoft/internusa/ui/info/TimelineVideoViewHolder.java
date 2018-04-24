package com.supersoft.internusa.ui.info;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.supersoft.internusa.helper.exoplayer.ExoPlayerHelper;
import com.supersoft.internusa.helper.exoplayer.SimpleExoPlayerViewHelper;
import com.supersoft.internusa.helper.exoplayer.ToroPlayer;
import com.supersoft.internusa.helper.exoplayer.ToroUtil;
import com.supersoft.internusa.helper.exoplayer.media.PlaybackInfo;
import com.supersoft.internusa.helper.exoplayer.widget.Container;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;

/**
 * Created by itclub21 on 1/19/2018.
 */

@SuppressWarnings("WeakerAccess") //
public class TimelineVideoViewHolder extends TimelineViewHolder implements ToroPlayer {
    @Nullable
    SimpleExoPlayerViewHelper helper;
    @Nullable private Uri mediaUri;

    private ExoPlayerHelper.EventListener listener = new ExoPlayerHelper.EventListener() {
        @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState);
            Log.e("viewHolder",format(Locale.getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady));
        }
    };

    TimelineVideoViewHolder(View itemView) {
        super(itemView);
        playerView.setVisibility(View.VISIBLE);
    }

    @Override public void setClickListener(View.OnClickListener clickListener) {
        super.setClickListener(clickListener);
        playerView.setOnClickListener(clickListener);
        im_slider.setOnClickListener(clickListener);
    }

    @Override void bind(Slidingvideo_adapter adapter, String item, List<Object> payloads) {
        super.bind(adapter, item, payloads);
        if (item != null) {
            mediaUri = Uri.parse(item);
        }
    }

    @NonNull
    @Override public View getPlayerView() {
        return this.playerView;
    }

    @NonNull @Override public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
        if (helper == null) {
            helper = new SimpleExoPlayerViewHelper(container, this, mediaUri);
            helper.setEventListener(listener);
        }
        helper.initialize(playbackInfo);
    }

    @Override public void play() {
        if (helper != null) helper.play();
    }

    @Override public void pause() {
        if (helper != null) helper.pause();
    }

    @Override public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override public void release() {
        if (helper != null) {
            helper.setEventListener(null);
            helper.release();
            helper = null;
        }
    }

    @Override public boolean wantsToPlay() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent()) >= 0.85;
    }

    @Override public void onSettled(Container container) {
        // Do nothing
    }

    @Override public int getPlayerOrder() {
        return getAdapterPosition();
    }

}
