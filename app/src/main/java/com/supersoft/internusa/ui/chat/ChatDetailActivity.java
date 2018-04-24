package com.supersoft.internusa.ui.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.bottomsheet.BottomSheet;
import com.supersoft.internusa.helper.imagePicker.AlbumSelectActivity;
import com.supersoft.internusa.helper.imagePicker.Image;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.services.xmpp.XMPPService;
import com.supersoft.internusa.helper.text.DividerSpan;
import com.supersoft.internusa.helper.text.QuoteSpan;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.helper.widget.CopyTextView;
import com.supersoft.internusa.model.ChatGroupDetail;
import com.supersoft.internusa.model.ChatGroupModel;
import com.supersoft.internusa.model.ChatSingleModel;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.model.ProfilDB;

import org.greenrobot.eventbus.EventBus;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.jid.Jid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itclub21 on 12/6/2017.
 */

public class ChatDetailActivity extends AppCompatActivity implements MessageAdapterListener, StanzaListener {
    private static final int READ_STORAGE_PERMISSION = 4000;
    private static final int WRITE_STORAGE_PERMISSION = 4001;
    private static final int LIMIT = 5;
    private static final int CODE_CREATE_TOPIK = 56;
    @BindView(R.id.slyce_messaging_recycler_view)RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.pgBar)ProgressBar pgBar;
    @BindView(R.id.txtMessage)EditText txtMessage;
    @BindView(R.id.imgAddNew)ImageView imgAddNew;
    @BindView(R.id.imgSend)ImageView imgSend;
    @BindView(R.id.toolbar_title)TextView toolbar_title;

    @BindView(R.id.rlJoin)RelativeLayout rlJoin;
    @BindView(R.id.rlTypeChat)RelativeLayout rlTypeChat;

    int checkedCount = 0;
    DBHelper db;
    ProfilDB profil;
    Session _session;
    private recycleAdapter mRecyclerAdapter;
    ArrayList<ChatGroupDetail> _model = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private Refresher mRefresher;
    private int LOCAL_ID = 0;
    private String CHAT_GROUP_IDBASE = "";
    ChatGroupModel tblTopik;
    private XMPPService mXMPPService;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_detail_activity);
        getWindow().setBackgroundDrawableResource(R.drawable.background_hd);
        if(mXMPPService == null) mXMPPService = XMPPService.getInstance(this);
        if(mXMPPService.shouldUseXmppConnection())
            mXMPPService.getConnection().addAsyncStanzaListener(this, MessageTypeFilter.NORMAL_OR_CHAT);

        ButterKnife.bind(this);
        db = new DBHelper(this);
        profil = db.getProfilDb();
        _session = new Session(this);
        mRecyclerAdapter = new recycleAdapter(this, _model, this);

        initToolbar();

        mLinearLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return !mRefresher.isRefreshing();
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        };
        mLinearLayoutManager.setStackFromEnd(true);

        // Setup recycler view
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return mRefresher.isRefreshing();
                    }
                }
        );
        tblTopik = new ChatGroupModel();
        mRefresher = new Refresher(false);
        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            LOCAL_ID = extras.getInt("LOCAL_ID",0);
            CHAT_GROUP_IDBASE = extras.getString("GROUP_ID");
            db.resetUnreadChat(CHAT_GROUP_IDBASE);
            _session.setGroupIdOpened(CHAT_GROUP_IDBASE);
            tblTopik = db.getSingleChatgorupByIdbase(CHAT_GROUP_IDBASE).get(0);
        }

        _model = db.getChatgorupDetail(CHAT_GROUP_IDBASE);
        mRecyclerAdapter.updateMessageItemDataList(_model);


        if(!tblTopik.JOINGROUP){
            rlJoin.setVisibility(View.VISIBLE);
            rlTypeChat.setVisibility(View.GONE);
        }
        else
        {
            rlJoin.setVisibility(View.GONE);
            rlTypeChat.setVisibility(View.VISIBLE);
        }

        if(_session.getMemid().equals(Integer.valueOf(tblTopik.CREATORID)))
        {
            rlJoin.setVisibility(View.GONE);
            rlTypeChat.setVisibility(View.VISIBLE);
            ChatGroupModel groupModel = new ChatGroupModel();
            groupModel.JOINGROUP = true;
            groupModel.IDBASE = CHAT_GROUP_IDBASE;
            db.updateJoinChatGroup(groupModel);
        }

        toolbar_title.setText(tblTopik.TOPIK);
        imgSend.setEnabled(false);
        txtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    imgSend.setEnabled(false);
                } else {
                    imgSend.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(Build.VERSION.SDK_INT >= 11)
        {
            mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int rigth, int bottom,
                                           int oldleft, int oldtop, int oldright, int oldBottom) {
                    if(bottom < oldBottom)
                    {
                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                            }
                        }, 100);
                    }
                }
            });
        }


    }

    public void onResume()
    {
        super.onResume();
        _session.setGroupDetailActivityIsOpen(true);

    }

    public void onPause()
    {
        super.onPause();
        _session.setGroupDetailActivityIsOpen(false);

    }

    public void onDestroy()
    {
        super.onDestroy();
        _session.setGroupDetailActivityIsOpen(false);
        _session.setGroupIdOpened("");

    }


    @Optional
    @OnClick({R.id.imgSend, R.id.btnJoin, R.id.imgAddNew})
    public void onSend(View resId)
    {
        switch (resId.getId())
        {
            case R.id.imgSend:
                ChatGroupDetail message = new ChatGroupDetail();
                message.TYPE = 3; //
                message.CREATEDATE = Constant.currDate();
                message.LASTMESSAGE = txtMessage.getText().toString();
                message.GROUPID = String.valueOf(CHAT_GROUP_IDBASE);
                message.REPLAYID = 0;
                message.IDCHAT = 0;
                message.URIPATH = "";
                message.DOWNLOADPATH = "";
                message.CREATORNAME = (profil.fullname == null) ? profil.hp : profil.fullname;
                message.CREATORID = String.valueOf(_session.getMemid());
                message.ISREAD = 0;

                long ID = db.insertChatGroupDetail("ChatDetailOnsend", true, message);
                _model.add(message);
                mRecyclerAdapter.updateMessageItemDataList(_model);
                List<String> listObj = new ArrayList<>();
                listObj.add("update_last_komen");
                listObj.add(CHAT_GROUP_IDBASE);
                listObj.add(txtMessage.getText().toString());
                createGroup(String.valueOf(CHAT_GROUP_IDBASE), String.valueOf(LOCAL_ID), txtMessage.getText().toString());
                txtMessage.setText("");
                EventBus.getDefault().post(listObj);
                break;
            case R.id.btnJoin:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroup(CHAT_GROUP_IDBASE, String.valueOf(LOCAL_ID));
                    }
                });

                break;
            case R.id.imgAddNew:

                new BottomSheet.Builder(this, R.style.BottomSheet_Dialog)
                        .grid() // <-- important part
                        .sheet(R.menu.menu_bottom_add_attach_chat_detail)
                        .listener(bsListener).show();
                break;
        }

    }

    private void initToolbar()
    {
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(getApplicationContext().getResources().getDrawable(R.drawable.ic_back_home_white));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    finish();
                }
            });
        }
    }

    DialogInterface.OnClickListener bsListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int hwich) {
            switch (hwich)
            {
                case R.id.att_camera:
                    break;
                case R.id.att_image:
                    readImageFromExternal();
                    break;
                case R.id.att_document:
                    break;
                case R.id.att_audio:
                    break;
                case R.id.att_video:
                    break;
                case R.id.att_location:
                    break;
            }
        }
    };

    public void readImageFromExternal()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Constant.checkPermissionForExternalStorage(this)) {
                Constant.requestStoragePermission(this, READ_STORAGE_PERMISSION);
            } else {
                Intent intent = new Intent(this, AlbumSelectActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
                startActivityForResult(intent, Constant.REQUEST_CODE);
            }
        }else{
            Intent intent = new Intent(this, AlbumSelectActivity.class);
            intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
            startActivityForResult(intent, Constant.REQUEST_CODE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<Image> image_uris = data.getParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES);
            if(image_uris.size() > 0){
                for(int i=0; i < image_uris.size(); i++)
                {
                    ChatGroupDetail message = new ChatGroupDetail();
                    message.TYPE = 2; //
                    message.CREATEDATE = Constant.currDate();
                    message.LASTMESSAGE = "kirim image";
                    message.URIPATH = "";
                    message.DOWNLOADPATH = image_uris.get(i).path;
                    message.GROUPID = String.valueOf(CHAT_GROUP_IDBASE);
                    message.REPLAYID = 0;
                    message.IDCHAT = 0;
                    message.CREATORNAME = (profil.fullname == null) ? profil.hp : profil.fullname;
                    message.CREATORID = String.valueOf(_session.getMemid());
                    message.ISREAD = 0;
                    long ID = db.insertChatGroupDetail("ChatDetailOnsend",true, message);
                    _model.add(message);
                    mRecyclerAdapter.updateMessageItemDataList(_model);
                }
                /*
                view_selected_photos_container.setVisibility(View.VISIBLE);

                grdAdapter = new thumbnailAdapter(getActivity(), image_uris, new AdapterCallback() {
                    @Override
                    public void removeImage(View view) {
                        Image img = (Image)view.getTag();
                        image_uris.remove(img);
                        grdAdapter.updateItems(image_uris);
                        if (image_uris.size() == 0) {
                            view_selected_photos_container.setVisibility(View.GONE);
                        }
                        grdAdapter.notifyDataSetChanged();
                    }
                });
                grdAdapter.updateItems(image_uris);
                grdAdapter.notifyDataSetChanged();
                aGridView.setAdapter(grdAdapter);
                //vwHeaderHandler.txtTotalImage.setText(grdAdapter.getItemCount() + " / " + LIMIT + " Gambar");
                */
            }
        }
    }

    private void createGroup(String groupid, final String localid, String message)
    {
        ProfilDB profil = new DBHelper(this).getProfilDb();
        Map<String, String> data = new HashMap<>();
        data.put("groupid", groupid);
        data.put("hp", profil.hp);
        data.put("mitraid", getResources().getString(R.string.CONF_MITRAID));
        data.put("data", message);
        data.put("localid", localid);

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/{fungsi}"
        Call<JsonObject> call = api.create_chat_by_topik(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "create_chat_by_topik"),data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                //Constant.debug("response", new Gson().toJson(response));
                if(response.isSuccessful())
                {
                    if(response != null)
                    {
                        int status = response.body().get("status").getAsInt();
                        if(status == 1) {
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }


    private void joinGroup(String groupid, final String localid)
    {
        pgBar.setVisibility(View.VISIBLE);
        ProfilDB profil = new DBHelper(this).getProfilDb();
        Map<String, String> data = new HashMap<>();
        data.put("groupid", groupid);
        data.put("hp", profil.hp);
        data.put("mitraid", getResources().getString(R.string.CONF_MITRAID));
        data.put("localid", localid);

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = api.create_chat_by_topik(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "join_topik"),data);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Constant.debug("response", new Gson().toJson(response.body()));
                pgBar.setVisibility(View.GONE);
                if(response.isSuccessful())
                {
                    if(response != null)
                    {
                        int status = response.body().get("status").getAsInt();
                        if(status == 1) {
                            ChatGroupModel groupModel = new ChatGroupModel();
                            groupModel.JOINGROUP = true;
                            groupModel.IDBASE = CHAT_GROUP_IDBASE;
                            db.updateJoinChatGroup(groupModel);

                            JsonArray isArrayRows = response.body().get("rows").getAsJsonArray();
                            if(isArrayRows.size() > 0)
                            {
                                for(int i=0; i < isArrayRows.size(); i++)
                                {
                                    JsonObject objects = isArrayRows.get(i).getAsJsonObject();
                                    ChatSingleModel object = new Gson().fromJson(objects, ChatSingleModel.class);
                                    db.insertChatGroupDetailByIDCHAT("cdetail",false, new ChatGroupDetail(object, 0));
                                    ChatGroupDetail chat = new ChatGroupDetail(object, 0);
                                    _model.add(chat);
                                }
                                mRecyclerAdapter.updateMessageItemDataList(_model);
                            }

                            rlJoin.setVisibility(View.GONE);
                            rlTypeChat.setVisibility(View.VISIBLE);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pgBar.setVisibility(View.GONE);
                Log.e("Err", t.getMessage());
            }
        });
    }

    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {

    }

    @Override
    public void processStanza(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
        Message message = (Message) packet;
        Jid from = message.getFrom();
        String body = message.getBody();
        Log.e("processStanza", "msg : " + body);


        try
        {
            InfoTipe object = new Gson().fromJson(body, InfoTipe.class);
            if(object.mitraid.equals(getResources().getString(R.string.CONF_MITRAID))) {
                if((object.id.length() > 2) && (object.id.substring(0,2).equals(Constant.NOTIF_KODE_CREATE_TOPIK)) ) // broadcast group
                {
                    Object chat = object.chatdata;
                    if(chat instanceof ChatSingleModel) {
                        ChatSingleModel cht = (ChatSingleModel) chat;
                        if(!_session.getMemid().equals(Integer.valueOf(cht.mem_id))) {
                            ChatGroupDetail item = new ChatGroupDetail(cht, 0);
                            db.resetUnreadChat(CHAT_GROUP_IDBASE);
                            _model.add(item);
                            mRecyclerAdapter.updateMessageItemDataList(_model);
                        }
                    }
                }
            }
        }catch (Exception ex)
        {

        }

    }

    public class recycleAdapter extends RecyclerView.Adapter<MessageViewHolder> implements CopyTextView.CopyHandler
    {
        ArrayList<ChatGroupDetail> _model = new ArrayList<>();
        private Context mContext;

        private static final int SENT = 0;
        private static final int RECEIVED = 1;
        private static final int STATUS = 2;
        private static final int DATE_SEPARATOR = 3;

        private com.supersoft.internusa.ui.chat.MessageAdapterListener listener;
        private SparseBooleanArray selectedItems;

        // array used to perform multiple animation at once
        private SparseBooleanArray animationItemsIndex;
        private boolean reverseAllAnimations = false;

        // index is used to animate only the selected row
        // dirty fix, find a better solution
        private int currentSelectedIndex = -1;


        public recycleAdapter(Context context, ArrayList<ChatGroupDetail> model, com.supersoft.internusa.ui.chat.MessageAdapterListener listener) {
            mContext = context;
            _model = model;
            this.listener = listener;
            selectedItems = new SparseBooleanArray();
            animationItemsIndex = new SparseBooleanArray();
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            MessageViewHolder viewHolder = null;

            MessageItemType messageItemType = MessageItemType.values()[viewType];
            switch (messageItemType) {
                case INCOMING_TEXT:
                    View incomingTextView = inflater.inflate(R.layout.item_message_incoming, parent, false);
                    viewHolder = new MessageIncomingViewHolder(incomingTextView);
                    break;
                case OUTGOING_TEXT:
                    View outgoingTextView = inflater.inflate(R.layout.item_message_outgoing, parent, false);
                    viewHolder = new MessageOngoingViewHolder(outgoingTextView);
                    break;
                case OUTGOING_MEDIA:
                    View outgoingMediaView = inflater.inflate(R.layout.item_message_outgoing_image, parent, false);
                    viewHolder = new MessageOngoingMediaViewHolder(outgoingMediaView);
                    break;
                case SEPARATE_DATE:
                    View sviewDate = inflater.inflate(R.layout.item_message_date, parent, false);
                    viewHolder = new MessageSparateDateViewHolder(sviewDate);
                    break;

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            if (holder == null) {
                return;
            }

            ChatGroupDetail messageItem = getMessageItemByPosition(position);
            if (messageItem != null) {
                MessageItemType messageItemType = MessageItemType.values()[messageItem.TYPE];
                switch (messageItemType)
                {
                    case INCOMING_TEXT:
                        MessageIncomingViewHolder incoming = (MessageIncomingViewHolder)holder;
                        incoming.txtMessage.setText(messageItem.LASTMESSAGE);
                        incoming.txtSender.setText(messageItem.CREATORNAME);
                        incoming.txtDate.setText(Constant.parseTime(Constant.convertStringtoDate(messageItem.CREATEDATE)));
                        break;
                    case OUTGOING_TEXT:
                        MessageOngoingViewHolder outgoing = (MessageOngoingViewHolder)holder;
                        outgoing.txtMessage.setText(messageItem.LASTMESSAGE);
                        outgoing.txtDate.setText(Constant.parseTime(Constant.convertStringtoDate(messageItem.CREATEDATE)));
                        break;
                    case SEPARATE_DATE:
                        MessageSparateDateViewHolder sparate = (MessageSparateDateViewHolder)holder;
                        sparate.txtMessage.setText(messageItem.CREATEDATE);
                        break;
                    case OUTGOING_MEDIA:
                        MessageOngoingMediaViewHolder mediaHolder = (MessageOngoingMediaViewHolder)holder;
                        mediaHolder.txtDate.setText(Constant.parseTime(Constant.convertStringtoDate(messageItem.CREATEDATE)));
                        Glide.with(mContext).load(messageItem.DOWNLOADPATH).dontAnimate().into(mediaHolder.imgLogo);
                        break;
                }
                //messageItem.buildMessageItem(MessageViewHolder);
            }

            applyClickEvents(holder, position);
        }

        private void applyClickEvents(MessageViewHolder holder, final int position) {

            /*
            holder.messageContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onMessageRowClicked(position);
                }
            });

            holder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onRowLongClicked(position);
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    return true;
                }
            });
            */
        }

        @Override
        public int getItemCount() {
            return _model != null ? _model.size() : 0;
        }

        @Override
        public int getItemViewType(int position) {

            // Get the item type
            Integer itemType = getMessageItemType(position);
            if (itemType != null) {
                return itemType;
            }

            return super.getItemViewType(position);
        }

        private ChatGroupDetail getMessageItemByPosition(int position) {
            if (_model != null && !_model.isEmpty()) {
                if (position >= 0 && position < _model.size()) {
                    ChatGroupDetail messageItem = _model.get(position);
                    if (messageItem != null) {
                        return messageItem;
                    }
                }
            }

            return null;
        }

        private Integer getMessageItemType(int position) {
            ChatGroupDetail messageItem = getMessageItemByPosition(position);
            if (messageItem != null) {
                return messageItem.TYPE;
            }

            return null;
        }


        public void updateMessageItemDataList(final ArrayList<ChatGroupDetail> messageItems) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _model = messageItems;
                    mRecyclerView.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
                    notifyDataSetChanged();
                }
            });

        }

        private String transformText(CharSequence text, int start, int end, boolean forCopy) {
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            Object copySpan = new Object();
            builder.setSpan(copySpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            DividerSpan[] dividerSpans = builder.getSpans(0, builder.length(), DividerSpan.class);
            for (DividerSpan dividerSpan : dividerSpans) {
                builder.replace(builder.getSpanStart(dividerSpan), builder.getSpanEnd(dividerSpan),
                        dividerSpan.isLarge() ? "\n\n" : "\n");
            }
            start = builder.getSpanStart(copySpan);
            end = builder.getSpanEnd(copySpan);
            if (start == -1 || end == -1) return "";
            builder = new SpannableStringBuilder(builder, start, end);
            if (forCopy) {
                QuoteSpan[] quoteSpans = builder.getSpans(0, builder.length(), QuoteSpan.class);
                for (QuoteSpan quoteSpan : quoteSpans) {
                    builder.insert(builder.getSpanStart(quoteSpan), "> ");
                }
            }
            return builder.toString();
        }

        @Override
        public String transformTextForCopy(CharSequence text, int start, int end) {
            if (text instanceof Spanned) {
                return transformText(text, start, end, true);
            } else {
                return text.toString().substring(start, end);
            }
        }


    }


}
