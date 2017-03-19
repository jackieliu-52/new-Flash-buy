package com.jackie.flash_buy.bus;

import android.graphics.PointF;

/**
 * 计划购物的Event
 */
public class PlanBuyEvent {
    public String message;
    public float degree;
    public float mapDegree;
    public PointF mPointF;

    public PlanBuyEvent(String message, float degree, float mapDegree, PointF pointF) {
        this.message = message;
        this.degree = degree;
        this.mapDegree = mapDegree;
        mPointF = pointF;
    }

    public PlanBuyEvent(String message) {
        this.message = message;
    }

    public PlanBuyEvent(String message, float degree, float mapDegree) {
        this.message = message;
        this.degree = degree;
        this.mapDegree = mapDegree;
    }
}
