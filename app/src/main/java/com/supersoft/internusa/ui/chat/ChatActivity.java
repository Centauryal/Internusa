package com.supersoft.internusa.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.badgeview.BadgeTabLayout;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ChatGroupModel;
import com.supersoft.internusa.model.ChatSingleModel;
import com.supersoft.internusa.model.ProfilDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MessageAdapterListener {
    private static final int CODE_CREATE_TOPIK = 56;
    private static final int CODE_SINGLE_CHAT = 57;
    @BindView(R.id.lvwChat)RecyclerView lvwChat;
    @BindView(R.id.swipe_refresh_layout)SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.btnCreate)FloatingActionButton btnCreate;
    @BindView(R.id.boxInfo)LinearLayout boxInfo;
    @BindView(R.id.btnRefresh)Button btnRefresh;
    @BindView(R.id.pgBar)ProgressBar pgBar;
    int checkedCount = 0;
    DBHelper db;
    ProfilDB profil;
    ChatMessageAdapter mAdapter;
    Session _session;
    ArrayList<ChatGroupModel> _model = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private int tmpColor = -1;
    View view;
    File imageFile;
    private static BadgeTabLayout mTabs;

    public static ChatActivity newInstance(BadgeTabLayout tblayout){
        mTabs = tblayout;
        ChatActivity fragment = new ChatActivity();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.chat_activity,null);
        ButterKnife.bind(this, view);
        db = new DBHelper(getActivity());
        _session = new Session(getActivity());
        profil = db.getProfilDb();
        _model = db.getChatgorup();
        boxInfo.setVisibility((_model.size() <= 1) ? View.VISIBLE : View.GONE);

        EventBus.getDefault().register(this);
        //swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new ChatMessageAdapter(getActivity(), _model, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        lvwChat.setLayoutManager(mLayoutManager);
        lvwChat.setItemAnimator(new DefaultItemAnimator());
        lvwChat.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        lvwChat.setAdapter(mAdapter);

        actionModeCallback = new ActionModeCallback();

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        getPriority("get_prioriti_topik");
                    }
                }
        );
        return view;
    }

    @OnClick({R.id.btnCreate, R.id.btnRefresh})
    public void btnCreate(View view)
    {
        switch(view.getId())
        {
            case R.id.btnCreate:
                Intent topik = new Intent(getActivity(), ChatTopicActivity.class);
                startActivityForResult(topik, CODE_CREATE_TOPIK);
                break;
            case R.id.btnRefresh:
                btnRefresh.setVisibility(View.GONE);
                getPriority("get_list_all_topik");
                break;
        }

    }

    public void onResume()
    {
        super.onResume();
        _session.setGroupActivityIsOpen(true);

    }

    public void onPause()
    {
        super.onPause();
        _session.setGroupActivityIsOpen(false);

    }

    public void onDestroy()
    {
        super.onDestroy();
        _session.setGroupActivityIsOpen(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscribe(Object obj)
    {
        if(obj instanceof ChatSingleModel)
        {
            ChatSingleModel chat = (ChatSingleModel) obj;
            if(chat.jenis.equals("create_topik"))
            {
                if(Integer.valueOf(chat.mem_id) != _session.getMemid()) {
                    boolean found = false;
                    mAdapter.resetAnimationIndex();
                    List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        ChatGroupModel detail = _model.get(selectedItemPositions.get(i));
                        Log.e("create topik", "IDBASE : " + detail.IDBASE + " == " + chat.groupid);
                        if (detail.IDBASE.equals(chat.groupid)) {
                            detail.ISUNREAD = db.countUnreadChat(chat.groupid);
                            detail.NAMAPENGIRIMTERAKHIR = chat.namapengirim;
                            detail.LASTMESSAGE = chat.message;
                            _model.set(selectedItemPositions.get(i), detail);
                            mAdapter.notifyDataSetChanged();
                            found = true;
                            return;
                        }
                    }
                    Constant.debug("found", "ketemu : " + found);
                    if (!found) {
                        ArrayList<ChatGroupModel> chatModel = db.getSingleChatgorupByIdbase(chat.groupid);
                        try {
                            _model.add(chatModel.get(0));
                            mAdapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {

                        }

                    }
                }
            }
            else if(chat.jenis.equals("chat_topik"))
            {
                Constant.debug("jenisSubs", "chat_topik");
                if(Integer.valueOf(chat.mem_id) != _session.getMemid()) {
                    mAdapter.resetAnimationIndex();
                    for (int i = _model.size() - 1; i >= 0; i--) {
                        ChatGroupModel detail = _model.get(i);
                        if (detail.IDBASE.equals(chat.groupid)) {
                            Constant.debug("found ", "ketemu OK : " + detail.IDBASE + " = " + chat.groupid);
                            detail.ISUNREAD = db.countUnreadChat(chat.groupid);
                            detail.NAMAPENGIRIMTERAKHIR = chat.namapengirim;
                            detail.LASTMESSAGE = chat.message;
                            _model.set(i, detail);
                            mAdapter.notifyDataSetChanged();
                            return;
                        }
                    }

                }
            }
        }
        else if (obj instanceof List)
        {
            if(((List)obj).size() == 3 && ((List)obj).get(0).equals("update_last_komen"))
            {
                String CHAT_GROUP_IDBASE = ((List)obj).get(1).toString();
                String MESSAGE = ((List)obj).get(2).toString();
                ChatGroupModel tmpObject = new ChatGroupModel();
                for (int i = _model.size() - 1; i >= 0; i--) {
                    ChatGroupModel detail = _model.get(i);

                    if (detail.IDBASE.equals(CHAT_GROUP_IDBASE)) {
                        detail.NAMAPENGIRIMTERAKHIR = (profil.fullname == null) ? profil.hp : profil.fullname;
                        detail.LASTMESSAGE = MESSAGE;
                        _model.set(i, detail);
                        tmpObject = detail;
                        _model.remove(detail);
                        _model.add(1, tmpObject);
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
        else  if  (obj instanceof Map)
        {

            Map<String, String> topik = (Map<String, String>) obj;
            Constant.debug("instanceofmap", "MAP action " + topik.get("action"));
            if(topik.get("action").equals("CREATE_TOPIK"))
            {
                tmpColor = Constant.getRandomMaterialColor(getActivity(), "400");
                ChatGroupModel chatGroup = new ChatGroupModel();
                chatGroup.LASTMESSAGE = "";
                chatGroup.TOPIK = topik.get("data").toString();
                chatGroup.CREATORNAME = (profil.fullname == null) ? profil.hp : profil.fullname;
                chatGroup.NAMAPENGIRIMTERAKHIR = (profil.fullname == null) ? profil.hp : profil.fullname;
                chatGroup.CREATORID = String.valueOf(_session.getMemid());
                chatGroup.CREATEDATE = Constant.currDate();
                chatGroup.COLOR = tmpColor;
                chatGroup.IMPORTANT = false;
                chatGroup.STATUS = "queue";
                chatGroup.ISUNREAD = 0;
                chatGroup.IDBASE = Constant.getCurrentTimeStampTopikID();
                if(topik.get("avatartopik").equals("")){
                    imageFile = new File(topik.get("avatartopik").toString());
                    chatGroup.AVATAR = topik.get("avatartopik").toString();
                }

                ArrayList<ChatGroupModel> model = new ArrayList<>();
                model.add(chatGroup);
                long resultId = db.insertChatGroup(chatGroup);
                chatGroup.ID = String.valueOf(resultId);

                _model.add(chatGroup);
                mAdapter.notifyDataSetChanged();
                if(resultId > 0)
                {
                    createGroup(chatGroup.IDBASE, String.valueOf(resultId), String.valueOf(tmpColor), chatGroup.TOPIK, chatGroup.AVATAR);
                }
            }
        }
    }


    private void createGroup(String groupid, final String localid, String color, String message, String avatar)
    {
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();
        Map<String, RequestBody> data = new HashMap<>();
        boolean useIconTopik = false;

        MultipartBody.Part body = null;
        try{
            if(imageFile.exists() && imageFile != null)
            {
                useIconTopik = true;
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
                body = MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);
            }
        }catch (NullPointerException e){}


        data.put("groupid", RequestBody.create(MediaType.parse("multipart/form-data"), groupid));
        data.put("hp", RequestBody.create(MediaType.parse("multipart/form-data"), profil.hp));
        data.put("mitraid", RequestBody.create(MediaType.parse("multipart/form-data"), getResources().getString(R.string.CONF_MITRAID)));
        data.put("data", RequestBody.create(MediaType.parse("multipart/form-data"), message));
        data.put("localid", RequestBody.create(MediaType.parse("multipart/form-data"), localid));
        data.put("color", RequestBody.create(MediaType.parse("multipart/form-data"), color));

        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/create_topik_chat"
        String url = String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "create_topik_chat");
        Call<JsonObject> call = (useIconTopik) ? api.create_topik_chat(url, data, body) : api.create_topik_chat(url, data);
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
                            ChatGroupModel chatGroup = new ChatGroupModel(response.body(), "create_group");

                            long resultId = db.insertChatGroup(chatGroup);

                            if (resultId == 1) {
                                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                                    ChatGroupModel item = _model.get(i);
                                    if (item.ID.equals(chatGroup.ID)) {
                                        mAdapter.updateItem(chatGroup, i);
                                        return;
                                    }
                                }
                            }

                        }
                        else Toast.makeText(getActivity(), "Status fail", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //Constant.debug("response", t.toString());
            }
        });
    }


    private void getPriority(final String action)
    {
        pgBar.setVisibility(View.VISIBLE);
        ProfilDB profil = new DBHelper(getActivity()).getProfilDb();
        Map<String, String> data = new HashMap<>();
        data.put("hp", profil.hp);
        data.put("mitraid", getResources().getString(R.string.CONF_MITRAID));

        //Constant.debug("getPriority", new Gson().toJson(data));
        RetrofitBuilder builder = new RetrofitBuilder();
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);

        Call<JsonArray> call = api.getPriority(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, action),data);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                //Constant.debug("getPriority", new Gson().toJson(response.body()));
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                pgBar.setVisibility(View.GONE);
                if(response.isSuccessful())
                {
                    if(response != null)
                    {
                        JsonArray arrData = response.body().getAsJsonArray();
                        if(arrData.size() > 0)
                        {

                            if(action.equals("get_prioriti_topik"))
                            {
                                for(int i=0; i < arrData.size(); i++)
                                {
                                    JsonObject obj = arrData.get(i).getAsJsonObject();
                                    ChatGroupModel chatGroup = new ChatGroupModel(obj, "priority");
                                    long resultId = db.insertChatGroupByIdBase(chatGroup);
                                }
                            }
                            else
                            {
                                boxInfo.setVisibility(View.GONE);
                                for(int i=0; i < arrData.size(); i++)
                                {
                                    JsonObject obj = arrData.get(i).getAsJsonObject();
                                    if(!obj.get("mem_id").getAsString().equals("0"))
                                    {
                                        ChatGroupModel chatGroup = new ChatGroupModel(obj, "sync_group");
                                        long resultId = db.insertChatGroupByIdBase(chatGroup);
                                    }

                                }
                            }

                            _model = new ArrayList<>();
                            _model = db.getChatgorup();
                            mAdapter.resetData(_model);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Constant.debug("ERR",t.toString());
                pgBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("actiityResult", "code " + resultCode + " req " + requestCode);

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        new Runnable() {
            @Override
            public void run() {
                getPriority("get_prioriti_topik");
            }
        };
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);

        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        ChatGroupModel message = _model.get(position);
        message.IMPORTANT = !message.IMPORTANT;
        _model.set(position, message);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            ChatGroupModel message = _model.get(position);
            message.ISUNREAD = 0;
            _model.set(position, message);
            mAdapter.notifyDataSetChanged();

            if((message.IDBASE != null) && (message.ID != null))
            {
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("LOCAL_ID", Integer.valueOf(message.ID));
                intent.putExtra("GROUP_ID", message.IDBASE);
                startActivityForResult(intent, CODE_SINGLE_CHAT);
            }

        }
    }

    @Override
    public void onRowLongClicked(int position) {
        enableActionMode(position);
    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements android.support.v7.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.chat_menu, menu);

            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.btnDelete:
                    deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {
            mAdapter.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            lvwChat.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            //mAdapter.removeData(selectedItemPositions.get(i));
            Constant.debug("deleteMessages", "position " + i);
            mAdapter.removeDataWithDb(db, selectedItemPositions.get(i));

        }
        mAdapter.notifyDataSetChanged();
    }
}