package com.jackie.flash_buy.bus;

/**
 * Created by Jack on 2016/11/3.
 */
public class PageEvent {
    public  int off;  //跳转到第几页，从1开始

    public PageEvent(int off) {
        this.off = off;
    }
}
