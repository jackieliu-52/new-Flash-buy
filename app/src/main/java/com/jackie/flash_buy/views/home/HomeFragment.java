package com.jackie.flash_buy.views.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.PlanTopAdapter;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.Market;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.ui.SmoothCheckBox;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerAdapter;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerViewHolder;
import com.jackie.flash_buy.ui.commonRecyclerView.DividerItemDecoration;
import com.jackie.flash_buy.ui.commonRecyclerView.RecyclerItemClickListener;
import com.jackie.flash_buy.views.plan.PlanActivity;
import com.jackie.flash_buy.views.sales.SalesActivity;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jack on 2016/11/1.
 */
public class HomeFragment extends BaseFragment {
    /**
     * 单例对象实例
     */
    private static HomeFragment instance = null;
    private Banner banner;
    private RecyclerView hometoprv;
    PlanTopAdapter mAdapter;
    private CommonRecycleView homeendrv;
    String[] images;

    List<TwoTuple<String,String>> tops;
    FloatingSearchView mSearchView;

    List<Item> mItems;
    List<Item> mItems1;
    List<Item> mItems2;
    List<Item> mItems3;
    List<Item> mItems4;

    List<Item> mTest;   //视频的所有Item

    /**
     * 对外接口
     *
     * @return HomeFragment
     */
    public static HomeFragment GetInstance() {
        if (instance == null)
            instance = new HomeFragment();
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.home_fragment, container, false);
        this.homeendrv = (CommonRecycleView) root.findViewById(R.id.home_end_rv);
        this.hometoprv = (RecyclerView) root.findViewById(R.id.home_top_rv);
        this.banner = (Banner) root.findViewById(R.id.banner);
        this.mSearchView = (FloatingSearchView) root.findViewById(R.id.floating_search_view);
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
//                mSearchView.swapSuggestions(newSuggestions);
            }
        });

        test();
        setupbanner();
        setTop();
        setBottom();
        return root;
    }


    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        banner.startAutoPlay();
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        banner.stopAutoPlay();
    }

    private void setupbanner() {
        images = getResources().getStringArray(R.array.url);  //设置轮播图

        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        banner.setImageLoader(new BannerImageLoader());
        //设置图片集合
        banner.setImages(Arrays.asList(images));
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.BackgroundToForeground);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                startActivity(new Intent(mContext, SalesActivity.class));
            }
        });
    }

    private void setBottom() {
        final List<Market> markets = new ArrayList<>();
        markets.add(new Market("Flash-Buy","智能超市","http://obsyvbwp3.bkt.clouddn.com/ic_launcher.png","活动一：","",0));
        markets.add(new Market("家乐福","中国最大的连锁超市","http://obsyvbwp3.bkt.clouddn.com/carrefour.jpg","活动一：水产7折优惠","活动二：满200送100购物券",1000));
        markets.add(new Market("沃尔玛","世界最大的连锁超市","http://obsyvbwp3.bkt.clouddn.com/walmart.jpg","活动一：","活动二：满200送100购物券",500));
        homeendrv.setAdapter(new CommonRecyclerAdapter<Market>(mContext,R.layout.rv_market_item,markets) {
            @Override
            public void convert(CommonRecyclerViewHolder holder, Market item) {
                //这里进行转化
                CircleImageView circleImageView = (CircleImageView)holder.getView(R.id.ivMarketLogo);
                if(!item.getLogo().equals("")) {
                    Picasso.with(mContext)
                            .load(item.getLogo())
                            .into(circleImageView);
                }

                //设置Logo
                holder.setText(R.id.rvTvMarketName,item.getName());
                holder.setText(R.id.rvTvMarketDes,item.getDesri());
                holder.setText(R.id.rvTvMarketDis,item.getDistance()+"m");
//                holder.setText(R.id.rvTvMarketa1,item.getActivity1());
//                holder.setText(R.id.rvTvMarketa2,item.getActivity2());
            }
        });
        homeendrv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, hometoprv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        PlanActivity.sMarkt = markets.get(position);
                        startActivity(new Intent(mContext,PlanActivity.class));
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do nothing
                    }
                })
        );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeendrv.setLayoutManager(linearLayoutManager);
        homeendrv.addItemDecoration(new DividerItemDecoration(mContext,LinearLayoutManager.VERTICAL));
    }

    private void setTop() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        hometoprv.setLayoutManager(linearLayoutManager);
        initTop();
        //设置适配器
        mAdapter = new PlanTopAdapter(mContext,tops);
        initClickEvent();
        hometoprv.setAdapter(mAdapter);
    }
    /**
     * 初始化顶部的Recycler
     */
    private void initTop(){
        tops = new ArrayList<>();
        TwoTuple<String,String> top = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/fruit_strawberry.JPG","生鲜蔬菜");
        TwoTuple<String,String> top1 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/drink_RIO.JPG","酒水饮料");
        TwoTuple<String,String> top2 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/oil.JPG","粮油副食");
        TwoTuple<String,String> top3 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/head_shoulder.JPG","美容洗护");
        tops.add(top);
        tops.add(top1);
        tops.add(top2);
        tops.add(top3);
    }


    private void initClickEvent(){
        mAdapter.setOnItemClickLitener(new PlanTopAdapter.OnItemClickLitener() {
            //打开一个Diaglog，可以选择商品
            @Override
            public void onItemClick(View view, int position) {
                //首先确认是哪个
                TwoTuple<String,String> temp = tops.get(position);
                CommonRecyclerAdapter<TwoTuple<Boolean,Item>> adapter;
                final List<TwoTuple<Boolean,Item>> items = new ArrayList<>();
                switch (temp.second){
                    case "生鲜蔬菜":
                        for(Item i: mTest){
                            items.add(new TwoTuple<>(false,i));
                        }
                        adapter = getAnotherAdapter(items);
                        new MaterialDialog.Builder(mContext)
                                .title("生鲜蔬菜")
                                .adapter(adapter, null)
                                .positiveText("加入购物车")
                                .negativeText("取消")
                                .negativeColorRes(R.color.orange_button)
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if(which == DialogAction.POSITIVE){
                                            //被选中的物品加入购物车
                                            for(TwoTuple<Boolean,Item> o: items){
                                                if(o.first) {
                                                    Item copyOne = o.second;
                                                    MainActivity.PlanBuy.add(new TwoTuple<>(true,new LineItem(copyOne)));   //加入购物车
                                                }
                                            }
                                            items.clear();  //清空整个列表
                                        }
                                    }
                                })
                                .show();
                        break;
                    case "酒水饮料":
                        for(Item i: mItems2){
                            items.add(new TwoTuple<>(false,i));
                        }
                        adapter = getAnotherAdapter(items);
                        new MaterialDialog.Builder(mContext)
                                .title("酒水饮料")
                                .adapter(adapter, null)
                                .positiveText("加入购物车")
                                .negativeText("取消")
                                .negativeColorRes(R.color.orange_button)
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if(which == DialogAction.POSITIVE){
                                            //被选中的物品加入购物车
                                            for(TwoTuple<Boolean,Item> o: items){
                                                if(o.first) {
                                                    Item copyOne = o.second;
                                                    MainActivity.PlanBuy.add(new TwoTuple<>(true,new LineItem(copyOne)));   //加入购物车
                                                }
                                            }
                                            items.clear();  //清空整个列表
                                        }
                                    }
                                })
                                .show();
                        break;
                    case "粮油副食":
                        for(Item i: mItems3){
                            items.add(new TwoTuple<>(false,i));
                        }
                        adapter = getAnotherAdapter(items);
                        new MaterialDialog.Builder(mContext)
                                .title("粮油副食")
                                .adapter(adapter, null)
                                .positiveText("加入购物车")
                                .negativeText("取消")
                                .negativeColorRes(R.color.orange_button)
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if(which == DialogAction.POSITIVE){
                                            //被选中的物品加入购物车
                                            for(TwoTuple<Boolean,Item> o: items){
                                                if(o.first) {
                                                    Item copyOne = o.second;
                                                    MainActivity.PlanBuy.add(new TwoTuple<>(true,new LineItem(copyOne)));   //加入购物车
                                                }
                                            }
                                            items.clear();  //清空整个列表
                                        }
                                    }
                                })
                                .show();
                        break;
                    case "美容洗护":
                        for(Item i: mItems4){
                            items.add(new TwoTuple<>(false,i));
                        }
                        adapter = getAnotherAdapter(items);
                        new MaterialDialog.Builder(mContext)
                                .title("美容洗护")
                                .adapter(adapter, null)
                                .positiveText("加入购物车")
                                .negativeText("取消")
                                .negativeColorRes(R.color.orange_button)
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        if(which.name().equals("POSITIVE")){
                                            String sum = "";
                                            //被选中的物品加入购物车
                                            for(TwoTuple<Boolean,Item> o: items){
                                                if(o.first) {
                                                    Item copyOne = o.second;
                                                    sum += copyOne.getName() + "    ";
                                                    MainActivity.PlanBuy.add(new TwoTuple<>(true,new LineItem(copyOne)));   //加入购物车
                                                }
                                            }
                                            items.clear();  //清空整个列表

                                            EventBus.getDefault()
                                                    .post(new MessageEvent("你点击了" + sum));
                                        }//
                                    }
                                })
                                .show();
                        break;
                    default:
                        Log.e("Fragment_plan",temp.second);
                        break;
                }

            }


        });
    }



    /**
     * 根据不同的数组获取上方四个常购商品的Adapter
     * @param items
     * @return
     */
    private CommonRecyclerAdapter getAnotherAdapter(final List<TwoTuple<Boolean,Item>> items){
        return new CommonRecyclerAdapter<TwoTuple<Boolean, Item>>(mContext,R.layout.home_plan_items,items) {
            @Override
            public void convert(final CommonRecyclerViewHolder holder, TwoTuple<Boolean, Item> o) {
                Item item = o.second;
                ((TextView)holder.getView(R.id.tv_item_name)).setTextAppearance(mContext,R.style.PlanTextStyle);
                holder.setText(R.id.tv_item_name, item.getName());

                //加载图片
                Picasso.with(mContext)
                        .load(item.getImage())
                        .placeholder(R.mipmap.ic_launcher)
                        .into(((ImageView) holder.getView(R.id.ci_image)));

                final SmoothCheckBox scb = (SmoothCheckBox) holder.getView(R.id.scb);

                holder.setOnClickListener(R.id.tv_item_name, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int pos = holder.getLayoutPosition();  //获得下标
                        items.get(pos).first = !items.get(pos).first;
                        scb.setChecked(items.get(pos).first, true);//播放动画
                    }
                });

                holder.getView(R.id.v_color).setVisibility(View.GONE);  //去除
                holder.getView(R.id.tv_item_date).setVisibility(View.GONE);  //去除

                scb.setChecked(o.first);
                scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        int pos = holder.getLayoutPosition();  //获得下标
                        items.get(pos).first = isChecked;
                        //提升一个视觉的差异
                        if(isChecked) {
                            holder.getView(R.id.v_color).setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.getView(R.id.v_color).setVisibility(View.GONE);
                        }
                    }
                });
            }
        };
    }

    /**
     * 加入测试数据
     */
    private void test(){
        mItems = new ArrayList<>();
        mItems1 = new ArrayList<>();
        mItems2 = new ArrayList<>();
        mItems3 = new ArrayList<>();
        mItems4 = new ArrayList<>();

        mTest = new ArrayList<>();

        Item item = new Item();
        item.setName("安慕希酸奶");
        item.setPrice(59.4);
        item.setImage("http://obsyvbwp3.bkt.clouddn.com/133.JPG");
        item.setIid("1330");
        item.setPid("13");
        item.setSource("中国");
        item.setSize("205g*12");

        Item item1 = new Item();
        item1.setName("三只松鼠夏威夷果");
        item1.setPrice(22.9);
        item1.setImage("http://obsyvbwp3.bkt.clouddn.com/134.JPG");
        item1.setIid("1340");
        item1.setPid("13");
        item1.setSource("中国");
        item1.setSize("265g");

        Item item2 = new Item();
        item2.setName("乐事无限薯片三连罐");
        item2.setPrice(35);
        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/135.JPG");
        item2.setIid("1350");
        item2.setPid("13");
        item2.setSource("中国");
        item2.setSize("104g*3");

        Item item3 = new Item();
        item3.setName("Aji泰氏风味榴莲饼");
        item3.setPrice(15.9);
        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/136.JPG");
        item3.setIid("1360");
        item3.setPid("13");
        item3.setSource("中国");
        item3.setSize("200g");

        Item item4 = new Item();
        item4.setName("统一老坛酸菜牛肉面");
        item4.setPrice(12.5);
        item4.setImage("http://obsyvbwp3.bkt.clouddn.com/137.JPG");
        item4.setIid("1370");
        item4.setPid("13");
        item4.setSource("中国");
        item4.setSize("121g*5");

        Item item5 = new Item();
        item5.setName("香楠玫瑰鲜花饼");
        item5.setPrice(3.9);
        item5.setImage("http://obsyvbwp3.bkt.clouddn.com/1380.JPG");
        item5.setIid("1380");
        item5.setPid("13");
        item5.setSource("中国");
        item5.setSize("60g");

        Item item6 = new Item();
        item6.setName("百草味碧根果奶油味");
        item6.setPrice(35.9);
        item6.setImage("http://obsyvbwp3.bkt.clouddn.com/1381.JPG");
        item6.setIid("1381");
        item6.setPid("13");
        item6.setSource("中国");
        item6.setSize("218g");

        Item item7 = new Item();
        item7.setName("张君雅小妹妹日式串烧烤丸子");
        item7.setPrice(7.9);
        item7.setImage("http://obsyvbwp3.bkt.clouddn.com/1383.JPG");
        item7.setIid("1383");
        item7.setPid("13");
        item7.setSource("中国");
        item7.setSize("80g");

        Item item8 = new Item();
        item8.setName("致中和龟苓膏");
        item8.setPrice(14.3);
        item8.setImage("http://obsyvbwp3.bkt.clouddn.com/1382.JPG");
        item8.setIid("1382");
        item8.setPid("13");
        item8.setSource("中国");
        item8.setSize("80g");

        Item item9 = new Item();
        item9.setName("姚太太榴莲干");
        item9.setPrice(12.8);
        item9.setImage("http://obsyvbwp3.bkt.clouddn.com/1384.JPG");
        item9.setIid("1384");
        item9.setPid("13");
        item9.setSource("中国");
        item9.setSize("30g");
        mItems.add(item);
        mItems.add(item1);
        mItems.add(item2);
        mItems.add(item3);
        mItems.add(item4);
        mItems.add(item5);
        mItems.add(item6);
        mItems.add(item7);
        mItems.add(item8);
        mItems.add(item9);

        Item item10 = new Item();
        item10.setName("云南白药牙膏薄荷清爽型");
        item10.setPrice(31);
        item10.setImage("http://obsyvbwp3.bkt.clouddn.com/141.JPG");
        item10.setIid("1410");
        item10.setPid("14");
        item10.setSource("中国");
        item10.setSize("210g");

        Item item11 = new Item();
        item11.setName("欧乐B电动牙刷");
        item11.setPrice(199);
        item11.setImage("http://obsyvbwp3.bkt.clouddn.com/142.JPG");
        item11.setIid("1420");
        item11.setPid("14");
        item11.setSource("中国");
        item11.setSize("0.255kg");

        Item item12 = new Item();
        item12.setName("Za姬芮新能真皙美白隔离霜");
        item12.setPrice(69.9);
        item12.setImage("http://obsyvbwp3.bkt.clouddn.com/143.JPG");
        item12.setIid("1430");
        item12.setPid("14");
        item12.setSource("中国");
        item12.setSize("35g");

        Item item13 = new Item();
        item13.setName("完美芦荟胶");
        item13.setPrice(89);
        item13.setImage("http://obsyvbwp3.bkt.clouddn.com/144.JPG");
        item13.setIid("1440");
        item13.setPid("14");
        item13.setSource("中国");
        item13.setSize("40g*3");

        Item item14 = new Item();
        item14.setName("力士沐浴露");
        item14.setPrice(28.9);
        item14.setImage("http://obsyvbwp3.bkt.clouddn.com/145.JPG");
        item14.setIid("1450");
        item14.setPid("14");
        item14.setSource("中国");
        item14.setSize("720ml");

        Item item15 = new Item();
        item15.setName("百雀羚眼部精华");
        item15.setPrice(128);
        item15.setImage("http://obsyvbwp3.bkt.clouddn.com/146.JPG");
        item15.setIid("1460");
        item15.setPid("14");
        item15.setSource("中国");
        item15.setSize("15ml");

        Item item16 = new Item();
        item16.setName("薇婷沐浴用脱毛膏");
        item16.setPrice(95.9);
        item16.setImage("http://obsyvbwp3.bkt.clouddn.com/1470.JPG");
        item16.setIid("1470");
        item16.setPid("14");
        item16.setSource("中国");
        item16.setSize("135ml");

        Item item17 = new Item();
        item17.setName("御泥坊亮颜水润蚕丝面膜");
        item17.setPrice(59.9);
        item17.setImage("http://obsyvbwp3.bkt.clouddn.com/1480.JPG");
        item17.setIid("1480");
        item17.setPid("14");
        item17.setSource("中国");
        item17.setSize("20片");

        Item item18 = new Item();
        item18.setName("御泥坊亮颜水润蚕丝面膜");
        item18.setPrice(99.9);
        item18.setImage("http://obsyvbwp3.bkt.clouddn.com/1490.JPG");
        item18.setIid("1490");
        item18.setPid("14");
        item18.setSource("中国");
        item18.setSize("120g*3");

        mItems1.add(item10);
        mItems1.add(item11);
        mItems1.add(item12);
        mItems1.add(item13);
        mItems1.add(item14);
        mItems1.add(item15);
        mItems1.add(item16);
        mItems1.add(item17);
        mItems1.add(item18);

        Item item19 = new Item();
        item19.setName("河北皇冠梨");
        item19.setPrice(29);
        item19.setImage("http://obsyvbwp3.bkt.clouddn.com/151.JPG");
        item19.setIid("1510");
        item19.setPid("15");
        item19.setSource("中国");
        item19.setSize("9*200g");

        Item item20 = new Item();
        item20.setName("泰国椰青");
        item20.setPrice(32);
        item20.setImage("http://obsyvbwp3.bkt.clouddn.com/152.JPG");
        item20.setIid("1520");
        item20.setPid("15");
        item20.setSource("中国");
        item20.setSize("1.4kg");

        Item item21 = new Item();
        item21.setName("海南西周蜜瓜");
        item21.setPrice(25);
        item21.setImage("http://obsyvbwp3.bkt.clouddn.com/153.JPG");
        item21.setIid("1530");
        item21.setPid("15");
        item21.setSource("中国");
        item21.setSize("1.5kg");

        Item item22 = new Item();
        item22.setName("海南三亚苹果芒");
        item22.setPrice(29.9);
        item22.setImage("http://obsyvbwp3.bkt.clouddn.com/155.JPG");
        item22.setIid("1550");
        item22.setPid("15");
        item22.setSource("中国");
        item22.setSize("1kg");

        Item item23 = new Item();
        item23.setName("智利牛油果");
        item23.setPrice(118);
        item23.setImage("http://obsyvbwp3.bkt.clouddn.com/154.JPG");
        item23.setIid("1540");
        item23.setPid("15");
        item23.setSource("中国");
        item23.setSize("1kg");

        Item item24 = new Item();
        item24.setName("泰国龙眼");
        item24.setPrice(118);
        item24.setImage("http://obsyvbwp3.bkt.clouddn.com/1551.JPG");
        item24.setIid("1551");
        item24.setPid("15");
        item24.setSource("中国");
        item24.setSize("500g");


        Item item25 = new Item();
        item25.setName("南非葡萄柚");
        item25.setPrice(59);
        item25.setImage("http://obsyvbwp3.bkt.clouddn.com/1552.JPG");
        item25.setIid("1552");
        item25.setPid("15");
        item25.setSource("中国");
        item25.setSize("280g*8");

        mItems2.add(item19);
        mItems2.add(item20);
        mItems2.add(item21);
        mItems2.add(item22);
        mItems2.add(item23);
        mItems2.add(item24);
        mItems2.add(item25);

        Item item26 = new Item();
        item26.setName("小熊SNJ-A10K5酸奶机");
        item26.setPrice(149);
        item26.setImage("http://obsyvbwp3.bkt.clouddn.com/161.JPG");
        item26.setIid("1610");
        item26.setPid("16");
        item26.setSource("中国");
        item26.setSize("8分杯内胆");

        Item item27 = new Item();
        item27.setName("九阳JYY-50YL1智能电压力锅");
        item27.setPrice(199);
        item27.setImage("http://obsyvbwp3.bkt.clouddn.com/162.JPG");
        item27.setIid("1620");
        item27.setPid("16");
        item27.setSource("中国");
        item27.setSize("5L");

        Item item28 = new Item();
        item28.setName("美的MJ-BL25B3料理机果汁机电动搅拌机");
        item28.setPrice(199);
        item28.setImage("http://obsyvbwp3.bkt.clouddn.com/163.JPG");
        item28.setIid("1630");
        item28.setPid("16");
        item28.setSource("中国");
        item28.setSize("2.7kg");

        Item item29 = new Item();
        item29.setName("九阳C06-M1电磁炉");
        item29.setPrice(129);
        item29.setImage("http://obsyvbwp3.bkt.clouddn.com/165.JPG");
        item29.setIid("1650");
        item29.setPid("16");
        item29.setSource("中国");
        item29.setSize("1000W");

        Item item30 = new Item();
        item30.setName("松下家用吸尘器WGE61");
        item30.setPrice(1990);
        item30.setImage("http://obsyvbwp3.bkt.clouddn.com/1661.JPG");
        item30.setIid("1661");
        item30.setPid("16");
        item30.setSource("中国");
        item30.setSize("850W");

        mItems3.add(item26);
        mItems3.add(item27);
        mItems3.add(item28);
        mItems3.add(item29);
        mItems3.add(item30);

        Item item31 = new Item();
        item31.setName("雀巢速溶咖啡1+2原味");
        item31.setPrice(99);
        item31.setImage("http://obsyvbwp3.bkt.clouddn.com/171.JPG");
        item31.setIid("1710");
        item31.setPid("17");
        item31.setSource("中国");
        item31.setSize("100条");

        Item item32 = new Item();
        item32.setName("桂格即食燕麦片");
        item32.setPrice(23.9);
        item32.setImage("http://obsyvbwp3.bkt.clouddn.com/172.JPG");
        item32.setIid("1720");
        item32.setPid("17");
        item32.setSource("中国");
        item32.setSize("1478g");

        mItems4.add(item32);
        mItems4.add(item31);

        Item test = new Item();
        test.setName("怡宝矿泉水");
        test.setPrice(2.0);
        test.setImage("http://obsyvbwp3.bkt.clouddn.com/yibao.jpg");
        test.setIid("1186");
        test.setPid("01");
        test.setSource("中国");
        test.setSize("555ml");

        Item test1 = new Item();
        test1.setName("出前一丁");
        test1.setPrice(22.5);
        test1.setImage("http://obsyvbwp3.bkt.clouddn.com/chuqianyiding.jpg");
        test1.setIid("980");
        test1.setPid("09");
        test1.setSource("中国");
        test1.setSize("100克*5");

        Item test2 = new Item();
        test2.setName("绿箭清新口香糖");
        test2.setPrice(8.9);
        test2.setImage("http://obsyvbwp3.bkt.clouddn.com/lvjian.jpg");
        test2.setIid("980");
        test2.setPid("06");
        test2.setSource("中国");
        test2.setSize("80g");

        Item test3 = new Item();
        test3.setName("养乐多");
        test3.setPrice(11);
        test3.setImage("http://obsyvbwp3.bkt.clouddn.com/yangleduo.jpg");
        test3.setIid("121");
        test3.setPid("01");
        test3.setSource("中国");
        test3.setSize("80g");


        mTest.add(test);
        mTest.add(test1);
        mTest.add(test2);
        mTest.add(test3);
    }
}
