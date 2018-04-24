package com.supersoft.internusa.ui.payment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/27/2017.
 */

public class KomplainFragment extends Fragment implements TextWatcher {
    @BindView(R.id.txtDescription)
    EditText txtDescription;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    public static KomplainFragment newInstance()
    {
        KomplainFragment komp = new KomplainFragment();
        return komp;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.komplain_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view)
    {
        if(Constant.Is_valid_all(txtDescription))
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pgBar.setVisibility(View.VISIBLE);
                    doSubmit();
                }
            }, 1000);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Constant.Is_valid_all(txtDescription);
    }

    private void doSubmit()
    {
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();
        Session _session = new Session(getActivity());
        ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.deviceid = _session.getDeviceId();
        inf.info = txtDescription.getText().toString();

        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI), "kirim_info");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = githubUserAPI.onSubmitDefault(inf);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pgBar.setVisibility(View.GONE);
                if(response.isSuccessful())
                {
                    JsonArray jArr = response.body().getAsJsonArray("response");
                    int status = jArr.get(0).getAsJsonObject().get("status").getAsInt();
                    String msg = jArr.get(0).getAsJsonObject().get("message").getAsString();
                    Constant.showInfoMessageDialog(getActivity(),msg,"Info.");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
            }
        });
    }
}
