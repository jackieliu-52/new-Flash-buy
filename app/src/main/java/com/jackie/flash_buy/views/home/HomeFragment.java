package com.jackie.flash_buy.views.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.model.Market;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerAdapter;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerViewHolder;
import com.jackie.flash_buy.ui.commonRecyclerView.DividerItemDecoration;
import com.jackie.flash_buy.ui.commonRecyclerView.RecyclerItemClickListener;
import com.jackie.flash_buy.views.plan.PlanActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jack on 2016/11/1.
 */
public class HomeFragment extends BaseFragment {
    /**
     * 单例对象实例
     */
    private static HomeFragment instance = null;
    private Banner banner;
    private CommonRecycleView hometoprv;
    private CommonRecycleView homeendrv;
    String[] images;
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
        this.hometoprv = (CommonRecycleView) root.findViewById(R.id.home_top_rv);
        this.banner = (Banner) root.findViewById(R.id.banner);

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
        banner.setBannerAnimation(Transformer.DepthPage);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(1500);
        //设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //banner设置方法全部调用完毕时最后调用
        banner.start();
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                Toast.makeText(mContext, "Hello：" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBottom() {
        final List<Market> markets = new ArrayList<>();
        markets.add(new Market());
        markets.add(new Market("家润多","中国最大的连锁超市","","","",1000));
        markets.add(new Market("沃尔玛","世界最大的连锁超市","","","",500));
        homeendrv.setAdapter(new CommonRecyclerAdapter<Market>(mContext,R.layout.rv_market_item,markets) {
            @Override
            public void convert(CommonRecyclerViewHolder holder, Market item) {
                //这里进行转化
                holder.setText(R.id.rvTvMarketName,item.getName());
                holder.setText(R.id.rvTvMarketDes,item.getDesri());
                holder.setText(R.id.rvTvMarketDis,item.getDistance()+"m");
                holder.setText(R.id.rvTvMarketa1,item.getActivity1());
                holder.setText(R.id.rvTvMarketa2,item.getActivity2());
            }
        });
        homeendrv.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, hometoprv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Market market = markets.get(position);
                        //进入PlanActivity
                        Intent intent = new Intent(mContext, PlanActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("market",market);
                        intent.putExtras(bundle);
                        startActivity(intent);
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
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        hometoprv.setLayoutManager(linearLayoutManager);
    }


}
