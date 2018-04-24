package com.supersoft.internusa.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Centaury on 21/04/2018.
 */
public class DaftarOnlineFragment extends Fragment implements View.OnClickListener {

    Button btnSubmit, btnDaftar;

    public static DaftarOnlineFragment newInstnace()
    {
        DaftarOnlineFragment fragment = new DaftarOnlineFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.aktifasi_daftaronline, container, false);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);
        btnDaftar = rootView.findViewById(R.id.btnDaftar);
        btnSubmit.setOnClickListener(this);
        btnDaftar.setOnClickListener(this);
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
            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI, ""));
        }
        else if(view.getId() == R.id.btnDaftar)
        {
            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_FORM_REGISTRASI, ""));
        }
    }
}
