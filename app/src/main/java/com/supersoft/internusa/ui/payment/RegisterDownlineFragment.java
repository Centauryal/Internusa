package com.supersoft.internusa.ui.payment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class RegisterDownlineFragment extends Fragment implements TextWatcher {

    @BindView(R.id.txtUpline)
    EditText txtUpline;

    @BindView(R.id.txtNama)
    EditText txtNama;

    @BindView(R.id.txtNohp)
    EditText txtNohp;

    @BindView(R.id.txtEmail)
    EditText txtEmail;

    @BindView(R.id.txtKota)
    DelayAutoCompleteTextView txtKota;

    @BindView(R.id.txtAddress)
    EditText txtAddress;

    @BindView(R.id.male_radio_button)
    RadioButton male_radio_button; // REGULAR

    @BindView(R.id.female_radio_button)
    RadioButton female_radio_button; //NETWORK

    @BindView(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    AlertDialog _dialog;
    public static RegisterDownlineFragment newInstance()
    {
        RegisterDownlineFragment fm = new RegisterDownlineFragment();
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_downline_fragment, container, false);

        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _dialog = new SpotsDialog(getActivity(), R.style.Custom);

        txtKota.setThreshold(3);
        txtKota.setAdapter(new AddressAutoCompleteAdapter(getActivity())); // 'this' is Activity instance
        txtKota.setLoadingIndicator(pb_loading_indicator);
        txtKota.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtKota.setText(book.getKkNama());
            }
        });
    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view)
    {
        if(Constant.Is_valid_all(txtUpline) && Constant.Is_valid_all(txtNohp) && Constant.Is_Valid_Sign_Number_Validation(8, 15, txtNohp))
        {
            daftarDownline();
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
        Constant.Is_valid_all(txtUpline);
        Constant.Is_valid_all(txtNama);
        Constant.Is_Valid_Sign_Number_Validation(8,15, txtNohp);
        Constant.Is_Valid_Email(txtEmail);
        Constant.Is_valid_all(txtKota);
        Constant.Is_valid_all(txtAddress);

    }

    private void daftarDownline()
    {
        _dialog.show();
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();

        ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.deviceid = Constant.getUUID(getActivity());
        inf.pin = profil.pin;
        inf.hp = profil.hp;

        inf.msisdn = txtNohp.getText().toString();
        inf.email = txtEmail.getText().toString();
        inf.address = txtAddress.getText().toString();
        inf.kota = txtKota.getText().toString();
        inf.an = txtNama.getText().toString();
        inf.upline = txtUpline.getText().toString();
        inf.type = (male_radio_button.isChecked()) ? "REGULAR" : (female_radio_button.isChecked()) ? "NETWORK" : "";
        inf.bonusup = "0";

        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"konfirmasi_daftar");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(inf);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(_dialog.isShowing())_dialog.dismiss();
                Log.e("res", new Gson().toJson(response.body()));
                if(response.isSuccessful())
                {
                    JsonArray jArr = response.body().getAsJsonArray("response");
                    int status = jArr.get(0).getAsJsonObject().get("status").getAsInt();
                    String message = jArr.get(0).getAsJsonObject().get("message").getAsString();
                    Constant.showInfoMessageDialog(getActivity(), message, "INFO");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(_dialog.isShowing())_dialog.dismiss();
                Log.e("res", t.getMessage());
            }
        });
    }


    public class AddressAutoCompleteAdapter extends BaseAdapter implements Filterable
    {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private List<Cities> resultList = new ArrayList<Cities>();

        public AddressAutoCompleteAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public Cities getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.simple_dropdown_item_2line, parent, false);
            }
            ((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getKkNama());
            ((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getKkKode());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<Cities> books = findBooks(mContext, constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = books;
                        filterResults.count = books.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<Cities>) results.values;
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }

        /**
         * Returns a search result for the given book title.
         */
        private List<Cities> findBooks(Context context, String bookTitle) {

            List<Cities> citi = new ArrayList<>();

            RetrofitBuilder builder = new RetrofitBuilder("getCities");
            RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
            String url = String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "getCities", bookTitle);
            Call<List<Cities>> repos = service.listCities(url);
            try {
                citi = new ArrayList<Cities>(repos.execute().body());
                //Log.e("res", new Gson().toJson(citi));
            } catch (IOException e) {
                e.printStackTrace();
                //Log.e("ERR", e.getMessage());
            }

            return citi;
        }
    }
}
