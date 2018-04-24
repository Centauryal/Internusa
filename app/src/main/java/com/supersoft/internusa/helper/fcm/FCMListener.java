package com.supersoft.internusa.helper.fcm;

/**
 * Created by itclub21 on 9/28/2017.
 */
import com.google.firebase.messaging.RemoteMessage;

public interface FCMListener {
    /**
     * Called when device is registered to FCM servers and received token
     *
     * @param deviceToken
     */
    void onDeviceRegistered(String deviceToken);

    /**
     * Called when downstream message receive by device.
     *
     * @param remoteMessage
     */
    void onMessage(RemoteMessage remoteMessage);

    /**
     * Called when device is unable to google play service.
     */
    void onPlayServiceError();
}
