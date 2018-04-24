package com.supersoft.internusa.helper.bottomsheet;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Centaury on 18/04/2018.
 */
public class FillerView extends LinearLayout {
    private View mMeasureTarget;


    public FillerView(Context context) {
        super(context);
    }


    public void setMeasureTarget(View lastViewSeen) {
        mMeasureTarget = lastViewSeen;
    }


    public FillerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(null != mMeasureTarget)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    mMeasureTarget.getMeasuredHeight(), MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
