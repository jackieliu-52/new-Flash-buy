package com.jackie.flash_buy.ui.commonRecyclerView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * RecyclerView的分割线
 */
public class MyItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    //不同的space划分
    public MyItemDecoration(int space) {
        this.space=space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left=0;
        outRect.right=0;
        outRect.bottom=space;
        if(parent.getChildAdapterPosition(view)==0){
            outRect.top=space;
        } else {
            outRect.top = 0;
        }
    }
}
