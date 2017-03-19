package com.jackie.flash_buy.views.plan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.DetailAdapter;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.ui.detail.MarginConfig;
import com.jackie.flash_buy.ui.detail.ZoomHeaderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class DetailActivity extends BaseActivity {
    private Item item;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ViewPager mViewPager;
    private ZoomHeaderView mZoomHeader;
    private boolean isFirst = true;

    private RelativeLayout mBottomView;

    public static int bottomY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        item = (Item) getIntent().getParcelableExtra("item");//获取数据


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_detail);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_detail);
        mZoomHeader = (ZoomHeaderView) findViewById(R.id.zoomHeader);
        mViewPager.setAdapter(new Adapter());
        mViewPager.setOffscreenPageLimit(4);
        CtrlLinearLayoutManager layoutManager = new CtrlLinearLayoutManager(this);

        //未展开禁止滑动
        layoutManager.setScrollEnabled(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new DetailAdapter());
        mRecyclerView.setAlpha(0);
        mBottomView = (RelativeLayout) findViewById(R.id.rv_bottom);

    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirst) {
            for (int i = 0; i < mViewPager.getChildCount(); i++) {
                View v = mViewPager.getChildAt(i).findViewById(R.id.ll_bottom);
                v.setY(mViewPager.getChildAt(i).findViewById(R.id.imageView).getHeight());
                v.setX(MarginConfig.MARGIN_LEFT_RIGHT);
                //触发一次dependency变化，让按钮归位
                mZoomHeader.setY(mZoomHeader.getY() - 1);
                isFirst = false;
            }
        }

        //隐藏底部栏]
        bottomY = (int) mBottomView.getY();
        mBottomView.setTranslationY(mBottomView.getY() + mBottomView.getHeight());
        mZoomHeader.setBottomView(mBottomView, bottomY);
    }

    class Adapter extends PagerAdapter {
        public Adapter() {
            views = new ArrayList<>();
            views.add(View.inflate(DetailActivity.this, R.layout.item_eleme_detail, null));
            views.get(0).findViewById(R.id.btn_plan_buy).setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Toast.makeText(DetailActivity.this, "加入购物车", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private ArrayList<View> views;

        @Override public int getCount() {
            return views.size();
        }

        @Override public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override public Object instantiateItem(ViewGroup container, int position) {
            TextView tv_name = (TextView) views.get(position).findViewById(R.id.tv_detail_name);
            tv_name.setText(item.getName());
            TextView tv_cost = (TextView) views.get(position).findViewById(R.id.tv_detail_cost);
            tv_cost.setText("￥" + item.getPrice());
            ImageView imageView = (ImageView)views.get(position).findViewById(R.id.imageView);
            Picasso.with(mContext)
                    .load(item.getImage())
                    .into(imageView);

            container.addView(views.get(position));

            return views.get(position);
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    @Override protected void onResume() {
        super.onResume();
    }

    @Override public void onBackPressed() {

        if (mZoomHeader.isExpand()) {
            mZoomHeader.restore(mZoomHeader.getY());
        } else {
            finish();
        }
    }


    public class CtrlLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public CtrlLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
