package com.supersoft.internusa.helper.services.xmpp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.services.ForegroundDetector;
import com.supersoft.internusa.helper.services.NotificationCenter;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ChatGroupDetail;
import com.supersoft.internusa.model.ChatGroupModel;
import com.supersoft.internusa.model.ChatSingleModel;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.model.KomenModel;
import com.supersoft.internusa.model.LikestatusModel;
import com.supersoft.internusa.model.Loghistory;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by itclub21 on 1/24/2018.
 */

public class HandleChatPacketListener extends StateChangeListener {

    private final StanzaListener mChatPacketListener;
    private final XMPPService mXMPPService;
    private final Settings mSettings;
    Context mApplicationContext;
    Session _session;

    public HandleChatPacketListener(XMPPService xmppService) {
        mXMPPService = xmppService;
        mSettings = Settings.getInstance(xmppService.getContext());
        mApplicationContext = xmppService.getContext();
        _session = new Session(this.mApplicationContext);
        mChatPacketListener = new StanzaListener() {

            @Override
            public void processStanza(Stanza packet) {
                Message message = (Message) packet;
                Jid from = message.getFrom();
                String body = message.getBody();
                //Log.e("chatPacket","Ignoring message from non-master JID: jid='" + from + "' message='"+ message + '\'');


                try
                {
                    Constant.debug("body", body);
                    InfoTipe object = new Gson().fromJson(body, InfoTipe.class);
                    DBHelper database = new DBHelper(mApplicationContext);

                    if(object.mitraid.equals(mApplicationContext.getResources().getString(R.string.CONF_MITRAID))) {

                        if ((object.id.length() > 2) && (object.id.substring(0, 2).equals(Constant.NOTIF_KODE_BIG_IMAGE))) {
                            showBig(object);
                            database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_BIG_IMAGE, object));
                        }
                        else if ((object.id.length() > 2) && (object.id.substring(0, 2).equals(Constant.NOTIF_KODE_HTML_CONTENT)))
                        {
                            NotificationCenter.getInstance().updateNotificationHtmlContent(mApplicationContext, object);
                            database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_HTML_CONTENT, object));
                        }
                        else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_CREATE_TOPIK)) ) // broadcast group
                        {
                            boolean isOpen = true;
                            Object chat = object.chatdata;
                            if(chat instanceof ChatSingleModel) {

                                ChatSingleModel cht = (ChatSingleModel) chat;
                                boolean seNotif = false;
                                if(!_session.getMemid().equals(Integer.valueOf(cht.mem_id))){
                                    if ("create_topik".equals(cht.jenis)) {
                                        seNotif = true;
                                        Loghistory log = new Loghistory();
                                        log.TYPE = Constant.NOTIF_KODE_CREATE_TOPIK;
                                        log.TITLE = cht.namapengirim + " membuat topik baru";
                                        log.DESCRIPTION = cht.message;
                                        log.IDBASE = cht.groupid;
                                        log.ACTIVITY = object.activity;
                                        log.CREATEDATE = cht.tanggal;
                                        database.insertLoghistory(log);
                                        database.insertChatGroupByIdBase(new ChatGroupModel(cht));
                                    }


                                    if ("chat_topik".equals(cht.jenis)) {
                                        ChatGroupModel groupModel = database.getSingleChatgorupByIdbase(cht.groupid).get(0);
                                        seNotif = (groupModel.JOINGROUP || _session.getMemid().equals(Integer.valueOf(groupModel.CREATORID)));
                                        Log.e("sNotif", "notif : " + seNotif);
                                        if(seNotif)
                                        {
                                            database.insertChatGroupDetail("RosterConnection",false, new ChatGroupDetail(cht, 1));
                                        }
                                    }

                                    if(seNotif) {
                                        if(ForegroundDetector.getInstance().isForeground()) EventBus.getDefault().post("badge_diskusi");
                                        if (_session.getGroupDetailActivityIsOpen() && (!_session.getGroupIdOpened().equals(""))) {
                                            //kl detail group opened dan pastinya groupid gak kosong
                                            if (_session.getGroupIdOpened().equals(((ChatSingleModel) chat).groupid)) {
                                                // dan ini kalo group id sama di broadcast ke activity detail group
                                                EventBus.getDefault().post(chat);
                                            } else
                                                NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                                        } else if (_session.getGroupActivityIsOpen()) {
                                            EventBus.getDefault().post(chat);
                                        } else
                                            NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                                    }
                                }
                            }
                        }
                        else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_LIKE)) ) {

                            if(object.likedata instanceof LikestatusModel)
                            {

                                LikestatusModel liker = object.likedata;
                                database.updateOwnlikes(String.valueOf(liker.infoid), liker.totallike);
                                database.updateLastUpdateTimeline(String.valueOf(liker.infoid));
                                database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_LIKE, object.likedata));

                                EventBus.getDefault().post(liker);
                            }
                        }
                        else if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_KOMENTAR)) ) {

                            if(object.komendata instanceof KomenModel)
                            {
                                KomenModel komen = object.komendata;
                                database.insertLoghistory(new Loghistory(Constant.NOTIF_KODE_KOMENTAR, komen));
                                int total = database.getTotalKomen(String.valueOf(komen.infoid));
                                database.updateTotalKomenIncreament(String.valueOf(komen.infoid));
                                database.updateLastUpdateTimeline(String.valueOf(komen.infoid));
                                NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                            }
                            /*
                            if (!_session.getKomentarActivityIsOpen())
                                NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                            else // nek kebuka cek infoid ne nek gak sama muncul notif
                            {

                                if(object.detail.size() > 0)
                                {
                                    for(int n=0; n < object.detail.size(); n++)
                                    {
                                        InfoItem detil = object.detail.get(n);

                                        if(detil.tipe.toLowerCase().equals("komentar"))
                                        {
                                            int total = database.getTotalKomen(String.valueOf(detil.infoid));
                                            database.updateTotalKomenIncreament(String.valueOf(detil.infoid));
                                            database.updateLastUpdateTimeline(String.valueOf(detil.infoid));
                                            if (Integer.valueOf(_session.getLastInfoId()) != detil.infoid)
                                                NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, object);
                                            else
                                                EventBus.getDefault().post(object);
                                        }

                                    }
                                }


                            }
                            */
                        }
                    }
                }
                catch (Exception ex)
                {
                    Log.e("execParse", ex.getMessage());
                    //NotificationCenter.getInstance().updateNotificationSimple(mApplicationContext, address, body);
                }
                /*
                if (MAXSElement.foundIn(packet)) {
                    // Ignore messages with a MAXS element. This is done to prevent endless loops of
                    // message sending between one or multiple MAXS instances.
                    Log.e("chatPacket","Ignoring message with MAXS element. jid='" + from + "' message='"
                            + message + '\'');
                    return;
                }

                if (mSettings.isMasterJID(from)) {
                    mXMPPService.newMessageFromMasterJID(message);
                } else {
                    Log.e("chatPacket","Ignoring message from non-master JID: jid='" + from + "' message='"
                            + message + '\'');
                }
                */
            }

        };
    }

    @Override
    public void newConnection(XMPPConnection connection) {
        connection.addAsyncStanzaListener(mChatPacketListener, MessageTypeFilter.NORMAL_OR_CHAT);

    }


    private void showBig(InfoTipe data)
    {
        loadBigImageWithNotif tasck = new loadBigImageWithNotif(mApplicationContext, data);
        tasck.execute();
    }

    class loadBigImageWithNotif extends AsyncTask<String, Integer, Bitmap>
    {
        InfoTipe data;
        Context ctx;
        public loadBigImageWithNotif(Context contxt, InfoTipe obj)
        {
            super();
            ctx = contxt;
            data = obj;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            try {
                URL url = new URL(data.bgpic);
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
            NotificationCenter.getInstance().updateNotificationBigImageSimple(mApplicationContext, data, bitmap);
        }
    }

}
