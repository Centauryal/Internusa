package com.supersoft.internusa.ui.payment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/27/2017.
 */

public class TransferSaldoMemberFragment extends Fragment implements TextWatcher {

    @BindView(R.id.txtNominal)
    EditText txtNominal;

    @BindView(R.id.txtAgenid)
    EditText txtAgenid;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    AlertDialog _dialog;

    public static TransferSaldoMemberFragment newInstance()
    {
        TransferSaldoMemberFragment fm = new TransferSaldoMemberFragment();
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transfer_saldo_member, container, false);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _dialog = new SpotsDialog(getActivity(), R.style.Custom);

    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view)
    {
        if(Constant.Is_valid_all(txtAgenid) && Constant.Is_Valid_Sign_Number_Validation(4, 10, txtNominal))
        {
            transferSaldo();
        }
    }


    private void transferSaldo()
    {
        _dialog.show();
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();

        final ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.deviceid = Constant.getUUID(getActivity());
        inf.pin = profil.pin;
        inf.hp = profil.hp;
        inf.nominal = txtNominal.getText().toString();
        inf.agentujuan = txtAgenid.getText().toString();


        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"konfirmasi_transfer");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(inf);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Log.e("RES", new Gson().toJson(response.body()));
                if(_dialog.isShowing())_dialog.dismiss();
                if(response.isSuccessful())
                {
                    int status = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsInt();
                    String message = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsString();
                    int isOtp;
                    try{
                        isOtp = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("isotp").getAsInt();
                    }catch (NullPointerException e)
                    {
                        isOtp = 0;
                    }

                    if(status == 1){
                        //Log.e("masuk status ", "SATYu");
                        Constant.goToNextFragement(getActivity(), MKonfirmasiPaymentFragment.newInstance(isOtp == 1, response.body(), "transfer", inf),R.id.container, false, false, false);

                    }
                    else
                    {
                        Constant.showInfoMessageDialog(getActivity(), message, "Failed Code Unknown");
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(_dialog.isShowing())_dialog.dismiss();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        Constant.Is_valid_all(txtAgenid);
        Constant.Is_Valid_Sign_Number_Validation(4, 10, txtNominal);
    }
}
