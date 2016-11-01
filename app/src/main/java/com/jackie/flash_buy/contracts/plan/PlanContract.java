package com.jackie.flash_buy.contracts.plan;

import com.jackie.flash_buy.BasePresenter;
import com.jackie.flash_buy.BaseView;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.TwoTuple;

import java.util.List;

/**
 * Created by Jack on 2016/10/15.
 */
public interface PlanContract {

    interface View extends BaseView<Presenter> {
        void setTypes(List<TwoTuple<String,Integer>> mTypes);
        void setItems(List<LineItem> lineItems);
        boolean isActive();

    }

    interface Presenter extends BasePresenter {
        List<LineItem> getItems();
        int getSelectedGroupPosition(String pid);  //根据商品的pid来获取Type的位置
        int getSelectedPosition(String pid);       //根据商品的pid来获取第一个具体LineItem的位置
        void add(LineItem lineItem);   //添加一个预购商品
        void remove(LineItem lineItem); //减少一个预购商品
        void end();  //释放资源
    }
}
