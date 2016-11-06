package com.jackie.flash_buy.views.buy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.bus.UiEvent;
import com.jackie.flash_buy.model.BulkItem;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.ui.Card.GoodsCardProvider;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.utils.InternetUtil;
import com.jackie.flash_buy.views.home.MainActivity;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 实时账单,需要把TimerTask换成handler，因为只要有一次一场，Timer就会挂掉
 */
public class Fragment_cart extends BaseFragment {
    private  final String TAG = "Fragment_cart";
    Context mContext;
    MaterialListView mListView;
    private MaterialListAdapter mListAdapter;

    public static int first = 1;   //是否是第一次进入,保存用户操作状态使用
    static boolean isMutiList = false;  //是否是多重列表


    SwipeRefreshLayout sr_swipeMaterialListView;


    private boolean visible;



    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mListView = (MaterialListView) view.findViewById(R.id.material_cart_list);
        sr_swipeMaterialListView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_MaterialListView);

        //init();
        mListAdapter = mListView.getAdapter();

        initRefresh();


        //如果不是第一次进入，那么保存用户的习惯，比如说商品排列方式
        if(first != 1){

        }

        first++;
        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }


    private void init() {
        //县清空，在刷新
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListAdapter.clearAll();
            }
        });
        for(LineItem lineItem: MainActivity.cart){
            Item item = lineItem.getItem();
            String num;
            if(item == null)
                break;
            //因为加了一个LineItem，所以有点bug要处理
            if(item.getName().equals(""))
                continue;

            String descri;
            if(item.getSize().equals("") || item.getSize().equals("未知"))
                descri = "未知";
            else
                descri = item.getSize();

            if (lineItem.isBulk){
                num = ((BulkItem)lineItem.getItem()).getWeight() +"kg";
                descri = ((BulkItem)lineItem.getItem()).getProduceTime() + "生产";
            } else{
                num = lineItem.getNum() + "";
            }
            if(item.getImage().equals("")){
                //设置默认展位图片
                item.setImage("http://obsyvbwp3.bkt.clouddn.com/good.png");
            }
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(item)
                    .withProvider(new GoodsCardProvider())
                    .setTitle(item.getName())
                    .setTitleGravity(Gravity.START)
                    .setDescription(descri)
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(item.getImage())
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("×  " + num)
                            .setTextResourceColor(R.color.black_button)
                    )
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText(item.realPrice()+" 元")

                            .setTextResourceColor(R.color.orange_button)
                    );
            provider.setDividerVisible(false);
            final Card card = provider.endConfig().build();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListView.getAdapter().add(card);
                }
            });
        }
    }

    private void initRefresh(){
        //设置刷新时动画的颜色，可以设置4个
        sr_swipeMaterialListView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        sr_swipeMaterialListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub

                init(); //再填充数据


                //最后再把刷新取消
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        sr_swipeMaterialListView.setRefreshing(false);

                    }
                });
            }//onRefresh
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"可见");
            visible= true;

        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"不可见");
            visible = false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void plan(UiEvent event){

        //Log.i("uirefresh","uiRefresh2333");
        if(visible){
            if(!MainActivity.TESTMODE) {
            }
           // Log.i("uirefresh","uiRefresh");
            init();
        }
    }



}
