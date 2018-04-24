package com.supersoft.internusa.ui.sidebar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.ui.profil.ProfesiFragment;
import com.supersoft.internusa.ui.profil.UsahaFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by itclub21 on 4/16/2017.
 */

public class MainTabUsahaProfesi extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    public static int isSelectedTab = 0;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.container_fragment)
    FrameLayout container_fragment;

    @BindView(R.id.tabs)
    TabLayout tabs;




    public static MainTabUsahaProfesi instance(int selected)
    {
        MainTabUsahaProfesi main = new MainTabUsahaProfesi();
        isSelectedTab = selected;
        return main;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_usaha_profesi);
        ButterKnife.bind(this);

        if(getIntent().getExtras() != null)
        {
            isSelectedTab = getIntent().getExtras().getInt("selected");
        }

        initToolbar();
        initViewPager();
    }



    public void initToolbar()
    {
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbar.setNavigationIcon(R.drawable.ic_back_home_white);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


    private void initViewPager() {

        tabs.addTab(tabs.newTab().setCustomView(createCustomTab(R.drawable.ic_store_white, "Usaha",R.color.white)));
        tabs.addTab(tabs.newTab().setCustomView(createCustomTab(R.drawable.ic_profesi_gray, "Profesi", R.color.gray)));
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#00000000"));
        tabs.setOnTabSelectedListener(this);

        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        selectedTab(isSelectedTab);
                    }
                }, 100);


    }

    private void selectedTab(final int selected)
    {

        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        FragmentManager fm = getSupportFragmentManager();
                        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
                        tabs.getTabAt(selected).select();
                        transaction.replace(R.id.container_fragment,(selected == 0) ? UsahaFragment.newInstance() : ProfesiFragment.newInstance());
                        fm.popBackStack();
                        transaction.commit();
                    }
                }, 100);
    }


    private TextView defaultCustomTab(TextView tv, int drawable, String text, int defaultColor)
    {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tv.setTextColor(getResources().getColor(defaultColor));
        tv.setGravity(Gravity.CENTER_VERTICAL);
        tv.setLayoutParams(params);
        tv.setCompoundDrawablePadding(20);
        tv.setText(text);
        Drawable img = getResources().getDrawable(drawable);
        Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
        //bitmap.eraseColor(defaultColor);
        Drawable d = new BitmapDrawable(this.getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));

        tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);
        return tv;
    }

    private TextView createCustomTab(int drawable, String text, int defaultColor){
        TextView cview = new TextView(this);
        return defaultCustomTab(cview, drawable, text, defaultColor);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        TextView tview = (TextView)tab.getCustomView();
        switch (tab.getPosition()) {
            case 0:
                selectedTab(0);
                defaultCustomTab(tview, R.drawable.ic_store_white, "Usaha", R.color.white);

                break;
            case 1:
                selectedTab(1);
                defaultCustomTab(tview, R.drawable.ic_profesi_white, "Profesi", R.color.white);
                break;
        }

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        TextView tview = (TextView)tab.getCustomView();
        switch (tab.getPosition()) {
            case 0:
                defaultCustomTab(tview, R.drawable.ic_store_gray, "Usaha", R.color.gray);
                break;
            case 1:
                defaultCustomTab(tview, R.drawable.ic_profesi_gray, "Profesi", R.color.gray);
                break;
        }

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
