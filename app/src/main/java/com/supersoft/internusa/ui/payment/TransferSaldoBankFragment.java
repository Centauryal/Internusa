package com.supersoft.internusa.ui.payment;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by itclub21 on 4/29/2017.
 */

public class TransferSaldoBankFragment extends Fragment implements TextWatcher{

    @BindView(R.id.spnBank)
    Spinner spnBank;

    @BindView(R.id.txtNorek)
    EditText txtNorek;

    @BindView(R.id.txtFullname) EditText txtFullname;

    @BindView(R.id.txtNominal) EditText txtNominal;

    @BindView( R.id.txtDescription)
    EditText txtDescription;

    @BindView(R.id.btnSubmit)Button btnSubmit;


    public static TransferSaldoBankFragment newInstance() {
        TransferSaldoBankFragment fragment = new TransferSaldoBankFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.transfer_saldo_bank, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);

        ArrayList<String> listReguler = new ArrayList<String>();
        listReguler.add("BCA");
        listReguler.add("MANDIRI");
        listReguler.add("BRI");
        listReguler.add("BNI");
        listReguler.add("BUKOPIN");
        listReguler.add("DANAMON");
        listReguler.add("CIMB NIAGA");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,listReguler);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnBank.setAdapter(dataAdapter);
    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view)
    {
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();
        if(Constant.Is_Valid_Sign_Number_Validation(4, 10, txtNominal) && Constant.Is_Valid_Sign_Number_Validation(4, 16, txtNorek) && Constant.Is_valid_all(txtFullname)) {
            ModelRequestInfo info = new ModelRequestInfo();
            info.nominal = txtNominal.getText().toString();
            info.an = txtFullname.getText().toString();
            info.bank = spnBank.getSelectedItem().toString();
            info.berita = txtDescription.getText().toString();
            info.agenid = profil.agenid;
            info.pin = profil.pin;
            info.deviceid = Constant.getUUID(getActivity());

            String frmString = String.format("{\"response\":[{\"data\":[{\"Bank Tujuan\":\""+spnBank.getSelectedItem().toString()+"\",\"No Rekening\":\""+txtNorek.getText().toString()+"\",\"Atas Nama\":\""+txtFullname.getText().toString()+"\",\"Nomonal\":\""+txtNominal.getText().toString()+"\",\"Berita\":\""+txtDescription.getText().toString()+"\"}],\"isotp\":0,\"message\":\"\",\"status\":2}]}");
            JsonParser parser = new JsonParser();
            JsonObject o = parser.parse(frmString).getAsJsonObject();

            Constant.goToNextFragement(getActivity(), MKonfirmasiPaymentFragment.newInstance(false,o,"transfer_bank",info),R.id.container, false, false, false);
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
        Constant.Is_Valid_Sign_Number_Validation(4, 10, txtNominal);
        Constant.Is_Valid_Sign_Number_Validation(4, 16, txtNorek);
        Constant.Is_valid_all(txtFullname);
    }
}
