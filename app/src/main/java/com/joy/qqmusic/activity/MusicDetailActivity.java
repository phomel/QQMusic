package com.joy.qqmusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joy.qqmusic.R;
import com.joy.qqmusic.app.MusicApplication;
import com.joy.qqmusic.base.BaseActivity;
import com.joy.qqmusic.bean.Music;
import com.joy.qqmusic.service.MusicService;
import com.joy.qqmusic.util.AppConstant;
import com.joy.qqmusic.util.DensityUtils;
import com.joy.qqmusic.util.HandleBitmapUtil;
import com.joy.qqmusic.util.LrcUtil;
import com.joy.qqmusic.util.MusicUtil;
import com.joy.qqmusic.widget.RoundImageView;

/**
 * Created by Administrator on 2016/10/6.
 */
public class MusicDetailActivity extends BaseActivity {

    private SeekBar mSeekBar;
    private TextView mProgress;
    private TextView duration;
    private LocalBroadcastManager lbm;
    private Context context;
    private RoundImageView rotateIv;
    private LinearLayout background;
    private RotateAnimation animation;
    private TextView mLrc;
    private int intDuration;
    private LocalReceiver receiver;
    private Bitmap bitmap;
    private Bitmap handledBit;
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_detail);
        context = this;
        registerMyReceiver();
        initTitle();
        initSeekBar();
        initDetailPlaying();
        initViews();
    }

    private void initDetailPlaying() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.chk_detail_playing);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        Button prev = (Button) findViewById(R.id.btn_detail_prev);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicApplication.currentMusic > 0) {
                    MusicApplication.currentMusic--;
                    Music music = MusicApplication.musics.get(MusicApplication.currentMusic);
                    //更新标题
                    //更新背景
                    updateBack(music);
                    Intent intent = new Intent(context, MusicService.class);
                    intent.putExtra("musicStatus", "start");
                    intent.putExtra("path", music.getPath());
                    startService(intent);
                } else {
                    Toast.makeText(context, getResources().getText(R.string.toast_error_prev_tip), Toast.LENGTH_SHORT);
                }
            }
        });
        Button next = (Button) findViewById(R.id.btn_detail_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MusicApplication.currentMusic < MusicApplication.musics.size() - 1) {
                    MusicApplication.currentMusic++;
                    Music music = MusicApplication.musics.get(MusicApplication.currentMusic);
                    //更新标题
                    //更新背景
                    updateBack(music);
                    Intent intent = new Intent(context, MusicService.class);
                    intent.putExtra("musicStatus", "start");
                    intent.putExtra("path", music.getPath());
                    startService(intent);
                } else {
                    Toast.makeText(context, getResources().getText(R.string.toast_error_next_tip), Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void updateBack(Music music) {
        title.setText(music.getTitle());
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (handledBit.isRecycled()) {
            handledBit.recycle();
        }
        bitmap = music.getAlbumIv();
        handledBit = HandleBitmapUtil.fastblur(bitmap, 40);
        Drawable drawable = new BitmapDrawable(handledBit);
        background.setBackgroundDrawable(drawable);
        rotateIv.setImageBitmap(bitmap);
        rotateIv.startAnimation(animation);
    }

    private void initSeekBar() {
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mProgress.setText(DensityUtils.int2stringFormat(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                intent = new Intent(context, MusicService.class);
                intent.putExtra("musicStatus", "progress");
                intent.putExtra("progress", seekBar.getProgress());
                startService(intent);
            }
        });
        mProgress = (TextView) findViewById(R.id.progress_duration);
        duration = (TextView) findViewById(R.id.duration);
    }

    private void initViews() {
        setResult(1);
        //toolbar
        mToolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tv_back.setBackgroundResource(R.mipmap.btn_menu_show_as_drop_dow);
        title.setText(MusicApplication.musics.get(MusicApplication.currentMusic).getTitle());
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //导航栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //状态栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        bitmap = MusicApplication.musics.get(MusicApplication.currentMusic).getAlbumIv();
        handledBit = HandleBitmapUtil.fastblur(bitmap, 40);
        Drawable drawable = new BitmapDrawable(handledBit);
        background = (LinearLayout) findViewById(R.id.line_background);
        background.setBackgroundDrawable(drawable);
        rotateIv = (RoundImageView) findViewById(R.id.iv_rotate_album);
        rotateIv.setImageBitmap(bitmap);
        intDuration = MusicApplication.musics.get(MusicApplication.currentMusic).getDuration();
        animation = new RotateAnimation(0f, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(30000L);
        animation.setRepeatCount(intDuration / 30000);
        animation.setInterpolator(new LinearInterpolator());
        rotateIv.startAnimation(animation);
        //seekbar初始值
        duration.setText(DensityUtils.int2stringFormat(intDuration));
        mSeekBar.setMax(intDuration);
        mLrc = (TextView) findViewById(R.id.tv_lrc);
    }

    private void registerMyReceiver() {
        lbm = LocalBroadcastManager.getInstance(this);
        receiver = new LocalReceiver();
        lbm.registerReceiver(receiver, new IntentFilter(AppConstant.MUSIC_RECEIVER));
    }

    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int i = intent.getIntExtra("progress", -1);
            updateProgress(i);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_music, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    //设置歌词
    public void setMiniLrc(String lrcStr) {
        mLrc.setText(lrcStr);
    }


    private void updateProgress(int i) {
        mSeekBar.setProgress(i);
        mProgress.setText(DensityUtils.int2stringFormat(i));
        //刷新歌词
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lbm.unregisterReceiver(receiver);
    }
}
