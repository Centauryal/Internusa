package com.supersoft.internusa.helper.imagePicker;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.supersoft.internusa.helper.util.Constant;
import com.supersoft.internusa.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import static com.supersoft.internusa.R.anim.abc_fade_in;
import static com.supersoft.internusa.R.anim.abc_fade_out;

/**
 * Created by Centaury on 19/04/2018.
 */
public class AlbumSelectActivity extends HelperActivity {
    private ArrayList<Album> albums;

    private TextView errorDisplay, tvProfile;
    private LinearLayout liFinish;

    private ProgressBar loader;
    private GridView gridView;
    private CustomAlbumSelectAdapter adapter;

    private ActionBar actionBar;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_select);
        setView(findViewById(R.id.layout_album_select));

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        Constant.limit = intent.getIntExtra(Constant.INTENT_EXTRA_LIMIT, Constant.DEFAULT_LIMIT);

        errorDisplay = findViewById(R.id.text_view_error);
        errorDisplay.setVisibility(View.INVISIBLE);

        tvProfile = findViewById(R.id.tvProfile);
        tvProfile.setText(R.string.album_view);
        liFinish = findViewById(R.id.liFinish);

        loader = findViewById(R.id.loader);
        gridView = findViewById(R.id.grid_view_album_select);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (albums.get(position).name.equals(getString(R.string.capture_photo))) {
                    //HelperClass.displayMessageOnScreen(getApplicationContext(), "HMM!", false);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ImageSelectActivity.class);
                    intent.putExtra(Constant.INTENT_EXTRA_ALBUM, albums.get(position).name);
                    startActivityForResult(intent, Constant.REQUEST_CODE);
                }
            }
        });

        liFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(abc_fade_in, abc_fade_out);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constant.PERMISSION_GRANTED: {
                        loadAlbums();
                        break;
                    }

                    case Constant.FETCH_STARTED: {
                        loader.setVisibility(View.VISIBLE);
                        gridView.setVisibility(View.INVISIBLE);
                        break;
                    }

                    case Constant.FETCH_COMPLETED: {
                        if (adapter == null) {
                            adapter = new CustomAlbumSelectAdapter(AlbumSelectActivity.this, getApplicationContext(), albums);
                            gridView.setAdapter(adapter);

                            loader.setVisibility(View.GONE);
                            gridView.setVisibility(View.VISIBLE);
                            orientationBasedUI(getResources().getConfiguration().orientation);

                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        break;
                    }

                    case Constant.ERROR: {
                        loader.setVisibility(View.GONE);
                        errorDisplay.setVisibility(View.VISIBLE);
                        break;
                    }

                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                loadAlbums();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);

        checkPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopThread();

        getContentResolver().unregisterContentObserver(observer);
        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                actionBar.setHomeAsUpIndicator(null);
            }
        }
        albums = null;
        if (adapter != null) {
            adapter.releaseResources();
        }
        gridView.setOnItemClickListener(null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    private void orientationBasedUI(int orientation) {
        final WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        if (adapter != null) {
            int size = orientation == Configuration.ORIENTATION_PORTRAIT ? metrics.widthPixels / 2 : metrics.widthPixels / 4;
            adapter.setLayoutParams(size);
        }
        gridView.setNumColumns(orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        overridePendingTransition(abc_fade_in, abc_fade_out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_CODE
                && resultCode == RESULT_OK
                && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                onBackPressed();
                return true;
            }

            default: {
                return false;
            }
        }
    }

    private void loadAlbums() {
        startThread(new AlbumLoaderRunnable());
    }

    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (adapter == null) {
                sendMessage(Constant.FETCH_STARTED);
            }

            //Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,null, null, MediaStore.Images.Media.DATE_MODIFIED);

            MergeCursor cursor = new MergeCursor(new Cursor[]{
                    getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null),
                    getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null),
                    getApplicationContext().getContentResolver().query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection, null, null, null),
                    getApplicationContext().getContentResolver().query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, projection, null, null, null)
            });
            if (cursor == null) {
                sendMessage(Constant.ERROR);
                return;
            }

            ArrayList<Album> temp = new ArrayList<>(cursor.getCount());
            HashSet<Long> albumSet = new HashSet<>();
            File file;
            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long albumId = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String album = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String image = cursor.getString(cursor.getColumnIndex(projection[2]));

                    if (!albumSet.contains(albumId)) {
                        /*
                        It may happen that some image file paths are still present in cache,
                        though image file does not exist. These last as long as media
                        scanner is not run again. To avoid get such image file paths, check
                        if image file exists.
                         */
                        file = new File(image);
                        if (file.exists()) {

                            temp.add(new Album(album, image));

                            /*if (!album.equals("Hiding particular folder")) {
                                temp.add(new Album(album, image));
                            }*/
                            albumSet.add(albumId);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();

            if (albums == null) {
                albums = new ArrayList<>();
            }
            albums.clear();
            // adding taking photo from camera option!
            /*albums.add(new Album(getString(R.string.capture_photo),
                    "https://image.freepik.com/free-vector/flat-white-camera_23-2147490625.jpg"));*/
            albums.addAll(temp);

            sendMessage(Constant.FETCH_COMPLETED);
        }
    }

    private void startThread(Runnable runnable) {
        stopThread();
        thread = new Thread(runnable);
        thread.start();
    }

    private void stopThread() {
        if (thread == null || !thread.isAlive()) {
            return;
        }

        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int what) {
        if (handler == null) {
            return;
        }

        Message message = handler.obtainMessage();
        message.what = what;
        message.sendToTarget();
    }

    @Override
    protected void permissionGranted() {
        Message message = handler.obtainMessage();
        message.what = Constant.PERMISSION_GRANTED;
        message.sendToTarget();
    }

    @Override
    protected void hideViews() {
        loader.setVisibility(View.GONE);
        gridView.setVisibility(View.INVISIBLE);
    }
}
