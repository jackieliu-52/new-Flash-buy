package com.jackie.flash_buy.contracts.home;

import com.jackie.flash_buy.BasePresenter;
import com.jackie.flash_buy.BaseView;
import com.jackie.flash_buy.model.TwoTuple;

import java.util.List;

/**
 * Created by Jack on 2016/10/15.
 */
public interface PlanContract {

    interface View extends BaseView<Presenter> {
        void setTypes(List<TwoTuple<String,Integer>> mTypes);
        boolean isActive();
    }

    interface Presenter extends BasePresenter {

    }
}
