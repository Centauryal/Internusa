package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.supersoft.internusa.helper.util.Constant;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.sm.packet.StreamManagement;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.privacy.PrivacyListManager;

public class HandleTransportStatus extends StateChangeListener {

    private final Context mContext;
    private final Settings mSettings;

    private String mStatusString;

    public HandleTransportStatus(Context context) {
        mContext = context;
        mStatusString = "inactive";
        mSettings = Settings.getInstance(context);
    }

    @Override
    public void connected(XMPPConnection connection) throws SmackException.NotConnectedException {
        String encryptionStatus;
        if (connection.isSecureConnection()) {
            encryptionStatus = "encrypted";
        } else {
            encryptionStatus = "unencrypted";
        }

        String compressionStatus;
        if (connection.isUsingCompression()) {
            compressionStatus = "compressed";
        } else {
            compressionStatus = "uncompressed";
        }

        String privacyListStatus;
        final String privacyInactive = "privacy inactive";
        try {
            if (PrivacyListManager.getInstanceFor(connection).isSupported()) {
                String privacyListName = PrivacyListManager.getInstanceFor(connection)
                        .getDefaultListName();
                privacyListStatus = privacyInactive;
            } else {
                privacyListStatus = "privacy not supported";
            }
        } catch (XMPPException.XMPPErrorException e) {
            if (XMPPError.Condition.item_not_found.equals(e.getXMPPError().getCondition())) {
                privacyListStatus = privacyInactive;
            } else {
                Log.e("handleTransport","connected", e);
                privacyListStatus = "privacy unkown";
            }
        } catch (InterruptedException | SmackException.NoResponseException e) {
            Log.e("handleTransport","connected", e);
            privacyListStatus = "privacy unkown";
        }

        String streamManagementStatus = "stream management ";
        if (connection instanceof XMPPTCPConnection) {
            XMPPTCPConnection c = (XMPPTCPConnection) connection;
            if (c.hasFeature(StreamManagement.StreamManagementFeature.ELEMENT,
                    StreamManagement.NAMESPACE)) {
                if (c.isSmEnabled()) {
                    streamManagementStatus += "active";
                } else {
                    streamManagementStatus += "not active";
                }
            } else {
                streamManagementStatus += "not supported";
            }
        } else {
            streamManagementStatus += "not supported by connnection";
        }

        setAndSendStatus("connected (" + encryptionStatus + ", " + compressionStatus + ", "+ privacyListStatus + ", " + streamManagementStatus + ")");
    }

    @Override
    public void disconnected(String reason) {
        if (reason.isEmpty()) {
            setAndSendStatus("disconnected");
        } else {
            setAndSendStatus("disconnected: " + reason);
        }
    }

    @Override
    public void connecting() {
        setAndSendStatus("connecting");
    }

    @Override
    public void disconnecting() {
        setAndSendStatus("disconnecting");
    }

    @Override
    public void waitingForNetwork() {
        setAndSendStatus("waiting for data connection");
    }

    @Override
    public void waitingForRetry(String optionalReason) {
        if (!optionalReason.isEmpty()) {
            optionalReason = ": " + optionalReason;
        }
        setAndSendStatus("Waiting for connection retry" + optionalReason);
    }

    protected void setAndSendStatus(String status) {
        mStatusString = status;
        sendStatus();
    }

    public void sendStatus() {
        Log.e("trasnstatu","Mustin kesinis : " + mStatusString);

        Intent intent = new Intent(Constant.ACTION_UPDATE_TRANSPORT_STATUS);
        intent.setClassName("com.supersoft.internusa",".services.ConnectionService");
        intent.putExtra(Constant.EXTRA_CONTENT, mStatusString);
        mContext.startService(intent);

    }
}
