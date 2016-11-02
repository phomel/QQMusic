package com.joy.qqmusic.bean;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/10/6.
 */
public class Music implements Serializable{

    private String title, artist, path, durStr;
    private int duration;
    private Bitmap albumIv;
    private boolean playing;

    public String getDurStr() {
        return durStr;
    }

    public void setDurStr(String durStr) {
        this.durStr = durStr;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Bitmap getAlbumIv() {
        return albumIv;
    }

    public void setAlbumIv(Bitmap albumIv) {
        this.albumIv = albumIv;
    }
}
