package com.jackie.flash_buy.views.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.contracts.home.PlanContract;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanFragment extends BaseFragment implements PlanContract.View {

    private PlanContract.Presenter mPresenter;

    @Override
    public void setPresenter(@NonNull PlanContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.plan_frag, container, false);

        return root;
    }
}
