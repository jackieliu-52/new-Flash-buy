package com.jackie.flash_buy.views.buy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.views.TestFragment;
import com.jackie.flash_buy.views.home.HomeFragment;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * 负责地图和购物车
 */
public class BuyFragment extends BaseFragment{
    /**
     * 单例对象实例
     */
    private static BuyFragment instance = null;
    private com.ogaclejapan.smarttablayout.SmartTabLayout viewpagerTab;
    private android.support.v4.view.ViewPager viewpager;
    /**
     * 对外接口
     *
     * @return HomeFragment
     */
    public static BuyFragment GetInstance() {
        if (instance == null)
            instance = new BuyFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.buy_frag, container, false);
        this.viewpager = (ViewPager) root.findViewById(R.id.viewpager);
        this.viewpagerTab = (SmartTabLayout) root.findViewById(R.id.viewpagerTab);
        viewpager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position){
                    case 0:
                        return new Fragment_map();
                    case 1:
                        return new Fragment_cart();

                    default:
                        Log.e(TAG,"we don't provide the fragment!");
                        return new TestFragment();
                }

            }
            @Override
            public int getCount() {
                return 2;
            }

        });
        setupTab(viewpagerTab);
        //绑定
        viewpagerTab.setViewPager(viewpager);

        return root;
    }


    private boolean setupTab(SmartTabLayout layout){
        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());


        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.custom_tab_icon1, container,
                        false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_map)
                                .sizeDp(24));
                        break;
                    case 1:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_layers)
                                .sizeDp(24));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
        return true;
    }
}
