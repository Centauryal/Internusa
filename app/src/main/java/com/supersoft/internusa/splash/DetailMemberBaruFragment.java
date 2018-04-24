package com.supersoft.internusa.splash;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supersoft.internusa.helper.cropper.CropImage;
import com.supersoft.internusa.helper.cropper.CropImageView;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.PojoDefault;
import com.supersoft.internusa.model.Row;
import com.supersoft.internusa.R;

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
 * Created by Centaury on 21/04/2018.
 */
public class DetailMemberBaruFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    @BindView(R.id.txtFullname)
    TextView txtFullname;

    @BindView(R.id.txtPhonenumber)
    TextView txtPhonenumber;
    @BindView(R.id.txtEmail)
    TextView txtEmail;
    @BindView(R.id.login_button)
    Button login_button;
    @BindView(R.id.txtLocation)
    TextView txtLocation;
    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    static ArrayList<Row> pjoDefault;
    static ModelRequestInfo mRequest;
    static Row mRow;
    static String type; // MERGE / NEW


    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;
    private static final String TAG = "MainActivity";
    String alamat = "";
    double gLatitude = 0.0D;
    double gLongitude = 0.0D;

    Uri picuri;
    Uri lastUriFile;
    File lastFile;
    private Uri mCropImageUri;
    AlertDialog progressDialog;

    public static DetailMemberBaruFragment instance(ModelRequestInfo mdl, Row rw, String tp)
    {
        DetailMemberBaruFragment fm = new DetailMemberBaruFragment();
        mRequest = mdl;
        mRow = rw;
        type = tp;
        return fm;
    }

    public static DetailMemberBaruFragment instance()
    {
        DetailMemberBaruFragment fm = new DetailMemberBaruFragment();
        return fm;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.detail_member_baru_fragment, container, false);
        ButterKnife.bind(this, rootView);
        progressDialog = new SpotsDialog(getContext(), R.style.Custom);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        login_button.setOnClickListener(this);
        txtLocation.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);

        txtFullname.setText(mRequest.nama);
        txtPhonenumber.setText(mRequest.hp);
        txtEmail.setText(mRequest.email);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.login_button:
                // type : MERGER / NEW
                registrasi_from_app(mRequest);
                break;
            case R.id.txtLocation:
                permissions.add(ACCESS_COARSE_LOCATION);
                permissions.add(ACCESS_FINE_LOCATION);
                permissionsToRequest = findUnAskedPermissions(permissions);
                if(permissionsToRequest.size() > 0)
                {
                    ActivityCompat.requestPermissions(getActivity(), (String[])permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
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

                break;
            case R.id.imgAvatar:
                // kalo di dalam activity
                //CropImage.startPickImageActivity(getActivity());

                // kalo di dalam fragment
                if (CropImage.isExplicitCameraPermissionRequired(getContext())) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    startActivityForResult(CropImage.getPickImageChooserIntent(getContext()), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
                }
                break;
        }
    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())showAlert();
        return isLocationEnabled();
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
                return (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }



    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_CODE_TAKE_PICTURE && resultCode == getActivity().RESULT_OK) {
            CropImage.activity(picuri).setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
        }else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == getActivity().RESULT_OK){

            Uri source = CropImage.getPickImageResultUri(getActivity(), data);
            //
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), source) && CropImage.isExplicitCameraPermissionRequired(getActivity())) {

                requirePermissions = true;
                mCropImageUri = source;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                CropImage.activity(source).setGuidelines(CropImageView.Guidelines.ON).start(getContext(), this);
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if(resultCode == getActivity().RESULT_OK){
                lastUriFile = CropImage.getActivityResult(data).getUri();
                lastFile = new File(lastUriFile.getPath());
                Glide.with(getContext()).load(lastFile).into(imgAvatar);
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception ex = CropImage.getActivityResult(data).getError();
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity(mCropImageUri).setGuidelines(CropImageView.Guidelines.ON).start(getActivity());
            } else {
                Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }

        if(requestCode == ALL_PERMISSIONS_RESULT)
        {
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
                                            ActivityCompat.requestPermissions(getActivity(), (String[])permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    }
                                });
                        return;
                    }
                }

            }
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        txtLocation.setText("Connected");
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(getActivity(), "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
        txtLocation.setText("Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        txtLocation.setText(connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                alamat = address + " " + city + " " + country;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        txtLocation.setText(alamat);
    }


    public void onStop()
    {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            mGoogleApiClient.disconnect();
        }
        super.onStop();

    }

    //add New Member ke rekankita.com

    private void daftarMember(final ModelRequestInfo model, final String idagen)
    {

        progressDialog.show();
        Session _session = Constant.getSession(getContext());
        model.mitraid = getResources().getString(R.string.CONF_MITRAID);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), lastFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", lastFile.getName(), requestFile);
        RequestBody member_id = RequestBody.create(MediaType.parse("multipart/form-data"), (model.type.equals("NEW")) ? "" : model.member_id);
        RequestBody email = RequestBody.create(MediaType.parse("multipart/form-data"), model.email);
        RequestBody agenid = RequestBody.create(MediaType.parse("multipart/form-data"), idagen);
        RequestBody hp = RequestBody.create(MediaType.parse("multipart/form-data"), model.hp);
        RequestBody nama = RequestBody.create(MediaType.parse("multipart/form-data"), model.nama);
        RequestBody alamat = RequestBody.create(MediaType.parse("multipart/form-data"), txtLocation.getText().toString());
        RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(gLatitude));
        RequestBody lon = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(gLongitude));
        RequestBody gender = RequestBody.create(MediaType.parse("multipart/form-data"), model.gender);
        RequestBody kota = RequestBody.create(MediaType.parse("multipart/form-data"), model.address);
        RequestBody pertanyaan = RequestBody.create(MediaType.parse("multipart/form-data"), model.pertanyaan);
        RequestBody jawaban = RequestBody.create(MediaType.parse("multipart/form-data"), model.jawaban);
        RequestBody reg_gcm = RequestBody.create(MediaType.parse("multipart/form-data"), "");
        RequestBody mitraid = RequestBody.create(MediaType.parse("multipart/form-data"), getContext().getResources().getString(R.string.CONF_MITRAID));
        RequestBody type = RequestBody.create(MediaType.parse("multipart/form-data"), model.type);

        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<PojoDefault> call = githubUserAPI.daftarMember(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "daftarMember"),body , member_id, email, agenid, hp, nama, alamat, lat, lon, gender, kota, pertanyaan, jawaban, reg_gcm, mitraid,  type);
        call.enqueue(new Callback<PojoDefault>() {
            @Override
            public void onResponse(Call<PojoDefault> call, Response<PojoDefault> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful()){
                    try{
                        Constant.showInfoMessageDialog(getContext(),"ERROR: \n" + response.errorBody().string(),"ERROR");
                    }catch (IOException ex){

                    }
                    return;
                }
                PojoDefault respo = response.body();

            }

            @Override
            public void onFailure(Call<PojoDefault> call, final Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog.isShowing())progressDialog.dismiss();
                        Constant.showInfoMessageDialog(getContext(), t.getMessage(), "FAILED");
                    }
                });

            }
        });
    }


    // daftar ke server pulsa

    private void registrasi_from_app(final ModelRequestInfo inf){
        progressDialog.show();
        Session _session = Constant.getSession(getContext());
        final DBHelper db = new DBHelper(getContext());
        inf.lat = String.valueOf(gLatitude);
        inf.lon = String.valueOf(gLongitude);
        RetrofitBuilder builder = new RetrofitBuilder(getResources().getString(R.string.CONF_URI) ,"registrasi_from_app");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);

        Call<JsonObject> call = githubUserAPI.registrasi_from_app(inf);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful()){
                    try{
                        Constant.showInfoMessageDialog(getContext(),"ERROR: \n" + response.errorBody().string(),"ERROR");
                    }catch (IOException ex){

                    }
                    return;
                }
                //Log.e("RESPOsms", new Gson().toJson(response).toString());
                JsonObject tmpInfo = response.body();
                JsonArray jArr = tmpInfo.getAsJsonArray("response");
                if(jArr.size() > 0)
                {
                    int status = jArr.get(0).getAsJsonObject().get("status").getAsInt();
                    String msg = jArr.get(0).getAsJsonObject().get("message").getAsString();
                    if(status == 1)
                    {
                        //res.getJSONObject(0).getJSONArray("data").getJSONObject(0);
                        JsonObject param = jArr.get(0).getAsJsonObject().get("data").getAsJsonArray().get(0).getAsJsonObject();
                        boolean ok = db.addProfil(inf.nama,inf.email,"", inf.hp,"","0","0","");
                        if(ok)
                        {

                            daftarMember(inf, param.get("agenid").getAsString());
                        }
                    }
                    else
                    {
                        Constant.showInfoMessageDialog(getContext(),"Pendaftaran GAGAL.\n"+msg ,"ERROR");
                    }
                }
                else
                    Constant.showInfoMessageDialog(getContext(),"Tidak ada response / mungkin TIME OUT." ,"ERROR");


            }

            @Override
            public void onFailure(Call<JsonObject> call, final Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog.isShowing())progressDialog.dismiss();
                        Constant.showInfoMessageDialog(getContext(), t.getMessage(), "FAILED");
                    }
                });
            }
        });
    }
}
