package com.supersoft.internusa.helper.services.xmpp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.services.ssl.MemorizingTrustManager;
import com.supersoft.internusa.helper.util.Constant;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;

/**
 * Created by itclub21 on 1/22/2018.
 */

public class Settings implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static Settings sSettings;
    private static final String JID = "xmpp_jid";
    private static final String PASSWORD = "xmpp_password";
    private final String MANUAL_SERVICE_SETTINGS_PORT  = "xmpp_port";
    private final String MANUAL_SERVICE_SETTINGS_HOST = "xmpp_host";
    private final String MANUAL_SERVICE_SETTINGS = "manual_setting";
    private final String MANUAL_SERVICE_SETTINGS_SERVICE = "service_host";
    private final Set<String> XMPP_CONNECTION_SETTINGS;
    private EntityBareJid mJidCache;

    public static synchronized Settings getInstance(Context context) {
        if (sSettings == null) {
            sSettings = new Settings(context);
        }
        return sSettings;
    }

    private SharedPreferences mSharedPreferences;
    private XMPPTCPConnectionConfiguration mConnectionConfiguration;
    private Set<EntityBareJid> mMasterJidCache;
    private String MITRAID = "";

    private Settings(Context context) {
        // this.mSharedPreferences =
        // context.getSharedPreferences(Constants.MAIN_PACKAGE,
        // Context.MODE_PRIVATE);
        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        MITRAID = context.getResources().getString(R.string.CONF_MITRAID);
        XMPP_CONNECTION_SETTINGS = new HashSet<String>(Arrays.asList(
                new String[] { JID, PASSWORD, MANUAL_SERVICE_SETTINGS, MANUAL_SERVICE_SETTINGS_HOST,
                        MANUAL_SERVICE_SETTINGS_PORT }));
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    public String getPassword() {
        return mSharedPreferences.getString(PASSWORD, "123456");
    }

    private int getManualServiceSettingsPort() {
        return Integer.parseInt(mSharedPreferences.getString(MANUAL_SERVICE_SETTINGS_PORT, "5222"));
    }

    private String getManualServiceSettingsHost() {
        return mSharedPreferences.getString(MANUAL_SERVICE_SETTINGS_HOST, Constant.XMPP_HOST);
    }

    private DomainBareJid getManualServiceSettingsService() throws XmppStringprepException {
        final String jidString = mSharedPreferences.getString(MANUAL_SERVICE_SETTINGS_SERVICE, "");
        return JidCreate.domainBareFrom(jidString);
    }

    private boolean getManualServiceSettings() {
        return mSharedPreferences.getBoolean(MANUAL_SERVICE_SETTINGS, false);
    }


    public String checkIfReadyToConnect() {
        if (getPassword().isEmpty()) return "Password not set or empty";
        if (getJid() == null) return "JID not set or empty";
        if (getManualServiceSettings()) {
            if (getManualServiceSettingsHost().isEmpty()) return "XMPP Server Host not specified";
            try {
                getManualServiceSettingsService();
            } catch (XmppStringprepException e) {
                String causingString = e.getCausingString();
                if (causingString.isEmpty()) {
                    return "XMPP Server service name not specified";
                } else {
                    return "Not a valid service string: '" + causingString + "'";
                }
            }
        }

        return null;
    }

    public EntityBareJid getJid() {
        if (mJidCache != null) {
            return mJidCache;
        }
        String jidString = mSharedPreferences.getString(JID, "");
        Log.e("JID", jidString);
        if (jidString.isEmpty()) {
            return null;
        }
        EntityBareJid bareJid;
        try {
            bareJid = JidCreate.entityBareFrom(jidString);
        } catch (XmppStringprepException e) {
            throw new AssertionError(e);
        }
        return bareJid;
    }

    public XMPPTCPConnectionConfiguration getConnectionConfiguration(Context context) throws XmppStringprepException {
        if (mConnectionConfiguration == null) {
            DomainBareJid service;
            XMPPTCPConnectionConfiguration.Builder confBuilder = XMPPTCPConnectionConfiguration.builder();
            if (getManualServiceSettings()) {
                String host = getManualServiceSettingsHost();
                int port = getManualServiceSettingsPort();
                service = getManualServiceSettingsService();
                confBuilder.setHost(host);
                confBuilder.setPort(port);
                confBuilder.setXmppDomain(service);
            } else {
                service = JidCreate.from(mSharedPreferences.getString(JID, "")).asDomainBareJid();
            }

            //confBuilder.setUsernameAndPassword("738-microz", "123456");
            confBuilder.setUsernameAndPassword(getJid().getLocalpart(), getPassword());
            confBuilder.setXmppDomain(service);
            confBuilder.setResource("AR" + MITRAID);
            confBuilder.setSocketFactory(XMPPSocketFactory.getInstance());
            confBuilder.setCompressionEnabled(false);

            ConnectionConfiguration.SecurityMode securityMode;
            /*
            if ("opt".equals(securityModeString)) {

            } else if ("req".equals(securityModeString)) {
                securityMode = ConnectionConfiguration.SecurityMode.required;
            } else if ("dis".equals(securityModeString)) {
                securityMode = ConnectionConfiguration.SecurityMode.disabled;
            } else {
                throw new IllegalArgumentException("Unknown security mode: " + securityModeString);
            }
            */
            securityMode = ConnectionConfiguration.SecurityMode.disabled;
            confBuilder.setSecurityMode(securityMode);

            confBuilder.setSendPresence(true);
            confBuilder.setDebuggerEnabled(true);
            TLSUtils.disableHostnameVerificationForTlsCertificates(confBuilder);

            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, MemorizingTrustManager.getInstanceList(context), new SecureRandom());
                confBuilder.setCustomSSLContext(sc);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            } catch (KeyManagementException e) {
                throw new IllegalStateException(e);
            }

            mConnectionConfiguration = confBuilder.build();
        }

        return mConnectionConfiguration;
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        for (String s : XMPP_CONNECTION_SETTINGS) {
            if (s.equals(key)) {
                mConnectionConfiguration = null;
                break;
            }
        }
        //if (key.equals(DEBUG_DNS)) {
         //   setDnsDebug();
        //} else if (key.equals(XMPP_STREAM_COMPRESSION_SYNC_FLUSH)) {
        //    setSyncFlush();
        //}
    }
}
