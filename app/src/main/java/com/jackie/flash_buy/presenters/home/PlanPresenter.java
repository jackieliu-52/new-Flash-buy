package com.jackie.flash_buy.presenters.home;

import com.jackie.flash_buy.contracts.home.PlanContract;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.views.home.MainActivity;
import com.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanPresenter implements PlanContract.Presenter {
    private final PlanContract.View mPlanView;
    //因为fragment是单例模式，所以这里也是单例模式
    private static PlanPresenter instance = null;


    private List<TwoTuple<String, Integer>> mTypes;  //种类
    private List<LineItem> mLineItems;   //种类对应的商品


    /**
     * 对外接口
     *
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
        mTypes = new ArrayList<>();
        mLineItems = new ArrayList<>();
    }

    @Override
    public void start() {
        //do some init
        initTestDatas();
        mPlanView.setTypes(mTypes);
        mPlanView.setItems(mLineItems);
    }

    private void initTestDatas() {
        mTypes = new ArrayList<>();
        mLineItems = new ArrayList<>();

        mTypes.add(new TwoTuple<String, Integer>("蔬菜", 0));
        mTypes.add(new TwoTuple<String, Integer>("零食", 0));

        mLineItems.add(new LineItem(new Item("苹果", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));
        mLineItems.add(new LineItem(new Item("苹果1", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));
        mLineItems.add(new LineItem(new Item("苹果2", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));
        mLineItems.add(new LineItem(new Item("苹果3", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));
        mLineItems.add(new LineItem(new Item("苹果4", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));
        mLineItems.add(new LineItem(new Item("苹果5", "蔬菜", "", "", "垃圾公司", 3.22, "没有规格"),1));

        mLineItems.add(new LineItem(new Item("巧克力", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果1", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果2", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果3", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果4", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果5", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果6", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果7", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));
        mLineItems.add(new LineItem(new Item("糖果8", "零食", "", "", "垃圾公司", 2.33, "没有规格"),2));

    }

    @Override
    public List<LineItem> getItems() {
        return mLineItems;
    }

    @Override
    public int getSelectedGroupPosition(String pid) {
        for (int i = 0; i < mTypes.size(); i++) {
            if (pid.equals(mTypes.get(i).first)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public int getSelectedPosition(String pid) {
        for (int i = 0; i < mLineItems.size(); i++) {
            if (pid.equals(mLineItems.get(i).getItem().getPid()))
                return i;
        }
        return 0;
    }

    @Override
    public void add(LineItem lineItem) {
        boolean exist = false;
        //先看看之前有没有加入
        for (TwoTuple<Boolean,LineItem> lineItem1 : MainActivity.PlanBuy) {
            if (lineItem1.second.getGoods_ID().equals(lineItem.getGoods_ID())) {
                exist = true;
                lineItem1.second.addNum(); //数量加一
                break;
            }
        }
        if (!exist) {
            MainActivity.PlanBuy.add(new TwoTuple<Boolean,LineItem>(true,lineItem));
        }
        //不管之前怎么处理，add肯定需要将该大品类的数量加1，然后通知Adapter刷新UI
        addTypeNum(lineItem.getItem().getPid());

    }

    private void addTypeNum(String type) {
        for(int i = 0 ; i < mTypes.size();i++){
            if (mTypes.get(i).first.equals(type)) {
                mTypes.set(i,new TwoTuple<>(type, mTypes.get(i).second + 1));
                //mPlanView.setTypes(mTypes); //通知UI刷新
                return;
            }
        }
//        for (TwoTuple<String, Integer> item : mTypes) {
//
//        }
        Log.e("PlanPresenter", "没有这个Type可以add");
    }

    @Override
    public void remove(LineItem lineItem) {
        //遍历删除
        for (TwoTuple<Boolean,LineItem> lineItem1 : MainActivity.PlanBuy) {
            if (lineItem1.second.getGoods_ID().equals(lineItem.getGoods_ID())) {
                lineItem1.second.minusNum(); //数量减一
                removeTypeNum(lineItem.getItem().getPid());
                return;
            }
        }
        Log.e("PlanPresenter", "没有LineItem可以remove");

    }

    private void removeTypeNum(String type) {
        for(int i = 0 ; i < mTypes.size();i++) {
            if (mTypes.get(i).first.equals(type)) {
                mTypes.set(i,new TwoTuple<>(type, mTypes.get(i).second - 1));
                return;
            }
        }
        Log.e("PlanPresenter", "没有这个Type可以remove");
    }
}
