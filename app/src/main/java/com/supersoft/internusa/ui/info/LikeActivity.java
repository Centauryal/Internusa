package com.supersoft.internusa.ui.info;

/**
 * Created by selvi on 10/14/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.ProfilDB;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.lvwData)
    ListView lvwData;


    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    @BindView(R.id.lblInfo)
    TextView lblInfo;

    @BindView(R.id.imgSuka)ImageView imgSuka;
    @BindView(R.id.txtTotal) TextView txtTotal;

    ArrayList<Rows> rows = new ArrayList<>();
    KomenAdapter mAdapter;

    String info_id = "";
    int POSITION = 0;
    ProfilDB profil;
    DBHelper _db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_activity);
        ButterKnife.bind(this);

        _db = new DBHelper(this);
        profil = _db.getProfilDb();

        initToolbar();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            info_id = extras.getString("INFOID");
            POSITION = extras.getInt("POS");
            if(!info_id.equals(""))
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadKomentar(info_id);
                    }
                }, 1000);
            }
        }

    }

    private void initToolbar()
    {
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_left_light));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    @OnClick(R.id.imgSuka)
    public void imgLike()
    {
        imgSuka.startAnimation(Constant.AnimBounce(this));
        setLikes(info_id, imgSuka);
    }

    private void setLikes(final String infoid, final ImageView imgSuka)
    {
        final ModelRequestInfo req = new ModelRequestInfo();
        req.hp = profil.hp;
        req.mitraid = getResources().getString(R.string.CONF_MITRAID);
        req.info_id = infoid;

        RetrofitBuilder builder = new RetrofitBuilder("kegiatanBaru");
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = api.setLikes(profil.hp, req);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(!response.isSuccessful())return;

                try
                {
                    JsonObject obj = response.body();
                    if(!obj.has("status")) // kalo menemukan ini berarti error
                    {
                        _db.updateOwnlikes(infoid, Integer.parseInt(obj.get("total").toString()));
                        _db.updateTotalLikes(infoid, obj.get("ownlike").getAsBoolean());
                        MsgEvent event = new MsgEvent(POSITION, obj.get("total").toString(), obj.get("ownlike").getAsBoolean(),"likes");
                        EventBus.getDefault().post(event);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadKomentar(info_id);
                            }
                        });
                    }

                }
                catch (JsonParseException ex)
                {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void loadKomentar(String info_id)
    {

        ProfilDB profil = new DBHelper(this).getProfilDb();
        final ModelRequestInfo req = new ModelRequestInfo();
        req.info_id = info_id;

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getListWhosLike/{hp}"
        String url = String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "getListWhosLike", profil.hp);
        Call<JsonObject> call = api.getListWhosLike(url, info_id, getResources().getString(R.string.CONF_MITRAID));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pgBar.setVisibility(View.GONE);
                if(!response.isSuccessful())return;

                try
                {
                    JsonObject obj = response.body();
                    int status = obj.get("status").getAsInt();
                    String msg = obj.get("msg").getAsString();
                    if(status == 1)
                    {
                        boolean ownlike = obj.get("ownlike").getAsBoolean();
                        JsonArray jArr = obj.get("rows").getAsJsonArray();
                        rows = new ArrayList<>();
                        lblInfo.setVisibility(View.GONE);
                        txtTotal.setText(String.valueOf(jArr.size()));
                        if(jArr.size() > 0)
                        {
                            for(int i=0; i < jArr.size(); i++)
                            {
                                JsonObject object = jArr.get(i).getAsJsonObject();
                                Rows mdl = new Rows();
                                mdl.mem_id = object.get("mem_id").getAsInt();
                                mdl.nama = object.get("nama").getAsString();
                                mdl.foto = object.get("foto").getAsString();
                                rows.add(mdl);

                            }
                            mAdapter = new KomenAdapter(LikeActivity.this, rows);
                            lvwData.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            lblInfo.setVisibility(View.VISIBLE);
                        }

                        if(ownlike) imgSuka.setImageResource(R.drawable.ic_like_hati);

                    }
                    else
                    {
                        lblInfo.setVisibility(View.VISIBLE);
                        lblInfo.setText(msg);
                    }

                }
                catch (JsonParseException ex)
                {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
                lblInfo.setVisibility(View.VISIBLE);
                lblInfo.setText(t.getMessage());
            }
        });
    }


    public class LikesModel
    {
        @SerializedName("ownlike")
        @Expose
        private Boolean ownlike;

        @SerializedName("status")
        @Expose
        private int status;

        @SerializedName("msg")
        @Expose
        private String msg;

        @SerializedName("rows")
        @Expose
        private List<Rows> rows;

    }

    public class Rows
    {
        @SerializedName("mem_id")
        @Expose
        private int mem_id;

        @SerializedName("nama")
        @Expose
        private String nama;

        @SerializedName("foto")
        @Expose
        private String foto;
    }


    private class KomenAdapter extends BaseAdapter
    {

        Context _context;
        ArrayList<Rows> arrayAdapter;

        public KomenAdapter(Context _ctx, ArrayList<Rows> adp)
        {
            _context = _ctx;
            arrayAdapter = adp;
        }

        @Override
        public int getCount() {
            return arrayAdapter.size();
        }

        @Override
        public Rows getItem(int i) {
            return arrayAdapter.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            viewHolder holder;
            if(view == null)
            {
                view = LayoutInflater.from(_context).inflate(R.layout.likes_grid_item, null);
                holder = new viewHolder(view);
                view.setTag(holder);
            }
            else
            {
                holder = (viewHolder)view.getTag();
            }

            holder.txtNama.setText(getItem(i).nama);
            Constant.loadDefaultAvatar(_context,getItem(i).foto,holder.imgAvatar);
            return view;
        }
    }

    public class viewHolder
    {
        @BindView(R.id.txtNama)
        public TextView txtNama;

        @BindView(R.id.imgAvatar)
        public CircleImageView imgAvatar;

        public viewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}
