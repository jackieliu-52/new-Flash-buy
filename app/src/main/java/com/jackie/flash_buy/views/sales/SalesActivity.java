package com.jackie.flash_buy.views.sales;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.utils.activity.ActivityUtils;

/**
 * Created by Jack on 2016/11/3.
 */
public class SalesActivity extends BaseActivity {
    Fragment_cuxiao mFragment_cuxiao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("促销信息");   //设置标题
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mFragment_cuxiao = (Fragment_cuxiao) (getSupportFragmentManager().findFragmentById(R.id.saleContentFrame));

        if(mFragment_cuxiao ==null){
            mFragment_cuxiao = new Fragment_cuxiao();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment_cuxiao, R.id.saleContentFrame);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
