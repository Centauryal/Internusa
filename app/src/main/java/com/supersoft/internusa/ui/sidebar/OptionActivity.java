package com.supersoft.internusa.ui.sidebar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.bluetooth.BluetoothService;
import com.supersoft.internusa.helper.bluetooth.BluetoothState;
import com.supersoft.internusa.helper.bluetooth.DeviceList;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.ui.chat.ChatActivity;
import com.supersoft.internusa.ui.chat.HistoryPembayaranActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 4/18/2017.
 */

public class OptionActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    final static int PERMISSION_REQUEST_CONTACT = 1090;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_activity);
        ButterKnife.bind(this);

        initTollbar();


        if (getSupportFragmentManager().findFragmentById(R.id.content_preference) == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_preference, new SettingsFragment()).commit();
        }
    }

    private void initTollbar()
    {
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }




    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {
        private static final String CONTACT_ID = ContactsContract.Contacts._ID;
        private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        private static final String PHONE_CONTACT_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;

        private static final int REQUEST_ENABLE_BT = 2;
        private static final int REQUEST_CONNECT_DEVICE = 1;


        Preference mHistory;
        Preference mExportContact;
        Preference mImportContact;
        Preference mNotification;
        Preference mServicebg;
        Preference mConnecting_printer;

        Session _session;

        private int mProgressStatus;
        Handler mHandler = new Handler();

        BluetoothService _bt;
        BluetoothDevice con_dev = null;



        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.option_preferences);
            _session = new Session(getActivity());
            _bt = new BluetoothService(getActivity(), bHandler);


            mHistory = findPreference("hitory_pembayaran");

            mExportContact = findPreference("sync_contact");
            mImportContact = findPreference("retreive_contact");
            mConnecting_printer = findPreference("connecting_printer");
            mNotification = findPreference("all_notification");
            mServicebg = findPreference("run_as_background");
            mNotification.setSummary(_session.getOptionNotification() ? "Enabled" : "Disabled");
            mServicebg.setSummary(_session.getOptionServiceBg() ? "Service as backgound is ON" : "Service as backgound is OFF");

            mHistory.setOnPreferenceClickListener(this);
            mExportContact.setOnPreferenceClickListener(this);
            mImportContact.setOnPreferenceClickListener(this);
            mNotification.setOnPreferenceClickListener(this);
            mServicebg.setOnPreferenceClickListener(this);
            mConnecting_printer.setOnPreferenceClickListener(this);

            EventBus.getDefault().register(this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mExportContact.setSummary((checkPermissionReadContact()) ? "Total Kontak " + getAll(getActivity()).size() : "Kontak membutuhkan permission.");
                }
            }, 1000);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            switch (preference.getKey())
            {
                case "chat":
                    Constant.showActivity(getActivity(), ChatActivity.class, false);
                    break;
                case "hitory_pembayaran":
                    Constant.showActivity(getActivity(), HistoryPembayaranActivity.class, false);
                    break;
                case "hitory_info":

                    break;
                case "connecting_printer":
                    askForBlututPermission();
                    if(checkPermissionBlutut()) {
                        if( _bt.isBTopen() == false ){
                            EventBus.getDefault().post(BluetoothState.BT_SERVICE_OPEN);
                        }
                        else
                        {
                            String address = _session.getLastMacAddrBlutut();
                            if (address.length() < 8){
                                EventBus.getDefault().post(BluetoothState.BT_FIND_ADDRESS);
                            }
                            else{
                                if(_bt.getState() == 3){
                                    //printText("");
                                }else {
                                    con_dev = _bt.getDevByMac(address);
                                    _bt.connect(con_dev);
                                }
                            }
                        }
                    }
                    break;
                case "sync_contact": //export
                    askForContactPermission();
                    if(checkPermissionReadContact()) {
                        mExportContact.setSummary("Total Kontak " + getAll(getActivity()).size());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Backup Contact");
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                exportContact();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, null);
                        builder.setMessage("Anda yakin akan export semua kontak Anda?\nData kontak Anda adalah privacy yang kami jaga. ");//TODO put real question

                        builder.show();

                    }
                    break;
                case "retreive_contact": //import
                    askForWriteContactPermission();
                    if(checkPermissionWriteContact()) {
                        retreiveContact();
                    }
                    break;
                case "all_notification":
                    boolean checked = ((SwitchPreferenceCompat) preference).isChecked();

                    _session.setOptionNotification(checked);
                    preference.setSummary(checked == false ? "Disabled" : "Enabled");
                    break;
                case "run_as_background":
                    boolean chkService = ((SwitchPreferenceCompat) preference).isChecked();

                    _session.setOptionServiceBg(chkService);
                    preference.setSummary(chkService == false ? "Service as backgound is OFF" : "Service as backgound is ON");
                    if(chkService)
                        getActivity().sendBroadcast(new Intent("com.supersoft.internusa.StartForeground"));
                    else
                        getActivity().sendBroadcast(new Intent("com.supersoft.internusa.StopForeground"));

                    break;

            }

            return false;
        }


        @Subscribe
        public void onReceive(Object act)
        {
            if(act instanceof Integer)
            {
                int ACTION = (int)act;
                if (ACTION == BluetoothState.BT_CONNECT_DEV_SUCCESS) {
                }else if (ACTION == BluetoothState.BT_SERVICE_OPEN) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }else if (ACTION == BluetoothState.BT_CONNECT_DEV_FAILED) {
                    Toast.makeText(getActivity(),"Failed..", Toast.LENGTH_SHORT).show();
                }else if (ACTION == BluetoothState.BT_FIND_ADDRESS) {
                    Intent inte = new Intent(getActivity(), DeviceList.class);
                    startActivityForResult(inte, BluetoothState.REQUEST_CONNECT_DEVICE);
                }else if (ACTION == BluetoothState.REQUEST_ENABLE_BT) {
                    String address = _session.getLastMacAddrBlutut();
                    //Log.e("READ",address);
                    if (address.length() < 8){
                        Intent inte = new Intent(getActivity(), DeviceList.class);
                        startActivityForResult(inte, BluetoothState.REQUEST_CONNECT_DEVICE);
                    }else{
                        con_dev = _bt.getDevByMac(address);
                        _bt.connect(con_dev);
                    }
                }else if (ACTION == BluetoothState.BT_PRINT_FINISH){

                }
            }

        }

        private final Handler bHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //txtInfo.setVisibility(View.VISIBLE);
                switch (msg.what) {
                    case BluetoothService.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothService.STATE_CONNECTED:
                                //txtInfo.setText("connected");
                                //Intent inten = new Intent();

                                EventBus.getDefault().post(BluetoothState.BT_CONNECT_DEV_SUCCESS);
                                break;
                            case BluetoothService.STATE_CONNECTING:
                                //txtInfo.setText("connecting");
                                break;
                            case BluetoothService.STATE_LISTEN:
                            case BluetoothService.STATE_NONE:
                                //txtInfo.setText("Unknown Message");
                                break;
                        }
                        break;
                    case BluetoothService.MESSAGE_CONNECTION_LOST:
                        //txtInfo.setText("Device connection was lost");
                        break;
                    case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸

                        EventBus.getDefault().post(BluetoothState.BT_CONNECT_DEV_FAILED);
                        break;
                }
            }

        };

        private boolean checkPermissionBlutut()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;

            }
            else{
                return true;
            }
        }

        private boolean checkPermissionReadContact()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;

            }
            else{
                return true;
            }
        }

        private boolean checkPermissionWriteContact()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED;

            }
            else{
                return true;
            }
        }

        public ArrayList<phoneContactObject> getAll(Context context) {
            ContentResolver cr = context.getContentResolver();

            Cursor pCur = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{PHONE_NUMBER, PHONE_CONTACT_ID, PHONE_CONTACT_NAME},
                    null,
                    null,
                    null
            );
            if(pCur != null){
                //Log.e("CON", "ADA " + pCur.getCount());
                if(pCur.getCount() > 0) {
                    HashMap<Integer, ArrayList<phoneContactObject>> phones = new HashMap<>();
                    while (pCur.moveToNext()) {
                        Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));
                        //Log.e("CON", "CIS " + contactId);
                        ArrayList<phoneContactObject> curPhones = new ArrayList<>();
                        phoneContactObject obj = new phoneContactObject();
                        if (phones.containsKey(contactId)) {


                            curPhones = phones.get(contactId);
                        }
                        obj.contactId = contactId;
                        obj.contactName = pCur.getString(pCur.getColumnIndex(PHONE_CONTACT_NAME));
                        obj.contactNumber = pCur.getString(pCur.getColumnIndex(PHONE_NUMBER));
                        //Log.e("contact e", "CID " + contactId + " Name : " + obj.contactName + " NUm " + obj.contactNumber);
                        curPhones.add(obj);
                        phones.put(contactId, curPhones);
                    }
                    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,new String[]{CONTACT_ID, HAS_PHONE_NUMBER},HAS_PHONE_NUMBER + " > 0",null,null);
                    if (cur != null) {
                        if (cur.getCount() > 0) {
                            ArrayList<phoneContactObject> contacts = new ArrayList<>();
                            while (cur.moveToNext()) {
                                int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));

                                if(phones.containsKey(id)) {
                                    contacts.addAll(phones.get(id));
                                }
                            }
                            return contacts;
                        }
                        cur.close();
                    }
                }
                pCur.close();
            }
            return new ArrayList<>();
        }

        public void askForContactPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Contacts access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm Contacts access");//TODO put real question
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]
                                                {Manifest.permission.READ_CONTACTS}
                                        , PERMISSION_REQUEST_CONTACT);
                            }
                        });
                        builder.show();
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                PERMISSION_REQUEST_CONTACT);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }else{
                    getAll(getActivity());
                }
            }
            else{
                getAll(getActivity());
            }
        }


        public void askForWriteContactPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_CONTACTS)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Contacts access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm Contacts access");//TODO put real question
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]
                                                {Manifest.permission.WRITE_CONTACTS} , PERMISSION_REQUEST_CONTACT);
                            }
                        });
                        builder.show();
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_CONTACTS},PERMISSION_REQUEST_CONTACT);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
        }


        public void askForBlututPermission(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.BLUETOOTH)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Bluetooth access needed");
                        builder.setPositiveButton(android.R.string.ok, null);
                        builder.setMessage("please confirm Bluetooth access");//TODO put real question
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                requestPermissions(
                                        new String[]
                                                {Manifest.permission.BLUETOOTH} , PERMISSION_REQUEST_CONTACT);
                            }
                        });
                        builder.show();


                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.BLUETOOTH},PERMISSION_REQUEST_CONTACT);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
            switch (requestCode) {
                case PERMISSION_REQUEST_CONTACT: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        getAll(getActivity());
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.

                    } else {
                        Toast.makeText(getActivity(),"No permission for contacts", Toast.LENGTH_SHORT).show();
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                    return;
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }



        }


        private void exportContact()
        {
            ProfilDB _db = new DBHelper(getActivity()).getProfilDb();
            RetrofitBuilder builder = new RetrofitBuilder("splash");
            RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
            //"android/v2/index.php/microz/sync_phonebook/{query}"
            Call<JsonObject> call = githubUserAPI.exportContact(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "sync_phonebook", _db.hp), getAll(getActivity()));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    //Log.e("JSON", new Gson().toJson(response.body()));
                    if(response.isSuccessful())
                    {
                        int rowStatus = response.body().get("status").getAsInt();
                        String msg = response.body().get("message").getAsString();
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("ERR", t.getMessage());
                }
            });
        }

        public void startProgress() {


            final ProgressDialog progressBar = new ProgressDialog(getActivity());

            progressBar.setTitle("Please wait..");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(mProgressStatus);
            progressBar.show();
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(final Void... params) {
                    while (mProgressStatus < 100) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mProgressStatus += 15;
                        mHandler.post(new Runnable() {
                            public void run() {
                                progressBar.setProgress(mProgressStatus);
                            }
                        });
                    }
                    return null;
                }

            }.execute();
        }

        private void retreiveContact()
        {

            final ProgressDialog progressBar = new ProgressDialog(getActivity());
            final ProfilDB _db = new DBHelper(getActivity()).getProfilDb();
            RetrofitBuilder builder = new RetrofitBuilder("splash");
            RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
            //"android/v2/index.php/microz/retreive_contact/{query}"
            Call<JsonObject> call = githubUserAPI.retreiveContact(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, "retreive_contact",_db.hp));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, final Response<JsonObject> response) {
                    //Log.e("JSON", new Gson().toJson(response.body()));
                    if(response.isSuccessful())
                    {
                        int rowStatus = response.body().get("status").getAsInt();
                        final JsonArray data = response.body().get("data").getAsJsonArray();

                        if(rowStatus > 0)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Import Contact");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    progressBar.setTitle("Please wait..");
                                    progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    progressBar.setProgress(mProgressStatus);
                                    progressBar.setMax(data.size());
                                    progressBar.show();

                                    new AsyncTask<Void, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(final Void... params) {
                                            while (mProgressStatus < data.size()) {
                                                try {
                                                    JsonObject dataObj = data.get(mProgressStatus).getAsJsonObject();
                                                    String mem_id = dataObj.get("mem_id").getAsString();
                                                    String nomorhp = dataObj.get("nomorhp").getAsString();
                                                    String nama = dataObj.get("nama").getAsString();
                                                    String prefik = dataObj.get("prefik").getAsString();
                                                    AddContact(prefik + "" + nomorhp, nama);
                                                    Thread.sleep(10);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                mProgressStatus++;
                                                mHandler.post(new Runnable() {
                                                    public void run() {
                                                        progressBar.setProgress(mProgressStatus);
                                                    }
                                                });
                                            }
                                            mHandler.post(new Runnable() {
                                                public void run() {
                                                    if(progressBar.isShowing()) progressBar.dismiss();
                                                }
                                            });
                                            return null;
                                        }


                                    }.execute();


                                    /*
                                    for(int j=0; i < data.size(); j++)
                                    {
                                        JsonObject dataObj = data.get(i).getAsJsonObject();
                                        String mem_id = dataObj.get("mem_id").getAsString();
                                        String nomorhp = dataObj.get("nomorhp").getAsString();
                                        String nama = dataObj.get("nama").getAsString();
                                        String prefik = dataObj.get("prefik").getAsString();
                                        AddContact(prefik + "" + nomorhp, nama);
                                    }
                                    */
                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, null);
                            builder.setMessage("Ditemukan "+data.size()+" di ID ANda, \nApakah yakin anda akan merger atau kemungkinan duplicate data kontak anda? ");//TODO put real question

                            builder.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {

                }
            });
        }



        public boolean AddContact(String hp, String nama){

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            int rawContactInsertIndex = ops.size();

            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, hp)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, "1").build());


            //Display name/Contact name
            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Contacts.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                    .withValue(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nama)
                    .build());

            try {
                ContentProviderResult[] res = getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            return true;
        }

    } // end of frament option



}
