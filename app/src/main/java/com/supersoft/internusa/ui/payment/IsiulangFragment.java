package com.supersoft.internusa.ui.payment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.HistorytrxModel;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoLoadVoucher;
import com.supersoft.internusa.model.PojoLoadVoucherDatum;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 3/7/2017.
 */

public class IsiulangFragment extends Fragment implements TextWatcher, View.OnClickListener {
    View rootView;
    DBHelper _db;
    Session _session;
    static PojoLoadProvider.Child getChild;
    static PojoLoadProvider.Row getRow;
    @BindView(R.id.txtPhonenumber)
    DelayAutoCompleteTextView txtPhonenumber;
    @BindView(R.id.pgBar) ProgressBar pb_loading_indicator;
    EditText txtNominal;
    Spinner spnVoucher;
    Button btnNext;
    AlertDialog _dialog;

    //header
    TextView txtInfo;
    TextView txtOperator;
    TextView txtDescription;
    TextView txtUrutan;


    String lblVoc = "";
    String opr;
    voucherAdapter adapter;
    LinearLayout mainFrame;
    ArrayAdapter<String> mAdapter;
    ArrayList<PojoLoadVoucherDatum> aResult = new ArrayList<>();
    final static int PERMISSION_REQUEST_CONTACT = 222;


    public static IsiulangFragment newInstance(PojoLoadProvider.Child CH, PojoLoadProvider.Row RW) {
        IsiulangFragment fragment = new IsiulangFragment();
        getChild = CH;
        getRow = RW;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.isiulang_fragment, container, false);
        ButterKnife.bind(this, rootView);
        _session = new Session(getActivity());
        _db = new DBHelper(getActivity());
        _dialog = new SpotsDialog(getActivity(), R.style.Custom);
        txtInfo = rootView.findViewById(R.id.txtInfo);
        txtDescription = rootView.findViewById(R.id.txtDescription);
        txtOperator = rootView.findViewById(R.id.txtOperator);
        txtUrutan = rootView.findViewById(R.id.txtUrutan);
        spnVoucher = rootView.findViewById(R.id.spnVoucher);
        //mainFrame = (LinearLayout)rootView.findViewById(R.id.coordinate);
        btnNext = rootView.findViewById(R.id.btnSubmit);
        btnNext.setOnClickListener(this);

        txtNominal = rootView.findViewById(R.id.txtNominal);

        txtPhonenumber.setThreshold(3);
        txtPhonenumber.setAdapter(new NomorAutocompleteAdapter(_db, getActivity())); // 'this' is Activity instance
        txtPhonenumber.setLoadingIndicator(pb_loading_indicator);
        txtPhonenumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HistorytrxModel book = (HistorytrxModel) adapterView.getItemAtPosition(position);
                txtPhonenumber.setText(book.TUJUAN);
            }
        });
        return rootView;
    }

    @OnClick({R.id.btnPhonebook})
    public void onButterClick(View id)
    {
        switch (id.getId())
        {
            case R.id.btnPhonebook:
                askForContactPermission();
                break;
        }
    }
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        initStatusHeader();

    }
    private void initStatusHeader(){

        txtInfo.setText(getRow.getNama());
        txtOperator.setText(getChild.getName());
        opr = getChild.getName();
        adapter = new voucherAdapter(getActivity(), aResult);
        adapter.setDropDownViewResource(R.layout.spinner_voucher);

        final PojoLoadVoucherDatum voucher = new PojoLoadVoucherDatum();
        voucher.setVtype("Please wait...");
        voucher.setHarga("--");
        voucher.setKet("");
        aResult = new ArrayList<PojoLoadVoucherDatum>();
        aResult.add(voucher);
        adapter = new voucherAdapter(getActivity(), aResult);
        spnVoucher.setAdapter(adapter);
        spnVoucher.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadVoucher(_db.getProfilDb());
            }
        }, 2000);
        spnVoucher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PojoLoadVoucherDatum data = (PojoLoadVoucherDatum)adapterView.getItemAtPosition(i);
                lblVoc = (data.getVtype().equals("Pilih Salah Satu")) ? "" : data.getVtype();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        Constant.Is_Valid_Sign_Number_Validation(8,20,txtPhonenumber);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSubmit:
                if(Constant.isConnectingToInternet(getActivity())){
                    if(Constant.Is_Valid_Sign_Number_Validation(8,20, txtPhonenumber)){
                        ProcessIsiulang(_db.getProfilDb());
                    }
                }else {
                    Constant.showInfoMessageDialog(getActivity(),"Anda tidak terhubung dengan internet!\nCek koneksi Anda.","ERROR");
                }
                break;
        }
    }


    private void ProcessIsiulang(ProfilDB profil){
        _dialog.show();
        final ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.pin = profil.pin;
        inf.hp = profil.hp;
        inf.deviceid = _session.getDeviceId();
        inf.msisdn = txtPhonenumber.getText().toString();
        inf.nominal = txtNominal.getText().toString();
        inf.voc = lblVoc;
        inf.code = getChild.getCode();
        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"confirmation");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> api = githubUserAPI.onSubmitDefault(inf);
        api.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                _dialog.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }
                //Log.e("RES", new Gson().toJson(response.body()));

                _db.addHistory(new HistorytrxModel(txtPhonenumber.getText().toString()));
                int status = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("status").getAsInt();
                String message = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("message").getAsString();
                int isOtp;
                try{
                    isOtp = response.body().get("response").getAsJsonArray().get(0).getAsJsonObject().get("isotp").getAsInt();
                }catch (NullPointerException e)
                {
                    isOtp = 0;
                }

                if(status == 1){
                    Constant.goToNextFragement(getActivity(), MKonfirmasiPaymentFragment.newInstance(isOtp == 1, response.body(),"recharge",inf),R.id.container, false, false, false);
                }
                else if(status == 2)
                {
                    Log.e("masuk status ", "2 ");
                    Constant.showInfoMessageDialog(getActivity(), message, "Info");
                }
                else if(status == 8)
                {
                    Log.e("masuk status ", "8 ada yg belum di set");
                    Constant.showInfoMessageDialog(getActivity(), message, "Error");
                }else
                {
                    Constant.showInfoMessageDialog(getActivity(), message, "Failed");
                }
                //
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                _dialog.dismiss();
                Constant.showInfoMessageDialog(getActivity(), t.getMessage(), "Failed");
            }
        });

    }

    private class voucherAdapter extends ArrayAdapter<PojoLoadVoucherDatum>
    {
        ArrayList<PojoLoadVoucherDatum> result;
        Context _context;

        public voucherAdapter(Context ctx, ArrayList<PojoLoadVoucherDatum> rs){
            super(ctx,0, rs);
            _context = ctx;
            result = rs;

        }

        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public PojoLoadVoucherDatum getItem(int i) {
            return result.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return customView(i, view, viewGroup);
        }

        public View getDropDownView(int i, View view, ViewGroup viewGroup){

            return customView(i, view, viewGroup);
        }

        public View customView(int i, View view, ViewGroup viewGroup){
            PojoLoadVoucherDatum data = getItem(i);
            if(view == null){
                view = LayoutInflater.from(_context).inflate(R.layout.spinner_voucher,viewGroup, false);
            }
            TextView txt = view.findViewById(android.R.id.text1);
            txt.setText(data.getVtype() +" " + data.getKet() + " ("+data.getHarga()+")");


            return view;
        }
    }

    private void loadVoucher(ProfilDB db){
        final ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = db.agenid;
        inf.opr = getChild.getOpr();
        final PojoLoadVoucherDatum voucher = new PojoLoadVoucherDatum();
        voucher.setVtype("Pilih Salah Satu");
        voucher.setHarga("Optional");
        voucher.setKet("");

        try {
            RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI), "loadVoucher");
            RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);

            Call<PojoLoadVoucher> call = githubUserAPI.loadVoucher(inf);
            call.enqueue(new Callback<PojoLoadVoucher>() {
                @Override
                public void onResponse(Call<PojoLoadVoucher> call, Response<PojoLoadVoucher> response) {
                    //Log.e("LOGG", new Gson().toJson(response));
                    if (!response.isSuccessful()) {
                        return;
                    }
                    PojoLoadVoucher pojos = response.body();
                    aResult = new ArrayList<PojoLoadVoucherDatum>(pojos.getData());
                    aResult.add(voucher);
                    adapter = new voucherAdapter(getActivity(), aResult);
                    spnVoucher.setAdapter(adapter);
                    spnVoucher.setSelection(aResult.size() - 1);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<PojoLoadVoucher> call, Throwable t) {
                    Snackbar snackbar = Snackbar.make(mainFrame, t.getMessage(), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loadVoucher(_db.getProfilDb());
                        }
                    });
                    View view = snackbar.getView();

                    TextView tv = view.findViewById(android.support.design.R.id.snackbar_text);
                    //tv.setCompoundDrawablesWithIntrinsicBounds(null, getActivity().getResources().getDrawable(R.drawable.ic_cloud_off_black_48dp),null,null);
                    tv.setTextColor(Color.RED);
                    snackbar.show();
                }
            });
        }
        catch (NullPointerException ex)
        {

        }
    }


    private void getContact(int PICK){
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK);
    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},PERMISSION_REQUEST_CONTACT);
                }
            }
            else
            {
                getContact(PERMISSION_REQUEST_CONTACT);
            }
        }
        else
        {
            getContact(PERMISSION_REQUEST_CONTACT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContact(PERMISSION_REQUEST_CONTACT);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getActivity(),"No permission for contacts", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String contactNumber = "";
        String contactID = "";
        if(resultCode == getActivity().RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_CONTACT) {
                Uri contactData = data.getData();
                Cursor cursorID = getActivity().getContentResolver().query(contactData, new String[]{ContactsContract.Contacts._ID}, null, null, null);
                if (cursorID.moveToFirst()) {

                    contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
                }
                cursorID.close();
                //Log.d("CONTACT", "Contact ID: " + contactID);

                Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                                ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                        new String[]{contactID},
                        null);

                if (cursorPhone.moveToFirst()) {
                    contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }

                cursorPhone.close();
                txtPhonenumber.setText(Constant.replaceNonDigit(contactNumber));
            }
        }
    }

}
