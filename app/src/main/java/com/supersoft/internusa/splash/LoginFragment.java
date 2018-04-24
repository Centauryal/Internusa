package com.supersoft.internusa.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Centaury on 21/04/2018.
 */
public class LoginFragment extends Fragment implements View.OnClickListener, TextWatcher {
    Button btnSubmit;
    static Session _session;
    static DBHelper _db = null;
    EditText txtPassword, txtAgenid;
    TextView txtAktifasi;

    public static LoginFragment newInstnace(DBHelper db, Session session)
    {
        LoginFragment fragment = new LoginFragment();
        _db = db;
        _session = session;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.login_activity, container, false);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        txtAgenid = rootView.findViewById(R.id.txtAgenid);
        txtPassword = rootView.findViewById(R.id.txtPassword);
        txtAktifasi = rootView.findViewById(R.id.txtAktifasi);
        btnSubmit.setOnClickListener(this);
        txtAktifasi.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btnSubmit)
        {
            //EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI, null));
            if(Constant.Is_valid_all(txtAgenid) &&
                    Constant.Is_valid_all(txtPassword)) {
                //Constant.showActivity(getActivity(), DashBoard.class, true);
                EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, txtAgenid.getText().toString().toUpperCase(),txtPassword.getText().toString()));
            }
        }
        else if(view.getId() == R.id.txtAktifasi)
        {
            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, ""));
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
        Constant.Is_valid_all(txtAgenid);
        Constant.Is_valid_all(txtPassword);
    }
}
