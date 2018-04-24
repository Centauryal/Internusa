package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.amazinglistview.InfiniteScrollListAdapter;
import com.supersoft.internusa.helper.exoplayer.widget.Container;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 11/11/2017.
 */

public class InfoFragmentAdapter extends InfiniteScrollListAdapter {

    private NewPageListener newPageListener;
    //SlidingImage_Adapter mAdapter;
    Slidingvideo_adapter mAdapter;
    ArrayList<Row> tempDatum = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ProfilDB profil;
    public static final int TYPE_ABU = 0;
    public static final int TYPE_GREEN = 1;
    public static final int TYPE_ORANGE = 2;
    public static final int TYPE_BLACK = 3;
    private int maxLine = 3;
    public static final String expandText = "View More";
    private boolean viewMore = true;
    Context _context;

    // A demo listener to pass actions from view to adapter
    public static abstract class NewPageListener {
        public abstract void onScrollNext();
        public abstract View getInfiniteScrollListView(int position, View convertView, ViewGroup parent);
    }

    public InfoFragmentAdapter(NewPageListener newPageListener) {
        this.newPageListener = newPageListener;
    }

    public InfoFragmentAdapter(Context context, ArrayList<Row> datum) {
        _context = context;
        profil = new DBHelper(_context).getProfilDb();
        this.tempDatum = datum;
    }

    public void setPageListener(NewPageListener newPageListener)
    {
        this.newPageListener = newPageListener;
    }

    public void addEntriesToTop(ArrayList<Row> entries) {
        // Add entries in reversed order to achieve a sequence used in most of messaging/chat apps
        if (entries != null) {
            Collections.reverse(entries);
        }
        // Add entries to the top of the list
        this.tempDatum.addAll(0, entries);
        notifyDataSetChanged();
    }


    public void addEntriesToBottom(ArrayList<Row> entries) {
        // Add entries to the bottom of the list
        this.tempDatum.addAll(entries);
        notifyDataSetChanged();
    }

    public void clearEntries() {
        // Clear all the data points
        this.tempDatum.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }


    @Override
    public int getItemViewType(int position) {
        switch (tempDatum.get(position).getStatus())
        {
            case "Publik":
                return TYPE_ABU;
            case "Info":
                return TYPE_GREEN;
            default:
                return TYPE_ORANGE;
        }

    }

    @Override
    protected void onScrollNext() {
        if (newPageListener != null) {
            newPageListener.onScrollNext();
        }
    }


    public void setMoreView(int position, boolean value)
    {
        getItem(position).onViewMore = value;
        notifyDataSetChanged();
    }

    @Override
    public View getInfiniteScrollListView(final int position, View convertView, ViewGroup parent) {
        if (newPageListener != null) {
            return newPageListener.getInfiniteScrollListView(position, convertView, parent);
        }
        return convertView;

    }

    @Override
    public int getCount() {
        return tempDatum.size();
    }

    @Override
    public Row getItem(int position) {
        return tempDatum.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }




    public class holder
    {

        @BindView(R.id.imgAvatar) public CircleImageView imgAvatar;
        //@BindView(R.id.pager) public ViewPager mPager;
        @BindView(R.id.pager) public Container mPager;
        //@BindView(R.id.indicator) public CirclePageIndicator indicator;
        //@BindView(R.id.view_pager_indicator) public OverflowPagerIndicator indicator;
        @BindView(R.id.rl_gallery) public RelativeLayout rl_gallery;
        @BindView(R.id.txtFullname) public TextView txtFullname;
        @BindView(R.id.txtTanggal) public TextView txtTanggal;
        @BindView(R.id.txtStatus) public TextView txtStatus;
        @BindView(R.id.txtDescription) public TextView txtDescription;
        @BindView(R.id.txtTotalLike) public TextView txtTotalLike;
        @BindView(R.id.txtTotalKomentar) public TextView txtTotalKomentar;
        @BindView(R.id.imgSuka) public ImageView imgSuka;
        @BindView(R.id.imgKomentar) public ImageView imgKomentar;

        public holder(View view)
        {
            ButterKnife.bind(this, view);
        }



    }
}
