package com.supersoft.internusa.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;

/**
 * Created by itclub21 on 12/26/2017.
 */

public class CodehtmlContentActivity extends AppCompatActivity {

    private Context mContext;
    private Activity mActivity;
    private RelativeLayout mRelativeLayout;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    String uriHtml = "";
    TextView toolbar_title;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request window feature action bar
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kode_html_content);
        // Get the application context
        mContext = getApplicationContext();
        // Get the activity
        mActivity = CodehtmlContentActivity.this;

        // Change the action bar color
        //getSupportActionBar().setBackgroundDrawable(
        //        new ColorDrawable(Color.parseColor("#FFFF0000"))
        //);

        // Get the widgets reference from XML layout
        mRelativeLayout = findViewById(R.id.rl);
        mWebView = findViewById(R.id.web_view);
        mProgressBar = findViewById(R.id.pb);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar = findViewById(R.id.toolbar);
        initToolbar();

        Intent intent = getIntent();
        if(intent.hasExtra(Constant.FULL_IMAGE_ACTIVITY))
        {
            toolbar_title.setText(intent.getStringExtra(Constant.FULL_IMAGE_TITLE));
            uriHtml = intent.getStringExtra(Constant.FULL_IMAGE_ACTIVITY);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    renderWebPage(uriHtml);
                }
            });
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

    protected void renderWebPage(String urlToRender){
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                // Do something on page loading started
                // Visible the progressbar
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url){
                // Do something when page loading finished
                //Toast.makeText(mContext,"Page Loaded.",Toast.LENGTH_SHORT).show();
            }

        });

        /*
            WebView
                A View that displays web pages. This class is the basis upon which you can roll your
                own web browser or simply display some online content within your Activity. It uses
                the WebKit rendering engine to display web pages and includes methods to navigate
                forward and backward through a history, zoom in and out, perform text searches and more.

            WebChromeClient
                 WebChromeClient is called when something that might impact a browser UI happens,
                 for instance, progress updates and JavaScript alerts are sent here.
        */
        mWebView.setWebChromeClient(new WebChromeClient(){
            /*
                public void onProgressChanged (WebView view, int newProgress)
                    Tell the host application the current progress of loading a page.

                Parameters
                    view : The WebView that initiated the callback.
                    newProgress : Current page loading progress, represented by an integer
                        between 0 and 100.
            */
            public void onProgressChanged(WebView view, int newProgress){
                // Update the progress bar with page loading progress
                mProgressBar.setProgress(newProgress);
                if(newProgress == 100){
                    // Hide the progressbar
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        // Enable the javascript
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Render the web page
        mWebView.loadUrl(urlToRender);
    }
}
