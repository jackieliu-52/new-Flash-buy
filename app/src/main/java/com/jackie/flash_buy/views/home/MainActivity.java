package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.presenters.home.PlanPresenter;
import com.jackie.flash_buy.ui.NoScrollViewPager;
import com.jackie.flash_buy.views.TestFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class MainActivity extends BaseActivity {
    private Context mContext;

    private NoScrollViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;

    private PlanFragment mPlanFragment;
    private PlanPresenter mPlanPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mContext = this;
        initUI();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }



    private void setupTab(SmartTabLayout layout) {
        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = inflater.inflate(R.layout.custom_tab_icon_and_text, container,
                        false);
                ImageView icon = (ImageView) view.findViewById(R.id.custom_tab_icon);
                TextView textView = (TextView) view.findViewById(R.id.custom_tab_text);

                final String[] mTitles = new String[]{"购物", "购物车","扫一扫", "订单","我的"};
                textView.setText(mTitles[position]);
                switch (position) {

                    case 0:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_cart_plus)
                                .sizeDp(18));
                        break;
                    case 1:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_shopping_cart)
                                .sizeDp(18));
                        break;
                    case 2:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_camera)
                                .sizeDp(18));
                        break;
                    case 3:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_th_list)
                                .sizeDp(18));
                        break;
                    case 4:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_user)
                                .sizeDp(18));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return view;
            }
        });
    }
    private void initUI() {
        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_main);
        mViewPager.setOffscreenPageLimit(0);  //取消预加载
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] mTitles = new String[]{"购物", "购物车","扫一扫", "订单","我的"};

            @Override
            public BaseFragment getItem(int position) {
                switch (position){
                    case 0:
                        mPlanFragment = PlanFragment.GetInstance();
                        mPlanPresenter = PlanPresenter.GetInstance(mPlanFragment);
                        return  mPlanFragment;
                    case 1:
                        return new TestFragment();
                    case 2:
                        return new TestFragment();
                    case 3:
                        return new TestFragment();
                    case 4:
                        return new TestFragment();
                    default:
                        Log.e(TAG,"we don't provide the fragment!");
                        return new TestFragment();
                }

            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            //这个方法返回Tab显示的文字。这里通过在实例化TabFragment的时候，传入的title参数返回标题。
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }

        });
        mSmartTabLayout = (SmartTabLayout)findViewById(R.id.stb);
        setupTab(mSmartTabLayout);
        mSmartTabLayout.setViewPager(mViewPager);
    }



}
