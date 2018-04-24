package com.supersoft.internusa.helper.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonParseException;
import com.supersoft.internusa.Dashboard;
import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


/**
 * Receiver which trigger action on {@link FCMListener}
 */
public class FCMListenerService extends FirebaseMessagingService {
    private static final String TAG = FCMListenerService.class.getSimpleName();
    private int NOTIFICATION_ID = 465023;
    Session _session;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        String body= data.get("body");
        String title= data.get("title");
        String _ID = data.get("id");

        FCMManager.getInstance(getApplicationContext()).onMessage(remoteMessage);
        _session = new Session(getApplicationContext());
        JSONObject obj = null;
        int infoid = 0;
        String tipe = "";
        Log.e(TAG, _ID);
        if((_ID.length() > 2) && (_ID.substring(0,2).equals("99")) )
        {
            Log.e(TAG, "Big style");
            showBig(data);
            return;
        }

        try
        {
            Map<String, String> params = remoteMessage.getData();
            obj = new JSONObject(params);
            String det = obj.getString("detail");
            JSONArray detail = new JSONArray(det);
            tipe = detail.getJSONObject(0).getString("tipe").toString();
            infoid = detail.getJSONObject(0).getInt("infoid");

        }
        catch (JsonParseException e)
        {
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("CEK", "is " + _session.getKomentarActivityIsOpen() + " && infodi " + _session.getLastInfoId() + "="+infoid);

        if(!_session.getKomentarActivityIsOpen()) // nek intent komentar klose
            sendNotification(title, body, obj.toString());
        else // nek kebuka cek infoid ne nek gak sama muncul notif
        {
            if(Integer.valueOf(_session.getLastInfoId()) != infoid)
                sendNotification(title, body, obj.toString());
        }
    }


    private void showBig(Map<String, String> data)
    {
        loadBigImageWithNotif tasck = new loadBigImageWithNotif(getApplicationContext(), data);
        tasck.execute();
    }

    private void sendNotification(String title, String messageBody, String hashMap) {

        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("data", hashMap);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


    private void sendNotificationBigPictureStyle(Context context, Map<String, String> params, Bitmap bgPic, String uri){

        String body= params.get("body");
        String title= params.get("title");
        String activity= params.get("activity");
        int _ID = Integer.parseInt(params.get("id"));

        Intent intent = new Intent(this, PromocodeImageActivity.class);
        ArrayList<String> imagesUri = new ArrayList<>();
        imagesUri.add(uri);
        intent.putStringArrayListExtra("BitmapImage", imagesUri);
        intent.putExtra("CLASS", Constant.FULL_IMAGE_NOTIF_BIG_STYLE);
        intent.putExtra(Constant.FULL_IMAGE_ACTIVITY, activity);
        //intent.putExtra("BitmapImage", uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);

        Bitmap profilePicture = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(bgPic);
        style.setBigContentTitle(title);
        style.setSummaryText(body);
        style.bigLargeIcon(profilePicture);
        builder.setStyle(style);
        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID + _ID, notification);

    }

    class loadBigImageWithNotif extends AsyncTask<String, Integer, Bitmap>
    {
        Map<String, String> data;
        Context ctx;
        public loadBigImageWithNotif(Context contxt, Map<String, String> params)
        {
            super();
            ctx = contxt;
            data = params;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            try {
                URL url = new URL(data.get("bgpic"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            sendNotificationBigPictureStyle(ctx, data, bitmap, data.get("bgpic"));
        }
    }
}