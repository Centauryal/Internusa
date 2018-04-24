package com.supersoft.internusa.ui.payment;

/**
 * Created by itclub21 on 3/9/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.supersoft.internusa.R;
import com.supersoft.internusa.model.MsgEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.ParseException;

public class FragmentIsiSaldo extends Fragment implements TextWatcher {
    View rootView;
    Button btnLanjutkan;
    EditText txtNominal;
    private DecimalFormat df;
    private DecimalFormat dfnd;
    private boolean hasFractionalPart;

    public static FragmentIsiSaldo newInstance(String content) {
        FragmentIsiSaldo fragment = new FragmentIsiSaldo();
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        df = new DecimalFormat("#,###.##");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("#,###");
        hasFractionalPart = false;

        rootView = inflater.inflate(R.layout.fragment_isi_saldo, null, false);
        txtNominal = rootView.findViewById(R.id.txtNominal);
        btnLanjutkan = rootView.findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNominal.getText().length() > 0) {
                    String nnom = txtNominal.getText().toString().replace(".","");
                    nnom = nnom.replace(",","");
                    double nom = Double.parseDouble(nnom);
                    double min = 10000.0;

                    if (nom >= min) {
                        EventBus.getDefault().post(new MsgEvent(1, txtNominal.getText().toString()));
                    }else{
                        Toast.makeText(getActivity(), "Maaf Desposit minimal 10.000", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Maaf silahkan masukan nominal dulu", Toast.LENGTH_SHORT).show();
                }

            }
        });
        txtNominal.addTextChangedListener(this);
        return rootView;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        hasFractionalPart = s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
    }

    @Override
    public void afterTextChanged(Editable s) {
        txtNominal.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = txtNominal.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = txtNominal.getSelectionStart();
            if (hasFractionalPart) {
                txtNominal.setText(df.format(n));
            } else {
                txtNominal.setText(dfnd.format(n));
            }
            endlen = txtNominal.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= txtNominal.getText().length()) {
                txtNominal.setSelection(sel);
            } else {
                // place cursor at the end?
                txtNominal.setSelection(txtNominal.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        txtNominal.addTextChangedListener(this);
    }
}
