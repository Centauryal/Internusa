package com.supersoft.internusa.ui.profil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.supersoft.internusa.R;

/**
 * Created by itclub21 on 4/13/2017.
 */

public class ProfilFragment extends Fragment {

    View view;

    public static ProfilFragment newInstance()
    {
        ProfilFragment fm = new ProfilFragment();
        return fm;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profil_fragment, null);
        return view;
    }
}
