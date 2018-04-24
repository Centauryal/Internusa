package com.supersoft.internusa.ui.chat;

/**
 * Created by itclub21 on 12/7/2017.
 */

public class Refresher {
    private int mRefreshingCount;

    public Refresher(boolean mIsRefreshing) {
        if (mIsRefreshing)
            this.mRefreshingCount = 1;
        else
            this.mRefreshingCount = 0;
    }

    public boolean isRefreshing() {
        return mRefreshingCount > 0;
    }

    public void setIsRefreshing(boolean mIsRefreshing) {
        if (mIsRefreshing) {
            mRefreshingCount++;
        } else if (mRefreshingCount > 0) {
            mRefreshingCount--;
        }
    }
}