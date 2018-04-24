package com.supersoft.internusa.ui.chat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.cropper.CropImage;
import com.supersoft.internusa.helper.cropper.CropImageView;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by itclub21 on 12/4/2017.
 */

public class ChatTopicActivity extends AppCompatActivity implements TextWatcher {
    private final static int ALL_PERMISSIONS_RESULT = 101;
    @BindView(R.id.txtInfo) EditText txtInfo;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;
    private Uri mCropImageUri;
    Uri picuri;
    Uri lastUriFile;
    File lastFile;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();
    String sHostIcon = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_topik);
        ButterKnife.bind(this);
        initToolbar();

    }


    private void initToolbar()
    {

        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!txtInfo.getText().equals(""))
                    {

                    }
                    finish();
                }
            });
        }
    }

    @OnEditorAction(R.id.txtInfo)
    protected boolean onSendButton(int actionID)
    {
        if(actionID == EditorInfo.IME_ACTION_DONE)
        {
            submit();
            return true;
        }
        return false;
    }

    @OnClick(R.id.imgAvatar)
    protected void addIconTopik()
    {
        readImageFromExternal();
    }


    public void readImageFromExternal()
    {
        if (CropImage.isExplicitCameraPermissionRequired(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            startActivityForResult(CropImage.getPickImageChooserIntent(this), CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }
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
                Glide.with(this).load(lastFile).into(imgAvatar);
                //updateProfilPoto(lastFile);

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
                                            ActivityCompat.requestPermissions(ChatTopicActivity.this, (String[])permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                        }
                                    }
                                });
                        return;
                    }
                }

            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
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

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.save_icon, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           case R.id.action_save:
               if(txtInfo.getText().length() > 10)
                    submit();
               else
                   Toast.makeText(this, "Penulisan topik minimal 10 karakter", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void submit()
    {

        Map<String, String> data  = new HashMap<>();
        data.put("action", "CREATE_TOPIK");
        data.put("data", txtInfo.getText().toString());
        data.put("groupid", "");
        data.put("avatartopik", (lastFile == null) ? "" : lastFile.getAbsolutePath());

        EventBus.getDefault().post(data);
        //setResult(Activity.RESULT_OK, dt);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
