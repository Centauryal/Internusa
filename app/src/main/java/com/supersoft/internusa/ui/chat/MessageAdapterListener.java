package com.supersoft.internusa.ui.chat;

/**
 * Created by itclub21 on 12/16/2017.
 */

public interface MessageAdapterListener {
    void onIconClicked(int position);

    void onIconImportantClicked(int position);

    void onMessageRowClicked(int position);

    void onRowLongClicked(int position);
}
