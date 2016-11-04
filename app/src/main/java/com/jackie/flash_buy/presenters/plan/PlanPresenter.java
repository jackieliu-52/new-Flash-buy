package com.jackie.flash_buy.presenters.plan;

import com.jackie.flash_buy.contracts.plan.PlanContract;
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


        mTypes.add(new TwoTuple<>("零食区域", 0));
        mTypes.add(new TwoTuple<>("酒水饮料", 0));
        mTypes.add(new TwoTuple<>("电器家居", 0));



        mLineItems.add(new LineItem(new Item("张君雅小妹妹串烧休闲丸子", "零食区域", "324", "http://obsyvbwp3.bkt.clouddn.com/zhangjunya.JPG", "张君雅公司", 6.3, "60g"),3));
        mLineItems.add(new LineItem(new Item("三只松鼠夏威夷果","零食区域","0202","http://obsyvbwp3.bkt.clouddn.com/134.JPG","夏威夷公司",9.00,"一瓶"),3));
        mLineItems.add(new LineItem(new Item("乐事无限薯片三连罐","零食区域","0303","http://obsyvbwp3.bkt.clouddn.com/135.JPG","乐事薯片",4.00,"DT3200"),3));
        mLineItems.add(new LineItem(new Item("Aji泰氏风味榴莲饼","零食区域","0303","http://obsyvbwp3.bkt.clouddn.com/136.JPG","心相印",4.00,"DT3200"),3));


        mLineItems.add(new LineItem(new Item("怡宝矿泉水", "酒水饮料", "1101", "http://obsyvbwp3.bkt.clouddn.com/yibao.jpg", "怡宝有限公司", 2, "555ml"),1));
        mLineItems.add(new LineItem(new Item("安慕希酸奶", "酒水饮料", "133", "http://obsyvbwp3.bkt.clouddn.com/133.jpg", "伊利公司", 3.22, "205g"),1));
        Item test3 = new Item();
        test3.setName("养乐多");
        test3.setPrice(11);
        test3.setImage("http://obsyvbwp3.bkt.clouddn.com/yangleduo.jpg");
        test3.setIid("121");
        test3.setPid("酒水饮料");
        test3.setSource("中国");
        test3.setSize("80g");
        mLineItems.add(new LineItem(test3,1));





        Item item26 = new Item();
        item26.setName("小熊SNJ-A10K5酸奶机");
        item26.setPrice(149);
        item26.setImage("http://obsyvbwp3.bkt.clouddn.com/161.JPG");
        item26.setIid("1610");
        item26.setPid("电器家居");
        item26.setSource("中国");
        item26.setSize("8分杯内胆");

        mLineItems.add(new LineItem(item26,4));


        Item item27 = new Item();
        item27.setName("九阳JYY-50YL1智能电压力锅");
        item27.setPrice(199);
        item27.setImage("http://obsyvbwp3.bkt.clouddn.com/162.JPG");
        item27.setIid("1620");
        item27.setPid("电器家居");
        item27.setSource("中国");
        item27.setSize("5L");

        mLineItems.add(new LineItem(item27,4));

        Item item28 = new Item();
        item28.setName("美的MJ-BL25B3料理机果汁机电动搅拌机");
        item28.setPrice(199);
        item28.setImage("http://obsyvbwp3.bkt.clouddn.com/163.JPG");
        item28.setIid("1630");
        item28.setPid("电器家居");
        item28.setSource("中国");
        item28.setSize("2.7kg");

        mLineItems.add(new LineItem(item28,4));
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
            lineItem.addNum();
           // Log.i("TG",lineItem.getNum());
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
                if(lineItem1.second.getNum() <= 0){
                    //如果数量归零删除这个
                    MainActivity.PlanBuy.remove(lineItem1);  //删除这个商品
                }
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
    @Override
    public void end(){
        instance = null; //需要释放掉
    }

    @Override
    public double unitPrice(){
        double sum = 0;
        for(TwoTuple<Boolean,LineItem> lineItem1 : MainActivity.PlanBuy){
            LineItem item = lineItem1.second;
            sum += item.getUnitPrice();
        }
        return  sum;
    }
}
