package com.jackie.flash_buy.views.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jackie.flash_buy.BaseActivity;
import com.jackie.flash_buy.BaseFragment;
import com.jackie.flash_buy.R;
import com.jackie.flash_buy.adapter.ItemAdapter;
import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.bus.ListEvent;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.bus.PageEvent;
import com.jackie.flash_buy.bus.PlanBuyEvent;
import com.jackie.flash_buy.model.BulkItem;
import com.jackie.flash_buy.model.InternetItem;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.Market;
import com.jackie.flash_buy.model.Order;
import com.jackie.flash_buy.model.TwoTuple;
import com.jackie.flash_buy.model.User;
import com.jackie.flash_buy.presenters.plan.PlanPresenter;
import com.jackie.flash_buy.ui.NoScrollViewPager;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.utils.Util;
import com.jackie.flash_buy.views.TestFragment;
import com.jackie.flash_buy.views.buy.BuyFragment;
import com.jackie.flash_buy.views.buy.Fragment_cart;
import com.jackie.flash_buy.views.buy.LocationService;
import com.jackie.flash_buy.views.goods.GoodsActivity;
import com.jackie.flash_buy.views.order.OrderActivity;
import com.jackie.flash_buy.views.plan.PlanFragment;
import com.jackie.flash_buy.views.scan.ScanActivity;
import com.lzp.floatingactionbuttonplus.FabTagLayout;
import com.lzp.floatingactionbuttonplus.FloatingActionButtonPlus;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
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

import me.shaohui.bottomdialog.BottomDialog;

public class MainActivity extends BaseActivity {
    public static int status = 0 ;  //状态，0-未开始购物，1-购物中（需要定位），2-购买结束
    public static boolean isLogin = false;  //是否登录
    public static List<TwoTuple<Boolean,LineItem>> PlanBuy  = new ArrayList<>();  //计划购买的商品，已选择的商品
    public static boolean TESTMODE = false;  //测试模式
    public static List<LineItem> realPlanBuy  = new ArrayList<>();  //计划购买的商品，已选择的商品

    public static Market market; //当前购物的超市
    public static User user = new User(); //当前用户
    public static int orderId = 01;  //订单号码

    public static ArrayList<LineItem> cart = new ArrayList<>();   //当前购物车

    private Context mContext;

    private NoScrollViewPager mViewPager;
    private SmartTabLayout mSmartTabLayout;



    private HomeFragment mHomeFragment;
    private BuyFragment mBuyFragment;
    private Fragment_account mAccountFragment;

    private CoordinatorLayout clContent;
    public FloatingActionButtonPlus mActionButtonPlus;
    //购物车有关
    private ListView lv_cart;
    ItemAdapter lvAdapter;

    static {
        //访问数据库看看是不是登录成功了

    }

    public User getUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("jackieLiu", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        String name = sharedPreferences.getString("name", "");
        String mail = sharedPreferences.getString("mail", "");
        return  new User(mail,id,name);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationService.startLocation(this);  //开始定位！




        if(!isLogin){
            user = getUser();  //如果没有登录，就从SP里面去获得用户的资料
            if(user.getId().equals("")){
                //那么还是没有登录成功
            }else {
                //登录成功
                isLogin = true;
            }
        }
        EventBus.getDefault().register(this);
        setFab();

        mContext = this;
        clContent = (CoordinatorLayout) findViewById(R.id.clt);
        initUI();

        //测试模式
        if(TESTMODE)
            testMode();

    }

    private void setFab() {
        mActionButtonPlus =(FloatingActionButtonPlus) findViewById(R.id.fabPlus);
        mActionButtonPlus.setOnItemClickListener(new FloatingActionButtonPlus.OnItemClickListener() {
            @Override
            public void onItemClick(FabTagLayout tagView, int position) {
                if(position == 0){
                    Intent intent =new Intent(mContext,ScanActivity.class);
                    intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
                    startActivity(intent);
                }
                //1是favorite,2是cart
                if(position == 2){
                    BottomDialog.create(getSupportFragmentManager())
                            .setViewListener(new BottomDialog.ViewListener() {      // 可以进行一些必要对View的操作
                                @Override
                                public void bindView(View v) {
                                    // you can do bind view operation
                                    lv_cart = (ListView) v.findViewById(R.id.list_cart);
                                    lvAdapter = new ItemAdapter(mContext,R.layout.list_item,cart);
                                    lv_cart.setAdapter(lvAdapter);
                                    TextView tv_total_cost = (TextView) v.findViewById(R.id.tv_total_cost);
                                    //底部面板的结账按钮
                                    ImageView iv_check_out = (ImageView) v.findViewById(R.id.iv_checkout);
                                    iv_check_out.setOnClickListener(new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            //支付前要进行判断，是否还有东西没有买
                                            boolean flag = false;
                                            if(realPlanBuy.size() == 0)
                                                flag = true;
                                            if(!flag){
                                                new MaterialDialog.Builder(mContext)
                                                        .title("温馨提醒")
                                                        .content("您的预购清单中还有商品没有购买，是否要支付")
                                                        .positiveText("是")
                                                        .negativeText("否")
                                                        .onAny(new MaterialDialog.SingleButtonCallback() {
                                                            @Override
                                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                if (which == DialogAction.POSITIVE) {
                                                                    //生成新的订单，清空购物车，跳转支付页面
                                                                    Order order = new Order(cart,"订单号",user.getId(), Util.getCurrentDate(),"alipay","家润多",0,0);
                                                                    User.orders.add(order);
                                                                    lv_cart.setAdapter(null);
                                                                    cart = new ArrayList<>();
                                                                    realPlanBuy = new ArrayList<>();
                                                                    //跳转到订单详情进行支付
                                                                    Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                                                    intent.putExtra("order", order);
                                                                    startActivity(intent);
                                                                }
                                                            }
                                                        })
                                                        .show();
                                            } else{
                                                //生成新的订单，清空购物车，跳转支付页面
                                                Order order = new Order(cart,orderId+"",user.getId(), Util.getCurrentDate(),"init","Flashbuy",0,0);
                                                User.orders.add(order);
                                                cart = new ArrayList<>();
                                                lvAdapter.notifyDataSetChanged();
                                                realPlanBuy = new ArrayList<>();
                                                //跳转到订单详情进行支付
                                                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                                                intent.putExtra("order", order);
                                                startActivity(intent);
                                            }

                                        }
                                    });
                                }
                            })
                            .setLayoutRes(R.layout.layout_bottom_dialog)
                            .setDimAmount(0.1f)            // Dialog window 背景色深度 范围：0 到 1，默认是0.2f
                            .setCancelOutside(true)     // 点击外部区域是否关闭，默认true
                            .setTag("BottomDialog")     // 设置DialogFragment的tag
                            .show();
                }
                if(position == 3){
                    TESTMODE = !TESTMODE; //取反
                    testMode();  //获取测试数据
                    EventBus.getDefault().post(new MessageEvent("测试模式：" + TESTMODE));
                }
                //EventBus.getDefault().post(new MessageEvent("Click btn" + position));
            }
        });
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

                final String[] mTitles = new String[]{"超市", "购物车", "订单","我的"};
                textView.setText(mTitles[position]);
                switch (position) {

                    case 0:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_shopping_basket)
                                .sizeDp(18));
                        break;
                    case 1:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_shopping_cart)
                                .sizeDp(18));
                        break;
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
        mViewPager.setOffscreenPageLimit(3);  //全部都预加载
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] mTitles = new String[]{"超市", "购物车", "订单","我的"};

            @Override
            public BaseFragment getItem(int position) {
                switch (position){
                    case 0:
                        mHomeFragment = HomeFragment.GetInstance();
                        return  mHomeFragment;
                    case 1:
                        //返回购买的Fragment
                        mBuyFragment = BuyFragment.GetInstance();
                        return mBuyFragment;

                    case 2:
                        return new Fragment_list();
                    case 3:
                        mAccountFragment = Fragment_account.GetInstance();
                        return mAccountFragment;
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
        if(messageEvent.item != null){
            Toast toast = Toast.makeText(getApplicationContext(),
                    messageEvent.message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.BOTTOM, 0, 80);
            toast.show();
            return;
        }
        if(messageEvent.message.equals("定位失败！请检查蓝牙是否打开")){
            Toast toast = Toast.makeText(getApplicationContext(),
                    messageEvent.message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
        }else {
            Snackbar.make(clContent, messageEvent.message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void pageChanged(PageEvent event){
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + event.off, true);
        status = 1; //需要定位了
        EventBus.getDefault().post(new PlanBuyEvent("initMap"));
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
                initCart(internetEvent.message); //初始化购物车
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


    /**
     * 测试模式下的初始化
     */
    private void testMode(){
        cart = new ArrayList<>();

        Item item2 = new Item();
        item2.setName("安慕希酸奶");
        item2.setPrice(59.4);
        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/133.JPG");
        item2.setIid("1330");
        item2.setPid("13");
        item2.setSource("中国");
        item2.setSize("205g*12");
        LineItem lineItem = new LineItem();
        lineItem.setItem(item2);
        lineItem.setNum(1);

        //这里有一个小bug,第一个东西不能显示出来
        cart.add(lineItem);

        Item item3 = new Item();
        item3.setName("三只松鼠夏威夷果");
        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/134.JPG");
        item3.setPrice(5);
        LineItem lineItem3 = new LineItem();
        lineItem3.setItem(item3);
        lineItem3.setNum(1);
        cart.add(lineItem3);


    }

    /**
     * 向服务器请求购物车信息
     * @param url
     */
    private void initCart(String url){
        cart = new ArrayList<>();
        String json;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) url1
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            //网页返回的状态码
            int code = connection.getResponseCode();
            //Log.i("购物车查询","结果码" + code);
            if(code == 200) {
                InputStream is = connection.getInputStream();
                json = Util.readStream(is);
                //避免Unicode转义
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                //将json转换为一个Order

                Order order = gson.fromJson(json,Order.class);
                if(order == null){

                }else {
                    //将剩下的加入购物车
                    cart.addAll(order.getLineItems());

                    double total_price = 0;
                    //如果说这个预购商品已经放进去了，那么就移除它
                    for (LineItem lineItem : cart) {
                        for (LineItem tempItem : realPlanBuy) {
                            if (lineItem.getItem().getName().equals(tempItem.getItem().getName())) {
                                realPlanBuy.remove(tempItem); //移除预购商品
                            }
                        }
                        total_price += lineItem.getUnitPrice();
                    }

                    if (lvAdapter != null) {
                        lvAdapter.notifyDataSetChanged(); //通知更新
                    }
                    EventBus.getDefault().post(new MessageEvent("刷新购物车成功",new Item()));
                }
            }

            else {
                // EventBus.getDefault().post(new MessageEvent("刷新购物车失败，请检查网络"));
            }

        }
        catch (Exception e) {
            // EventBus.getDefault().post(new MessageEvent("刷新购物车失败，请检查网络"));
            e.printStackTrace();
        }

    }
}
