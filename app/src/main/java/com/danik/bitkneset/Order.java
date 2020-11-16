package com.danik.bitkneset;

import java.util.Date;

public class Order {

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
}
