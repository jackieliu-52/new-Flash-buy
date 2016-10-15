package com.jackie.flash_buy.views.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.contracts.home.PlanContract;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerAdapter;
import com.jackie.flash_buy.ui.commonRecyclerView.CommonRecyclerViewHolder;
import com.jackie.flash_buy.ui.commonRecyclerView.DividerDecoration;
import com.jackie.flash_buy.ui.commonRecyclerView.RecyclerItemClickListener;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Jack on 2016/10/15.
 */
public class PlanFragment extends BaseFragment implements PlanContract.View {

    private PlanContract.Presenter mPresenter;

    /**
     * 单例对象实例
     */
    private static PlanFragment instance = null;
    private com.jackie.flash_buy.ui.commonRecyclerView.CommonRecycleView rvType;
    CommonRecyclerAdapter commonRecyclerAdapter; //种类

    private StickyListHeadersListView lvItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        commonRecyclerAdapter = new CommonRecyclerAdapter<TwoTuple<String,Integer>>(mContext,R.layout.rv_item_type,new ArrayList<TwoTuple<String,Integer>>(0)) {

            @Override
            public void convert(final CommonRecyclerViewHolder holder, TwoTuple<String,Integer> item) {
                //这里进行转化
                holder.setText(R.id.tvType,item.first);
                holder.setText(R.id.tvCount,item.second + " ");
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.plan_frag, container, false);
        this.lvItems = (StickyListHeadersListView) root.findViewById(R.id.lvItems);
        this.rvType = (CommonRecycleView) root.findViewById(R.id.rvType);

        rvType.setLayoutManager(new LinearLayoutManager(mContext));
        rvType.setAdapter(commonRecyclerAdapter);  //设置Adapter
        rvType.addItemDecoration(new DividerDecoration(mContext));  //设置分割线
        rvType.addOnItemTouchListener(
                new RecyclerItemClickListener(mContext, rvType ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String type = ((TwoTuple<String,Integer>)commonRecyclerAdapter.getData().get(position)).first;
                        //得到了type,接下来应该根据Type进行相应定位

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do nothing
                    }
                })
        );
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
        commonRecyclerAdapter.replaceData(mTypes);  //更新
    }
    public interface TypeListener {
        //根据type定位listView
       void   onTypeClick(String a);
    }

}
