package com.supersoft.internusa.ui.payment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.model.HistorytrxModel;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 3/10/2017.
 */

public class PrabayarFragment extends Fragment{

    View rootView;
    //header
    TextView txtInfo;
    TextView txtOperator;
    TextView txtDescription;
    TextView txtUrutan;

    @BindView(R.id.txtPhonenumber)
    DelayAutoCompleteTextView txtPhonenumber;
    @BindView(R.id.txtNopelanggan)
    DelayAutoCompleteTextView txtNopelanggan;
    @BindView(R.id.pgBar)
    ProgressBar pgBarNoPelanggan;
    @BindView(R.id.pgbarLoading)
    ProgressBar pgBarHppelanggan;
    final static int PERMISSION_REQUEST_CONTACT = 222;

    static PojoLoadProvider.Child getChild;
    static PojoLoadProvider.Row getRow;
    AlertDialog _dialog;
    String opr = "";
    DBHelper _db;


    public static PrabayarFragment newInstance(PojoLoadProvider.Child CH, PojoLoadProvider.Row RW) {
        PrabayarFragment fragment = new PrabayarFragment();
        getChild = CH;
        getRow = RW;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.prabayar_fragment, container, false);
        ButterKnife.bind(this, rootView);
        _dialog = new SpotsDialog(getActivity(), R.style.SpotsDialogDefault);
        _db = new DBHelper(getActivity());
        txtInfo = rootView.findViewById(R.id.txtInfo);
        txtDescription = rootView.findViewById(R.id.txtDescription);
        txtOperator = rootView.findViewById(R.id.txtOperator);
        txtUrutan = rootView.findViewById(R.id.txtUrutan);

        txtPhonenumber.setThreshold(3);
        txtPhonenumber.setAdapter(new NomorAutocompleteAdapter(_db, getActivity())); // 'this' is Activity instance
        txtPhonenumber.setLoadingIndicator(pgBarHppelanggan);
        txtPhonenumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HistorytrxModel book = (HistorytrxModel) adapterView.getItemAtPosition(position);
                txtPhonenumber.setText(book.TUJUAN);
            }
        });

        txtNopelanggan.setThreshold(3);
        txtNopelanggan.setAdapter(new NomorAutocompleteAdapter(_db, getActivity())); // 'this' is Activity instance
        txtNopelanggan.setLoadingIndicator(pgBarNoPelanggan);
        txtNopelanggan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                HistorytrxModel book = (HistorytrxModel) adapterView.getItemAtPosition(position);
                txtNopelanggan.setText(book.TUJUAN);
            }
        });

        return rootView;
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        //_session = new Session(getActivity());
        initStatusHeader();

    }
    private void initStatusHeader(){
        txtInfo.setText(getRow.getNama());
        txtOperator.setText(getChild.getName());
        opr = getChild.getName();

    }

    @OnClick({R.id.btnPhonebook, R.id.btnSubmit})
    public void butterOnClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnSubmit:
                if(Constant.isConnectingToInternet(getActivity())){
                    if(Constant.Is_Valid_Sign_Number_Validation(8,20, txtNopelanggan)){
                        _db.addHistory(new HistorytrxModel(txtPhonenumber.getText().toString()));
                        cek_tagihan(_db.getProfilDb());
                    }
                }else {
                    Constant.showInfoMessageDialog(getActivity(),"Anda tidak terhubung dengan internet!\nCek koneksi Anda.","ERROR");
                }
                break;
            case R.id.btnPhonebook:
                askForContactPermission();
                break;
        }
    }

    private void cek_tagihan(ProfilDB profil){
        _dialog.show();
        final ModelRequestInfo inf = new ModelRequestInfo();
        inf.agenid = profil.agenid;
        inf.pin = profil.pin;
        inf.hp = profil.hp;
        inf.deviceid = Constant.getUUID(getActivity());
        inf.msisdn = txtNopelanggan.getText().toString();
        inf.hp_pelanggan = txtPhonenumber.getText().toString();
        inf.code = getChild.getCode();

        //Log.e("request", new Gson().toJson(inf));
        RetrofitBuilder builder = new RetrofitBuilder(getActivity().getResources().getString(R.string.CONF_URI),"cek_tagihan");
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

                _db.addHistory(new HistorytrxModel(txtNopelanggan.getText().toString()));
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
                    Constant.showInfoMessageDialog(getActivity(), message, "Info");
                }
                else if(status == 2)
                {

                    String datum = new Gson().toJson(message);
                    Constant.goToNextFragement(getActivity(), MKonfirmasiPaymentFragment.newInstance(isOtp == 1, response.body(), "bayar_tagihan", inf),R.id.container, false, false, false);
                }
                else if(status == 8)
                {
                    Log.e("masuk status ", "8 ada yg belum di set");
                    Constant.showInfoMessageDialog(getActivity(), message, "Error Code #8");
                }else
                {
                    Constant.showInfoMessageDialog(getActivity(), message, "Failed Code Unknown");
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
        if (resultCode == getActivity().RESULT_OK) {
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
                txtNopelanggan.setText(Constant.replaceNonDigit(contactNumber));
            }
        }
    }
}
