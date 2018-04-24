package com.supersoft.internusa.splash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.CountryCode;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Centaury on 21/04/2018.
 */
public class AktifasiFragment extends Fragment implements TextWatcher {
    private Toolbar toolbar;
    EditText txtAgenid;
    Button btnSubmit;
    Spinner spnCountrycode;
    AppCompatActivity appCompat;
    ArrayList<CountryCode> arrCode = new ArrayList<>();
    DBHelper _db;
    String prefik_code = "";

    public static AktifasiFragment newInstnace()
    {
        AktifasiFragment fragment = new AktifasiFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.aktifasi, container, false);
        _db = new DBHelper(getActivity());
        txtAgenid = rootView.findViewById(R.id.txtAgenid);
        spnCountrycode = rootView.findViewById(R.id.spnCountryCode);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.Is_Valid_Sign_Number_Validation(8,14, txtAgenid))
                {
                    StartApp.PREFIK_NUMBER = prefik_code;
                    EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI, txtAgenid.getText().toString(), prefik_code));

                }
            }
        });
        initToolbar(rootView);
        arrCode = _db.getCountryCode();
        //Log.e("countrycode", "ada " + arrCode.size());
        String[] items = new String[arrCode.size()];
        for (int j=0; j < arrCode.size(); j++)
        {
            items[j] = arrCode.get(j).PREFIK;
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_country_code, items);
        adapter.setDropDownViewResource(R.layout.spinner_item_country_code);
        spnCountrycode.setAdapter(adapter);
        spnCountrycode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefik_code = (String)adapterView.getSelectedItem();
                //Log.e("prefi", prefik_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return rootView;
    }

    private void initToolbar(View view){
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, null));
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
        Constant.Is_Valid_Sign_Number_Validation(8,14, txtAgenid);
    }

}
