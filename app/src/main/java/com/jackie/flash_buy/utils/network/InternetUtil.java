package com.jackie.flash_buy.utils.network;



import com.jackie.flash_buy.bus.MessageEvent;
import com.jackie.flash_buy.model.InternetItem;
import com.jackie.flash_buy.model.Order;
import com.jackie.flash_buy.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 专门处理网络请求的工具类
 */
public class InternetUtil {
//    public static String root = "http://155o554j78.iok.la:49817/Flash-Buy/";
    public static String baseUrl = "192.168.191.1"; //默认ip地址
    public static String root = "http://"+ baseUrl +":443/Flash-Buy/";   //局域网测试
    public final static String args_cart = "Cart/Data?cartNumber=9&userId=9";
    public final static String args_checkout = "Cart/Data?cartNumber=9&userId=9";  //暂时用post来处理结账功能
    public final static String args_login = "Cart/Login?uuid=";
    public final static String args_location = "Cart/MapPoint?userId";


    public static String cartUrl = root + args_cart;
    public static String logInUrl = root + args_login;

    public static String searchUrl =  root + "search?name=";

    public static Retrofit rxGsonRetrofit;
    public static Retrofit rxRetrofit;

    static {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(5, TimeUnit.SECONDS);

        //饿汉式
        rxGsonRetrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //rxjava
                .addConverterFactory(GsonConverterFactory.create())  //gson
                .client(httpClientBuilder.build())
                .build();

        rxRetrofit= new Retrofit.Builder().baseUrl(baseUrl)
                .client(httpClientBuilder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //rxjava
                .build();
    }

    public static void refreshIp(){
        cartUrl = root + args_cart;
        logInUrl = root + args_login;
        searchUrl =  root + "search?name=";
    }
    /**
     *  post json数据给服务器端，如果接收成功并返回正确结果，那么该函数返回true
     * @param json
     * @param args
     * @return
     */
    public static boolean postInfo(String json, String args){
        boolean flag = false;
        //url拼接
        String urlStr = root + args;
        //post数据
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);        //设置连接超时时间
            conn.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            conn.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            conn.setRequestMethod("POST");     //设置以Post方式提交数据
            conn.setUseCaches(false);               //使用Post方式不能使用缓存
            // 设置contentType
            conn.setRequestProperty("Content-Type", "application/json");
            OutputStream os = conn.getOutputStream();

            os.write(json.getBytes());
            os.close();
            //服务器返回的响应码
            int code = conn.getResponseCode();
            if (code == 200) {
                // 等于200了,下面呢我们就可以获取服务器的数据了
                InputStream is = conn.getInputStream();
                String result = Util.readStream(is);
                //如果服务器传回了结果，表示服务器接收成功
                if(result != null)
                    flag = true;
            }
        }catch (Exception e)
        {
            EventBus.getDefault().post(new MessageEvent("更新失败"));
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 发送文本信息给服务器
     * @param str  文本
     * @param args  路径参数
     * @return   成功返回true
     */
    public static boolean postStr(String str, String args) {
        String urlStr = root + args;
        byte[] data = str.getBytes();  //String转换为byte[]
        try {

            URL url = new URL(urlStr);

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);     //设置连接超时时间
            httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
            if(response == HttpURLConnection.HTTP_OK) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void login(DisposableObserver<ResponseBody> disposableObserver,String uuid){
        LogInService service = rxRetrofit.create(LogInService.class);
        service.logIn(uuid)
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver);
    }

    public static void findItemByCode(DisposableObserver<InternetItem> disposableObserver, String code){
        InternetBarService service = rxGsonRetrofit.create(InternetBarService.class);
        service.findItemByCode(code)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(disposableObserver);
    }

    public static void getCart(DisposableObserver<Order> disposableObserver){
        CartService service = rxGsonRetrofit.create(CartService.class);
        service.getOrder()
                .subscribeWith(disposableObserver);
    }
}
