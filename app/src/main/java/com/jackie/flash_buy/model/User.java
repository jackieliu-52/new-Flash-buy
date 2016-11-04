package com.jackie.flash_buy.model;

import java.util.ArrayList;

/**
 * 用户类
 */
public class User {
    public static ArrayList<Order> orders = new ArrayList<>(); //用户所有订单
    private String id;     //手机号码
    private String password = "";    //暂时无用
    private String name = "";   //姓名
    private String sex = "女"; //性别，默认为女
    private int age = 18;  //年龄，默认18岁
    private double remaining = 0; //余额
    //新增
    private double spend = 0 ; //已消费金额
    private String mail = ""; //邮箱
    private int frequency = 1 ; //发送总结账单的频率，默认一周一次
    private boolean sendMail = true; //是否发送账单，默认为true
    private boolean allergic = true; //是否启动过敏源提醒，默认开启
    private ArrayList<String> sources = new ArrayList<>();    //过敏源列表
    public static ArrayList<Item> starItems = new ArrayList<>(); //收藏的商品
    private boolean isLike = true; //是否打开猜你喜欢
    private ArrayList<Item> like = new ArrayList<>(); //猜你喜欢的商品

    public User(String mail, String id, String name) {
        this.mail = mail;
        this.id = id;
        this.name = name;
    }

    static {
        User.orders = new ArrayList<>();
        Item item1 = new Item();
        item1.setName("小熊酸奶机");
        item1.setImage("http://obsyvbwp3.bkt.clouddn.com/161.JPG");
        item1.setPrice(149);
        Item item2 = new Item();
        item2.setName("九阳智能电压力锅");
        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/162.JPG");
        item2.setPrice(199);
        Item item3 = new Item();
        item3.setPrice(33);
        item3.setName("雀巢速溶咖啡");
        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/171.JPG");

        Item item5 = new Item();
        item5.setName("香楠玫瑰鲜花饼");
        item5.setPrice(3.9);
        item5.setImage("http://obsyvbwp3.bkt.clouddn.com/1380.JPG");
        item5.setIid("1380");
        item5.setPid("13");
        item5.setSource("中国");
        item5.setSize("60g");

        Item item8 = new Item();
        item8.setName("致中和龟苓膏");
        item8.setPrice(14.3);
        item8.setImage("http://obsyvbwp3.bkt.clouddn.com/1382.JPG");
        item8.setIid("1382");
        item8.setPid("13");
        item8.setSource("中国");
        item8.setSize("80g");

        Item item9 = new Item();
        item9.setName("姚太太榴莲干");
        item9.setPrice(12.8);
        item9.setImage("http://obsyvbwp3.bkt.clouddn.com/1384.JPG");
        item9.setIid("1384");
        item9.setPid("13");
        item9.setSource("中国");
        item9.setSize("30g");

        ArrayList<LineItem> lineItems = new ArrayList<>();
        LineItem lineItem1 = new LineItem();
        lineItem1.setItem(item1);
        lineItem1.setNum(1);

        LineItem lineItem2 = new LineItem();
        lineItem2.setItem(item2);
        lineItem2.setNum(2);

        LineItem lineItem3 = new LineItem();
        lineItem3.setItem(item3);
        lineItem3.setNum(1);

        LineItem lineItem4 = new LineItem();
        lineItem4.setItem(item5);
        lineItem4.setNum(1);
        LineItem lineItem5 = new LineItem();
        lineItem5.setItem(item8);
        lineItem5.setNum(1);
        LineItem lineItem9 = new LineItem();
        lineItem9.setItem(item9);
        lineItem9.setNum(1);

        lineItems.add(lineItem1);
        lineItems.add(lineItem2);
        lineItems.add(lineItem4);
        Order order1 = new Order(lineItems,"1","2","8/10","aliPay","家乐福",0,0);

        User.orders.add(order1);
        ArrayList<LineItem> lineItems1 = new ArrayList<>();
        lineItems1.addAll(lineItems);
        lineItems1.add(lineItem3);
        lineItems1.add(lineItem5);
        lineItems1.add(lineItem9);

        Order order2 = new Order(lineItems1,"2","2","8/12","weixin","沃尔玛",0,1);

        User.orders.add(order2);

        Item item4 = new Item();
        item4.setName("安慕希酸奶");
        item4.setPrice(59.4);
        item4.setImage("http://obsyvbwp3.bkt.clouddn.com/133.JPG");
        item4.setIid("1370");
        item4.setPid("13");
        item4.setSource("中国");
        item4.setSize("121g*5");


        starItems.add(item4);
    }
    public double getSpend() {
        return spend;
    }

    public void setSpend(double spend) {
        this.spend = spend;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isSendMail() {
        return sendMail;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }

    public boolean isAllergic() {
        return allergic;
    }

    public void setAllergic(boolean allergic) {
        this.allergic = allergic;
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }

    public ArrayList<Item> getStarItems() {
        return starItems;
    }

    public void setStarItems(ArrayList<Item> starItems) {
        this.starItems = starItems;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public ArrayList<Item> getLike() {
        return like;
    }

    public void setLike(ArrayList<Item> like) {
        this.like = like;
    }

    public static ArrayList<Order> getOrders() {
        return orders;
    }

    public static void setOrders(ArrayList<Order> orders) {
        User.orders = orders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

}
