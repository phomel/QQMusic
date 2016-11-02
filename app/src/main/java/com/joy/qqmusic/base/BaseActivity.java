package com.joy.qqmusic.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.joy.qqmusic.R;

/**
 * Created by Administrator on 2016/10/6.
 */
public class BaseActivity extends AppCompatActivity {

    protected TextView tv_back;
    protected TextView title;
    protected Context context;
    protected Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    protected void initTitle() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_back = (TextView) findViewById(R.id.btn_back);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView) findViewById(R.id.title);
    }
}
