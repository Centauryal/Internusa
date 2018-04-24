package com.supersoft.internusa.ui.sidebar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.exception.UnCaughtException;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.Loghistory;
import com.supersoft.internusa.ui.info.KomentarActivity;
import com.supersoft.internusa.view.CodehtmlContentActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 12/29/2017.
 */

public class Historyloginfo extends AppCompatActivity {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.lvwData)ListView lvwData;
    @BindView(R.id.txtInfo)TextView info;
    DBHelper _db;
    ArrayList<Loghistory> mResult = new ArrayList<>();
    lvwAdapter mAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(Historyloginfo.this));
        setContentView(R.layout.history_log_info);
        ButterKnife.bind(this);
        setupToolbar();

        _db = new DBHelper(this);
        mResult = _db.getLoghistory();
        mAdapter = new lvwAdapter(this, mResult);
        lvwData.setAdapter(mAdapter);
        info.setVisibility(View.GONE);
        if(mResult.size() <= 0)
        {
            info.setVisibility(View.VISIBLE);
        }
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    public class lvwAdapter extends BaseAdapter implements View.OnClickListener
    {

        Context _context;
        ArrayList<Loghistory> history = new ArrayList<>();

        public lvwAdapter(Context context, ArrayList<Loghistory> result)
        {
            this._context = context;
            this.history = result;
        }

        @Override
        public int getCount() {
            return history.size();
        }

        @Override
        public Loghistory getItem(int i) {
            return history.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public String parseType(int position)
        {
            String type = getItem(position).TYPE;
            String value = "";
            if(type.equals(Constant.NOTIF_KODE_HTML_CONTENT)) value = "Notifikasi";
            else if(type.equals(Constant.NOTIF_KODE_KOMENTAR)) value = "Komentar";
            else if(type.equals(Constant.NOTIF_KODE_LIKE)) value = "Like";
            else if(type.equals(Constant.NOTIF_KODE_BIG_IMAGE)) value = "Notifikasi";

            return value;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            viewHolder holder = null;
            if(view == null)
            {
                view = LayoutInflater.from(_context).inflate(R.layout.item_log_history, null);
                holder = new viewHolder(view);
                view.setTag(holder);
            }
            else
            {
                holder = (viewHolder)view.getTag();
            }
            try
            {
                String type = String.format("<b>%s</b> dari <b>%s</b> sekitar <small>%s</small>",parseType(i),"System", (getItem(i).CREATEDATE == "") ? "NULL" : Constant.parseDate(getItem(i).CREATEDATE));
                holder.jenis.setText(Html.fromHtml(type));
                holder.txtInfo.setText(getItem(i).DESCRIPTION);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String type = getItem(i).TYPE;

                        Toast.makeText(_context, "view clicked " + type+","+getItem(i).IDBASE+","+getItem(i).TITLE +","+getItem(i).ACTIVITY, Toast.LENGTH_SHORT).show();
                        if(type.equals(Constant.NOTIF_KODE_HTML_CONTENT))
                        {
                            Intent intent = new Intent(_context, CodehtmlContentActivity.class);
                            intent.putExtra(Constant.FULL_IMAGE_ACTIVITY, getItem(i).ACTIVITY);
                            intent.putExtra(Constant.FULL_IMAGE_TITLE, getItem(i).TITLE);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            _context.startActivity(intent);
                        }
                        else if(type.equals(Constant.NOTIF_KODE_KOMENTAR))
                        {
                            Intent intent = new Intent(_context, KomentarActivity.class);
                            intent.putExtra("INFOID", getItem(i).IDBASE);
                            intent.putExtra("POS", 0);
                            _context.startActivity(intent);
                        }
                    }
                });
            }
            catch (NullPointerException e)
            {

            }

            return view;
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(_context, "view clicked", Toast.LENGTH_SHORT).show();
        }

        class viewHolder
        {
            @BindView(R.id.txtInfo)
            public TextView txtInfo;
            @BindView(R.id.jenis)
            public  TextView jenis;
            public View mView;
            public viewHolder(View view)
            {
                this.mView = view;
                ButterKnife.bind(this, view);

            }
        }
    }

}
