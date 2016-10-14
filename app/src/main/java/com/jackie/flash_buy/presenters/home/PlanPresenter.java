package com.jackie.flash_buy.presenters.home;

import com.jackie.flash_buy.contracts.home.PlanContract;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanPresenter implements PlanContract.Presenter {
    private final PlanContract.View mPlanView;


    public PlanPresenter(PlanContract.View planView) {
        mPlanView = planView;
        mPlanView.setPresenter(this); //bind
    }

    @Override
    public void start() {
        //do some init
    }
}
