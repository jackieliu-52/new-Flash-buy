package com.jackie.flash_buy.presenters.home;

import com.jackie.flash_buy.contracts.home.PlanContract;
import com.jackie.flash_buy.model.TwoTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanPresenter implements PlanContract.Presenter {
    private final PlanContract.View mPlanView;
    //因为fragment是单例模式，所以这里也是单例模式
    private static PlanPresenter instance = null;


    private List<TwoTuple<String,Integer>>  mTypes;  //种类

    /**
     * 对外接口
     * @return PlanPresenter
     */
    public static PlanPresenter GetInstance(PlanContract.View planView) {
        if (instance == null)
            instance = new PlanPresenter(planView);
        return instance;
    }

    public PlanPresenter(PlanContract.View planView) {
        mPlanView = planView;
        mPlanView.setPresenter(this); //bind
    }

    @Override
    public void start() {
        //do some init
        initTestDatas();
        mPlanView.setTypes(mTypes);
    }

    private void initTestDatas() {
        mTypes = new ArrayList<>();
        mTypes.add(new TwoTuple<String, Integer>("蔬菜",0));
        mTypes.add(new TwoTuple<String, Integer>("肉类",0));
    }
}
