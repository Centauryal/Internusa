package com.supersoft.internusa.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by itclub21 on 3/22/2017.
 */

public class About extends AppCompatActivity {


    @BindView(R.id.lblVersion)
    TextView lblVersion;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ButterKnife.bind(this);

        initToolbar();

    }

    private void initToolbar(){
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
        lblVersion.setText("Version " + Constant.Version(this) + " (" +Constant.RELEASE_TYPE+")");
    }



}
