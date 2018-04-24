package com.supersoft.internusa.helper.services.xmpp;

/**
 * Created by itclub21 on 1/22/2018.
 */

import android.util.Log;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.Async;
import org.jivesoftware.smack.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SendStanzaDatabaseHandler extends StateChangeListener {

    private final XMPPService mXMPPService;

    public SendStanzaDatabaseHandler(XMPPService xmppService) {
        mXMPPService = xmppService;
    }

    @Override
    public void newConnection(final XMPPConnection newConnection) {
        if (!(newConnection instanceof XMPPTCPConnection)) {
            return;
        }
        final XMPPTCPConnection connection = (XMPPTCPConnection) newConnection;

        connection.addPacketSendingListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza stanza) throws NotConnectedException {
                // This only works if stream management is enabled
                Log.e("stanza", "masuk stanza");
                if (!connection.isSmEnabled()) {
                    return;
                }

                // If the stanza has no packet id, then we can't match it later
                if (StringUtils.isNullOrEmpty(stanza.getStanzaId())) {
                    return;
                }

                // Filter out IQ requests
                if (stanza instanceof IQ) {
                    IQ iq = (IQ) stanza;
                    if (iq.isRequestIQ()) {
                        return;
                    }
                }


                //mSendUnackedStanzasTable.addStanza(stanza);
            }
            // Match all stanza by using 'null' as filter
        }, null);

        // A listener that will remove stanzas from the database
        connection.addStanzaAcknowledgedListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws NotConnectedException {
                String id = packet.getStanzaId();
                if (StringUtils.isNullOrEmpty(id)) {
                    return;
                }
                //mSendUnackedStanzasTable.removeId(id);
            }
        });

        connection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processStanza(Stanza packet) throws NotConnectedException, InterruptedException {
                Message smackMessage = (Message) packet;
                String address = ""+smackMessage.getFrom();
                String body = smackMessage.getBody();
                Message.Type type = smackMessage.getType();

                //Log.e("stanza", "from " + address + " BODY " + body +  " TYPE " + type);
            }
        }, new StanzaTypeFilter(Message.class));
    }

    @Override
    public void connected(final XMPPConnection connection) {
        final List<Stanza> toResend = new ArrayList<>();//mSendUnackedStanzasTable.getAllAndDelete();
        if (toResend.isEmpty()) {
            return;
        }
        Async.go(new Runnable() {
            @Override
            public void run() {
                for (Stanza stanza : toResend) {
                    try {
                        connection.sendStanza(stanza);
                    } catch (NotConnectedException | InterruptedException e) {
                        // Simply abort if sending the stanzas throws an exception. We could
                        // consider re-adding the stanzas that weren't send to the database, but
                        // right now, just abort.
                        Log.e("stanzdb","resend unacked stanzas got exception, aborting", e);
                        break;
                    }
                }
            }
        }, "Re-send unacked stanzas");
    }
}
