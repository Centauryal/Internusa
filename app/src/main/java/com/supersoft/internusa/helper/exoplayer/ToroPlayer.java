package com.supersoft.internusa.helper.exoplayer;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.supersoft.internusa.helper.exoplayer.widget.Container;
import com.supersoft.internusa.helper.exoplayer.media.PlaybackInfo;

/**
 * Created by Centaury on 19/04/2018.
 */
public interface ToroPlayer {
    @NonNull
    View getPlayerView();

    @NonNull
    PlaybackInfo getCurrentPlaybackInfo();

    /**
     * Initialize resource for the incoming playback. After this point, {@link ToroPlayer} should be
     * able to start the playback at anytime in the future (This doesn't mean that any call to {@link
     * ToroPlayer#play()} will start the playback immediately. It can start buffering enough resource
     * before any rendering).
     *
     * @param container the RecyclerView contains this Player.
     * @param playbackInfo initialize info for the preparation.
     */
    void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo);

    /**
     * Start playback or resume from a pausing state.
     */
    void play();

    /**
     * Pause current playback.
     */
    void pause();

    boolean isPlaying();

    /**
     * Tear down all the setup. This should release all player instances.
     */
    void release();

    boolean wantsToPlay();

    /**
     * @return prefer playback order in list. Can be customized.
     */
    int getPlayerOrder();

    /**
     * Notify a Player about its {@link Container}'s scroll state change.
     */
    void onSettled(Container container);

    /**
     * A convenient callback to help {@link ToroPlayer} to listen to different playback states.
     */
    interface EventListener {

        void onBuffering(); // ExoPlayer state: 2

        void onPlaying(); // ExoPlayer state: 3, play flag: true

        void onPaused();  // ExoPlayer state: 3, play flag: false

        void onCompleted(); // ExoPlayer state: 4
    }

    // Adapt from ExoPlayer.
    @SuppressWarnings("UnnecessaryInterfaceModifier") @Retention(RetentionPolicy.SOURCE)  //
    @IntDef({ State.STATE_IDLE, State.STATE_BUFFERING, State.STATE_READY, State.STATE_END })  //
    public @interface State {
        int STATE_IDLE = 1;
        int STATE_BUFFERING = 2;
        int STATE_READY = 3;
        int STATE_END = 4;
    }
}
