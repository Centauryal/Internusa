package com.supersoft.internusa.ui.payment;

/**
 * Created by itclub21 on 3/9/2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.supersoft.internusa.R;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojogetBank;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

public class FragmentKonfirmasiSaldo extends Fragment {

    View rootView;
    Bundle item;
    EditText txtNominal;
    Button btnLanjutkan;
    ListView lvwData;
    String selectedBank = "";
    String nominal = "";
    static PojogetBank gBank;
    private int selectedPosition = -1;

    public static FragmentKonfirmasiSaldo newInstance(PojogetBank bank) {
        FragmentKonfirmasiSaldo fragment = new FragmentKonfirmasiSaldo();
        gBank = bank;
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
        lvwData = rootView.findViewById(R.id.lvwData);
        btnLanjutkan = rootView.findViewById(R.id.btnLanjutkan);
        btnLanjutkan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedPosition == -1)
                {
                    Toast.makeText(getActivity(), "Silahkan pilih Bank Tujuan dulu.", Toast.LENGTH_SHORT).show();
                    return;
                }
                EventBus.getDefault().post(new MsgEvent(2,nominal, selectedBank));
            }
        });
        txtNominal.setText("Rp. "+gBank.getNominal());
        nominal = gBank.getNominal();
        ArrayList<PojogetBank.Datum> arDatum = new ArrayList<>(gBank.getData());
        listViewAdapter adapter = new listViewAdapter(getActivity(), R.layout.item_listview, arDatum);
        lvwData.setAdapter(adapter);

        return rootView;
    }


    private class listViewAdapter extends ArrayAdapter<PojogetBank.Datum>
    {
        private ArrayList<PojogetBank.Datum> myFriends;
        private Activity activity;


        public listViewAdapter(Activity context, int resource, ArrayList<PojogetBank.Datum> objects) {
            super(context, resource, objects);

            this.activity = context;
            this.myFriends = objects;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            // If holder not exist then locate all view from UI file.
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_listview, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                // if holder created, get tag from view
                holder = (ViewHolder) convertView.getTag();
            }

            holder.checkBox.setTag(position); // This line is important.

            holder.nama.setText(getItem(position).getNama());
            holder.bank.setText(getItem(position).getBank());
            holder.norek.setText(getItem(position).getRek());
            if (position == selectedPosition) {
                holder.checkBox.setChecked(true);
                selectedBank = getItem(position).getBank();
            } else holder.checkBox.setChecked(false);

            holder.checkBox.setOnClickListener(onStateChangedListener(holder.checkBox, position));

            return convertView;
        }

        private View.OnClickListener onStateChangedListener(final RadioButton checkBox, final int position) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        selectedPosition = position;
                    } else {
                        selectedPosition = -1;
                    }
                    notifyDataSetChanged();
                }
            };
        }

        private class ViewHolder {
            private TextView nama, norek, bank;
            private RadioButton checkBox;
            private View view;

            public ViewHolder(View v) {
                view = v;
                checkBox = v.findViewById(R.id.check);
                nama = v.findViewById(R.id.name);
                norek = v.findViewById(R.id.norek);
                bank = v.findViewById(R.id.bank);
            }
        }
    }
}
