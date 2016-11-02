package com.joy.qqmusic.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.ContentUris;

import com.joy.qqmusic.R;
import com.joy.qqmusic.activity.MusicDetailActivity;
import com.joy.qqmusic.app.MusicApplication;
import com.joy.qqmusic.bean.Music;
import com.joy.qqmusic.util.file.SerializableUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MusicUtil {

    static int[] image = {R.mipmap.img_01, R.mipmap.img_02, R.mipmap.img_03,
            R.mipmap.img_04, R.mipmap.img_05};
    static Random random = new Random();

    public static List<Music> getMusics(Context context) {

        //音乐路径
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //标题, 歌手, 路径, 时长, 专辑id(获取封面)
        String[] projects = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media._ID
        };
        Cursor cursor = context.getContentResolver().query(uri, projects, null, null, null);
        //断言判断
        assert cursor != null;
        Music music;
        while (cursor.moveToNext()) {
            music = new Music();
            music.setTitle(cursor.getString(cursor.getColumnIndex(projects[0])));
            music.setArtist(cursor.getString(cursor.getColumnIndex(projects[1])));
            music.setPath(cursor.getString(cursor.getColumnIndex(projects[2])));
            music.setDuration(cursor.getInt(cursor.getColumnIndex(projects[3])));
            String albumId = cursor.getString(cursor.getColumnIndex(projects[4]));
            music.setAlbumIv(getAlbumIv(context, albumId));
            music.setDurStr(DensityUtils.int2stringFormat(music.getDuration()));
            if (music.getDuration() > 50000) {
                MusicApplication.musics.add(music);
            }
        }
        cursor.close();
        return MusicApplication.musics;
    }

    private static Bitmap getAlbumIv(Context context, String albumId) {
        String album = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
                + "/" + albumId;
        Uri uri = Uri.parse(album);
        String[] projects = {
                MediaStore.Audio.Albums.ALBUM_ART
        };
        Cursor cursor = context.getContentResolver().query(uri, projects, null, null, null);
        assert cursor != null;
        if (cursor.moveToNext()) {
            String art = cursor.getString(cursor.getColumnIndex(projects[0]));
            cursor.close();
            return BitmapFactory.decodeFile(art);
        } else {
            cursor.close();
            //没有封面则加载默认图片
            Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), image[random.nextInt(5)]);
            return bitmap;
        }
    }

    //获取专辑图片
    public static Bitmap setArtwork(Context context, String url) {
        Uri selectedAudio = Uri.parse(url);
        MediaMetadataRetriever myRetriever = new MediaMetadataRetriever();
        myRetriever.setDataSource(context, selectedAudio); // the URI of audio file
        byte[] artwork;

        artwork = myRetriever.getEmbeddedPicture();

        if (artwork != null) {
            Bitmap bMap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
            return bMap;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(
                    context.getResources(), image[random.nextInt(5)]);
            return bitmap;
        }
    }

    public static LrcUtil parseLrc(MusicDetailActivity activity){
        LrcUtil lrcUtil = new LrcUtil(activity);
        String path = MusicApplication.musics.get(MusicApplication.currentMusic).getPath();
        String replace = path.replace(".mp3", ".lrc");
        File f = new File(replace);
        if (f.exists()){
            lrcUtil.ReadLRC(f);
        }
        return lrcUtil;
    }
}
