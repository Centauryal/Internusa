package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.supersoft.internusa.helper.exoplayer.CacheManager;
import com.supersoft.internusa.helper.util.Constant;

import java.util.ArrayList;

/**
 * Created by itclub21 on 1/19/2018.
 */

@SuppressWarnings({ "unused", "WeakerAccess" })
public class Slidingvideo_adapter extends RecyclerView.Adapter<TimelineViewHolder> implements CacheManager {

    private static final String TAG = "Toro:Fb:Adapter";

    static final int TYPE_OTHER = 1;
    static final int TYPE_VIDEO = 2;

    @Nullable private Callback callback;
    private ArrayList<String> IMAGES;
    Context _context;
    private int      itemCount = 0;
    public Slidingvideo_adapter() {
        super();
    }

    public Slidingvideo_adapter(Context ctx, ArrayList<String> IMAGES) {
        super();
        this._context = ctx;
        this.IMAGES = IMAGES;
        setHasStableIds(true);
    }

    public void setCallback(@Nullable Callback callback) {
        this.callback = callback;
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final TimelineViewHolder viewHolder = TimelineViewHolder.createViewHolder(parent, viewType);
        viewHolder.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                if (callback != null && pos != RecyclerView.NO_POSITION) {
                    callback.onItemClick(viewHolder, view, getItem(pos), pos);
                    Log.e("onClick", "onClick oke");
                }
            }
        });
        return viewHolder;
    }

    @Override public int getItemViewType(int position) {
        String mimeType = Constant.getMimeType(IMAGES.get(position));
        return (mimeType.contains("image")) ? TYPE_OTHER : TYPE_VIDEO;
    }

    @Override public void onBindViewHolder(TimelineViewHolder holder, int position) {
        holder.bind(this, getItem(position), null);
    }

    @Override public void onViewRecycled(TimelineViewHolder holder) {
        holder.onRecycled();
    }

    @Override public int getItemCount() {
        itemCount = this.IMAGES.size();
        return this.IMAGES.size();
    }

    public String getItem(int position) {
        return this.IMAGES.get(position);
    }

    static abstract class Callback {
        abstract void onItemClick(@NonNull TimelineViewHolder viewHolder, @NonNull View view, String item, int position);
    }

    // Implement the CacheManager;

    @NonNull @Override public Object getKeyForOrder(int order) {
        return getItem(order);
    }

    @Nullable @Override public Integer getOrderForKey(@NonNull Object key) {
        //return key instanceof FbItem ? items.indexOf(key) : null;
        return  null;
    }

    public Context getContext()
    {
        return this._context;
    }

    void updateItemCount(int newCount) {
        itemCount = newCount;

        notifyDataSetChanged();
    }
}
