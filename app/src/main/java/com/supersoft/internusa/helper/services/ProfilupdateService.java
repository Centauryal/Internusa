package com.supersoft.internusa.helper.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoProfilUsaha;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 11/6/2017.
 */

public class ProfilupdateService extends IntentService {
    private static final String TAG = "ProfilupdateService.class.getSimpleName()";

    DBHelper _db = null;
    ProfilDB pf;

    public ProfilupdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        _db = new DBHelper(getApplicationContext());
        pf = _db.getProfilDb();
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.hp = pf.hp;
        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoProfilUsaha> call = githubUserAPI.getProfilUsahaRekan(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "get_profil") ,inf);
        call.enqueue(new Callback<PojoProfilUsaha>() {
            @Override
            public void onResponse(Call<PojoProfilUsaha> call, Response<PojoProfilUsaha> response) {
                if(!response.isSuccessful()){

                    return;
                }
                PojoProfilUsaha respo = response.body();
                Row row = respo.getRows().get(0);
                _db.addProfil(row.getMemNama(),"","","","","","",row.getAvatar());
                EventBus.getDefault().post("update_pic_profil_from_service");
            }

            @Override
            public void onFailure(Call<PojoProfilUsaha> call, Throwable t) {

            }
        });
    }
}
