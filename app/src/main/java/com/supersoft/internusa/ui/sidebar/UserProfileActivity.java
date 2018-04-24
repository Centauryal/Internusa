package com.supersoft.internusa.ui.sidebar;

/**
 * Created by itclub21 on 4/11/2017.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.cropper.CropImage;
import com.supersoft.internusa.helper.cropper.CropImageView;
import com.supersoft.internusa.helper.exception.UnCaughtException;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.DateDialogFragment;
import com.supersoft.internusa.helper.util.DateDialogFragmentListener;
import com.supersoft.internusa.helper.util.DrawableClickListener;
import com.supersoft.internusa.helper.util.EditTextRightDrawable;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.Cities;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.view.DelayAutoCompleteTextView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, DateDialogFragmentListener {


    @BindView(R.id.anim_toolbar)
    Toolbar toolbar;

    @BindView(R.id.header)
    ImageView header;

    @BindView(R.id.txtFullname)
    EditText txtFullname;

    @BindView(R.id.txtAddress)
    DelayAutoCompleteTextView txtAddress;

    @BindView(R.id.pb_loading_indicator)  ProgressBar pb_loading_indicator;

    @BindView(R.id.txtTanggal)
    EditTextRightDrawable txtTanggal;

    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    @BindView(R.id.male_radio_button)
    RadioButton male_radio_button;

    @BindView(R.id.female_radio_button)
    RadioButton female_radio_button;

    @BindView(R.id.pgLoading)
    ProgressBar pgLoading;

    DBHelper _db;
    ProfilDB pf;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();


    private final static int ALL_PERMISSIONS_RESULT = 101;
    Uri picuri;
    Uri lastUriFile;
    File lastFile;
    private Uri mCropImageUri;
    private AlertDialog progressDialog;
    Calendar now;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(UserProfileActivity.this));
        setContentView(R.layout.profil_layout);
        _db = new DBHelper(this);
        pf = _db.getProfilDb();
        now = Calendar.getInstance();
        progressDialog = new SpotsDialog(this, R.style.Custom);
        injectViews();

        Constant.loadBgImage(this, pf.avatar, header);
        pgLoading.setVisibility(View.GONE);
        setupToolbar();

        new Thread(new Runnable() {
            @Override
            public void run() {
                get_profil();
            }
        }).start();

        txtTanggal.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if(target.equals(DrawablePosition.RIGHT))
                {
                    selectDate();
                }
            }
        });

        txtTanggal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus)
                {
                    disableSoftInputFromAppearing(txtTanggal);
                    selectDate();
                }
            }
        });


        txtAddress.setThreshold(3);
        txtAddress.setAdapter(new AddressAutoCompleteAdapter(this)); // 'this' is Activity instance
        txtAddress.setLoadingIndicator(pb_loading_indicator);
        txtAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Cities book = (Cities) adapterView.getItemAtPosition(position);
                txtAddress.setText(book.getKkNama());
            }
        });
    }

    protected void injectViews() {
        ButterKnife.bind(this);
    }

    private void setupToolbar() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle((pf.fullname == null) ? "KOSONG" : pf.fullname);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void get_profil()
    {
        ModelRequestInfo info = new ModelRequestInfo();
        info.hp = pf.hp;

        pgBar.setVisibility(View.VISIBLE);
        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = githubUserAPI.postDefault(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "get_profil"),info);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                pgBar.setVisibility(View.GONE);
                Constant.debug("res", new Gson().toJson(response.body()));
                if(response.isSuccessful())
                {
                    JsonArray jArray = response.body().getAsJsonArray("rows");
                    if(jArray.size() > 0)
                    {
                        for(int i=0; i < jArray.size(); i++)
                        {
                            JsonObject obj = jArray.get(i).getAsJsonObject();
                            String mem_nama = (obj.get("mem_nama").isJsonNull()) ? "" : obj.get("mem_nama").getAsString();
                            String mem_alamat = (obj.get("mem_alamat").isJsonNull()) ? "" : obj.get("mem_alamat").getAsString();
                            String mem_tgllhr = (obj.get("mem_tgllhr").isJsonNull()) ? "" : obj.get("mem_tgllhr").getAsString();
                            String mem_jeniskel = (obj.get("mem_jeniskel").isJsonNull()) ? "L" : obj.get("mem_jeniskel").getAsString();
                            txtFullname.setText(mem_nama);
                            txtAddress.setText(mem_alamat);
                            txtTanggal.setText(mem_tgllhr);
                            if(mem_jeniskel.equals("L"))
                                male_radio_button.setChecked(true);
                            else
                                female_radio_button.setChecked(true);
                            //Log.e("ENDE", mem_jeniskel);
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
            }
        });
    }


    @OnClick(R.id.btnSubmit)
    public void onSubmit()
    {
        updateProfil();
    }


    private void updateProfil()
    {
        progressDialog.setCancelable(false);
        progressDialog.show();
        Session _session = Constant.getSession(this);
        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/update_profil"
        String url = String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "update_profil");
        Call<JsonObject> call = githubUserAPI.updateProfil(url, pf.hp, txtFullname.getText().toString(), txtAddress.getText().toString(), txtTanggal.getText().toString(), (male_radio_button.isChecked()) ? "L" : "P");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful()){
                    Constant.showInfoMessageDialog(UserProfileActivity.this,"ERROR: \n" + response.errorBody().toString(),"ERROR");
                    return;
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, final Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressDialog.isShowing())progressDialog.dismiss();
                        Constant.showInfoMessageDialog(UserProfileActivity.this, t.getMessage(), "FAILED");
                    }
                });

            }
        });
    }


    private void updateProfilPoto(final File file)
    {
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        RequestBody hp = RequestBody.create(MediaType.parse("multipart/form-data"), pf.hp);

        RetrofitBuilder builder = new RetrofitBuilder("splash");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = githubUserAPI.updateProfileFoto(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "update_profil_poto"), hp, body);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Log.e("response", new Gson().toJson(response.body()));
                if(progressDialog.isShowing())progressDialog.dismiss();
                if(!response.isSuccessful()){
                    Constant.showInfoMessageDialog(UserProfileActivity.this,"ERROR: \n" + response.errorBody().toString(),"ERROR");
                    return;
                }

                int status = response.body().get("status").getAsInt();
                String avatar = response.body().get("message").getAsString();
                if(status == 1) {
                    Glide.with(UserProfileActivity.this).load(lastFile).into(header);
                    _db.updateAvatar(avatar);
                    EventBus.getDefault().post("update_poto_profil");
                }else
                    Constant.showInfoMessageDialog(UserProfileActivity.this,"ERROR: \n" + avatar,"ERROR");
            }

            @Override
            public void onFailure(Call<JsonObject> call, final Throwable t) {
                if(progressDialog.isShowing())progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Constant.showInfoMessageDialog(UserProfileActivity.this, t.getMessage(), "FAILED");
                    }
                });

            }
        });
    }


    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= 11) {
            editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
            editText.setTextIsSelectable(true);
        } else {
            editText.setRawInputType(InputType.TYPE_NULL);
            editText.setFocusable(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_image_toolbar, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_image: {
                if (CropImage.isExplicitCameraPermissionRequired(this)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
                } else {
                    startActivityForResult(CropImage.getPickImageChooserIntent(this), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
                }
                break;
            }
            // case blocks for other MenuItems (if any)
        }
        return false;
    }

    @Override
    public void onClick(View view) {

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



    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constant.REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
            CropImage.activity(picuri).setGuidelines(CropImageView.Guidelines.ON).start(this);
        }else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK){

            Uri source = CropImage.getPickImageResultUri(this, data);
            //
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(this, source) && CropImage.isExplicitCameraPermissionRequired(this)) {

                requirePermissions = true;
                mCropImageUri = source;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                CropImage.activity(source).setGuidelines(CropImageView.Guidelines.ON).start(this);
            }
        }else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            if(resultCode == RESULT_OK){
                lastUriFile = CropImage.getActivityResult(data).getUri();
                lastFile = new File(lastUriFile.getPath());

                updateProfilPoto(lastFile);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception ex = CropImage.getActivityResult(data).getError();
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.activity(mCropImageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
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
                                            ActivityCompat.requestPermissions(UserProfileActivity.this, (String[])permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    }
                                });
                        return;
                    }
                }

            }
        }
    }

    public void selectDate() {
        FragmentTransaction ft = getFragmentManager().beginTransaction(); //get the fragment
        DateDialogFragment frag = DateDialogFragment.newInstance(this, this, now);
        frag.show(ft, "DateDialogFragment");
    }

    @Override
    public void updateChangedDate(int year, int month, int day) {
        txtTanggal.setText(String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(day));
        now.set(year, month, day);
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
