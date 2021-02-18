package com.danik.bitkneset;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Order implements Parcelable { //going to be sent to outside the activity so gotta serialize it

    private String user;
    private String type;
    private String desc;
    private float amount;
    private boolean paid;
    private String date;

    public Order(String user, String type,String desc, float amount, boolean paid, String date) {
        this.user = user;
        this.type = type;
        this.desc = desc;
        this.amount = amount;
        this.paid = paid;
        this.date = date;
    }


    protected Order(Parcel in) {
        user = in.readString();
        type = in.readString();
        desc = in.readString();
        amount = in.readFloat();
        paid = in.readByte() != 0;
        date = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(type);
        dest.writeString(desc);
        dest.writeFloat(amount);
        dest.writeByte((byte) (paid ? 1 : 0));
        dest.writeString(date);
    }
}
