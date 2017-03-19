package com.jackie.flash_buy.views.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.ExpandViewAdapter;
import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.model.Aller_father;
import com.jackie.flash_buy.model.Allergen;
import com.jackie.flash_buy.utils.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 过敏源页面
 */
public class Fragment_aler extends Fragment {
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ExpandViewAdapter mAdapter;

    public static List<Allergen> Allergens = new ArrayList<>();
    public static boolean isChanged = false;

    public static  Fragment_aler  instance;
    /**
     * 对外接口
     *
     * @return Fragment_aler
     */
    public static Fragment_aler GetInstance() {
        if (instance == null)
            instance = new Fragment_aler();
        return instance;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aler, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.aler_recyview);
        init();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //瀑布流设置
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        return view;
    }

    private  void init(){
        Allergen allergen1 = new Allergen("牛奶",11);
        Allergen allergen2 = new Allergen("鸡蛋",12);

        Aller_father aller_father1 = new Aller_father("奶制品",1, Arrays.asList(allergen1,allergen2));

        Allergen allergen3 = new Allergen("花生",71);
        Allergen allergen4 = new Allergen("杏仁",72);

        Aller_father aller_father2 = new Aller_father("坚果",7,Arrays.asList(allergen3,allergen4));

        mAdapter = new ExpandViewAdapter(mContext, Arrays.asList(aller_father1,aller_father2));
    }

    /**
     * 告诉主页面，需要把修改的数据提交给服务器
     */
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(isChanged) {
            EventBus.getDefault().post(new InternetEvent("过敏源", Constant.POST_Aller));
        }
    }

}
