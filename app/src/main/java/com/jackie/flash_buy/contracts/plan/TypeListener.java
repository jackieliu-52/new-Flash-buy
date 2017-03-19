package com.jackie.flash_buy.contracts.plan;

import com.jackie.flash_buy.model.Item;
import com.jackie.flash_buy.model.LineItem;

/**
 * Created by Jack on 2016/10/20.
 */
public interface TypeListener {
    //根据type定位listView
    // void   onTypeClick(String a);
    //添加预购清单
    void add(LineItem lineItem, boolean refresh);
    //删除预购清单
    void removeItem(LineItem lineItem, boolean refresh);
    //打开外部列表
    void openItemActivity(Item item);
}
