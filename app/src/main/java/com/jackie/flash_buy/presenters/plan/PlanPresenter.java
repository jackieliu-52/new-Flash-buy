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

        mTypes.add(new TwoTuple<>("生鲜水果", 0));
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



        Item item19 = new Item();
        item19.setName("河北皇冠梨");
        item19.setPrice(29);
        item19.setImage("http://obsyvbwp3.bkt.clouddn.com/151.JPG");
        item19.setIid("1510");
        item19.setPid("生鲜水果");
        item19.setSource("中国");
        item19.setSize("9*200g");



        Item item20 = new Item();
        item20.setName("泰国椰青");
        item20.setPrice(32);
        item20.setImage("http://obsyvbwp3.bkt.clouddn.com/152.JPG");
        item20.setIid("1520");
        item20.setPid("生鲜水果");
        item20.setSource("中国");
        item20.setSize("1.4kg");

        Item item21 = new Item();
        item21.setName("海南西周蜜瓜");
        item21.setPrice(25);
        item21.setImage("http://obsyvbwp3.bkt.clouddn.com/153.JPG");
        item21.setIid("1530");
        item21.setPid("生鲜水果");
        item21.setSource("中国");
        item21.setSize("1.5kg");

        Item item22 = new Item();
        item22.setName("海南三亚苹果芒");
        item22.setPrice(29.9);
        item22.setImage("http://obsyvbwp3.bkt.clouddn.com/155.JPG");
        item22.setIid("1550");
        item22.setPid("生鲜水果");
        item22.setSource("中国");
        item22.setSize("1kg");

        Item item23 = new Item();
        item23.setName("智利牛油果");
        item23.setPrice(118);
        item23.setImage("http://obsyvbwp3.bkt.clouddn.com/154.JPG");
        item23.setIid("1540");
        item23.setPid("生鲜水果");
        item23.setSource("中国");
        item23.setSize("1kg");

        mLineItems.add(new LineItem(item19,5));
        mLineItems.add(new LineItem(item20,5));
        mLineItems.add(new LineItem(item21,5));
        mLineItems.add(new LineItem(item22,5));
        mLineItems.add(new LineItem(item23,5));

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
