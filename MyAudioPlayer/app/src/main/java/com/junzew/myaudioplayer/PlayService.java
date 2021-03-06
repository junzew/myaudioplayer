package com.junzew.myaudioplayer;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

public class PlayService extends Service implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mMediaPlayer = null;
    private IBinder mBinder = new PlayBinder();

    public PlayService() {
    }

    public class PlayBinder extends Binder {
        Service getService() {
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
       return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSelf();
            }
        });
        long id = intent.getLongExtra("id", 0);
        play(id);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    // play a song with id
    public void play(long id) {
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), contentUri);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        mMediaPlayer.pause();
    }
    public void resume() {
        mMediaPlayer.start();
    }
    public void stop() {
        mMediaPlayer.stop();
    }

    @Override
    public void onDestroy() {
        Log.i("PlayService", "onDestroy");
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
    }
}
