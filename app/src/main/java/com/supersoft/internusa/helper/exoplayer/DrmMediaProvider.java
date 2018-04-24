package com.supersoft.internusa.helper.exoplayer;

/**
 * Created by Centaury on 19/04/2018.
 */

import android.support.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.upstream.BandwidthMeter;

import com.supersoft.internusa.helper.exoplayer.media.DrmMedia;

/**
 * @author eneim (7/8/17).
 *
 *         A definition of a Media source that supplies a DRM media. Application that requires DRM
 *         supports should have a {@link MediaSourceBuilder} implements this interface to get
 *         support from {@link ExoPlayer} as well as {@link ExoPlayerHelper}.
 *
 *         See {@link ExoPlayerHelper#prepare(MediaSourceBuilder, BandwidthMeter)}.
 *         See {@link MediaSourceBuilder}.
 */

public interface DrmMediaProvider {

    @NonNull
    DrmMedia getDrmMedia();
}
