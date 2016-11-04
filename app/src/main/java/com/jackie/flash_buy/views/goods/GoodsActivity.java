package com.jackie.flash_buy.views.goods;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.views.home.MainActivity;
import com.jackie.flash_buy.views.sales.CommentActivity;
import com.jackie.flash_buy.views.scan.ScanActivity;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Jack on 2016/10/25.
 */
public class GoodsActivity extends BaseActivity {

    public static Item item;  //当前展示的Item


    private com.facebook.drawee.view.SimpleDraweeView sdgood;
    private android.widget.TextView tvname;
    private android.widget.ImageView ivstar;
    private android.widget.TextView tvcompany;
    private android.widget.TextView tvsource;
    private android.widget.TextView tvsize;
    private android.widget.ScrollView sv;
    private android.widget.ImageButton itemcomment;
    private android.widget.ImageButton itemscan;
    private android.widget.ImageButton itemadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        this.itemadd = (ImageButton) findViewById(R.id.item_add);
        this.itemscan = (ImageButton) findViewById(R.id.item_scan);
        this.itemcomment = (ImageButton) findViewById(R.id.item_comment);
        this.sv = (ScrollView) findViewById(R.id.sv);
        this.tvsize = (TextView) findViewById(R.id.tv_size);
        this.tvsource = (TextView) findViewById(R.id.tv_source);
        this.tvcompany = (TextView) findViewById(R.id.tv_company);
        this.ivstar = (ImageView) findViewById(R.id.iv_star);
        this.tvname = (TextView) findViewById(R.id.tv_name);
        this.sdgood = (SimpleDraweeView) findViewById(R.id.sd_good);

        initUI();
    }

    /**
     * 初始化UI的各种数据
     */
    private void initUI() {
        //刷新UI
        tvname.setText(item.getName());
        tvcompany.setText(item.getCompany());
        tvsize.setText(item.getSize());
        tvsource.setText(item.getSource());
        itemadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAdd();
            }
        });

        itemcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickComment();
            }
        });
        itemscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickScan();
            }
        });
    }


    public void clickAdd(){
        MainActivity.PlanBuy.add(new TwoTuple(true,item));
        EventBus.getDefault().post(new MessageEvent(item.getName() + "已加入计划购买清单"));
    }

    public void clickScan(){
        startActivity(new Intent(this, ScanActivity.class));
    }

    public void clickComment(){
        startActivity(new Intent(this, CommentActivity.class));
    }


}
