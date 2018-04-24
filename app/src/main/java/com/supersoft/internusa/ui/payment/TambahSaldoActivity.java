package com.supersoft.internusa.ui.payment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojogetBank;
import com.supersoft.internusa.model.ProfilDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 3/9/2017.
 */

public class TambahSaldoActivity extends FragmentActivity {
    RelativeLayout continerTambahSaldo;
    FragmentManager _fm;
    DBHelper _db;

    TextView lblStep1, lblStep2, lblStep3;
    int stepKe = 0;
    AlertDialog _loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_saldo_activity);
        _fm = getSupportFragmentManager();
        _db = new DBHelper(this);
        _loading = new SpotsDialog(this,R.style.SpotsDialogDefault);
        lblStep2 = findViewById(R.id.lblStep2);
        lblStep3 = findViewById(R.id.lblStep3);

        continerTambahSaldo = findViewById(R.id.continerTambahSaldo);
        FragmentTransaction _ft = _fm.beginTransaction();
        _ft.add(R.id.continerTambahSaldo, new FragmentIsiSaldo());
        _ft.commit();
    }


    public void onResume(){
        super.onResume();
        EventBus.getDefault().register(this);

    }

    public void onPause(){
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessage(MsgEvent evt)
    {
        if(evt.getPosition() == 1)
        {
            getBank(evt.getKey(), _db.getProfilDb());
        }
        else if(evt.getPosition() == 2) {
            String nominal = evt.getKey();
            String bank = evt.getPhonenumber();
            kirimSaldo(nominal, bank, _db.getProfilDb());
        }
    }


    private void getBank(String nominal, ProfilDB profil)
    {
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = profil.hp;
        inf.agenid = profil.agenid;
        inf.nominal = nominal;
        //Log.e("REQ", new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "load_bank");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojogetBank> call = githubUserAPI.loadBank(inf);
        call.enqueue(new Callback<PojogetBank>() {
            @Override
            public void onResponse(Call<PojogetBank> call, Response<PojogetBank> response) {

                if(!response.isSuccessful())
                {
                    return;
                }
                FragmentKonfirmasiSaldo konfimasSaldo = FragmentKonfirmasiSaldo.newInstance(response.body());
                FragmentTransaction _ft = _fm.beginTransaction();
                _ft.replace(R.id.continerTambahSaldo, konfimasSaldo);
                _ft.commit();
                lblStep2.setTextColor(getResources().getColor(R.color.white));

            }

            @Override
            public void onFailure(Call<PojogetBank> call, Throwable t) {
                Constant.showInfoMessageDialog(TambahSaldoActivity.this,t.getMessage(), "Failure");
            }
        });
    }


    private void kirimSaldo(final String nominal, final String bank, ProfilDB profil)
    {
        _loading.show();
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = profil.hp;
        inf.agenid = profil.agenid;
        inf.nominal = nominal;
        inf.bank = bank;

        //Log.e("REQ", new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "request_ticket");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = githubUserAPI.onSubmitDefault(inf);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("respo", new Gson().toJson(response.body()));
                if(_loading.isShowing())_loading.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }
                int status;
                String message = "";

                try
                {
                    status = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsInt();
                    message = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsString();
                }catch (NullPointerException e)
                {
                    message = "Error Parsing response, mungkin kosong";
                    status = 9;
                }
                if(status == 1) {

                    Bundle bundle = new Bundle();
                    bundle.putString("load_bank", new Gson().toJson(response.body()));
                    FragmentKonfirmasiSaldoFinish konfimasSaldo = FragmentKonfirmasiSaldoFinish.newInstance(bundle);
                    FragmentTransaction _ft = _fm.beginTransaction();
                    _ft.replace(R.id.continerTambahSaldo, konfimasSaldo);
                    _ft.commit();
                    lblStep3.setTextColor(getResources().getColor(R.color.white));
                }
                else
                {
                    Constant.showInfoMessageDialog(TambahSaldoActivity.this,"Response Server Kosong", "Response Error");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(_loading.isShowing())_loading.dismiss();
                Constant.showInfoMessageDialog(TambahSaldoActivity.this,t.getMessage(), "Failure");
            }
        });

    }
}
