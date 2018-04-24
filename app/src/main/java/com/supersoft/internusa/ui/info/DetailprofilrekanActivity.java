package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ListUsahaProfesi;
import com.supersoft.internusa.model.ProfilDB;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 11/20/2017.
 */

public class DetailprofilrekanActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgAvatar)   ImageView imgAvatar;
    @BindView(R.id.lvwData)
    ListView lvwData;
    ListUsahaAdapter adapter;

    ProfilDB profil;
    DBHelper _db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_rekan);
        ButterKnife.bind(this);

        _db = new DBHelper(this);
        profil = _db.getProfilDb();
        setupToolbar();
        adapter = new ListUsahaAdapter(this);
        lvwData.setAdapter(adapter);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String PHONE = bundle.getString("TELP");
            if(!PHONE.isEmpty() || PHONE != null)
            {
                fetchData(PHONE);
            }
        }
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("");
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }
    private void fetchData(String phone)
    {
        RetrofitBuilder builder = new RetrofitBuilder("get_info");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = githubUserAPI.getDetailprofilRekan(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "detail_profil_rekan", phone));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(!response.isSuccessful()){
                    return;
                }

                JsonObject obj = response.body();
                JsonArray profilArray = obj.get("profil").getAsJsonArray();
                if(profilArray.size() > 0)
                {
                    final String mem_nama = profilArray.get(0).getAsJsonObject().get("mem_nama").getAsString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("memNama", mem_nama);
                            getSupportActionBar().setTitle(mem_nama);
                        }
                    });

                    Constant.loadDefaulSlideImage(DetailprofilrekanActivity.this, profilArray.get(0).getAsJsonObject().get("avatar").getAsString(), imgAvatar);
                    JsonArray usahaArray = obj.get("usaha").getAsJsonArray();
                    ArrayList<ListUsahaProfesi> usahaProfesi = new ArrayList<>();
                    for(int u=0; u < usahaArray.size(); u++)
                    {
                        JsonObject ob = usahaArray.get(u).getAsJsonObject();
                        ListUsahaProfesi pro = new ListUsahaProfesi();
                        pro.setNama(ob.get("pnf_nama").getAsString());
                        pro.setDeskripsi(ob.get("deskripsi").getAsString());
                        usahaProfesi.add(pro);
                    }
                    adapter.addToList(usahaProfesi);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }



    public class ListUsahaAdapter extends BaseAdapter
    {
        Context _context;
        ArrayList<ListUsahaProfesi> _data = new ArrayList<>();
        public ListUsahaAdapter(Context ctx)
        {
            this._context = ctx;
        }

        public void addToList(ArrayList<ListUsahaProfesi> ctn)
        {
            this._data = ctn;
        }

        @Override
        public int getCount() {
            return _data.size();
        }

        @Override
        public ListUsahaProfesi getItem(int i) {
            return _data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            Viewholder _holder;
            if(view == null) {
                view = LayoutInflater.from(_context).inflate(R.layout.list_usaha_profesi_item_cardview, null);
                _holder = new Viewholder(view);
                view.setTag(_holder);
            }
            else
            {
                _holder = (Viewholder)view.getTag();
            }

            _holder.txtJudul.setText(getItem(i).getNama());
            _holder.txtIsi.setText(getItem(i).getDeskripsi());
            return view;
        }


    }

    public class Viewholder
    {
        @BindView(R.id.txtJudul)
        TextView txtJudul;

        @BindView(R.id.txtIsi)  TextView txtIsi;

        public Viewholder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}
