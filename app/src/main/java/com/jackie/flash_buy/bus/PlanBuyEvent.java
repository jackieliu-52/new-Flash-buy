package com.jackie.flash_buy.bus;

/**
 * 计划购物的Event
 */
public class PlanBuyEvent {
    public String message;

    public PlanBuyEvent(String message) {
        this.message = message;
    }
}
