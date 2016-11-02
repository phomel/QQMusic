package com.joy.qqmusic.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joy.qqmusic.R;
import com.joy.qqmusic.app.MusicApplication;
import com.joy.qqmusic.base.BaseActivity;
import com.joy.qqmusic.bean.Music;
import com.joy.qqmusic.service.MusicService;
import com.joy.qqmusic.util.AppConstant;
import com.joy.qqmusic.util.MusicUtil;
import com.joy.qqmusic.adapter.LocalMusicAdapter;
import com.joy.qqmusic.util.file.SerializableUtils;
import com.joy.qqmusic.widget.RoundImageView;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ListView localMusicList;
    private TextView tip;
    private ProgressDialog dialog;
    private LinearLayout bottomMusicDetail;
    private TextView musicTitle;
    private TextView musicArtist;
    private CheckBox chk;
    private int count = 0;
    private RoundImageView ivAlbum;
    private RotateAnimation animation;
    private LocalMusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkedpermission();
        initTitle();
        initViews();
        if (MusicApplication.musics.isEmpty()) {
            new MyAsyncTask().execute();
        } else {
            initList();
        }
    }

    /**
     * 初始化列表(列表相关)
     */
    private void initList() {
        //判断是否存在本地歌曲即musicList是否存在数据
        if (MusicApplication.musics.size() == 0) {
            localMusicList.setVisibility(View.GONE);
            tip.setText(R.string.txt_no_music_exist);
            tip.setVisibility(View.VISIBLE);
        } else {
            adapter = new LocalMusicAdapter(context, MusicApplication.musics);
            localMusicList.setAdapter(adapter);
            localMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MusicApplication.currentMusic = position;
                    Music music = MusicApplication.musics.get(position);
                    if (!music.isPlaying()) {
                        //设置底部信息
                        ivAlbum.setImageBitmap(music.getAlbumIv());
                        animation.setRepeatCount(music.getDuration()/10000);
                        ivAlbum.startAnimation(animation);
                        musicTitle.setText(music.getTitle());
                        musicArtist.setText(music.getArtist());
                        bottomMusicDetail.setVisibility(View.VISIBLE);
                        //更新listView
                        View playing = localMusicList.findViewWithTag(position);
                        if (count != position) {
                            MusicApplication.musics.get(count).setPlaying(false);
                        }
                        playing.setVisibility(View.VISIBLE);
                        count = position;
                        music.setPlaying(true);
                        adapter.notifyDataSetChanged();
                        //播放音乐
                        Intent intent = new Intent(context, MusicService.class);
                        intent.putExtra("musicStatus", "start");
                        intent.putExtra("path", music.getPath());
                        startService(intent);
                    } else {
                        //跳转
                        Intent intent = new Intent(context, MusicDetailActivity.class);
                        startActivityForResult(intent, 0);
                    }
                }
            });
        }
    }

    public void forwardMusicDetailActivity(View view) {
        //跳转
                Intent intent = new Intent(context, MusicDetailActivity.class);
                startActivityForResult(intent, 0);
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage(getString(R.string.dialog_scan_musics));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MusicUtil.getMusics(context);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            initList();
        }
    }

    //判断是否需要赋予权限
    private void checkedpermission() {
        //sdk>=6.0手动赋予权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    //初始化标题
    private void initViews() {
        //toolbar
        title.setText(R.string.txt_local_music);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        //底部菜单
        localMusicList = (ListView) findViewById(R.id.list_view_music);
        tip = (TextView) findViewById(R.id.tv_tip);
        bottomMusicDetail = (LinearLayout) findViewById(R.id.line_bottom_music_detail);
        musicTitle = (TextView) findViewById(R.id.tv_music_title);
        musicArtist = (TextView) findViewById(R.id.tv_music_artist);
        ivAlbum = (RoundImageView) findViewById(R.id.iv_album);
        chk = (CheckBox) findViewById(R.id.chk_playing);
        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent;
                if (isChecked) {
                    intent = new Intent(context, MusicService.class);
                    intent.putExtra("musicStatus", "pause");
                    startService(intent);
                } else {
                    intent = new Intent(context, MusicService.class);
                    intent.putExtra("musicStatus", "continueMusic");
                    startService(intent);
                }
            }
        });
        //动画
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(10000L);
    }

    //初始化菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意赋予权限
                //初始化数据
            } else {
                localMusicList.setVisibility(View.GONE);
                tip.setText(R.string.txt_permission_denied_tip);
                tip.setVisibility(View.VISIBLE);
                //不同意赋予权限,页面显示没有访问sd卡的权限
            }
        }
    }

    //解决菜单栏不显示的问题
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    /**
     * 移到后台运行
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 1) {
            Music music = MusicApplication.musics.get(MusicApplication.currentMusic);
            View playing = localMusicList.findViewWithTag(MusicApplication.currentMusic);
            if (count != MusicApplication.currentMusic) {
                MusicApplication.musics.get(count).setPlaying(false);
            }
            playing.setVisibility(View.VISIBLE);
            count = MusicApplication.currentMusic;
            music.setPlaying(true);
            adapter.notifyDataSetChanged();
            ivAlbum.setImageBitmap(music.getAlbumIv());
            ivAlbum.startAnimation(animation);
            musicTitle.setText(music.getTitle());
            musicArtist.setText(music.getArtist());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
