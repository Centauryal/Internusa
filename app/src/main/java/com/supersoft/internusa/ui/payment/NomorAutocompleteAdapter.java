package com.supersoft.internusa.ui.payment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.HistorytrxModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by selvi on 1/4/18.
 */

public class NomorAutocompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;
    private List<HistorytrxModel> resultList = new ArrayList<HistorytrxModel>();
    private DBHelper _db;
    public NomorAutocompleteAdapter(DBHelper db, Context context) {
        mContext = context;
        this._db = db;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public HistorytrxModel getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).TUJUAN);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<HistorytrxModel> books = findBooks(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = books;
                    filterResults.count = books.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<HistorytrxModel>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};
        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<HistorytrxModel> findBooks(Context context, String bookTitle) {

        List<HistorytrxModel> citi = new ArrayList<>();
        citi = _db.getHistoryTrx(bookTitle);

        return citi;
    }
}
