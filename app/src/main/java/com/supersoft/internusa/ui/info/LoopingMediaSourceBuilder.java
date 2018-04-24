package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.supersoft.internusa.helper.exoplayer.MediaSourceBuilder;

/**
 * Created by itclub21 on 1/19/2018.
 */

public class LoopingMediaSourceBuilder extends MediaSourceBuilder {

    public LoopingMediaSourceBuilder(Context context, Uri mediaUri) {
        super(context, mediaUri);
    }

    @Override public MediaSource build(BandwidthMeter bandwidthMeter) {
        MediaSource source = super.build(bandwidthMeter);
        return new LoopingMediaSource(source);
    }
}
