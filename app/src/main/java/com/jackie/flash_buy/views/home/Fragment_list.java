package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.model.Order;
import com.jackie.flash_buy.model.User;
import com.jackie.flash_buy.ui.Card.MyCardProvider;
import com.jackie.flash_buy.views.order.OrderActivity;
import com.litesuits.common.assist.Toastor;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;

/**
 * 订单类
 * Created by Jack on 2016/8/5.
 */
public class Fragment_list extends BaseFragment {
    final private String TAG = "Fragment_list";
    private Context mContext = null;
    private MaterialListView mListView;
    private Toastor toastor= null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Order mOrder;   //当前被点击的Order

    @Override
    public void onAttach(Context context){

        super.onAttach(context);
        this.mContext = context;
        toastor = new Toastor(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        mListView = (MaterialListView) view.findViewById(R.id.material_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_List);


        Log.i(TAG,"onCreateView");

        //填写数据
        for(Order order: User.getOrders()){
            fillArray(order);
        }

        // Set the dismiss listener
        mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull Card card, int position) {
                // Show a toast
                toastor.showSingletonToast("You have dismissed a " + card.getTag());
            }
        });

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Order order = (Order)card.getTag();
                mOrder = order;
                //跳转到订单详情
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("order", order);
                startActivity(intent);
              //  toastor.showSingletonToast("单机" + card.getTag());
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Order order = (Order)card.getTag();
                mOrder = order;

                //只有已经支付的订单才可以打印发票
                boolean isSold = false;
                isSold = order.getStatus() == 1;
                if(isSold) {
                    showDialog();
                }
        //        toastor.showSingletonToast("changji" + card.getTag());
            }
        });

        initRefresh();
        return view;
    }

    private void initRefresh(){
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventBus.getDefault().post(new MessageEvent("刷新订单"));
                //先获取订单(暂无)，再刷新界面
                mListView.getAdapter().clearAll();

                for(Order order: User.getOrders()){
                    fillArray(order);
              }

                //最后再把刷新取消
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }//onRefresh
        });
    }
    /**
     * 这是兼容的 AlertDialog
     */
    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("发票打印");
        builder.setMessage("您是否要打印发票?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "打印发票", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void fillArray(final Order order) {
        if(order.getStatus() == 1) {
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(order)
                    .withProvider(new MyCardProvider())
                    .setTitle(order.getSm_name() + " ：已支付")
                    .setTitleGravity(Gravity.START)
                    .setSubtitle("总价： "+order.getPayment())
                    .setSubtitleGravity(Gravity.START)
                    .setLineItem(order.getLineItems())
                    .setDrawable(R.drawable.dog)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText("打印发票")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //发票打印
                                    showDialog();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("查看明细")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                   //查看明细
                                }
                            }));

            provider.setDividerVisible(true);
            Card card2 = provider.endConfig().build();
            mListView.getAdapter().add(card2);
        }
        else {
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(order)
                    .setDismissible()
                    .withProvider(new MyCardProvider())
                    .setTitle(order.getSm_name() + " ：未支付")
                    .setTitleGravity(Gravity.START)
                    .setSubtitle("总价： "+order.getPayment())
                    .setSubtitleGravity(Gravity.START)
                    .setLineItem(order.getLineItems())
                    .setDrawable(R.drawable.dog)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText("支付")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //跳转支付
                                    toastor.showSingletonToast("暂时未开发支付功能");
                                    String temp = card.getProvider().getTitle();
                                    temp = temp.substring(0,temp.length()-4);
                                    card.getProvider().setTitle(temp + "：已支付");
                                    order.setStatus(1);
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("取消订单")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //这个
                                    toastor.showSingletonToast("您已经取消订单了"
                                            + card.getProvider().getTitle());
                                    Iterator<Order> sListIterator = User.orders.iterator();
                                    while(sListIterator.hasNext()){
                                        Order e = sListIterator.next();
                                        if(e.getOrderId().equals(card.getTag())){
                                            Log.i("取消订单",card.getTag().toString());
                                            sListIterator.remove();
                                        }
                                    }
                                    card.dismiss();
                                }
                            }));
            provider.setDividerVisible(true);
            Card card2 = provider.endConfig().build();
            mListView.getAdapter().add(card2);
        }


    }

}
