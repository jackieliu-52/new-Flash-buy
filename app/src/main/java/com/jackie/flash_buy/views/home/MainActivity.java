package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.model.InternetItem;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.presenters.home.PlanPresenter;
import com.jackie.flash_buy.ui.NoScrollViewPager;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.utils.Util;
import com.jackie.flash_buy.views.TestFragment;
import com.jackie.flash_buy.views.goods.GoodsActivity;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static int status ;  //状态，0-未绑定，1-绑定成功，2-购买结束
    public static List<TwoTuple<Boolean,LineItem>> PlanBuy  = new ArrayList<>();  //计划购买的商品，已选择的商品

    public static List<LineItem> realPlanBuy  = new ArrayList<>();  //计划购买的商品，已选择的商品


    private Context mContext;

    private NoScrollViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;

    private PlanFragment mPlanFragment;
    private PlanPresenter mPlanPresenter;
    private CoordinatorLayout clContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mContext = this;
        clContent = (CoordinatorLayout) findViewById(R.id.clt);
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

                final String[] mTitles = new String[]{"购物", "购物车", "订单","我的"};
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
//                    case 2:
//                        icon.setImageDrawable(new IconicsDrawable(mContext)
//                                .icon(FontAwesome.Icon.faw_camera)
//                                .sizeDp(18));
//                        break;
                    case 2:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_th_list)
                                .sizeDp(18));
                        break;
                    case 3:
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

            private String[] mTitles = new String[]{"购物", "购物车", "订单","我的"};

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void snackBar(MessageEvent messageEvent){
        Snackbar.make(clContent, messageEvent.message, Snackbar.LENGTH_SHORT).show();
//        new SnackBar.Builder(MainActivity.this)
//                .withMessage(messageEvent.message)
//                .withStyle(SnackBar.Style.ALERT)
//                .withDuration((short)2000)
//                .show();
    }


    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getInfo(InternetEvent internetEvent) {
        switch (internetEvent.type){
            case Constant.REQUEST_INTERNET_BAR:
                String httpUrl = "http://apis.baidu.com/3023/barcode/barcode";
                String httpArg = "barcode=" + internetEvent.message;
                httpUrl = httpUrl + "?" + httpArg;
                //保存网页信息
                String info;
                try {
                    URL url = new URL(httpUrl);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setRequestMethod("GET");
                    // 填入apikey到HTTP header
                    connection.setRequestProperty("apikey",  "ab7d6eef4f735da9892ee2c6682f5088");
                    connection.connect();
                    //网页返回的状态码
                    int code = connection.getResponseCode();

                    Log.i("result","状态码 ：" + code);
                    if(code == 200) {
                        InputStream is = connection.getInputStream();
                        info = Util.readStream(is);
                        //避免Unicode转义
                        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                        //将json转换为一个InternetItem
                        InternetItem internetItem = gson.fromJson(info,InternetItem.class);
                        GoodsActivity.item = new Item(internetItem);  //获得一个Item
                        startActivity(new Intent(this,GoodsActivity.class));
                    }
                    else {
                        //没有网络不能跳转
                        EventBus.getDefault().post(new MessageEvent("查询商品失败，请检查网络"));
                    }
                } catch (Exception e) {
                    //没有网络不能跳转
                    EventBus.getDefault().post(new MessageEvent("查询商品失败，请检查网络"));
                    e.printStackTrace();
                    Log.i("result","Exception:"+ e.toString() );
                }
                break;
            case Constant.REQUEST_Cart:
              //  initCart(internetEvent.message); //初始化购物车
                break;
            case Constant.REQUEST_Bulk:
                EventBus.getDefault().post("刷新散装食品！");
            //    initBulk(internetEvent.message);  //初始化散装商品
                break;
            case Constant.REQUEST_Search:
//                if(getSearchInfo(internetEvent.message)) {
//                    //成功
//                    Fragment_search fragment_search = new Fragment_search();
//                    switchContent(mContent,fragment_search);
//                }
//                else{
//                    //搜索没成功
//                    EventBus.getDefault().post(new MessageEvent("搜索商品失败，请检查网络"));
//                }
                break;
            case Constant.POST_Aller:
//                Gson gson = new Gson();
//                String json = gson.toJson(Fragment_aler.mAllergens, Aller_father.class);
//                if(InternetUtil.postInfo(json,"aller?userId=9")){
//                    //设置成功
//                    EventBus.getDefault().post(new MessageEvent("设置过敏源完成"));
//                }else {
//                    //设置没有成功
//                    EventBus.getDefault().post(new MessageEvent("设置过敏源失败，请检查网络"));
//                }
                break;
            default:
                Log.e("getInfo()","getInfo()" + internetEvent.type);
                break;
        }
    }



    /**
     * 因为有个Activity用到了WebView，但是根据网上说法，WebView可能没有正常地释放资源
     * 所以这里偷懒选择了这样一种方法来保证程序退出之后没有另外泄露
     */
    @Override
    protected void onDestroy(){
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
