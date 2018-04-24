package com.supersoft.internusa.splash;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supersoft.internusa.Dashboard;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.CountryCode;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Centaury on 21/04/2018.
 */
public class SplashFragment extends Fragment {
    static Session _session;
    static DBHelper _db = null;
    ProfilDB pf;
    String UUID = "";
    private CoordinatorLayout mainFrame;
    private PrefManager prefManager;

    public static SplashFragment newInstnace(DBHelper db, Session session)
    {
        SplashFragment fragment = new SplashFragment();
        _db = db;
        _session = session;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.splash_screen, container, false);
        mainFrame = rootView.findViewById(R.id.containercor);
        pf = _db.getProfilDb();
        prefManager = new PrefManager(getActivity());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.UUID = savedInstanceState.getString("UUID");
            loading(this.UUID);
        }else {
            loading(prefManager.getUuid());
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("UUID", this.UUID);
    }

    private void loading(final String uuid)
    {
        _session.setDeviceId(uuid);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String activated = _db.getFieldProfil("activated", true);
                String islogin = _db.getFieldProfil("islogin", true);
                ArrayList<CountryCode> cCode = _db.getCountryCode();
                if(cCode.size() <= 0)
                {
                    insertCountryCode();
                }

                if(activated.equals("1")){
                    if(islogin.equals("1")) {

                        Constant.showActivity(getActivity(), Dashboard.class, true);

                    }else {
                        EventBus.getDefault().post(new MsgEvent(Constant.EVENT_LOGIN, ""));
                    }
                }else {
                    EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, uuid));
                }

            }
        },3000);
    }



    public void insertCountryCode()
    {
        List<CountryCode> lcode = new ArrayList<>();
        lcode.add(new CountryCode("+62", "Indonesia"));
        lcode.add(new CountryCode("+60", "malaysia"));
        lcode.add(new CountryCode("+65", "Singapore"));
        lcode.add(new CountryCode("+82", "Korea"));
        lcode.add(new CountryCode("+81", "Japan"));
        lcode.add(new CountryCode("+886", "Taiwan"));
        lcode.add(new CountryCode("+852", "Hongkong"));
        lcode.add(new CountryCode("+850", "Korea Selatan"));
        lcode.add(new CountryCode("+966", "Saudi Arabia"));
        for(int i=0; i < lcode.size(); i++)
        {
            _db.createCountrycode(lcode.get(i));
        }

    }
}
