package com.junzew.myaudioplayer;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.junzew.myaudioplayer.MainActivity.ARTWORK_URI;

public class PlayActivity extends AppCompatActivity {

    private PlayService mService = null;
    private boolean mBound = false;
    private boolean isPlaying = false;
    @BindView(R.id.play_button) ImageView img;
    @BindView(R.id.album) ImageView albumArt;
    @BindView(R.id.song_title) TextView title;

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            PlayService.PlayBinder binder = (PlayService.PlayBinder) service;
            mService = (PlayService) binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initViews();
        bindPlayService();
    }

    private void bindPlayService() {
        Intent intent = new Intent(this, PlayService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        isPlaying = true;
    }

    private void initViews() {
        ButterKnife.bind(this);
        Intent i = getIntent();
        long albumId = i.getLongExtra("albumId", 0);
        Uri uri = ContentUris.withAppendedId(ARTWORK_URI, albumId);
        Picasso.with(PlayActivity.this)
                .load(uri)
                .placeholder(R.drawable.default_album)
                .into(albumArt);
        String songTitle = i.getStringExtra("title");
        title.setText(songTitle);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying) {
                    mService.pause();
                    Log.i("PlayActivity", "pause");
                    isPlaying = false;
                    img.setImageResource(R.drawable.play);
                } else {
                    isPlaying = true;
                    mService.resume();
                    img.setImageResource(R.drawable.pause_button);
                    Log.i("PlayActivity", "resume");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            mService.stopSelf();
            unbindService(mConnection);
            mBound = false;
        }
    }
}
