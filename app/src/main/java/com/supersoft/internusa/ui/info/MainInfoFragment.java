package com.supersoft.internusa.ui.info;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.supersoft.internusa.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.supersoft.internusa.StartApp;
import com.supersoft.internusa.helper.amazinglistview.InfiniteScrollListView;
import com.supersoft.internusa.helper.amazinglistview.InfiniteScrollListView.LoadingMode;
import com.supersoft.internusa.helper.amazinglistview.InfiniteScrollListView.StopPosition;
import com.supersoft.internusa.helper.exoplayer.widget.Container;
import com.supersoft.internusa.helper.imagePicker.AlbumSelectActivity;
import com.supersoft.internusa.helper.imagePicker.Image;
import com.supersoft.internusa.helper.retroiface.RetroBuilderInterface;
import com.supersoft.internusa.helper.retroiface.RetrofitBuilder;
import com.supersoft.internusa.helper.uploadservice.MultipartUploadRequest;
import com.supersoft.internusa.helper.uploadservice.ServerResponse;
import com.supersoft.internusa.helper.uploadservice.UploadInfo;
import com.supersoft.internusa.helper.uploadservice.UploadNotificationConfig;
import com.supersoft.internusa.helper.uploadservice.UploadService;
import com.supersoft.internusa.helper.uploadservice.UploadStatusDelegate;
import com.supersoft.internusa.helper.util.ArabicUtilities;
import com.supersoft.internusa.helper.util.BaseRecyclerViewAdapter;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.helper.util.SpacesItemDecoration;
import com.supersoft.internusa.helper.util.Util;
import com.supersoft.internusa.model.LikestatusModel;
import com.supersoft.internusa.model.ModelRequestInfo;
import com.supersoft.internusa.model.MsgEvent;
import com.supersoft.internusa.model.PojoProfilUsaha;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.model.Row;
import com.supersoft.internusa.model.Timelinetujuan;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static com.supersoft.internusa.ui.info.InfoFragmentAdapter.TYPE_ABU;
import static com.supersoft.internusa.ui.info.InfoFragmentAdapter.TYPE_GREEN;

/**
 * Created by itclub21 on 3/5/2017.
 */

public class MainInfoFragment extends Fragment implements UploadStatusDelegate, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener,SwipyRefreshLayout.OnRefreshListener {
    View view;

    InfiniteScrollListView lvwData;
    ModelRequestInfo inf;

    DBHelper _db;
    ProfilDB profil;
    ArrayList<Row> mAllResults = new ArrayList<>();
    InfoFragmentAdapter adapter;
    SwipyRefreshLayout swipyrefreshlayout;

    private static final int READ_STORAGE_PERMISSION = 4000;
    private static final int WRITE_STORAGE_PERMISSION = 4001;
    private static final int LIMIT = 5;
    protected RecyclerView aGridView;
    private boolean mRefreshing;
    private int mCurrentPage = 0;
    private static final int ITEMS_PER_PAGE = 10;

    FrameLayout view_selected_photos_container;
    thumbnailAdapter grdAdapter;
    ArrayList<Image> image_uris = new ArrayList<>();


    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private LocationManager locationManager;
    private static final String TAG = "MainActivity";
    String alamat = "";
    double gLatitude = 0.0D;
    double gLongitude = 0.0D;
    int LastID = 0;
    int onLike = 0;

    viewHeaderHandler vwHeaderHandler;
    private CoordinatorLayout coordinatorLayout;

    private Map<String, UploadProgressViewHolder> uploadProgressHolders = new HashMap<>();

    private ArrayList<Timelinetujuan.Tujuan> timeLineTujuan = new ArrayList<>();
    private ArrayList<Timelinetujuan.Tujuan> statusTimeline = new ArrayList<>();
    private String hintSelected = "";
    private String idStatusSelected = "";
    int idMemberGroup = 0;
    private LoadingMode loadingMode = LoadingMode.SCROLL_TO_BOTTOM;
    private StopPosition stopPosition = StopPosition.REMAIN_UNCHANGED;
    PagerSnapHelper snapHelper;
    LinePagerIndicatorDecoration pageIndicator;
    @BindView(R.id.ln_newinfo)LinearLayout ln_newinfo;

    public static MainInfoFragment newInstance(){
        MainInfoFragment fragment = new MainInfoFragment();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.info_main_fragment_layout,null);
        ButterKnife.bind(this, view);
        lvwData = view.findViewById(R.id.lvwData);
        View viewHeader = inflater.inflate(R.layout.header_list_info, null);
        lvwData.setLoadingView(getLayoutInflater().inflate(R.layout.loading_view, null));
        vwHeaderHandler = new viewHeaderHandler(viewHeader);
        snapHelper = new PagerSnapHelper();
        pageIndicator = new LinePagerIndicatorDecoration();
        swipyrefreshlayout = view.findViewById(R.id.SwipyRefreshLayout);
        swipyrefreshlayout.setOnRefreshListener(this);
        view_selected_photos_container = viewHeader.findViewById(R.id.view_selected_photos_container);
        aGridView = viewHeader.findViewById(R.id.rc_selected_photos);
        coordinatorLayout = view.findViewById(R.id.coordinatorLayout);

        _db = new DBHelper(getActivity());
        profil = _db.getProfilDb();
        adapter = new InfoFragmentAdapter(getActivity(), mAllResults);
        adapter.setPageListener(pageListener);
        LinearLayoutManager mLayoutManager_Linear = new LinearLayoutManager(getActivity());
        mLayoutManager_Linear.setOrientation(LinearLayoutManager.HORIZONTAL);
        aGridView.setLayoutManager(mLayoutManager_Linear);
        aGridView.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(getActivity(), 5), SpacesItemDecoration.TYPE_VERTICAL));
        aGridView.setHasFixedSize(true);

        aGridView.setAdapter(null);


        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);


        lvwData.addHeaderView(viewHeader);
        lvwData.setLoadingMode(loadingMode);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        lvwData.setLoadingView(layoutInflater.inflate(R.layout.loading_view, null));

        lvwData.setAdapter(adapter);

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            image_uris = data.getParcelableArrayListExtra(Constant.INTENT_EXTRA_IMAGES);
            if(image_uris.size() > 0){

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
            }
            else {
                view_selected_photos_container.setVisibility(View.GONE);
            }

        }
    }

    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);


        if (savedInstance != null) {
            inf = (ModelRequestInfo)savedInstance.getSerializable("REQUEST_MODEL");
            alamat = savedInstance.getString("alamat");
            //LastID = savedInstance.getInt("LASTID");
            mCurrentPage = savedInstance.getInt("INITIALPAGE");
            vwHeaderHandler.txtLocation.setText(alamat);
            try{
                mAllResults = (ArrayList<Row>)savedInstance.getSerializable(Constant.savedInstanceInfo);
                if(mAllResults.size() > 0){
                    adapter.clearEntries();
                    adapter.addEntriesToBottom(mAllResults);
                }
            }catch (NullPointerException e){

            }

            try
            {
                timeLineTujuan = (ArrayList<Timelinetujuan.Tujuan>)savedInstance.getSerializable("timelineTujuan");
                statusTimeline = (ArrayList<Timelinetujuan.Tujuan>)savedInstance.getSerializable("timelineStatus");
                vwHeaderHandler.txtDescription.setHint(savedInstance.getString("hintSelected").toString());
                setArrayStatusTimeline(timeLineTujuan,statusTimeline);

            }catch (NullPointerException e)
            {

            }
        }else {
            swipyrefreshlayout.post(new Runnable() {
                @Override
                public void run() {
                    swipyrefreshlayout.setRefreshing(true);
                    mRefreshing = true;
                    //getUsahaPage(mCurrentPage);
                    adapter.onScrollNext();
                    getTujuanTimeline();
                }
            });
        }


        if(StartApp.FoundNewTimeline)
        {
            ln_newinfo.setVisibility(View.VISIBLE);
        }
    }

    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void responseRobot(Object obje)
    {

        if(obje instanceof MsgEvent)
        {
            MsgEvent event = (MsgEvent)obje;

            if(event.getRowObject().equals("komentar")) {
                Row row = adapter.getItem(event.getPosition());
                row.totalkomentar = event.getKey();
                adapter.notifyDataSetChanged();
            }

            if(event.getRowObject().equals("likes")) {
                Row row = adapter.getItem(event.getPosition());
                row.totallikes = event.getKey();
                row.ownlike = (boolean)event.getObject();
                adapter.notifyDataSetChanged();
            }

            if(event.getRowObject().equals("newTimeline")) {
                if(LastID == 0) LastID = event.getPosition();
                if(!StartApp.ONCLICK_BUTTON_TIMELINE)
                    ln_newinfo.setVisibility(View.VISIBLE);

                StartApp.ONCLICK_BUTTON_TIMELINE = false;
            }
        }
        else if (obje instanceof LikestatusModel)
        {
            LikestatusModel liker = (LikestatusModel)obje;
            for (int i=0; i < adapter.getCount(); i++)
            {
                Row item = adapter.getItem(i);
                Log.e("updatLikeAdap", "Cehck info ID " + liker.infoid + " = " + item.getId());
                if(Integer.parseInt(item.getId()) == liker.infoid)
                {
                    if(onLike != liker.infoid) {
                        item.totallikes = String.valueOf(liker.totallike);
                        item.ownlike = false;
                        adapter.notifyDataSetChanged();
                    }
                    return;
                }
            }
            onLike = 0;
        }

    }

    @OnClick(R.id.ln_newinfo)
    public void onClick()
    {

        ln_newinfo.setVisibility(View.GONE);
        StartApp.FoundNewTimeline = false;
        StartApp.TotalNewTimeline = 0;
        adapter.onScrollNext();
    }

    InfoFragmentAdapter.NewPageListener pageListener = new InfoFragmentAdapter.NewPageListener() {
        @Override
        public void onScrollNext() {
            adapter.lock();

            int lastId = _db.LastTLid();
            if((adapter.getCount() == 0) && lastId > 0) {
                ArrayList<Row> datas = _db.getTimeline(0,0, 10000);

                Log.e("RoosterConnection", "local db timeline " + datas.size() + " last id base " + lastId);

                if(datas.size() > 0)
                {
                    swipyrefreshlayout.setRefreshing(false);
                    adapter.notifyHasMore();
                    if(LastID > 0)
                        adapter.addEntriesToTop(datas);
                    else
                    {
                        adapter.addEntriesToBottom(datas);
                    }
                    lvwData.requestLayout();
                    StartApp.LastID = lastId;
                    if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
                        switch(stopPosition) {
                            case REMAIN_UNCHANGED:
                                lvwData.setSelection(datas.size());
                                break;
                            case START_OF_LIST:
                                lvwData.setSelection(datas.size() < ITEMS_PER_PAGE ? 0 : 1);
                                break;
                            case END_OF_LIST:
                                lvwData.setSelection(1);
                                lvwData.smoothScrollToPosition(adapter.getCount());
                        }
                    } else {
                        if (stopPosition != StopPosition.REMAIN_UNCHANGED) {
                            lvwData.smoothScrollToPosition(stopPosition == StopPosition.START_OF_LIST ? 0 : (datas.size() < ITEMS_PER_PAGE ? adapter.getCount() : adapter.getCount() - 2));
                        }
                        else  if(LastID > 0)
                        {
                            lvwData.smoothScrollToPosition(0);
                        }
                    }

                }
                LastID = 0;
                return;
            }

            inf = new ModelRequestInfo();
            inf.hp = profil.hp;
            inf.deviceid = Constant.getUUID(getActivity());
            inf.mitraid = getActivity().getResources().getString(R.string.CONF_MITRAID);
            inf.limit = mCurrentPage;
            inf.lastid = lastId;//LastID;
            inf.offset = ITEMS_PER_PAGE;
            Log.e("onNext", new Gson().toJson(inf));
            RetrofitBuilder builder = new RetrofitBuilder("get_info");
            RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
            Call<PojoProfilUsaha> call = githubUserAPI.getProfilUsahaRekan(String.format(Constant.CONTROLLER_3S,Constant.CONTROLLER_DEV,"get_info","read",profil.hp),inf);
            call.enqueue(new Callback<PojoProfilUsaha>() {
                @Override
                public void onResponse(Call<PojoProfilUsaha> call, Response<PojoProfilUsaha> response) {
                    Constant.debug("OnNext", new Gson().toJson(response.body()));
                    swipyrefreshlayout.setRefreshing(false);
                    if(!response.isSuccessful()){
                        adapter.notifyEndOfList();
                        return;
                    }

                    PojoProfilUsaha pojos = response.body();
                    final ArrayList<Row> datas = new ArrayList<Row>(pojos.getRows());

                    if(datas.size() > 0)
                    {

                        adapter.notifyHasMore();
                        if(LastID > 0)
                            adapter.addEntriesToTop(datas);
                        else
                        {
                            mCurrentPage++;
                            adapter.addEntriesToBottom(datas);
                        }
                        lvwData.requestLayout();
                        //LastID = pojos.getLastId();
                        StartApp.LastID = pojos.getLastId();

                        if (loadingMode == LoadingMode.SCROLL_TO_TOP) {
                            switch(stopPosition) {
                                case REMAIN_UNCHANGED:
                                    lvwData.setSelection(datas.size());
                                    break;
                                case START_OF_LIST:
                                    lvwData.setSelection(datas.size() < ITEMS_PER_PAGE ? 0 : 1);
                                    break;
                                case END_OF_LIST:
                                    lvwData.setSelection(1);
                                    lvwData.smoothScrollToPosition(adapter.getCount());
                            }
                        } else {
                            if (stopPosition != StopPosition.REMAIN_UNCHANGED) {
                                lvwData.smoothScrollToPosition(stopPosition == StopPosition.START_OF_LIST ? 0 : (datas.size() < ITEMS_PER_PAGE ? adapter.getCount() : adapter.getCount() - 2));
                            }
                            else  if(LastID > 0)
                            {
                                lvwData.smoothScrollToPosition(0);
                            }
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int o=0; o < datas.size(); o++) {
                                    Log.e("mainInfoFrag", "data ke " + o);
                                    _db.insertTimeline(datas.get(o));
                                }
                            }
                        });

                    }
                    else
                        adapter.notifyEndOfList();

                    LastID = 0;
                }

                @Override
                public void onFailure(Call<PojoProfilUsaha> call, Throwable t) {
                    Constant.debug("onNext", t.getMessage());
                    swipyrefreshlayout.setRefreshing(false);
                    adapter.notifyEndOfList();
                }
            });
        }

        @Override
        public View getInfiniteScrollListView(final int position, View convertView, ViewGroup parent) {
            final Row item = adapter.getItem(position);
            final holder _holder;
            View v = convertView;
            int listViewItemType = adapter.getItemViewType(position);
            if(v == null){
                if(listViewItemType == TYPE_ABU)
                    v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_info, null);
                else if(listViewItemType == TYPE_GREEN)
                    v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_info_green, null);
                else
                    v = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_info_orange, null);

                _holder = new holder(v);
                v.setTag(_holder);
            }else {
                _holder = (holder)v.getTag();
            }
            Constant.loadDefaultAvatar(getActivity(), item.getAvatar(), _holder.imgAvatar);
            _holder.txtFullname.setText(item.pengirim);
            _holder.txtFullname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent profilRekan = new Intent(getActivity(), DetailprofilrekanActivity.class);
                    profilRekan.putExtra("TELP", item.telp);
                    getActivity().startActivity(profilRekan);
                }
            });
            _holder.txtTanggal.setText((item.tglkirim == null) ? "Just now" : Constant.parseDate(item.tglkirim));
            _holder.txtStatus.setText(Html.fromHtml("status <b>" + item.getStatus() + "<b>"));
            if (item.onViewMore == null) item.onViewMore  = false;
            String isi = ArabicUtilities.reshape(item.isi);
            if(isi.length() > 200)
            {
                String text=isi.substring(0,200)+"...";
                final String fulltext = isi;

                //Log.e("spann pos " + position, (item.onViewMore) ? "true" : "false");

                final SpannableString ss = new SpannableString(text + InfoFragmentAdapter.expandText);
                final SpannableString ss1 = new SpannableString(fulltext+" View Less");

                ClickableSpan span1 = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        adapter.setMoreView(position, true);
                    }
                    public void updateDrawState(TextPaint ds) {// override updateDrawState
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };

                ClickableSpan span2 = new ClickableSpan() {
                    @Override
                    public void onClick(View textView) {
                        adapter.setMoreView(position, false);

                    }

                    public void updateDrawState(TextPaint ds) {// override updateDrawState
                        ds.setUnderlineText(false); // set to false to remove underline
                    }
                };

                if(!item.onViewMore)
                {
                    ss.setSpan(span1, 203, 203 + InfoFragmentAdapter.expandText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss.setSpan(new ForegroundColorSpan(Color.BLUE), 203, 203 + InfoFragmentAdapter.expandText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    _holder.txtDescription.setText(ss);
                    _holder.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());

                }
                else
                {
                    ss1.setSpan(span2, fulltext.length(), ss1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ss1.setSpan(new ForegroundColorSpan(Color.BLUE), fulltext.length(), ss1.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                    _holder.txtDescription.setText(ss1);
                    _holder.txtDescription.setMovementMethod(LinkMovementMethod.getInstance());
                }

            }
            else
            {
                _holder.txtDescription.setText(isi);
            }
            final LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            _holder.mPager.setLayoutManager(horizontalLayoutManagaer);
            _holder.txtDescription.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
            Linkify.addLinks(_holder.txtDescription, Linkify.WEB_URLS);


            _holder.txtTotalLike.setText(item.totallikes + " Likes");
            _holder.txtTotalLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), LikeActivity.class);
                    intent.putExtra("INFOID", item.getId());
                    intent.putExtra("POS", position);
                    startActivity(intent);
                }
            });

            _holder.txtTotalKomentar.setText(item.totalkomentar + " Komentar");
            _holder.imgSuka.setImageResource((item.ownlike) ? R.drawable.ic_like_hati : R.drawable.ic_like_default);
            _holder.imgSuka.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _holder.imgSuka.startAnimation(Constant.AnimBounce(getActivity()));
                    setLikes(item.getId(), _holder.imgSuka, _holder.txtTotalLike, item);
                }
            });
            _holder.txtTotalKomentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), KomentarActivity.class);
                    intent.putExtra("INFOID", item.getId());
                    intent.putExtra("POS", position);
                    intent.putExtra("OWNHP", item.telp);
                    startActivity(intent);
                }
            });
            _holder.imgKomentar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), KomentarActivity.class);
                    intent.putExtra("INFOID", item.getId());
                    intent.putExtra("POS", position);
                    intent.putExtra("OWNHP", item.telp);
                    startActivity(intent);

                }
            });

            if(item.getImage().size() > 0) {
                _holder.rl_gallery.setVisibility(View.VISIBLE);
                adapter.image = new ArrayList<>(item.getImage());
                adapter.mAdapter = new Slidingvideo_adapter(getActivity(), adapter.image);
                snapHelper.attachToRecyclerView(_holder.mPager);
                _holder.mPager.setAdapter(adapter.mAdapter);

                _holder.mPager.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);

                        int visibleItemCount = horizontalLayoutManagaer.getChildCount();
                        int totalItemCount = horizontalLayoutManagaer.getItemCount();
                        int firstVisibleItemPosition = horizontalLayoutManagaer.findFirstVisibleItemPosition();
                        Log.e("page", "total item : " + totalItemCount + " fvisible " + firstVisibleItemPosition);

                        /*
                        if (!isLoading && !isLastPage) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0
                                    && totalItemCount >= PAGE_SIZE) {
                                loadMoreItems();
                            }
                        }
                        */
                    }
                });
                //_holder.mPager.addItemDecoration(pageIndicator);

            }
            else
            {
                _holder.rl_gallery.setVisibility(View.GONE);
            }
            return v;
        }
    };




    public class holder
    {

        @BindView(R.id.imgAvatar) public CircleImageView imgAvatar;
        //@BindView(R.id.pager) public ViewPager mPager;
        @BindView(R.id.pager) public Container mPager;
        //@BindView(R.id.indicator) public CirclePageIndicator indicator;
        //@BindView(R.id.view_pager_indicator) public OverflowPagerIndicator indicator;
        @BindView(R.id.rl_gallery) public RelativeLayout rl_gallery;
        @BindView(R.id.txtFullname) public TextView txtFullname;
        @BindView(R.id.txtTanggal) public TextView txtTanggal;
        @BindView(R.id.txtStatus) public TextView txtStatus;
        @BindView(R.id.txtDescription) public TextView txtDescription;
        @BindView(R.id.txtTotalLike) public TextView txtTotalLike;
        @BindView(R.id.txtTotalKomentar) public TextView txtTotalKomentar;
        @BindView(R.id.imgSuka) public ImageView imgSuka;
        @BindView(R.id.imgKomentar) public ImageView imgKomentar;

        public holder(View view)
        {
            ButterKnife.bind(this, view);
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constant.savedInstanceInfo, mAllResults);
        outState.putSerializable("timelineTujuan", timeLineTujuan);
        outState.putSerializable("timelineStatus", statusTimeline);
        outState.putSerializable("REQUEST_MODEL", inf);
        outState.putString("alamat", alamat);
        outState.putString("hintSelected", hintSelected);
        outState.putInt("LASTID", LastID);
        outState.putInt("INITIALPAGE", mCurrentPage);

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            mGoogleApiClient.disconnect();
        }
    }


    private void sendUpload()
    {
        StartApp.ONCLICK_BUTTON_TIMELINE = true;
        if(image_uris.size() > 0)
            uploadWithImage();
        else
            uploadWithoutImage();
    }

    public void readImageFromExternal()
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Constant.checkPermissionForExternalStorage(getActivity())) {
                Constant.requestStoragePermission(getActivity(), READ_STORAGE_PERMISSION);
            } else {
                Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
                startActivityForResult(intent, Constant.REQUEST_CODE);
            }
        }else{
            Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
            intent.putExtra(Constant.INTENT_EXTRA_LIMIT, LIMIT);
            startActivityForResult(intent, Constant.REQUEST_CODE);
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();
        for (Object perm : wanted) {
            if (!hasPermission((String)perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }




    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission(perms.toString())) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String)permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                ActivityCompat.requestPermissions(getActivity(), (String[])permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
            case WRITE_STORAGE_PERMISSION:
                sendUpload();
                break;
        }

    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    private ArrayList<File> compressImage()
    {
        ArrayList<File> tmpFile = new ArrayList<>();
        for(int i=0; i < image_uris.size(); i++) {
            Image img = image_uris.get(i);
            //Log.e("compress", "Name:" + img.name + " Path: " + img.path + " id " + img.id + " isselected " + img.isSelected);
            if(!img.name.equals("update")) {
                File fl = new File(img.path);
                if(img.mimeType.contains("video"))  fl = new File(img.path);
                else if(img.mimeType.contains("image"))  fl = Constant.CompressImage80(getContext(), new File(img.path));

                tmpFile.add(fl);
            }
        }
        return tmpFile;
    }

    private UploadNotificationConfig getNotificationConfig(String filename) {

        return new UploadNotificationConfig()
                .setIcon(R.mipmap.ic_launcher)
                .setCompletedIcon(R.drawable.ic_ab_done_gray)
                .setErrorIcon(R.drawable.crop__ic_cancel)
                .setTitle(filename)
                .setInProgressMessage(getString(R.string.uploading))
                .setCompletedMessage(getString(R.string.upload_success))
                .setErrorMessage(getString(R.string.upload_error))
                .setAutoClearOnSuccess(true)
                .setClearOnAction(true)
                .setRingToneEnabled(true);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private void uploadWithImage()
    {

        final String serverUrlString = Constant.BASE_URL + "android/v2/index.php/"+Constant.CONTROLLER_DEV+"/get_info/create/"+profil.hp;
        ArrayList<File> compressFile = compressImage();

        Timelinetujuan.Tujuan group = timeLineTujuan.get(vwHeaderHandler.spnTujuan.getSelectedItemPosition());
        Timelinetujuan.Tujuan tiStatus = statusTimeline.get(vwHeaderHandler.spnStatus.getSelectedItemPosition());
        MultipartUploadRequest req = new MultipartUploadRequest(getContext(), serverUrlString)
                .addParameter("mitraid",getContext().getResources().getString(R.string.CONF_MITRAID))
                .addParameter("description",vwHeaderHandler.txtDescription.getText().toString())
                .addParameter("hp",profil.hp)
                .addParameter("alamat",alamat)
                .addParameter("lat",Double.toString(gLatitude))
                .addParameter("lon",Double.toString(gLongitude))
                .addParameter("status", tiStatus.nama)
                .addParameter("membergroup", String.valueOf(idMemberGroup))
                .setNotificationConfig(getNotificationConfig("Upload Info"))
                //.setCustomUserAgent(USER_AGENT)
                .setAutoDeleteFilesAfterSuccessfulUpload(true)
                .setUsesFixedLengthStreamingMode(true)
                .setMaxRetries(3);


        for(int i=0; i < compressFile.size(); i++)
        {
            File img = compressFile.get(i);
            final String filename = new File(img.getPath()).getName();
            final String paramNameString = "uploaded_file[]";
            try {
                req.addFileToUpload(img.getAbsolutePath(), paramNameString);
            } catch (FileNotFoundException exc) {
                showToast(exc.getMessage());
            } catch (IllegalArgumentException exc) {
                showToast("Missing some arguments. " + exc.getMessage());
            }catch (Exception exc) {
                showToast(exc.getMessage());
            }
        }

        try {
            Toast.makeText(getActivity(), "Info sedang di upload...", Toast.LENGTH_SHORT).show();
            vwHeaderHandler.txtDescription.setText("");
            req.setUtf8Charset();
            String uploadID = req.setDelegate(this).startUpload();
            addUploadToList(uploadID,"Upload Kegiatan");

        } catch (IllegalArgumentException exc) {
            showToast("Missing some arguments. " + exc.getMessage());
        } catch (MalformedURLException exc) {
            showToast(exc.getMessage());
        }catch (NullPointerException e){
            showToast(e.getMessage());
        }

    }

    private void uploadWithoutImage()
    {
        vwHeaderHandler.btnSubmit.setEnabled(false);
        vwHeaderHandler.btnSubmit.setText("Uploading..");

        Timelinetujuan.Tujuan group = timeLineTujuan.get(vwHeaderHandler.spnTujuan.getSelectedItemPosition());
        Timelinetujuan.Tujuan tiStatus = statusTimeline.get(vwHeaderHandler.spnStatus.getSelectedItemPosition());

        RequestBody mitraid = RequestBody.create(MediaType.parse("multipart/form-data"), getContext().getResources().getString(R.string.CONF_MITRAID));
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), vwHeaderHandler.txtDescription.getText().toString());
        RequestBody hp = RequestBody.create(MediaType.parse("multipart/form-data"), profil.hp);
        RequestBody address = RequestBody.create(MediaType.parse("multipart/form-data"), alamat);
        RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-data"), Double.toString(gLatitude));
        RequestBody lon = RequestBody.create(MediaType.parse("multipart/form-data"), Double.toString(gLongitude));
        RequestBody status = RequestBody.create(MediaType.parse("multipart/form-data"), tiStatus.nama);
        RequestBody tipe = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(idMemberGroup));

        RetrofitBuilder builder = new RetrofitBuilder("kegiatanBaru");
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        //"android/v2/index.php/microz/get_info/create/{query}"
        Call<PojoProfilUsaha> call = api.uploadInfoWithoutImage(String.format(Constant.CONTROLLER_4S,Constant.CONTROLLER_DEV, "get_info", "create", profil.hp),mitraid, description, hp, address,lat, lon, status, tipe);
        call.enqueue(new Callback<PojoProfilUsaha>() {
            @Override
            public void onResponse(Call<PojoProfilUsaha> call, Response<PojoProfilUsaha> response) {
                //if(_pgbar.isShowing())_pgbar.dismiss();
                Log.e("res", new Gson().toJson(response.body()));
                vwHeaderHandler.btnSubmit.setEnabled(true);
                vwHeaderHandler.btnSubmit.setText("KIRIM");
                if(!response.isSuccessful())
                {
                    Snackbar snackBar = Snackbar.make(coordinatorLayout,"Error Code " + response.code() +"\n"+response.message(), Snackbar.LENGTH_SHORT);
                    snackBar.show();
                    return;
                }

                vwHeaderHandler.txtDescription.setText("");

                try
                {
                    PojoProfilUsaha pojos = response.body();
                    ArrayList<Row> datas = new ArrayList<Row>(pojos.getRows());
                    if(datas.size() > 0)
                    {
                        adapter.addEntriesToTop(datas);
                        lvwData.setSelection(0);
                    }
                }catch (NullPointerException e)
                {

                }

                //LastID = pojos.getLastId();
            }

            @Override
            public void onFailure(Call<PojoProfilUsaha> call, Throwable t) {
                vwHeaderHandler.btnSubmit.setEnabled(true);
                vwHeaderHandler.btnSubmit.setText("KIRIM");

                Snackbar snackBar = Snackbar.make(coordinatorLayout,t.getMessage(), Snackbar.LENGTH_SHORT);
                snackBar.show();
            }
        });
    }

    public void setArrayStatusTimeline(ArrayList<Timelinetujuan.Tujuan> tujuan , ArrayList<Timelinetujuan.Tujuan> status) throws NullPointerException
    {
        timeLineTujuan = tujuan;
        statusTimeline = status;

        String[] items = new String[timeLineTujuan.size()];
        final String[] idmembers = new String[timeLineTujuan.size()];
        for (int j=0; j < timeLineTujuan.size(); j++)
        {
            items[j] = timeLineTujuan.get(j).nama;
            idmembers[j] = timeLineTujuan.get(j).id;
        }

        final String[] itemsStatus = new String[statusTimeline.size()];
        final String[] hintStatus = new String[statusTimeline.size()];
        final String[] idStatus = new String[statusTimeline.size()];
        for (int j=0; j < statusTimeline.size(); j++)
        {
            itemsStatus[j] = statusTimeline.get(j).nama;
            hintStatus[j] = statusTimeline.get(j).hint;
            idStatus[j] = statusTimeline.get(j).id;
        }

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_timeline_tujuan, items);
        adapter.setDropDownViewResource(R.layout.spinner_timeline_dropdown);
        vwHeaderHandler.spnTujuan.setAdapter(adapter);
        vwHeaderHandler.spnTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                idMemberGroup = Integer.parseInt(idmembers[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<String> sdapter;
        sdapter = new StatusAdapter(getActivity(), R.layout.spinner_timeline_tujuan, itemsStatus);
        vwHeaderHandler.spnStatus.setAdapter(sdapter);
        vwHeaderHandler.spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                vwHeaderHandler.txtDescription.setHint(hintStatus[i]);
                hintSelected = hintStatus[i];
                idStatusSelected = idStatus[i];
                if(idStatusSelected.equals("3"))
                {
                    vwHeaderHandler.spnTujuan.setVisibility(View.VISIBLE);
                }
                else
                {
                    vwHeaderHandler.spnTujuan.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getTujuanTimeline()
    {
        PrefManager pref = new PrefManager(getActivity());
        Session _session = Constant.getSession(getActivity());
        RetrofitBuilder builder = new RetrofitBuilder("getTujuanTimeline");
        RetroBuilderInterface githubUserAPI = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<Timelinetujuan> call = githubUserAPI.getTujuanTimeline(String.format(Constant.CONTROLLER_2S, Constant.CONTROLLER_DEV, "getTujuanTimeline"), pref.getUuid(), profil.hp, getActivity().getResources().getString(R.string.CONF_MITRAID));
        call.enqueue(new Callback<Timelinetujuan>() {
            @Override
            public void onResponse(Call<Timelinetujuan> call, Response<Timelinetujuan> response) {
                if(!response.isSuccessful()){
                    return;
                }
                Timelinetujuan tTujuan = response.body();
                try {
                    setArrayStatusTimeline(new ArrayList<Timelinetujuan.Tujuan>(tTujuan.tujuan),new ArrayList<Timelinetujuan.Tujuan>(tTujuan.status));
                }
                catch (NullPointerException e)
                {

                }

            }

            @Override
            public void onFailure(Call<Timelinetujuan> call, Throwable t) {

            }
        });
    }



    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        vwHeaderHandler.txtLocation.setText("Connected");
        startLocationUpdates();

        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

            // mLatitudeTextView.setText(String.valueOf(mLocation.getLatitude()));
            //mLongitudeTextView.setText(String.valueOf(mLocation.getLongitude()));
        } else {
            Toast.makeText(getActivity(), "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
        vwHeaderHandler.txtLocation.setText("Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        vwHeaderHandler.txtLocation.setText(connectionResult.getErrorCode());
    }

    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

    }

    @Override
    public void onLocationChanged(Location location) {

        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        gLatitude = location.getLatitude();
        gLongitude = location.getLongitude();
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        try
        {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    //alamat = address + " " + city + " " + country;
                    alamat = city + " " + state;
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        catch (NullPointerException ex)
        {

        }

        vwHeaderHandler.txtLocation.setText(alamat);
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private void setLikes(final String infoid, final ImageView imgSuka, final TextView txtSuka, final Row item)
    {
        final ModelRequestInfo req = new ModelRequestInfo();
        req.hp = profil.hp;
        req.mitraid = getActivity().getResources().getString(R.string.CONF_MITRAID);
        req.info_id = infoid;
        onLike = Integer.valueOf(infoid);

        RetrofitBuilder builder = new RetrofitBuilder("kegiatanBaru");
        RetroBuilderInterface api = builder.getRetrofit().create(RetroBuilderInterface.class);
        Call<JsonObject> call = api.setLikes(String.format(Constant.CONTROLLER_4S, Constant.CONTROLLER_DEV,"get_info", "likes", profil.hp), req);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if(!response.isSuccessful())return;

                try
                {
                    JsonObject obj = response.body();
                    if(!obj.has("status")) // kalo menemukan ini berarti error
                    {

                        item.ownlike = obj.get("ownlike").getAsBoolean();
                        item.totallikes = obj.get("total").toString();
                        _db.updateOwnlikes(infoid, Integer.parseInt(item.totallikes));
                        _db.updateTotalLikes(infoid, item.ownlike);
                        adapter.notifyDataSetChanged();
                    }

                }
                catch (JsonParseException ex)
                {

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void addUploadToList(String uploadID, String filename) {
        View uploadProgressView = getActivity().getLayoutInflater().inflate(R.layout.view_upload_progress, null);
        UploadProgressViewHolder viewHolder = new UploadProgressViewHolder(uploadProgressView, filename);
        viewHolder.uploadId = uploadID;
        //container.addView(viewHolder.itemView, 0);
        uploadProgressHolders.put(uploadID, viewHolder);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if(direction == SwipyRefreshLayoutDirection.TOP){
            LastID = _db.LastTLid();
            adapter.onScrollNext();
            if(statusTimeline.size() <= 0)
            {
                getTujuanTimeline();
            }

        }
    }

    class UploadProgressViewHolder {

        @BindView(R.id.uploadTitle) TextView uploadTitle;
        @BindView(R.id.uploadProgress) ProgressBar progressBar;

        String uploadId;

        UploadProgressViewHolder(View view, String filename) {
            ButterKnife.bind(this, view);

            progressBar.setMax(100);
            progressBar.setProgress(0);

            uploadTitle.setText(getString(R.string.upload_progress, filename));
        }

        @OnClick(R.id.cancelUploadButton)
        void onCancelUploadClick() {
            if (uploadId == null)
                return;

            UploadService.stopUpload(uploadId);
        }
    }
    private void logSuccessfullyUploadedFiles(List<String> files) {
        for (String file : files) {
            Log.e(TAG, "Success:" + file);
        }
    }

    private void deleteFaterSuccessfully(List<String> files)
    {
        try {
            // delete the original file
            for (String file : files) {
                new File(file).delete();
                Log.e("tag", "deleted FIle " + file);
            }

            vwHeaderHandler.txtDescription.setText("");
            image_uris.clear();
            view_selected_photos_container.setVisibility(View.GONE);

        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }


    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {
        Log.e(TAG, String.format(Locale.getDefault(), "ID: %1$s (%2$d%%) at %3$.2f Kbit/s",
                uploadInfo.getUploadId(), uploadInfo.getProgressPercent(),
                uploadInfo.getUploadRate()));
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        uploadProgressHolders.get(uploadInfo.getUploadId())
                .progressBar.setProgress(uploadInfo.getProgressPercent());
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, Exception exception) {
        Log.e(TAG, "Error with ID: " + uploadInfo.getUploadId() + ": "
                + exception.getLocalizedMessage(), exception);
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        //container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
        Log.e(TAG, String.format(Locale.getDefault(),
                "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$d, body:[%5$s]",
                uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                uploadInfo.getUploadRate(), serverResponse.getHttpCode(),
                serverResponse.getBodyAsString()));
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        for (Map.Entry<String, String> header : serverResponse.getHeaders().entrySet()) {
            Log.e("Header", header.getKey() + ": " + header.getValue());
        }

        //Log.e("BODY", serverResponse.getBodyAsString());

        //Log.e(TAG, "Printing response body bytes");
        byte[] ba = serverResponse.getBody();
        for (int j = 0; j < ba.length; j++) {
            //Log.e(TAG, String.format("%02X ", ba[j]));
        }
        deleteFaterSuccessfully(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null) return;

        //container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());

        try {
            Gson g = new Gson();
            PojoProfilUsaha pojos = g.fromJson(serverResponse.getBodyAsString(), PojoProfilUsaha.class);
            ArrayList<Row> datas = new ArrayList<Row>(pojos.getRows());

            adapter.addEntriesToTop(datas);
            lvwData.setSelection(0);
            //LastID = pojos.getLastId();
        }catch (JsonParseException e)
        {

        }
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
        logSuccessfullyUploadedFiles(uploadInfo.getSuccessfullyUploadedFiles());

        if (uploadProgressHolders.get(uploadInfo.getUploadId()) == null)
            return;

        //container.removeView(uploadProgressHolders.get(uploadInfo.getUploadId()).itemView);
        uploadProgressHolders.remove(uploadInfo.getUploadId());
    }

    public interface AdapterCallback {
        void removeImage(View view);
    }

    public class thumbnailAdapter extends BaseRecyclerViewAdapter<Image, thumbnailAdapter.SelectedPhotoHolder>
    {
        ArrayList<Image> imgUri;
        Context _context;
        Activity _activity;
        private AdapterCallback mAdapterCallback;

        public thumbnailAdapter(Context context, ArrayList<Image> img){
            super(context);
            this._context = context;
            this._activity = (Activity)_context;
            this.imgUri = img;

        }

        public thumbnailAdapter(Context context, ArrayList<Image> img, AdapterCallback call){
            super(context);
            this._context = context;
            this._activity = (Activity)_context;
            this.imgUri = img;
            this.mAdapterCallback = call;
        }


        public int getItemCount() {
            return this.imgUri.size();
        }

        public Image getItem(int position){
            return this.imgUri.get(position);
        }

        @Override
        public void onBindView(SelectedPhotoHolder holder, int position) {
            Image uri = getItem(position);
            Log.e("thumbnailAdapter", "Name : " + uri.name + " mimeTyupe : " + uri.mimeType);
            Glide.with(_activity)
                    .load(uri.path)
                    .dontAnimate()
                    .centerCrop()
                    .error(R.drawable.imagepicker)
                    .into(holder.selected_photo);

            holder.iv_close.setTag(uri);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
            View view = mInflater.inflate(R.layout.picker_list_item_selected_thumbnail, parent, false);
            return new SelectedPhotoHolder(view);
        }


        class SelectedPhotoHolder extends RecyclerView.ViewHolder {

            ImageView selected_photo;
            ImageView iv_close;


            public SelectedPhotoHolder(View itemView) {
                super(itemView);
                selected_photo = itemView.findViewById(R.id.selected_photo);
                iv_close = itemView.findViewById(R.id.iv_close);
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Image uri = (Image) view.getTag();
                        mAdapterCallback.removeImage(view);
                        //_activity.removeImage(uri);
                    }
                });

            }
        }
    }

    public class viewHeaderHandler
    {

        @BindView(R.id.imgAddNew)  public ImageView imgAddNew;
        @BindView(R.id.imgAddLocation) public ImageView imgAddLocation;
        @BindView(R.id.txtLocation) public TextView txtLocation;
        @BindView(R.id.txtDescription) public EditText txtDescription;

        @BindView(R.id.btnSubmit) public Button btnSubmit;
        @BindView(R.id.spnStatus)public Spinner spnStatus;
        @BindView(R.id.spnTujuan)public Spinner spnTujuan;

        public viewHeaderHandler(View view)
        {
            ButterKnife.bind(this, view);
        }

        @Optional
        @OnClick({R.id.imgAddNew,R.id.imgAddLocation, R.id.btnSubmit})
        public void onSubmit(View view)
        {
            int vId = view.getId();
            switch (vId)
            {
                case R.id.imgAddNew:
                    readImageFromExternal();
                    break;
                case R.id.imgAddLocation:

                    permissions.add(ACCESS_COARSE_LOCATION);
                    permissions.add(ACCESS_FINE_LOCATION);
                    permissionsToRequest = findUnAskedPermissions(permissions);
                    if(permissionsToRequest.size() > 0)
                    {
                        ActivityCompat.requestPermissions(getActivity(), (String[])permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    }
                    else
                    {
                        if (mGoogleApiClient.isConnected())
                        {
                            mGoogleApiClient.disconnect();
                        }
                        if(checkLocation())
                        {
                            mGoogleApiClient.connect();
                        }
                    }

                    break;
                case R.id.btnSubmit:
                    //Log.e("uri_size", "ADA IMG : " + image_uris.size());
                    if(vwHeaderHandler.txtDescription.getText().length() > 5) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (!Constant.checkPermissionForWriteExternalStorage(getActivity())) {
                                sendUpload();
                                Constant.requestWriteStoragePermission(getActivity(), WRITE_STORAGE_PERMISSION);
                            } else {
                                sendUpload();
                                //sendBroadcastMessage();
                            }

                        } else {
                            sendUpload();
                        }
                    }
                    break;
            }
        }
    }

    //spinner status adapter
    public class StatusAdapter extends ArrayAdapter<String>
    {
        private Activity context;
        String[] data = null;

        public StatusAdapter(Activity context, int resource, String[] data2)
        {
            super(context, resource, data2);
            this.context = context;
            this.data = data2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            if(row == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinner_timeline_tujuan, parent, false);
            }
            //put the data in it
            String item = data[position];
            if(item != null)
            {
                TextView text1 = row.findViewById(R.id.text);
                if(item.equals("Publik"))
                    text1.setTextColor(context.getResources().getColor(R.color.publik_color));
                else if(item.equals("Info"))
                    text1.setTextColor(context.getResources().getColor(R.color.info_color));
                else
                    text1.setTextColor(context.getResources().getColor(R.color.konsul_color));
                text1.setText(item);
            }

            return row;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View row = convertView;
            if(row == null)
            {
                LayoutInflater inflater = context.getLayoutInflater();
                row = inflater.inflate(R.layout.spinner_status_timeline_dropdown, parent, false);
            }
            //put the data in it
            String item = data[position];
            if(item != null)
            {
                TextView text1 = row.findViewById(R.id.text);
                View spaceView = row.findViewById(R.id.spaceView);
                if(position == (data.length)-1) spaceView.setVisibility(View.GONE);
                else  spaceView.setVisibility(View.VISIBLE);
                if(item.equals("Publik"))
                    text1.setTextColor(context.getResources().getColor(R.color.publik_color));
                else if(item.equals("Info"))
                    text1.setTextColor(context.getResources().getColor(R.color.info_color));
                else
                    text1.setTextColor(context.getResources().getColor(R.color.konsul_color));
                text1.setText(item);
            }

            return row;
        }
    }
}
