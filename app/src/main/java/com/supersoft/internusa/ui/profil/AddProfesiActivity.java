package com.supersoft.internusa.ui.profil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.ProfesiParamModel;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.mProfesi;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/21/2017.
 */

public class AddProfesiActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.btnLogin)Button btnLogin;

    @BindView(R.id.pb_loading_indicator_profesi)ProgressBar pb_loading_indicator_profesi;
    @BindView(R.id.txtProfesi)DelayAutoCompleteTextView txtProfesi;
    @BindView(R.id.pb_loading_indicator_pendidikan)ProgressBar pb_loading_indicator_pendidikan;
    @BindView(R.id.txtPendidikan)DelayAutoCompleteTextView txtPendidikan;
    @BindView(R.id.pb_loading_indicator_jurusan)ProgressBar pb_loading_indicator_jurusan;
    @BindView(R.id.txtJurusan)DelayAutoCompleteTextView txtJurusan;
    @BindView(R.id.pb_loading_indicator_instansi)ProgressBar pb_loading_indicator_instansi;
    @BindView(R.id.txtInstansi)DelayAutoCompleteTextView txtInstansi;
    @BindView(R.id.pb_loading_indicator_posisi)ProgressBar pb_loading_indicator_posisi;
    @BindView(R.id.txtPosisi)DelayAutoCompleteTextView txtPosisi;
    @BindView(R.id.txtDescription)EditText txtDescription;


    private AlertDialog progressDialog;
    String CRUD = "";
    String ID = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_sheet_add_profesi);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            CRUD = extras.getString("CRUD");
            if(CRUD.equals("update"))
            {
                ID = extras.getString("ID");
            }
        }
        progressDialog = new SpotsDialog(this, R.style.Custom);
        btnLogin.setOnClickListener(this);
        setupToolbar();

        initAutoComplete();

        if(!ID.equals(""))
        {
            loadProfesi();
        }
    }


    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


    private void loadProfesi()
    {
        progressDialog.show();
        RetrofitBuilder builder = new RetrofitBuilder("getProfesi");
        RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getProfesi/{crud}/{query}"
        final Call<List<mProfesi>> repos = service.listProfesi(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getProfesi","update",ID));
        repos.enqueue(new Callback<List<mProfesi>>() {
            @Override
            public void onResponse(Call<List<mProfesi>> call, Response<List<mProfesi>> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                Log.e("res", new Gson().toJson(response.body()));
                if(!response.isSuccessful())
                {

                    return;
                }

                ArrayList<mProfesi> profesiList = new ArrayList<mProfesi>(response.body());
                if(profesiList.size() > 0)
                {
                    mProfesi p = profesiList.get(0);
                    txtProfesi.setText(p.getBidang());
                    txtInstansi.setText(p.getInstansi());
                    txtJurusan.setText(p.getJurNama());
                    txtPendidikan.setText(p.getSekoNama());
                    txtDescription.setText(p.getDeskripsi());
                    txtPosisi.setText(p.getPosisi());
                }

            }

            @Override
            public void onFailure(Call<List<mProfesi>> call, Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
            }
        });
    }


    private void initAutoComplete()
    {
        txtProfesi.setThreshold(3);
        txtProfesi.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_mposisi_nonformal")); // 'this' is Activity instance
        txtProfesi.setLoadingIndicator(pb_loading_indicator_posisi);
        txtProfesi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtProfesi.setText(book.getKkNama());
            }
        });


        txtPendidikan.setThreshold(3);
        txtPendidikan.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_msekolah")); // 'this' is Activity instance
        txtPendidikan.setLoadingIndicator(pb_loading_indicator_pendidikan);
        txtPendidikan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtPendidikan.setText(book.getKkNama());
            }
        });


        txtJurusan.setThreshold(3);
        txtJurusan.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_mjurusan")); // 'this' is Activity instance
        txtJurusan.setLoadingIndicator(pb_loading_indicator_jurusan);
        txtJurusan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtJurusan.setText(book.getKkNama());
            }
        });


        txtInstansi.setThreshold(3);
        txtInstansi.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_minstansi")); // 'this' is Activity instance
        txtInstansi.setLoadingIndicator(pb_loading_indicator_instansi);
        txtInstansi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtInstansi.setText(book.getKkNama());
            }
        });


        txtPosisi.setThreshold(3);
        txtPosisi.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_mposisi")); // 'this' is Activity instance
        txtPosisi.setLoadingIndicator(pb_loading_indicator_posisi);
        txtPosisi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtPosisi.setText(book.getKkNama());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnLogin:
                createProfesi();
                break;
        }
    }

    private void createProfesi()
    {
        progressDialog.show();
        DBHelper _db = new DBHelper(this);
        ProfilDB profil = _db.getProfilDb();

        ProfesiParamModel model = new ProfesiParamModel();
        model.agenid = profil.agenid;
        model.hp = profil.hp;
        model.bidang = txtProfesi.getText().toString();
        model.description = txtDescription.getText().toString();
        model.posisi = txtPosisi.getText().toString();
        model.sekolah = txtPendidikan.getText().toString();
        model.jurusan = txtJurusan.getText().toString();
        model.instansi = txtInstansi.getText().toString();
        model.tag = "";
        model.pfid = ID;


        RetrofitBuilder builder = new RetrofitBuilder("createProfesi");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getProfesi/{crud}"
        Call<JsonObject> call = githubUserAPI.createProfesi(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "getProfesi","create"),model);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }
                JsonObject obj = response.body();
                if(obj.has("status"))
                {
                    int status = obj.get("status").getAsInt();
                    String msg = obj.get("message").getAsString();
                    if(status == 1) {
                        Intent intent = getIntent();
                        intent.putExtra("SOMETHING", "EXTRAS");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    else
                    {
                        Constant.showInfoMessageDialog(getApplicationContext(),msg,"ERROR");
                    }
                }
                else  Constant.showInfoMessageDialog(getApplicationContext(),"Tidak mendapat response","ERROR");

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }



    public class AddressAutoCompleteAdapter extends BaseAdapter implements Filterable
    {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private List<Cities> resultList = new ArrayList<Cities>();
        private String func;

        public AddressAutoCompleteAdapter(Context context, String fun) {
            mContext = context;
            func = fun;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Cities getItem(int index) {
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
            ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getKkNama());
            ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getKkKode());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<Cities> books = findBooks(func, constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = books;
                        filterResults.count = books.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<Cities>) results.values;
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
        private List<Cities> findBooks(String fun, String bookTitle) {

            List<Cities> citi = new ArrayList<>();

            RetrofitBuilder builder = new RetrofitBuilder("getCities");
            RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
            //"android/v2/index.php/microz/{func}/{query}"

            Call<List<Cities>> repos = service.listCitiesOther(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, fun, bookTitle));
            try {
                citi = new ArrayList<Cities>(repos.execute().body());
                //Log.e("res", new Gson().toJson(citi));
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e("ERR", e.getMessage());
            }

            return citi;
        }
    }
}
