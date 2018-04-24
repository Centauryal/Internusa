package com.supersoft.internusa.helper.fcm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.supersoft.internusa.R;
import com.supersoft.internusa.helper.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by itclub21 on 11/11/2017.
 */

public class PromocodeImageActivity extends AppCompatActivity {
    @BindView(R.id.rl_layout)
    RelativeLayout rl_layout;

    @BindView(R.id.pgBar)
    ProgressBar pgBar;

    @BindView(R.id.btnLanjutkan)
    Button btnLanjutkan;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    SlidingImageAdapter sAdapter;
    @BindView(R.id.pager)
    ViewPager pager;

    @BindView(R.id.rl_layout_header)
    RelativeLayout rl_layout_header;

    @BindView(R.id.rl_layout_footer)
    RelativeLayout rl_layout_footer;

    @BindView(R.id.txtTotal)
    TextView txtTotal;

    String ACTIVITY = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.full_image_promo);
        ButterKnife.bind(this);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Intent intent = getIntent();
        final ArrayList<String> bitmap = new ArrayList<>(intent.getStringArrayListExtra("BitmapImage"));
        pgBar.setVisibility(View.GONE);

        sAdapter = new SlidingImageAdapter(this, bitmap);
        pager.setAdapter(sAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            float tempPositionOffset = 0;

            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtTotal.setText((position + 1) + "/" + bitmap.size());
                    }
                });
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        switch (intent.getStringExtra("CLASS"))
        {
            case Constant.FULL_IMAGE_SLIDING_INFO:
                rl_layout_header.setVisibility(View.VISIBLE);
                rl_layout_footer.setVisibility(View.GONE);
                break;
            case Constant.FULL_IMAGE_NOTIF_BIG_STYLE:
                rl_layout_header.setVisibility(View.GONE);
                rl_layout_footer.setVisibility(View.VISIBLE);
                break;
        }

        if(intent.hasExtra(Constant.FULL_IMAGE_ACTIVITY))
        {
            ACTIVITY = intent.getStringExtra(Constant.FULL_IMAGE_ACTIVITY);

        }
        //loadBigImageWithNotif task = new loadBigImageWithNotif(this, bitmap);
        //task.execute();



    }

    @OnClick({R.id.btnCancel, R.id.btnClose})
    public void onCancel()
    {
        finish();
    }

    @OnClick(R.id.btnLanjutkan)
    public void onLanjut()
    {
        EventBus.getDefault().post(ACTIVITY);
        finish();
    }



    class loadBigImageWithNotif extends AsyncTask<String, Integer, Bitmap>
    {
        String data;
        Context ctx;
        public loadBigImageWithNotif(Context contxt, String uri)
        {
            super();
            ctx = contxt;
            data = uri;
        }

        @Override
        protected Bitmap doInBackground(String... params)
        {
            try {
                URL url = new URL(data);
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
            BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
            rl_layout.setBackground(ob);
            pgBar.setVisibility(View.GONE);
        }
    }


    public class SlidingImageAdapter extends PagerAdapter {


        private ArrayList<String> IMAGES;
        private LayoutInflater layoutInflater;
        Activity activity;

        public SlidingImageAdapter() {
            super();
        }

        public SlidingImageAdapter(Activity activity,ArrayList<String> IMAGES) {
            super();
            this.activity = activity;
            this.IMAGES=IMAGES;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_placing, container, false);
            ImageView im_slider = view.findViewById(R.id.image);
            ProgressBar progressbar = view.findViewById(R.id.progressBar);
            im_slider.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Constant.loadDefaulSlideImage(this.activity.getApplicationContext(),IMAGES.get(position), im_slider, progressbar);
            container.addView(view);
            return view;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
