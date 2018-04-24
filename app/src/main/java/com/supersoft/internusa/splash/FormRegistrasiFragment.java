package com.supersoft.internusa.splash;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.CountryCode;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.R;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import retrofit2.Call;

/**
 * Created by Centaury on 21/04/2018.
 */
public class FormRegistrasiFragment extends Fragment implements TextWatcher {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;
    @BindView(R.id.txtFullname)
    EditText txtFullname;
    @BindView(R.id.txtPhonenumber)        EditText txtPhonenumber;
    @BindView(R.id.txtAddress)
    DelayAutoCompleteTextView txtAddress;
    @BindView(R.id.txtEmail)              EditText txtEmail;
    @BindView(R.id.txtUpline)             EditText txtUpline;
    @BindView(R.id.male_radio_button)
    RadioButton male_radio_button;
    @BindView(R.id.female_radio_button)   RadioButton female_radio_button;
    @BindView(R.id.txtPassword)           EditText txtPassword;
    @BindView(R.id.txtConfirmPassword)    EditText txtConfirmPassword;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar pb_loading_indicator;
    @BindView(R.id.spnCountryCode)
    Spinner spnCountryCode;
    ArrayList<CountryCode> countryCode = new ArrayList<>();
    DBHelper _db;
    String prefik_code = "";


    public static FormRegistrasiFragment newInstnace()
    {
        FormRegistrasiFragment fragment = new FormRegistrasiFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.form_registrasi_fragment_v1, container, false);
        ButterKnife.bind(this, rootView);
        _db = new DBHelper(getActivity());
        initToolbar();
        countryCode = _db.getCountryCode();
        //Log.e("countrycode", "ada " + arrCode.size());
        String[] items = new String[countryCode.size()];
        for (int j=0; j < countryCode.size(); j++)
        {
            items[j] = countryCode.get(j).PREFIK;
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_country_code, items);
        adapter.setDropDownViewResource(R.layout.spinner_item_country_code);
        spnCountryCode.setAdapter(adapter);
        spnCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtAddress.setThreshold(3);
        txtAddress.setAdapter(new AddressAutoCompleteAdapter(getContext())); // 'this' is Activity instance
        txtAddress.setLoadingIndicator(pb_loading_indicator);
        txtAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtAddress.setText(book.getKkNama());
            }
        });
    }

    private void initToolbar()
    {
        if(toolbar != null)
        {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(activity.getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EventBus.getDefault().post(new MsgEvent(Constant.EVENT_AKTIFASI_DAFTAR_ONLINE, null));
                }
            });
        }
    }

    @Optional
    @OnClick(R.id.btnSubmit)
    public void onSubmit(View view)
    {
        if (Constant.Is_valid_all(txtFullname)
                && Constant.Is_Valid_Sign_Number_Validation(7, 15, txtPhonenumber)
                && Constant.Is_Valid_Email(txtEmail)
                && Constant.Is_valid_all(txtAddress)
                && Constant.Is_valid_all(txtPassword)
                && Constant.Is_Confirm_Password(txtPassword, txtConfirmPassword)) {

            ModelRequestInfo req = new ModelRequestInfo();
            req.hp = prefik_code + txtPhonenumber.getText().toString();
            req.nama = txtFullname.getText().toString();
            req.address = txtAddress.getText().toString();
            req.email = txtEmail.getText().toString();
            req.upline = txtUpline.getText().toString();
            req.password = txtPassword.getText().toString();
            req.confpassword = txtConfirmPassword.getText().toString();
            req.gender = (male_radio_button.isChecked()) ? "L" : "P";
            req.ccode = prefik_code;
            EventBus.getDefault().post(new MsgEvent(Constant.EVENT_MERGE_MEMBER, "merge", req));
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
        Constant.Is_valid_all(txtFullname);
        Constant.Is_Valid_Sign_Number_Validation(7, 15, txtPhonenumber);
        Constant.Is_Valid_Email(txtEmail);
        Constant.Is_valid_all(txtAddress);
        Constant.Is_valid_all(txtUpline);
        Constant.Is_valid_all(txtPassword);
        Constant.Is_Confirm_Password(txtPassword, txtConfirmPassword);
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
            //"android/v2/index.php/microz/getCities/{query}"
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
