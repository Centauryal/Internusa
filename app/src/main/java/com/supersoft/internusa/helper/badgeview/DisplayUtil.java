package com.supersoft.internusa.helper.badgeview;

import android.content.Context;

/**
 * Created by Centaury on 18/04/2018.
 */
public class DisplayUtil {

    public static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
