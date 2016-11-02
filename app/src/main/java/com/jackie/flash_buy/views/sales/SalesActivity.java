package com.jackie.flash_buy.views.sales;

import android.os.Bundle;

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

        mFragment_cuxiao = (Fragment_cuxiao) (getSupportFragmentManager().findFragmentById(R.id.saleContentFrame));

        if(mFragment_cuxiao ==null){
            mFragment_cuxiao = new Fragment_cuxiao();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mFragment_cuxiao, R.id.saleContentFrame);
        }
    }
}
