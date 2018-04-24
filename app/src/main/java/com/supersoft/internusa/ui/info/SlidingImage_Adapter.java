package com.supersoft.internusa.ui.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.exoplayer.ExoPlayerHelper;
import com.supersoft.internusa.helper.exoplayer.SimpleExoPlayerViewHelper;
import com.supersoft.internusa.helper.exoplayer.ToroPlayer;
import com.supersoft.internusa.helper.exoplayer.media.PlaybackInfo;
import com.supersoft.internusa.helper.exoplayer.widget.Container;
import com.supersoft.internusa.helper.fcm.PromocodeImageActivity;
import com.supersoft.internusa.helper.util.Constant;

import java.util.ArrayList;
import java.util.Locale;

import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
import static java.lang.String.format;

/**
 * Created by itclub21 on 11/11/2017.
 */

public class SlidingImage_Adapter extends PagerAdapter implements ToroPlayer{


    private ArrayList<String> IMAGES;
    private LayoutInflater layoutInflater;
    Activity activity;
    @Nullable
    SimpleExoPlayerViewHelper helper;
    @Nullable
    ExoPlayerHelper exohelper;
    boolean isAutoplay = false;
    SimpleExoPlayerView playerView;
    Uri UriPath;
    private PlaybackInfo playbackInfo;


    public SlidingImage_Adapter() {
        super();
    }

    public SlidingImage_Adapter(Activity activity,ArrayList<String> IMAGES) {
        super();
        this.activity = activity;
        this.IMAGES=IMAGES;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    private ExoPlayerHelper.EventListener listener = new ExoPlayerHelper.EventListener() {
        @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            super.onPlayerStateChanged(playWhenReady, playbackState);
            //state.setText(format(Locale.getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady));
            Log.e("listener--",format(Locale.getDefault(), "STATE: %d・PWR: %s", playbackState, playWhenReady));
        }
    };

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        String mimeType = Constant.getMimeType(IMAGES.get(position));
        View view = null;
        if(mimeType.contains("image"))
            view = layoutInflater.inflate(R.layout.image_placing, container, false);
        else if(mimeType.contains("video"))
            view = layoutInflater.inflate(R.layout.video_placing, container, false);
        else
            view = layoutInflater.inflate(R.layout.image_placing, container, false);


        if(mimeType.contains("video"))
        {
            playerView = view.findViewById(R.id.vv);
            this.UriPath = Uri.parse(IMAGES.get(position));
            playerView.requestFocus();
            Log.e("simpleExo", IMAGES.get(position));
            LoopingMediaSourceBuilder mediaSourceBuilder = new LoopingMediaSourceBuilder(this.activity, this.UriPath);
            exohelper = new ExoPlayerHelper(playerView, EXTENSION_RENDERER_MODE_OFF, true);
            exohelper.setPlaybackInfo(playbackInfo);
            ActivityCompat.postponeEnterTransition(this.activity);
            playerView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override public void onGlobalLayout() {
                            playerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            ActivityCompat.startPostponedEnterTransition(activity);
                        }
                    });
            try {
                exohelper.prepare(mediaSourceBuilder);
                if (exohelper != null && !exohelper.isPlaying()) {
                    exohelper.play();
                }
            } catch (ParserException e) {
                e.printStackTrace();
                Log.e("err", e.getMessage());
            }
        }
        else
        {
            ImageView im_slider = view.findViewById(R.id.image);
            final ProgressBar progressBar = view.findViewById(R.id.progrss);
            Constant.loadDefaulSlideImage(this.activity.getApplicationContext(),IMAGES.get(position), im_slider, progressBar);
            container.addView(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent inten = new Intent(activity, PromocodeImageActivity.class);
                    inten.putStringArrayListExtra("BitmapImage", IMAGES);
                    inten.putExtra("CLASS", Constant.FULL_IMAGE_SLIDING_INFO);
                    activity.startActivity(inten);
                }
            });
        }


        return view;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public View getPlayerView() {
        Log.e("totoPla", "masuk getPlayerView");
        return this.playerView;
    }

    @NonNull
    @Override
    public PlaybackInfo getCurrentPlaybackInfo() {
        return helper != null ? helper.getLatestPlaybackInfo() : new PlaybackInfo();
    }

    @Override
    public void initialize(@NonNull Container container, @Nullable PlaybackInfo playbackInfo) {
        if (helper == null) {
            helper = new SimpleExoPlayerViewHelper(container, this, this.UriPath);
            helper.setEventListener(listener);
        }
        helper.initialize(playbackInfo);
    }

    @Override
    public void play() {
        if (helper != null) helper.play();
    }

    @Override
    public void pause() {
        if (helper != null) helper.pause();
    }

    @Override
    public boolean isPlaying() {
        return helper != null && helper.isPlaying();
    }

    @Override
    public void release() {
        if (helper != null) {
            helper.release();
            helper = null;
        }
    }

    @Override
    public boolean wantsToPlay() {
        return false;
    }

    @Override
    public int getPlayerOrder() {
        return 0;
    }

    @Override
    public void onSettled(Container container) {

    }
}
