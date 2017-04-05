package com.jackie.flash_buy.utils.network;

import com.jackie.flash_buy.model.Order;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * 获取购物车信息
 */
public interface CartService {
    @GET(InternetUtil.args_cart)
    Observable<Order> getOrder();
}
