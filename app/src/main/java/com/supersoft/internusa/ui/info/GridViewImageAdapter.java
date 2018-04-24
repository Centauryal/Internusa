package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.imagePicker.Image;
import com.supersoft.internusa.helper.util.Constant;

import java.util.List;

/**
 * Created by itclub21 on 3/30/2017.
 */

public class GridViewImageAdapter extends ArrayAdapter<Image> {
    private final Context context;
    List<Image> items;

    public GridViewImageAdapter(Context context, List<Image> items) {
        super(context, R.layout.app_grid_item, items);

        this.context = context;
        this.items = items;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View appView = inflater.inflate(R.layout.app_grid_item, parent, false);

        Image app = this.getItem(position);
        ImageView imgView = appView.findViewById(R.id.app_icon);
        Constant.loadDefaulSlideImage(this.context,app.path,imgView);

        return appView;
    }
}
