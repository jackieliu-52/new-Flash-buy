package com.jackie.flash_buy.bus;


import com.jackie.flash_buy.utils.Constant;

/**
 * Created by Jack on 2016/8/12.
 */
public class InternetEvent {
    public String message;
    public int type;     //

    public InternetEvent(String message) {
        this(message, Constant.REQUEST_INTERNET_BAR);
    }

    public InternetEvent(String message, int type) {
        this.message = message;
        this.type = type;
    }
}
