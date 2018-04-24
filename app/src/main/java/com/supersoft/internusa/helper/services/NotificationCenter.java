package com.supersoft.internusa.helper.services;

/**
 * Created by itclub21 on 11/3/2017.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.supersoft.internusa.Dashboard;
import com.supersoft.internusa.helper.fcm.PromocodeImageActivity;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.view.CodehtmlContentActivity;

import java.util.ArrayList;

public class NotificationCenter {

    private static int totalEvents = 1;

    public static final int didReceivedNewMessages = totalEvents++;
    public static final int updateInterfaces = totalEvents++;
    public static final int dialogsNeedReload = totalEvents++;
    public static final int closeChats = totalEvents++;
    public static final int messagesDeleted = totalEvents++;
    public static final int messagesRead = totalEvents++;
    public static final int messagesDidLoaded = totalEvents++;
    public static final int messageReceivedByAck = totalEvents++;
    public static final int messageReceivedByServer = totalEvents++;
    public static final int messageSendError = totalEvents++;
    public static final int contactsDidLoaded = totalEvents++;
    public static final int chatDidCreated = totalEvents++;
    public static final int chatDidFailCreate = totalEvents++;
    public static final int chatInfoDidLoaded = totalEvents++;
    public static final int chatInfoCantLoad = totalEvents++;
    public static final int mediaDidLoaded = totalEvents++;
    public static final int mediaCountDidLoaded = totalEvents++;
    public static final int encryptedChatUpdated = totalEvents++;
    public static final int messagesReadEncrypted = totalEvents++;
    public static final int encryptedChatCreated = totalEvents++;
    public static final int dialogPhotosLoaded = totalEvents++;
    public static final int removeAllMessagesFromDialog = totalEvents++;
    public static final int notificationsSettingsUpdated = totalEvents++;
    public static final int pushMessagesUpdated = totalEvents++;
    public static final int blockedUsersDidLoaded = totalEvents++;
    public static final int openedChatChanged = totalEvents++;
    public static final int stopEncodingService = totalEvents++;
    public static final int didCreatedNewDeleteTask = totalEvents++;
    public static final int mainUserInfoChanged = totalEvents++;
    public static final int privacyRulesUpdated = totalEvents++;
    public static final int updateMessageMedia = totalEvents++;
    public static final int recentImagesDidLoaded = totalEvents++;
    public static final int replaceMessagesObjects = totalEvents++;
    public static final int didSetPasscode = totalEvents++;
    public static final int didSetTwoStepPassword = totalEvents++;
    public static final int didRemovedTwoStepPassword = totalEvents++;
    public static final int screenStateChanged = totalEvents++;
    public static final int didLoadedReplyMessages = totalEvents++;
    public static final int didLoadedPinnedMessage = totalEvents++;
    public static final int newSessionReceived = totalEvents++;
    public static final int didReceivedWebpages = totalEvents++;
    public static final int didReceivedWebpagesInUpdates = totalEvents++;
    public static final int stickersDidLoaded = totalEvents++;
    public static final int featuredStickersDidLoaded = totalEvents++;
    public static final int didReplacedPhotoInMemCache = totalEvents++;
    public static final int messagesReadContent = totalEvents++;
    public static final int botInfoDidLoaded = totalEvents++;
    public static final int userInfoDidLoaded = totalEvents++;
    public static final int botKeyboardDidLoaded = totalEvents++;
    public static final int chatSearchResultsAvailable = totalEvents++;
    public static final int musicDidLoaded = totalEvents++;
    public static final int needShowAlert = totalEvents++;
    public static final int didUpdatedMessagesViews = totalEvents++;
    public static final int needReloadRecentDialogsSearch = totalEvents++;
    public static final int locationPermissionGranted = totalEvents++;
    public static final int peerSettingsDidLoaded = totalEvents++;
    public static final int wasUnableToFindCurrentLocation = totalEvents++;
    public static final int reloadHints = totalEvents++;
    public static final int reloadInlineHints = totalEvents++;
    public static final int newDraftReceived = totalEvents++;
    public static final int recentDocumentsDidLoaded = totalEvents++;
    public static final int cameraInitied = totalEvents++;
    public static final int needReloadArchivedStickers = totalEvents++;
    public static final int didSetNewWallpapper = totalEvents++;
    public static final int archivedStickersCountDidLoaded = totalEvents++;
    public static final int paymentFinished = totalEvents++;
    public static final int reloadInterface = totalEvents++;
    public static final int suggestedLangpack = totalEvents++;
    public static final int channelRightsUpdated = totalEvents++;
    public static final int proxySettingsChanged = totalEvents++;

    public static final int httpFileDidLoaded = totalEvents++;
    public static final int httpFileDidFailedLoad = totalEvents++;

    public static final int messageThumbGenerated = totalEvents++;

    public static final int wallpapersDidLoaded = totalEvents++;
    public static final int closeOtherAppActivities = totalEvents++;
    public static final int didUpdatedConnectionState = totalEvents++;
    public static final int didReceiveSmsCode = totalEvents++;
    public static final int didReceiveCall = totalEvents++;
    public static final int emojiDidLoaded = totalEvents++;
    public static final int appDidLogout = totalEvents++;

    public static final int FileDidUpload = totalEvents++;
    public static final int FileDidFailUpload = totalEvents++;
    public static final int FileUploadProgressChanged = totalEvents++;
    public static final int FileLoadProgressChanged = totalEvents++;
    public static final int FileDidLoaded = totalEvents++;
    public static final int FileDidFailedLoad = totalEvents++;
    public static final int FilePreparingStarted = totalEvents++;
    public static final int FileNewChunkAvailable = totalEvents++;
    public static final int FilePreparingFailed = totalEvents++;

    public static final int messagePlayingProgressDidChanged = totalEvents++;
    public static final int messagePlayingDidReset = totalEvents++;
    public static final int messagePlayingPlayStateChanged = totalEvents++;
    public static final int messagePlayingDidStarted = totalEvents++;
    public static final int recordProgressChanged = totalEvents++;
    public static final int recordStarted = totalEvents++;
    public static final int recordStartError = totalEvents++;
    public static final int recordStopped = totalEvents++;
    public static final int screenshotTook = totalEvents++;
    public static final int albumsDidLoaded = totalEvents++;
    public static final int audioDidSent = totalEvents++;
    public static final int audioRouteChanged = totalEvents++;

    public static final int didStartedCall = totalEvents++;
    public static final int didEndedCall = totalEvents++;
    public static final int closeInCallActivity = totalEvents++;

    private SparseArray<ArrayList<Object>> observers = new SparseArray<>();
    private SparseArray<ArrayList<Object>> removeAfterBroadcast = new SparseArray<>();
    private SparseArray<ArrayList<Object>> addAfterBroadcast = new SparseArray<>();
    private ArrayList<DelayedPost> delayedPosts = new ArrayList<>(10);

    private int broadcasting = 0;
    private boolean animationInProgress;

    private int[] allowedNotifications;
    private InfoTipe infoTipe;
    public interface NotificationCenterDelegate {
        void didReceivedNotification(int id, Object... args);
    }

    private class DelayedPost {

        private DelayedPost(int id, Object[] args) {
            this.id = id;
            this.args = args;
        }

        private int id;
        private Object[] args;
    }

    private static volatile NotificationCenter Instance = null;

    public static NotificationCenter getInstance() {
        NotificationCenter localInstance = Instance;
        if (localInstance == null) {
            synchronized (NotificationCenter.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new NotificationCenter();
                }
            }
        }
        return localInstance;
    }

    public void setAllowedNotificationsDutingAnimation(int notifications[]) {
        allowedNotifications = notifications;
    }

    public void setAnimationInProgress(boolean value) {
        animationInProgress = value;
        if (!animationInProgress && !delayedPosts.isEmpty()) {
            for (int a = 0; a < delayedPosts.size(); a++) {
                DelayedPost delayedPost = delayedPosts.get(a);
                postNotificationNameInternal(delayedPost.id, true, delayedPost.args);
            }
            delayedPosts.clear();
        }
    }

    public boolean isAnimationInProgress() {
        return animationInProgress;
    }

    public void postNotificationName(int id, Object... args) {
        boolean allowDuringAnimation = false;
        if (allowedNotifications != null) {
            for (int a = 0; a < allowedNotifications.length; a++) {
                if (allowedNotifications[a] == id) {
                    allowDuringAnimation = true;
                    break;
                }
            }
        }
        postNotificationNameInternal(id, allowDuringAnimation, args);
    }

    public void postNotificationNameInternal(int id, boolean allowDuringAnimation, Object... args) {

        if (!allowDuringAnimation && animationInProgress) {
            DelayedPost delayedPost = new DelayedPost(id, args);
            delayedPosts.add(delayedPost);

            return;
        }
        broadcasting++;
        ArrayList<Object> objects = observers.get(id);
        if (objects != null && !objects.isEmpty()) {
            for (int a = 0; a < objects.size(); a++) {
                Object obj = objects.get(a);
                ((NotificationCenterDelegate) obj).didReceivedNotification(id, args);
            }
        }
        broadcasting--;
        if (broadcasting == 0) {
            if (removeAfterBroadcast.size() != 0) {
                for (int a = 0; a < removeAfterBroadcast.size(); a++) {
                    int key = removeAfterBroadcast.keyAt(a);
                    ArrayList<Object> arrayList = removeAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        removeObserver(arrayList.get(b), key);
                    }
                }
                removeAfterBroadcast.clear();
            }
            if (addAfterBroadcast.size() != 0) {
                for (int a = 0; a < addAfterBroadcast.size(); a++) {
                    int key = addAfterBroadcast.keyAt(a);
                    ArrayList<Object> arrayList = addAfterBroadcast.get(key);
                    for (int b = 0; b < arrayList.size(); b++) {
                        addObserver(arrayList.get(b), key);
                    }
                }
                addAfterBroadcast.clear();
            }
        }
    }

    public void addObserver(Object observer, int id) {

        if (broadcasting != 0) {
            ArrayList<Object> arrayList = addAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                addAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<Object> objects = observers.get(id);
        if (objects == null) {
            observers.put(id, (objects = new ArrayList<>()));
        }
        if (objects.contains(observer)) {
            return;
        }
        objects.add(observer);
    }

    public void removeObserver(Object observer, int id) {

        if (broadcasting != 0) {
            ArrayList<Object> arrayList = removeAfterBroadcast.get(id);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                removeAfterBroadcast.put(id, arrayList);
            }
            arrayList.add(observer);
            return;
        }
        ArrayList<Object> objects = observers.get(id);
        if (objects != null) {
            objects.remove(observer);
        }
    }


    public void updateNotificationSimple(Context context, Object info)
    {
        infoTipe = (InfoTipe)info;
        String data = new Gson().toJson(infoTipe);

        Intent intent = new Intent(context, Dashboard.class);

        intent.putExtra("data", data);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        final Builder mBuilder;

        mBuilder = buildSingleConversations(context, infoTipe.title, infoTipe.body);
        setPendingIntent(mBuilder, pendingIntent);
        modifyForSoundVibrationAndLight(context, mBuilder, true);
        notificationManager.notify(10010, mBuilder.build());
    }

    public void updateNotificationHtmlContent(Context context, InfoTipe info)
    {
        Intent intent = new Intent(context, CodehtmlContentActivity.class);
        intent.putExtra(Constant.FULL_IMAGE_ACTIVITY, info.activity);
        intent.putExtra(Constant.FULL_IMAGE_TITLE, info.title);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        final Builder mBuilder;
        mBuilder = buildSingleConversations(context, info.title, info.body);
        setPendingIntent(mBuilder, pendingIntent);
        modifyForSoundVibrationAndLight(context, mBuilder, true);
        notificationManager.notify(10010, mBuilder.build());

    }

    public void updateNotificationBigImageSimple(Context context, InfoTipe info, Bitmap bgPic)
    {

        Intent intent = new Intent(context, PromocodeImageActivity.class);
        ArrayList<String> imagesUri = new ArrayList<>();
        imagesUri.add(info.bgpic);
        intent.putStringArrayListExtra("BitmapImage", imagesUri);
        intent.putExtra("CLASS", Constant.FULL_IMAGE_NOTIF_BIG_STYLE);
        intent.putExtra(Constant.FULL_IMAGE_ACTIVITY, info.activity);
        //intent.putExtra("BitmapImage", uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        final Builder mBuilder;
        mBuilder = buildSingleConversations(context, info.title, info.body);
        setPendingIntent(mBuilder, pendingIntent);
        modifyForSoundVibrationAndLight(context, mBuilder, true);
        notificationManager.notify(10010, mBuilder.build());

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap profilePicture = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher);

        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle();
        style.bigPicture(bgPic);
        style.setBigContentTitle(info.title);
        style.setSummaryText(info.body);
        style.bigLargeIcon(profilePicture);
        mBuilder.setStyle(style);
        Notification notification = mBuilder.build();
        NotificationManagerCompat.from(context).notify(10020 + Integer.parseInt(info.id), notification);
    }

    private Builder buildSingleConversations(final Context context, final String from, final String msg) {
        final Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(from);
        mBuilder.setContentText(msg);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            mBuilder.setSmallIcon(R.drawable.ic_notification_lolipop);
            mBuilder.setColor(ContextCompat.getColor(context, R.color.primary500));
        }
        else
        {
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }

        mBuilder.setAutoCancel(true);
        return mBuilder;
    }

    private void setPendingIntent(Builder mBuilder, PendingIntent pendingIntent)
    {
        mBuilder.setContentIntent(pendingIntent);
    }

    private void modifyForSoundVibrationAndLight(Context context, Builder mBuilder, boolean notify) {
        final Resources resources = context.getResources();
        final String ringtone = "";//preferences.getString("notification_ringtone", resources.getString(R.string.notification_ringtone));
        final boolean vibrate = true;//preferences.getBoolean("vibrate_on_notification", resources.getBoolean(R.bool.vibrate_on_notification));
        final boolean led = true;//preferences.getBoolean("led", resources.getBoolean(R.bool.led));
        final boolean headsup = true;//preferences.getBoolean("notification_headsup", resources.getBoolean(R.bool.headsup_notifications));
        if (notify) {
            if (vibrate) {
                final int dat = 70;
                final long[] pattern = {0, 3 * dat, dat, dat};
                mBuilder.setVibrate(pattern);
            } else {
                mBuilder.setVibrate(new long[]{0});
            }
            Uri uri = Uri.parse(ringtone);
            try {
                mBuilder.setSound(fixRingtoneUri(context, uri));
            } catch (SecurityException e) {
                Log.e("RoosterConnection","unable to use custom notification sound "+uri.toString());
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        mBuilder.setPriority(notify ? (headsup ? NotificationCompat.PRIORITY_HIGH : NotificationCompat.PRIORITY_DEFAULT) : NotificationCompat.PRIORITY_LOW);
        setNotificationColor(context, mBuilder);
        mBuilder.setDefaults(0);
        if (led) {
            mBuilder.setLights(0xff00FF00, 2000, 3000);
        }
    }

    private void setNotificationColor(Context context, final Builder mBuilder) {
        mBuilder.setColor(ContextCompat.getColor(context, R.color.primary500));
    }

    private Uri fixRingtoneUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && "file".equals(uri.getScheme())) {
            return uri;//FileBackend.getUriForFile(context,new File(uri.getPath()));
        } else {
            return uri;
        }
    }
}
