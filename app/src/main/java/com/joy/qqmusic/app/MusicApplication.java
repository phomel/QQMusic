package com.joy.qqmusic.app;

import android.app.Application;
import android.util.Log;

import com.joy.qqmusic.bean.Music;
import com.joy.qqmusic.util.AppConstant;
import com.joy.qqmusic.util.file.SerializableUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/31.
 */
public class MusicApplication extends Application{

    public static List<Music> musics;
    public static int currentMusic = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        musics = new ArrayList<>();
    }
}
