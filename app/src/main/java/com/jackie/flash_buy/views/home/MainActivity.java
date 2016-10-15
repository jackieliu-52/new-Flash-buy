package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.presenters.home.PlanPresenter;
import com.jackie.flash_buy.ui.NoScrollViewPager;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

public class MainActivity extends BaseActivity {
    private Context mContext;

    private NoScrollViewPager mViewPager;
    private TabLayout mTabLayout;
    private TabLayout.Tab buy;
    private TabLayout.Tab cart;
    private TabLayout.Tab scan;
    private TabLayout.Tab order;
    private TabLayout.Tab my;

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
        initEvents();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * 初始化tab的各种点击事件，这里效率有点低，每次都会更新一个新的drawable
     */
    private void initEvents() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            //选中
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTabLayout.getTabAt(0)) {
                    buy.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_cart_plus)
                            .colorRes(R.color.colorPrimaryLight)
                            .sizeDp(24));
                    mViewPager.setCurrentItem(0);
                } else if (tab == mTabLayout.getTabAt(1)) {
                    cart.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_shopping_cart)
                            .colorRes(R.color.colorPrimaryLight)
                            .sizeDp(24));
                    mViewPager.setCurrentItem(1);
                } else if (tab == mTabLayout.getTabAt(2)) {
                    scan.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_camera)
                            .colorRes(R.color.colorPrimaryLight)
                            .sizeDp(24));
                    mViewPager.setCurrentItem(2);
                }else if (tab == mTabLayout.getTabAt(3)){
                    order.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_th_list)
                            .colorRes(R.color.colorPrimaryLight)
                            .sizeDp(24));
                    mViewPager.setCurrentItem(3);
                }else if(tab == mTabLayout.getTabAt(4)){
                    my.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_user)
                            .colorRes(R.color.colorPrimaryLight)
                            .sizeDp(24));
                    mViewPager.setCurrentItem(4);
                }

            }
            //未选中时的图标
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == mTabLayout.getTabAt(0)) {
                    buy.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_cart_plus)
                            .colorRes(R.color.grey_light)
                            .sizeDp(24));
                } else if (tab == mTabLayout.getTabAt(1)) {
                    cart.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_shopping_cart)
                            .colorRes(R.color.grey_light)
                            .sizeDp(24));
                } else if (tab == mTabLayout.getTabAt(2)) {
                    scan.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_camera)
                            .colorRes(R.color.grey_light)
                            .sizeDp(24));
                }else if (tab == mTabLayout.getTabAt(3)){
                    order.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_th_list)
                            .colorRes(R.color.grey_light)
                            .sizeDp(24));
                }else if(tab == mTabLayout.getTabAt(4)){
                    my.setIcon(new IconicsDrawable(mContext)
                            .icon(FontAwesome.Icon.faw_user)
                            .colorRes(R.color.grey_light)
                            .sizeDp(24));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initUI() {

        mViewPager = (NoScrollViewPager) findViewById(R.id.vp_main);
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] mTitles = new String[]{"购物", "购物","扫一扫", "订单","我的"};

            @Override
            public BaseFragment getItem(int position) {
                switch (position){
                    case 0:
                        mPlanFragment = PlanFragment.GetInstance();
                        mPlanPresenter = PlanPresenter.GetInstance(mPlanFragment);
                        return  mPlanFragment;
                    case 1:
                        return new PlanFragment();
                    case 2:
                        return new PlanFragment();
                    case 3:
                        return new PlanFragment();
                    case 4:
                        return new PlanFragment();
                    default:
                        Log.e(TAG,"we don't provide the fragment!");
                        return new BaseFragment();
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
        mTabLayout = (TabLayout) findViewById(R.id.ntb);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED); //补满屏幕
        //获取tab，加载图片
        buy = mTabLayout.getTabAt(0);
        cart = mTabLayout.getTabAt(1);
        scan = mTabLayout.getTabAt(2);
        order = mTabLayout.getTabAt(3);
        my = mTabLayout.getTabAt(4);

        buy.setIcon(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_cart_plus)
                .colorRes(R.color.grey_light)
                .sizeDp(24));
        cart.setIcon(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_shopping_cart)
                .colorRes(R.color.grey_light)
                .sizeDp(24));
        scan.setIcon(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_camera)
                .colorRes(R.color.grey_light)
                .sizeDp(24));
        order.setIcon(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_th_list)
                .colorRes(R.color.grey_light)
                .sizeDp(24));
        my.setIcon(new IconicsDrawable(this)
                .icon(FontAwesome.Icon.faw_user)
                .colorRes(R.color.grey_light)
                .sizeDp(24));

    }




}
