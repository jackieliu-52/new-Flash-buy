package com.jackie.flash_buy.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jack on 2016/11/1.
 */
public class Market implements Parcelable {
    private String name;
    private String desri;
    private String logo;
    private String activity1;
    private String activity2; //暂时只有两条活动
    private int distance;  //距离，以m为单位

    public Market() {
        this("Flashbuy超市","扫描零费时，付款一秒钟","","","",0);
    }

    public Market(String name, String desri, String logo, String activity1, String activity2, int distance) {
        this.name = name;
        this.desri = desri;
        this.logo = logo;
        this.activity1 = activity1;
        this.activity2 = activity2;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesri() {
        return desri;
    }

    public void setDesri(String desri) {
        this.desri = desri;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getActivity1() {
        return activity1;
    }

    public void setActivity1(String activity1) {
        this.activity1 = activity1;
    }

    public String getActivity2() {
        return activity2;
    }

    public void setActivity2(String activity2) {
        this.activity2 = activity2;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.desri);
        dest.writeString(this.logo);
        dest.writeString(this.activity1);
        dest.writeString(this.activity2);
        dest.writeInt(this.distance);
    }

    protected Market(Parcel in) {
        this.name = in.readString();
        this.desri = in.readString();
        this.logo = in.readString();
        this.activity1 = in.readString();
        this.activity2 = in.readString();
        this.distance = in.readInt();
    }

    public static final Parcelable.Creator<Market> CREATOR = new Parcelable.Creator<Market>() {
        @Override
        public Market createFromParcel(Parcel source) {
            return new Market(source);
        }

        @Override
        public Market[] newArray(int size) {
            return new Market[size];
        }
    };
}
