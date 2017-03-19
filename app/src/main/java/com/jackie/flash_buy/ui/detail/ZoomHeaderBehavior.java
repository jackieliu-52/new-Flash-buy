package com.jackie.flash_buy.ui.detail;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.jackie.flash_buy.R;

@SuppressWarnings("unused")
public class ZoomHeaderBehavior extends CoordinatorLayout.Behavior<View> {
    public ZoomHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isFirst = true;  //是否是第一次加载
    private ZoomHeaderView mDependency;

    /** 依赖于 ZoomHeaderView */
    @Override public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof ZoomHeaderView;    //依据是头部view
    }

    @Override public boolean onDependentViewChanged(CoordinatorLayout parent, final View child,
                                                    View dependency) {
        init((RecyclerView) child, dependency);

        //recyclerView Top始终处于ZoomHeaderView bottom
        child.setY(dependency.getY() + dependency.getHeight());

        //获取viewpager
        ViewPager viewPager = ((ZoomHeaderView) dependency).getViewPager();

        for (int i = 0; i <viewPager.getChildCount(); i++) {
            //对所有item进行缩放
            changeView(child, dependency, viewPager, i);
        }

        return super.onDependentViewChanged(parent, child, dependency);
    }

    /**
     * 改变view的形状之类的
     */
    private void changeView(View child, View dependency, ViewPager viewPager, int i) {
        View view = viewPager.getChildAt(i);
        View target = view.findViewById(R.id.linearLayout);
        View img = view.findViewById(R.id.imageView);
        View bottom = view.findViewById(R.id.ll_bottom);
        View buyButton = view.findViewById(R.id.btn_plan_buy);
        View nameTxt = view.findViewById(R.id.tv_detail_name);
        View costText = view.findViewById(R.id.tv_detail_cost);
        View left = null;

        //如果不是viewpager的第一个子项
        if (viewPager.getCurrentItem() > 0) {
            left = viewPager.getChildAt(viewPager.getCurrentItem() - 1);
        }

        float progress = -dependency.getY() / dependency.getHeight() * 2;

        //recyclerView 渐变
        child.setAlpha(progress * 2);



        if (target.getWidth() * (1 + progress) <= dependency.getWidth()) {

            if (progress > 0) {
                //让text x对齐image 左侧
                bottom.offsetLeftAndRight(
                        (int) (target.getWidth() / 2 - target.getWidth() * (1 + progress) / 2
                                + MarginConfig.MARGIN_LEFT_RIGHT - bottom.getX()));

                //让button x 对齐image 右侧
                buyButton.offsetLeftAndRight(
                        (int) (target.getWidth() / 2 + target.getWidth() * (1 + progress) / 2
                                - buyButton.getWidth()
                                - MarginConfig.MARGIN_LEFT_RIGHT
                                - buyButton.getX()));

                //按钮与文字居中
                buyButton.offsetTopAndBottom((int) (bottom.getY()
                        + (costText.getY() + costText.getHeight() - nameTxt.getY()) / 2
                        + nameTxt.getY() - buyButton.getHeight() / 2 - buyButton.getY()));
            }
        }


        if (progress > 0 && progress < 0.8) {

            //缩放容器的X，使文字背景也放大
            target.setScaleX(1 + progress);

            //保证图片宽高比 放大img Y
            img.setScaleY(0.6f + progress);
        }
    }

    /**
     * 初始化一些属性
     */
    private void init(final RecyclerView child, View dependency) {
        if (isFirst) {
            mDependency = (ZoomHeaderView) dependency;
            isFirst = false;
            mDependency.setRecyclerView(child);
            //滚动监听RV的滚动条
            child.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    //当recycler处于顶部的时候 并且滑动，则恢复为viewpager
                    if (child.getChildAt(0).getY() == 0) {
                        mDependency.restore(mDependency.getY());
                    }
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }
    }

    /**
     * 制定Behavior关注的滑动方向
     */
    @Override public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                                                 View directTargetChild, View target, int nestedScrollAxes) {
        //只关心垂直事件
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 快速滑动
     */
    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, View child, View target,
                                 float velocityX, float velocityY, boolean consumed) {

        //向下Fling并且到顶部
        //如果rv当前正好在顶部且向上滑动
        if (velocityY < 0 && ((RecyclerView) target).getChildAt(0).getY() == 0) {
            mDependency.restore(mDependency.getY());
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target,
                                  int dx, int dy, int[] consumed) {

        //如果在顶部
        if (((RecyclerView) target).getChildAt(0).getY() == 0) {
            //向下滑动
            if (dy < 0) {
                mDependency.setY(mDependency.getY() - dy);
                //小于阀值
                if (mDependency.getY() < 500) {
                    mDependency.restore(mDependency.getY());
                }
            }
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }
}
