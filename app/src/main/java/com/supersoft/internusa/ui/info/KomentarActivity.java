package com.supersoft.internusa.ui.info;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.exception.UnCaughtException;
import com.supersoft.internusa.helper.fcm.FCMListener;
import com.supersoft.internusa.helper.fcm.FCMManager;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.services.xmpp.XMPPService;
import com.supersoft.internusa.helper.util.ArabicUtilities;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.InfoItem;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.model.KomenModel;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomentarActivity extends AppCompatActivity implements FCMListener,  StanzaListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lvwData) ListView lvwData;
    @BindView(R.id.btSend) ImageButton btnSubmit;
    @BindView(R.id.etMessage) EditText etMessage;
    @BindView(R.id.pgBar) ProgressBar pgBar;
    @BindView(R.id.lblInfo) TextView lblInfo;
    @BindView(R.id.txtDescription) TextView txtDescription;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.subTitleToolbar) TextView subTitleToolbar;
    @BindView(R.id.relativeSubmit) RelativeLayout relativeSubmit;

    private static final String COL_PUBLIK = "#dddddd";
    private static final String COL_PUBLIK_BODY = "#efefef";

    private static final String COL_INFO = "#a2e497";
    private static final String COL_INFO_BODY = "#d9f3d5";

    private static final String COL_OTHER = "#f8da9b";
    private static final String COL_OTHER_BODY = "#faefd9";

    private String TIPE_OF_INFO = "";
    private int LAST_POSITION = 0;

    ArrayList<KomenModel> komenModel = new ArrayList<>();
    KomenAdapter mAdapter;
    XMPPService mXMPPService;

    int info_id = 0;
    int POSITION = 0;
    boolean ISOWN = false;

    Session _session;

    DBHelper _db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(KomentarActivity.this));
        setContentView(R.layout.komentar_activity);
        ButterKnife.bind(this);
        _session = new Session(this);
        _db = new DBHelper(this);
        mAdapter = new KomenAdapter(KomentarActivity.this);
        lvwData.setAdapter(mAdapter);
        if(mXMPPService == null) mXMPPService = XMPPService.getInstance(this);
        if(mXMPPService.shouldUseXmppConnection())
            mXMPPService.getConnection().addAsyncStanzaListener(this, MessageTypeFilter.NORMAL_OR_CHAT);

        FCMManager.getInstance(this).registerListener(this);

        initToolbar();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            info_id = Integer.valueOf(extras.getString("INFOID"));
            POSITION = extras.getInt("POS");
            if(info_id > 0)
            {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Row> datas = _db.getTimeline(info_id,0, 10000);
                        if(datas.size() > 0) {
                            TIPE_OF_INFO = datas.get(0).getStatus();
                            fetchInfo(datas.get(0).telp, datas.get(0).isi, datas.get(0).pengirim, datas.get(0).tglkirim);
                            ArrayList<KomenModel> localData = _db.getKomen(info_id);
                            fetchKomentar(localData);
                        }
                        pgBar.setVisibility(View.GONE);

                    }
                }, 1000);

                // thread ini untuk cek apa ada yg terbaru di komentar, di running sekali aja

            }
        }

        int maxid = _db.getMaxKomenByInfoid(info_id);
        Log.e("maxid", "max : " + maxid);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadKomentar(info_id, 0);
            }
        }, 3000);

    }

    @OnClick(R.id.btSend)
    public void onSubmit()
    {
        if(!etMessage.getText().equals("") && etMessage.getText().length() > 2)
        {
            createKomentar(info_id);
        }
    }

    @OnEditorAction(R.id.etMessage)
    public boolean onActionSend(int actionId)
    {
        if(actionId == EditorInfo.IME_ACTION_SEND)
        {
            createKomentar(info_id);
        }
        return true;
    }

    public void onResume()
    {
        super.onResume();
        _session.setKomentarActivityIsOpen(true);
        _session.setLastInfoId(String.valueOf(info_id));
    }

    public void onPause()
    {
        super.onPause();
        _session.setKomentarActivityIsOpen(false);
        _session.setLastInfoId("");
    }

    public void onDestroy()
    {
        super.onDestroy();
        _session.setKomentarActivityIsOpen(false);
        _session.setLastInfoId("");
        FCMManager.getInstance(this).unRegisterListener();
    }


    private void initToolbar()
    {
        final DBHelper db = new DBHelper(this);
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_arrow_left_light));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(POSITION != -1) {
                        //MsgEvent event = new MsgEvent(POSITION, String.valueOf(db.getTotalKomen(String.valueOf(info_id))), "", "komentar");
                        //EventBus.getDefault().post(event);
                    }
                    finish();
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        final DBHelper db = new DBHelper(this);
        if(POSITION != -1) {
            MsgEvent event = new MsgEvent(POSITION, String.valueOf(db.getTotalKomen(String.valueOf(info_id))), "", "komentar");
            EventBus.getDefault().post(event);
        }
        super.onBackPressed();
    }

    private void loadKomentar(final int info_id, final int komenid)
    {
        final ProfilDB profil = new DBHelper(this).getProfilDb();
        final ModelRequestInfo req = new ModelRequestInfo();
        req.info_id = String.valueOf(info_id);
        req.mitraid = getResources().getString(R.string.CONF_MITRAID);
        if(komenid > 0) req.lastid = komenid;

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        String url = String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "get_info", (komenid >0 ) ? "read_komen_byid" : "read_komen", profil.hp);
        Call<JsonObject> call = api.getKomnetar(url, req);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(!response.isSuccessful())return;

                try
                {
                    JsonObject obj = response.body();
                    JsonArray jArr = obj.get("rows").getAsJsonArray();

                    int indicator = 0;
                    if (jArr.size() > 0) {
                        komenModel = new ArrayList<KomenModel>();
                        lblInfo.setVisibility(View.GONE);
                        for (int i = 0; i < jArr.size(); i++) {
                            JsonObject object = jArr.get(i).getAsJsonObject();
                            KomenModel mdl = new KomenModel();
                            mdl.idbase = object.get("id").getAsInt();
                            mdl.infoid = info_id;
                            mdl.pengirim = object.get("pengirim").getAsString();
                            mdl.isi = object.get("isi").getAsString();
                            mdl.tglkirim = object.get("tglkirim").getAsString();
                            mdl.kirimdari = object.get("kirimdari").getAsString();
                            long isUpdate = _db.insertKomentar(mdl, false); // 1=update karena ada data
                            if(isUpdate > 1) {
                                komenModel.add(mdl);
                                indicator++;
                            }

                        }
                        if(indicator > 0) fetchKomentar(komenModel);
                        int total = _db.getTotalKomen(String.valueOf(info_id));
                        _db.updateTotalKomenIncreament(String.valueOf(info_id), jArr.size());
                    }
                    else
                    {
                        if(_db.getTotalKomen(String.valueOf(info_id)) <=0 )
                        {
                            lblInfo.setVisibility(View.VISIBLE);
                        }
                    }

                }
                catch (JsonParseException ex)
                {

                }
                catch (NullPointerException e)
                {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //pgBar.setVisibility(View.GONE);
                //lblInfo.setVisibility(View.VISIBLE);
                //lblInfo.setText(t.getMessage());
            }
        });
    }


    private void fetchInfo(String telp, String content, String pengirim, String tglkirim)
    {
        ProfilDB profil = new DBHelper(this).getProfilDb();
        String body = content;
        String notelp = telp;
        toolbar_title.setText(pengirim);
        subTitleToolbar.setText(Constant.parseDate(tglkirim));
        String expandText = "View More";
        if(notelp.equals(profil.hp.replace("+","")))
        {
            ISOWN = true;
        }

        if(TIPE_OF_INFO.equals("Publik")) {
            toolbar.setBackgroundColor(Color.parseColor(COL_PUBLIK));
            txtDescription.setBackgroundColor(Color.parseColor(COL_PUBLIK_BODY));
        }
        else if(TIPE_OF_INFO.equals("Info")) {
            toolbar.setBackgroundColor(Color.parseColor(COL_INFO));
            txtDescription.setBackgroundColor(Color.parseColor(COL_INFO_BODY));
        }
        else
        {
            toolbar.setBackgroundColor(Color.parseColor(COL_OTHER));
            txtDescription.setBackgroundColor(Color.parseColor(COL_OTHER_BODY));
        }

        String isi = ArabicUtilities.reshape(body);
        if(isi.length() > 200)
        {
            String text=isi.substring(0,200)+"...";
            final String fulltext = isi;
            final SpannableString ss = new SpannableString(text + expandText);
            ClickableSpan span1 = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    final SpannableString ss1 = new SpannableString(fulltext+" View Less");
                    ClickableSpan span2 = new ClickableSpan() {
                        @Override
                        public void onClick(View textView) {
                            txtDescription.setText(ss);
                            txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
                            relativeSubmit.setVisibility(View.VISIBLE);
                        }

                        public void updateDrawState(TextPaint ds) {// override updateDrawState
                            ds.setUnderlineText(false); // set to false to remove underline
                        }
                    };

                    ss1.setSpan(span2, fulltext.length(), ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss1.setSpan(new ForegroundColorSpan(Color.BLUE), fulltext.length(), ss1.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    txtDescription.setText(ss1);
                    txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
                    relativeSubmit.setVisibility(View.GONE);

                }
                public void updateDrawState(TextPaint ds) {// override updateDrawState
                    ds.setUnderlineText(false); // set to false to remove underline
                }
            };

            ss.setSpan(span1, 203, 203 + expandText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.BLUE), 203, 203 + expandText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            txtDescription.setText(ss);
            txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
            relativeSubmit.setVisibility(View.VISIBLE);

        }
        else
        {
            txtDescription.setText(isi);
        }
        txtDescription.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        Linkify.addLinks(txtDescription, Linkify.WEB_URLS);
    }

    private void fetchKomentar(ArrayList<KomenModel> data)
    {
        mAdapter.addNewItem(data, TIPE_OF_INFO);
        lvwData.setSelection(mAdapter.getCount());
    }

    private ModelRequestInfo putAdapter(ProfilDB profil)
    {
        ModelRequestInfo req = new ModelRequestInfo();
        req.info_id = String.valueOf(info_id);
        req.mitraid = getResources().getString(R.string.CONF_MITRAID);
        req.berita = etMessage.getText().toString();

        KomenModel mdl = new KomenModel();
        mdl.pengirim = (profil.fullname == null) ? profil.hp : profil.fullname;
        mdl.isi = req.berita;
        mdl.tglkirim = Constant.currDate();
        long localid = _db.insertKomentar(mdl, true);
        req.localid = String.valueOf(localid);

        mAdapter.addNewItem(mdl, TIPE_OF_INFO);

        LAST_POSITION = mAdapter.getCount();
        lvwData.setSelection(LAST_POSITION);
        return req;
    }

    private void createKomentar(final int info_id)
    {
        final DBHelper db = new DBHelper(this);
        ProfilDB profil = db.getProfilDb();

        final ModelRequestInfo req = putAdapter(profil);

        btnSubmit.setEnabled(false);
        etMessage.setText("");

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        String url = String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV, "get_info", "create_komentar", profil.hp);
        Call<JsonObject> call = api.getKomnetar(url, req);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //pgBar.setVisibility(View.GONE);
                Log.e("res", new Gson().toJson(response.body()));
                btnSubmit.setEnabled(true);
                if(!response.isSuccessful())return;

                try
                {
                    final JsonObject obj = response.body();
                    final JsonArray jArr = obj.get("data").getAsJsonArray();
                    if(jArr.size() > 0)
                    {
                        String total = "0";
                        lblInfo.setVisibility(View.GONE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Constant.debug("lastpos", "-"+LAST_POSITION);

                                KomenModel mdl = mAdapter.getItem(LAST_POSITION-1);
                                mdl.id = obj.get("localid").getAsString();
                                mdl.idbase = jArr.get(0).getAsJsonObject().get("id").getAsInt();
                                mdl.infoid = info_id;
                                mdl.tglkirim = Constant.currDate();
                                mdl.pengirim= jArr.get(0).getAsJsonObject().get("pengirim").getAsString();
                                mdl.kirimdari = "";

                                _db.insertKomentar(mdl, true);
                                db.updateLastUpdateTimeline(String.valueOf(mdl.idbase));
                                mAdapter.notifyDataSetChanged();

                            }
                        });

                        if(POSITION != -1) {
                            db.updateTotalKomenIncreament(String.valueOf(info_id));
                        }

                        //Constant.debug("broadApp", "ada member " + msgBroadcast.size());
                        //if(msgBroadcast.size() > 0) sendBroadcastMessage();

                    }
                }
                catch (JsonParseException ex)
                {
                    //Log.e("res", ex.getMessage());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                btnSubmit.setEnabled(true);
                //Log.e("res", t.getMessage());
            }
        });
    }


    @Override
    public void onDeviceRegistered(String deviceToken) {

    }

    @Override
    public void onMessage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        JSONObject obj = null;
        int infoid = 0;
        int lastkomen = 0;
        String tipe = "";
        try
        {
            Map<String, String> params = remoteMessage.getData();
            obj = new JSONObject(params);

            String det = obj.getString("detail");
            JSONArray detail = new JSONArray(det);
            tipe = detail.getJSONObject(0).getString("tipe").toString();
            infoid = detail.getJSONObject(0).getInt("infoid");
            lastkomen = detail.getJSONObject(0).getInt("lastkomen");

        }
        catch (JsonParseException e)
        {
            //Log.e("ERRRO", e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.e("ERRROE", e.getMessage());
        }
        if((lastkomen > 0) && (!ISOWN))
            loadKomentar(infoid, lastkomen);

    }

    @Override
    public void onPlayServiceError() {

    }

    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
        Message message = (Message) packet;
        Jid from = message.getFrom();
        String body = message.getBody();

        try{
            InfoTipe object = new Gson().fromJson(body, InfoTipe.class);
            Log.e("komenStanza", "dapet " + body);
            if(object.mitraid.equals(getResources().getString(R.string.CONF_MITRAID)) && (object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_KOMENTAR))) {

                if(object.komendata instanceof KomenModel)
                {
                    KomenModel komen = object.komendata;
                    if (info_id == komen.infoid) {
                        loadKomentar(info_id, komen.idbase);
                    }
                }else {
                    if (object.detail.size() > 0) {
                        InfoItem detil = object.detail.get(0);
                        if (detil.tipe.toLowerCase().equals("komentar")) {
                            if (Integer.valueOf(_session.getLastInfoId()) == detil.infoid) {
                                loadKomentar(info_id, detil.lastkomen);
                            }
                        }
                    }
                }

            }
        }
        catch (Exception ex)
        {

        }
    }


    private class KomenAdapter extends BaseAdapter
    {

        Context _context;
        ArrayList<KomenModel> arrayAdapter = new ArrayList<>();
        String tipe_info = "";
        private String expandText = " View More";
        public KomenAdapter(Context _ctx)
        {
            super();
            _context = _ctx;
        }

        public void addNewItem(ArrayList<KomenModel> model, String tipe)
        {
            tipe_info = tipe;
            Constant.debug("addIem","ada "+model.size());

            if(arrayAdapter == null)
                arrayAdapter = new ArrayList<>(model);
            else
                arrayAdapter.addAll(model);

            notifyDataSetChanged();
        }

        public void addNewItem(KomenModel model, String tipe)
        {
            tipe_info = tipe;

            if(arrayAdapter != null)
                arrayAdapter.add(model);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return arrayAdapter.size();
        }

        @Override
        public KomenModel getItem(int i) {
            return arrayAdapter.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void setMoreView(int position, boolean value)
        {
            getItem(position).viewMore = value;
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            viewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(_context).inflate(R.layout.komentar_item, null);
                holder = new viewHolder(view);
                view.setTag(holder);
            } else {
                holder = (viewHolder) view.getTag();
            }

            if (getItem(i).viewMore == null) getItem(i).viewMore = false;

            holder.txtTanggal.setText((getItem(i).tglkirim == null) ? "" : Constant.parseDate(getItem(i).tglkirim));
            String description = ArabicUtilities.reshape(getItem(i).pengirim + " " + getItem(i).isi);
            SpannableString s3;
            if (description.length() > 200)
            {
                String text=description.substring(0,200)+"...";
                s3 = new SpannableString(text + expandText);
            }
            else
            {
                s3 = new SpannableString(description);
            }

            final SpannableString ss1 = new SpannableString(description+" View Less");
            if(tipe_info.equals("Publik")) {
                s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else if(tipe_info.equals("Info")) {
                s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.info_color)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.info_color)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            else
            {
                s3.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.konsul_color)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                s3.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.konsul_color)), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss1.setSpan(new RelativeSizeSpan(1f), 0, getItem(i).pengirim.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            SpannableStringBuilder builder = new SpannableStringBuilder();
            if(description.length() > 200)
            {
                ClickableSpan span1 = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        setMoreView(i, true);
                    }
                    public void updateDrawState(TextPaint ds) {// override updateDrawState
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };
                ClickableSpan span2 = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        setMoreView(i, false);

                    }

                    public void updateDrawState(TextPaint ds) {// override updateDrawState
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };

                if(!getItem(i).viewMore)
                {

                    s3.setSpan(span1, 203, 203 + expandText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s3.setSpan(new ForegroundColorSpan(Color.BLUE), 203, 203 + expandText.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.append(s3);
                    holder.txtDescription.setText(builder);
                    holder.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

                }
                else
                {

                    ss1.setSpan(span2, description.length(), ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss1.setSpan(new ForegroundColorSpan(Color.BLUE), description.length(), ss1.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    builder.append(ss1);
                    holder.txtDescription.setText(builder);
                    holder.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
                }

            }
            else
            {
                //holder.txtDescription.setText(description);
                builder.append(s3);
                holder.txtDescription.setText(builder);
            }

            holder.txtDescription.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
            //Linkify.addLinks(holder.txtDescription, Linkify.WEB_URLS);
            return view;
        }
    }

    public class viewHolder
    {
        @BindView(R.id.txtTanggal)
        public TextView txtTanggal;

        @BindView(R.id.txtDescription)
        public TextView txtDescription;

        public viewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }
}
