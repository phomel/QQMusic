package com.joy.qqmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import com.joy.qqmusic.util.AppConstant;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/10/6.
 */
public class MusicService extends Service {

    private MediaPlayer mMediaPlayer;
    private LocalBroadcastManager lbm;
    private Intent intentReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });
        lbm = LocalBroadcastManager.getInstance(this);
        intentReceiver = new Intent(AppConstant.MUSIC_RECEIVER);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String musicStatus = intent.getStringExtra("musicStatus");
        switch (musicStatus) {
            case "start":
                String path = intent.getStringExtra("path");
                play(path);
                break;
            case "pause":
                pause();
                break;
            case "stop":
                stop();
                break;
            case "continueMusic":
                continueMusic();
                break;
            case "progress":
                int i = intent.getIntExtra("progress", -1);
                setProgress(i);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (lbm!=null){
                    intentReceiver.putExtra("progress",mMediaPlayer.getCurrentPosition());
                    lbm.sendBroadcast(intentReceiver);
                }

            }
        },0,1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void setProgress(int i) {
        mMediaPlayer.seekTo(i);
    }

    private void play(String path) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    private void continueMusic() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
        }
    }
}
