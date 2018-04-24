package com.supersoft.internusa.helper.bottomsheet;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Centaury on 18/04/2018.
 */
public class PinnedSectionGridView extends GridView {

    // -- class fields

    private int mNumColumns;
    private int mHorizontalSpacing;
    private int mColumnWidth;
    private int mAvailableWidth;

    public PinnedSectionGridView(Context context) {
        super(context);
    }

    public PinnedSectionGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedSectionGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getNumColumns() {
        return mNumColumns;
    }

    @Override
    public void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        super.setNumColumns(numColumns);
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        mHorizontalSpacing = horizontalSpacing;
        super.setHorizontalSpacing(horizontalSpacing);
    }

    public int getColumnWidth() {
        return mColumnWidth;
    }

    @Override
    public void setColumnWidth(int columnWidth) {
        mColumnWidth = columnWidth;
        super.setColumnWidth(columnWidth);
    }

    public int getAvailableWidth() {
        return mAvailableWidth != 0 ? mAvailableWidth : getWidth();
    }
}
