package com.jackie.flash_buy.views.plan;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.GoodsAdapter;
import com.jackie.flash_buy.bus.PageEvent;
import com.jackie.flash_buy.contracts.plan.PlanContract;
import com.jackie.flash_buy.contracts.plan.TypeListener;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.ui.NestedScrollingListView;
import com.jackie.flash_buy.ui.SmoothCheckBox;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerAdapter;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerViewHolder;
import com.jackie.flash_buy.ui.commonRecyclerView.DividerDecoration;
import com.jackie.flash_buy.ui.commonRecyclerView.RecyclerItemClickListener;
import com.jackie.flash_buy.views.home.MainActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
    //底部
    private View bottom;
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private TextView total_cost;
    private double total_price; //总价
    private TextView total_cost1; //总价1

    //预购商品
    private CommonRecycleView mCommonRecycleView;
    CommonRecyclerAdapter mCommonRecyclerAdapter; //所需要购买商品


    private boolean visible;//是否可见

    //这里要提供一个释放的方法
    public static void endFragment(){
        instance = null;
    }

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
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"不可见");
            visible = false;

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.plan_frag, container, false);
        //底部布局
        bottomSheetLayout = (BottomSheetLayout) root.findViewById(R.id.bottomSheetLayout);
        total_cost1 = (TextView) root.findViewById(R.id.tv_total_cost);
        root.findViewById(R.id.iv_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        //上面的布局
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
                        //Log.i(TAG,type);
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

        //API21以上才有效果,可以让Fab随着消失...然而改版后并没有fab了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvItems.setNestedScrollingEnabled(true);
        }

        mGoodsAdapter = new GoodsAdapter(mContext,R.layout.goods_item,new ArrayList<LineItem>(0),this);

        lvItems.setAdapter(mGoodsAdapter);
        lvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //根据firstVisibleItem获取分类ID，根据分类id获取左侧要选中的位置
                LineItem lineItem = mPresenter.getItems().get(firstVisibleItem);

                //如果当前选中的selected的ID与当前最上面显示的Item的Id不一致的时候，就需要刷星页面
                if(!commonRecyclerAdapter.selected.equals(lineItem.getItem().getPid())) {
                    commonRecyclerAdapter.selected = lineItem.getItem().getPid();

                    commonRecyclerAdapter.notifyDataSetChanged();
                    //平滑地滚动到相应位置的RecyclerView中
                    rvType.smoothScrollToPosition(mPresenter.getSelectedGroupPosition(lineItem.getItem().getPid()));
                    Log.i(TAG,lineItem.getItem().getPid());
                }
            }
        });

        bottom = root.findViewById(R.id.bottom);
        bottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();
            }
        });
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
        mGoodsAdapter = new GoodsAdapter(mContext,R.layout.goods_item,lineItems,this);
        lvItems.setAdapter(mGoodsAdapter);
    }

    @Override
    public void add(LineItem lineItem,boolean refresh){
        mPresenter.add(lineItem);
        commonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
        mGoodsAdapter.notifyDataSetChanged();
        if(mCommonRecyclerAdapter != null){
            mCommonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
        }

        if(total_cost != null){
            total_price = mPresenter.unitPrice();
            total_cost.setText(total_price + "元");
            total_cost1.setText(total_price + "元");
        }
    }

    @Override
    public void removeItem(LineItem lineItem, boolean refresh){
        mPresenter.remove(lineItem);
        commonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
        mGoodsAdapter.notifyDataSetChanged();
        if(mCommonRecyclerAdapter != null){
            mCommonRecyclerAdapter.notifyDataSetChanged(); //刷新UI
        }

        if(total_cost != null){
            total_price = mPresenter.unitPrice();
            total_cost.setText(total_price + "元");
            total_cost1.setText(total_price + "元");
        }
    }



    private View createBottomSheetView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bottom_sheet,(ViewGroup) getActivity().getWindow().getDecorView(),false);
        mCommonRecycleView = (CommonRecycleView) view.findViewById(R.id.plan_list);
        mCommonRecycleView.setLayoutManager(new LinearLayoutManager(mContext));
        mCommonRecyclerAdapter  = getAdapter();
        mCommonRecycleView.setAdapter(mCommonRecyclerAdapter);
        view.findViewById(R.id.iv_checkout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        total_cost = (TextView) view.findViewById(R.id.tv_total_cost);
        total_price = mPresenter.unitPrice();
        total_cost.setText(total_price + "");
        return view;
    }

    private void showBottomSheet(){
        if(bottomSheet==null){
            bottomSheet = createBottomSheetView();
        }
        if(bottomSheetLayout.isSheetShowing()){
            bottomSheetLayout.dismissSheet();
        }else {
            bottomSheetLayout.showWithSheetView(bottomSheet);
        }
    }
    /**
     * 确定用户是否要开始购物
     */
    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("开始购物");
        builder.setMessage("您是否要开始购物?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(TwoTuple<Boolean,LineItem> temp : MainActivity.PlanBuy){
                    if(temp.first) {
                        MainActivity.realPlanBuy.add(temp.second);
                    }
                }
                mCommonRecyclerAdapter.clearAll();  //删除所有数据
                MainActivity.PlanBuy = new ArrayList<>(); //清空数据


                //与此同时，应该通知map的Activity准备好地图
                getActivity().finish();//先关闭Activity

                //虽然退出了Activity，但是还是会执行这段代码
                EventBus.getDefault().post(new PageEvent(1));
            }
        });
        builder.show();
    }


    /**
     * 获得顾客预购商品的Adapter
     * @return
     */
    private CommonRecyclerAdapter<TwoTuple<Boolean,LineItem>> getAdapter(){
        return new CommonRecyclerAdapter<TwoTuple<Boolean,LineItem>>(mContext, R.layout.plan_items, MainActivity.PlanBuy) {

            @Override
            public void convert(final CommonRecyclerViewHolder holder, final TwoTuple<Boolean,LineItem> o) {

                Item item = o.second.getItem();
                holder.setText(R.id.tv_item_name, item.getName());
                holder.setText(R.id.tv_item_date, item.getPid());   //区域,考虑使用EPC字段
                if(!item.getImage().equals("")){
                    //加载图片
                    Picasso.with(mContext)
                            .load(item.getImage())
                            .placeholder(R.mipmap.ic_launcher)
                            .into(((ImageView) holder.getView(R.id.ci_image)));
                }




                SmoothCheckBox scb = (SmoothCheckBox) holder.getView(R.id.scb);
                scb.setChecked(o.first);
                scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        int pos = holder.getLayoutPosition();  //获得下标

                        MainActivity.PlanBuy.get(pos).first = isChecked;

                        holder.getView(R.id.v_color).setVisibility(View.VISIBLE);
                        Drawable color = getResources().getDrawable(R.color.bg_Gray);
                        holder.getView(R.id.v_color).setBackground(color);
                    }
                });
                holder.setText(R.id.planCount,o.second.getNum()+"");
                holder.getView(R.id.planTvAdd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add(o.second,true);
                    }
                });
                holder.getView(R.id.planTvMinus).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeItem(o.second,true);
                    }
                });

            }
        };
    }
}
