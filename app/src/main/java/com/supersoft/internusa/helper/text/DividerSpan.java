package com.supersoft.internusa.helper.text;

/**
 * Created by itclub21 on 12/16/2017.
 */

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class DividerSpan extends MetricAffectingSpan {

    private static final float PROPORTION = 0.3f;

    private final boolean large;

    public DividerSpan(boolean large) {
        this.large = large;
    }

    public boolean isLarge() {
        return large;
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTextSize(tp.getTextSize() * PROPORTION);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTextSize(p.getTextSize() * PROPORTION);
    }
}
