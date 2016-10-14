package com.jackie.flash_buy;

import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.leakcanary.LeakCanary;

import java.io.File;

/**
 * Created by Jack on 2016/10/14.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);           //检测一下内存泄露

        //设置Fresco的缓存路径和大小
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), "image"))
                .setBaseDirectoryName("fresco_disk")
                .setMaxCacheSize(200 * 1024 * 1024)//200MB
                .build();

        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, imagePipelineConfig);

    }
}
