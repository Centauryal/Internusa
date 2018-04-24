package com.supersoft.internusa.ui.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supersoft.internusa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by itclub21 on 10/11/2017.
 */

public class FragmentKonfirmasiSaldoFinish extends Fragment {
    View rootView;
    Bundle item;
    EditText txtNominal;
    TextView lblbeli;
    Button btnLanjutkan;
    LinearLayout linearLayout;
    String selectedBank = "";
    String nominal = "";

    public static FragmentKonfirmasiSaldoFinish newInstance(Bundle bundle) {
        FragmentKonfirmasiSaldoFinish fragment = new FragmentKonfirmasiSaldoFinish();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_konfirmasi_saldo, null, false);
        txtNominal = rootView.findViewById(R.id.txtNominal);
        lblbeli = rootView.findViewById(R.id.lblbeli);
        txtNominal.setVisibility(View.GONE);
        linearLayout = rootView.findViewById(R.id.linearLayout);
        btnLanjutkan = rootView.findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setText("Selesai");
        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        //txtNominal.setText("Rp. "+item.getString("nominal"));
        nominal = item.getString("nominal");
        try{
            JSONObject obj = new JSONObject(item.getString("load_bank"));
            JSONArray arr = obj.getJSONArray("response");
            for(int i=0; i < arr.length() ; i++){
                JSONObject oo = arr.getJSONObject(i);
                lblbeli.setText(oo.getString("message").toString());
            }
        }catch (JSONException e){

        }
        return rootView;
    }
}