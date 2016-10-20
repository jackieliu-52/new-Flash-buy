package com.jackie.flash_buy.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;

/**
 * Created by Jack on 2016/10/16.
 */
public class TestFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_frag, container, false);
    }
}
