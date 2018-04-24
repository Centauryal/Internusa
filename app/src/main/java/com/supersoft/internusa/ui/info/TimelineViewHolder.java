package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 1/19/2018.
 */

public class TimelineViewHolder extends RecyclerView.ViewHolder {

    private static final int LAYOUT_RES_OTHER = R.layout.image_placing;
    static TimelineViewHolder createViewHolder(ViewGroup parent, int type) {
        View view = null;
        final TimelineViewHolder viewHolder;
        if(view == null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_RES_OTHER, parent, false);
            switch (type) {
                case Slidingvideo_adapter.TYPE_OTHER:
                    viewHolder = new TimelineViewHolder(view);
                    view.setTag(viewHolder);
                    break;
                case Slidingvideo_adapter.TYPE_VIDEO:
                    viewHolder = new TimelineVideoViewHolder(view);
                    view.setTag(viewHolder);
                    break;
                default:
                    throw new IllegalArgumentException("Non supported view type: " + type);
            }
        }
        else
        {
            viewHolder = (TimelineViewHolder)view.getTag();
        }
        return viewHolder;
    }

    @Nullable @BindView(R.id.image) ImageView im_slider;
    @Nullable @BindView(R.id.progrss) ProgressBar progrss;
    @BindView(R.id.fb_video_player)
    SimpleExoPlayerView playerView;
    Context mContext;
    TimelineViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        mContext = itemView.getContext();
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.itemView.setOnClickListener(clickListener);
    }

    void bind(Slidingvideo_adapter adapter, String item, List<Object> payloads) {
        Constant.loadDefaultSlideImage(mContext,item, im_slider, progrss);

    }

    void onRecycled() {
        im_slider.setImageBitmap(null);
    }
}
