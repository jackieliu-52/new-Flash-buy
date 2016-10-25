package com.jackie.flash_buy.views.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.GoodsAdapter;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.contracts.home.PlanContract;
import com.jackie.flash_buy.contracts.home.TypeListener;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.ui.NestedScrollingListView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerAdapter;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerViewHolder;
import com.jackie.flash_buy.ui.commonRecyclerView.DividerDecoration;
import com.jackie.flash_buy.ui.commonRecyclerView.RecyclerItemClickListener;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.views.scan.ScanActivity;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanFragment extends BaseFragment implements PlanContract.View,TypeListener {

    private PlanContract.Presenter mPresenter;

    /**
     * 单例对象实例
     */
    private static PlanFragment instance = null;
    private com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView rvType;
    CommonRecyclerAdapter commonRecyclerAdapter; //种类

    private NestedScrollingListView lvItems;
    private GoodsAdapter mGoodsAdapter;  //商品的adapter

    //fab
//    private FloatingActionButton mFloatingActionButton;
    private boolean visible;//是否可见
    private FloatingActionButtonPlus mActionButtonPlus;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonRecyclerAdapter = new CommonRecyclerAdapter<TwoTuple<String,Integer>>(mContext,R.layout.rv_item_type,new ArrayList<TwoTuple<String,Integer>>(0)) {
//            public String type = "init";

            @Override
            public void convert(final CommonRecyclerViewHolder holder, TwoTuple<String,Integer> item) {
                //这里进行转化
                holder.setText(R.id.tvType,item.first);
//                Log.i("convert","selectd:" +selected);
//                Log.i("convert",item.first);
//                Log.i("convert",item.second+"");
                if(item.second >= 1){
                    holder.getView(R.id.tvCount).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tvCount,item.second + " ");
                }else {
                    holder.getView(R.id.tvCount).setVisibility(View.INVISIBLE);
                }
                //如果被选中了
                if(item.first.equals(selected)){
                    holder.getContent().setBackgroundColor(Color.WHITE);
                    holder.getView(R.id.v_color).setVisibility(View.VISIBLE);
                }else {
                    holder.getContent().setBackgroundColor(Color.TRANSPARENT);
                    holder.getView(R.id.v_color).setVisibility(View.INVISIBLE);
                }
            }
        };

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"可见");
            visible= true;
//            if(mFloatingActionButton != null)
//                mFloatingActionButton.setVisibility(View.VISIBLE);
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"不可见");
            visible = false;
//            if(mFloatingActionButton != null)
//                mFloatingActionButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.plan_frag, container, false);
        mActionButtonPlus =(FloatingActionButtonPlus) getActivity().findViewById(R.id.fabPlus);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                EventBus.getDefault().post(new MessageEvent("Click btn" + position));
            }
        });
//        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//
//        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//                Intent intent =new Intent(mContext,ScanActivity.class);
//                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
//                startActivity(intent);
//            }
//        });

        this.rvType = (CommonRecycleView) root.findViewById(R.id.rvType);

        rvType.setLayoutManager(new LinearLayoutManager(mContext));
        rvType.setAdapter(commonRecyclerAdapter);  //设置Adapter
        rvType.addItemDecoration(new DividerDecoration(mContext));  //设置分割线
        //添加点击函数
        rvType.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, rvType ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String type = ((TwoTuple<String,Integer>)commonRecyclerAdapter.getData().get(position)).first;
                        commonRecyclerAdapter.selected = type;   //设定当前选中的item

                        //view.setBackgroundColor(Color.WHITE);
                        commonRecyclerAdapter.notifyDataSetChanged();
                        //得到了type,接下来应该根据Type进行相应定位
                        Log.i(TAG,type);
                        lvItems.setSelection(mPresenter.getSelectedPosition(type));

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do nothing
                    }
                })
        );
        //listView
        this.lvItems = (NestedScrollingListView) root.findViewById(R.id.lvItems);

        //API21以上才有效果----第二种方式，比较简便
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvItems.setNestedScrollingEnabled(true);
        }

        mGoodsAdapter = new GoodsAdapter(mContext,R.layout.item_goods,new ArrayList<LineItem>(0),this);

        lvItems.setAdapter(mGoodsAdapter);
//        lvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                //根据firstVisibleItem获取分类ID，根据分类id获取左侧要选中的位置
//                LineItem lineItem = mPresenter.getItems().get(firstVisibleItem);
//                //如果当前选中的selected的ID与当前最上面显示的Item的Id不一致的时候，就需要刷星页面
//                if(!commonRecyclerAdapter.selected.equals(lineItem.getItem().getPid())) {
//                    commonRecyclerAdapter.selected = lineItem.getItem().getPid();
//
//                    commonRecyclerAdapter.notifyDataSetChanged();
//                    //平滑地滚动到相应位置的RecyclerView中
//                    rvType.smoothScrollToPosition(mPresenter.getSelectedGroupPosition(lineItem.getItem().getPid()));
//                    Log.i(TAG,lineItem.getItem().getPid());
//                }
//            }
//        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();  //初始化数据
    }

    /**
     * 对外接口
     *
     * @return PlanFragment
     */
    public static PlanFragment GetInstance() {
        if (instance == null)
            instance = new PlanFragment();
        return instance;
    }

    @Override
    public void setPresenter(@NonNull PlanContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setTypes(List<TwoTuple<String,Integer>> mTypes){
        commonRecyclerAdapter.replaceData(mTypes);  //更新数据
    }

    @Override
    public void setItems(List<LineItem> lineItems){
        //更新数据
        mGoodsAdapter = new GoodsAdapter(mContext,R.layout.item_goods,lineItems,this);
        lvItems.setAdapter(mGoodsAdapter);
    }

    @Override
    public void add(LineItem lineItem,boolean refresh){
        mPresenter.add(lineItem);
        commonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
    }

    @Override
    public void remove(LineItem lineItem, boolean refresh){
        mPresenter.remove(lineItem);
        commonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
    }




}
