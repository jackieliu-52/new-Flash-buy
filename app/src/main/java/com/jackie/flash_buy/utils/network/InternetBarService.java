package com.jackie.flash_buy.utils.network;

import com.jackie.flash_buy.model.InternetItem;
import com.jackie.flash_buy.utils.Constant;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * 根据条形码获取商品信息
 */
public interface InternetBarService {
    //采用绝对路径
    @Headers("apikey: "+ Constant.Bar_APIKEY)
    @GET("http://apis.baidu.com/3023/barcode/barcode?barcode={code}")
    Observable<InternetItem> findItemByCode(@Path("code") String code);
}
