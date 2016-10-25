package com.jackie.flash_buy.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.util.Log;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Jack on 2016/10/26.
 */
public class NestedScrollingListView extends StickyListHeadersListView implements NestedScrollingChild {
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    public NestedScrollingListView(final Context context) {
        super(context);
        initHelper();

    }

    public NestedScrollingListView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initHelper();
    }

    public NestedScrollingListView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHelper();
    }



    private void initHelper() {
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    @Override
    public void setNestedScrollingEnabled(final boolean enabled) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWrappedList().setNestedScrollingEnabled(true);
        }
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(final int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(final int dxConsumed, final int dyConsumed, final int dxUnconsumed, final int dyUnconsumed, final int[] offsetInWindow) {
        Log.i("Behavior","dy:" + dyUnconsumed);
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(final int dx, final int dy, final int[] consumed, final int[] offsetInWindow) {
        Log.i("Behavior","dy:" + dy);
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(final float velocityX, final float velocityY, final boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(final float velocityX, final float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
