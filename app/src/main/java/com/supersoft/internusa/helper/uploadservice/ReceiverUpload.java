package com.supersoft.internusa.helper.uploadservice;

import android.content.Context;

/**
 * Created by itclub21 on 10/12/2017.
 */

public class ReceiverUpload extends UploadServiceBroadcastReceiver {
    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        // your implementation

    }


    public void onError(final Context context, final UploadInfo uploadInfo, final Exception exception) {
        // your implementation
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        // your implementation
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        // your implementation
    }
}
