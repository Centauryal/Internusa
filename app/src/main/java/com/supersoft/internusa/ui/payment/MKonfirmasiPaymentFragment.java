package com.supersoft.internusa.ui.payment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.DrawableClickListener;
import com.supersoft.internusa.helper.util.EditTextRightDrawable;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoResponseConfirm;
import com.supersoft.internusa.model.ProfilDB;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/27/2017.
 */

public class MKonfirmasiPaymentFragment extends Fragment implements DrawableClickListener {

    @BindView(R.id.listLinear)
    LinearLayout listLinear;

    @BindView(R.id.txtOtp)
    EditTextRightDrawable txtOtp;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;


    AlertDialog _dialog;
    static boolean isOtp;
    static JsonObject jsObject;
    static PojoResponseConfirm responseConfirm;
    static String action;
    static ModelRequestInfo _info;
    String REF_ID = "";


    public static MKonfirmasiPaymentFragment newInstance(boolean otp, PojoResponseConfirm obj, String act)
    {
        MKonfirmasiPaymentFragment frm = new MKonfirmasiPaymentFragment();
        isOtp = otp;
        responseConfirm = obj;
        action = act;

        return frm;
    }

    public static MKonfirmasiPaymentFragment newInstance(boolean otp, JsonObject obj, String act, ModelRequestInfo mrs)
    {
        MKonfirmasiPaymentFragment frm = new MKonfirmasiPaymentFragment();
        isOtp = otp;
        jsObject = obj;
        action = act;
        _info = mrs;
        return frm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m_konfirmasi_payment, container, false);

        ButterKnife.bind(this, view);
        txtOtp.setVisibility(View.GONE);
        txtOtp.setDrawableClickListener(this);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOtp && txtOtp.getText().length() < 2)
                    Toast.makeText(getActivity(),"OTP anda belum di isi", Toast.LENGTH_SHORT).show();
                else
                    doSubmit();
            }
        });
        return view;
    }

    //
    //
    /*
    PLN :
    {"response":[{"data":[{"harga":"23884"}],"isotp":0,"message":"TAG PLN nmr 514010535139 an SUPARNO Bulan 201704/// Seb 22734+2500\u003d25234 Hrg 23884 Utk via SMS Ketik BAYAR.4894980.5306","status":2}]}
    *
     */

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _dialog = new SpotsDialog(getActivity(), R.style.Custom);
        Log.e("TRAF", new Gson().toJson(jsObject));

        //String message = jsObject.get("message").getAsString();
        JsonArray jArr = jsObject.get("response").getAsJsonArray();
        listLinear.removeAllViews();
        for(int i=0; i < jArr.size(); i++)
        {
            JsonObject arData = jArr.get(i).getAsJsonObject();
            JsonObject jsObj = arData.get("data").getAsJsonArray().get(0).getAsJsonObject();
            Map<String, Object> attributes = new HashMap<String, Object>();
            Set<Map.Entry<String, JsonElement>> entrySet = jsObj.entrySet();
            for(Map.Entry<String,JsonElement> entry : entrySet){
                Log.e("ITER","KEY "+entry.getKey());
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View vw = inflater.inflate(R.layout.m_konfirmasi_payment_item, null);
                TextView txtTitle = vw.findViewById(R.id.lblTitle);
                TextView txtValue = vw.findViewById(R.id.lblValue);

                txtTitle.setText(entry.getKey());
                txtValue.setText(jsObj.get(entry.getKey()).isJsonNull() ? "NULL" : jsObj.get(entry.getKey()).getAsString());
                //if (key.equals("trxid"))REF_ID = valu;
                if(entry.getKey().equals("trxid"))REF_ID = jsObj.get(entry.getKey()).getAsString();

                listLinear.addView(vw);

            }

        }

    }



    private void requestOTP()
    {

        _dialog.show();
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();

        ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.deviceid = Constant.getUUID(getActivity());
        inf.pin = profil.pin;
        inf.hp = profil.hp;


        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"request_token");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(inf);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(_dialog.isShowing())_dialog.dismiss();
                if(response.isSuccessful())
                {
                    int status = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsInt();
                    String message = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsString();
                    Constant.showInfoMessageDialog(getActivity(), message, "Failed Code Unknown");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(_dialog.isShowing())_dialog.dismiss();
            }
        });
    }


    public void doSubmit()
    {
        _dialog.show();
        _info.trxid = REF_ID;
        _info.dinapin = txtOtp.getText().toString();
        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),action);
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(_info);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(_dialog.isShowing())_dialog.dismiss();
                if(response.isSuccessful())
                {
                    int status = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsInt();
                    String message = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsString();
                    Constant.showMessageDialogColoseActivity(getActivity(), message, "INFO");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(_dialog.isShowing())_dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(DrawablePosition target) {
        if(target == DrawablePosition.RIGHT)
        {
            requestOTP();
        }
    }
}
