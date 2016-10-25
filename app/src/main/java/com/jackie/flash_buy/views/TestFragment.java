package com.jackie.flash_buy.views;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.ui.NestedScrollingListView;
import com.jackie.flash_buy.ui.NestedScrollingListView2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/10/16.
 */
public class TestFragment extends BaseFragment {
    private ArrayList<String> mdatas = new ArrayList();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_frag, container, false);
        NestedScrollingListView2 lvtest = (NestedScrollingListView2) root.findViewById(R.id.lv_test);
        //API21以上才有效果----第二种方式，比较简便
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lvtest.setNestedScrollingEnabled(true);
        }
        initData();
        lvtest.setAdapter(new MyListAdapter(mContext,R.layout.item_goods,mdatas));
        return root;
    }

    private void initData() {
        for(int i=0;i<30;i++){
            mdatas.add("---------"+i+"-----------");
        }
    }

    class MyListAdapter extends ArrayAdapter<String>{
        private List<String> dataList;
        private Context mContext;
        private int resId;
        private LayoutInflater mInflater;

        public MyListAdapter (Context mContext, int resId, List<String> dataList) {
            super(mContext,resId,dataList);
            this.dataList = dataList;
            this.mContext = mContext;
            this.resId = resId;
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final String s = getItem(position); //得到LineItem
            View view;
            ViewHolder viewHolder;
            if(convertView == null){
                view = mInflater.inflate(R.layout.test_listview,parent,false);
                viewHolder = new ViewHolder(view,s);
                view.setTag(viewHolder);
            }else {
                view = convertView;
                viewHolder = (ViewHolder)view.getTag();
            }

            return view;
        }

        class ViewHolder{
            private TextView name;

            public ViewHolder(View LineItemView,String s){
                name = (TextView) LineItemView.findViewById(R.id.test_lv);
                name.setText(s);
            }
        }
    }


}
