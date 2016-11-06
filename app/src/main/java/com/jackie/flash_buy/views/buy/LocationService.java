package com.jackie.flash_buy.views.buy;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jackie.flash_buy.bus.InternetEvent;
import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.bus.UiEvent;
import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.model.Round;
import com.jackie.flash_buy.model.iBeaconView;
import com.jackie.flash_buy.utils.BluetoothManager;
import com.jackie.flash_buy.utils.Constant;
import com.jackie.flash_buy.utils.InternetUtil;
import com.jackie.flash_buy.utils.location.LocationHelper;
import com.jackie.flash_buy.views.home.MainActivity;
import com.skybeacon.sdk.RangingBeaconsListener;
import com.skybeacon.sdk.ScanServiceStateCallback;
import com.skybeacon.sdk.locate.SKYBeacon;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.skybeacon.sdk.locate.SKYRegion;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 用于定位的Service，同时也可以定时获取Cart信息
 */
public class LocationService extends IntentService {
    private static final String ACTION_LOCATION = "Location!";
    public static List<Double> distances = new ArrayList<>(); //与各个ibeacon之间的距离
    private static final Double MAX = Double.MAX_VALUE;  //不能检测时的距离
    public static Context mContext;

    //圆心和距离
    static double[][] positions;
    static double[] dts;
    static RectF mRectF, mRectF1, mRectF2, mRectF3, mRectF4; //四个矩形，用于防止判定定位在货架上

    private static List<iBeaconView> beacons; //所有beacons
    private static List<Round> mRounds;  //所有的rounds

    public static TimerTask timerTask;
    private static Timer timer;


    //测试
    private int times;  //打印次数
    private boolean test; //测试
    private TimerTask mTimerTask; //测试

    public LocationService() {
        super("LocationService");
    }

    //开始定位，只会调用一次
    public static void startLocation(Context context) {
        mContext = context;
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_LOCATION);
        context.startService(intent);
        initBeacon();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_LOCATION.equals(action)) {
                //开始定位
                Log.d("LocationService", "开始定位了");
                timer = new Timer();
                timerTask = getTimerTask();
                timer.schedule(timerTask, 0, 2500);  //2.5s进行一次定位操作
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LocationService", "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopRanging();
        Log.e("LocationService", "onDestroy");
    }

    private TimerTask getTimerTask() {
        return new TimerTask() {
            @Override
            public void run() {
                //testCart();
                if(!MainActivity.TESTMODE) {
                    getCart();
                }
                EventBus.getDefault().post(new UiEvent("cart"));  //刷新UI
                //如果蓝牙打开了，才进行蓝牙连接
                if(BluetoothManager.isBluetoothEnabled()) {
                    getLocation();
                }else{
                    //只提示用户一次
                    EventBus.getDefault().post(new MessageEvent("请您打开蓝牙"));
                }

            }
        };
    }

    private void testCart(){
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


        MainActivity.cart.add(lineItem);
    }
    private void getLocation(){
        //这里面进行定位操作
        double d1 = distances.get(0);
        double d2 = distances.get(1);
        double d3 = distances.get(2);
        dts = new double[]{d1 * 100, d2 * 100, d3 * 100};  //距离*100，方便计算
        //Log.i("Location","d1: " + d1 + "米  d2:  " + d2 + "米  d3:  " + d3);
        boolean flag;
        flag = ((d1 != MAX) && (d2 != MAX) && (d3 != MAX));
        if (flag) {
            //开始定位
            Log.d("LocationService", "开始定位");

            // 获得定位点
            double[] centroid = LocationHelper.getLocation(beacons, dts);


            if (centroid != null) {
                Log.i("LocationService", "定位 sucess");
                if (!mRectF.contains((float) centroid[0], (float) centroid[1])) {
                    //如果定位结果超出地图的范围，那么不绘制
                    return;
                }
                Fragment_map.location = new PointF((float) centroid[0], (float) centroid[1]);

                //如果定位在货架上
                if (mRectF1.contains((float) centroid[0], (float) centroid[1])) {
                    //如果在第一个矩形中
                    Fragment_map.location = new PointF(55, 299);
                }
                if (mRectF2.contains((float) centroid[0], (float) centroid[1])) {
                    //如果在第2个矩形中
                    Fragment_map.location = new PointF(270, 300);
                }
                if (mRectF3.contains((float) centroid[0], (float) centroid[1])) {
                    //如果在第3个矩形中
                    Fragment_map.location = new PointF(400, 300);
                }
                if (mRectF4.contains((float) centroid[0], (float) centroid[1])) {
                    //如果在第4个矩形中
                    Fragment_map.location = new PointF(640, 300);
                }
            } else {
                Log.i("LocationService", "定位错误");
            }

            //如果有定位信息，就发送给服务器
            if (Fragment_map.location != null) {
                InternetUtil.postStr("", InternetUtil.args4 + "=9&x=" + Fragment_map.location.x + "&y=" + Fragment_map.location.y);

            }
        } else {
            Log.i("LocationService", "有ibeacon没有定位信息");
            EventBus.getDefault().post(new MessageEvent("定位失败！请检查蓝牙是否打开"));
        }
    }
    /**
     * 从服务器拉取Cart信息
     */
    private void getCart(){
        EventBus.getDefault().post(new InternetEvent(InternetUtil.cartUrl, Constant.REQUEST_Cart));
    }

    /**
     * 初始化距离，设置beacon管理
     */
    private static void initBeacon() {
        beacons = new ArrayList<>();
        mRounds = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            distances.add(MAX);
        }
        positions = new double[][]{{524, 326}, {122, 471}, {108, 192}}; //三个beacon的位置
        iBeaconView iBeaconView1 = new iBeaconView();
        iBeaconView1.location = new PointF(524, 326);
        iBeaconView iBeaconView2 = new iBeaconView();
        iBeaconView2.location = new PointF(122, 471);
        iBeaconView iBeaconView3 = new iBeaconView();
        iBeaconView3.location = new PointF(108, 192);
        beacons.add(iBeaconView1);
        beacons.add(iBeaconView2);
        beacons.add(iBeaconView3);
        //圆的初始化
        mRounds.add(new Round(iBeaconView1.location.x, iBeaconView1.location.y, (float) MAX.doubleValue()));
        mRounds.add(new Round(iBeaconView2.location.x, iBeaconView2.location.y, (float) MAX.doubleValue()));
        mRounds.add(new Round(iBeaconView3.location.x, iBeaconView3.location.y, (float) MAX.doubleValue()));

        mRectF = new RectF(0, 0, 750, 760);    //整体
        mRectF1 = new RectF(120, 0, 175, 600);  //左1
        mRectF2 = new RectF(176, 0, 230, 600);  //左2
        mRectF3 = new RectF(430, 50, 485, 650);  //右1
        mRectF4 = new RectF(486, 50, 540, 650); //右2


        SKYBeaconManager.getInstance().init(mContext);
        SKYBeaconManager.getInstance().setCacheTimeMillisecond(3000);
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(2000);

        //开始扫描
        startRanging();
    }


    private static void startRanging() {
        //开始服务

        SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {

            @Override
            public void onServiceDisconnected() {
                // TODO Auto-generated method stub
                Log.i("Service", "onServiceDisconnected");
            }

            @Override
            public void onServiceConnected() {
                // TODO Auto-generated method stub
                Log.i("Service", "onServiceConnected");
                SKYBeaconManager.getInstance().startRangingBeacons(null);
            }
        });


        SKYBeaconManager.getInstance().setRangingBeaconsListener(new RangingBeaconsListener() {
            // 单id beacons扫描结果处理
            @Override
            public void onRangedBeacons(SKYRegion beaconRegion, @SuppressWarnings("rawtypes") List beaconList) {
                // TODO Auto-generated method stub
                if (beaconList.size() == 0)
                    EventBus.getDefault().post(new MessageEvent("没有检测到蓝牙设备"));
                for (int i = 0; i < beaconList.size(); i++) {
                    iBeaconView beacon = new iBeaconView();
                    beacon.mac = ((SKYBeacon) beaconList.get(i)).getDeviceAddress();
                    beacon.rssi = ((SKYBeacon) beaconList.get(i)).getRssi();
                    beacon.isMultiIDs = false;
                    beacon.detailInfo = ((SKYBeacon) beaconList.get(i)).getProximityUUID() + "\r\nMajor: " + String.valueOf(((SKYBeacon) beaconList.get(i)).getMajor()) + "\tMinir: "
                            + String.valueOf(((SKYBeacon) beaconList.get(i)).getMinor()) + "\r\n";
                    beacon.uuid = ((SKYBeacon) beaconList.get(i)).getMinor();
                    //获得距离
                    double distance = ((SKYBeacon) beaconList.get(i)).getDistance();


                    //因为这里只有三个beacon，所以可以直接处理序号问题
                    //但是这里需要处理一下没有检测到的情况，也就是说distance为-1.0米的情况
                    switch (beacon.uuid) {
                        case 1:
                            if (distance != -1.0) {
                                beacons.set(0, beacon);
                                distances.set(0, distance);
                            }
                            break;
                        case 2:
                            if (distance != -1.0) {
                                distances.set(1, distance);
                                beacons.set(1, beacon);
                            }
                            break;
                        case 3:
                            if (distance != -1.0) {
                                beacons.set(2, beacon);
                                distances.set(2, distance);
                            }
                            break;
                    }
                }//for

            }

            // 多id beacons扫描结果处理，我们不适用
            @Override
            public void onRangedBeaconsMultiIDs(SKYRegion beaconRegion, @SuppressWarnings("rawtypes") List beaconMultiIDsList) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRangedNearbyBeacons(SKYRegion beaconRegion, List beaconList) {
                // TODO Auto-generated method stub

            }
        });
    }


    /**
     * 停止扫描,当Activity结束后结束
     */
    private void stopRanging() {
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopRangingBeasons(null);
    }


    /**
     * 打印指印，可以直接放入数据库内
     */
    public void printFingerprint(List l1, List l2, List l3, List d1, List d2, List d3) {
        //打印第几次了，预计每个位置打印50次取平均值
        //  Toast.makeText(mContext,"第" + times +"次打印",Toast.LENGTH_SHORT).show();
        for (int i = 0; i < 3; i++) {
            iBeaconView beacon = beacons.get(i);
            double distance = distances.get(i);
            if (beacon.rssi != -1) {
                // Log.i("DEBUGLOCATION"+times, beacon.uuid + "||" +beacon.rssi + "||" + distance);

                switch (beacon.uuid) {
                    case 1:
                        l1.add(beacon.rssi);
                        d1.add(distance);
                        break;
                    case 2:
                        l2.add(beacon.rssi);
                        d2.add(distance);
                        break;
                    case 3:
                        l3.add(beacon.rssi);
                        d3.add(distance);
                        break;
                }

            }

        }//for
    }


    /**
     * 测试获得指印
     */
    private void getFinger() {
        test = false;
        times = 1;
        timer = new Timer();

        final List<Integer> rssis1 = new ArrayList<>();
        final List<Double> dis1 = new ArrayList<>();
        final List<Integer> rssis2 = new ArrayList<>();
        final List<Double> dis2 = new ArrayList<>();
        final List<Integer> rssis3 = new ArrayList<>();
        final List<Double> dis3 = new ArrayList<>();


        mTimerTask = new TimerTask() {
            @Override
            public void run() {

                printFingerprint(rssis1, rssis2, rssis3, dis1, dis2, dis3);


                //50的倍数
                if (times % 50 == 0) {
                    int rsum1 = 0;
                    double dsum1 = 0;
                    int rsum2 = 0;
                    double dsum2 = 0;
                    int rsum3 = 0;
                    double dsum3 = 0;
                    for (int i = 0; i < rssis1.size(); i++) {
                        rsum1 += rssis1.get(i);
                    }
                    for (int i = 0; i < dis1.size(); i++) {
                        dsum1 += dis1.get(i);
                    }
                    Log.i("rssi", "1 avg:" + (rsum1 * 1.0) / (rssis1.size() * 1.0));
                    Log.i("dis", "1 avg:" + dsum1 / dis1.size());


                    for (int i = 0; i < rssis2.size(); i++) {
                        rsum2 += rssis2.get(i);
                    }
                    for (int i = 0; i < dis2.size(); i++) {
                        dsum2 += dis2.get(i);
                    }
                    Log.i("rssi", "2 avg:" + (rsum2 * 1.0) / (rssis2.size() * 1.0));
                    Log.i("dis", "2 avg:" + dsum2 / dis2.size());


                    for (int i = 0; i < rssis3.size(); i++) {
                        rsum3 += rssis3.get(i);
                    }
                    for (int i = 0; i < dis3.size(); i++) {
                        dsum3 += dis3.get(i);
                    }
                    Log.i("rssi", "3 avg:" + (rsum3 * 1.0) / (rssis3.size() * 1.0));
                    Log.i("dis", "3 avg:" + dsum3 / dis3.size());

                    //同时应该要清空List
                    rssis1.clear();
                    rssis2.clear();
                    rssis3.clear();
                    dis1.clear();
                    dis2.clear();
                    dis3.clear();
                    if (mTimerTask != null) {
                        mTimerTask.cancel();
                    }
                    EventBus.getDefault().post(new MessageEvent("success!"));


                }//if == 50
                times++;
            }
        };

        timer.schedule(mTimerTask, 0, 500);  //0.5s进行指纹打印

    }

}

