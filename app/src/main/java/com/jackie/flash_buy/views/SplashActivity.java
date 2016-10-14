package com.jackie.flash_buy.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.utils.Util;
import com.jackie.flash_buy.views.home.MainActivity;

/**
 * Created by Jack on 2016/10/14.
 */
public class SplashActivity extends BaseActivity {
    SimpleDraweeView dvSpalash;

    TextView tvAuthor;

    private Context mContext;
    private Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash);
        dvSpalash = (SimpleDraweeView) findViewById(R.id.dv_spalash);
        tvAuthor = (TextView) findViewById(R.id.tv_author) ;
        mContext = SplashActivity.this;
        init();
    }

    private void init() {
        initAnimation();
    }

    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        int resId = Util.stringToId(mContext,"spalash_pic");
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(resId))
                .build();
        dvSpalash.setImageURI(uri);
        tvAuthor.setText("jack");
        //启动动画
        dvSpalash.startAnimation(animation);
    }
}
