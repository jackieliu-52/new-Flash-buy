package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by Jack on 2016/11/1.
 */
public class BannerImageLoader implements ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        Picasso.with(context).load((String) path).into(imageView);
    }
}
