package com.supersoft.internusa.ui.profil;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.imagePicker.AlbumSelectActivity;
import com.supersoft.internusa.helper.imagePicker.Image;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.BaseRecyclerViewAdapter;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.DrawableClickListener;
import com.supersoft.internusa.helper.util.DrawableClickListener.DrawablePosition;
import com.supersoft.internusa.helper.util.EditTextRightDrawable;
import com.supersoft.internusa.helper.util.SpacesItemDecoration;
import com.supersoft.internusa.helper.util.Util;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.PojoDefault;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.mUsaha;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by itclub21 on 4/22/2017.
 */

public class AddUsahaActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.btnLogin)Button btnLogin;
    @BindView(R.id.txtNamaUsaha)EditText txtNamaUsaha;
    @BindView(R.id.view_selected_photos_container)FrameLayout view_selected_photos_container;
    @BindView(R.id.rc_selected_photos)RecyclerView rc_selected_photos;
    @BindView(R.id.txtAddImage)TextView txtAddImage;
    @BindView(R.id.txtDescription)EditText txtDescription;
    @BindView(R.id.pb_loading_indicator_kategori) ProgressBar pb_loading_indicator_kategori;
    @BindView(R.id.txtKategori) DelayAutoCompleteTextView txtKategori;
    @BindView(R.id.pb_loading_indicator_posisi)ProgressBar pb_loading_indicator_posisi;
    @BindView(R.id.txtPosisi)DelayAutoCompleteTextView txtPosisi;
    @BindView(R.id.txtAddress)EditTextRightDrawable txtAddress;
    @BindView(R.id.txtWesite)EditText txtWesite;


    private AlertDialog progressDialog;
    String CRUD = "";
    String ID = "";

    double gLatitude = 0.0D;
    double gLongitude = 0.0D;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private static final int READ_STORAGE_PERMISSION = 4000;
    private static final int LIMIT = 5;
    thumbnailAdapter grdAdapter;
    ArrayList<Image> image_uris = new ArrayList<>();



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_usaha_activity);
        ButterKnife.bind(this);


        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            CRUD = extras.getString("CRUD");
            if(CRUD.equals("update"))
            {
                ID = extras.getString("ID");
            }
        }

        txtAddImage.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        progressDialog = new SpotsDialog(this, R.style.Custom);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        txtAddress.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if(target == DrawablePosition.RIGHT)
                {
                    permissions.add(ACCESS_COARSE_LOCATION);
                    permissions.add(ACCESS_FINE_LOCATION);
                    permissionsToRequest = findUnAskedPermissions(permissions);
                    if(permissionsToRequest.size() > 0)
                    {
                        ActivityCompat.requestPermissions(AddUsahaActivity.this, (String[])permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    }
                    else
                    {
                        if (mGoogleApiClient.isConnected())
                        {
                            mGoogleApiClient.disconnect();
                        }
                        if(checkLocation())
                        {
                            mGoogleApiClient.connect();
                        }
                    }
                }
            }
        });
        setupToolbar();
        initAutoComplete();


        LinearLayoutManager mLayoutManager_Linear = new LinearLayoutManager(AddUsahaActivity.this);
        mLayoutManager_Linear.setOrientation(LinearLayoutManager.HORIZONTAL);
        rc_selected_photos.setLayoutManager(mLayoutManager_Linear);
        rc_selected_photos.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(AddUsahaActivity.this, 5), SpacesItemDecoration.TYPE_VERTICAL));
        rc_selected_photos.setHasFixedSize(true);
        rc_selected_photos.setAdapter(null);

        if(!ID.equals(""))
        {
            loadUsaha();
        }
    }

    private void setupToolbar() {
        if (toolbar != null) {
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

    public void onStart()
    {
        super.onStart();
    }

    private ArrayList<File> compressImage()
    {
        ArrayList<File> tmpFile = new ArrayList<>();
        for(int i=0; i < image_uris.size(); i++) {
            Image img = image_uris.get(i);
            //Log.e("compress", "Name:" + img.name + " Path: " + img.path + " id " + img.id + " isselected " + img.isSelected);
            if(!img.name.equals("update")) {
                File fl = Constant.CompressImage80(this, new File(img.path));
                tmpFile.add(fl);
            }
        }
        return tmpFile;
    }

    private void TambahUsahaWithImage()
    {
        progressDialog.show();
        final ProfilDB profil = new DBHelper(AddUsahaActivity.this).getProfilDb();
        ArrayList<File> compressFile = compressImage();

        MultipartBody.Part[] ImagesParts = new MultipartBody.Part[compressFile.size()];
        for(int i=0; i < compressFile.size(); i++) {
            File img = compressFile.get(i);
            final String filename = new File(img.getPath()).getName();
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), img);
            ImagesParts[i] = MultipartBody.Part.createFormData("uploaded_file[]", filename, requestFile);
        }


        RequestBody nama_usaha = RequestBody.create(MediaType.parse("multipart/form-data"), txtNamaUsaha.getText().toString());
        RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"), txtAddress.getText().toString());
        RequestBody bidang = RequestBody.create(MediaType.parse("multipart/form-data"), txtKategori.getText().toString());
        RequestBody deskripsi = RequestBody.create(MediaType.parse("multipart/form-data"), txtDescription.getText().toString());
        RequestBody website = RequestBody.create(MediaType.parse("multipart/form-data"), txtWesite.getText().toString());
        RequestBody posisi = RequestBody.create(MediaType.parse("multipart/form-data"), txtPosisi.getText().toString());

        RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(gLatitude));
        RequestBody lon = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(gLongitude));
        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), ID);

        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getUsahaku/create/{query}"
        Call<PojoDefault> call = githubUserAPI.tambahUsahaWithImage(String.format(Constant.CONTROLLER_4S,Constant.CONTROLLER_DEV, "getUsahakku", "create", profil.hp),ImagesParts , nama_usaha, alamat, bidang, deskripsi, website, posisi, lat, lon, type);
        call.enqueue(new Callback<PojoDefault>() {
            @Override
            public void onResponse(Call<PojoDefault> call, Response<PojoDefault> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                //Log.e("RES", new Gson().toJson(response.body()));
                if(!response.isSuccessful()){
                    try{
                        Constant.showInfoMessageDialog(AddUsahaActivity.this,"ERROR: \n" + response.errorBody().string(),"ERROR");
                    }catch (IOException ex){

                    }
                    return;
                }
                PojoDefault respo = response.body();
                if(respo.getStatus() == 1)
                {
                    Intent intent = getIntent();
                    intent.putExtra("SOMETHING", "EXTRAS");
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<PojoDefault> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog.isShowing())progressDialog.dismiss();
                        Constant.showInfoMessageDialog(AddUsahaActivity.this, t.getMessage(), "FAILED");
                    }
                });

            }
        });
    }

    private void deleteImageById(final String idx, final thumbnailAdapter grdadap, final Image img)
    {
        progressDialog.show();
        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface retro = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/getUsahaku/{crud}/{query}"
        Call<List<mUsaha>> call = retro.listUsaha(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getUsahaku","delete_image",idx));
        call.enqueue(new Callback<List<mUsaha>>() {
            @Override
            public void onResponse(Call<List<mUsaha>> call, Response<List<mUsaha>> response) {
                //Log.e("response", new Gson().toJson(response.body()));
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful())
                {
                    return;
                }

                List<mUsaha> pojos = response.body();
                if(pojos.size() > 0)
                {
                    image_uris.remove(img);
                    grdAdapter.updateItems(image_uris);
                    grdadap.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<List<mUsaha>> call, Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                Constant.showInfoMessageDialog(AddUsahaActivity.this,t.getMessage(), "Error!");
            }
        });
    }

    private void loadUsaha()
    {
        progressDialog.show();

        ProfilDB pf = new DBHelper(this).getProfilDb();
        RetrofitBuilder builder = new RetrofitBuilder("getUsaa");
        RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
        final Call<List<mUsaha>> repos = service.listUsaha(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "getUsahaku","update",ID));
        repos.enqueue(new Callback<List<mUsaha>>() {
            @Override
            public void onResponse(Call<List<mUsaha>> call, Response<List<mUsaha>> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                //Log.e("res", new Gson().toJson(response.body()));
                if(!response.isSuccessful())
                {
                    return;
                }

                ArrayList<mUsaha> profesiList = new ArrayList<mUsaha>(response.body());
                if(profesiList.size() > 0)
                {
                    mUsaha p = profesiList.get(0);
                    final ArrayList<Image> gallery = new ArrayList<Image>(p.getGallery());
                    if(gallery.size() > 0)
                    {
                        for(int i=0; i < gallery.size(); i++)
                        {
                            Image gall = gallery.get(i);
                            Image img = new Image(gall.id, "update", gall.path, false);
                            image_uris.add(img);
                        }
                        view_selected_photos_container.setVisibility(View.VISIBLE);
                        grdAdapter = new thumbnailAdapter(AddUsahaActivity.this, image_uris, new AdapterCallback() {
                            @Override
                            public void removeImage(View view) {
                                Image img = (Image)view.getTag();

                                if (image_uris.size() == 0) {
                                    view_selected_photos_container.setVisibility(View.GONE);
                                }
                                deleteImageById(String.valueOf(img.id), grdAdapter, img);
                            }
                        });
                        grdAdapter.updateItems(image_uris);
                        grdAdapter.notifyDataSetChanged();
                        rc_selected_photos.setAdapter(grdAdapter);
                    }
                    txtNamaUsaha.setText(p.getPnfNama());
                    txtAddress.setText(p.getAlamatDetail());
                    txtKategori.setText(p.getBidang());
                    txtDescription.setText(p.getDeskripsi());
                    txtPosisi.setText(p.getPosisi());
                }

            }

            @Override
            public void onFailure(Call<List<mUsaha>> call, Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
            }
        });
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission((String)perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void onDestroy()
    {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            image_uris = data.getParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES);

            if(image_uris.size() > 0){
                view_selected_photos_container.setVisibility(View.VISIBLE);
                grdAdapter = new thumbnailAdapter(AddUsahaActivity.this, image_uris, new AdapterCallback() {
                    @Override
                    public void removeImage(View view) {
                        Image img = (Image)view.getTag();
                        image_uris.remove(img);
                        grdAdapter.updateItems(image_uris);
                        if (image_uris.size() == 0) {
                            view_selected_photos_container.setVisibility(View.GONE);
                        }
                        grdAdapter.notifyDataSetChanged();
                    }
                });
                grdAdapter.updateItems(image_uris);
                grdAdapter.notifyDataSetChanged();
                rc_selected_photos.setAdapter(grdAdapter);
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms.toString())) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String)permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                ActivityCompat.requestPermissions(AddUsahaActivity.this, (String[])permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddUsahaActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(AddUsahaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddUsahaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        txtAddress.setText("Connected");
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(AddUsahaActivity.this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        mGoogleApiClient.connect();
        txtAddress.setText("Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.e(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        txtAddress.setText(connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(AddUsahaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddUsahaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        gLatitude = location.getLatitude();
        gLongitude = location.getLongitude();
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(AddUsahaActivity.this, Locale.getDefault());
        String alamat = "";
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                //alamat = address + " " + city + " " + country;
                alamat = city + " " + state;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        txtAddress.setText(alamat);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddUsahaActivity.this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private void initAutoComplete()
    {
        txtKategori.setThreshold(3);
        txtKategori.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_mbidang")); // 'this' is Activity instance
        txtKategori.setLoadingIndicator(pb_loading_indicator_kategori);
        txtKategori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtKategori.setText(book.getKkNama());
            }
        });


        txtPosisi.setThreshold(3);
        txtPosisi.setAdapter(new AddressAutoCompleteAdapter(getApplicationContext(),"sync_mposisi_nonformal")); // 'this' is Activity instance
        txtPosisi.setLoadingIndicator(pb_loading_indicator_posisi);
        txtPosisi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtPosisi.setText(book.getKkNama());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.txtAddImage:
                readImageFromExternal();
                break;
            case R.id.btnLogin: //save
                TambahUsahaWithImage();
                break;
        }
    }


    public void readImageFromExternal()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Constant.checkPermissionForExternalStorage(AddUsahaActivity.this)) {
                Constant.requestStoragePermission(AddUsahaActivity.this, READ_STORAGE_PERMISSION);
            } else {
                Intent intent = new Intent(AddUsahaActivity.this, AlbumSelectActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
                startActivityForResult(intent, Constant.REQUEST_CODE);
            }
        }else{
            Intent intent = new Intent(AddUsahaActivity.this, AlbumSelectActivity.class);
            intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
            startActivityForResult(intent, Constant.REQUEST_CODE);
        }
    }

    public class AddressAutoCompleteAdapter extends BaseAdapter implements Filterable
    {
        private static final int MAX_RESULTS = 10;
        private Context mContext;
        private List<Cities> resultList = new ArrayList<Cities>();
        private String func;

        public AddressAutoCompleteAdapter(Context context, String fun) {
            mContext = context;
            func = fun;
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
                        List<Cities> books = findBooks(func, constraint.toString());

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
        private List<Cities> findBooks(String fun, String bookTitle) {

            List<Cities> citi = new ArrayList<>();

            RetrofitBuilder builder = new RetrofitBuilder("getCities");
            RetroBuilderInterface service = builder.getRetrofit().create(RetroBuilderInterface.class);
            Call<List<Cities>> repos = service.listCitiesOther(String.format(Constant.CONTROLLER_3S, Constant.CONTROLLER_DEV, fun, bookTitle));
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

    public interface AdapterCallback {
        void removeImage(View view);
    }

    public class thumbnailAdapter extends BaseRecyclerViewAdapter<Image, thumbnailAdapter.SelectedPhotoHolder>
    {
        ArrayList<Image> imgUri;
        Context _context;
        Activity _activity;
        private AdapterCallback mAdapterCallback;



        public thumbnailAdapter(Context context, ArrayList<Image> img){
            super(context);
            this._context = context;
            this._activity = (Activity)_context;
            this.imgUri = img;

        }

        public thumbnailAdapter(Context context, ArrayList<Image> img, AdapterCallback call){
            super(context);
            this._context = context;
            this._activity = (Activity)_context;
            this.imgUri = img;
            this.mAdapterCallback = call;
        }


        public int getItemCount() {
            return this.imgUri.size();
        }

        public Image getItem(int position){
            return this.imgUri.get(position);
        }

        @Override
        public void onBindView(SelectedPhotoHolder holder, int position) {
            Image uri = getItem(position);

            Glide.with(AddUsahaActivity.this)
                    .load(uri.path)
                    .dontAnimate()
                    .centerCrop()
                    .error(R.drawable.imagepicker)
                    .into(holder.selected_photo);

            holder.iv_close.setTag(uri);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            View view = mInflater.inflate(R.layout.picker_list_item_selected_thumbnail, parent, false);
            return new SelectedPhotoHolder(view);
        }


        class SelectedPhotoHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.selected_photo)
            ImageView selected_photo;

            @BindView(R.id.iv_close)
            ImageView iv_close;



            public SelectedPhotoHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Image uri = (Image) view.getTag();
                        mAdapterCallback.removeImage(view);
                        //_activity.removeImage(uri);
                    }
                });

            }
        }
    }
}
