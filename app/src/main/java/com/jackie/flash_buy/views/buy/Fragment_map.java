package com.jackie.flash_buy.views.buy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.flash_buy.R;
import com.jackie.flash_buy.bus.PlanBuyEvent;
import com.jackie.flash_buy.model.LineItem;
import com.jackie.flash_buy.views.home.MainActivity;
import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.test.TestData;
import com.onlylemi.mapview.library.utils.MapUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 地图展示的Fragment
 *
 *
 *
 *
 */
public class Fragment_map extends android.support.v4.app.Fragment  implements SensorEventListener  {
    final private String TAG = "Fragment_map";
    private Context mContext;


    public static List<Double> distances = new ArrayList<>(); //与各个ibeacon之间的距离
    private static final Double MAX = Double.MAX_VALUE;  //不能检测时的距离

    private MapView mapView;

    private MarkLayer markLayer;
    private RouteLayer routeLayer;
    private LocationLayer locationLayer;

    private List<PointF> nodes;    //辅助点
    private List<PointF> nodesContract; //哪些辅助点到哪些辅助点之间是可达的
    private List<PointF> marks;   //货品的点
    private List<String> marksName;  //货品名称
    public static List<Boolean> chosed = TestData.getChosed();

    public static PointF location; //当前定位的坐标


    private Timer timer = null;
    private TimerTask timerTask = null;
    private boolean dingweing; //看看是否定位了
    private boolean visible; //是否可见
    private boolean openSensor = true; //是否打开传感器,默认打开
    private SensorManager sensorManager; //传感器



    public static List<Integer> nums = new ArrayList<>();  //各个区域的数量
    BitmapLayer bitmapLayer1;
    BitmapLayer bitmapLayer2;
    BitmapLayer bitmapLayer3;
    Bitmap bmp1;
    Bitmap bmp2;
    Bitmap bmp3;

    static {
        for(int i =0;i < 3;i++){
            nums.add(Integer.valueOf(0));
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        sensorManager.unregisterListener(this); //关闭传感器

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Log.i(TAG,"onCreateView");


        initMapDatas(); //初始化地图数据

        mapView = (MapView) view.findViewById(R.id.mapview);
        loadMap();
        //注册传感器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor
                (Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

        //震动测试
  //    VibrateUtil.vibrate(mContext,1000);


        return view;
    }

    /**
     * 加载地图
     */
    private void loadMap(){
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("map2.png"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"get map error[picture]");
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                //路径
                routeLayer = new RouteLayer(mapView);
                mapView.addLayer(routeLayer);

                markLayer = new MarkLayer(mapView, marks, marksName);
                markLayer.setChosed(chosed);   //被选中的mark字体变红

                mapView.addLayer(markLayer);

                //点击函数
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                        PointF target = new PointF(marks.get(num).x, marks.get(num).y);       //获得被触摸的点

                        chosed.set(num,true);  //设置被选中
                        //marks.get(4)为起点
                        List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints
                                (marks.get(4), target, nodes, nodesContract);
                        routeLayer.setNodeList(nodes);
                        routeLayer.setRouteList(routeList);
                        mapView.refresh();
                    }
                });


                locationLayer = new LocationLayer(mapView, new PointF(650, 760));  //起点
                locationLayer.setOpenCompass(true);
                locationLayer.setCompassIndicatorCircleRotateDegree(60);  //罗盘
                locationLayer.setCompassIndicatorArrowRotateDegree(-30);  //方向
                mapView.addLayer(locationLayer);


                mapView.refresh();   //draw地图
                Log.i("test","233");
                loadMapDetails();  //加载细节
                if(!dingweing) {
                    startTimer(); //开始

                }
            }

            @Override
            public void onMapLoadFail() {
            }
        });
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }
    /**
     * 加载地图数据
     */
    private void initMapDatas(){
        nodes = TestData.getNodesList();
        nodesContract = TestData.getNodesContactList();
        marks = TestData.getMarks();    //点
        marksName = TestData.getMarksName();  //点的名字

        MapUtils.init(nodes.size(), nodesContract.size());
    }

    /**
     * 预购商品数量的
     */
    private void loadMapDetails(){
        bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bitmapLayer1 = new BitmapLayer(mapView, bmp1);
        bitmapLayer1.setLocation(new PointF(485,477));  //酒水饮料区域
        bitmapLayer2 = new BitmapLayer(mapView, bmp2);
        bitmapLayer2.setLocation(new PointF(175,552));  //糖果零食区域
        bitmapLayer3 = new BitmapLayer(mapView, bmp3);
        bitmapLayer3.setLocation(new PointF(175,250));  //生鲜水果区域

        mapView.addLayer(bitmapLayer1);
        mapView.addLayer(bitmapLayer2);
        mapView.addLayer(bitmapLayer3);


        mapView.refresh();   //draw地图
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
            visible = true;
            //如果不再定位中而且需要定位
            if(!dingweing && MainActivity.status == 1){
                startTimer(); //开始定位
            }
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"in");
            visible = false;
            //定位中
            if(dingweing) {
                stopTimer();  //关闭计时器
            }

        }
    }

    private void startTimer() {
        stopTimer();
        dingweing = true;
        //Log.i(TAG, "开始定位233");
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
               if(visible){
                   //定位
                   if(location != null){
                       Log.i("location","x:"+ location.x+"   y:" + location.y);
                       locationLayer.setCurrentPosition(location);
                       mapView.refresh();           //刷新,可以不在UI线程中执行
                   }
               }
            }
        };
        timer.schedule(timerTask, 0, 2500);  //2.5s进行一次定位操作
    }

    private void stopTimer() {
        dingweing = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }



    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void plan(PlanBuyEvent planBuyEvent){
        Log.i("Test","MapLoadFinish2333");

        if(planBuyEvent.message.equals("initMap")){
            //阻塞直到地图加载完全
            while (true){
                if( mapView.isMapLoadFinish()){
                    Log.i("Test","MapLoadFinish");

                    //注册传感器
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor
                            (Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

                    startTimer();  //开始定位

                    setMapDetail();   //设置各个区域的数字

                    //这里只进行一次路径规划
                    pathPlanning();

                    mapView.refresh();   //draw地图
                    break; //跳出循环
                }
            }
        }
    }

    /**
     * 根据方向传感器的值来绘制罗盘的方向
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mapView.isMapLoadFinish() && openSensor && visible) {
            float mapDegree = 0; // the rotate between reality map to northern
            float degree = 0;
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                degree = event.values[0];
            }

            locationLayer.setCompassIndicatorCircleRotateDegree(-degree);
            locationLayer.setCompassIndicatorArrowRotateDegree(mapDegree + mapView
                    .getCurrentRotateDegrees() + degree);
            mapView.refresh();
        }
    }

    /**
     * 设置预购商品的数量
     */
    private void setMapDetail(){
        nums.set(0,Integer.valueOf(0));
        nums.set(1,Integer.valueOf(0));
        nums.set(2,Integer.valueOf(0));
        //遍历
        for(LineItem item: MainActivity.realPlanBuy){
            switch (item.getItem().getPid()){
                case "01":
                    nums.set(0,Integer.valueOf(nums.get(0).intValue() + 1));
                    break;
                case "06":
                    nums.set(1,Integer.valueOf(nums.get(1).intValue() + 1));
                    break;
                case "09":
                    nums.set(2,Integer.valueOf(nums.get(2).intValue() + 1));
                    break;
            }
        }
        switch (nums.get(0)){
            case 1:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
        switch (nums.get(1)){
            case 1:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
        switch (nums.get(2)){
            case 1:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
        bitmapLayer1.setBitmap(bmp1);
        bitmapLayer2.setBitmap(bmp2);
        bitmapLayer3.setBitmap(bmp3);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    /**
     * 路径规划函数
     */
    private void pathPlanning(){
        boolean flag1,flag2,flag3;
        flag1 = flag2 = flag3 = false;
        //这里判断可能要考虑锁的问题，因为在定时函数中，nums.get(0)被不断的初始化
        //或许可以考虑用观察者模式来解决商品信息变更所导致的变化问题
        if(nums.get(0) != 0){
            flag1 = true;
        }
        if(nums.get(1) != 0){
            flag2 = true;
        }
        if(nums.get(2) != 0){
            flag3 = true;
        }

        List<PointF> list = new ArrayList<>();
        list.add(marks.get(4));
        list.add(marks.get(5));
        if(flag1) {
            list.add(marks.get(2));
            MarkLayer.chosed.set(2,true);
            list.add(marks.get(3));
            MarkLayer.chosed.set(3,true);
        }
        if(flag2) {
            list.add(marks.get(0));
            MarkLayer.chosed.set(0,true);
        }
        if(flag3){
            list.add(marks.get(1));
            MarkLayer.chosed.set(1,true);
        }
        List<Integer> routeList = MapUtils.getBestPathBetweenPoints(list, nodes,
                nodesContract);
        routeLayer.setNodeList(nodes);
        routeLayer.setRouteList(routeList);
    }

}
