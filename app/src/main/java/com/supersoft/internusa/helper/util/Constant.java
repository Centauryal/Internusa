package com.supersoft.internusa.helper.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.compresor.Compressor;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.view.BounceInterpolator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Centaury on 19/04/2018.
 */
public class Constant {
    public static String RELEASE_TYPE = "dev"; //dev,release
    private static boolean DEBUG = true;
    public static String XMPP_HOST = "rekankita.com";
    public static int XMPP_PORT = 5222;
    public static int XMPP_PORT_SSL = 5223;

    public static String NOTIF_KODE_HTML_CONTENT = "95";
    public static String NOTIF_KODE_BIG_IMAGE = "99";
    public static String NOTIF_KODE_CREATE_TOPIK = "19";
    public static String NOTIF_KODE_KOMENTAR = "10";
    public static String NOTIF_KODE_LIKE = "11";

    public static final String DATE_SEPARATOR_BODY = "DATE_SEPARATOR";


    public static String BASE_URL = "http://rekankita.com/"; //195.110.58.164
    public static String SELECTION_PHOTO = "Ambil Photo";
    public static String SELECTION_GALLERY = "Pilih Gallery";
    public static String APP_PATH_SD_CARD = "/Internusa";
    //public static final String CONTROLLER_REKANKITA = "android/v2/index.php/{controller}/{function}";
    public static final String CONTROLLER_DEV = (RELEASE_TYPE.equals("dev")) ? "devcontroller" : "microz";
    public static final String CONTROLLER_2S = "android/v2/index.php/%s/%s";
    public static final String CONTROLLER_3S = "android/v2/index.php/%s/%s/%s";
    public static final String CONTROLLER_4S = "android/v2/index.php/%s/%s/%s/%s";
    //public static final String C_GET_INFO = "android/v2/index.php/%s/get_info/%s/%s";

    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;

    public static final int EVENT_SPLASH = 0;
    public static final int EVENT_AKTIFASI_DAFTAR_ONLINE = 1;
    public static final int EVENT_LOGIN = 2;
    public static final int EVENT_AKTIFASI = 3;
    public static final int EVENT_OTP = 4;
    public static final int EVENT_FORM_REGISTRASI = 5;
    public static final int EVENT_MERGE_MEMBER = 6;
    public static final int EVENT_DETAIL_PROFIL = 7;

    public static final int EVENT_NUM_PAGES = 6;


    public static final String savedInstancePayment = "COM.SAVE.INSTANCE.PAYMENT";
    public static final String savedInstanceProfil = "COM.SAVE.INSTANCE.PROFIL";
    public static final String savedInstanceInfo = "COM.SAVE.INSTANCE.INFO";

    public static final String FULL_IMAGE_SLIDING_INFO = "SLIDING_IMAGE_INFO";
    public static final String FULL_IMAGE_NOTIF_BIG_STYLE = "NOTIF_BIG_STYLE";
    public static final String FULL_IMAGE_ACTIVITY = "NOTIF_BIG_ACTIVITY";
    public static final String FULL_IMAGE_TITLE = "TITLE";

    public static final String ACTIVITY_EXPORT_CONTACT = "EXPORT_CONTACT";
    public static final String ACTIVITY_IMPORT_CONTACT = "IMPORT_CONTACT";
    public static final String ACTIVITY_PRINT_HISTORY = "PRINT_HISTORY";
    public static final String ACTIVITY_PRABAYAR = "PRA_BAYAR";
    public static final String ACTIVITY_PASCA_BAYAR = "PASCA_BAYAR";
    public static final String ACTIVITY_DEPOSIT = "DEPOSIT";



    // untuk gallery picker

    public static final int PERMISSION_REQUEST_CODE = 1000;
    public static final int PERMISSION_GRANTED = 1001;
    public static final int PERMISSION_DENIED = 1002;

    public static final int REQUEST_CODE = 2000;

    public static final int FETCH_STARTED = 2001;
    public static final int FETCH_COMPLETED = 2002;
    public static final int ERROR = 2005;

    /**
     * Request code for permission has to be < (1 << 8)
     * Otherwise throws java.lang.IllegalArgumentException: Can only use lower 8 bits for requestCode
     */
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 23;

    public static final String INTENT_EXTRA_ALBUM = "album";
    public static final String INTENT_EXTRA_IMAGES = "images";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final int DEFAULT_LIMIT = 10;


    // untuk xmpp service
    public static final String PACKAGE = "com.supersoft.internusa.services.xmpp";
    public static final String ACTION_REQUEST_UPDATE_MAXS_STATUS = "com.supersoft.internusa.REQUEST_UPDATE_MAXS_STATUS";
    public static final String ACTION_START_SERVICE = "com.supersoft.internusa.START_SERVICE";
    public static final String ACTION_STOP_SERVICE = "com.supersoft.internusa.STOP_SERVICE";
    public static final String ACTION_SET_STATUS = "com.supersoft.internusa.SET_STATUS";
    public static final String MAIN_TRANSPORT_SERVICE = "com.supersoft.internusa.services.xmpp.MAXSTransportIntentService";
    public static final String ACTION_UPDATE_TRANSPORT_STATUS = "com.supersoft.internusa.services.UPDATE_TRANSPORT_STATUS";
    public static final String EXTRA_CONTENT = "com.supersoft.internusa.CONTENT";
    public static final String ACTION_SEND_AS_MESSAGE = PACKAGE + ".SEND_AS_MESSAGE";
    public static final String ACTION_SEND_AS_IQ = PACKAGE + ".SEND_AS_IQ";
    public static final String ACTION_NETWORK_TYPE_CHANGED = PACKAGE + ".NETWORK_TYPE_CHANGED";
    public static final String ACTION_NETWORK_CONNECTED = PACKAGE + ".NETWORK_CONNECTED";
    public static final String ACTION_NETWORK_DISCONNECTED = PACKAGE + ".NETWORK_DISCONNECTED";
    public static final String ACTION_CONNECTION_CLOSED_ON_ERROR = PACKAGE + ".CONNECTION_CLOSED_ON_ERROR";

    public static final String XMPP_CONNECTED = PACKAGE + ".XMPP_CONNECTED";
    public static final String XMPP_NOT_CONNECT = PACKAGE + ".XMPP_NOT_CONNECT";


    //Maximum number of images that can be selected at a time
    public static int limit;

    public static int getColorForName(String name) {
        if (name == null || name.isEmpty()) {
            return 0xFF202020;
        }
        int colors[] = {0xFFe91e63, 0xFF9c27b0, 0xFF673ab7, 0xFF3f51b5,
                0xFF5677fc, 0xFF03a9f4, 0xFF00bcd4, 0xFF009688, 0xFFff5722,
                0xFF795548, 0xFF607d8b};
        return colors[(int) ((name.hashCode() & 0xffffffffl) % colors.length)];
    }

    public static Boolean Is_Valid_Sign_Number_Validation(int MinLen, int MaxLen,EditText edt) throws NumberFormatException {
        boolean valid_sign_number = false;
        if (edt.getText().toString().length() <= 0) {
            edt.setError("Field must be fill");
        } else if (Double.valueOf(edt.getText().toString()) < MinLen
                || Double.valueOf(edt.getText().length()) > MaxLen) {
            edt.setError("Out of Range " + MinLen + " or " + MaxLen);
        } else {
            valid_sign_number = true;

        }
        return valid_sign_number;
    }

    public static Boolean Is_valid_all(EditText edt) throws NumberFormatException{
        boolean valid_name = false;
        if (edt.getText().toString().length() <= 0) {
            edt.setError("Field must be fill");
        } else {
            valid_name = true;
        }
        return valid_name;
    }

    public static Boolean Is_Valid_Email(EditText edt) {
        boolean valid_email = false;
        if (edt.getText().toString() == null) {
            edt.setError("Invalid Email Address");
        } else if (isEmailValid(edt.getText().toString()) == false) {
            edt.setError("Invalid Email Address");
        } else {
            valid_email = true;
        }
        return valid_email;
    }

    static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Boolean Is_Confirm_Password(EditText pass, EditText confirm){
        boolean valid = false;
        if(pass.getText().toString().length() < 4){
            pass.setError("Password minimal 4 char");
        }else if(!confirm.getText().toString().toUpperCase().equals(pass.getText().toString().toUpperCase())){
            confirm.setError("Password tidak cocok");
        }else{
            valid = true;
        }
        return valid;
    }


    public static void showInfoMessageDialog(Context context, String message, String title)
    {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    public static void showMessageDialogColoseActivity(final Context context, String message, String title)
    {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which)
                    {
                        ((Activity)context).finish();
                    }
                })
                .create().show();
    }

    public static String getCurrentTimeStamp(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getCurrentTimeStampTopikID(){
        return new SimpleDateFormat("yyMMddHmmssSSS").format(new Date());
    }

    public static Uri SaveTakePicture(){
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
        File saveFile = null;
        String currentDateandTime;
        try{
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            currentDateandTime = sdf.format(new Date()).replace(" ","");

            saveFile = new File(fullPath + "/B3_"+ currentDateandTime+".jpg");
            if (!saveFile.exists()) {
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                saveFile.delete();
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }catch (Exception e){

        }
        return Uri.fromFile(saveFile);
    }


    public static Uri DesctinationCroppedImage(){
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;
        File saveFile = null;
        String currentDateandTime;
        try{
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            currentDateandTime = sdf.format(new Date()).replace(" ","");

            saveFile = new File(fullPath + "/B3_CROPPED_"+ currentDateandTime+".jpg");
            if (!saveFile.exists()) {
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                saveFile.delete();
                try {
                    saveFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }catch (Exception e){

        }
        return Uri.fromFile(saveFile);
    }


    public static String getUUID(Context context)
    {
        try
        {
            Session session = new Session(context);
            return session.getDeviceId();
        }catch (NullPointerException e){}
        return "";
    }

    private static char[] hextable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
        }
        return s;
    }

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        m.update(s.getBytes(), 0, s.length());
        return byteArrayToHex(m.digest());
    }

    public static String md5(String s) {
        return digest(s, "MD5");
    }

    public static void showActivity(Activity act, Class cls, boolean finish)
    {
        Intent inten = new Intent(act, cls);
        act.startActivity(inten);
        if(finish) act.finish();
    }


    public static void goToNextFragement(final Activity ctx, final Fragment fragment, final int container, final boolean addToBackStack, final boolean animate, final boolean allowBackstage)
    {
        ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction transaction = ctx.getFragmentManager().beginTransaction();
                if(animate) transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
                transaction.replace(container, fragment);
                if(addToBackStack) transaction.addToBackStack(null);
                if(!allowBackstage) transaction.disallowAddToBackStack();

                // Commit the transaction
                transaction.commit();
            }
        });

    }

    public static void goToPrevFragement(final Activity ctx, final Fragment fragment, final int container, final boolean addToBackStack, final boolean animate, final boolean allowBackstage)
    {
        FragmentTransaction transaction = ctx.getFragmentManager().beginTransaction();
        if(animate) transaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
        transaction.replace(container, fragment);
        if(addToBackStack) transaction.addToBackStack(null);
        if(!allowBackstage) transaction.disallowAddToBackStack();

        // Commit the transaction
        transaction.commit();

    }


    public static void glideDefault(Activity act, String url, ColorDrawable placeholder, ImageView img)
    {
        Glide
                .with(act)
                .load(url)
                .centerCrop()
                .placeholder(placeholder)
                .dontAnimate()
                .into(img);
    }

    public static SpannableStringBuilder makeSectionOfTextBold(String text, String textToBold){

        SpannableStringBuilder builder=new SpannableStringBuilder();

        if(textToBold.length() > 0 && !textToBold.trim().equals("")){

            //for counting start/end indexes
            String testText = text.toLowerCase(Locale.US);
            String testTextToBold = textToBold.toLowerCase(Locale.US);
            int startingIndex = testText.indexOf(testTextToBold);
            int endingIndex = startingIndex + testTextToBold.length();
            //for counting start/end indexes

            if(startingIndex < 0 || endingIndex <0){
                return builder.append(text);
            }
            else if(startingIndex >= 0 && endingIndex >=0){

                builder.append(text);
                builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        }else{
            return builder.append(text);
        }

        return builder;
    }

    public static void wlog(String tag, String m)
    {
        Log.e(tag, m);
    }



    private static ColorDrawable[] vibrantLightColorList =
            {
                    new ColorDrawable(Color.parseColor("#9ACCCD")), new ColorDrawable(Color.parseColor("#8FD8A0")),
                    new ColorDrawable(Color.parseColor("#CBD890")), new ColorDrawable(Color.parseColor("#DACC8F")),
                    new ColorDrawable(Color.parseColor("#D9A790")), new ColorDrawable(Color.parseColor("#D18FD9")),
                    new ColorDrawable(Color.parseColor("#FF6772")), new ColorDrawable(Color.parseColor("#DDFB5C"))
            };

    public static ColorDrawable getRandomDrawbleColor() {
        int idx = new Random().nextInt(vibrantLightColorList.length);
        return vibrantLightColorList[idx];
    }


    public static String currDate()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String curDate = sdf.format(c.getTime());
        return curDate;
    }

    public static Date convertStringtoDate(String sdate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        try {
            date = formatter.parse(sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String parseDate(Date date)
    {
        DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "";
        strDate = dateFormat1.format(date);
        return strDate;
    }

    public static String parseTime(Date date)
    {
        //HH:mm a : 22:20 PM
        DateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        String strDate = "";
        strDate = dateFormat1.format(date);
        return strDate;
    }

    public static String parseDate(String timeAtMiliseconds) {
        if (timeAtMiliseconds.equalsIgnoreCase("")) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String result = "baru saja";
        String dataSot = formatter.format(new Date());
        String agoformater = "";
        long dayagolong = 0;
        try
        {

            Calendar calendar = Calendar.getInstance();
            ParsePosition pos = new ParsePosition(0);
            dayagolong = formatter.parse(timeAtMiliseconds, pos).getTime();
            //long dayagolong = Long.valueOf(timeAtMiliseconds) * 1000;
            calendar.setTimeInMillis(dayagolong);
            agoformater = formatter.format(calendar.getTime());
        }catch (NullPointerException e)
        {
            return "";
        }
        //API.log("Day Ago "+dayago);


        Date CurrentDate = null;
        Date CreateDate = null;

        try {
            CurrentDate = formatter.parse(dataSot);
            CreateDate = formatter.parse(agoformater);

            long different = Math.abs(CurrentDate.getTime() - CreateDate.getTime());

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            different = different % secondsInMilli;
            if (elapsedDays == 0) {
                if (elapsedHours == 0) {
                    if (elapsedMinutes == 0) {
                        if (elapsedSeconds < 0) {
                            return "0" + " detik";
                        } else {
                            if (elapsedDays > 0 && elapsedSeconds < 59) {
                                return "baru saja";
                            }
                        }
                    } else {
                        return String.valueOf(elapsedMinutes) + " mnt yg lalu";
                    }
                } else {
                    return String.valueOf(elapsedHours) + " jam yg lalu";
                }

            } else {
                if (elapsedDays <= 29) {
                    return String.valueOf(elapsedDays) + " hari yg lalu";
                }
                if (elapsedDays > 29 && elapsedDays <= 58) {
                    return "1 bln yg lalu";
                }
                if (elapsedDays > 58 && elapsedDays <= 87) {
                    return "2 bln yg lalu";
                }
                if (elapsedDays > 87 && elapsedDays <= 116) {
                    return "3 bln yg lalu";
                }
                if (elapsedDays > 116 && elapsedDays <= 145) {
                    return "4 bln yg lalu";
                }
                if (elapsedDays > 145 && elapsedDays <= 174) {
                    return "5 bln yg lalu";
                }
                if (elapsedDays > 174 && elapsedDays <= 203) {
                    return "6 bln yg lalu";
                }
                if (elapsedDays > 203 && elapsedDays <= 232) {
                    return "7 bln yg lalu";
                }
                if (elapsedDays > 232 && elapsedDays <= 261) {
                    return "8 bln yg lalu";
                }
                if (elapsedDays > 261 && elapsedDays <= 290) {
                    return "9 bln yg lalu";
                }
                if (elapsedDays > 290 && elapsedDays <= 319) {
                    return "10 bln yg lalu";
                }
                if (elapsedDays > 319 && elapsedDays <= 348) {
                    return "11 bln yg lalu";
                }
                if (elapsedDays > 348 && elapsedDays <= 360) {
                    return "12 bln yg lalu";
                }

                if (elapsedDays > 360 && elapsedDays <= 720) {
                    return "1 thn yg lalu";
                }

                if (elapsedDays > 720) {
                    SimpleDateFormat formatterYear = new SimpleDateFormat("MM/dd/yyyy");
                    Calendar calendarYear = Calendar.getInstance();
                    calendarYear.setTimeInMillis(dayagolong);
                    return formatterYear.format(calendarYear.getTime()) + "";
                }

            }

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return result;
    }



    public static Session getSession(Context _context)
    {
        return new Session(_context);
    }

    public static Drawable getDrawable(Context _context, int id)
    {
        final int version = Build.VERSION.SDK_INT;
        if(version >= 21)
        {
            return ContextCompat.getDrawable(_context, id);
        }
        else
        {
            return _context.getResources().getDrawable(id);
        }
    }


    public static File CompressImage80(Context context, File file){
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD;

        File compressedImage = new Compressor.Builder(context)
                .setQuality(85)
                .setDestinationDirectoryPath(fullPath)
                .build()
                .compressToFile(file);
        return compressedImage;
    }

    public static String Version( Context _context)
    {
        try {
            PackageInfo pInfo = _context.getPackageManager().getPackageInfo(_context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0.0.0";
        }
    }

    public static boolean hasPermissionInManifest(@NonNull Context context, @NonNull String permissionName) {
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equalsIgnoreCase(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }


    public static String replaceNonDigit(final String dig)
    {
        if(dig == null || dig.length() == 0)
        {
            return  "";
        }
        return dig.replaceAll("[^0-9]+","");
    }


    public static boolean isConnectingToInternet(Context _context){

        ConnectivityManager connectivity =
                (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    public static void loadDefaultAvatar(Context ctx, String uri, CircleImageView img)
    {
        Glide.with(ctx).load(uri).dontAnimate().placeholder(R.drawable.user_profil_grey).into(img);
    }

    public static void loadDefaultAvatar(Context ctx, File uri, CircleImageView img)
    {
        Glide.with(ctx).load(uri).dontAnimate().placeholder(R.drawable.user_profil_grey).into(img);
    }

    public static void loadDefaulSlideImage(Context ctx, String uri, final ImageView img)
    {

        AnimationDrawable animationDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            animationDrawable=(AnimationDrawable)ctx.getDrawable(R.drawable.anim_image_placeholder);
        else
            animationDrawable=(AnimationDrawable)ctx.getResources().getDrawable(R.drawable.anim_image_placeholder);
        animationDrawable.start();

        Glide.with(ctx)
                .load(uri)
                .dontAnimate()
                .placeholder(animationDrawable)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                    {
                        return false;
                    }

                    //This is invoked when your image is downloaded and is ready
                    //to be loaded to the image view
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        img.setImageResource(0);
                        return false;
                    }
                })
                .into(img);
    }


    public static void loadDefaulSlideImage(Context ctx, String uri, final ImageView img, final ProgressBar pgBar)
    {

        AnimationDrawable animationDrawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            animationDrawable=(AnimationDrawable)ctx.getDrawable(R.drawable.anim_image_placeholder);
        else
            animationDrawable=(AnimationDrawable)ctx.getResources().getDrawable(R.drawable.anim_image_placeholder);
        animationDrawable.start();
        pgBar.setVisibility(View.VISIBLE);
        Glide.with(ctx)
                .load(uri)
                .dontAnimate()
                //.placeholder(animationDrawable)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                    {
                        pgBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        pgBar.setVisibility(View.GONE);
                        img.setImageResource(0);
                        return false;
                    }
                })
                .into(img);
    }


    public static void loadDefaultSlideImage(Context ctx, String uri, final ImageView img, final ProgressBar pgBar)
    {
        pgBar.setVisibility(View.VISIBLE);
        Glide.with(ctx)
                .load(uri)
                .dontAnimate()
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
                    {
                        pgBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
                    {
                        pgBar.setVisibility(View.GONE);
                        GlideDrawableImageViewTarget glideTarget = (GlideDrawableImageViewTarget) target;
                        ImageView iv = glideTarget.getView();
                        int width = iv.getMeasuredWidth();
                        int targetHeight = width * resource.getIntrinsicHeight() / resource.getIntrinsicWidth();
                        if(iv.getLayoutParams().height != targetHeight) {
                            iv.getLayoutParams().height = targetHeight;
                            iv.requestLayout();
                        }

                        Log.e("targetH", "" + targetHeight + " layouH : " + iv.getLayoutParams().height);
                        return false;
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(img);
    }

    public static void loadDefaultProfilSidebar(Context ctx, String uri, ImageView img)
    {
        if(ctx != null) {
            Glide.with(ctx).load(uri).dontAnimate()
                    .placeholder(R.drawable.add_user_profil)
                    .error(R.drawable.user_profil_grey) // kalo image error gak bisa di load
                    .into(img);
        }
    }

    public static void loadBgImage(final Context ctx, String uri, ImageView img)
    {
        Glide.clear(img);
        Glide.with(ctx)
                .load(uri)
                .asBitmap()
                .into(new BitmapImageViewTarget(img){
                    public void onResourceReady(Bitmap bitmap, GlideAnimation anim){
                        super.onResourceReady(bitmap, anim);
                        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                            @SuppressWarnings("ResourceType")
                            @Override
                            public void onGenerated(Palette palette) {
                                // Here's your generated palette
                                //mutedColor = palette.getMutedColor(getResources().getColor(R.color.colorPrimaryDark));
                                //collapsingToolbar.setContentScrimColor(getResources().getColor(mutedColor));
                                //collapsingToolbar.setStatusBarScrimColor(ctx.getResources().getColor(R.color.colorPrimary));
                            }
                        });


                    }

                    public void onLoadFailed(Exception e, Drawable errorDrawable)
                    {
                        super.onLoadFailed(e, errorDrawable);
                    }
                })
        ;
    }

    public static boolean checkPermissionForExternalStorage(Activity activity) {
        int result =
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkPermissionForWriteExternalStorage(Activity activity) {
        int result =
                ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean requestStoragePermission(Activity activity, int READ_STORAGE_PERMISSION) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_STORAGE_PERMISSION);
            }
        } else {
        }
        return false;
    }

    public static boolean requestWriteStoragePermission(Activity activity, int READ_STORAGE_PERMISSION) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},READ_STORAGE_PERMISSION);
            }
        } else {
        }
        return false;
    }

    public static Animation AnimBounce(Context _context)
    {
        final Animation myAnim = AnimationUtils.loadAnimation(_context, R.anim.bounce);
        // Use bounce interpolator with amplitude 0.2 and frequency 20
        BounceInterpolator interpolator = new BounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);
        return myAnim;
    }

    public static String fixEncodingUnicode(String response) {
        String str = "";
        try {
            // displayed as    desired encoding
            str = new String(response.getBytes("windows-1254"), "UTF-8");
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }

        String decodedStr = Html.fromHtml(str).toString();
        return decodedStr;
    }


    public static void sendFcmToServer(final Context _context, String deviceToken)
    {
        PrefManager prefManager = new PrefManager(_context);
        DBHelper _db = new DBHelper(_context);
        final Session _session = Constant.getSession(_context);
        String hpnumber = _db.getFieldProfil("hp", false);
        RetrofitBuilder builder = new RetrofitBuilder("registerGcm");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/registerFcm"
        Call<JsonObject> result = githubUserAPI.registerFcm(String.format(CONTROLLER_2S, CONTROLLER_DEV, "registerFcm"),prefManager.getUuid(), deviceToken, hpnumber, _context.getResources().getString(R.string.CONF_MITRAID));
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(!response.isSuccessful())
                {
                    Log.e("result", response.message());
                    return;
                }
                _session.setMemid(response.body().get("mem_id").getAsInt());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
                prefs.edit().putBoolean("xmpp_registered", true).commit();
                //StartApp.startLoginXmpp();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("result", t.getMessage());
            }
        });
    }

    public static int getRandomMaterialColor(Context context, String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    public static void debug(final String TAG, final String BODY)
    {
        if(DEBUG)
            Log.e(TAG, BODY);
    }

    /*
    private static final Pattern XMPP_PATTERN = Pattern
            .compile("xmpp\\:(?:(?:["
                    + Patterns.GOOD_IRI_CHAR
                    + "\\;\\/\\?\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])"
                    + "|(?:\\%[a-fA-F0-9]{2}))+");
    */
    private static final Linkify.TransformFilter WEBURL_TRANSFORM_FILTER = new Linkify.TransformFilter() {
        @Override
        public String transformUrl(Matcher matcher, String url) {
            if (url == null) {
                return null;
            }
            final String lcUrl = url.toLowerCase(Locale.US);
            if (lcUrl.startsWith("http://") || lcUrl.startsWith("https://")) {
                return url;
            } else {
                return "http://"+url;
            }
        }
    };
    private static final Linkify.MatchFilter WEBURL_MATCH_FILTER = new Linkify.MatchFilter() {
        @Override
        public boolean acceptMatch(CharSequence cs, int start, int end) {
            return start < 1 || (cs.charAt(start-1) != '@' && cs.charAt(start-1) != '.' && !cs.subSequence(Math.max(0,start - 3),start).equals("://"));
        }
    };

    public static final String getMimeType(String fileName)
    {
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        int index = fileName.lastIndexOf('.')+1;
        String ext = fileName.substring(index).toLowerCase();
        String type = mime.getMimeTypeFromExtension(ext);
        return type;
    }
}
