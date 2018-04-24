package com.supersoft.internusa.helper.pageIndicator;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Centaury on 19/04/2018.
 */
public interface RecyclerPageIndicator /*extends ViewPager.OnPageChangeListener*/ {
    /**
     * Bind the indicator to a RecyclerView.
     *
     * @param view
     */
    void setViewPager(RecyclerView view);

    /**
     * Bind the indicator to a RecyclerView.
     *
     * @param view
     * @param initialPosition
     */
    void setViewPager(RecyclerView view, int initialPosition);

    /**
     * <p>Set the current page of both the ViewPager and indicator.</p>
     *
     * <p>This <strong>must</strong> be used if you need to set the page before
     * the views are drawn on screen (e.g., default start page).</p>
     *
     * @param item
     */
    void setCurrentItem(int item);

    /**
     * Set a page change listener which will receive forwarded events.
     *
     * @param listener
     */
//    void setOnPageChangeListener(RecyclerView.OnPageChangeListener listener);

    /**
     * Notify the indicator that the fragment list has changed.
     */
    void notifyDataSetChanged();
}
