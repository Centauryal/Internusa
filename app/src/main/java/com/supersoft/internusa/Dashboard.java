package com.supersoft.internusa;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.supersoft.internusa.helper.badgeview.BadgeTabLayout;
import com.supersoft.internusa.helper.exception.UnCaughtException;
import com.supersoft.internusa.helper.services.ConnectionService;
import com.supersoft.internusa.helper.services.NetworkStateReceiver;
import com.supersoft.internusa.helper.services.ProfilupdateService;
import com.supersoft.internusa.helper.services.RoosterConnection;
import com.supersoft.internusa.helper.services.TimelineService;
import com.supersoft.internusa.helper.services.xmpp.XMPPService;
import com.supersoft.internusa.helper.util.CircleImageView;
import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.helper.util.DBHelper;
import com.supersoft.internusa.helper.util.PrefManager;
import com.supersoft.internusa.helper.util.Session;
import com.supersoft.internusa.model.ChatSingleModel;
import com.supersoft.internusa.model.InfoTipe;
import com.supersoft.internusa.model.ProfilDB;
import com.supersoft.internusa.splash.SplashMain;
import com.supersoft.internusa.ui.About;
import com.supersoft.internusa.ui.chat.ChatActivity;
import com.supersoft.internusa.ui.chat.ChatDetailActivity;
import com.supersoft.internusa.ui.info.KomentarActivity;
import com.supersoft.internusa.ui.info.MainInfoFragment;
import com.supersoft.internusa.ui.payment.MainPayment;
import com.supersoft.internusa.ui.payment.TambahSaldoActivity;
import com.supersoft.internusa.ui.profil.MainProfil;
import com.supersoft.internusa.ui.sidebar.Historyloginfo;
import com.supersoft.internusa.ui.sidebar.MainTabUsahaProfesi;
import com.supersoft.internusa.ui.sidebar.OptionActivity;
import com.supersoft.internusa.ui.sidebar.UserProfileActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import me.leolin.shortcutbadger.ShortcutBadger;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkStateReceiver.NetworkStateReceiverListener, DrawerLayout.DrawerListener {


    private BadgeTabLayout mTabs;
    private ViewPager mViewPager;
    private int[] imageResId = {
            R.drawable.ic_action_info,
            //R.drawable.ic_action_profil,
            R.drawable.ic_action_payment,
            //R.drawable.ic_action_discussion
    };

    DBHelper _db;
    private Session _sess;
    private PrefManager prefManager;
    TelephonyManager tm;
    //private String[] titleId = {"Info", "Profil", "Iklan", "Etalasae", "Payment"};
    private String[] titleId = {"Info & Konsultasi", "Payment"};

    @BindView(R.id.btnLogout)
    Button btnLogout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view)  NavigationView navigationView;
    TextView txtFullname;

    CircleImageView imgAvatar;
    ProfilDB pf;

    XMPPService mXMPPService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(Dashboard.this));
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        _db = new DBHelper(this);
        prefManager = new PrefManager(this);
        _sess = new Session(this);
        pf = _db.getProfilDb();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(this);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main_home);
        txtFullname = headerLayout.findViewById(R.id.txtFullname);
        txtFullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.showActivity(Dashboard.this, UserProfileActivity.class, false);
            }
        });
        TextView txtPhonenum = headerLayout.findViewById(R.id.txtPhonenumber);
        imgAvatar = headerLayout.findViewById(R.id.imageView);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.showActivity(Dashboard.this, UserProfileActivity.class, false);
            }
        });


        Constant.loadDefaultProfilSidebar(this,pf.avatar, imgAvatar);
        txtFullname.setText(pf.fullname);
        txtPhonenum.setText(pf.hp);

        initViewPager();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            InfoTipe info = new Gson().fromJson(extras.getString("data"), InfoTipe.class);
            Log.e("klik", new Gson().toJson(info));
            if ((info.id.length() > 2) && (info.id.substring(0, 2).equals(Constant.NOTIF_KODE_CREATE_TOPIK))) {
                mViewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mViewPager.setCurrentItem(3);
                    }
                }, 1000);

                if(info.chatdata instanceof ChatSingleModel)
                {
                    ChatSingleModel item = info.chatdata;
                    Intent intent = new Intent(this, ChatDetailActivity.class);
                    intent.putExtra("GROUP_ID", String.valueOf(item.groupid));
                    intent.putExtra("LOCAL_ID", 0);
                    startActivity(intent);
                }

            } else if ((info.id.length() > 2) && (info.id.substring(0, 2).equals(Constant.NOTIF_KODE_HTML_CONTENT))) {

            }
            else if((info.id.length() > 2) && (info.id.substring(0,2).equals(Constant.NOTIF_KODE_KOMENTAR)) )
            {
                if(info.detail.get(0).tipe.toLowerCase().equals("komentar"))
                {
                    Intent intent = new Intent(this, KomentarActivity.class);
                    intent.putExtra("INFOID", String.valueOf(info.detail.get(0).infoid));
                    intent.putExtra("POS", -1);
                    startActivity(intent);
                }
                else if(info.detail.get(0).tipe.toLowerCase().equals("broadcastgroup"))
                {
                    Intent intent = new Intent(this, ChatActivity.class);
                    intent.putExtra("INFOID", String.valueOf(info.detail.get(0).infoid));
                    intent.putExtra("POS", -1);
                    startActivity(intent);
                }
            }
        }


        EventBus.getDefault().register(this);
        startService(new Intent(this, TimelineService.class));
        startService(new Intent(this, ProfilupdateService.class));

        //if(mXMPPService.getConnection().isAuthenticated()) imgAvatar.setBorderColor(getResources().getColor(R.color.info_color));

    }

    private void initViewPager(){
        mTabs = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.post(new Runnable() {
            @Override
            public void run() {
                mTabs.setupWithViewPager(mViewPager);
                for(int i=0; i < imageResId.length; i++){
                    mTabs.getTabAt(i).setCustomView(getTabView(i));
                }

                int ada = _db.countUnreadChatAllTopik();
                Log.e("chatDash", "Unread : " + ada);
                if(ada > 0) mTabs.with(3).badge(true).icon(imageResId[3]).label("Diskusi").badgeCount(ada).build();
            }
        });
        mTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tab select", "position " + tab.getPosition());
                if(tab.getPosition() == 3) refreshTabBadge();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.e("tab select", "uselect position " + tab.getPosition());
                if(tab.getPosition() == 3) refreshTabBadge();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void refreshTabBadge()
    {
        mTabs.post(new Runnable() {
            @Override
            public void run() {
                int ada = _db.countUnreadChatAllTopik();
                Log.e("onTabSelected", "Unread subscribe : " + ada);
                if(ada > 0)
                    mTabs.with(3).badge(true).icon(imageResId[3]).label("Diskusi").badgeCount((ada == 0) ? 1 : ada).build();
                else
                    mTabs.with(3).badge(false).icon(imageResId[3]).label("Diskusi").badgeCount(0).build();
            }
        });
    }

    public View getTabView(int position)
    {
        View v = LayoutInflater.from(this).inflate(R.layout.tab_custom_icon, null);
        ImageView mIconView = v.findViewById(R.id.tab_icon);
        TextView mBadgeTextView = v.findViewById(R.id.tab_badge);
        TextView mLabelText = v.findViewById(R.id.tab_label);
        mLabelText.setText(titleId[position]);
        mIconView.setImageResource(imageResId[position]);
        mBadgeTextView.setVisibility(View.GONE);
        return v;
    }
    @Optional
    @OnClick(R.id.btnLogout)
    public void onSubmit(View view)
    {
        boolean ok = _db.setIsLogin("0");
        if(ok) Constant.showActivity(this, SplashMain.class, true);
    }

    @Override
    public void onBackPressed() {
        ShortcutBadger.removeCount(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profil) {
            Constant.showActivity(this, UserProfileActivity.class, false);
        } else if (id == R.id.nav_option) {
            Constant.showActivity(this, OptionActivity.class, false);
        } else if (id == R.id.nav_info) {
            Constant.showActivity(this, About.class, false);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onNetworkAvailable() {
        Log.e("netWork", "Network Available");
        Log.e("netWork", "selected Tab " + mViewPager.getCurrentItem());
        StartApp.isNetworkAvailable = true;
    }

    @Override
    public void onNetworkUnavailable() {
        Log.e("netWork", "onNetworkUnavailable");
        Log.e("netWork", "selected Tab " + mViewPager.getCurrentItem());
        StartApp.isNetworkAvailable = false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {
        pf = _db.getProfilDb();
        Constant.loadDefaultProfilSidebar(this,pf.avatar, imgAvatar);
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }


    class MyAdapter extends FragmentStatePagerAdapter {
        Context _context;
        Fragment myFragment = null;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            _context = getApplicationContext();
        }

        @Override
        public Fragment getItem(int position)
        {

            switch (position){
                case 0 : return MainInfoFragment.newInstance();
                //case 1 : return MainProfil.newInstance();
                //case 2 : return MainInfoFragment.newInstance();
                case 1 : return MainPayment.newInstance();
                //case 2 : return ChatActivity.newInstance(mTabs);
            }
            return null;
        }

        @Override
        public int getCount() {

            return 2;

        }


        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader){
            Log.e("restoreState","satet restore");
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            myFragment = (Fragment) super.instantiateItem(container, position);
            if (myFragment != null) {
                Log.i("FRAG", "Fragment retrieved " + myFragment.toString());
            }
            return myFragment;
        }
    }

    @Subscribe
    public void onSubscribeRobot(Object param)
    {
        if(param instanceof String) {
            String keyword = (String)param;
            switch (keyword) {
                case Constant.ACTIVITY_DEPOSIT:
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(2, true);
                        }
                    }, 100);
                    Intent inten = new Intent(this, TambahSaldoActivity.class);
                    startActivity(inten);
                    break;
                case Constant.ACTIVITY_EXPORT_CONTACT:
                    break;
                case Constant.ACTIVITY_IMPORT_CONTACT:
                    break;
                case Constant.ACTIVITY_PRINT_HISTORY:
                    break;
                case Constant.ACTIVITY_PRABAYAR:
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(2, true);
                        }
                    }, 100);
                    break;
                case Constant.ACTIVITY_PASCA_BAYAR:
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mViewPager.setCurrentItem(2, true);
                        }
                    }, 100);
                    break;
                case "update_poto_profil":
                    pf = _db.getProfilDb();
                    Constant.loadDefaultProfilSidebar(this, pf.avatar, imgAvatar);
                    txtFullname.setText(pf.fullname);
                    break;
                case "AUTHENTICATED":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgAvatar.setBorderColor(getResources().getColor((ConnectionService.sConnectionState == RoosterConnection.ConnectionState.AUTHENTICATED) ? R.color.info_color : R.color.white));

                        }
                    });
                case Constant.XMPP_CONNECTED:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgAvatar.setBorderColor(getResources().getColor(R.color.info_color));
                        }
                    });
                    break;
                case Constant.XMPP_NOT_CONNECT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgAvatar.setBorderColor(getResources().getColor(R.color.white));
                        }
                    });
                    break;
                case "update_pic_profil_from_service":
                    Log.e("updateAvater", "Update avatar from service");
                    pf = _db.getProfilDb();
                    Constant.loadDefaultProfilSidebar(this, pf.avatar, imgAvatar);
                    break;
                case "badge_diskusi":
                    Log.e("badgeDisku", "subscribe kesini bacge_disku");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int ada = _db.countUnreadChatAllTopik();
                            Log.e("chatDash", "Unread subscribe : " + ada);
                            if(ada > 0)
                                mTabs.with(2).badge(true).icon(imageResId[2]).label("Diskusi").badgeCount((ada == 0) ? 1 : ada).build();
                            else
                                mTabs.with(2).noBadge().build();
                        }
                    });
                default:
                    break;
            }
        }
        else if(param instanceof ChatSingleModel)
        {
            ChatSingleModel chat = (ChatSingleModel) param;
            if(chat.jenis.equals("chat_topik"))
            {
                Constant.debug("dashboard", "chat_topik");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int ada = _db.countUnreadChatAllTopik();
                        Log.e("chatDash", "Unread subscribe : " + ada);
                        mTabs.with(2).badge(true).icon(imageResId[2]).label("Diskusi").badgeCount((ada == 0) ? 1 : ada).build();
                    }
                });
            }
        }
    }
}
