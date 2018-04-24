package com.supersoft.internusa.helper.util;

/**
 * Created by itclub21 on 10/21/2017.
 */

public interface DateDialogFragmentListener{
    //this interface is a listener between the Date Dialog fragment and the activity to update the buttons date
    void updateChangedDate(int year, int month, int day);
}
