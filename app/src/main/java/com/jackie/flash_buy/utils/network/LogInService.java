package com.jackie.flash_buy.utils.network;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * 处理登录请求
 */
public interface LogInService {
    @POST(InternetUtil.args_login+"/{uuid}&userId=9&password=9")
    @FormUrlEncoded
    Observable<ResponseBody> logIn(@Path("uuid") String uuid);
}
