package com.jackie.flash_buy.views.buy;

import android.support.v7.util.DiffUtil;

import com.jackie.flash_buy.model.LineItem;

import java.util.List;

/**
 * Created by Jack on 2016/11/10.
 */
public class MyDiffCallback extends DiffUtil.Callback {
    private List<LineItem> oldList;
    private List<LineItem> newList;

    public MyDiffCallback(List<LineItem> oldList, List<LineItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }
    //Item是否相同
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getItem().getName().equals(newList.get(newItemPosition).getItem().getName());
    }

    //内容是否相同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        LineItem oldOne = oldList.get(oldItemPosition);
        LineItem newOne= newList.get(newItemPosition);
        if (oldOne.getNum() != newOne.getNum() ) {
            return false;//如果有内容不同，就返回false
        }
        return true; //默认两个data内容是相同的
    }
}
